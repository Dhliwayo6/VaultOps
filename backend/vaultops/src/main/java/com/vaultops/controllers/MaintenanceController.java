package com.vaultops.controllers;

import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.model.UpdateMaintenance;
import com.vaultops.services.maintenance.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MaintenanceController {
    private CreateMaintenanceService createMaintenanceService;
    private GetMaintenanceService getMaintenanceService;
    private GetMaintenancesService getMaintenancesService;
    private UpdateMaintenanceService updateMaintenanceService;
    private DeleteMaintenanceService deleteMaintenanceService;

    public MaintenanceController(CreateMaintenanceService createMaintenanceService,
                                 GetMaintenanceService getMaintenanceService,
                                 GetMaintenancesService getMaintenancesService,
                                 UpdateMaintenanceService updateMaintenanceService,
                                 DeleteMaintenanceService deleteMaintenanceService) {
        this.createMaintenanceService = createMaintenanceService;
        this.getMaintenanceService = getMaintenanceService;
        this.getMaintenancesService = getMaintenancesService;
        this.updateMaintenanceService = updateMaintenanceService;
        this.deleteMaintenanceService = deleteMaintenanceService;
    }

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
