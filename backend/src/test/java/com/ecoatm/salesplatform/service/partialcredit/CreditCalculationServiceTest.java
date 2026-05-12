package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pure-function tests for {@link CreditCalculationService}. Per the
 * Sprint 3 chunk-1 spec each line-credit formula is asserted in
 * isolation; the header summary is asserted against a mixed mid-state
 * (some ACCEPTED, some DECLINED, some PENDING) request.
 */
@ExtendWith(MockitoExtension.class)
class CreditCalculationServiceTest {

    @Mock WeekRepository weekRepository;

    CreditCalculationService service;

    @BeforeEach
    void setUp() {
        service = new CreditCalculationService(weekRepository);
    }

    // -------------------------------------------------------------------
    // Missing-device formula
    // -------------------------------------------------------------------

    @Test
    @DisplayName("computeLineCredit(Missing) returns paid amount regardless of decision")
    void missingLine_returnsPaidAmount_whenPending() {
        MissingDeviceLine line = newMissingLine(new BigDecimal("42.50"),
                ReviewDecision.PENDING);

        BigDecimal credit = service.computeLineCredit(line);

        // The method is pure — caller (header-summary aggregate) filters
        // by reviewDecision; the per-line formula is decision-agnostic.
        assertThat(credit).isEqualByComparingTo("42.50");
    }

    @Test
    @DisplayName("computeLineCredit(Missing) returns zero when amountPaid is null")
    void missingLine_returnsZero_whenAmountPaidNull() {
        MissingDeviceLine line = newMissingLine(null, ReviewDecision.ACCEPTED);

        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("0");
    }

    // -------------------------------------------------------------------
    // Wrong-device formula
    // -------------------------------------------------------------------

    @Test
    @DisplayName("computeLineCredit(Wrong) returns paid - latestPrice when positive")
    void wrongLine_returnsDifference_whenPaidExceedsLatest() {
        WrongDeviceLine line = newWrongLine(new BigDecimal("100.00"),
                new BigDecimal("30.00"), ReviewDecision.ACCEPTED);

        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("computeLineCredit(Wrong) returns 0 when latestPrice exceeds paid (auto-decline floor)")
    void wrongLine_returnsZero_whenLatestExceedsPaid() {
        WrongDeviceLine line = newWrongLine(new BigDecimal("40.00"),
                new BigDecimal("90.00"), ReviewDecision.ACCEPTED);

        // Receipt-worth-more-than-paid → recommendation engine auto-declines,
        // but the floor defends the calc against reviewer override.
        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("computeLineCredit(Wrong) returns 0 when latestPrice is null")
    void wrongLine_returnsZero_whenLatestPriceNull() {
        WrongDeviceLine line = newWrongLine(new BigDecimal("50.00"), null,
                ReviewDecision.ACCEPTED);

        // Null latest_price means the lookup failed (no bid_data rows
        // or unresolved received device). Recommendation engine flags
        // this; the calc treats it defensively as zero credit.
        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("computeLineCredit(Wrong) returns 0 when paid is null")
    void wrongLine_returnsZero_whenPaidNull() {
        WrongDeviceLine line = newWrongLine(null, new BigDecimal("30.00"),
                ReviewDecision.ACCEPTED);

        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("0");
    }

    // -------------------------------------------------------------------
    // Encumbered-device formula
    // -------------------------------------------------------------------

    @Test
    @DisplayName("computeLineCredit(Encumbered) returns reviewer-entered actualValue")
    void encumberedLine_returnsActualValue() {
        EncumberedDeviceLine line = newEncumberedLine(new BigDecimal("75.00"),
                new BigDecimal("25.00"), ReviewDecision.ACCEPTED);

        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("computeLineCredit(Encumbered) returns 0 when actualValue is null")
    void encumberedLine_returnsZero_whenActualValueNull() {
        EncumberedDeviceLine line = newEncumberedLine(new BigDecimal("75.00"),
                null, ReviewDecision.ACCEPTED);

        assertThat(service.computeLineCredit(line)).isEqualByComparingTo("0");
    }

    // -------------------------------------------------------------------
    // Header summary aggregate
    // -------------------------------------------------------------------

    @Test
    @DisplayName("computeHeaderSummary aggregates mixed reasons + decisions correctly")
    void headerSummary_mixedReasons() {
        // Request layout — mirrors a realistic mid-review state:
        //   missing: 2 lines, 1 ACCEPTED ($30 paid), 1 DECLINED ($20 paid)
        //   wrong:   2 lines, 1 ACCEPTED ($100 paid, $30 latest → credit 70),
        //            1 PENDING ($80 paid, no decision yet)
        //   encumbered: 1 line, ACCEPTED ($60 paid, $25 actualValue)
        CreditRequest cr = new CreditRequest();

        MissingDeviceLine m1 = newMissingLine(new BigDecimal("30.00"), ReviewDecision.ACCEPTED);
        MissingDeviceLine m2 = newMissingLine(new BigDecimal("20.00"), ReviewDecision.DECLINED);

        WrongDeviceLine w1 = newWrongLine(new BigDecimal("100.00"), new BigDecimal("30.00"), ReviewDecision.ACCEPTED);
        w1.setExpectedEcoatmCode("ECO-APPLE-XR-64");
        WrongDeviceLine w2 = newWrongLine(new BigDecimal("80.00"), new BigDecimal("20.00"), ReviewDecision.PENDING);
        w2.setExpectedEcoatmCode("ECO-SAMS-S20-128");

        EncumberedDeviceLine e1 = newEncumberedLine(new BigDecimal("60.00"),
                new BigDecimal("25.00"), ReviewDecision.ACCEPTED);

        CreditCalculationService.HeaderSummary summary = service.computeHeaderSummary(
                cr, List.of(m1, m2), List.of(w1, w2), List.of(e1));

        // requestedQty = total line count = 5
        assertThat(summary.requestedQty()).isEqualTo(5);
        // requestedSkus = distinct expected_ecoatm_code = {XR-64, S20-128} = 2
        assertThat(summary.requestedSkus()).isEqualTo(2);
        // requestedTotal = 30 + 20 + 100 + 80 + 60 = 290
        assertThat(summary.requestedTotal()).isEqualByComparingTo("290.00");

        // approvedQty = ACCEPTED count = 3 (m1, w1, e1)
        assertThat(summary.approvedQty()).isEqualTo(3);
        // approvedSkus = distinct ACCEPTED expected_ecoatm_code = {XR-64} = 1
        // (missing + encumbered carry no ecoatm_code today; only wrong contributes)
        assertThat(summary.approvedSkus()).isEqualTo(1);
        // approvedTotal = 30 (missing m1 full paid) + 70 (wrong w1: 100-30)
        //                  + 25 (encumbered e1 actualValue) = 125
        assertThat(summary.approvedTotal()).isEqualByComparingTo("125.00");
    }

    @Test
    @DisplayName("computeHeaderSummary handles all-empty lists gracefully")
    void headerSummary_emptyLists() {
        CreditRequest cr = new CreditRequest();

        CreditCalculationService.HeaderSummary summary = service.computeHeaderSummary(
                cr, List.of(), List.of(), List.of());

        assertThat(summary.requestedQty()).isEqualTo(0);
        assertThat(summary.requestedSkus()).isEqualTo(0);
        assertThat(summary.requestedTotal()).isEqualByComparingTo("0");
        assertThat(summary.approvedQty()).isEqualTo(0);
        assertThat(summary.approvedSkus()).isEqualTo(0);
        assertThat(summary.approvedTotal()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("computeHeaderSummary handles null line lists as empty")
    void headerSummary_nullLists() {
        CreditRequest cr = new CreditRequest();

        CreditCalculationService.HeaderSummary summary = service.computeHeaderSummary(
                cr, null, null, null);

        assertThat(summary.requestedQty()).isEqualTo(0);
        assertThat(summary.approvedTotal()).isEqualByComparingTo("0");
    }

    // -------------------------------------------------------------------
    // resolveWeekIdForCredit
    // -------------------------------------------------------------------

    @Test
    @DisplayName("resolveWeekIdForCredit returns weekId when WeekRepository finds the week")
    void resolveWeekId_happyPath() throws Exception {
        CreditRequest cr = new CreditRequest();
        cr.setOrderCreatedDate(Instant.parse("2026-04-15T10:00:00Z"));
        Week week = newWeek(7777L);

        when(weekRepository.findByDate(any(Instant.class)))
                .thenReturn(Optional.of(week));

        Optional<Long> result = service.resolveWeekIdForCredit(cr);

        assertThat(result).contains(7777L);
    }

    @Test
    @DisplayName("resolveWeekIdForCredit returns empty when orderCreatedDate is null")
    void resolveWeekId_nullOrderDate() {
        CreditRequest cr = new CreditRequest();
        cr.setOrderCreatedDate(null);

        Optional<Long> result = service.resolveWeekIdForCredit(cr);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("resolveWeekIdForCredit returns empty when CreditRequest is null")
    void resolveWeekId_nullRequest() {
        assertThat(service.resolveWeekIdForCredit(null)).isEmpty();
    }

    @Test
    @DisplayName("resolveWeekIdForCredit returns empty when no week covers the date")
    void resolveWeekId_noMatchingWeek() {
        CreditRequest cr = new CreditRequest();
        cr.setOrderCreatedDate(Instant.parse("2099-01-01T00:00:00Z"));

        when(weekRepository.findByDate(any(Instant.class)))
                .thenReturn(Optional.empty());

        assertThat(service.resolveWeekIdForCredit(cr)).isEmpty();
    }

    // -------------------------------------------------------------------
    // Test fixtures
    // -------------------------------------------------------------------

    private static MissingDeviceLine newMissingLine(BigDecimal amountPaid,
                                                     ReviewDecision decision) {
        MissingDeviceLine line = new MissingDeviceLine();
        line.setBarcodeSubmitted("BC-MISS-" + System.nanoTime());
        line.setAmountPaid(amountPaid);
        line.setReviewDecision(decision);
        return line;
    }

    private static WrongDeviceLine newWrongLine(BigDecimal expectedAmountPaid,
                                                 BigDecimal latestPrice,
                                                 ReviewDecision decision) {
        WrongDeviceLine line = new WrongDeviceLine();
        line.setExpectedBarcode("BC-WRONG-" + System.nanoTime());
        line.setExpectedAmountPaid(expectedAmountPaid);
        line.setLatestPrice(latestPrice);
        line.setReviewDecision(decision);
        return line;
    }

    private static EncumberedDeviceLine newEncumberedLine(BigDecimal amountPaid,
                                                           BigDecimal actualValue,
                                                           ReviewDecision decision) {
        EncumberedDeviceLine line = new EncumberedDeviceLine();
        line.setBarcodeSubmitted("BC-ENC-" + System.nanoTime());
        line.setAmountPaid(amountPaid);
        line.setActualValue(actualValue);
        line.setReviewDecision(decision);
        return line;
    }

    /**
     * Week entity has no public id setter (immutable from JPA's
     * perspective). Reflection avoids polluting the production type
     * with a test-only mutator.
     */
    private static Week newWeek(long id) throws Exception {
        Week w = new Week();
        Field f = Week.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(w, id);
        return w;
    }
}
