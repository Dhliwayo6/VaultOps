package com.vaultops.model;

import com.vaultops.enums.ImportStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "import_logs")
@Data
public class ImportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String fileName;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    private Integer totalRecords;
    private Integer successCount;
    private Integer errorCount;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "importLog", cascade = CascadeType.ALL)
    private List<ImportError> errors = new ArrayList<>();
}
