package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OrderHistoryResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryTabCounts;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.OrderHistoryService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderHistoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OrderHistoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OrderHistoryService orderHistoryService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private OrderHistoryResponse makeDto(Long id, String orderNumber) {
        return new OrderHistoryResponse(
                id, orderNumber,
                LocalDateTime.of(2025, 1, 10, 8, 0),
                LocalDateTime.of(2025, 1, 12, 9, 0),
                "Ordered", null, null, 2, 5,
                new BigDecimal("200.00"), "Jane Doe", "TechCo",
                LocalDateTime.of(2025, 1, 12, 9, 0), "Order", id
        );
    }

    @Nested
    @DisplayName("GET /api/v1/pws/orders")
    class ListOrdersEndpoint {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            mockMvc.perform(get("/api/v1/pws/orders"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns paginated orders for authenticated user")
        void returnsPaginatedOrders() throws Exception {
            OrderHistoryResponse dto = makeDto(1L, "ORD-001");
            Page<OrderHistoryResponse> page = new PageImpl<>(
                    List.of(dto), PageRequest.of(0, 20), 1);
            when(orderHistoryService.listOrders(eq("all"), eq(1L), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/orders")
                            .param("tab", "all")
                            .param("userId", "1")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-001"))
                    .andExpect(jsonPath("$.content[0].totalPrice").value(200.00))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.size").value(20));
        }

        @Test
        @DisplayName("defaults tab to 'all' when not provided")
        void defaultsTabToAll() throws Exception {
            Page<OrderHistoryResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryService.listOrders(eq("all"), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/orders")
                            .param("userId", "1")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk());

            verify(orderHistoryService).listOrders(eq("all"), any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("passes tab and pagination params to service")
        void passesTabAndPagination() throws Exception {
            Page<OrderHistoryResponse> page = new PageImpl<>(List.of(), PageRequest.of(1, 50), 0);
            when(orderHistoryService.listOrders(eq("inProcess"), eq(2L), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/pws/orders")
                            .param("tab", "inProcess")
                            .param("userId", "2")
                            .param("page", "1")
                            .param("size", "50")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk());

            verify(orderHistoryService).listOrders(eq("inProcess"), eq(2L), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("returns 400 when service throws IllegalArgumentException for bad tab")
        void returns400ForUnknownTab() throws Exception {
            when(orderHistoryService.listOrders(eq("badTab"), any(), any(), any(Pageable.class)))
                    .thenThrow(new IllegalArgumentException("Unknown tab: badTab"));

            mockMvc.perform(get("/api/v1/pws/orders")
                            .param("tab", "badTab")
                            .param("userId", "1")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 200 with empty content array when no results")
        void returnsEmptyContentArray() throws Exception {
            Page<OrderHistoryResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(orderHistoryService.listOrders(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/pws/orders")
                            .param("userId", "1")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pws/orders/counts")
    class GetTabCountsEndpoint {

        @Test
        @DisplayName("returns 401 without auth token")
        void requiresAuth() throws Exception {
            mockMvc.perform(get("/api/v1/pws/orders/counts"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns tab counts for authenticated user")
        void returnsTabCounts() throws Exception {
            when(orderHistoryService.getTabCounts(eq(1L), any()))
                    .thenReturn(new OrderHistoryTabCounts(5L, 3L, 12L, 20L));

            mockMvc.perform(get("/api/v1/pws/orders/counts")
                            .param("userId", "1")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recent").value(5))
                    .andExpect(jsonPath("$.inProcess").value(3))
                    .andExpect(jsonPath("$.complete").value(12))
                    .andExpect(jsonPath("$.all").value(20));
        }

        @Test
        @DisplayName("returns all-zero counts when service returns zeros")
        void returnsZeroCounts() throws Exception {
            when(orderHistoryService.getTabCounts(eq(99L), any()))
                    .thenReturn(new OrderHistoryTabCounts(0L, 0L, 0L, 0L));

            mockMvc.perform(get("/api/v1/pws/orders/counts")
                            .param("userId", "99")
                            .header("Authorization", "Bearer " + validToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.all").value(0));
        }
    }
}
