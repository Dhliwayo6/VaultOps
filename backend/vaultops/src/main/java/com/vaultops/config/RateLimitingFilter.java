package com.vaultops.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.services.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.EstimationProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = request;
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 1. Wrap request for login endpoint to cache request body
        boolean isLogin = "/api/auth/login".equals(uri) && "POST".equalsIgnoreCase(method);
        if (isLogin) {
            httpRequest = new CachedBodyHttpServletRequest(httpRequest);
        }

        String ip = getClientIP(httpRequest);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) ? auth.getName() : null;
        String userOrIP = user != null ? user : ip;

        // 2. Lockout Check for login attempts (5 failures in 15 minutes)
        if (isLogin) {
            String email = extractEmailFromRequest((CachedBodyHttpServletRequest) httpRequest);
            Bucket ipFailedBucket = rateLimitingService.resolveFailedLoginBucket("failed-login-ip:" + ip);
            Bucket emailFailedBucket = email != null ? rateLimitingService.resolveFailedLoginBucket("failed-login-email:" + email) : null;

            if (ipFailedBucket.getAvailableTokens() <= 0) {
                log.warn("Login blocked: IP {} is locked out due to too many failed login attempts", ip);
                send429Response(response, ipFailedBucket);
                return;
            }

            if (emailFailedBucket != null && emailFailedBucket.getAvailableTokens() <= 0) {
                log.warn("Login blocked: Account {} is locked out due to too many failed login attempts", email);
                send429Response(response, emailFailedBucket);
                return;
            }
        }

        // 3. Global Bot/Scraping Protection (max 200 requests/minute)
        String globalKey = "global:" + userOrIP;
        Bucket globalBucket = rateLimitingService.resolveGeneralBucket(globalKey, 200, Duration.ofMinutes(1));
        if (!globalBucket.tryConsume(1)) {
            log.warn("Potential bot activity detected: IP {} / User {} made more than 200 requests in a minute", ip, user);
            send429Response(response, globalBucket);
            return;
        }

        // 4. Endpoint Specific Rate Limiting
        Bucket endpointBucket = resolveEndpointBucket(uri, method, userOrIP, ip);
        if (endpointBucket != null && !endpointBucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for endpoint: {} {} by IP {} / User {}", method, uri, ip, user);
            send429Response(response, endpointBucket);
            return;
        }

        // 5. Proceed with filter chain
        try {
            filterChain.doFilter(httpRequest, response);
        } finally {
            // 6. Post-execution handler: Track failed logins
            if (isLogin) {
                String email = extractEmailFromRequest((CachedBodyHttpServletRequest) httpRequest);
                int status = response.getStatus();
                if (status >= 200 && status < 300) {
                    // Successful login -> Reset failed attempts
                    rateLimitingService.resetFailedLoginBucket("failed-login-ip:" + ip);
                    if (email != null) {
                        rateLimitingService.resetFailedLoginBucket("failed-login-email:" + email);
                    }
                } else if (status == 400 || status == 401) {
                    // Failed login -> Consume a failure token
                    rateLimitingService.resolveFailedLoginBucket("failed-login-ip:" + ip).tryConsume(1);
                    if (email != null) {
                        rateLimitingService.resolveFailedLoginBucket("failed-login-email:" + email).tryConsume(1);
                    }
                }
            }
        }
    }

    private Bucket resolveEndpointBucket(String uri, String method, String userOrIP, String ip) {
        // A. Login frequency protection (30 requests/minute to prevent CPU hash exhaust)
        if ("/api/auth/login".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return rateLimitingService.resolveGeneralBucket("login-freq:" + ip, 30, Duration.ofMinutes(1));
        }

        // B. Registration limits (3 per hour per IP)
        if ("/api/auth/register".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return rateLimitingService.resolveGeneralBucket("register-ip:" + ip, 3, Duration.ofHours(1));
        }

        // C. Password reset requests (3 per hour per IP)
        if ("/api/auth/request-reset".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return rateLimitingService.resolveGeneralBucket("reset-request-ip:" + ip, 3, Duration.ofHours(1));
        }

        // D. Asset Export (5 per minute per User/IP)
        if (uri.startsWith("/api/export/excel") || uri.startsWith("/api/export/csv")) {
            return rateLimitingService.resolveGeneralBucket("export:" + userOrIP, 5, Duration.ofMinutes(1));
        }

        // E. Asset Import (5 per minute per User/IP)
        if ("/api/import/assets".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return rateLimitingService.resolveGeneralBucket("import:" + userOrIP, 5, Duration.ofMinutes(1));
        }

        // F. Search and filter (30 per minute per User/IP)
        if (uri.startsWith("/api/asset/search")) {
            return rateLimitingService.resolveGeneralBucket("search:" + userOrIP, 30, Duration.ofMinutes(1));
        }

        // Stats endpoints (20 per minute per User/IP)
        if (uri.startsWith("/api/stats/")) {
            return rateLimitingService.resolveGeneralBucket("stats:" + userOrIP, 20, Duration.ofMinutes(1));
        }

        // Audit log endpoint (20 per minute per User/IP)
        if (uri.startsWith("/api/audit-log")) {
            return rateLimitingService.resolveGeneralBucket("audit-log:" + userOrIP, 20, Duration.ofMinutes(1));
        }

        // G. General endpoints (100 per minute per User/IP)
        if (uri.startsWith("/api/")) {
            return rateLimitingService.resolveGeneralBucket("general:" + userOrIP, 100, Duration.ofMinutes(1));
        }

        return null;
    }

    private void send429Response(HttpServletResponse response, Bucket bucket) throws IOException {
        EstimationProbe probe = bucket.estimateAbilityToConsume(1);
        long secondsToWait = Math.max(1, probe.getNanosToWaitForRefill() / 1_000_000_000L);
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(secondsToWait));
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Too many requests. Please try again later.\"}");
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private String extractEmailFromRequest(CachedBodyHttpServletRequest request) {
        try {
            byte[] body = request.getCachedBody();
            Map<?, ?> map = objectMapper.readValue(body, Map.class);
            return map != null ? (String) map.get("email") : null;
        } catch (Exception e) {
            return null;
        }
    }
}
