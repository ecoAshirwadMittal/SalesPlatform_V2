package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.RmaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RmaController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class RmaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private RmaService rmaService;
    @MockBean private com.ecoatm.salesplatform.service.BuyerCodeService buyerCodeService;
    @MockBean private com.ecoatm.salesplatform.security.UploadRateLimiter uploadRateLimiter;
    @MockBean private com.ecoatm.salesplatform.service.rma.RmaOracleService rmaOracleService;

    @org.junit.jupiter.api.BeforeEach
    void ownershipAllowedByDefault() {
        // Buyer-code ownership is enforced now (CR-3); default the caller to owner.
        when(buyerCodeService.isUserAuthorizedForBuyerCode(anyLong(), anyLong())).thenReturn(true);
        // H-10 rate limiter is a collaborator now; allow uploads by default.
        when(uploadRateLimiter.tryAcquire(anyString())).thenReturn(true);
    }

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    // --- Auth checks ---

    @Test
    void getRmas_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/rma"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getSummary_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/rma/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRmaDetail_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/rma/1"))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/v1/pws/rma ---

    @Test
    void getRmas_withToken_returnsOk() throws Exception {
        when(rmaService.getAllRmas(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/rma")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRmas_withBuyerCodeId_filtersByBuyer() throws Exception {
        when(rmaService.getRmasByBuyerCode(eq(100L), isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/rma?buyerCodeId=100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).getRmasByBuyerCode(100L, null);
        verify(rmaService, never()).getAllRmas(any());
    }

    @Test
    void getRmas_withStatus_passesFilter() throws Exception {
        when(rmaService.getAllRmas("Open")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/rma?status=Open")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).getAllRmas("Open");
    }

    // --- GET /api/v1/pws/rma/summary ---

    @Test
    void getSummary_withToken_returnsSummaries() throws Exception {
        when(rmaService.getAllSummary()).thenReturn(List.of(
                new RmaSummaryResponse("Total", "Total", 5, 2500, 10, 15)
        ));

        mockMvc.perform(get("/api/v1/pws/rma/summary")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Total"))
                .andExpect(jsonPath("$[0].rmaCount").value(5));
    }

    @Test
    void getSummary_withBuyerCodeId_filtersByBuyer() throws Exception {
        when(rmaService.getSummary(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/rma/summary?buyerCodeId=100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).getSummary(100L);
        verify(rmaService, never()).getAllSummary();
    }

    // --- GET /api/v1/pws/rma/{rmaId} ---

    @Test
    void getRmaDetail_withToken_returnsDetail() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.getRmaDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/pws/rma/1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    // --- PUT /api/v1/pws/rma/{rmaId}/items/approve-all ---

    @Test
    void approveAll_withToken_returns200() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.approveAllItems(1L)).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/items/approve-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).approveAllItems(1L);
    }

    @Test
    void approveAll_withoutToken_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/pws/rma/1/items/approve-all"))
                .andExpect(status().isUnauthorized());
    }

    // --- PUT /api/v1/pws/rma/{rmaId}/items/decline-all ---

    @Test
    void declineAll_withToken_returns200() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.declineAllItems(1L)).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/items/decline-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).declineAllItems(1L);
    }

    // --- PUT /api/v1/pws/rma/items/{itemId}/status ---

    @Test
    void updateItemStatus_withToken_returns200() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.updateItemStatus(eq(5L), eq("Approve"))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/items/5/status")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Approve\"}"))
                .andExpect(status().isOk());

        verify(rmaService).updateItemStatus(5L, "Approve");
    }

    @Test
    void updateItemStatus_withoutToken_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/pws/rma/items/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Approve\"}"))
                .andExpect(status().isUnauthorized());
    }

    // --- PUT /api/v1/pws/rma/{rmaId}/complete-review ---

    @Test
    void completeReview_withToken_returns200() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.completeReview(eq(1L), eq(1L))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/complete-review")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).completeReview(1L, 1L);
    }

    // CR-3/C6: the reviewer id comes from the JWT principal; a spoofed ?userId=5
    // request param is ignored (previously it was trusted as the reviewer).
    @Test
    void completeReview_spoofedUserId_usesJwtPrincipal() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.completeReview(eq(1L), eq(1L))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/complete-review?userId=5")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).completeReview(1L, 1L);
    }

    // CR-3/C6 (security review 2026-07-10): a buyer must never reach the internal
    // review actions — a Bidder approving/finalizing an RMA is the core exploit.
    @Test
    void approveAll_withBidderRole_returns403() throws Exception {
        String bidderToken = jwtService.generateToken(
                9L, "bidder@buyerco.com", java.util.List.of("Bidder"), false);
        mockMvc.perform(put("/api/v1/pws/rma/1/items/approve-all")
                        .header("Authorization", "Bearer " + bidderToken))
                .andExpect(status().isForbidden());
    }

    // --- POST /api/v1/pws/rma/{rmaId}/resubmit-oracle (Task B0) ---

    private com.ecoatm.salesplatform.model.pws.Rma rmaWithOracle(
            Long id, boolean successful, String status, String number, Integer httpCode) {
        com.ecoatm.salesplatform.model.pws.Rma rma = new com.ecoatm.salesplatform.model.pws.Rma();
        rma.setId(id);
        rma.setIsSuccessful(successful);
        rma.setOracleRmaStatus(status);
        rma.setOracleNumber(number);
        rma.setOracleHttpCode(httpCode);
        return rma;
    }

    @Test
    void resubmitOracle_withInternalToken_returns200WithOracleStatus() throws Exception {
        when(rmaOracleService.createRmaInOracle(7L))
                .thenReturn(rmaWithOracle(7L, true, "simulated", "SIM-123", 200));

        mockMvc.perform(post("/api/v1/pws/rma/7/resubmit-oracle")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rmaId").value(7))
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.oracleNumber").value("SIM-123"))
                .andExpect(jsonPath("$.oracleHttpCode").value(200));

        verify(rmaOracleService).createRmaInOracle(7L);
    }

    // A failed Oracle create is still a 200 — the resubmit ran, Oracle rejected.
    @Test
    void resubmitOracle_oracleFailure_returns200WithSuccessFalse() throws Exception {
        when(rmaOracleService.createRmaInOracle(7L))
                .thenReturn(rmaWithOracle(7L, false, "Oracle API is disabled", null, null));

        mockMvc.perform(post("/api/v1/pws/rma/7/resubmit-oracle")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.oracleRmaStatus").value("Oracle API is disabled"));
    }

    // Wrong-role: a Bidder must never reach the internal Oracle resubmit action.
    // Denied by BOTH the SecurityConfig matcher (Bidder excluded) and the
    // method-level @PreAuthorize (defense-in-depth) → 403.
    @Test
    void resubmitOracle_withBidderRole_returns403() throws Exception {
        String bidderToken = jwtService.generateToken(
                9L, "bidder@buyerco.com", List.of("Bidder"), false);

        mockMvc.perform(post("/api/v1/pws/rma/7/resubmit-oracle")
                        .header("Authorization", "Bearer " + bidderToken))
                .andExpect(status().isForbidden());

        verify(rmaOracleService, never()).createRmaInOracle(anyLong());
    }

    @Test
    void resubmitOracle_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/pws/rma/7/resubmit-oracle"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resubmitOracle_rmaNotFound_returns404() throws Exception {
        when(rmaOracleService.createRmaInOracle(999L))
                .thenThrow(new IllegalArgumentException("RMA not found: 999"));

        mockMvc.perform(post("/api/v1/pws/rma/999/resubmit-oracle")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/v1/pws/rma/submit — M-6 size + content-type gate ---

    @Test
    void submitRma_validCsv_returns200() throws Exception {
        when(rmaService.submitRmaRequest(eq(100L), eq(1L), any()))
                .thenReturn(RmaSubmitResponse.success(55L, "RMA-1", 3));

        MockMultipartFile file = new MockMultipartFile("file", "rma.csv", "text/csv",
                "IMEI/Serial,Return Reason\n123456789012345,Defective\n".getBytes());

        mockMvc.perform(multipart("/api/v1/pws/rma/submit")
                        .file(file)
                        .param("buyerCodeId", "100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void submitRma_missingContentType_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "rma.csv", null,
                "IMEI/Serial,Return Reason\n123456789012345,Defective\n".getBytes());

        mockMvc.perform(multipart("/api/v1/pws/rma/submit")
                        .file(file)
                        .param("buyerCodeId", "100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("CSV")));
    }

    @Test
    void submitRma_oversizeFile_returns400() throws Exception {
        byte[] big = new byte[10 * 1024 * 1024 + 1]; // 10 MB + 1 byte
        MockMultipartFile file = new MockMultipartFile("file", "rma.csv", "text/csv", big);

        mockMvc.perform(multipart("/api/v1/pws/rma/submit")
                        .file(file)
                        .param("buyerCodeId", "100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("10 MB")));
    }
}
