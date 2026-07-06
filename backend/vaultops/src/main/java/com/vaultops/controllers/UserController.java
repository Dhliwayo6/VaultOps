package com.vaultops.controllers;

import com.vaultops.dtos.UserDTO;
import com.vaultops.dtos.ChangeRoleRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UserDTO updatedUser = userService.changeRole(id, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }
}
