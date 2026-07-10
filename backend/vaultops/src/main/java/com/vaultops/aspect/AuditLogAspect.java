package com.vaultops.aspect;

import com.vaultops.dtos.*;
import com.vaultops.model.AuditLog;
import com.vaultops.repository.AuditLogRepository;
import com.vaultops.repository.ImportLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ImportLogRepository importLogRepository;

    // AssetService Mutations
    @AfterReturning(pointcut = "execution(* com.vaultops.services.AssetService.create(..))", returning = "result")
    public void logAssetCreate(Object result) {
        if (result instanceof AssetDTO dto) {
            saveLog("CREATE_ASSET", "Asset", dto.id().toString(), "Asset registered: " + dto.name());
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.AssetService.update(..))", returning = "result")
    public void logAssetUpdate(Object result) {
        if (result instanceof AssetDTO dto) {
            saveLog("UPDATE_ASSET", "Asset", dto.id().toString(), "Asset updated: " + dto.name());
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.AssetService.delete(..)) && args(id)", argNames = "id")
    public void logAssetDelete(Long id) {
        saveLog("DELETE_ASSET", "Asset", id.toString(), "Asset deleted");
    }

    // MaintenanceService Mutations
    @AfterReturning(pointcut = "execution(* com.vaultops.services.MaintenanceService.create(..))", returning = "result")
    public void logMaintenanceCreate(Object result) {
        if (result instanceof MaintenanceDTO dto) {
            saveLog("CREATE_MAINTENANCE", "Maintenance", dto.getId().toString(), "Maintenance task created for asset: " + (dto.getAsset() != null ? dto.getAsset().id() : "N/A"));
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.MaintenanceService.update(..))", returning = "result")
    public void logMaintenanceUpdate(Object result) {
        if (result instanceof MaintenanceDTO dto) {
            saveLog("UPDATE_MAINTENANCE", "Maintenance", dto.getId().toString(), "Maintenance task updated for asset: " + (dto.getAsset() != null ? dto.getAsset().id() : "N/A"));
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.MaintenanceService.delete(..)) && args(id)", argNames = "id")
    public void logMaintenanceDelete(Long id) {
        saveLog("DELETE_MAINTENANCE", "Maintenance", id.toString(), "Maintenance record deleted");
    }

    // MigrationService Mutations
    @AfterReturning(pointcut = "execution(* com.vaultops.services.MigrationService.create(..))", returning = "result")
    public void logMigrationCreate(Object result) {
        if (result instanceof MigrationDTO dto) {
            saveLog("CREATE_MIGRATION", "Migration", dto.getId().toString(), "Migration recorded for asset: " + (dto.getAsset() != null ? dto.getAsset().id() : "N/A"));
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.MigrationService.update(..))", returning = "result")
    public void logMigrationUpdate(Object result) {
        if (result instanceof MigrationDTO dto) {
            saveLog("UPDATE_MIGRATION", "Migration", dto.getId().toString(), "Migration record updated for asset: " + (dto.getAsset() != null ? dto.getAsset().id() : "N/A"));
        }
    }

    @AfterReturning(pointcut = "execution(* com.vaultops.services.MigrationService.delete(..)) && args(id)", argNames = "id")
    public void logMigrationDelete(Long id) {
        saveLog("DELETE_MIGRATION", "Migration", id.toString(), "Migration record deleted");
    }

    // AssetImportService (Async)
    @AfterReturning(pointcut = "execution(* com.vaultops.services.AssetImportService.processImportAsync(..)) && args(importLogId, ..)", argNames = "importLogId")
    public void logImportComplete(Long importLogId) {
        importLogRepository.findById(importLogId).ifPresent(logEntry -> {
            String status = logEntry.getStatus() != null ? logEntry.getStatus().name() : "UNKNOWN";
            saveLog("IMPORT_COMPLETED", "ImportLog", importLogId.toString(),
                String.format("Bulk import completed. File: %s, Status: %s, Success: %d, Errors: %d",
                    logEntry.getFileName(), status, logEntry.getSuccessCount(), logEntry.getErrorCount()), logEntry.getUserId());
        });
    }

    @AfterThrowing(pointcut = "execution(* com.vaultops.services.AssetImportService.processImportAsync(..)) && args(importLogId, ..)", throwing = "ex", argNames = "importLogId, ex")
    public void logImportFailed(Long importLogId, Exception ex) {
        importLogRepository.findById(importLogId).ifPresent(logEntry -> {
            saveLog("IMPORT_FAILED", "ImportLog", importLogId.toString(),
                String.format("Bulk import failed. File: %s, Error: %s", logEntry.getFileName(), ex.getMessage()), logEntry.getUserId());
        });
    }

    // AuthService OTP Verify
    @AfterReturning(pointcut = "execution(* com.vaultops.services.AuthService.verifyOtp(..)) && args(request)", argNames = "request")
    public void logOtpVerifySuccess(VerifyOtpRequest request) {
        saveLog("OTP_VERIFICATION_SUCCESS", "User", null,
            "OTP verified successfully", request.getEmail());
    }

    @AfterThrowing(pointcut = "execution(* com.vaultops.services.AuthService.verifyOtp(..)) && args(request)", throwing = "ex", argNames = "request, ex")
    public void logOtpVerifyFailure(VerifyOtpRequest request, Exception ex) {
        saveLog("OTP_VERIFICATION_FAILURE", "User", null,
            "OTP verification failed: " + ex.getMessage(), request.getEmail());
    }

    // AuthService Login
    @AfterReturning(pointcut = "execution(* com.vaultops.services.AuthService.login(..)) && args(request)", argNames = "request")
    public void logLoginSuccess(LoginRequest request) {
        saveLog("LOGIN_SUCCESS", "User", null,
            "User logged in successfully", request.getEmail());
    }

    @AfterThrowing(pointcut = "execution(* com.vaultops.services.AuthService.login(..)) && args(request)", throwing = "ex", argNames = "request, ex")
    public void logLoginFailure(LoginRequest request, Exception ex) {
        saveLog("LOGIN_FAILURE", "User", null,
            "Login failed: " + ex.getMessage(), request.getEmail());
    }

    // AuthService Register
    @AfterReturning(pointcut = "execution(* com.vaultops.services.AuthService.register(..)) && args(request)", argNames = "request")
    public void logRegister(RegisterRequest request) {
        saveLog("REGISTER_USER", "User", null,
            "User registered: " + request.getName(), request.getEmail());
    }

    // Log recording helpers
    private void saveLog(String actionType, String resourceType, String resourceId, String description) {
        String currentUser = getCurrentUserEmail();
        saveLog(actionType, resourceType, resourceId, description, currentUser);
    }

    private void saveLog(String actionType, String resourceType, String resourceId, String description, String actingUser) {
        AuditLog auditLog = AuditLog.builder()
            .timestamp(LocalDateTime.now())
            .actingUser(actingUser)
            .actionType(actionType)
            .resourceType(resourceType)
            .resourceId(resourceId)
            .description(description)
            .build();

        auditLogRepository.save(auditLog);

        // Standard structured log mapping console outputs from Task 07g
        log.info("AUDIT_LOG - User: {}, Action: {}, ResourceType: {}, ResourceId: {}, Description: {}",
            actingUser, actionType, resourceType, resourceId, description);
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "system";
    }
}
