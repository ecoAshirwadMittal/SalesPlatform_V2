-- =============================================================================
-- V72: Flatten QBC junction tables into direct FK columns on
--      buyer_mgmt.qualified_buyer_codes.
--
-- Rationale: The Mendix source modeled SchedulingAuction ↔ QBC and
-- BuyerCode ↔ QBC as M:N associations. In reality each QBC row belongs to
-- exactly one SchedulingAuction and one BuyerCode; the junctions are
-- redundant. Flattening enables one-hop delete on SA rewrite
-- (SUB_ClearQualifiedBuyerList parity) and clean uniqueness enforcement.
--
-- Not dropped: buyer_mgmt.qbc_bid_rounds — QBC participates in state
-- tracking across multiple bid rounds, which is a legitimate M:N.
-- =============================================================================

ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ADD COLUMN scheduling_auction_id BIGINT,
    ADD COLUMN buyer_code_id         BIGINT;

-- Backfill direct FKs from V9 junction tables (V23 seed populated them).
UPDATE buyer_mgmt.qualified_buyer_codes qbc
   SET scheduling_auction_id = qsa.scheduling_auction_id
  FROM buyer_mgmt.qbc_scheduling_auctions qsa
 WHERE qsa.qualified_buyer_code_id = qbc.id;

UPDATE buyer_mgmt.qualified_buyer_codes qbc
   SET buyer_code_id = qbcbc.buyer_code_id
  FROM buyer_mgmt.qbc_buyer_codes qbcbc
 WHERE qbcbc.qualified_buyer_code_id = qbc.id;

-- Any QBC row without a SchedulingAuction or BuyerCode was orphaned in
-- the Mendix source. Drop those before enforcing NOT NULL.
--
-- Additionally, the V23-seeded qbc_scheduling_auctions junction carries
-- raw Mendix IDs that were never remapped into the local
-- auctions.scheduling_auctions id space (V64 added the FK with NOT VALID
-- exactly for this reason). Those rows are functionally orphaned too —
-- their scheduling_auction_id points at a non-existent parent — so drop
-- them before enforcing the FK.
DELETE FROM buyer_mgmt.qualified_buyer_codes
 WHERE scheduling_auction_id IS NULL
    OR buyer_code_id IS NULL
    OR scheduling_auction_id NOT IN (SELECT id FROM auctions.scheduling_auctions)
    OR buyer_code_id         NOT IN (SELECT id FROM buyer_mgmt.buyer_codes);

ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ALTER COLUMN scheduling_auction_id SET NOT NULL,
    ALTER COLUMN buyer_code_id         SET NOT NULL,
    ADD CONSTRAINT fk_qbc_scheduling_auction
        FOREIGN KEY (scheduling_auction_id)
        REFERENCES auctions.scheduling_auctions(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_qbc_buyer_code
        FOREIGN KEY (buyer_code_id)
        REFERENCES buyer_mgmt.buyer_codes(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_qbc_sa_bc UNIQUE (scheduling_auction_id, buyer_code_id);

CREATE INDEX idx_qbc_sa ON buyer_mgmt.qualified_buyer_codes(scheduling_auction_id);
CREATE INDEX idx_qbc_bc ON buyer_mgmt.qualified_buyer_codes(buyer_code_id);

-- Drop redundant junctions (data preserved on QBC row directly).
DROP TABLE buyer_mgmt.qbc_scheduling_auctions;
DROP TABLE buyer_mgmt.qbc_buyer_codes;

-- Attach id sequence (V9 declared BIGINT PRIMARY KEY without IDENTITY).
CREATE SEQUENCE IF NOT EXISTS buyer_mgmt.qualified_buyer_codes_id_seq;
SELECT setval(
  'buyer_mgmt.qualified_buyer_codes_id_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM buyer_mgmt.qualified_buyer_codes), 0), 1)
);
ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ALTER COLUMN id SET DEFAULT nextval('buyer_mgmt.qualified_buyer_codes_id_seq');
ALTER SEQUENCE buyer_mgmt.qualified_buyer_codes_id_seq
    OWNED BY buyer_mgmt.qualified_buyer_codes.id;

COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.scheduling_auction_id IS
  'Flattened from V9 qbc_scheduling_auctions junction (V72). One QBC belongs to exactly one SchedulingAuction.';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.buyer_code_id IS
  'Flattened from V9 qbc_buyer_codes junction (V72). One QBC represents exactly one BuyerCode.';
