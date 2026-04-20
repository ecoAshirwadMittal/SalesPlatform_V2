package com.ecoatm.salesplatform.dto;

/**
 * Row for the Buyers Overview admin grid.
 *
 * Source: Mendix AuctionUI.Buyer_Overview page (Data grid 2 columns).
 * `buyerCodesDisplay` is computed at read time via string_agg over
 * buyer_mgmt.buyer_code_buyers — intentionally NOT denormalized on the
 * buyers table. See docs/architecture/decisions.md (2026-04-15 entry).
 */
public record BuyerOverviewResponse(
        Long id,
        String companyName,
        String buyerCodesDisplay,
        String status
) {}
