package com.vaultops.services.maintenance;

import com.vaultops.Query;
import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetMaintenanceService implements Query<Long, MaintenanceDTO> {
    private MaintenanceRepository maintenanceRepository;

    public GetMaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public ResponseEntity<MaintenanceDTO> execute(Long id) {
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findById(id);

        if (maintenanceOptional.isPresent()) {
            return ResponseEntity.ok(new MaintenanceDTO(maintenanceOptional.get()));
        }

        throw new IllegalArgumentException("Maintenance history with id:" + id + " not found");
    }
}
