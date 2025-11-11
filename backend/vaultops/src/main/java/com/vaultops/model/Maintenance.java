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
    private Asset asset;

    @Column(name = "date")
    private LocalDate date;

    @NotBlank(message = "Service provider cannot be blank!")
    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "description")
    private String description;

    @Column(name = "cost")
    private BigDecimal cost;
}
