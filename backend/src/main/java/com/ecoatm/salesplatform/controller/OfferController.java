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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pws/offers")
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
            @RequestParam(required = false) Long userId) {
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.getOrCreateCart(buyerCodeId));
    }

    /** Add or update a single item in the cart. qty=0 removes the item. */
    @PutMapping("/cart/items")
    public ResponseEntity<OfferResponse> upsertItem(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody CartItemRequest request) {
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.upsertCartItem(buyerCodeId, request));
    }

    /** Remove a single item from the cart by SKU. */
    @DeleteMapping("/cart/items/{sku}")
    public ResponseEntity<OfferResponse> removeItem(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) Long userId,
            @PathVariable String sku) {
        if (!authorize(userId, buyerCodeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerService.removeItem(buyerCodeId, sku));
    }

    /** Reset the cart — clear all items, zero totals. */
    @DeleteMapping("/cart")
    public ResponseEntity<OfferResponse> resetCart(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) Long userId) {
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
            @RequestParam(required = false) Long userId) {
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
            @RequestParam(required = false) Long userId) {
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
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(offerService.submitOrder(offerId, userId));
    }

    /** Export cart items as CSV download. */
    @GetMapping("/cart/export")
    public ResponseEntity<byte[]> exportCart(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) Long userId) {
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
     * Authorization check: validate user owns the buyer code.
     * When userId is null (no JWT yet), skip the check for backwards compatibility.
     * Once JWT auth is implemented, userId will come from the token and this will be mandatory.
     */
    private boolean authorize(Long userId, Long buyerCodeId) {
        if (userId == null) return true;
        return buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }
}
