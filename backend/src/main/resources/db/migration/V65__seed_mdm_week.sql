-- =============================================================================
-- V65 — Seed mdm.week with ISO weeks from 2020-01-01 through 2030-12-31
--
-- Generated programmatically (not ported from Mendix) so local dev and fresh
-- environments have a populated weekly calendar for the auctions module.
--
-- Conventions:
--   • ISO-8601 week numbering (Monday start, week 1 = first week with Thursday)
--   • week_id business key = (ISO-year * 100) + ISO-week (e.g., 202401)
--   • week_start_datetime = Monday 00:00:00 UTC
--   • week_end_datetime   = Sunday 23:59:59 UTC
--   • week_display        = "YYYY / WkNN"
--   • Idempotent via UNIQUE (week_id) — safe to re-run
-- =============================================================================

-- Ensure week_id is unique so ON CONFLICT has a target and duplicates can't sneak in
ALTER TABLE mdm.week
    ADD CONSTRAINT uq_mdm_week_week_id UNIQUE (week_id);

INSERT INTO mdm.week (
    week_id,
    year,
    week_number,
    week_start_datetime,
    week_end_datetime,
    week_display,
    week_display_short,
    week_number_string,
    auction_data_purged,
    created_date,
    changed_date
)
SELECT
    (EXTRACT(ISOYEAR FROM monday)::BIGINT * 100)
        + EXTRACT(WEEK    FROM monday)::BIGINT                         AS week_id,
    EXTRACT(ISOYEAR FROM monday)::INTEGER                              AS year,
    EXTRACT(WEEK    FROM monday)::INTEGER                              AS week_number,
    monday                                                             AS week_start_datetime,
    monday + INTERVAL '6 days 23 hours 59 minutes 59 seconds'          AS week_end_datetime,
    EXTRACT(ISOYEAR FROM monday)::TEXT
        || ' / Wk'
        || LPAD(EXTRACT(WEEK FROM monday)::TEXT, 2, '0')               AS week_display,
    'Wk' || LPAD(EXTRACT(WEEK FROM monday)::TEXT, 2, '0')              AS week_display_short,
    LPAD(EXTRACT(WEEK FROM monday)::TEXT, 2, '0')                      AS week_number_string,
    false                                                              AS auction_data_purged,
    NOW()                                                              AS created_date,
    NOW()                                                              AS changed_date
FROM (
    SELECT date_trunc('week', d)::TIMESTAMPTZ AS monday
    FROM generate_series(
        DATE '2020-01-01',
        DATE '2030-12-31',
        INTERVAL '1 week'
    ) AS d
    GROUP BY date_trunc('week', d)
) AS weeks
ON CONFLICT (week_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- Tighten NOT NULL constraints now that the calendar is backfilled.
-- Safe because the INSERT above populates every one of these columns for
-- every row, and mdm.week has no other writers prior to V65.
-- ---------------------------------------------------------------------------
ALTER TABLE mdm.week
    ALTER COLUMN week_id             SET NOT NULL,
    ALTER COLUMN year                SET NOT NULL,
    ALTER COLUMN week_number         SET NOT NULL,
    ALTER COLUMN week_start_datetime SET NOT NULL,
    ALTER COLUMN week_end_datetime   SET NOT NULL,
    ALTER COLUMN week_display        SET NOT NULL;
