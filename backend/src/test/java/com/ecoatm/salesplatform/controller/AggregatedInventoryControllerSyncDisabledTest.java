package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.event.AggInventorySyncRequestedEvent;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryExcelExporter;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies POST .../sync behaviour when {@code snowflake.enabled=false}.
 *
 * <p>Kept as a separate top-level class so Surefire's default {@code **‌/*Test.java}
 * discovery includes it automatically. The companion enabled-case tests live in
 * {@link AggregatedInventoryControllerSyncTest}.
 */
@WebMvcTest(AggregatedInventoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000",
    "snowflake.enabled=false"
})
class AggregatedInventoryControllerSyncDisabledTest {

    @TestConfiguration
    static class EventCaptureConfig {
        @Bean
        CapturedEvents capturedEvents() {
            return new CapturedEvents();
        }

        @Bean
        SyncEventCollector syncEventCollector(CapturedEvents store) {
            return new SyncEventCollector(store);
        }
    }

    static class CapturedEvents {
        final List<AggInventorySyncRequestedEvent> syncEvents = new ArrayList<>();
    }

    static class SyncEventCollector {
        private final CapturedEvents store;

        SyncEventCollector(CapturedEvents store) {
            this.store = store;
        }

        @EventListener
        void onSyncRequested(AggInventorySyncRequestedEvent event) {
            store.syncEvents.add(event);
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CapturedEvents capturedEvents;

    @MockBean
    private AggregatedInventoryService service;

    @MockBean
    private WeekRepository weekRepository;

    @MockBean
    private AggregatedInventoryExcelExporter excelExporter;

    @MockBean
    private SnowflakeSyncLogRepository snowflakeSyncLogRepository;

    @BeforeEach
    void clearEvents() {
        capturedEvents.syncEvents.clear();
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("POST /weeks/{weekId}/sync returns 202 SKIPPED_DISABLED and does not publish when snowflake is disabled")
    void trigger_whenSnowflakeDisabled_returns202_andSkipsDisabledWithoutPublish() throws Exception {
        mvc.perform(post("/api/v1/admin/inventory/weeks/42/sync"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("SKIPPED_DISABLED"))
                .andExpect(jsonPath("$.source").value("SNOWFLAKE_AGG_INVENTORY"));

        assertThat(capturedEvents.syncEvents).isEmpty();
    }
}
