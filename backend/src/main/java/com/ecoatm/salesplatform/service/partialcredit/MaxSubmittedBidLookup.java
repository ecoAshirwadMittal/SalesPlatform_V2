package com.ecoatm.salesplatform.service.partialcredit;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Mendix parity: {@code SUB_PartialCredit_GetMaxSubmittedBid}.
 *
 * <p>Resolves the "latest price" the market paid for a received device
 * during a specific auction week. Drives
 * {@link CreditCalculationService}'s wrong-device credit formula —
 * {@code AmountToCredit = max(0, ExpectedAmountPaid - latestPrice)}.
 *
 * <p>The lookup joins {@code auctions.bid_data → auctions.bid_rounds →
 * auctions.scheduling_auctions → auctions.auctions} because
 * {@code bid_data} does not carry the {@code week_id} directly — the week
 * lives on the parent {@code auctions.auctions} row (collapsed from the
 * Mendix {@code auction_week} junction; see V58 migration comment).
 */
@Component
public class MaxSubmittedBidLookup {

    /**
     * Why JDBC rather than JPA: this is a pure aggregate. JPA would force
     * a result entity / projection class for one BigDecimal — JDBC is
     * leaner and the SQL is already cross-table.
     */
    private static final String MAX_SUBMITTED_BID_SQL = """
            SELECT MAX(bd.submitted_bid_amount)
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br ON bd.bid_round_id = br.id
            JOIN auctions.scheduling_auctions sa ON br.scheduling_auction_id = sa.id
            JOIN auctions.auctions a ON sa.auction_id = a.id
            WHERE a.week_id = ?
              AND bd.ecoid = ?
              AND bd.merged_grade = ?
              AND bd.submitted_bid_amount IS NOT NULL
              AND bd.is_deprecated = FALSE
            """;

    private final JdbcTemplate jdbc;

    public MaxSubmittedBidLookup(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Returns the max {@code submitted_bid_amount} across every
     * bid_round of the given week for the (ecoatmCode, mergedGrade)
     * tuple. Empty when no rows match.
     *
     * <p>Any of the three arguments being {@code null} or blank short-
     * circuits to {@link Optional#empty()} — the caller (credit-calc
     * engine) is responsible for handling the empty case (treats as
     * {@code latestPrice = 0} in the recommendation rule).
     */
    public Optional<BigDecimal> maxSubmittedBid(Long weekId,
                                                 String ecoatmCode,
                                                 String mergedGrade) {
        if (weekId == null || isBlank(ecoatmCode) || isBlank(mergedGrade)) {
            return Optional.empty();
        }
        try {
            BigDecimal result = jdbc.queryForObject(
                    MAX_SUBMITTED_BID_SQL,
                    BigDecimal.class,
                    weekId, ecoatmCode, mergedGrade);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
