package com.vaultops.assets.repository;

import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("AssetRepository Custom Queries Tests")
public class AssetRepositoryTests {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
    }

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
            assetRepository.saveAndFlush(asset);

            entityManager.createQuery("UPDATE Asset a SET a.createdAt = :date WHERE a.id = :id")
                    .setParameter("date", LocalDateTime.now().minusDays(10 - i))
                    .setParameter("id", asset.getId())
                    .executeUpdate();
        }

        // Add some assets with other usage statuses to ensure they are filtered out
        Asset storageAsset = new Asset();
        storageAsset.setName("Storage Asset");
        storageAsset.setType("Laptop");
        storageAsset.setLocation("Storage");
        storageAsset.setAssignment(Assignment.UNASSIGNED);
        storageAsset.setConditionStatus(ConditionStatus.GOOD);
        storageAsset.setUsageStatus(Usage.STORAGE);
        assetRepository.saveAndFlush(storageAsset);

        entityManager.createQuery("UPDATE Asset a SET a.createdAt = :date WHERE a.id = :id")
                .setParameter("date", LocalDateTime.now())
                .setParameter("id", storageAsset.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

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

    @Test
    @DisplayName("Should find assets by name containing substring")
    void shouldFindByNameContaining() {
        Asset asset1 = createDummyAsset("MacBook Pro", "Electronics", "SN-1", Usage.IN_USE, ConditionStatus.EXCELLENT);
        Asset asset2 = createDummyAsset("Lenovo ThinkPad", "Electronics", "SN-2", Usage.STORAGE, ConditionStatus.GOOD);
        assetRepository.save(asset1);
        assetRepository.save(asset2);

        List<Asset> results = assetRepository.findByNameContaining("Book");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("MacBook Pro");
    }

    @Test
    @DisplayName("Should find assets by name or type containing substring")
    void shouldFindByNameOrTypeContaining() {
        Asset asset1 = createDummyAsset("MacBook Pro", "Hardware", "SN-1", Usage.IN_USE, ConditionStatus.EXCELLENT);
        Asset asset2 = createDummyAsset("Office Chair", "Furniture", "SN-2", Usage.STORAGE, ConditionStatus.GOOD);
        assetRepository.save(asset1);
        assetRepository.save(asset2);

        List<Asset> results = assetRepository.findByNameOrTypeContaining("Furn");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Office Chair");
    }

    @Test
    @DisplayName("Should find and count assets by usage or condition statuses")
    void shouldFindAndCountByStatus() {
        Asset asset1 = createDummyAsset("Server", "Hardware", "SN-1", Usage.SERVICE, ConditionStatus.DAMAGED);
        Asset asset2 = createDummyAsset("Switch", "Hardware", "SN-2", Usage.SERVICE, ConditionStatus.DAMAGED);
        Asset asset3 = createDummyAsset("Router", "Hardware", "SN-3", Usage.STORAGE, ConditionStatus.EXCELLENT);
        assetRepository.saveAll(List.of(asset1, asset2, asset3));

        List<Asset> inService = assetRepository.findAssetsByUsageStatus(Usage.SERVICE);
        assertThat(inService).hasSize(2);

        List<Asset> damaged = assetRepository.findAssetsByConditionStatus(ConditionStatus.DAMAGED);
        assertThat(damaged).hasSize(2);

        assertThat(assetRepository.countAssetsByUsageStatus(Usage.SERVICE)).isEqualTo(2L);
        assertThat(assetRepository.countAssetsByConditionStatus(ConditionStatus.DAMAGED)).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should calculate average days in status correctly")
    void shouldGetAverageDaysInStatus() {
        Asset asset1 = createDummyAsset("Server A", "Hardware", "SN-1", Usage.SERVICE, ConditionStatus.GOOD);
        assetRepository.saveAndFlush(asset1);

        Asset asset2 = createDummyAsset("Server B", "Hardware", "SN-2", Usage.SERVICE, ConditionStatus.GOOD);
        assetRepository.saveAndFlush(asset2);

        entityManager.createQuery("UPDATE Asset a SET a.createdAt = :date WHERE a.id = :id")
                .setParameter("date", LocalDateTime.now().minusDays(10))
                .setParameter("id", asset1.getId())
                .executeUpdate();

        entityManager.createQuery("UPDATE Asset a SET a.createdAt = :date WHERE a.id = :id")
                .setParameter("date", LocalDateTime.now().minusDays(20))
                .setParameter("id", asset2.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        Double avgDays = assetRepository.getAverageDaysInStatus(Usage.SERVICE);
        assertThat(avgDays).isCloseTo(15.0, org.assertj.core.data.Offset.offset(1.0));
    }

    @Test
    @DisplayName("Should find asset by serial number, type, or type and condition")
    void shouldFindByOtherFields() {
        Asset asset1 = createDummyAsset("Server A", "Hardware", "SN-SPECIAL", Usage.IN_USE, ConditionStatus.EXCELLENT);
        Asset asset2 = createDummyAsset("Server B", "Hardware", "SN-2", Usage.IN_USE, ConditionStatus.GOOD);
        assetRepository.saveAll(List.of(asset1, asset2));

        Optional<Asset> foundSerial = assetRepository.findBySerialNumber("SN-SPECIAL");
        assertThat(foundSerial).isPresent();
        assertThat(foundSerial.get().getName()).isEqualTo("Server A");

        List<Asset> foundType = assetRepository.findByType("Hardware");
        assertThat(foundType).hasSize(2);

        List<Asset> foundTypeAndCond = assetRepository.findByTypeAndConditionStatus("Hardware", ConditionStatus.EXCELLENT);
        assertThat(foundTypeAndCond).hasSize(1);
        assertThat(foundTypeAndCond.get(0).getName()).isEqualTo("Server A");
    }

    private Asset createDummyAsset(String name, String type, String serial, Usage usage, ConditionStatus condition) {
        Asset a = new Asset();
        a.setName(name);
        a.setType(type);
        a.setSerialNumber(serial);
        a.setLocation("Cape Town");
        a.setAssignment(Assignment.UNASSIGNED);
        a.setUsageStatus(usage);
        a.setConditionStatus(condition);
        a.setPurchasePrice(BigDecimal.valueOf(100.00));
        a.setCreatedAt(LocalDateTime.now());
        return a;
    }
}
