package com.ecoatm.salesplatform.service.auctions.recalc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Writes recalc status fields in a REQUIRES_NEW transaction so the status
 * survives even when the surrounding recalc tx rolls back.
 *
 * <p>Used by {@link BidRankingService} and {@link TargetPriceRecalcService}
 * for the FAILED + RUNNING-clear paths. The SUCCESS path can use the
 * surrounding REQUIRES_NEW tx (no need for a sub-tx since the tx commits).
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
        String column = process.equals("RANKING") ? "ranking" : "target_price";
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
        String column = process.equals("RANKING") ? "ranking" : "target_price";
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
        String column = process.equals("RANKING") ? "ranking" : "target_price";
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

    Instant now() { return Instant.now(); }   // package-private for test override
}
