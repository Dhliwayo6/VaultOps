package com.vaultops.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RowError {
    private int rowNumber;
    private String fieldName;
    private String errorMessage;
    private String invalidValue;
}
