package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetAssetsInUseService implements Query<Void, Long> {

    private final AssetRepository assetRepository;

    public GetAssetsInUseService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Long> execute(Void input) {
        Long count = assetRepository.countAssetsByUsageStatus(Usage.IN_USE);
        return ResponseEntity.ok(count);
    }
}
