package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test Update Asset endpoint")
public class UpdateAssetsTests {
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
    @DisplayName("Should update asset successfully")
    void updateAsset_WhenAssetIsPresent_ShouldReturnUpdatedAsset() throws Exception{
        Asset updatedAsset = new Asset();
        updatedAsset.setId(1L);
        updatedAsset.setName("Macbook Laptop - Updated");
        updatedAsset.setType("Laptop");
        updatedAsset.setUsageStatus(Usage.STORAGE);
        updatedAsset.setConditionStatus(ConditionStatus.FAIR);
        updatedAsset.setAssignment(Assignment.UNASSIGNED);

        AssetDTO updatedAssetDTO = new AssetDTO(updatedAsset);

        when(updateAssetService.execute(any(UpdateAssetCommand.class))).thenReturn(ResponseEntity.ok(updatedAssetDTO));

        mockMvc.perform(put("/api/asset/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAssetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Macbook Laptop - Updated"))
                .andExpect(jsonPath("$.type").value("Laptop"))
                .andExpect(jsonPath("$.usageStatus").value("STORAGE"))
                .andExpect(jsonPath("$.conditionStatus").value("FAIR"))
                .andExpect(jsonPath("$.assignment").value("UNASSIGNED"));

        verify(updateAssetService, times(1)).execute(any(UpdateAssetCommand.class));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND status when asset does exist")
    void updateAsset_WhenAssetDoesNotExist_ShouldReturnNotFound() throws Exception{
        when(updateAssetService.execute(any(UpdateAssetCommand.class))).thenThrow(new AssetNotFoundException());

        mockMvc.perform(put("/api/asset/1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isNotFound());

        verify(updateAssetService, times(1)).execute(any(UpdateAssetCommand.class));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when ID format is invalid")
    void updateAsset_WhenIDFormatIsInvalid_ShouldReturnBadRequest() throws Exception{
        mockMvc.perform(put("/api/asset/foo"))
                .andExpect(status().isBadRequest());

        verify(updateAssetService, never()).execute(any(UpdateAssetCommand.class));
    }

}
