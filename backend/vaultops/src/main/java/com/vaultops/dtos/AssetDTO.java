package com.vaultops.dtos;

import com.vaultops.enums.Assignment;
import com.vaultops.model.Asset;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssetDTO(
    Long id,
    String name,
    String type,
    String location,
    Assignment assignment,
    String serialNumber,
    BigDecimal purchasePrice,
    LocalDate purchaseDate,
    ConditionStatus conditionStatus,
    Usage usageStatus,
    String assignedTo,
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
            asset.getCreatedAt(),
            asset.getLatestUpdatedDate()
        );
    }
}
