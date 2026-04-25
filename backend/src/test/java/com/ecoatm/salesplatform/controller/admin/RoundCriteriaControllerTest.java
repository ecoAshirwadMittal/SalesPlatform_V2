package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.RoundCriteriaResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.admin.RoundCriteriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Authorization slice for {@link RoundCriteriaController}. Confirms the
 * Lane 4 "non-admin PUT returns 403" requirement and the
 * Administrator + SalesOps happy paths. Service is mocked — the service-
 * layer behavior is exhaustively covered in {@link com.ecoatm.salesplatform.service.admin.RoundCriteriaServiceTest}.
 */
@WebMvcTest(RoundCriteriaController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class RoundCriteriaControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private RoundCriteriaService service;

    private static final String VALID_BODY = """
            {
              "regularBuyerQualification": "All_Buyers",
              "regularBuyerInventoryOptions": "Full_Inventory",
              "stbAllowAllBuyersOverride": true
            }
            """;

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("PUT /{round} — Bidder is denied (403); service is never invoked")
    void put_bidder_returns403() throws Exception {
        mvc.perform(put("/api/v1/admin/round-criteria/2")
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());

        verify(service, never()).upsert(any(Integer.class), any());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /{round} — Administrator returns 200 with the upserted projection")
    void put_administrator_returns200() throws Exception {
        when(service.upsert(eq(2), any())).thenReturn(
                new RoundCriteriaResponse(2, "All_Buyers", "Full_Inventory", true));

        mvc.perform(put("/api/v1/admin/round-criteria/2")
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.round").value(2))
                .andExpect(jsonPath("$.regularBuyerQualification").value("All_Buyers"))
                .andExpect(jsonPath("$.regularBuyerInventoryOptions").value("Full_Inventory"))
                .andExpect(jsonPath("$.stbAllowAllBuyersOverride").value(true));
    }

    @Test
    @WithMockUser(roles = {"SalesOps"})
    @DisplayName("PUT /{round} — SalesOps is allowed (matches QA Mendix gate)")
    void put_salesOps_returns200() throws Exception {
        when(service.upsert(eq(2), any())).thenReturn(
                new RoundCriteriaResponse(2, "All_Buyers", "Full_Inventory", true));

        mvc.perform(put("/api/v1/admin/round-criteria/2")
                        .with(csrf())
                        .contentType("application/json")
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /{round} — Administrator returns 200 with the projection")
    void get_administrator_returns200() throws Exception {
        when(service.get(2)).thenReturn(
                new RoundCriteriaResponse(2, "Bid_Buyers_Only", "Inventory_With_Bids", false));

        mvc.perform(get("/api/v1/admin/round-criteria/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regularBuyerQualification").value("Bid_Buyers_Only"));
    }

    @Test
    @WithMockUser(roles = {"Bidder"})
    @DisplayName("GET /{round} — Bidder is denied (403)")
    void get_bidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/round-criteria/2"))
                .andExpect(status().isForbidden());

        verify(service, never()).get(any(Integer.class));
    }
}
