package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CaseLotResponse;
import com.ecoatm.salesplatform.dto.DeviceRequest;
import com.ecoatm.salesplatform.dto.DeviceResponse;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.service.PwsInventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final PwsInventoryService inventoryService;
    private final CaseLotRepository caseLotRepository;

    public InventoryController(PwsInventoryService inventoryService, CaseLotRepository caseLotRepository) {
        this.inventoryService = inventoryService;
        this.caseLotRepository = caseLotRepository;
    }

    /**
     * POST /api/v1/inventory/devices
     * Create a single new device.
     */
    @PostMapping("/devices")
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceRequest request) {
        try {
            DeviceResponse response = inventoryService.createDevice(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/v1/inventory/devices/bulk
     * Bulk-create or upsert devices from a sync payload.
     */
    @PostMapping("/devices/bulk")
    public ResponseEntity<List<DeviceResponse>> bulkCreate(@Valid @RequestBody List<DeviceRequest> requests) {
        List<DeviceResponse> responses = inventoryService.bulkCreateDevices(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * PUT /api/v1/inventory/devices/{id}
     * Update an existing device.
     */
    @PutMapping("/devices/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        try {
            DeviceResponse response = inventoryService.updateDevice(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/inventory/devices/{id}
     */
    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inventoryService.getDeviceById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/inventory/devices?sku=PWS000000100297
     */
    @GetMapping(value = "/devices", params = "sku")
    public ResponseEntity<?> getDeviceBySku(@RequestParam String sku) {
        try {
            return ResponseEntity.ok(inventoryService.getDeviceBySku(sku));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/inventory/devices
     * List all active devices.
     */
    @GetMapping("/devices")
    public ResponseEntity<List<DeviceResponse>> listActiveDevices() {
        return ResponseEntity.ok(inventoryService.listActiveDevices());
    }

    /**
     * GET /api/v1/inventory/case-lots
     * List all active case lots with device details.
     */
    @GetMapping("/case-lots")
    public ResponseEntity<List<CaseLotResponse>> listActiveCaseLots() {
        List<CaseLotResponse> responses = caseLotRepository.findByIsActiveTrueOrderByIdAsc()
                .stream()
                .map(CaseLotResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
