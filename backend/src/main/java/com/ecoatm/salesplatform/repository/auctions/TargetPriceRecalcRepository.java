package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Native CTE for round-N+1 target-price recalc.
 *
 * <p>Single bulk UPDATE that, for the closed round R:
 * <ol>
 *   <li>Computes MaxBid per (ecoid, merged_grade) over closed-round bids.</li>
 *   <li>Aggregates buyer codes that hit the MaxBid (comma-joined).</li>
 *   <li>Looks up matching {@code target_price_factors} band for round R+1.</li>
 *   <li>Joins {@code auctions.reserve_bid} (4A) for EB floor.</li>
 *   <li>Joins {@code auctions.po_detail} (4B) — max price across active POs
 *       overlapping the auction week.</li>
 *   <li>Computes {@code GREATEST(MaxBid+factor, EB, PO)}; UPDATEs
 *       {@code aggregated_inventory} columns for round R + R+1.</li>
 * </ol>
 *
 * <p>Two SQL constants — R = 1 → writes round1_max_bid + round2_target_price
 * + r2_target_price_factor[_type] + round2_eb_for_target. R = 2 → writes
 * round2_max_bid + round3_target_price + r3_* + round3_eb_for_target.
 */
@Repository
public class TargetPriceRecalcRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String TARGET_PRICE_SQL_R1_TO_R2 = """
        WITH params AS (
          SELECT a.week_id AS week_id, sa.round AS closed_round
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
           WHERE sa.id = CAST(:round_id AS bigint)
        ),
        max_bids AS (
          SELECT bd.ecoid, bd.merged_grade,
                 MAX(bd.submitted_bid_amount) AS max_bid
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
                         AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount > 0
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        buyer_codes AS (
          SELECT bd.ecoid, bd.merged_grade,
                 STRING_AGG(DISTINCT bd.code, ',' ORDER BY bd.code) AS codes
            FROM auctions.bid_data bd
            JOIN max_bids mb USING (ecoid, merged_grade)
            JOIN params   p  ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount = mb.max_bid
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        factors AS (
          SELECT mb.ecoid, mb.merged_grade,
                 tpf.factor_amount, tpf.factor_type
            FROM max_bids mb
            JOIN auctions.target_price_factors tpf
              ON mb.max_bid BETWEEN tpf.minimum_value AND tpf.maximum_value
            JOIN auctions.target_price_factor_filters tpff
              ON tpff.target_price_factor_id = tpf.id
            JOIN auctions.bid_round_selection_filters brf
              ON brf.id = tpff.bid_round_selection_filter_id
            JOIN params p ON brf.round = p.closed_round + 1
        ),
        eb AS (
          SELECT product_id AS ecoid, grade AS merged_grade,
                 bid AS reserve_value
            FROM auctions.reserve_bid
        ),
        po_max AS (
          SELECT pod.product_id AS ecoid, pod.grade AS merged_grade,
                 MAX(pod.price) AS po_price
            FROM auctions.po_detail pod
            JOIN auctions.purchase_order po ON po.id = pod.purchase_order_id
            JOIN params p
              ON p.week_id BETWEEN po.week_from_id AND po.week_to_id
           GROUP BY pod.product_id, pod.grade
        ),
        evaluated AS (
          SELECT mb.ecoid, mb.merged_grade,
                 mb.max_bid,
                 bc.codes        AS max_buyer_codes,
                 f.factor_amount,
                 f.factor_type,
                 eb.reserve_value,
                 po.po_price,
                 CASE
                   WHEN f.factor_type = 'Percentage_Factor'
                     THEN ROUND(mb.max_bid * f.factor_amount / 100, 2)
                   WHEN f.factor_type = 'Flat_Amount'
                     THEN ROUND(mb.max_bid + f.factor_amount, 2)
                   ELSE mb.max_bid
                 END AS max_bid_plus_factor
            FROM max_bids   mb
            LEFT JOIN buyer_codes bc USING (ecoid, merged_grade)
            LEFT JOIN factors     f  USING (ecoid, merged_grade)
            LEFT JOIN eb              USING (ecoid, merged_grade)
            LEFT JOIN po_max      po USING (ecoid, merged_grade)
        )
        UPDATE auctions.aggregated_inventory ai
           SET round1_max_bid                = e.max_bid,
               round1_max_bid_buyer_code     = e.max_buyer_codes,
               round2_target_price           = GREATEST(
                                                 e.max_bid_plus_factor,
                                                 COALESCE(e.reserve_value, 0),
                                                 COALESCE(e.po_price, 0)
                                               ),
               r2_target_price_factor        = e.factor_amount,
               r2_target_price_factor_type   = e.factor_type,
               round2_eb_for_target          = COALESCE(e.reserve_value, 0)
          FROM evaluated e, params p
         WHERE ai.week_id = p.week_id
           AND ai.ecoid2 = e.ecoid
           AND ai.merged_grade = e.merged_grade
        """;

    private static final String TARGET_PRICE_SQL_R2_TO_R3 = """
        WITH params AS (
          SELECT a.week_id AS week_id, sa.round AS closed_round
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
           WHERE sa.id = CAST(:round_id AS bigint)
        ),
        max_bids AS (
          SELECT bd.ecoid, bd.merged_grade,
                 MAX(bd.submitted_bid_amount) AS max_bid
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
                         AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount > 0
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        buyer_codes AS (
          SELECT bd.ecoid, bd.merged_grade,
                 STRING_AGG(DISTINCT bd.code, ',' ORDER BY bd.code) AS codes
            FROM auctions.bid_data bd
            JOIN max_bids mb USING (ecoid, merged_grade)
            JOIN params   p  ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount = mb.max_bid
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        factors AS (
          SELECT mb.ecoid, mb.merged_grade,
                 tpf.factor_amount, tpf.factor_type
            FROM max_bids mb
            JOIN auctions.target_price_factors tpf
              ON mb.max_bid BETWEEN tpf.minimum_value AND tpf.maximum_value
            JOIN auctions.target_price_factor_filters tpff
              ON tpff.target_price_factor_id = tpf.id
            JOIN auctions.bid_round_selection_filters brf
              ON brf.id = tpff.bid_round_selection_filter_id
            JOIN params p ON brf.round = p.closed_round + 1
        ),
        eb AS (
          SELECT product_id AS ecoid, grade AS merged_grade, bid AS reserve_value
            FROM auctions.reserve_bid
        ),
        po_max AS (
          SELECT pod.product_id AS ecoid, pod.grade AS merged_grade,
                 MAX(pod.price) AS po_price
            FROM auctions.po_detail pod
            JOIN auctions.purchase_order po ON po.id = pod.purchase_order_id
            JOIN params p
              ON p.week_id BETWEEN po.week_from_id AND po.week_to_id
           GROUP BY pod.product_id, pod.grade
        ),
        evaluated AS (
          SELECT mb.ecoid, mb.merged_grade,
                 mb.max_bid,
                 bc.codes        AS max_buyer_codes,
                 f.factor_amount,
                 f.factor_type,
                 eb.reserve_value,
                 po.po_price,
                 CASE
                   WHEN f.factor_type = 'Percentage_Factor'
                     THEN ROUND(mb.max_bid * f.factor_amount / 100, 2)
                   WHEN f.factor_type = 'Flat_Amount'
                     THEN ROUND(mb.max_bid + f.factor_amount, 2)
                   ELSE mb.max_bid
                 END AS max_bid_plus_factor
            FROM max_bids   mb
            LEFT JOIN buyer_codes bc USING (ecoid, merged_grade)
            LEFT JOIN factors     f  USING (ecoid, merged_grade)
            LEFT JOIN eb              USING (ecoid, merged_grade)
            LEFT JOIN po_max      po USING (ecoid, merged_grade)
        )
        UPDATE auctions.aggregated_inventory ai
           SET round2_max_bid                = e.max_bid,
               round2_max_bid_buyer_code     = e.max_buyer_codes,
               round3_target_price           = GREATEST(
                                                 e.max_bid_plus_factor,
                                                 COALESCE(e.reserve_value, 0),
                                                 COALESCE(e.po_price, 0)
                                               ),
               r3_target_price_factor        = e.factor_amount,
               r3_target_price_factor_type   = e.factor_type,
               round3_eb_for_target          = COALESCE(e.reserve_value, 0)
          FROM evaluated e, params p
         WHERE ai.week_id = p.week_id
           AND ai.ecoid2 = e.ecoid
           AND ai.merged_grade = e.merged_grade
        """;

    @Transactional(propagation = Propagation.MANDATORY)
    public int recalcClosedRound(long schedulingAuctionId, int closedRound) {
        String sql = switch (closedRound) {
            case 1 -> TARGET_PRICE_SQL_R1_TO_R2;
            case 2 -> TARGET_PRICE_SQL_R2_TO_R3;
            default -> throw new IllegalArgumentException(
                "closedRound must be 1 or 2: was " + closedRound);
        };
        Query q = em.createNativeQuery(sql);
        q.setParameter("round_id", schedulingAuctionId);
        return q.executeUpdate();
    }
}
