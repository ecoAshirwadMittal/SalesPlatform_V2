package com.ecoatm.salesplatform.event;

/**
 * Published by the Phase-6 sync controller to trigger an async Snowflake
 * aggregated-inventory pull. Delivered post-commit by
 * {@link AggInventorySyncListener} on the {@code snowflakeExecutor} thread pool.
 *
 * <p>Carries only the primary-key handles needed by the listener —
 * events must be small and thread-safe to cross executor boundaries safely.
 *
 * @param weekId      PK of {@code mdm.week} whose inventory should be synced
 * @param triggeredBy free-form label threaded into audit logs — typically the
 *                    email of the admin who triggered the run
 */
public record AggInventorySyncRequestedEvent(long weekId, String triggeredBy) {}
