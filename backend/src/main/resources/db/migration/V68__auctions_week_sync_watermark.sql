-- Phase 1 of docs/tasks/aggregated-inventory-snowflake-sync-plan.md
-- Watermark per (week, source) replaces Mendix AggregatedInventoryTotals.MaxUploadTime.
-- Also installs the unique index the Phase 4 upsert depends on.

CREATE TABLE IF NOT EXISTS auctions.week_sync_watermark (
    week_id                BIGINT       PRIMARY KEY
                           REFERENCES mdm.week(id) ON DELETE CASCADE,
    source                 VARCHAR(32)  NOT NULL,
    last_source_upload_at  TIMESTAMPTZ  NOT NULL,
    last_synced_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    row_count              INTEGER      NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_wsw_source
    ON auctions.week_sync_watermark (source);

-- Pre-check: if any duplicate live rows exist on the upsert key, mark all
-- but the newest as deprecated. The unique-partial index below would fail
-- otherwise. Live = is_deprecated = false. Tie-break by id DESC (most
-- recently inserted wins).
WITH duplicates AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY ecoid2, merged_grade, datawipe, week_id
               ORDER BY id DESC
           ) AS rn
    FROM auctions.aggregated_inventory
    WHERE is_deprecated = false
)
UPDATE auctions.aggregated_inventory a
   SET is_deprecated = true,
       changed_date = NOW()
  FROM duplicates d
 WHERE a.id = d.id
   AND d.rn > 1;

CREATE UNIQUE INDEX IF NOT EXISTS uq_agi_ecoid_grade_dw_week
    ON auctions.aggregated_inventory (ecoid2, merged_grade, datawipe, week_id)
    WHERE is_deprecated = false;
