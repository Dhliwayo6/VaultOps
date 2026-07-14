package com.vaultops.assets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.dtos.ImportResponse;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.ImportStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.model.ImportLog;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.ImportLogRepository;
import com.vaultops.services.AssetService;
import com.vaultops.services.AssetStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@DisplayName("Async Import, Streaming Export, and Caching Integration Tests")
public class AsyncImportAndCacheTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImportLogRepository importLogRepository;

    @Autowired
    private com.vaultops.repository.ImportErrorRepository importErrorRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetStatsService assetStatsService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        importLogRepository.deleteAll();
        assetRepository.deleteAll();
        Cache cache = cacheManager.getCache("assetStatsCache");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("Verify that Excel export streams the file successfully with correct headers")
    void testExcelExportStreaming() throws Exception {
        Asset asset = new Asset();
        asset.setName("Laptop");
        asset.setType("Electronics");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.EXCELLENT);
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setSerialNumber("SN12345");
        asset.setLocationByName("Office");
        asset.setCreatedAt(LocalDateTime.now());
        assetRepository.save(asset);

        mockMvc.perform(get("/api/export/assets/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("assets-export-")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    @DisplayName("Verify successful async import updates status to PENDING -> IN_PROGRESS -> COMPLETED")
    void testAsyncImportSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "assets.csv",
                "text/csv",
                ("Name,Type,Location,Assignment,Serial Number,Condition Status,Usage Status,Assigned To,Purchase Date,Purchase Price\n" +
                 "Test Asset,Electronics,Office,unassigned,SN-123456,EXCELLENT,STORAGE,,,1200.00\n").getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/import/assets").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.importLogId").exists())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        ImportResponse response = objectMapper.readValue(responseStr, ImportResponse.class);
        Long logId = response.getImportLogId();

        ImportLog log = importLogRepository.findById(logId).orElseThrow();
        assertThat(log.getFileName()).isEqualTo("assets.csv");

        // Wait up to 5 seconds for background thread to run and update state to COMPLETED
        long startTime = System.currentTimeMillis();
        while (log.getStatus() != ImportStatus.COMPLETED && (System.currentTimeMillis() - startTime) < 5000) {
            Thread.sleep(100);
            log = importLogRepository.findById(logId).orElseThrow();
        }

        assertThat(log.getStatus()).isEqualTo(ImportStatus.COMPLETED);
        assertThat(log.getTotalRecords()).isEqualTo(1);
        assertThat(log.getSuccessCount()).isEqualTo(1);
        assertThat(log.getErrorCount()).isEqualTo(0);

        // Verify the asset is saved in database
        assertThat(assetRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Verify partial failure async import updates status to PARTIAL_SUCCESS and stores ImportError")
    void testAsyncImportPartialFailure() throws Exception {
        // Row 1 is invalid (missing Name), Row 2 is valid
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "partial.csv",
                "text/csv",
                ("Name,Type,Location,Assignment,Serial Number,Condition Status,Usage Status,Assigned To,Purchase Date,Purchase Price\n" +
                 ",Electronics,Office,unassigned,SN-99999,EXCELLENT,STORAGE,,,1200.00\n" +
                 "Valid Asset,Electronics,Office,unassigned,SN-88888,EXCELLENT,STORAGE,,,1200.00\n").getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/import/assets").file(file))
                .andExpect(status().isOk())
                .andReturn();

        ImportResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ImportResponse.class);
        Long logId = response.getImportLogId();

        ImportLog log = importLogRepository.findById(logId).orElseThrow();
        long startTime = System.currentTimeMillis();
        while (log.getStatus() != ImportStatus.PARTIAL_SUCCESS && (System.currentTimeMillis() - startTime) < 5000) {
            Thread.sleep(100);
            log = importLogRepository.findById(logId).orElseThrow();
        }

        assertThat(log.getStatus()).isEqualTo(ImportStatus.PARTIAL_SUCCESS);
        assertThat(log.getTotalRecords()).isEqualTo(2);
        assertThat(log.getSuccessCount()).isEqualTo(1);
        assertThat(log.getErrorCount()).isEqualTo(1);

        java.util.List<com.vaultops.model.ImportError> errors = importErrorRepository.findByImportLogId(logId);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getFieldName()).isEqualTo("name");
    }

    @Test
    @DisplayName("Verify fetching invalid job status throws JobNotFoundException")
    void testGetImportLogNotFound() throws Exception {
        mockMvc.perform(get("/api/import/logs/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify stats cache is populated and evicted correctly on asset modifications")
    void testStatsCachingAndEviction() {
        Cache cache = cacheManager.getCache("assetStatsCache");
        assertThat(cache).isNotNull();
        assertThat(cache.get("getDashboardStats_all")).isNull();
        assertThat(cache.get("getDashboardAlerts_all")).isNull();

        // Call the service to populate cache
        DashboardStatsDTO stats1 = assetStatsService.getDashboardStats(null);
        assertThat(stats1).isNotNull();
        
        var alerts1 = assetStatsService.getDashboardAlerts(null);
        assertThat(alerts1).isNotNull();

        // Verify they are now cached
        Cache.ValueWrapper statsWrapper = cache.get("getDashboardStats_all");
        assertThat(statsWrapper).isNotNull();
        assertThat(statsWrapper.get()).isEqualTo(stats1);

        Cache.ValueWrapper alertsWrapper = cache.get("getDashboardAlerts_all");
        assertThat(alertsWrapper).isNotNull();
        assertThat(alertsWrapper.get()).isEqualTo(alerts1);

        // Perform asset creation and check eviction
        Asset asset = new Asset();
        asset.setName("Cached Laptop");
        asset.setType("Electronics");
        asset.setUsageStatus(Usage.STORAGE);
        asset.setConditionStatus(ConditionStatus.GOOD);
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setSerialNumber("SN77777");
        asset.setLocationByName("Storage Room");
        asset.setCreatedAt(LocalDateTime.now());
        
        assetService.create(asset);

        // Cache must be evicted
        assertThat(cache.get("getDashboardStats_all")).isNull();
        assertThat(cache.get("getDashboardAlerts_all")).isNull();
    }
}
