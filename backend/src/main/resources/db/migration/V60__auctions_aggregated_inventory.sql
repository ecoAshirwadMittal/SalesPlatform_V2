-- =============================================================================
-- V60: Auctions module — aggregated inventory (device × week × grade rollup)
-- Source: auctionui$aggregatedinventory (+ collapsed junctions _brand, _model,
--         _carrier, _week, r2pomaxbidweek, r3pomaxbidweek),
--         auctionui$aggreegatedinventorytotals (+ collapsed _week junction)
--
-- Design notes:
--   • All N:1 junctions collapsed to direct FK columns (see plan §3.3).
--     All are nullable because the legacy junctions are sparse (80,298 of
--     87,284 parent rows = ~92% populated).
--   • _modelname junction dropped (Q1 — MasterDeviceInventory covers it).
--   • brand/model/carrier are also retained as denormalized TEXT columns
--     matching the legacy aggregatedinventory schema (for display + Snowflake
--     export compatibility). The FK columns are the authoritative link.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- auctions.aggregated_inventory (per-device per-week grade rollup)
-- Source: auctionui$aggregatedinventory (87,284 rows in prod)
-- Collapsed junctions:
--   aggregatedinventory_brand    → brand_id
--   aggregatedinventory_model    → model_id
--   aggregatedinventory_carrier  → carrier_id
--   aggregatedinventory_week     → week_id
--   r2pomaxbidweek               → r2_po_max_bid_week_id (nullable)
--   r3pomaxbidweek               → r3_po_max_bid_week_id (nullable)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.aggregated_inventory (
    id                          BIGSERIAL       PRIMARY KEY,
    legacy_id                   BIGINT          UNIQUE,

    -- Device identity
    ecoid2                      VARCHAR(100),
    name                        VARCHAR(512),
    device_id                   VARCHAR(100),   -- Mendix string UUID, not FK to mdm.device
    category                    VARCHAR(100),

    -- Denormalized dimension text (retained for Snowflake export parity)
    brand                       VARCHAR(200),
    model                       VARCHAR(200),
    carrier                     VARCHAR(200),

    -- Dimension FKs (collapsed from junctions)
    brand_id                    BIGINT          REFERENCES mdm.brand(id),
    model_id                    BIGINT          REFERENCES mdm.model(id),
    carrier_id                  BIGINT          REFERENCES mdm.carrier(id),
    week_id                     BIGINT          REFERENCES mdm.week(id),

    -- Grade + flags
    merged_grade                VARCHAR(30),
    datawipe                    BOOLEAN         NOT NULL DEFAULT false,
    is_total_quantity_modified  BOOLEAN         NOT NULL DEFAULT false,
    is_deprecated               BOOLEAN         NOT NULL DEFAULT false,

    -- Quantities
    total_quantity              INTEGER         NOT NULL DEFAULT 0,
    dw_total_quantity           INTEGER         NOT NULL DEFAULT 0,

    -- Payout metrics
    avg_payout                  NUMERIC(14, 4)  DEFAULT 0,
    total_payout                NUMERIC(14, 2)  DEFAULT 0,
    dw_avg_payout               NUMERIC(14, 4)  DEFAULT 0,
    dw_total_payout             NUMERIC(14, 2)  DEFAULT 0,

    -- Target price (per round)
    avg_target_price            NUMERIC(14, 4)  DEFAULT 0,
    dw_avg_target_price         NUMERIC(14, 4)  DEFAULT 0,
    round1_target_price         NUMERIC(14, 4)  DEFAULT 0,
    round2_target_price         NUMERIC(14, 4)  DEFAULT 0,
    round3_target_price         NUMERIC(14, 4)  DEFAULT 0,
    round1_target_price_dw      NUMERIC(14, 4)  DEFAULT 0,

    -- Max-bid snapshots
    round1_max_bid              NUMERIC(14, 2)  DEFAULT 0,
    round2_max_bid              NUMERIC(14, 2)  DEFAULT 0,
    round1_max_bid_buyer_code   VARCHAR(100),
    round2_max_bid_buyer_code   VARCHAR(100),

    -- Round 2/3 target price factor (enum + amount)
    r2_target_price_factor      NUMERIC(14, 4),
    r3_target_price_factor      NUMERIC(14, 4),
    r2_target_price_factor_type VARCHAR(20),
    r3_target_price_factor_type VARCHAR(20),
    round2_eb_for_target        NUMERIC(14, 4),
    round3_eb_for_target        NUMERIC(14, 4),

    -- R2/R3 purchase order max bid snapshots
    r2_po_max_buyer_code        VARCHAR(100),
    r2_po_max_bid               NUMERIC(14, 2)  DEFAULT 0,
    r2_po_max_bid_week_id       BIGINT          REFERENCES mdm.week(id),
    r3_po_max_buyer_code        VARCHAR(100),
    r3_po_max_bid               NUMERIC(14, 2)  DEFAULT 0,
    r3_po_max_bid_week_id       BIGINT          REFERENCES mdm.week(id),

    -- Audit
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_date                TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date                TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                    BIGINT          REFERENCES identity.users(id),
    changed_by_id               BIGINT          REFERENCES identity.users(id),

    CONSTRAINT chk_agi_r2_factor_type CHECK (r2_target_price_factor_type IS NULL OR r2_target_price_factor_type IN ('Percentage_Factor','Flat_Amount')),
    CONSTRAINT chk_agi_r3_factor_type CHECK (r3_target_price_factor_type IS NULL OR r3_target_price_factor_type IN ('Percentage_Factor','Flat_Amount'))
);

COMMENT ON TABLE  auctions.aggregated_inventory IS 'Per-device per-week grade rollup used as the inventory backbone for auctions (auctionui$aggregatedinventory)';
COMMENT ON COLUMN auctions.aggregated_inventory.device_id IS 'Mendix string UUID (not an FK to mdm.device — intentional for legacy parity)';
COMMENT ON COLUMN auctions.aggregated_inventory.brand_id IS 'FK to mdm.brand — collapsed from auctionui$aggregatedinventory_brand junction';
COMMENT ON COLUMN auctions.aggregated_inventory.week_id IS 'FK to mdm.week — collapsed from auctionui$aggregatedinventory_week junction';
COMMENT ON COLUMN auctions.aggregated_inventory.r2_po_max_bid_week_id IS 'FK to mdm.week — collapsed from auctionui$r2pomaxbidweek';
COMMENT ON COLUMN auctions.aggregated_inventory.r3_po_max_bid_week_id IS 'FK to mdm.week — collapsed from auctionui$r3pomaxbidweek';

-- Indexes for hot-path queries (ecoid lookup, grade filter, week rollups)
CREATE INDEX idx_agi_ecoid2          ON auctions.aggregated_inventory(ecoid2);
CREATE INDEX idx_agi_week            ON auctions.aggregated_inventory(week_id);
CREATE INDEX idx_agi_brand           ON auctions.aggregated_inventory(brand_id);
CREATE INDEX idx_agi_model           ON auctions.aggregated_inventory(model_id);
CREATE INDEX idx_agi_carrier         ON auctions.aggregated_inventory(carrier_id);
CREATE INDEX idx_agi_merged_grade    ON auctions.aggregated_inventory(merged_grade);
CREATE INDEX idx_agi_week_grade      ON auctions.aggregated_inventory(week_id, merged_grade);
CREATE INDEX idx_agi_deprecated      ON auctions.aggregated_inventory(is_deprecated) WHERE is_deprecated = false;

-- ---------------------------------------------------------------------------
-- auctions.aggregated_inventory_totals (per-week totals summary)
-- Source: auctionui$aggreegatedinventorytotals (9 rows — note legacy typo)
-- Collapsed: auctionui$agginventorytotals_week → week_id
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.aggregated_inventory_totals (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    week_id                 BIGINT          REFERENCES mdm.week(id),
    total_quantity          INTEGER         NOT NULL DEFAULT 0,
    dw_total_quantity       INTEGER         NOT NULL DEFAULT 0,
    total_payout            NUMERIC(16, 2)  DEFAULT 0,
    dw_total_payout         NUMERIC(16, 2)  DEFAULT 0,
    device_count            INTEGER         NOT NULL DEFAULT 0,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE auctions.aggregated_inventory_totals IS 'Per-week inventory totals summary (auctionui$aggreegatedinventorytotals — legacy typo preserved in source only)';

CREATE INDEX idx_agit_week ON auctions.aggregated_inventory_totals(week_id);
