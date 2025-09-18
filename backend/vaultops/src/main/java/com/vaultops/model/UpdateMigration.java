package com.vaultops.model;

public class UpdateMigration {
    private Long id;
    private Migration migration;

    public UpdateMigration(Long id, Migration migration) {
        this.id = id;
        this.migration = migration;
    }

    public Long getId() {
        return id;
    }

    public Migration getMigration() {
        return migration;
    }
}
