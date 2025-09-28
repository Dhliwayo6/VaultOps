package com.vaultops.dtos;

import com.vaultops.model.Migration;

import java.util.Objects;


public class MigrationDTO {
    private Long id;
    private AssetDTO2 asset;
    private String fromLocation;
    private String toLocation;
    private String movedBy;
    private String description;

    public MigrationDTO(Migration migration) {
        this.id = migration.getId();
        this.asset = new AssetDTO2(migration.getAsset());
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

    public AssetDTO2 getAsset() {
        return asset;
    }

    public void setAsset(AssetDTO2 asset) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MigrationDTO that = (MigrationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(asset, that.asset) && Objects.equals(fromLocation, that.fromLocation) && Objects.equals(toLocation, that.toLocation) && Objects.equals(movedBy, that.movedBy) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asset, fromLocation, toLocation, movedBy, description);
    }
}
