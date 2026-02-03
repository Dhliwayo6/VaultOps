package com.vaultops.controllers;

import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.model.UpdateMigration;
import com.vaultops.services.migration.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MigrationsController {

    private final CreateMigrationService createMigrationService;
    private final GetMigrationsService getMigrationsService;
    private final GetMigrationService getMigrationService;
    private final UpdateMigrationService updateMigrationService;
    private final DeleteMigrationService deleteMigrationService;

    @PostMapping("/migration")
    public ResponseEntity<MigrationDTO> createMigration(@RequestBody Migration migration) {
        return createMigrationService.execute(migration);
    }

    @GetMapping("/migration")
    public ResponseEntity<List<MigrationDTO>> getMigrations() {
        return getMigrationsService.execute(null);
    }

    @GetMapping("/migration/{id}")
    public ResponseEntity<MigrationDTO> getMigrationById(@PathVariable Long id) {
        return getMigrationService.execute(id);
    }

    @PutMapping("/migration/{id}")
    public ResponseEntity<MigrationDTO> updateMigration(@PathVariable Long id,
                                                        @RequestBody Migration migration) {
        return updateMigrationService.execute(new UpdateMigration(id, migration));
    }

    @DeleteMapping("/migration/{id}")
    public ResponseEntity<Void> deleteMigration(@PathVariable Long id) {
        return deleteMigrationService.execute(id);
    }
}

