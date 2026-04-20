-- =============================================================================
-- V59: Auctions module — bid rounds + configuration singletons
-- Source: auctionui$bidround (+ collapsed junctions bidround_schedulingauction,
--         bidround_buyercode, bidround_week, bidround_submittedby),
--         auctionui$bidroundselectionfilter, auctionui$targetpricefactor
--         (+ targetpricefactor_bidroundselectionfilter junction),
--         auctionui$bidranking, auctionui$sharepointmethod,
--         auctionui$biddatatotalquantityconfig
--
-- Design notes:
--   • All N:1 bidround junctions collapsed to FK columns on bid_rounds
--     (see plan §3.1). bid_round_week is sparse (119/1917) — nullable FK.
--   • Deferred-FK tighten on buyer_mgmt.qbc_* is in V64, after extractor loads
--     auctions data (§7 risk mitigation).
--   • target_price_factor_filters stays as a junction — it is a real M:M
--     (one factor band can apply to multiple round filters).
-- =============================================================================

-- ---------------------------------------------------------------------------
-- auctions.bid_rounds (per-buyer-code snapshot of a round's submission state)
-- Source: auctionui$bidround (1,917 rows)
-- Collapsed junctions:
--   bidround_schedulingauction (1,924) → scheduling_auction_id (N:1)
--   bidround_buyercode         (1,924) → buyer_code_id          (N:1)
--   bidround_week              (  119) → week_id              (N:1, nullable)
--   bidround_submittedby       (1,145) → submitted_by_user_id  (N:1, nullable)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_rounds (
    id                              BIGSERIAL       PRIMARY KEY,
    legacy_id                       BIGINT          UNIQUE,
    scheduling_auction_id           BIGINT          NOT NULL REFERENCES auctions.scheduling_auctions(id) ON DELETE CASCADE,
    buyer_code_id                   BIGINT          NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    week_id                         BIGINT          REFERENCES mdm.week(id),
    submitted_by_user_id            BIGINT          REFERENCES user_mgmt.ecoatm_direct_users(user_id),
    submitted                       BOOLEAN         NOT NULL DEFAULT false,
    submitted_datetime              TIMESTAMPTZ,
    uploaded_to_sharepoint          BOOLEAN         NOT NULL DEFAULT false,
    upload_to_sharepoint_datetime   TIMESTAMPTZ,
    note                            VARCHAR(2000),
    is_deprecated                   BOOLEAN         NOT NULL DEFAULT false,
    created_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                        BIGINT          REFERENCES identity.users(id),
    changed_by_id                   BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE  auctions.bid_rounds IS 'Per-buyer-code per-round bid submission state (auctionui$bidround)';
COMMENT ON COLUMN auctions.bid_rounds.scheduling_auction_id IS 'Parent round — collapsed from auctionui$bidround_schedulingauction junction';
COMMENT ON COLUMN auctions.bid_rounds.buyer_code_id IS 'Bidding buyer code — collapsed from auctionui$bidround_buyercode junction';
COMMENT ON COLUMN auctions.bid_rounds.week_id IS 'Optional week link — collapsed from auctionui$bidround_week (sparse, 119/1917 in prod)';
COMMENT ON COLUMN auctions.bid_rounds.submitted_by_user_id IS 'User who submitted — collapsed from auctionui$bidround_submittedby (sparse, 1145/1917)';
COMMENT ON COLUMN auctions.bid_rounds.is_deprecated IS 'Soft-delete flag for superseded bid round rows';

CREATE INDEX idx_br_scheduling      ON auctions.bid_rounds(scheduling_auction_id);
CREATE INDEX idx_br_buyer_code      ON auctions.bid_rounds(buyer_code_id);
CREATE INDEX idx_br_week            ON auctions.bid_rounds(week_id);
CREATE INDEX idx_br_submitted       ON auctions.bid_rounds(submitted);
CREATE INDEX idx_br_sa_buyer_code   ON auctions.bid_rounds(scheduling_auction_id, buyer_code_id);

-- ---------------------------------------------------------------------------
-- auctions.bid_round_selection_filters (round qualification criteria)
-- Source: auctionui$bidroundselectionfilter (2 rows: R2 + R3 criteria)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_round_selection_filters (
    id                              BIGSERIAL       PRIMARY KEY,
    legacy_id                       BIGINT          UNIQUE,
    round                           INTEGER         NOT NULL,
    target_percent                  NUMERIC(10, 4),
    target_value                    NUMERIC(14, 2),
    total_value_floor               NUMERIC(14, 2),
    merged_grade1                   VARCHAR(30),
    merged_grade2                   VARCHAR(30),
    merged_grade3                   VARCHAR(30),
    stb_allow_all_buyers_override   BOOLEAN         NOT NULL DEFAULT false,
    stb_include_all_inventory       BOOLEAN         NOT NULL DEFAULT false,
    regular_buyer_qualification     VARCHAR(30)     NOT NULL DEFAULT 'Only_Qualified',
    regular_buyer_inventory_options VARCHAR(40)     NOT NULL DEFAULT 'InventoryRound1QualifiedBids',
    created_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_brsf_round              CHECK (round IN (2, 3)),
    CONSTRAINT chk_brsf_regular_qual       CHECK (regular_buyer_qualification IN ('Only_Qualified','All_Buyers')),
    CONSTRAINT chk_brsf_regular_inventory  CHECK (regular_buyer_inventory_options IN ('InventoryRound1QualifiedBids','ShowAllInventory'))
);

COMMENT ON TABLE  auctions.bid_round_selection_filters IS 'Round 2/3 qualification criteria (auctionui$bidroundselectionfilter)';
COMMENT ON COLUMN auctions.bid_round_selection_filters.regular_buyer_inventory_options IS 'Enum_RegularBuyerInventoryOption — legacy Mendix typo "ShowAllINventory" cleaned up to "ShowAllInventory"';

-- ---------------------------------------------------------------------------
-- auctions.target_price_factors (band-based price adjustment factors)
-- Source: auctionui$targetpricefactor (24 rows)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.target_price_factors (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    minimum_value           NUMERIC(14, 2)  NOT NULL,
    maximum_value           NUMERIC(14, 2)  NOT NULL,
    factor_type             VARCHAR(20)     NOT NULL,
    factor_amount           NUMERIC(14, 4)  NOT NULL,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_tpf_factor_type CHECK (factor_type IN ('Percentage_Factor','Flat_Amount')),
    CONSTRAINT chk_tpf_band        CHECK (maximum_value >= minimum_value)
);

COMMENT ON TABLE auctions.target_price_factors IS 'Price band → factor mapping used by R2/R3 target price calc (auctionui$targetpricefactor)';

-- ---------------------------------------------------------------------------
-- auctions.target_price_factor_filters (M:M factor ↔ round filter)
-- Source: auctionui$targetpricefactor_bidroundselectionfilter (24 rows)
-- Kept as junction — a factor band can be referenced by multiple round filters.
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.target_price_factor_filters (
    target_price_factor_id          BIGINT  NOT NULL REFERENCES auctions.target_price_factors(id) ON DELETE CASCADE,
    bid_round_selection_filter_id   BIGINT  NOT NULL REFERENCES auctions.bid_round_selection_filters(id) ON DELETE CASCADE,
    PRIMARY KEY (target_price_factor_id, bid_round_selection_filter_id)
);

-- ---------------------------------------------------------------------------
-- auctions.bid_ranking_config (singleton)
-- Source: auctionui$bidranking (1 row)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_ranking_config (
    id              BIGSERIAL       PRIMARY KEY,
    legacy_id       BIGINT          UNIQUE,
    display_rank    INTEGER         NOT NULL DEFAULT 5,
    minimum_bid     NUMERIC(14, 2)  NOT NULL DEFAULT 0.00,
    maximum_rank    INTEGER         NOT NULL DEFAULT 10,
    created_date    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE auctions.bid_ranking_config IS 'Singleton — bid ranking display config (auctionui$bidranking)';

-- ---------------------------------------------------------------------------
-- auctions.sharepoint_method_config (singleton)
-- Source: auctionui$sharepointmethod (1 row)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.sharepoint_method_config (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    use_api_upload          BOOLEAN         NOT NULL DEFAULT false,
    oql_endpoint            VARCHAR(500),
    api_endpoint            VARCHAR(500),
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  auctions.sharepoint_method_config IS 'Singleton — SharePoint upload mode: OQL vs API (auctionui$sharepointmethod)';
COMMENT ON COLUMN auctions.sharepoint_method_config.use_api_upload IS 'True: upload via REST API; false: upload via OQL legacy method';

-- ---------------------------------------------------------------------------
-- auctions.bid_data_quantity_overrides (per-ecoid grade quantity override)
-- Source: auctionui$biddatatotalquantityconfig (4 rows)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_data_quantity_overrides (
    id              BIGSERIAL       PRIMARY KEY,
    legacy_id       BIGINT          UNIQUE,
    ecoid           VARCHAR(100)    NOT NULL,
    grade           VARCHAR(30)     NOT NULL,
    quantity        INTEGER         NOT NULL,
    created_date    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (ecoid, grade)
);

COMMENT ON TABLE auctions.bid_data_quantity_overrides IS 'Per-ecoid grade quantity override for bid data (auctionui$biddatatotalquantityconfig)';
