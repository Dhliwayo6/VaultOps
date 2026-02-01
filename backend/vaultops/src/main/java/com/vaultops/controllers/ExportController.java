package com.vaultops.controllers;

import com.vaultops.dtos.ExportFilter;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.services.AssetExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/export")
@Slf4j
@RequiredArgsConstructor
public class ExportController {

    private final AssetExportService exportService;

    @GetMapping("/assets/excel")
    public ResponseEntity<Resource> exportToExcel(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String usage,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDateTo) {

        log.info("Excel export request - category: {}, condition: {}, usage: {}",
                category, condition, usage);

        ExportFilter filter = buildFilter(category, condition, usage, location,
                purchaseDateFrom, purchaseDateTo);

        ByteArrayResource resource = exportService.exportToExcel(filter);

        String filename = String.format("assets-export-%s.xlsx",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/assets/csv")
    public ResponseEntity<Resource> exportToCsv(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String usage,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDateTo) {

        log.info("CSV export request - category: {}, condition: {}, usage: {}",
                category, condition, usage);

        ExportFilter filter = buildFilter(category, condition, usage, location,
                purchaseDateFrom, purchaseDateTo);

        ByteArrayResource resource = exportService.exportToCsv(filter);

        String filename = String.format("assets-export-%s.csv",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private ExportFilter buildFilter(String category, String condition, String usage,
                                     String location, LocalDate purchaseDateFrom,
                                     LocalDate purchaseDateTo) {
        return ExportFilter.builder()
                .category(category)
                .condition(condition != null ? ConditionStatus.valueOf(condition.toUpperCase()) : null)
                .usage(usage != null ? Usage.valueOf(usage.toUpperCase()) : null)
                .location(location)
                .purchaseDateFrom(purchaseDateFrom)
                .purchaseDateTo(purchaseDateTo)
                .build();
    }
}