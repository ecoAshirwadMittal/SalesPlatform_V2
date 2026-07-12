package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseOrderValidatorTest {

    WeekRepository weekRepo;
    BuyerCodeRepository bcRepo;
    PurchaseOrderRepository poRepo;
    PurchaseOrderValidator validator;

    @BeforeEach
    void init() {
        weekRepo = mock(WeekRepository.class);
        bcRepo = mock(BuyerCodeRepository.class);
        poRepo = mock(PurchaseOrderRepository.class);
        validator = new PurchaseOrderValidator(weekRepo, bcRepo, poRepo);
    }

    @Test
    void weekRangeOkPasses() {
        Week from = stubWeek(1L, 202501);
        Week to = stubWeek(2L, 202504);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        var resolved = validator.resolveWeekRange(1L, 2L);
        assertThat(resolved.from().getId()).isEqualTo(1L);
        assertThat(resolved.to().getId()).isEqualTo(2L);
    }

    @Test
    void weekRangeReversedThrows() {
        Week from = stubWeek(1L, 202504);
        Week to = stubWeek(2L, 202501);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        assertThatThrownBy(() -> validator.resolveWeekRange(1L, 2L))
                .isInstanceOf(PurchaseOrderValidationException.class)
                .hasMessageContaining("week_from must be <= week_to");
    }

    @Test
    void weekIdNotFoundThrows() {
        when(weekRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> validator.resolveWeekRange(99L, 100L))
                .isInstanceOf(PurchaseOrderValidationException.class);
    }

    @Test
    void buyerCodesAllExistPasses() {
        when(bcRepo.findCodesIn(List.of("ABC", "DEF"))).thenReturn(List.of("ABC", "DEF"));
        validator.requireBuyerCodes(List.of("ABC", "DEF"));
    }

    @Test
    void missingBuyerCodesThrowsListingOffenders() {
        when(bcRepo.findCodesIn(List.of("ABC", "MISSING"))).thenReturn(List.of("ABC"));
        assertThatThrownBy(() -> validator.requireBuyerCodes(List.of("ABC", "MISSING")))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("MISSING_BUYER_CODE");
                    assertThat(ex.getDetails()).contains("MISSING");
                });
    }

    // ---- VAL_WeekRange_PO (gap 0.1) — GLOBAL overlap guard ----

    @Test
    void overlappingWeekRangeThrowsNamingConflict() {
        Week from = stubWeek(10L, 202601, "2026 / Wk01");
        Week to = stubWeek(12L, 202603, "2026 / Wk03");
        var range = new PurchaseOrderValidator.WeekRange(from, to);
        PurchaseOrder conflict = stubPo(7L, "2025 / Wk50", "2026 / Wk02");
        when(poRepo.findOverlappingWeekRange(202601L, 202603L, null))
                .thenReturn(List.of(conflict));

        assertThatThrownBy(() -> validator.requireNonOverlappingWeekRange(range, null))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("OVERLAPPING_WEEK_RANGE");
                    assertThat(ex.getMessage())
                            .contains("overlaps existing purchase order 7")
                            .contains("2026 / Wk01 - 2026 / Wk03")   // candidate
                            .contains("2025 / Wk50 - 2026 / Wk02");  // existing
                    assertThat(ex.getDetails()).contains("7");
                });
    }

    @Test
    void nonOverlappingWeekRangePasses() {
        Week from = stubWeek(10L, 202601, "2026 / Wk01");
        Week to = stubWeek(12L, 202603, "2026 / Wk03");
        var range = new PurchaseOrderValidator.WeekRange(from, to);
        when(poRepo.findOverlappingWeekRange(202601L, 202603L, null)).thenReturn(List.of());

        validator.requireNonOverlappingWeekRange(range, null); // no throw
    }

    @Test
    void updateForwardsOwnIdAsExclusion() {
        Week from = stubWeek(10L, 202601, "2026 / Wk01");
        Week to = stubWeek(12L, 202603, "2026 / Wk03");
        var range = new PurchaseOrderValidator.WeekRange(from, to);
        when(poRepo.findOverlappingWeekRange(202601L, 202603L, 99L)).thenReturn(List.of());

        validator.requireNonOverlappingWeekRange(range, 99L); // no throw

        // The edited PO's own id must be forwarded so it never conflicts with
        // itself on an unchanged-range re-save.
        verify(poRepo).findOverlappingWeekRange(202601L, 202603L, 99L);
    }

    private static Week stubWeek(long id, int weekId) {
        return stubWeek(id, weekId, null);
    }

    private static Week stubWeek(long id, int weekId, String display) {
        Week w = new Week();
        try {
            var idField = Week.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(w, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        w.setWeekId(weekId);
        if (display != null) w.setWeekDisplay(display);
        return w;
    }

    private static PurchaseOrder stubPo(long id, String fromDisplay, String toDisplay) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        Week from = new Week(); from.setWeekDisplay(fromDisplay);
        Week to = new Week(); to.setWeekDisplay(toDisplay);
        po.setWeekFrom(from);
        po.setWeekTo(to);
        return po;
    }
}
