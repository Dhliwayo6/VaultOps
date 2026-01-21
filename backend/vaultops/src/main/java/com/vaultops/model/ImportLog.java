package com.vaultops.model;

import com.vaultops.enums.ImportStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
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
    private String errorMessages;
    private LocalDate startedAt;
    private LocalDate completedAt;

    @OneToMany(mappedBy = "import_log", cascade = CascadeType.ALL)
    private List<ImportError> errors = new ArrayList<>();
}
