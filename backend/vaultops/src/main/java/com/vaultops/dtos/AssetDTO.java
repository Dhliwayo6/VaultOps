package com.vaultops.dtos;

import com.vaultops.enums.Assignment;
import com.vaultops.model.Asset;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AssetDTO {
    private Long id;
    private String name;
    private String type;
    private String location;
    private Assignment assignment;
    private String serialNumber;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private ConditionStatus conditionStatus;
    private Usage usageStatus;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime latestUpdatedDate;

    public AssetDTO(Asset assets) {
        this.id = assets.getId();
        this.name = assets.getName();
        this.type = assets.getType();
        this.location = assets.getLocation();
        this.assignment = assets.getAssignment();
        this.serialNumber = assets.getSerialNumber();
        this.purchasePrice = assets.getPurchasePrice();
        this.purchaseDate = assets.getPurchaseDate();
        this.assignedTo = assets.getAssignedTo();
        this.conditionStatus = assets.getConditionStatus();
        this.usageStatus = assets.getUsageStatus();
        this.createdAt = assets.getCreatedAt();
        this.latestUpdatedDate = assets.getLatestUpdatedDate();
    }
}
