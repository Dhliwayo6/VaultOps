package com.vaultops.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleSheetsExportRequest {
    private String spreadsheetId;
    private String title;
    private String sheetName;
}
