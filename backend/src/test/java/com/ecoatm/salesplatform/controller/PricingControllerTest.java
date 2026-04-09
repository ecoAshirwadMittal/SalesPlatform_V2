package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.PricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class PricingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private PricingService pricingService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private PricingDeviceResponse makeDto(Long id, String sku) {
        PricingDeviceResponse dto = new PricingDeviceResponse();
        dto.setId(id);
        dto.setSku(sku);
        dto.setCurrentListPrice(new BigDecimal("100.00"));
        dto.setCurrentMinPrice(new BigDecimal("80.00"));
        dto.setBrandName("Apple");
        dto.setCategoryName("Cell Phone");
        dto.setModelName("iPhone 15");
        return dto;
    }

    @Nested
    @DisplayName("GET /api/v1/pws/pricing/devices")
    class ListPricingDevices {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            mockMvc.perform(get("/api/v1/pws/pricing/devices"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns paginated pricing data")
        void returnsPaginatedData() throws Exception {
            PricingDeviceResponse dto = makeDto(1L, "PWS001");
            Page<PricingDeviceResponse> page = new PageImpl<>(
                    List.of(dto), PageRequest.of(0, 20), 1);
            when(pricingService.listPricingDevices(any(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].sku").value("PWS001"))
                    .andExpect(jsonPath("$.content[0].currentListPrice").value(100.00))
                    .andExpect(jsonPath("$.content[0].brandName").value("Apple"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.size").value(20));
        }

        @Test
        @DisplayName("passes filter params to service")
        void passesFilters() throws Exception {
            Page<PricingDeviceResponse> page = new PageImpl<>(
                    List.of(), PageRequest.of(0, 20), 0);
            when(pricingService.listPricingDevices(any(), eq("PWS"), eq("Cell Phone"),
                    isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .param("sku", "PWS")
                            .param("category", "Cell Phone")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("respects page and size params")
        void respectsPagination() throws Exception {
            Page<PricingDeviceResponse> page = new PageImpl<>(
                    List.of(), PageRequest.of(2, 50), 0);
            when(pricingService.listPricingDevices(any(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .param("page", "2")
                            .param("size", "50")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk());
        }
    }
}
