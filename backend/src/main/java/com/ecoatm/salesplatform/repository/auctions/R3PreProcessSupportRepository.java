package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pre-process Phase 1 — delete unsubmitted R2 bids.
 *
 * <p>Ports Mendix {@code SUB_Round2_DeleteUnsubmittedBids}: removes
 * bid_data rows on the R2 SA where {@code bid_amount = 0 OR bid_amount IS NULL}.
 * Joins through {@code auctions.bid_rounds} to scope to the R2 SA's bid_rounds.
 * The {@code IS NULL} clause is defensive — the column is currently NOT NULL
 * in the schema, but keeping the predicate is cheap and future-proof.
 */
@Repository
public class R3PreProcessSupportRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String DELETE_UNSUBMITTED_SQL = """
        DELETE FROM auctions.bid_data bd
         USING auctions.bid_rounds br
         WHERE bd.bid_round_id = br.id
           AND br.scheduling_auction_id = CAST(:r2_sa_id AS bigint)
           AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)
        """;

    @Transactional(propagation = Propagation.MANDATORY)
    public int deleteUnsubmittedBids(long r2SchedulingAuctionId) {
        return em.createNativeQuery(DELETE_UNSUBMITTED_SQL)
            .setParameter("r2_sa_id", r2SchedulingAuctionId)
            .executeUpdate();
    }
}
