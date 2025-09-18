package com.vaultops.services.maintenance;

import com.vaultops.Command;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteMaintenanceService implements Command<Long, Void> {

    private MaintenanceRepository maintenanceRepository;

    public DeleteMaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public ResponseEntity<Void> execute(Long id) {
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findById(id);

        if (maintenanceOptional.isPresent()) {
            maintenanceRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        throw new IllegalArgumentException("Maintenance history not found!");
    }
}
