package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.enums.Condition;
import com.vaultops.enums.Usage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class AssetDTO {
    private Long id;
    private String name;
    private String type;
    private String location;
    private String serialNumber;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private Condition conditionStatus;
    private Usage usageStatus;
    private LocalDate createdAt;

    public AssetDTO(Asset assets) {
        this.id = assets.getId();
        this.name = assets.getName();
        this.type = assets.getType();
        this.location = assets.getLocation();
        this.serialNumber = assets.getSerialNumber();
        this.purchasePrice = assets.getPurchasePrice();
        this.purchaseDate = assets.getPurchaseDate();
        this.conditionStatus = assets.getCondition();
        this.usageStatus = assets.getUsage();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Condition getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(Condition conditionStatus) {
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
        AssetDTO assetDTO = (AssetDTO) o;
        return Objects.equals(id, assetDTO.id) && Objects.equals(name, assetDTO.name) && Objects.equals(type, assetDTO.type) && Objects.equals(location, assetDTO.location) && Objects.equals(serialNumber, assetDTO.serialNumber) && Objects.equals(purchasePrice, assetDTO.purchasePrice) && condition == assetDTO.condition && usage == assetDTO.usage && Objects.equals(createdAt, assetDTO.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, location, serialNumber, purchasePrice, condition, usage, createdAt);
    }
}
