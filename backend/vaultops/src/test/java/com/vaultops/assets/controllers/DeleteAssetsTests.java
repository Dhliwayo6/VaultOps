package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.model.Asset;
import com.vaultops.services.asset.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests for delete asset endpoint")
public class DeleteAssetsTests {

    @Autowired private MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @MockitoBean private CreateAssetService createAssetService;
    @MockitoBean private GetAssetService getAssetService;
    @MockitoBean private GetAssetsService getAssetsService;
    @MockitoBean private DeleteAssetService deleteAssetService;
    @MockitoBean private UpdateAssetService updateAssetService;
    @MockitoBean private SearchAssetService searchAssetService;

    @Test
    @DisplayName(("Should return 204 NO CONTENT status when asset exists"))
    void deleteAssetById_WhenAssetIsPresent_ShouldReturnNoContent() throws Exception{
        when(deleteAssetService.execute(1L)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/asset/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(deleteAssetService, times(1)).execute(1L);

    }

    @Test
    @DisplayName(("Should return 404 NOT FOUND status when asset does not exist"))
    void deleteAssetById_WhenAssetIsNotPresent_ShouldReturnNotFound() throws Exception{
        when(deleteAssetService.execute(1000L)).thenThrow(new AssetNotFoundException());

        mockMvc.perform(delete("/api/asset/1000")).andExpect(status().isNotFound());
        verify(deleteAssetService, times(1)).execute(1000L);

    }



}
