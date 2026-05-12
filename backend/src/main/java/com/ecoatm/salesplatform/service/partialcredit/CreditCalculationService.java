package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Pure credit-calc engine for the partial-credit admin review surface
 * (Sprint 3, SPKB-3660 — see {@code docs/tasks/partial-credit-sprint3-implementation-plan.md}).
 *
 * <p>Two responsibilities:
 * <ul>
 *   <li>Per-line credit math: {@code computeLineCredit(...)} for each of
 *       the three line types. Pure functions — no DB, no transactions,
 *       safe to call from anywhere in the request lifecycle.</li>
 *   <li>Header summary aggregation: {@code computeHeaderSummary(...)}
 *       collapses the three line tables down to the six header counter
 *       fields ({@code requested_*} + {@code approved_*}). Pure too —
 *       caller is responsible for persisting the result to
 *       {@code credit_requests}.</li>
 * </ul>
 *
 * <p>Per the §11 RESOLVED decisions in the Sprint 3 plan, this engine
 * never throws on a null {@code latestPrice} or {@code actualValue} —
 * the recommendation engine (Chunk 2) flags those lines for reviewer
 * attention separately. Treating null defensively as zero keeps the
 * calc total a safe lower bound.
 *
 * <p>Week lookup is exposed via {@link #resolveWeekIdForCredit} so the
 * orchestration layer (Chunk 3) can derive the {@code weekId} argument
 * for {@link MaxSubmittedBidLookup} from
 * {@code CreditRequest.orderCreatedDate}.
 */
@Service
public class CreditCalculationService {

    private final WeekRepository weekRepository;

    public CreditCalculationService(WeekRepository weekRepository) {
        this.weekRepository = weekRepository;
    }

    // -------------------------------------------------------------------
    // Per-line credit math
    // -------------------------------------------------------------------

    /**
     * Missing device: full credit equals what the buyer paid for the
     * absent unit. Returns zero when {@code amountPaid} is null (defensive).
     *
     * <p>The method is pure — caller filters by
     * {@code reviewDecision = ACCEPTED} before persisting; the result is
     * the same regardless of the decision state.
     */
    public BigDecimal computeLineCredit(MissingDeviceLine line) {
        return nullToZero(line.getAmountPaid());
    }

    /**
     * Wrong device: credit equals the gap between paid and what the
     * received device is actually worth (max submitted bid). Floored at
     * zero so a receipt-worth-more-than-paid scenario yields no credit
     * (the recommendation engine auto-declines those anyway, but the
     * floor defends against reviewer override).
     *
     * <p>{@code latestPrice == null} returns zero — the resolver
     * upstream failed to find the device or the bid_data had no rows;
     * the recommendation engine flags those cases.
     */
    public BigDecimal computeLineCredit(WrongDeviceLine line) {
        BigDecimal paid = nullToZero(line.getExpectedAmountPaid());
        BigDecimal latest = line.getLatestPrice();
        if (latest == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = paid.subtract(latest);
        return diff.signum() > 0 ? diff : BigDecimal.ZERO;
    }

    /**
     * Encumbered device: credit equals the reviewer-entered
     * {@code actualValue}. Null defaults to zero.
     */
    public BigDecimal computeLineCredit(EncumberedDeviceLine line) {
        return nullToZero(line.getActualValue());
    }

    // -------------------------------------------------------------------
    // Header summary aggregation
    // -------------------------------------------------------------------

    /**
     * Recomputes the six header counter fields ({@code requested_skus},
     * {@code requested_qty}, {@code requested_total} + the
     * {@code approved_*} trio) from the three line collections of a
     * single request. Pure — caller persists.
     *
     * <p>Per §8 of the cross-repo impl plan:
     * <ul>
     *   <li>{@code requestedSkus} = distinct {@code ecoatm_code}. Today
     *       only {@code wrongDeviceLines.expectedEcoatmCode} carries
     *       this; missing + encumbered lines have no ecoatm_code yet
     *       (Sprint-2 schema). They contribute to qty + total but not
     *       to skus.</li>
     *   <li>{@code requestedQty} = total count of all lines.</li>
     *   <li>{@code requestedTotal} = sum of {@code amountPaid} (missing
     *       / encumbered) + {@code expectedAmountPaid} (wrong).</li>
     *   <li>{@code approved_*} = same shape but filtered to
     *       {@code reviewDecision = ACCEPTED}. {@code approvedTotal} sums
     *       the per-line {@code computeLineCredit} results.</li>
     * </ul>
     */
    public HeaderSummary computeHeaderSummary(CreditRequest cr,
                                              List<MissingDeviceLine> missingLines,
                                              List<WrongDeviceLine> wrongLines,
                                              List<EncumberedDeviceLine> encumberedLines) {
        // CreditRequest is accepted for future use (e.g. header-only
        // overrides) but is unused in the current aggregate — request
        // identity is implicit in the caller's line filters.
        List<MissingDeviceLine> missing = nullSafe(missingLines);
        List<WrongDeviceLine> wrong = nullSafe(wrongLines);
        List<EncumberedDeviceLine> encumbered = nullSafe(encumberedLines);

        Set<String> requestedSkus = new HashSet<>();
        Set<String> approvedSkus = new HashSet<>();
        int requestedQty = 0;
        int approvedQty = 0;
        BigDecimal requestedTotal = BigDecimal.ZERO;
        BigDecimal approvedTotal = BigDecimal.ZERO;

        for (MissingDeviceLine line : missing) {
            requestedQty++;
            requestedTotal = requestedTotal.add(nullToZero(line.getAmountPaid()));
            if (line.getReviewDecision() == ReviewDecision.ACCEPTED) {
                approvedQty++;
                approvedTotal = approvedTotal.add(computeLineCredit(line));
            }
        }

        for (WrongDeviceLine line : wrong) {
            requestedQty++;
            requestedTotal = requestedTotal.add(nullToZero(line.getExpectedAmountPaid()));
            addIfNotBlank(requestedSkus, line.getExpectedEcoatmCode());
            if (line.getReviewDecision() == ReviewDecision.ACCEPTED) {
                approvedQty++;
                approvedTotal = approvedTotal.add(computeLineCredit(line));
                addIfNotBlank(approvedSkus, line.getExpectedEcoatmCode());
            }
        }

        for (EncumberedDeviceLine line : encumbered) {
            requestedQty++;
            requestedTotal = requestedTotal.add(nullToZero(line.getAmountPaid()));
            if (line.getReviewDecision() == ReviewDecision.ACCEPTED) {
                approvedQty++;
                approvedTotal = approvedTotal.add(computeLineCredit(line));
            }
        }

        return new HeaderSummary(
                requestedSkus.size(), requestedQty, requestedTotal,
                approvedSkus.size(), approvedQty, approvedTotal);
    }

    // -------------------------------------------------------------------
    // Week resolution
    // -------------------------------------------------------------------

    /**
     * Looks up the auction week containing the request's
     * {@code orderCreatedDate}. Used by the orchestration layer (Chunk 3)
     * to derive the {@code weekId} argument for
     * {@link MaxSubmittedBidLookup#maxSubmittedBid}.
     *
     * <p>Returns empty when the request has no order date or no week
     * row covers it.
     */
    public Optional<Long> resolveWeekIdForCredit(CreditRequest cr) {
        if (cr == null || cr.getOrderCreatedDate() == null) {
            return Optional.empty();
        }
        return weekRepository.findByDate(cr.getOrderCreatedDate())
                .map(w -> w.getId());
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static void addIfNotBlank(Set<String> sink, String value) {
        if (value != null && !value.isBlank()) {
            sink.add(value);
        }
    }

    /**
     * The six counter fields shown on the admin "Requested Credit" /
     * "Total Credit" panels (design notes §3.3) and persisted onto
     * {@code credit_requests}.
     *
     * <p>Kept nested to keep the calc engine self-contained — the
     * record has no behavior of its own and is meaningless outside the
     * service.
     */
    public record HeaderSummary(int requestedSkus, int requestedQty,
                                BigDecimal requestedTotal,
                                int approvedSkus, int approvedQty,
                                BigDecimal approvedTotal) {}
}
