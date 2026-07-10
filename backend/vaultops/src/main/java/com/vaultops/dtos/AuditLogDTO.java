package com.vaultops.dtos;

import com.vaultops.model.AuditLog;
import java.time.LocalDateTime;

public record AuditLogDTO(
    Long id,
    LocalDateTime timestamp,
    String actingUser,
    String actionType,
    String resourceType,
    String resourceId,
    String description
) {
    public AuditLogDTO(AuditLog log) {
        this(
            log.getId(),
            log.getTimestamp(),
            maskEmail(log.getActingUser()),
            log.getActionType(),
            log.getResourceType(),
            log.getResourceId(),
            log.getDescription()
        );
    }

    private static String maskEmail(String user) {
        if (user == null) {
            return "unknown";
        }
        int atIndex = user.indexOf('@');
        if (atIndex <= 0) {
            return user;
        }
        String localPart = user.substring(0, atIndex);
        String domainPart = user.substring(atIndex);
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domainPart;
        }
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
    }
}
