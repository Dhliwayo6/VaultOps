package com.vaultops.services;

import com.vaultops.Command;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchAssetService implements Command<String, List<AssetDTO>> {

    private AssetRepository assetRepository;

    public SearchAssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<List<AssetDTO>> execute(String name) {
        List<AssetDTO> assets = assetRepository.findByNameOrTypeContaining(name).stream().map(AssetDTO::new).toList();

        if (assets.isEmpty()) {
            throw new NoResultsException();
        }

        return ResponseEntity.ok(assets);
    }
}
