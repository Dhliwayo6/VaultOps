package com.vaultops.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vaultops.dtos.*;
import com.vaultops.model.User;
import com.vaultops.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Verification OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIP(httpRequest);
        log.info("OTP verification attempt - Email: {}, IP: {}", request.getEmail(), ip);
        try {
            authService.verifyOtp(request);
            log.info("OTP verification success - Email: {}, IP: {}", request.getEmail(), ip);
            return ResponseEntity.ok(Map.of("success", true, "message", "Account activated successfully"));
        } catch (Exception e) {
            log.warn("OTP verification failure - Email: {}, IP: {}, Reason: {}", request.getEmail(), ip, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "message", "New OTP code sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        String ip = getClientIP(httpRequest);
        log.info("Authentication attempt - Email: {}, IP: {}", request.getEmail(), ip);
        try {
            AuthService.LoginResult result = authService.login(request);
            log.info("Authentication success - Email: {}, IP: {}", request.getEmail(), ip);
            setRefreshTokenCookie(response, result.getRefreshToken());

            User user = result.getUser();
            AuthResponse authResponse = new AuthResponse(
                    result.getAccessToken(),
                    new AuthResponse.UserInfo(user.getName(), user.getEmail(), user.getRole().name())
            );
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.warn("Authentication failure - Email: {}, IP: {}, Reason: {}", request.getEmail(), ip, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No refresh token provided"));
        }

        try {
            AuthService.LoginResult result = authService.refresh(refreshToken);
            setRefreshTokenCookie(response, result.getRefreshToken());

            User user = result.getUser();
            AuthResponse authResponse = new AuthResponse(
                    result.getAccessToken(),
                    new AuthResponse.UserInfo(user.getName(), user.getEmail(), user.getRole().name())
            );
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        String refreshToken = extractRefreshToken(request);

        authService.logout(accessToken, refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@Valid @RequestBody RequestResetRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIP(httpRequest);
        log.info("Password reset request attempt - Email: {}, IP: {}", request.getEmail(), ip);
        authService.requestReset(request.getEmail());
        log.info("Password reset request success - Email: {}, IP: {}", request.getEmail(), ip);
        return ResponseEntity.ok(Map.of("success", true, "message", "If the email is registered, a reset link has been sent."));
    }

    @PostMapping("/complete-reset")
    public ResponseEntity<?> completeReset(@Valid @RequestBody CompleteResetRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIP(httpRequest);
        log.info("Password reset completion attempt - IP: {}", ip);
        try {
            authService.completeReset(request.getToken(), request.getPassword());
            log.info("Password reset completion success - IP: {}", ip);
            return ResponseEntity.ok(Map.of("success", true, "message", "Password has been reset successfully"));
        } catch (Exception e) {
            log.warn("Password reset completion failure - IP: {}, Reason: {}", ip, e.getMessage());
            throw e;
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
