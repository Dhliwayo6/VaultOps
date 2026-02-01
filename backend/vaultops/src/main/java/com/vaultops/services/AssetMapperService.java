package com.vaultops.services;

import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssetMapperService {

    public Asset mapToEntity(AssetImportDTO dto) {
        Asset asset = new Asset();
        updateEntity(asset, dto);
        return asset;
    }

    public void updateEntity(Asset asset, AssetImportDTO dto) {
        asset.setName(dto.getName());
        asset.setType(dto.getType());
        asset.setLocation(dto.getLocation());
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

        if (dto.getPurchasePrice() != null && !dto.getPurchasePrice().isEmpty()) {
            try {
                asset.setPurchasePrice(new BigDecimal(dto.getPurchasePrice()));
            } catch (NumberFormatException e) {
                log.warn(e.getMessage());
            }
        }
    }

    public List<Asset> mapToEntities(List<AssetImportDTO> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}