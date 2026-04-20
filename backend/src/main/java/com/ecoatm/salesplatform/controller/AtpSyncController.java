package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto;
import com.ecoatm.salesplatform.dto.SyncRunLogResponse;
import com.ecoatm.salesplatform.model.integration.SyncRunLog;
import com.ecoatm.salesplatform.repository.integration.SyncRunLogRepository;
import com.ecoatm.salesplatform.security.CurrentPrincipal;
import com.ecoatm.salesplatform.service.AtpSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class AtpSyncController {

    private static final Logger log = LoggerFactory.getLogger(AtpSyncController.class);
    private final AtpSyncService atpSyncService;
    private final SyncRunLogRepository syncRunLogRepository;

    public AtpSyncController(AtpSyncService atpSyncService, SyncRunLogRepository syncRunLogRepository) {
        this.atpSyncService = atpSyncService;
        this.syncRunLogRepository = syncRunLogRepository;
    }

    /**
     * POST /api/v1/inventory/sync/full
     * Mirrors legacy ACT_FullInventorySync_WithoutReport.
     */
    @PostMapping("/sync/full")
    public ResponseEntity<?> fullSync() {
        return runAndLog("FULL", () -> atpSyncService.fullInventorySync());
    }

    /**
     * POST /api/v1/inventory/sync/simulate
     * Simulate a sync by posting Deposco-formatted inventory data directly.
     */
    @PostMapping("/sync/simulate")
    public ResponseEntity<?> simulateSync(@RequestBody List<DeposcoInventoryDto.ItemInventory> items) {
        return runAndLog("SIMULATE", () -> atpSyncService.simulateSync(items));
    }

    /**
     * GET /api/v1/inventory/sync/logs
     * Recent sync runs for the PWS Data Center → Shipments & Sync admin page.
     */
    @GetMapping("/sync/logs")
    public ResponseEntity<Page<SyncRunLogResponse>> recentLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        Page<SyncRunLog> rows = syncRunLogRepository.findAllByOrderByStartTimeDesc(PageRequest.of(page, size));
        return ResponseEntity.ok(rows.map(SyncRunLogResponse::from));
    }

    private ResponseEntity<?> runAndLog(String syncType, java.util.function.Supplier<AtpSyncResult> action) {
        SyncRunLog entry = new SyncRunLog();
        entry.setSyncType(syncType);
        entry.setStatus("RUNNING");
        entry.setStartTime(LocalDateTime.now());
        entry.setTriggeredBy(CurrentPrincipal.displayName());
        entry = syncRunLogRepository.save(entry);

        try {
            log.info("ATP sync triggered type={} by={}", syncType, entry.getTriggeredBy());
            AtpSyncResult result = action.get();

            entry.setStatus("SUCCESS");
            entry.setEndTime(result.getSyncEndTime() != null ? result.getSyncEndTime() : LocalDateTime.now());
            entry.setTotalItemsReceived(result.getTotalItemsReceived());
            entry.setDevicesUpdated(result.getDevicesUpdated());
            entry.setDevicesMissing(result.getDevicesMissing());
            syncRunLogRepository.save(entry);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Sync failed type={}: {}", syncType, e.getMessage(), e);
            entry.setStatus("FAILED");
            entry.setEndTime(LocalDateTime.now());
            entry.setErrorMessage(truncate(e.getMessage(), 2000));
            syncRunLogRepository.save(entry);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Sync failed: " + e.getMessage()));
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
