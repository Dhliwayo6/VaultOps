package com.vaultops.model;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public Asset(Long id, String name,
                 String type, String location,
                 String serialNumber,
                 BigDecimal purchasePrice,
                 LocalDate purchaseDate,
                 ConditionStatus conditionStatus,
                 Usage usage, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.serialNumber = serialNumber;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
        this.conditionStatus = conditionStatus;
        this.usageStatus = usage;
        this.createdAt = createdAt;
    }

    public Asset() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public ConditionStatus getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ConditionStatus conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public Usage getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(Usage usage) {
        this.usageStatus = usage;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
