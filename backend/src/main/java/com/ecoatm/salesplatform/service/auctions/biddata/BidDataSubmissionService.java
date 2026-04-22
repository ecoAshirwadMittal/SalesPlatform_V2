package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidSubmissionResult;
import com.ecoatm.salesplatform.dto.SaveBidRequest;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Save + submit engine for bidder-entered bid data. Ports Mendix
 * {@code SUB_SaveBidData} and {@code ACT_SubmitBids}.
 *
 * <p>Two scenarios are load-bearing:
 *
 * <ul>
 *   <li><b>save</b> — single-row update of {@code bid_quantity} /
 *       {@code bid_amount}. Validates ownership (the editor must belong to
 *       the row's buyer code), the round is open, and the quantity is in
 *       {@code [0, maximum_quantity]}. Null quantity is allowed (parity with
 *       Mendix — a bidder can clear an entry without entering a value).
 *   <li><b>submit</b> — bulk update of every {@code bid_data} row for the
 *       given round + buyer code, sliding the previous {@code submitted_*}
 *       values into {@code last_valid_*} before overwriting them with the
 *       current {@code bid_*} values. The round itself is then flipped to
 *       {@code submitted = TRUE}. Postgres evaluates the right-hand side of
 *       every {@code SET} clause against the row's pre-UPDATE state, so
 *       reading {@code submitted_bid_quantity} into {@code last_valid_bid_quantity}
 *       in the same statement is safe.
 * </ul>
 *
 * <p><b>Per-buyer scope on submit:</b> the spec's UPDATE filtered only by
 * {@code bid_round_id}, but a bid round contains rows for every qualified
 * buyer code. Without {@code AND buyer_code_id = ?} a submit by buyer A
 * would silently flip rows belonging to buyer B. The signature therefore
 * takes an explicit {@code buyerCodeId} that the controller will resolve
 * from the authenticated user's active buyer code.
 */
@Service
public class BidDataSubmissionService {

    /**
     * 2-hop ownership query. The actual schema is
     * {@code user_mgmt.user_buyers (user_id, buyer_id)} +
     * {@code buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)} —
     * there is no direct {@code (user_id, buyer_code_id)} column. A user
     * owns a buyer code iff at least one shared {@code buyer_id} exists.
     */
    private static final String OWNERSHIP_SQL = """
            SELECT COUNT(*) FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_id = ub.buyer_id
            WHERE ub.user_id = ? AND bcb.buyer_code_id = ?
            """;

    private static final String ROUND_STATUS_SQL =
            "SELECT round_status FROM auctions.bid_rounds WHERE id = ?";

    private static final String RESOLVE_BID_ROUND_SQL =
            "SELECT bid_round_id FROM auctions.bid_data WHERE id = ?";

    private static final String SUBMIT_BID_DATA_SQL = """
            UPDATE auctions.bid_data SET
                last_valid_bid_quantity = submitted_bid_quantity,
                last_valid_bid_amount   = submitted_bid_amount,
                submitted_bid_quantity  = bid_quantity,
                submitted_bid_amount    = bid_amount,
                submitted_datetime      = NOW(),
                submit_user             = ?,
                changed_date            = NOW(),
                changed_by_id           = ?
            WHERE bid_round_id = ? AND buyer_code_id = ?
            """;

    private static final String SUBMIT_BID_ROUND_SQL = """
            UPDATE auctions.bid_rounds SET
                submitted            = TRUE,
                submitted_datetime   = NOW(),
                submitted_by_user_id = ?,
                changed_date         = NOW()
            WHERE id = ?
            """;

    private final BidDataRepository bidDataRepo;
    private final BidRoundRepository bidRoundRepo;
    private final JdbcTemplate jdbc;

    public BidDataSubmissionService(BidDataRepository bidDataRepo,
                                    BidRoundRepository bidRoundRepo,
                                    JdbcTemplate jdbc) {
        this.bidDataRepo = bidDataRepo;
        this.bidRoundRepo = bidRoundRepo;
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 10)
    public BidDataRow save(long userId, long bidDataId, SaveBidRequest req) {
        BidData row = bidDataRepo.findById(bidDataId)
                .orElseThrow(() -> new BidDataSubmissionException("BID_DATA_NOT_FOUND",
                        "Bid data not found: " + bidDataId));

        assertOwnership(userId, row.getBuyerCodeId());
        assertRoundOpen(row.getBidRoundId());
        validateAmountAndQuantity(req, row.getMaximumQuantity());

        row.setBidQuantity(req.bidQuantity());
        row.setBidAmount(req.bidAmount());
        row.setChangedDate(Instant.now());
        row.setChangedById(userId);
        BidData saved = bidDataRepo.save(row);

        return toDto(saved);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 15)
    public BidSubmissionResult submit(long userId, String username, long bidRoundId, long buyerCodeId) {
        // IDOR guard: assert before any DB read or write. The round-level
        // submitted flag would otherwise flip even when the per-buyer-code
        // bid_data UPDATE wrote 0 rows.
        assertOwnership(userId, buyerCodeId);

        BidRound round = bidRoundRepo.findById(bidRoundId)
                .orElseThrow(() -> new BidDataSubmissionException("BID_ROUND_NOT_FOUND",
                        "Bid round not found: " + bidRoundId));

        if ("Closed".equals(round.getRoundStatus())) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }

        boolean isResubmit = round.getSubmitted();

        int rowsAffected = jdbc.update(SUBMIT_BID_DATA_SQL,
                username, userId, bidRoundId, buyerCodeId);

        jdbc.update(SUBMIT_BID_ROUND_SQL, userId, bidRoundId);

        return new BidSubmissionResult(bidRoundId, rowsAffected, Instant.now(), isResubmit);
    }

    /**
     * Resolve the {@code bid_round_id} for a given {@code bid_data} row.
     * Used by the controller layer to drive the per-(user, round)
     * rate-limit bucket without forcing the client to send the round id
     * on every save call.
     *
     * @throws BidDataSubmissionException with code {@code BID_DATA_NOT_FOUND}
     *         when no row exists for the id.
     */
    @Transactional(readOnly = true, timeout = 5)
    public long resolveBidRoundId(long bidDataId) {
        try {
            Long bidRoundId = jdbc.queryForObject(RESOLVE_BID_ROUND_SQL, Long.class, bidDataId);
            if (bidRoundId == null) {
                throw new BidDataSubmissionException("BID_DATA_NOT_FOUND",
                        "Bid data not found: " + bidDataId);
            }
            return bidRoundId;
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            throw new BidDataSubmissionException("BID_DATA_NOT_FOUND",
                    "Bid data not found: " + bidDataId);
        }
    }

    private void assertOwnership(long userId, long buyerCodeId) {
        Long count = jdbc.queryForObject(OWNERSHIP_SQL, Long.class, userId, buyerCodeId);
        if (count == null || count == 0L) {
            throw new BidDataSubmissionException("NOT_YOUR_BID_DATA",
                    "User does not own buyer_code_id=" + buyerCodeId);
        }
    }

    private void assertRoundOpen(long bidRoundId) {
        String status = jdbc.queryForObject(ROUND_STATUS_SQL, String.class, bidRoundId);
        if ("Closed".equals(status)) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }
    }

    private static void validateAmountAndQuantity(SaveBidRequest req, Integer maximumQuantity) {
        if (req.bidAmount() == null || req.bidAmount().signum() < 0) {
            throw new BidDataValidationException("INVALID_AMOUNT",
                    "bidAmount must be non-null and >= 0");
        }
        if (req.bidQuantity() != null) {
            if (req.bidQuantity() < 0) {
                throw new BidDataValidationException("INVALID_QUANTITY",
                        "bidQuantity must be >= 0");
            }
            if (maximumQuantity != null && req.bidQuantity() > maximumQuantity) {
                throw new BidDataValidationException("INVALID_QUANTITY",
                        "bidQuantity exceeds maximum_quantity=" + maximumQuantity);
            }
        }
    }

    private static BidDataRow toDto(BidData r) {
        return new BidDataRow(
                r.getId(),
                r.getBidRoundId(),
                r.getEcoid(),
                r.getMergedGrade(),
                r.getBuyerCodeType(),
                r.getBidQuantity(),
                r.getBidAmount(),
                r.getTargetPrice(),
                r.getMaximumQuantity(),
                r.getPayout(),
                r.getSubmittedBidQuantity(),
                r.getSubmittedBidAmount(),
                r.getLastValidBidQuantity(),
                r.getLastValidBidAmount(),
                r.getSubmittedDatetime(),
                r.getChangedDate());
    }
}
