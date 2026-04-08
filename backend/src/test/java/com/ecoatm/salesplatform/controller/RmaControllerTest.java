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
        when(rmaService.completeReview(eq(1L), isNull())).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/complete-review")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).completeReview(1L, null);
    }

    @Test
    void completeReview_withUserId_passesUserId() throws Exception {
        RmaResponse rmaResp = new RmaResponse();
        RmaDetailResponse detail = new RmaDetailResponse(rmaResp, Collections.emptyList());
        when(rmaService.completeReview(eq(1L), eq(5L))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/pws/rma/1/complete-review?userId=5")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(rmaService).completeReview(1L, 5L);
    }
}
