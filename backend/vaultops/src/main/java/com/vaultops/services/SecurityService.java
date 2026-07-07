package com.vaultops.services;

import com.vaultops.exceptions.JobNotFoundException;
import com.vaultops.exceptions.UserNotFoundException;
import com.vaultops.model.ImportLog;
import com.vaultops.model.User;
import com.vaultops.repository.ImportLogRepository;
import com.vaultops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final ImportLogRepository importLogRepository;
    private final UserRepository userRepository;

    public void checkOwnershipOrAdmin(Long resourceId, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String currentUsername = auth.getName(); // email address

        if ("ImportLog".equalsIgnoreCase(resourceType)) {
            ImportLog log = importLogRepository.findById(resourceId)
                    .orElseThrow(() -> new JobNotFoundException("Import log not found: " + resourceId));
            if (!isAdmin && !currentUsername.equals(log.getUserId())) {
                // Return 404 to prevent resource enumeration
                throw new JobNotFoundException("Import log not found: " + resourceId);
            }
        } else if ("User".equalsIgnoreCase(resourceType)) {
            User user = userRepository.findById(resourceId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + resourceId));
            if (!isAdmin && !currentUsername.equals(user.getEmail())) {
                // Return 404 to prevent resource enumeration
                throw new UserNotFoundException("User not found with ID: " + resourceId);
            }
        } else {
            throw new IllegalArgumentException("Unknown resource type: " + resourceType);
        }
    }
}
