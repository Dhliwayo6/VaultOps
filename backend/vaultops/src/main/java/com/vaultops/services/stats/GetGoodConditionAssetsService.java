package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetGoodConditionAssetsService implements Query<Void, Long> {
    private final AssetRepository assetRepository;

    public GetGoodConditionAssetsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Long> execute(Void input) {
        Long count = assetRepository.countAssetsByConditionStatus(ConditionStatus.GOOD);
        return ResponseEntity.ok(count);
    }
}