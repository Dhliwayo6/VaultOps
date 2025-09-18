package com.vaultops.services.maintenance;

import com.vaultops.Command;
import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.model.UpdateMaintenance;
import com.vaultops.repository.MaintenanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateMaintenanceService implements Command<UpdateMaintenance, MaintenanceDTO> {

    private MaintenanceRepository maintenanceRepository;

    public UpdateMaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public ResponseEntity<MaintenanceDTO> execute(UpdateMaintenance updateMaintenance) {
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findById(updateMaintenance.getId());

        if (maintenanceOptional.isPresent()) {
            Maintenance maintenance = updateMaintenance.getMaintenance();
            maintenance.setId(updateMaintenance.getId());

            maintenanceRepository.save(maintenance);
            return ResponseEntity.ok(new MaintenanceDTO(maintenance));
        }

        throw new IllegalArgumentException("Maintenance history not found!");
    }
}
