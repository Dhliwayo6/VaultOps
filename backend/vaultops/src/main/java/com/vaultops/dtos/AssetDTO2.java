package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import lombok.Data;

@Data
public class AssetDTO2 {
    private String name;
    private String serialNumber;
    private ConditionStatus conditionStatus;
    private Usage usageStatus;
    private String assignedTo;

    public AssetDTO2(Asset assets) {
        this.name = assets.getName();
        this.serialNumber = assets.getSerialNumber();
        this.conditionStatus = assets.getConditionStatus();
        this.usageStatus = assets.getUsageStatus();
        this.assignedTo = assets.getAssignedTo();
    }

}
