package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single-CTE generator for {@code auctions.bid_data} rows.
 *
 * <p>Ports Mendix {@code SUB_CreateBidData} as a single Postgres CTE that:
 * <ol>
 *   <li>Resolves the bid round, scheduling auction, week and buyer-code
 *       parameters into a {@code params} CTE.</li>
 *   <li>Short-circuits when {@code auctions.bid_data} already has rows for
 *       this {@code bid_round_id} (idempotency guard — second call returns
 *       0 rows inserted).</li>
 *   <li>Filters {@code aggregated_inventory} for the week, excluding
 *       deprecated rows and rows with no relevant quantity for the
 *       buyer-code branch (DW vs Wholesale).</li>
 *   <li>Joins to the prior round's bid_data (if any) for the carry-forward
 *       columns ({@code previous_round_bid_quantity},
 *       {@code previous_round_bid_amount}).</li>
 *   <li>Inserts one {@code auctions.bid_data} row per qualified inventory
 *       line.</li>
 * </ol>
 *
 * <p>{@code @Transactional(propagation = MANDATORY)} is required by the
 * caller contract — the orchestrating service must already hold a
 * transaction. Under {@code @DataJpaTest} the outer test transaction
 * satisfies this.
 *
 * <p>The R2/R3 per-row threshold cascade and STB shortcut were ported from
 * Mendix {@code SUB_Round2AggregatedInventorySingleItem} and the per-row form
 * of sub-project 6's R3 selection rule (sub-project 5b, 2026-05-07).
 */
@Repository
public class BidDataCreationRepository {

    @PersistenceContext
    private EntityManager em;

    /*
     * Notes on schema deltas vs the original plan (`auction-bid-data-create-plan.md`
     * lines 1133-1273):
     *
     *   • The plan derives the buyer-code DW flag from a non-existent
     *     `bc.data_wipe_approved` column. The actual schema (V8) only has
     *     `bc.buyer_code_type IN ('Wholesale','Data_Wipe',
     *     'Purchasing_Order_Data_Wipe','Purchasing_Order')`. We collapse the
     *     two DW variants into the 'DW' branch.
     *
     *   • The plan joins `LEFT JOIN buyer_mgmt.buyers b ON b.id = bc.buyer_id`
     *     but `buyer_codes.buyer_id` does not exist — the buyer↔buyer_code
     *     relationship is the M:M junction `buyer_mgmt.buyer_code_buyers`.
     *     We pick any buyer (LIMIT 1) for the denormalized
     *     {@code bid_data.company_name} column. Mendix exhibits the same
     *     fan-out — first-buyer-wins is the conventional reading.
     */
    /*
     * Postgres `::type` cast syntax collides with Hibernate's named-parameter
     * tokenizer (`:foo` is treated as a parameter, so `:bid_round_id::bigint`
     * registers a phantom `bigint` parameter). Use ANSI `CAST(... AS bigint)`
     * instead — it's portable and Hibernate-safe.
     */
    private static final String CTE_SQL = """
        WITH
        params AS (
            SELECT
                CAST(:bid_round_id    AS bigint) AS bid_round_id,
                CAST(:buyer_code_id   AS bigint) AS buyer_code_id,
                CAST(:bid_data_doc_id AS bigint) AS bid_data_doc_id,
                br.scheduling_auction_id AS scheduling_auction_id,
                sa.round                 AS round,
                a.week_id                AS week_id,
                bc.code                  AS buyer_code_text,
                (SELECT b.company_name
                   FROM buyer_mgmt.buyer_code_buyers bcb
                   JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                  WHERE bcb.buyer_code_id = bc.id
                  ORDER BY bcb.buyer_id
                  LIMIT 1)               AS company_name,
                CASE
                    WHEN bc.buyer_code_type IN ('Data_Wipe', 'Purchasing_Order_Data_Wipe')
                        THEN 'DW'
                    ELSE 'Wholesale'
                END                      AS buyer_code_type,
                -- 5b: R2 BRSF inputs (NULL when round=1; chk_brsf_round permits 2 or 3 only)
                brsf.target_percent                  AS target_pct,
                brsf.target_value                    AS target_val,
                brsf.regular_buyer_qualification     AS qual_mode,
                brsf.regular_buyer_inventory_options AS inv_mode,
                -- 5b: R3 BRSF inputs
                brsf.bid_percentage_variation        AS pct_var,
                brsf.bid_amount_variation            AS amt_var,
                brsf.rank_qualification_limit        AS rank_lim
            FROM auctions.bid_rounds br
            JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
            JOIN auctions.auctions a              ON a.id = sa.auction_id
            JOIN buyer_mgmt.buyer_codes bc        ON bc.id = :buyer_code_id
            LEFT JOIN auctions.bid_round_selection_filters brsf ON brsf.round = sa.round
            WHERE br.id = :bid_round_id
        ),
        existing_check AS (
            SELECT COUNT(*) AS n
            FROM auctions.bid_data
            WHERE bid_round_id = :bid_round_id
        ),
        qualified_buyer_check AS (
            SELECT qbc.included, qbc.is_special_treatment, qbc.qualification_type
            FROM buyer_mgmt.qualified_buyer_codes qbc, params
            WHERE qbc.scheduling_auction_id = params.scheduling_auction_id
              AND qbc.buyer_code_id         = params.buyer_code_id
        ),
        inventory AS (
            /* Alias `ecoid2` → `ecoid` so downstream CTEs and the final INSERT
               can refer to the canonical `bid_data.ecoid` column name. The
               source table `auctions.aggregated_inventory` historically named
               this column `ecoid2`. */
            SELECT ai.*, ai.ecoid2 AS ecoid
            FROM auctions.aggregated_inventory ai, params
            WHERE ai.week_id = params.week_id
              AND ai.is_deprecated = false
              AND (
                    (params.buyer_code_type = 'DW'        AND ai.dw_total_quantity > 0)
                 OR (params.buyer_code_type = 'Wholesale' AND ai.total_quantity    > 0)
                  )
        ),
        prior_round_biddata AS (
            -- 5b: latest submitted nonzero bid per (ecoid, merged_grade) across ALL prior rounds.
            --   R2 (round=2): only R1 SAs match `sa_prev.round < 2` → DISTINCT ON returns the
            --     unique R1 bid per AE.
            --   R3 (round=3): R1+R2 SAs match → DISTINCT ON + ORDER BY submitted_datetime DESC
            --     picks R2 over R1 when both exist.
            --   R1 (round=1): no prior rounds → CTE returns zero rows; bid_meets_threshold
            --     defaults TRUE in the outer CASE.
            SELECT DISTINCT ON (bd.ecoid, bd.merged_grade)
                   bd.ecoid,
                   bd.merged_grade,
                   bd.submitted_bid_quantity AS prev_qty,
                   bd.submitted_bid_amount   AS prev_amount,
                   bd.round3_bid_rank        AS prev_rank
              FROM auctions.bid_data bd
              JOIN auctions.bid_rounds br_prev
                ON br_prev.id = bd.bid_round_id AND br_prev.submitted = TRUE
              JOIN auctions.scheduling_auctions sa_prev
                ON sa_prev.id = br_prev.scheduling_auction_id
              JOIN auctions.scheduling_auctions sa_cur
                ON sa_cur.id = (SELECT scheduling_auction_id FROM params)
               AND sa_prev.auction_id = sa_cur.auction_id
             WHERE bd.buyer_code_id = (SELECT buyer_code_id FROM params)
               AND sa_prev.round    < (SELECT round FROM params)
               AND bd.submitted_bid_amount > 0
             ORDER BY bd.ecoid, bd.merged_grade, bd.submitted_datetime DESC
        ),
        inventory_with_threshold AS (
            SELECT inv.*,
                   prb.prev_qty, prb.prev_amount, prb.prev_rank,
                   CASE
                     -- 5b R2 cascade (5 branches, DW vs Wholesale split, ports SUB_Round2AggregatedInventorySingleItem)
                     WHEN p.round = 2 THEN
                       CASE
                         WHEN p.qual_mode = 'All_Buyers' THEN TRUE
                         WHEN prb.prev_amount IS NULL THEN
                           (p.inv_mode = 'ShowAllInventory')
                         WHEN p.buyer_code_type = 'DW' THEN
                              (inv.dw_avg_target_price = 0 AND prb.prev_amount > 0)
                           OR (inv.dw_avg_target_price > 0
                               AND prb.prev_amount / inv.dw_avg_target_price >= 1 - (p.target_pct / 100))
                           OR (inv.dw_avg_target_price - prb.prev_amount <= p.target_val)
                           OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                           OR (p.inv_mode = 'ShowAllInventory')
                         ELSE
                              (inv.avg_target_price = 0 AND prb.prev_amount > 0)
                           OR (inv.avg_target_price > 0
                               AND prb.prev_amount / inv.avg_target_price >= 1 - (p.target_pct / 100))
                           OR (inv.avg_target_price - prb.prev_amount <= p.target_val)
                           OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                           OR (p.inv_mode = 'ShowAllInventory')
                       END
                     -- 5b R3 cascade (4 branches, ports per-row form of sub-project 6's R3 selection rule)
                     WHEN p.round = 3 THEN
                       (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
                       OR (p.pct_var IS NOT NULL
                           AND prb.prev_amount IS NOT NULL
                           AND prb.prev_amount
                               >= inv.round3_target_price - (inv.round3_target_price * p.pct_var / 100))
                       OR (p.amt_var IS NOT NULL
                           AND prb.prev_amount IS NOT NULL
                           AND prb.prev_amount >= inv.round3_target_price - p.amt_var)
                       OR (p.rank_lim IS NOT NULL
                           AND prb.prev_rank IS NOT NULL
                           AND prb.prev_rank <= p.rank_lim)
                     -- R1 fallthrough: no prior round, no per-row filtering
                     ELSE TRUE
                   END AS bid_meets_threshold
            FROM inventory inv
            CROSS JOIN params p
            LEFT JOIN prior_round_biddata prb
                   ON prb.ecoid = inv.ecoid AND prb.merged_grade = inv.merged_grade
        ),
        inventory_qualified AS (
            SELECT iwt.*,
                   (q.is_special_treatment OR iwt.bid_meets_threshold) AS row_visible
              FROM inventory_with_threshold iwt
              CROSS JOIN qualified_buyer_check q
             WHERE q.included = TRUE
        ),
        qualified_rows AS (
            SELECT iq.*
            FROM inventory_qualified iq,
                 existing_check ec
            WHERE iq.row_visible = TRUE AND ec.n = 0
        )
        INSERT INTO auctions.bid_data (
            bid_round_id, buyer_code_id, aggregated_inventory_id,
            ecoid, merged_grade, code, company_name,
            bid_quantity, bid_amount, target_price, maximum_quantity, buyer_code_type,
            previous_round_bid_quantity, previous_round_bid_amount,
            bid_round, week_id, bid_data_doc_id,
            created_date, changed_date
        )
        SELECT
            :bid_round_id,
            (SELECT buyer_code_id FROM params),
            qr.id,
            qr.ecoid, qr.merged_grade,
            (SELECT buyer_code_text FROM params),
            (SELECT company_name    FROM params),
            NULL,
            0,
            CASE WHEN (SELECT buyer_code_type FROM params) = 'DW'
                 THEN qr.dw_avg_target_price
                 ELSE qr.avg_target_price END,
            CASE WHEN (SELECT buyer_code_type FROM params) = 'DW'
                 THEN qr.dw_total_quantity
                 ELSE qr.total_quantity END,
            (SELECT buyer_code_type FROM params),
            qr.prev_qty, qr.prev_amount,
            (SELECT round FROM params),
            /* mdm.week.id is BIGINT but bid_data.week_id is INTEGER (V61);
               narrowing is intentional and safe given week IDs are small
               surrogate serials. */
            (SELECT CAST(week_id AS integer) FROM params),
            :bid_data_doc_id,
            NOW(), NOW()
        FROM qualified_rows qr
        """;

    /**
     * Generates {@code auctions.bid_data} rows for the given bid round.
     *
     * <p>Idempotent: when the bid round already has any {@code bid_data}
     * rows the CTE inserts zero rows. Returns the number of rows inserted.
     *
     * @param bidRoundId   the {@code auctions.bid_rounds.id} to populate
     * @param buyerCodeId  the {@code buyer_mgmt.buyer_codes.id} bidding
     * @param bidDataDocId the {@code auctions.bid_data_docs.id} slot —
     *                     stamped on every inserted row
     * @return number of {@code bid_data} rows inserted. A return value of
     *         {@code 0} is ambiguous and can mean any of the following,
     *         which the caller must distinguish out-of-band:
     *         <ul>
     *           <li><strong>(a) Idempotent skip</strong> —
     *               {@code auctions.bid_data} already has at least one row
     *               for this {@code bid_round_id} and the {@code existing_check}
     *               CTE short-circuited the INSERT.</li>
     *           <li><strong>(b) Missing QBC row</strong> — no
     *               {@code buyer_mgmt.qualified_buyer_codes} row exists for
     *               the {@code (scheduling_auction_id, buyer_code_id)} pair.
     *               The {@code inventory_qualified} CTE uses an implicit
     *               cross join against {@code qualified_buyer_check}, so a
     *               missing QBC produces zero qualified rows. R1 init
     *               (sub-project 3) is responsible for seeding QBCs;
     *               callers in Task 8 should verify QBC presence before
     *               invoking this method.</li>
     *           <li><strong>(c) Zero qualified inventory</strong> — every
     *               {@code aggregated_inventory} row for the week was
     *               filtered out by the deprecation flag, the DW vs
     *               wholesale quantity predicate, or the QBC
     *               {@code included = true} gate.</li>
     *         </ul>
     * @implNote The {@code existing_check} CTE is a <em>soft</em> guard,
     *           not a true mutex. Two concurrent callers can both observe
     *           {@code n=0} within their snapshots and both proceed to
     *           INSERT, producing duplicate {@code bid_data} rows for the
     *           same {@code bid_round_id}. The real safety net is
     *           {@code pg_advisory_xact_lock(hashtext('bid_data_gen'),
     *           bid_round_id)} acquired by {@code BidDataCreationService}
     *           in Task 8. Direct callers that bypass that service MUST
     *           acquire the same advisory lock before invoking this
     *           method; otherwise duplicate-row anomalies are possible
     *           under concurrent load.
     * @implNote The {@code row_visible} and {@code bid_meets_threshold} predicates
     *           were filled in by sub-project 5b. See
     *           {@code docs/tasks/auction-r2-r3-row-visibility-design.md}.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int generate(long bidRoundId, long buyerCodeId, long bidDataDocId) {
        return em.createNativeQuery(CTE_SQL)
                .setParameter("bid_round_id", bidRoundId)
                .setParameter("buyer_code_id", buyerCodeId)
                .setParameter("bid_data_doc_id", bidDataDocId)
                .executeUpdate();
    }
}
