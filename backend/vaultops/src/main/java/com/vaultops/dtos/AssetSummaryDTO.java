package com.vaultops.dtos;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;

public record AssetSummaryDTO(
    Long id,
    String name,
    String serialNumber,
    ConditionStatus conditionStatus,
    Usage usageStatus,
    String assignedTo
) {
    public AssetSummaryDTO(Asset asset) {
        this(
            asset.getId(),
            asset.getName(),
            asset.getSerialNumber(),
            asset.getConditionStatus(),
            asset.getUsageStatus(),
            asset.getAssignedTo()
        );
    }
}
