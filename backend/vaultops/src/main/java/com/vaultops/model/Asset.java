package com.vaultops.model;

import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "assets")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Asset name cannot be blank")
    private String name;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Asset type is required")
    private String type;

    @Column(name = "location", nullable = false)
    private String location;

    @NotNull(message = "Assignment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment", nullable = false)
    private Assignment assignment;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @NotNull(message = "Condition status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false)
    private ConditionStatus conditionStatus;

    @NotNull(message = "Usage status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Usage usageStatus;

    private String AssignedTo;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;
}
