# Architecture Decisions Log

Running log of notable technical decisions. Each entry is lightweight
ADR-style: context, decision, consequences. Newest first.

---

## 2026-04-20 — Auction lifecycle cron: per-row tx, ShedLock leader election, event-driven downstream

**Status:** Accepted (sub-project 0 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `AuctionUI.ACT_ScheduleAuctionCheckStatus` runs every minute and
transitions auction rounds (`Scheduled→Started`, `Started→Closed`),
fanning out into Round 1/2/3 init, bid ranking, target price calc,
special-buyer processing, and Snowflake push. The downstream tree is the
entire Mendix bid engine. We split the port into seven sub-projects;
this ADR covers sub-project 0 — the cron skeleton and event contract
that the other six will subscribe to.

### Decision

- **Multi-instance safety via ShedLock + `infra.shedlock`.** Spring
  `@Scheduled` runs on every JVM by default; ShedLock's
  `@SchedulerLock(name="auctionLifecycle", lockAtLeastFor=10s,
  lockAtMostFor=55s)` ensures only one node executes per tick.
  `lockAtMostFor < poll-ms` so a crashed leader is reclaimable on the
  next minute.
- **Per-row REQUIRES_NEW transactions.** The orchestrator
  (`AuctionLifecycleService.tick()`) opens no tx itself; each row
  transition runs in its own short tx via `RoundTransitionService`,
  guarded by a `SELECT ... FOR UPDATE` re-check. One bad row logs at
  ERROR and is skipped; others proceed. Trades the legacy Mendix
  all-or-nothing atomicity for forward-progress under partial failure.
- **Hybrid event contract.** Cron-driven transitions emit
  `RoundStartedEvent` / `RoundClosedEvent` (post-commit). The existing
  `AuctionScheduledEvent` / `AuctionUnscheduledEvent` continue to fire
  from the admin HTTP flows. Downstream listeners filter on `round`
  field instead of subscribing to a generic transition event.
- **Reusable `infra` schema.** `infra.scheduled_job_run` records every
  tick (status, duration, counters JSONB, node id) and is reusable for
  any future cron job. `infra.shedlock` is the ShedLock JDBC table.
  Audit shape is *lightweight + payload counters* — same write cost as
  status-only, ~10× more useful for forensic debugging.
- **Six stub listeners ship with this PR.** They log "would do X" and
  define the contract sub-projects 1-6 must preserve when they replace
  each stub with real logic (Snowflake push, R1/R2/R3 init, bid
  ranking, special-buyer processing, R3 pre-process).

### Alternatives considered

- **Postgres advisory lock** (`pg_try_advisory_lock`) — simpler than
  ShedLock, no new dependency. Rejected because the user explicitly
  anticipates many more cron jobs, and ShedLock provides per-job naming
  and TTL semantics out of the box.
- **Single tx for the whole tick** (Mendix shape) — preserves legacy
  atomicity. Rejected because one bad row would block all others on
  every tick until manually cleared; per-row tx prefers liveness.
- **Reuse `AuctionScheduleService` for the cron entry point.**
  Rejected — it conflates user-driven actions (HTTP, validates against
  bids/started rounds) with system-driven actions (cron, no validation,
  just transitions). Different invariants, different audit needs,
  different test surface.

### Consequences

- A round picked up by the selector but flipped between selector and
  `FOR UPDATE` re-check throws `RoundAlreadyTransitionedException`,
  which the orchestrator treats as benign and logs at DEBUG only.
- Listener failures do NOT roll back the round transition (they fire
  post-commit). Sub-projects 1-6 must each handle their own retry /
  DLQ semantics.
- The `counters` JSONB allows future jobs to add their own keys without
  schema migrations — but the existing four (`roundsStarted`,
  `roundsClosed`, `auctionsAffected`, `errorCount`) are part of the
  audit contract and cannot be renamed.
- Tests disable the cron via `application-test.yml`; integration tests
  drive `service.tick()` (or `scheduler.runTick()`) directly.
- Coverage target on `service/auctions/lifecycle/**` and
  `infra/scheduledjob/**` is 90%+ — central code, must stay tested.

### References

- Plan: `docs/tasks/auction-lifecycle-cron-plan.md`
- Spec: `docs/tasks/auction-lifecycle-cron-design.md`
- Schema: `V70__infra_schema_and_scheduled_job_run.sql`,
  `V71__infra_shedlock.sql`
- Mendix source: `migration_context/backend/ACT_ScheduleAuctionCheckStatus.md`,
  `ACT_SetAuctionScheduleClosed.md`, `ACT_SetAuctionScheduleStarted.md`,
  `services/SUB_SetAuctionStatus.md`

---

## 2026-04-20 — Auction lifecycle: Create persists only the auction row; Schedule persists the rounds (partial supersession of 2026-04-19)

**Status:** Accepted. Partially supersedes the 2026-04-19 ADR
("Create Auction: dedicated endpoint + in-tx round creation +
enum-as-varchar"). The enum-as-varchar, dedicated-endpoint, and
method-security decisions from the prior ADR remain in force.

### Context

Porting the Mendix Auction Scheduling + Confirm flow
(`docs/tasks/auction-scheduling-plan.md`) surfaced three divergences
between the 2026-04-19 implementation and the Mendix source of truth:

1. **Title example in the ADR is wrong.** The narrative used
   `"Auction Week 17 2026"`, but `Week.weekDisplay` has shape
   `"YYYY / WkNN"` (see `V58__create_auctions_schema_and_core.sql:29`
   and `V65__seed_mdm_week.sql:42`). Existing legacy rows in
   `auctions.auctions` use titles like `"Auction 2026 / Wk04"`. The
   runtime code in `AuctionService.buildAuctionTitle` is already
   correct — only the ADR example was misleading.
2. **Round-creation timing is wrong (architectural).** The 2026-04-19
   ADR stated that `POST /admin/auctions` writes the auction row plus
   three `SchedulingAuction` rows in one tx. Mendix
   `ACT_Create_Auction` only writes the Auction row plus a transient
   helper; the three rounds are persisted later by
   `ACT_SaveScheduleAuction` when the admin clicks **Confirm** on the
   scheduling page. Our current endpoint conflates the two Mendix
   steps.
3. **Round 3 display name is wrong.** The ADR and current code emit
   `"Round 3"`. Mendix `ACT_SaveScheduleAuction` uses the verbatim
   string `"Upsell Round"`, which surfaces in the scheduling UI and
   in email templates.

### Decision

- **Title example correction.** The canonical title is
  `"Auction " + Week.weekDisplay`, e.g. `"Auction 2026 / Wk04"`. No
  code change — this is a narrative fix only.
- **Split Create from Schedule.** `POST /admin/auctions` will persist
  **only** the `auctions.auctions` row (status `Unscheduled`). A new
  `PUT /admin/auctions/{id}/schedule` will persist the three
  `auctions.scheduling_auctions` rows and flip the auction to
  `Scheduled` in a single tx. The lifecycle invariant is explicit:
  - `Unscheduled` → exactly 0 rounds.
  - `Scheduled` / `Started` / `Closed` → exactly 3 rounds.
- **Round 3 name.** Round 3's display name is `"Upsell Round"`, not
  `"Round 3"`. Rounds 1 and 2 keep their numeric labels. The name is
  a presentation concern; the underlying `round` column stays `1|2|3`.
- **No data migration.** No auctions have been created through the
  new-app endpoint in any real environment yet, so refactoring does
  not require backfilling or splitting existing rows.

### Consequences

- `AuctionService.createAuction` will be refactored in Phase B of
  `docs/tasks/auction-scheduling-plan.md` to stop writing rounds.
  A new `AuctionService.scheduleAuction(id, offsets)` will own the
  round persistence and status flip.
- `docs/api/rest-endpoints.md` will be updated: the `rounds[]` block
  drops out of the `POST /admin/auctions` `201` response, and a new
  `PUT /admin/auctions/{id}/schedule` section is added.
- The 2026-04-19 ADR stays in place as historical context — its
  decisions on enum storage (`VARCHAR(20)` + `@Enumerated(STRING)`),
  the dedicated `/api/v1/admin/auctions` controller, the explicit
  `SecurityConfig.requestMatchers` rule for SalesOps, and the two
  distinct 409 error shapes (duplicate title vs. duplicate week)
  remain in force and are not revisited here.
- The existing atomicity argument from the 2026-04-19 ADR
  (auction + rounds must commit together) still applies — but now it
  applies to the `PUT .../schedule` transaction, not to `POST`. An
  `Unscheduled` auction with zero rounds is a valid, expected state.
- Tests: the seven `AuctionServiceTest` cases and four
  `AuctionControllerTest` cases will be re-shaped in Phase B. No
  test is deleted; assertions on round creation move to the new
  schedule-service tests.

### References

- Plan: `docs/tasks/auction-scheduling-plan.md`
- Amended ADR: 2026-04-19 — Create Auction (this file, below)
- Mendix source:
  `migration_context/backend/ACT_Create_Auction.md`,
  `migration_context/backend/ACT_SaveScheduleAuction.md`,
  `migration_context/backend/ACT_Create_SchedulingAuction_Helper_Default.md`
- Schema: `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`,
  `V65__seed_mdm_week.sql`

---

## 2026-04-19 — Create Auction: dedicated endpoint + in-tx round creation + enum-as-varchar

**Status:** Accepted (Phases 1–3 of `docs/tasks/create-auction-plan.md`).

### Context

The admin inventory page's `Create Auction` button was a placeholder
`alert()` stub. Mendix `ACT_Create_Auction` writes one
`AuctionUI.Auction` plus three `SchedulingAuction` rows (rounds 1–3)
with fixed hour offsets from `Week.weekStartDateTime`, case-insensitive
title uniqueness, and one-auction-per-week invariants. The port needs
to:

1. Place the write behind a stable REST surface without coupling to
   `/admin/inventory`.
2. Persist the three rounds atomically so a partial commit can't leave
   an auction without rounds (which would crash the round editor).
3. Represent the four status enums (`AuctionStatus`,
   `SchedulingAuctionStatus`, `ScheduleAuctionInitStatus`,
   `ReminderEmails`) in a way that matches V58's `CHECK` constraints
   without adding a separate lookup table.

### Decision

- **New controller at `/api/v1/admin/auctions`.** A dedicated
  `AuctionController` owns the POST rather than bolting onto
  `AggregatedInventoryController`. The resource is `auctions`, not
  `inventory`; a follow-up round-edit screen will add `GET /{id}` and
  `PUT /{id}/rounds/{r}` to the same controller without touching the
  inventory surface. Method-level `@PreAuthorize("hasAnyRole('Administrator','SalesOps')")`
  is paired with an explicit `SecurityConfig.requestMatchers` rule
  because the filter-chain short-circuits `/api/v1/admin/**` on the
  Administrator role before method security runs — without the explicit
  matcher, SalesOps gets 403 at the filter instead of reaching
  `@PreAuthorize`. This pattern matches the existing
  `/api/v1/admin/inventory` rule.
- **In-transaction round creation, no post-commit event.** Unlike the
  PWS email flow (2026-04-13 ADR) and Snowflake sync (2026-04-18 ADR),
  Create Auction has no external side effects — no email, no Snowflake
  push, no notification. All writes belong in the same `@Transactional`
  method. Splitting the rounds into a post-commit listener would
  introduce the exact partial-state bug the atomic write prevents.
- **Enum storage: `VARCHAR(20)` with `@Enumerated(EnumType.STRING)`.**
  V58 already enforces values with CHECK constraints:
  `chk_auctions_status IN ('Unscheduled','Scheduled','Started','Closed')`
  et al. `EnumType.STRING` makes Java and Postgres representations
  identical so a DB browser round-trip doesn't show opaque ordinals.
  `EnumType.ORDINAL` is rejected: renaming an enum constant in code
  would silently reinterpret existing rows. A dedicated lookup table
  is overkill for four immutable states.
- **`Week` as entity-less `Long` FK.** `Auction.weekId` is a plain
  `Long`, not `@ManyToOne Week`. The round editor and overview pages
  will load the `Week` via `WeekRepository` when they land; we avoid
  premature bidirectional mapping and keep `Auction` small.
- **Title composition mirrors Mendix verbatim.** `AuctionService.buildAuctionTitle`
  returns `"Auction " + weekDisplay.trim()`, then appends
  `" " + customSuffix.trim()` when the suffix is non-blank. Matches
  `VAL_Create_Auction` character-for-character — important because the
  `existsByAuctionTitleIgnoreCase` check runs against Mendix-written
  history.
- **Two distinct 409s, disambiguated by message.** The backend throws
  `DuplicateAuctionTitleException` (title collision, most common admin
  mistake — shown as inline field error in the modal) and
  `AuctionAlreadyExistsException` (week collision, a race the frontend
  also guards via `totals.hasAuction` — shown as a banner). Both map
  to HTTP 409 via `GlobalExceptionHandler`; the frontend distinguishes
  by substring-matching `"name already exists"` in the response body.
  Typed client-side error classes (`DuplicateAuctionTitleError` /
  `AuctionAlreadyExistsError`) make the distinction explicit at the
  call site.
- **Frontend button gating.** The button is rendered only when
  `hasInventory && isCurrentWeek && !hasAuction`. Mirrors the Mendix
  visibility expression and avoids a second API call — the helper
  flags land as part of `/admin/inventory/totals` (2026-04-18 ADR).
  Creating an auction triggers a `refresh()` that flips `hasAuction`
  to `true`, causing the button to disappear on the next render.

### Alternatives considered

- **Extend `AggregatedInventoryController`.** Rejected: the controller
  name would lie about its responsibility, and the round-edit /
  overview endpoints coming later would compound the mismatch.
- **Post-commit event for round creation.** Rejected: atomic failure
  semantics matter — a rolled-back auction must not leave three
  orphaned rounds pointing at a non-existent `auction_id`.
- **Dedicated lookup tables for status enums.** Rejected: the four
  states are immutable Mendix constants; a FK adds joins and migration
  surface for zero benefit.
- **DB-level partial unique index `(week_id) WHERE auction_status <> 'Closed'`.**
  Deferred: `existsByWeekId` inside the tx is sufficient for the
  single-admin flow; a concurrent double-click is the only realistic
  race and would produce a 409 on the second attempt after the first
  commits. Revisit if QA surfaces it.

### Consequences

- One auction + three rounds always commit together — the round editor
  can assume the rounds exist whenever an auction does.
- Admin users see the button disappear immediately after success
  without a second totals fetch — the existing `refresh()` call
  already refetches `/totals`.
- Enum renames now require a two-step migration (alter CHECK
  constraint + rewrite rows) instead of a silent ordinal shift — a
  win for safety, a small cost at migration time.
- Test coverage: 7 unit tests in `AuctionServiceTest` (happy path,
  custom suffix, whitespace suffix, missing week, duplicate week,
  duplicate title, null weekId) + 4 MockMvc tests in
  `AuctionControllerTest` (admin 201 + Location header, SalesOps 201,
  bidder 403, duplicate title 409). All green.
- Follow-ups:
  - Round editor screen — will add `GET /admin/auctions/{id}` and
    `PUT /admin/auctions/{id}/rounds/{r}` to `AuctionController`.
  - `snowflake_json` audit push — separate nightly job, separate ADR.
  - `ACT_UnscheduleAuction` port — separate plan once the round
    editor lands.

### References

- Plan: `docs/tasks/create-auction-plan.md`
- API: `docs/api/rest-endpoints.md` (`## Auctions (Admin)`)
- Schema: `backend/src/main/resources/db/migration/V58__auctions_core.sql`
- Files: `controller/AuctionController.java`,
  `service/auctions/AuctionService.java`,
  `model/auctions/Auction.java`,
  `model/auctions/SchedulingAuction.java`,
  `security/SecurityConfig.java`,
  `frontend/src/lib/auctions.ts`,
  `frontend/.../inventory/CreateAuctionModal.tsx`
- Related ADRs: 2026-04-18 (helper flags in `/totals`),
  2026-04-13 (post-commit event pattern used elsewhere but not here)
- Mendix source:
  `migration_context/backend/ACT_Create_Auction.md`,
  `migration_context/backend/domain/VAL_Create_Auction.md`,
  `migration_context/frontend/components/Pages_Page/Create_Auction.md`

---

## 2026-04-18 — Aggregated Inventory: non-DW KPI totals must filter DW-only groups

**Status:** Accepted (Phase 9 pixel-match reconciliation of
`docs/tasks/aggregated-inventory-snowflake-sync-plan.md`).

### Context

After Wk17 2026 data landed in `auctions.aggregated_inventory` via the
new Snowflake sync, the admin KPI strip diverged from Mendix QA:

| KPI | QA (Mendix) | Local (pre-fix) | Delta |
|---|---|---|---|
| Total Payout | $1,855,306.30 | $2,043,895.24 | +$188,588.94 |
| Avg Target Price | $42.17 | $93.27 | +$51.10 |
| Total Quantity, DW KPIs | all match | all match | 0 |

Root cause: Snowflake's `DATA_QUERY` in `SnowflakeAggInventoryReader`
wraps TotalPayout and AvgTargetPrice in
`COALESCE(SUM(CASE WHEN Data_WIPE_GOOD='' ...), SUM(CASE WHEN Data_WIPE_GOOD='DW' ...))`.
For groups where every device is DW (so the non-DW CASE sums to 0), the
COALESCE falls through and the DW aggregate is persisted into the
**non-DW columns** `total_payout` / `avg_target_price`. Mendix's totals
row (`DEVICE_ID = 'Total'`) is computed upstream and already excludes
these DW-only groups, which is why the legacy UI matched.

Our `AggregatedInventoryService.getTotals()` was summing
`total_payout` and weighting `avg_target_price` by `total_quantity`
unconditionally, so DW-only groups were double-counted — once in the
non-DW KPIs and once in the DW KPIs.

### Decision

- **Filter DW-only groups from the non-DW KPIs.** `getTotals()` now uses
  PostgreSQL `FILTER (WHERE a.total_quantity > a.dw_total_quantity)` on
  the non-DW sum and weighted average. A group is "non-DW" when its
  total quantity exceeds its DW quantity; if they're equal, the group
  is DW-only and excluded.
- **Weighted average uses the non-DW slice of quantity.** The divisor
  becomes `SUM(total_quantity - dw_total_quantity)` filtered to the
  same predicate, matching Mendix's Total-row math.
- **DW KPIs are unchanged.** `dw_total_payout`, `dw_total_quantity`,
  and `dw_avg_target_price` already read from the DW-specific columns,
  which are accurate regardless of the COALESCE behavior above.

### Why not fix it upstream in the Snowflake SQL?

Tempting, but the Mendix source of truth (`JA_SnowflakeToMendix` →
`DATA_QUERY`) uses the same COALESCE pattern, and the grand-total row
Mendix UI consumes is computed by the same upstream query. Changing
the reader SQL to split non-DW vs DW into separate columns would
diverge from Mendix's row-level shape and complicate the eventual
parity comparison of the per-row grid. Filtering at read time in
Postgres is cheap, local, and leaves the Mendix-parity column layout
intact.

### Also in this commit: Snowflake JDBC driver quirks

Two reader-layer fixes were required to get rows flowing at all:

- **Case-sensitive column labels.** Snowflake JDBC uppercases every
  unquoted alias in the result set metadata, so `rs.getString("ecoID")`
  silently returned `null` on every row. `mapRow` now uses the
  canonical uppercase form for all ResultSet lookups
  (`ECOID`, `MAXUPLOADTIME`, `AVGTARGETPRICE`, etc.). The SQL aliases
  can stay mixed-case — it's the runtime label lookup that matters.
- **TIMESTAMP_TZ → VARCHAR fallback.** When per-row TZ offsets differ
  from the session `TIMEZONE`, the driver can surface the column as a
  VARCHAR, and `rs.getTimestamp` throws. `readUtcTimestamp` now catches
  the SQLException and falls back to parsing the string via
  `OffsetDateTime` / `LocalDateTime`. The `MaxUploadInventory` CTE also
  exposes `MaxUploadTime` as
  `CONVERT_TIMEZONE('UTC', MAX(Upload_Time))::TIMESTAMP_NTZ` so the
  materialized column is portable across clients; the inner join still
  uses the raw `MaxUploadTimeRaw` alias so equality compares at the
  original precision.

### Consequences

- All six admin KPIs (Total Quantity, Total Payout, Avg Target Price
  plus their DW counterparts) match Mendix QA to the cent for Wk17
  2026 (11,735 rows).
- Future Snowflake syncs remain drift-free because the filter is keyed
  on the row-level invariant `total_quantity > dw_total_quantity`, not
  on any sync-time flag.
- Unit tests in `AggregatedInventoryServiceTest` continue to pass —
  they mock the EntityManager return values, so the SQL change is
  transparent to the test suite. An integration test covering the
  DW-only-group case would strengthen coverage and is queued as
  follow-up.

### References

- Plan: `docs/tasks/aggregated-inventory-snowflake-sync-plan.md` (Phase 9)
- Files: `service/auctions/AggregatedInventoryService.java`,
  `service/auctions/snowflake/SnowflakeAggInventoryReader.java`
- Related ADRs: 2026-04-18 (sync pattern), 2026-04-17 (read-time totals)
- Mendix source:
  `migration_context/backend/services/JavaActions_JavaAction/JA_SnowflakeToMendix.md`,
  `SUB_LoadAggregatedInventoryTotals.md`

---

## 2026-04-18 — Aggregated Inventory: Snowflake sync via post-commit event + watermark-based incremental pull

**Status:** Accepted (Phases 0–8 of `docs/tasks/aggregated-inventory-snowflake-sync-plan.md`).

### Context

The Mendix legacy flow synced `AggregatedInventory` from the Snowflake
`Master_Inventory_List_Snapshot` table on every `AggregatedInventory_Home`
page open via a synchronous `JA_SnowflakeToMendix` microflow. The port needs to:

1. Avoid blocking the admin page on Snowflake response time (the legacy
   synchronous UX regularly stalled 30s+ on the first visit each day).
2. Not overwrite admin edits that flipped `is_total_quantity_modified = true`
   (honors the 2026-04-17 ADR).
3. Skip work when Snowflake has no newer data (Mendix used
   `AggregatedInventoryTotals.MaxUploadTime` for this; we need an equivalent).
4. Gate prod-only wiring so local dev / CI never hit Snowflake.

### Decision

- **Post-commit event bridge.** `AggregatedInventoryController.triggerSync`
  publishes `AggInventorySyncRequestedEvent`. `AggInventorySyncListener`
  handles it via `@TransactionalEventListener(phase = AFTER_COMMIT)` +
  `@Async("snowflakeExecutor")`. HTTP returns `202 Accepted` immediately;
  the actual sync runs on a dedicated 3-thread executor. Mirrors the
  2026-04-13 PWS email pattern.
- **Watermark-based incremental pull.** New table
  `auctions.week_sync_watermark (week_id, source, last_source_upload_at,
  last_synced_at, row_count)` PK `(week_id, source)`. Before each sync,
  `AggregatedInventorySnowflakeSyncService` runs a cheap
  `SELECT MAX(Upload_Time) FROM Master_Inventory_List_Snapshot
  WHERE Auction_Week=? AND Auction_Year=?`. If the returned timestamp is
  ≤ the stored `last_source_upload_at`, the sync short-circuits with
  status `SKIPPED_UP_TO_DATE`. Otherwise it runs the paginated data query and
  bumps the watermark on success.
- **Admin-override preservation.** The upsert filters out rows where
  `is_total_quantity_modified = true`. Those rows keep their
  admin-entered `total_quantity` and `dw_total_quantity`; all other
  columns still refresh from Snowflake. Matches the legacy Mendix
  `SUB_UpdateAggregatedInventory` branch that skipped the quantity
  overwrite when the flag was set.
- **Drop the `DEVICE_ID='Total'` row at the reader layer.** The
  Snowflake SQL appends a grand-total row via `UNION ALL`. Per the
  2026-04-17 ADR we compute totals at read time, so
  `SnowflakeAggInventoryReader` filters the Total row before batching.
- **Feature flag.** `snowflake.enabled` (default `false`) gates
  `SnowflakeDataSourceConfig`, the reader, the sync service, and the
  listener via `@ConditionalOnProperty`. When disabled, the trigger
  endpoint returns `{ "status": "SKIPPED_DISABLED" }` and no thread pool
  is provisioned. `SnowflakeSyncLogRepository` is always present so the
  status endpoint works in both modes.
- **Fail-fast on misconfigured prod.** `SnowflakeDataSourceConfig`
  throws at startup when `snowflake.enabled=true` and
  `username`/`password` are blank. Prevents silent "sync always returns
  `SKIPPED_DISABLED`" in a deploy that intended the opposite.
- **Frontend fire-and-forget.** `/admin/auctions-data-center/inventory`
  posts to the trigger endpoint on every week change and polls the
  status endpoint every 3s for up to 90s. `403` from the trigger
  (non-admin role) is swallowed so the read-only view still renders.

### Alternatives considered

- **Scheduled nightly pull (cron).** Rejected: admins expect the page
  to reflect the current day's Snowflake state, not yesterday's. A
  per-view trigger is cheaper overall because most weeks don't change
  intraday (the watermark short-circuits the work).
- **Synchronous pull inside the page request (Mendix 1:1).** Rejected
  — 30s+ latency on first visit each day is unacceptable and the
  Mendix UX regularly hung on it.
- **Materialize totals in `aggregated_inventory_totals` and have the
  sync write that too.** Rejected — the 2026-04-17 ADR already
  established read-time computation as the source of truth; writing
  both invites the exact drift bug the 2026-04-15 ADR fixed.
- **Revive Mendix-style full-table replace (truncate + insert).**
  Rejected — breaks admin-override preservation; an idempotent upsert
  keyed on `(week_id, product_id, merged_grade, datawipe)` is the
  correct shape.

### Consequences

- Admin inventory page latency is bounded by a single `202 Accepted`
  response and a 3s poll cadence; Snowflake outages never block the
  page render.
- An admin that edits a row mid-sync is safe: the override flag
  protects their quantity; all other fields will still catch up on the
  next sync.
- No DLQ in Phase 1. Failed syncs log via
  `integration.snowflake_sync_log` (status=`FAILED`, `error_message`
  populated). The status endpoint surfaces the failure; monitoring
  must tail the log table.
- Enabling prod delivery is a config-only change (`SNOWFLAKE_ENABLED=true`
  + credentials) with no redeploy.
- Test coverage: unit tests on reader, service (6 scenarios), listener
  (commit fires + rollback does not), controller (MockMvc
  `202`/`200`/`403`). Integration test is gated on
  `@EnabledIfEnvironmentVariable("SNOWFLAKE_IT", "true")` so it only
  runs in envs with real Snowflake creds.

### References

- Plan: `docs/tasks/aggregated-inventory-snowflake-sync-plan.md`
- Schema: `backend/src/main/resources/db/migration/V67__auctions_week_sync_watermark.sql`
- Related ADRs: 2026-04-17 (read-time totals computation),
  2026-04-13 (PWS email async pattern template)
- Mendix source:
  `migration_context/backend/services/JavaActions_JavaAction/JA_SnowflakeToMendix.md`,
  `SUB_UpdateAggregatedInventory.md`,
  `SUB_LoadAggregatedInventoryTotals.md`

---

## 2026-04-17 — Aggregated Inventory: compute totals at read time + keep quantity override flag

**Status:** Accepted (Phases 3.2 and 5.1 of `docs/tasks/aggregated-inventory-page-plan.md`).

### Context

Mendix `AggregatedInventoryTotals` stored one row per week with precomputed
sums. Mendix also exposed `isTotalQuantityModified` on `AggregatedInventory`
so a nightly sync could tell whether to overwrite an admin-edited quantity.
Porting 1:1 would duplicate state and require a scheduled refresher. The
legacy bug surface is the same one the buyer-overview ADR (2026-04-15) fixed.

### Decision

- **Totals computed at read time** from `auctions.aggregated_inventory`
  with weighted averages (`SUM(price * qty) / SUM(qty)`) rather than
  reading `auctions.aggregated_inventory_totals`. The totals table stays
  in the schema for future Snowflake export parity but is not read by the
  page.
- **Preserve `is_total_quantity_modified`** exactly as the legacy column.
  The PUT handler flips it to `true` on save, and the (future) inventory
  sync must honor the flag.
- **Excluded rows:** `is_deprecated = true` rows never appear in the grid
  or the KPI strip.

### Alternatives considered

- **Read `aggregated_inventory_totals` directly.** Rejected for the same
  reason as the buyer overview: the denormalized row drifts whenever an
  admin edit lands out-of-band. Recomputing is O(N) over ~87k rows
  filtered by `week_id` — sub-second with the `idx_agi_week` index.
- **Materialized view.** Over-engineered for 87k rows. Revisit if
  `EXPLAIN ANALYZE` regresses past 200 ms.

### Consequences

- The totals table is now informational only. A follow-up can either
  drop it or wire the Snowflake sync to populate it from the same query.
- Create Auction remains a stub until the auction scheduling module
  lands — the page's button shows a placeholder dialog rather than a
  404 or a dead action.

### References

- Plan: `docs/tasks/aggregated-inventory-page-plan.md`
- Schema: `backend/src/main/resources/db/migration/V60__auctions_aggregated_inventory.sql`
- Mendix source: `migration_context/frontend/components/Pages_Page/PG_AggregatedInventory.md`

---

## 2026-04-13 — Auth token moved from localStorage to HttpOnly cookie

**Status:** Accepted — backend portion shipped (Phase 3.1 of Theme 3,
`docs/tasks/todos-resolution-plan.md`). Frontend atomic migration
(Phase 3.2) tracked separately; must ship in the same deploy window.

### Context

The frontend stored the JWT in `localStorage` and attached it via an
`Authorization: Bearer …` header. Any XSS vulnerability — including
transitive ones in third-party React components — would allow an
attacker to exfiltrate the token and impersonate the user for its full
lifetime (up to 7 days with "remember me"). `TODO(H-5)` in
`frontend/src/app/(auth)/login/LoginForm.tsx:46` flagged this.

### Decision

- **Cookie-issued JWT.** `AuthController.loginExternalUser` now returns
  a `Set-Cookie: auth_token=…; HttpOnly; Secure; SameSite=Strict;
  Path=/; Max-Age=…` header on successful login. The token is no longer
  serialized in the JSON body (`LoginResponse.token` is `@JsonIgnore`d —
  kept internally as a carrier between `AuthService` and the controller).
- **TTL.** 8 hours for standard login, 24 hours when `rememberMe=true`.
  The JWT signing expiry (`app.jwt.*-expiration-ms`) is unchanged and
  remains the authoritative clock — the cookie `Max-Age` just stops the
  browser from presenting a token past the point the server would reject
  it anyway.
- **`Secure` flag is configurable.** `auth.cookie.secure` defaults to
  `false` so `http://localhost` dev works; the `production` profile and
  QA env var `AUTH_COOKIE_SECURE=true` flip it on. `HttpOnly` and
  `SameSite=Strict` are enforced in code and not configurable.
- **Filter fallback.** `JwtAuthenticationFilter` extracts the token from
  `Authorization` header first (server-to-server, tests, transitional
  clients), then from the `auth_token` cookie. Both paths feed the same
  `jwtService.isValid` + `SecurityContextHolder` pipeline.
- **Logout.** New `POST /api/v1/auth/logout` (permitAll) returns
  `204 No Content` with `Set-Cookie: auth_token=; Max-Age=0; …` to
  expire the cookie in the browser. The JWT itself is not revoked —
  without a denylist, logout is best-effort client-side.
- **CSRF stays disabled.** `SameSite=Strict` blocks cross-site form
  submissions from carrying the cookie, which is the realistic CSRF
  vector for a cookie-auth API. A full double-submit token scheme would
  add friction without meaningful risk reduction for this app's flows.

### Alternatives considered

- **Keep `localStorage` + add CSP.** Rejected: CSP reduces XSS surface
  but does not eliminate it; any inline-script escape still exposes the
  token. `HttpOnly` is a structurally stronger guarantee.
- **`SameSite=Lax`.** Considered for the eventual email-link flow (R9
  in the plan) where a GET navigation from an email client should
  arrive authed. Deferred: none of the current flows require it, and
  `Strict` is the stronger default. Revisit if marketing-link flows
  need it.
- **Revoke JWT on logout via denylist.** Rejected for Phase 1: no Redis
  in the stack yet, and the 8h/24h ceilings limit the blast radius.

### Consequences

- Frontend must stop reading/writing the token in any form —
  `apiFetch.ts` switches to `credentials: 'include'`, SSR fetches
  forward the `Cookie` header, and a ~12-file audit strips
  `localStorage.getItem('auth_token')` call sites. This work is Phase
  3.2 and must ship in the **same deploy** as the backend — half-deploys
  break login.
- Backward compatibility: the `Authorization` header path still works,
  so Postman collections, integration tests, and any lingering
  server-to-server consumer keep functioning during the transition.
- Test coverage: `AuthControllerTest` adds four new assertions
  (`login_withValidCredentials_setsHttpOnlyCookieAndOmitsTokenFromBody`,
  `login_withRememberMe_setsLongerMaxAge`, `logout_clearsCookie`,
  `me_withCookieToken_returnsUserInfo`). 15/15 green.
- Follow-ups:
  - **SSO callback cookie issuance.** `AuthController.handleSSORedirect`
    is still a pre-auth outbound redirect stub — there is no token to
    cookie yet. When the real callback lands it MUST reuse
    `buildAuthCookie(token, DEFAULT_TTL)` and attach the `Set-Cookie`
    header on the post-auth redirect. An inline `TODO(Theme 3)` now
    sits above the stub so the next SSO PR cannot miss it.
  - **Registrable-domain parity.** `SameSite=Strict` requires the
    frontend and backend in prod to share the same eTLD+1 for the
    browser to attach the cookie. Approved topologies and a
    verification checklist live in `docs/deployment/environments.md`
    under *Auth Cookie (Theme 3) → Registrable-domain parity*.
    Preferred deployment shape: reverse-proxy `/api/*` through the
    frontend origin so the question never arises.

### References

- Plan: `docs/tasks/todos-resolution-plan.md` (Theme 3, Phase 3.1)
- Source TODO: `frontend/src/app/(auth)/login/LoginForm.tsx:46`
- Files: `controller/AuthController.java`, `dto/LoginResponse.java`,
  `security/JwtAuthenticationFilter.java`, `security/SecurityConfig.java`,
  `resources/application.yml`

---

## 2026-04-13 — PWS email delivery: post-commit event + async + feature flag

**Status:** Accepted (Theme 1 of `docs/tasks/todos-resolution-plan.md`).

### Context

Mendix parity requires four buyer-facing notifications on the PWS flow:
offer confirmation, order confirmation, pending-order (adjusted quantity),
and counter offer. The legacy Mendix microflows (`SUB_SendPWS*Email.md`)
fire synchronously inside the offer/order transaction. Porting that shape
1:1 exposes three risks:

1. **R1 — transactional coupling:** an SMTP outage would fail an offer
   submission that the DB already accepted.
2. **R3 — wrong recipients in lower envs:** dev/CI must never reach real
   buyers, and QA must be enablable independently of prod.
3. **R4 — rollback leakage:** a notification sent before the tx commits
   would be visible to customers even if the offer rolls back.

### Decision

- **Post-commit event bridge.** `OfferService` / `OfferReviewService`
  publish a sealed `PwsOfferEmailEvent` via `ApplicationEventPublisher`
  from inside the business tx. `PwsOfferEmailListener` handles each variant
  with `@TransactionalEventListener(phase = AFTER_COMMIT)` +
  `@Transactional(propagation = REQUIRES_NEW, readOnly = true)`. Rollbacks
  drop the event — no mail is sent.
- **Async executor.** The listener hands the materialized aggregate to
  `PWSEmailService`, whose send methods run on a dedicated
  `emailExecutor` (`AsyncConfig.EMAIL_EXECUTOR`) via `@Async`. Offer
  submission latency is unaffected by SMTP response time.
- **Eager fetch across async boundary.** `OfferRepository.findByIdWithItems`
  uses `LEFT JOIN FETCH o.items` so the listener's short read-only tx
  produces a fully-initialized `Offer` before the aggregate crosses
  threads — avoiding `LazyInitializationException` on the @Async worker.
- **Feature flag.** `pws.email.enabled` (default `false`) swaps
  `LoggingEmailSender` ↔ `SmtpEmailSender` via `@ConditionalOnProperty`.
  Local dev and CI log payloads only; QA and prod enable per
  `docs/deployment/environments.md`.
- **Retry + swallow.** `SmtpEmailSender.send` is
  `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))`
  on `MailException`/`MessagingException`. After the final retry,
  `PWSEmailService.safeSend` catches the exception, logs
  `PWS email delivery failed template={} offerId={} offerNumber={}`, and
  never rethrows — addressing R1.
- **Recipient resolution via native queries.**
  `EcoATMDirectUserRepository.findActiveEmailsByBuyerCodeId` and
  `findBuyerCompanyNameByBuyerCodeId` encode the Mendix recipient rules.
  The adjusted-quantity template CCs `pws.email.sales-address`.
- **Templates.** Thymeleaf HTML templates under
  `resources/templates/email/pws-*.html`, rendered with Mendix-parity
  styling tokens (`Trebuchet MS`, `#514F4E`, `#B7B5B5`, `#2CB34A`,
  `ecoATM, LLC` footer).

### Alternatives considered

- **Synchronous in-tx send (Mendix 1:1).** Rejected: couples offer
  submission availability to SMTP, and rollbacks would leak mail.
- **Outbox table + poller.** Heavier than needed for Phase 1 and
  duplicates work that Theme 2 (`integration.snowflake_sync_log`) will
  introduce for a different purpose. Revisit if a retry budget / DLQ is
  required — see "No DLQ yet" in `environments.md`.
- **Spring `ApplicationEventPublisher` without `AFTER_COMMIT`.** Rejected:
  default sync listeners fire before commit, re-introducing R4.

### Consequences

- Offer submission latency is bounded by DB commit, not SMTP.
- An SMTP outage drops notifications silently after 3 retries; there is
  no dead-letter queue in Phase 1. Monitoring must watch the
  `PWS email delivery failed` log line.
- Enabling prod delivery is a config-only change (`PWS_EMAIL_ENABLED=true`
  + SMTP env vars) with no redeploy — see
  `docs/deployment/environments.md`.
- Test coverage: 6 unit tests (`PWSEmailServiceTest`) mock the template
  engine and sender to assert recipient branching and failure-swallowing;
  1 end-to-end test (`PWSEmailServiceIT`) wires a real `TemplateEngine` +
  `SmtpEmailSender` → GreenMail on `ServerSetupTest.SMTP` and asserts
  Mendix-parity markers in the rendered body.

### References

- Plan: `docs/tasks/todos-resolution-plan.md` (Theme 1)
- Config: `docs/deployment/environments.md`
- Mendix source: `migration_context/backend/SUB_SendPWSOfferEmail.md`,
  `SUB_SendPWSOrderEmail.md`, `SUB_SendPWSPendingOrderEmail.md`,
  `SUB_SendPWSCounterOfferEmail.md`
