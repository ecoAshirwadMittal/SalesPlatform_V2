package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OrderDetailByDeviceResponse;
import com.ecoatm.salesplatform.dto.OrderDetailBySkuResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryTabCounts;
import com.ecoatm.salesplatform.security.PwsOwnershipGuard;
import com.ecoatm.salesplatform.service.OrderHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Buyer-facing order-history surface.
 *
 * <p><b>Security (review 2026-07-10, H-3/CR-3):</b> {@code userId} is taken from
 * the JWT, never a request param — the service scopes every list/count to the
 * authenticated user's own buyer codes, so a caller can no longer read another
 * user's order history by passing someone else's userId. The offer-detail
 * endpoints additionally verify offer ownership before returning line data.
 */
@RestController
@RequestMapping("/api/v1/pws/orders")
@PreAuthorize("hasAnyRole('Bidder','Administrator')")
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;
    private final PwsOwnershipGuard ownership;

    public OrderHistoryController(OrderHistoryService orderHistoryService,
                                  PwsOwnershipGuard ownership) {
        this.orderHistoryService = orderHistoryService;
        this.ownership = ownership;
    }

    @GetMapping
    public ResponseEntity<Page<OrderHistoryResponse>> listOrders(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        long userId = (Long) auth.getPrincipal();
        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders(tab, userId, buyerCodeId, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/counts")
    public ResponseEntity<OrderHistoryTabCounts> getTabCounts(
            @RequestParam(required = false) Long buyerCodeId,
            Authentication auth) {

        long userId = (Long) auth.getPrincipal();
        OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(userId, buyerCodeId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/{offerId}/details/by-sku")
    public ResponseEntity<List<OrderDetailBySkuResponse>> getDetailsBySku(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderHistoryService.getDetailsBySku(offerId));
    }

    @GetMapping("/{offerId}/details/by-device")
    public ResponseEntity<List<OrderDetailByDeviceResponse>> getDetailsByDevice(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderHistoryService.getDetailsByDevice(offerId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
