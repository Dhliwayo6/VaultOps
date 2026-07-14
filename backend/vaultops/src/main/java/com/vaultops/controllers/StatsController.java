package com.vaultops.controllers;

import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.dtos.DashboardAlertDTO;
import com.vaultops.dtos.CategoryConditionStatDTO;
import com.vaultops.dtos.MonthlyTrendDTO;
import com.vaultops.dtos.FinancialStatsDTO;
import com.vaultops.services.AssetStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class StatsController {

    private final AssetStatsService assetStatsService;

    @GetMapping("/stats/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(@RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(assetStatsService.getDashboardStats(locationId));
    }

    @GetMapping("/stats/trends")
    public ResponseEntity<List<MonthlyTrendDTO>> getMonthlyTrends(@RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(assetStatsService.getMonthlyTrends(locationId));
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<List<CategoryConditionStatDTO>> getCategoryConditionStats(@RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(assetStatsService.getCategoryConditionStats(locationId));
    }

    @GetMapping("/stats/alerts")
    public ResponseEntity<List<DashboardAlertDTO>> getDashboardAlerts(@RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(assetStatsService.getDashboardAlerts(locationId));
    }

    @GetMapping("/stats/value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialStatsDTO> getFinancialStats(@RequestParam(required = false) Long locationId) {
        return ResponseEntity.ok(assetStatsService.getFinancialStats(locationId));
    }
}
