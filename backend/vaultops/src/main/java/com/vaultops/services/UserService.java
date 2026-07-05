package com.vaultops.services;

import com.vaultops.dtos.UserDTO;
import com.vaultops.enums.UserRole;
import com.vaultops.exceptions.UserNotFoundException;
import com.vaultops.model.User;
import com.vaultops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
