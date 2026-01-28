package com.vaultops.assets.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;

import java.time.LocalDateTime;

public class TestData {
    public static Asset createInitialAsset() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);
        asset.setCreatedAt(LocalDateTime.now());
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

    public static Asset createAssetInService() {
        Asset asset = createInitialAsset();
        asset.setUsageStatus(Usage.SERVICE);
        asset.setConditionStatus(ConditionStatus.DAMAGED);
        return asset;
    }

    public static Asset createAssetInStorage() {
        Asset asset = createInitialAsset();
        asset.setUsageStatus(Usage.STORAGE);
        asset.setAssignment(Assignment.UNASSIGNED);
        return asset;
    }

    public static AssetDTO createDefaultAssetDTO() {
        return new AssetDTO(createInitialAsset());
    }


}
