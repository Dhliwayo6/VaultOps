package com.vaultops.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportResponse {
    private boolean success;
    private String message;
    private Long importLogId;
    private int totalRows;
    private int validRows;
    private int invalidRows;
    private int createdRows;
    private int updatedRows;
    private List<RowError> errors;
}
