package com.vaultops.assets.controllers;

import com.vaultops.config.JwtTokenProvider;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.Asset;
import com.vaultops.model.AuditLog;
import com.vaultops.model.User;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.AuditLogRepository;
import com.vaultops.repository.UserRepository;
import com.vaultops.services.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Audit Log and Centralized Aspect Integration Tests")
public class AuditLogTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AssetService assetService;

    private String userToken;
    private String adminToken;
    private User admin;
    private User regularUser;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();

        regularUser = new User();
        regularUser.setName("Regular User");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword("Password123!");
        regularUser.setRole(UserRole.USER);
        regularUser.setStatus(UserStatus.ACTIVE);
        regularUser = userRepository.save(regularUser);
        userToken = jwtTokenProvider.generateAccessToken(regularUser.getEmail(), regularUser.getRole().name(), regularUser.getName(), regularUser.getId());

        admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("Password123!");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.save(admin);
        adminToken = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRole().name(), admin.getName(), admin.getId());
    }

    @Test
    @DisplayName("Should reject GET /api/audit-log without JWT token with 401 status")
    void getAuditLogs_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/audit-log"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject GET /api/audit-log for regular user with 403 status")
    void getAuditLogs_WithUserToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/audit-log")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should accept GET /api/audit-log for admin with 200 status")
    void getAuditLogs_WithAdminToken_ShouldReturnOk() throws Exception {
        AuditLog log = AuditLog.builder()
                .actionType("TEST_ACTION")
                .actingUser("admin@example.com")
                .description("Seed action")
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);

        mockMvc.perform(get("/api/audit-log")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].actionType").value("TEST_ACTION"))
                .andExpect(jsonPath("$.content[0].actingUser").value("a***n@example.com")); // Masked email assertion
    }

    @Test
    @DisplayName("Should automatically record CREATE_ASSET audit entry on asset creation")
    void saveAsset_ShouldAutoCreateAuditLog() throws Exception {
        // Trigger via REST endpoint to ensure SecurityContext is populated
        mockMvc.perform(post("/api/asset")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content("{\"name\":\"Server Rack B\",\"type\":\"Hardware\",\"location\":\"CPT Vault\",\"assignment\":\"UNASSIGNED\",\"conditionStatus\":\"EXCELLENT\",\"usageStatus\":\"STORAGE\",\"serialNumber\":\"SN-98218\"}"))
                .andExpect(status().isCreated());

        List<AuditLog> logs = auditLogRepository.findAll();
        assertFalse(logs.isEmpty());
        AuditLog entry = logs.stream()
                .filter(l -> "CREATE_ASSET".equals(l.getActionType()))
                .findFirst()
                .orElseThrow();
        assertEquals("admin@example.com", entry.getActingUser());
        assertEquals("Asset", entry.getResourceType());
    }

    @Test
    @DisplayName("Should filter audit logs by actionType and date ranges correctly")
    void getAuditLogs_WithFiltering_ShouldReturnMatchingLogs() throws Exception {
        AuditLog log1 = AuditLog.builder()
                .actionType("CREATE_ASSET")
                .actingUser("admin@example.com")
                .description("Asset registered")
                .timestamp(LocalDateTime.now().minusDays(5))
                .build();
        AuditLog log2 = AuditLog.builder()
                .actionType("DELETE_ASSET")
                .actingUser("admin@example.com")
                .description("Asset deleted")
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log1);
        auditLogRepository.save(log2);

        // Filter by actionType
        mockMvc.perform(get("/api/audit-log")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("actionType", "DELETE_ASSET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].actionType").value("DELETE_ASSET"));

        // Filter by date range (log1 should be excluded if we filter for the last 2 days)
        String startDate = LocalDateTime.now().minusDays(2).toString();
        mockMvc.perform(get("/api/audit-log")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startDate", startDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].actionType").value("DELETE_ASSET"));
    }
}
