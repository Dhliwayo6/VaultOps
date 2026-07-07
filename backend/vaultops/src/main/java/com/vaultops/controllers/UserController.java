package com.vaultops.controllers;

import com.vaultops.dtos.UserDTO;
import com.vaultops.dtos.ChangeRoleRequest;
import com.vaultops.dtos.ChangeStatusRequest;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.services.UserService;
import com.vaultops.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        securityService.checkOwnershipOrAdmin(id, "User");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UserDTO updatedUser = userService.changeRole(id, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String requestorEmail = auth.getName();
        UserDTO updatedUser = userService.changeStatus(id, request.getStatus(), requestorEmail);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String requestorEmail = auth.getName();
        userService.deleteUser(id, requestorEmail);
        return ResponseEntity.noContent().build();
    }
}
