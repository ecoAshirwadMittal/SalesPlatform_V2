package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.event.ReviewCompletedEvent;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.service.email.EmailMessage;
import com.ecoatm.salesplatform.service.email.EmailSender;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
 * <p>Covers the two operating modes:
 * <ol>
 *   <li><b>flag off</b> (Sprint 3 default) — listener logs intent only,
 *       never touches {@link EmailSender} or the repositories.</li>
 *   <li><b>flag on</b> — listener resolves recipients, renders the
 *       outcome-specific subject + body, and dispatches via the
 *       configured {@link EmailSender}.</li>
 * </ol>
 *
 * <p>The listener is robust by design — invalid input (null id, missing
 * request row, no recipients, sender throws) must never propagate out of
 * the async worker. We assert each of those degrade-gracefully paths.
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ReviewCompletedEmailListenerTest {

    @Mock private CreditRequestRepository creditRequestRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private EmailSender emailSender;

    @Test
    @DisplayName("flag=false — logs intent and skips the send")
    void disabledByFlag_logsIntent_doesNotSend(CapturedOutput output) {
        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, false);

        listener.onReviewCompleted(new ReviewCompletedEvent(42L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompletedEmailListener");
        assertThat(output.getOut()).contains("(disabled)");
        assertThat(output.getOut()).contains("APPROVED");
        assertThat(output.getOut()).contains("creditRequestId=42");
        verifyNoInteractions(emailSender, creditRequestRepository, directUserRepository);
    }

    @Test
    @DisplayName("flag=true APPROVED — renders subject + body and calls EmailSender.send")
    void enabledApproved_dispatchesEmail() {
        CreditRequest cr = approvedRequest(7L, "PC-2026-007", "ORD-123", new BigDecimal("125.50"), 55L);
        when(creditRequestRepository.findById(7L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[]{"buyer1@example.com", "Buyer One"},
                new Object[]{"buyer2@example.com", "Buyer Two"}));

        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);
        listener.onReviewCompleted(new ReviewCompletedEvent(7L, SystemStatus.APPROVED, 99L, Instant.now()));

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        EmailMessage msg = captor.getValue();
        assertThat(msg.to()).containsExactly("buyer1@example.com", "buyer2@example.com");
        assertThat(msg.subject()).isEqualTo("Your partial credit request PC-2026-007 has been approved");
        assertThat(msg.htmlBody()).contains("PC-2026-007").contains("approved").contains("125.50").contains("ORD-123");
        assertThat(msg.textBody()).contains("PC-2026-007").contains("approved").contains("125.50");
    }

    @Test
    @DisplayName("flag=true DECLINED — subject reflects decline outcome and omits credit total")
    void enabledDeclined_dispatchesEmail() {
        CreditRequest cr = approvedRequest(8L, "PC-2026-008", "ORD-456", new BigDecimal("0.00"), 60L);
        when(creditRequestRepository.findById(8L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(60L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));

        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);
        listener.onReviewCompleted(new ReviewCompletedEvent(8L, SystemStatus.DECLINED, 99L, Instant.now()));

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        EmailMessage msg = captor.getValue();
        assertThat(msg.subject()).isEqualTo("Your partial credit request PC-2026-008 has been declined");
        assertThat(msg.htmlBody()).contains("declined").doesNotContain("Approved credit total");
        assertThat(msg.textBody()).contains("declined").doesNotContain("Approved credit total");
    }

    @Test
    @DisplayName("flag=true + request id null — logs warning and skips send")
    void enabledNullRequestId_logsWarning(CapturedOutput output) {
        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);

        listener.onReviewCompleted(new ReviewCompletedEvent(null, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("null requestId");
        verify(emailSender, never()).send(any());
    }

    @Test
    @DisplayName("flag=true + request not found — logs warning and skips send")
    void enabledRequestNotFound_logsWarning(CapturedOutput output) {
        when(creditRequestRepository.findById(999L)).thenReturn(Optional.empty());

        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);
        listener.onReviewCompleted(new ReviewCompletedEvent(999L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no CreditRequest for id=999");
        verify(emailSender, never()).send(any());
    }

    @Test
    @DisplayName("flag=true + no recipients — logs warning and skips send")
    void enabledNoRecipients_logsWarning(CapturedOutput output) {
        CreditRequest cr = approvedRequest(11L, "PC-2026-011", "ORD-789", new BigDecimal("10.00"), 70L);
        when(creditRequestRepository.findById(11L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);
        listener.onReviewCompleted(new ReviewCompletedEvent(11L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailSender, never()).send(any());
    }

    @Test
    @DisplayName("EmailSender throwing is caught and logged — async worker never blows up")
    void senderThrows_isCaughtAndLogged(CapturedOutput output) {
        CreditRequest cr = approvedRequest(12L, "PC-2026-012", "ORD-999", new BigDecimal("50.00"), 80L);
        when(creditRequestRepository.findById(12L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));
        org.mockito.Mockito.doThrow(new RuntimeException("smtp boom")).when(emailSender).send(any());

        ReviewCompletedEmailListener listener =
                new ReviewCompletedEmailListener(creditRequestRepository, directUserRepository, emailSender, true);
        // Must not throw — the listener swallows + logs every exception.
        listener.onReviewCompleted(new ReviewCompletedEvent(12L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompleted email delivery failed");
        assertThat(output.getOut()).contains("smtp boom");
    }

    // ── helpers ────────────────────────────────────────────────────────────

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
