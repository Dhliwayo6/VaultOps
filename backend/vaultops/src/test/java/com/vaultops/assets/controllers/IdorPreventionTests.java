package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.config.JwtTokenProvider;
import com.vaultops.enums.ImportStatus;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.ImportLog;
import com.vaultops.model.User;
import com.vaultops.repository.ImportLogRepository;
import com.vaultops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("IDOR Prevention and Ownership Checks Tests")
public class IdorPreventionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImportLogRepository importLogRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.mail.javamail.JavaMailSender mockMailSender;

    private User userA;
    private User userB;
    private User admin;

    private String tokenA;
    private String tokenB;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        importLogRepository.deleteAll();
        userRepository.deleteAll();

        // Create User A (USER role)
        userA = new User();
        userA.setName("User A");
        userA.setEmail("usera@example.com");
        userA.setPassword("DummyPass123!");
        userA.setRole(UserRole.USER);
        userA.setStatus(UserStatus.ACTIVE);
        userA.setCreatedAt(LocalDateTime.now());
        userA = userRepository.save(userA);
        tokenA = jwtTokenProvider.generateAccessToken(userA.getEmail(), userA.getRole().name(), userA.getName(), userA.getId());

        // Create User B (USER role)
        userB = new User();
        userB.setName("User B");
        userB.setEmail("userb@example.com");
        userB.setPassword("DummyPass123!");
        userB.setRole(UserRole.USER);
        userB.setStatus(UserStatus.ACTIVE);
        userB.setCreatedAt(LocalDateTime.now());
        userB = userRepository.save(userB);
        tokenB = jwtTokenProvider.generateAccessToken(userB.getEmail(), userB.getRole().name(), userB.getName(), userB.getId());

        // Create Admin User
        admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("DummyPass123!");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setCreatedAt(LocalDateTime.now());
        admin = userRepository.save(admin);
        tokenAdmin = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRole().name(), admin.getName(), admin.getId());
    }

    @Test
    @DisplayName("Verify user can access their own ImportLog, other user gets 404, Admin gets 200")
    void testImportLogOwnershipAccess() throws Exception {
        // Create an ImportLog owned by User A
        ImportLog log = new ImportLog();
        log.setUserId(userA.getEmail());
        log.setFileName("test-import.csv");
        log.setFileSize(1234L);
        log.setStatus(ImportStatus.PENDING);
        log.setStartedAt(LocalDateTime.now());
        log = importLogRepository.save(log);

        // 1. Owner (User A) polls status -> should be 200 OK
        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // 2. Non-owner (User B) polls status -> should be 404 Not Found (IDOR prevention)
        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // 3. Admin polls status -> should be 200 OK (role override)
        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify user can access their own profile, other user gets 404, Admin gets 200")
    void testUserProfileOwnershipAccess() throws Exception {
        // 1. Owner (User A) accesses profile A -> should be 200 OK
        mockMvc.perform(get("/api/users/" + userA.getId())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // 2. Non-owner (User B) accesses profile A -> should be 404 Not Found (IDOR prevention)
        mockMvc.perform(get("/api/users/" + userA.getId())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // 3. Admin accesses profile A -> should be 200 OK (role override)
        mockMvc.perform(get("/api/users/" + userA.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify ImportLog creator ID is taken from token, not client parameter spoofing")
    void testImportLogIdentityDerivation() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "assets.csv", "text/csv", "name,type,location\nTest Asset,Hardware,Cape Town".getBytes()
        );

        // User A starts an import
        mockMvc.perform(multipart("/api/import/assets")
                        .file(file)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // Verify the created log is assigned to User A, not "system" or any spoofed user
        assertThat(importLogRepository.findAll()).hasSize(1);
        ImportLog log = importLogRepository.findAll().get(0);
        assertThat(log.getUserId()).isEqualTo(userA.getEmail());
    }
}
