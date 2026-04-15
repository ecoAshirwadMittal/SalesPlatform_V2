package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.service.OfferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OfferControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OfferService offerService;
    @MockBean private BuyerCodeService buyerCodeService;

    private String validToken() {
        return jwtService.generateToken(1L, "test@test.com", List.of("Bidder"), false);
    }

    @Test
    void getCart_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/pws/offers/cart")
                        .param("buyerCodeId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCart_withToken_returnsCart() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setTotalQty(0);
        response.setTotalSkus(0);
        response.setTotalPrice(BigDecimal.ZERO);
        response.setItems(List.of());
        when(offerService.getOrCreateCart(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pws/offers/cart")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQty").value(0));
    }

    @Test
    void upsertItem_withBlankSku_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/pws/offers/cart/items")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"\",\"offerPrice\":10.00,\"quantity\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertItem_withNullQuantity_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/pws/offers/cart/items")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"PWS001\",\"offerPrice\":10.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertItem_withNegativeQuantity_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/pws/offers/cart/items")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"PWS001\",\"offerPrice\":10.00,\"quantity\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertItem_withValidRequest_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setTotalQty(1);
        response.setTotalSkus(1);
        response.setTotalPrice(BigDecimal.TEN);
        response.setItems(List.of());
        when(offerService.upsertCartItem(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/pws/offers/cart/items")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"PWS001\",\"offerPrice\":10.00,\"quantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQty").value(1));
    }

    // --- removeItem ---

    @Test
    void removeItem_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setItems(List.of());
        when(offerService.removeItem(1L, "SKU-001")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/pws/offers/cart/items/SKU-001")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }

    // --- resetCart ---

    @Test
    void resetCart_withToken_returns200() throws Exception {
        OfferResponse response = new OfferResponse();
        response.setItems(List.of());
        when(offerService.resetCart(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/pws/offers/cart")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());
    }

    // --- submitCart ---

    @Test
    void submitCart_withToken_returns200() throws Exception {
        when(offerService.submitCart(eq(1L), isNull()))
                .thenReturn(SubmitResponse.salesReview(1L, 2));

        mockMvc.perform(post("/api/v1/pws/offers/cart/submit")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void submitCart_exception_returnsError() throws Exception {
        when(offerService.submitCart(eq(1L), isNull()))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/v1/pws/offers/cart/submit")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    // --- submitOffer ---

    @Test
    void submitOffer_withToken_returns200() throws Exception {
        when(offerService.submitOffer(1L, null))
                .thenReturn(SubmitResponse.offerSubmitted(1L, "OFF-001"));

        mockMvc.perform(post("/api/v1/pws/offers/1/submit-offer")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- submitOrder ---

    @Test
    void submitOrder_withToken_returns200() throws Exception {
        when(offerService.submitOrder(eq(1L), isNull()))
                .thenReturn(SubmitResponse.submitted(1L, "ORD-001"));

        mockMvc.perform(post("/api/v1/pws/offers/1/submit-order")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- exportCart ---

    @Test
    void exportCart_withToken_returnsCsv() throws Exception {
        when(offerService.exportCartCsv(1L)).thenReturn("SKU,Qty\nSKU-001,5\n");

        mockMvc.perform(get("/api/v1/pws/offers/cart/export")
                        .param("buyerCodeId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=cart_export.csv"))
                .andExpect(content().string("SKU,Qty\nSKU-001,5\n"));
    }

    // --- authorize ---

    @Test
    void authorize_forbiddenUser_returns403() throws Exception {
        when(buyerCodeService.isUserAuthorizedForBuyerCode(99L, 1L)).thenReturn(false);

        mockMvc.perform(get("/api/v1/pws/offers/cart")
                        .param("buyerCodeId", "1")
                        .param("userId", "99")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isForbidden());
    }
}
