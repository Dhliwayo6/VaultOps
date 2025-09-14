package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AssetController {
    private final CreateAssetService createAssetService;
    private final GetAssetService getAssetService;
    private final GetAssetsService getAssetsService;
    private final UpdateAssetService updateAssetService;
    private final SearchAssetService searchAssetService;

    public AssetController(CreateAssetService createAssetService,
                           GetAssetService getAssetService,
                           GetAssetsService getAssetsService,
                           UpdateAssetService updateAssetService,
                           SearchAssetService searchAssetService) {
        this.createAssetService = createAssetService;
        this.getAssetService = getAssetService;
        this.getAssetsService = getAssetsService;
        this.updateAssetService = updateAssetService;
        this.searchAssetService = searchAssetService;
    }

    @PostMapping("/asset")
    public ResponseEntity<AssetDTO> createAsset(@RequestBody Asset asset) {
        return createAssetService.execute(asset);
    }

    @GetMapping("asset/{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        return getAssetService.execute(id);
    }

    @GetMapping("/assets")
    public ResponseEntity<List<AssetDTO>> getAssets() {
        return getAssetsService.execute(null);
    }

    @GetMapping("/asset/search")
    public ResponseEntity<List<AssetDTO>> searchAssetByName(@RequestParam String name) {
        return searchAssetService.execute(name);
    }

    @PutMapping("/asset/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        return updateAssetService.execute(new UpdateAssetCommand(id, asset));
    }
}
