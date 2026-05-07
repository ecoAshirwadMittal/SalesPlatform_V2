-- Reset R3 lifecycle status columns between controller IT test methods.
-- The seed uses ON CONFLICT DO NOTHING, so prior test state persists unless
-- explicitly reset here. Resets all SA rows seeded by r3-lifecycle-seed.sql.
UPDATE auctions.scheduling_auctions
SET r3_preprocess_status     = 'PENDING',
    r3_preprocess_error      = NULL,
    r3_preprocess_started_at = NULL,
    r3_preprocess_finished_at = NULL,
    r3_init_status           = 'PENDING',
    r3_init_error            = NULL,
    r3_init_started_at       = NULL,
    r3_init_finished_at      = NULL
WHERE id IN (6001, 6002, 6003, 6011, 6012, 6021, 6022, 6023);
