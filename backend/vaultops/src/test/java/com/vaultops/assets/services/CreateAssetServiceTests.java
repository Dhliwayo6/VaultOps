package com.vaultops.assets.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.asset.CreateAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Asset Service tests")
public class CreateAssetServiceTests {
    @Mock private AssetRepository assetRepository;
    @InjectMocks CreateAssetService createAssetService;
    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setId(1L);
        asset.setName("Laptop");
        asset.setType("Electronics");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setCreatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("Should create asset and return 201 CREATED status")
    void execute_WithValidAsset_ShouldSaveAndReturnCreated() {
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        ResponseEntity<AssetDTO> response = createAssetService.execute(asset);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Laptop");

        verify(assetRepository, times(1)).save(asset);
    }

    
}