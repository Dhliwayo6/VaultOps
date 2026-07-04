//package com.vaultops.services;
//
//import com.google.api.services.sheets.v4.Sheets;
//import com.vaultops.repository.AssetRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class GoogleSheetsImportService {
//
//    private final Sheets sheetsService;
//    private final FileParserService parserService;
//
//    public List<AssetImportDTO> importFromGoogleSheets(
//            String spreadsheetId, String sheetName) throws IOException {
//
//        log.info("Importing from Google Sheets: {} - {}", spreadsheetId, sheetName);
//
//        // Construct range (e.g., "Sheet1" or "Sheet1!A1:Z1000")
//        String range = sheetName != null ? sheetName : "Sheet1";
//
//        // Fetch data from Google Sheets
//        ValueRange response = sheetsService.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//
//        List<List<Object>> values = response.getValues();
//
//        if (values == null || values.isEmpty()) {
//            throw new NoDataException("No data found in spreadsheet");
//        }
//
//        // Convert to DTOs
//        return parseGoogleSheetsData(values);
//    }
//
//    private List<AssetImportDTO> parseGoogleSheetsData(List<List<Object>> values) {
//        List<AssetImportDTO> dtos = new ArrayList<>();
//
//        // First row is header
//        List<Object> headerRow = values.get(0);
//        Map<String, Integer> columnMap = new HashMap<>();
//
//        for (int i = 0; i < headerRow.size(); i++) {
//            String header = headerRow.get(i).toString()
//                    .trim()
//                    .toLowerCase()
//                    .replaceAll("[^a-z0-9]", "");
//            columnMap.put(header, i);
//        }
//
//        // Validate required columns
//        validateRequiredColumns(columnMap);
//
//        // Parse data rows
//        for (int i = 1; i < values.size(); i++) {
//            List<Object> row = values.get(i);
//
//            if (isEmptyRow(row)) {
//                continue;
//            }
//
//            try {
//                AssetImportDTO dto = parseRow(row, columnMap);
//                dto.setRowNumber(i + 1); // Google Sheets row number
//                dtos.add(dto);
//            } catch (Exception e) {
//                log.warn("Error parsing row {}: {}", i + 1, e.getMessage());
//                AssetImportDTO errorDto = new AssetImportDTO();
//                errorDto.setRowNumber(i + 1);
//                errorDto.setParseError(e.getMessage());
//                dtos.add(errorDto);
//            }
//        }
//
//        return dtos;
//    }
//
//    private AssetImportDTO parseRow(List<Object> row, Map<String, Integer> columnMap) {
//        AssetImportDTO dto = new AssetImportDTO();
//
//        dto.setName(getValue(row, columnMap, "name"));
//        dto.setSerialNumber(getValue(row, columnMap, "serialnumber"));
//        dto.setCategory(getValue(row, columnMap, "category"));
//        dto.setConditionStatus(getValue(row, columnMap, "condition"));
//        dto.setUsageStatus(getValue(row, columnMap, "usage"));
//
//        String purchaseDate = getValue(row, columnMap, "purchasedate");
//        if (purchaseDate != null && !purchaseDate.isEmpty()) {
//            dto.setPurchaseDate(parseDate(purchaseDate));
//        }
//
//        String warrantyExpiry = getValue(row, columnMap, "warrantyexpiry");
//        if (warrantyExpiry != null && !warrantyExpiry.isEmpty()) {
//            dto.setWarrantyExpiryDate(parseDate(warrantyExpiry));
//        }
//
//        dto.setLocation(getValue(row, columnMap, "location"));
//        dto.setAssignedTo(getValue(row, columnMap, "assignedto"));
//        dto.setNotes(getValue(row, columnMap, "notes"));
//
//        return dto;
//    }
//
//    private String getValue(List<Object> row, Map<String, Integer> columnMap, String header) {
//        Integer index = columnMap.get(header);
//        if (index == null || index >= row.size()) {
//            return null;
//        }
//
//        Object value = row.get(index);
//        if (value == null) {
//            return null;
//        }
//
//        String strValue = value.toString().trim();
//        return strValue.isEmpty() ? null : strValue;
//    }
//
//    private LocalDate parseDate(String dateStr) {
//        List<DateTimeFormatter> formatters = Arrays.asList(
//                DateTimeFormatter.ISO_LOCAL_DATE,
//                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
//                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
//                DateTimeFormatter.ofPattern("MM/dd/yyyy")
//        );
//
//        for (DateTimeFormatter formatter : formatters) {
//            try {
//                return LocalDate.parse(dateStr, formatter);
//            } catch (DateTimeParseException e) {
//                // Try next formatter
//            }
//        }
//
//        throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
//    }
//
//    private void validateRequiredColumns(Map<String, Integer> columnMap) {
//        List<String> required = Arrays.asList(
//                "name", "serialnumber", "category", "condition", "usage"
//        );
//
//        List<String> missing = required.stream()
//                .filter(col -> !columnMap.containsKey(col))
//                .collect(Collectors.toList());
//
//        if (!missing.isEmpty()) {
//            throw new InvalidFileException(
//                    "Missing required columns: " + String.join(", ", missing)
//            );
//        }
//    }
//
//    private boolean isEmptyRow(List<Object> row) {
//        return row.stream().allMatch(obj ->
//                obj == null || obj.toString().trim().isEmpty()
//        );
//    }
//}
