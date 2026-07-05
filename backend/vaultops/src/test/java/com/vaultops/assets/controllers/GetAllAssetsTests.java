package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.services.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser
@DisplayName("Test for Get All Assets endpoint")
public class GetAllAssetsTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private AssetService assetService;

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
        asset1.setId(2L);
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
        when(assetService.getAllNonPaginated()).thenReturn(assetDTOS);

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Macbook Laptop"))
                .andExpect(jsonPath("$[1].name").value("MSi Modern 14 Laptop"))
                .andExpect(jsonPath("$[2].name").value("ViewChoice Monitor"));

        verify(assetService, times(1)).getAllNonPaginated();
    }

    @Test
    @DisplayName("Should return list of all assets with different usage statuses")
    void getAllAssets_ReturnListOfAssetsWithDifferentUsageStatuses() throws Exception{
        when(assetService.getAllNonPaginated()).thenReturn(assetDTOS);

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].usageStatus").value("IN_USE"))
                .andExpect(jsonPath("$[1].usageStatus").value("STORAGE"))
                .andExpect(jsonPath("$[2].usageStatus").value("SERVICE"));
    }

    @Test
    @DisplayName("Should throw custom exception NoResultsException when list is empty")
    void getAllAssets_WhenListIsEmpty_ThrowCustomException() throws Exception{
        when(assetService.getAllNonPaginated()).thenThrow(new NoResultsException());

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk());

        verify(assetService, times(1)).getAllNonPaginated();
    }

    @Test
    @DisplayName("Should return the correct content-type")
    void getAllAssets_ShouldReturnJSONContentType() throws Exception{
        when(assetService.getAllNonPaginated()).thenReturn(assetDTOS);

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Test should return results with populated fields")
    void getAssetsById_ShouldReturnFullFields() throws Exception {
        when(assetService.getAllNonPaginated()).thenReturn(assetDTOS);

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].usageStatus").exists())
                .andExpect(jsonPath("$[0].conditionStatus").exists())
                .andExpect(jsonPath("$[0].assignment").exists());
    }
}
