package com.vaultops.services.asset;

import com.vaultops.Command;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateAssetService implements Command<Asset, AssetDTO> {
    private AssetRepository assetRepository;

    public CreateAssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<AssetDTO> execute(Asset asset) {
        Asset assetSaved = assetRepository.save(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AssetDTO(assetSaved));

    }
}
