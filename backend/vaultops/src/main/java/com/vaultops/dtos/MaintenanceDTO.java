package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.model.Maintenance;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MaintenanceDTO {
    private Long id;
    private AssetDTO2 asset;
    private LocalDate date;
    private String performedBy;
    private String description;
    private BigDecimal cost;

    public MaintenanceDTO(Maintenance maintenance) {
        this.id = maintenance.getId();
        this.asset = new AssetDTO2(maintenance.getAsset());
        this.date = maintenance.getDate();
        this.performedBy = maintenance.getPerformedBy();
        this.description = maintenance.getDescription();
        this.cost = maintenance.getCost();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssetDTO2 getAsset() {
        return asset;
    }

    public void setAsset(AssetDTO2 asset) {
        this.asset = asset;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
