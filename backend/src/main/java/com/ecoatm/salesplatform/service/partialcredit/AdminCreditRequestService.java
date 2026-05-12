package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ActionRecommendation;
import com.ecoatm.salesplatform.model.partialcredit.enums.PrologResult;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.CreditCalculationService.HeaderSummary;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Admin-side orchestration for the partial-credit review workflow
 * (Sprint 3 chunk 3 — SPKB-3658, SPKB-3660, SPKB-3661, SPKB-3663).
 *
 * <p>Sits next to {@link CreditRequestService} (buyer side) instead of
 * extending it, per the §3.6 RESOLVED design decision: the buyer service
 * carries buyer-code scoping in every call; admin paths skip that gate
 * and operate on any request regardless of ownership. Mixing the two
 * would muddle the contract.
 *
 * <p>Composes the Wave-1 services landed in commits ba63e71 + a84a8c9:
 * <ul>
 *   <li>{@link CreditCalculationService} — per-line credit math + header
 *       summary aggregation</li>
 *   <li>{@link ActionRecommendationService} — wrong-device default
 *       Accept/Decline calibration</li>
 *   <li>{@link MaxSubmittedBidLookup} — caches {@code latest_price} on
 *       wrong-device lines at review-open</li>
 *   <li>{@link ResolveReceivedDeviceService} — resolves the buyer-entered
 *       IMEI/model string to a canonical device tuple</li>
 * </ul>
 *
 * <p>Each public method is annotated {@code @Transactional}; the
 * combination of repository writes + the per-line re-recompute fan-out
 * must commit or roll back together so a partial-bulk failure doesn't
 * leave the request in a half-saved state.
 */
@Service
@Transactional
public class AdminCreditRequestService {

    /**
     * Indicates which of the three line tables a per-line decision call
     * targets. Used by {@link #setLineDecision} +
     * {@link #setSectionDecision} so the orchestration layer can route to
     * the right repository without sniffing types.
     */
    public enum LineKind {
        MISSING,
        WRONG,
        ENCUMBERED
    }

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestStatusRepository statusRepository;
    private final MissingDeviceLineRepository missingDeviceLineRepository;
    private final WrongDeviceLineRepository wrongDeviceLineRepository;
    private final EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    private final CreditCalculationService creditCalculationService;
    private final ActionRecommendationService actionRecommendationService;
    private final MaxSubmittedBidLookup maxSubmittedBidLookup;
    private final ResolveReceivedDeviceService resolveReceivedDeviceService;
    private final ApplicationEventPublisher eventPublisher;

    public AdminCreditRequestService(
            CreditRequestRepository creditRequestRepository,
            CreditRequestStatusRepository statusRepository,
            MissingDeviceLineRepository missingDeviceLineRepository,
            WrongDeviceLineRepository wrongDeviceLineRepository,
            EncumberedDeviceLineRepository encumberedDeviceLineRepository,
            CreditCalculationService creditCalculationService,
            ActionRecommendationService actionRecommendationService,
            MaxSubmittedBidLookup maxSubmittedBidLookup,
            ResolveReceivedDeviceService resolveReceivedDeviceService,
            ApplicationEventPublisher eventPublisher) {
        this.creditRequestRepository = creditRequestRepository;
        this.statusRepository = statusRepository;
        this.missingDeviceLineRepository = missingDeviceLineRepository;
        this.wrongDeviceLineRepository = wrongDeviceLineRepository;
        this.encumberedDeviceLineRepository = encumberedDeviceLineRepository;
        this.creditCalculationService = creditCalculationService;
        this.actionRecommendationService = actionRecommendationService;
        this.maxSubmittedBidLookup = maxSubmittedBidLookup;
        this.resolveReceivedDeviceService = resolveReceivedDeviceService;
        this.eventPublisher = eventPublisher;
    }

    // -------------------------------------------------------------------
    // Landing list
    // -------------------------------------------------------------------

    /**
     * Returns the admin landing list filtered by the supplied criteria.
     * Filtering happens in-memory after a JPA fetch — this is acceptable
     * for Sprint 3 because the partial-credit table is small (hundreds,
     * not millions). Sprint 4 may push the filter down to JPQL if growth
     * demands.
     *
     * <p>Per the §11 RESOLVED decision (Q5), `colorHex` is read live from
     * the database; admin status-config edits propagate without redeploy.
     */
    @Transactional(readOnly = true)
    public Page<CreditRequest> listForAdmin(AdminListFilter filter, Pageable pageable) {
        Objects.requireNonNull(filter, "filter must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");

        // Pull the full set first — the partial-credit table is small.
        // We sort by request_date desc to match the buyer-side landing
        // ordering shown to admins by default.
        List<CreditRequest> all = creditRequestRepository.findAll().stream()
                .sorted(Comparator.comparing(CreditRequest::getRequestDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        Map<Long, SystemStatus> systemStatusById = new HashMap<>();
        for (CreditRequestStatus row : statusRepository.findAll()) {
            systemStatusById.put(row.getId(), row.getSystemStatus());
        }

        List<CreditRequest> filtered = new ArrayList<>();
        for (CreditRequest cr : all) {
            if (matchesFilter(cr, filter, systemStatusById)) {
                filtered.add(cr);
            }
        }

        // Slice the filtered list to the requested page window.
        int total = filtered.size();
        int from = Math.min((int) pageable.getOffset(), total);
        int to = Math.min(from + pageable.getPageSize(), total);
        return new PageImpl<>(filtered.subList(from, to), pageable, total);
    }

    /**
     * Returns the four landing-chip counters (PENDING_APPROVAL,
     * UNDER_REVIEW, APPROVED, DECLINED). The DRAFT bucket is intentionally
     * excluded — admins never review drafts.
     */
    @Transactional(readOnly = true)
    public AdminStatusCounters statusCounters() {
        EnumMap<SystemStatus, Integer> counts = new EnumMap<>(SystemStatus.class);
        for (SystemStatus s : SystemStatus.values()) {
            counts.put(s, 0);
        }

        Map<Long, SystemStatus> systemStatusById = new HashMap<>();
        for (CreditRequestStatus row : statusRepository.findAll()) {
            systemStatusById.put(row.getId(), row.getSystemStatus());
        }

        for (CreditRequest cr : creditRequestRepository.findAll()) {
            SystemStatus s = systemStatusById.get(cr.getStatusId());
            if (s != null) {
                counts.merge(s, 1, Integer::sum);
            }
        }
        return new AdminStatusCounters(
                counts.get(SystemStatus.PENDING_APPROVAL),
                counts.get(SystemStatus.UNDER_REVIEW),
                counts.get(SystemStatus.APPROVED),
                counts.get(SystemStatus.DECLINED));
    }

    // -------------------------------------------------------------------
    // Open for review (state transition + side effects)
    // -------------------------------------------------------------------

    /**
     * First-reviewer wins: transitions {@code PENDING_APPROVAL ->
     * UNDER_REVIEW} on the first call, stamps {@code reviewedById}, and
     * pre-computes the wrong-device per-line {@code latest_price} +
     * {@code action_recommendation} cache.
     *
     * <p>Idempotent: subsequent calls (or calls from a different
     * reviewer) on an already-UNDER_REVIEW request leave the status alone
     * and skip the latest_price recompute. Per the Confluence rule —
     * status moves to UNDER_REVIEW the first time ANY reviewer opens;
     * subsequent opens by anyone leave it in UNDER_REVIEW.
     */
    public OpenReviewResult openForReview(Long requestId, Long reviewerUserId) {
        CreditRequest cr = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + requestId));
        SystemStatus current = systemStatusOf(cr);

        boolean transitioned = false;
        if (current == SystemStatus.PENDING_APPROVAL) {
            CreditRequestStatus underReview = statusRepository.findBySystemStatus(SystemStatus.UNDER_REVIEW)
                    .orElseThrow(() -> new IllegalStateException(
                            "UNDER_REVIEW status row missing — V89 seed not applied"));
            cr.setStatusId(underReview.getId());
            cr.setReviewedById(reviewerUserId);
            cr.setChangedDate(Instant.now());
            cr.setChangedById(reviewerUserId);
            creditRequestRepository.save(cr);
            transitioned = true;

            // Compute the latest_price + action_recommendation cache on
            // every wrong-device line. This is idempotent — subsequent
            // openForReview calls on the same UNDER_REVIEW request skip
            // this branch entirely.
            primeWrongDeviceLines(cr, reviewerUserId);
        } else if (current != SystemStatus.UNDER_REVIEW) {
            // Re-opens of APPROVED/DECLINED/DRAFT are nonsensical — the
            // controller should never call us in those states, but we
            // refuse defensively.
            throw new IllegalStateException(
                    "Cannot open for review: request " + requestId + " is in " + current);
        }

        List<MissingDeviceLine> missing = missingDeviceLineRepository.findByCreditRequestIdOrderById(requestId);
        List<WrongDeviceLine> wrong = wrongDeviceLineRepository.findByCreditRequestIdOrderById(requestId);
        List<EncumberedDeviceLine> encumbered = encumberedDeviceLineRepository.findByCreditRequestIdOrderById(requestId);
        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr, missing, wrong, encumbered);

        return new OpenReviewResult(cr, missing, wrong, encumbered, summary, transitioned);
    }

    /**
     * Walks every wrong-device line and caches:
     * <ul>
     *   <li>{@code actualEcoatmCode} + {@code actualGrade} via
     *       {@link ResolveReceivedDeviceService} (when not already set
     *       from a Sprint-2 submit)</li>
     *   <li>{@code latestPrice} via {@link MaxSubmittedBidLookup} keyed
     *       by the week containing {@code orderCreatedDate}</li>
     *   <li>{@code actionRecommendation} via
     *       {@link ActionRecommendationService}</li>
     * </ul>
     * The Sprint-2 wizard captures {@code actualImeiOrModel} as a freeform
     * buyer entry; the resolve step on review-open canonicalises it so
     * the rest of the pipeline runs against {@code mdm.device} keys.
     */
    private void primeWrongDeviceLines(CreditRequest cr, Long reviewerUserId) {
        Long weekId = creditCalculationService.resolveWeekIdForCredit(cr).orElse(null);
        List<WrongDeviceLine> lines = wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId());
        Instant now = Instant.now();

        for (WrongDeviceLine line : lines) {
            // Resolve the actual device from the buyer's entry when the
            // line doesn't already carry an ecoatm code. Phase-1
            // {@code ResolveReceivedDeviceService} returns empty for
            // IMEI-shaped inputs; recommendation engine treats that as
            // "unclassified" and defaults to DECLINE.
            if (isBlank(line.getActualEcoatmCode()) && line.getActualImeiOrModel() != null) {
                resolveReceivedDeviceService.resolve(line.getActualImeiOrModel())
                        .ifPresent(match -> {
                            line.setActualEcoatmCode(match.ecoatmCode());
                            line.setActualGrade(match.mergedGrade());
                            line.setActualBrand(match.brand());
                            line.setActualModel(match.model());
                        });
            }

            // Refresh latest_price every open — the bid_data aggregate can
            // change between reviewer sessions. We only persist when the
            // value actually changes to avoid noisy changed_by_id churn.
            BigDecimal newLatest = null;
            if (weekId != null && line.getActualEcoatmCode() != null && line.getActualGrade() != null) {
                newLatest = maxSubmittedBidLookup
                        .maxSubmittedBid(weekId, line.getActualEcoatmCode(), line.getActualGrade())
                        .orElse(null);
            }
            line.setLatestPrice(newLatest);
            line.setLatestPriceComputedOn(now);

            // Recompute the recommendation last so it observes whatever
            // resolve + latest_price just produced. Idempotent unless one
            // of those inputs changed.
            ActionRecommendation recommendation =
                    actionRecommendationService.recommend(line, newLatest);
            line.setActionRecommendation(recommendation);
            line.setChangedDate(now);
            line.setChangedById(reviewerUserId);
            wrongDeviceLineRepository.save(line);
        }
    }

    // -------------------------------------------------------------------
    // Per-line / per-section / global decision
    // -------------------------------------------------------------------

    /**
     * Apply a reviewer decision to one line. Recomputes the per-line
     * credit + the header summary. The request must be in UNDER_REVIEW.
     */
    public LineDecisionResult setLineDecision(Long requestId, Long lineId, LineKind kind,
                                              ReviewDecision decision, Long reviewerUserId) {
        Objects.requireNonNull(kind, "kind must not be null");
        Objects.requireNonNull(decision, "decision must not be null");
        CreditRequest cr = loadUnderReview(requestId);
        Instant now = Instant.now();

        Object updatedLine = applyDecisionToLine(cr.getId(), lineId, kind, decision, reviewerUserId, now);
        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr,
                missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        return new LineDecisionResult(updatedLine, summary);
    }

    /**
     * Apply a reviewer decision to every line of one kind on the request.
     * Per the §11 RESOLVED decisions, bulk applies to ALL lines (not just
     * PENDING ones) so an admin can flip an already-decided section in
     * one click.
     */
    public SectionDecisionResult setSectionDecision(Long requestId, LineKind kind,
                                                    ReviewDecision decision, Long reviewerUserId) {
        Objects.requireNonNull(kind, "kind must not be null");
        Objects.requireNonNull(decision, "decision must not be null");
        CreditRequest cr = loadUnderReview(requestId);
        Instant now = Instant.now();

        int updated = applyDecisionToSection(cr.getId(), kind, decision, reviewerUserId, now);

        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr,
                missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        return new SectionDecisionResult(kind, updated, summary);
    }

    /**
     * Bulk apply a reviewer decision to every line across all three
     * sections in one transaction. Convenience entry-point for the
     * "Approve All" / "Decline All" header buttons.
     */
    public GlobalDecisionResult setGlobalDecision(Long requestId, ReviewDecision decision,
                                                   Long reviewerUserId) {
        Objects.requireNonNull(decision, "decision must not be null");
        CreditRequest cr = loadUnderReview(requestId);
        Instant now = Instant.now();

        int missingUpdated = applyDecisionToSection(cr.getId(), LineKind.MISSING, decision, reviewerUserId, now);
        int wrongUpdated = applyDecisionToSection(cr.getId(), LineKind.WRONG, decision, reviewerUserId, now);
        int encumberedUpdated = applyDecisionToSection(cr.getId(), LineKind.ENCUMBERED, decision, reviewerUserId, now);

        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr,
                missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        return new GlobalDecisionResult(missingUpdated, wrongUpdated, encumberedUpdated, summary);
    }

    private Object applyDecisionToLine(Long requestId, Long lineId, LineKind kind,
                                       ReviewDecision decision, Long reviewerUserId, Instant now) {
        switch (kind) {
            case MISSING -> {
                MissingDeviceLine line = missingDeviceLineRepository.findById(lineId)
                        .orElseThrow(() -> new EntityNotFoundException("MissingDeviceLine " + lineId));
                requireOwnership(line.getCreditRequestId(), requestId, "missing");
                line.setReviewDecision(decision);
                line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                        ? creditCalculationService.computeLineCredit(line)
                        : BigDecimal.ZERO);
                line.setChangedDate(now);
                line.setChangedById(reviewerUserId);
                return missingDeviceLineRepository.save(line);
            }
            case WRONG -> {
                WrongDeviceLine line = wrongDeviceLineRepository.findById(lineId)
                        .orElseThrow(() -> new EntityNotFoundException("WrongDeviceLine " + lineId));
                requireOwnership(line.getCreditRequestId(), requestId, "wrong");
                line.setReviewDecision(decision);
                line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                        ? creditCalculationService.computeLineCredit(line)
                        : BigDecimal.ZERO);
                line.setChangedDate(now);
                line.setChangedById(reviewerUserId);
                return wrongDeviceLineRepository.save(line);
            }
            case ENCUMBERED -> {
                EncumberedDeviceLine line = encumberedDeviceLineRepository.findById(lineId)
                        .orElseThrow(() -> new EntityNotFoundException("EncumberedDeviceLine " + lineId));
                requireOwnership(line.getCreditRequestId(), requestId, "encumbered");
                line.setReviewDecision(decision);
                line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                        ? creditCalculationService.computeLineCredit(line)
                        : BigDecimal.ZERO);
                line.setChangedDate(now);
                line.setChangedById(reviewerUserId);
                return encumberedDeviceLineRepository.save(line);
            }
        }
        // Unreachable — switch is exhaustive over LineKind. Required because
        // the compiler can't prove exhaustiveness without explicit return.
        throw new IllegalStateException("unreachable: kind=" + kind);
    }

    private int applyDecisionToSection(Long requestId, LineKind kind, ReviewDecision decision,
                                        Long reviewerUserId, Instant now) {
        int count = 0;
        switch (kind) {
            case MISSING -> {
                for (MissingDeviceLine line : missingDeviceLineRepository.findByCreditRequestIdOrderById(requestId)) {
                    line.setReviewDecision(decision);
                    line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                            ? creditCalculationService.computeLineCredit(line)
                            : BigDecimal.ZERO);
                    line.setChangedDate(now);
                    line.setChangedById(reviewerUserId);
                    missingDeviceLineRepository.save(line);
                    count++;
                }
            }
            case WRONG -> {
                for (WrongDeviceLine line : wrongDeviceLineRepository.findByCreditRequestIdOrderById(requestId)) {
                    line.setReviewDecision(decision);
                    line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                            ? creditCalculationService.computeLineCredit(line)
                            : BigDecimal.ZERO);
                    line.setChangedDate(now);
                    line.setChangedById(reviewerUserId);
                    wrongDeviceLineRepository.save(line);
                    count++;
                }
            }
            case ENCUMBERED -> {
                for (EncumberedDeviceLine line : encumberedDeviceLineRepository.findByCreditRequestIdOrderById(requestId)) {
                    line.setReviewDecision(decision);
                    line.setAmountToCredit(decision == ReviewDecision.ACCEPTED
                            ? creditCalculationService.computeLineCredit(line)
                            : BigDecimal.ZERO);
                    line.setChangedDate(now);
                    line.setChangedById(reviewerUserId);
                    encumberedDeviceLineRepository.save(line);
                    count++;
                }
            }
        }
        return count;
    }

    private void requireOwnership(Long actualRequestId, Long expectedRequestId, String kind) {
        if (actualRequestId == null || !actualRequestId.equals(expectedRequestId)) {
            // Defensive — a reviewer mis-clicking shouldn't cross-write
            // a line that belongs to a different request, even if line
            // ids happen to collide.
            throw new EntityNotFoundException(
                    "Line does not belong to request " + expectedRequestId + " (kind=" + kind + ")");
        }
    }

    // -------------------------------------------------------------------
    // Encumbered: reviewer-entered Prolog Result + Actual Value
    // -------------------------------------------------------------------

    /**
     * Reviewer fills in the Prolog Result + Actual Value on one
     * encumbered line. Recomputes that line's credit + the header
     * summary. The request must be in UNDER_REVIEW.
     */
    public EncumberedLineEntry setEncumberedFields(Long requestId, Long lineId,
                                                    PrologResult prologResult,
                                                    BigDecimal actualValue,
                                                    Long reviewerUserId) {
        Objects.requireNonNull(prologResult, "prologResult must not be null");
        CreditRequest cr = loadUnderReview(requestId);
        EncumberedDeviceLine line = encumberedDeviceLineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("EncumberedDeviceLine " + lineId));
        requireOwnership(line.getCreditRequestId(), requestId, "encumbered");

        line.setPrologResult(prologResult);
        line.setActualValue(actualValue);
        // Recompute the line credit using the new actualValue. The credit
        // value is sticky regardless of decision state — header summary
        // still filters by reviewDecision = ACCEPTED before summing.
        if (line.getReviewDecision() == ReviewDecision.ACCEPTED) {
            line.setAmountToCredit(creditCalculationService.computeLineCredit(line));
        }
        Instant now = Instant.now();
        line.setChangedDate(now);
        line.setChangedById(reviewerUserId);
        encumberedDeviceLineRepository.save(line);

        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr,
                missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()),
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        return new EncumberedLineEntry(line, summary);
    }

    // -------------------------------------------------------------------
    // Complete review (terminal transition)
    // -------------------------------------------------------------------

    /**
     * Final transition: validates every line carries a non-PENDING
     * decision, then flips the request to APPROVED or DECLINED and
     * publishes {@link ReviewCompletedEvent} for the email listener.
     *
     * <p>Per the §11 RESOLVED Q4 decision the outcome is reviewer-chosen,
     * not auto-derived from the line decisions — the UI pre-selects a
     * default but reviewers can always override.
     */
    public CompleteReviewResult completeReview(Long requestId, SystemStatus outcome,
                                                Long reviewerUserId) {
        if (outcome != SystemStatus.APPROVED && outcome != SystemStatus.DECLINED) {
            throw new CreditRequestValidationException(List.of(
                    new ValidationIssue("INVALID_OUTCOME",
                            "Outcome must be APPROVED or DECLINED, got " + outcome)));
        }

        CreditRequest cr = loadUnderReview(requestId);
        List<MissingDeviceLine> missing = missingDeviceLineRepository.findByCreditRequestIdOrderById(requestId);
        List<WrongDeviceLine> wrong = wrongDeviceLineRepository.findByCreditRequestIdOrderById(requestId);
        List<EncumberedDeviceLine> encumbered = encumberedDeviceLineRepository.findByCreditRequestIdOrderById(requestId);

        if (hasPendingLines(missing, wrong, encumbered)) {
            throw new CreditRequestValidationException(List.of(
                    new ValidationIssue("LINES_PENDING_DECISION",
                            "Every line must have an Accept or Decline decision before completing review.")));
        }

        CreditRequestStatus terminal = statusRepository.findBySystemStatus(outcome)
                .orElseThrow(() -> new IllegalStateException(
                        outcome.name() + " status row missing — V89 seed not applied"));
        Instant now = Instant.now();
        cr.setStatusId(terminal.getId());
        cr.setReviewCompletedOn(now);
        cr.setReviewedById(reviewerUserId);
        cr.setChangedDate(now);
        cr.setChangedById(reviewerUserId);

        // Snapshot the final header summary onto the request row so the
        // landing page can render the approved totals without a per-row
        // re-aggregate read. The recompute also ensures the totals
        // reflect the latest per-line decisions.
        HeaderSummary summary = recomputeAndPersistHeaderSummary(cr, missing, wrong, encumbered);
        creditRequestRepository.save(cr);

        // Publish AFTER the save so a downstream rollback (which doesn't
        // happen today but might in Sprint 4 if we add a post-save audit
        // write) doesn't leave a phantom email queued.
        eventPublisher.publishEvent(new ReviewCompletedEvent(
                cr.getId(), outcome, reviewerUserId, now));

        return new CompleteReviewResult(cr, outcome, summary, missing, wrong, encumbered);
    }

    private boolean hasPendingLines(List<MissingDeviceLine> missing,
                                     List<WrongDeviceLine> wrong,
                                     List<EncumberedDeviceLine> encumbered) {
        for (MissingDeviceLine line : missing) {
            if (line.getReviewDecision() == ReviewDecision.PENDING) return true;
        }
        for (WrongDeviceLine line : wrong) {
            if (line.getReviewDecision() == ReviewDecision.PENDING) return true;
        }
        for (EncumberedDeviceLine line : encumbered) {
            if (line.getReviewDecision() == ReviewDecision.PENDING) return true;
        }
        return false;
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private CreditRequest loadUnderReview(Long requestId) {
        CreditRequest cr = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + requestId));
        SystemStatus s = systemStatusOf(cr);
        if (s != SystemStatus.UNDER_REVIEW) {
            throw new CreditRequestValidationException(List.of(
                    new ValidationIssue("NOT_UNDER_REVIEW",
                            "Request " + requestId + " is not under review (status=" + s + ").")));
        }
        return cr;
    }

    private SystemStatus systemStatusOf(CreditRequest cr) {
        return statusRepository.findById(cr.getStatusId())
                .map(CreditRequestStatus::getSystemStatus)
                .orElseThrow(() -> new IllegalStateException(
                        "Status row missing for credit request " + cr.getId()));
    }

    private HeaderSummary recomputeAndPersistHeaderSummary(
            CreditRequest cr,
            List<MissingDeviceLine> missing,
            List<WrongDeviceLine> wrong,
            List<EncumberedDeviceLine> encumbered) {
        HeaderSummary summary = creditCalculationService.computeHeaderSummary(
                cr, missing, wrong, encumbered);
        cr.setRequestedSkus(summary.requestedSkus());
        cr.setRequestedQty(summary.requestedQty());
        cr.setRequestedTotal(summary.requestedTotal());
        cr.setApprovedSkus(summary.approvedSkus());
        cr.setApprovedQty(summary.approvedQty());
        cr.setApprovedTotal(summary.approvedTotal());
        cr.setTotalDevices(summary.requestedQty());
        creditRequestRepository.save(cr);
        return summary;
    }

    private boolean matchesFilter(CreditRequest cr, AdminListFilter filter,
                                   Map<Long, SystemStatus> systemStatusById) {
        if (filter.status() != null) {
            SystemStatus actual = systemStatusById.get(cr.getStatusId());
            if (actual != filter.status()) {
                return false;
            }
        }
        if (filter.buyerCodeId() != null
                && !filter.buyerCodeId().equals(cr.getBuyerCodeId())) {
            return false;
        }
        if (filter.orderNumber() != null && !filter.orderNumber().isBlank()) {
            String needle = filter.orderNumber().toLowerCase();
            String hay = cr.getOrderNumber() == null ? "" : cr.getOrderNumber().toLowerCase();
            if (!hay.contains(needle)) {
                return false;
            }
        }
        if (filter.reason() != null) {
            if (!matchesReason(cr, filter.reason())) {
                return false;
            }
        }
        if (filter.dateFrom() != null && cr.getRequestDate() != null) {
            Instant from = filter.dateFrom().atStartOfDay()
                    .atZone(java.time.ZoneOffset.UTC).toInstant();
            if (cr.getRequestDate().isBefore(from)) {
                return false;
            }
        }
        if (filter.dateTo() != null && cr.getRequestDate() != null) {
            Instant to = filter.dateTo().plusDays(1).atStartOfDay()
                    .atZone(java.time.ZoneOffset.UTC).toInstant();
            if (!cr.getRequestDate().isBefore(to)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesReason(CreditRequest cr, LineKind reason) {
        return switch (reason) {
            case MISSING -> Boolean.TRUE.equals(cr.getHasMissingDevice());
            case WRONG -> Boolean.TRUE.equals(cr.getHasWrongDevice());
            case ENCUMBERED -> Boolean.TRUE.equals(cr.getHasEncumberedDevice());
        };
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // -------------------------------------------------------------------
    // Public result records (return shapes for the controller)
    // -------------------------------------------------------------------

    /**
     * Filter inputs for the admin landing list. Every field is optional
     * (null = match-all). {@code orderNumber} is a case-insensitive
     * contains match — buyers + reviewers often copy fragments of an
     * order number into the filter.
     */
    public record AdminListFilter(
            SystemStatus status,
            Long buyerCodeId,
            String orderNumber,
            LineKind reason,
            LocalDate dateFrom,
            LocalDate dateTo) {
    }

    /**
     * Status-counter chip values rendered along the top of the admin
     * landing. DRAFT is not surfaced — admins never review drafts.
     */
    public record AdminStatusCounters(
            int pendingApproval,
            int underReview,
            int approved,
            int declined) {
    }

    /**
     * Return shape for {@link #openForReview}. {@code transitioned}
     * indicates whether this call did the PENDING_APPROVAL ->
     * UNDER_REVIEW state flip (so the controller can emit an audit
     * marker when it's true).
     */
    public record OpenReviewResult(
            CreditRequest creditRequest,
            List<MissingDeviceLine> missingLines,
            List<WrongDeviceLine> wrongLines,
            List<EncumberedDeviceLine> encumberedLines,
            HeaderSummary summary,
            boolean transitioned) {
    }

    /**
     * Return shape for {@link #setLineDecision}. {@code updatedLine} is
     * the typed entity for the kind of line that was modified — the
     * controller pattern-matches on it to project the response DTO.
     */
    public record LineDecisionResult(Object updatedLine, HeaderSummary summary) {
    }

    /** Return shape for {@link #setSectionDecision}. */
    public record SectionDecisionResult(LineKind kind, int updatedCount,
                                         HeaderSummary summary) {
    }

    /** Return shape for {@link #setGlobalDecision}. */
    public record GlobalDecisionResult(int missingUpdated,
                                        int wrongUpdated,
                                        int encumberedUpdated,
                                        HeaderSummary summary) {
    }

    /** Return shape for {@link #setEncumberedFields}. */
    public record EncumberedLineEntry(EncumberedDeviceLine line,
                                       HeaderSummary summary) {
    }

    /**
     * Return shape for {@link #completeReview}. Carries the final lines
     * + header so the controller can shape the response (including the
     * email payload context) without a re-read.
     */
    public record CompleteReviewResult(CreditRequest creditRequest,
                                        SystemStatus outcome,
                                        HeaderSummary summary,
                                        List<MissingDeviceLine> missingLines,
                                        List<WrongDeviceLine> wrongLines,
                                        List<EncumberedDeviceLine> encumberedLines) {
    }
}
