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
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminListFilter;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminStatusCounters;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.CompleteReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.EncumberedLineEntry;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.GlobalDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.OpenReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.SectionDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.CreditCalculationService.HeaderSummary;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mockito unit tests for {@link AdminCreditRequestService}. Each test
 * exercises one of the service's eight public methods + at least one
 * validation/branch failure.
 *
 * <p>The bulk of the orchestration logic is plain JPA-CRUD glue around
 * the Wave-1 services; the mocks reflect that — we verify the right
 * sequence of repository calls + event publishes rather than re-asserting
 * the credit-calc / recommendation math (covered in Wave-1 unit tests).
 */
@ExtendWith(MockitoExtension.class)
class AdminCreditRequestServiceTest {

    @Mock CreditRequestRepository creditRequestRepository;
    @Mock CreditRequestStatusRepository statusRepository;
    @Mock MissingDeviceLineRepository missingDeviceLineRepository;
    @Mock WrongDeviceLineRepository wrongDeviceLineRepository;
    @Mock EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    @Mock CreditCalculationService creditCalculationService;
    @Mock ActionRecommendationService actionRecommendationService;
    @Mock MaxSubmittedBidLookup maxSubmittedBidLookup;
    @Mock ResolveReceivedDeviceService resolveReceivedDeviceService;
    @Mock ApplicationEventPublisher eventPublisher;

    AdminCreditRequestService service;

    // Status rows — one per SystemStatus value, stable ids 1..5 for clarity.
    CreditRequestStatus pendingApproval;
    CreditRequestStatus underReview;
    CreditRequestStatus approved;
    CreditRequestStatus declined;

    @BeforeEach
    void setUp() {
        service = new AdminCreditRequestService(
                creditRequestRepository,
                statusRepository,
                missingDeviceLineRepository,
                wrongDeviceLineRepository,
                encumberedDeviceLineRepository,
                creditCalculationService,
                actionRecommendationService,
                maxSubmittedBidLookup,
                resolveReceivedDeviceService,
                eventPublisher);

        pendingApproval = statusRow(2L, SystemStatus.PENDING_APPROVAL);
        underReview = statusRow(3L, SystemStatus.UNDER_REVIEW);
        approved = statusRow(4L, SystemStatus.APPROVED);
        declined = statusRow(5L, SystemStatus.DECLINED);
    }

    // -------------------------------------------------------------------
    // openForReview
    // -------------------------------------------------------------------

    @Test
    @DisplayName("openForReview transitions PENDING_APPROVAL -> UNDER_REVIEW on first open")
    void openForReview_firstOpen_transitions() {
        CreditRequest cr = creditRequest(100L, pendingApproval.getId());
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findBySystemStatus(SystemStatus.UNDER_REVIEW))
                .thenReturn(Optional.of(underReview));
        when(statusRepository.findById(pendingApproval.getId()))
                .thenReturn(Optional.of(pendingApproval));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(emptySummary());
        when(creditCalculationService.resolveWeekIdForCredit(cr))
                .thenReturn(Optional.empty());

        OpenReviewResult result = service.openForReview(100L, 999L);

        assertThat(result.transitioned()).isTrue();
        assertThat(cr.getStatusId()).isEqualTo(underReview.getId());
        assertThat(cr.getReviewedById()).isEqualTo(999L);
        verify(creditRequestRepository, times(2)).save(cr);
    }

    @Test
    @DisplayName("openForReview is idempotent on second open (already UNDER_REVIEW)")
    void openForReview_secondOpen_idempotent() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        cr.setReviewedById(50L);
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(emptySummary());

        OpenReviewResult result = service.openForReview(100L, 999L);

        assertThat(result.transitioned()).isFalse();
        // Reviewer id is NOT overwritten on idempotent re-open.
        assertThat(cr.getReviewedById()).isEqualTo(50L);
        verify(statusRepository, never()).findBySystemStatus(SystemStatus.UNDER_REVIEW);
    }

    @Test
    @DisplayName("openForReview primes latest_price + recommendation on wrong-device lines")
    void openForReview_primesWrongDeviceLines() {
        CreditRequest cr = creditRequest(100L, pendingApproval.getId());
        cr.setOrderCreatedDate(Instant.parse("2026-04-01T00:00:00Z"));

        WrongDeviceLine wrong = new WrongDeviceLine();
        wrong.setId(700L);
        wrong.setCreditRequestId(100L);
        wrong.setExpectedBrand("Apple");
        wrong.setExpectedAmountPaid(new BigDecimal("100.00"));
        wrong.setActualEcoatmCode("ECO-X");
        wrong.setActualGrade("A");

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findBySystemStatus(SystemStatus.UNDER_REVIEW))
                .thenReturn(Optional.of(underReview));
        when(statusRepository.findById(pendingApproval.getId()))
                .thenReturn(Optional.of(pendingApproval));
        when(creditCalculationService.resolveWeekIdForCredit(cr))
                .thenReturn(Optional.of(42L));
        // First call (primer): returns the line. Second call (recompute):
        // also returns the line. Mockito ignores call order on stubs.
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(wrong));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(maxSubmittedBidLookup.maxSubmittedBid(42L, "ECO-X", "A"))
                .thenReturn(Optional.of(new BigDecimal("30.00")));
        when(actionRecommendationService.recommend(eq(wrong), eq(new BigDecimal("30.00"))))
                .thenReturn(ActionRecommendation.ACCEPT);
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(emptySummary());

        OpenReviewResult result = service.openForReview(100L, 999L);

        assertThat(wrong.getLatestPrice()).isEqualByComparingTo("30.00");
        assertThat(wrong.getActionRecommendation()).isEqualTo(ActionRecommendation.ACCEPT);
        verify(wrongDeviceLineRepository).save(wrong);
        assertThat(result.transitioned()).isTrue();
    }

    @Test
    @DisplayName("openForReview throws when request is APPROVED")
    void openForReview_rejectsApproved() {
        CreditRequest cr = creditRequest(100L, approved.getId());
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(approved.getId()))
                .thenReturn(Optional.of(approved));

        assertThatThrownBy(() -> service.openForReview(100L, 999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("APPROVED");
    }

    // -------------------------------------------------------------------
    // setLineDecision
    // -------------------------------------------------------------------

    @Test
    @DisplayName("setLineDecision happy path: updates line + recomputes header summary")
    void setLineDecision_happyPath() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        MissingDeviceLine line = newMissingLine(700L, 100L, new BigDecimal("50.00"));

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(missingDeviceLineRepository.findById(700L)).thenReturn(Optional.of(line));
        when(creditCalculationService.computeLineCredit(line))
                .thenReturn(new BigDecimal("50.00"));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(line));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(new HeaderSummary(0, 1, new BigDecimal("50.00"), 0, 1, new BigDecimal("50.00")));

        LineDecisionResult result = service.setLineDecision(
                100L, 700L, LineKind.MISSING, ReviewDecision.ACCEPTED, 999L);

        assertThat(line.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
        assertThat(line.getAmountToCredit()).isEqualByComparingTo("50.00");
        assertThat(result.summary().approvedQty()).isEqualTo(1);
    }

    @Test
    @DisplayName("setLineDecision rejects when request is not UNDER_REVIEW")
    void setLineDecision_rejectsNotUnderReview() {
        CreditRequest cr = creditRequest(100L, pendingApproval.getId());
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(pendingApproval.getId()))
                .thenReturn(Optional.of(pendingApproval));

        assertThatThrownBy(() -> service.setLineDecision(
                100L, 700L, LineKind.MISSING, ReviewDecision.ACCEPTED, 999L))
                .isInstanceOf(CreditRequestValidationException.class)
                .satisfies(ex -> {
                    var issues = ((CreditRequestValidationException) ex).getIssues();
                    assertThat(issues).hasSize(1);
                    assertThat(issues.get(0).code()).isEqualTo("NOT_UNDER_REVIEW");
                });
    }

    @Test
    @DisplayName("setLineDecision declined sets amount_to_credit to zero")
    void setLineDecision_declined_zerosCredit() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        MissingDeviceLine line = newMissingLine(700L, 100L, new BigDecimal("50.00"));

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(missingDeviceLineRepository.findById(700L)).thenReturn(Optional.of(line));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(line));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(emptySummary());

        service.setLineDecision(100L, 700L, LineKind.MISSING, ReviewDecision.DECLINED, 999L);

        assertThat(line.getReviewDecision()).isEqualTo(ReviewDecision.DECLINED);
        assertThat(line.getAmountToCredit()).isEqualByComparingTo("0");
        // computeLineCredit should NOT be called on declined lines.
        verify(creditCalculationService, never()).computeLineCredit(any(MissingDeviceLine.class));
    }

    // -------------------------------------------------------------------
    // setSectionDecision
    // -------------------------------------------------------------------

    @Test
    @DisplayName("setSectionDecision bulk-applies decision across every line in the section")
    void setSectionDecision_bulk_appliesToAll() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        WrongDeviceLine wrong1 = newWrongLine(700L, 100L, "Apple", new BigDecimal("100.00"), new BigDecimal("30.00"));
        WrongDeviceLine wrong2 = newWrongLine(701L, 100L, "Samsung", new BigDecimal("80.00"), new BigDecimal("20.00"));

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(wrong1, wrong2));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeLineCredit(wrong1)).thenReturn(new BigDecimal("70.00"));
        when(creditCalculationService.computeLineCredit(wrong2)).thenReturn(new BigDecimal("60.00"));
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(new HeaderSummary(2, 2, new BigDecimal("180.00"), 2, 2, new BigDecimal("130.00")));

        SectionDecisionResult result = service.setSectionDecision(
                100L, LineKind.WRONG, ReviewDecision.ACCEPTED, 999L);

        assertThat(result.updatedCount()).isEqualTo(2);
        assertThat(wrong1.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
        assertThat(wrong1.getAmountToCredit()).isEqualByComparingTo("70.00");
        assertThat(wrong2.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
        assertThat(wrong2.getAmountToCredit()).isEqualByComparingTo("60.00");
        verify(wrongDeviceLineRepository).save(wrong1);
        verify(wrongDeviceLineRepository).save(wrong2);
    }

    // -------------------------------------------------------------------
    // setGlobalDecision
    // -------------------------------------------------------------------

    @Test
    @DisplayName("setGlobalDecision applies across all three sections")
    void setGlobalDecision_appliesAcrossAllSections() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        MissingDeviceLine missing = newMissingLine(700L, 100L, new BigDecimal("50.00"));
        WrongDeviceLine wrong = newWrongLine(701L, 100L, "Apple", new BigDecimal("100.00"), new BigDecimal("30.00"));
        EncumberedDeviceLine encumbered = newEncumberedLine(702L, 100L, new BigDecimal("40.00"));

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(missing));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(wrong));
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(encumbered));
        when(creditCalculationService.computeLineCredit(missing)).thenReturn(new BigDecimal("50.00"));
        when(creditCalculationService.computeLineCredit(wrong)).thenReturn(new BigDecimal("70.00"));
        when(creditCalculationService.computeLineCredit(encumbered)).thenReturn(new BigDecimal("40.00"));
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(emptySummary());

        GlobalDecisionResult result = service.setGlobalDecision(
                100L, ReviewDecision.ACCEPTED, 999L);

        assertThat(result.missingUpdated()).isEqualTo(1);
        assertThat(result.wrongUpdated()).isEqualTo(1);
        assertThat(result.encumberedUpdated()).isEqualTo(1);
        assertThat(missing.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
        assertThat(wrong.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
        assertThat(encumbered.getReviewDecision()).isEqualTo(ReviewDecision.ACCEPTED);
    }

    // -------------------------------------------------------------------
    // setEncumberedFields
    // -------------------------------------------------------------------

    @Test
    @DisplayName("setEncumberedFields persists prolog + actual value and recomputes credit for ACCEPTED line")
    void setEncumberedFields_recomputesCredit() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        EncumberedDeviceLine line = newEncumberedLine(702L, 100L, new BigDecimal("40.00"));
        line.setReviewDecision(ReviewDecision.ACCEPTED);

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(encumberedDeviceLineRepository.findById(702L)).thenReturn(Optional.of(line));
        when(creditCalculationService.computeLineCredit(line)).thenReturn(new BigDecimal("25.00"));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(line));
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(new HeaderSummary(0, 1, new BigDecimal("40.00"), 0, 1, new BigDecimal("25.00")));

        EncumberedLineEntry result = service.setEncumberedFields(
                100L, 702L, PrologResult.ENCUMBERED, new BigDecimal("25.00"), 999L);

        assertThat(line.getPrologResult()).isEqualTo(PrologResult.ENCUMBERED);
        assertThat(line.getActualValue()).isEqualByComparingTo("25.00");
        assertThat(line.getAmountToCredit()).isEqualByComparingTo("25.00");
        assertThat(result.summary().approvedTotal()).isEqualByComparingTo("25.00");
    }

    // -------------------------------------------------------------------
    // completeReview
    // -------------------------------------------------------------------

    @Test
    @DisplayName("completeReview happy path flips to APPROVED + publishes event")
    void completeReview_happyPath_publishesEvent() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        MissingDeviceLine line = newMissingLine(700L, 100L, new BigDecimal("50.00"));
        line.setReviewDecision(ReviewDecision.ACCEPTED);

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(statusRepository.findBySystemStatus(SystemStatus.APPROVED))
                .thenReturn(Optional.of(approved));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(line));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(creditCalculationService.computeHeaderSummary(any(), any(), any(), any()))
                .thenReturn(new HeaderSummary(0, 1, new BigDecimal("50.00"), 0, 1, new BigDecimal("50.00")));

        CompleteReviewResult result = service.completeReview(100L, SystemStatus.APPROVED, 999L);

        assertThat(cr.getStatusId()).isEqualTo(approved.getId());
        assertThat(cr.getReviewCompletedOn()).isNotNull();
        assertThat(cr.getReviewedById()).isEqualTo(999L);
        assertThat(result.outcome()).isEqualTo(SystemStatus.APPROVED);

        ArgumentCaptor<ReviewCompletedEvent> captor = ArgumentCaptor.forClass(ReviewCompletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ReviewCompletedEvent event = captor.getValue();
        assertThat(event.requestId()).isEqualTo(100L);
        assertThat(event.outcome()).isEqualTo(SystemStatus.APPROVED);
        assertThat(event.reviewerUserId()).isEqualTo(999L);
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("completeReview rejects when any line still has PENDING decision")
    void completeReview_rejectsPendingLines() {
        CreditRequest cr = creditRequest(100L, underReview.getId());
        MissingDeviceLine accepted = newMissingLine(700L, 100L, new BigDecimal("50.00"));
        accepted.setReviewDecision(ReviewDecision.ACCEPTED);
        MissingDeviceLine pending = newMissingLine(701L, 100L, new BigDecimal("30.00"));
        // pending stays PENDING by default

        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(underReview.getId()))
                .thenReturn(Optional.of(underReview));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(accepted, pending));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.completeReview(100L, SystemStatus.APPROVED, 999L))
                .isInstanceOf(CreditRequestValidationException.class)
                .satisfies(ex -> {
                    var issues = ((CreditRequestValidationException) ex).getIssues();
                    assertThat(issues).hasSize(1);
                    assertThat(issues.get(0).code()).isEqualTo("LINES_PENDING_DECISION");
                });
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("completeReview rejects INVALID_OUTCOME when outcome is not APPROVED/DECLINED")
    void completeReview_rejectsInvalidOutcome() {
        assertThatThrownBy(() -> service.completeReview(100L, SystemStatus.UNDER_REVIEW, 999L))
                .isInstanceOf(CreditRequestValidationException.class)
                .satisfies(ex -> {
                    var issues = ((CreditRequestValidationException) ex).getIssues();
                    assertThat(issues.get(0).code()).isEqualTo("INVALID_OUTCOME");
                });
        verify(eventPublisher, never()).publishEvent(any());
        // Should not even hit the repo because outcome validation runs first.
        verify(creditRequestRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("completeReview rejects when request is not UNDER_REVIEW")
    void completeReview_rejectsNotUnderReview() {
        CreditRequest cr = creditRequest(100L, pendingApproval.getId());
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(pendingApproval.getId()))
                .thenReturn(Optional.of(pendingApproval));

        assertThatThrownBy(() -> service.completeReview(100L, SystemStatus.APPROVED, 999L))
                .isInstanceOf(CreditRequestValidationException.class)
                .satisfies(ex -> {
                    var issues = ((CreditRequestValidationException) ex).getIssues();
                    assertThat(issues.get(0).code()).isEqualTo("NOT_UNDER_REVIEW");
                });
        verify(eventPublisher, never()).publishEvent(any());
    }

    // -------------------------------------------------------------------
    // Landing list + counters
    // -------------------------------------------------------------------

    @Test
    @DisplayName("listForAdmin filters by status and pages")
    void listForAdmin_filtersByStatus() {
        CreditRequest crUnder = creditRequest(100L, underReview.getId());
        crUnder.setRequestDate(Instant.parse("2026-05-01T00:00:00Z"));
        CreditRequest crApproved = creditRequest(101L, approved.getId());
        crApproved.setRequestDate(Instant.parse("2026-05-02T00:00:00Z"));

        when(creditRequestRepository.findAll())
                .thenReturn(List.of(crUnder, crApproved));
        when(statusRepository.findAll())
                .thenReturn(List.of(pendingApproval, underReview, approved, declined));

        Page<CreditRequest> page = service.listForAdmin(
                new AdminListFilter(SystemStatus.APPROVED, null, null, null, null, null),
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("statusCounters aggregates by SystemStatus")
    void statusCounters_counts() {
        CreditRequest a = creditRequest(1L, pendingApproval.getId());
        CreditRequest b = creditRequest(2L, pendingApproval.getId());
        CreditRequest c = creditRequest(3L, underReview.getId());
        CreditRequest d = creditRequest(4L, approved.getId());

        when(creditRequestRepository.findAll()).thenReturn(List.of(a, b, c, d));
        when(statusRepository.findAll())
                .thenReturn(List.of(pendingApproval, underReview, approved, declined));

        AdminStatusCounters counters = service.statusCounters();

        assertThat(counters.pendingApproval()).isEqualTo(2);
        assertThat(counters.underReview()).isEqualTo(1);
        assertThat(counters.approved()).isEqualTo(1);
        assertThat(counters.declined()).isEqualTo(0);
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static CreditRequestStatus statusRow(Long id, SystemStatus systemStatus) {
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(id);
        row.setSystemStatus(systemStatus);
        row.setInternalStatusText(systemStatus.name());
        row.setExternalStatusText(systemStatus.name());
        row.setColorHex("#888888");
        return row;
    }

    private static CreditRequest creditRequest(Long id, Long statusId) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber("PCR-T-" + id);
        cr.setOrderNumber("SO-" + id);
        cr.setBuyerCodeId(500L);
        cr.setStatusId(statusId);
        cr.setRequestDate(Instant.parse("2026-05-01T00:00:00Z"));
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }

    private static MissingDeviceLine newMissingLine(Long id, Long crId, BigDecimal paid) {
        MissingDeviceLine line = new MissingDeviceLine();
        line.setId(id);
        line.setCreditRequestId(crId);
        line.setBarcodeSubmitted("BC-" + id);
        line.setAmountPaid(paid);
        return line;
    }

    private static WrongDeviceLine newWrongLine(Long id, Long crId, String brand,
                                                 BigDecimal paid, BigDecimal latest) {
        WrongDeviceLine line = new WrongDeviceLine();
        line.setId(id);
        line.setCreditRequestId(crId);
        line.setExpectedBarcode("BC-" + id);
        line.setExpectedBrand(brand);
        line.setExpectedAmountPaid(paid);
        line.setLatestPrice(latest);
        return line;
    }

    private static EncumberedDeviceLine newEncumberedLine(Long id, Long crId, BigDecimal paid) {
        EncumberedDeviceLine line = new EncumberedDeviceLine();
        line.setId(id);
        line.setCreditRequestId(crId);
        line.setBarcodeSubmitted("BC-" + id);
        line.setAmountPaid(paid);
        return line;
    }

    private static HeaderSummary emptySummary() {
        return new HeaderSummary(0, 0, BigDecimal.ZERO, 0, 0, BigDecimal.ZERO);
    }
}
