package com.ecoatm.salesplatform.event;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventorySnowflakeSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges the Phase-6 sync controller to
 * {@link AggregatedInventorySnowflakeSyncService} via an async event.
 *
 * <p>The handler runs on {@link AsyncConfig#SNOWFLAKE_EXECUTOR} so the
 * 202-Accepted controller response is returned to the caller immediately —
 * the actual Snowflake pull (potentially minutes for 87k rows) happens
 * on a background thread without blocking the HTTP request.
 *
 * <p>Using {@link TransactionPhase#AFTER_COMMIT} guarantees that a rollback
 * in the publishing transaction suppresses the sync entirely — no partial
 * or phantom syncs are triggered by failed requests. {@code fallbackExecution
 * = true} allows the Phase-6 controller (which is not itself transactional)
 * to publish the event outside a transaction and still have it delivered.
 *
 * <p>Gated on {@code snowflake.enabled=true} so this bean is absent when
 * the feature is disabled, mirroring the gate on
 * {@link AggregatedInventorySnowflakeSyncService}. An absent listener means
 * no wasted bean registrations and no misleading log lines when disabled.
 */
@Component
@ConditionalOnProperty(name = "snowflake.enabled", havingValue = "true")
public class AggInventorySyncListener {

    private static final Logger log = LoggerFactory.getLogger(AggInventorySyncListener.class);

    private final AggregatedInventorySnowflakeSyncService service;

    public AggInventorySyncListener(AggregatedInventorySnowflakeSyncService service) {
        this.service = service;
    }

    /**
     * Receives a {@link AggInventorySyncRequestedEvent} after the publishing
     * transaction commits and dispatches {@code syncWeek} on the Snowflake
     * executor thread pool.
     *
     * <p>The service already catches all {@code SnowflakeReadException},
     * {@code DataAccessException}, and {@code RuntimeException} variants and
     * returns a {@code FAILED} result — it never rethrows. Additional
     * async-specific failures (thread interruption, executor rejection) are
     * logged at ERROR and swallowed here so they do not propagate to the
     * uncaught-exception handler and potentially hang the thread.
     *
     * @param event the sync request carrying {@code weekId} and
     *              {@code triggeredBy}
     */
    @Async(AsyncConfig.SNOWFLAKE_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onSyncRequested(AggInventorySyncRequestedEvent event) {
        log.info("AggInventorySync event received weekId={} triggeredBy={}",
                event.weekId(), event.triggeredBy());
        try {
            service.syncWeek(event.weekId(), event.triggeredBy());
        } catch (Exception ex) {
            // Defensive catch for async-specific failures not covered by the service
            // (e.g. RejectedExecutionException surfacing inside the call, thread
            // interruption). Log and swallow so the executor thread stays alive.
            log.error("Async sync dispatch failed weekId={} triggeredBy={}",
                    event.weekId(), event.triggeredBy(), ex);
        }
    }
}
