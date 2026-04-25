package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerInventoryOption;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerQualification;

/**
 * Response payload for the simplified Lane 4 admin R2-criteria surface
 * ({@code GET /api/v1/admin/round-criteria/{round}}).
 *
 * <p>Exposes only the three fields the QA POM
 * {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings} drives:
 * regular-buyer qualification, regular-buyer inventory option, and the
 * special-treatment-buyer all-buyers override flag. Other columns on
 * {@link BidRoundSelectionFilter} (target_percent, merged_grade1, etc.)
 * are intentionally omitted — the broader admin surface lives at
 * {@code /api/v1/admin/auctions/round-filters/{round}} (Phase D).
 *
 * <p>Friendly enum names mirror what the QA test API expects:
 * <ul>
 *   <li>{@code Bid_Buyers_Only} ↔ DB {@code Only_Qualified}</li>
 *   <li>{@code All_Buyers}      ↔ DB {@code All_Buyers}</li>
 *   <li>{@code Inventory_With_Bids} ↔ DB {@code InventoryRound1QualifiedBids}</li>
 *   <li>{@code Full_Inventory}      ↔ DB {@code ShowAllInventory}</li>
 * </ul>
 */
public record RoundCriteriaResponse(
        Integer round,
        String regularBuyerQualification,
        String regularBuyerInventoryOptions,
        Boolean stbAllowAllBuyersOverride) {

    /** "All_Buyers" + "Bid_Buyers_Only" — the QA-facing canonical strings. */
    public static final String QUALIFICATION_ALL_BUYERS = "All_Buyers";
    public static final String QUALIFICATION_BID_BUYERS_ONLY = "Bid_Buyers_Only";

    /** "Full_Inventory" + "Inventory_With_Bids" — the QA-facing canonical strings. */
    public static final String INVENTORY_FULL = "Full_Inventory";
    public static final String INVENTORY_WITH_BIDS = "Inventory_With_Bids";

    /**
     * Project a persisted {@link BidRoundSelectionFilter} into the
     * Lane-4 simplified response. The DB-layer enums are translated to
     * the Lane-4 friendly names so the admin page never has to know about
     * the Mendix-legacy strings.
     */
    public static RoundCriteriaResponse from(BidRoundSelectionFilter entity) {
        return new RoundCriteriaResponse(
                entity.getRound(),
                toFriendlyQualification(entity.getRegularBuyerQualification()),
                toFriendlyInventory(entity.getRegularBuyerInventoryOptions()),
                Boolean.TRUE.equals(entity.getStbAllowAllBuyersOverride()));
    }

    /**
     * Build a defaults-only response for a given round when no row exists
     * yet. Mirrors the entity's column defaults so the admin page can
     * still render meaningful initial state without an extra round-trip.
     */
    public static RoundCriteriaResponse defaultsFor(int round) {
        return new RoundCriteriaResponse(
                round,
                QUALIFICATION_BID_BUYERS_ONLY,
                INVENTORY_WITH_BIDS,
                Boolean.FALSE);
    }

    private static String toFriendlyQualification(RegularBuyerQualification value) {
        if (value == null) return QUALIFICATION_BID_BUYERS_ONLY;
        return switch (value) {
            case All_Buyers -> QUALIFICATION_ALL_BUYERS;
            case Only_Qualified -> QUALIFICATION_BID_BUYERS_ONLY;
        };
    }

    private static String toFriendlyInventory(RegularBuyerInventoryOption value) {
        if (value == null) return INVENTORY_WITH_BIDS;
        return switch (value) {
            case ShowAllInventory -> INVENTORY_FULL;
            case InventoryRound1QualifiedBids -> INVENTORY_WITH_BIDS;
        };
    }
}
