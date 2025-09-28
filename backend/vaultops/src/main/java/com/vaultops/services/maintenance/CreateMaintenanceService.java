package com.vaultops.services.maintenance;

import com.vaultops.Command;
import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateMaintenanceService implements Command<Maintenance, MaintenanceDTO> {

    private MaintenanceRepository maintenanceRepository;

    public CreateMaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public ResponseEntity<MaintenanceDTO> execute(Maintenance maintenance) {
        Maintenance maintenanceSaved = maintenanceRepository.save(maintenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MaintenanceDTO(maintenanceSaved));
    }
}
