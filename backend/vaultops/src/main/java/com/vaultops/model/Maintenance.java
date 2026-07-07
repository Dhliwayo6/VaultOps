package com.vaultops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    @jakarta.validation.constraints.NotNull(message = "Asset is required")
    private Asset asset;

    @Column(name = "date")
    @jakarta.validation.constraints.PastOrPresent(message = "Maintenance date cannot be in the future")
    private LocalDate date;

    @NotBlank(message = "Service provider cannot be blank!")
    @jakarta.validation.constraints.Size(max = 255, message = "Performed by must not exceed 255 characters")
    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "description")
    @jakarta.validation.constraints.Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Column(name = "cost")
    @jakarta.validation.constraints.DecimalMin(value = "0.0", message = "Cost must be positive or zero")
    private BigDecimal cost;
}
