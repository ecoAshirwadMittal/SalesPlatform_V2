package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.pws.FuturePriceConfig;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.FuturePriceConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuturePriceConfigController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class FuturePriceConfigControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private FuturePriceConfigService configService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private FuturePriceConfig makeConfig(Long id, LocalDateTime date) {
        FuturePriceConfig c = new FuturePriceConfig();
        c.setId(id);
        c.setFuturePriceDate(date);
        c.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        c.setUpdatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        return c;
    }

    @Test
    @DisplayName("GET /config returns 401 without auth")
    void getConfigRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/pricing/config"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /config returns current config")
    void getConfigReturnsConfig() throws Exception {
        when(configService.getConfig()).thenReturn(
                makeConfig(1L, LocalDateTime.of(2026, 5, 1, 0, 0)));

        mockMvc.perform(get("/api/v1/pws/pricing/config")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.futurePriceDate").value("2026-05-01T00:00"));
    }

    @Test
    @DisplayName("GET /config returns empty string when date is null")
    void getConfigReturnsEmptyWhenNull() throws Exception {
        when(configService.getConfig()).thenReturn(makeConfig(1L, null));

        mockMvc.perform(get("/api/v1/pws/pricing/config")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futurePriceDate").value(""));
    }

    @Test
    @DisplayName("PUT /config updates the future price date")
    void putConfigUpdatesDate() throws Exception {
        when(configService.updateFuturePriceDate(any())).thenReturn(
                makeConfig(1L, LocalDateTime.of(2026, 6, 15, 0, 0)));

        mockMvc.perform(put("/api/v1/pws/pricing/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"futurePriceDate\":\"2026-06-15T00:00:00\"}")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futurePriceDate").value("2026-06-15T00:00"));
    }

    @Test
    @DisplayName("PUT /config returns 401 without auth")
    void putConfigRequiresAuth() throws Exception {
        mockMvc.perform(put("/api/v1/pws/pricing/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"futurePriceDate\":\"2026-06-15T00:00:00\"}"))
                .andExpect(status().isUnauthorized());
    }
}
