package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.R2BuyerAssignmentResponse;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentResult;
import com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Admin recovery surface for sub-project 5 (R2 Buyer Assignment).
 *
 * <p>Mirrors the canonical 4C admin controller pattern in
 * {@link RecalcAdminController}: thin wrapper over a service whose
 * {@code recalculate(saId)} entry point owns the full state-flip lifecycle.
 *
 * <p>RBAC: Administrator + SalesOps (matches the sibling 4C
 * {@code /re-rank} + {@code /recalculate-target-price} matchers in
 * {@code SecurityConfig}; the design's {@code ROLE_ADMIN} shorthand is
 * spelled {@code Administrator} in this codebase).
 *
 * <p>Re-reads the SchedulingAuction <em>after</em> the service call so
 * {@code r2InitStartedAt}/{@code r2InitFinishedAt} reflect the persisted
 * timestamps written by {@code RecalcStatusUpdater} rather than wall-clock
 * times around the controller call.
 */
@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class R2BuyerAssignmentAdminController {

    private final R2BuyerAssignmentService service;
    private final SchedulingAuctionRepository saRepo;

    public R2BuyerAssignmentAdminController(R2BuyerAssignmentService service,
                                            SchedulingAuctionRepository saRepo) {
        this.service = service;
        this.saRepo = saRepo;
    }

    @PostMapping("/{id}/reassign-r2-buyers")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public R2BuyerAssignmentResponse reassign(@PathVariable("id") long saId) {
        R2BuyerAssignmentResult result = service.recalculate(saId);

        // Re-read the SA for the persisted started/finished_at + final status.
        // The service's @Transactional(REQUIRES_NEW) commits before returning,
        // so this read sees the latest row.
        SchedulingAuction sa = saRepo.findById(saId).orElseThrow(
            () -> new EntityNotFoundException("scheduling_auction", saId));

        // DTO field semantics: qualifiedCount = qualified-but-not-special.
        // Service result.qualifiedCount() is the UNION (qualified + special).
        int qualifiedNonSpecial = result.qualifiedCount() - result.specialTreatmentCount();

        return new R2BuyerAssignmentResponse(
            saId,
            sa.getR2InitStatus().name(),
            sa.getR2InitError(),
            sa.getR2InitStartedAt() != null
                ? sa.getR2InitStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR2InitFinishedAt() != null
                ? sa.getR2InitFinishedAt().atOffset(ZoneOffset.UTC) : null,
            qualifiedNonSpecial,
            result.specialTreatmentCount(),
            result.notQualifiedCount(),
            result.specialBidDataCount(),
            result.durationMs()
        );
    }
}
