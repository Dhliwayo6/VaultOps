package com.vaultops.services;

import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetStatsService {

    private final AssetRepository assetRepository;

    @Cacheable(value = "assetStatsCache")
    public DashboardStatsDTO getDashboardStats() {
        long totalAssets = assetRepository.count();
        long inUseCount = assetRepository.countAssetsByUsageStatus(Usage.IN_USE);
        long inStorageCount = assetRepository.countAssetsByUsageStatus(Usage.STORAGE);
        long inServiceCount = assetRepository.countAssetsByUsageStatus(Usage.SERVICE);

        long excellentCount = assetRepository.countAssetsByConditionStatus(ConditionStatus.EXCELLENT);
        long goodCount = assetRepository.countAssetsByConditionStatus(ConditionStatus.GOOD);
        long fairCount = assetRepository.countAssetsByConditionStatus(ConditionStatus.FAIR);
        long badCount = assetRepository.countAssetsByConditionStatus(ConditionStatus.BAD);
        long damagedCount = assetRepository.countAssetsByConditionStatus(ConditionStatus.DAMAGED);

        Double avgDays = assetRepository.getAverageDaysInStatus(Usage.SERVICE);
        double averageDaysInRepair = avgDays != null ? avgDays : 0.0;

        return new DashboardStatsDTO(
            totalAssets,
            inUseCount,
            inStorageCount,
            inServiceCount,
            excellentCount,
            goodCount,
            fairCount,
            badCount,
            damagedCount,
            averageDaysInRepair
        );
    }
}
