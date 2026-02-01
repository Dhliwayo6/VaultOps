package com.vaultops.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private int skippedRows;

    private List<RowError> errors;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
}