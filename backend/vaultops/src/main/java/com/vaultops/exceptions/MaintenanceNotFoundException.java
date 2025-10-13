package com.vaultops.exceptions;

import com.vaultops.enums.ErrorMessages;

public class MaintenanceNotFoundException extends RuntimeException{
    public MaintenanceNotFoundException() {
        super(ErrorMessages.MAINTENANCE_NOT_FOUND.getMessage());
    }
}
