package com.vaultops.dtos;

public record DashboardStatsDTO(
    long totalAssets,
    long inUseCount,
    long inStorageCount,
    long inServiceCount,
    long excellentCount,
    long goodCount,
    long fairCount,
    long badCount,
    long damagedCount,
    double averageDaysInRepair
) {}
