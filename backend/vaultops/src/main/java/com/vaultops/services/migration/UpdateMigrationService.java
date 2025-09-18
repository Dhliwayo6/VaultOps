package com.vaultops.services.migration;

import com.vaultops.Command;
import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.model.UpdateMigration;
import com.vaultops.repository.MigrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateMigrationService implements Command<UpdateMigration, MigrationDTO> {

    private MigrationRepository migrationRepository;

    public UpdateMigrationService(MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
    }

    public ResponseEntity<MigrationDTO> execute(UpdateMigration updateMigration) {
        Optional<Migration> migrationOptional = migrationRepository.findById(updateMigration.getId());

        if (migrationOptional.isPresent()) {
            Migration migration = updateMigration.getMigration();
            migration.setId(updateMigration.getId());

            migrationRepository.save(migration);
            return ResponseEntity.ok(new MigrationDTO(migration));
        }

        throw new IllegalArgumentException("Migration history not found!");
    }
}
