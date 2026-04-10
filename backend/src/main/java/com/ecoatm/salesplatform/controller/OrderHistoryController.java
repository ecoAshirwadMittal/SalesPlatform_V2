package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OrderDetailByDeviceResponse;
import com.ecoatm.salesplatform.dto.OrderDetailBySkuResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryResponse;
import com.ecoatm.salesplatform.dto.OrderHistoryTabCounts;
import com.ecoatm.salesplatform.service.OrderHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pws/orders")
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    public OrderHistoryController(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderHistoryResponse>> listOrders(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders(tab, userId, buyerCodeId, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/counts")
    public ResponseEntity<OrderHistoryTabCounts> getTabCounts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long buyerCodeId) {

        OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(userId, buyerCodeId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/{offerId}/details/by-sku")
    public ResponseEntity<List<OrderDetailBySkuResponse>> getDetailsBySku(
            @PathVariable Long offerId) {
        List<OrderDetailBySkuResponse> result = orderHistoryService.getDetailsBySku(offerId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{offerId}/details/by-device")
    public ResponseEntity<List<OrderDetailByDeviceResponse>> getDetailsByDevice(
            @PathVariable Long offerId) {
        List<OrderDetailByDeviceResponse> result = orderHistoryService.getDetailsByDevice(offerId);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
