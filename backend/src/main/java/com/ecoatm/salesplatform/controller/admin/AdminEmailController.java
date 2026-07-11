package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.email.EmailTemplateUpsert;
import com.ecoatm.salesplatform.dto.email.EmailTemplateView;
import com.ecoatm.salesplatform.dto.email.PreviewRequest;
import com.ecoatm.salesplatform.dto.email.SendTestRequest;
import com.ecoatm.salesplatform.dto.email.SmtpConfigUpdate;
import com.ecoatm.salesplatform.dto.email.SmtpConfigView;
import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.EmailTemplate;
import com.ecoatm.salesplatform.repository.email.EmailTemplateRepository;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.email.EmailService;
import com.ecoatm.salesplatform.service.email.SmtpConfigService;
import com.ecoatm.salesplatform.service.email.TemplateRenderer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Admin REST surface for the unified email module: SMTP configuration
 * (Task 7) plus email-template CRUD/preview/send-test (Task 8). The
 * email-log surface lands in T9 and is intentionally NOT built here.
 * {@code Administrator}-only — this is operational infrastructure config,
 * not a SalesOps/buyer-facing surface.
 *
 * <p><b>Design decision D2 — password never exposed:</b> the SMTP password
 * is env-only ({@code spring.mail.password}); {@code email.smtp_config}
 * (V92) has no password column. Neither {@link SmtpConfigView} (the GET/PUT
 * response) nor {@link SmtpConfigUpdate} (the PUT request body) declares a
 * password field, so there is no code path in this controller that can
 * read, write, or echo one. {@code POST /smtp/test} exercises the
 * env-supplied {@link JavaMailSender} bean directly — it never touches
 * anything from the request body.
 *
 * <p><b>Task 8 — templates:</b> standard CRUD over {@code email.template}
 * (V92) via {@link EmailTemplateRepository}, plus two render-only actions:
 * {@code POST /templates/{id}/preview} (bypasses the {@code enabled} check —
 * an admin must be able to preview a disabled template before flipping it
 * back on, mirroring {@code AdminPartialCreditController.previewEmailTemplate})
 * and {@code POST /templates/{id}/send-test} (a real outbound send via
 * {@link EmailService#sendTemplated}, so it is rate-limited — keyed by the
 * authenticated user id rather than {@link UploadRateLimiter#clientIp},
 * because {@code X-Forwarded-For} is spoofable and this endpoint always has
 * a verified JWT principal; security review 2026-07-10).
 */
@RestController
@RequestMapping("/api/v1/admin/email")
@PreAuthorize("hasRole('Administrator')")
public class AdminEmailController {

    private static final Logger log = LoggerFactory.getLogger(AdminEmailController.class);

    /** Prefix for the send-test rate-limit bucket key — see the class Javadoc. */
    private static final String SEND_TEST_RATE_LIMIT_PREFIX = "email-send-test:";

    private final SmtpConfigService smtpConfigService;
    private final UploadRateLimiter uploadRateLimiter;
    // ObjectProvider, not a hard JavaMailSender dependency: MailSenderAutoConfiguration
    // only activates when spring.mail.host is set, which it is not in this app today
    // (see application.yml). A hard constructor dependency here would make the whole
    // controller — and therefore the whole app context — fail to boot.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final EmailTemplateRepository emailTemplateRepository;
    private final TemplateRenderer templateRenderer;
    private final EmailService emailService;

    public AdminEmailController(
            SmtpConfigService smtpConfigService,
            UploadRateLimiter uploadRateLimiter,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            EmailTemplateRepository emailTemplateRepository,
            TemplateRenderer templateRenderer,
            EmailService emailService) {
        this.smtpConfigService = smtpConfigService;
        this.uploadRateLimiter = uploadRateLimiter;
        this.mailSenderProvider = mailSenderProvider;
        this.emailTemplateRepository = emailTemplateRepository;
        this.templateRenderer = templateRenderer;
        this.emailService = emailService;
    }

    // -------------------------------------------------------------------
    // GET /smtp — current config. No password field, ever (D2).
    // -------------------------------------------------------------------

    @GetMapping("/smtp")
    public SmtpConfigView getSmtp() {
        return SmtpConfigView.from(smtpConfigService.get());
    }

    // -------------------------------------------------------------------
    // PUT /smtp — update config. The audit actor id is resolved from the
    // authenticated JWT principal, never a request field. SmtpConfigUpdate
    // has no password component, so any password/encryptedPassword field a
    // client sends is silently dropped by Jackson before this method body
    // even runs (D2).
    // -------------------------------------------------------------------

    @PutMapping("/smtp")
    public SmtpConfigView updateSmtp(@Valid @RequestBody SmtpConfigUpdate patch, Authentication auth) {
        return SmtpConfigView.from(smtpConfigService.update(patch, principalUserId(auth)));
    }

    // -------------------------------------------------------------------
    // POST /smtp/test — live connection check. Rate-limited first (this
    // triggers a real outbound SMTP connection attempt). Uses only the
    // env-supplied mail sender bean; nothing from the request body.
    // -------------------------------------------------------------------

    @PostMapping("/smtp/test")
    public ResponseEntity<SmtpTestResult> testSmtp(HttpServletRequest request) {
        if (!uploadRateLimiter.tryAcquire(UploadRateLimiter.clientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (!(sender instanceof JavaMailSenderImpl impl)) {
            return ResponseEntity.ok(new SmtpTestResult(false, "SMTP is not configured"));
        }
        try {
            impl.testConnection();
            return ResponseEntity.ok(new SmtpTestResult(true, "Connection succeeded"));
        } catch (Exception ex) {
            // Admin-only diagnostic endpoint — the whole point is to surface *why* the
            // connection failed, so ex.getMessage() intentionally goes back in the
            // response (never a password; testConnection() never had one to leak).
            // Also logged server-side per the "log detailed errors server-side" rule.
            log.warn("[AdminEmailController] SMTP test connection failed: {}", ex.getMessage());
            return ResponseEntity.ok(new SmtpTestResult(false, ex.getMessage()));
        }
    }

    // -------------------------------------------------------------------
    // GET /templates — list every email.template row.
    // -------------------------------------------------------------------

    @GetMapping("/templates")
    public List<EmailTemplateView> listTemplates() {
        return emailTemplateRepository.findAll().stream().map(EmailTemplateView::from).toList();
    }

    // -------------------------------------------------------------------
    // POST /templates — create. Duplicate templateKey -> 409 (checked via
    // findByTemplateKey before any write). Audit columns (created/changed
    // date + by) are stamped from the server clock and the JWT principal,
    // never from the request body.
    // -------------------------------------------------------------------

    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    public EmailTemplateView createTemplate(@Valid @RequestBody EmailTemplateUpsert body, Authentication auth) {
        if (emailTemplateRepository.findByTemplateKey(body.templateKey()).isPresent()) {
            throw new IllegalStateException("Email template key already exists: " + body.templateKey());
        }
        EmailTemplate entity = new EmailTemplate();
        entity.setTemplateKey(body.templateKey());
        applyEditableFields(entity, body);
        Instant now = Instant.now();
        Long principal = principalUserId(auth);
        entity.setCreatedDate(now);
        entity.setChangedDate(now);
        entity.setCreatedById(principal);
        entity.setChangedById(principal);
        return EmailTemplateView.from(emailTemplateRepository.save(entity));
    }

    // -------------------------------------------------------------------
    // GET /templates/{id}
    // -------------------------------------------------------------------

    @GetMapping("/templates/{id}")
    public EmailTemplateView getTemplate(@PathVariable Long id) {
        return EmailTemplateView.from(requireTemplate(id));
    }

    // -------------------------------------------------------------------
    // PUT /templates/{id} — update. templateKey is immutable: body.templateKey()
    // is validated (same @Valid shape as create) but never written onto the
    // entity, so a client attempting to change it is silently ignored rather
    // than erroring — the key is a stable identifier senders (e.g. the
    // Partial-Credit listener) resolve templates by.
    // -------------------------------------------------------------------

    @PutMapping("/templates/{id}")
    public EmailTemplateView updateTemplate(
            @PathVariable Long id, @Valid @RequestBody EmailTemplateUpsert body, Authentication auth) {
        EmailTemplate entity = requireTemplate(id);
        applyEditableFields(entity, body);
        entity.setChangedDate(Instant.now());
        entity.setChangedById(principalUserId(auth));
        return EmailTemplateView.from(emailTemplateRepository.save(entity));
    }

    // -------------------------------------------------------------------
    // DELETE /templates/{id} — hard delete. Phase 1 does not guard against
    // deleting a key a sender still references by name (e.g. the
    // Partial-Credit listener) — a future phase should add a reference
    // check before allowing delete.
    // -------------------------------------------------------------------

    @DeleteMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable Long id) {
        if (!emailTemplateRepository.existsById(id)) {
            throw new EntityNotFoundException("Email template not found: id=" + id);
        }
        emailTemplateRepository.deleteById(id);
    }

    // -------------------------------------------------------------------
    // POST /templates/{id}/preview — renders subject/html/text against the
    // supplied vars. Deliberately bypasses the `enabled` check (unlike
    // EmailService.sendTemplated) — an admin disabling a template must
    // still be able to preview it before flipping it back on, mirroring
    // AdminPartialCreditController.previewEmailTemplate.
    // -------------------------------------------------------------------

    @PostMapping("/templates/{id}/preview")
    public TemplatePreviewResult previewTemplate(
            @PathVariable Long id, @RequestBody(required = false) PreviewRequest body) {
        EmailTemplate entity = requireTemplate(id);
        Map<String, Object> vars = (body == null || body.vars() == null) ? Map.of() : body.vars();
        String subject = templateRenderer.renderPlain(entity.getSubject(), vars);
        String html = templateRenderer.render(entity.getContentHtml(), vars);
        String text = entity.getContentPlain() == null
                ? null
                : templateRenderer.renderPlain(entity.getContentPlain(), vars);
        return new TemplatePreviewResult(subject, html, text);
    }

    // -------------------------------------------------------------------
    // POST /templates/{id}/send-test — a real outbound send via
    // EmailService.sendTemplated, so the rate-limit gate runs FIRST (before
    // even loading the template) — keyed by the authenticated user id, not
    // clientIp: this endpoint always has a verified JWT principal, and
    // UploadRateLimiter.clientIp() trusts a spoofable X-Forwarded-For
    // (security review 2026-07-10). T7's /smtp/test intentionally keeps its
    // IP-keyed limiter as-is; unifying both is a separate follow-up.
    // -------------------------------------------------------------------

    @PostMapping("/templates/{id}/send-test")
    public ResponseEntity<SendTestResult> sendTest(
            @PathVariable Long id, @Valid @RequestBody SendTestRequest body, Authentication auth) {
        if (!uploadRateLimiter.tryAcquire(SEND_TEST_RATE_LIMIT_PREFIX + principalUserId(auth))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        EmailTemplate template = requireTemplate(id);
        Map<String, Object> vars = body.vars() == null ? Map.of() : body.vars();
        EmailLog result = emailService.sendTemplated(
                template.getTemplateKey(),
                vars,
                new EmailService.SendOverrides(List.of(body.toAddress()), null, null),
                new EmailService.SourceRef("ADMIN_SEND_TEST", id));
        boolean success = result.getStatus() == EmailStatus.SENT;
        return ResponseEntity.ok(new SendTestResult(success, result.getId(), result.getStatus().name()));
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got "
                + (principal == null ? "null" : principal.getClass()));
    }

    private EmailTemplate requireTemplate(Long id) {
        return emailTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Email template not found: id=" + id));
    }

    /** Applies every editable column except {@code templateKey} (immutable post-create). */
    private static void applyEditableFields(EmailTemplate entity, EmailTemplateUpsert body) {
        entity.setTemplateName(body.templateName());
        entity.setSubject(body.subject());
        entity.setContentHtml(body.contentHtml());
        entity.setContentPlain(body.contentPlain());
        entity.setFromAddress(body.fromAddress());
        entity.setFromDisplayName(body.fromDisplayName());
        entity.setReplyTo(body.replyTo());
        entity.setToDefault(body.toDefault());
        entity.setCcDefault(body.ccDefault());
        entity.setBccDefault(body.bccDefault());
        // has_attachment/enabled are NOT NULL columns (V92) — default to the same
        // values a brand-new EmailTemplate gets (entity field initializers) rather
        // than letting a null upsert value through to a NOT NULL column violation.
        entity.setHasAttachment(body.hasAttachment() != null ? body.hasAttachment() : Boolean.FALSE);
        entity.setEnabled(body.enabled() != null ? body.enabled() : Boolean.TRUE);
        entity.setDescription(body.description());
    }

    /** {@code POST /smtp/test} response body. */
    public record SmtpTestResult(boolean success, String message) {
    }

    /** {@code POST /templates/{id}/preview} response body. */
    public record TemplatePreviewResult(String subject, String html, String text) {
    }

    /** {@code POST /templates/{id}/send-test} response body. */
    public record SendTestResult(boolean success, Long logId, String status) {
    }
}
