package com.vaultops.services;

import com.vaultops.Query;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAssetsService implements Query<Void, List<AssetDTO>> {

    private AssetRepository assetRepository;

    public GetAssetsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<List<AssetDTO>> execute(Void input) {
        List<Asset> assets = assetRepository.findAll();
        List<AssetDTO> assetDTOS = assets.stream().map(AssetDTO::new).toList();

        if (assetDTOS.isEmpty()) {
            throw new NoResultsException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(assetDTOS);
    }
}
