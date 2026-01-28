package com.vaultops.assets.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.asset.GetAssetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Asset Service tests")
public class GetAssetServiceTests {
    @Mock private AssetRepository assetRepository;
    @InjectMocks GetAssetService getAssetService;
    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setId(1L);
        asset.setName("Laptop");
        asset.setType("Electronics");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return asset when found")
    void execute_WhenAssetExists_ShouldReturnAsset() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        ResponseEntity<AssetDTO> response = getAssetService.execute(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Laptop");

        verify(assetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw AssetNotFoundException when asset not found")
    void execute_WhenAssetNotFound_ShouldThrowException() {
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getAssetService.execute(999L))
                .isInstanceOf(AssetNotFoundException.class);

        verify(assetRepository, times(1)).findById(999L);
    }
}
