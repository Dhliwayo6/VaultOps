package com.vaultops.services;

import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public MaintenanceDTO create(Maintenance maintenance) {
        Maintenance saved = maintenanceRepository.save(maintenance);
        return new MaintenanceDTO(saved);
    }

    public MaintenanceDTO getById(Long id) {
        return maintenanceRepository.findById(id)
                .map(MaintenanceDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance history with id:" + id + " not found"));
    }

    public List<MaintenanceDTO> getAll() {
        List<Maintenance> list = maintenanceRepository.findAll();
        if (list.isEmpty()) {
            throw new NoResultsException();
        }
        return list.stream().map(MaintenanceDTO::new).toList();
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public MaintenanceDTO update(Long id, Maintenance maintenance) {
        Optional<Maintenance> optional = maintenanceRepository.findById(id);
        if (optional.isPresent()) {
            maintenance.setId(id);
            Maintenance saved = maintenanceRepository.save(maintenance);
            return new MaintenanceDTO(saved);
        }
        throw new IllegalArgumentException("Maintenance history not found!");
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public void delete(Long id) {
        Optional<Maintenance> optional = maintenanceRepository.findById(id);
        if (optional.isPresent()) {
            maintenanceRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Maintenance history not found!");
        }
    }
}
