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
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @jakarta.validation.constraints.Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Asset type is required")
    @jakarta.validation.constraints.Size(max = 255, message = "Type must not exceed 255 characters")
    private String type;

    @Column(name = "location", nullable = false)
    @NotBlank(message = "Asset location is required")
    @jakarta.validation.constraints.Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @NotNull(message = "Assignment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment", nullable = false)
    private Assignment assignment;

    @Column(name = "serial_number", unique = true)
    @jakarta.validation.constraints.Size(max = 100, message = "Serial number must not exceed 100 characters")
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Z0-9-]+$", message = "Serial number must contain only uppercase letters, numbers, and hyphens")
    private String serialNumber;

    @Column(name = "purchase_price")
    @jakarta.validation.constraints.DecimalMin(value = "0.0", message = "Purchase price must be positive or zero")
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    @jakarta.validation.constraints.PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @NotNull(message = "Condition status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false)
    private ConditionStatus conditionStatus;

    @NotNull(message = "Usage status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Usage usageStatus;

    @jakarta.validation.constraints.Size(max = 255, message = "Assigned to must not exceed 255 characters")
    private String assignedTo;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "latest_updated_date")
    private LocalDateTime latestUpdatedDate;
}
