package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetAssetsInStorageService implements Query<Void, Integer> {

    private final AssetRepository assetRepository;

    public GetAssetsInStorageService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Integer> execute(Void input) {
        Integer count = assetRepository.countAssetsByUsageStatus(Usage.STORAGE);
        return ResponseEntity.ok(count);
    }
}
