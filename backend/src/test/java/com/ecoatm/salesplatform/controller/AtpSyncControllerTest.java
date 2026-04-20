package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.model.integration.SyncRunLog;
import com.ecoatm.salesplatform.repository.integration.SyncRunLogRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.AtpSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @MockBean private SyncRunLogRepository syncRunLogRepository;

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

    private SyncRunLog makeLogEntry(Long id) {
        SyncRunLog e = new SyncRunLog();
        e.setId(id);
        return e;
    }

    @Test
    void fullSync_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/sync/full"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fullSync_withToken_returns200_andPersistsSuccessLog() throws Exception {
        when(syncRunLogRepository.save(any(SyncRunLog.class))).thenAnswer(inv -> {
            SyncRunLog arg = inv.getArgument(0);
            if (arg.getId() == null) arg.setId(42L);
            return arg;
        });
        when(atpSyncService.fullInventorySync()).thenReturn(makeResult(10, 2));

        mockMvc.perform(post("/api/v1/inventory/sync/full")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devicesUpdated").value(10))
                .andExpect(jsonPath("$.devicesMissing").value(2));

        verify(syncRunLogRepository, times(2)).save(any(SyncRunLog.class));
    }

    @Test
    void fullSync_exception_returns500_andPersistsFailedLog() throws Exception {
        when(syncRunLogRepository.save(any(SyncRunLog.class))).thenAnswer(inv -> {
            SyncRunLog arg = inv.getArgument(0);
            if (arg.getId() == null) arg.setId(7L);
            return arg;
        });
        when(atpSyncService.fullInventorySync()).thenThrow(new RuntimeException("Connection failed"));

        mockMvc.perform(post("/api/v1/inventory/sync/full")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Connection failed")));

        verify(syncRunLogRepository, times(2)).save(any(SyncRunLog.class));
    }

    @Test
    void simulateSync_withToken_returns200() throws Exception {
        when(syncRunLogRepository.save(any(SyncRunLog.class))).thenAnswer(inv -> inv.getArgument(0));
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
        when(syncRunLogRepository.save(any(SyncRunLog.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atpSyncService.simulateSync(anyList())).thenThrow(new RuntimeException("Parse error"));

        mockMvc.perform(post("/api/v1/inventory/sync/simulate")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"itemNumber\":\"BAD\"}]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Parse error")));
    }

    @Test
    void recentLogs_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/sync/logs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void recentLogs_withToken_returnsMappedPage() throws Exception {
        SyncRunLog entry = new SyncRunLog();
        entry.setId(99L);
        entry.setSyncType("FULL");
        entry.setStatus("SUCCESS");
        entry.setStartTime(LocalDateTime.of(2026, 4, 15, 10, 0));
        entry.setEndTime(LocalDateTime.of(2026, 4, 15, 10, 5));
        entry.setTotalItemsReceived(200);
        entry.setDevicesUpdated(15);
        entry.setDevicesMissing(3);
        entry.setTriggeredBy("admin@test.com");

        Page<SyncRunLog> page = new PageImpl<>(List.of(entry));
        when(syncRunLogRepository.findAllByOrderByStartTimeDesc(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/inventory/sync/logs")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(99))
                .andExpect(jsonPath("$.content[0].syncType").value("FULL"))
                .andExpect(jsonPath("$.content[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$.content[0].devicesUpdated").value(15))
                .andExpect(jsonPath("$.content[0].triggeredBy").value("admin@test.com"))
                .andExpect(jsonPath("$.content[0].durationMs").value(300000));
    }
}
