-- =============================================================================
-- V74: P8 Admin Surfaces — audit tables for Lane 3A (Bid Data) and Lane 3B
--      (Qualified Buyer Codes).
--
-- Why two new tables instead of reusing buyer_mgmt.buyer_code_change_logs?
-- That existing table only tracks buyer_code_type changes (old/new strings)
-- and has no notion of either bid_data deletes or QBC included flips. We
-- mirror the auctions.reserve_bid_audit pattern (referenced by the EB module
-- design) so admin DELETE / PATCH actions remain traceable per-row.
--
-- Soft-delete strategy for bid_data: the existing is_deprecated column on
-- auctions.bid_data is the soft-delete flag. The DELETE endpoint sets it to
-- TRUE rather than removing the row, which preserves the historical bid for
-- audit / replay. The audit row records who flipped the flag and when.
-- =============================================================================

CREATE TABLE auctions.bid_data_audit (
    id                       BIGSERIAL       PRIMARY KEY,
    bid_data_id              BIGINT          NOT NULL
                                            REFERENCES auctions.bid_data(id)
                                            ON DELETE CASCADE,
    action                   VARCHAR(20)     NOT NULL,
    -- Snapshot of pre-action values so the audit is self-contained even if
    -- the bid_data row is later hard-deleted by a cleanup job.
    bid_round_id             BIGINT,
    buyer_code_id            BIGINT,
    bid_amount               NUMERIC(14, 4),
    bid_quantity             INTEGER,
    submitted_bid_amount     NUMERIC(14, 4),
    submitted_bid_quantity   INTEGER,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE  auctions.bid_data_audit IS 'Audit trail for admin actions on auctions.bid_data (P8 Lane 3A).';
COMMENT ON COLUMN auctions.bid_data_audit.action IS 'One of: SOFT_DELETE, RESTORE.';

CREATE INDEX idx_bda_bid_data    ON auctions.bid_data_audit(bid_data_id);
CREATE INDEX idx_bda_created     ON auctions.bid_data_audit(created_date DESC);

CREATE TABLE buyer_mgmt.qualified_buyer_code_audit (
    id                          BIGSERIAL       PRIMARY KEY,
    qualified_buyer_code_id     BIGINT          NOT NULL
                                                REFERENCES buyer_mgmt.qualified_buyer_codes(id)
                                                ON DELETE CASCADE,
    scheduling_auction_id       BIGINT,
    buyer_code_id               BIGINT,
    old_included                BOOLEAN,
    new_included                BOOLEAN,
    old_qualification_type      VARCHAR(20),
    new_qualification_type      VARCHAR(20),
    created_date                TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_by_id               BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE  buyer_mgmt.qualified_buyer_code_audit IS 'Audit trail for admin manual qualify/unqualify on QBCs (P8 Lane 3B).';

CREATE INDEX idx_qbca_qbc        ON buyer_mgmt.qualified_buyer_code_audit(qualified_buyer_code_id);
CREATE INDEX idx_qbca_created    ON buyer_mgmt.qualified_buyer_code_audit(created_date DESC);
