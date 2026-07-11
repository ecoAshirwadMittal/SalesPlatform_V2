package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.email.SmtpConfigUpdate;
import com.ecoatm.salesplatform.model.email.SmtpConfig;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.email.SmtpConfigService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Task 7 — admin SMTP config endpoints. The load-bearing assertions are
 * security ones: D2 (password never in the GET response or accepted from
 * the PUT body), authz (non-admin 403), and the {@code /smtp/test} rate
 * limit (it triggers a real outbound SMTP connection).
 */
@WebMvcTest(AdminEmailController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AdminEmailControllerSmtpIT {

    private static final long ADMIN_USER_ID = 9L;

    @Autowired private MockMvc mvc;

    @MockBean private SmtpConfigService smtpConfigService;
    @MockBean private UploadRateLimiter uploadRateLimiter;

    @BeforeEach
    void rateLimiterAllowsByDefault() {
        when(uploadRateLimiter.tryAcquire(anyString())).thenReturn(true);
    }

    // -------------------------------------------------------------------
    // GET /api/v1/admin/email/smtp
    // -------------------------------------------------------------------

    @Test
    void getSmtp_neverReturnsPassword() throws Exception {
        when(smtpConfigService.get()).thenReturn(sampleConfig());

        mvc.perform(get("/api/v1/admin/email/smtp").with(admin()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.serverHost").value("smtp.example.com"))
           .andExpect(jsonPath("$.password").doesNotExist())
           .andExpect(jsonPath("$.encryptedPassword").doesNotExist());
    }

    // -------------------------------------------------------------------
    // PUT /api/v1/admin/email/smtp
    // -------------------------------------------------------------------

    @Test
    void putSmtp_ignoresPasswordField() throws Exception {
        when(smtpConfigService.update(any(SmtpConfigUpdate.class), eq(ADMIN_USER_ID)))
                .thenReturn(sampleConfig());

        String bodyIncludingPasswordFields = "{"
                + "\"serverHost\":\"smtp.new.com\","
                + "\"serverPort\":587,"
                + "\"protocol\":\"SMTP\","
                + "\"fromAddress\":\"a@b.com\","
                + "\"fromDisplayName\":\"A\","
                + "\"replyTo\":\"r@b.com\","
                + "\"useSsl\":false,"
                + "\"useTls\":true,"
                + "\"enabled\":true,"
                + "\"maxRetryAttempts\":3,"
                + "\"timeoutMs\":10000,"
                + "\"password\":\"hunter2\","
                + "\"encryptedPassword\":\"hunter2-encrypted\""
                + "}";

        mvc.perform(put("/api/v1/admin/email/smtp").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyIncludingPasswordFields))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.serverHost").value("smtp.example.com"))
           .andExpect(jsonPath("$.password").doesNotExist())
           .andExpect(jsonPath("$.encryptedPassword").doesNotExist());

        // SmtpConfigUpdate has no password/encryptedPassword component at all, so
        // Jackson can only have bound the eleven real config fields — proving the
        // password fields sent on the wire were structurally dropped, not merely
        // unused.
        ArgumentCaptor<SmtpConfigUpdate> captor = ArgumentCaptor.forClass(SmtpConfigUpdate.class);
        verify(smtpConfigService).update(captor.capture(), eq(ADMIN_USER_ID));
        assertThat(captor.getValue()).isEqualTo(new SmtpConfigUpdate(
                "smtp.new.com", 587, "SMTP", "a@b.com", "A", "r@b.com",
                false, true, true, 3, 10000));
    }

    // -------------------------------------------------------------------
    // Authz — non-admin never reaches this surface
    // -------------------------------------------------------------------

    @Test
    void nonAdmin_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/email/smtp").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------
    // POST /api/v1/admin/email/smtp/test — rate limited
    // -------------------------------------------------------------------

    @Test
    void test_rateLimited_returns429_whenLimiterDenies() throws Exception {
        when(uploadRateLimiter.tryAcquire(anyString())).thenReturn(false);

        mvc.perform(post("/api/v1/admin/email/smtp/test").with(admin()))
           .andExpect(status().isTooManyRequests());

        verify(uploadRateLimiter).tryAcquire(anyString());
        verify(smtpConfigService, never()).get();
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

    private static SmtpConfig sampleConfig() {
        SmtpConfig config = new SmtpConfig();
        config.setId(1L);
        config.setServerHost("smtp.example.com");
        config.setServerPort(587);
        config.setProtocol("SMTP");
        config.setFromAddress("noreply@example.com");
        config.setFromDisplayName("EcoATM");
        config.setReplyTo("support@example.com");
        config.setUseSsl(false);
        config.setUseTls(true);
        config.setEnabled(true);
        config.setMaxRetryAttempts(3);
        config.setTimeoutMs(10000);
        config.setChangedDate(Instant.parse("2026-07-01T00:00:00Z"));
        return config;
    }
}
