package com.vaultops.assets.controllers;

import com.vaultops.exceptions.*;
import com.vaultops.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Global Exception Handler Tests")
public class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Should format AssetNotFoundException properly")
    void testAssetNotFoundException() {
        AssetNotFoundException ex = new AssetNotFoundException();
        ErrorResponse res = handler.handleAssetNotFoundException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Asset not found!");
    }

    @Test
    @DisplayName("Should format NoResultsException properly")
    void testNoResultsException() {
        NoResultsException ex = new NoResultsException();
        ErrorResponse res = handler.handleNoResultsException(ex, request);
        assertThat(res.getMessage()).isEqualTo("No results found");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException")
    void testHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");
        ErrorResponse res = handler.handleHttpMessageNotReadableException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Invalid JSON input or malformed enum value");
    }

    @Test
    @DisplayName("Should format MigrationNotFoundException")
    void testMigrationNotFoundException() {
        MigrationNotFoundException ex = new MigrationNotFoundException();
        ErrorResponse res = handler.handleMigrationNotFoundException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Migration history not found!");
    }

    @Test
    @DisplayName("Should format MaintenanceNotFoundException")
    void testMaintenanceNotFoundException() {
        MaintenanceNotFoundException ex = new MaintenanceNotFoundException();
        ErrorResponse res = handler.handleMaintenanceNotFoundException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Maintenance not found!");
    }

    @Test
    @DisplayName("Should format InvalidFileException")
    void testInvalidFileException() {
        InvalidFileException ex = new InvalidFileException("Bad file format");
        ErrorResponse res = handler.handleInvalidFileException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Bad file format");
    }

    @Test
    @DisplayName("Should format NoDataException")
    void testNoDataException() {
        NoDataException ex = new NoDataException("No data available");
        ErrorResponse res = handler.handleNoDataException(ex, request);
        assertThat(res.getMessage()).isEqualTo("No data available");
    }

    @Test
    @DisplayName("Should format ExportException")
    void testExportException() {
        ExportException ex = new ExportException("Excel export failure", null);
        ErrorResponse res = handler.handleExportException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Excel export failure");
    }

    @Test
    @DisplayName("Should handle MaxUploadSizeExceededException")
    void testMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024L);
        ErrorResponse res = handler.handleMaxUploadSizeExceeded(ex);
        assertThat(res.getMessage()).isEqualTo("File size exceeds maximum allowed size of 10MB");
    }

    @Test
    @DisplayName("Should format IllegalArgumentException")
    void testIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid ID parameter");
        ErrorResponse res = handler.handleIllegalArgumentException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Invalid parameter: Invalid ID parameter");
    }

    @Test
    @DisplayName("Should format JobNotFoundException")
    void testJobNotFoundException() {
        JobNotFoundException ex = new JobNotFoundException("Job 404");
        ErrorResponse res = handler.handleJobNotFoundException(ex, request);
        assertThat(res.getMessage()).isEqualTo("Job 404");
    }

    @Test
    @DisplayName("Should handle AccessDeniedException")
    void testAccessDeniedException() {
        org.springframework.security.access.AccessDeniedException ex = new org.springframework.security.access.AccessDeniedException("Denied");
        ErrorResponse res = handler.handleAccessDenied(ex, request);
        assertThat(res.getMessage()).isEqualTo("Access Denied: You do not have permission to perform this action.");
    }

    @Test
    @DisplayName("Should handle generic exception")
    void testGenericException() throws Exception {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("An internal server error occurred.");
    }
}
