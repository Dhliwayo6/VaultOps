package com.vaultops.dtos;

import com.vaultops.enums.ImportStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportLogDTO {
    private Long id;
    private String userId;
    private String fileName;
    private Long fileSize;
    private ImportStatus status;
    private Integer totalRecords;
    private Integer successCount;
    private Integer errorCount;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationMs;
}