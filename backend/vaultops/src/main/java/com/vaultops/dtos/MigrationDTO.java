package com.vaultops.dtos;

import com.vaultops.model.Migration;
import lombok.Data;

@Data
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
}
