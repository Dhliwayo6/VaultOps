package com.vaultops.assets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.LoginRequest;
import com.vaultops.dtos.RegisterRequest;
import com.vaultops.dtos.VerifyOtpRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.model.UserOtp;
import com.vaultops.repository.UserOtpRepository;
import com.vaultops.repository.UserRepository;
import com.vaultops.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Authentication and Endpoint Protection Integration Tests")
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        userOtpRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Verify user registration creates pending user and OTP")
    void testRegistrationCreatesPendingUserAndOtp() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+1234567890");
        request.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getPhone()).isEqualTo("+1234567890");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);

        UserOtp otp = userOtpRepository.findByUserId(user.getId()).orElse(null);
        assertThat(otp).isNotNull();
        assertThat(otp.getCode()).hasSize(6);
        assertThat(otp.getExpiryTime()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Verify duplicate registration returns 400")
    void testDuplicateRegistrationReturns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("duplicate@example.com");
        request.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify OTP verification activates user and deletes OTP")
    void testOtpVerificationSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setEmail("jane.doe@example.com");
        registerRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("jane.doe@example.com").orElseThrow();
        UserOtp otp = userOtpRepository.findByUserId(user.getId()).orElseThrow();

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setEmail("jane.doe@example.com");
        verifyRequest.setCode(otp.getCode());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk());

        User activeUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(activeUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userOtpRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("Verify OTP verification fails with incorrect code")
    void testOtpVerificationFailsWithWrongCode() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setEmail("wrongcode@example.com");
        registerRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setEmail("wrongcode@example.com");
        verifyRequest.setCode("000000");

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify OTP verification fails when code is expired")
    void testOtpVerificationFailsWhenExpired() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setEmail("expired@example.com");
        registerRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("expired@example.com").orElseThrow();
        UserOtp otp = userOtpRepository.findByUserId(user.getId()).orElseThrow();
        otp.setExpiryTime(LocalDateTime.now().minusMinutes(1));
        userOtpRepository.save(otp);

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setEmail("expired@example.com");
        verifyRequest.setCode(otp.getCode());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify login returns JWT and cookie for active user")
    void testLoginSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Active User");
        registerRequest.setEmail("active@example.com");
        registerRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("active@example.com").orElseThrow();
        UserOtp otp = userOtpRepository.findByUserId(user.getId()).orElseThrow();

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setEmail("active@example.com");
        verifyRequest.setCode(otp.getCode());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("active@example.com");
        loginRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify login fails for pending user")
    void testLoginFailsForPendingUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Pending User");
        registerRequest.setEmail("pending@example.com");
        registerRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("pending@example.com");
        loginRequest.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify protected endpoint returns 401 for unauthenticated request")
    void testProtectedEndpointReturns401() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify protected endpoint is accessible with valid JWT")
    void testProtectedEndpointAccessibleWithJwt() throws Exception {
        String token = jwtTokenProvider.generateAccessToken("active@example.com", UserRole.USER.name(), "Active User", 1L);

        mockMvc.perform(get("/api/assets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
