# Architecture Decisions Log

Running log of notable technical decisions. Each entry is lightweight
ADR-style: context, decision, consequences. Newest first.

---

## 2026-04-23 — Bidder dashboard + bid_data generation

**Status:** Accepted (sub-project 3 of
`docs/tasks/auction-lifecycle-cron-design.md`; plan at
`docs/tasks/auction-bid-data-create-plan.md`).

### Context

Mendix `ACT_OpenBidderDashboard` is a landing-route microflow that (1)
picks the week's scheduling auction, (2) resolves which round's grid to
show — or which "bidding closed" copy to render — and (3) on the
`Started` branch calls `ACT_CreateBidData` to materialize one
`BidData` row per `(aggregated_inventory, bid_round, buyer_code)` tuple.
Rows are not pre-computed at round start: generation happens lazily the
first time the bidder opens the page. Subsequent opens short-circuit
because the rows already exist. Submit (`ACT_SubmitBidRound`) is a
copy-forward: `bid_*` → `submitted_*`, prior `submitted_*` →
`last_valid_*`. The round stays re-submittable until its status flips
to `Closed`.

Porting this verbatim surfaced four decisions worth recording.

### Decision

- **Synchronous generation on dashboard open.** The `GET /dashboard`
  handler, when the landing result is `GRID`, calls
  `BidDataCreationService.ensureRowsExist(buyerCodeId, bidRoundId, userId)`
  **inside the request**. First open for a `(buyer_code, round)` pair
  pays the write; all subsequent opens are a cheap existence check.
  Matches Mendix latency on the legacy flow (~1–3s for ~580 rows) and
  avoids a batch job that would have to fan out across every qualified
  buyer code at round start.
- **Single-CTE `INSERT ... SELECT` guarded by a Postgres advisory
  lock.** All 500–600 rows land in one statement. The advisory lock is
  keyed on `(bid_round_id, buyer_code_id)` so two concurrent tabs for
  the same bidder serialize inside Postgres instead of racing at the
  ORM layer. The CTE selects from `auctions.aggregated_inventory`
  joined to the QBC set flattened in V72, filtered to non-DW rows for
  Round 1. R2/R3 threshold filtering
  (`bid_meets_threshold = TRUE`) is **stubbed** in this sub-project —
  sub-project 4 will replace the placeholder with the real per-round
  qualification logic.
- **`bid_quantity` nullability is the no-cap sentinel.** `NULL` means
  "I accept any quantity up to `maximum_quantity`"; `0` means "price
  me in but ship zero units" (valid, unusual, used for hedging).
  Storing a sentinel instead of a separate flag column keeps the save
  path one UPDATE and keeps the grid's JSON body one-to-one with the
  row. Reject negative quantities with `INVALID_QUANTITY` at the
  service boundary — the frontend also clamps but the backend is the
  authority.
- **Submit is a re-callable copy-forward, not a state transition.**
  `POST /bid-rounds/{id}/submit?buyerCodeId=…` flips every row in the
  `(round, buyer_code)` slice. Calling it twice is safe: the second
  call rewrites `submitted_*` from the latest `bid_*` and stores the
  prior `submitted_*` into `last_valid_*`. The response's
  `resubmit: true` flag lets the UI show "resubmitted" confirmation
  copy. The round's own status stays `Started` until the cron closes
  it; the submit endpoint does not mutate `bid_round.round_status`.
- **Rate limit: 60 req/min per `(user, bid_round)`.** Enforced by
  `BidRateLimiter` **before** the DB write on `PUT /bid-data/{id}`.
  The limiter never touches the database on a denied request —
  important because the realistic abuse shape is a stuck auto-save
  loop firing hundreds of writes/min. Bucket scope is
  `(user, round)` not `(user, buyer_code, round)` because a single
  user + single round with N open buyer codes is not a realistic
  workload on the Mendix-parity UI; re-scoping is a one-line change
  if the assumption breaks.
- **Admin bypass at the role level, not per-endpoint.**
  `BidderDashboardController` is annotated
  `@PreAuthorize("hasAnyRole('Bidder','Administrator')")` at the class
  level. The Administrator bypass mirrors the existing admin-write
  pattern in `AuctionController` — admins can act on behalf of any
  bidder for diagnostic / recovery purposes. The `SecurityConfig`
  matcher at `/api/v1/bidder/**` must list both roles; a mismatch
  here fails closed at the filter chain before method security runs
  (same ordering pitfall as the 2026-04-19 SalesOps matcher ADR).
- **Error codes are carried in a typed field, not parsed from the
  message.** `BidDataValidationException` and
  `BidDataSubmissionException` expose a `code` getter
  (`INVALID_QUANTITY`, `INVALID_AMOUNT`, `NOT_YOUR_BID_DATA`,
  `BID_DATA_NOT_FOUND`, `ROUND_CLOSED`, `NOT_YOUR_BID_ROUND`,
  `BID_ROUND_NOT_FOUND`). `GlobalExceptionHandler` maps them to HTTP
  statuses; the frontend's typed error classes
  (`VersionConflictError`, `RoundClosedError`, `RateLimitedError`)
  check on these codes, not on substrings.

### Alternatives considered

- **Pre-generate all rows at Round 1 start.** Rejected: the R1 init
  cron listener would have to fan out across every qualified buyer
  code (~580 in prod), multiplying the listener's tx size by two
  orders of magnitude. Lazy generation distributes the write across
  the window when bidders actually open the page.
- **Row-level lock (`SELECT ... FOR UPDATE`) instead of advisory lock.**
  Rejected: there is no single row to lock — the whole point is to
  serialize the `INSERT ... SELECT` against itself. An advisory lock
  keyed on the pair is exactly the right primitive.
- **Store a `no_cap` boolean alongside `bid_quantity INT NOT NULL`.**
  Rejected: doubles the null-safety surface (both columns must agree)
  and the save path becomes a branch. `NULL` as the sentinel is
  idiomatic Postgres and one less column.
- **Return `409 Conflict` with `Retry-After` on the rate limit.**
  Rejected in favor of `429 Too Many Requests` (empty body) — the
  standard code for the scenario. The frontend's
  `RateLimitedError.retryAfterMs` is a client-side backoff and
  doesn't depend on a server header.
- **Transition `bid_round.round_status` to a synthetic `Submitted`
  value on submit.** Rejected: Mendix keeps `round_status` as
  lifecycle-only (`Scheduled | Started | Closed | Unscheduled`) and
  tracks submission on the `BidRound.submitted` flag. Adding a
  synthetic status would diverge from the cron's transition matrix.

### Consequences

- First-open latency scales with the row count for the bidder's
  buyer-code slice (~500–600 rows → ~1s in dev Postgres). Subsequent
  opens are one SELECT.
- Two concurrent tabs for the same `(bidder, round, buyer_code)`
  serialize at the advisory lock — the second tab waits for the
  first to commit, then short-circuits because rows now exist.
- Submit can be called repeatedly until the cron flips the round to
  `Closed`. An admin recovery of a stuck submit is a plain HTTP
  call, not a DB surgery.
- Rate limiter state is in-process. A horizontally-scaled backend
  would over-permit (N instances × 60/min) — acceptable for current
  single-node deploys; revisit if/when we scale out.
- The R2/R3 threshold stub (`bid_meets_threshold = TRUE`) is a known
  gap. Sub-project 4 will add the real per-round qualification
  predicates; the frontend contract (rows filtered to "qualified
  only" on R2/R3) does not change.

### References

- Plan: `docs/tasks/auction-bid-data-create-plan.md`
- Spec: `docs/tasks/auction-bid-data-create-design.md`
- Schema: `V73__bid_data_docs_and_submit_columns.sql`
- Related ADRs: 2026-04-22 (R1 init + QBC flattening),
  2026-04-20 (cron skeleton + event contract),
  2026-04-19 (admin security matcher ordering)
- Mendix source:
  `migration_context/backend/ACT_OpenBidderDashboard.md`,
  `ACT_CreateBidData.md`, `ACT_SubmitBidRound.md`

---

## 2026-04-22 — Auction R1 init: listener + admin endpoint + QBC schema flattening

**Status:** Accepted (sub-project 2 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `SUB_InitializeRound1` fires when a Round 1 scheduling auction
transitions `Scheduled → Started`. It performs three effects:
(1) clamp aggregated-inventory non-DW `avg_target_price` below the
`minimum_allowed_bid` floor; (2) clamp DW `dw_avg_target_price` the
same way; (3) rewrite the Qualified Buyer Codes set — delete any
existing QBCs for the scheduling auction, insert one fresh QBC per
active wholesale/data-wipe buyer code with
`qualification_type='Qualified'`, `included=true`,
`is_special_treatment=false`. Ports `ACT_UpdateRound1TargetPrice_MinBid`
+ `SUB_CreateQualifiedBuyersEntity` + `SUB_ClearQualifiedBuyerList`
into our listener + service model, skipping
`SUB_HandleSpecialTreatmentBuyerOnRoundStart` (deferred).

### Decision

- **Post-commit listener + admin recovery endpoint.** `R1InitListener`
  is a `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`
  consumer of `RoundStartedEvent`, gated by `auctions.r1-init.enabled`
  via `@ConditionalOnProperty`. Admin endpoint
  `POST /api/v1/admin/auctions/{id}/rounds/1/init` (Administrator only,
  synchronous) calls the same `Round1InitializationService.initialize`
  entry point and is never gated.
- **Single `@Transactional(REQUIRES_NEW, timeout=30)` method owns all
  three effects.** All-or-nothing: any failure rolls back the clamp
  and QBC rewrite together. The round transition itself has already
  committed by the time this runs, so a failure here does not undo
  Started status — matches the 2026-04-20 cron ADR.
- **QBC schema flattening (V72).** Mendix modeled SA ↔ QBC and
  BuyerCode ↔ QBC as M:N junctions (`qbc_scheduling_auctions`,
  `qbc_buyer_codes`). Each QBC row in practice belongs to exactly one
  SA and one BuyerCode. V72 adds `scheduling_auction_id` +
  `buyer_code_id` directly on `qualified_buyer_codes`, backfills from
  the junctions, drops both junction tables, and enforces
  `UNIQUE(scheduling_auction_id, buyer_code_id)`. Enables one-hop
  delete on the SA rewrite (`SUB_ClearQualifiedBuyerList` parity) and
  eliminates a degenerate 2-hop join for "which auction is this QBC
  for".
- **Direct SA ↔ BuyerCode association (`SchedulingAuction_QualifiedBuyers`)
  dropped.** The QBC graph already encodes the same set; the Mendix
  association was historical modeling duplication.
- **Dev-first rollout.** `auctions.r1-init.enabled=true` in dev
  `application.yml`; `false` in `application-test.yml` so unit/IT
  suites stay deterministic (the full-chain IT opts in via
  `@TestPropertySource`). QA enables via env var after one full
  manual dev cycle + intentional failure drill.

### Alternatives considered

- **Split into three services** (clamp, clear QBCs, create QBCs).
  Rejected — YAGNI. The three effects are cohesive by contract and
  Mendix treats them as one microflow. Nothing else in the port needs
  any step in isolation.
- **Per-listener audit table** (e.g., `auctions.round_init_run`).
  Deferred — logs are the audit surface for Phase 1, consistent with
  sub-project 1's `[deferred-writer]` stance. `integration.scheduled_job_run`
  already captures the tick-level aggregate.
- **Keep the Mendix `SchedulingAuction_QualifiedBuyers` association as
  a parallel junction.** Rejected — duplicates state that the
  flattened QBC row already encodes; a QBC rewrite would have to stay
  in sync with two tables.
- **Rename `snowflakeExecutor` → `auctionDownstreamExecutor`.** YAGNI
  until 2–3 more listeners share the pool.

### Consequences

- One R1 listener invocation performs at most two UPDATEs + one DELETE
  + N INSERTs (one per active wholesale/data-wipe buyer code — ~580
  in prod). All within a single tx bounded by 30s.
- Listener failures are logged at ERROR and swallowed — the async
  executor thread is not poisoned. Monitoring must watch the
  `r1-init failed ...` log line.
- Admin endpoint is the recovery path. Running it a second time
  against the same auction is a no-op on clamp counts (nothing below
  floor anymore) but fully rewrites the QBC set.
- Unique constraint `uq_qbc_sa_bc` means a concurrent double-fire
  (listener + admin) would throw and roll back the later run — safe
  by construction.
- Special-treatment buyer handling (Mendix
  `SUB_HandleSpecialTreatmentBuyerOnRoundStart`) is deferred to a
  later sub-project.

### References

- Plan: `docs/tasks/auction-r1-init-plan.md`
- Spec: `docs/tasks/auction-r1-init-design.md`
- Schema: `V72__buyer_mgmt_qbc_flatten.sql`
- Related ADRs: 2026-04-21 (Snowflake push pattern + executor),
  2026-04-20 (cron + event contract), 2026-04-19 (admin security
  matcher ordering, entity-less FK)
- Mendix source:
  `migration_context/backend/services/SUB_InitializeRound1.md`,
  `ACT_UpdateRound1TargetPrice_MinBid.md`,
  `services/SUB_CreateQualifiedBuyersEntity.md`,
  `services/SUB_ClearQualifiedBuyerList.md`,
  `Act_GetOrCreateBuyerCodeSubmitConfig.md`

---

## 2026-04-21 — Auction status Snowflake push: listener wired, writer deferred

**Status:** Accepted (Phase 1 of sub-project 1,
`docs/tasks/auction-status-snowflake-push-plan.md`).

### Context

Mendix `SUB_SendAuctionAndSchedulingActionToSnowflake_async` pushes one row
to Snowflake on every round Start/Close transition. The microflow docs
describe the call shape (export XML → resolve username → `ExecuteDatabaseQuery`)
but the target Snowflake table schema is opaque — we don't yet know the
column set or type mapping. Blocking sub-project 1 on that archaeology
would starve the listener contract the other five sub-projects (R1/R2/R3
init, bid ranking, R3 pre-process) need to co-exist with on the event bus.

### Decision

- **Ship the listener, payload record, writer interface, and feature flag
  now.** `AuctionStatusSnowflakePushListener` consumes
  `RoundStartedEvent` / `RoundClosedEvent` via
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`,
  re-fetches the auction + week aggregate in a REQUIRES_NEW read-only tx,
  and hands an `AuctionStatusPushPayload` to an
  `AuctionStatusSnowflakeWriter`.
- **Default writer is logging-only.** `LoggingAuctionStatusSnowflakeWriter`
  emits a single structured INFO line prefixed with `[deferred-writer]`
  containing every field the real writer will eventually persist
  (`action`, `auctionId`, `auctionTitle`, `weekId`, `weekDisplay`,
  `round`, `transitionedAt`, `actor`). The marker makes it trivial to
  grep logs for unshipped payloads once the real writer lands.
- **Feature flag at the listener, not the writer.**
  `auctions.snowflake-push.enabled=false` short-circuits inside the
  listener before any DB read, so the feature is fully inert in dev/CI.
  Flipping it on in QA/prod is a config change, no redeploy.
- **Swallow writer failures.** The listener catches `RuntimeException`
  from the writer and logs at ERROR — matches the "downstream failures
  never undo the round transition" contract from the 2026-04-20 cron ADR
  (listeners fire post-commit; their failures cannot roll back the
  transaction that already committed).

### Alternatives considered

- **Port the real Snowflake `INSERT` now.** Rejected: target table is
  unknown; guessing would bake in a shape we'd have to migrate off.
- **Pull the feature flag from `BuyerCodeSubmitConfig.SendAuctionDataToSnowflake`
  (Mendix parity).** Rejected: the `AuctionsFeatureConfig` entity sits on
  a parallel branch; coupling the two is avoided by using yml. When that
  entity lands, swapping the flag source is a one-line change.
- **Let writer exceptions propagate to fail the round transition.**
  Rejected: the transaction has already committed by the time the
  `AFTER_COMMIT` listener runs — propagating would at best log a second
  stack trace, at worst poison the `snowflakeExecutor` thread.

### Consequences

- The six-listener contract from the 2026-04-20 cron ADR is unbroken:
  push is live, consuming events, on `snowflakeExecutor`.
- Flipping to real Snowflake delivery is a single-class drop-in
  (`@Primary` override or a second `@ConditionalOnProperty`-gated bean).
  The payload shape is part of the contract and must not change without
  a writer migration.
- The INFO log line is the current audit surface.
  `integration.snowflake_sync_log` will be wired when the real writer
  lands — the agg-inventory sync already uses it, so the table is ready.
- Test coverage: 3 unit test classes (`AuctionStatusPushPayloadTest`,
  `LoggingAuctionStatusSnowflakeWriterTest`,
  `AuctionStatusSnowflakePushListenerTest` — 5 branches) + 1 IT
  (`AuctionStatusSnowflakePushIT`) exercising the full
  cron → event → listener → capturing-writer chain across the async
  executor boundary. All green.

### References

- Plan: `docs/tasks/auction-status-snowflake-push-plan.md`
- Mendix source: `migration_context/backend/services/SUB_SendAuctionAndSchedulingActionToSnowflake_async.md`,
  `ACT_SetAuctionScheduleStarted.md`, `ACT_SetAuctionScheduleClosed.md`
- Related ADRs: 2026-04-20 (cron skeleton, event contract),
  2026-04-18 (agg-inventory sync — same post-commit + @Async executor
  pattern, different writer surface).

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
- Amended ADR: 2026-04-19 — Create Auction (archived in `decisions-archive.md`)
- Mendix source:
  `migration_context/backend/ACT_Create_Auction.md`,
  `migration_context/backend/ACT_SaveScheduleAuction.md`,
  `migration_context/backend/ACT_Create_SchedulingAuction_Helper_Default.md`
- Schema: `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`,
  `V65__seed_mdm_week.sql`


## Older entries

ADRs dated 2026-04-19 and earlier have been moved to
[`decisions-archive.md`](./decisions-archive.md) to keep this file
compact. They remain authoritative for the decisions they record and
are cross-referenced from newer entries above.

Archived entries:

- 2026-04-19 — Create Auction: dedicated endpoint + in-tx round creation + enum-as-varchar (partially superseded by the 2026-04-20 Schedule split above)
- 2026-04-18 — Aggregated Inventory: non-DW KPI totals must filter DW-only groups
- 2026-04-18 — Aggregated Inventory: Snowflake sync via post-commit event + watermark-based incremental pull
- 2026-04-17 — Aggregated Inventory: compute totals at read time + keep quantity override flag
- 2026-04-13 — Auth token moved from localStorage to HttpOnly cookie
- 2026-04-13 — PWS email delivery: post-commit event + async + feature flag
