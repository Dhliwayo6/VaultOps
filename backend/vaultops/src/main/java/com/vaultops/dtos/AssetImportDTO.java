package com.vaultops.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetImportDTO {
    private Integer rowNumber;
    private String parseError;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Serial number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Invalid serial number format")
    private String serialNumber;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Condition is required")
    private String conditionStatus;
    private String usageStatus;
    private LocalDate purchaseDate;

    @NotBlank(message = "Location is required")
    private String location;

    private String assignment;
    private String notes;
}
