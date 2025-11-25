package com.vaultops.assets.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.asset.UpdateAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Asset Service tests")
public class UpdateAssetServiceTests {
    @Mock private AssetRepository assetRepository;
    @InjectMocks UpdateAssetService updateAssetService;
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
    @DisplayName("Should update asset when it exists")
    void execute_WhenAssetExists_ShouldUpdateAndReturn() {
        Asset updatedData = new Asset();
        updatedData.setName("Updated asset name");
        updatedData.setType("Updated asset type");
        updatedData.setUsageStatus(Usage.SERVICE);
        updatedData.setConditionStatus(ConditionStatus.BAD);
        updatedData.setAssignment(Assignment.UNASSIGNED);

        UpdateAssetCommand command = new UpdateAssetCommand(1L, updatedData);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<AssetDTO> response = updateAssetService.execute(command);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).save(any(Asset.class));

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository).save(assetCaptor.capture());

        Asset savedAsset = assetCaptor.getValue();
        assertThat(savedAsset.getId()).isEqualTo(1L);
        assertThat(savedAsset.getName()).isEqualTo("Updated asset name");
        assertThat(savedAsset.getUsageStatus()).isEqualTo(Usage.SERVICE);
    }


}
