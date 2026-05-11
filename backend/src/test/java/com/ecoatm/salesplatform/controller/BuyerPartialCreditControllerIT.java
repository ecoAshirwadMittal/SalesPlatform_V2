package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestValidationException;
import com.ecoatm.salesplatform.service.partialcredit.ValidationIssue;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuyerPartialCreditController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class BuyerPartialCreditControllerIT {

    @Autowired MockMvc mvc;
    @MockBean CreditRequestService service;

    @BeforeEach
    void primeDefaultStatusRow() {
        // Every controller path calls findStatusRow when building a DTO,
        // so prime the mock to return a sensible PENDING row by default.
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(2L);
        row.setSystemStatus(SystemStatus.PENDING_APPROVAL);
        row.setExternalStatusText("Pending Approval");
        when(service.findStatusRow(anyLong())).thenReturn(Optional.of(row));
        when(service.getMissingLines(anyLong())).thenReturn(List.of());
        when(service.getWrongLines(anyLong())).thenReturn(List.of());
        when(service.getEncumberedLines(anyLong())).thenReturn(List.of());
    }

    @Test
    void createDraft_returns201_withDetailBody() throws Exception {
        CreditRequest cr = newRequest(1L, "PCR-X1", "SO-1");
        when(service.createDraft(eq("SO-1"), eq(100L), any(), anyBoolean())).thenReturn(cr);

        mvc.perform(post("/api/v1/buyer/partial-credit/draft")
                .with(bidder())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderNumber\":\"SO-1\",\"buyerCodeId\":100}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(1))
           .andExpect(jsonPath("$.requestNumber").value("PCR-X1"))
           .andExpect(jsonPath("$.orderNumber").value("SO-1"));
    }

    @Test
    void update_returnsUpdatedDetail() throws Exception {
        CreditRequest cr = newRequest(7L, "PCR-X2", "SO-2");
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(service.update(eq(7L), any(), anyBoolean(),
                eq(Boolean.TRUE), any(), any(), any())).thenReturn(cr);

        mvc.perform(patch("/api/v1/buyer/partial-credit/7")
                .with(bidder())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"hasMissingDevice\":true,\"shipmentDamaged\":\"NO\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(7))
           .andExpect(jsonPath("$.hasMissingDevice").value(true));
    }

    @Test
    void submit_returnsPendingApproval() throws Exception {
        CreditRequest cr = newRequest(9L, "PCR-X3", "SO-3");
        cr.setStatusId(2L);
        when(service.submit(eq(9L), any(), anyBoolean())).thenReturn(cr);

        mvc.perform(post("/api/v1/buyer/partial-credit/9/submit").with(bidder()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.systemStatus").value("PENDING_APPROVAL"));
    }

    @Test
    void submit_invalidRequest_returns400WithIssues() throws Exception {
        when(service.submit(eq(11L), any(), anyBoolean()))
                .thenThrow(new CreditRequestValidationException(List.of(
                        ValidationIssue.noReasonSelected(),
                        ValidationIssue.damageNotAnswered())));

        mvc.perform(post("/api/v1/buyer/partial-credit/11/submit").with(bidder()))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
           .andExpect(jsonPath("$.issues[0].code").value("NO_REASON_SELECTED"))
           .andExpect(jsonPath("$.issues[1].code").value("DAMAGE_NOT_ANSWERED"));
    }

    @Test
    void getById_returnsDetail() throws Exception {
        CreditRequest cr = newRequest(13L, "PCR-X4", "SO-4");
        when(service.getById(eq(13L), any(), anyBoolean())).thenReturn(cr);

        mvc.perform(get("/api/v1/buyer/partial-credit/13").with(bidder()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(13));
    }

    @Test
    void getById_otherBuyersRequest_returns403() throws Exception {
        when(service.getById(eq(13L), any(), anyBoolean()))
                .thenThrow(new SecurityException("User does not own buyer_code_id=999"));

        mvc.perform(get("/api/v1/buyer/partial-credit/13").with(bidder()))
           .andExpect(status().isForbidden())
           .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void salesOpsRole_blocked() throws Exception {
        mvc.perform(get("/api/v1/buyer/partial-credit?buyerCodeId=100").with(salesOps()))
           .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/v1/buyer/partial-credit?buyerCodeId=100"))
           .andExpect(status().isUnauthorized());
    }

    // ─── helpers ───────────────────────────────────────────────────────

    /** Authenticated bidder with Long principal — matches what
     *  JwtAuthenticationFilter sets in production. */
    private static RequestPostProcessor bidder() {
        return authentication(asAuth(1L, "bidder@test.com", "Bidder"));
    }

    private static RequestPostProcessor salesOps() {
        return authentication(asAuth(2L, "ops@test.com", "SalesOps"));
    }

    private static UsernamePasswordAuthenticationToken asAuth(
            Long userId, String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }

    private static CreditRequest newRequest(long id, String number, String order) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber(number);
        cr.setOrderNumber(order);
        cr.setRequestDate(Instant.now());
        cr.setStatusId(1L);
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }
}
