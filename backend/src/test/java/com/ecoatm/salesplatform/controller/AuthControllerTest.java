package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private AuthService authService;
    @MockBean private BuyerCodeService buyerCodeService;

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        LoginResponse successResponse = new LoginResponse(true, "Authentication successful", "jwt-token-here");
        successResponse.setUser(new LoginResponse.UserInfo(1L, "John", "Doe", "John Doe", "john@test.com", "JD"));
        when(authService.authenticateLocalUser(any())).thenReturn(successResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.user.userId").value(1));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        LoginResponse failResponse = new LoginResponse(false, "Incorrect Password", null);
        when(authService.authenticateLocalUser(any())).thenReturn(failResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_withValidCredentials_returnsUserInfoWithInitials() throws Exception {
        LoginResponse successResponse = new LoginResponse(true, "Authentication successful", "jwt-token");
        successResponse.setUser(new LoginResponse.UserInfo(9001L, "Admin", "User", "Admin User", "admin@test.com", "AU"));
        when(authService.authenticateLocalUser(any())).thenReturn(successResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.com\",\"password\":\"Admin123!\",\"rememberMe\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.firstName").value("Admin"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andExpect(jsonPath("$.user.initials").value("AU"))
                .andExpect(jsonPath("$.user.email").value("admin@test.com"));
    }

    @Test
    void login_withBlankEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"Password123!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withMissingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withValidToken_returnsUserInfo() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(42L, "Test", "User", "Test User", "test@test.com", "TU");
        when(authService.getCurrentUser(42L)).thenReturn(userInfo);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.initials").value("TU"));
    }

    @Test
    void buyerCodes_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/buyer-codes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buyerCodes_withValidToken_returnsData() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        when(buyerCodeService.getBuyerCodesForUser(42L)).thenReturn(List.of(
                new BuyerCodeResponse(1L, "BC001", "Test Buyer", "ACME Corp")
        ));

        mockMvc.perform(get("/api/v1/auth/buyer-codes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("BC001"));
    }

    @Test
    void me_withExpiredToken_returns401() throws Exception {
        JwtService expiredJwtService = new JwtService(
                "test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!", 0, 0);
        String token = expiredJwtService.generateToken(1L, "test@test.com", List.of("Bidder"), false);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withDummyToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer dummy-jwt-token-42"))
                .andExpect(status().isUnauthorized());
    }
}
