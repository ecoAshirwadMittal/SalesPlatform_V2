-- V33: RMA (Return Merchandise Authorization) module tables
-- Source: ecoatm_rma schema from Mendix (migration_context/database/schema-ecoatm_rma.md)

-- ============================================================
-- 1. rma_status — lookup table for RMA workflow statuses
-- ============================================================
CREATE TABLE IF NOT EXISTS pws.rma_status (
    id                           BIGSERIAL PRIMARY KEY,
    system_status                VARCHAR(50)  NOT NULL UNIQUE,
    internal_status_text         VARCHAR(200),
    external_status_text         VARCHAR(200),
    internal_status_hex_code     VARCHAR(33),
    external_status_hex_code     VARCHAR(33),
    sales_status_header_hex_code VARCHAR(33),
    sales_table_hover_hex_code   VARCHAR(33),
    status_grouped_to            VARCHAR(50),
    sort_order                   INTEGER,
    is_default                   BOOLEAN DEFAULT FALSE,
    description                  TEXT,
    status_verbiage_bidder       TEXT,
    created_date                 TIMESTAMP DEFAULT NOW(),
    updated_date                 TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- 2. rma_reason — valid return reasons (managed by admin)
-- ============================================================
CREATE TABLE IF NOT EXISTS pws.rma_reason (
    id             BIGSERIAL PRIMARY KEY,
    valid_reasons  VARCHAR(200) NOT NULL,
    is_active      BOOLEAN DEFAULT TRUE,
    created_date   TIMESTAMP DEFAULT NOW(),
    updated_date   TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- 3. rma — main RMA request header
-- ============================================================
CREATE TABLE IF NOT EXISTS pws.rma (
    id                    BIGSERIAL PRIMARY KEY,
    number                VARCHAR(100),
    buyer_code_id         BIGINT REFERENCES buyer_mgmt.buyer_codes(id),
    submitted_by_user_id  BIGINT REFERENCES identity.users(id),
    reviewed_by_user_id   BIGINT REFERENCES identity.users(id),
    rma_status_id         BIGINT REFERENCES pws.rma_status(id),
    request_skus          INTEGER DEFAULT 0,
    request_qty           INTEGER DEFAULT 0,
    request_sales_total   INTEGER DEFAULT 0,
    approved_skus         INTEGER DEFAULT 0,
    approved_qty          INTEGER DEFAULT 0,
    approved_sales_total  INTEGER DEFAULT 0,
    approved_count        INTEGER DEFAULT 0,
    declined_count        INTEGER DEFAULT 0,
    submitted_date        TIMESTAMP,
    approval_date         TIMESTAMP,
    review_completed_on   TIMESTAMP,
    system_status         VARCHAR(200),
    oracle_rma_status     TEXT,
    oracle_number         VARCHAR(200),
    oracle_id             VARCHAR(200),
    oracle_http_code      INTEGER,
    is_successful         BOOLEAN,
    all_rma_items_valid   BOOLEAN DEFAULT TRUE,
    json_content          TEXT,
    oracle_json_response  TEXT,
    entity_owner          VARCHAR(200),
    entity_changer        VARCHAR(200),
    created_date          TIMESTAMP DEFAULT NOW(),
    updated_date          TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_rma_buyer_code ON pws.rma(buyer_code_id);
CREATE INDEX idx_rma_status ON pws.rma(rma_status_id);
CREATE INDEX idx_rma_submitted_by ON pws.rma(submitted_by_user_id);

-- ============================================================
-- 4. rma_item — individual line items within an RMA
-- ============================================================
CREATE TABLE IF NOT EXISTS pws.rma_item (
    id              BIGSERIAL PRIMARY KEY,
    rma_id          BIGINT NOT NULL REFERENCES pws.rma(id) ON DELETE CASCADE,
    device_id       BIGINT REFERENCES mdm.device(id),
    order_id        BIGINT REFERENCES pws."order"(id),
    imei            VARCHAR(200),
    order_number    VARCHAR(200),
    ship_date       TIMESTAMP,
    sale_price      INTEGER,
    return_reason   TEXT,
    status          VARCHAR(10),
    status_display  VARCHAR(200),
    decline_reason  TEXT,
    entity_owner    VARCHAR(200),
    entity_changer  VARCHAR(200),
    created_date    TIMESTAMP DEFAULT NOW(),
    updated_date    TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_rma_item_rma ON pws.rma_item(rma_id);
CREATE INDEX idx_rma_item_device ON pws.rma_item(device_id);
CREATE INDEX idx_rma_item_order ON pws.rma_item(order_id);

-- ============================================================
-- 5. Seed data: rma_status (9 statuses from production)
-- ============================================================
INSERT INTO pws.rma_status (system_status, internal_status_text, external_status_text, status_grouped_to, is_default, sort_order) VALUES
    ('Submitted',       'Pending Approval', 'Submitted',        'Pending_Approval', TRUE,  1),
    ('Approved',        'Approved',         'Approved to Ship', 'Open',             FALSE, 2),
    ('New',             'Approved to Ship', 'Approved to Ship', 'Open',             FALSE, 3),
    ('Hold',            'Approved to Ship', 'Approved to Ship', 'Open',             FALSE, 4),
    ('Receiving',       'Approved to Ship', 'Approved to Ship', 'Open',             FALSE, 5),
    ('Partial Receipt', 'Approved to Ship', 'Approved to Ship', 'Open',             FALSE, 6),
    ('Received',        'Received',         'Received',         'Closed',           FALSE, 7),
    ('Declined',        'Declined',         'Declined',         'Declined',         FALSE, 8),
    ('Canceled',        'Canceled',         'Canceled',         'Closed',           FALSE, 9);

-- ============================================================
-- 6. Seed data: rma_reason (15 active reasons from production)
-- ============================================================
INSERT INTO pws.rma_reason (valid_reasons, is_active) VALUES
    ('FMIP/ MDM Locked/ BL / Chimera/ FRP',        TRUE),
    ('Cracked Screen',                               TRUE),
    ('Gaps/ Parts Fit/ Screen Lift/ etc.',           TRUE),
    ('Physically Damaged',                            TRUE),
    ('Defective Battery/ Lower 69%',                 TRUE),
    ('Activation/ software Issues/ Face ID',         TRUE),
    ('Android/ Google / Samsung Locked',             TRUE),
    ('Defective Charger/ Data Port',                 TRUE),
    ('No Power',                                      TRUE),
    ('Finance Locked',                                TRUE),
    ('Defective Camera',                              TRUE),
    ('Defective Display',                             TRUE),
    ('Will not detect SIM/ SD card',                 TRUE),
    ('Wrong Make/ Model/ Capacity/ Carrier/ IMEI',   TRUE),
    ('Defective Microphone/ Speaker/ Volume Keys',   TRUE);
