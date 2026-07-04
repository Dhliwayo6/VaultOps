//package com.vaultops.services;
//
//@Service
//@Slf4j
//public class GoogleSheetsExportService {
//
//    private final Sheets sheetsService;
//    private final AssetRepository assetRepository;
//
//    public GoogleSheetsExportResponse exportToGoogleSheets(
//            GoogleSheetsExportRequest request) throws IOException {
//
//        log.info("Exporting to Google Sheets: {}", request);
//
//        // Fetch assets
//        List<Asset> assets = assetRepository.findAll();
//
//        if (assets.isEmpty()) {
//            throw new NoDataException("No assets to export");
//        }
//
//        // Create or use existing spreadsheet
//        String spreadsheetId = request.getSpreadsheetId() != null
//                ? request.getSpreadsheetId()
//                : createNewSpreadsheet(request.getTitle());
//
//        // Prepare data
//        List<List<Object>> values = prepareExportData(assets);
//
//        // Write to sheet
//        String sheetName = request.getSheetName() != null ? request.getSheetName() : "Sheet1";
//        String range = sheetName + "!A1";
//
//        ValueRange body = new ValueRange().setValues(values);
//
//        sheetsService.spreadsheets().values()
//                .update(spreadsheetId, range, body)
//                .setValueInputOption("RAW")
//                .execute();
//
//        // Format the sheet
//        formatSheet(spreadsheetId, sheetName);
//
//        // Build response
//        GoogleSheetsExportResponse response = new GoogleSheetsExportResponse();
//        response.setSpreadsheetId(spreadsheetId);
//        response.setSpreadsheetUrl(
//                "https://docs.google.com/spreadsheets/d/" + spreadsheetId
//        );
//        response.setRowCount(assets.size());
//        response.setSheetName(sheetName);
//
//        log.info("Export completed. {} assets exported to spreadsheet {}",
//                assets.size(), spreadsheetId);
//
//        return response;
//    }
//
//    private String createNewSpreadsheet(String title) throws IOException {
//        Spreadsheet spreadsheet = new Spreadsheet()
//                .setProperties(new SpreadsheetProperties()
//                        .setTitle(title != null ? title : "Asset Export - " + LocalDateTime.now()));
//
//        spreadsheet = sheetsService.spreadsheets()
//                .create(spreadsheet)
//                .execute();
//
//        return spreadsheet.getSpreadsheetId();
//    }
//
//    private List<List<Object>> prepareExportData(List<Asset> assets) {
//        List<List<Object>> values = new ArrayList<>();
//
//        // Add header
//        values.add(Arrays.asList(
//                "ID", "Name", "Serial Number", "Category", "Condition",
//                "Usage", "Purchase Date", "Warranty Expiry", "Location",
//                "Assigned To", "Created Date", "Last Updated"
//        ));
//
//        // Add data rows
//        for (Asset asset : assets) {
//            values.add(Arrays.asList(
//                    asset.getId(),
//                    asset.getName(),
//                    asset.getSerialNumber(),
//                    asset.getCategory(),
//                    asset.getConditionStatus().toString(),
//                    asset.getUsage().toString(),
//                    asset.getPurchaseDate() != null ? asset.getPurchaseDate().toString() : "",
//                    asset.getWarrantyExpiryDate() != null ? asset.getWarrantyExpiryDate().toString() : "",
//                    asset.getLocation() != null ? asset.getLocation() : "",
//                    asset.getAssignedTo() != null ? asset.getAssignedTo() : "",
//                    asset.getCreatedDate().toString(),
//                    asset.getLastUpdatedDate().toString()
//            ));
//        }
//
//        return values;
//    }
//
//    private void formatSheet(String spreadsheetId, String sheetName) throws IOException {
//        // Get sheet ID
//        Spreadsheet spreadsheet = sheetsService.spreadsheets()
//                .get(spreadsheetId)
//                .execute();
//
//        Integer sheetId = spreadsheet.getSheets().stream()
//                .filter(s -> sheetName.equals(s.getProperties().getTitle()))
//                .findFirst()
//                .map(s -> s.getProperties().getSheetId())
//                .orElse(0);
//
//        List<Request> requests = new ArrayList<>();
//
//        // Format header row (bold, background color)
//        requests.add(new Request()
//                .setRepeatCell(new RepeatCellRequest()
//                        .setRange(new GridRange()
//                                .setSheetId(sheetId)
//                                .setStartRowIndex(0)
//                                .setEndRowIndex(1))
//                        .setCell(new CellData()
//                                .setUserEnteredFormat(new CellFormat()
//                                        .setTextFormat(new TextFormat().setBold(true))
//                                        .setBackgroundColor(new Color()
//                                                .setRed(0.2f)
//                                                .setGreen(0.2f)
//                                                .setBlue(0.8f))
//                                        .setHorizontalAlignment("CENTER")))
//                        .setFields("userEnteredFormat(backgroundColor,textFormat,horizontalAlignment)")));
//
//        // Freeze header row
//        requests.add(new Request()
//                .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
//                        .setProperties(new SheetProperties()
//                                .setSheetId(sheetId)
//                                .setGridProperties(new GridProperties()
//                                        .setFrozenRowCount(1)))
//                        .setFields("gridProperties.frozenRowCount")));
//
//        // Auto-resize columns
//        requests.add(new Request()
//                .setAutoResizeDimensions(new AutoResizeDimensionsRequest()
//                        .setDimensions(new DimensionRange()
//                                .setSheetId(sheetId)
//                                .setDimension("COLUMNS")
//                                .setStartIndex(0)
//                                .setEndIndex(12))));
//
//        // Apply all formatting
//        BatchUpdateSpreadsheetRequest body =
//                new BatchUpdateSpreadsheetRequest().setRequests(requests);
//
//        sheetsService.spreadsheets()
//                .batchUpdate(spreadsheetId, body)
//                .execute();
//    }
//}
