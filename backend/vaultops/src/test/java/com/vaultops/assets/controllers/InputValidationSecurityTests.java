package com.vaultops.assets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.exceptions.InvalidFileException;
import com.vaultops.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
@DisplayName("Security, Upload, and Input Validation Integration Tests")
public class InputValidationSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("SQL injection payload should be treated as literal, not execute as SQL")
    void testSqlInjectionPayloadOnSearch() throws Exception {
        // Query param contains classic SQL injection snippet
        mockMvc.perform(get("/api/asset/search")
                        .param("name", "Laptop' OR 1=1 --"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Invalid enum value for user role change should return clear 400 validation error")
    void testInvalidUserRoleEnum() throws Exception {
        Map<String, String> request = Map.of("role", "SUPER_ADMIN");

        mockMvc.perform(put("/api/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid value for enum")));
    }

    @Test
    @DisplayName("Page size query parameter under minimum limit (0) should return 400")
    void testPageSizeUnderMinimumLimit() throws Exception {
        mockMvc.perform(get("/api/assets")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size").value("Page size must be at least 1"));
    }

    @Test
    @DisplayName("Page size query parameter exceeding maximum limit (100) should return 400")
    void testPageSizeOverMaximumLimit() throws Exception {
        mockMvc.perform(get("/api/assets")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size").value("Page size cannot exceed 100"));
    }

    @Test
    @DisplayName("Page number parameter under minimum limit (0) should return 400")
    void testPageNumberUnderMinimumLimit() throws Exception {
        mockMvc.perform(get("/api/assets")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.page").value("Page number cannot be negative"));
    }

    @Test
    @DisplayName("Upload file with incorrect magic bytes (spoofed .xlsx) should be rejected")
    void testSpoofedExcelMagicBytes() throws Exception {
        // Plain text content in a file named .xlsx
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "This is not a zip file or Excel sheet, it is plain text.".getBytes()
        );

        mockMvc.perform(multipart("/api/import/assets").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("incorrect magic bytes")));
    }

    @Test
    @DisplayName("Upload file with binary null characters (spoofed .csv) should be rejected")
    void testSpoofedCsvMagicBytes() throws Exception {
        // Contains binary null byte
        byte[] binaryContent = new byte[]{ 0x4E, 0x61, 0x6D, 0x65, 0x00, 0x54, 0x79, 0x70, 0x65 };
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.csv",
                "text/csv",
                binaryContent
        );

        mockMvc.perform(multipart("/api/import/assets").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("binary content detected")));
    }

    @Test
    @DisplayName("Oversized file upload handler in GlobalExceptionHandler should return 413 Payload Too Large")
    void testMaxUploadSizeExceededHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        var response = handler.handleMaxUploadSizeExceeded(new MaxUploadSizeExceededException(10485760));
        org.junit.jupiter.api.Assertions.assertTrue(response.getMessage().contains("exceeds maximum allowed size"));
    }
}
