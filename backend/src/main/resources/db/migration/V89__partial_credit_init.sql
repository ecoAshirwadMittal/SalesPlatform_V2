-- V89: Partial Credit Requests module — Sprint 1 foundations.
--
-- See docs/tasks/partial-credit-modern-implementation-plan.md.
--
-- Adds the `partial_credit` schema, 7 tables (header + 3 line kinds +
-- photos + uploads + status config), 5 status seed rows, and 4 module
-- role rows. No data dependencies on existing PWS/order rows — this
-- feature pulls live order data from Snowflake at submit time and
-- denormalises the manifest fields onto the line entities.

CREATE SCHEMA IF NOT EXISTS partial_credit;

-- ---------------------------------------------------------------------
-- Status config — parallels RMA's status table. Seeded with 5 system
-- statuses; SystemStatus is the enum constant the application checks
-- against, while internal/external text and color are admin-editable.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.credit_request_statuses (
    id                    BIGSERIAL PRIMARY KEY,
    system_status         VARCHAR(40) NOT NULL UNIQUE,
    internal_status_text  VARCHAR(100) NOT NULL,
    external_status_text  VARCHAR(100) NOT NULL,
    color_hex             VARCHAR(7)  NOT NULL DEFAULT '#888888',
    sort_order            INTEGER     NOT NULL DEFAULT 0,
    show_in_user_counters BOOLEAN     NOT NULL DEFAULT TRUE,
    is_default            BOOLEAN     NOT NULL DEFAULT FALSE,
    status_grouped_to     VARCHAR(40),
    CONSTRAINT chk_crs_system_status CHECK (system_status IN (
        'DRAFT', 'PENDING_APPROVAL', 'UNDER_REVIEW', 'APPROVED', 'DECLINED'))
);

INSERT INTO partial_credit.credit_request_statuses
    (system_status, internal_status_text, external_status_text, color_hex, sort_order, show_in_user_counters, is_default) VALUES
    ('DRAFT',            'Draft',             'Draft',             '#888888', 0, FALSE, TRUE),
    ('PENDING_APPROVAL', 'Pending Approval',  'Pending Approval',  '#D08214', 1, TRUE,  FALSE),
    ('UNDER_REVIEW',     'Under Review',      'Pending Approval',  '#407874', 2, TRUE,  FALSE),
    ('APPROVED',         'Approved',          'Approved',          '#14AC36', 3, TRUE,  FALSE),
    ('DECLINED',         'Declined',          'Declined',          '#B3261E', 4, TRUE,  FALSE);

-- ---------------------------------------------------------------------
-- Header — one row per credit request. Order/buyer metadata is captured
-- (denormalised) at submit time so the row is self-contained even if
-- Snowflake changes. The three has_* booleans encode which reasons the
-- buyer flagged; line tables hang off this row.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.credit_requests (
    id                  BIGSERIAL PRIMARY KEY,
    request_number      VARCHAR(40)  NOT NULL UNIQUE,
    request_date        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    submitted_date      TIMESTAMPTZ,
    status_id           BIGINT       NOT NULL REFERENCES partial_credit.credit_request_statuses(id),
    -- Denormalised order metadata (captured from Snowflake VW_SALE_ORDER_SHIPMENT)
    order_number        VARCHAR(200) NOT NULL,
    party_name          VARCHAR(200),
    order_created_date  TIMESTAMPTZ,
    order_shipped_date  TIMESTAMPTZ,
    -- Reason flags
    has_missing_device      BOOLEAN NOT NULL DEFAULT FALSE,
    has_wrong_device        BOOLEAN NOT NULL DEFAULT FALSE,
    has_encumbered_device   BOOLEAN NOT NULL DEFAULT FALSE,
    -- Damage Q&A
    shipment_damaged    VARCHAR(20) NOT NULL DEFAULT 'NOT_ANSWERED'
        CONSTRAINT chk_cr_shipment_damaged CHECK (shipment_damaged IN ('YES','NO','NOT_ANSWERED')),
    -- Computed totals (cached for list display; recomputed on line edits)
    total_devices      INTEGER NOT NULL DEFAULT 0,
    requested_skus     INTEGER NOT NULL DEFAULT 0,
    requested_qty      INTEGER NOT NULL DEFAULT 0,
    requested_total    NUMERIC(14, 2) NOT NULL DEFAULT 0,
    approved_skus      INTEGER NOT NULL DEFAULT 0,
    approved_qty       INTEGER NOT NULL DEFAULT 0,
    approved_total     NUMERIC(14, 2) NOT NULL DEFAULT 0,
    -- Review tracking
    reviewed_by_id     BIGINT REFERENCES identity.users(id),
    review_completed_on TIMESTAMPTZ,
    -- Submission tracking
    submitted_by_id    BIGINT REFERENCES identity.users(id),
    -- Buyer linkage
    buyer_code_id      BIGINT NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    buyer_id           BIGINT REFERENCES buyer_mgmt.buyers(id),
    -- Standard audit
    created_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id      BIGINT REFERENCES identity.users(id),
    changed_by_id      BIGINT REFERENCES identity.users(id)
    -- "At least one reason" is enforced in the service (CreditRequestValidator).
    -- A CHECK constraint here would need a subquery to allow DRAFT rows with
    -- no reasons selected yet; Postgres prohibits subqueries in CHECK.
);

CREATE INDEX idx_cr_status_request_date    ON partial_credit.credit_requests (status_id, request_date DESC);
CREATE INDEX idx_cr_buyer_code             ON partial_credit.credit_requests (buyer_code_id, status_id);
CREATE INDEX idx_cr_order_number           ON partial_credit.credit_requests (order_number);

-- ---------------------------------------------------------------------
-- Missing-device line — one row per barcode the buyer reported as
-- missing. Brand/Model/Grade/etc are denormalised from Snowflake at
-- submit time so the line is self-contained.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.missing_device_lines (
    id                  BIGSERIAL PRIMARY KEY,
    credit_request_id   BIGINT NOT NULL REFERENCES partial_credit.credit_requests(id) ON DELETE CASCADE,
    barcode_submitted   VARCHAR(200) NOT NULL,
    line_status         VARCHAR(20) NOT NULL DEFAULT 'VALID'
        CONSTRAINT chk_mdl_line_status CHECK (line_status IN ('VALID','DUPLICATE','NOT_IN_ORDER')),
    brand               VARCHAR(100),
    model               VARCHAR(200),
    grade               VARCHAR(40),
    box_number          VARCHAR(100),
    amount_paid         NUMERIC(14, 2),
    -- Manifest cross-check (Phase 1 reviewer can see ship status from Snowflake)
    ship_status         VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN'
        CONSTRAINT chk_mdl_ship_status CHECK (ship_status IN ('SHIPPED','NOT_SHIPPED','UNKNOWN')),
    -- Review decision
    review_decision     VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CONSTRAINT chk_mdl_review_decision CHECK (review_decision IN ('PENDING','ACCEPTED','DECLINED')),
    amount_to_credit    NUMERIC(14, 2),
    -- Audit
    created_date        TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_date        TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id       BIGINT REFERENCES identity.users(id),
    changed_by_id       BIGINT REFERENCES identity.users(id)
);

CREATE INDEX idx_mdl_request ON partial_credit.missing_device_lines (credit_request_id);

-- ---------------------------------------------------------------------
-- Wrong-device line — buyer reports they received a different device
-- than expected. ExpectedXxx are the manifest's record of what should
-- have shipped; ActualXxx are what the buyer claims they received.
-- latest_price is the live aggregate of max submitted bid for the
-- received device within the same auction week — computed at
-- review-open and cached here.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.wrong_device_lines (
    id                              BIGSERIAL PRIMARY KEY,
    credit_request_id               BIGINT NOT NULL REFERENCES partial_credit.credit_requests(id) ON DELETE CASCADE,
    -- Expected (from Snowflake manifest)
    expected_barcode                VARCHAR(200) NOT NULL,
    expected_device_description     VARCHAR(400),
    expected_brand                  VARCHAR(100),
    expected_model                  VARCHAR(200),
    expected_grade                  VARCHAR(40),
    expected_box_number             VARCHAR(100),
    expected_amount_paid            NUMERIC(14, 2),
    expected_ecoatm_code            VARCHAR(40),
    expected_week_id                BIGINT REFERENCES mdm.week(id),
    -- Actual (what the buyer reports they received)
    actual_imei_or_model            VARCHAR(200),
    actual_device_description       VARCHAR(400),
    actual_ecoatm_code              VARCHAR(40),
    actual_grade                    VARCHAR(40),
    actual_brand                    VARCHAR(100),
    actual_model                    VARCHAR(200),
    -- Pricing (live aggregate from auctions.bid_data, cached at review-open)
    latest_price                    NUMERIC(14, 4),
    latest_price_computed_on        TIMESTAMPTZ,
    -- Recommendation engine output (per SPKB-3661)
    action_recommendation           VARCHAR(20)
        CONSTRAINT chk_wdl_action_rec CHECK (action_recommendation IN ('ACCEPT','DECLINE') OR action_recommendation IS NULL),
    line_status                     VARCHAR(20) NOT NULL DEFAULT 'VALID'
        CONSTRAINT chk_wdl_line_status CHECK (line_status IN ('VALID','DUPLICATE','NOT_IN_ORDER')),
    review_decision                 VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CONSTRAINT chk_wdl_review_decision CHECK (review_decision IN ('PENDING','ACCEPTED','DECLINED')),
    amount_to_credit                NUMERIC(14, 2),
    -- Audit
    created_date                    TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_date                    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id                   BIGINT REFERENCES identity.users(id),
    changed_by_id                   BIGINT REFERENCES identity.users(id)
);

CREATE INDEX idx_wdl_request ON partial_credit.wrong_device_lines (credit_request_id);

-- ---------------------------------------------------------------------
-- Encumbered-device line — iCloud/MDM-locked devices. Phase 1 has the
-- reviewer enter prolog_result + actual_value manually; Phase 2 will
-- automate the Prolog check and wire RMA auto-creation.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.encumbered_device_lines (
    id                  BIGSERIAL PRIMARY KEY,
    credit_request_id   BIGINT NOT NULL REFERENCES partial_credit.credit_requests(id) ON DELETE CASCADE,
    barcode_submitted   VARCHAR(200) NOT NULL,
    line_status         VARCHAR(20) NOT NULL DEFAULT 'VALID'
        CONSTRAINT chk_edl_line_status CHECK (line_status IN ('VALID','DUPLICATE','NOT_IN_ORDER')),
    brand               VARCHAR(100),
    model               VARCHAR(200),
    grade               VARCHAR(40),
    box_number          VARCHAR(100),
    amount_paid         NUMERIC(14, 2),
    -- Reviewer-entered in Phase 1
    prolog_result       VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CONSTRAINT chk_edl_prolog_result CHECK (prolog_result IN ('ENCUMBERED','NOT_ENCUMBERED','PENDING')),
    actual_value        NUMERIC(14, 2),
    -- Review decision
    review_decision     VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CONSTRAINT chk_edl_review_decision CHECK (review_decision IN ('PENDING','ACCEPTED','DECLINED')),
    amount_to_credit    NUMERIC(14, 2),
    -- Phase 2 placeholder
    rma_id              BIGINT,
    -- Audit
    created_date        TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_date        TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id       BIGINT REFERENCES identity.users(id),
    changed_by_id       BIGINT REFERENCES identity.users(id)
);

CREATE INDEX idx_edl_request ON partial_credit.encumbered_device_lines (credit_request_id);

-- ---------------------------------------------------------------------
-- Photos — uploaded by the buyer either as proof of shipment damage
-- (request-scoped) or as per-line evidence of the wrong device
-- (wrong-device-line-scoped). bytea is fine for Phase 1; if file volume
-- gets unwieldy we externalise to S3 later.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.credit_request_photos (
    id                      BIGSERIAL PRIMARY KEY,
    credit_request_id       BIGINT NOT NULL REFERENCES partial_credit.credit_requests(id) ON DELETE CASCADE,
    wrong_device_line_id    BIGINT REFERENCES partial_credit.wrong_device_lines(id) ON DELETE SET NULL,
    kind                    VARCHAR(20) NOT NULL
        CONSTRAINT chk_crp_kind CHECK (kind IN ('DAMAGE','WRONG_DEVICE')),
    original_filename       VARCHAR(400) NOT NULL,
    content_type            VARCHAR(100) NOT NULL,
    size_bytes              INTEGER NOT NULL,
    blob                    BYTEA NOT NULL,
    -- Audit
    created_date            TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id           BIGINT REFERENCES identity.users(id)
);

CREATE INDEX idx_crp_request ON partial_credit.credit_request_photos (credit_request_id);
CREATE INDEX idx_crp_line    ON partial_credit.credit_request_photos (wrong_device_line_id)
    WHERE wrong_device_line_id IS NOT NULL;

-- ---------------------------------------------------------------------
-- Uploads — the xlsx/csv barcode files the buyer paste-or-uploads
-- during the wizard. Kept verbatim so we can audit what was submitted.
-- ---------------------------------------------------------------------
CREATE TABLE partial_credit.credit_request_uploads (
    id                  BIGSERIAL PRIMARY KEY,
    credit_request_id   BIGINT NOT NULL REFERENCES partial_credit.credit_requests(id) ON DELETE CASCADE,
    kind                VARCHAR(40) NOT NULL
        CONSTRAINT chk_cru_kind CHECK (kind IN ('MISSING_BARCODES','WRONG_DEVICES','ENCUMBERED_BARCODES')),
    original_filename   VARCHAR(400) NOT NULL,
    content_type        VARCHAR(100) NOT NULL,
    size_bytes          INTEGER NOT NULL,
    blob                BYTEA NOT NULL,
    process_status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CONSTRAINT chk_cru_process CHECK (process_status IN ('PENDING','PROCESSED','FAILED')),
    error_report        TEXT,
    -- Audit
    created_date        TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by_id       BIGINT REFERENCES identity.users(id)
);

CREATE INDEX idx_cru_request ON partial_credit.credit_request_uploads (credit_request_id);

-- ---------------------------------------------------------------------
-- Module roles. identity.user_roles uses manually-assigned IDs (no
-- sequence default — same convention as V15__seed_dev_roles_and_users).
-- IDs 1101–1104 stay clear of the existing prod range (1–11) and the
-- dev-seed range (1001–1006).
-- ---------------------------------------------------------------------
INSERT INTO identity.user_roles (id, model_guid, name, description) VALUES
    (1101, 'pc-guid-0001-buyer',    'PartialCredit_Buyer',
        'Wholesale buyer — can submit and view own credit requests'),
    (1102, 'pc-guid-0002-salesrep', 'PartialCredit_SalesRep',
        'Sales rep — can submit on behalf of a buyer and view all requests; cannot approve'),
    (1103, 'pc-guid-0003-salesops', 'PartialCredit_SalesOps',
        'Sales ops reviewer — can view + accept/decline all requests'),
    (1104, 'pc-guid-0004-admin',    'PartialCredit_Admin',
        'Admin — full configuration + reviewer + status-config page')
ON CONFLICT (name) DO NOTHING;
