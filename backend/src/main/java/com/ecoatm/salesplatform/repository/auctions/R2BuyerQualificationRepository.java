package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of active wholesale/data-wipe buyer-code ids that
 * qualify for round 2 of the supplied scheduling auction. Implements
 * the per-AE Mendix predicate from
 * {@code SUB_Round2AggregatedInventorySingleItem} as one Postgres CTE.
 *
 * <p>The CTE uses the new-stack two-value enums introduced in V59
 * ({@code RegularBuyerQualification ∈ {Only_Qualified, All_Buyers}},
 * {@code RegularBuyerInventoryOption ∈ {InventoryRound1QualifiedBids,
 * ShowAllInventory}}) — see design §3.5b. CHECK constraints on
 * {@code auctions.bid_round_selection_filters} prevent any other value
 * from existing at row-write time, so the CTE has no defensive null/unknown
 * handling.
 *
 * <p>The active-codes universe and the bid-data join both restrict to
 * {@code ('Wholesale', 'Data_Wipe')} only — Purchasing_Order variants
 * are a separate flow (decision 3.5c).
 */
@Repository
public class R2BuyerQualificationRepository {

    @PersistenceContext private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id AS scheduling_auction_id,
                 sa.auction_id, a.week_id, sa.round,
                 brsf.regular_buyer_qualification     AS qual_mode,
                 brsf.regular_buyer_inventory_options AS inv_mode,
                 brsf.target_percent                  AS target_pct,
                 brsf.target_value                    AS target_val
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
            JOIN auctions.bid_round_selection_filters brsf ON brsf.round = sa.round
           WHERE sa.id = CAST(:r2_sa_id AS bigint)
        ),
        prior_sa AS (
          SELECT sa.id AS prev_sa_id
            FROM auctions.scheduling_auctions sa, params p
           WHERE sa.auction_id = p.auction_id
             AND sa.round = p.round - 1
        ),
        active_codes AS (
          -- Auction-bid types only — see design decision 3.5c.
          SELECT bc.id AS buyer_code_id, bc.buyer_code_type
            FROM buyer_mgmt.buyer_codes bc
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
            JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
           WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
             AND b.status = 'Active'
        ),
        r1_bids AS (
          SELECT bd.buyer_code_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS bid_amount,
                 CASE bd.buyer_code_type
                   WHEN 'Data_Wipe' THEN ai.dw_avg_target_price
                   ELSE                  ai.avg_target_price
                 END AS r1_target_price
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id AND br.submitted = TRUE
            JOIN prior_sa psa           ON psa.prev_sa_id = br.scheduling_auction_id
            JOIN auctions.aggregated_inventory ai
              ON ai.ecoid2 = bd.ecoid AND ai.merged_grade = bd.merged_grade
             AND ai.week_id = (SELECT week_id FROM params)
           WHERE bd.submitted_bid_amount > 0
        ),
        qualifies_per_ae AS (
          -- Only_Qualified predicate cascade. The new-stack enums (V59) are 2-value
          -- only — RegularBuyerQualification ∈ {All_Buyers, Only_Qualified} and
          -- RegularBuyerInventoryOption ∈ {InventoryRound1QualifiedBids, ShowAllInventory}.
          -- All_Buyers is handled separately in the UNION below — never reaches here.
          SELECT r.buyer_code_id,
                 CASE
                   WHEN r.r1_target_price = 0 AND r.bid_amount > 0 THEN TRUE
                   WHEN r.r1_target_price > 0
                        AND r.bid_amount / r.r1_target_price >= 1 - p.target_pct THEN TRUE
                   WHEN (r.r1_target_price - r.bid_amount) <= p.target_val THEN TRUE
                   WHEN p.inv_mode = 'InventoryRound1QualifiedBids' AND r.bid_amount > 0 THEN TRUE
                   WHEN p.inv_mode = 'ShowAllInventory' THEN TRUE
                   ELSE FALSE
                 END AS qualifies
            FROM r1_bids r, params p
           WHERE p.qual_mode = 'Only_Qualified'
        )
        SELECT ac.buyer_code_id
          FROM active_codes ac, params p
         WHERE p.qual_mode = 'All_Buyers'
        UNION
        SELECT q.buyer_code_id FROM qualifies_per_ae q WHERE q.qualifies = TRUE
        """;

    public Set<Long> qualifiedBuyerCodes(long r2SchedulingAuctionId) {
        @SuppressWarnings("unchecked")
        List<Number> rows = em.createNativeQuery(CTE_SQL)
            .setParameter("r2_sa_id", r2SchedulingAuctionId)
            .getResultList();
        Set<Long> ids = new HashSet<>(rows.size());
        for (Number n : rows) {
            ids.add(n.longValue());
        }
        return ids;
    }
}
