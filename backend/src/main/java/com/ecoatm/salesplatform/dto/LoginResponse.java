package com.ecoatm.salesplatform.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    // Not serialized to JSON — the token is conveyed as an HttpOnly Set-Cookie
    // header by AuthController. Kept as an internal carrier between AuthService
    // and the controller.
    @JsonIgnore
    private String token;
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
