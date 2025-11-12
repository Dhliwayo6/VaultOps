package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetAssetsInStorageService implements Query<Void, Long> {

    private final AssetRepository assetRepository;

    public GetAssetsInStorageService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Long> execute(Void input) {
        Long count = assetRepository.countAssetsByUsageStatus(Usage.STORAGE);
        return ResponseEntity.ok(count);
    }
}
