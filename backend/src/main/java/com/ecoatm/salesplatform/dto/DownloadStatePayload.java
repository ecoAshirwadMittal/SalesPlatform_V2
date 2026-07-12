package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * DOWNLOAD-mode payload for the bidder dashboard's ended-auction state.
 * Ports legacy {@code BidDownloadOnBuyerCodeSelect}: the auction heading
 * ("Auction {year} / Wk{week}") plus the round numbers this buyer code has
 * downloadable bids for (one "Download your Round {N} Bids" button each).
 *
 * <p>{@code null} on the response for the live-path {@code Download} branches
 * (BUYER_NOT_INCLUDED / ROUND2_DOWNLOAD) — the frontend falls back to a
 * heading-less Round 1 panel there.
 */
public record DownloadStatePayload(String auctionTitle, List<Integer> rounds) {}
