package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto;
import com.ecoatm.salesplatform.service.AtpSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class AtpSyncController {

    private static final Logger log = LoggerFactory.getLogger(AtpSyncController.class);
    private final AtpSyncService atpSyncService;

    public AtpSyncController(AtpSyncService atpSyncService) {
        this.atpSyncService = atpSyncService;
    }

    /**
     * POST /api/v1/inventory/sync/full
     * Trigger a full ATP inventory sync from Deposco.
     * Mirrors: ACT_FullInventorySync_WithoutReport
     */
    @PostMapping("/sync/full")
    public ResponseEntity<?> fullSync() {
        try {
            log.info("Full ATP sync triggered via API");
            AtpSyncResult result = atpSyncService.fullInventorySync();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Full sync failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Sync failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/v1/inventory/sync/simulate
     * Simulate a sync by posting Deposco-formatted inventory data directly.
     * Useful for development/testing without a live Deposco connection.
     *
     * Request body: array of ItemInventory objects
     */
    @PostMapping("/sync/simulate")
    public ResponseEntity<?> simulateSync(@RequestBody List<DeposcoInventoryDto.ItemInventory> items) {
        try {
            log.info("Simulated ATP sync triggered with {} items", items.size());
            AtpSyncResult result = atpSyncService.simulateSync(items);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Simulated sync failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Sync failed: " + e.getMessage()));
        }
    }
}
