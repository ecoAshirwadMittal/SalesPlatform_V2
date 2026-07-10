package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OfferListItem;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.security.PwsOwnershipGuard;
import com.ecoatm.salesplatform.service.CounterOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Buyer-facing counter-offer surface.
 *
 * <p><b>Security (review 2026-07-10, CR-3):</b> every endpoint is scoped to the
 * JWT principal; the caller must own the target buyer code / offer before any
 * read or write. Previously none of these had an ownership check, so any
 * authenticated user could read or mutate another company's counter-offer by
 * enumerating sequential offer ids (and {@code submit} creates a real order).
 */
@RestController
@RequestMapping("/api/v1/pws/counter-offers")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('Bidder','Administrator')")
public class CounterOfferController {

    private final CounterOfferService counterOfferService;
    private final PwsOwnershipGuard ownership;

    public CounterOfferController(CounterOfferService counterOfferService,
                                  PwsOwnershipGuard ownership) {
        this.counterOfferService = counterOfferService;
        this.ownership = ownership;
    }

    @GetMapping
    public ResponseEntity<List<OfferListItem>> listCounterOffers(
            @RequestParam Long buyerCodeId, Authentication auth) {
        if (!ownership.ownsBuyerCode((Long) auth.getPrincipal(), buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(counterOfferService.listCounterOffers(buyerCodeId));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponse> getCounterOfferDetail(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(counterOfferService.getCounterOfferDetail(offerId));
    }

    @PutMapping("/{offerId}/items/{itemId}/action")
    public ResponseEntity<OfferResponse> setBuyerItemAction(
            @PathVariable Long offerId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String buyerCounterStatus = body.get("buyerCounterStatus");
        return ResponseEntity.ok(counterOfferService.setBuyerItemAction(offerId, itemId, buyerCounterStatus));
    }

    @PostMapping("/{offerId}/accept-all")
    public ResponseEntity<OfferResponse> acceptAllCounters(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(counterOfferService.acceptAllCounters(offerId));
    }

    @PostMapping("/{offerId}/submit")
    public ResponseEntity<SubmitResponse> submitCounterResponse(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(counterOfferService.submitCounterResponse(offerId));
    }

    @PostMapping("/{offerId}/cancel")
    public ResponseEntity<SubmitResponse> cancelOffer(
            @PathVariable Long offerId, Authentication auth) {
        if (!ownership.ownsOffer((Long) auth.getPrincipal(), offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(counterOfferService.cancelOffer(offerId));
    }
}
