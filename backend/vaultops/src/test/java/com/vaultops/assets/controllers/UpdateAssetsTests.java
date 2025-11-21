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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook Laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);

    }


}
