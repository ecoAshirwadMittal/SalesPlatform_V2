package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.event.partialcredit.CreditRequestSubmittedEvent;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
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
 * Unit tests for {@link CreditRequestSubmittedEmailListener}.
 *
 * <p>Mirror of {@code ReviewCompletedEmailListenerTest}: the listener hands a
 * single {@code sendTemplated} call to {@link EmailService}, which owns
 * rendering (from {@code email.template}), the {@code email.log} write, and
 * delivery. These tests cover the wiring — the enabled-gate, the
 * reload/recipient-resolution guards, the exact
 * {@link EmailService.SendOverrides} / {@link EmailService.SourceRef} shape and
 * variable map passed through, and that any exception {@code sendTemplated}
 * raises is swallowed (never allowed to escape the async listener).
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CreditRequestSubmittedEmailListenerTest {

    @Mock private CreditRequestRepository creditRequestRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private EmailService emailService;

    @Test
    @DisplayName("flag=false — logs intent and skips the send")
    void disabledByFlag_logsIntent_doesNotSend(CapturedOutput output) {
        CreditRequestSubmittedEmailListener listener = newListener(false);

        listener.onCreditRequestSubmitted(new CreditRequestSubmittedEvent(42L, 99L, Instant.now()));

        assertThat(output.getOut()).contains("CreditRequestSubmittedEmailListener");
        assertThat(output.getOut()).contains("(disabled)");
        assertThat(output.getOut()).contains("creditRequestId=42");
        verifyNoInteractions(emailService, creditRequestRepository, directUserRepository);
    }

    @Test
    @DisplayName("flag=true — calls EmailService.sendTemplated with the CreditRequestSubmitted key + vars + overrides + source")
    void enabled_callsEmailServiceWithSubmittedTemplate() {
        CreditRequest cr = draft(7L, "ORD-123", new BigDecimal("125.50"), 55L, true, true, false);
        when(creditRequestRepository.findById(7L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[] {"buyer1@example.com", "Buyer One"},
                new Object[] {"buyer2@example.com", "Buyer Two"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(55L)).thenReturn(List.of("Acme Corp"));
        when(emailService.sendTemplated(eq("CreditRequestSubmitted"), any(), any(), any()))
                .thenReturn(sentLog());

        newListener(true)
                .onCreditRequestSubmitted(new CreditRequestSubmittedEvent(7L, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        // recipients travel via SendOverrides.to — the V101 template has
        // to_default=null, so a null overrides would make sendTemplated throw
        // "no recipients". cc/bcc are explicitly null (template default).
        verify(emailService).sendTemplated(
                eq("CreditRequestSubmitted"),
                varsCaptor.capture(),
                eq(new EmailService.SendOverrides(
                        List.of("buyer1@example.com", "buyer2@example.com"), null, null)),
                eq(new EmailService.SourceRef("PARTIAL_CREDIT", 7L)));

        Map<String, Object> vars = varsCaptor.getValue();
        // Legacy convention: RequestNumber = 'CR' + OrderNumber.
        assertThat(vars).containsEntry("requestNumber", "CRORD-123");
        assertThat(vars).containsEntry("buyerName", "Acme Corp");
        assertThat(vars).containsEntry("requestReasons", "Missing, Wrong");
        // totalDevices = Requested_Total (rendered as a plain-string number).
        assertThat(vars).containsEntry("totalDevices", "125.50");
    }

    @Test
    @DisplayName("flag=true — requestReasons reflects only the flagged reasons (Encumbered only)")
    void enabled_requestReasonsReflectsFlags() {
        CreditRequest cr = draft(9L, "ORD-777", new BigDecimal("0.00"), 61L, false, false, true);
        when(creditRequestRepository.findById(9L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(61L))
                .thenReturn(List.<Object[]>of(new Object[] {"buyer@example.com", "Buyer"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(61L)).thenReturn(List.of());
        when(emailService.sendTemplated(eq("CreditRequestSubmitted"), any(), any(), any()))
                .thenReturn(sentLog());

        newListener(true)
                .onCreditRequestSubmitted(new CreditRequestSubmittedEvent(9L, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailService).sendTemplated(eq("CreditRequestSubmitted"), varsCaptor.capture(), any(), any());
        Map<String, Object> vars = varsCaptor.getValue();
        assertThat(vars).containsEntry("requestReasons", "Encumbered");
        // Empty company lookup falls back to an empty string, never null.
        assertThat(vars).containsEntry("buyerName", "");
    }

    @Test
    @DisplayName("flag=true + request id null — logs warning and skips send")
    void enabledNullRequestId_logsWarning(CapturedOutput output) {
        newListener(true).onCreditRequestSubmitted(new CreditRequestSubmittedEvent(null, 99L, Instant.now()));

        assertThat(output.getOut()).contains("null requestId");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("flag=true + request not found — logs warning, skips send")
    void enabledRequestNotFound_logsWarning(CapturedOutput output) {
        when(creditRequestRepository.findById(999L)).thenReturn(Optional.empty());

        newListener(true).onCreditRequestSubmitted(new CreditRequestSubmittedEvent(999L, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no CreditRequest for id=999");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("flag=true + no recipients — logs warning, skips send")
    void enabledNoRecipients_logsWarning(CapturedOutput output) {
        CreditRequest cr = draft(11L, "ORD-789", new BigDecimal("10.00"), 70L, true, false, false);
        when(creditRequestRepository.findById(11L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        newListener(true).onCreditRequestSubmitted(new CreditRequestSubmittedEvent(11L, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("EmailService.sendTemplated throwing — swallowed, never escapes the async listener")
    void emailServiceThrows_swallowsException(CapturedOutput output) {
        CreditRequest cr = draft(12L, "ORD-999", new BigDecimal("50.00"), 80L, true, false, false);
        when(creditRequestRepository.findById(12L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[] {"buyer@example.com", "Buyer"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(80L)).thenReturn(List.of("Buyer Co"));
        when(emailService.sendTemplated(eq("CreditRequestSubmitted"), any(), any(), any()))
                .thenThrow(new IllegalStateException("Email template is disabled: CreditRequestSubmitted"));

        // Must not throw — the listener swallows + logs every exception so a
        // send failure can never surface to (or roll back) the caller.
        newListener(true)
                .onCreditRequestSubmitted(new CreditRequestSubmittedEvent(12L, 99L, Instant.now()));

        assertThat(output.getOut()).contains("CreditRequestSubmitted email delivery failed");
        assertThat(output.getOut()).contains("Email template is disabled");
    }

    private CreditRequestSubmittedEmailListener newListener(boolean enabled) {
        return new CreditRequestSubmittedEmailListener(
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

    private static CreditRequest draft(
            Long id, String orderNumber, BigDecimal requestedTotal, Long buyerCodeId,
            boolean missing, boolean wrong, boolean encumbered) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber("PCR-" + id);
        cr.setOrderNumber(orderNumber);
        cr.setRequestedTotal(requestedTotal);
        cr.setBuyerCodeId(buyerCodeId);
        cr.setHasMissingDevice(missing);
        cr.setHasWrongDevice(wrong);
        cr.setHasEncumberedDevice(encumbered);
        return cr;
    }
}
