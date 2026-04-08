-- =============================================================================
-- V10: Buyer Management – Session Helpers & Round 3 Helpers
-- Source: ecoatm_buyermanagement$buyercode_sessionandtabhelper,
--         ecoatm_buyermanagement$buyercode_sessionandtabhelper_session,
--         ecoatm_buyermanagement$buyercode_sessionandtabhelper_buyercode,
--         ecoatm_buyermanagement$buyercodeselect_helper,
--         ecoatm_buyermanagement$buyercodeselect_helper_bidround,
--         ecoatm_buyermanagement$buyercodeselect_helper_buyercode,
--         ecoatm_buyermanagement$buyercodeselect_helper_schedulingauction,
--         ecoatm_buyermanagement$round3nosalesrephelper,
--         ecoatm_buyermanagement$buyer_round3nosalesrephelper
--
-- Design notes:
--   • These tables are Mendix session-scoped UI helpers; most have 0-1 rows.
--   • Kept for migration completeness. In the new stack, session state is
--     managed server-side (Spring Session + Redis), so these will become
--     no-ops or be removed in a subsequent cleanup migration.
--   • buyercodeselect_helper is a form-selection wizard helper (0 rows in prod).
--   • round3nosalesrephelper is used for Round 3 bid filtering (0 rows in prod).
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Session & Tab Helper (tracks open browser tabs for buyer code lock)
-- Source: ecoatm_buyermanagement$buyercode_sessionandtabhelper
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_code_session_helpers (
    id              BIGINT        PRIMARY KEY,
    tab_id          VARCHAR(200), -- browser tab fingerprint
    created_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id        BIGINT        REFERENCES identity.users(id),
    changed_by_id   BIGINT        REFERENCES identity.users(id)
);

COMMENT ON TABLE buyer_mgmt.buyer_code_session_helpers IS 'Tracks browser tab identity for buyer code lock management (ecoatm_buyermanagement$buyercode_sessionandtabhelper)';

-- Session link for session helper
CREATE TABLE buyer_mgmt.buyer_code_session_helper_sessions (
    helper_id   BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_code_session_helpers(id) ON DELETE CASCADE,
    session_id  BIGINT  NOT NULL REFERENCES identity.sessions(id) ON DELETE CASCADE,
    PRIMARY KEY (helper_id, session_id)
);

-- Buyer code link for session helper
CREATE TABLE buyer_mgmt.buyer_code_session_helper_codes (
    helper_id       BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_code_session_helpers(id) ON DELETE CASCADE,
    buyer_code_id   BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_codes(id)               ON DELETE CASCADE,
    PRIMARY KEY (helper_id, buyer_code_id)
);

-- ---------------------------------------------------------------------------
-- Buyer Code Select Helper (wizard state for buyer code assignment)
-- Source: ecoatm_buyermanagement$buyercodeselect_helper
-- 0 rows in prod — session-scoped ephemeral state
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_code_select_helpers (
    id              BIGINT        PRIMARY KEY,
    company_name    VARCHAR(200),
    code            VARCHAR(200),
    is_upsell_round BOOLEAN,
    note            VARCHAR(2000)
);

COMMENT ON TABLE buyer_mgmt.buyer_code_select_helpers IS 'Session-scoped wizard state for buyer code selection (ecoatm_buyermanagement$buyercodeselect_helper); 0 rows in prod';

CREATE TABLE buyer_mgmt.buyer_code_select_helper_bid_rounds (
    helper_id       BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_code_select_helpers(id) ON DELETE CASCADE,
    bid_round_id    BIGINT  NOT NULL,  -- FK to auctionui.bid_rounds (V-auction)
    PRIMARY KEY (helper_id, bid_round_id)
);

CREATE TABLE buyer_mgmt.buyer_code_select_helper_buyer_codes (
    helper_id       BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_code_select_helpers(id) ON DELETE CASCADE,
    buyer_code_id   BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_codes(id) ON DELETE CASCADE,
    PRIMARY KEY (helper_id, buyer_code_id)
);

CREATE TABLE buyer_mgmt.buyer_code_select_helper_scheduling_auctions (
    helper_id               BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_code_select_helpers(id) ON DELETE CASCADE,
    scheduling_auction_id   BIGINT  NOT NULL,  -- FK to auctionui.scheduling_auctions (V-auction)
    PRIMARY KEY (helper_id, scheduling_auction_id)
);

-- ---------------------------------------------------------------------------
-- Round 3 No-Sales-Rep Helper (for buyers without a rep in Round 3 logic)
-- Source: ecoatm_buyermanagement$round3nosalesrephelper + buyer link
-- 0 rows in prod — Mendix query helper object
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.round3_no_sales_rep_helpers (
    id  BIGINT  PRIMARY KEY
);

COMMENT ON TABLE buyer_mgmt.round3_no_sales_rep_helpers IS 'Helper for Round 3 buyers without sales rep assignment (ecoatm_buyermanagement$round3nosalesrephelper); 0 rows in prod';

CREATE TABLE buyer_mgmt.buyer_round3_helpers (
    buyer_id    BIGINT  NOT NULL REFERENCES buyer_mgmt.buyers(id) ON DELETE CASCADE,
    helper_id   BIGINT  NOT NULL REFERENCES buyer_mgmt.round3_no_sales_rep_helpers(id) ON DELETE CASCADE,
    PRIMARY KEY (buyer_id, helper_id)
);

COMMENT ON TABLE buyer_mgmt.buyer_round3_helpers IS 'Junction: buyers to round3 helper objects (ecoatm_buyermanagement$buyer_round3nosalesrephelper)';
