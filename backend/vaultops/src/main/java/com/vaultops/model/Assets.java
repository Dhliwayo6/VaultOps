package com.vaultops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "assets")
public class Assets {
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

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "purchasePrice")
    private BigDecimal purchasePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "condtion")
    private Condition condition;

    @Enumerated(EnumType.STRING)
    private Usage usage;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    public Assets(Long id, String name,
                  String type, String location,
                  String serialNumber,
                  BigDecimal purchasePrice,
                  Condition condition,
                  Usage usage, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.serialNumber = serialNumber;
        this.purchasePrice = purchasePrice;
        this.condition = condition;
        this.usage = usage;
        this.createdAt = createdAt;
    }

    public Assets() {
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

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
