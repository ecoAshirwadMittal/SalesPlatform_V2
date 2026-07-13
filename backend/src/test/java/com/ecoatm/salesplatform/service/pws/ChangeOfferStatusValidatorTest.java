package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ChangeOfferStatusValidator} — the port of the Mendix
 * {@code VAL_ChargeOfferStatusHelper_IsValid} guard. Every invalid branch must
 * throw {@link IllegalArgumentException} (→ HTTP 400); every valid combination
 * must pass silently.
 */
class ChangeOfferStatusValidatorTest {

    private final ChangeOfferStatusValidator validator = new ChangeOfferStatusValidator();

    private static final LocalDate D1 = LocalDate.of(2026, 1, 1);
    private static final LocalDate D31 = LocalDate.of(2026, 1, 31);

    // ── All Period branch ──────────────────────────────────────────────

    @Test
    @DisplayName("allPeriod without a selected order → 400")
    void allPeriodWithoutOrderThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("explicitly-selected order");
    }

    @Test
    @DisplayName("allPeriod status change without a target status → 400")
    void allPeriodStatusChangeWithoutTargetThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, "  ", false, false, List.of(5L));

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("target order status");
    }

    @Test
    @DisplayName("allPeriod + selected order + target status → passes (no fromOfferStatus needed)")
    void allPeriodStatusChangeValidPasses() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, "CANCELLED", false, false, List.of(5L));

        assertThatCode(() -> validator.validate(req)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("allPeriod + selected order + metadata-only → passes (no target status needed)")
    void allPeriodMetadataOnlyValidPasses() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, null, true, true, List.of(5L));

        assertThatCode(() -> validator.validate(req)).doesNotThrowAnyException();
    }

    // ── Date-range branch ──────────────────────────────────────────────

    @Test
    @DisplayName("date-range without dates → 400")
    void dateRangeWithoutDatesThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, null, null, "SALES_REVIEW", "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("starting date");
    }

    @Test
    @DisplayName("date-range with only a starting date → 400")
    void dateRangeMissingEndDateThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, null, "SALES_REVIEW", "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("date-range with endingDate == startingDate → 400 (must be strictly after)")
    void dateRangeEndEqualsStartThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, D1, "SALES_REVIEW", "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ending date must be after");
    }

    @Test
    @DisplayName("date-range with endingDate < startingDate → 400")
    void dateRangeEndBeforeStartThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D31, D1, "SALES_REVIEW", "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("date-range status change without a target status → 400")
    void dateRangeStatusChangeWithoutTargetThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, D31, "SALES_REVIEW", null, false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("target order status");
    }

    @Test
    @DisplayName("date-range status change without a from offer status → 400 (the safety guard)")
    void dateRangeStatusChangeWithoutFromThrows() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, D31, "  ", "CANCELLED", false, false, List.of());

        assertThatThrownBy(() -> validator.validate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from offer status");
    }

    @Test
    @DisplayName("date-range status change with both from + to → passes")
    void dateRangeStatusChangeValidPasses() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, D31, "SALES_REVIEW", "CANCELLED", false, false, List.of());

        assertThatCode(() -> validator.validate(req)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("date-range metadata-only → passes (no from/to required)")
    void dateRangeMetadataOnlyValidPasses() {
        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, D1, D31, null, null, true, true, List.of());

        assertThatCode(() -> validator.validate(req)).doesNotThrowAnyException();
    }
}
