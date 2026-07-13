package com.ecoatm.salesplatform.listener.buyermgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.email.EmailService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * Unit tests for {@link ManualQualificationEmailListener}.
 *
 * <p>Mirrors {@code RmaApprovedEmailListenerTest}: the listener hands a single
 * {@code sendTemplated} call to {@link EmailService}, which owns rendering (from
 * {@code email.template}), the {@code email.log} write, and delivery. These
 * tests cover the wiring — the legacy {@code NF_OnIncludedChanged_New} gate
 * ({@code roundStatus == Started && included == true}), the no-recipients guard,
 * the exact {@link EmailService.SendOverrides} / {@link EmailService.SourceRef}
 * shape and {@code vars} map, and that any exception {@code sendTemplated} raises
 * is swallowed (never allowed to escape the async listener and roll back — or
 * surface to — the already committed override).
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ManualQualificationEmailListenerTest {

    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookup;
    @Mock private EmailService emailService;

    private static final Instant QUALIFIED_AT = Instant.parse("2026-07-12T14:30:00Z");

    @Test
    @DisplayName("Started + included — calls sendTemplated with ManualQualification key + vars + overrides + source")
    void startedAndIncluded_callsEmailService() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(55L)).thenReturn(List.<Object[]>of(
                new Object[] {"buyer1@example.com", "Buyer One"},
                new Object[] {"buyer2@example.com", "Buyer Two"}));
        when(buyerCodeLookup.findCodeById(55L)).thenReturn("BC001");
        when(emailService.sendTemplated(eq("ManualQualification"), any(), any(), any()))
                .thenReturn(sentLog());

        newListener().onQualificationOverridden(event(7L, 55L, 900L, true,
                SchedulingAuctionStatus.Started));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        // Recipients travel via SendOverrides.to — the V99-seeded
        // ManualQualification template has to_default=null, so a null overrides
        // would make sendTemplated throw "no recipients". cc/bcc stay null
        // (template default). SourceRef id is the qualifiedBuyerCodeId.
        verify(emailService).sendTemplated(
                eq("ManualQualification"),
                varsCaptor.capture(),
                eq(new EmailService.SendOverrides(
                        List.of("buyer1@example.com", "buyer2@example.com"), null, null)),
                eq(new EmailService.SourceRef("QUALIFICATION", 7L)));

        Map<String, Object> vars = varsCaptor.getValue();
        assertThat(vars).containsEntry("buyerCode", "BC001");
        assertThat(vars).containsEntry("schedulingAuctionId", 900L);
        assertThat(vars).containsEntry("qualifiedAtDisplay", "2026-07-12 14:30 UTC");
    }

    @Test
    @DisplayName("Started + included=false — never touches EmailService or any collaborator")
    void startedNotIncluded_doesNotSend() {
        newListener().onQualificationOverridden(event(8L, 55L, 900L, false,
                SchedulingAuctionStatus.Started));

        verifyNoInteractions(emailService, directUserRepository, buyerCodeLookup);
    }

    @Test
    @DisplayName("included=true but round not Started — never touches EmailService or any collaborator")
    void notStarted_doesNotSend() {
        newListener().onQualificationOverridden(event(9L, 55L, 900L, true,
                SchedulingAuctionStatus.Scheduled));

        verifyNoInteractions(emailService, directUserRepository, buyerCodeLookup);
    }

    @Test
    @DisplayName("Started + included + no recipients — logs warning, skips send")
    void noRecipients_logsWarning(CapturedOutput output) {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(70L)).thenReturn(List.of());

        newListener().onQualificationOverridden(event(10L, 70L, 901L, true,
                SchedulingAuctionStatus.Started));

        assertThat(output.getOut()).contains("no active recipients for buyerCodeId=70");
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("EmailService.sendTemplated throwing — swallowed, never escapes the async listener")
    void emailServiceThrows_swallowsException(CapturedOutput output) {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(80L))
                .thenReturn(List.<Object[]>of(new Object[] {"buyer@example.com", "Buyer"}));
        when(buyerCodeLookup.findCodeById(80L)).thenReturn("BC012");
        when(emailService.sendTemplated(eq("ManualQualification"), any(), any(), any()))
                .thenThrow(new IllegalStateException("Email template is disabled: ManualQualification"));

        // Must not throw — the listener swallows + logs every exception so a
        // send failure can never surface to (or roll back) the caller.
        newListener().onQualificationOverridden(event(12L, 80L, 902L, true,
                SchedulingAuctionStatus.Started));

        assertThat(output.getOut()).contains("Manual-qualification email delivery failed");
        assertThat(output.getOut()).contains("Email template is disabled");
    }

    private ManualQualificationEmailListener newListener() {
        return new ManualQualificationEmailListener(
                directUserRepository, buyerCodeLookup, emailService);
    }

    private static QualificationOverriddenEvent event(
            Long qbcId, Long buyerCodeId, Long schedulingAuctionId,
            boolean included, SchedulingAuctionStatus roundStatus) {
        return new QualificationOverriddenEvent(
                qbcId, buyerCodeId, schedulingAuctionId, included, roundStatus, 99L, QUALIFIED_AT);
    }

    private static EmailLog sentLog() {
        EmailLog log = new EmailLog();
        log.setId(1L);
        log.setStatus(EmailStatus.SENT);
        return log;
    }
}
