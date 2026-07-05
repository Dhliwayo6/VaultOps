package com.vaultops.assets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.enums.UserRole;
import com.vaultops.enums.UserStatus;
import com.vaultops.model.User;
import com.vaultops.repository.UserRepository;
import com.vaultops.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Role-Based Authorization Integration Tests")
public class AuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User adminUser;
    private User regularUser;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create Admin
        adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("Password123!");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getEmail(), adminUser.getRole().name(), adminUser.getName(), adminUser.getId());

        // Create User
        regularUser = new User();
        regularUser.setName("Regular User");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword("Password123!");
        regularUser.setRole(UserRole.USER);
        regularUser.setStatus(UserStatus.ACTIVE);
        regularUser = userRepository.save(regularUser);
        userToken = jwtTokenProvider.generateAccessToken(regularUser.getEmail(), regularUser.getRole().name(), regularUser.getName(), regularUser.getId());
    }

    @Test
    @DisplayName("Verify user management listing endpoint is gated behind ADMIN")
    void testUserListingEndpointAccess() throws Exception {
        // Gated for USER (returns 403)
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        // Permitted for ADMIN (returns 200)
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify role promotion endpoint is gated behind ADMIN")
    void testRolePromotionEndpointAccess() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("role", "ADMIN");

        // Gated for USER (returns 403)
        mockMvc.perform(put("/api/users/" + regularUser.getId() + "/role")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());

        // Permitted for ADMIN (returns 200 and promotes role)
        mockMvc.perform(put("/api/users/" + regularUser.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Verify asset deletion endpoint is gated behind ADMIN")
    void testAssetDeleteEndpointAccess() throws Exception {
        // Gated for USER (returns 403)
        mockMvc.perform(delete("/api/asset/999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
