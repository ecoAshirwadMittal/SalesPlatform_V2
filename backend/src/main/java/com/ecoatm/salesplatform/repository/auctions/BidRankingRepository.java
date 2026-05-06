package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Native CTE for round-N+1 bid rank computation.
 *
 * <p>Ports Mendix {@code ACT_TriggerBidRankingCalculation}. Single bulk
 * UPDATE with DENSE_RANK over (ecoid, merged_grade) ORDER BY
 * submitted_bid_amount DESC. The reserve-floor branch is selected by
 * {@code auctions.bid_ranking_config.include_reserve_floor}.
 *
 * <p>Two SQL constants because the round-suffix columns must be statically
 * spelled — Postgres does not have dynamic column references in UPDATE
 * SET targets. R = 1 → writes round2 columns; R = 2 → writes round3 columns.
 *
 * <p>{@code @Transactional(propagation = MANDATORY)} — caller (BidRankingService)
 * already owns the REQUIRES_NEW boundary.
 */
@Repository
public class BidRankingRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String RANKING_SQL_R2 = """
        WITH params AS (
          SELECT
            sa.id            AS scheduling_auction_id,
            a.week_id        AS week_id,
            sa.round         AS closed_round,
            cfg.minimum_bid           AS min_bid,
            cfg.display_rank          AS min_display_rank,
            cfg.maximum_rank          AS max_display_rank,
            cfg.include_reserve_floor AS include_eb
          FROM auctions.scheduling_auctions sa
          JOIN auctions.auctions a ON a.id = sa.auction_id
          CROSS JOIN auctions.bid_ranking_config cfg
          WHERE sa.id = CAST(:round_id AS bigint)
        ),
        candidates AS (
          SELECT bd.id AS bid_data_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS amount
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
           WHERE bd.bid_round = p.closed_round
             AND bd.submitted_bid_amount >= p.min_bid

          UNION ALL

          SELECT NULL::bigint AS bid_data_id, rb.product_id AS ecoid,
                 rb.grade AS merged_grade, rb.bid AS amount
            FROM auctions.reserve_bid rb
            JOIN params p ON p.include_eb = TRUE
        ),
        ranked AS (
          SELECT bid_data_id,
                 DENSE_RANK() OVER (
                   PARTITION BY ecoid, merged_grade
                   ORDER BY amount DESC
                 ) AS calc_rank
            FROM candidates
        )
        UPDATE auctions.bid_data target
           SET round2_bid_rank         = r.calc_rank,
               display_round2_bid_rank = CASE
                 WHEN r.calc_rank BETWEEN p.min_display_rank AND p.max_display_rank
                   THEN r.calc_rank
                 ELSE NULL
               END
          FROM ranked r, params p
         WHERE r.bid_data_id IS NOT NULL
           AND target.id = r.bid_data_id
        """;

    private static final String RANKING_SQL_R3 = """
        WITH params AS (
          SELECT
            sa.id            AS scheduling_auction_id,
            a.week_id        AS week_id,
            sa.round         AS closed_round,
            cfg.minimum_bid           AS min_bid,
            cfg.display_rank          AS min_display_rank,
            cfg.maximum_rank          AS max_display_rank,
            cfg.include_reserve_floor AS include_eb
          FROM auctions.scheduling_auctions sa
          JOIN auctions.auctions a ON a.id = sa.auction_id
          CROSS JOIN auctions.bid_ranking_config cfg
          WHERE sa.id = CAST(:round_id AS bigint)
        ),
        candidates AS (
          SELECT bd.id AS bid_data_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS amount
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
           WHERE bd.bid_round = p.closed_round
             AND bd.submitted_bid_amount >= p.min_bid

          UNION ALL

          SELECT NULL::bigint AS bid_data_id, rb.product_id AS ecoid,
                 rb.grade AS merged_grade, rb.bid AS amount
            FROM auctions.reserve_bid rb
            JOIN params p ON p.include_eb = TRUE
        ),
        ranked AS (
          SELECT bid_data_id,
                 DENSE_RANK() OVER (
                   PARTITION BY ecoid, merged_grade
                   ORDER BY amount DESC
                 ) AS calc_rank
            FROM candidates
        )
        UPDATE auctions.bid_data target
           SET round3_bid_rank         = r.calc_rank,
               display_round3_bid_rank = CASE
                 WHEN r.calc_rank BETWEEN p.min_display_rank AND p.max_display_rank
                   THEN r.calc_rank
                 ELSE NULL
               END
          FROM ranked r, params p
         WHERE r.bid_data_id IS NOT NULL
           AND target.id = r.bid_data_id
        """;

    /**
     * Runs the DENSE_RANK UPDATE for the round that just closed.
     *
     * @param schedulingAuctionId scheduling_auctions.id of the closed round
     * @param closedRound         1 or 2; throws IAE otherwise
     * @return number of bid_data rows updated
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int rankClosedRound(long schedulingAuctionId, int closedRound) {
        String sql = switch (closedRound) {
            case 1 -> RANKING_SQL_R2;
            case 2 -> RANKING_SQL_R3;
            default -> throw new IllegalArgumentException(
                "closedRound must be 1 or 2: was " + closedRound);
        };
        Query q = em.createNativeQuery(sql);
        q.setParameter("round_id", schedulingAuctionId);
        return q.executeUpdate();
    }
}
