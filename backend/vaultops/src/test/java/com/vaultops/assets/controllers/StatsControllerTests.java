package com.vaultops.assets.controllers;

import com.vaultops.config.JwtTokenProvider;
import com.vaultops.services.RateLimitingService;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.Asset;
import com.vaultops.model.Maintenance;
import com.vaultops.model.User;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.MaintenanceRepository;
import com.vaultops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.rate-limiting.enabled=true")
@AutoConfigureMockMvc
@DisplayName("Stats Controller Endpoint and Security Tests")
public class StatsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RateLimitingService rateLimitingService;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        maintenanceRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();
        rateLimitingService.clearAllBuckets();

        // 1. Create a regular user and get token
        User user = new User();
        user.setName("Regular User");
        user.setEmail("user@example.com");
        user.setPassword("Password123!");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        userToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getName(), user.getId());

        // 2. Create an admin user and get token
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("Password123!");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.save(admin);
        adminToken = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRole().name(), admin.getName(), admin.getId());

        // 3. Seed some dummy data
        Asset asset1 = new Asset();
        asset1.setName("Dell XPS Laptop");
        asset1.setType("Laptop");
        asset1.setLocationByName("HQ");
        asset1.setSerialNumber("SN-XYZ-111");
        asset1.setAssignment(Assignment.UNASSIGNED);
        asset1.setUsageStatus(Usage.IN_USE);
        asset1.setConditionStatus(ConditionStatus.EXCELLENT);
        asset1.setPurchasePrice(new BigDecimal("1500.00"));
        asset1.setPurchaseDate(LocalDate.now());
        asset1.setCreatedAt(LocalDateTime.now().minusMonths(1));
        assetRepository.save(asset1);

        Asset asset2 = new Asset();
        asset2.setName("Server Pro");
        asset2.setType("Server");
        asset2.setLocationByName("Datacenter");
        asset2.setSerialNumber("SN-XYZ-222");
        asset2.setAssignment(Assignment.UNASSIGNED);
        asset2.setUsageStatus(Usage.STORAGE);
        asset2.setConditionStatus(ConditionStatus.GOOD);
        asset2.setPurchasePrice(new BigDecimal("3500.00"));
        asset2.setPurchaseDate(LocalDate.now());
        asset2.setCreatedAt(LocalDateTime.now());
        assetRepository.save(asset2);

        Maintenance m1 = new Maintenance();
        m1.setAsset(asset1);
        m1.setDescription("Screen fix");
        m1.setPerformedBy("Dell Support");
        m1.setCost(new BigDecimal("200.00"));
        m1.setDate(LocalDate.now());
        maintenanceRepository.save(m1);
    }

    @Test
    @DisplayName("Should accept GET /api/stats/trends for both USER and ADMIN")
    void getMonthlyTrends_ShouldReturnOk() throws Exception {
        // Test USER
        mockMvc.perform(get("/api/stats/trends")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[5].month", is(LocalDate.now().toString().substring(0, 7))))
                .andExpect(jsonPath("$[5].assetCount", is(2))) // asset2 was created now
                .andExpect(jsonPath("$[5].maintenanceCount", is(1))); // m1 was created now

        // Test ADMIN
        mockMvc.perform(get("/api/stats/trends")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should accept GET /api/stats/categories for both USER and ADMIN")
    void getCategoryConditionStats_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/stats/categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category", notNullValue()))
                .andExpect(jsonPath("$[0].conditionStatus", notNullValue()))
                .andExpect(jsonPath("$[0].count", notNullValue()));
    }

    @Test
    @DisplayName("Should accept GET /api/stats/value for ADMIN but deny USER with 403 status")
    void getFinancialStats_ShouldEnforceGatedAccess() throws Exception {
        // ADMIN should succeed
        mockMvc.perform(get("/api/stats/value")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssetValuation", is(5000.00)))
                .andExpect(jsonPath("$.averageAssetValue", is(2500.00)))
                .andExpect(jsonPath("$.totalMaintenanceExpenditure", is(200.00)));

        // USER should be forbidden (403)
        mockMvc.perform(get("/api/stats/value")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should enforce rate limit of 20 requests per minute on stats endpoints")
    void getStats_RateLimiting_ShouldEnforceLimit() throws Exception {
        // Let's call /api/stats/trends multiple times.
        // The limit is 20. Call 21 times.
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/api/stats/trends")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());
        }

        // 21st call should return 429 Too Many Requests
        mockMvc.perform(get("/api/stats/trends")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should return alerts for damaged, overdue repairs, and expiring warranties")
    void getDashboardAlerts_ShouldReturnCorrectAlerts() throws Exception {
        // Clear setup seed data first to test alerts cleanly
        maintenanceRepository.deleteAll();
        assetRepository.deleteAll();
        rateLimitingService.clearAllBuckets();

        // 1. Seed a damaged asset
        Asset damagedAsset = new Asset();
        damagedAsset.setName("Damaged Laptop");
        damagedAsset.setType("Laptop");
        damagedAsset.setLocationByName("HQ");
        damagedAsset.setSerialNumber("SN-DMG");
        damagedAsset.setAssignment(Assignment.UNASSIGNED);
        damagedAsset.setUsageStatus(Usage.IN_USE);
        damagedAsset.setConditionStatus(ConditionStatus.DAMAGED);
        damagedAsset.setPurchasePrice(new BigDecimal("1000.00"));
        damagedAsset.setPurchaseDate(LocalDate.now());
        damagedAsset.setCreatedAt(LocalDateTime.now());
        assetRepository.save(damagedAsset);

        // 2. Seed an overdue repair asset (usageStatus = SERVICE, created > 14 days ago)
        Asset overdueAsset = new Asset();
        overdueAsset.setName("Repairing Server");
        overdueAsset.setType("Server");
        overdueAsset.setLocationByName("HQ");
        overdueAsset.setSerialNumber("SN-REP");
        overdueAsset.setAssignment(Assignment.UNASSIGNED);
        overdueAsset.setUsageStatus(Usage.SERVICE);
        overdueAsset.setConditionStatus(ConditionStatus.GOOD);
        overdueAsset.setPurchasePrice(new BigDecimal("2000.00"));
        overdueAsset.setPurchaseDate(LocalDate.now());
        overdueAsset.setCreatedAt(LocalDateTime.now().minusDays(15));
        Asset savedOverdue = assetRepository.save(overdueAsset);
        jdbcTemplate.update("UPDATE assets SET created_at = ? WHERE id = ?", 
            java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(15)), 
            savedOverdue.getId());

        // 3. Seed an expiring warranty asset (warrantyExpiryDate within 30 days)
        Asset warrantyAsset = new Asset();
        warrantyAsset.setName("New Tablet");
        warrantyAsset.setType("Tablet");
        warrantyAsset.setLocationByName("HQ");
        warrantyAsset.setSerialNumber("SN-WRR");
        warrantyAsset.setAssignment(Assignment.UNASSIGNED);
        warrantyAsset.setUsageStatus(Usage.IN_USE);
        warrantyAsset.setConditionStatus(ConditionStatus.EXCELLENT);
        warrantyAsset.setPurchasePrice(new BigDecimal("500.00"));
        warrantyAsset.setPurchaseDate(LocalDate.now());
        warrantyAsset.setWarrantyExpiryDate(LocalDate.now().plusDays(10));
        warrantyAsset.setCreatedAt(LocalDateTime.now());
        assetRepository.save(warrantyAsset);

        mockMvc.perform(get("/api/stats/alerts")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is("damaged")))
                .andExpect(jsonPath("$[0].type", is("danger")))
                .andExpect(jsonPath("$[0].count", is(1)))
                .andExpect(jsonPath("$[1].id", is("overdue-repair")))
                .andExpect(jsonPath("$[1].type", is("warning")))
                .andExpect(jsonPath("$[1].count", is(1)))
                .andExpect(jsonPath("$[2].id", is("expiring-warranty")))
                .andExpect(jsonPath("$[2].type", is("info")))
                .andExpect(jsonPath("$[2].count", is(1)));
    }
}
