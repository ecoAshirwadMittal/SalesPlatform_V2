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
 * Tests for OracleConfigController — credentials now come from env vars,
 * not from database columns.
 */
@WebMvcTest(OracleConfigController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000",
    "oracle.username=test_oracle_user",
    "oracle.password=test_oracle_secret"
})
class OracleConfigControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OracleConfigRepository oracleConfigRepository;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private String bidderToken() {
        return jwtService.generateToken(2L, "bidder@test.com", List.of("Bidder"), false);
    }

    private OracleConfig makeConfig() {
        OracleConfig config = new OracleConfig();
        config.setId(1L);
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
                .andExpect(jsonPath("$.username").value("test_oracle_user"))
                .andExpect(jsonPath("$.isCreateOrderApiOn").value(true));
    }

    // --- Credentials come from env vars, never exposed ---

    @Test
    void getConfig_passwordIsNeverExposed() throws Exception {
        when(oracleConfigRepository.findAll()).thenReturn(List.of(makeConfig()));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.hasPassword").value(true));
    }

    // --- updateConfig updates paths/settings only, not credentials ---

    @Test
    void updateConfig_updatesPathsAndSettings() throws Exception {
        OracleConfig existingConfig = makeConfig();
        when(oracleConfigRepository.findAll()).thenReturn(List.of(existingConfig));
        when(oracleConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "authPath": "https://oracle.example.com/auth",
                                "timeoutMs": 10000,
                                "isCreateOrderApiOn": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
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
    void testAuth_withValidConfig_httpFails_returnsError() throws Exception {
        OracleConfig config = makeConfig();
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
                                "authPath": "https://updated.example.com/auth",
                                "createOrderPath": "https://updated.example.com/order",
                                "createRmaPath": "https://updated.example.com/rma",
                                "timeoutMs": 15000,
                                "isCreateOrderApiOn": false
                            }
                            """))
                .andExpect(status().isOk())
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

    // --- hasPassword reflects env var state ---

    @Test
    void getConfig_hasPasswordReflectsEnvVar() throws Exception {
        when(oracleConfigRepository.findAll()).thenReturn(List.of(makeConfig()));

        mockMvc.perform(get("/api/v1/admin/oracle-config")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPassword").value(true));
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
