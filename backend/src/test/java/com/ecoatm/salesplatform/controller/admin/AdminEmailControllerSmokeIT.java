package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context smoke IT for {@link AdminEmailController} (whole-branch review,
 * final review batch, fix #1).
 *
 * <p>The three existing admin-email ITs ({@link AdminEmailControllerSmtpIT},
 * {@link AdminEmailControllerTemplatesIT}, {@link AdminEmailControllerLogIT})
 * are all {@code @WebMvcTest} slices with every repository/service
 * {@code @MockBean}-ed out. None of them drives a real request through
 * controller -&gt; service -&gt; repository -&gt; real Postgres -&gt; DTO
 * serialization — which is exactly the gap that let a stale dev server's
 * live {@code 500} on these endpoints go undiagnosed at the HTTP-contract
 * level: the mocked suites would have stayed green regardless of a real
 * wiring, query, or serialization defect, because they never exercise real
 * collaborators. This class closes that gap: it extends
 * {@link PostgresIntegrationTest} (real Flyway'd Postgres, full Spring
 * context) instead of slicing the web layer.
 *
 * <p>Auth is injected directly via the
 * {@code SecurityMockMvcRequestPostProcessors.authentication(...)} post
 * processor (same shape {@code JwtAuthenticationFilter} installs from a real
 * JWT) rather than a real token — mirrors
 * {@code BidderDashboardFullChainIT#jwtBidder()}, the existing full-context
 * {@code @AutoConfigureMockMvc} + {@code PostgresIntegrationTest} pattern in
 * this codebase.
 *
 * <p>No fixture setup is needed: {@code email.smtp_config} id=1 is seeded by
 * {@code V92}, and an empty {@code email.template}/{@code email.log} result
 * set is still a valid {@code 200} — this suite asserts the HTTP contract
 * (status code, and that real serialization doesn't blow up), not row-level
 * content, which the mocked slice ITs already cover in depth.
 */
@AutoConfigureMockMvc
class AdminEmailControllerSmokeIT extends PostgresIntegrationTest {

    /** Arbitrary — none of the GET endpoints under test resolve the principal id. */
    private static final long ADMIN_USER_ID = 9L;

    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("GET /smtp — real controller -> SmtpConfigService -> repository -> DB round trip returns 200")
    void getSmtp_realContext_returns200() throws Exception {
        mvc.perform(get("/api/v1/admin/email/smtp").with(admin()))
           .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /templates — real controller -> repository -> DB round trip returns 200")
    void listTemplates_realContext_returns200() throws Exception {
        mvc.perform(get("/api/v1/admin/email/templates").with(admin()))
           .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /log — real controller -> repository -> DB round trip returns 200")
    void listLog_realContext_returns200() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log").with(admin()))
           .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /log?from=garbage — MethodArgumentTypeMismatchException maps to 400, not 500 (fix #4)")
    void listLog_unparsableFromParam_returns400() throws Exception {
        mvc.perform(get("/api/v1/admin/email/log").with(admin()).param("from", "not-a-date"))
           .andExpect(status().isBadRequest());
    }

    /** Auth shape that matches what {@code JwtAuthenticationFilter} installs from a real JWT. */
    private static RequestPostProcessor admin() {
        return authentication(new UsernamePasswordAuthenticationToken(
                ADMIN_USER_ID,
                "admin@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_Administrator"))));
    }
}
