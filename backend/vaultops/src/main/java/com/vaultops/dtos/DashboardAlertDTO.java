package com.vaultops.dtos;

public record DashboardAlertDTO(
    String id,
    String type,
    String title,
    String message,
    long count,
    String link,
    String actionLabel
) {}
