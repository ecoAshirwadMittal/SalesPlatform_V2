package com.ecoatm.salesplatform.service.rma;

/**
 * Writes an RMA snapshot to Snowflake — the modern seam replacing the legacy
 * {@code SUB_SendRMADetailsToSnowflake} JDBC call.
 *
 * <p>Two implementations are selected by {@code rma.sync.writer}, mirroring the
 * PO / recalc Snowflake writer pairs:
 * <ul>
 *   <li>{@link LoggingRmaSnowflakeWriter} — default ({@code logging} or unset);
 *       a no-op that logs the row it <em>would</em> push. Keeps dev / test off a
 *       live Snowflake DataSource.</li>
 *   <li>{@link JdbcRmaSnowflakeWriter} — prod ({@code jdbc}); calls the
 *       {@code AUCTIONS.UPSERT_RMA_DATA(?)} stored procedure.</li>
 * </ul>
 */
public interface RmaSnowflakeWriter {

    /**
     * Push a single RMA snapshot to Snowflake. Implementations must be safe to
     * call off the request thread and may throw on infrastructure failure — the
     * caller ({@code RmaSnowflakePushListener}) swallows and logs any exception
     * so a failed push never affects the already-committed review.
     */
    void push(RmaSnowflakePayload payload);
}
