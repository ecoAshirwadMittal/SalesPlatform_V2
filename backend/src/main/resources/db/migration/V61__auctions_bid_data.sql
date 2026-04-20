-- =============================================================================
-- V61: Auctions module — bid_data fact table
-- Source: auctionui$biddata (11,619,882 rows in prod; modern scope: last
--         2 weeks only, ~450K rows — see plan §7 decision 1)
--         + collapsed junctions biddata_bidround, biddata_buyercode,
--           biddata_aggregatedinventory (all N:1 per row-count analysis)
--
-- Design notes:
--   • All three junctions collapse to direct FK columns. This removes
--     ~35M rows of pure junction-table bloat (relevant even at the
--     450K-row last-2-weeks scope).
--   • Indexes chosen for the hot query paths observed in microflows:
--     (bid_round_id), (ecoid, bid_round), (merged_grade, bid_round),
--     (company_name) for buyer drill-down.
--   • Table created empty — bulk load happens in an extractor follow-up.
-- =============================================================================

CREATE TABLE auctions.bid_data (
    id                              BIGSERIAL       PRIMARY KEY,
    legacy_id                       BIGINT          UNIQUE,

    -- Collapsed FK columns (formerly junction tables)
    bid_round_id                    BIGINT          NOT NULL REFERENCES auctions.bid_rounds(id) ON DELETE CASCADE,
    buyer_code_id                   BIGINT          NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    aggregated_inventory_id         BIGINT          REFERENCES auctions.aggregated_inventory(id),

    -- Device identity (denormalized from aggregated_inventory for Snowflake parity)
    ecoid                           VARCHAR(100),
    merged_grade                    VARCHAR(30),
    merged_grade_text               TEXT,
    code                            VARCHAR(100),        -- denormalized buyer code string
    company_name                    VARCHAR(500),        -- denormalized buyer company

    -- Current-round bid
    bid_quantity                    INTEGER         NOT NULL DEFAULT 0,
    bid_amount                      NUMERIC(14, 2)  NOT NULL DEFAULT 0,
    target_price                    NUMERIC(14, 4),
    payout                          NUMERIC(14, 2),
    maximum_quantity                INTEGER,
    margin                          NUMERIC(14, 4),
    buyer_code_type                 VARCHAR(30),

    -- Previous-round snapshot
    previous_round_bid_quantity     INTEGER,
    previous_round_bid_amount       NUMERIC(14, 2),

    -- Submission tracking
    submit_datetime                 TIMESTAMPTZ,
    submitted_datetime              TIMESTAMPTZ,
    submitted_bid_amount            NUMERIC(14, 2),
    submitted_bid_quantity          INTEGER,
    temp_da_bid_amount              NUMERIC(14, 2),

    -- Last valid snapshot (rollback reference)
    last_valid_bid_quantity         INTEGER,
    last_valid_bid_amount           NUMERIC(14, 2),

    -- Ranking (display + internal)
    highest_bid                     BOOLEAN         NOT NULL DEFAULT false,
    display_round2_bid_rank         INTEGER,
    display_round3_bid_rank         INTEGER,
    round2_bid_rank                 INTEGER,
    round3_bid_rank                 INTEGER,

    -- Round + week denormalization
    bid_round                       INTEGER,
    week_id                         INTEGER,             -- denormalized int week-id (not FK — matches legacy biddata.week_id numeric)

    -- Admin state
    data_wipe_quantity              INTEGER         NOT NULL DEFAULT 0,
    rejected                        BOOLEAN         NOT NULL DEFAULT false,
    is_changed                      BOOLEAN         NOT NULL DEFAULT false,
    reject_reason                   TEXT,
    accept_reason                   TEXT,
    is_deprecated                   BOOLEAN         NOT NULL DEFAULT false,

    -- Audit
    submit_user                     VARCHAR(200),        -- "user" column in legacy (reserved word avoided)
    created_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                        BIGINT          REFERENCES identity.users(id),
    changed_by_id                   BIGINT          REFERENCES identity.users(id),

    CONSTRAINT chk_bd_round_range CHECK (bid_round IS NULL OR bid_round BETWEEN 1 AND 3)
);

COMMENT ON TABLE  auctions.bid_data IS 'Per-buyer-code per-device bid fact rows — the auction fact table (auctionui$biddata). Scope: last 2 weeks of history; older rows stay in Snowflake.';
COMMENT ON COLUMN auctions.bid_data.bid_round_id IS 'Parent bid round — collapsed from auctionui$biddata_bidround (N:1)';
COMMENT ON COLUMN auctions.bid_data.buyer_code_id IS 'Bidding buyer code — collapsed from auctionui$biddata_buyercode (N:1)';
COMMENT ON COLUMN auctions.bid_data.aggregated_inventory_id IS 'Device rollup reference — collapsed from auctionui$biddata_aggregatedinventory (N:1)';
COMMENT ON COLUMN auctions.bid_data.submit_user IS 'Legacy biddata.user column (renamed to avoid SQL reserved-word collision)';

-- Indexes for hot-path queries (round filter, company drill-down, ranking)
CREATE INDEX idx_bd_bid_round         ON auctions.bid_data(bid_round_id);
CREATE INDEX idx_bd_buyer_code        ON auctions.bid_data(buyer_code_id);
CREATE INDEX idx_bd_agg_inv           ON auctions.bid_data(aggregated_inventory_id);
CREATE INDEX idx_bd_ecoid_round       ON auctions.bid_data(ecoid, bid_round);
CREATE INDEX idx_bd_grade_round       ON auctions.bid_data(merged_grade, bid_round);
CREATE INDEX idx_bd_company           ON auctions.bid_data(company_name);
CREATE INDEX idx_bd_submitted         ON auctions.bid_data(submitted_datetime DESC);
CREATE INDEX idx_bd_round_rank2       ON auctions.bid_data(round2_bid_rank) WHERE round2_bid_rank IS NOT NULL;
CREATE INDEX idx_bd_round_rank3       ON auctions.bid_data(round3_bid_rank) WHERE round3_bid_rank IS NOT NULL;
CREATE INDEX idx_bd_highest_bid       ON auctions.bid_data(highest_bid) WHERE highest_bid = true;
CREATE INDEX idx_bd_not_deprecated    ON auctions.bid_data(bid_round_id) WHERE is_deprecated = false;
