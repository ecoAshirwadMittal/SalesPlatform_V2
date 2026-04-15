package com.ecoatm.salesplatform.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Atomic, per-buyer-code, per-year offer number generator.
 *
 * Mendix parity: ACr_UpdateOfferID — {@code {BuyerCode}{YY}{LeftPad(sequence, 3, '0')}}
 *   e.g. {@code BC00126001} = buyer code "BC001" + year "26" + sequence "001".
 *
 * Uses {@code pws.offer_id_sequence} as the per-buyer-code/year counter with
 * a single {@code INSERT ... ON CONFLICT ... DO UPDATE ... RETURNING} round-trip
 * so the read-modify-write is atomic at the row level (no separate SELECT).
 *
 * Extracted from OfferService during Phase 5 of the simplification plan
 * (docs/tasks/simplification-phase5-plan.md).
 */
@Component
public class OfferNumberGenerator {

    private final EntityManager em;
    private final BuyerCodeLookupService buyerCodeLookup;

    public OfferNumberGenerator(EntityManager em, BuyerCodeLookupService buyerCodeLookup) {
        this.em = em;
        this.buyerCodeLookup = buyerCodeLookup;
    }

    /**
     * Produce the next offer number for a given buyer code.
     *
     * @throws IllegalStateException when the buyer code id is not found.
     */
    @Transactional
    public String next(Long buyerCodeId) {
        String buyerCode = buyerCodeLookup.findCodeById(buyerCodeId);
        if (buyerCode == null) {
            throw new IllegalStateException("Buyer code not found: " + buyerCodeId);
        }

        String yearPrefix = String.format("%02d", Year.now().getValue() % 100);

        int seq = ((Number) em.createNativeQuery("""
                INSERT INTO pws.offer_id_sequence (buyer_code_id, year_prefix, max_sequence)
                VALUES (:bcId, :yp, 1)
                ON CONFLICT (buyer_code_id, year_prefix)
                DO UPDATE SET max_sequence = pws.offer_id_sequence.max_sequence + 1
                RETURNING max_sequence
                """)
                .setParameter("bcId", buyerCodeId)
                .setParameter("yp", yearPrefix)
                .getSingleResult()).intValue();

        String paddedSeq = String.format("%03d", seq);
        return buyerCode + yearPrefix + paddedSeq;
    }
}
