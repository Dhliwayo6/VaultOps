package com.vaultops.controllers;

import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.model.UpdateMigration;
import com.vaultops.services.migration.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MigrationsController {

    private CreateMigrationService createMigrationService;
    private GetMigrationsService getMigrationsService;
    private GetMigrationService getMigrationService;
    private UpdateMigrationService updateMigrationService;
    private DeleteMigrationService deleteMigrationService;

    public MigrationsController(CreateMigrationService createMigrationService,
                                GetMigrationsService getMigrationsService,
                                GetMigrationService getMigrationService,
                                UpdateMigrationService updateMigrationService,
                                DeleteMigrationService deleteMigrationService) {
        this.createMigrationService = createMigrationService;
        this.getMigrationsService = getMigrationsService;
        this.getMigrationService = getMigrationService;
        this.updateMigrationService = updateMigrationService;
        this.deleteMigrationService = deleteMigrationService;
    }

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

