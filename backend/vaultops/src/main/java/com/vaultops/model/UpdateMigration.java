package com.vaultops.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMigration {
    private Long id;
    private Migration migration;
}
