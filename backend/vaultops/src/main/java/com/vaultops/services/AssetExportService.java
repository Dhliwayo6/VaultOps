package com.vaultops.services;

import com.opencsv.CSVWriter;
import com.vaultops.dtos.ExportFilter;
import com.vaultops.exceptions.ExportException;
import com.vaultops.exceptions.NoDataException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetExportService {

    private final AssetRepository assetRepository;

    public ByteArrayResource exportToExcel(ExportFilter filter) {
        log.info("Starting Excel export with filter: {}", filter);

        List<Asset> assets = fetchAssets(filter);

        if (assets.isEmpty()) {
            throw new NoDataException("No assets found matching the filter criteria");
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Assets");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            createHeaderRow(sheet, headerStyle);

            int rowNum = 1;
            for (Asset asset : assets) {
                Row row = sheet.createRow(rowNum++);
                populateAssetRow(row, asset, dateStyle, currencyStyle);
            }

            for (int i = 0; i < 13; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 12));
            sheet.createFreezePane(0, 1);

            workbook.write(out);
            log.info("Excel export completed. {} assets exported", assets.size());
            return new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ExportException("Failed to export to Excel", e);
        }
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID", "Name", "Type", "Serial Number", "Location", "Assignment",
                "Condition Status", "Usage Status", "Assigned To", "Purchase Date",
                "Purchase Price", "Created At", "Latest Updated Date"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void populateAssetRow(Row row, Asset asset, CellStyle dateStyle, CellStyle currencyStyle) {
        row.createCell(0).setCellValue(asset.getId());
        row.createCell(1).setCellValue(asset.getName());
        row.createCell(2).setCellValue(asset.getType());
        row.createCell(3).setCellValue(asset.getSerialNumber() != null ? asset.getSerialNumber() : "");
        row.createCell(4).setCellValue(asset.getLocation());
        row.createCell(5).setCellValue(asset.getAssignment().toString());
        row.createCell(6).setCellValue(asset.getConditionStatus().toString());
        row.createCell(7).setCellValue(asset.getUsageStatus().toString());
        row.createCell(8).setCellValue(asset.getAssignedTo() != null ? asset.getAssignedTo() : "");

        if (asset.getPurchaseDate() != null) {
            Cell dateCell = row.createCell(9);
            dateCell.setCellValue(asset.getPurchaseDate());
            dateCell.setCellStyle(dateStyle);
        } else {
            row.createCell(9).setCellValue("");
        }

        if (asset.getPurchasePrice() != null) {
            Cell priceCell = row.createCell(10);
            priceCell.setCellValue(asset.getPurchasePrice().doubleValue());
            priceCell.setCellStyle(currencyStyle);
        } else {
            row.createCell(10).setCellValue("");
        }

        if (asset.getCreatedAt() != null) {
            Cell createdCell = row.createCell(11);
            createdCell.setCellValue(asset.getCreatedAt());
            createdCell.setCellStyle(dateStyle);
        }

        if (asset.getLatestUpdatedDate() != null) {
            Cell updatedCell = row.createCell(12);
            updatedCell.setCellValue(asset.getLatestUpdatedDate());
            updatedCell.setCellStyle(dateStyle);
        }
    }

    public ByteArrayResource exportToCsv(ExportFilter filter) {
        log.info("Starting CSV export with filter: {}", filter);

        List<Asset> assets = fetchAssets(filter);

        if (assets.isEmpty()) {
            throw new NoDataException("No assets found matching the filter criteria");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            String[] header = {
                    "ID", "Name", "Type", "Serial Number", "Location", "Assignment",
                    "Condition Status", "Usage Status", "Assigned To", "Purchase Date",
                    "Purchase Price", "Created At", "Latest Updated Date"
            };
            writer.writeNext(header);

            for (Asset asset : assets) {
                String[] data = {
                        String.valueOf(asset.getId()),
                        asset.getName(),
                        asset.getType(),
                        asset.getSerialNumber() != null ? asset.getSerialNumber() : "",
                        asset.getLocation(),
                        asset.getAssignment().toString(),
                        asset.getConditionStatus().toString(),
                        asset.getUsageStatus().toString(),
                        asset.getAssignedTo() != null ? asset.getAssignedTo() : "",
                        asset.getPurchaseDate() != null ? asset.getPurchaseDate().toString() : "",
                        asset.getPurchasePrice() != null ? asset.getPurchasePrice().toString() : "",
                        asset.getCreatedAt() != null ? asset.getCreatedAt().toString() : "",
                        asset.getLatestUpdatedDate() != null ? asset.getLatestUpdatedDate().toString() : ""
                };
                writer.writeNext(data);
            }

            writer.flush();
            log.info("CSV export completed. {} assets exported", assets.size());
            return new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ExportException("Failed to export to CSV", e);
        }
    }

    private List<Asset> fetchAssets(ExportFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return assetRepository.findAll();
        }

        Specification<Asset> spec = buildSpecification(filter);
        return assetRepository.findAll(spec);
    }

    private Specification<Asset> buildSpecification(ExportFilter filter) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getCategory()));
            }

            if (filter.getCondition() != null) {
                predicates.add(cb.equal(root.get("conditionStatus"), filter.getCondition()));
            }

            if (filter.getUsage() != null) {
                predicates.add(cb.equal(root.get("usageStatus"), filter.getUsage()));
            }

            if (filter.getLocation() != null) {
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + filter.getLocation().toLowerCase() + "%"));
            }

            if (filter.getPurchaseDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"),
                        filter.getPurchaseDateFrom()));
            }

            if (filter.getPurchaseDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"),
                        filter.getPurchaseDateTo()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }
}