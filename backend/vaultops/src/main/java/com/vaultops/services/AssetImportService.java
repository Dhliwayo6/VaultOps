package com.vaultops.services;

import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.dtos.ImportResponse;
import com.vaultops.dtos.RowError;
import com.vaultops.dtos.ValidationResult;
import com.vaultops.enums.ImportStatus;
import com.vaultops.exceptions.InvalidFileException;
import com.vaultops.model.Asset;
import com.vaultops.model.ImportError;
import com.vaultops.model.ImportLog;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.ImportErrorRepository;
import com.vaultops.repository.ImportLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetImportService {

    private final FileParserService parserService;
    private final AssetValidationService validationService;
    private final AssetMapperService mapperService;
    private final AssetRepository assetRepository;
    private final ImportLogRepository importLogRepository;

    @Value("${app.import.batch-size}")
    private int batchSize;

    @Transactional
    public ImportResponse processImport(MultipartFile file, boolean dryRun) {
        LocalDateTime startTime = LocalDateTime.now();
        ImportLog importLog = createImportLog(file);

        try {
            log.info("Parsing file: {}", file.getOriginalFilename());
            List<AssetImportDTO> dtos = parserService.parseFile(file);
            importLog.setTotalRecords(dtos.size());

            if (dtos.isEmpty()) {
                throw new InvalidFileException("No data found in file");
            }

            log.info("Validating {} records", dtos.size());
            List<ValidationResult> validationResults = validationService.validateAssets(dtos);

            List<AssetImportDTO> validRecords = new ArrayList<>();
            List<ImportError> errors = new ArrayList<>();

            for (int i = 0; i < dtos.size(); i++) {
                AssetImportDTO dto = dtos.get(i);
                ValidationResult validation = validationResults.get(i);

                if (validation.isValid()) {
                    validRecords.add(dto);
                } else {
                    for (Map.Entry<String, String> error : validation.getFieldErrors().entrySet()) {
                        ImportError importError = new ImportError();
                        importError.setImportLog(importLog);
                        importError.setRowNum(dto.getRowNumber());
                        importError.setFieldName(error.getKey());
                        importError.setErrorMessage(error.getValue());
                        importError.setInvalidValue(getFieldValue(dto, error.getKey()));
                        errors.add(importError);
                    }
                }
            }

            importLog.setSuccessCount(validRecords.size());
            importLog.setErrorCount(dtos.size() - validRecords.size());

            int created = 0;
            int updated = 0;

            if (!dryRun && !validRecords.isEmpty()) {
                log.info("Processing {} valid records", validRecords.size());
                Map<String, Integer> results = processValidRecords(validRecords);
                created = results.get("created");
                updated = results.get("updated");
            }

            if (!errors.isEmpty()) {
                importLog.setErrors(errors);
            }

            importLog.setStatus(
                    errors.isEmpty() ? ImportStatus.SUCCESS : ImportStatus.PARTIAL_SUCCESS
            );
            importLog.setCompletedAt(LocalDateTime.now());
            importLogRepository.save(importLog);

            return buildResponse(
                    true,
                    dtos.size(),
                    validRecords.size(),
                    errors.size(),
                    created,
                    updated,
                    errors,
                    importLog.getId(),
                    startTime
            );

        } catch (Exception e) {
            log.error("Import failed", e);
            importLog.setStatus(ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            importLog.setCompletedAt(LocalDateTime.now());
            importLogRepository.save(importLog);

            return buildErrorResponse(e.getMessage(), importLog.getId(), startTime);
        }
    }

    private ImportLog createImportLog(MultipartFile file) {
        ImportLog log = new ImportLog();
        log.setUserId("system");
        log.setFileName(file.getOriginalFilename());
        log.setFileSize(file.getSize());
        log.setStatus(ImportStatus.PROCESSING);
        log.setStartedAt(LocalDateTime.now());
        return log;
    }

    private Map<String, Integer> processValidRecords(List<AssetImportDTO> validRecords) {
        int created = 0;
        int updated = 0;

        for (int i = 0; i < validRecords.size(); i += batchSize) {
            int end = Math.min(i + batchSize, validRecords.size());
            List<AssetImportDTO> batch = validRecords.subList(i, end);

            List<Asset> toSave = new ArrayList<>();

            for (AssetImportDTO dto : batch) {
                Optional<Asset> existing = assetRepository
                        .findBySerialNumber(dto.getSerialNumber());

                if (existing.isPresent()) {
                    Asset asset = existing.get();
                    mapperService.updateEntity(asset, dto);
                    toSave.add(asset);
                    updated++;
                } else {
                    Asset asset = mapperService.mapToEntity(dto);
                    toSave.add(asset);
                    created++;
                }
            }

            assetRepository.saveAll(toSave);
            assetRepository.flush();
        }

        return Map.of("created", created, "updated", updated);
    }

    private String getFieldValue(AssetImportDTO dto, String fieldName) {
        try {
            return switch (fieldName) {
                case "name" -> dto.getName();
                case "type" -> dto.getType();
                case "location" -> dto.getLocation();
                case "assignment" -> dto.getAssignment();
                case "serialNumber" -> dto.getSerialNumber();
                case "conditionStatus" -> dto.getConditionStatus();
                case "usageStatus" -> dto.getUsageStatus();
                case "assignedTo" -> dto.getAssignedTo();
                case "purchaseDate" -> dto.getPurchaseDate() != null ? dto.getPurchaseDate().toString() : null;
                case "purchasePrice" -> dto.getPurchasePrice();
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private ImportResponse buildResponse(
            boolean success,
            int total,
            int valid,
            int invalid,
            int created,
            int updated,
            List<ImportError> errors,
            Long importLogId,
            LocalDateTime startTime) {

        LocalDateTime endTime = LocalDateTime.now();

        List<RowError> rowErrors = errors.stream()
                .map(e -> new RowError(
                        e.getRowNum(),
                        e.getFieldName(),
                        e.getErrorMessage(),
                        e.getInvalidValue()
                ))
                .collect(Collectors.toList());

        return ImportResponse.builder()
                .success(success)
                .message(invalid == 0 ? "Import completed successfully" :
                        String.format("Import completed with %d errors", invalid))
                .importLogId(importLogId)
                .totalRows(total)
                .validRows(valid)
                .invalidRows(invalid)
                .createdRows(created)
                .updatedRows(updated)
                .errors(rowErrors)
                .startTime(startTime)
                .endTime(endTime)
                .durationMs(Duration.between(startTime, endTime).toMillis())
                .build();
    }

    private ImportResponse buildErrorResponse(
            String errorMessage, Long importLogId, LocalDateTime startTime) {

        return ImportResponse.builder()
                .success(false)
                .message("Import failed: " + errorMessage)
                .importLogId(importLogId)
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .build();
    }
}