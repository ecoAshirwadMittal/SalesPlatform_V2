package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.PODetailListResponse;
import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.dto.PurchaseOrderListResponse;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PODetailService;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.POExcelBuilder;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PurchaseOrderService;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PurchaseOrderValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller slice IT for {@link PurchaseOrderController}.
 *
 * <p>Mirrors 4A's {@code ReserveBidControllerIT} pattern (slice via
 * {@link WebMvcTest} with {@code @MockBean} services + the real
 * {@link SecurityConfig} imported) rather than the {@code @SpringBootTest}
 * scaffold in the task plan: the slice variant gives identical security-wiring
 * coverage at a fraction of the boot cost, and matches the load-bearing
 * pattern already used by every controller test in this repo.
 *
 * <p>The upload test exercises the real Excel-MIME multipart codepath against
 * a mocked {@link PODetailService} so it never depends on seeded PO rows in
 * H2 — sufficient because Task 13 is about <em>controller wiring + RBAC</em>,
 * while end-to-end DB seeding lives in {@code PODetailServiceIT}.
 */
@WebMvcTest(PurchaseOrderController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class PurchaseOrderControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean PurchaseOrderService poService;
    @MockBean PODetailService detailService;
    @MockBean POExcelBuilder excelBuilder;

    @Test
    @WithMockUser(roles = "Administrator")
    void administratorCanList() throws Exception {
        when(poService.list(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PurchaseOrderListResponse(List.of(), 0L, 0, 20));

        mvc.perform(get("/api/v1/admin/purchase-orders").param("page", "0"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @WithMockUser(roles = "SalesOps")
    void salesOpsCanList() throws Exception {
        when(poService.list(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PurchaseOrderListResponse(List.of(), 0L, 0, 20));

        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    void bidderForbidden() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedReturns401() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void invalidWeekRangeReturns400() throws Exception {
        when(poService.create(any())).thenThrow(new PurchaseOrderValidationException(
                "INVALID_WEEK_RANGE", "week_from id not found: 999999", List.of()));

        String body = om.writeValueAsString(Map.of("weekFromId", 999_999, "weekToId", 999_999));
        mvc.perform(post("/api/v1/admin/purchase-orders")
                   .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("INVALID_WEEK_RANGE"));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void uploadWrongMediaType415() throws Exception {
        mvc.perform(post("/api/v1/admin/purchase-orders/1/details/upload")
                   .contentType(MediaType.APPLICATION_JSON).content("{}"))
           .andExpect(status().is(415));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void uploadValidExcelReturns200() throws Exception {
        when(detailService.upload(eq(1L), any()))
                .thenReturn(new PODetailUploadResult(20, 0, 0, List.of()));

        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/fixtures/po-upload-sample.xlsx"));
        MockMultipartFile file = new MockMultipartFile("file",
                "po-upload-sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);

        mvc.perform(multipart("/api/v1/admin/purchase-orders/1/details/upload").file(file))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.createdCount").isNumber());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void detailsListReturns200() throws Exception {
        when(detailService.list(anyLong(), any(Pageable.class)))
                .thenReturn(new PODetailListResponse(List.of(), 0L, 0, 20));

        mvc.perform(get("/api/v1/admin/purchase-orders/1/details"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void getByIdReturns200() throws Exception {
        when(poService.findById(1L)).thenReturn(new PurchaseOrderRow(
                1L, 100L, "2026-W01", 101L, "2026-W04", "2026-W01 - 2026-W04",
                PurchaseOrderLifecycleState.ACTIVE, 0, Instant.now(), Instant.now(), null));

        mvc.perform(get("/api/v1/admin/purchase-orders/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(1));
    }
}
