package com.vaultops.services;

import com.vaultops.config.JwtTokenProvider;
import com.vaultops.dtos.LoginRequest;
import com.vaultops.dtos.RegisterRequest;
import com.vaultops.dtos.VerifyOtpRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.model.UserOtp;
import com.vaultops.repository.UserOtpRepository;
import com.vaultops.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Data
    @AllArgsConstructor
    public static class LoginResult {
        private String accessToken;
        private String refreshToken;
        private User user;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.PENDING);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        sendVerificationOtp(savedUser);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("User is already active");
        }

        UserOtp userOtp = userOtpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if (!userOtp.getCode().equals(request.getCode())) {
            throw new IllegalArgumentException("Incorrect OTP code");
        }

        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP code has expired");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        userOtpRepository.delete(userOtp);
    }

    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("User is already active");
        }

        // Clean up any old OTP first
        userOtpRepository.findByUserId(user.getId()).ifPresent(userOtpRepository::delete);
        sendVerificationOtp(user);
    }

    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalArgumentException("Account is not activated yet. Please verify your OTP.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new LoginResult(accessToken, refreshToken, user);
    }

    @Transactional(readOnly = true)
    public String refresh(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user session"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Invalid user session");
        }

        return jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());
    }

    private void sendVerificationOtp(User user) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        UserOtp userOtp = new UserOtp();
        userOtp.setUser(user);
        userOtp.setCode(code);
        userOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        userOtpRepository.save(userOtp);
        emailService.sendOtp(user.getEmail(), code);
    }
}
