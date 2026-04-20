-- =============================================================================
-- V64: Tighten deferred FK constraints on buyer_mgmt.qbc_* tables
--
-- V9 created qbc_scheduling_auctions, qbc_bid_rounds, and
-- qbc_query_helper_auctions with "dangling" BIGINT columns — no FK constraint
-- because auctionui$* was not yet migrated. Now that V58–V62 have created the
-- auctions schema and tables, we can add the real constraints.
--
-- IMPORTANT ordering: this migration must run AFTER the extractor script has
-- populated auctions.auctions, auctions.scheduling_auctions, and
-- auctions.bid_rounds with the remapped IDs referenced by the existing V23
-- qbc_* rows. If Flyway is the gate, ensure the extractor runs before
-- Flyway reaches V64.
--
-- Strategy: add constraints as NOT VALID so existing V23 qbc_* rows (which
-- reference Mendix IDs that have not yet been remapped into the new
-- auctions.* id space) are NOT checked at migration time. New inserts and
-- updates will be enforced. A follow-up migration (run after the extractor
-- repopulates auctions.* and remaps qbc_* FK columns) will VALIDATE each
-- constraint to promote it to a fully enforced state.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- qbc_scheduling_auctions.scheduling_auction_id → auctions.scheduling_auctions(id)
-- ---------------------------------------------------------------------------
ALTER TABLE buyer_mgmt.qbc_scheduling_auctions
    ADD CONSTRAINT fk_qbc_sa_scheduling_auction
    FOREIGN KEY (scheduling_auction_id)
    REFERENCES auctions.scheduling_auctions(id)
    ON DELETE CASCADE
    NOT VALID;

CREATE INDEX IF NOT EXISTS idx_qbc_sa_scheduling_auction
    ON buyer_mgmt.qbc_scheduling_auctions(scheduling_auction_id);

-- ---------------------------------------------------------------------------
-- qbc_bid_rounds.bid_round_id → auctions.bid_rounds(id)
-- ---------------------------------------------------------------------------
ALTER TABLE buyer_mgmt.qbc_bid_rounds
    ADD CONSTRAINT fk_qbc_br_bid_round
    FOREIGN KEY (bid_round_id)
    REFERENCES auctions.bid_rounds(id)
    ON DELETE CASCADE
    NOT VALID;

CREATE INDEX IF NOT EXISTS idx_qbc_br_bid_round
    ON buyer_mgmt.qbc_bid_rounds(bid_round_id);

-- ---------------------------------------------------------------------------
-- qbc_query_helper_auctions.auction_id → auctions.auctions(id)
-- ---------------------------------------------------------------------------
ALTER TABLE buyer_mgmt.qbc_query_helper_auctions
    ADD CONSTRAINT fk_qbc_qh_auction
    FOREIGN KEY (auction_id)
    REFERENCES auctions.auctions(id)
    ON DELETE CASCADE
    NOT VALID;

CREATE INDEX IF NOT EXISTS idx_qbc_qh_auction
    ON buyer_mgmt.qbc_query_helper_auctions(auction_id);

COMMENT ON CONSTRAINT fk_qbc_sa_scheduling_auction ON buyer_mgmt.qbc_scheduling_auctions IS 'Deferred FK from V9 — tightened in V64 after auctions module migration';
COMMENT ON CONSTRAINT fk_qbc_br_bid_round           ON buyer_mgmt.qbc_bid_rounds           IS 'Deferred FK from V9 — tightened in V64 after auctions module migration';
COMMENT ON CONSTRAINT fk_qbc_qh_auction             ON buyer_mgmt.qbc_query_helper_auctions IS 'Deferred FK from V9 — tightened in V64 after auctions module migration';
