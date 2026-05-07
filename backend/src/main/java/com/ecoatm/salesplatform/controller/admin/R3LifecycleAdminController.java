package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.R3InitResponse;
import com.ecoatm.salesplatform.dto.admin.R3PreProcessResponse;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.exception.R3LifecycleValidationException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.r3init.R3InitResult;
import com.ecoatm.salesplatform.service.auctions.r3init.R3InitService;
import com.ecoatm.salesplatform.service.auctions.r3init.R3PreProcessResult;
import com.ecoatm.salesplatform.service.auctions.r3init.R3PreProcessService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

/**
 * Admin recovery surface for sub-project 6 (R3 Pre-process + Init).
 *
 * <p>Two endpoints share one controller class — the pre-process and init
 * are sibling phases of the same lifecycle and the combined class is smaller
 * than the split would be.
 *
 * <p>Both methods catch {@code IllegalArgumentException} (round mismatch) and
 * {@code IllegalStateException} (predecessor-guard failure) from the services
 * and re-throw them as {@link R3LifecycleValidationException}, which the
 * {@code GlobalExceptionHandler} maps to HTTP 422. Other call sites that throw
 * those standard exception types continue to return 400/409 as before.
 *
 * <p>RBAC: Administrator only. Both SecurityConfig matchers and
 * {@code @PreAuthorize} must agree for the check to pass in integration tests.
 */
@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class R3LifecycleAdminController {

    private final R3PreProcessService preProcessService;
    private final R3InitService       initService;
    private final SchedulingAuctionRepository saRepo;

    public R3LifecycleAdminController(R3PreProcessService preProcessService,
                                      R3InitService initService,
                                      SchedulingAuctionRepository saRepo) {
        this.preProcessService = preProcessService;
        this.initService = initService;
        this.saRepo = saRepo;
    }

    /**
     * Run (or re-run) R3 pre-process for the given R3 scheduling auction.
     *
     * <p>Resolves the sibling R2 SA internally. Re-reads the SA after the
     * service call so timestamps reflect the persisted values written by
     * {@code RecalcStatusUpdater}.
     *
     * @param r3SaId  the id of the round-3 scheduling auction
     * @return        response with status + counts from the run
     * @throws EntityNotFoundException         SA id unknown (→ 404)
     * @throws R3LifecycleValidationException  round mismatch or no R2 sibling (→ 422)
     */
    @PostMapping("/{id}/preprocess-r3")
    @PreAuthorize("hasRole('Administrator')")
    public R3PreProcessResponse preprocess(@PathVariable("id") long r3SaId) {
        R3PreProcessResult result;
        try {
            result = preProcessService.recalculate(r3SaId);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new R3LifecycleValidationException(ex.getMessage(), ex);
        }

        // Re-read after REQUIRES_NEW commit so timestamps are populated.
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r3SaId));

        // DTO qualifiedCount = qualified-but-not-special.
        // Service result.qualifiedCount() is the UNION (qualified + special).
        int qualifiedNonSpecial = result.qualifiedCount() - result.specialTreatmentCount();

        return new R3PreProcessResponse(
            r3SaId,
            sa.getR3PreprocessStatus().name(),
            sa.getR3PreprocessError(),
            sa.getR3PreprocessStartedAt() != null
                ? sa.getR3PreprocessStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR3PreprocessFinishedAt() != null
                ? sa.getR3PreprocessFinishedAt().atOffset(ZoneOffset.UTC) : null,
            qualifiedNonSpecial,
            result.specialTreatmentCount(),
            result.notQualifiedCount(),
            result.reportRowCount(),
            result.deletedBidsCount(),
            result.durationMs()
        );
    }

    /**
     * Run (or re-run) R3 init for the given R3 scheduling auction.
     *
     * <p>Refused if {@code r3_preprocess_status != SUCCESS} on the same SA row.
     *
     * @param r3SaId  the id of the round-3 scheduling auction
     * @return        response with status + duration
     * @throws EntityNotFoundException         SA id unknown (→ 404)
     * @throws R3LifecycleValidationException  predecessor guard or round mismatch (→ 422)
     */
    @PostMapping("/{id}/reinit-r3")
    @PreAuthorize("hasRole('Administrator')")
    public R3InitResponse reinit(@PathVariable("id") long r3SaId) {
        R3InitResult result;
        try {
            result = initService.recalculate(r3SaId);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new R3LifecycleValidationException(ex.getMessage(), ex);
        }

        // Re-read after REQUIRES_NEW commit so timestamps are populated.
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r3SaId));

        return new R3InitResponse(
            r3SaId,
            sa.getR3InitStatus().name(),
            sa.getR3InitError(),
            sa.getR3InitStartedAt() != null
                ? sa.getR3InitStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR3InitFinishedAt() != null
                ? sa.getR3InitFinishedAt().atOffset(ZoneOffset.UTC) : null,
            result.durationMs()
        );
    }
}
