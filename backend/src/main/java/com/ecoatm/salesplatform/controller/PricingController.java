package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.service.PricingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
