-- V83: 5 — R2 Buyer Assignment — status flags
-- Additive only.

ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r2_init_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r2_init_error         TEXT,
    ADD COLUMN r2_init_started_at    TIMESTAMPTZ,
    ADD COLUMN r2_init_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r2_init_status
        CHECK (r2_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_status IS
    '5: PENDING (round not yet started) | RUNNING | SUCCESS | FAILED | SKIPPED (config gate FALSE)';
COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_error IS
    '5: exception class + message (truncated to 4000 chars) on FAILED';
