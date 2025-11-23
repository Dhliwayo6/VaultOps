package com.vaultops.assets.services;

import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Asset Service tests")
public class CreateAssetServiceTests {
    @Mock private AssetRepository assetRepository;
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
}