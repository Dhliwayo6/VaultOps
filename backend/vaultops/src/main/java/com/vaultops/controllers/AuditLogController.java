package com.vaultops.controllers;

import com.vaultops.dtos.AuditLogDTO;
import com.vaultops.model.AuditLog;
import com.vaultops.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/audit-log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogDTO>> getAuditLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.isBlank()) {
            try {
                if (startDate.contains("T")) {
                    start = LocalDateTime.parse(startDate);
                } else {
                    start = LocalDateTime.parse(startDate + "T00:00:00");
                }
            } catch (Exception e) {
                log.warn("Failed to parse start date: {}", startDate);
            }
        }

        if (endDate != null && !endDate.isBlank()) {
            try {
                if (endDate.contains("T")) {
                    end = LocalDateTime.parse(endDate);
                } else {
                    end = LocalDateTime.parse(endDate + "T23:59:59");
                }
            } catch (Exception e) {
                log.warn("Failed to parse end date: {}", endDate);
            }
        }

        // Sort by timestamp descending
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        String finalAction = (actionType != null && !actionType.isBlank()) ? actionType : null;
        
        Page<AuditLog> logs = auditLogRepository.findFilteredLogs(finalAction, start, end, pageable);
        Page<AuditLogDTO> dtos = logs.map(AuditLogDTO::new);

        return ResponseEntity.ok(dtos);
    }
}
