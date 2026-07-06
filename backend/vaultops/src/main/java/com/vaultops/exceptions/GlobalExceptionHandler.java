package com.vaultops.exceptions;

import com.vaultops.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleAssetNotFoundException(AssetNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoResultsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleNoResultsException(NoResultsException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HTTP message not readable: {}", e.getMessage());
        String message = "Invalid JSON input or malformed enum value";
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();
            if (ife.getTargetType().isEnum()) {
                message = "Invalid value for enum: " + ife.getValue();
            }
        }
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MigrationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleMigrationNotFoundException(MigrationNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MaintenanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleMaintenanceNotFoundException(MaintenanceNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidFileException(InvalidFileException e) {
        log.warn("Invalid file upload: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoDataException(NoDataException e) {
        log.warn("No data found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ExportException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleExportException(ExportException e) {
        log.error("Export failed", e);
        return new ErrorResponse(e.getMessage());
    }

    // Spring built-in exception
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("File size exceeded: {}", e.getMessage());
        return new ErrorResponse("File size exceeds maximum allowed size of 10MB");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return new ErrorResponse("Invalid parameter: " + e.getMessage());
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleJobNotFoundException(JobNotFoundException e) {
        log.warn("Job not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        String message = "Validation failed: " + errors.size() + " error(s)";
        log.warn("Validation errors: {}", errors);
        return new ErrorResponse(message, errors);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleConstraintViolationException(jakarta.validation.ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        for (jakarta.validation.ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            int lastDot = path.lastIndexOf('.');
            String fieldName = lastDot != -1 ? path.substring(lastDot + 1) : path;
            errors.put(fieldName, violation.getMessage());
        }
        String message = "Validation failed: " + errors.size() + " error(s)";
        log.warn("Constraint violation errors: {}", errors);
        return new ErrorResponse(message, errors);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleAccessDenied(org.springframework.security.access.AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return new ErrorResponse("Access Denied: You do not have permission to perform this action.");
    }
}
