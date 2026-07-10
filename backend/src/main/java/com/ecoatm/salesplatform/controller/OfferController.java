package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.CartItemRequest;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Buyer-facing PWS cart / offer / order surface.
 *
 * <p><b>Security (review 2026-07-10, CR-3):</b> every endpoint is scoped to the
 * authenticated caller — {@code userId} is taken from the JWT (never a request
 * parameter), and ownership of the target buyer code / offer is enforced before
 * any read or write. Previously {@code userId} was an optional request param and
 * the ownership check bypassed when it was absent, so any authenticated user
 * could read or mutate another company's cart and force a real Oracle order.
 */
@RestController
@RequestMapping("/api/v1/pws/offers")
@PreAuthorize("hasAnyRole('Bidder','Administrator')")
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);
    private final OfferService offerService;
    private final BuyerCodeService buyerCodeService;

    public OfferController(OfferService offerService, BuyerCodeService buyerCodeService) {
        this.offerService = offerService;
        this.buyerCodeService = buyerCodeService;
    }

    /** Get or create the active DRAFT cart for a buyer code. */
    @GetMapping("/cart")
    public ResponseEntity<OfferResponse> getCart(
            @RequestParam Long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.getOrCreateCart(buyerCodeId));
    }

    /** Add or update a single item in the cart. qty=0 removes the item. */
    @PutMapping("/cart/items")
    public ResponseEntity<OfferResponse> upsertItem(
            @RequestParam Long buyerCodeId,
            @Valid @RequestBody CartItemRequest request,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.upsertCartItem(buyerCodeId, request));
    }

    /** Remove a single item from the cart by SKU. */
    @DeleteMapping("/cart/items/{sku}")
    public ResponseEntity<OfferResponse> removeItem(
            @RequestParam Long buyerCodeId,
            @PathVariable String sku,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.removeItem(buyerCodeId, sku));
    }

    /** Reset the cart — clear all items, zero totals. */
    @DeleteMapping("/cart")
    public ResponseEntity<OfferResponse> resetCart(
            @RequestParam Long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.resetCart(buyerCodeId));
    }

    /**
     * Validate and submit the cart.
     * Returns one of 4 outcomes: alreadySubmitted, exceedingQty, salesReview, or submitted.
     */
    @PostMapping("/cart/submit")
    public ResponseEntity<SubmitResponse> submitCart(
            @RequestParam Long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(offerService.submitCart(buyerCodeId, userId));
        } catch (Exception e) {
            log.error("Cart submit failed for buyerCodeId={}", buyerCodeId, e);
            return ResponseEntity.ok(SubmitResponse.error(java.util.List.of("Submit failed: " + e.getMessage())));
        }
    }

    /**
     * Submit offer for sales review — called from "Almost Done" modal.
     * Sets the offer status to SALES_REVIEW so a sales rep can review below-list-price items.
     */
    @PostMapping("/{offerId}/submit-offer")
    public ResponseEntity<SubmitResponse> submitOffer(
            @PathVariable Long offerId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorizeOffer(userId, offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.submitOffer(offerId, userId));
    }

    /**
     * Submit order from a finalized offer — called after sales review is complete.
     * Clones Mendix ACT_Offer_SubmitOrder: creates Order, sends to Oracle, handles response.
     * Only processes accepted items (Accept, Counter+BuyerAccept, Finalize).
     */
    @PostMapping("/{offerId}/submit-order")
    public ResponseEntity<SubmitResponse> submitOrder(
            @PathVariable Long offerId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorizeOffer(userId, offerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.submitOrder(offerId, userId));
    }

    /** Export cart items as CSV download. */
    @GetMapping("/cart/export")
    public ResponseEntity<byte[]> exportCart(
            @RequestParam Long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String csv = offerService.exportCartCsv(buyerCodeId);
        byte[] bytes = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cart_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    /**
     * The authenticated user must own the buyer code. {@code userId} always comes
     * from the JWT, so there is no null-bypass any more (security review CR-3).
     */
    private boolean authorize(long userId, Long buyerCodeId) {
        return buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }

    /**
     * Ownership check for offerId-based endpoints: the caller must own the buyer
     * code the offer belongs to. A non-existent offer passes here so the service
     * returns its normal "not found" response rather than leaking existence via
     * 403-vs-200.
     */
    private boolean authorizeOffer(long userId, Long offerId) {
        Long buyerCodeId = offerService.getOfferBuyerCodeId(offerId);
        if (buyerCodeId == null) {
            return true;
        }
        return buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }
}
