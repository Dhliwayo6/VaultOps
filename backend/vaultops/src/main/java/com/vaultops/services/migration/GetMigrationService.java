package com.vaultops.services.migration;

import com.vaultops.Query;
import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetMigrationService implements Query<Long, MigrationDTO> {

    private MigrationRepository migrationRepository;

    public GetMigrationService(MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
    }
    
    @Override
    public ResponseEntity<MigrationDTO> execute(Long id) {
        Optional<Migration> migrationOptional = migrationRepository.findById(id);

        if (migrationOptional.isPresent()) {
            return ResponseEntity.ok(new MigrationDTO(migrationOptional.get()));
        }

        throw new IllegalArgumentException("Migration record not found!");
    }
}
