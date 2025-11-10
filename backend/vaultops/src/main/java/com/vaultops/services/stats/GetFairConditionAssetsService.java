package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetFairConditionAssetsService implements Query<Void, Integer> {
    private final AssetRepository assetRepository;

    public GetFairConditionAssetsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Integer> execute(Void input) {
        Integer count = assetRepository.countAssetsByConditionStatus(ConditionStatus.FAIR);
        return ResponseEntity.ok(count);
    }
}