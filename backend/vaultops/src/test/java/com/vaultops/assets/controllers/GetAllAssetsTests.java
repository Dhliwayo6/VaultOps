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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        
    }
}
