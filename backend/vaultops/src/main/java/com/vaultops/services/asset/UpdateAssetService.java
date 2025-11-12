package com.vaultops.services.asset;

import com.vaultops.Command;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateAssetService implements Command<UpdateAssetCommand, AssetDTO> {

    private AssetRepository assetRepository;

    public UpdateAssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<AssetDTO> execute(UpdateAssetCommand cmd) {
        Optional<Asset> assetOptional = assetRepository.findById(cmd.getId());

        if (assetOptional.isPresent()) {
            Asset asset = cmd.getAsset();
            asset.setId(cmd.getId());

            assetRepository.save(asset);
            return ResponseEntity.ok(new AssetDTO(asset));
        }

        throw new AssetNotFoundException();
    }
}
