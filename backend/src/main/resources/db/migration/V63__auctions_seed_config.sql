-- =============================================================================
-- V63: Auctions module — seed singletons + default target price factor bands
-- Sources: auctionui$bidranking, auctionui$sharepointmethod,
--          auctionui$targetpricefactor (default band layout)
--
-- Scope: configuration singletons only. Historical bulk data for bid_data /
-- aggregated_inventory (last 2 weeks) is loaded via the extractor script
-- follow-up, not via Flyway (consistent with V16–V24 convention).
-- =============================================================================

-- ---------------------------------------------------------------------------
-- bid_ranking_config — singleton
-- ---------------------------------------------------------------------------
INSERT INTO auctions.bid_ranking_config (display_rank, minimum_bid, maximum_rank)
VALUES (5, 0.00, 10)
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- sharepoint_method_config — singleton (default to OQL legacy mode)
-- ---------------------------------------------------------------------------
INSERT INTO auctions.sharepoint_method_config (use_api_upload, oql_endpoint, api_endpoint)
VALUES (false, NULL, NULL)
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- target_price_factors — default bands (from legacy auctionui$targetpricefactor
-- 24-row layout — 12 bands each for Percentage_Factor and Flat_Amount).
-- These are placeholder bands; production values will be reseeded by ops.
-- ---------------------------------------------------------------------------
INSERT INTO auctions.target_price_factors (minimum_value, maximum_value, factor_type, factor_amount)
VALUES
    (     0.00,     10.00, 'Percentage_Factor',  5.0000),
    (    10.01,     25.00, 'Percentage_Factor',  7.5000),
    (    25.01,     50.00, 'Percentage_Factor', 10.0000),
    (    50.01,    100.00, 'Percentage_Factor', 12.5000),
    (   100.01,    250.00, 'Percentage_Factor', 15.0000),
    (   250.01,    500.00, 'Percentage_Factor', 17.5000),
    (   500.01,   1000.00, 'Percentage_Factor', 20.0000),
    (  1000.01,   9999.99, 'Percentage_Factor', 22.5000),
    (     0.00,     10.00, 'Flat_Amount',        0.5000),
    (    10.01,     50.00, 'Flat_Amount',        1.0000),
    (    50.01,    250.00, 'Flat_Amount',        2.5000),
    (   250.01,   9999.99, 'Flat_Amount',        5.0000)
ON CONFLICT DO NOTHING;
