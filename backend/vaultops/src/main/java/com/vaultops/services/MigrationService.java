package com.vaultops.services;

import com.vaultops.dtos.MigrationDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final MigrationRepository migrationRepository;

    public MigrationDTO create(Migration migration) {
        Migration saved = migrationRepository.save(migration);
        return new MigrationDTO(saved);
    }

    public MigrationDTO getById(Long id) {
        return migrationRepository.findById(id)
                .map(MigrationDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Migration record not found!"));
    }

    public List<MigrationDTO> getAll() {
        List<Migration> list = migrationRepository.findAll();
        if (list.isEmpty()) {
            throw new NoResultsException();
        }
        return list.stream().map(MigrationDTO::new).toList();
    }

    public MigrationDTO update(Long id, Migration migration) {
        Optional<Migration> optional = migrationRepository.findById(id);
        if (optional.isPresent()) {
            migration.setId(id);
            Migration saved = migrationRepository.save(migration);
            return new MigrationDTO(saved);
        }
        throw new IllegalArgumentException("Migration record not found!");
    }

    public void delete(Long id) {
        Optional<Migration> optional = migrationRepository.findById(id);
        if (optional.isPresent()) {
            migrationRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Migration record not found!");
        }
    }
}
