package com.vaultops.services;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.exceptions.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileParserService {

    public List<AssetImportDTO> parseFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new InvalidFileException("File name is null");
        }

        if (filename.endsWith(".xlsx")) {
            return parseExcel(file.getInputStream());
        } else if (filename.endsWith(".csv")) {
            return parseCsv(file.getInputStream());
        }

        throw new InvalidFileException("Unsupported file format");
    }

    private List<AssetImportDTO> parseExcel(InputStream inputStream) throws IOException {
        List<AssetImportDTO> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getLastRowNum() < 1) {
                throw new InvalidFileException("File contains no data rows");
            }

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = mapHeaders(headerRow);
            validateRequiredColumns(columnMap);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                try {
                    AssetImportDTO dto = parseExcelRow(row, columnMap);
                    dto.setRowNumber(i + 1);
                    dtos.add(dto);
                } catch (Exception e) {
                    log.warn("Error parsing row {}: {}", i + 1, e.getMessage());
                    AssetImportDTO errorDto = new AssetImportDTO();
                    errorDto.setRowNumber(i + 1);
                    errorDto.setParseError(e.getMessage());
                    dtos.add(errorDto);
                }
            }
        }

        return dtos;
    }

    private Map<String, Integer> mapHeaders(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue()
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "");
            columnMap.put(header, cell.getColumnIndex());
        }

        return columnMap;
    }

    private void validateRequiredColumns(Map<String, Integer> columnMap) {
        List<String> required = Arrays.asList(
                "name", "type", "location", "assignment", "serialnumber",
                "conditionstatus", "usagestatus"
        );

        List<String> missing = required.stream()
                .filter(col -> !columnMap.containsKey(col))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new InvalidFileException(
                    "Missing required columns: " + String.join(", ", missing)
            );
        }
    }

    private AssetImportDTO parseExcelRow(Row row, Map<String, Integer> columnMap) {
        AssetImportDTO dto = new AssetImportDTO();

        dto.setName(getCellValue(row, columnMap, "name"));
        dto.setType(getCellValue(row, columnMap, "type"));
        dto.setLocation(getCellValue(row, columnMap, "location"));
        dto.setAssignment(getCellValue(row, columnMap, "assignment"));
        dto.setSerialNumber(getCellValue(row, columnMap, "serialnumber"));
        dto.setConditionStatus(getCellValue(row, columnMap, "conditionstatus"));
        dto.setUsageStatus(getCellValue(row, columnMap, "usagestatus"));
        dto.setAssignedTo(getCellValue(row, columnMap, "assignedto"));
        dto.setPurchaseDate(getDateCellValue(row, columnMap, "purchasedate"));

        String purchasePrice = getCellValue(row, columnMap, "purchaseprice");
        if (purchasePrice != null && !purchasePrice.isEmpty()) {
            dto.setPurchasePrice(purchasePrice);
        }

        return dto;
    }

    private String getCellValue(Row row, Map<String, Integer> columnMap, String header) {
        Integer colIndex = columnMap.get(header);
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        yield String.valueOf((long) value);
                    } else {
                        yield String.valueOf(value);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    private LocalDate getDateCellValue(Row row, Map<String, Integer> columnMap, String header) {
        Integer colIndex = columnMap.get(header);
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                return parseDate(dateStr);
            }
        } catch (Exception e) {
            log.warn("Failed to parse date from cell: {}", e.getMessage());
        }

        return null;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                log.warn(e.getMessage());
            }
        }

        throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValue(row, Map.of(String.valueOf(i), i), String.valueOf(i));
                if (value != null && !value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<AssetImportDTO> parseCsv(InputStream inputStream) throws IOException {
        List<AssetImportDTO> dtos = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(',')
                        .withIgnoreQuotations(false)
                        .build())
                .build()) {

            String[] headers = reader.readNext();
            if (headers == null) {
                throw new InvalidFileException("CSV file is empty");
            }

            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                columnMap.put(header, i);
            }

            validateRequiredColumns(columnMap);

            String[] line;
            int rowNum = 2;

            while ((line = reader.readNext()) != null) {
                if (isEmptyLine(line)) continue;

                try {
                    AssetImportDTO dto = parseCsvRow(line, columnMap);
                    dto.setRowNumber(rowNum);
                    dtos.add(dto);
                } catch (Exception e) {
                    log.warn("Error parsing CSV row {}: {}", rowNum, e.getMessage());
                    AssetImportDTO errorDto = new AssetImportDTO();
                    errorDto.setRowNumber(rowNum);
                    errorDto.setParseError(e.getMessage());
                    dtos.add(errorDto);
                }

                rowNum++;
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return dtos;
    }

    private AssetImportDTO parseCsvRow(String[] line, Map<String, Integer> columnMap) {
        AssetImportDTO dto = new AssetImportDTO();

        dto.setName(getValue(line, columnMap, "name"));
        dto.setType(getValue(line, columnMap, "type"));
        dto.setLocation(getValue(line, columnMap, "location"));
        dto.setAssignment(getValue(line, columnMap, "assignment"));
        dto.setSerialNumber(getValue(line, columnMap, "serialnumber"));
        dto.setConditionStatus(getValue(line, columnMap, "conditionstatus"));
        dto.setUsageStatus(getValue(line, columnMap, "usagestatus"));
        dto.setAssignedTo(getValue(line, columnMap, "assignedto"));

        String purchaseDate = getValue(line, columnMap, "purchasedate");
        if (purchaseDate != null && !purchaseDate.isEmpty()) {
            dto.setPurchaseDate(parseDate(purchaseDate));
        }

        dto.setPurchasePrice(getValue(line, columnMap, "purchaseprice"));

        return dto;
    }

    private String getValue(String[] line, Map<String, Integer> columnMap, String header) {
        Integer index = columnMap.get(header);
        if (index == null || index >= line.length) return null;

        String value = line[index].trim();
        return value.isEmpty() ? null : value;
    }

    private boolean isEmptyLine(String[] line) {
        return Arrays.stream(line).allMatch(s -> s == null || s.trim().isEmpty());
    }
}