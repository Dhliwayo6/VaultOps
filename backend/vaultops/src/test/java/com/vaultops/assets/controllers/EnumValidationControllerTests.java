package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.services.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.vaultops.dtos.AssetDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser
@DisplayName("Enum Validation Controller Tests")
public class EnumValidationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssetService assetService;

    @Test
    @DisplayName("Should accept all valid values for Usage and ConditionStatus")
    void testValidEnums() throws Exception {
        for (Usage usage : Usage.values()) {
            for (ConditionStatus condition : ConditionStatus.values()) {
                Asset asset = new Asset();
                asset.setName("Test Laptop");
                asset.setType("Laptop");
                asset.setLocation("Office 1");
                asset.setAssignment(Assignment.UNASSIGNED);
                asset.setUsageStatus(usage);
                asset.setConditionStatus(condition);

                AssetDTO assetDTO = new AssetDTO(asset);
                when(assetService.create(any(Asset.class)))
                        .thenReturn(assetDTO);

                mockMvc.perform(post("/api/asset")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(asset)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.usageStatus").value(usage.name()))
                        .andExpect(jsonPath("$.conditionStatus").value(condition.name()));
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request with clear error message when invalid enum value is submitted")
    void testInvalidEnum() throws Exception {
        String invalidJson = "{"
                + "\"name\":\"Test Laptop\","
                + "\"type\":\"Laptop\","
                + "\"location\":\"Office 1\","
                + "\"assignment\":\"UNASSIGNED\","
                + "\"usageStatus\":\"IN_STORAGE\","
                + "\"conditionStatus\":\"EXCELLENT\""
                + "}";

        mockMvc.perform(post("/api/asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for enum: IN_STORAGE"));
    }
}
