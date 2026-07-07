package com.vaultops.services;

import com.vaultops.dtos.UserDTO;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.exceptions.UserNotFoundException;
import com.vaultops.model.User;
import com.vaultops.repository.UserRepository;
import com.vaultops.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO changeRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    @Transactional
    public UserDTO changeStatus(Long id, UserStatus status, String requestorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        if (user.getEmail().equals(requestorEmail)) {
            throw new IllegalArgumentException("Cannot change your own account status");
        }
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        if (status == UserStatus.SUSPENDED) {
            userRefreshTokenRepository.deleteByUser(user);
        }
        return new UserDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long id, String requestorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        if (user.getEmail().equals(requestorEmail)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return new UserDTO(user);
    }
}
