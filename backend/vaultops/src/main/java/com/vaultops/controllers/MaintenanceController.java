package com.vaultops.controllers;

import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.model.UpdateMaintenance;
import com.vaultops.services.maintenance.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MaintenanceController {
    private final CreateMaintenanceService createMaintenanceService;
    private final GetMaintenanceService getMaintenanceService;
    private final GetMaintenancesService getMaintenancesService;
    private final UpdateMaintenanceService updateMaintenanceService;
    private final DeleteMaintenanceService deleteMaintenanceService;

    @PostMapping("/maintenance")
    public ResponseEntity<MaintenanceDTO> createMaintenance(@RequestBody Maintenance maintenance) {
        return createMaintenanceService.execute(maintenance);
    }

    @GetMapping("/maintenances")
    public ResponseEntity<List<MaintenanceDTO>> getMaintenances() {
        return getMaintenancesService.execute(null);
    }

    @GetMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenance(@PathVariable Long id) {
        return getMaintenanceService.execute(id);
    }

    @PutMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return updateMaintenanceService.execute(new UpdateMaintenance(id, maintenance));
    }

    @DeleteMapping("maintenance/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        return deleteMaintenanceService.execute(id);
    }

}
