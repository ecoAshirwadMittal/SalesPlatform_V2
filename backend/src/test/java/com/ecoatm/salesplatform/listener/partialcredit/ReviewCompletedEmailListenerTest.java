package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * Unit tests for {@link ReviewCompletedEmailListener}.
 *
 * <p><b>T11 (unified email migration):</b> the listener no longer renders,
 * sends, or audits directly — it hands a single {@code sendTemplated} call
 * to {@link EmailService}, which owns rendering (from {@code email.template}),
 * recipient resolution, the {@code email.log} write, and delivery. These
 * tests cover the wiring: the enabled-gate, the reload/recipient-resolution
 * guards, the template-key selection by outcome, the exact
 * {@link EmailService.SendOverrides} / {@link EmailService.SourceRef} shape
 * passed through, and that any exception {@code sendTemplated} raises is
 * swallowed (never allowed to escape the async listener).
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ReviewCompletedEmailListenerTest {

    @Mock private CreditRequestRepository creditRequestRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private EmailService emailService;

    @Test
    @DisplayName("flag=false — logs intent and skips the send")
    void disabledByFlag_logsIntent_doesNotSend(CapturedOutput output) {
        ReviewCompletedEmailListener listener = newListener(false);

        listener.onReviewCompleted(new ReviewCompletedEvent(42L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompletedEmailListener");
        assertThat(output.getOut()).contains("(disabled)");
        assertThat(output.getOut()).contains("APPROVED");
        assertThat(output.getOut()).contains("creditRequestId=42");
        verifyNoInteractions(emailService, creditRequestRepository, directUserRepository);
    }

    @Test
    @DisplayName("flag=true APPROVED — calls EmailService.sendTemplated with the Approved key + vars + overrides + source")
    void enabledApproved_callsEmailServiceWithApprovedTemplate() {
        CreditRequest cr = approvedRequest(7L, "PC-2026-007", "ORD-123", new BigDecimal("125.50"), 55L);
        when(creditRequestRepository.findById(7L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[]{"buyer1@example.com", "Buyer One"},
                new Object[]{"buyer2@example.com", "Buyer Two"}));
        when(emailService.sendTemplated(eq("ReviewCompleted_Approved"), any(), any(), any()))
                .thenReturn(sentLog());

        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(7L, SystemStatus.APPROVED, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        // recipients travel via SendOverrides.to — the copied PC templates
        // have to_default=null, so a null overrides would make sendTemplated
        // throw "no recipients". cc/bcc are explicitly null (template default).
        verify(emailService).sendTemplated(
                eq("ReviewCompleted_Approved"),
                varsCaptor.capture(),
                eq(new EmailService.SendOverrides(
                        List.of("buyer1@example.com", "buyer2@example.com"), null, null)),
                eq(new EmailService.SourceRef("PARTIAL_CREDIT", 7L)));

        Map<String, Object> vars = varsCaptor.getValue();
        assertThat(vars).containsEntry("requestNumber", "PC-2026-007");
        assertThat(vars).containsEntry("orderNumber", "ORD-123");
        // approvedTotalDisplay carries the currency sign so the V90 seed
        // could avoid Flyway placeholder collisions.
        assertThat(vars).containsEntry("approvedTotalDisplay", "$125.50");
    }

    @Test
    @DisplayName("flag=true DECLINED — calls EmailService.sendTemplated with the Declined key and omits approvedTotal var")
    void enabledDeclined_callsEmailServiceWithDeclinedTemplate() {
        CreditRequest cr = approvedRequest(8L, "PC-2026-008", "ORD-456", new BigDecimal("0.00"), 60L);
        when(creditRequestRepository.findById(8L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(60L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));
        when(emailService.sendTemplated(eq("ReviewCompleted_Declined"), any(), any(), any()))
                .thenReturn(sentLog());

        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(8L, SystemStatus.DECLINED, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailService).sendTemplated(
                eq("ReviewCompleted_Declined"),
                varsCaptor.capture(),
                eq(new EmailService.SendOverrides(List.of("buyer@example.com"), null, null)),
                eq(new EmailService.SourceRef("PARTIAL_CREDIT", 8L)));

        // Declined variant must NOT include approvedTotalDisplay — the
        // template doesn't reference it, and supplying it would mask a
        // future template-bug if the variable name diverged.
        assertThat(varsCaptor.getValue()).doesNotContainKey("approvedTotalDisplay");
    }

    @Test
    @DisplayName("flag=true + request id null — logs warning and skips send")
    void enabledNullRequestId_logsWarning(CapturedOutput output) {
        newListener(true).onReviewCompleted(new ReviewCompletedEvent(null, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("null requestId");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("flag=true + request not found — logs warning, skips send")
    void enabledRequestNotFound_logsWarning(CapturedOutput output) {
        when(creditRequestRepository.findById(999L)).thenReturn(Optional.empty());

        newListener(true).onReviewCompleted(new ReviewCompletedEvent(999L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no CreditRequest for id=999");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("flag=true + no recipients — logs warning, skips send")
    void enabledNoRecipients_logsWarning(CapturedOutput output) {
        CreditRequest cr = approvedRequest(11L, "PC-2026-011", "ORD-789", new BigDecimal("10.00"), 70L);
        when(creditRequestRepository.findById(11L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        newListener(true).onReviewCompleted(new ReviewCompletedEvent(11L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("EmailService.sendTemplated throwing — swallowed, never escapes the async listener")
    void emailServiceThrows_swallowsException(CapturedOutput output) {
        CreditRequest cr = approvedRequest(12L, "PC-2026-012", "ORD-999", new BigDecimal("50.00"), 80L);
        when(creditRequestRepository.findById(12L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));
        when(emailService.sendTemplated(eq("ReviewCompleted_Approved"), any(), any(), any()))
                .thenThrow(new IllegalStateException("Email template is disabled: ReviewCompleted_Approved"));

        // Must not throw — the listener swallows + logs every exception so a
        // send failure can never surface to (or roll back) the caller.
        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(12L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompleted email delivery failed");
        assertThat(output.getOut()).contains("Email template is disabled");
    }

    private ReviewCompletedEmailListener newListener(boolean enabled) {
        return new ReviewCompletedEmailListener(
                creditRequestRepository,
                directUserRepository,
                emailService,
                enabled);
    }

    private static EmailLog sentLog() {
        EmailLog log = new EmailLog();
        log.setId(1L);
        log.setStatus(EmailStatus.SENT);
        return log;
    }

    private static CreditRequest approvedRequest(
            Long id, String requestNumber, String orderNumber, BigDecimal approvedTotal, Long buyerCodeId) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber(requestNumber);
        cr.setOrderNumber(orderNumber);
        cr.setApprovedTotal(approvedTotal);
        cr.setBuyerCodeId(buyerCodeId);
        return cr;
    }
}
