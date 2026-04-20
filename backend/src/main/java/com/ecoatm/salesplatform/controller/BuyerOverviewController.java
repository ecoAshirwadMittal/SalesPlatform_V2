package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerDetailResponse;
import com.ecoatm.salesplatform.dto.BuyerOverviewPageResponse;
import com.ecoatm.salesplatform.dto.BuyerUpsertRequest;
import com.ecoatm.salesplatform.dto.SalesRepSummary;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import com.ecoatm.salesplatform.service.BuyerEditService;
import com.ecoatm.salesplatform.service.BuyerOverviewService;
import com.ecoatm.salesplatform.service.snowflake.BuyerSnowflakeEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/buyers")
public class BuyerOverviewController {

    private final BuyerOverviewService buyerOverviewService;
    private final BuyerEditService buyerEditService;
    private final SalesRepresentativeRepository salesRepRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BuyerOverviewController(BuyerOverviewService buyerOverviewService,
                                    BuyerEditService buyerEditService,
                                    SalesRepresentativeRepository salesRepRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.buyerOverviewService = buyerOverviewService;
        this.buyerEditService = buyerEditService;
        this.salesRepRepository = salesRepRepository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public ResponseEntity<BuyerOverviewPageResponse> list(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String buyerCodes,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(
                buyerOverviewService.search(companyName, buyerCodes, status, page, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<?> create(@Valid @RequestBody BuyerUpsertRequest req,
                                     Authentication auth) {
        BuyerDetailResponse detail = buyerEditService.create(req, auth);
        URI location = URI.create("/api/v1/admin/buyers/" + detail.id());
        return ResponseEntity.created(location).body(detail);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id, Authentication auth) {
        try {
            BuyerDetailResponse detail = buyerEditService.get(id, auth);
            return ResponseEntity.ok(detail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                     @Valid @RequestBody BuyerUpsertRequest req,
                                     Authentication auth) {
        try {
            BuyerDetailResponse detail = buyerEditService.update(id, req, auth);
            return ResponseEntity.ok(detail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BuyerEditService.BuyerDisableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, Authentication auth) {
        try {
            BuyerDetailResponse detail = buyerEditService.toggleStatus(id, auth);
            return ResponseEntity.ok(detail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BuyerEditService.BuyerDisableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/snowflake-sync")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<Map<String, String>> syncAllToSnowflake() {
        eventPublisher.publishEvent(new BuyerSnowflakeEvent.AllBuyersSync());
        return ResponseEntity.accepted()
                .body(Map.of("message", "Snowflake sync initiated"));
    }

    @GetMapping("/sales-representatives")
    public ResponseEntity<List<SalesRepSummary>> listSalesReps() {
        List<SalesRepresentative> reps = salesRepRepository.findByActiveTrueOrderByFirstNameAscLastNameAsc();
        List<SalesRepSummary> summaries = reps.stream()
                .map(sr -> new SalesRepSummary(sr.getId(), sr.getFirstName(), sr.getLastName()))
                .toList();
        return ResponseEntity.ok(summaries);
    }
}
