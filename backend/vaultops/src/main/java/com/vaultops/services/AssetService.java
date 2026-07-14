package com.vaultops.services;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetSummaryDTO;
import com.vaultops.enums.Usage;
import com.vaultops.exceptions.AssetNotFoundException;
import com.vaultops.exceptions.NoResultsException;
import com.vaultops.model.Asset;
import com.vaultops.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import com.vaultops.model.Location;
import com.vaultops.repository.LocationRepository;
import com.vaultops.exceptions.LocationCapacityExceededException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapperService assetMapperService;
    private final LocationRepository locationRepository;

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public AssetDTO create(Asset asset) {
        if (asset.getLocation() != null && asset.getLocation().getId() != null) {
            Location location = locationRepository.findById(asset.getLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Location not found"));
            long currentCount = assetRepository.countByLocationId(location.getId());
            if (currentCount >= location.getMaxCapacity()) {
                throw new LocationCapacityExceededException("Location " + location.getName() + " is already at maximum capacity (" + location.getMaxCapacity() + ")");
            }
            asset.setLocation(location);
        } else {
            Location defaultLoc = locationRepository.findById(1L)
                    .orElseThrow(() -> new IllegalStateException("Default location not found"));
            asset.setLocation(defaultLoc);
        }
        Asset savedAsset = assetRepository.save(asset);
        return assetMapperService.mapToDTO(savedAsset);
    }

    public AssetDTO getById(Long id) {
        return assetRepository.findById(id)
                .map(assetMapperService::mapToDTO)
                .orElseThrow(AssetNotFoundException::new);
    }

    public Page<AssetDTO> getAll(Pageable pageable) {
        return getAll(pageable, null);
    }

    public Page<AssetDTO> getAll(Pageable pageable, Long locationId) {
        Page<Asset> assets;
        if (locationId != null) {
            assets = assetRepository.findByLocationId(locationId, pageable);
        } else {
            assets = assetRepository.findAll(pageable);
        }
        if (assets.isEmpty()) {
            throw new NoResultsException();
        }
        return assets.map(assetMapperService::mapToDTO);
    }

    public List<AssetDTO> getAllNonPaginated() {
        return getAllNonPaginated(null);
    }

    public List<AssetDTO> getAllNonPaginated(Long locationId) {
        List<Asset> assets;
        if (locationId != null) {
            assets = assetRepository.findByLocationId(locationId);
        } else {
            assets = assetRepository.findAll();
        }
        if (assets.isEmpty()) {
            throw new NoResultsException();
        }
        return assets.stream()
                .map(assetMapperService::mapToDTO)
                .toList();
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public AssetDTO update(Long id, Asset assetDetails) {
        Optional<Asset> assetOptional = assetRepository.findById(id);

        if (assetOptional.isPresent()) {
            Asset existingAsset = assetOptional.get();
            if (assetDetails.getLocation() != null && assetDetails.getLocation().getId() != null) {
                Location newLocation = locationRepository.findById(assetDetails.getLocation().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Location not found"));
                
                if (existingAsset.getLocation() == null || !existingAsset.getLocation().getId().equals(newLocation.getId())) {
                    long currentCount = assetRepository.countByLocationId(newLocation.getId());
                    if (currentCount >= newLocation.getMaxCapacity()) {
                        throw new LocationCapacityExceededException("Location " + newLocation.getName() + " is already at maximum capacity (" + newLocation.getMaxCapacity() + ")");
                    }
                }
                assetDetails.setLocation(newLocation);
            } else {
                assetDetails.setLocation(existingAsset.getLocation());
            }

            assetDetails.setId(id);
            Asset savedAsset = assetRepository.save(assetDetails);
            return assetMapperService.mapToDTO(savedAsset);
        }

        throw new AssetNotFoundException();
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public void delete(Long id) {
        Optional<Asset> assetOptional = assetRepository.findById(id);
        if (assetOptional.isPresent()) {
            assetRepository.deleteById(id);
        } else {
            throw new AssetNotFoundException();
        }
    }

    public List<AssetDTO> search(String name) {
        List<Asset> assets = assetRepository.findByNameOrTypeContaining(name);
        if (assets.isEmpty()) {
            throw new NoResultsException();
        }
        return assets.stream()
                .map(assetMapperService::mapToDTO)
                .toList();
    }

    public List<AssetSummaryDTO> getTopFourInUse() {
        List<Asset> assets = assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.IN_USE);
        if (assets.isEmpty()) {
            throw new NoResultsException();
        }
        return assets.stream()
                .map(assetMapperService::mapToSummaryDTO)
                .toList();
    }

    public List<AssetSummaryDTO> getTopFourInRepairs() {
        List<Asset> assets = assetRepository.findTop4ByUsageStatusOrderByCreatedAtDesc(Usage.SERVICE);
        return assets.stream()
                .map(assetMapperService::mapToSummaryDTO)
                .toList();
    }
}
