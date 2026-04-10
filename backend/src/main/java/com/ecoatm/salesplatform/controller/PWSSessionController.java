package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Implements the legacy ACT_Open_PWS_Order session activation flow.
 * Binds a buyer code to the current session and checks for pending counter-offers.
 */
@RestController
@RequestMapping("/api/v1/pws/session")
@RequiredArgsConstructor
public class PWSSessionController {

    private static final Logger log = LoggerFactory.getLogger(PWSSessionController.class);

    private final OfferRepository offerRepository;
    private final BuyerCodeService buyerCodeService;
    private final BuyerCodeLookupService buyerCodeLookup;

    /**
     * Activate a PWS session for the given buyer code.
     * Mirrors legacy ACT_Open_PWS_Order → SUB_NavigateToCounterOffers:
     *   1. Look up the buyer code ID from the code string
     *   2. Verify the authenticated user is authorized for this buyer code
     *   3. Count offers in Buyer_Acceptance status for that buyer code
     *   4. Return the count so the frontend can route accordingly
     */
    @PostMapping("/activate")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> activate(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, String> request) {
        String buyerCode = request.get("buyerCode");
        if (buyerCode == null || buyerCode.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "buyerCode is required"));
        }

        // Look up buyer code ID from the code string
        Long buyerCodeId = buyerCodeLookup.findActiveIdByCode(buyerCode);

        if (buyerCodeId == null) {
            log.warn("Session activate: buyer code not found: {}", buyerCode);
            return ResponseEntity.ok(Map.of("pendingCounterOffers", 0, "buyerCodeId", 0));
        }

        // Verify the authenticated user is authorized for this buyer code
        if (!buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId)) {
            log.warn("Session activate: user {} not authorized for buyerCode {}", userId, buyerCode);
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Not authorized for this buyer code"));
        }

        // Count Buyer_Acceptance offers for this buyer code (SUB_NavigateToCounterOffers logic)
        List<?> counterOffers = offerRepository
                .findByStatusAndBuyerCodeIdOrderByUpdatedDateDesc("Buyer_Acceptance", buyerCodeId);
        int pendingCount = counterOffers.size();

        log.info("Session activate: buyerCode={}, buyerCodeId={}, pendingCounterOffers={}",
                buyerCode, buyerCodeId, pendingCount);

        return ResponseEntity.ok(Map.of(
                "pendingCounterOffers", pendingCount,
                "buyerCodeId", buyerCodeId
        ));
    }
}
