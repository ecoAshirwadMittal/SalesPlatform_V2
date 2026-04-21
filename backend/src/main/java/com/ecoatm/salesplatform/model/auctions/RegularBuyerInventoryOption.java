package com.ecoatm.salesplatform.model.auctions;

/**
 * Mirrors Mendix {@code EcoATM_BuyerManagement.Enum_RegularBuyerInventoryOption}.
 * Values match the {@code chk_brsf_regular_inventory} CHECK constraint in V59.
 *
 * <p>Controls which inventory the qualifying buyer sees:
 * either only inventory that attracted a Round-1 bid, or the full inventory.
 *
 * <p>Note: Mendix stored this as the typo {@code ShowAllINventory}; the V59
 * migration cleaned that up to {@code ShowAllInventory}.
 */
public enum RegularBuyerInventoryOption {
    InventoryRound1QualifiedBids,
    ShowAllInventory
}
