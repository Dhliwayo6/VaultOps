package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;

import java.time.LocalDate;
import java.util.Objects;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ConditionStatus getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ConditionStatus conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public Usage getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(Usage usageStatus) {
        this.usageStatus = usageStatus;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AssetDTO2 assetDTO2 = (AssetDTO2) o;
        return Objects.equals(id, assetDTO2.id) && Objects.equals(name, assetDTO2.name) && Objects.equals(serialNumber, assetDTO2.serialNumber) && conditionStatus == assetDTO2.conditionStatus && usageStatus == assetDTO2.usageStatus && Objects.equals(createdAt, assetDTO2.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, serialNumber, conditionStatus, usageStatus, createdAt);
    }
}
