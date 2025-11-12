package com.vaultops.services.asset;

import com.vaultops.Query;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetDTO2;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;

public class GetTopFourAssetsInUseService implements Query<Void, List<AssetDTO2>> {
    private AssetRepository assetRepository;

    public GetTopFourAssetsInUseService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<List<AssetDTO2>> execute(Void input) {
        List<Asset> assets = assetRepository.findAssetsByUsageStatus(Usage.IN_USE).stream().limit(4).toList();
        List<AssetDTO2> assetDTOS = assets.stream()
                .sorted(Comparator.comparing(Asset::getCreatedAt).reversed())
                .map(AssetDTO2::new).toList();

        if (assetDTOS.isEmpty()) {
            throw new NoResultsException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(assetDTOS);
    }
}

