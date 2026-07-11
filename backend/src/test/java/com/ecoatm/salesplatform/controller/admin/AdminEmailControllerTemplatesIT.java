package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.model.email.EmailTemplate;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;
import com.ecoatm.salesplatform.repository.email.EmailTemplateRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.email.EmailService;
import com.ecoatm.salesplatform.service.email.TemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Task 8 — admin email-template CRUD + preview + send-test endpoints on the
 * (Task 7) {@link AdminEmailController}. Mirrors
 * {@link AdminEmailControllerSmtpIT}'s auth setup exactly. Load-bearing
 * assertions: duplicate {@code templateKey} -> 409, {@code templateKey}
 * immutability on PUT, 404 everywhere an id is missing, preview bypassing
 * the enabled-check, and the send-test rate limit being keyed by the
 * authenticated user (not IP).
 */
@WebMvcTest(AdminEmailController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AdminEmailControllerTemplatesIT {

    private static final long ADMIN_USER_ID = 9L;

    @Autowired private MockMvc mvc;

    @MockBean private EmailTemplateRepository emailTemplateRepository;
    @MockBean private EmailService emailService;
    @MockBean private TemplateRenderer templateRenderer;
    @MockBean private UploadRateLimiter uploadRateLimiter;

    // AdminEmailController also depends on these T7/T9 collaborators —
    // @WebMvcTest needs every constructor dependency mocked even though this
    // suite never exercises the SMTP or /log endpoints.
    @MockBean private com.ecoatm.salesplatform.service.email.SmtpConfigService smtpConfigService;
    @MockBean private EmailLogRepository emailLogRepository;

    @BeforeEach
    void rateLimiterAllowsByDefault() {
        when(uploadRateLimiter.tryAcquire(anyString())).thenReturn(true);
    }

    // -------------------------------------------------------------------
    // POST /api/v1/admin/email/templates — create
    // -------------------------------------------------------------------

    @Test
    void createTemplate_success_returns201WithId() throws Exception {
        when(emailTemplateRepository.findByTemplateKey("NEW_KEY")).thenReturn(Optional.empty());
        when(emailTemplateRepository.save(any(EmailTemplate.class))).thenAnswer(inv -> {
            EmailTemplate saved = inv.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        mvc.perform(post("/api/v1/admin/email/templates").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("NEW_KEY", "New Template", "Subject", "<p>Body</p>")))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(42))
           .andExpect(jsonPath("$.templateKey").value("NEW_KEY"))
           .andExpect(jsonPath("$.templateName").value("New Template"));

        ArgumentCaptor<EmailTemplate> captor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailTemplateRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedById()).isEqualTo(ADMIN_USER_ID);
        assertThat(captor.getValue().getChangedById()).isEqualTo(ADMIN_USER_ID);
        assertThat(captor.getValue().getCreatedDate()).isNotNull();
        assertThat(captor.getValue().getChangedDate()).isNotNull();
    }

    @Test
    void createTemplate_duplicateKey_returns409() throws Exception {
        when(emailTemplateRepository.findByTemplateKey("DUP_KEY"))
                .thenReturn(Optional.of(sampleTemplate(1L, "DUP_KEY")));

        mvc.perform(post("/api/v1/admin/email/templates").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("DUP_KEY", "Name", "Subject", "<p>Body</p>")))
           .andExpect(status().isConflict());

        verify(emailTemplateRepository, never()).save(any());
    }

    @Test
    void createTemplate_badTemplateKeyPattern_returns400() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("Bad Key!", "Name", "Subject", "<p>Body</p>")))
           .andExpect(status().isBadRequest());

        verify(emailTemplateRepository, never()).findByTemplateKey(any());
        verify(emailTemplateRepository, never()).save(any());
    }

    @Test
    void createTemplate_blankSubject_returns400() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("VALID_KEY", "Name", "", "<p>Body</p>")))
           .andExpect(status().isBadRequest());

        verify(emailTemplateRepository, never()).save(any());
    }

    @Test
    void createTemplate_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates").with(bidder())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("KEY", "Name", "Subject", "<p>Body</p>")))
           .andExpect(status().isForbidden());

        verify(emailTemplateRepository, never()).save(any());
    }

    // -------------------------------------------------------------------
    // GET /api/v1/admin/email/templates — list
    // -------------------------------------------------------------------

    @Test
    void listTemplates_returnsAll() throws Exception {
        when(emailTemplateRepository.findAll())
                .thenReturn(List.of(sampleTemplate(1L, "K1"), sampleTemplate(2L, "K2")));

        mvc.perform(get("/api/v1/admin/email/templates").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].templateKey").value("K1"))
           .andExpect(jsonPath("$[1].templateKey").value("K2"));
    }

    @Test
    void listTemplates_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/email/templates").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------
    // GET /api/v1/admin/email/templates/{id}
    // -------------------------------------------------------------------

    @Test
    void getTemplate_found_returns200() throws Exception {
        when(emailTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate(1L, "K1")));

        mvc.perform(get("/api/v1/admin/email/templates/1").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.templateKey").value("K1"));
    }

    @Test
    void getTemplate_missing_returns404() throws Exception {
        when(emailTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/admin/email/templates/99").with(admin()))
           .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------
    // PUT /api/v1/admin/email/templates/{id} — templateKey is immutable
    // -------------------------------------------------------------------

    @Test
    void updateTemplate_updatesChangedDate_andIgnoresTemplateKeyChange() throws Exception {
        EmailTemplate existing = sampleTemplate(1L, "ORIGINAL_KEY");
        Instant oldChangedDate = existing.getChangedDate();
        when(emailTemplateRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(emailTemplateRepository.save(any(EmailTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(put("/api/v1/admin/email/templates/1").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("ATTEMPTED_NEW_KEY", "Updated Name", "Updated Subject", "<p>Updated</p>")))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.templateKey").value("ORIGINAL_KEY"))
           .andExpect(jsonPath("$.templateName").value("Updated Name"));

        ArgumentCaptor<EmailTemplate> captor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailTemplateRepository).save(captor.capture());
        assertThat(captor.getValue().getTemplateKey()).isEqualTo("ORIGINAL_KEY");
        assertThat(captor.getValue().getChangedDate()).isAfter(oldChangedDate);
        assertThat(captor.getValue().getChangedById()).isEqualTo(ADMIN_USER_ID);
    }

    @Test
    void updateTemplate_missing_returns404() throws Exception {
        when(emailTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(put("/api/v1/admin/email/templates/99").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(upsertJson("KEY", "Name", "Subject", "<p>Body</p>")))
           .andExpect(status().isNotFound());

        verify(emailTemplateRepository, never()).save(any());
    }

    // -------------------------------------------------------------------
    // DELETE /api/v1/admin/email/templates/{id}
    // -------------------------------------------------------------------

    @Test
    void deleteTemplate_success_returns204() throws Exception {
        when(emailTemplateRepository.existsById(1L)).thenReturn(true);

        mvc.perform(delete("/api/v1/admin/email/templates/1").with(admin()))
           .andExpect(status().isNoContent());

        verify(emailTemplateRepository).deleteById(1L);
    }

    @Test
    void deleteTemplate_missing_returns404() throws Exception {
        when(emailTemplateRepository.existsById(99L)).thenReturn(false);

        mvc.perform(delete("/api/v1/admin/email/templates/99").with(admin()))
           .andExpect(status().isNotFound());

        verify(emailTemplateRepository, never()).deleteById(any());
    }

    // -------------------------------------------------------------------
    // POST /api/v1/admin/email/templates/{id}/preview — bypasses enabled
    // -------------------------------------------------------------------

    @Test
    void previewTemplate_rendersVars_bypassesEnabledCheck() throws Exception {
        EmailTemplate disabled = sampleTemplate(1L, "K1");
        disabled.setEnabled(false);
        when(emailTemplateRepository.findById(1L)).thenReturn(Optional.of(disabled));
        when(templateRenderer.renderPlain(eq(disabled.getSubject()), anyMap())).thenReturn("Rendered Subject Bob");
        when(templateRenderer.render(eq(disabled.getContentHtml()), anyMap())).thenReturn("<p>Rendered Bob</p>");
        when(templateRenderer.renderPlain(eq(disabled.getContentPlain()), anyMap())).thenReturn("Rendered Plain Bob");

        mvc.perform(post("/api/v1/admin/email/templates/1/preview").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vars\":{\"name\":\"Bob\"}}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.subject").value("Rendered Subject Bob"))
           .andExpect(jsonPath("$.html").value("<p>Rendered Bob</p>"))
           .andExpect(jsonPath("$.text").value("Rendered Plain Bob"));
    }

    @Test
    void previewTemplate_nullContentPlain_textIsNull() throws Exception {
        EmailTemplate noPlain = sampleTemplate(1L, "K1");
        noPlain.setContentPlain(null);
        when(emailTemplateRepository.findById(1L)).thenReturn(Optional.of(noPlain));
        when(templateRenderer.renderPlain(eq(noPlain.getSubject()), anyMap())).thenReturn("Subj");
        when(templateRenderer.render(eq(noPlain.getContentHtml()), anyMap())).thenReturn("<p>Body</p>");

        mvc.perform(post("/api/v1/admin/email/templates/1/preview").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vars\":{}}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.subject").value("Subj"))
           .andExpect(jsonPath("$.html").value("<p>Body</p>"))
           .andExpect(jsonPath("$.text").doesNotExist());
    }

    @Test
    void previewTemplate_missing_returns404() throws Exception {
        when(emailTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/v1/admin/email/templates/99/preview").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vars\":{}}"))
           .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------
    // POST /api/v1/admin/email/templates/{id}/send-test
    // -------------------------------------------------------------------

    @Test
    void sendTest_success_returns200_callsEmailServiceWithOverride_userKeyedRateLimit() throws Exception {
        EmailTemplate template = sampleTemplate(7L, "WELCOME_EMAIL");
        when(emailTemplateRepository.findById(7L)).thenReturn(Optional.of(template));
        EmailLog resultLog = new EmailLog();
        resultLog.setId(555L);
        resultLog.setStatus(EmailStatus.SENT);
        when(emailService.sendTemplated(
                eq("WELCOME_EMAIL"), anyMap(), any(EmailService.SendOverrides.class), any(EmailService.SourceRef.class)))
                .thenReturn(resultLog);

        mvc.perform(post("/api/v1/admin/email/templates/7/send-test").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"qa@example.com\",\"vars\":{\"name\":\"Bob\"}}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.success").value(true))
           .andExpect(jsonPath("$.logId").value(555))
           .andExpect(jsonPath("$.status").value("SENT"));

        ArgumentCaptor<EmailService.SendOverrides> overridesCaptor =
                ArgumentCaptor.forClass(EmailService.SendOverrides.class);
        ArgumentCaptor<EmailService.SourceRef> sourceCaptor = ArgumentCaptor.forClass(EmailService.SourceRef.class);
        verify(emailService).sendTemplated(eq("WELCOME_EMAIL"), anyMap(), overridesCaptor.capture(), sourceCaptor.capture());
        assertThat(overridesCaptor.getValue().to()).containsExactly("qa@example.com");
        assertThat(overridesCaptor.getValue().cc()).isNull();
        assertThat(overridesCaptor.getValue().bcc()).isNull();
        assertThat(sourceCaptor.getValue().module()).isEqualTo("ADMIN_SEND_TEST");
        assertThat(sourceCaptor.getValue().id()).isEqualTo(7L);

        // User-keyed, not IP-keyed (security-review hardening) — exact key match.
        verify(uploadRateLimiter).tryAcquire(eq("email-send-test:" + ADMIN_USER_ID));
    }

    @Test
    void sendTest_missingTemplate_returns404() throws Exception {
        when(emailTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/v1/admin/email/templates/99/send-test").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"qa@example.com\",\"vars\":{}}"))
           .andExpect(status().isNotFound());

        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    void sendTest_rateLimited_returns429_andSkipsTemplateLookup() throws Exception {
        when(uploadRateLimiter.tryAcquire(anyString())).thenReturn(false);

        mvc.perform(post("/api/v1/admin/email/templates/7/send-test").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"qa@example.com\",\"vars\":{}}"))
           .andExpect(status().isTooManyRequests());

        verify(emailTemplateRepository, never()).findById(any());
        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    void sendTest_badEmail_returns400() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates/7/send-test").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"not-an-email\",\"vars\":{}}"))
           .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    void sendTest_blankToAddress_returns400() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates/7/send-test").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"\",\"vars\":{}}"))
           .andExpect(status().isBadRequest());

        verify(emailService, never()).sendTemplated(any(), any(), any(), any());
    }

    @Test
    void sendTest_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/email/templates/7/send-test").with(bidder())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"toAddress\":\"qa@example.com\",\"vars\":{}}"))
           .andExpect(status().isForbidden());

        verify(uploadRateLimiter, never()).tryAcquire(anyString());
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static RequestPostProcessor admin() {
        return authentication(asAuth(ADMIN_USER_ID, "admin@test.com", "Administrator"));
    }

    private static RequestPostProcessor bidder() {
        return authentication(asAuth(1L, "bidder@test.com", "Bidder"));
    }

    private static UsernamePasswordAuthenticationToken asAuth(Long userId, String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }

    private static EmailTemplate sampleTemplate(Long id, String key) {
        EmailTemplate t = new EmailTemplate();
        t.setId(id);
        t.setTemplateKey(key);
        t.setTemplateName("Sample Template");
        t.setSubject("Sample Subject {{name}}");
        t.setContentHtml("<p>Hello {{name}}</p>");
        t.setContentPlain("Hello {{name}}");
        t.setFromAddress("sender@example.com");
        t.setFromDisplayName("Sender");
        t.setReplyTo("reply@example.com");
        t.setToDefault("buyer@example.com");
        t.setHasAttachment(false);
        t.setEnabled(true);
        t.setDescription("desc");
        t.setCreatedDate(Instant.parse("2026-01-01T00:00:00Z"));
        t.setChangedDate(Instant.parse("2026-01-01T00:00:00Z"));
        t.setCreatedById(9L);
        t.setChangedById(9L);
        return t;
    }

    private static String upsertJson(String templateKey, String templateName, String subject, String contentHtml) {
        return "{"
                + "\"templateKey\":\"" + templateKey + "\","
                + "\"templateName\":\"" + templateName + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"contentHtml\":\"" + contentHtml + "\","
                + "\"contentPlain\":\"Plain text body\","
                + "\"fromAddress\":\"sender@example.com\","
                + "\"fromDisplayName\":\"Sender\","
                + "\"replyTo\":\"reply@example.com\","
                + "\"toDefault\":\"buyer@example.com\","
                + "\"ccDefault\":null,"
                + "\"bccDefault\":null,"
                + "\"hasAttachment\":false,"
                + "\"enabled\":true,"
                + "\"description\":\"A template\""
                + "}";
    }
}
