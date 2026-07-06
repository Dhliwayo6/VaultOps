package com.vaultops.services;

import com.vaultops.config.JwtTokenProvider;
import com.vaultops.dtos.LoginRequest;
import com.vaultops.dtos.RegisterRequest;
import com.vaultops.dtos.VerifyOtpRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.model.UserOtp;
import com.vaultops.model.DenylistedToken;
import com.vaultops.model.UserRefreshToken;
import com.vaultops.model.PasswordResetToken;
import com.vaultops.repository.UserOtpRepository;
import com.vaultops.repository.UserRepository;
import com.vaultops.repository.DenylistedTokenRepository;
import com.vaultops.repository.UserRefreshTokenRepository;
import com.vaultops.repository.PasswordResetTokenRepository;
import com.vaultops.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DenylistedTokenRepository denylistedTokenRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

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

        // Enforce constant-time comparison for OTP validation
        if (!SecurityUtils.constantTimeEquals(userOtp.getCode(), request.getCode())) {
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

    @Transactional
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Reject login for unverified accounts with the exact same generic message
        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Store refresh token hash in database for rotation/revocation
        String tokenHash = SecurityUtils.hashSha256(refreshToken);
        UserRefreshToken userRefreshToken = new UserRefreshToken(
                user, tokenHash, LocalDateTime.now().plusDays(7)
        );
        userRefreshTokenRepository.save(userRefreshToken);

        return new LoginResult(accessToken, refreshToken, user);
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public LoginResult refresh(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user session"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Invalid user session");
        }

        String tokenHash = SecurityUtils.hashSha256(refreshToken);
        UserRefreshToken storedToken = userRefreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // Reuse detection / expiry validation
        if (storedToken.isRevoked() || storedToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            // Revoke all refresh tokens for this user immediately
            userRefreshTokenRepository.deleteByUser(user);
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Mark current token as revoked
        storedToken.setRevoked(true);
        userRefreshTokenRepository.save(storedToken);

        // Generate new rotated access and refresh tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        String newTokenHash = SecurityUtils.hashSha256(newRefreshToken);
        UserRefreshToken newUserRefreshToken = new UserRefreshToken(
                user, newTokenHash, LocalDateTime.now().plusDays(7)
        );
        userRefreshTokenRepository.save(newUserRefreshToken);

        return new LoginResult(newAccessToken, newRefreshToken, user);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            try {
                // Decode token to extract expiration
                com.auth0.jwt.interfaces.DecodedJWT decoded = com.auth0.jwt.JWT.decode(accessToken);
                Date expiresAt = decoded.getExpiresAt();
                LocalDateTime expiry = expiresAt.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                String tokenHash = SecurityUtils.hashSha256(accessToken);
                if (!denylistedTokenRepository.existsByTokenHash(tokenHash)) {
                    DenylistedToken denylistedToken = new DenylistedToken(tokenHash, expiry);
                    denylistedTokenRepository.save(denylistedToken);
                }
            } catch (Exception e) {
                // Ignore parsing errors on logout
            }
        }

        if (refreshToken != null) {
            String tokenHash = SecurityUtils.hashSha256(refreshToken);
            userRefreshTokenRepository.findByTokenHash(tokenHash).ifPresent(userRefreshTokenRepository::delete);
        }
    }

    @Transactional
    public void requestReset(String email) {
        // Return same generic response shape (completed in controller) to prevent enumeration.
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Generate cryptographically random token
            String rawToken = UUID.randomUUID().toString();
            String tokenHash = SecurityUtils.hashSha256(rawToken);

            // Store hashed token with 15 minutes expiry
            PasswordResetToken resetToken = new PasswordResetToken(
                    user, tokenHash, LocalDateTime.now().plusMinutes(15)
            );
            passwordResetTokenRepository.save(resetToken);

            // Email reset link containing the raw token
            String resetLink = "http://localhost:5173/reset-password?token=" + rawToken;
            mailService.sendPlainTextEmail(
                    user.getEmail(),
                    "VaultOps - Password Reset Request",
                    "Hello " + user.getName() + ",\n\n" +
                    "To reset your password, please click the link below or copy and paste it into your browser:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 15 minutes.\n\n" +
                    "If you did not request a password reset, please ignore this email."
            );
        }
    }

    @Transactional
    public void completeReset(String rawToken, String newPassword) {
        String tokenHash = SecurityUtils.hashSha256(rawToken);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        // Enforce constant-time comparison on verification hash
        if (!SecurityUtils.constantTimeEquals(resetToken.getTokenHash(), tokenHash)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        if (resetToken.isUsed() || resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        // Set used flag
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Update hashed password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate token immediately
        passwordResetTokenRepository.delete(resetToken);
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
