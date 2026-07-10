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

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class StatsController {

    private final AssetStatsService assetStatsService;

    @GetMapping("/stats/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(assetStatsService.getDashboardStats());
    }

    @GetMapping("/stats/trends")
    public ResponseEntity<List<MonthlyTrendDTO>> getMonthlyTrends() {
        return ResponseEntity.ok(assetStatsService.getMonthlyTrends());
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<List<CategoryConditionStatDTO>> getCategoryConditionStats() {
        return ResponseEntity.ok(assetStatsService.getCategoryConditionStats());
    }

    @GetMapping("/stats/alerts")
    public ResponseEntity<List<DashboardAlertDTO>> getDashboardAlerts() {
        return ResponseEntity.ok(assetStatsService.getDashboardAlerts());
    }

    @GetMapping("/stats/value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialStatsDTO> getFinancialStats() {
        return ResponseEntity.ok(assetStatsService.getFinancialStats());
    }
}
