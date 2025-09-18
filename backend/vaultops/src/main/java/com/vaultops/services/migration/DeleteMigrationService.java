package com.vaultops.services.migration;

import com.vaultops.Command;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DeleteMigrationService implements Command<Long, Void> {

    private MigrationRepository migrationRepository;

    public DeleteMigrationService(MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
    }

    @Override
    public ResponseEntity<Void> execute(Long id) {
        Optional<Migration> migrationOptional = migrationRepository.findById(id);

        if (migrationOptional.isPresent()) {
            migrationRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        throw new IllegalArgumentException("Migration history not found!");
    }
}
