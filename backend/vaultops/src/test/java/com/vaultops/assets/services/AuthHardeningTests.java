package com.vaultops.assets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.LoginRequest;
import com.vaultops.dtos.RegisterRequest;
import com.vaultops.dtos.VerifyOtpRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.model.UserOtp;
import com.vaultops.model.PasswordResetToken;
import com.vaultops.model.UserRefreshToken;
import com.vaultops.repository.UserRepository;
import com.vaultops.repository.UserOtpRepository;
import com.vaultops.repository.PasswordResetTokenRepository;
import com.vaultops.repository.UserRefreshTokenRepository;
import com.vaultops.repository.DenylistedTokenRepository;
import com.vaultops.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Authentication Hardening Integration Tests")
public class AuthHardeningTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;

    @Autowired
    private DenylistedTokenRepository denylistedTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.mail.javamail.JavaMailSender mockMailSender;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepository.deleteAll();
        userRefreshTokenRepository.deleteAll();
        denylistedTokenRepository.deleteAll();
        userOtpRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Verify password request-reset returns generic success for nonexistent email")
    void testRequestResetNonexistentEmail() throws Exception {
        mockMvc.perform(post("/api/auth/request-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "nonexistent@example.com"))))
                .andExpect(status().isOk());
        // Verify no tokens were created
        assertThat(passwordResetTokenRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Verify password request-reset creates token for existing user")
    void testRequestResetExistingEmail() throws Exception {
        User user = createActiveUser("resetuser@example.com");

        mockMvc.perform(post("/api/auth/request-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "resetuser@example.com"))))
                .andExpect(status().isOk());

        assertThat(passwordResetTokenRepository.findAll()).hasSize(1);
        PasswordResetToken token = passwordResetTokenRepository.findAll().get(0);
        assertThat(token.getUser().getId()).isEqualTo(user.getId());
        assertThat(token.isUsed()).isFalse();
        assertThat(token.getExpiryTime()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Verify complete-reset works once and is single-use")
    void testCompleteResetSuccessAndSingleUse() throws Exception {
        User user = createActiveUser("completeuser@example.com");

        // Manually trigger request-reset logic
        mockMvc.perform(post("/api/auth/request-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "completeuser@example.com"))))
                .andExpect(status().isOk());

        // Find the created token. Since it is hashed in DB, we'll manually fetch it to see.
        PasswordResetToken tokenRecord = passwordResetTokenRepository.findAll().get(0);
        
        // Wait, because we hashed the token in DB, we don't know the rawToken from the DB.
        // Let's create a known rawToken and store its hash in the database to test!
        String rawToken = "my-secure-random-token";
        String tokenHash = com.vaultops.utils.SecurityUtils.hashSha256(rawToken);
        tokenRecord.setTokenHash(tokenHash);
        passwordResetTokenRepository.save(tokenRecord);

        // Perform complete-reset with valid rawToken
        mockMvc.perform(post("/api/auth/complete-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "token", rawToken,
                                "password", "NewSecurePassword123!"
                        ))))
                .andExpect(status().isOk());

        // Verify token is deleted/invalidated
        assertThat(passwordResetTokenRepository.findAll()).isEmpty();

        // Verify password has changed and can log in with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("completeuser@example.com");
        loginRequest.setPassword("NewSecurePassword123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify expired reset token is rejected")
    void testExpiredResetTokenRejected() throws Exception {
        User user = createActiveUser("expiredtoken@example.com");

        String rawToken = "expired-token-raw";
        String tokenHash = com.vaultops.utils.SecurityUtils.hashSha256(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken(
                user, tokenHash, LocalDateTime.now().minusMinutes(1)
        );
        passwordResetTokenRepository.save(resetToken);

        mockMvc.perform(post("/api/auth/complete-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "token", rawToken,
                                "password", "NewSecurePassword123!"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify logout denylists the access token")
    void testLogoutDenylistsAccessToken() throws Exception {
        User user = createActiveUser("logoutuser@example.com");

        // Login to get access token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("logoutuser@example.com");
        loginRequest.setPassword("SecurePass123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        Map<?, ?> responseMap = objectMapper.readValue(responseBody, Map.class);
        String accessToken = (String) responseMap.get("accessToken");
        Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");

        // Make sure it works before logout
        mockMvc.perform(get("/api/assets")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Logout
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(refreshCookie))
                .andExpect(status().isOk());

        // Verify access token is now rejected with 401
        mockMvc.perform(get("/api/assets")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify refresh token rotation and reuse detection")
    void testRefreshTokenRotationAndReuseDetection() throws Exception {
        User user = createActiveUser("rtruser@example.com");

        // Login to get refresh token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("rtruser@example.com");
        loginRequest.setPassword("SecurePass123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");
        assertThat(refreshCookie).isNotNull();

        // Perform refresh to get rotated tokens
        MvcResult refreshResult1 = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andReturn();

        Cookie rotatedCookie = refreshResult1.getResponse().getCookie("refreshToken");
        assertThat(rotatedCookie).isNotNull();
        assertThat(rotatedCookie.getValue()).isNotEqualTo(refreshCookie.getValue());

        // Verify the original cookie cannot be reused and revokes everything on reuse detection
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isUnauthorized());

        // Verify the rotated cookie was also revoked as a result of reuse detection!
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(rotatedCookie))
                .andExpect(status().isUnauthorized());

        // All tokens for this user must have been deleted
        assertThat(userRefreshTokenRepository.findByUser(user)).isEmpty();
    }

    private User createActiveUser(String email) {
        User user = new User();
        user.setName("Hardening User");
        user.setEmail(email);
        // Using plain string here since we save directly, but AuthService matches via encoder.
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12).encode("SecurePass123!"));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
