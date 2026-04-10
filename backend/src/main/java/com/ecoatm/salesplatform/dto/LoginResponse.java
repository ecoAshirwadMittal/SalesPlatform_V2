package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private String token; // For later JWT integration
    private UserInfo user;

    public LoginResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String firstName;
        private String lastName;
        private String fullName;
        private String email;
        private String initials;
        private List<String> roles;
    }
}
