-- V82: 4C — Bid Ranking + Target-Price Recalc — status flags + config
-- Additive only. NOT NULL DEFAULT semantics give every existing row a
-- sane starting state without a separate backfill statement.

ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN ranking_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN ranking_error               TEXT,
    ADD COLUMN ranking_started_at          TIMESTAMPTZ,
    ADD COLUMN ranking_finished_at         TIMESTAMPTZ,
    ADD COLUMN target_price_status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN target_price_error          TEXT,
    ADD COLUMN target_price_started_at     TIMESTAMPTZ,
    ADD COLUMN target_price_finished_at    TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_ranking_status
        CHECK (ranking_status      IN ('PENDING','RUNNING','SUCCESS','FAILED')),
    ADD CONSTRAINT chk_sa_target_price_status
        CHECK (target_price_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.ranking_status IS
    '4C recalc: PENDING (round not yet closed) | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.target_price_status IS
    '4C recalc: PENDING | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.ranking_error IS
    '4C recalc: exception class + message (truncated to 4000 chars) on FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.target_price_error IS
    '4C recalc: exception class + message (truncated to 4000 chars) on FAILED';

ALTER TABLE auctions.bid_ranking_config
    ADD COLUMN include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN auctions.bid_ranking_config.include_reserve_floor IS
    '4C: TRUE -> reserve_bid rows participate in DENSE_RANK as priority bidders; FALSE -> ranking is bid_data only';
