package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.RegularBuyerInventoryOption;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerQualification;

/**
 * Request payload for {@code PUT /api/v1/admin/round-criteria/{round}}.
 *
 * <p>Lane 4 exposes only the three settings the QA POM
 * {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings} drives. The
 * service preserves any other column on
 * {@link com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter}
 * (target_percent, merged_grade1, etc.) — those remain at their existing
 * row values when present, or fall back to the JPA entity / DB defaults
 * on first insert.
 *
 * <p>Translation between the friendly Lane-4 enum names and the DB-side
 * legacy enums is handled here:
 * <ul>
 *   <li>{@code Bid_Buyers_Only} → {@link RegularBuyerQualification#Only_Qualified}</li>
 *   <li>{@code All_Buyers}      → {@link RegularBuyerQualification#All_Buyers}</li>
 *   <li>{@code Full_Inventory}      → {@link RegularBuyerInventoryOption#ShowAllInventory}</li>
 *   <li>{@code Inventory_With_Bids} → {@link RegularBuyerInventoryOption#InventoryRound1QualifiedBids}</li>
 * </ul>
 *
 * <p>Unknown values throw {@link IllegalArgumentException} (mapped to 400).
 */
public record RoundCriteriaUpdateRequest(
        String regularBuyerQualification,
        String regularBuyerInventoryOptions,
        Boolean stbAllowAllBuyersOverride) {

    public RegularBuyerQualification toQualificationEnum() {
        if (regularBuyerQualification == null) {
            throw new IllegalArgumentException("regularBuyerQualification is required");
        }
        return switch (regularBuyerQualification) {
            case "All_Buyers" -> RegularBuyerQualification.All_Buyers;
            case "Bid_Buyers_Only" -> RegularBuyerQualification.Only_Qualified;
            default -> throw new IllegalArgumentException(
                    "regularBuyerQualification must be 'All_Buyers' or 'Bid_Buyers_Only' (was '"
                            + regularBuyerQualification + "')");
        };
    }

    public RegularBuyerInventoryOption toInventoryEnum() {
        if (regularBuyerInventoryOptions == null) {
            throw new IllegalArgumentException("regularBuyerInventoryOptions is required");
        }
        return switch (regularBuyerInventoryOptions) {
            case "Full_Inventory" -> RegularBuyerInventoryOption.ShowAllInventory;
            case "Inventory_With_Bids" -> RegularBuyerInventoryOption.InventoryRound1QualifiedBids;
            default -> throw new IllegalArgumentException(
                    "regularBuyerInventoryOptions must be 'Full_Inventory' or 'Inventory_With_Bids' (was '"
                            + regularBuyerInventoryOptions + "')");
        };
    }

    /** Defaults to {@code false} when caller omits the field — DB column is NOT NULL. */
    public boolean stbAllowAllBuyersOverrideOrDefault() {
        return Boolean.TRUE.equals(stbAllowAllBuyersOverride);
    }
}
