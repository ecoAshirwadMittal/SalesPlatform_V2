package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    /**
     * PO-lifecycle helper: every PO whose stored week range
     * {@code [weekFrom, weekTo]} contains the given target week — i.e. the POs
     * that are ACTIVE for that week.
     *
     * <p><b>Comparison is on the business {@code weekId}</b> (mdm.week.week_id,
     * {@code year*100 + weekNumber}), NOT the surrogate {@code mdm.week.id}. The
     * surrogate is not chronologically ordered — the V65 seed assigns it via
     * {@code GROUP BY} with no {@code ORDER BY} — so a range test on the
     * surrogate {@code weekFrom.id}/{@code weekTo.id} can wrongly include or
     * exclude a multi-week PO. This mirrors gap 0.1's
     * {@link #findOverlappingWeekRange} (the overlap producer side) and the 4C
     * target-price {@code po_max} fix (task #37, the recalc consumer side) — the
     * same week-model rule everywhere a PO week range is compared
     * chronologically.
     *
     * <p><b>Callers MUST pass the business {@code weekId}</b> of the target week
     * (e.g. {@code week.getWeekId()}), NOT the surrogate {@code week.getId()}.
     */
    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.weekId <= :weekId
          AND po.weekTo.weekId   >= :weekId
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

    /**
     * VAL_WeekRange_PO (gap 0.1) — GLOBAL week-range overlap guard.
     *
     * <p>Returns every existing PO whose stored week range
     * {@code [weekFrom.weekId, weekTo.weekId]} intersects the candidate range
     * {@code [fromWeekId, toWeekId]}. Two closed integer intervals overlap iff
     * {@code a1 <= b2 AND b1 <= a2}; with {@code a = existing} and
     * {@code b = candidate} that is {@code weekFrom.weekId <= :toWeekId AND
     * weekTo.weekId >= :fromWeekId}.
     *
     * <p><b>Scope is GLOBAL by design</b> (human decision 2026-07-11, gap 0.1):
     * there is deliberately NO product / grade / buyer-code predicate. This
     * mirrors the legacy Mendix {@code VAL_WeekRange_PO} rule — which queried
     * {@code WeekPeriod}, an entity carrying no buyer-code link, comparing by
     * {@code WeekStartDateTime} — and guarantees the 4C target-price recalc
     * never sees two PO floor candidates for a single (product, grade, week):
     * its {@code po_max} CTE takes {@code MAX(price)} per
     * {@code (product_id, grade)} across every PO whose range contains the
     * auction week, with no buyer scoping (see {@link TargetPriceRecalcRepository}).
     *
     * <p><b>Comparison is on the business {@code weekId}</b> (mdm.week.week_id,
     * {@code year*100 + weekNumber}), NOT the surrogate {@code mdm.week.id}.
     * The surrogate id is <em>not</em> chronologically ordered — the V65 seed
     * assigns it via {@code GROUP BY} with no {@code ORDER BY}, so id order does
     * not track calendar order (verified by
     * {@code PurchaseOrderOverlapRepositoryIT}). The business {@code weekId} is
     * monotonic with {@code WeekStartDateTime}, so comparing on it yields the
     * correct chronological overlap and exactly matches the legacy rule's
     * {@code WeekStartDateTime} test. (Caveat: 4C's own
     * {@code p.week_id BETWEEN po.week_from_id AND po.week_to_id} binds the
     * surrogate id; that is a pre-existing concern outside gap 0.1 — this guard
     * enforces the chronologically-correct business rule regardless.)
     *
     * <p>{@code excludeId} is the surrogate PO id and lets an update skip the PO
     * being edited so re-saving a PO with an unchanged range does not flag
     * itself. Pass {@code null} on create.
     */
    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.weekId <= :toWeekId
          AND po.weekTo.weekId   >= :fromWeekId
          AND (:excludeId IS NULL OR po.id <> :excludeId)
        ORDER BY po.weekFrom.weekId ASC
        """)
    java.util.List<PurchaseOrder> findOverlappingWeekRange(
            @Param("fromWeekId") Long fromWeekId,
            @Param("toWeekId")   Long toWeekId,
            @Param("excludeId")  Long excludeId);
}
