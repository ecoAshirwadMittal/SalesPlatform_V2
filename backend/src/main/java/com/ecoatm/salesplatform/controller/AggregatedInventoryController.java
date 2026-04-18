package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse;
import com.ecoatm.salesplatform.dto.WeekOption;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

    public AggregatedInventoryController(AggregatedInventoryService service, WeekRepository weekRepository) {
        this.service = service;
        this.weekRepository = weekRepository;
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
}
