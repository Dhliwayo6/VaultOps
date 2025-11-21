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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test for Get All Assets endpoint")
public class GetAllAssetsTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CreateAssetService createAssetService;
    @MockitoBean private GetAssetService getAssetService;
    @MockitoBean private GetAssetsService getAssetsService;
    @MockitoBean private DeleteAssetService deleteAssetService;
    @MockitoBean private UpdateAssetService updateAssetService;
    @MockitoBean private SearchAssetService searchAssetService;

    private List<AssetDTO> assetDTOS;

    @BeforeEach
    void setUp() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook Laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);

        Asset asset1 = new Asset();
        asset.setId(2L);
        asset1.setName("MSi Modern 14 Laptop");
        asset1.setType("Laptop");
        asset1.setUsageStatus(Usage.STORAGE);
        asset1.setConditionStatus(ConditionStatus.EXCELLENT);
        asset1.setAssignment(Assignment.UNASSIGNED);

        Asset asset2 = new Asset();
        asset2.setId(3L);
        asset2.setName("ViewChoice Monitor");
        asset2.setType("Monitor");
        asset2.setUsageStatus(Usage.SERVICE);
        asset2.setConditionStatus(ConditionStatus.DAMAGED);
        asset2.setAssignment(Assignment.UNASSIGNED);

        assetDTOS = Arrays.asList(new AssetDTO(asset), new AssetDTO(asset1), new AssetDTO(asset2));
    }

    @Test
    @DisplayName("Should return list of all assets")
    void getAllAssets_WhenAssetsArePresent_ReturnList() throws Exception{
        when(getAssetsService.execute(null)).thenReturn(ResponseEntity.ok(assetDTOS));

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Macbook Laptop"))
                .andExpect(jsonPath("$[1].name").value("MSi Modern 14 Laptop"))
                .andExpect(jsonPath("$[2].name").value("ViewChoice Monitor"));

        verify(getAssetsService, times(1)).execute(null);
    }
}
