package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.EmailTemplate;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;
import com.ecoatm.salesplatform.repository.email.EmailTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link EmailService}. Mocks every collaborator
 * ({@link EmailTemplateRepository}, {@link EmailLogRepository},
 * {@link SmtpConfigService}, {@link TemplateRenderer}, {@link EmailSender}) —
 * this suite is about {@code EmailService}'s own orchestration (load →
 * render → resolve → snapshot → send → audit), not about the rendering
 * engine's substitution rules ({@link TemplateRendererTest} already owns
 * those) or real SMTP delivery ({@link SmtpEmailSenderTest} owns that).
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private EmailTemplateRepository emailTemplateRepository;
    @Mock private EmailLogRepository emailLogRepository;
    @Mock private SmtpConfigService smtpConfigService;
    @Mock private TemplateRenderer templateRenderer;
    @Mock private EmailSender emailSender;

    private EmailService service;

    @BeforeEach
    void setUp() {
        service = new EmailService(
                emailTemplateRepository, emailLogRepository, smtpConfigService, templateRenderer, emailSender);
    }

    // ── sendTemplated — success ─────────────────────────────────────────

    @Test
    @DisplayName("sendTemplated — success writes a SENT log (PENDING then SENT) and calls the sender")
    void success_writesSentLog_callsSender() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        when(templateRenderer.renderPlain(eq(template.getSubject()), anyMap())).thenReturn("Rendered Subject");
        when(templateRenderer.render(eq(template.getContentHtml()), anyMap())).thenReturn("<p>Rendered</p>");
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmailLog log = service.sendTemplated(
                "K", Map.of("x", "1"), null, new EmailService.SourceRef("PARTIAL_CREDIT", 7L));

        assertThat(log.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(log.getSentDate()).isNotNull();
        assertThat(log.getCreatedDate()).isNotNull();
        assertThat(log.getSubject()).isEqualTo("Rendered Subject");
        assertThat(log.getContentHtml()).isEqualTo("<p>Rendered</p>");
        assertThat(log.getToAddress()).isEqualTo("buyer@example.com");
        assertThat(log.getSourceModule()).isEqualTo("PARTIAL_CREDIT");
        assertThat(log.getSourceId()).isEqualTo(7L);
        assertThat(log.getRetryCount()).isZero();
        verify(emailSender).send(any(EmailMessage.class));
        verify(emailLogRepository, times(2)).save(any()); // PENDING then SENT
    }

    @Test
    @DisplayName("sendTemplated — renders content_plain into the message's textBody when the template has one")
    void success_rendersContentPlainWhenTemplateHasContentPlain() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        template.setContentPlain("Plain {{x}}");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        when(templateRenderer.renderPlain(eq(template.getSubject()), anyMap())).thenReturn("Subject");
        when(templateRenderer.renderPlain(eq("Plain {{x}}"), anyMap())).thenReturn("Plain rendered");
        when(templateRenderer.render(eq(template.getContentHtml()), anyMap())).thenReturn("<p>html</p>");
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.sendTemplated("K", Map.of("x", "1"), null, null);

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        assertThat(captor.getValue().textBody()).isEqualTo("Plain rendered");
    }

    // ── sendTemplated — template lookup / enablement ────────────────────

    @Test
    @DisplayName("sendTemplated — unknown template key throws EntityNotFoundException before any log/send")
    void templateNotFound_throws() {
        when(emailTemplateRepository.findByTemplateKey("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendTemplated("MISSING", Map.of(), null, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("MISSING");

        verifyNoInteractions(emailSender);
        verify(emailLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendTemplated — disabled template throws IllegalStateException before any log/send")
    void templateDisabled_throws() {
        EmailTemplate template = template("K", false);
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> service.sendTemplated("K", Map.of(), null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("K");

        verifyNoInteractions(emailSender);
        verify(emailLogRepository, never()).save(any());
    }

    // ── sendTemplated — sender failure ──────────────────────────────────

    @Test
    @DisplayName("sendTemplated — sender exception writes a FAILED log with next_attempt_at, never rethrows")
    void senderThrows_writesFailed_setsNextAttempt() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        stubRendering();
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("smtp down")).when(emailSender).send(any());

        EmailLog log = service.sendTemplated("K", Map.of(), null, null);

        assertThat(log.getStatus()).isEqualTo(EmailStatus.FAILED);
        assertThat(log.getErrorMessage()).contains("smtp down");
        assertThat(log.getNextAttemptAt()).isNotNull();
        verify(emailLogRepository, times(2)).save(any());
    }

    // ── sendTemplated — recipient / from resolution ─────────────────────

    @Test
    @DisplayName("sendTemplated — SendOverrides.to wins over template default; from falls back to smtp config")
    void overrideBeatsDefault_fromFallsBackToSmtp() {
        EmailTemplate template = template("K", true);
        template.setToDefault("default@example.com");
        template.setFromAddress(null);
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        stubRendering();
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(smtpConfigService.resolvedFromAddress()).thenReturn("smtp-default@example.com");
        EmailService.SendOverrides overrides = new EmailService.SendOverrides(
                List.of("custom@example.com"), null, null);

        EmailLog log = service.sendTemplated("K", Map.of(), overrides, null);

        assertThat(log.getToAddress()).isEqualTo("custom@example.com");
        assertThat(log.getFromAddress()).isEqualTo("smtp-default@example.com");
        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        assertThat(captor.getValue().to()).containsExactly("custom@example.com");
        assertThat(captor.getValue().from()).isEqualTo("smtp-default@example.com");
    }

    @Test
    @DisplayName("sendTemplated — no recipients (no override, blank template default) throws before any log/send")
    void noRecipients_throws() {
        EmailTemplate template = template("K", true); // toDefault left null
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> service.sendTemplated("K", Map.of(), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("K");

        verifyNoInteractions(emailSender);
        verify(emailLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendTemplated — explicit empty cc/bcc override wins over template defaults and stores null")
    void emptyCcBccOverride_winsOverDefaultAndStoresNull() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        template.setCcDefault("cc-default@example.com");
        template.setBccDefault("bcc-default@example.com");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        stubRendering();
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        EmailService.SendOverrides overrides = new EmailService.SendOverrides(null, List.of(), List.of());

        EmailLog log = service.sendTemplated("K", Map.of(), overrides, null);

        assertThat(log.getCc()).isNull();
        assertThat(log.getBcc()).isNull();
    }

    @Test
    @DisplayName("sendTemplated — blank (whitespace-only) template from_address falls back to smtp config")
    void blankTemplateFromAddress_fallsBackToSmtp() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        template.setFromAddress("   ");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        stubRendering();
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(smtpConfigService.resolvedFromAddress()).thenReturn("smtp-default@example.com");

        EmailLog log = service.sendTemplated("K", Map.of(), null, null);

        assertThat(log.getFromAddress()).isEqualTo("smtp-default@example.com");
    }

    @Test
    @DisplayName("sendTemplated — blank template from AND blank smtp from resolve to null, never a blank string")
    void blankTemplateAndSmtpFrom_resolvesToNull() {
        EmailTemplate template = template("K", true);
        template.setToDefault("buyer@example.com");
        template.setFromAddress("");
        when(emailTemplateRepository.findByTemplateKey("K")).thenReturn(Optional.of(template));
        stubRendering();
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(smtpConfigService.resolvedFromAddress()).thenReturn("   ");

        EmailLog log = service.sendTemplated("K", Map.of(), null, null);

        assertThat(log.getFromAddress()).isNull();
    }

    // ── resend ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("resend — rebuilds the EmailMessage from the log snapshot and marks SENT on success")
    void resend_rebuildsFromSnapshot_success() {
        EmailLog existing = snapshotLog();
        when(emailLogRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmailLog result = service.resend(42L);

        assertThat(result.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(result.getSentDate()).isNotNull();
        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        EmailMessage sent = captor.getValue();
        assertThat(sent.to()).containsExactly("buyer@example.com");
        assertThat(sent.cc()).containsExactly("cc@example.com");
        assertThat(sent.bcc()).isEmpty();
        assertThat(sent.from()).isEqualTo("noreply@example.com");
        assertThat(sent.replyTo()).isNull();
        assertThat(sent.subject()).isEqualTo("Snapshot subject");
        assertThat(sent.htmlBody()).isEqualTo("<p>Snapshot html</p>");
        assertThat(sent.textBody()).isNull();
        verify(emailLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("resend — sender exception marks FAILED, sets next_attempt_at, and does not bump retry_count")
    void resend_senderThrows_setsFailedAndNextAttempt_doesNotBumpRetryCount() {
        EmailLog existing = snapshotLog();
        existing.setRetryCount(1);
        when(emailLogRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(emailLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("smtp still down")).when(emailSender).send(any());

        EmailLog result = service.resend(42L);

        assertThat(result.getStatus()).isEqualTo(EmailStatus.FAILED);
        assertThat(result.getErrorMessage()).contains("smtp still down");
        assertThat(result.getNextAttemptAt()).isNotNull();
        // T5 scope only: retry_count/re-queue bookkeeping is refined in T6's
        // EmailRetryWorker, which calls this method rather than duplicating it.
        assertThat(result.getRetryCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("resend — unknown log id throws EntityNotFoundException, no send attempted")
    void resend_unknownId_throws() {
        when(emailLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resend(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verifyNoInteractions(emailSender);
    }

    // ── backoff ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("backoff — doubles per retry count and caps at the maximum, never overflows")
    void backoff_doublesWithCapAndNoOverflow() {
        assertThat(EmailService.backoff(0)).isEqualTo(Duration.ofMinutes(1));
        assertThat(EmailService.backoff(1)).isEqualTo(Duration.ofMinutes(2));
        assertThat(EmailService.backoff(3)).isEqualTo(Duration.ofMinutes(8));
        assertThat(EmailService.backoff(6)).isEqualTo(Duration.ofMinutes(60)); // capped
        assertThat(EmailService.backoff(50)).isEqualTo(Duration.ofMinutes(60)); // no overflow, still capped
    }

    // ── helpers ──────────────────────────────────────────────────────────

    /** Stubs the renderer broadly (any template string / any vars) for tests
     *  that exercise recipient/from resolution or failure handling and don't
     *  care about exact rendered content — that precision is covered by the
     *  two {@code success_*} tests above and by {@link TemplateRendererTest}.
     *  {@link EmailMessage}'s constructor requires a non-blank subject, so
     *  every test that reaches it needs at least this much stubbed. */
    private void stubRendering() {
        when(templateRenderer.renderPlain(anyString(), anyMap())).thenReturn("Subject");
        when(templateRenderer.render(anyString(), anyMap())).thenReturn("<p>html</p>");
    }

    private static EmailTemplate template(String key, boolean enabled) {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateKey(key);
        template.setSubject("Subject {{x}}");
        template.setContentHtml("<p>{{x}}</p>");
        template.setEnabled(enabled);
        return template;
    }

    private static EmailLog snapshotLog() {
        EmailLog log = new EmailLog();
        log.setId(42L);
        log.setTemplateKey("K");
        log.setFromAddress("noreply@example.com");
        log.setToAddress("buyer@example.com");
        log.setCc("cc@example.com");
        log.setBcc(null);
        log.setSubject("Snapshot subject");
        log.setContentHtml("<p>Snapshot html</p>");
        log.setStatus(EmailStatus.FAILED);
        log.setRetryCount(0);
        return log;
    }
}
