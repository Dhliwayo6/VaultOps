package com.vaultops.assets.services;

import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import com.vaultops.services.AssetStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Stats Service Tests")
public class AssetStatsServiceTests {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetStatsService assetStatsService;

    @Test
    @DisplayName("Should generate dashboard statistics correctly from repository counts")
    void getDashboardStats_ShouldCalculateStatsCorrectly() {
        when(assetRepository.count()).thenReturn(10L);
        when(assetRepository.countAssetsByUsageStatus(Usage.IN_USE)).thenReturn(3L);
        when(assetRepository.countAssetsByUsageStatus(Usage.STORAGE)).thenReturn(5L);
        when(assetRepository.countAssetsByUsageStatus(Usage.SERVICE)).thenReturn(2L);

        when(assetRepository.countAssetsByConditionStatus(ConditionStatus.EXCELLENT)).thenReturn(4L);
        when(assetRepository.countAssetsByConditionStatus(ConditionStatus.GOOD)).thenReturn(3L);
        when(assetRepository.countAssetsByConditionStatus(ConditionStatus.FAIR)).thenReturn(1L);
        when(assetRepository.countAssetsByConditionStatus(ConditionStatus.BAD)).thenReturn(1L);
        when(assetRepository.countAssetsByConditionStatus(ConditionStatus.DAMAGED)).thenReturn(1L);

        when(assetRepository.getAverageDaysInStatus(Usage.SERVICE)).thenReturn(5.5);

        DashboardStatsDTO stats = assetStatsService.getDashboardStats();

        assertThat(stats).isNotNull();
        assertThat(stats.totalAssets()).isEqualTo(10L);
        assertThat(stats.inUseCount()).isEqualTo(3L);
        assertThat(stats.inStorageCount()).isEqualTo(5L);
        assertThat(stats.inServiceCount()).isEqualTo(2L);
        assertThat(stats.excellentCount()).isEqualTo(4L);
        assertThat(stats.goodCount()).isEqualTo(3L);
        assertThat(stats.fairCount()).isEqualTo(1L);
        assertThat(stats.badCount()).isEqualTo(1L);
        assertThat(stats.damagedCount()).isEqualTo(1L);
        assertThat(stats.averageDaysInRepair()).isEqualTo(5.5);

        verify(assetRepository, times(1)).count();
        verify(assetRepository, times(1)).countAssetsByUsageStatus(Usage.IN_USE);
        verify(assetRepository, times(1)).countAssetsByUsageStatus(Usage.STORAGE);
        verify(assetRepository, times(1)).countAssetsByUsageStatus(Usage.SERVICE);
        verify(assetRepository, times(1)).countAssetsByConditionStatus(ConditionStatus.EXCELLENT);
        verify(assetRepository, times(1)).countAssetsByConditionStatus(ConditionStatus.GOOD);
        verify(assetRepository, times(1)).countAssetsByConditionStatus(ConditionStatus.FAIR);
        verify(assetRepository, times(1)).countAssetsByConditionStatus(ConditionStatus.BAD);
        verify(assetRepository, times(1)).countAssetsByConditionStatus(ConditionStatus.DAMAGED);
        verify(assetRepository, times(1)).getAverageDaysInStatus(Usage.SERVICE);
    }

    @Test
    @DisplayName("Should return 0 for average turnaround when repository returns null")
    void getDashboardStats_WithNullTurnaround_ShouldReturnZero() {
        when(assetRepository.count()).thenReturn(0L);
        when(assetRepository.countAssetsByUsageStatus(any(Usage.class))).thenReturn(0L);
        when(assetRepository.countAssetsByConditionStatus(any(ConditionStatus.class))).thenReturn(0L);
        when(assetRepository.getAverageDaysInStatus(Usage.SERVICE)).thenReturn(null);

        DashboardStatsDTO stats = assetStatsService.getDashboardStats();

        assertThat(stats.averageDaysInRepair()).isEqualTo(0.0);
    }
}
