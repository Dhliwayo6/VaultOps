package com.vaultops.model;

public class UpdateMaintenance {
    private Long id;
    private Maintenance maintenance;

    public UpdateMaintenance(Long id, Maintenance maintenance) {
        this.id = id;
        this.maintenance = maintenance;
    }

    public Long getId() {
        return id;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }
}
