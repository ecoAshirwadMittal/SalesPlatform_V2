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
 * <p>Two known stubs marked with {@code TODO} ({@code bid_meets_threshold}
 * and {@code row_visible}) are left as constant {@code TRUE} for sub-project
 * 4 (R2/R3 qualification gate) to replace.
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
                  LIMIT 1)               AS company_name,
                CASE
                    WHEN bc.buyer_code_type IN ('Data_Wipe', 'Purchasing_Order_Data_Wipe')
                        THEN 'DW'
                    ELSE 'Wholesale'
                END                      AS buyer_code_type
            FROM auctions.bid_rounds br
            JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
            JOIN auctions.auctions a              ON a.id = sa.auction_id
            JOIN buyer_mgmt.buyer_codes bc        ON bc.id = :buyer_code_id
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
        selection_filter AS (
            SELECT bsf.*
            FROM auctions.bid_round_selection_filters bsf, params
            WHERE bsf.round = params.round
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
        inventory_with_threshold AS (
            SELECT inv.*,
                   /* TODO: sub-project 4 will replace this constant with a
                      real R2/R3 threshold check derived from
                      selection_filter and prior_round_biddata. */
                   TRUE AS bid_meets_threshold
            FROM inventory inv
        ),
        inventory_qualified AS (
            SELECT iwt.*,
                   /* TODO: sub-project 4 will replace this with the
                      special-treatment + included branching from
                      qualified_buyer_check. */
                   TRUE AS row_visible
            FROM inventory_with_threshold iwt, qualified_buyer_check q
            WHERE q.included = true
        ),
        prior_scheduling_auction AS (
            SELECT sa_prev.id AS prev_sa_id
            FROM auctions.scheduling_auctions sa_prev, params
            WHERE sa_prev.auction_id = (
                SELECT auction_id FROM auctions.scheduling_auctions
                WHERE id = params.scheduling_auction_id
            )
              AND sa_prev.round = params.round - 1
        ),
        prior_round_biddata AS (
            SELECT bd.ecoid, bd.merged_grade,
                   bd.submitted_bid_quantity AS prev_qty,
                   bd.submitted_bid_amount   AS prev_amount
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br_prev ON br_prev.id = bd.bid_round_id
            JOIN prior_scheduling_auction psa ON psa.prev_sa_id = br_prev.scheduling_auction_id
            WHERE bd.buyer_code_id = (SELECT buyer_code_id FROM params)
        ),
        qualified_rows AS (
            SELECT iq.*, prb.prev_qty, prb.prev_amount
            FROM inventory_qualified iq
            LEFT JOIN prior_round_biddata prb
                   ON prb.ecoid = iq.ecoid AND prb.merged_grade = iq.merged_grade,
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
     * @implNote Sub-project 4 will redesign the {@code row_visible} +
     *           {@code bid_meets_threshold} stubs (currently constant
     *           {@code TRUE}) and may also convert the
     *           {@code inventory_qualified} cross join to a {@code LEFT JOIN}
     *           so that "missing QBC" can be distinguished from "QBC
     *           present but excluded". Until then, mode (b) above is the
     *           caller's responsibility to detect.
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
