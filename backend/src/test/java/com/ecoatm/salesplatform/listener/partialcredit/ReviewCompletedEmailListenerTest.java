package com.ecoatm.salesplatform.listener.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.ecoatm.salesplatform.service.partialcredit.EmailAuditService;
import com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService;
import com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService.RenderedEmail;
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
 * <p>Sprint 4 / Chunk 2 — the listener no longer renders subject/body
 * itself. It hands a (template_key, variables) pair to
 * {@link EmailTemplateService} and audits the outcome via
 * {@link EmailAuditService}. The actual rendering logic is exercised by
 * {@link com.ecoatm.salesplatform.service.partialcredit.EmailTemplateServiceTest};
 * here we cover the wiring + the graceful-degradation paths.
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ReviewCompletedEmailListenerTest {

    @Mock private CreditRequestRepository creditRequestRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private EmailSender emailSender;
    @Mock private EmailTemplateService emailTemplateService;
    @Mock private EmailAuditService emailAuditService;

    @Test
    @DisplayName("flag=false — logs intent and skips the send")
    void disabledByFlag_logsIntent_doesNotSend(CapturedOutput output) {
        ReviewCompletedEmailListener listener = newListener(false);

        listener.onReviewCompleted(new ReviewCompletedEvent(42L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompletedEmailListener");
        assertThat(output.getOut()).contains("(disabled)");
        assertThat(output.getOut()).contains("APPROVED");
        assertThat(output.getOut()).contains("creditRequestId=42");
        verifyNoInteractions(
                emailSender, creditRequestRepository, directUserRepository, emailTemplateService, emailAuditService);
    }

    @Test
    @DisplayName("flag=true APPROVED — renders ReviewCompleted_Approved and audits success")
    void enabledApproved_rendersAndDispatchesAndAudits() {
        CreditRequest cr = approvedRequest(7L, "PC-2026-007", "ORD-123", new BigDecimal("125.50"), 55L);
        when(creditRequestRepository.findById(7L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[]{"buyer1@example.com", "Buyer One"},
                new Object[]{"buyer2@example.com", "Buyer Two"}));
        RenderedEmail rendered = new RenderedEmail("subj", "<p>html body</p>", "text body");
        when(emailTemplateService.render(eq("ReviewCompleted_Approved"), any())).thenReturn(rendered);

        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(7L, SystemStatus.APPROVED, 99L, Instant.now()));

        // The listener passes the correct variables to the renderer.
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailTemplateService).render(eq("ReviewCompleted_Approved"), varsCaptor.capture());
        Map<String, Object> vars = varsCaptor.getValue();
        assertThat(vars).containsEntry("requestNumber", "PC-2026-007");
        assertThat(vars).containsEntry("orderNumber", "ORD-123");
        // approvedTotalDisplay carries the currency sign so the V90 seed
        // could avoid Flyway placeholder collisions.
        assertThat(vars).containsEntry("approvedTotalDisplay", "$125.50");

        // The rendered output is what actually goes to the sender.
        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(messageCaptor.capture());
        EmailMessage msg = messageCaptor.getValue();
        assertThat(msg.to()).containsExactly("buyer1@example.com", "buyer2@example.com");
        assertThat(msg.subject()).isEqualTo("subj");
        assertThat(msg.htmlBody()).isEqualTo("<p>html body</p>");
        assertThat(msg.textBody()).isEqualTo("text body");

        // Audit row written with success=true and no error.
        verify(emailAuditService).recordBatch(
                eq("ReviewCompleted_Approved"),
                eq(List.of("buyer1@example.com", "buyer2@example.com")),
                eq(7L),
                eq(true),
                isNull());
    }

    @Test
    @DisplayName("flag=true DECLINED — renders ReviewCompleted_Declined and omits approvedTotal var")
    void enabledDeclined_rendersDeclinedTemplate() {
        CreditRequest cr = approvedRequest(8L, "PC-2026-008", "ORD-456", new BigDecimal("0.00"), 60L);
        when(creditRequestRepository.findById(8L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(60L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));
        when(emailTemplateService.render(eq("ReviewCompleted_Declined"), any()))
                .thenReturn(new RenderedEmail("decl-subj", "<p>decl html</p>", "decl text"));

        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(8L, SystemStatus.DECLINED, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailTemplateService).render(eq("ReviewCompleted_Declined"), varsCaptor.capture());
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
        verify(emailSender, never()).send(any());
        verify(emailAuditService, never()).recordBatch(anyString(), anyList(), anyLong(), anyBoolean(), any());
    }

    @Test
    @DisplayName("flag=true + request not found — logs warning, skips send, no audit row")
    void enabledRequestNotFound_logsWarning(CapturedOutput output) {
        when(creditRequestRepository.findById(999L)).thenReturn(Optional.empty());

        newListener(true).onReviewCompleted(new ReviewCompletedEvent(999L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no CreditRequest for id=999");
        verify(emailSender, never()).send(any());
        verify(emailAuditService, never()).recordBatch(anyString(), anyList(), anyLong(), anyBoolean(), any());
    }

    @Test
    @DisplayName("flag=true + no recipients — logs warning, skips send, no audit row")
    void enabledNoRecipients_logsWarning(CapturedOutput output) {
        CreditRequest cr = approvedRequest(11L, "PC-2026-011", "ORD-789", new BigDecimal("10.00"), 70L);
        when(creditRequestRepository.findById(11L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        newListener(true).onReviewCompleted(new ReviewCompletedEvent(11L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailSender, never()).send(any());
        verify(emailAuditService, never()).recordBatch(anyString(), anyList(), anyLong(), anyBoolean(), any());
    }

    @Test
    @DisplayName("EmailSender throwing — audit row recorded with success=false, error swallowed")
    void senderThrows_auditsFailureAndSwallows(CapturedOutput output) {
        CreditRequest cr = approvedRequest(12L, "PC-2026-012", "ORD-999", new BigDecimal("50.00"), 80L);
        when(creditRequestRepository.findById(12L)).thenReturn(Optional.of(cr));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[]{"buyer@example.com", "Buyer"}));
        when(emailTemplateService.render(eq("ReviewCompleted_Approved"), any()))
                .thenReturn(new RenderedEmail("s", "<p>h</p>", "t"));
        org.mockito.Mockito.doThrow(new RuntimeException("smtp boom")).when(emailSender).send(any());

        // Must not throw — the listener swallows + logs every exception.
        newListener(true)
                .onReviewCompleted(new ReviewCompletedEvent(12L, SystemStatus.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("ReviewCompleted email delivery failed");
        assertThat(output.getOut()).contains("smtp boom");
        // Failure path still records an audit row — knowing a send failed
        // matters more than knowing one succeeded.
        verify(emailAuditService).recordBatch(
                eq("ReviewCompleted_Approved"),
                eq(List.of("buyer@example.com")),
                eq(12L),
                eq(false),
                eq("smtp boom"));
    }

    private ReviewCompletedEmailListener newListener(boolean enabled) {
        return new ReviewCompletedEmailListener(
                creditRequestRepository,
                directUserRepository,
                emailSender,
                emailTemplateService,
                emailAuditService,
                enabled);
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
