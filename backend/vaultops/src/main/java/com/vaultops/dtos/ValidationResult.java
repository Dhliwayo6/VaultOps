package com.vaultops.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationResult {
    private boolean valid = true;
    private Map<String, String> fieldErrors = new HashMap<>();

    public void addError(String field, String message) {
        valid = false;
        fieldErrors.put(field, message);
    }

}