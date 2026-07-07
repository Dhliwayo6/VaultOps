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
import com.vaultops.services.AssetImportService;
import com.vaultops.services.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Import Controller Poll and Auth Integration Tests")
public class ImportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImportLogRepository importLogRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User adminUser;
    private User regularUser;
    private User otherUser;
    private String adminToken;
    private String userToken;
    private String otherUserToken;

    @BeforeEach
    void setUp() {
        importLogRepository.deleteAll();
        userRepository.deleteAll();

        // Create Admin
        adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("Password123!");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getEmail(), adminUser.getRole().name(), adminUser.getName(), adminUser.getId());

        // Create Regular User
        regularUser = new User();
        regularUser.setName("Regular User");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword("Password123!");
        regularUser.setRole(UserRole.USER);
        regularUser.setStatus(UserStatus.ACTIVE);
        regularUser = userRepository.save(regularUser);
        userToken = jwtTokenProvider.generateAccessToken(regularUser.getEmail(), regularUser.getRole().name(), regularUser.getName(), regularUser.getId());

        // Create Other User
        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("Password123!");
        otherUser.setRole(UserRole.USER);
        otherUser.setStatus(UserStatus.ACTIVE);
        otherUser = userRepository.save(otherUser);
        otherUserToken = jwtTokenProvider.generateAccessToken(otherUser.getEmail(), otherUser.getRole().name(), otherUser.getName(), otherUser.getId());
    }

    @Test
    @DisplayName("Should successfully return log when polled by its owner")
    void getImportLog_WhenPolledByOwner_ShouldReturnLog() throws Exception {
        ImportLog log = new ImportLog();
        log.setUserId(regularUser.getEmail());
        log.setFileName("test.csv");
        log.setFileSize(100L);
        log.setStatus(ImportStatus.PENDING);
        log.setStartedAt(LocalDateTime.now());
        log = importLogRepository.save(log);

        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(log.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.fileName").value("test.csv"));
    }

    @Test
    @DisplayName("Should return log when polled by an admin even if they are not the owner")
    void getImportLog_WhenPolledByAdmin_ShouldReturnLog() throws Exception {
        ImportLog log = new ImportLog();
        log.setUserId(regularUser.getEmail());
        log.setFileName("test.csv");
        log.setFileSize(100L);
        log.setStatus(ImportStatus.IN_PROGRESS);
        log.setStartedAt(LocalDateTime.now());
        log = importLogRepository.save(log);

        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(log.getId()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should fail with 404/AccessDenied when a non-owner/non-admin polls the log to prevent enumeration")
    void getImportLog_WhenPolledByNonOwner_ShouldReturnNotFound() throws Exception {
        ImportLog log = new ImportLog();
        log.setUserId(regularUser.getEmail());
        log.setFileName("test.csv");
        log.setFileSize(100L);
        log.setStatus(ImportStatus.COMPLETED);
        log.setStartedAt(LocalDateTime.now());
        log = importLogRepository.save(log);

        mockMvc.perform(get("/api/import/logs/" + log.getId())
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isNotFound()); // SecurityService returns 404 (JobNotFoundException)
    }

    @Test
    @DisplayName("Should return 404 when log ID does not exist")
    void getImportLog_WhenNotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/import/logs/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return logs with correct statuses (COMPLETED, FAILED)")
    void getImportLog_ShouldShowCorrectStatus() throws Exception {
        ImportLog log1 = new ImportLog();
        log1.setUserId(regularUser.getEmail());
        log1.setFileName("completed.csv");
        log1.setFileSize(100L);
        log1.setStatus(ImportStatus.COMPLETED);
        log1.setStartedAt(LocalDateTime.now());
        log1 = importLogRepository.save(log1);

        mockMvc.perform(get("/api/import/logs/" + log1.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        ImportLog log2 = new ImportLog();
        log2.setUserId(regularUser.getEmail());
        log2.setFileName("failed.csv");
        log2.setFileSize(100L);
        log2.setStatus(ImportStatus.FAILED);
        log2.setErrorMessage("Malformed headers");
        log2.setStartedAt(LocalDateTime.now());
        log2 = importLogRepository.save(log2);

        mockMvc.perform(get("/api/import/logs/" + log2.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.errorMessage").value("Malformed headers"));
    }
}
