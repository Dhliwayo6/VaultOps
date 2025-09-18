package com.vaultops.services.maintenance;

import com.vaultops.Query;
import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMaintenancesService implements Query<Void, List<MaintenanceDTO>> {

    private MaintenanceRepository maintenanceRepository;

    public GetMaintenancesService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public ResponseEntity<List<MaintenanceDTO>> execute(Void input) {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        List<MaintenanceDTO> maintenanceDTOS = maintenances.stream().map(MaintenanceDTO::new).toList();

        if (maintenanceDTOS.isEmpty()) {
            throw new NoResultsException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(maintenanceDTOS);
    }
}
