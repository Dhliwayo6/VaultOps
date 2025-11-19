package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.AssetNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests for Get Asset By Id Endpoint")
public class GetAssetsByIdTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CreateAssetService createAssetService;
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
    @DisplayName("Should return valid asset when found by ID")
    void getAssetById_WhenAssetsIsPresent_ReturnAsset() throws Exception{
        when(getAssetService.execute(1L)).thenReturn(ResponseEntity.ok(assetDTO));

        mockMvc.perform(get("/api/asset/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Macbook Laptop"))
                .andExpect(jsonPath("$.type").value("Laptop"))
                .andExpect(jsonPath("$.usageStatus").value("IN_USE"))
                .andExpect(jsonPath("$.conditionStatus").value("FAIR"))
                .andExpect(jsonPath("$.assignment").value("ASSIGNED"));

        verify(getAssetService, times(1)).execute(1L);
    }

    @Test
    @DisplayName("Should 404 NOT FOUND error code when asset is not present")
    void getAssetById_WhenAssetNotFound_ReturnNotFoundError() throws Exception {
        when(getAssetService.execute(1000L)).thenThrow(new AssetNotFoundException());

        mockMvc.perform(get("/api/asset/1000")).andExpect(status().isNotFound());
        verify(getAssetService, times(1)).execute(1000L);
    }

    @Test
    @DisplayName("Test should return 400 BAD REQUEST error code for invalid id")
    void getAssetById_WhenInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/asset/foo")).andExpect(status().isBadRequest());

        verify(getAssetService, never()).execute(any());

    }

    @Test
    @DisplayName("Should return correct content-type")
    void getAssetsById_ShouldReturnContentAsJson() throws Exception{
        when(getAssetService.execute(1L)).thenReturn(ResponseEntity.ok(assetDTO));

        mockMvc.perform(get("/api/asset/1")).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

    }
}
