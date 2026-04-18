package com.ecoatm.salesplatform.event;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventorySnowflakeSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link AggInventorySyncListener}.
 *
 * <p>Uses the {@code *IT.java} naming convention (excluded from the default
 * Surefire {@code **Test.java} / {@code **Tests.java} patterns) to match the
 * sibling {@code AggregatedInventorySnowflakeSyncServiceIT}, which also uses a
 * real Spring context via {@link PostgresIntegrationTest}. Run explicitly with:
 * <pre>
 *   mvn test -Dtest=AggInventorySyncListenerIT
 * </pre>
 *
 * <p>A {@code @Primary} Mockito mock of
 * {@link AggregatedInventorySnowflakeSyncService} is registered via
 * {@link TestConfiguration} (not {@code @MockBean}, which would poison the
 * shared context cache). The mock is reset in {@link #resetMocks()} between
 * tests.
 *
 * <p>The test exercises the full {@code @TransactionalEventListener} /
 * {@code @Async} wiring — a Mockito-only approach would not activate the
 * Spring event infrastructure and would give false confidence.
 */
@TestPropertySource(properties = {
        "snowflake.enabled=true",
        "snowflake.username=dummy-test-user",
        "snowflake.password=dummy-test-pwd",
        "snowflake.jdbc-url=jdbc:snowflake://it-test-unused.snowflakecomputing.com/?db=ECO_DEV"
})
class AggInventorySyncListenerIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class MockSyncServiceConfig {
        @Bean
        @Primary
        public AggregatedInventorySnowflakeSyncService mockSyncService() {
            return Mockito.mock(AggregatedInventorySnowflakeSyncService.class);
        }
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AggregatedInventorySnowflakeSyncService syncService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(syncService);
    }

    @Test
    @DisplayName("commit_firesSync: event published inside a committed tx triggers syncWeek")
    void commit_firesSync() {
        // Publish the event inside a real transaction that commits.
        // TransactionTemplate is used so the outer test method is NOT
        // annotated @Transactional (which would roll back and suppress the
        // AFTER_COMMIT listener, masking the assertion).
        transactionTemplate.executeWithoutResult(status ->
                eventPublisher.publishEvent(
                        new AggInventorySyncRequestedEvent(42L, "test@example.com")));

        // @Async runs on a separate thread — timeout(2000) polls until Mockito
        // sees the call or the deadline passes.
        verify(syncService, timeout(2000)).syncWeek(eq(42L), eq("test@example.com"));
    }

    @Test
    @DisplayName("rollback_doesNotFireSync: event published in a rolled-back tx is suppressed")
    void rollback_doesNotFireSync() {
        // Publish the event inside a transaction that rolls back.
        // The RuntimeException is caught at the test boundary so the test
        // itself does not fail — it is there solely to trigger the rollback.
        try {
            transactionTemplate.executeWithoutResult(status -> {
                eventPublisher.publishEvent(
                        new AggInventorySyncRequestedEvent(99L, "rollback@example.com"));
                // Force rollback — AFTER_COMMIT listener must NOT fire.
                throw new RuntimeException("forced rollback");
            });
        } catch (RuntimeException ignored) {
            // Expected — the rollback is intentional.
        }

        // Give the executor a brief window to demonstrate it does NOT fire.
        // 500 ms is ample since the executor is idle and any spurious delivery
        // would land well within this window.
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        verify(syncService, never()).syncWeek(eq(99L), eq("rollback@example.com"));
    }

    @Test
    @DisplayName("fallbackExecution_firesWhenNoTx: event published outside a tx still triggers syncWeek")
    void fallbackExecution_firesWhenNoTx() {
        // The Phase-6 POST endpoint is not transactional — it just calls
        // eventPublisher.publishEvent(...) and returns 202. Without
        // fallbackExecution=true the event would be silently dropped.
        // Publishing directly from a non-transactional method simulates
        // that controller path.
        eventPublisher.publishEvent(
                new AggInventorySyncRequestedEvent(7L, "notx@example.com"));

        verify(syncService, timeout(2000)).syncWeek(eq(7L), eq("notx@example.com"));
    }
}
