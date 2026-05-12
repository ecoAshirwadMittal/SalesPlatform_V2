package com.ecoatm.salesplatform.controller.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.BuyerCodeOption;
import com.ecoatm.salesplatform.dto.partialcredit.BuyerUserOption;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.OnBehalfSubmissionService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OnBehalfPartialCreditController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OnBehalfPartialCreditControllerIT {

    @Autowired MockMvc mvc;
    @MockBean OnBehalfSubmissionService onBehalfService;
    @MockBean CreditRequestService creditRequestService;

    @BeforeEach
    void primeStatusRow() {
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(1L);
        row.setSystemStatus(SystemStatus.DRAFT);
        row.setExternalStatusText("Draft");
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(row));
        when(creditRequestService.getMissingLines(anyLong())).thenReturn(List.of());
        when(creditRequestService.getWrongLines(anyLong())).thenReturn(List.of());
        when(creditRequestService.getEncumberedLines(anyLong())).thenReturn(List.of());
    }

    // ── GET /buyer-codes ─────────────────────────────────────────────

    @Test
    void listBuyerCodes_asSalesRep_returns200() throws Exception {
        when(onBehalfService.listEligibleBuyerCodes(any())).thenReturn(List.of(
                new BuyerCodeOption(1L, "20399", "Acme Corp")));

        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes").with(salesRep()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[0].code").value("20399"))
           .andExpect(jsonPath("$[0].buyerName").value("Acme Corp"));
    }

    @Test
    void listBuyerCodes_asAdministrator_returns200() throws Exception {
        when(onBehalfService.listEligibleBuyerCodes(any())).thenReturn(List.of());

        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes").with(administrator()))
           .andExpect(status().isOk());
    }

    @Test
    void listBuyerCodes_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes").with(bidder()))
           .andExpect(status().isForbidden());
    }

    @Test
    void listBuyerCodes_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes"))
           .andExpect(status().isUnauthorized());
    }

    // ── GET /buyer-codes/{id}/users ──────────────────────────────────

    @Test
    void listBuyers_asSalesRep_returns200() throws Exception {
        when(onBehalfService.listBuyersForCode(any(), eq(42L))).thenReturn(List.of(
                new BuyerUserOption(101L, "Nadia", "nadia@example.com")));

        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes/42/users").with(salesRep()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].userId").value(101))
           .andExpect(jsonPath("$[0].email").value("nadia@example.com"));
    }

    @Test
    void listBuyers_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/salesrep/partial-credit/buyer-codes/42/users").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // ── POST /drafts ─────────────────────────────────────────────────

    @Test
    void createDraft_asSalesRep_returns201_withDetail() throws Exception {
        CreditRequest draft = new CreditRequest();
        draft.setId(500L);
        draft.setRequestNumber("PCR-1");
        draft.setOrderNumber("SO-1");
        draft.setBuyerCodeId(42L);
        draft.setStatusId(1L);
        draft.setRequestDate(Instant.parse("2026-05-12T00:00:00Z"));
        draft.setHasMissingDevice(false);
        draft.setHasWrongDevice(false);
        draft.setHasEncumberedDevice(false);
        draft.setIsOnBehalf(true);
        draft.setOnBehalfOfId(101L);
        draft.setOnBehalfBuyerCodeId(42L);
        when(onBehalfService.createDraftOnBehalf(any(), eq("SO-1"), eq(42L), eq(101L))).thenReturn(draft);

        mvc.perform(post("/api/v1/salesrep/partial-credit/drafts")
                .with(salesRep())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderNumber\":\"SO-1\",\"buyerCodeId\":42,\"onBehalfOfUserId\":101}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(500))
           .andExpect(jsonPath("$.orderNumber").value("SO-1"));
    }

    @Test
    void createDraft_unassociatedUser_returns403() throws Exception {
        doThrow(new SecurityException("User 999 is not associated with buyer_code_id=42"))
            .when(onBehalfService).createDraftOnBehalf(any(), any(), any(), eq(999L));

        mvc.perform(post("/api/v1/salesrep/partial-credit/drafts")
                .with(salesRep())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderNumber\":\"SO-1\",\"buyerCodeId\":42,\"onBehalfOfUserId\":999}"))
           .andExpect(status().isForbidden())
           .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void createDraft_blankOrder_returns400() throws Exception {
        doThrow(new IllegalArgumentException("orderNumber is required"))
            .when(onBehalfService).createDraftOnBehalf(any(), eq("  "), any(), any());

        mvc.perform(post("/api/v1/salesrep/partial-credit/drafts")
                .with(salesRep())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderNumber\":\"  \",\"buyerCodeId\":42,\"onBehalfOfUserId\":101}"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    void createDraft_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/salesrep/partial-credit/drafts")
                .with(bidder())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderNumber\":\"SO-1\",\"buyerCodeId\":42,\"onBehalfOfUserId\":101}"))
           .andExpect(status().isForbidden());
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static RequestPostProcessor salesRep() {
        return authentication(asAuth(7L, "rep@test.com", "SalesRep"));
    }

    private static RequestPostProcessor administrator() {
        return authentication(asAuth(3L, "admin@test.com", "Administrator"));
    }

    private static RequestPostProcessor bidder() {
        return authentication(asAuth(1L, "bidder@test.com", "Bidder"));
    }

    private static UsernamePasswordAuthenticationToken asAuth(
            Long userId, String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }
}
