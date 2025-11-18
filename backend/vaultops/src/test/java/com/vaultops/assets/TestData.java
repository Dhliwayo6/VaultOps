package com.vaultops.assets;

import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;

import java.time.LocalDate;

public class TestData {
    public static Asset createInitialAsset() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);
        asset.setCreatedAt(LocalDate.now());
        return asset;
    }

    public static Asset createAssetWithId(Long id) {
        Asset asset = createInitialAsset();
        asset.setId(id);
        return asset;
    }

    public static Asset createAssetWithName(String name) {
        Asset asset = createInitialAsset();
        asset.setName(name);
        return asset;
    }

}
