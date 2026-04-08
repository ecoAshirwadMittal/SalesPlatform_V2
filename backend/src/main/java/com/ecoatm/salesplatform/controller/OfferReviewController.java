package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.OfferReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the PWSOffers review page.
 * Sales reps use this to review, counter, accept, decline, and finalize offers.
 */
@RestController
@RequestMapping("/api/v1/pws/offer-review")
@CrossOrigin(origins = "http://localhost:3000")
public class OfferReviewController {

    private final OfferReviewService reviewService;

    public OfferReviewController(OfferReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** Status summary cards — one per status tab. */
    @GetMapping("/summary")
    public ResponseEntity<List<OfferSummary>> getSummary() {
        return ResponseEntity.ok(reviewService.getStatusSummaries());
    }

    /** List offers, optionally filtered by status. */
    @GetMapping
    public ResponseEntity<List<OfferListItem>> listOffers(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(reviewService.listOffers(status));
    }

    /** Get offer detail with all items for the review page. */
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponse> getOfferDetail(@PathVariable Long offerId) {
        return ResponseEntity.ok(reviewService.getOfferDetail(offerId));
    }

    /** Set a single item's action (Accept/Counter/Decline/Finalize). */
    @PutMapping("/{offerId}/items/{itemId}/action")
    public ResponseEntity<OfferResponse> setItemAction(
            @PathVariable Long offerId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        String action = body.get("action");
        return ResponseEntity.ok(reviewService.setItemAction(offerId, itemId, action));
    }

    /** Update counter qty/price for a countered item. */
    @PutMapping("/{offerId}/items/{itemId}/counter")
    public ResponseEntity<OfferResponse> updateItemCounter(
            @PathVariable Long offerId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> body) {
        Integer counterQty = body.get("counterQty") != null
                ? ((Number) body.get("counterQty")).intValue() : null;
        BigDecimal counterPrice = body.get("counterPrice") != null
                ? new BigDecimal(body.get("counterPrice").toString()) : null;
        return ResponseEntity.ok(reviewService.updateItemCounter(offerId, itemId, counterQty, counterPrice));
    }

    /** Accept All items in the offer. */
    @PostMapping("/{offerId}/accept-all")
    public ResponseEntity<OfferResponse> acceptAll(@PathVariable Long offerId) {
        return ResponseEntity.ok(reviewService.acceptAll(offerId));
    }

    /** Decline All items in the offer. */
    @PostMapping("/{offerId}/decline-all")
    public ResponseEntity<OfferResponse> declineAll(@PathVariable Long offerId) {
        return ResponseEntity.ok(reviewService.declineAll(offerId));
    }

    /** Finalize All items in the offer. */
    @PostMapping("/{offerId}/finalize-all")
    public ResponseEntity<OfferResponse> finalizeAll(@PathVariable Long offerId) {
        return ResponseEntity.ok(reviewService.finalizeAll(offerId));
    }

    /** Complete Review — validates and transitions the offer. */
    @PostMapping("/{offerId}/complete-review")
    public ResponseEntity<SubmitResponse> completeReview(
            @PathVariable Long offerId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(reviewService.completeReview(offerId, userId));
    }
}
