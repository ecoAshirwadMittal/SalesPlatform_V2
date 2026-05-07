package com.ecoatm.salesplatform.service.auctions.recalc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes recalc status fields on {@code auctions.scheduling_auctions}.
 *
 * <p>Used by {@link BidRankingService} and {@link TargetPriceRecalcService}.
 * Each method's transaction propagation is chosen so the on-disk status is
 * the right answer regardless of how the surrounding recalc tx exits:
 *
 * <ul>
 *   <li>{@code tryFlipToRunning} runs in {@code REQUIRES_NEW} so the
 *       state-flip is observable to other callers immediately, before the
 *       recalc work begins.</li>
 *   <li>{@code markSuccess} runs in {@code MANDATORY} — it joins the parent
 *       recalc tx (which will commit on the success path), and a missing
 *       parent tx is a wiring bug we want to surface.</li>
 *   <li>{@code markFailed} runs in {@code REQUIRES_NEW} so the FAILED row
 *       survives even when the parent recalc tx rolls back.</li>
 * </ul>
 */
@Component
public class RecalcStatusUpdater {

    private final JdbcTemplate jdbc;

    public RecalcStatusUpdater(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Atomic state-flip from any non-RUNNING state to RUNNING. Returns true
     * if the row flipped (caller proceeds), false if it was already RUNNING
     * (caller throws RecalcAlreadyRunningException).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryFlipToRunning(long schedulingAuctionId, String process) {
        String column = columnPrefix(process);
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'RUNNING',
                   %s_started_at  = NOW(),
                   %s_finished_at = NULL,
                   %s_error       = NULL
             WHERE id = ?
               AND %s_status <> 'RUNNING'
            """.formatted(column, column, column, column, column);
        int rows = jdbc.update(sql, schedulingAuctionId);
        return rows == 1;
    }

    /**
     * Mark SUCCESS. Called from inside the recalc tx — uses MANDATORY so
     * a missing parent tx is a wiring bug.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void markSuccess(long schedulingAuctionId, String process) {
        String column = columnPrefix(process);
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'SUCCESS',
                   %s_finished_at = NOW(),
                   %s_error       = NULL
             WHERE id = ?
            """.formatted(column, column, column);
        jdbc.update(sql, schedulingAuctionId);
    }

    /**
     * Mark FAILED in a REQUIRES_NEW tx so the row survives the parent
     * recalc tx rolling back. Caller is in the catch block of the recalc
     * service and the parent tx is doomed.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(long schedulingAuctionId, String process, String errorText) {
        String column = columnPrefix(process);
        String truncated = errorText == null
            ? null
            : errorText.length() > 4000 ? errorText.substring(0, 4000) : errorText;
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'FAILED',
                   %s_finished_at = NOW(),
                   %s_error       = ?
             WHERE id = ?
            """.formatted(column, column, column);
        jdbc.update(sql, truncated, schedulingAuctionId);
    }

    /**
     * Mark SKIPPED. Sub-project 5 calls this when the config gate
     * ({@code calculate_round2_buyer_participation = false}) short-circuits
     * R2_INIT — the service is itself in REQUIRES_NEW, so MANDATORY joins
     * that tx (which will commit cleanly; no parent rollback to survive).
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void markSkipped(long schedulingAuctionId, String process) {
        String column = columnPrefix(process);
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'SKIPPED',
                   %s_finished_at = NOW(),
                   %s_error       = NULL
             WHERE id = ?
            """.formatted(column, column, column);
        jdbc.update(sql, schedulingAuctionId);
    }

    /**
     * Maps a process identifier to the schema column-prefix used in the
     * status UPDATE statements. Throws on any unrecognised value rather than
     * silently routing to {@code target_price} — a typo (e.g. "ranking"
     * lowercase) would otherwise corrupt the wrong status column.
     */
    private static String columnPrefix(String process) {
        return switch (process) {
            case "RANKING" -> "ranking";
            case "TARGET_PRICE" -> "target_price";
            case "R2_INIT" -> "r2_init";
            case "R3_INIT" -> "r3_init";
            case "R3_PREPROCESS" -> "r3_preprocess";
            case null -> throw new IllegalArgumentException(
                "process must not be null");
            default -> throw new IllegalArgumentException(
                "Unknown recalc process: " + process);
        };
    }
}
