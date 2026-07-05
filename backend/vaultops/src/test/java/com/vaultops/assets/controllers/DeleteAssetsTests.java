package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.services.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@DisplayName("Tests for delete asset endpoint")
public class DeleteAssetsTests {

    @Autowired private MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @MockitoBean private AssetService assetService;

    @Test
    @DisplayName("Should return 204 NO CONTENT status when asset exists")
    void deleteAssetById_WhenAssetIsPresent_ShouldReturnNoContent() throws Exception{
        doNothing().when(assetService).delete(1L);

        mockMvc.perform(delete("/api/asset/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(assetService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND status when asset does not exist")
    void deleteAssetById_WhenAssetIsNotPresent_ShouldReturnNotFound() throws Exception{
        doThrow(new AssetNotFoundException()).when(assetService).delete(1000L);

        mockMvc.perform(delete("/api/asset/1000")).andExpect(status().isNotFound());
        verify(assetService, times(1)).delete(1000L);
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST status for invalid ID format")
    void deleteAssetById_WhenIDFormatIsInvalid_ShouldReturnBadRequest() throws Exception{
        mockMvc.perform(delete("/api/asset/bar")).andExpect(status().isBadRequest());
        verify(assetService, never()).delete(any());
    }

    @Test
    @DisplayName("Should handle the same multiple delete requests gracefully")
    void deleteAssetById_WhenCalledMultipleTimes_ShouldHandleCorrectly() throws Exception{
        doNothing().when(assetService).delete(1L);
        mockMvc.perform(delete("/api/asset/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(assetService, times(1)).delete(1L);

        doThrow(new AssetNotFoundException()).when(assetService).delete(1000L);
        mockMvc.perform(delete("/api/asset/1000")).andExpect(status().isNotFound());
    }
}
