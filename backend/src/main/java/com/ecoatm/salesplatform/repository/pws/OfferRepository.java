package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
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

    /**
     * SLA-tag write: flags every offer sitting in an SLA-tracked status
     * ({@code Sales_Review} / {@code Buyer_Acceptance}) whose {@code updated_date}
     * (day-truncated) is on or before {@code cutoff} — the date {@code sla_days}
     * business days ago computed by {@code SlaTagService}. Only untagged rows are
     * touched so the returned count reflects newly-tagged offers. Modern port of
     * legacy {@code SUB_SetSLATag}'s {@code trimToDays(UpdateDate) <=
     * trimToDays(ResultDate)} filter.
     *
     * <p>A {@code BEFORE UPDATE} trigger on {@code pws.offer} resets
     * {@code updated_date = NOW()}, so this write also bumps {@code updated_date}
     * as a side effect — matching the legacy commit and the one-shot nature of
     * {@code offer_beyond_sla}.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE pws.offer SET offer_beyond_sla = true "
            + "WHERE status IN (:statuses) "
            + "AND CAST(updated_date AS date) <= :cutoff "
            + "AND (offer_beyond_sla IS NULL OR offer_beyond_sla = false)",
            nativeQuery = true)
    int tagOverdueOffers(@Param("statuses") Collection<String> statuses,
                         @Param("cutoff") LocalDate cutoff);

    /**
     * SLA-tag clear: resets {@code offer_beyond_sla = false} on every SLA-tracked
     * offer currently flagged. Modern port of legacy
     * {@code SUB_RemoveSLATagsForAllOffers} (which likewise scopes to the
     * {@code Sales_Review} / {@code Buyer_Acceptance} statuses). Returns the
     * number of rows cleared.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE pws.offer SET offer_beyond_sla = false "
            + "WHERE status IN (:statuses) AND offer_beyond_sla = true",
            nativeQuery = true)
    int clearAllSlaTags(@Param("statuses") Collection<String> statuses);
}
