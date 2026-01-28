package com.vaultops.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "import_errors")
@Data
public class ImportError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "import_log_id")
    private ImportLog importLog;

    private Integer rowNum;
    private String fieldName;
    private String errorMessage;
    private String invalidValue;
}
