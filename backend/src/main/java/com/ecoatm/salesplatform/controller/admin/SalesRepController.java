package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.SalesRepCreateRequest;
import com.ecoatm.salesplatform.dto.SalesRepResponse;
import com.ecoatm.salesplatform.dto.SalesRepUpdateRequest;
import com.ecoatm.salesplatform.service.SalesRepService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Admin REST surface for sales-representative write CRUD (gap-analysis 2.4,
 * sub-feature 1).
 *
 * <p>Dedicated {@code /api/v1/admin/sales-representatives/**} namespace —
 * deliberately NOT under {@code /api/v1/admin/buyers/**} (which grants
 * {@code Compliance} write access). Class-level {@code @PreAuthorize} gates all
 * endpoints to {@code Administrator} or {@code SalesOps}; the matching
 * {@code SecurityConfig} URL rule is the load-bearing fence and this annotation
 * is defense-in-depth against the matcher being rearranged.
 *
 * <p>Identity (owner on create, changer on update) is derived from the
 * JWT-authenticated principal — {@code JwtAuthenticationFilter} installs the
 * caller's user id as the principal — never from a request field. Domain-error
 * mapping (duplicate/offer-ref -&gt; 409, not-found -&gt; 404, validation -&gt;
 * 400) lives in {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/v1/admin/sales-representatives")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class SalesRepController {

    private final SalesRepService service;

    public SalesRepController(SalesRepService service) {
        this.service = service;
    }

    @GetMapping
    public List<SalesRepResponse> list() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<SalesRepResponse> create(@Valid @RequestBody SalesRepCreateRequest req,
                                                    Authentication auth) {
        SalesRepResponse created = service.create(req, callerId(auth));
        return ResponseEntity
                .created(URI.create("/api/v1/admin/sales-representatives/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public SalesRepResponse update(@PathVariable Long id,
                                   @Valid @RequestBody SalesRepUpdateRequest req,
                                   Authentication auth) {
        return service.update(id, req, callerId(auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * The verified JWT principal is the caller's user id ({@code Long}) — see
     * {@code JwtAuthenticationFilter}. Never trust an identity from the body.
     */
    private static Long callerId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
