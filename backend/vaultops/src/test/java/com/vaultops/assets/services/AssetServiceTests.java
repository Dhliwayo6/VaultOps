package com.vaultops.assets.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetSummaryDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.model.Location;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.LocationRepository;
import com.vaultops.services.AssetMapperService;
import com.vaultops.services.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Service Consolidation Tests")
public class AssetServiceTests {

    @Mock private AssetRepository assetRepository;
    @Mock private LocationRepository locationRepository;

    private AssetMapperService assetMapperService;
    private AssetService assetService;

    private Asset asset;
    private Location location;

    @BeforeEach
    void setUp() {
        assetMapperService = new AssetMapperService(locationRepository);
        assetService = new AssetService(assetRepository, assetMapperService, locationRepository);

        location = new Location();
        location.setId(1L);
        location.setName("Unassigned");
        location.setMaxCapacity(100);

        asset = new Asset();
        asset.setId(1L);
        asset.setName("Laptop");
        asset.setType("Electronics");
        asset.setLocation(location);
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);
        asset.setCreatedAt(LocalDateTime.now());

        lenient().when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));
        lenient().when(locationRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(location));
        lenient().when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // --- CREATE TESTS ---

    @Test
    @DisplayName("Should create asset and return AssetDTO")
    void create_WithValidAsset_ShouldSaveAndReturnDto() {
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        AssetDTO response = assetService.create(asset);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Laptop");

        verify(assetRepository, times(1)).save(asset);
    }

    @Test
    @DisplayName("Should preserve asset properties during save")
    void create_ShouldPreserveAssetProperties() {
        Asset assetWithAllFields = new Asset();
        assetWithAllFields.setName("Server");
        assetWithAllFields.setType("Hardware");
        assetWithAllFields.setUsageStatus(Usage.STORAGE);
        assetWithAllFields.setConditionStatus(ConditionStatus.FAIR);
        assetWithAllFields.setAssignment(Assignment.UNASSIGNED);
        assetWithAllFields.setCreatedAt(LocalDateTime.now());

        when(assetRepository.save(any(Asset.class))).thenReturn(assetWithAllFields);

        AssetDTO response = assetService.create(assetWithAllFields);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Server");
        assertThat(response.type()).isEqualTo("Hardware");
        assertThat(response.usageStatus()).isEqualTo(Usage.STORAGE);
        assertThat(response.conditionStatus()).isEqualTo(ConditionStatus.FAIR);
        verify(assetRepository).save(assetWithAllFields);
    }

    // --- GET BY ID TESTS ---

    @Test
    @DisplayName("Should return asset when found")
    void getById_WhenAssetExists_ShouldReturnAsset() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        AssetDTO response = assetService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Laptop");

        verify(assetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw AssetNotFoundException when asset not found")
    void getById_WhenAssetNotFound_ShouldThrowException() {
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.getById(999L))
                .isInstanceOf(AssetNotFoundException.class);

        verify(assetRepository, times(1)).findById(999L);
    }

    // --- GET ALL TESTS (PAGINATED & NON-PAGINATED) ---

    @Test
    @DisplayName("Should return all assets non-paginated when they exist")
    void getAllNonPaginated_WhenAssetsExist_ShouldReturnList() {
        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setName("Monitor");
        asset2.setType("Electronics");

        List<Asset> assets = Arrays.asList(asset, asset2);
        when(assetRepository.findAll()).thenReturn(assets);

        List<AssetDTO> response = assetService.getAllNonPaginated();

        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(AssetDTO::name)
                .containsExactly("Laptop", "Monitor");

        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw custom NoResultsException when no assets exist non-paginated")
    void getAllNonPaginated_WhenNoAssets_ShouldThrowException() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assetService.getAllNonPaginated())
                .isInstanceOf(NoResultsException.class);

        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return page of assets when they exist")
    void getAll_WhenAssetsExist_ShouldReturnPage() {
        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setName("Monitor");
        asset2.setType("Electronics");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Asset> page = new PageImpl<>(Arrays.asList(asset, asset2), pageable, 2);
        when(assetRepository.findAll(pageable)).thenReturn(page);

        Page<AssetDTO> response = assetService.getAll(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent())
                .extracting(AssetDTO::name)
                .containsExactly("Laptop", "Monitor");

        verify(assetRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should throw NoResultsException when no assets exist paginated")
    void getAll_WhenNoAssets_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(assetRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        assertThatThrownBy(() -> assetService.getAll(pageable))
                .isInstanceOf(NoResultsException.class);
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should update asset when it exists")
    void update_WhenAssetExists_ShouldUpdateAndReturn() {
        Asset updatedData = new Asset();
        updatedData.setName("Updated asset name");
        updatedData.setType("Updated asset type");
        updatedData.setUsageStatus(Usage.SERVICE);
        updatedData.setConditionStatus(ConditionStatus.BAD);
        updatedData.setAssignment(Assignment.UNASSIGNED);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssetDTO response = assetService.update(1L, updatedData);

        assertThat(response).isNotNull();

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).save(any(Asset.class));

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository).save(assetCaptor.capture());

        Asset savedAsset = assetCaptor.getValue();
        assertThat(savedAsset.getId()).isEqualTo(1L);
        assertThat(savedAsset.getName()).isEqualTo("Updated asset name");
        assertThat(savedAsset.getUsageStatus()).isEqualTo(Usage.SERVICE);
    }

    @Test
    @DisplayName("Should throw custom exception when asset not found for update")
    void update_WhenAssetNotFound_ShouldThrowException() {
        Asset updatedData = new Asset();
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.update(999L, updatedData))
                .isInstanceOf(AssetNotFoundException.class);

        verify(assetRepository, times(1)).findById(999L);
        verify(assetRepository, never()).save(any());
    }

    // --- DELETE TESTS ---

    @Test
    @DisplayName("Should delete asset when it exists")
    void delete_WhenAssetExists_ShouldDelete() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        doNothing().when(assetRepository).deleteById(1L);

        assetService.delete(1L);

        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw custom exception when asset not found for delete")
    void delete_WhenAssetNotFound_ShouldThrowAssetNotException() {
        when(assetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.delete(999L))
                .isInstanceOf(AssetNotFoundException.class);

        verify(assetRepository, times(1)).findById(999L);
        verify(assetRepository, never()).deleteById(any());
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should search asset by name")
    void search_ShouldReturnResults() {
        when(assetRepository.findByNameOrTypeContaining("Lap")).thenReturn(List.of(asset));

        List<AssetDTO> results = assetService.search("Lap");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Should throw NoResultsException when search returns empty")
    void search_WhenNoResults_ShouldThrowException() {
        when(assetRepository.findByNameOrTypeContaining("Unknown")).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assetService.search("Unknown"))
                .isInstanceOf(NoResultsException.class);
    }

    // --- TOP FOUR TESTS ---

    @Test
    @DisplayName("Should return top four in use assets")
    void getTopFourInUse_ShouldReturnList() {
        when(assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.IN_USE)).thenReturn(List.of(asset));

        List<AssetSummaryDTO> results = assetService.getTopFourInUse();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Should throw NoResultsException when top four in use is empty")
    void getTopFourInUse_WhenEmpty_ShouldThrowException() {
        when(assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.IN_USE)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assetService.getTopFourInUse())
                .isInstanceOf(NoResultsException.class);
    }

    @Test
    @DisplayName("Should return top four in repairs assets")
    void getTopFourInRepairs_ShouldReturnList() {
        asset.setUsageStatus(Usage.SERVICE);
        when(assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.SERVICE)).thenReturn(List.of(asset));

        List<AssetSummaryDTO> results = assetService.getTopFourInRepairs();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Laptop");
    }
}
