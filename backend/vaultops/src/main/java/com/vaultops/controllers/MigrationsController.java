package com.vaultops.controllers;

import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.services.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MigrationsController {

    private final MigrationService migrationService;

    @PostMapping("/migration")
    public ResponseEntity<MigrationDTO> createMigration(@RequestBody Migration migration) {
        return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.create(migration));
    }

    @GetMapping("/migration")
    public ResponseEntity<List<MigrationDTO>> getMigrations() {
        return ResponseEntity.ok(migrationService.getAll());
    }

    @GetMapping("/migration/{id}")
    public ResponseEntity<MigrationDTO> getMigrationById(@PathVariable Long id) {
        return ResponseEntity.ok(migrationService.getById(id));
    }

    @PutMapping("/migration/{id}")
    public ResponseEntity<MigrationDTO> updateMigration(@PathVariable Long id, @RequestBody Migration migration) {
        return ResponseEntity.ok(migrationService.update(id, migration));
    }

    @DeleteMapping("/migration/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMigration(@PathVariable Long id) {
        migrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
