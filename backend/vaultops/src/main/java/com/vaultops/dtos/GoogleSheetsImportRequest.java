package com.vaultops.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleSheetsImportRequest {
    @NotBlank(message = "Spreadsheet ID is required")
    private String spreadsheetId;
    private String sheetName = "sheet1";
}




