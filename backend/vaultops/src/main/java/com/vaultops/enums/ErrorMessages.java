package com.vaultops.enums;

public enum ErrorMessages {
        ASSET_NOT_FOUND("Asset not found!"),
        MIGRATION_NOT_FOUND("Migration history not found!"),
        MAINTENANCE_NOT_FOUND("Maintenance not found!"),
        NO_RESULTS("No results found"),
        NO_ASSETS("No assets found");

        private String message;

        ErrorMessages(String message) {
            this.message = message;
        }

    public String getMessage() {
        return message;
    }
}
