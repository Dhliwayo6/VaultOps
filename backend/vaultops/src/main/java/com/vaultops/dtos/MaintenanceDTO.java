package com.vaultops.dtos;

import com.vaultops.model.Maintenance;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
}
