package com.vaultops.dtos;

public record MonthlyTrendDTO(
    String month,
    long assetCount,
    long maintenanceCount
) {}
