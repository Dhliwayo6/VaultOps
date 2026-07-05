package com.vaultops.controllers;

import com.vaultops.dtos.DashboardStatsDTO;
import com.vaultops.services.AssetStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

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
}
