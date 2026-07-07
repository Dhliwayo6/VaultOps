package com.vaultops.dtos;

import com.vaultops.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {
    @NotNull(message = "Status is required")
    private UserStatus status;
}
