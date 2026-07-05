package com.vaultops.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private String name;
        private String email;
        private String role;
    }
}
