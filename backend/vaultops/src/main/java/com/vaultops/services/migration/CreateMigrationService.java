package com.vaultops.services.migration;

import com.vaultops.Command;
import com.vaultops.dtos.MigrationDTO;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CreateMigrationService implements Command<Migration, MigrationDTO> {
    private MigrationRepository migrationRepository;

    public CreateMigrationService(MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
    }

    @Override
    public ResponseEntity<MigrationDTO> execute(Migration migration) {
        Migration migrationSaved = migrationRepository.save(migration);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MigrationDTO(migrationSaved));
    }
}
