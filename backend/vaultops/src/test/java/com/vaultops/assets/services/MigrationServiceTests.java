package com.vaultops.assets.services;

import com.vaultops.dtos.MigrationDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.model.Migration;
import com.vaultops.repository.MigrationRepository;
import com.vaultops.services.MigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Migration Service Tests")
public class MigrationServiceTests {

    @Mock
    private MigrationRepository migrationRepository;

    @InjectMocks
    private MigrationService migrationService;

    private Migration migration;

    @BeforeEach
    void setUp() {
        Asset asset = new Asset();
        asset.setId(2L);
        asset.setName("Laptop");

        migration = new Migration();
        migration.setId(1L);
        migration.setAsset(asset);
        migration.setFromLocation("Cape Town");
        migration.setToLocation("Johannesburg");
        migration.setMovedBy("Alice Smith");
        migration.setDescription("Datacenter migration step 1");
    }

    @Test
    @DisplayName("Should create migration record successfully")
    void create_ShouldSaveAndReturnDto() {
        when(migrationRepository.save(any(Migration.class))).thenReturn(migration);

        MigrationDTO result = migrationService.create(migration);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Datacenter migration step 1");
        verify(migrationRepository, times(1)).save(migration);
    }

    @Test
    @DisplayName("Should get migration by ID when found")
    void getById_WhenFound_ShouldReturnDto() {
        when(migrationRepository.findById(1L)).thenReturn(Optional.of(migration));

        MigrationDTO result = migrationService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(migrationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when getById fails")
    void getById_WhenNotFound_ShouldThrowException() {
        when(migrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> migrationService.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Migration record not found!");
    }

    @Test
    @DisplayName("Should return all migration records when they exist")
    void getAll_WhenExists_ShouldReturnList() {
        when(migrationRepository.findAll()).thenReturn(List.of(migration));

        List<MigrationDTO> results = migrationService.getAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Datacenter migration step 1");
    }

    @Test
    @DisplayName("Should throw NoResultsException when no records exist")
    void getAll_WhenEmpty_ShouldThrowException() {
        when(migrationRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> migrationService.getAll())
                .isInstanceOf(NoResultsException.class);
    }

    @Test
    @DisplayName("Should update migration record when it exists")
    void update_WhenExists_ShouldUpdateAndReturn() {
        when(migrationRepository.findById(1L)).thenReturn(Optional.of(migration));
        when(migrationRepository.save(any(Migration.class))).thenReturn(migration);

        MigrationDTO result = migrationService.update(1L, migration);

        assertThat(result).isNotNull();
        verify(migrationRepository, times(1)).findById(1L);
        verify(migrationRepository, times(1)).save(migration);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update fails due to non-existence")
    void update_WhenNotFound_ShouldThrowException() {
        when(migrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> migrationService.update(99L, migration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Migration record not found!");
    }

    @Test
    @DisplayName("Should delete migration record when it exists")
    void delete_WhenExists_ShouldDelete() {
        when(migrationRepository.findById(1L)).thenReturn(Optional.of(migration));
        doNothing().when(migrationRepository).deleteById(1L);

        migrationService.delete(1L);

        verify(migrationRepository, times(1)).findById(1L);
        verify(migrationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting non-existent record")
    void delete_WhenNotFound_ShouldThrowException() {
        when(migrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> migrationService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Migration record not found!");
    }
}
