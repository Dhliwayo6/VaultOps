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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapperService assetMapperService;

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    public AssetDTO create(Asset asset) {
        Asset savedAsset = assetRepository.save(asset);
        return assetMapperService.mapToDTO(savedAsset);
    }

    public AssetDTO getById(Long id) {
        return assetRepository.findById(id)
                .map(assetMapperService::mapToDTO)
                .orElseThrow(AssetNotFoundException::new);
    }

    public Page<AssetDTO> getAll(Pageable pageable) {
        Page<Asset> assets = assetRepository.findAll(pageable);
        if (assets.isEmpty()) {
            throw new NoResultsException();
        }
        return assets.map(assetMapperService::mapToDTO);
    }

    public List<AssetDTO> getAllNonPaginated() {
        List<Asset> assets = assetRepository.findAll();
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
