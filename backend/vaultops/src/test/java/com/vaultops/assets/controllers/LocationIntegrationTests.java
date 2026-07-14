package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.config.JwtTokenProvider;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.Asset;
import com.vaultops.model.Location;
import com.vaultops.model.User;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.LocationRepository;
import com.vaultops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Location Integration and Security Tests")
public class LocationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
        // Clear non-default locations
        locationRepository.findAll().stream()
                .filter(l -> l.getId() != 1L)
                .forEach(l -> locationRepository.delete(l));
        userRepository.deleteAll();

        // Create standard user
        User user = new User();
        user.setName("Regular User");
        user.setEmail("user@example.com");
        user.setPassword("Password123!");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        userToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());

        // Create admin user
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
    @DisplayName("Should retrieve locations list for authenticated user")
    void getLocations_WithUserToken_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/locations")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Unassigned"));
    }

    @Test
    @DisplayName("Should reject Location creation for non-admin user")
    void createLocation_WithUserToken_ShouldReturnForbidden() throws Exception {
        Location newLocation = new Location();
        newLocation.setName("Forbidden Zone");
        newLocation.setMaxCapacity(10);

        mockMvc.perform(post("/api/location")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow Location creation for admin user")
    void createLocation_WithAdminToken_ShouldReturnCreated() throws Exception {
        Location newLocation = new Location();
        newLocation.setName("Warehouse B");
        newLocation.setMaxCapacity(50);
        newLocation.setDescription("High security vault chamber");
        newLocation.setAddress("456 Vault Way");

        mockMvc.perform(post("/api/location")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Warehouse B"))
                .andExpect(jsonPath("$.maxCapacity").value(50));
    }

    @Test
    @DisplayName("Should reject Location update for non-admin user")
    void updateLocation_WithUserToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/location/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hacked Name\",\"maxCapacity\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow Location update for admin user")
    void updateLocation_WithAdminToken_ShouldReturnOk() throws Exception {
        Location location = new Location();
        location.setName("Server Room 1");
        location.setMaxCapacity(5);
        location = locationRepository.save(location);

        location.setMaxCapacity(10);

        mockMvc.perform(put("/api/location/" + location.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxCapacity").value(10));
    }

    @Test
    @DisplayName("Should reject delete default location (ID 1)")
    void deleteLocation_DefaultLocation_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/location/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject delete location with active assets")
    void deleteLocation_WithActiveAssets_ShouldReturnBadRequest() throws Exception {
        Location loc = new Location();
        loc.setName("Chamber C");
        loc.setMaxCapacity(10);
        loc = locationRepository.save(loc);

        // Assign asset to it
        Asset asset = new Asset();
        asset.setName("Vault Key");
        asset.setType("Physical");
        asset.setSerialNumber("KEY-998877");
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setConditionStatus(ConditionStatus.EXCELLENT);
        asset.setUsageStatus(Usage.STORAGE);
        asset.setLocation(loc);
        assetRepository.save(asset);

        mockMvc.perform(delete("/api/location/" + loc.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should enforce capacity limit on Asset creation")
    void createAsset_OverCapacityLimit_ShouldReturnBadRequest() throws Exception {
        Location tightLocation = new Location();
        tightLocation.setName("Small Safe");
        tightLocation.setMaxCapacity(1);
        tightLocation = locationRepository.save(tightLocation);

        // Fill capacity
        Asset asset1 = new Asset();
        asset1.setName("Gold Bar 1");
        asset1.setType("Commodity");
        asset1.setSerialNumber("GB-01");
        asset1.setAssignment(Assignment.UNASSIGNED);
        asset1.setConditionStatus(ConditionStatus.EXCELLENT);
        asset1.setUsageStatus(Usage.STORAGE);
        asset1.setLocation(tightLocation);
        assetRepository.save(asset1);

        // Try to add second asset (exceeding capacity limit 1)
        mockMvc.perform(post("/api/asset")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gold Bar 2\",\"type\":\"Commodity\",\"serialNumber\":\"GB-02\",\"assignment\":\"UNASSIGNED\",\"conditionStatus\":\"EXCELLENT\",\"usageStatus\":\"STORAGE\",\"location\":{\"id\":" + tightLocation.getId() + "}}"))
                .andExpect(status().isBadRequest());
    }
}
