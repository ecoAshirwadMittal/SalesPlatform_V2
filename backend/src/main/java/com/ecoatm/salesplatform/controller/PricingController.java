package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CsvUploadResult;
import com.ecoatm.salesplatform.dto.PriceHistoryResponse;
import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.dto.PricingUpdateRequest;
import com.ecoatm.salesplatform.service.PricingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pws/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/devices")
    public ResponseEntity<Page<PricingDeviceResponse>> listPricingDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String capacity,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String grade) {

        Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                PageRequest.of(page, size), sku, category, brand, model, carrier, capacity, color, grade);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> updateFuturePrices(
            @PathVariable Long id,
            @RequestBody PricingUpdateRequest request) {
        try {
            PricingDeviceResponse result = pricingService.updateFuturePrices(
                    id, request.getFutureListPrice(), request.getFutureMinPrice());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/devices/bulk")
    public ResponseEntity<?> bulkUpdateFuturePrices(@RequestBody List<PricingUpdateRequest> requests) {
        try {
            List<PricingDeviceResponse> results = pricingService.bulkUpdateFuturePrices(requests);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/devices/{id}/history")
    public ResponseEntity<List<PriceHistoryResponse>> getDevicePriceHistory(@PathVariable Long id) {
        List<PriceHistoryResponse> history = pricingService.getPriceHistory(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping(value = "/devices/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPricingCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            CsvUploadResult result = pricingService.processPricingCsv(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }
}
