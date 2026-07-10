package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.MasterDataItemDto;
import com.ecoatm.salesplatform.dto.MasterDataItemRequest;
import com.ecoatm.salesplatform.service.AdminMasterDataService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin CRUD over MDM lookup tables for the PWS Data Center → Master Data
 * tabbed screen. Mounted under /admin so SecurityConfig already requires
 * the Administrator role.
 */
@RestController
@RequestMapping("/api/v1/admin/master-data")
// L-6 (security review 2026-07-10): defense-in-depth. The SecurityConfig
// "/api/v1/admin/**" matcher already requires Administrator; this class-level
// @PreAuthorize re-asserts it at the method layer so the MDM lookup CRUD can
// never be exposed to a lesser role by a future matcher change.
@PreAuthorize("hasRole('Administrator')")
public class AdminMasterDataController {

    private final AdminMasterDataService service;

    public AdminMasterDataController(AdminMasterDataService service) {
        this.service = service;
    }

    @GetMapping("/types")
    public ResponseEntity<?> listSupportedTypes() {
        return ResponseEntity.ok(Map.of("types", service.supportedTypes()));
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> list(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Page<MasterDataItemDto> result = service.list(type, page, size);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> create(
            @PathVariable String type,
            @RequestBody MasterDataItemRequest request) {
        try {
            return ResponseEntity.ok(service.create(type, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{type}/{id}")
    public ResponseEntity<?> update(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody MasterDataItemRequest request) {
        try {
            return ResponseEntity.ok(service.update(type, id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<?> softDelete(
            @PathVariable String type,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.softDelete(type, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }
}
