package com.ecoatm.salesplatform.listener.rma;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
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
 * Unit tests for {@link RmaApprovedEmailListener}.
 *
 * <p>Mirrors {@code ReviewCompletedEmailListenerTest}: the listener hands a
 * single {@code sendTemplated} call to {@link EmailService}, which owns
 * rendering (from {@code email.template}), the {@code email.log} write, and
 * delivery. These tests cover the wiring — the APPROVED-only gate, the
 * reload/recipient-resolution guards, the exact
 * {@link EmailService.SendOverrides} / {@link EmailService.SourceRef} shape and
 * {@code vars} map (including the {@code $#,##0.00} currency formatting), and
 * that any exception {@code sendTemplated} raises is swallowed (never allowed
 * to escape the async listener and roll back — or surface to — the already
 * committed review).
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class RmaApprovedEmailListenerTest {

    @Mock private RmaRepository rmaRepository;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookup;
    @Mock private EmailService emailService;

    @Test
    @DisplayName("APPROVED — calls EmailService.sendTemplated with the RMA_Approved key + vars + overrides + source")
    void approved_callsEmailServiceWithRmaApprovedTemplate() {
        Rma rma = approvedRma(7L, "RMABC0126001", 55L, 3, 2, new BigDecimal("1234.50"));
        rma.getItems().add(item("356789012345678", "Broken Screen", "Approve"));
        rma.getItems().add(item("111222333444555", "Water Damage", "Decline")); // excluded from summary
        when(rmaRepository.findById(7L)).thenReturn(Optional.of(rma));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[] {"buyer1@example.com", "Buyer One"},
                new Object[] {"buyer2@example.com", "Buyer Two"}));
        when(buyerCodeLookup.findCodeById(55L)).thenReturn("BC001");
        when(emailService.sendTemplated(eq("RMA_Approved"), any(), any(), any())).thenReturn(sentLog());

        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(7L, RmaReviewOutcome.APPROVED, 99L, Instant.now()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        // Recipients travel via SendOverrides.to — the V93-seeded RMA_Approved
        // template has to_default=null, so a null overrides would make
        // sendTemplated throw "no recipients". cc/bcc stay null (template default).
        verify(emailService).sendTemplated(
                eq("RMA_Approved"),
                varsCaptor.capture(),
                eq(new EmailService.SendOverrides(
                        List.of("buyer1@example.com", "buyer2@example.com"), null, null)),
                eq(new EmailService.SourceRef("RMA", 7L)));

        Map<String, Object> vars = varsCaptor.getValue();
        assertThat(vars).containsEntry("rmaNumber", "RMABC0126001");
        assertThat(vars).containsEntry("buyerCode", "BC001");
        assertThat(vars).containsEntry("approvedQty", 3);
        assertThat(vars).containsEntry("approvedSkus", 2);
        // approvedTotalDisplay carries the currency sign so the V93 seed avoids
        // a Flyway ${...} placeholder collision; $#,##0.00 → thousands + 2 dp.
        assertThat(vars).containsEntry("approvedTotalDisplay", "$1,234.50");
        // Item summary lists APPROVED items only (mirrors SUB_SendEmail_RMAApproved).
        assertThat((String) vars.get("approvedItemsSummary"))
                .contains("356789012345678")
                .contains("Broken Screen")
                .doesNotContain("Water Damage");
    }

    @Test
    @DisplayName("DECLINED — never touches EmailService or any repository")
    void declined_doesNotSend() {
        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(8L, RmaReviewOutcome.DECLINED, 99L, Instant.now()));

        verifyNoInteractions(emailService, rmaRepository, directUserRepository, buyerCodeLookup);
    }

    @Test
    @DisplayName("APPROVED + rmaId null — logs warning, skips send")
    void approvedNullRmaId_logsWarning(CapturedOutput output) {
        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(null, RmaReviewOutcome.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("null rmaId");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("APPROVED + RMA not found — logs warning, skips send")
    void approvedRmaNotFound_logsWarning(CapturedOutput output) {
        when(rmaRepository.findById(999L)).thenReturn(Optional.empty());

        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(999L, RmaReviewOutcome.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no Rma for id=999");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("APPROVED + no recipients — logs warning, skips send")
    void approvedNoRecipients_logsWarning(CapturedOutput output) {
        Rma rma = approvedRma(11L, "RMABC0126011", 70L, 1, 1, new BigDecimal("10.00"));
        when(rmaRepository.findById(11L)).thenReturn(Optional.of(rma));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(11L, RmaReviewOutcome.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("EmailService.sendTemplated throwing — swallowed, never escapes the async listener")
    void emailServiceThrows_swallowsException(CapturedOutput output) {
        Rma rma = approvedRma(12L, "RMABC0126012", 80L, 1, 1, new BigDecimal("50.00"));
        when(rmaRepository.findById(12L)).thenReturn(Optional.of(rma));
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[] {"buyer@example.com", "Buyer"}));
        when(buyerCodeLookup.findCodeById(80L)).thenReturn("BC012");
        when(emailService.sendTemplated(eq("RMA_Approved"), any(), any(), any()))
                .thenThrow(new IllegalStateException("Email template is disabled: RMA_Approved"));

        // Must not throw — the listener swallows + logs every exception so a
        // send failure can never surface to (or roll back) the caller.
        newListener().onRmaReviewCompleted(
                new RmaReviewCompletedEvent(12L, RmaReviewOutcome.APPROVED, 99L, Instant.now()));

        assertThat(output.getOut()).contains("RMA approval email delivery failed");
        assertThat(output.getOut()).contains("Email template is disabled");
    }

    private RmaApprovedEmailListener newListener() {
        return new RmaApprovedEmailListener(
                rmaRepository, directUserRepository, buyerCodeLookup, emailService);
    }

    private static EmailLog sentLog() {
        EmailLog log = new EmailLog();
        log.setId(1L);
        log.setStatus(EmailStatus.SENT);
        return log;
    }

    private static Rma approvedRma(
            Long id, String number, Long buyerCodeId,
            Integer approvedQty, Integer approvedSkus, BigDecimal approvedTotal) {
        Rma rma = new Rma();
        rma.setId(id);
        rma.setNumber(number);
        rma.setBuyerCodeId(buyerCodeId);
        rma.setApprovedQty(approvedQty);
        rma.setApprovedSkus(approvedSkus);
        rma.setApprovedSalesTotal(approvedTotal);
        return rma;
    }

    private static RmaItem item(String imei, String returnReason, String status) {
        RmaItem it = new RmaItem();
        it.setImei(imei);
        it.setReturnReason(returnReason);
        it.setStatus(status);
        return it;
    }
}
