package com.vaultops.assets.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.asset.GetAssetsService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Asset Service tests")
public class GetAssetsServiceTests {
    @Mock private AssetRepository assetRepository;
    @InjectMocks GetAssetsService getAssetsService;
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
    @DisplayName("Should return all assets when they exist")
    void execute_WhenAssetsExist_ShouldReturnList() {
        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setName("Monitor");
        asset2.setType("Electronics");

        List<Asset> assets = Arrays.asList(asset, asset2);
        when(assetRepository.findAll()).thenReturn(assets);

        ResponseEntity<List<AssetDTO>> response = getAssetsService.execute(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(AssetDTO::getName)
                .containsExactly("Laptop", "Monitor");

        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw custom NoResultsException when no assets exist")
    void execute_WhenNoAssets_ShouldThrowException() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> getAssetsService.execute(null))
                .isInstanceOf(NoResultsException.class);

        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should convert all assets to DTOs")
    void execute_ShouldConvertAllAssetsToDTO() {
        List<Asset> assets = Arrays.asList(asset, asset, asset);
        when(assetRepository.findAll()).thenReturn(assets);

        ResponseEntity<List<AssetDTO>> response = getAssetsService.execute(null);

        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody())
                .allMatch(dto -> dto.getName().equals("Laptop"));
    }
}
