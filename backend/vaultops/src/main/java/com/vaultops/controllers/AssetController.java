package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.services.CreateAssetService;
import com.vaultops.services.GetAssetService;
import com.vaultops.services.GetAssetsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AssetController {
    private final CreateAssetService createAssetService;
    private final GetAssetService getAssetService;
    private final GetAssetsService getAssetsService;

    public AssetController(CreateAssetService createAssetService,
                           GetAssetService getAssetService,
                           GetAssetsService getAssetsService) {
        this.createAssetService = createAssetService;
        this.getAssetService = getAssetService;
        this.getAssetsService = getAssetsService;
    }

    @PostMapping("/asset")
    public ResponseEntity<AssetDTO> createAsset(@RequestBody Asset asset) {
        return createAssetService.execute(asset);
    }

    @GetMapping("task{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        return getAssetService.execute(id);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<AssetDTO>> getAssets() {
        return getAssetsService.execute(null);
    }
}
