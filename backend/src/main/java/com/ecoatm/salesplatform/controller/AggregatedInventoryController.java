package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest;
import com.ecoatm.salesplatform.dto.SyncStatusResponse;
import com.ecoatm.salesplatform.dto.SyncTriggerResponse;
import com.ecoatm.salesplatform.dto.WeekOption;
import com.ecoatm.salesplatform.event.AggInventorySyncRequestedEvent;
import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryExcelExporter;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventorySnowflakeSyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class AggregatedInventoryController {

    private final AggregatedInventoryService service;
    private final WeekRepository weekRepository;
    private final AggregatedInventoryExcelExporter excelExporter;
    private final ApplicationEventPublisher eventPublisher;
    private final SnowflakeSyncLogRepository snowflakeSyncLogRepository;
    private final boolean snowflakeEnabled;

    public AggregatedInventoryController(AggregatedInventoryService service,
                                         WeekRepository weekRepository,
                                         AggregatedInventoryExcelExporter excelExporter,
                                         ApplicationEventPublisher eventPublisher,
                                         SnowflakeSyncLogRepository snowflakeSyncLogRepository,
                                         @Value("${snowflake.enabled:false}") boolean snowflakeEnabled) {
        this.service = service;
        this.weekRepository = weekRepository;
        this.excelExporter = excelExporter;
        this.eventPublisher = eventPublisher;
        this.snowflakeSyncLogRepository = snowflakeSyncLogRepository;
        this.snowflakeEnabled = snowflakeEnabled;
    }

    @GetMapping("/weeks")
    public ResponseEntity<List<WeekOption>> listWeeks() {
        List<Week> weeks = weekRepository.findAllByOrderByWeekStartDateTimeDesc();
        List<WeekOption> options = weeks.stream()
                .map(w -> new WeekOption(w.getId(), w.getWeekDisplay(),
                        w.getWeekStartDateTime(), w.getWeekEndDateTime()))
                .toList();
        return ResponseEntity.ok(options);
    }

    @GetMapping
    public ResponseEntity<AggregatedInventoryPageResponse> list(
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String grades,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.search(weekId, productId, grades, brand, model, modelName, carrier, page, pageSize));
    }

    @GetMapping("/totals")
    public ResponseEntity<AggregatedInventoryTotalsResponse> totals(@RequestParam(required = false) Long weekId) {
        return ResponseEntity.ok(service.getTotals(weekId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<AggregatedInventoryRow> update(
            @PathVariable Long id,
            @RequestBody AggregatedInventoryUpdateRequest req) {
        AggregatedInventory e = service.updateRow(id, req);
        AggregatedInventoryRow row = new AggregatedInventoryRow(
                e.getId(), e.getEcoid2(), e.getMergedGrade(),
                e.getBrand(), e.getModel(), e.getName(), e.getCarrier(),
                e.getDwTotalQuantity(), e.getDwAvgTargetPrice(),
                e.getTotalQuantity(), e.getAvgTargetPrice(),
                e.isDatawipe());
        return ResponseEntity.ok(row);
    }

    @GetMapping(value = "/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String grades,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String carrier) throws java.io.IOException {

        var all = service.search(weekId, productId, grades, brand, model, modelName, carrier, 0, Integer.MAX_VALUE);
        var baos = new java.io.ByteArrayOutputStream();
        excelExporter.write(all.content(), baos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"aggregated-inventory.xlsx\"")
                .body(baos.toByteArray());
    }

    @PostMapping("/weeks/{weekId}/sync")
    public ResponseEntity<SyncTriggerResponse> triggerSync(
            @PathVariable long weekId,
            Authentication authentication) {
        if (!snowflakeEnabled) {
            return ResponseEntity.accepted().body(
                    new SyncTriggerResponse("SKIPPED_DISABLED",
                            AggregatedInventorySnowflakeSyncService.SOURCE));
        }
        String triggeredBy = authentication != null ? authentication.getName() : "unknown";
        eventPublisher.publishEvent(new AggInventorySyncRequestedEvent(weekId, triggeredBy));
        return ResponseEntity.accepted().body(
                new SyncTriggerResponse("ACCEPTED",
                        AggregatedInventorySnowflakeSyncService.SOURCE));
    }

    @GetMapping("/weeks/{weekId}/sync/status")
    public ResponseEntity<SyncStatusResponse> syncStatus(@PathVariable long weekId) {
        return snowflakeSyncLogRepository
                .findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                        AggregatedInventorySnowflakeSyncService.SYNC_TYPE,
                        String.valueOf(weekId))
                .map(SyncStatusResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(SyncStatusResponse.none()));
    }
}
