package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.id <= :weekId
          AND po.weekTo.id   >= :weekId
        ORDER BY po.changedDate DESC
        """)
    Page<PurchaseOrder> findActiveOnDate(@Param("weekId") Long weekId, Pageable pageable);

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE (:weekFromId IS NULL OR po.weekFrom.id >= :weekFromId)
          AND (:weekToId   IS NULL OR po.weekTo.id   <= :weekToId)
          AND (:yearFrom   IS NULL OR po.weekFrom.year >= :yearFrom)
          AND (:yearTo     IS NULL OR po.weekTo.year   <= :yearTo)
        """)
    Page<PurchaseOrder> findFiltered(
            @Param("weekFromId") Long weekFromId,
            @Param("weekToId") Long weekToId,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"weekFrom", "weekTo", "details", "details.buyerCode"})
    @org.springframework.data.jpa.repository.Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id")
    java.util.Optional<com.ecoatm.salesplatform.model.auctions.PurchaseOrder> findByIdWithDetails(@Param("id") Long id);

    /**
     * Exact-range lookup for the PO landing page: given the two week ids
     * picked in the top dropdowns, return every PO whose stored range matches
     * exactly. Expected cardinalities:
     *   - 0 → empty state on the landing (offer to create)
     *   - 1 → load that PO's grid
     *   - 2+ → CONFIG ERROR — the schema does not enforce uniqueness of
     *          (weekFromId, weekToId) since legitimate weekly POs share their
     *          own week as both bounds (1+ per week is normal historically),
     *          but two POs covering an identical multi-week span is treated
     *          as a data-quality issue and surfaced to the admin.
     *
     * Sorted by changedDate DESC so that, even in the malformed multi-match
     * case, the first row is the most recently touched one — gives the admin
     * something stable to look at while they fix the duplicate.
     */
    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.id = :weekFromId
          AND po.weekTo.id   = :weekToId
        ORDER BY po.changedDate DESC
        """)
    java.util.List<PurchaseOrder> findByExactWeekRange(
            @Param("weekFromId") Long weekFromId,
            @Param("weekToId")   Long weekToId);
}
