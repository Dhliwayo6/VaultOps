package com.vaultops.services.asset;

import com.vaultops.Query;
import com.vaultops.dtos.AssetDTO2;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetTopForAssetsInRepairs implements Query<Void, List<AssetDTO2>> {
    private AssetRepository assetRepository;

    public GetTopForAssetsInRepairs(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<List<AssetDTO2>> execute(Void input) {
        List<Asset> assets = assetRepository.findAssetsByUsageStatus(Usage.SERVICE).stream().limit(4).toList();
        List<AssetDTO2>  assetDTOs = assets.stream()
                .sorted(Comparator.comparing(Asset::getCreatedAt).reversed())
                .map(AssetDTO2::new)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(assetDTOs);
    }
}
