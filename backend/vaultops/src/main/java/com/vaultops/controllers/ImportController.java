package com.vaultops.controllers;

import com.vaultops.dtos.ImportResponse;
import com.vaultops.exceptions.NoDataException;
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

@RestController
@RequestMapping("/api/import")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ImportController {

    private final AssetImportService importService;
    private final FileValidator fileValidator;
    private final TemplateService templateService;
    private final ImportLogRepository importLogRepository;

    @PostMapping("/assets")
    public ResponseEntity<ImportResponse> importAssets(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean dryRun) {

        log.info("Import request - file: {}, size: {} bytes, dryRun: {}",
                file.getOriginalFilename(), file.getSize(), dryRun);

        fileValidator.validateFile(file);

        ImportResponse response = importService.processImport(file, dryRun);

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
            @RequestParam(defaultValue = "50") int limit) {

        log.info("Fetching import logs, limit: {}", limit);

        List<ImportLog> logs = importLogRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "startedAt")
                ).stream()
                .limit(limit)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<ImportLog> getImportLog(@PathVariable Long id) {
        log.info("Fetching import log with id: {}", id);

        ImportLog log = importLogRepository.findById(id)
                .orElseThrow(() -> new NoDataException("Import log not found: " + id));

        return ResponseEntity.ok(log);
    }
}