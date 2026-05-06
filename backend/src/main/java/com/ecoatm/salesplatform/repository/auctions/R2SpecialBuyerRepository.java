package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of buyer-code ids whose owning special-buyer ALL of
 * whose DW/Wholesale codes qualify as "special-treatment" (STB) for the
 * supplied R2 scheduling auction. Ports the Mendix
 * {@code SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe} +
 * {@code SUB_IsSpecialTreatmentBuyer} flow as one Postgres CTE.
 *
 * <p>A code is "STB" iff
 * {@code stb_allow_all_buyers_override = TRUE} OR the buyer has zero
 * submitted bids for that code in any round prior to the current round.
 * A buyer is "all-codes STB" iff {@code bool_and(is_stb)} across their
 * DW/WH codes is TRUE — see design §3.7's "all codes must qualify"
 * semantic. Only those buyers' codes appear in the result.
 *
 * <p>Non-special buyers ({@code is_special_buyer = FALSE}) are filtered
 * upstream in {@code special_buyers} and never reach the downstream
 * aggregate.
 */
@Repository
public class R2SpecialBuyerRepository {

    @PersistenceContext private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT
            sa.id                  AS scheduling_auction_id,
            sa.auction_id          AS auction_id,
            sa.round               AS round,
            brsf.stb_allow_all_buyers_override
          FROM auctions.scheduling_auctions sa
          JOIN auctions.bid_round_selection_filters brsf
            ON brsf.round = sa.round
          WHERE sa.id = CAST(:r2_sa_id AS bigint)
        ),
        special_buyers AS (
          -- All buyers with is_special_buyer = TRUE
          SELECT b.id AS buyer_id
            FROM buyer_mgmt.buyers b
           WHERE b.is_special_buyer = TRUE
        ),
        special_dwwh_codes AS (
          -- Their DW/WH buyer codes, joined to active codes
          SELECT bcb.buyer_id, bc.id AS buyer_code_id
            FROM buyer_mgmt.buyer_code_buyers bcb
            JOIN special_buyers sb ON sb.buyer_id = bcb.buyer_id
            JOIN buyer_mgmt.buyer_codes bc
              ON bc.id = bcb.buyer_code_id
             AND bc.buyer_code_type IN ('Wholesale','Data_Wipe')
        ),
        prior_round_bids AS (
          -- Count of submitted bids by (buyer, code) in any round prior to current
          SELECT bd.buyer_code_id, bcb2.buyer_id, COUNT(*) AS bid_count
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id AND br.submitted = TRUE
            JOIN auctions.scheduling_auctions sa
              ON sa.id = br.scheduling_auction_id
            JOIN params p                    ON p.auction_id = sa.auction_id AND sa.round < p.round
            JOIN buyer_mgmt.buyer_code_buyers bcb2
              ON bcb2.buyer_code_id = bd.buyer_code_id
           GROUP BY bd.buyer_code_id, bcb2.buyer_id
        ),
        code_is_stb AS (
          -- A code is special-treatment iff override OR no prior-round bids by this buyer
          SELECT s.buyer_id, s.buyer_code_id,
                 CASE
                   WHEN p.stb_allow_all_buyers_override = TRUE THEN TRUE
                   WHEN COALESCE(prb.bid_count, 0) = 0          THEN TRUE
                   ELSE FALSE
                 END AS is_stb
            FROM special_dwwh_codes s
            LEFT JOIN prior_round_bids prb
              ON prb.buyer_id = s.buyer_id
             AND prb.buyer_code_id = s.buyer_code_id
            CROSS JOIN params p
        ),
        buyers_all_codes_stb AS (
          -- A buyer is "stb-eligible" iff ALL their DW/WH codes are STB
          SELECT buyer_id
            FROM code_is_stb
           GROUP BY buyer_id
          HAVING bool_and(is_stb) = TRUE
        )
        SELECT cs.buyer_code_id
          FROM code_is_stb cs
          JOIN buyers_all_codes_stb bs ON bs.buyer_id = cs.buyer_id
        """;

    public Set<Long> specialTreatmentBuyerCodes(long r2SchedulingAuctionId) {
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
