package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetDTO2;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.services.asset.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AssetController {
    private final CreateAssetService createAssetService;
    private final GetAssetService getAssetService;
    private final GetAssetsService getAssetsService;
    private final UpdateAssetService updateAssetService;
    private final SearchAssetService searchAssetService;
    private final DeleteAssetService deleteAssetService;
    private final GetTopFourAssetsInUseService getTopFourAssetsInUseService;
    private final GetTopFourAssetsInRepairsService getTopFourAssetsInRepairsService;

    @GetMapping("/assets/top-four/in-use")
    public ResponseEntity<List<AssetDTO2>> getTopFourAssetsInUse() {
        return getTopFourAssetsInUseService.execute(null);
    }

    @GetMapping("/assets/top-four/in-repairs")
    public ResponseEntity<List<AssetDTO2>> getTopFourAssetsInRepairs() {
        return getTopFourAssetsInRepairsService.execute(null);
    }

    @PostMapping("/asset")
    public ResponseEntity<AssetDTO> createAsset(@Valid @RequestBody Asset asset) {
        return createAssetService.execute(asset);
    }

    @GetMapping("/asset/{id}")
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

    @DeleteMapping("/asset/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        return deleteAssetService.execute(id);
    }
}
