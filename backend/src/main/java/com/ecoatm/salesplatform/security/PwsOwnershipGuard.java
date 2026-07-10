package com.ecoatm.salesplatform.security;

import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.service.OfferService;
import org.springframework.stereotype.Component;

/**
 * Shared PWS ownership checks for buyer-facing controllers.
 *
 * <p>The authenticated user (JWT principal) must own the target buyer code /
 * offer before any read or write — otherwise any authenticated account could
 * reach another company's cart, counter-offer, or order by enumerating
 * sequential ids (security review 2026-07-10, CR-3). Centralising the check here
 * keeps every buyer-facing controller consistent and gives one place to audit.
 */
@Component
public class PwsOwnershipGuard {

    private final BuyerCodeService buyerCodeService;
    private final OfferService offerService;

    public PwsOwnershipGuard(BuyerCodeService buyerCodeService, OfferService offerService) {
        this.buyerCodeService = buyerCodeService;
        this.offerService = offerService;
    }

    /** True if the user may act on the given buyer code. */
    public boolean ownsBuyerCode(long userId, Long buyerCodeId) {
        return buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }

    /**
     * True if the user owns the buyer code the offer belongs to. A non-existent
     * offer returns {@code true} so the service can return its normal "not found"
     * response rather than leaking existence via 403-vs-200.
     */
    public boolean ownsOffer(long userId, Long offerId) {
        Long buyerCodeId = offerService.getOfferBuyerCodeId(offerId);
        return buyerCodeId == null || buyerCodeService.isUserAuthorizedForBuyerCode(userId, buyerCodeId);
    }
}
