package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.email.EmailService;
import com.ecoatm.salesplatform.service.email.EmailService.SendOverrides;
import com.ecoatm.salesplatform.service.email.EmailService.SourceRef;
import jakarta.persistence.EntityNotFoundException;
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

/**
 * Unit coverage for {@link AccountingEmailService} — the manual admin
 * accounting-notification send (gap 2.5 Task 4). Mocks {@link EmailService};
 * the load-bearing branches are the exact {@code sendTemplated} arguments
 * (template key + {@link SendOverrides} + {@link SourceRef} + var map), the
 * APPROVED-state guard, the no-recipients fail-safe, and the missing-request
 * 404 — each asserted to send exactly once, or not at all.
 */
@ExtendWith(MockitoExtension.class)
class AccountingEmailServiceTest {

    private static final long REQUEST_ID = 100L;
    private static final long BUYER_CODE_ID = 500L;
    private static final long APPROVED_STATUS_ID = 4L;

    @Mock private CreditRequestRepository creditRequestRepository;
    @Mock private CreditRequestStatusRepository statusRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookupService;
    @Mock private WeekRepository weekRepository;
    @Mock private EmailService emailService;

    private AccountingEmailService newService(List<String> recipients) {
        return new AccountingEmailService(
                creditRequestRepository,
                statusRepository,
                buyerCodeLookupService,
                weekRepository,
                emailService,
                recipients);
    }

    @Test
    @DisplayName("approved request + configured recipients → sends once with the exact template key, overrides, source, and vars")
    void approvedWithRecipients_sendsOnce_withExactArgs() {
        AccountingEmailService service = newService(
                List.of("  accounting@ecoatm.com ", "ap@ecoatm.com"));

        CreditRequest cr = approvedRequest();
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.APPROVED)));
        when(buyerCodeLookupService.findCodeById(BUYER_CODE_ID)).thenReturn("NB_PWS");
        when(weekRepository.findByDate(cr.getOrderCreatedDate()))
                .thenReturn(Optional.of(week(18)));
        when(emailService.sendTemplated(any(), any(), any(), any()))
                .thenReturn(sentLog(77L));

        EmailLog result = service.sendAccountingEmail(REQUEST_ID);

        assertThat(result.getStatus()).isEqualTo(EmailStatus.SENT);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<SendOverrides> overridesCaptor = ArgumentCaptor.forClass(SendOverrides.class);
        ArgumentCaptor<SourceRef> sourceCaptor = ArgumentCaptor.forClass(SourceRef.class);
        verify(emailService).sendTemplated(
                eq("CreditRequestSalesApproved"),
                varsCaptor.capture(),
                overridesCaptor.capture(),
                sourceCaptor.capture());

        // Recipients travel as SendOverrides.to (V102 template has to_default=NULL),
        // trimmed and blank-free; cc/bcc null (template default).
        assertThat(overridesCaptor.getValue())
                .isEqualTo(new SendOverrides(
                        List.of("accounting@ecoatm.com", "ap@ecoatm.com"), null, null));
        assertThat(sourceCaptor.getValue())
                .isEqualTo(new SourceRef("PARTIAL_CREDIT", REQUEST_ID));

        assertThat(varsCaptor.getValue())
                .containsEntry("requestNumber", "CRSO-1")
                .containsEntry("weekNumber", "W18")
                .containsEntry("buyerName", "Acme Corp")
                .containsEntry("buyerCode", "NB_PWS")
                .containsEntry("requestReasons", "Missing, Wrong")
                .containsEntry("totalDevicesApproved", "3")
                .containsEntry("totalAmountApproved", "$1234.50");
    }

    @Test
    @DisplayName("no recipients configured → throws NotConfigured, never sends")
    void noRecipients_throws_noSend() {
        AccountingEmailService service = newService(List.of());
        CreditRequest cr = approvedRequest();
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.APPROVED)));

        assertThatThrownBy(() -> service.sendAccountingEmail(REQUEST_ID))
                .isInstanceOf(AccountingRecipientsNotConfiguredException.class)
                .hasMessageContaining("not configured");

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("recipient list of only blanks → treated as unconfigured, never sends")
    void blankRecipients_throws_noSend() {
        AccountingEmailService service = newService(List.of("   ", ""));
        CreditRequest cr = approvedRequest();
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.APPROVED)));

        assertThatThrownBy(() -> service.sendAccountingEmail(REQUEST_ID))
                .isInstanceOf(AccountingRecipientsNotConfiguredException.class);

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("request under review → throws NotApproved, never sends")
    void underReview_throws_noSend() {
        AccountingEmailService service = newService(List.of("accounting@ecoatm.com"));
        CreditRequest cr = approvedRequest();
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.UNDER_REVIEW)));

        assertThatThrownBy(() -> service.sendAccountingEmail(REQUEST_ID))
                .isInstanceOf(CreditRequestNotApprovedException.class)
                .hasMessageContaining("not APPROVED");

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("declined request → throws NotApproved, never sends")
    void declined_throws_noSend() {
        AccountingEmailService service = newService(List.of("accounting@ecoatm.com"));
        CreditRequest cr = approvedRequest();
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.DECLINED)));

        assertThatThrownBy(() -> service.sendAccountingEmail(REQUEST_ID))
                .isInstanceOf(CreditRequestNotApprovedException.class);

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("missing request → EntityNotFoundException, never sends")
    void missingRequest_throwsNotFound_noSend() {
        AccountingEmailService service = newService(List.of("accounting@ecoatm.com"));
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendAccountingEmail(REQUEST_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CreditRequest " + REQUEST_ID);

        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    @DisplayName("no order date / unresolved week → weekNumber renders empty, buyerName falls back to contact")
    void weekAndBuyerNameFallbacks() {
        AccountingEmailService service = newService(List.of("accounting@ecoatm.com"));

        CreditRequest cr = approvedRequest();
        cr.setOrderCreatedDate(null);       // no date → no week lookup
        cr.setPartyName("  ");              // blank company → fall back to contact name
        cr.setBuyerName("Jane Buyer");
        when(creditRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(cr));
        when(statusRepository.findById(APPROVED_STATUS_ID))
                .thenReturn(Optional.of(statusRow(SystemStatus.APPROVED)));
        when(buyerCodeLookupService.findCodeById(BUYER_CODE_ID)).thenReturn(null); // unknown → ""
        when(emailService.sendTemplated(any(), any(), any(), any())).thenReturn(sentLog(88L));

        service.sendAccountingEmail(REQUEST_ID);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailService).sendTemplated(eq("CreditRequestSalesApproved"), varsCaptor.capture(),
                any(), any());
        assertThat(varsCaptor.getValue())
                .containsEntry("weekNumber", "")
                .containsEntry("buyerName", "Jane Buyer")
                .containsEntry("buyerCode", "");
        // The week lookup is never consulted when there is no order date.
        verifyNoInteractions(weekRepository);
    }

    // -------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------

    private static CreditRequest approvedRequest() {
        CreditRequest cr = new CreditRequest();
        cr.setId(REQUEST_ID);
        cr.setRequestNumber("PCR-1");
        cr.setOrderNumber("SO-1");
        cr.setPartyName("Acme Corp");
        cr.setBuyerCodeId(BUYER_CODE_ID);
        cr.setStatusId(APPROVED_STATUS_ID);
        cr.setOrderCreatedDate(Instant.parse("2026-05-01T00:00:00Z"));
        cr.setHasMissingDevice(true);
        cr.setHasWrongDevice(true);
        cr.setHasEncumberedDevice(false);
        cr.setApprovedQty(3);
        cr.setApprovedTotal(new BigDecimal("1234.50"));
        return cr;
    }

    private static CreditRequestStatus statusRow(SystemStatus status) {
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(APPROVED_STATUS_ID);
        row.setSystemStatus(status);
        return row;
    }

    private static Week week(int weekNumber) {
        Week w = new Week();
        w.setWeekNumber(weekNumber);
        return w;
    }

    private static EmailLog sentLog(Long id) {
        EmailLog logRow = new EmailLog();
        logRow.setId(id);
        logRow.setStatus(EmailStatus.SENT);
        return logRow;
    }
}
