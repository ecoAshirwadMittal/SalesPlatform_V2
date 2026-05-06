package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Special-buyer bulk INSERT into {@code auctions.bid_data} — ports Mendix
 * {@code SUB_CreateBidDataForAllAE}.
 *
 * <p>For each non-deprecated {@code aggregated_inventory} row in the R2
 * scheduling auction's week, generates exactly one {@code bid_data} row
 * for the supplied special-treatment buyer code. The DW vs Wholesale
 * branch on {@code bc.buyer_code_type} drives both the {@code target_price}
 * (avg vs dw_avg) and {@code maximum_quantity} (total vs dw_total)
 * column sources.
 *
 * <p>Distinct from {@link BidDataCreationRepository} (per design §3.8):
 * the existing repository is per-(bid_round, buyer_code) and short-circuits
 * on the QBC {@code included} gate; special buyers need a "give me every
 * AE row regardless of R1 bid" semantic, so this repository takes a
 * different SQL path.
 *
 * <p>The {@code special_qbcs} CTE in design §7.4 joined to
 * {@code qualified_buyer_codes}; this repository instead accepts the
 * {@code buyer_code_id} as a direct parameter — the per-buyer-code Java
 * loop in {@code BidDataForAllAEService} (Task 10) is responsible for
 * iterating over the special-treatment QBC set. Cleaner separation: the
 * repo writes for one code at a time; the service composes the loop.
 *
 * <p>{@code @Transactional(propagation = MANDATORY)} — caller must hold
 * a transaction. The orchestrating {@code R2BuyerAssignmentService} runs
 * in {@code REQUIRES_NEW} and satisfies this contract.
 */
@Repository
public class BidDataForAllAERepository {

    @PersistenceContext private EntityManager em;

    /*
     * Postgres `::type` cast syntax collides with Hibernate's named-parameter
     * tokenizer. Use ANSI `CAST(... AS bigint)` instead — see the same note
     * on BidDataCreationRepository.CTE_SQL.
     */
    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id        AS scheduling_auction_id,
                 sa.round     AS round,
                 a.week_id    AS week_id
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
           WHERE sa.id = CAST(:r2_sa_id AS bigint)
        ),
        buyer_code_info AS (
          SELECT bc.id AS buyer_code_id,
                 bc.code AS code_text,
                 bc.buyer_code_type,
                 (SELECT b.company_name
                    FROM buyer_mgmt.buyer_code_buyers bcb
                    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                   WHERE bcb.buyer_code_id = bc.id
                   ORDER BY bcb.buyer_id
                   LIMIT 1) AS company_name
            FROM buyer_mgmt.buyer_codes bc
           WHERE bc.id = CAST(:buyer_code_id AS bigint)
        )
        INSERT INTO auctions.bid_data (
            bid_round_id, buyer_code_id, aggregated_inventory_id,
            ecoid, merged_grade, code, company_name,
            bid_quantity, bid_amount, target_price, maximum_quantity,
            buyer_code_type, bid_round, week_id, bid_data_doc_id,
            created_date, changed_date
        )
        SELECT CAST(:bid_round_id AS bigint),
               bci.buyer_code_id,
               ai.id,
               ai.ecoid2,
               ai.merged_grade,
               bci.code_text,
               bci.company_name,
               NULL,
               0,
               CASE bci.buyer_code_type
                 WHEN 'Data_Wipe' THEN ai.dw_avg_target_price
                 ELSE                  ai.avg_target_price
               END,
               CASE bci.buyer_code_type
                 WHEN 'Data_Wipe' THEN ai.dw_total_quantity
                 ELSE                  ai.total_quantity
               END,
               bci.buyer_code_type,
               (SELECT round FROM params),
               (SELECT CAST(week_id AS integer) FROM params),
               CAST(:bid_data_doc_id AS bigint),
               NOW(), NOW()
          FROM buyer_code_info bci
          CROSS JOIN auctions.aggregated_inventory ai
          JOIN params p ON p.week_id = ai.week_id
         WHERE ai.is_deprecated = FALSE
           AND CASE bci.buyer_code_type
                 WHEN 'Data_Wipe' THEN ai.dw_total_quantity
                 ELSE                  ai.total_quantity
               END > 0
        """;

    /**
     * Inserts one {@code auctions.bid_data} row per non-deprecated
     * {@code aggregated_inventory} row in the R2 scheduling auction's
     * week, for the supplied special-treatment buyer code.
     *
     * @param r2SchedulingAuctionId the R2 {@code scheduling_auctions.id}
     *                              whose week scopes the AE join
     * @param buyerCodeId           the special-treatment
     *                              {@code buyer_codes.id} bidding
     * @param bidRoundId            the {@code bid_rounds.id} that the
     *                              inserted rows attach to (per-buyer-code
     *                              slot resolved by
     *                              {@code BidDataForAllAEService})
     * @param bidDataDocId          the {@code bid_data_docs.id} stamped
     *                              on every inserted row
     * @return number of {@code bid_data} rows inserted — typically equals
     *         the count of non-deprecated AE rows for the week with a
     *         positive (DW vs WH branch) quantity
     * @implNote <strong>Caller contract:</strong> This method APPENDS rows
     *           to {@code auctions.bid_data} for the supplied
     *           ({@code bid_round_id}, {@code buyer_code_id}) pair. There is
     *           <strong>no idempotency guard</strong> — calling twice with
     *           the same {@code bid_round_id} produces duplicate rows. The
     *           caller ({@code BidDataForAllAEService} in sub-project 5
     *           Task 10) is responsible for ensuring at-most-once invocation
     *           per ({@code SchedulingAuction}, special-treatment buyer code)
     *           tuple, typically by getting-or-creating exactly one
     *           {@code bid_round} per pair before invocation.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int insertForSpecialBuyer(long r2SchedulingAuctionId,
                                      long buyerCodeId,
                                      long bidRoundId,
                                      long bidDataDocId) {
        return em.createNativeQuery(CTE_SQL)
            .setParameter("r2_sa_id", r2SchedulingAuctionId)
            .setParameter("buyer_code_id", buyerCodeId)
            .setParameter("bid_round_id", bidRoundId)
            .setParameter("bid_data_doc_id", bidDataDocId)
            .executeUpdate();
    }
}
