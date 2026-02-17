package com.vaultops.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig {

    @Value("${app.import.max-file-size}")
    private long maxFileSize;

    @Value("${app.import.temp-directory}")
    private String tempDirectory;

    @PostConstruct
    public void init() {
        Path tempPath = Paths.get(tempDirectory);
        try {
            Files.createDirectories(tempPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }
}
