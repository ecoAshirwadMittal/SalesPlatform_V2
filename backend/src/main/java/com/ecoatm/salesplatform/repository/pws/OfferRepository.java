package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByBuyerCodeIdAndOfferTypeAndStatus(
            Long buyerCodeId, String offerType, String status);

    /**
     * Eagerly fetches an offer with its items — required by the async PWS
     * email listener, which runs outside the originating transaction and
     * therefore cannot trigger lazy collection loads.
     */
    @Query("SELECT o FROM Offer o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Offer> findByIdWithItems(@Param("id") Long id);

    /** Find all non-DRAFT offers, ordered by most recently updated. */
    List<Offer> findByStatusNotOrderByUpdatedDateDesc(String status);

    /** Find offers by status, ordered by most recently updated. */
    List<Offer> findByStatusOrderByUpdatedDateDesc(String status);

    /** Count offers by status. */
    long countByStatus(String status);

    /** Count all non-DRAFT offers. */
    long countByStatusNot(String status);

    /** Find offers by status and buyer code, ordered by most recently updated. */
    List<Offer> findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc(String status, Long buyerCodeId);

    /**
     * Candidate offers for the counter-offer reminder job: those still sitting
     * in the given status ({@code Buyer_Acceptance}) with at least one reminder
     * not yet sent. Faithful port of the legacy
     * {@code SUB_SendCounterOfferReminderEmail} filter
     * {@code [OfferStatus='Buyer_Acceptance'] [FirstReminderSent=false or SecondReminderSent=false]}.
     * Ordered by id for a deterministic, stable per-tick sweep.
     */
    @Query("""
            SELECT o FROM Offer o
            WHERE o.status = :status
              AND (o.firstReminderSent = false OR o.secondReminderSent = false)
            ORDER BY o.id
            """)
    List<Offer> findCounterReminderCandidates(@Param("status") String status);

    /**
     * Aggregate offer summary by status: count offers, active SKUs, total qty, total price.
     * "Active SKUs" = offer_items with quantity > 0.
     * Returns Object[] rows: [status, offerCount, activeSkuCount, totalQty, totalPrice].
     */
    @Query(value = """
            WITH offer_agg AS (
                SELECT status,
                       COUNT(*)                          AS offer_count,
                       COALESCE(SUM(total_qty), 0)       AS total_qty,
                       COALESCE(SUM(total_price), 0)     AS total_price
                FROM pws.offer
                WHERE status IN :statuses
                GROUP BY status
            ),
            sku_agg AS (
                SELECT o.status, COUNT(*) AS active_sku_count
                FROM pws.offer o
                JOIN pws.offer_item oi ON oi.offer_id = o.id
                WHERE o.status IN :statuses AND oi.quantity > 0
                GROUP BY o.status
            )
            SELECT oa.status,
                   oa.offer_count,
                   COALESCE(sa.active_sku_count, 0),
                   oa.total_qty,
                   oa.total_price
            FROM offer_agg oa
            LEFT JOIN sku_agg sa ON sa.status = oa.status
            """, nativeQuery = true)
    List<Object[]> getStatusSummaries(@Param("statuses") List<String> statuses);
}
