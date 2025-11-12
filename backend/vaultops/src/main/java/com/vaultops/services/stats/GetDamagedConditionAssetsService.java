package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.exceptions.NoAssetsMessageException;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetDamagedConditionAssetsService implements Query<Void, Integer>{
    private final AssetRepository assetRepository;

    public GetDamagedConditionAssetsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Integer> execute(Void input) {
        Integer count = assetRepository.countAssetsByConditionStatus(ConditionStatus.DAMAGED);
        if (count.equals(0)) {
            throw new NoAssetsMessageException();
        }
        return ResponseEntity.ok(count);
    }
}
