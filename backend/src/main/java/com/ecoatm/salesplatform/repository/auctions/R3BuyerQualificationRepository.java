package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of active wholesale/data-wipe buyer-code ids that qualify
 * for round 3 of the supplied scheduling auction via the regular path.
 *
 * <p>Implements the product-owner-supplied R3 qualification rule (see design
 * §7.2): per (ecoid, grade, buyer_code) take latest bid across R1+R2,
 * compare against round3_target_price + round3_bid_rank, qualify if any of
 * three filter branches matches. All-NULL filters fall through to qualify.
 *
 * <p>Inputs from {@code auctions.bid_round_selection_filters} (round=3):
 * <ul>
 *   <li>{@code bid_percentage_variation} — whole-percent (5 = 5%).
 *       Branch: latest_bid &ge; target - (target * pct / 100)</li>
 *   <li>{@code bid_amount_variation} — flat amount.
 *       Branch: latest_bid &ge; target - amount</li>
 *   <li>{@code rank_qualification_limit} — rank ceiling.
 *       Branch: round3_bid_rank &le; limit AND rank IS NOT NULL</li>
 * </ul>
 * All three NULL → fall-through qualify (any buyer with a valid latest bid).
 */
@Repository
public class R3BuyerQualificationRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id          AS r3_sa_id,
                 sa.auction_id  AS auction_id,
                 brsf.bid_percentage_variation  AS pct_var,
                 brsf.bid_amount_variation      AS amt_var,
                 brsf.rank_qualification_limit  AS rank_lim
            FROM auctions.scheduling_auctions sa
            JOIN auctions.bid_round_selection_filters brsf
              ON brsf.round = 3
           WHERE sa.id = CAST(:r3_sa_id AS bigint)
        ),
        latest_bid AS (
          SELECT bd.ecoid,
                 bd.merged_grade,
                 bd.submitted_bid_amount,
                 bd.round3_bid_rank,
                 br.buyer_code_id,
                 ai.round3_target_price,
                 ROW_NUMBER() OVER (
                   PARTITION BY bd.ecoid, bd.merged_grade, br.buyer_code_id
                   ORDER BY bd.submitted_datetime DESC
                 ) AS rn
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id
            JOIN auctions.scheduling_auctions sa
              ON sa.id = br.scheduling_auction_id
            JOIN buyer_mgmt.buyer_codes bc   ON bc.id = br.buyer_code_id
            JOIN auctions.aggregated_inventory ai
              ON ai.id = bd.aggregated_inventory_id, params p
           WHERE sa.auction_id = p.auction_id
             AND sa.round IN (1, 2)
             AND br.submitted = TRUE
             AND bd.submitted_bid_amount > 0
             AND bc.buyer_code_type IN ('Data_Wipe','Wholesale')
        ),
        filtered_latest AS (
          SELECT * FROM latest_bid WHERE rn = 1
        )
        SELECT DISTINCT fl.buyer_code_id
          FROM filtered_latest fl, params p
         WHERE
           -- All three branches NULL → fall-through, qualify everyone
           (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
           -- Percentage branch
           OR (p.pct_var IS NOT NULL
               AND fl.submitted_bid_amount
                   >= fl.round3_target_price - (fl.round3_target_price * p.pct_var / 100))
           -- Flat-amount branch
           OR (p.amt_var IS NOT NULL
               AND fl.submitted_bid_amount >= fl.round3_target_price - p.amt_var)
           -- Rank branch (IS NOT NULL guard prevents silently excluding candidates
           -- when 4C did not write a rank for a given buyer/AE pair)
           OR (p.rank_lim IS NOT NULL
               AND fl.round3_bid_rank IS NOT NULL
               AND fl.round3_bid_rank <= p.rank_lim)
        """;

    /**
     * Returns the set of buyer-code ids that qualify for R3 via the regular path
     * for the given R3 scheduling-auction id.
     *
     * @param r3SchedulingAuctionId the id of the round-3 scheduling auction
     * @return an unordered set of qualifying buyer-code ids (never null, may be empty)
     */
    public Set<Long> qualifiedBuyerCodes(long r3SchedulingAuctionId) {
        @SuppressWarnings("unchecked")
        List<Number> rows = em.createNativeQuery(CTE_SQL)
            .setParameter("r3_sa_id", r3SchedulingAuctionId)
            .getResultList();
        Set<Long> ids = new HashSet<>(rows.size());
        for (Number n : rows) ids.add(n.longValue());
        return ids;
    }
}
