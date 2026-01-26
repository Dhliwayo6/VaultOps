package com.vaultops.dtos;

import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExportFilter {
    private String category;
    private ConditionStatus condition;
    private Usage usage;
    private String location;
    private LocalDate purchaseDateFrom;
    private LocalDate purchaseDateTo;

    public boolean isEmpty() {
        return category == null && condition == null && usage == null &&
                location == null && purchaseDateFrom == null && purchaseDateTo == null;
    }
}