package com.vaultops.dtos;

import com.vaultops.enums.Assignment;
import com.vaultops.model.Asset;
import com.vaultops.model.Location;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssetDTO(
    Long id,
    String name,
    String type,
    Location location,
    Assignment assignment,
    String serialNumber,
    BigDecimal purchasePrice,
    LocalDate purchaseDate,
    ConditionStatus conditionStatus,
    Usage usageStatus,
    String assignedTo,
    LocalDate warrantyExpiryDate,
    LocalDateTime createdAt,
    LocalDateTime latestUpdatedDate
) {
    public AssetDTO(Asset asset) {
        this(
            asset.getId(),
            asset.getName(),
            asset.getType(),
            asset.getLocation(),
            asset.getAssignment(),
            asset.getSerialNumber(),
            asset.getPurchasePrice(),
            asset.getPurchaseDate(),
            asset.getConditionStatus(),
            asset.getUsageStatus(),
            asset.getAssignedTo(),
            asset.getWarrantyExpiryDate(),
            asset.getCreatedAt(),
            asset.getLatestUpdatedDate()
        );
    }
}
