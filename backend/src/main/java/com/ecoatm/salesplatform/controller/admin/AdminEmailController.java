package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.email.SmtpConfigUpdate;
import com.ecoatm.salesplatform.dto.email.SmtpConfigView;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.email.SmtpConfigService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin REST surface for the unified email module's SMTP configuration
 * (Task 7 — SMTP endpoints only; the email-templates and email-log
 * surfaces land in T8/T9 and are intentionally NOT built here).
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
 */
@RestController
@RequestMapping("/api/v1/admin/email")
@PreAuthorize("hasRole('Administrator')")
public class AdminEmailController {

    private static final Logger log = LoggerFactory.getLogger(AdminEmailController.class);

    private final SmtpConfigService smtpConfigService;
    private final UploadRateLimiter uploadRateLimiter;
    // ObjectProvider, not a hard JavaMailSender dependency: MailSenderAutoConfiguration
    // only activates when spring.mail.host is set, which it is not in this app today
    // (see application.yml). A hard constructor dependency here would make the whole
    // controller — and therefore the whole app context — fail to boot.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public AdminEmailController(
            SmtpConfigService smtpConfigService,
            UploadRateLimiter uploadRateLimiter,
            ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.smtpConfigService = smtpConfigService;
        this.uploadRateLimiter = uploadRateLimiter;
        this.mailSenderProvider = mailSenderProvider;
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
    public SmtpConfigView updateSmtp(@RequestBody SmtpConfigUpdate patch, Authentication auth) {
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
    // Helpers
    // -------------------------------------------------------------------

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got "
                + (principal == null ? "null" : principal.getClass()));
    }

    /** {@code POST /smtp/test} response body. */
    public record SmtpTestResult(boolean success, String message) {
    }
}
