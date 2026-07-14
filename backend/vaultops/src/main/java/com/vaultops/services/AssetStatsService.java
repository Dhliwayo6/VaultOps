package com.vaultops.services;

import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.dtos.DashboardAlertDTO;
import com.vaultops.dtos.CategoryConditionStatDTO;
import com.vaultops.dtos.MonthlyTrendDTO;
import com.vaultops.dtos.FinancialStatsDTO;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.repository.AssetRepository;
import com.vaultops.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetStatsService {

    private final AssetRepository assetRepository;
    private final MaintenanceRepository maintenanceRepository;

    @Cacheable(value = "assetStatsCache", key = "#root.methodName + '_' + (#locationId != null ? #locationId : 'all')")
    public DashboardStatsDTO getDashboardStats(Long locationId) {
        long totalAssets = assetRepository.count(locationId);
        long inUseCount = assetRepository.countAssetsByUsageStatusAndLocationId(Usage.IN_USE, locationId);
        long inStorageCount = assetRepository.countAssetsByUsageStatusAndLocationId(Usage.STORAGE, locationId);
        long inServiceCount = assetRepository.countAssetsByUsageStatusAndLocationId(Usage.SERVICE, locationId);

        long excellentCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.EXCELLENT, locationId);
        long goodCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.GOOD, locationId);
        long fairCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.FAIR, locationId);
        long badCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.BAD, locationId);
        long damagedCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.DAMAGED, locationId);

        Double avgDays = assetRepository.getAverageDaysInStatusAndLocationId(Usage.SERVICE, locationId);
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

    public DashboardStatsDTO getDashboardStats() {
        return getDashboardStats(null);
    }

    @Cacheable(value = "assetStatsCache", key = "#root.methodName + '_' + (#locationId != null ? #locationId : 'all')")
    public List<CategoryConditionStatDTO> getCategoryConditionStats(Long locationId) {
        return assetRepository.getAssetCountsGroupedByCategoryAndCondition(locationId);
    }

    public List<CategoryConditionStatDTO> getCategoryConditionStats() {
        return getCategoryConditionStats(null);
    }

    @Cacheable(value = "assetStatsCache", key = "#root.methodName + '_' + (#locationId != null ? #locationId : 'all')")
    public List<MonthlyTrendDTO> getMonthlyTrends(Long locationId) {
        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgoDate = now.minusMonths(5).withDayOfMonth(1);
        LocalDateTime startDateTime = sixMonthsAgoDate.atStartOfDay();

        List<Object[]> assetResults = assetRepository.getAssetCreationTrends(startDateTime, locationId);
        List<Object[]> maintenanceResults = maintenanceRepository.getMaintenanceTrends(sixMonthsAgoDate, locationId);

        Map<String, Long> assetsMap = new HashMap<>();
        for (Object[] row : assetResults) {
            if (row != null && row.length == 3 && row[0] != null && row[1] != null && row[2] != null) {
                int year = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                String monthStr = String.format("%04d-%02d", year, month);
                assetsMap.put(monthStr, ((Number) row[2]).longValue());
            }
        }

        Map<String, Long> maintenanceMap = new HashMap<>();
        for (Object[] row : maintenanceResults) {
            if (row != null && row.length == 3 && row[0] != null && row[1] != null && row[2] != null) {
                int year = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                String monthStr = String.format("%04d-%02d", year, month);
                maintenanceMap.put(monthStr, ((Number) row[2]).longValue());
            }
        }

        List<MonthlyTrendDTO> trends = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now).minusMonths(i);
            String monthStr = ym.toString();
            long assetsCount = assetsMap.getOrDefault(monthStr, 0L);
            long maintCount = maintenanceMap.getOrDefault(monthStr, 0L);
            trends.add(new MonthlyTrendDTO(monthStr, assetsCount, maintCount));
        }
        return trends;
    }

    public List<MonthlyTrendDTO> getMonthlyTrends() {
        return getMonthlyTrends(null);
    }

    @Cacheable(value = "assetStatsCache", key = "#root.methodName + '_' + (#locationId != null ? #locationId : 'all')")
    public FinancialStatsDTO getFinancialStats(Long locationId) {
        BigDecimal totalValuation = assetRepository.getTotalAssetValuation(locationId);
        BigDecimal averageAssetValue = assetRepository.getAverageAssetValue(locationId);
        BigDecimal totalMaintCost = maintenanceRepository.getTotalMaintenanceExpenditure(locationId);
        return new FinancialStatsDTO(totalValuation, averageAssetValue, totalMaintCost);
    }

    public FinancialStatsDTO getFinancialStats() {
        return getFinancialStats(null);
    }

    @Cacheable(value = "assetStatsCache", key = "#root.methodName + '_' + (#locationId != null ? #locationId : 'all')")
    public List<DashboardAlertDTO> getDashboardAlerts(Long locationId) {
        List<DashboardAlertDTO> alerts = new ArrayList<>();

        // 1. Damaged condition alert
        long damagedCount = assetRepository.countAssetsByConditionStatusAndLocationId(ConditionStatus.DAMAGED, locationId);
        if (damagedCount > 0) {
            alerts.add(new DashboardAlertDTO(
                "damaged",
                "danger",
                "Damaged Assets Detected",
                damagedCount + " asset(s) are currently marked as DAMAGED and require immediate attention.",
                damagedCount,
                "/assets?condition=DAMAGED" + (locationId != null ? "&locationId=" + locationId : ""),
                "Resolve"
            ));
        }

        // 2. Overdue repairs alert (in SERVICE status for > 14 days)
        long overdueRepairCount = assetRepository.countOverdueRepairs(14, locationId);
        if (overdueRepairCount > 0) {
            alerts.add(new DashboardAlertDTO(
                "overdue-repair",
                "warning",
                "Overdue Repairs",
                overdueRepairCount + " asset(s) have been in service status for longer than 14 days.",
                overdueRepairCount,
                "/assets?usage=SERVICE" + (locationId != null ? "&locationId=" + locationId : ""),
                "View Repairs"
            ));
        }

        // 3. Expiring warranties alert (< 30 days)
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        long expiringWarrantyCount = assetRepository.countExpiringWarranties(thirtyDaysFromNow, locationId);
        if (expiringWarrantyCount > 0) {
            alerts.add(new DashboardAlertDTO(
                "expiring-warranty",
                "info",
                "Warranties Expiring Soon",
                expiringWarrantyCount + " asset(s) have warranties expiring within the next 30 days.",
                expiringWarrantyCount,
                "/assets?warrantyExpiring=true" + (locationId != null ? "&locationId=" + locationId : ""),
                "Review"
            ));
        }

        return alerts;
    }

    public List<DashboardAlertDTO> getDashboardAlerts() {
        return getDashboardAlerts(null);
    }
}
