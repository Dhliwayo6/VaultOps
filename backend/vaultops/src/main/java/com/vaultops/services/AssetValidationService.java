package com.vaultops.services;

import com.vaultops.dtos.AssetImportDTO;
import com.vaultops.dtos.ValidationResult;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetValidationService {

    private final AssetRepository assetRepository;
    private final Validator validator;

    public List<ValidationResult> validateAssets(List<AssetImportDTO> dtos) {
        return dtos.stream()
                .map(this::validateAsset)
                .collect(Collectors.toList());
    }

    public ValidationResult validateAsset(AssetImportDTO dto) {
        ValidationResult result = new ValidationResult();

        if (dto.getParseError() != null) {
            result.addError("parse", dto.getParseError());
            return result;
        }

        Set<ConstraintViolation<AssetImportDTO>> violations = validator.validate(dto);
        for (ConstraintViolation<AssetImportDTO> violation : violations) {
            result.addError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        validateEnums(dto, result);
        validateDates(dto, result);
        validateBusinessRules(dto, result);

        return result;
    }

    private void validateEnums(AssetImportDTO dto, ValidationResult result) {
        if (dto.getAssignment() != null) {
            try {
                Assignment.valueOf(dto.getAssignment().toUpperCase());
            } catch (IllegalArgumentException e) {
                result.addError("assignment",
                        "Invalid value. Must be one of: " +
                                Arrays.toString(Assignment.values()));
            }
        }

        if (dto.getConditionStatus() != null) {
            try {
                ConditionStatus.valueOf(dto.getConditionStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                result.addError("conditionStatus",
                        "Invalid value. Must be one of: " +
                                Arrays.toString(ConditionStatus.values()));
            }
        }

        if (dto.getUsageStatus() != null) {
            try {
                Usage.valueOf(dto.getUsageStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                result.addError("usageStatus",
                        "Invalid value. Must be one of: " +
                                Arrays.toString(Usage.values()));
            }
        }
    }

    private void validateDates(AssetImportDTO dto, ValidationResult result) {
        LocalDate today = LocalDate.now();

        if (dto.getPurchaseDate() != null && dto.getPurchaseDate().isAfter(today)) {
            result.addError("purchaseDate", "Purchase date cannot be in the future");
        }
    }

    private void validateBusinessRules(AssetImportDTO dto, ValidationResult result) {
        if (dto.getPurchasePrice() != null && !dto.getPurchasePrice().isEmpty()) {
            try {
                Double.parseDouble(dto.getPurchasePrice());
            } catch (NumberFormatException e) {
                result.addError("purchasePrice", "Invalid purchase price format");
            }
        }
    }
}