package com.vaultops.assets.repository;

import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("AssetRepository Custom Queries Tests")
public class AssetRepositoryTests {

    @Autowired
    private AssetRepository assetRepository;

    @Test
    @DisplayName("Should find top 4 assets by usage status ordered by createdAt descending")
    void shouldFindTop4ByUsageStatusOrderByCreatedAtDesc() {
        // Create 6 assets with different createdAt values
        for (int i = 1; i <= 6; i++) {
            Asset asset = new Asset();
            asset.setName("Asset " + i);
            asset.setType("Laptop");
            asset.setLocation("Office " + i);
            asset.setAssignment(Assignment.UNASSIGNED);
            asset.setConditionStatus(ConditionStatus.EXCELLENT);
            asset.setUsageStatus(Usage.IN_USE);
            // set createdAt sequentially so we can assert order
            asset.setCreatedAt(LocalDateTime.now().minusDays(10 - i));
            assetRepository.save(asset);
        }

        // Add some assets with other usage statuses to ensure they are filtered out
        Asset storageAsset = new Asset();
        storageAsset.setName("Storage Asset");
        storageAsset.setType("Laptop");
        storageAsset.setLocation("Storage");
        storageAsset.setAssignment(Assignment.UNASSIGNED);
        storageAsset.setConditionStatus(ConditionStatus.GOOD);
        storageAsset.setUsageStatus(Usage.STORAGE);
        storageAsset.setCreatedAt(LocalDateTime.now());
        assetRepository.save(storageAsset);

        // Fetch top 4 IN_USE assets
        List<Asset> top4 = assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.IN_USE);

        // Assert size is 4
        assertThat(top4).hasSize(4);

        // Assert order (most recent first: i=6, i=5, i=4, i=3)
        assertThat(top4.get(0).getName()).isEqualTo("Asset 6");
        assertThat(top4.get(1).getName()).isEqualTo("Asset 5");
        assertThat(top4.get(2).getName()).isEqualTo("Asset 4");
        assertThat(top4.get(3).getName()).isEqualTo("Asset 3");
    }
}
