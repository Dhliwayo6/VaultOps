package com.vaultops.dtos;

import com.vaultops.enums.ConditionStatus;

public record CategoryConditionStatDTO(
    String category,
    ConditionStatus conditionStatus,
    long count
) {}
