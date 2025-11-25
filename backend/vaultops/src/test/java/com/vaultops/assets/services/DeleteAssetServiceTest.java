package com.vaultops.assets.services;

import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.asset.DeleteAssetService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Asset Service tests")
public class DeleteAssetServiceTest {
    @Mock private AssetRepository assetRepository;
    @InjectMocks DeleteAssetService deleteAssetService;
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
    @DisplayName("Should delete asset when it exists")
    void execute_WhenAssetExists_ShouldDeleteAndReturnNotFound() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        doNothing().when(assetRepository).deleteById(1L);

        ResponseEntity<Void> response = deleteAssetService.execute(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).deleteById(1L);
    }

}
