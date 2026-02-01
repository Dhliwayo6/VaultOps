package com.vaultops.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetImportDTO {
    private Integer rowNumber;
    private String parseError;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Type is required")
    @Size(max = 255, message = "Type must not exceed 255 characters")
    private String type;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @NotBlank(message = "Assignment is required")
    private String assignment;

    @NotBlank(message = "Serial number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Serial number must contain only uppercase letters, numbers, and hyphens")
    private String serialNumber;

    @NotBlank(message = "Condition status is required")
    private String conditionStatus;

    @NotBlank(message = "Usage status is required")
    private String usageStatus;

    // Optional fields
    private String assignedTo;
    private LocalDate purchaseDate;
    private String purchasePrice; // String for validation before converting to BigDecimal
}