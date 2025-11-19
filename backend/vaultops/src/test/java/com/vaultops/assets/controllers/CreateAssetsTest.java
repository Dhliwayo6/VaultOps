package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.services.asset.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests for Create Asset Endpoint")
public class CreateAssetsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean
    private CreateAssetService createAssetService;
    @MockitoBean private GetAssetService getAssetService;
    @MockitoBean private GetAssetsService getAssetsService;
    @MockitoBean private DeleteAssetService deleteAssetService;
    @MockitoBean private UpdateAssetService updateAssetService;
    @MockitoBean private SearchAssetService searchAssetService;

    private Asset asset;
    private AssetDTO assetDTO;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook Laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);

        assetDTO = new AssetDTO(asset);

    }

    @Test
    @DisplayName("Create asset successfully and 201 status")
    void createAsset_ValidData_ShouldReturnCreatedStaus() throws Exception {
        when(createAssetService.execute(any(Asset.class))).thenReturn(ResponseEntity.status(201).body(assetDTO));

        mockMvc.perform(post("/api/asset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Macbook Laptop"))
                .andExpect(jsonPath("$.type").value("Laptop"))
                .andExpect(jsonPath("$.usageStatus").value("IN_USE"))
                .andExpect(jsonPath("$.conditionStatus").value("FAIR"))
                .andExpect(jsonPath("$.assignment").value("ASSIGNED"));

        verify(createAssetService, times(1)).execute(any(Asset.class));
    }

    @Test
    @DisplayName("Should return Error code 400 BAD REQUEST when required fields are missing")
    void createAsset_WithoutRequiredFields_ShouldReturnBadRequest() throws Exception {
        Asset invalidAsset = new Asset();
        invalidAsset.setName("Nokia 3310");
        mockMvc.perform(post("/api/asset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAsset))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle various asset types")
    void createAsset_WithDifferentTypes_ShouldCreateSuccessfully() throws Exception {
        asset.setType("Desktop");
        AssetDTO desktopDTO = new AssetDTO(asset);

        when(createAssetService.execute(any(Asset.class)))
                .thenReturn(ResponseEntity.status(201).body(desktopDTO));

        mockMvc.perform(post("/api/asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Desktop"));
    }






}
