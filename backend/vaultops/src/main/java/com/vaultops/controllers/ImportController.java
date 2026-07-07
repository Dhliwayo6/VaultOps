package com.vaultops.controllers;

import com.vaultops.dtos.ImportResponse;
import com.vaultops.exceptions.NoDataException;
import com.vaultops.exceptions.JobNotFoundException;
import com.vaultops.model.ImportLog;
import com.vaultops.repository.ImportLogRepository;
import com.vaultops.services.AssetImportService;
import com.vaultops.services.TemplateService;
import com.vaultops.config.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.vaultops.services.SecurityService;

@RestController
@RequestMapping("/api/import")
@Validated
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ImportController {

    private final AssetImportService importService;
    private final FileValidator fileValidator;
    private final TemplateService templateService;
    private final ImportLogRepository importLogRepository;
    private final SecurityService securityService;

    @PostMapping("/assets")
    public ResponseEntity<ImportResponse> importAssets(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean dryRun,
            Authentication authentication) {

        log.info("Import request - file: {}, size: {} bytes, dryRun: {}, user: {}",
                file.getOriginalFilename(), file.getSize(), dryRun, authentication.getName());

        fileValidator.validateFile(file);

        // Create and persist the ImportLog with PENDING status
        ImportLog importLog = new ImportLog();
        importLog.setUserId(authentication.getName());
        importLog.setFileName(file.getOriginalFilename());
        importLog.setFileSize(file.getSize());
        importLog.setStatus(com.vaultops.enums.ImportStatus.PENDING);
        importLog.setStartedAt(java.time.LocalDateTime.now());
        importLog = importLogRepository.save(importLog);

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (java.io.IOException e) {
            throw new com.vaultops.exceptions.InvalidFileException("Failed to read file bytes: " + e.getMessage());
        }

        // Kick off async processing
        importService.processImportAsync(importLog.getId(), fileBytes, file.getOriginalFilename(), dryRun);

        // Return immediately with importLogId
        ImportResponse response = ImportResponse.builder()
                .success(true)
                .message("Import job started successfully")
                .importLogId(importLog.getId())
                .startTime(importLog.getStartedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/template/excel")
    public ResponseEntity<Resource> downloadExcelTemplate() {
        log.info("Excel template download requested");

        ByteArrayResource resource = templateService.generateExcelTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=asset-import-template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/template/csv")
    public ResponseEntity<Resource> downloadCsvTemplate() {
        log.info("CSV template download requested");

        ByteArrayResource resource = templateService.generateCsvTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=asset-import-template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ImportLog>> getImportLogs(
            @RequestParam(defaultValue = "50") int limit,
            Authentication authentication) {

        log.info("Fetching import logs, limit: {}, user: {}", limit, authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<ImportLog> logs;
        if (isAdmin) {
            logs = importLogRepository.findAll(
                            Sort.by(Sort.Direction.DESC, "startedAt")
                    ).stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } else {
            logs = importLogRepository.findByUserIdOrderByStartedAtDesc(authentication.getName()).stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<ImportLog> getImportLog(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Fetching import log with id: {}, user: {}", id, authentication.getName());

        securityService.checkOwnershipOrAdmin(id, "ImportLog");

        ImportLog log = importLogRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Import log not found: " + id));

        return ResponseEntity.ok(log);
    }
}