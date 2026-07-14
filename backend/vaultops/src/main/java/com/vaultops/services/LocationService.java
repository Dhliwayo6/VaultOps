package com.vaultops.services;

import com.vaultops.model.Location;
import com.vaultops.repository.LocationRepository;
import com.vaultops.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final AssetRepository assetRepository;

    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    public Location getById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with ID: " + id));
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public Location create(Location location) {
        if (locationRepository.findByNameIgnoreCase(location.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("Location with name '" + location.getName() + "' already exists");
        }
        return locationRepository.save(location);
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public Location update(Long id, Location details) {
        Location existing = getById(id);

        // Check name uniqueness if changed
        if (!existing.getName().equalsIgnoreCase(details.getName().trim())) {
            if (locationRepository.findByNameIgnoreCase(details.getName().trim()).isPresent()) {
                throw new IllegalArgumentException("Location with name '" + details.getName() + "' already exists");
            }
        }

        // Check if new capacity is lower than current asset count
        long currentAssetCount = assetRepository.countByLocationId(id);
        if (details.getMaxCapacity() < currentAssetCount) {
            throw new IllegalArgumentException("Cannot set capacity to " + details.getMaxCapacity() 
                    + " as " + currentAssetCount + " assets are currently assigned to this location");
        }

        existing.setName(details.getName().trim());
        existing.setDescription(details.getDescription());
        existing.setAddress(details.getAddress());
        existing.setMaxCapacity(details.getMaxCapacity());

        return locationRepository.save(existing);
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public void delete(Long id) {
        if (id == 1L) {
            throw new IllegalArgumentException("Cannot delete default/unassigned location");
        }

        Location existing = getById(id);

        long currentAssetCount = assetRepository.countByLocationId(id);
        if (currentAssetCount > 0) {
            throw new IllegalArgumentException("Cannot delete location as assets are still assigned to it");
        }

        locationRepository.deleteById(id);
    }
}
