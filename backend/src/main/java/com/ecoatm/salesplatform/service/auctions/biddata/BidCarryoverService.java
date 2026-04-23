package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.CarryoverResult;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carryover service — copies the previous week's submitted bid values into the
 * current round's {@code bid_quantity} / {@code bid_amount} columns.
 *
 * <p><b>SQL strategy:</b> find the most-recent prior {@code bid_rounds} row
 * that shares the same {@code (buyer_code_id, round)} but belongs to a
 * different auction (i.e. a different week). Join that row's {@code bid_data}
 * slice to the current slice on {@code (buyer_code_id, ecoid, merged_grade)}.
 * Where a prior-row match exists AND has a non-null submitted amount, copy
 * {@code submitted_bid_quantity} → {@code bid_quantity} and
 * {@code submitted_bid_amount} → {@code bid_amount} on the current row.
 *
 * <p><b>Idempotency:</b> calling this endpoint twice produces the same result
 * because the UPDATE always overwrites from the prior week's values, not from
 * the current values.
 *
 * <p><b>Advisory lock:</b> same {@code hashtext('bid_data_gen')} namespace used
 * by {@link BidDataCreationService}, keyed by {@code bid_round_id}, serialises
 * concurrent carryover calls on the same round. The {@code (int)} narrowing cast
 * is intentional — see {@link BidDataCreationService} for the Postgres overload
 * rationale.
 *
 * <p><b>Ownership check:</b> mirrors {@link BidDataSubmissionService} — an
 * Administrator bypasses the check; a Bidder must own the buyer code via the
 * {@code user_mgmt.user_buyers} → {@code buyer_mgmt.buyer_code_buyers} join.
 */
@Service
public class BidCarryoverService {

    private static final Logger log = LoggerFactory.getLogger(BidCarryoverService.class);

    private static final int TX_TIMEOUT_SECONDS = 20;

    /**
     * Ownership check: 2-hop via user_buyers → buyer_code_buyers.
     * Duplicated from BidDataSubmissionService — see the comment there.
     */
    private static final String OWNERSHIP_SQL = """
            SELECT COUNT(*) FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_id = ub.buyer_id
            WHERE ub.user_id = ? AND bcb.buyer_code_id = ?
            """;

    /**
     * Find the {@code (round_number, prev_week_display)} of the most-recent
     * prior bid round for the same buyer code + round number, from a different
     * scheduling auction (= a different week's auction).
     *
     * Returns one row: {@code (prev_bid_round_id, week_display)} or empty.
     *
     * The {@code JOIN auctions.auctions a ON a.id = sa.auction_id}
     * → {@code JOIN mdm.week w ON w.id = a.week_id} chain resolves the
     * human-readable week display (e.g. "2026 / Wk15") for the success modal.
     *
     * We exclude the current bid round's scheduling_auction_id to ensure we
     * don't copy from the same week.
     */
    private static final String FIND_PREV_ROUND_SQL = """
            SELECT br_prev.id AS prev_bid_round_id, w.week_display
              FROM auctions.bid_rounds br_curr
              JOIN auctions.scheduling_auctions sa_curr
                ON sa_curr.id = br_curr.scheduling_auction_id
              -- find another bid_round with same buyer_code + same round number
              JOIN auctions.bid_rounds br_prev
                ON  br_prev.buyer_code_id = br_curr.buyer_code_id
                AND br_prev.scheduling_auction_id <> br_curr.scheduling_auction_id
              JOIN auctions.scheduling_auctions sa_prev
                ON  sa_prev.id = br_prev.scheduling_auction_id
                AND sa_prev.round = sa_curr.round
              JOIN auctions.auctions a ON a.id = sa_prev.auction_id
              JOIN mdm.week w          ON w.id  = a.week_id
             WHERE br_curr.id = ?
               -- only prior weeks (week starts before current week's start)
               AND w.week_start_datetime < (
                     SELECT w2.week_start_datetime
                       FROM auctions.auctions a2
                       JOIN mdm.week w2 ON w2.id = a2.week_id
                      WHERE a2.id = sa_curr.auction_id
                   )
             ORDER BY w.week_start_datetime DESC
             LIMIT 1
            """;

    /**
     * Copy submitted_bid_quantity / submitted_bid_amount from the previous
     * week's bid_data slice into the current slice, matching on
     * (buyer_code_id, ecoid, merged_grade).
     *
     * Only copies rows where the previous row has a non-null submitted amount
     * OR a non-null submitted quantity — i.e. rows that the bidder actually
     * submitted previously. Returns the number of rows updated.
     */
    private static final String CARRYOVER_UPDATE_SQL = """
            UPDATE auctions.bid_data AS curr
               SET bid_quantity  = prev.submitted_bid_quantity,
                   bid_amount    = COALESCE(prev.submitted_bid_amount, 0),
                   changed_date  = NOW()
              FROM auctions.bid_data AS prev
             WHERE curr.bid_round_id    = ?
               AND curr.buyer_code_id   = ?
               AND prev.bid_round_id    = ?
               AND prev.buyer_code_id   = curr.buyer_code_id
               AND prev.ecoid           = curr.ecoid
               AND prev.merged_grade    = curr.merged_grade
               AND (prev.submitted_bid_quantity IS NOT NULL
                    OR (prev.submitted_bid_amount IS NOT NULL
                        AND prev.submitted_bid_amount > 0))
            """;

    private final BidRoundRepository bidRoundRepo;
    private final JdbcTemplate jdbc;

    public BidCarryoverService(BidRoundRepository bidRoundRepo, JdbcTemplate jdbc) {
        this.bidRoundRepo = bidRoundRepo;
        this.jdbc = jdbc;
    }

    /**
     * Execute the carryover for the given bid round + buyer code.
     *
     * @param userId      authenticated user id (for ownership check)
     * @param bidRoundId  current bid round id
     * @param buyerCodeId buyer code to scope the carryover to
     * @return counts of copied vs not-found rows + prev week display string
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = TX_TIMEOUT_SECONDS)
    public CarryoverResult carryover(long userId, long bidRoundId, long buyerCodeId) {
        assertOwnership(userId, buyerCodeId);

        BidRound round = bidRoundRepo.findById(bidRoundId)
                .orElseThrow(() -> new BidDataSubmissionException("BID_ROUND_NOT_FOUND",
                        "Bid round not found: " + bidRoundId));

        if ("Closed".equals(round.getRoundStatus())) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }

        // Serialize concurrent carryover calls on the same (round) — same advisory
        // lock namespace as BidDataCreationService; keyed only on bidRoundId here.
        // See BidDataCreationService for the (int) cast rationale.
        jdbc.queryForObject(
                "SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)",
                Object.class, (int) bidRoundId);

        // Locate the most-recent prior bid round for the same (buyer_code, round).
        Long prevBidRoundId = null;
        String prevWeekDisplay = null;
        var rows = jdbc.queryForList(FIND_PREV_ROUND_SQL, bidRoundId);
        if (!rows.isEmpty()) {
            var row = rows.get(0);
            Object idObj = row.get("prev_bid_round_id");
            if (idObj instanceof Number n) {
                prevBidRoundId = n.longValue();
            }
            Object wdObj = row.get("week_display");
            if (wdObj instanceof String s) {
                prevWeekDisplay = s;
            }
        }

        if (prevBidRoundId == null) {
            // No prior week found — return empty result so the frontend shows
            // the "no bids from last week" empty-state modal.
            log.info("carryover bidRoundId={} buyerCodeId={} — no prior week found",
                    bidRoundId, buyerCodeId);
            return new CarryoverResult(0, 0, null);
        }

        int copied = jdbc.update(CARRYOVER_UPDATE_SQL,
                bidRoundId, buyerCodeId, prevBidRoundId);

        // Count rows that were NOT matched (current rows that had no prior-week
        // counterpart or whose prior row had no submitted values).
        int total = countCurrentRows(bidRoundId, buyerCodeId);
        int notFound = Math.max(0, total - copied);

        log.info("carryover bidRoundId={} buyerCodeId={} prevBidRoundId={} prevWeek='{}' copied={} notFound={}",
                bidRoundId, buyerCodeId, prevBidRoundId, prevWeekDisplay, copied, notFound);

        return new CarryoverResult(copied, notFound, prevWeekDisplay);
    }

    private int countCurrentRows(long bidRoundId, long buyerCodeId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ? AND buyer_code_id = ?",
                Integer.class, bidRoundId, buyerCodeId);
        return count != null ? count : 0;
    }

    private void assertOwnership(long userId, long buyerCodeId) {
        if (hasAdministratorRole()) {
            return;
        }
        Long count = jdbc.queryForObject(OWNERSHIP_SQL, Long.class, userId, buyerCodeId);
        if (count == null || count == 0L) {
            throw new BidDataSubmissionException("NOT_YOUR_BID_ROUND",
                    "User does not own buyer_code_id=" + buyerCodeId);
        }
    }

    private static boolean hasAdministratorRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        for (var authority : auth.getAuthorities()) {
            if ("ROLE_Administrator".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
