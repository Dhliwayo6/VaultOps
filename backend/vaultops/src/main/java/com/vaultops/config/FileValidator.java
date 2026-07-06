package com.vaultops.config;

import com.vaultops.exceptions.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class FileValidator {

    @Value("${app.import.allowed-extensions}")
    private String allowedExtensions;

    @Value("${app.import.max-file-size}")
    private long maxFileSize;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InvalidFileException("File name is missing");
        }

        // Validate extension
        String extension = getFileExtension(filename);
        List<String> allowed = Arrays.asList(allowedExtensions.split(","));

        if (!allowed.contains(extension.toLowerCase())) {
            throw new InvalidFileException(
                    String.format("Invalid file type '%s'. Allowed: %s",
                            extension, String.join(", ", allowed))
            );
        }

        // Validate size
        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException(
                    String.format("File size %.2f MB exceeds maximum of %.2f MB",
                            file.getSize() / 1024.0 / 1024.0,
                            maxFileSize / 1024.0 / 1024.0)
            );
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (!isValidMimeType(contentType, extension)) {
            throw new InvalidFileException(
                    "File content type does not match extension"
            );
        }

        // Validate magic bytes/file signature
        validateMagicBytes(file, extension);
    }

    private void validateMagicBytes(MultipartFile file, String extension) {
        try (java.io.InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int read = is.read(header);
            if (read < 4) {
                throw new InvalidFileException("File is too small to be valid");
            }
            if (extension.equalsIgnoreCase("xlsx")) {
                // ZIP magic bytes: 0x50, 0x4B, 0x03, 0x04
                if (header[0] != 0x50 || header[1] != 0x4B || header[2] != 0x03 || header[3] != 0x04) {
                    throw new InvalidFileException("Invalid Excel file format (incorrect magic bytes)");
                }
            } else if (extension.equalsIgnoreCase("csv")) {
                // Verify CSV starts with plain text characters, not binary null bytes
                for (int i = 0; i < read; i++) {
                    if (header[i] == 0) {
                        throw new InvalidFileException("Invalid CSV file format (binary content detected)");
                    }
                }
                byte[] checkBuffer = new byte[1024];
                int checkRead = is.read(checkBuffer);
                for (int i = 0; i < checkRead; i++) {
                    if (checkBuffer[i] == 0) {
                        throw new InvalidFileException("Invalid CSV file format (binary content detected)");
                    }
                }
            }
        } catch (java.io.IOException e) {
            throw new InvalidFileException("Failed to read file contents: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }

    private boolean isValidMimeType(String contentType, String extension) {
        if (contentType == null) return false;

        return switch (extension.toLowerCase()) {
            case "xlsx" -> contentType.contains("spreadsheetml") ||
                    contentType.contains("excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "csv" -> contentType.contains("csv") ||
                    contentType.contains("text") ||
                    contentType.equals("text/csv");
            default -> false;
        };
    }
}
