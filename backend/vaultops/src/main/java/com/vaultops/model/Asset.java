package com.vaultops.model;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(name = "location")
    private String location;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false)
    private ConditionStatus conditionStatus;

    @Enumerated(EnumType.STRING)
    private Usage usageStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;
}
