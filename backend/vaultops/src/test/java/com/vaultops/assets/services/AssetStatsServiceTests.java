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
        when(assetRepository.count((Long) null)).thenReturn(10L);
        when(assetRepository.countAssetsByUsageStatusAndLocationId(Usage.IN_USE, null)).thenReturn(3L);
        when(assetRepository.countAssetsByUsageStatusAndLocationId(Usage.STORAGE, null)).thenReturn(5L);
        when(assetRepository.countAssetsByUsageStatusAndLocationId(Usage.SERVICE, null)).thenReturn(2L);

        when(assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.EXCELLENT, null)).thenReturn(4L);
        when(assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.GOOD, null)).thenReturn(3L);
        when(assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.FAIR, null)).thenReturn(1L);
        when(assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.BAD, null)).thenReturn(1L);
        when(assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.DAMAGED, null)).thenReturn(1L);

        when(assetRepository.getAverageDaysInStatusAndLocationId(Usage.SERVICE, null)).thenReturn(5.5);

        DashboardStatsDTO stats = assetStatsService.getDashboardStats(null);

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

        verify(assetRepository, times(1)).count((Long) null);
        verify(assetRepository, times(1)).countAssetsByUsageStatusAndLocationId(Usage.IN_USE, null);
        verify(assetRepository, times(1)).countAssetsByUsageStatusAndLocationId(Usage.STORAGE, null);
        verify(assetRepository, times(1)).countAssetsByUsageStatusAndLocationId(Usage.SERVICE, null);
        verify(assetRepository, times(1)).countAssetsByConditionStatusAndLocationId(ConditionStatus.EXCELLENT, null);
        verify(assetRepository, times(1)).countAssetsByConditionStatusAndLocationId(ConditionStatus.GOOD, null);
        verify(assetRepository, times(1)).countAssetsByConditionStatusAndLocationId(ConditionStatus.FAIR, null);
        verify(assetRepository, times(1)).countAssetsByConditionStatusAndLocationId(ConditionStatus.BAD, null);
        verify(assetRepository, times(1)).countAssetsByConditionStatusAndLocationId(ConditionStatus.DAMAGED, null);
        verify(assetRepository, times(1)).getAverageDaysInStatusAndLocationId(Usage.SERVICE, null);
    }

    @Test
    @DisplayName("Should return 0 for average turnaround when repository returns null")
    void getDashboardStats_WithNullTurnaround_ShouldReturnZero() {
        when(assetRepository.count((Long) null)).thenReturn(0L);
        when(assetRepository.countAssetsByUsageStatusAndLocationId(any(Usage.class), any())).thenReturn(0L);
        when(assetRepository.countAssetsByConditionStatusAndLocationId(any(ConditionStatus.class), any())).thenReturn(0L);
        when(assetRepository.getAverageDaysInStatusAndLocationId(Usage.SERVICE, null)).thenReturn(null);

        DashboardStatsDTO stats = assetStatsService.getDashboardStats(null);

        assertThat(stats.averageDaysInRepair()).isEqualTo(0.0);
    }
}
