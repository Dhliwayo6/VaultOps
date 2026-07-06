package com.vaultops.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(String message) {
        this.message = message;
        this.errors = null;
    }

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
