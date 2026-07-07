package com.vaultops.assets.services;

import com.vaultops.dtos.MaintenanceDTO;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.model.Maintenance;
import com.vaultops.repository.MaintenanceRepository;
import com.vaultops.services.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Maintenance Service Tests")
public class MaintenanceServiceTests {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private MaintenanceService maintenanceService;

    private Maintenance maintenance;

    @BeforeEach
    void setUp() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Server");

        maintenance = new Maintenance();
        maintenance.setId(1L);
        maintenance.setAsset(asset);
        maintenance.setDescription("Server fan replacement");
        maintenance.setPerformedBy("John Doe");
        maintenance.setCost(BigDecimal.valueOf(150.0));
        maintenance.setDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should create maintenance record successfully")
    void create_ShouldSaveAndReturnDto() {
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceDTO result = maintenanceService.create(maintenance);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Server fan replacement");
        verify(maintenanceRepository, times(1)).save(maintenance);
    }

    @Test
    @DisplayName("Should get maintenance by ID when found")
    void getById_WhenFound_ShouldReturnDto() {
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenance));

        MaintenanceDTO result = maintenanceService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(maintenanceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when getById fails")
    void getById_WhenNotFound_ShouldThrowException() {
        when(maintenanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> maintenanceService.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maintenance history with id:99 not found");
    }

    @Test
    @DisplayName("Should return all maintenance records when they exist")
    void getAll_WhenExists_ShouldReturnList() {
        when(maintenanceRepository.findAll()).thenReturn(List.of(maintenance));

        List<MaintenanceDTO> results = maintenanceService.getAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Server fan replacement");
    }

    @Test
    @DisplayName("Should throw NoResultsException when no records exist")
    void getAll_WhenEmpty_ShouldThrowException() {
        when(maintenanceRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> maintenanceService.getAll())
                .isInstanceOf(NoResultsException.class);
    }

    @Test
    @DisplayName("Should update maintenance record when it exists")
    void update_WhenExists_ShouldUpdateAndReturn() {
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenance));
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceDTO result = maintenanceService.update(1L, maintenance);

        assertThat(result).isNotNull();
        verify(maintenanceRepository, times(1)).findById(1L);
        verify(maintenanceRepository, times(1)).save(maintenance);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update fails due to non-existence")
    void update_WhenNotFound_ShouldThrowException() {
        when(maintenanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> maintenanceService.update(99L, maintenance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maintenance history not found!");
    }

    @Test
    @DisplayName("Should delete maintenance record when it exists")
    void delete_WhenExists_ShouldDelete() {
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenance));
        doNothing().when(maintenanceRepository).deleteById(1L);

        maintenanceService.delete(1L);

        verify(maintenanceRepository, times(1)).findById(1L);
        verify(maintenanceRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting non-existent record")
    void delete_WhenNotFound_ShouldThrowException() {
        when(maintenanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> maintenanceService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maintenance history not found!");
    }
}
