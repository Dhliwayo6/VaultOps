package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.services.CreateAssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssetController {
    private final CreateAssetService createAssetService;

    public AssetController(CreateAssetService createAssetService) {
        this.createAssetService = createAssetService;
    }

    @PostMapping
    public ResponseEntity<AssetDTO> createAsset(@RequestBody Asset asset) {
        return createAssetService.execute(asset);
    }
}
