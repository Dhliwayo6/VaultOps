package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.services.CreateAssetService;
import com.vaultops.services.GetAssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssetController {
    private final CreateAssetService createAssetService;
    private final GetAssetService getAssetService;

    public AssetController(CreateAssetService createAssetService,
                           GetAssetService getAssetService) {
        this.createAssetService = createAssetService;
        this.getAssetService = getAssetService;
    }

    @PostMapping("/asset")
    public ResponseEntity<AssetDTO> createAsset(@RequestBody Asset asset) {
        return createAssetService.execute(asset);
    }

    @GetMapping("task{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        return getAssetService.execute(id);
    }
}
