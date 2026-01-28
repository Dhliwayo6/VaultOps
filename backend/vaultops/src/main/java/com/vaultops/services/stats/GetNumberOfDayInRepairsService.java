package com.vaultops.services.stats;

import com.vaultops.Query;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class GetNumberOfDayInRepairsService implements Query<UpdateAssetCommand, Long> {
    private AssetRepository assetRepository;

    public GetNumberOfDayInRepairsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public ResponseEntity<Long> execute(UpdateAssetCommand command) {
        Optional<Asset> optionalAsset = assetRepository.findById(command.getId());

        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            LocalDateTime serviceDate = asset.getCreatedAt();
            LocalDate currentDate = LocalDate.now();
            Long days = ChronoUnit.DAYS.between(serviceDate, currentDate);
            return ResponseEntity.ok(days);
        }

        throw new AssetNotFoundException();
    }
}
