package com.vaultops.services;

import com.vaultops.Query;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetAssetService implements Query<Long, AssetDTO> {

    private AssetRepository assetRepository;

    public GetAssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<AssetDTO> execute(Long id) {
        Optional<Asset> assetOptional = assetRepository.findById(id);

        if (assetOptional.isPresent()) {
            return ResponseEntity.ok(new AssetDTO(assetOptional.get()));
        }

        throw new IllegalArgumentException("Asset not found");
    }
}
