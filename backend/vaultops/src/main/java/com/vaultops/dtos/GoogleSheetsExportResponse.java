package com.vaultops.dtos;

import lombok.Data;

@Data
public class GoogleSheetsExportResponse {
    private String spreadsheetId;
    private String spreadsheetUrl;
    private String sheetName;
    private int rowCount;
}
