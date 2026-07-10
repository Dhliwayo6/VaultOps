package com.vaultops.dtos;

import java.math.BigDecimal;

public record FinancialStatsDTO(
    BigDecimal totalAssetValuation,
    BigDecimal averageAssetValue,
    BigDecimal totalMaintenanceExpenditure
) {}
