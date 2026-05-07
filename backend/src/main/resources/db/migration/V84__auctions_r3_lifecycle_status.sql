-- V84: Sub-project 6 — R3 init + pre-process — schema additions
-- Additive only. R2 column semantics-update is comment-only (no data change).

-- ─── R3 selection-criteria knobs on bid_round_selection_filters ──────────
ALTER TABLE auctions.bid_round_selection_filters
    ADD COLUMN bid_percentage_variation  NUMERIC(10, 4),
    ADD COLUMN bid_amount_variation      NUMERIC(14, 2),
    ADD COLUMN rank_qualification_limit  INTEGER;

COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_percentage_variation IS
    '6: R3 qualification — whole-percent threshold (5 = 5%). Branch active when NOT NULL: latest_bid >= round3_target_price - (round3_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_amount_variation IS
    '6: R3 qualification — flat amount. Branch active when NOT NULL: latest_bid >= round3_target_price - amount.';
COMMENT ON COLUMN auctions.bid_round_selection_filters.rank_qualification_limit IS
    '6: R3 qualification — rank ceiling. Branch active when NOT NULL: round3_bid_rank <= limit. All three branches NULL → qualify everyone.';

-- ─── R2 convention re-alignment to whole-percent (comment only) ──────────
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_percent IS
    '6: R2 qualification — whole-percent threshold (5 = 5%). Was treated as decimal in V59/sub-project 5; sub-project 6 normalises to whole-percent across both rounds. Formula: bid >= avg_target_price - (avg_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_value IS
    'R2 qualification — flat amount threshold (mirrors Mendix bidamountvariation semantics for R3).';

-- ─── R3 lifecycle status columns on scheduling_auctions ──────────────────
ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r3_preprocess_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_preprocess_error         TEXT,
    ADD COLUMN r3_preprocess_started_at    TIMESTAMPTZ,
    ADD COLUMN r3_preprocess_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_preprocess_status
        CHECK (r3_preprocess_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED')),

    ADD COLUMN r3_init_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_init_error               TEXT,
    ADD COLUMN r3_init_started_at          TIMESTAMPTZ,
    ADD COLUMN r3_init_finished_at         TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_init_status
        CHECK (r3_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED | SKIPPED (R3 SA exists with has_round=false). Lives on the R3 SA row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED. Init refuses to flip to SUCCESS unless r3_preprocess_status = SUCCESS on the same row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED, including the "predecessor not SUCCESS" guard message.';
