package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OfferListItem;
import com.ecoatm.salesplatform.dto.OfferResponse;
import com.ecoatm.salesplatform.dto.SubmitResponse;
import com.ecoatm.salesplatform.service.CounterOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pws/counter-offers")
@CrossOrigin(origins = "http://localhost:3000")
public class CounterOfferController {

    private final CounterOfferService counterOfferService;

    public CounterOfferController(CounterOfferService counterOfferService) {
        this.counterOfferService = counterOfferService;
    }

    @GetMapping
    public ResponseEntity<List<OfferListItem>> listCounterOffers(
            @RequestParam Long buyerCodeId) {
        return ResponseEntity.ok(counterOfferService.listCounterOffers(buyerCodeId));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponse> getCounterOfferDetail(
            @PathVariable Long offerId) {
        return ResponseEntity.ok(counterOfferService.getCounterOfferDetail(offerId));
    }

    @PutMapping("/{offerId}/items/{itemId}/action")
    public ResponseEntity<OfferResponse> setBuyerItemAction(
            @PathVariable Long offerId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        String buyerCounterStatus = body.get("buyerCounterStatus");
        return ResponseEntity.ok(counterOfferService.setBuyerItemAction(offerId, itemId, buyerCounterStatus));
    }

    @PostMapping("/{offerId}/accept-all")
    public ResponseEntity<OfferResponse> acceptAllCounters(
            @PathVariable Long offerId) {
        return ResponseEntity.ok(counterOfferService.acceptAllCounters(offerId));
    }

    @PostMapping("/{offerId}/submit")
    public ResponseEntity<SubmitResponse> submitCounterResponse(
            @PathVariable Long offerId) {
        return ResponseEntity.ok(counterOfferService.submitCounterResponse(offerId));
    }

    @PostMapping("/{offerId}/cancel")
    public ResponseEntity<SubmitResponse> cancelOffer(
            @PathVariable Long offerId) {
        return ResponseEntity.ok(counterOfferService.cancelOffer(offerId));
    }
}
