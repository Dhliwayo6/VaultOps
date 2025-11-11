package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class AssetDTO2 {
    private Long id;
    private String name;
    private String serialNumber;
    private ConditionStatus conditionStatus;
    private Usage usageStatus;
    private LocalDate createdAt;

    public AssetDTO2(Asset assets) {
        this.id = assets.getId();
        this.name = assets.getName();
        this.serialNumber = assets.getSerialNumber();
        this.conditionStatus = assets.getConditionStatus();
        this.usageStatus = assets.getUsageStatus();
        this.createdAt = assets.getCreatedAt();
    }

}
