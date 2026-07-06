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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Verification OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Account activated successfully"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "message", "New OTP code sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        setRefreshTokenCookie(response, result.getRefreshToken());

        User user = result.getUser();
        AuthResponse authResponse = new AuthResponse(
                result.getAccessToken(),
                new AuthResponse.UserInfo(user.getName(), user.getEmail(), user.getRole().name())
        );
        return ResponseEntity.ok(authResponse);
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
    public ResponseEntity<?> requestReset(@Valid @RequestBody RequestResetRequest request) {
        authService.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "message", "If the email is registered, a reset link has been sent."));
    }

    @PostMapping("/complete-reset")
    public ResponseEntity<?> completeReset(@Valid @RequestBody CompleteResetRequest request) {
        authService.completeReset(request.getToken(), request.getPassword());
        return ResponseEntity.ok(Map.of("success", true, "message", "Password has been reset successfully"));
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
}
