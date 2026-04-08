package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.CounterOfferService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CounterOfferController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class CounterOfferControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private CounterOfferService counterOfferService;

    private String validToken() {
        return jwtService.generateToken(1L, "bidder@test.com", List.of("Bidder"), false);
    }

    @Test
    void listCounterOffers_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/counter-offers").param("buyerCodeId", "100"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listCounterOffers_withToken_returns200() throws Exception {
        when(counterOfferService.listCounterOffers(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pws/counter-offers")
                        .param("buyerCodeId", "100")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCounterOfferDetail_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(counterOfferService.getCounterOfferDetail(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pws/counter-offers/1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1));
    }

    @Test
    void setBuyerItemAction_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(counterOfferService.setBuyerItemAction(eq(1L), eq(2L), eq("Accept")))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/pws/counter-offers/1/items/2/action")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"buyerCounterStatus\":\"Accept\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1));
    }

    @Test
    void acceptAllCounters_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setOfferId(1L);
        response.setItems(List.of());
        when(counterOfferService.acceptAllCounters(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/pws/counter-offers/1/accept-all")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }

    @Test
    void submitCounterResponse_withToken_returns200() throws Exception {
        when(counterOfferService.submitCounterResponse(1L))
                .thenReturn(SubmitResponse.submitted(1L, "ORD-001"));

        mockMvc.perform(post("/api/v1/pws/counter-offers/1/submit")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void cancelOffer_withToken_returns200() throws Exception {
        when(counterOfferService.cancelOffer(1L))
                .thenReturn(SubmitResponse.submitted(1L, "OFF-001"));

        mockMvc.perform(post("/api/v1/pws/counter-offers/1/cancel")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
