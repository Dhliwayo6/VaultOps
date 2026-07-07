package com.vaultops.assets.controllers;

import com.vaultops.config.JwtTokenProvider;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.Asset;
import com.vaultops.model.User;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("JWT Security Endpoint Protection Tests")
public class SecurityJwtEndpointTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("Regular User");
        user.setEmail("user@example.com");
        user.setPassword("Password123!");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        userToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());

        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("Password123!");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.save(admin);
        adminToken = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRole().name(), admin.getName(), admin.getId());
    }

    @Test
    @DisplayName("Should reject GET /api/assets without JWT token with 401 status")
    void getAssets_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should accept GET /api/assets with valid user JWT token")
    void getAssets_WithValidUserToken_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/assets")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should accept POST /api/asset for valid user role")
    void createAsset_WithUserToken_ShouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/asset")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"name\":\"Server\",\"type\":\"Hardware\",\"location\":\"Cape Town\",\"assignment\":\"UNASSIGNED\",\"conditionStatus\":\"EXCELLENT\",\"usageStatus\":\"STORAGE\",\"serialNumber\":\"SN-112233\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject DELETE /api/asset/{id} for non-admin user role with 403 status")
    void deleteAsset_WithUserToken_ShouldReturnForbidden() throws Exception {
        Asset asset = new Asset();
        asset.setName("Server");
        asset.setType("Hardware");
        asset.setLocation("Cape Town");
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setConditionStatus(ConditionStatus.EXCELLENT);
        asset.setUsageStatus(Usage.STORAGE);
        asset.setSerialNumber("SN-XYZ");
        asset = assetRepository.save(asset);

        mockMvc.perform(delete("/api/asset/" + asset.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should accept DELETE /api/asset/{id} with valid admin JWT token")
    void deleteAsset_WithAdminToken_ShouldReturnNoContent() throws Exception {
        Asset asset = new Asset();
        asset.setName("Server");
        asset.setType("Hardware");
        asset.setLocation("Cape Town");
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setConditionStatus(ConditionStatus.EXCELLENT);
        asset.setUsageStatus(Usage.STORAGE);
        asset.setSerialNumber("SN-XYZ");
        asset = assetRepository.save(asset);

        mockMvc.perform(delete("/api/asset/" + asset.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
