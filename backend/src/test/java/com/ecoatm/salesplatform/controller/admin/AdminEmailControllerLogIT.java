package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import com.ecoatm.salesplatform.repository.email.EmailLogRepository;
import com.ecoatm.salesplatform.repository.email.EmailTemplateRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.email.EmailService;
import com.ecoatm.salesplatform.service.email.SmtpConfigService;
import com.ecoatm.salesplatform.service.email.TemplateRenderer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Task 9 — admin email-log list/detail/resend endpoints on the (Task 7/8)
 * {@link AdminEmailController}. Mirrors {@link AdminEmailControllerSmtpIT}
 * and {@link AdminEmailControllerTemplatesIT}'s auth setup exactly.
 * Load-bearing assertions: filter params are parsed and forwarded to
 * {@link EmailLogRepository#search} verbatim (including the default
 * page/size/sort when omitted), an invalid {@code status} value 400s rather
 * than 500ing, detail exposes the rendered {@code contentHtml} snapshot, and
 * resend resets {@code retryCount}/{@code nextAttemptAt} and saves BEFORE
 * calling {@link EmailService#resend} (the admin count-bypass, design §5).
 */
@WebMvcTest(AdminEmailController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AdminEmailControllerLogIT {

    private static final long ADMIN_USER_ID = 9L;

    @Autowired private MockMvc mvc;

    @MockBean private EmailLogRepository emailLogRepository;
    @MockBean private EmailService emailService;

    // AdminEmailController also depends on these T7/T8 collaborators —
    // @WebMvcTest needs every constructor dependency mocked even though this
    // suite never exercises the SMTP or template endpoints.
    @MockBean private EmailTemplateRepository emailTemplateRepository;
    @MockBean private TemplateRenderer templateRenderer;
    @MockBean private SmtpConfigService smtpConfigService;
    @MockBean private UploadRateLimiter uploadRateLimiter;

    // -------------------------------------------------------------------
    // GET /api/v1/admin/email/log — filtered + paged list
    // -------------------------------------------------------------------

    @Test
    void listLog_defaultParams_usesPageZeroSizeTwenty_andNullFilters() throws Exception {
        Page<EmailLog> page = new PageImpl<>(List.of(sampleLog(1L, EmailStatus.SENT)));
        when(emailLogRepository.search(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mvc.perform(get("/api/v1/admin/email/log").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].id").value(1))
           .andExpect(jsonPath("$.content[0].status").value("SENT"))
           .andExpect(jsonPath("$.totalElements").value(1));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(emailLogRepository).search(isNull(), isNull(), isNull(), isNull(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("createdDate").descending());
    }

    @Test
    void listLog_withAllFilters_parsesAndForwardsThemToSearch() throws Exception {
        Page<EmailLog> page = new PageImpl<>(List.of(sampleLog(2L, EmailStatus.FAILED)));
        when(emailLogRepository.search(
                eq(EmailStatus.FAILED), any(Instant.class), any(Instant.class), eq("WELCOME_EMAIL"), any(Pageable.class)))
                .thenReturn(page);

        mvc.perform(get("/api/v1/admin/email/log")
                .with(admin())
                .param("status", "FAILED")
                .param("from", "2026-01-01T00:00:00Z")
                .param("to", "2026-02-01T00:00:00Z")
                .param("templateKey", "WELCOME_EMAIL")
                .param("page", "2")
                .param("size", "5"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].id").value(2));

        ArgumentCaptor<Instant> fromCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> toCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(emailLogRepository).search(
                eq(EmailStatus.FAILED), fromCaptor.capture(), toCaptor.capture(), eq("WELCOME_EMAIL"), pageableCaptor.capture());
        assertThat(fromCaptor.getValue()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(toCaptor.getValue()).isEqualTo(Instant.parse("2026-02-01T00:00:00Z"));
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
    }

    @Test
    void listLog_invalidStatus_returns400_skipsSearch() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log").with(admin()).param("status", "BOGUS"))
           .andExpect(status().isBadRequest());

        verify(emailLogRepository, never()).search(any(), any(), any(), any(), any());
    }

    @Test
    void listLog_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log").with(bidder()))
           .andExpect(status().isForbidden());
    }

    @Test
    void listLog_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log"))
           .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------
    // GET /api/v1/admin/email/log/{id} — detail incl. rendered content_html
    // -------------------------------------------------------------------

    @Test
    void getLog_found_returns200WithContentHtmlSnapshot() throws Exception {
        EmailLog log = sampleLog(3L, EmailStatus.SENT);
        log.setContentHtml("<p>Rendered snapshot</p>");
        when(emailLogRepository.findById(3L)).thenReturn(Optional.of(log));

        mvc.perform(get("/api/v1/admin/email/log/3").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(3))
           .andExpect(jsonPath("$.contentHtml").value("<p>Rendered snapshot</p>"));
    }

    @Test
    void getLog_missing_returns404() throws Exception {
        when(emailLogRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/admin/email/log/99").with(admin()))
           .andExpect(status().isNotFound());
    }

    @Test
    void getLog_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log/3").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------
    // POST /api/v1/admin/email/log/{id}/resend — admin-forced, count-bypass
    // -------------------------------------------------------------------

    @Test
    void resendLog_success_resetsRetryCountAndNextAttempt_beforeCallingEmailServiceResend() throws Exception {
        EmailLog existing = sampleLog(5L, EmailStatus.FAILED);
        existing.setRetryCount(3);
        existing.setNextAttemptAt(Instant.now().plusSeconds(120));
        when(emailLogRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(emailLogRepository.save(any(EmailLog.class))).thenAnswer(inv -> inv.getArgument(0));

        EmailLog resent = sampleLog(5L, EmailStatus.SENT);
        resent.setRetryCount(0);
        when(emailService.resend(5L)).thenReturn(resent);

        mvc.perform(post("/api/v1/admin/email/log/5/resend").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(5))
           .andExpect(jsonPath("$.status").value("SENT"));

        // Load-first, reset+save, THEN resend — the admin count-bypass sequencing.
        InOrder inOrder = inOrder(emailLogRepository, emailService);
        ArgumentCaptor<EmailLog> savedCaptor = ArgumentCaptor.forClass(EmailLog.class);
        inOrder.verify(emailLogRepository).findById(5L);
        inOrder.verify(emailLogRepository).save(savedCaptor.capture());
        inOrder.verify(emailService).resend(5L);

        assertThat(savedCaptor.getValue().getRetryCount()).isEqualTo(0);
        assertThat(savedCaptor.getValue().getNextAttemptAt()).isNull();
    }

    @Test
    void resendLog_missing_returns404_skipsSaveAndEmailServiceResend() throws Exception {
        when(emailLogRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/v1/admin/email/log/99/resend").with(admin()))
           .andExpect(status().isNotFound());

        verify(emailLogRepository, never()).save(any());
        verify(emailService, never()).resend(any());
    }

    @Test
    void resendLog_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/email/log/5/resend").with(bidder()))
           .andExpect(status().isForbidden());

        verify(emailLogRepository, never()).findById(any());
        verify(emailService, never()).resend(any());
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

    private static EmailLog sampleLog(Long id, EmailStatus status) {
        EmailLog log = new EmailLog();
        log.setId(id);
        log.setTemplateKey("WELCOME_EMAIL");
        log.setFromAddress("sender@example.com");
        log.setToAddress("buyer@example.com");
        log.setSubject("Subject");
        log.setContentHtml("<p>Body</p>");
        log.setStatus(status);
        log.setRetryCount(0);
        log.setCreatedDate(Instant.parse("2026-01-01T00:00:00Z"));
        return log;
    }
}
