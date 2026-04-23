package com.ecoatm.salesplatform.security;

import com.ecoatm.salesplatform.controller.AuthController;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.service.PasswordResetService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests that SecurityFilterChain enforces authentication rules correctly.
 * Uses @WebMvcTest with AuthController as the test surface since it has
 * both public (/login) and protected (/me, /buyer-codes) endpoints.
 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class SecurityConfigTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private AuthService authService;
    @MockBean private BuyerCodeService buyerCodeService;
    @MockBean private PasswordResetService passwordResetService;

    // --- Public endpoints ---

    @Test
    void loginEndpoint_isPublicAndAccessible() throws Exception {
        // Login is permitAll — should reach the controller (not 401/403)
        com.ecoatm.salesplatform.dto.LoginResponse failResp =
                new com.ecoatm.salesplatform.dto.LoginResponse(false, "No account", null);
        when(authService.authenticateLocalUser(any())).thenReturn(failResp);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized()); // 401 from controller, not 403 from filter
    }

    // --- Protected endpoints reject unauthenticated requests ---

    @Test
    void protectedEndpoint_returns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buyerCodesEndpoint_returns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/buyer-codes"))
                .andExpect(status().isUnauthorized());
    }

    // --- Expired / invalid tokens ---

    @Test
    void expiredToken_returns401() throws Exception {
        JwtService expiredService = new JwtService(
                "test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!", 0, 0);
        String token = expiredService.generateToken(1L, "a@b.com", List.of("ADMIN"), false);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenString_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer dummy-jwt-token-42"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSignedWithDifferentSecret_returns401() throws Exception {
        JwtService wrongKeyService = new JwtService(
                "wrong-secret-key-must-also-be-at-least-32-bytes!!!", 3600000, 7200000);
        String token = wrongKeyService.generateToken(1L, "a@b.com", List.of("ADMIN"), false);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void basicAuthHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Basic dXNlcjpwYXNz"))
                .andExpect(status().isUnauthorized());
    }
}
