package com.vaultops.exceptions;

import com.vaultops.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler(NoAssetsMessageException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleNoAssetsException(NoAssetsMessageException e) {
        return new ErrorResponse(e.getMessage());
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

    //Catch-all for unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return new ErrorResponse("An unexpected error occurred. Please try again later.");
    }
}
