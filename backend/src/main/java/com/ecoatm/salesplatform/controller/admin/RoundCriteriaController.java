package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.RoundCriteriaResponse;
import com.ecoatm.salesplatform.dto.RoundCriteriaUpdateRequest;
import com.ecoatm.salesplatform.service.admin.RoundCriteriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lane 4 admin R2-criteria controller. Mirrors the QA POM
 * {@code ACC_RoundTwoCriteriaPage} via a deliberately narrow surface:
 * three settings (regular-buyer qualification, regular-buyer inventory
 * option, special-treatment allow-all-buyers override) per round (2 or 3).
 *
 * <p>Class-level {@code @PreAuthorize} grants both Administrator and
 * SalesOps. The matching filter-chain entry in
 * {@code SecurityConfig.filterChain} permits both roles for all methods
 * under {@code /api/v1/admin/round-criteria/**}; without that override the
 * default {@code /api/v1/admin/**} catch-all would block SalesOps before
 * method security ever runs.
 */
@RestController
@RequestMapping("/api/v1/admin/round-criteria")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class RoundCriteriaController {

    private final RoundCriteriaService service;

    public RoundCriteriaController(RoundCriteriaService service) {
        this.service = service;
    }

    /**
     * Returns the persisted criteria for the given round (2 or 3). 404 when
     * no row exists yet — the admin page renders defaults locally rather
     * than the API silently inventing them.
     */
    @GetMapping("/{round}")
    public RoundCriteriaResponse get(@PathVariable int round) {
        return service.get(round);
    }

    /**
     * Upserts the criteria row for the given round. Unknown enum strings
     * are rejected with 400 by the service before any DB write so a
     * malformed payload never lands a partial row.
     */
    @PutMapping("/{round}")
    public RoundCriteriaResponse update(
            @PathVariable int round,
            @RequestBody RoundCriteriaUpdateRequest req) {
        return service.upsert(round, req);
    }
}
