-- =============================================================================
-- V8: Buyer Management – Buyers, Buyer Codes, Sales Representatives
-- Source: ecoatm_buyermanagement$buyer, ecoatm_buyermanagement$buyercode,
--         ecoatm_buyermanagement$salesrepresentative,
--         ecoatm_buyermanagement$buyer_salesrepresentative,
--         ecoatm_buyermanagement$buyercode_buyer,
--         ecoatm_buyermanagement$auctionsfeature
--
-- Design notes:
--   • buyer is the company entity; buyer code ties a buyer to auction participation.
--   • buyercodetype enum: Wholesale | Data_Wipe | Purchasing_Order_Data_Wipe | Purchasing_Order
--   • Validation/display helper columns EXCLUDED from buyer:
--       isfailedbuyerdisable, buyercodeinvalidmessage_*, buyercodesemptyvalidationmessage,
--       buyeruniquevalidationmessage, buyercodetypeinvalidmessage, buyeremptyvalidationmessage,
--       buyercodesdisplay (all purely UI display/validation state)
--   • buyercode EXCLUDED: typevalid, codeemptyvalid, codeuniquevalid, showsubmitofferbtn
--   • auctionsfeature is a singleton feature-flag table — kept in full.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Sales Representatives (ecoATM internal staff managing buyer relationships)
-- Source: ecoatm_buyermanagement$salesrepresentative
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.sales_representatives (
    id                      BIGINT        PRIMARY KEY,
    -- Legacy sequential integer ID from Mendix (separate from bigint PK)
    sales_representative_id BIGINT,       -- was: salesrepresentativeid (1, 2, 4...)
    first_name              VARCHAR(200),
    last_name               VARCHAR(200),
    active                  BOOLEAN       NOT NULL DEFAULT true,
    created_date            TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT        REFERENCES identity.users(id),
    changed_by_id           BIGINT        REFERENCES identity.users(id)
);

COMMENT ON TABLE  buyer_mgmt.sales_representatives IS 'Internal ecoATM sales reps managing buyer accounts (ecoatm_buyermanagement$salesrepresentative)';
COMMENT ON COLUMN buyer_mgmt.sales_representatives.sales_representative_id IS 'Legacy sequential ID from Mendix, separate from the bigint PK';

-- ---------------------------------------------------------------------------
-- Buyers (company-level entities)
-- Source: ecoatm_buyermanagement$buyer
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyers (
    id              BIGINT        PRIMARY KEY,
    submission_id   BIGINT,                   -- Mendix sequential object ID
    company_name    VARCHAR(200),
    status          VARCHAR(8)    NOT NULL DEFAULT 'Active',  -- Active | Disabled
    is_special_buyer BOOLEAN      NOT NULL DEFAULT false,
    created_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id        BIGINT        REFERENCES identity.users(id),
    changed_by_id   BIGINT        REFERENCES identity.users(id),
    entity_owner    VARCHAR(200)  -- email of the ecoATM staff owner (denormalized, for data migration)
);

COMMENT ON TABLE  buyer_mgmt.buyers IS 'Buyer (company) entities in the platform (ecoatm_buyermanagement$buyer)';
COMMENT ON COLUMN buyer_mgmt.buyers.is_special_buyer IS 'Marks buyers for special treatment in pricing/qualification logic';
COMMENT ON COLUMN buyer_mgmt.buyers.entity_owner IS 'Email of Mendix entity owner; denormalized for migration; resolved to user_id via identity.accounts on import';

-- ---------------------------------------------------------------------------
-- Buyer ↔ Sales Representative (M:M)
-- Source: ecoatm_buyermanagement$buyer_salesrepresentative
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_sales_reps (
    buyer_id            BIGINT  NOT NULL REFERENCES buyer_mgmt.buyers(id)                ON DELETE CASCADE,
    sales_rep_id        BIGINT  NOT NULL REFERENCES buyer_mgmt.sales_representatives(id) ON DELETE CASCADE,
    PRIMARY KEY (buyer_id, sales_rep_id)
);

COMMENT ON TABLE buyer_mgmt.buyer_sales_reps IS 'Many-to-many between buyers and sales reps (ecoatm_buyermanagement$buyer_salesrepresentative)';

-- ---------------------------------------------------------------------------
-- Buyer Codes (auction participation codes)
-- Source: ecoatm_buyermanagement$buyercode
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_codes (
    id              BIGINT        PRIMARY KEY,
    submission_id   BIGINT,                   -- Mendix sequential object ID
    code            VARCHAR(200)  NOT NULL,   -- e.g. "20399" — the auction participation code
    buyer_code_type VARCHAR(26),              -- Wholesale | Data_Wipe | Purchasing_Order_Data_Wipe | Purchasing_Order
    status          VARCHAR(8)    NOT NULL DEFAULT 'Active',  -- Active | Inactive
    budget          INTEGER,                  -- Buyer's allocated budget for the event
    soft_delete     BOOLEAN       NOT NULL DEFAULT false,
    created_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id        BIGINT        REFERENCES identity.users(id),
    changed_by_id   BIGINT        REFERENCES identity.users(id),
    entity_owner    VARCHAR(200)  -- email of Mendix owner; resolve to user_id on import
);

COMMENT ON TABLE  buyer_mgmt.buyer_codes IS 'Buyer codes used for auction participation (ecoatm_buyermanagement$buyercode)';
COMMENT ON COLUMN buyer_mgmt.buyer_codes.code IS 'Short alphanumeric auction participation identifier, e.g. "20399"';
COMMENT ON COLUMN buyer_mgmt.buyer_codes.buyer_code_type IS 'Wholesale | Data_Wipe | Purchasing_Order_Data_Wipe | Purchasing_Order';
COMMENT ON COLUMN buyer_mgmt.buyer_codes.budget IS 'Buyer code budget in USD cents or whole dollars as stored in Mendix';
COMMENT ON COLUMN buyer_mgmt.buyer_codes.soft_delete IS 'Soft delete flag — records are retained for audit, excluded from active queries';

-- ---------------------------------------------------------------------------
-- Buyer Code ↔ Buyer (M:M — code can be shared across buyers, buyer has many codes)
-- Source: ecoatm_buyermanagement$buyercode_buyer
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_code_buyers (
    buyer_code_id   BIGINT  NOT NULL REFERENCES buyer_mgmt.buyer_codes(id) ON DELETE CASCADE,
    buyer_id        BIGINT  NOT NULL REFERENCES buyer_mgmt.buyers(id)       ON DELETE CASCADE,
    PRIMARY KEY (buyer_code_id, buyer_id)
);

COMMENT ON TABLE buyer_mgmt.buyer_code_buyers IS 'Many-to-many between buyer codes and buyer companies (ecoatm_buyermanagement$buyercode_buyer)';

-- ---------------------------------------------------------------------------
-- Buyer Code Change Log
-- Source: ecoatm_buyermanagement$buyercodechangelog +
--         ecoatm_buyermanagement$buyercodechangelog_buyercode
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.buyer_code_change_logs (
    id                  BIGINT        PRIMARY KEY,
    buyer_code_id       BIGINT        REFERENCES buyer_mgmt.buyer_codes(id),
    old_buyer_code_type VARCHAR(200),
    new_buyer_code_type VARCHAR(200),
    edited_by           VARCHAR(200),  -- email of editor at time of change
    edited_on           TIMESTAMP,
    created_date        TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date        TIMESTAMP     NOT NULL DEFAULT NOW(),
    owner_id            BIGINT        REFERENCES identity.users(id),
    changed_by_id       BIGINT        REFERENCES identity.users(id)
);

COMMENT ON TABLE buyer_mgmt.buyer_code_change_logs IS 'Audit log of buyer code type changes (ecoatm_buyermanagement$buyercodechangelog + _buyercode junction collapsed)';

-- ---------------------------------------------------------------------------
-- Auctions Feature Flags (singleton configuration)
-- Source: ecoatm_buyermanagement$auctionsfeature
-- ---------------------------------------------------------------------------
CREATE TABLE buyer_mgmt.auctions_feature_config (
    id                                  BIGINT    PRIMARY KEY,
    auction_round2_minutes_offset       INTEGER   NOT NULL DEFAULT 360,
    auction_round3_minutes_offset       INTEGER   NOT NULL DEFAULT 180,
    send_buyer_to_snowflake             BOOLEAN   NOT NULL DEFAULT true,
    send_bid_data_to_snowflake          BOOLEAN   NOT NULL DEFAULT true,
    send_auction_data_to_snowflake      BOOLEAN   NOT NULL DEFAULT true,
    send_bid_ranking_to_snowflake       BOOLEAN   NOT NULL DEFAULT false,
    create_excel_bid_export             BOOLEAN   NOT NULL DEFAULT true,
    generate_round3_files               BOOLEAN   NOT NULL DEFAULT true,
    calculate_round2_buyer_participation BOOLEAN  NOT NULL DEFAULT true,
    send_files_to_sharepoint_on_submit  BOOLEAN   NOT NULL DEFAULT true,
    sp_retry_count                      INTEGER   NOT NULL DEFAULT 3,
    round2_criteria_active              BOOLEAN   NOT NULL DEFAULT false,
    minimum_allowed_bid                 NUMERIC(12,8) NOT NULL DEFAULT 2.00,
    legacy_auction_dashboard_active     BOOLEAN   NOT NULL DEFAULT false,
    require_wholesale_user_agreement    BOOLEAN   NOT NULL DEFAULT false,
    created_date                        TIMESTAMP NOT NULL DEFAULT NOW(),
    changed_date                        TIMESTAMP NOT NULL DEFAULT NOW(),
    owner_id                            BIGINT    REFERENCES identity.users(id),
    changed_by_id                       BIGINT    REFERENCES identity.users(id)
);

COMMENT ON TABLE  buyer_mgmt.auctions_feature_config IS 'Singleton feature flag and configuration table for auction behavior (ecoatm_buyermanagement$auctionsfeature)';
COMMENT ON COLUMN buyer_mgmt.auctions_feature_config.minimum_allowed_bid IS 'Minimum bid value in USD; enforced during bid submission validation';

-- Indexes
CREATE INDEX idx_buyers_status        ON buyer_mgmt.buyers(status);
CREATE INDEX idx_buyers_company       ON buyer_mgmt.buyers(company_name);
CREATE INDEX idx_buyer_codes_code     ON buyer_mgmt.buyer_codes(code);
CREATE INDEX idx_buyer_codes_type     ON buyer_mgmt.buyer_codes(buyer_code_type);
CREATE INDEX idx_buyer_codes_status   ON buyer_mgmt.buyer_codes(status);
CREATE INDEX idx_buyer_codes_softdel  ON buyer_mgmt.buyer_codes(soft_delete) WHERE soft_delete = false;
