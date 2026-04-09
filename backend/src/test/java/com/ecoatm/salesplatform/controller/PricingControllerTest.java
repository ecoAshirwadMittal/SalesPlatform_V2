package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CsvUploadResult;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

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
                    isNull(), isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull()))
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
                    isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .param("sku", "PWS")
                            .param("category", "Cell Phone")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("passes price filter params to service")
        void passesPriceFilters() throws Exception {
            Page<PricingDeviceResponse> page = new PageImpl<>(
                    List.of(), PageRequest.of(0, 20), 0);
            when(pricingService.listPricingDevices(any(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(), isNull(),
                    eq(new BigDecimal("100.00")), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .param("currentListPrice", "100.00")
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
                    isNull(), isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/pricing/devices")
                            .param("page", "2")
                            .param("size", "50")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/pws/pricing/devices/{id}")
    class UpdateFuturePrices {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            mockMvc.perform(put("/api/v1/pws/pricing/devices/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"futureListPrice\":120.00,\"futureMinPrice\":95.00}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("updates future prices and returns updated device")
        void updatesAndReturns() throws Exception {
            PricingDeviceResponse dto = makeDto(1L, "PWS001");
            dto.setFutureListPrice(new BigDecimal("120.00"));
            dto.setFutureMinPrice(new BigDecimal("95.00"));
            when(pricingService.updateFuturePrices(eq(1L), any(), any())).thenReturn(dto);

            mockMvc.perform(put("/api/v1/pws/pricing/devices/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"futureListPrice\":120.00,\"futureMinPrice\":95.00}")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sku").value("PWS001"))
                    .andExpect(jsonPath("$.futureListPrice").value(120.00));
        }

        @Test
        @DisplayName("returns 400 when device not found")
        void returns400WhenNotFound() throws Exception {
            when(pricingService.updateFuturePrices(eq(999L), any(), any()))
                    .thenThrow(new IllegalArgumentException("Device not found: 999"));

            mockMvc.perform(put("/api/v1/pws/pricing/devices/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"futureListPrice\":120.00}")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/pws/pricing/devices/bulk")
    class BulkUpdateFuturePrices {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            mockMvc.perform(put("/api/v1/pws/pricing/devices/bulk")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[]"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("updates multiple devices and returns list")
        void updatesMultiple() throws Exception {
            PricingDeviceResponse d1 = makeDto(1L, "PWS001");
            d1.setFutureListPrice(new BigDecimal("50.00"));
            PricingDeviceResponse d2 = makeDto(2L, "PWS002");
            d2.setFutureListPrice(new BigDecimal("60.00"));
            when(pricingService.bulkUpdateFuturePrices(any())).thenReturn(List.of(d1, d2));

            mockMvc.perform(put("/api/v1/pws/pricing/devices/bulk")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[{\"deviceId\":1,\"futureListPrice\":50.00,\"futureMinPrice\":40.00},"
                                    + "{\"deviceId\":2,\"futureListPrice\":60.00,\"futureMinPrice\":45.00}]")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].futureListPrice").value(50.00))
                    .andExpect(jsonPath("$[1].futureListPrice").value(60.00));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/pws/pricing/devices/upload")
    class UploadPricingCsv {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "pricing.csv",
                    "text/csv", "sku,futureListPrice,futureMinPrice\n".getBytes());
            mockMvc.perform(multipart("/api/v1/pws/pricing/devices/upload").file(file))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("uploads CSV and returns result")
        void uploadsAndReturnsResult() throws Exception {
            CsvUploadResult result = new CsvUploadResult(2, 2, 0, List.of());
            when(pricingService.processPricingCsv(any())).thenReturn(result);

            MockMultipartFile file = new MockMultipartFile("file", "pricing.csv",
                    "text/csv", "sku,futureListPrice,futureMinPrice\nPWS001,120,95\nPWS002,60,45\n".getBytes());

            mockMvc.perform(multipart("/api/v1/pws/pricing/devices/upload")
                            .file(file)
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalRows").value(2))
                    .andExpect(jsonPath("$.updatedCount").value(2))
                    .andExpect(jsonPath("$.errorCount").value(0));
        }

        @Test
        @DisplayName("returns 400 for empty file")
        void returns400ForEmptyFile() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "pricing.csv",
                    "text/csv", new byte[0]);

            mockMvc.perform(multipart("/api/v1/pws/pricing/devices/upload")
                            .file(file)
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isBadRequest());
        }
    }
}
