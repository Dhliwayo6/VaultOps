package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.config.JwtTokenProvider;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.repository.UserRepository;
import com.vaultops.services.RateLimitingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.rate-limiting.enabled=true")
@AutoConfigureMockMvc
@DisplayName("Abuse Protection and Rate Limiting Tests")
public class RateLimitingTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RateLimitingService rateLimitingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.mail.javamail.JavaMailSender mockMailSender;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        // Clear rate limiting maps to ensure fresh state for each test run
        // We can just rely on unique IPs or emails if needed, but clearing is cleaner.
        // Actually, we can use fresh/different emails for each test or let the mockMvc trigger it.
        userRepository.deleteAll();

        user = new User();
        user.setName("Rate Limit User");
        user.setEmail("ratelimit@example.com");
        user.setPassword("SecurePassword123!");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        token = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());
    }

    @Test
    @DisplayName("Verify exceeding login failure limit locks account/IP with 429 and Retry-After")
    void testLoginFailureRateLimiting() throws Exception {
        String loginEmail = "failure-login-" + System.currentTimeMillis() + "@example.com";

        // Perform 5 failed login attempts (should return 400 Bad Request since account doesn't exist)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "email", loginEmail,
                                    "password", "WrongPassword"
                            ))))
                    .andExpect(status().isBadRequest());
        }

        // The 6th attempt should be blocked with 429 Too Many Requests and have a Retry-After header
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", loginEmail,
                                "password", "WrongPassword"
                        ))))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    @DisplayName("Verify exceeding registration limit throws 429")
    void testRegistrationRateLimiting() throws Exception {
        String randomIp = "192.168.10." + (int)(Math.random() * 200 + 1);

        // Perform 3 registration attempts from the same IP (will return 400/200 depending on validation, but consumes tokens)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/auth/register")
                            .header("X-Forwarded-For", randomIp)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "name", "User " + i,
                                    "email", "reg-" + i + "-" + System.currentTimeMillis() + "@example.com",
                                    "password", "Password123!"
                            ))));
        }

        // 4th attempt from same IP should return 429
        mockMvc.perform(post("/api/auth/register")
                        .header("X-Forwarded-For", randomIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "User Blocked",
                                "email", "reg-blocked-" + System.currentTimeMillis() + "@example.com",
                                "password", "Password123!"
                        ))))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    @DisplayName("Verify exceeding export limit throws 429")
    void testExportRateLimiting() throws Exception {
        String testUserToken = jwtTokenProvider.generateAccessToken("export-user@example.com", "USER", "Export User", 999L);

        // Perform 5 export requests (should return 200 or 500 depending on DB state, but will consume tokens)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/export/csv")
                            .header("Authorization", "Bearer " + testUserToken));
        }

        // 6th export request should return 429
        mockMvc.perform(get("/api/export/csv")
                        .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    @DisplayName("Verify normal usage under limits is not rate limited")
    void testNormalUsageNotRateLimited() throws Exception {
        // Normal requests under limits (1 general request) should succeed
        mockMvc.perform(get("/api/assets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
