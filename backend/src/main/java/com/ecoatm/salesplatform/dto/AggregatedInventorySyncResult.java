package com.ecoatm.salesplatform.dto;

/**
 * Outcome of one run of the aggregated-inventory Snowflake sync.
 *
 * <p>Phase 4 of {@code docs/tasks/aggregated-inventory-snowflake-sync-plan.md}.
 * Status taxonomy matches plan §3.3 exactly:
 * <ul>
 *   <li>{@link SyncStatus#SKIPPED_LOCKED} — an auction row exists for the week,
 *       so we must not stomp on numbers a round is already using.</li>
 *   <li>{@link SyncStatus#SKIPPED_UP_TO_DATE} — our watermark is already at or
 *       past Snowflake's {@code MAX(Upload_Time)}.</li>
 *   <li>{@link SyncStatus#SKIPPED_DISABLED} — {@code snowflake.enabled=false}
 *       so the service bean never loaded; the caller short-circuits.</li>
 *   <li>{@link SyncStatus#COMPLETED} — rows upserted, watermark bumped.</li>
 *   <li>{@link SyncStatus#FAILED} — Snowflake error or DB error caught inside
 *       the service; the outer transaction rolls back the upsert and the
 *       audit log row captures the error via
 *       {@code REQUIRES_NEW} so the failure is observable.</li>
 * </ul>
 *
 * <p>This record is Phase 4 only; the Phase 6 controller maps it to HTTP.
 * Phase 5 publishes {@code AggInventorySyncRequestedEvent} which feeds the
 * same {@code source} string for observability.
 *
 * @param status       outcome category
 * @param rowsRead     rows pulled from Snowflake (zero for any SKIPPED_* case)
 * @param rowsUpserted rows actually written to Postgres (batchUpdate sum)
 * @param durationMs   wall-clock duration of the run in milliseconds
 * @param source       always {@code SNOWFLAKE_AGG_INVENTORY} for this phase
 * @param logId        PK of the {@code integration.snowflake_sync_log} row;
 *                     {@code null} only for {@link SyncStatus#SKIPPED_DISABLED}
 *                     where the bean never fires
 * @param errorMessage populated only for {@link SyncStatus#FAILED}
 */
public record AggregatedInventorySyncResult(
        SyncStatus status,
        int rowsRead,
        int rowsUpserted,
        long durationMs,
        String source,
        Long logId,
        String errorMessage) {

    public static final String SOURCE_SNOWFLAKE_AGG_INVENTORY = "SNOWFLAKE_AGG_INVENTORY";

    public enum SyncStatus {
        SKIPPED_LOCKED,
        SKIPPED_UP_TO_DATE,
        SKIPPED_DISABLED,
        COMPLETED,
        FAILED
    }

    public static AggregatedInventorySyncResult completed(
            int rowsRead, int rowsUpserted, long durationMs, Long logId) {
        return new AggregatedInventorySyncResult(
                SyncStatus.COMPLETED, rowsRead, rowsUpserted, durationMs,
                SOURCE_SNOWFLAKE_AGG_INVENTORY, logId, null);
    }

    public static AggregatedInventorySyncResult skippedLocked(Long logId) {
        return new AggregatedInventorySyncResult(
                SyncStatus.SKIPPED_LOCKED, 0, 0, 0L,
                SOURCE_SNOWFLAKE_AGG_INVENTORY, logId, null);
    }

    public static AggregatedInventorySyncResult skippedUpToDate(Long logId, long durationMs) {
        return new AggregatedInventorySyncResult(
                SyncStatus.SKIPPED_UP_TO_DATE, 0, 0, durationMs,
                SOURCE_SNOWFLAKE_AGG_INVENTORY, logId, null);
    }

    public static AggregatedInventorySyncResult skippedDisabled() {
        return new AggregatedInventorySyncResult(
                SyncStatus.SKIPPED_DISABLED, 0, 0, 0L,
                SOURCE_SNOWFLAKE_AGG_INVENTORY, null, null);
    }

    public static AggregatedInventorySyncResult failed(
            Long logId, long durationMs, String errorMessage) {
        return new AggregatedInventorySyncResult(
                SyncStatus.FAILED, 0, 0, durationMs,
                SOURCE_SNOWFLAKE_AGG_INVENTORY, logId, errorMessage);
    }
}
