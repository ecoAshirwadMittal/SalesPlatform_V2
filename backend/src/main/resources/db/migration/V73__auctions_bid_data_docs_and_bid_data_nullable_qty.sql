-- V73: auctions.bid_data_docs + bid_data alterations for bidder dashboard.
-- Source: Mendix ACT_BidDataDoc_GetOrCreate + ACT_CreateBidData.

CREATE TABLE auctions.bid_data_docs (
    id                BIGSERIAL      PRIMARY KEY,
    legacy_id         BIGINT         UNIQUE,
    user_id           BIGINT         NOT NULL REFERENCES identity.users(id),
    buyer_code_id     BIGINT         NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    week_id           BIGINT         NOT NULL REFERENCES mdm.week(id),
    file_name         VARCHAR(500),
    file_ref          VARCHAR(1000),
    file_size         BIGINT,
    content_type      VARCHAR(200),
    uploaded_datetime TIMESTAMPTZ,
    created_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    changed_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_bdd_user_buyer_week UNIQUE (user_id, buyer_code_id, week_id)
);

CREATE INDEX idx_bdd_user_week ON auctions.bid_data_docs(user_id, week_id);

COMMENT ON TABLE auctions.bid_data_docs IS
  'Per-(user,buyer_code,week) document slot for bidder dashboard. File fields stay null until CSV upload ships.';

ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP DEFAULT;
ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP NOT NULL;

ALTER TABLE auctions.bid_data
    ADD COLUMN bid_data_doc_id BIGINT REFERENCES auctions.bid_data_docs(id) ON DELETE SET NULL;

CREATE INDEX idx_bd_doc ON auctions.bid_data(bid_data_doc_id);

COMMENT ON COLUMN auctions.bid_data.bid_quantity IS
  'Buyer-entered bid quantity. NULL = no cap (accept any); 0 = decline; N = max N units.';
COMMENT ON COLUMN auctions.bid_data.bid_data_doc_id IS
  'Document slot shared across rows for this (user, buyer_code, week). NULL when no doc created.';
