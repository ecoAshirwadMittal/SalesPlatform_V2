package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.OfferSummary;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.OfferReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferReviewController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OfferReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OfferReviewService reviewService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    @Test
    void summary_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/offer-review/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void summary_withToken_returnsSummaries() throws Exception {
        when(reviewService.getStatusSummaries()).thenReturn(List.of(
                new OfferSummary("Sales_Review", "Sales Review", 3, 10, 50, BigDecimal.valueOf(500))
        ));

        mockMvc.perform(get("/api/v1/pws/offer-review/summary")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Sales_Review"))
                .andExpect(jsonPath("$[0].offerCount").value(3));
    }

    @Test
    void listOffers_withToken_returnsOffers() throws Exception {
        when(reviewService.listOffers(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/offer-review")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void setItemAction_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.setItemAction(eq(1L), eq(2L), eq("Accept"))).thenReturn(response);

        mockMvc.perform(put("/api/v1/pws/offer-review/1/items/2/action")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"Accept\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1));
    }

    @Test
    void completeReview_withToken_returns200() throws Exception {
        when(reviewService.completeReview(eq(1L), isNull()))
                .thenReturn(SubmitResponse.offerSubmitted(1L, "OFF-001"));

        mockMvc.perform(post("/api/v1/pws/offer-review/1/complete-review")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getOfferDetail_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.getOfferDetail(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pws/offer-review/1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1));
    }

    @Test
    void updateItemCounter_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.updateItemCounter(eq(1L), eq(2L), any(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/pws/offer-review/1/items/2/counter")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"counterQty\":5,\"counterPrice\":8.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1));
    }

    @Test
    void acceptAll_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.acceptAll(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/pws/offer-review/1/accept-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }

    @Test
    void declineAll_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.declineAll(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/pws/offer-review/1/decline-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }

    @Test
    void finalizeAll_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(reviewService.finalizeAll(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/pws/offer-review/1/finalize-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }
}
