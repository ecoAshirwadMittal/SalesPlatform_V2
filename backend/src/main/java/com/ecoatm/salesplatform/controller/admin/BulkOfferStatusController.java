package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import com.ecoatm.salesplatform.dto.ChangeOfferStatusResult;
import com.ecoatm.salesplatform.service.pws.BulkOfferStatusService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Administrator-only bulk offer-status change tool (gap-analysis 2.3
 * sub-feature 3) — a faithful port of the Mendix {@code ACT_ChangeOfferStatus}
 * admin action, used to correct offer status in bulk after a bad Oracle sync.
 *
 * <p><b>Authz (defense-in-depth):</b> the {@code /api/v1/admin/pws/**}
 * {@code SecurityConfig} matcher (Administrator-only) is the load-bearing fence;
 * this class-level {@code @PreAuthorize} re-asserts it at the method-invocation
 * layer so a future matcher edit cannot silently widen access. SalesOps and
 * Bidder are excluded (403).
 *
 * <p><b>Identity:</b> the audit caller is derived from the verified JWT
 * ({@link Authentication}) — never from a request field.
 */
@RestController
@RequestMapping("/api/v1/admin/pws")
@PreAuthorize("hasRole('Administrator')")
public class BulkOfferStatusController {

    private final BulkOfferStatusService service;

    public BulkOfferStatusController(BulkOfferStatusService service) {
        this.service = service;
    }

    @PostMapping("/bulk-status")
    public ChangeOfferStatusResult bulkChangeStatus(@Valid @RequestBody ChangeOfferStatusRequest req,
                                                    Authentication auth) {
        return service.changeStatus(req, actor(auth));
    }

    /**
     * Resolve a stable audit stamp from the verified JWT principal.
     * {@code JwtAuthenticationFilter} installs {@code principal=userId (Long)} and
     * {@code credentials=email (String)}; the email is the friendliest audit
     * stamp, falling back to the numeric principal. Never sourced from the body.
     */
    private static String actor(Authentication auth) {
        Object credentials = auth.getCredentials();
        if (credentials instanceof String email && !email.isBlank()) {
            return email;
        }
        return String.valueOf(auth.getPrincipal());
    }
}
