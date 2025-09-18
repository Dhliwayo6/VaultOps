package com.vaultops.dtos;

import com.vaultops.model.Asset;
import com.vaultops.model.Migration;


public class MigrationDTO {
    private Long id;
    private Asset asset;
    private String fromLocation;
    private String toLocation;
    private String movedBy;
    private String description;

    public MigrationDTO(Migration migration) {
        this.id = migration.getId();
        this.asset = migration.getAsset();
        this.fromLocation = migration.getFromLocation();
        this.toLocation = migration.getToLocation();
        this.movedBy = migration.getMovedBy();
        this.description = migration.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getMovedBy() {
        return movedBy;
    }

    public void setMovedBy(String movedBy) {
        this.movedBy = movedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
