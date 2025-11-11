package com.vaultops.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateMaintenance {
    private Long id;
    private Maintenance maintenance;

}
