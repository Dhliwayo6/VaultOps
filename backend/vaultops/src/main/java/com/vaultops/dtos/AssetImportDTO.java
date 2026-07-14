package com.vaultops.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
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

    private String assignedTo;
    private LocalDate purchaseDate;

    @DecimalMin(value = "0.00", message = "Purchase price must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Purchase price must be a valid dollar amount")
    private BigDecimal purchasePrice;
}