package com.vaultops.dtos;

import com.vaultops.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    @NotNull(message = "Role is required")
    private UserRole role;
}
