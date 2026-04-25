-- =============================================================================
-- V78: auctions — purchase_order + po_detail (PurchaseOrder module)
-- Source: ecoatm_po$purchaseorder (13 rows), ecoatm_po$podetail (9,895).
-- Drops: ecoatm_po$weeklypo (12,384 rows — fulfillment tracker, 4C unused),
--        ecoatm_po$weekperiod (54 — derivable from week_from/to),
--        ecoatm_po$purchaseorderdoc (82 — file blobs, modern streams),
--        ecoatm_po$pohelper (21 — Mendix client-side UX scratch).
-- Junctions collapsed to direct FK: purchaseorder_week_from/to → week_from_id /
--        week_to_id; podetail_buyercode → buyer_code_id;
--        podetail_purchaseorder → purchase_order_id.
-- Design: docs/tasks/auction-po-module-design.md
-- =============================================================================

CREATE TABLE auctions.purchase_order (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    week_from_id            BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_to_id              BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_range_label        VARCHAR(200)    NOT NULL,
    valid_year_week         BOOLEAN         NOT NULL DEFAULT TRUE,
    total_records           INTEGER         NOT NULL DEFAULT 0,
    po_refresh_timestamp    TIMESTAMPTZ,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id)
);

CREATE INDEX idx_po_week_from    ON auctions.purchase_order(week_from_id);
CREATE INDEX idx_po_week_to      ON auctions.purchase_order(week_to_id);
CREATE INDEX idx_po_changed_date ON auctions.purchase_order(changed_date DESC);

CREATE TABLE auctions.po_detail (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    purchase_order_id       BIGINT          NOT NULL
                                            REFERENCES auctions.purchase_order(id)
                                            ON DELETE CASCADE,
    buyer_code_id           BIGINT          NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    product_id              VARCHAR(100)    NOT NULL,
    grade                   VARCHAR(200)    NOT NULL,
    model_name              VARCHAR(200),
    price                   NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    qty_cap                 INTEGER,
    price_fulfilled         NUMERIC(14, 4),
    qty_fulfilled           INTEGER,
    temp_buyer_code         VARCHAR(200),
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_po_detail UNIQUE (purchase_order_id, product_id, grade, buyer_code_id)
);

CREATE INDEX idx_pod_po             ON auctions.po_detail(purchase_order_id);
CREATE INDEX idx_pod_buyer_code     ON auctions.po_detail(buyer_code_id);
CREATE INDEX idx_pod_product_grade  ON auctions.po_detail(product_id, grade);
