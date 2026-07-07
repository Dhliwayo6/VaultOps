package com.vaultops.assets.services;

import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.dtos.ValidationResult;
import com.vaultops.enums.ImportStatus;
import com.vaultops.model.Asset;
import com.vaultops.model.ImportLog;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.ImportLogRepository;
import com.vaultops.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Import Service Tests")
public class AssetImportServiceTests {

    @Mock private FileParserService parserService;
    @Mock private AssetValidationService validationService;
    @Mock private AssetMapperService mapperService;
    @Mock private AssetRepository assetRepository;
    @Mock private ImportLogRepository importLogRepository;

    @InjectMocks
    private AssetImportService importService;

    private ImportLog importLog;
    private byte[] fileBytes;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(importService, "batchSize", 100);

        importLog = new ImportLog();
        importLog.setId(1L);
        importLog.setStatus(ImportStatus.PROCESSING);
        importLog.setFileName("test.csv");

        fileBytes = "dummy CSV content".getBytes();
    }

    @Test
    @DisplayName("Should successfully import all valid records and update log status to COMPLETED")
    void processImportAsync_ShouldSucceedFully() throws IOException {
        when(importLogRepository.findById(1L)).thenReturn(Optional.of(importLog));

        AssetImportDTO record = new AssetImportDTO();
        record.setName("Server A");
        record.setSerialNumber("SN-AAAA");
        record.setRowNumber(2);

        List<AssetImportDTO> dtos = List.of(record);
        when(parserService.parseFile(fileBytes, "test.csv")).thenReturn(dtos);

        ValidationResult valResult = new ValidationResult(); // valid = true by default
        when(validationService.validateAssets(dtos)).thenReturn(List.of(valResult));

        Asset asset = new Asset();
        asset.setName("Server A");
        when(assetRepository.findBySerialNumber("SN-AAAA")).thenReturn(Optional.empty());
        when(mapperService.mapToEntity(record)).thenReturn(asset);

        importService.processImportAsync(1L, fileBytes, "test.csv", false);

        assertThat(importLog.getStatus()).isEqualTo(ImportStatus.COMPLETED);
        assertThat(importLog.getTotalRecords()).isEqualTo(1);
        assertThat(importLog.getSuccessCount()).isEqualTo(1);
        assertThat(importLog.getErrorCount()).isEqualTo(0);

        verify(assetRepository, times(1)).saveAll(anyList());
        verify(importLogRepository, times(2)).save(importLog);
    }

    @Test
    @DisplayName("Should process import with some invalid rows and update status to PARTIAL_SUCCESS")
    void processImportAsync_ShouldHandlePartialFailures() throws IOException {
        when(importLogRepository.findById(1L)).thenReturn(Optional.of(importLog));

        AssetImportDTO validRecord = new AssetImportDTO();
        validRecord.setName("Server A");
        validRecord.setSerialNumber("SN-AAAA");
        validRecord.setRowNumber(2);

        AssetImportDTO invalidRecord = new AssetImportDTO();
        invalidRecord.setName("");
        invalidRecord.setRowNumber(3);

        List<AssetImportDTO> dtos = Arrays.asList(validRecord, invalidRecord);
        when(parserService.parseFile(fileBytes, "test.csv")).thenReturn(dtos);

        ValidationResult validVal = new ValidationResult();
        ValidationResult invalidVal = new ValidationResult();
        invalidVal.addError("name", "Name is required");

        when(validationService.validateAssets(dtos)).thenReturn(Arrays.asList(validVal, invalidVal));

        Asset asset = new Asset();
        when(assetRepository.findBySerialNumber("SN-AAAA")).thenReturn(Optional.empty());
        when(mapperService.mapToEntity(validRecord)).thenReturn(asset);

        importService.processImportAsync(1L, fileBytes, "test.csv", false);

        assertThat(importLog.getStatus()).isEqualTo(ImportStatus.PARTIAL_SUCCESS);
        assertThat(importLog.getTotalRecords()).isEqualTo(2);
        assertThat(importLog.getSuccessCount()).isEqualTo(1);
        assertThat(importLog.getErrorCount()).isEqualTo(1);
        assertThat(importLog.getErrors()).hasSize(1);
        assertThat(importLog.getErrors().get(0).getErrorMessage()).isEqualTo("Name is required");
    }

    @Test
    @DisplayName("Should fail cleanly and set status to FAILED when parsing throws an exception")
    void processImportAsync_WhenMalformedFile_ShouldSetFailedStatus() throws IOException {
        when(importLogRepository.findById(1L)).thenReturn(Optional.of(importLog));
        when(parserService.parseFile(fileBytes, "test.csv")).thenThrow(new RuntimeException("Invalid CSV headers"));

        importService.processImportAsync(1L, fileBytes, "test.csv", false);

        assertThat(importLog.getStatus()).isEqualTo(ImportStatus.FAILED);
        assertThat(importLog.getErrorMessage()).isEqualTo("Invalid CSV headers");
        verify(assetRepository, never()).saveAll(anyList());
    }
}
