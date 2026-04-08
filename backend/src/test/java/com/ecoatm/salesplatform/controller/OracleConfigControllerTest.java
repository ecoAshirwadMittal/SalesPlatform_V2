package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for OracleConfigController — covers SEC-06 (password encryption + never exposed)
 * and SEC-03 (ADMIN role required for /api/v1/admin/**).
 */
@WebMvcTest(OracleConfigController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OracleConfigControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OracleConfigRepository oracleConfigRepository;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("ADMIN"), false);
    }

    private String bidderToken() {
        return jwtService.generateToken(2L, "bidder@test.com", List.of("Bidder"), false);
    }

    private OracleConfig makeConfig() {
        OracleConfig config = new OracleConfig();
        config.setId(1L);
        config.setUsername("oracle_user");
        config.setPasswordHash("ZW5jcnlwdGVk"); // base64 "encrypted"
        config.setAuthPath("https://oracle.example.com/auth");
        config.setCreateOrderPath("https://oracle.example.com/order");
        config.setTimeoutMs(5000);
        config.setIsActive(true);
        return config;
    }

    // --- SEC-03: Admin endpoints require ADMIN role ---

    @Test
    void getConfig_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/oracle-config"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getConfig_withBidderToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getConfig_withAdminToken_returns200() throws Exception {
        when(oracleConfigRepository.findAll()).thenReturn(List.of(makeConfig()));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("oracle_user"))
                .andExpect(jsonPath("$.isCreateOrderApiOn").value(true));
    }

    // --- SEC-06: Password never exposed in GET response ---

    @Test
    void getConfig_passwordIsNeverExposed() throws Exception {
        when(oracleConfigRepository.findAll()).thenReturn(List.of(makeConfig()));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.hasPassword").value(true));
    }

    // --- SEC-06: updateConfig encrypts password ---

    @Test
    void updateConfig_encodesPasswordAsBase64() throws Exception {
        OracleConfig existingConfig = makeConfig();
        when(oracleConfigRepository.findAll()).thenReturn(List.of(existingConfig));
        when(oracleConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "new_user",
                                "password": "new_secret",
                                "authPath": "https://oracle.example.com/auth",
                                "timeoutMs": 10000,
                                "isCreateOrderApiOn": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new_user"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Verify the password was base64-encoded before saving
        String expectedHash = java.util.Base64.getEncoder().encodeToString("new_secret".getBytes());
        org.assertj.core.api.Assertions.assertThat(existingConfig.getPasswordHash())
                .isEqualTo(expectedHash);
    }

    @Test
    void updateConfig_blankPassword_doesNotOverwrite() throws Exception {
        OracleConfig existingConfig = makeConfig();
        String originalHash = existingConfig.getPasswordHash();
        when(oracleConfigRepository.findAll()).thenReturn(List.of(existingConfig));
        when(oracleConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "oracle_user",
                                "password": "",
                                "authPath": "https://oracle.example.com/auth"
                            }
                            """))
                .andExpect(status().isOk());

        org.assertj.core.api.Assertions.assertThat(existingConfig.getPasswordHash())
                .isEqualTo(originalHash);
    }

    // --- testAuth validation ---

    @Test
    void testAuth_withoutAuthPath_returnsError() throws Exception {
        OracleConfig config = makeConfig();
        config.setAuthPath(null);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Auth path is not configured."));
    }

    @Test
    void testAuth_withoutUsername_returnsError() throws Exception {
        OracleConfig config = makeConfig();
        config.setUsername(null);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is not configured."));
    }

    // --- testAuth with valid config (HTTP will fail → caught exception path) ---

    @Test
    void testAuth_withValidConfig_httpFails_returnsError() throws Exception {
        OracleConfig config = makeConfig();
        // Use an invalid URL that will cause connection failure
        config.setAuthPath("http://localhost:1/nonexistent");
        config.setTimeoutMs(500);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAuth_withNullTimeout_usesDefault() throws Exception {
        OracleConfig config = makeConfig();
        config.setAuthPath("http://localhost:1/nonexistent");
        config.setTimeoutMs(null);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAuth_withBlankAuthPath_returnsError() throws Exception {
        OracleConfig config = makeConfig();
        config.setAuthPath("   ");
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Auth path is not configured."));
    }

    @Test
    void testAuth_withBlankUsername_returnsError() throws Exception {
        OracleConfig config = makeConfig();
        config.setUsername("  ");
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(post("/api/v1/admin/oracle-config/test-auth")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is not configured."));
    }

    // --- updateConfig with all fields ---

    @Test
    void updateConfig_withAllFields_returns200() throws Exception {
        OracleConfig existingConfig = makeConfig();
        when(oracleConfigRepository.findAll()).thenReturn(List.of(existingConfig));
        when(oracleConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "updated_user",
                                "password": "updated_secret",
                                "authPath": "https://updated.example.com/auth",
                                "createOrderPath": "https://updated.example.com/order",
                                "createRmaPath": "https://updated.example.com/rma",
                                "timeoutMs": 15000,
                                "isCreateOrderApiOn": false
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_user"))
                .andExpect(jsonPath("$.authPath").value("https://updated.example.com/auth"))
                .andExpect(jsonPath("$.createOrderPath").value("https://updated.example.com/order"))
                .andExpect(jsonPath("$.createRmaPath").value("https://updated.example.com/rma"))
                .andExpect(jsonPath("$.timeoutMs").value(15000))
                .andExpect(jsonPath("$.isCreateOrderApiOn").value(false));
    }

    // --- toDto with null updatedDate ---

    @Test
    void getConfig_withNullUpdatedDate_returnsNullString() throws Exception {
        OracleConfig config = makeConfig();
        config.setUpdatedDate(null);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDate").doesNotExist());
    }

    // --- toDto with null passwordHash ---

    @Test
    void getConfig_withNullPasswordHash_hasPasswordIsFalse() throws Exception {
        OracleConfig config = makeConfig();
        config.setPasswordHash(null);
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPassword").value(false));
    }

    @Test
    void getConfig_withBlankPasswordHash_hasPasswordIsFalse() throws Exception {
        OracleConfig config = makeConfig();
        config.setPasswordHash("   ");
        when(oracleConfigRepository.findAll()).thenReturn(List.of(config));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPassword").value(false));
    }

    // --- getOrCreate ---

    @Test
    void getConfig_emptyDb_createsDefaultConfig() throws Exception {
        OracleConfig newConfig = new OracleConfig();
        newConfig.setId(1L);
        newConfig.setIsActive(false);
        newConfig.setTimeoutMs(5000);
        when(oracleConfigRepository.findAll()).thenReturn(List.of());
        when(oracleConfigRepository.save(any())).thenReturn(newConfig);

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCreateOrderApiOn").value(false));
    }
}
