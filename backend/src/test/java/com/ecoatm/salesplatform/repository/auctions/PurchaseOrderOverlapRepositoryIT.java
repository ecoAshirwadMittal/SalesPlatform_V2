package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration coverage for {@link PurchaseOrderRepository#findOverlappingWeekRange}
 * — the VAL_WeekRange_PO (gap 0.1) GLOBAL week-range overlap guard.
 *
 * <p>Scope decision (human, 2026-07-11): reject a candidate PO whose week
 * range overlaps ANY existing PO's range, with NO product / grade / buyer-code
 * scoping. These cases prove exactly that: an overlapping range is flagged even
 * when the two POs share no buyer, product, or grade — the behaviour that keeps
 * the 4C target-price recalc from ever seeing two PO floor candidates for one
 * (product, grade, week).
 *
 * <p>The guard compares the business {@code weekId} (chronological), NOT the
 * surrogate {@code mdm.week.id} (which the V65 seed does not assign in calendar
 * order — verified during development). To stay deterministic on the shared dev
 * DB — whose {@code mdm.week} also carries a few non-conforming legacy rows
 * (e.g. {@code week_id=60901}) — these cases pin to the clean, always-seeded
 * 2024 window {@code 202401..202406} (six consecutive ISO weeks, well clear of
 * the {@code V81} seed POs which live in 2025/2026). {@code @Transactional}
 * rolls every case back.
 */
@Transactional
class PurchaseOrderOverlapRepositoryIT extends PostgresIntegrationTest {

    /** Six consecutive clean ISO weeks (V65): 202401 (2024-01-01) … 202406. */
    private static final long FIRST_WEEK_ID = 202401L;

    @Autowired PurchaseOrderRepository poRepo;
    @PersistenceContext EntityManager em;

    private List<Week> weeks;
    private BuyerCode buyerX;
    private BuyerCode buyerY;

    @BeforeEach
    void seedRefs() {
        weeks = em.createQuery(
                        "SELECT w FROM Week w WHERE w.weekId BETWEEN :lo AND :hi ORDER BY w.weekId ASC",
                        Week.class)
                .setParameter("lo", FIRST_WEEK_ID)
                .setParameter("hi", FIRST_WEEK_ID + 5)
                .getResultList();
        assertThat(weeks)
                .as("clean 2024 calendar window must be seeded by V65")
                .hasSize(6);

        List<BuyerCode> bcs = em.createQuery(
                        "SELECT b FROM BuyerCode b ORDER BY b.id ASC", BuyerCode.class)
                .setMaxResults(2).getResultList();
        assertThat(bcs).isNotEmpty();
        buyerX = bcs.get(0);
        buyerY = bcs.size() > 1 ? bcs.get(1) : bcs.get(0);
    }

    /** 1-based accessor: {@code w(1)} → W1 … {@code w(6)} → W6 (by weekId). */
    private Week w(int oneBased) {
        return weeks.get(oneBased - 1);
    }

    private PurchaseOrder persistPo(Week from, Week to, BuyerCode bc,
                                    String product, String grade) {
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(from);
        po.setWeekTo(to);
        po.setWeekRangeLabel(from.getWeekDisplay() + " - " + to.getWeekDisplay());
        em.persist(po);

        PODetail d = new PODetail();
        d.setPurchaseOrder(po);
        d.setBuyerCode(bc);
        d.setProductId(product);
        d.setGrade(grade);
        d.setPrice(new BigDecimal("100.0000"));
        em.persist(d);

        em.flush();
        return po;
    }

    @Test
    @DisplayName("business weekId is a chronological ordinal — why the guard compares weekId, not the surrogate id")
    void weekIdIsAChronologicalOrdinal() {
        // Across the clean window, week_start_datetime must strictly ascend with
        // weekId — i.e. weekId tracks the calendar and matches the legacy
        // VAL_WeekRange_PO WeekStartDateTime comparison. (The surrogate
        // mdm.week.id does NOT track the calendar — V65 assigns it via GROUP BY
        // with no ORDER BY — which is exactly why the guard uses weekId.)
        for (int i = 1; i < weeks.size(); i++) {
            assertThat(weeks.get(i).getWeekId())
                    .isEqualTo(weeks.get(i - 1).getWeekId() + 1);
            assertThat(weeks.get(i).getWeekStartDateTime())
                    .isAfter(weeks.get(i - 1).getWeekStartDateTime());
        }
    }

    @Test
    @DisplayName("overlapping range (same span) is detected → rejected")
    void overlappingSameSpanDetected() {
        PurchaseOrder po1 = persistPo(w(1), w(3), buyerX, "P1", "A_YYY");

        // candidate [W2..W4] intersects existing [W1..W3]
        var hits = poRepo.findOverlappingWeekRange(w(2).getWeekId(), w(4).getWeekId(), null);

        assertThat(hits).extracting(PurchaseOrder::getId).contains(po1.getId());
    }

    @Test
    @DisplayName("GLOBAL scope: overlapping POs with different buyer/product/grade still conflict")
    void overlapIsGlobalAcrossBuyerProductGrade() {
        PurchaseOrder po1 = persistPo(w(1), w(3), buyerX, "P1", "A_YYY");
        // Fully different buyer + product + grade, overlapping weeks.
        PurchaseOrder po2 = persistPo(w(2), w(5), buyerY, "P2", "B_ZZZ");

        // From po2's perspective (excluding itself), po1 is still an overlap —
        // the guard carries no buyer/product/grade predicate. This is the
        // behaviour the human chose (Candidate A, exact VAL_WeekRange_PO parity).
        var fromPo2 = poRepo.findOverlappingWeekRange(
                w(2).getWeekId(), w(5).getWeekId(), po2.getId());

        assertThat(fromPo2).extracting(PurchaseOrder::getId).contains(po1.getId());
    }

    @Test
    @DisplayName("ranges sharing only a boundary week overlap (inclusive)")
    void boundarySharedWeekOverlaps() {
        PurchaseOrder po1 = persistPo(w(1), w(3), buyerX, "P1", "A_YYY");

        // [W3..W5] shares exactly W3 with [W1..W3] → 4C would see both cover W3.
        var hits = poRepo.findOverlappingWeekRange(w(3).getWeekId(), w(5).getWeekId(), null);

        assertThat(hits).extracting(PurchaseOrder::getId).contains(po1.getId());
    }

    @Test
    @DisplayName("non-overlapping range is allowed")
    void nonOverlappingAllowed() {
        PurchaseOrder po1 = persistPo(w(1), w(3), buyerX, "P1", "A_YYY");

        // [W4..W6] is disjoint from [W1..W3]
        var hits = poRepo.findOverlappingWeekRange(w(4).getWeekId(), w(6).getWeekId(), null);

        assertThat(hits).extracting(PurchaseOrder::getId).doesNotContain(po1.getId());
    }

    @Test
    @DisplayName("update excludes the PO being edited (unchanged-range re-save allowed)")
    void updateExcludesSelf() {
        PurchaseOrder po1 = persistPo(w(1), w(3), buyerX, "P1", "A_YYY");

        // Re-validating po1's own range while excluding po1 → no self-conflict.
        var excludingSelf = poRepo.findOverlappingWeekRange(
                w(1).getWeekId(), w(3).getWeekId(), po1.getId());
        assertThat(excludingSelf).extracting(PurchaseOrder::getId).doesNotContain(po1.getId());

        // Without the exclusion po1 overlaps itself — proves the exclusion is
        // load-bearing, not incidental.
        var withoutExclusion = poRepo.findOverlappingWeekRange(
                w(1).getWeekId(), w(3).getWeekId(), null);
        assertThat(withoutExclusion).extracting(PurchaseOrder::getId).contains(po1.getId());
    }
}
