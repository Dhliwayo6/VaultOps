package com.vaultops.controllers;

import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.services.MaintenanceService;
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
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @PostMapping("/maintenance")
    public ResponseEntity<MaintenanceDTO> createMaintenance(@RequestBody Maintenance maintenance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.create(maintenance));
    }

    @GetMapping("/maintenances")
    public ResponseEntity<List<MaintenanceDTO>> getMaintenances() {
        return ResponseEntity.ok(maintenanceService.getAll());
    }

    @GetMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getById(id));
    }

    @PutMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.update(id, maintenance));
    }

    @DeleteMapping("/maintenance/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
