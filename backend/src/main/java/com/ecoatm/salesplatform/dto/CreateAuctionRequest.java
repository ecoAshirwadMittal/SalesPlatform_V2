package com.ecoatm.salesplatform.dto;

/**
 * Body for {@code POST /api/v1/admin/auctions}. Mirrors the Mendix
 * Create Auction popup inputs — {@code weekId} identifies the target
 * auction week; {@code customSuffix} is the optional text the admin
 * types alongside the read-only "Auction &lt;weekDisplay&gt;" prefix.
 */
public record CreateAuctionRequest(Long weekId, String customSuffix) {}
