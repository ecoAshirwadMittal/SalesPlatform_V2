# Aggregated Inventory — Snowflake Sync on Week Change

**Status:** Draft — 2026-04-18
**Owner:** SalesPlatform_Modern / auctions module
**Legacy source:** `ACT_GetAggregateInventoryforWeek` (nanoflow) →
`SUB_SetAggregatedIventoryHelper` → `SUB_LoadAggregatedInventoryTotals` →
`SUB_LoadAggregatedInventory`.

---

## 1. Goal

Port the Mendix flow that runs when a user picks a week on the
Aggregated Inventory page. On each week change, the page must:

1. Decide whether the `auctions.aggregated_inventory` rows for that
   week are stale against Snowflake's `VW_AGGREGATED_INVENTORY`
   (or legacy equivalent), using `MAX(UPLOAD_TIME)` as the freshness
   key.
2. If stale, reload the week's rows from Snowflake in pages of 1000
   (legacy `CONST_SF_QueryPageSize`), upserting into
   `auctions.aggregated_inventory` while preserving admin overrides
   (ADR 2026-04-17 — `is_total_quantity_modified = true` rows are
   protected).
3. Record the refreshed `MAX(UPLOAD_TIME)` against the week so the
   next week-change click is a no-op when nothing changed.
4. Log every run to `integration.snowflake_sync_log` for audit.
5. Return helper flags to the frontend (`hasInventory`,
   `hasAuction`, `isCurrentWeek`) so the page can enable/disable
   "Create Auction" and the KPI strip.

---

## 2. Legacy flow — detailed trace

| Step | Legacy artifact | Behavior |
|---|---|---|
| Trigger | `ACT_GetAggregateInventoryforWeek.md` | Nanoflow on week-select; guards on `$SelectedWeek != empty`, shows progress, calls helper, opens page |
| Helper setup | `SUB_SetAggregatedIventoryHelper.md` | Calls Totals loader, then sets `HasInventory`, `HasAuction`, `isCurrentWeek` booleans on the AggInventoryHelper |
| Sync guard | `SUB_LoadAggregatedInventoryTotals.md` | Short-circuits if an `AuctionUI.Auction` already exists for the week (locked for bidding). Otherwise queries Snowflake for `MAX(UPLOADTIME)` for the week, parses with `parseDateTimeUTC(…, 'yyyy-MM-dd''T''HH:mm:ss')`, compares against persisted `AggregatedInventoryTotals.MaxUploadTime`. Only reloads when the Snowflake max is newer (or no totals row exists). |
| Reload | `SUB_LoadAggregatedInventory.md` | Deletes existing `AggregatedInventory_Week` rows and totals, then paginates Snowflake at `CONST_SF_QueryPageSize=1000`, `ExportXml` → `ImportXml` via `IM_AggregatedInventory` into `auctionui$aggregatedinventory`, loops until a page returns empty |
| JDBC | `JA_SnowflakeToMendix.md` | Java action that runs the JDBC query, materializes JSON, and lets `IM_AggregatedInventory` map into typed entities |
| Field map | `IM_AggregatedInventory.md` + `JSON_AggregatedInventory.md` | 20-field mapping: `EcoID`, `MG` (merged grade), `DataWipe`, averages (`AvgTargetPrice`, `AvgPayout`, `DWAvg*`), totals (`TotalPayout`, `TotalQuantity`, `DW*`), round 1 target prices, device labels (`Name`, `Model`, `Brand`, `Carrier`, `Category`, `Device_id`), `Created_at` |
| Scheduled catch-up | `SE_GetAggregatedInventory.md` | **Disabled** in legacy; would run every minute via `SUB_UploadInventoryForCurrentWeek` (current-week only). Not porting initially. |

### Legacy quirks to NOT re-introduce

- **Delete-then-insert** per week. Modern path must upsert so admin
  edits (see next point) and cross-job bookkeeping survive.
- **Overwrite of admin-edited quantities.** ADR 2026-04-17 already
  established `is_total_quantity_modified` as the sticky override
  flag. Sync must skip `total_quantity` / `dw_total_quantity` writes
  when that flag is `true`.
- **Always runs on every week change.** We debounce by comparing
  `MAX(UPLOAD_TIME)` to a persisted watermark per week.
- **Creates a separate totals row.** Per ADR 2026-04-17, totals are
  computed at read-time from `auctions.aggregated_inventory`. Sync
  does not write to `auctions.aggregated_inventory_totals`.

---

## 3. Modern architecture

### 3.1 Reuse existing Theme 2 Snowflake infrastructure

`docs/tasks/todos-resolution-plan.md` (Theme 2) already specifies:

- `net.snowflake:snowflake-jdbc:3.16.1` dependency
- `snowflake.*` config keys (`enabled`, `jdbc-url`, `user`,
  `password`, `warehouse`, `database`, `schema`, `role`,
  `pool.maximum-size=3`)
- `SnowflakeDataSourceConfig` bean (`@ConditionalOnProperty
  snowflake.enabled=true`) with a HikariCP pool
- `snowflakeExecutor` (`AsyncConfig.java`)
- `integration.snowflake_sync_log` table (V25 in Theme 2)

This plan **depends on Theme 2 Phase 2.1–2.3 landing first** (pom,
config keys, DataSource bean). If Theme 2 ships alongside, share the
pool and log table. If Theme 2 slips, this plan can land its own
minimal `SnowflakeDataSourceConfig` behind the same flag — the
scope of that standalone path is called out in §7 below.

#### Confirmed connection settings

JDBC URL (confirmed by product owner 2026-04-18):

```
jdbc:snowflake://ecoatm.snowflakecomputing.com/?db=ECO_DEV&schema=AUCTIONS
```

Credentials are injected via environment variables — never
committed. `application.yml` binds to `${SNOWFLAKE_USERNAME:}` and
`${SNOWFLAKE_PASSWORD:}`; empty defaults + `snowflake.enabled=false`
together keep the app bootable in dev / CI without the secrets.

```yaml
# backend/src/main/resources/application.yml (add to existing keys)
snowflake:
  enabled: ${SNOWFLAKE_ENABLED:false}
  jdbc-url: ${SNOWFLAKE_JDBC_URL:jdbc:snowflake://ecoatm.snowflakecomputing.com/?db=ECO_DEV&schema=AUCTIONS&TIMEZONE=UTC}
  username: ${SNOWFLAKE_USERNAME:}
  password: ${SNOWFLAKE_PASSWORD:}
  pool:
    maximum-size: ${SNOWFLAKE_POOL_MAX:3}
```

Add `SNOWFLAKE_JDBC_URL`, `SNOWFLAKE_USERNAME`, `SNOWFLAKE_PASSWORD`,
and `SNOWFLAKE_ENABLED` to the env-var table in
`docs/deployment/environments.md` as part of Phase 9 (docs).
`SNOWFLAKE_JDBC_URL` is overridable so QA/prod can swap `ECO_DEV`
for `ECO_QA` / `ECO_PROD` without a code change.

Startup guard in `SnowflakeDataSourceConfig`: when
`snowflake.enabled=true`, fail-fast if `username` or `password` are
blank — a misconfigured prod deploy should not silently fall
back to "sync always returns `SKIPPED_DISABLED`".

### 3.2 New schema (one Flyway migration)

`V67__auctions_week_sync_watermark.sql`:

```sql
CREATE TABLE auctions.week_sync_watermark (
    week_id                BIGINT       NOT NULL
                           REFERENCES mdm.week(id) ON DELETE CASCADE,
    source                 VARCHAR(32)  NOT NULL,        -- 'SNOWFLAKE_AGG_INVENTORY'
    last_source_upload_at  TIMESTAMPTZ  NOT NULL,
    last_synced_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    row_count              INTEGER      NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_week_sync_watermark PRIMARY KEY (week_id, source)
);

CREATE INDEX idx_wsw_source ON auctions.week_sync_watermark (source);
```

Rationale: one row per `(week, source)` captures the Mendix
`AggregatedInventoryTotals.MaxUploadTime` semantic without reviving
the whole totals row (see ADR 2026-04-17).

### 3.3 New backend components

```
service/auctions/
  AggregatedInventorySnowflakeSyncService.java    NEW
  snowflake/
    SnowflakeAggInventoryReader.java              NEW  — paginated JDBC cursor
    SnowflakeAggInventoryRow.java                 NEW  — record, 20 fields
repository/
  WeekSyncWatermarkRepository.java                NEW  — JPA
model/auctions/
  WeekSyncWatermark.java                          NEW  — @Entity
controller/
  AggregatedInventoryController.java              EDIT — add trigger endpoint
dto/
  AggregatedInventorySyncResult.java              NEW  — {status, rowsUpserted, durationMs, source}
event/
  AggInventorySyncRequestedEvent.java             NEW  — {weekId, triggeredBy}
  AggInventorySyncListener.java                   NEW  — @Async("snowflakeExecutor")
```

Call shape on the service:

```java
public AggregatedInventorySyncResult syncWeek(long weekId, String triggeredBy);
```

Returns one of:
- `SKIPPED_LOCKED` — `auctions.auction` exists for this week (parity
  with Mendix guard; protects active rounds).
- `SKIPPED_UP_TO_DATE` — Snowflake `MAX(UPLOAD_TIME)` ≤ watermark.
- `SKIPPED_DISABLED` — `snowflake.enabled=false`.
- `COMPLETED` — rows upserted, watermark bumped.
- `FAILED` — Snowflake error, logged.

### 3.4 Upsert shape (preserves admin overrides)

```sql
INSERT INTO auctions.aggregated_inventory (
    ecoid2, merged_grade, datawipe, week_id,
    name, model, brand, carrier, category, device_id,
    avg_target_price, avg_payout, total_payout, total_quantity,
    round1_target_price, round1_target_price_dw,
    dw_avg_target_price, dw_avg_payout, dw_total_payout, dw_total_quantity,
    created_at, changed_date
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
ON CONFLICT (ecoid2, merged_grade, datawipe, week_id)
DO UPDATE SET
    name = EXCLUDED.name,
    model = EXCLUDED.model,
    brand = EXCLUDED.brand,
    carrier = EXCLUDED.carrier,
    category = EXCLUDED.category,
    device_id = EXCLUDED.device_id,
    avg_target_price = EXCLUDED.avg_target_price,
    avg_payout = EXCLUDED.avg_payout,
    total_payout = EXCLUDED.total_payout,
    -- Honor admin override flag:
    total_quantity = CASE
        WHEN auctions.aggregated_inventory.is_total_quantity_modified THEN auctions.aggregated_inventory.total_quantity
        ELSE EXCLUDED.total_quantity
    END,
    dw_total_quantity = CASE
        WHEN auctions.aggregated_inventory.is_total_quantity_modified THEN auctions.aggregated_inventory.dw_total_quantity
        ELSE EXCLUDED.dw_total_quantity
    END,
    round1_target_price = EXCLUDED.round1_target_price,
    round1_target_price_dw = EXCLUDED.round1_target_price_dw,
    dw_avg_target_price = EXCLUDED.dw_avg_target_price,
    dw_avg_payout = EXCLUDED.dw_avg_payout,
    dw_total_payout = EXCLUDED.dw_total_payout,
    changed_date = NOW()
WHERE auctions.aggregated_inventory.is_deprecated = false;
```

Requires a unique index:

```sql
CREATE UNIQUE INDEX IF NOT EXISTS uq_agi_ecoid_grade_dw_week
    ON auctions.aggregated_inventory (ecoid2, merged_grade, datawipe, week_id)
    WHERE is_deprecated = false;
```

(Ships inside the same V67 migration.)

Rows present in our table but **absent** from a fresh Snowflake page
set are left untouched. Hard-delete is intentionally avoided —
admin edits would otherwise be wiped. If a hard prune is ever
needed, add a `--prune` admin endpoint that sets `is_deprecated =
true` for rows missing from the latest sync; out of scope for this
plan.

### 3.5 Endpoint

```
POST /api/v1/admin/inventory/weeks/{weekId}/sync
Roles: Administrator, SalesOps
Body: none
Response 202:
  {
    "status": "ACCEPTED",
    "source": "SNOWFLAKE_AGG_INVENTORY"
  }
```

Behavior: publishes `AggInventorySyncRequestedEvent`, returns 202.
The listener runs on `snowflakeExecutor`, writes a PENDING row to
`integration.snowflake_sync_log`, calls `syncWeek`, updates the log
row to `COMPLETED` / `SKIPPED_*` / `FAILED`.

Why async: the 87k-row table pages ~87 round trips × 1000 rows =
multiple seconds to minutes depending on Snowflake warehouse size.
Blocking the week-select UI is a regression against Mendix's
"progress spinner" UX. The frontend instead polls the existing
`GET /admin/inventory` list — an empty page during the sync surfaces
a small "syncing…" banner (3.6).

### 3.6 Frontend changes

`frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`:

- On `weekId` change, in addition to refetching the grid, POST to
  `/admin/inventory/weeks/{weekId}/sync`.
- Fire-and-forget (don't await the 202). The existing grid refetch
  stays non-blocking.
- Add a lightweight polling loop: every 3s for up to 90s, query
  `GET /admin/inventory/weeks/{weekId}/sync/status` (also new — see
  below) while the latest log row is `PENDING`. When it flips, do a
  single final grid refetch.
- Surface a subtle "syncing from Snowflake…" banner above the grid
  header while `PENDING`. Match existing Mendix-parity styling
  tokens (`--color-text-muted`, 12px).

`GET /api/v1/admin/inventory/weeks/{weekId}/sync/status`:
- Returns `{ status: PENDING|COMPLETED|SKIPPED_*|FAILED|NONE,
  lastSyncedAt, rowsUpserted, errorMessage }` read from the latest
  `snowflake_sync_log` row scoped to `(source = 'SNOWFLAKE_AGG_INVENTORY',
  week_id = {weekId})`.

Only trigger the sync when the user is an `Administrator` or
`SalesOps`. Bidder-role viewers should only read the cached table.

### 3.7 Helper-flags response (replaces Mendix AggInventoryHelper)

Extend `GET /admin/inventory/totals` to include:

```json
{
  "totalQuantity": …,
  "totalPayout": …,
  "averageTargetPrice": …,
  "dwTotalQuantity": …,
  "dwTotalPayout": …,
  "dwAverageTargetPrice": …,
  "lastSyncedAt": "...",
  "hasInventory": true,
  "hasAuction": false,
  "isCurrentWeek": true,
  "syncStatus": "COMPLETED"
}
```

`hasAuction` reads from the existing `auctions.auction` table (FK
to `mdm.week`). `isCurrentWeek` is computed via `WeekRepository`
(same filter Mendix used: `weekEndDateTime > now()` ∧ week row
matches). `hasInventory` is a cheap `EXISTS` against
`aggregated_inventory` for the week. This removes the need for a
separate helper endpoint.

---

## 4. Delivery phases

| # | Phase | Files | Check | Test |
|---|---|---|---|---|
| 1 | **Schema** | `V67__…sql` (watermark table + unique index) | Flyway green on fresh DB | Flyway integration test (existing pattern from V60) |
| 2 | **Domain + repo** | `WeekSyncWatermark.java`, `WeekSyncWatermarkRepository.java` | Boot starts | `@DataJpaTest` roundtrip |
| 3 | **Reader** | `SnowflakeAggInventoryRow.java`, `SnowflakeAggInventoryReader.java` (paginated cursor, page-size constant `1000`) | Compiles | Unit test with an in-memory H2 stub for the cursor interface + integration test behind `@EnabledIfEnvironmentVariable("SNOWFLAKE_IT", "true")` |
| 4 | **Service** | `AggregatedInventorySnowflakeSyncService.java` — orchestrates lock guard → watermark check → reader loop → batch upsert → watermark bump | Compiles | 6 unit tests (table below §6) |
| 5 | **Async wiring** | `AggInventorySyncRequestedEvent.java`, `AggInventorySyncListener.java` (`@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`) | Events publish/consume | 2 tests: commit fires, rollback does not |
| 6 | **Endpoint + status** | `AggregatedInventoryController.java` additions | 202 for POST, 200 for GET status | MockMvc tests for both + 403 for bidder role |
| 7 | **Helper flags** | Extend `AggregatedInventoryService` + `AggregatedInventoryTotalsResponse` | Existing page shows the new fields | MockMvc assertion on totals endpoint |
| 8 | **Frontend** | `inventory/page.tsx`, `lib/aggregatedInventory.ts` | Week change triggers POST; banner appears during `PENDING` | Playwright spec: pick week, mock POST 202 + two GETs (PENDING → COMPLETED), assert banner visibility and final refetch |
| 9 | **Docs** | `rest-endpoints.md`, `architecture/decisions.md` (new ADR) | — | — |

Phases 1→9 are linear by dependency; each is small enough to ship
standalone behind `snowflake.enabled=false`.

---

## 5. Snowflake query shape

The real Snowflake source is `ECO_DEV.AUCTIONS.Master_Inventory_List_Snapshot`
joined to `dim_device`. Parameters are `{Auction_Week}` (int week
number) and `{Auction_Year}` (int), with `{Offset}` / `{Fetch}` for
pagination. The legacy SQL (confirmed 2026-04-18) lives at §9.

### 5.1 Week mapping

Our API takes a `weekId` that is the PK of `mdm.week`. The Snowflake
query needs `week_number` + `year`. `mdm.week` already stores both
(see `V58__create_auctions_schema_and_core.sql`,
`V65__seed_mdm_week.sql`) — use them directly:

```java
record WeekKey(int auctionWeek, int auctionYear) {}

WeekKey key = weekRepository.findById(weekId)
    .map(w -> new WeekKey(w.getWeekNumber(), w.getYear()))
    .orElseThrow(() -> new EntityNotFoundException("week " + weekId));
```

### 5.2 Watermark query

Cheap; run every week change to decide whether to reload:

```sql
SELECT MAX(Upload_Time) AS MAX_UPLOAD_TIME
FROM   Master_Inventory_List_Snapshot
WHERE  Auction_Week = ?
  AND  Auction_Year = ?
```

Snowflake returns `TIMESTAMP_NTZ`. Force UTC interpretation on the
session (`TIMEZONE=UTC` URL param) and convert via
`ResultSet.getTimestamp(…, utcCal).toInstant()`.

### 5.3 Data query (paginated)

Only runs when the watermark is newer than
`week_sync_watermark.last_source_upload_at`. The full SQL is in §9 —
summary of behaviors that matter for the port:

- Single CTE query: `MaxUploadInventory` → `AggregatedData` →
  `DimDeviceFiltered` → left-outer-join for device labels.
- **Drop the `UNION ALL` "Totals" row** from what we ingest.
  Legacy used it to populate `AggregatedInventoryTotals`; per ADR
  2026-04-17 we compute totals at read time, so
  `SnowflakeAggInventoryReader` filters out rows where
  `DEVICE_ID = 'Total'` before materializing.
- Pagination: `OFFSET {Offset} ROWS FETCH NEXT {Fetch} ROWS ONLY`
  with `Fetch = 1000` (`SnowflakeAggInventoryReader.PAGE_SIZE`),
  stop when a page returns fewer than `PAGE_SIZE` rows.
- Ordering: `ecoID, DEVICE_ID, MG, DataWipe` — stable across
  pages; the reader relies on this for deterministic batching.
- `Round1TargetPrice` / `Round1TargetPrice_DW` are SELECT aliases
  over `AvgTargetPrice` / `DWAvgTargetPrice` — no separate source
  column, no additional math in Java.

### 5.4 Column → domain mapping

| Snowflake column | Java field | Notes |
|---|---|---|
| `ecoID` | `String ecoid2` | From `current_device_ecoatm_code` |
| `DEVICE_ID` | `String deviceId` | `dim_device.DEVICE_ID`; "Total" row filtered out |
| `MG` | `String mergedGrade` | null allowed |
| `DataWipe` | `boolean datawipe` | `"DW"` → `true`, `""`/null → `false` (confirmed) |
| `MaxUploadTime` | (not stored on row) | used only for watermark bookkeeping |
| `AvgTargetPrice` | `BigDecimal avgTargetPrice` | |
| `AvgMargin` | (skipped) | no column in `auctions.aggregated_inventory`; not ported |
| `AvgPayout` | `BigDecimal avgPayout` | |
| `TotalPayout` | `BigDecimal totalPayout` | |
| `TotalQuantity` | `int totalQuantity` | sync writes only when `is_total_quantity_modified = false` |
| `DWAvgTargetPrice` | `BigDecimal dwAvgTargetPrice` | |
| `DWAvgMargin` | (skipped) | no column; not ported |
| `DWAvgPayout` | `BigDecimal dwAvgPayout` | |
| `DWTotalPayout` | `BigDecimal dwTotalPayout` | |
| `DWTotalQuantity` | `int dwTotalQuantity` | override-guarded |
| `ROUND1TARGETPRICE` | `BigDecimal round1TargetPrice` | = AvgTargetPrice upstream |
| `ROUND1TARGETPRICE_DW` | `BigDecimal round1TargetPriceDw` | = DWAvgTargetPrice upstream |
| `name` | `String name` | |
| `model` | `String model` | free-text; matched to `mdm.model.name` downstream if needed |
| `brand` | `String brand` | |
| `carrier` | `String carrier` | |
| `category` | `String category` | |
| `created_at` | `Instant createdAt` | `dim_device.CREATED_AT::DATE` |

The `AvgMargin` / `DWAvgMargin` columns exist in the Snowflake
output but `auctions.aggregated_inventory` has no equivalent column
— they are ignored. If the UI ever needs margin, add a column in a
follow-up migration rather than trying to stash it in an existing
field.

Legacy used `parseDateTimeUTC(…, 'yyyy-MM-dd''T''HH:mm:ss')`. The
JDBC driver returns `TIMESTAMP_NTZ` as `java.sql.Timestamp` in the
session timezone — set `TIMEZONE=UTC` in the JDBC URL so both
paths agree, and convert to `Instant` via `ts.toInstant()`.

---

## 6. Test matrix

Service-level:

| Test | Setup | Assertion |
|---|---|---|
| `syncWeek_weekHasAuction_returnsSkippedLocked` | Insert `auctions.auction` for week | Reader not called; log row `SKIPPED_LOCKED` |
| `syncWeek_watermarkFresh_returnsSkippedUpToDate` | Watermark `>=` mocked Snowflake max | Data reader not called; watermark untouched |
| `syncWeek_watermarkStale_upsertsAllPages` | Mock reader returns 3 pages (1000/1000/342) | 2342 upserts; watermark = reader max; log `COMPLETED` |
| `syncWeek_respectsAdminOverride` | Pre-existing row with `is_total_quantity_modified=true`; reader returns different quantity | Row `total_quantity` unchanged; other fields updated |
| `syncWeek_snowflakeDisabled_returnsSkippedDisabled` | `snowflake.enabled=false` | Reader bean is absent / service short-circuits; log row NOT inserted |
| `syncWeek_snowflakeThrows_logsFailed` | Reader throws `SQLException` on page 2 | Log row `FAILED` with error message; partial batch rolled back (outer `@Transactional`) |

Controller-level:

| Test | Role | Expected |
|---|---|---|
| `trigger_asAdmin_returns202` | Administrator | 202 + event published |
| `trigger_asSalesOps_returns202` | SalesOps | 202 |
| `trigger_asBidder_returns403` | Bidder | 403 |
| `status_returnsLatestLogRow` | any | 200 with mapped log row |

Frontend:

| Spec | Behavior |
|---|---|
| `aggregated-inventory-sync.spec.ts` | Change week → POST fires → banner visible while status PENDING → banner disappears on COMPLETED → grid refetches once |
| `aggregated-inventory-sync-error.spec.ts` | Status FAILED → banner shows error tone + "Retry" action → retry triggers POST again |

---

## 7. Theme 2 dependency + fallback

Theme 2 (per `docs/tasks/todos-resolution-plan.md`) lands:
- `V25__create_snowflake_sync_log.sql`
- `SnowflakeDataSourceConfig`
- `snowflake.*` config + `snowflakeExecutor`

If Theme 2 ships **before** this plan → reuse all three directly.

If Theme 2 slips, this plan ships a standalone subset:
- A copy of `V25` renamed to `V68__create_snowflake_sync_log.sql`
  (or coordinate version number at merge time).
- A minimal `SnowflakeDataSourceConfig` gated on
  `snowflake.enabled=true`.
- A minimal `snowflakeExecutor` bean inside the existing
  `AsyncConfig`.
- When Theme 2 later lands, it inherits the shared infrastructure
  without duplication (rename/move refactor).

Either way the feature flag stays `snowflake.enabled=false` in
dev and CI — no local Snowflake credential is required to build
or run the app.

---

## 8. Rollout

| Env | `snowflake.enabled` | Notes |
|---|---|---|
| local dev | false | Sync endpoint returns `SKIPPED_DISABLED`; grid still loads cached rows |
| CI | false | Same |
| QA | true | Credentials from QA env vars; observe `integration.snowflake_sync_log` |
| prod | true | Gate behind admin-only traffic for 48h before announcing to SalesOps |

Rollback: set `snowflake.enabled=false` and redeploy. The cached
`auctions.aggregated_inventory` stays intact — reads aren't gated
by the flag. The watermark table retains stale data and is re-used
correctly when the flag flips back on.

---

## 9. Confirmed answers + source SQL (2026-04-18)

1. **Snowflake source.** Not a view — queries hit
   `ECO_DEV.AUCTIONS.Master_Inventory_List_Snapshot` joined to
   `dim_device`. Parameters are `{Auction_Week}` + `{Auction_Year}`
   (integers), plus `{Offset}` / `{Fetch}` for paging. Full source
   SQL (per-device paged + per-week totals variant) below:
   /*
===============================================================
Purpose:
    - Retrieve aggregated auction data for devices, grouped by `ecoatm_code` and `MG` (Merged Grade).
    - Metrics include averages, totals, and counts, both overall and specific to "DW" (Data Wipe Good) devices.
    - Provides a "Totals" row to summarize all metrics.
    - Supports dynamic filtering based on Auction Week and Year, and includes pagination for large result sets.

Dynamic Parameters:
    {Auction_Week} - Auction week to filter the data.
    {Auction_Year} - Auction year to filter the data.
    {Offset}       - Row offset for pagination (starting point).
    {Fetch}        - Number of rows to fetch per page.

Logic:
    - Aggregate all metrics (`AvgTargetPrice`, `AvgMargin`, `AvgPayout`, `TotalPayout`, etc.) for devices.
    - Determine `DataWipe` as "DW" if at least one "DW" device exists in the group otherwise, leave it blank.
    - Separate metrics for all devices and only "DW" devices in each group.
    - A totals row includes aggregated metrics for the entire dataset.
===============================================================
*/

WITH MaxUploadInventory AS (
    -- Step 1: Get the most recent upload time for the specified Auction Week and Year.
    SELECT 
        MAX(Upload_Time) AS MaxUploadTime
    FROM 
        Master_Inventory_List_Snapshot
    WHERE 
        Auction_Week = {Auction_Week} -- Dynamic Auction Week
        AND Auction_Year = {Auction_Year} -- Dynamic Auction Year
),
AggregatedData AS (
    /*
    Step {Auction_Week}: Calculate aggregated metrics for each `ecoatm_code` and `MG` (Merged Grade).
    - Metrics include averages, totals, and counts for all devices and "DW" devices.
    - The `DataWipe` column is determined as:
        - "DW" if at least one "DW" device exists in the group.
        - Blank ('') otherwise.
    */
    SELECT 
        current_device_ecoatm_code AS ecoID, -- Device ID
        MG, -- Merged Grade
        CASE 
            WHEN SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 ELSE 0 END) > 0 THEN 'DW' 
            ELSE '' -- Empty string for non-DW devices
        END AS DataWipe, -- Determine DataWipe status
        (SELECT MaxUploadTime FROM MaxUploadInventory) AS MaxUploadTime, -- Most recent upload time
COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN target_price END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END), 2)
        ) AS AvgTargetPrice,
        COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN margin END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN margin END), 2)
        ) AS AvgMargin,
        COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
        ) AS AvgPayout,
        COALESCE(
            ROUND(SUM(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
            ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
        ) AS TotalPayout,        
        COUNT(*) AS TotalQuantity, -- Total count of all devices
        -- DW-specific metrics
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END),2) AS DWAvgTargetPrice,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN margin END),2) AS DWAvgMargin,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END),2) AS DWAvgPayout,
        ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END),2) AS DWTotalPayout,
        COUNT(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 END) AS DWTotalQuantity -- Count of DW devices
    FROM 
        Master_Inventory_List_Snapshot
    WHERE 
        Auction_Year = {Auction_Year} -- Dynamic Auction Year
        AND Auction_Week = {Auction_Week} -- Dynamic Auction Week
        AND Upload_Time = (SELECT MaxUploadTime FROM MaxUploadInventory) -- Filter by most recent upload time
    GROUP BY 
        current_device_ecoatm_code, MG -- Group by Device ID and Merged Grade
),
DimDeviceFiltered AS (
    /*
    Step 3: Retrieve descriptive device details from the `dim_device` table.
    - Includes DEVICE_ID, ecoatm_code, name, brand, category, carrier, creation date, and model.
    - These details are joined with aggregated data to enrich the output.
    */
    SELECT DISTINCT 
        DEVICE_ID, -- Unique identifier for the device
        ecoatm_code, -- Device ID
        name, -- Device name
        device_brand AS brand, -- Device brand
        device_category AS category, -- Device category
        device_carrier AS carrier, -- Carrier information
        CREATED_AT::DATE AS created_at, -- Device creation date
        device_model AS model -- Device model
    FROM 
        dim_device
),
AggregatedTotals AS (
    /*
    Step 4: Calculate overall totals for all metrics across the dataset.
    - Includes aggregated averages, totals, and counts for all devices and "DW" devices.
    */
    SELECT 
        NULL AS ecoID, -- Null for ecoatm_code in totals row
        NULL AS MG, -- Null for Merged Grade in totals row
        NULL AS DataWipe, -- Null for DataWipe in totals row
        (SELECT MaxUploadTime FROM MaxUploadInventory) AS MaxUploadTime, -- Most recent upload time
               COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN target_price END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END), 2)
        ) AS AvgTargetPrice,
        COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN margin END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN margin END), 2)
        ) AS AvgMargin,
        COALESCE(
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
            ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
        ) AS AvgPayout,
        COALESCE(
            ROUND(SUM(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
            ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
        ) AS TotalPayout,      
        COUNT(*) AS TotalQuantity, -- Total count of all devices
        -- DW-specific metrics
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END),2) AS DWAvgTargetPrice,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN margin END),2) AS DWAvgMargin,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END),2) AS DWAvgPayout,
        ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END),2) AS DWTotalPayout,
        COUNT(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 END) AS DWTotalQuantity -- Count of DW devices
    FROM 
        Master_Inventory_List_Snapshot
    WHERE 
        Auction_Year = {Auction_Year} -- Dynamic Auction Year
        AND Auction_Week = {Auction_Week} -- Dynamic Auction Week
        AND Upload_Time = (SELECT MaxUploadTime FROM MaxUploadInventory)
)

-- Final Query: Combine AggregatedData and Totals, and join with DimDeviceFiltered for enriched output
SELECT 
    agg.ecoID,
    dd.DEVICE_ID, -- Device ID
    agg.MG, -- Merged Grade
    agg.DataWipe, -- DataWipe status
    agg.MaxUploadTime, -- Most recent upload time
    agg.AvgTargetPrice,
    agg.AvgMargin,
    agg.AvgPayout,
    agg.TotalPayout,
    agg.TotalQuantity,
    agg.DWAvgTargetPrice,
    agg.DWAvgMargin,
    agg.DWAvgPayout,
    agg.DWTotalPayout,
    agg.DWTotalQuantity,
    dd.name,
    dd.model,
    dd.brand,
    dd.carrier,
    dd.created_at,
    dd.category,
    agg.AvgTargetPrice as ROUND1TARGETPRICE,
    agg.DWAvgTargetPrice as ROUND1TARGETPRICE_DW
FROM 
    AggregatedData agg
LEFT OUTER JOIN 
    DimDeviceFiltered dd
ON 
    agg.ecoID = dd.ecoatm_code

UNION ALL

-- Totals Row
SELECT 
    ecoID,
    'Total' AS DEVICE_ID, -- Totals row identifier
    MG,
    DataWipe,
    MaxUploadTime,
    AvgTargetPrice,
    AvgMargin,
    AvgPayout,
    TotalPayout,
    TotalQuantity,
    DWAvgTargetPrice,
    DWAvgMargin,
    DWAvgPayout,
    DWTotalPayout,
    DWTotalQuantity,
    NULL AS name,
    NULL AS model,
    NULL AS brand,
    NULL AS carrier,
    NULL AS created_at,
    NULL AS category,
    AvgTargetPrice as ROUND1TARGETPRICE,
    DWAvgTargetPrice as ROUND1TARGETPRICE_DW
FROM 
    AggregatedTotals

ORDER BY 
    ecoID,DEVICE_ID, MG,DataWipe
OFFSET 
    {Offset} ROWS -- Dynamic pagination start point
FETCH NEXT 
    {Fetch} ROWS ONLY -- Dynamic number of rows to fetch per page.
    For totals - WITH MaxUploadInventory AS (
    SELECT 
        MAX(Upload_Time) AS MaxUploadTime
    FROM 
        Master_Inventory_List_Snapshot
    WHERE 
        Auction_Week = {Auction_Week} -- Dynamic Auction Week
        AND Auction_Year = {Auction_Year} -- Dynamic Auction Year
),
AggregatedInventoryTotals AS (
    SELECT 
        (SELECT MaxUploadTime FROM MaxUploadInventory) AS MaxUploadTime, -- Include MaxUploadTime
        
        -- Data Wipe (DW) Metrics
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2) AS DWAvgPayout,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END), 2) AS DWAvgTargetPrice,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN margin END), 2) AS DWAvgMargin,
        COUNT(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 END) AS DWTotalQuantity,
        
        -- Non-Data Wipe (NDW) Metrics
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD IS NULL OR Data_WIPE_GOOD != 'DW' THEN payout END), 2) AS NDWAvgPayout,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD IS NULL OR Data_WIPE_GOOD != 'DW' THEN target_price END), 2) AS NDWAvgTargetPrice,
        ROUND(AVG(CASE WHEN Data_WIPE_GOOD IS NULL OR Data_WIPE_GOOD != 'DW' THEN margin END), 2) AS NDWAvgMargin,
        COUNT(CASE WHEN Data_WIPE_GOOD IS NULL OR Data_WIPE_GOOD != 'DW' THEN 1 END) AS NDWTotalQuantity
    FROM 
        Master_Inventory_List_Snapshot
    WHERE 
        Auction_Year = {Auction_Year} -- Dynamic Auction Year
        AND Auction_Week = {Auction_Week} -- Dynamic Auction Week
        AND Upload_Time = (SELECT MaxUploadTime FROM MaxUploadInventory) -- Use Max Upload Time
)
SELECT 
    MaxUploadTime,
    DWAvgPayout,
    DWAvgTargetPrice,
    DWAvgMargin,
    DWTotalQuantity,
    NDWAvgPayout,
    NDWAvgTargetPrice,
    NDWAvgMargin,
    NDWTotalQuantity
FROM 
    AggregatedInventoryTotals
2. **Datawipe encoding — confirmed.** `"DW"` → `true`,
   empty-string / null → `false`.
3. **Unique key — confirmed approach.** Phase 1 adds the unique
   index on `(ecoid2, merged_grade, datawipe, week_id) WHERE
   is_deprecated = false`. Pre-index QA scan + data-fix migration
   (`V66_5` or embedded in `V67`) land before the `CREATE UNIQUE
   INDEX`.
4. **`round2/round3` — confirmed.** Computed downstream; sync
   leaves `round2_*` / `round3_*` / `r2_*` / `r3_*` columns
   untouched.
5. **Totals row — do not ingest.** The source SQL appends a
   `DEVICE_ID='Total'` row via `UNION ALL`. We compute totals at
   read time (ADR 2026-04-17), so the reader filters the Total row
   out before batching. The separate per-week totals variant below
   is for reference only and is not called by this flow.

---

## 10. References

- Legacy: `migration_context/frontend/ACT_GetAggregateInventoryforWeek.md`,
  `migration_context/backend/services/SUB_SetAggregatedIventoryHelper.md`,
  `SUB_LoadAggregatedInventoryTotals.md`, `SUB_LoadAggregatedInventory.md`,
  `migration_context/backend/dtos/ImportMappings_ImportMapping/IM_AggregatedInventory.md`,
  `migration_context/backend/dtos/JsonStructures_JsonStructure/JSON_AggregatedInventory.md`,
  `migration_context/backend/domain/Constants_Constant/CONST_SF_QueryPageSize.md`,
  `migration_context/backend/services/JavaActions_JavaAction/JA_SnowflakeToMendix.md`
- ADRs: `docs/architecture/decisions.md` → 2026-04-17 aggregated
  inventory, 2026-04-13 PWS email (async pattern template)
- Related plan: `docs/tasks/todos-resolution-plan.md` Theme 2 —
  Snowflake sync infrastructure
- Existing schema: `backend/src/main/resources/db/migration/V60__auctions_aggregated_inventory.sql`
- Existing service: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java`
