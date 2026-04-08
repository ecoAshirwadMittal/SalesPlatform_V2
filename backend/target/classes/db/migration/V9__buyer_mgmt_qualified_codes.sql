-- =============================================================================
-- V9: Buyer Management – Qualified Buyer Codes
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes,
--         ecoatm_buyermanagement$qualifiedbuyercodes_buyercode,
--         ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction,
--         ecoatm_buyermanagement$qualifiedbuyercodes_bidround,
--         ecoatm_buyermanagement$qualifiedbuyercodes_submittedby,
--         ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper,
--         ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_auction,
--         ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_session
--
-- Design notes:
--   • qualifiedbuyercodes is the central pivot linking buyer codes to auction
--     scheduling events. 23,736 rows in prod.
--   • qualificationtype enum: Qualified | Not_Qualified | Manual
--   • Junction tables to schedulingauction and bidround reference the auctionui
--     module which is NOT migrated in this PR. Their FKs are deferred (no FK
--     constraint now; added in a subsequent auction module migration).
--   • qualifiedbuyercodesqueryhelper is a Mendix session-scoped helper object
--     (query cache). It is kept for migration completeness but will be replaced
--     by server-side caching in the new stack.
--   • system$owner / system$changedby are migrated as owner_id / changed_by_id.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Qualified Buyer Codes (central qualification pivot)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qualified_buyer_codes (
    id                          BIGINT        PRIMARY KEY,
    qualification_type          VARCHAR(13)   NOT NULL DEFAULT 'Not_Qualified',
                                                        -- Qualified | Not_Qualified | Manual
    included                    BOOLEAN       NOT NULL DEFAULT false,
    submitted                   BOOLEAN       NOT NULL DEFAULT false,
    submitted_datetime          TIMESTAMP,
    opened_dashboard            BOOLEAN       NOT NULL DEFAULT false,
    opened_dashboard_datetime   TIMESTAMP,
    is_special_treatment        BOOLEAN       NOT NULL DEFAULT false,
    created_date                TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date                TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id                    BIGINT        REFERENCES identity.users(id),
    changed_by_id               BIGINT        REFERENCES identity.users(id)
);

COMMENT ON TABLE  buyer_mgmt.qualified_buyer_codes IS 'Qualification status for a buyer code in the context of an auction event (ecoatm_buyermanagement$qualifiedbuyercodes)';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.qualification_type IS 'Qualified | Not_Qualified | Manual — how the buyer code was qualified for this event';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.included IS 'Whether this buyer code is included in the auction event lot';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.submitted IS 'True once the buyer has submitted their bid for this qualification';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.is_special_treatment IS 'Marks records receiving special pricing or inclusion logic overrides';

-- ---------------------------------------------------------------------------
-- Qualified Buyer Code ↔ Buyer Code (M:M)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes_buyercode
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qbc_buyer_codes (
    qualified_buyer_code_id BIGINT  NOT NULL REFERENCES buyer_mgmt.qualified_buyer_codes(id) ON DELETE CASCADE,
    buyer_code_id           BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_codes(id)           ON DELETE CASCADE,
    PRIMARY KEY (qualified_buyer_code_id, buyer_code_id)
);

COMMENT ON TABLE buyer_mgmt.qbc_buyer_codes IS 'Links qualified buyer codes to their underlying buyer codes (ecoatm_buyermanagement$qualifiedbuyercodes_buyercode)';

-- ---------------------------------------------------------------------------
-- Qualified Buyer Code ↔ Scheduling Auction (M:M)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction
-- Note: auctionui$schedulingauction not yet migrated — FK deferred to auction module V.
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qbc_scheduling_auctions (
    qualified_buyer_code_id     BIGINT  NOT NULL REFERENCES buyer_mgmt.qualified_buyer_codes(id) ON DELETE CASCADE,
    scheduling_auction_id       BIGINT  NOT NULL,  -- FK to auctionui.scheduling_auctions (V-auction)
    PRIMARY KEY (qualified_buyer_code_id, scheduling_auction_id)
);

COMMENT ON TABLE  buyer_mgmt.qbc_scheduling_auctions IS 'Links qualified buyer codes to scheduling auction events (ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction)';
COMMENT ON COLUMN buyer_mgmt.qbc_scheduling_auctions.scheduling_auction_id IS 'FK to auctionui.scheduling_auctions — constraint added when auction module is migrated';

-- ---------------------------------------------------------------------------
-- Qualified Buyer Code ↔ Bid Round (M:M)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes_bidround
-- Note: auctionui$bidround not yet migrated — FK deferred to auction module V.
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qbc_bid_rounds (
    qualified_buyer_code_id BIGINT  NOT NULL REFERENCES buyer_mgmt.qualified_buyer_codes(id) ON DELETE CASCADE,
    bid_round_id            BIGINT  NOT NULL,  -- FK to auctionui.bid_rounds (V-auction)
    PRIMARY KEY (qualified_buyer_code_id, bid_round_id)
);

COMMENT ON TABLE  buyer_mgmt.qbc_bid_rounds IS 'Links qualified buyer codes to specific bid rounds (ecoatm_buyermanagement$qualifiedbuyercodes_bidround)';
COMMENT ON COLUMN buyer_mgmt.qbc_bid_rounds.bid_round_id IS 'FK to auctionui.bid_rounds — constraint added when auction module is migrated';

-- ---------------------------------------------------------------------------
-- Qualified Buyer Code ↔ Submitted By (user who submitted bid)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodes_submittedby (0 rows in prod)
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qbc_submitted_by (
    qualified_buyer_code_id BIGINT  NOT NULL REFERENCES buyer_mgmt.qualified_buyer_codes(id) ON DELETE CASCADE,
    user_id                 BIGINT  NOT NULL REFERENCES identity.users(id)                    ON DELETE CASCADE,
    PRIMARY KEY (qualified_buyer_code_id, user_id)
);

COMMENT ON TABLE buyer_mgmt.qbc_submitted_by IS 'Tracks which user submitted the bid for a qualified buyer code (ecoatm_buyermanagement$qualifiedbuyercodes_submittedby)';

-- ---------------------------------------------------------------------------
-- Query Helper (Mendix session-scoped cache for QBC lookups)
-- Source: ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper
-- Note: In the new stack, this will be replaced by Spring Cache + Redis.
--       Kept here for migration data completeness only (52 rows in prod).
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.qbc_query_helpers (
    id  BIGINT  PRIMARY KEY
);

COMMENT ON TABLE buyer_mgmt.qbc_query_helpers IS 'Mendix session-scoped query cache helper (ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper); replaced by server-side cache in new stack';

-- Auction link for query helper
CREATE TABLE buyer_mgmt.qbc_query_helper_auctions (
    query_helper_id BIGINT  NOT NULL REFERENCES buyer_mgmt.qbc_query_helpers(id) ON DELETE CASCADE,
    auction_id      BIGINT  NOT NULL,  -- FK to auctionui.auctions (V-auction)
    PRIMARY KEY (query_helper_id, auction_id)
);

-- Session link for query helper
CREATE TABLE buyer_mgmt.qbc_query_helper_sessions (
    query_helper_id BIGINT  NOT NULL REFERENCES buyer_mgmt.qbc_query_helpers(id) ON DELETE CASCADE,
    session_id      BIGINT  NOT NULL REFERENCES identity.sessions(id) ON DELETE CASCADE,
    PRIMARY KEY (query_helper_id, session_id)
);

-- Performance indexes
CREATE INDEX idx_qbc_qualification_type ON buyer_mgmt.qualified_buyer_codes(qualification_type);
CREATE INDEX idx_qbc_included           ON buyer_mgmt.qualified_buyer_codes(included);
CREATE INDEX idx_qbc_submitted          ON buyer_mgmt.qualified_buyer_codes(submitted);
CREATE INDEX idx_qbc_special_treatment  ON buyer_mgmt.qualified_buyer_codes(is_special_treatment);
CREATE INDEX idx_qbc_created_date       ON buyer_mgmt.qualified_buyer_codes(created_date DESC);
