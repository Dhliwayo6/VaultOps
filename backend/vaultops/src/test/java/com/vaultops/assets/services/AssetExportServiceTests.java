package com.vaultops.assets.services;

import com.vaultops.dtos.ExportFilter;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.NoDataException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.AssetExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Export Service Tests")
public class AssetExportServiceTests {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetExportService assetExportService;

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setId(1L);
        asset.setName("Laptop");
        asset.setType("Electronics");
        asset.setSerialNumber("SN-12345");
        asset.setLocation("Cape Town");
        asset.setAssignment(Assignment.UNASSIGNED);
        asset.setConditionStatus(ConditionStatus.EXCELLENT);
        asset.setUsageStatus(Usage.STORAGE);
        asset.setPurchasePrice(BigDecimal.valueOf(15000.50));
        asset.setCreatedAt(LocalDateTime.now());
        asset.setLatestUpdatedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should export assets to Excel successfully")
    void exportToExcel_ShouldCreateValidExcelFile() throws IOException {
        when(assetRepository.findAll()).thenReturn(List.of(asset));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assetExportService.exportToExcel(null, out);

        byte[] bytes = out.toByteArray();
        assertThat(bytes).isNotEmpty();

        // Verify it can be opened as an Excel workbook and has headers
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Assets");
            assertThat(sheet).isNotNull();
            Row headerRow = sheet.getRow(0);
            assertThat(headerRow).isNotNull();
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Name");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("Type");

            Row dataRow = sheet.getRow(1);
            assertThat(dataRow).isNotNull();
            assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("Laptop");
            assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("Electronics");
        }
    }

    @Test
    @DisplayName("Should throw NoDataException when exporting empty dataset to Excel")
    void exportToExcel_WithNoData_ShouldThrowNoDataException() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThatThrownBy(() -> assetExportService.exportToExcel(null, out))
                .isInstanceOf(NoDataException.class)
                .hasMessageContaining("No assets found matching the filter criteria");
    }

    @Test
    @DisplayName("Should export assets to CSV successfully")
    void exportToCsv_ShouldCreateValidCsv() {
        when(assetRepository.findAll()).thenReturn(List.of(asset));

        ByteArrayResource resource = assetExportService.exportToCsv(null);
        assertThat(resource).isNotNull();

        String csvContent = new String(resource.getByteArray());
        assertThat(csvContent).contains("\"ID\",\"Name\",\"Type\",\"Serial Number\"");
        assertThat(csvContent).contains("\"1\",\"Laptop\",\"Electronics\",\"SN-12345\"");
    }

    @Test
    @DisplayName("Should throw NoDataException when exporting empty dataset to CSV")
    void exportToCsv_WithNoData_ShouldThrowNoDataException() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assetExportService.exportToCsv(null))
                .isInstanceOf(NoDataException.class);
    }

    @Test
    @DisplayName("Should support filtering in export using repository specifications")
    void exportToExcel_WithFilter_ShouldUseRepositoryWithSpec() throws IOException {
        ExportFilter filter = ExportFilter.builder()
                .category("Electronics")
                .condition(ConditionStatus.EXCELLENT)
                .build();

        when(assetRepository.findAll(any(Specification.class))).thenReturn(List.of(asset));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assetExportService.exportToExcel(filter, out);

        assertThat(out.toByteArray()).isNotEmpty();
        verify(assetRepository, times(1)).findAll(any(Specification.class));
    }
}
