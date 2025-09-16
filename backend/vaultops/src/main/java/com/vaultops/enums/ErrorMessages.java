package com.vaultops.enums;

public enum ErrorMessages {
        ASSET_NOT_FOUND("Asset not found!"),
        NO_RESULTS("No results!");

        private String message;

        ErrorMessages(String message) {
            this.message = message;
        }

    public String getMessage() {
        return message;
    }
}
