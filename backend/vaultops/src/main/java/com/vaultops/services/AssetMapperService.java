package com.vaultops.services;

import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetSummaryDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.model.Location;
import com.vaultops.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetMapperService {

    private final LocationRepository locationRepository;

    public Asset mapToEntity(AssetImportDTO dto) {
        Asset asset = new Asset();
        updateEntity(asset, dto);
        return asset;
    }

    public void updateEntity(Asset asset, AssetImportDTO dto) {
        asset.setName(dto.getName());
        asset.setType(dto.getType());

        String locName = dto.getLocation();
        if (locName == null || locName.trim().isEmpty()) {
            locName = "Unassigned";
        }
        String finalLocName = locName.trim();
        Location location = locationRepository.findByNameIgnoreCase(finalLocName)
                .orElseGet(() -> {
                    Location newLoc = new Location();
                    newLoc.setName(finalLocName);
                    newLoc.setMaxCapacity(100);
                    newLoc.setDescription("Automatically created during import");
                    return locationRepository.save(newLoc);
                });
        asset.setLocation(location);

        asset.setSerialNumber(dto.getSerialNumber());
        asset.setAssignment(
                Assignment.valueOf(dto.getAssignment().toUpperCase())
        );
        asset.setConditionStatus(
                ConditionStatus.valueOf(dto.getConditionStatus().toUpperCase())
        );
        asset.setUsageStatus(
                Usage.valueOf(dto.getUsageStatus().toUpperCase())
        );
        asset.setAssignedTo(dto.getAssignedTo());
        asset.setPurchaseDate(dto.getPurchaseDate());

        asset.setPurchasePrice(dto.getPurchasePrice());
    }

    public List<Asset> mapToEntities(List<AssetImportDTO> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    public AssetDTO mapToDTO(Asset asset) {
        if (asset == null) {
            return null;
        }
        return new AssetDTO(asset);
    }

    public AssetSummaryDTO mapToSummaryDTO(Asset asset) {
        if (asset == null) {
            return null;
        }
        return new AssetSummaryDTO(asset);
    }
}