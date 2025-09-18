package com.vaultops.services.migration;

import com.vaultops.Query;
import com.vaultops.dtos.MigrationDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetMigrationsService implements Query<Void, List<MigrationDTO>> {
    private MigrationRepository migrationRepository;

    public GetMigrationsService(MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
    }

    @Override
    public ResponseEntity<List<MigrationDTO>> execute(Void input) {
        List<Migration> migrations = migrationRepository.findAll();
        List<MigrationDTO> migrationDTOS = migrations.stream().map(MigrationDTO::new).toList();

        if (migrationDTOS.isEmpty()) {
            throw new NoResultsException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(migrationDTOS);
    }
}
