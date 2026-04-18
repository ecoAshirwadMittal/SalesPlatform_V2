-- Phase 1 of docs/tasks/aggregated-inventory-snowflake-sync-plan.md
-- Watermark per (week, source) replaces Mendix AggregatedInventoryTotals.MaxUploadTime.
-- Also installs the unique index the Phase 4 upsert depends on.

CREATE TABLE IF NOT EXISTS auctions.week_sync_watermark (
    week_id                BIGINT       NOT NULL
                           REFERENCES mdm.week(id) ON DELETE CASCADE,
    source                 VARCHAR(32)  NOT NULL,
    last_source_upload_at  TIMESTAMPTZ  NOT NULL,
    last_synced_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    row_count              INTEGER      NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_week_sync_watermark PRIMARY KEY (week_id, source)
);

CREATE INDEX IF NOT EXISTS idx_wsw_source
    ON auctions.week_sync_watermark (source);

-- One-time data fixup to guarantee the partial unique index below can be
-- created. Safe to re-run because updating already-deprecated rows is a no-op.
--
-- Pre-check: if any duplicate live rows exist on the upsert key, mark all
-- but the newest as deprecated. The unique-partial index below would fail
-- otherwise. Live = is_deprecated = false. Tie-break by id DESC (most
-- recently inserted wins). NULL-keyed rows are skipped because the partial
-- unique index only enforces uniqueness on non-NULL tuples (Postgres
-- NULL-distinct semantics); PARTITION BY would otherwise group NULLs and
-- silently deprecate live-but-distinct NULL-keyed rows.
WITH duplicates AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY ecoid2, merged_grade, datawipe, week_id
               ORDER BY id DESC
           ) AS rn
    FROM auctions.aggregated_inventory
    WHERE is_deprecated = false
      AND ecoid2 IS NOT NULL
      AND merged_grade IS NOT NULL
      AND week_id IS NOT NULL
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
