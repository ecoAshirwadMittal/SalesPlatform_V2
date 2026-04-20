package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.event.AggInventorySyncRequestedEvent;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.event.EventListener;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc controller tests for the Phase-6 sync-trigger and sync-status endpoints.
 *
 * <p>Uses {@code @WebMvcTest} with {@code @Import(SecurityConfig…)} — the same wiring
 * as {@link AggregatedInventoryControllerTest}.
 *
 * <p>{@code ApplicationEventPublisher} is a special Spring internal interface: even if
 * registered as a {@code @MockBean}, Spring injects the real {@code ApplicationContext}
 * as the publisher into the controller constructor, bypassing the mock. To verify events
 * were published we therefore register an {@code ApplicationListener} via
 * {@code @TestConfiguration} that collects all {@code AggInventorySyncRequestedEvent}
 * instances into a {@code List} we can assert against.
 *
 * <p>Two inner test classes split tests by the {@code snowflake.enabled} property so the
 * value is fixed at Spring context load time (overriding a single property per-test is
 * not possible with {@code @TestPropertySource} in {@code @WebMvcTest} slices).
 */
@WebMvcTest(AggregatedInventoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class,
        AggregatedInventoryControllerSyncTest.EventCaptureConfig.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000",
    "snowflake.enabled=true"
})
class AggregatedInventoryControllerSyncTest {

    /**
     * Captures {@link AggInventorySyncRequestedEvent} instances published by the controller.
     *
     * <p>{@code @TestConfiguration} (not {@code @Configuration}) is intentional: Spring Boot's
     * {@code @WebMvcTest} slice treats a nested {@code @Configuration} class as the primary
     * application configuration, which replaces the auto-configured slice and causes the
     * controller's handler mappings to disappear. {@code @TestConfiguration} marks the class
     * as an additive contribution so the slice keeps its own auto-configuration.
     *
     * <p>Uses {@code @EventListener} instead of {@code ApplicationListener} because
     * {@code AggInventorySyncRequestedEvent} is a plain record (not an {@code ApplicationEvent}
     * subclass) — Spring 4.2+ supports publishing plain objects but the typed
     * {@code ApplicationListener<T>} interface requires {@code T extends ApplicationEvent}.
     */
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

    /** Simple mutable store for captured events — reset before each test. */
    static class CapturedEvents {
        final List<AggInventorySyncRequestedEvent> syncEvents = new ArrayList<>();
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

    // -------------------------------------------------------------------------
    // POST /weeks/{weekId}/sync — snowflake.enabled=true (class-level default)
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("POST /weeks/{weekId}/sync as Administrator returns 202 ACCEPTED and publishes event")
    void trigger_asAdmin_returns202_andPublishesEvent() throws Exception {
        mvc.perform(post("/api/v1/admin/inventory/weeks/42/sync"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.source").value("SNOWFLAKE_AGG_INVENTORY"));

        assertThat(capturedEvents.syncEvents).hasSize(1);
        AggInventorySyncRequestedEvent evt = capturedEvents.syncEvents.get(0);
        assertThat(evt.weekId()).isEqualTo(42L);
        // @WithMockUser defaults username to "user"
        assertThat(evt.triggeredBy()).isEqualTo("user");
    }

    @Test
    @WithMockUser(roles = "SalesOps")
    @DisplayName("POST /weeks/{weekId}/sync as SalesOps returns 202 ACCEPTED and publishes event")
    void trigger_asSalesOps_returns202_andPublishesEvent() throws Exception {
        mvc.perform(post("/api/v1/admin/inventory/weeks/7/sync"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.source").value("SNOWFLAKE_AGG_INVENTORY"));

        assertThat(capturedEvents.syncEvents).hasSize(1);
        assertThat(capturedEvents.syncEvents.get(0).weekId()).isEqualTo(7L);
    }

    @Test
    @WithMockUser(roles = "Bidder")
    @DisplayName("POST /weeks/{weekId}/sync as Bidder returns 403 and does not publish")
    void trigger_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/inventory/weeks/42/sync"))
                .andExpect(status().isForbidden());

        assertThat(capturedEvents.syncEvents).isEmpty();
    }

    // -------------------------------------------------------------------------
    // GET /weeks/{weekId}/sync/status
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("GET /weeks/{weekId}/sync/status returns 200 with mapped log row when one exists")
    void status_whenLogExists_returns200_withMappedLog() throws Exception {
        Instant t1 = Instant.parse("2026-04-18T10:00:00Z");
        Instant t2 = Instant.parse("2026-04-18T10:05:00Z");

        SnowflakeSyncLog log = new SnowflakeSyncLog();
        log.setStatus("COMPLETED");
        log.setStartedAt(t1);
        log.setFinishedAt(t2);
        log.setRowsUpserted(123);
        log.setErrorMessage(null);

        when(snowflakeSyncLogRepository
                .findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                        "SNOWFLAKE_AGG_INVENTORY", "42"))
                .thenReturn(Optional.of(log));

        mvc.perform(get("/api/v1/admin/inventory/weeks/42/sync/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.rowsUpserted").value(123))
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    @DisplayName("GET /weeks/{weekId}/sync/status returns 200 with NONE sentinel when no log row exists")
    void status_whenNoLog_returns200_withNoneSentinel() throws Exception {
        when(snowflakeSyncLogRepository
                .findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                        "SNOWFLAKE_AGG_INVENTORY", "99"))
                .thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/admin/inventory/weeks/99/sync/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NONE"))
                .andExpect(jsonPath("$.lastSyncedAt").doesNotExist())
                .andExpect(jsonPath("$.rowsUpserted").doesNotExist())
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }

}
