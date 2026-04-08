package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.AtpSyncService;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtpSyncController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AtpSyncControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private AtpSyncService atpSyncService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private AtpSyncResult makeResult(int updated, int missing) {
        AtpSyncResult result = new AtpSyncResult();
        result.setSyncStartTime(LocalDateTime.now());
        result.setSyncEndTime(LocalDateTime.now());
        result.setDevicesUpdated(updated);
        result.setDevicesMissing(missing);
        result.setTotalItemsReceived(updated + missing);
        result.setMissingSkus(List.of());
        return result;
    }

    @Test
    void fullSync_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/sync/full"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fullSync_withToken_returns200() throws Exception {
        when(atpSyncService.fullInventorySync()).thenReturn(makeResult(10, 2));

        mockMvc.perform(post("/api/v1/inventory/sync/full")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devicesUpdated").value(10))
                .andExpect(jsonPath("$.devicesMissing").value(2));
    }

    @Test
    void fullSync_exception_returns500() throws Exception {
        when(atpSyncService.fullInventorySync()).thenThrow(new RuntimeException("Connection failed"));

        mockMvc.perform(post("/api/v1/inventory/sync/full")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Connection failed")));
    }

    @Test
    void simulateSync_withToken_returns200() throws Exception {
        when(atpSyncService.simulateSync(anyList())).thenReturn(makeResult(5, 0));

        mockMvc.perform(post("/api/v1/inventory/sync/simulate")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"itemNumber\":\"SKU-001\",\"facilities\":[{\"availableToPromise\":50}]}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devicesUpdated").value(5));
    }

    @Test
    void simulateSync_exception_returns500() throws Exception {
        when(atpSyncService.simulateSync(anyList())).thenThrow(new RuntimeException("Parse error"));

        mockMvc.perform(post("/api/v1/inventory/sync/simulate")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"itemNumber\":\"BAD\"}]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Parse error")));
    }
}
