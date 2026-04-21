# Design — Auction Lifecycle Cron (Sub-project 0)

**Date:** 2026-04-20
**Status:** Approved (ready for implementation plan)
**Mendix source:** `AuctionUI.ACT_ScheduleAuctionCheckStatus`,
`AuctionUI.ACT_SetAuctionScheduleClosed`,
`AuctionUI.ACT_SetAuctionScheduleStarted`,
`AuctionUI.SUB_SetAuctionStatus`
**Scope owner:** This is sub-project 0 of a 7-part decomposition that ports
the entire `ACT_ScheduleAuctionCheckStatus` downstream tree. The other six
sub-projects each consume the events emitted here.

---

## 1. Background

`ACT_ScheduleAuctionCheckStatus` is the heartbeat of the Mendix auction
engine. A scheduled event runs it every minute. Each tick:

- Closes any `SchedulingAuction` whose `RoundStatus = Started` and whose
  `End_DateTime` has passed.
- Starts any `SchedulingAuction` whose `RoundStatus = Scheduled` and whose
  `Start_DateTime` has arrived.
- Fans out into per-round work: Round 1 inventory init, Round 2 buyer
  assignment, special-buyer processing, bid ranking, target price
  calculation, Round 3 pre-processing, Snowflake push of the new status.
- Updates the parent `Auction.AuctionStatus` according to the rule in
  `SUB_SetAuctionStatus`: all rounds Closed → `Closed`; any round Started
  → `Started`; otherwise leave alone.

Porting it whole would mean implementing the entire bid engine in one
release. The `2026-04-20` decomposition splits the work into seven
independent sub-projects (#0 through #6); this document covers
**sub-project 0 only** — the cron skeleton, status transitions, parent
reconciliation, audit infrastructure, and stub listeners that will later
be replaced by sub-projects 1-6.

The new app already has the static surface this work plugs into:

- `Auction`, `SchedulingAuction` entities and their status enums.
- `AuctionScheduleService` (manual schedule / unschedule via HTTP), which
  this design **does not** modify.
- `AuctionScheduledEvent`, `AuctionUnscheduledEvent` for the manual flows.
- `AsyncConfig` with `snowflakeExecutor` and `emailExecutor` for future
  per-listener offload (not used by sub-project 0 itself).

## 2. Goals & non-goals

**Goals**

1. Every minute, transition rounds whose start or end time has passed,
   on a single backend instance even when the deployment is HA.
2. Emit `RoundStartedEvent` and `RoundClosedEvent` post-commit so future
   sub-projects can subscribe.
3. Reconcile the parent `Auction.AuctionStatus` after each tick using the
   exact rule in `SUB_SetAuctionStatus`.
4. Write one audit row per tick into `infra.scheduled_job_run` with
   counters (`roundsStarted`, `roundsClosed`, `auctionsAffected`,
   `errorCount`).
5. Provide a generic `ScheduledJobRunRecorder` and `infra.shedlock`
   table that future cron jobs can reuse.
6. Ship six **stub listeners** (one per future downstream service) that
   log "would do X" and define the contract sub-projects 1-6 must honor.

**Non-goals**

- No real bid persistence (sub-project 2).
- No real Round 1/2/3 init logic (sub-projects 3, 5, 6).
- No real bid ranking or target price (sub-project 4).
- No real special-buyer processing or Round 2 buyer assignment
  (sub-project 5).
- No real Snowflake push of auction status (sub-project 1).
- No admin UI changes — `Schedule` and `Unschedule` continue to flow
  through the existing HTTP endpoints unchanged.
- No DLQ. A failed tick is recorded `FAILED` in `scheduled_job_run`;
  monitoring tails the table.

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| D1 | **Multi-instance safe via ShedLock** + `infra.shedlock` table. | Spring `@Scheduled` fires on every JVM by default. ShedLock is the community standard, ~30 KB, and the user explicitly anticipates more cron jobs landing. |
| D2 | **Audit shape: lightweight + payload counters.** Single row per tick with `counters JSONB`. | Same write cost as a status-only row, ~10× more useful for forensic debugging. |
| D3 | **Hybrid event contract.** Per-round events (`RoundStartedEvent`, `RoundClosedEvent`) for cron-driven transitions; existing `AuctionScheduledEvent` / `AuctionUnscheduledEvent` for admin-driven transitions. | Cron and admin flows have different invariants and audit requirements; keeping them in separate event types avoids polymorphism in listeners. |
| D4 | **Per-row REQUIRES_NEW transactions.** One bad row logs and is skipped; others proceed. | Best forward-progress guarantee — a single misconfigured round can't block every other auction's lifecycle. |
| D5 | **Reusable `infra` schema.** `infra.scheduled_job_run` and `infra.shedlock` are platform primitives, not auction-specific. | "A lot more jobs" coming; signaling platform-level by location avoids future renames. |
| D6 | **Enabled in all profiles, 60 s interval.** `auctions.lifecycle.enabled` defaults true; tests opt out via `application-test.yml`. | Matches Mendix exactly. CI runs without surprise jitter because tests disable the property. |
| D7 | **Service decomposition: four small classes.** `AuctionLifecycleScheduler`, `AuctionLifecycleService`, `RoundTransitionService`, `AuctionStatusReconciler`. Plus `ScheduledJobRunRecorder` in `infra/`. | Each class has one job; `RoundTransitionService` and `AuctionStatusReconciler` are reusable from a future admin "force close" or manual reconcile button. |
| D8 | **All datetimes UTC.** Drop the Mendix `ACT_GetTimeOffset` Java action. | Our schema stores `Instant` / `TIMESTAMPTZ`; comparisons against `Instant.now()` are direct. Mendix needed the offset because its `[%CurrentDateTime%]` returns server-local time. |
| D9 | **No new column on `auctions.scheduling_auctions` or `auctions.auctions`.** | The cron writes only existing fields (`round_status`, `auction_status`, `changed_date`, `changed_by`). |
| D10 | **`SKIPPED_LOCKED` is best-effort logged.** ShedLock has no public callback for "lock not acquired". If we can hook one, we record; otherwise we accept that skipped runs are silent. | Acceptable trade-off — the leader logs every minute, so silence on the follower is implicit evidence of a healthy lock. |

## 4. Module layout

```
backend/src/main/java/com/ecoatm/salesplatform/
├─ config/
│  └─ SchedulingConfig.java                 // @EnableScheduling + LockProvider bean
├─ infra/scheduledjob/
│  ├─ ScheduledJobRun.java                  // entity → infra.scheduled_job_run
│  ├─ ScheduledJobRunStatus.java            // enum: RUNNING, OK, FAILED, SKIPPED_LOCKED
│  ├─ ScheduledJobRunRepository.java
│  └─ ScheduledJobRunRecorder.java          // begin(jobName) → handle, end(handle, status, counters)
├─ service/auctions/lifecycle/
│  ├─ AuctionLifecycleScheduler.java        // @Scheduled(fixedDelay=60000) + @SchedulerLock
│  ├─ AuctionLifecycleService.java          // tick() — orchestrates start + close phases
│  ├─ RoundTransitionService.java           // @Transactional(REQUIRES_NEW) per row
│  ├─ AuctionStatusReconciler.java          // @Transactional(REQUIRES_NEW) per affected auction
│  └─ stub/
│     ├─ R1InitStubListener.java
│     ├─ R2InitStubListener.java
│     ├─ R3InitStubListener.java
│     ├─ BidRankingStubListener.java
│     ├─ R3PreProcessStubListener.java
│     └─ SnowflakePushStubListener.java
└─ event/
   ├─ RoundStartedEvent.java                // record(roundId, round, auctionId, weekId)
   └─ RoundClosedEvent.java                 // record(roundId, round, auctionId, weekId)

backend/src/main/resources/db/migration/
├─ V70__infra_schema_and_scheduled_job_run.sql
└─ V71__infra_shedlock.sql

backend/pom.xml
└─ + net.javacrumbs.shedlock:shedlock-spring
└─ + net.javacrumbs.shedlock:shedlock-provider-jdbc-template
```

## 5. Data model

### `infra` schema

```sql
-- V70__infra_schema_and_scheduled_job_run.sql
CREATE SCHEMA IF NOT EXISTS infra;

CREATE TABLE infra.scheduled_job_run (
    id              BIGSERIAL PRIMARY KEY,
    job_name        VARCHAR(80)  NOT NULL,
    started_at      TIMESTAMPTZ  NOT NULL,
    finished_at     TIMESTAMPTZ,
    status          VARCHAR(20)  NOT NULL,
    node_id         VARCHAR(120) NOT NULL,
    duration_ms     INTEGER,
    error_message   TEXT,
    counters        JSONB,
    CONSTRAINT chk_sjr_status
      CHECK (status IN ('RUNNING','OK','FAILED','SKIPPED_LOCKED'))
);

CREATE INDEX idx_sjr_job_started ON infra.scheduled_job_run (job_name, started_at DESC);
```

```sql
-- V71__infra_shedlock.sql
CREATE TABLE infra.shedlock (
    name        VARCHAR(64)  PRIMARY KEY,
    lock_until  TIMESTAMP    NOT NULL,
    locked_at   TIMESTAMP    NOT NULL,
    locked_by   VARCHAR(255) NOT NULL
);
```

### Event records

```java
public record RoundStartedEvent(long roundId, int round, long auctionId, long weekId) {}
public record RoundClosedEvent (long roundId, int round, long auctionId, long weekId) {}
```

Both fire `AFTER_COMMIT` on the same thread that ran the per-row transaction.
Listeners must register `@TransactionalEventListener(phase = AFTER_COMMIT)`
to avoid firing on rollback.

### `counters` JSONB shape

```json
{
  "roundsStarted": 2,
  "roundsClosed": 1,
  "auctionsAffected": 2,
  "errorCount": 0
}
```

## 6. Tick algorithm

Two new methods are added to the existing `SchedulingAuctionRepository`
(no new repository class):

```java
// Returns ids only — keeps each row's transition in its own short tx
@Query("""
  select s.id from SchedulingAuction s
  where s.roundStatus = 'Started'
    and s.endDatetime < :now
""")
List<Long> findIdsToClose(@Param("now") Instant now);

@Query("""
  select s.id from SchedulingAuction s
  where s.roundStatus = 'Scheduled'
    and s.startDatetime <= :now
""")
List<Long> findIdsToStart(@Param("now") Instant now);
```

`AuctionLifecycleService.tick()` is **not** annotated `@Transactional` — it
opens no transaction itself. Each downstream call opens its own
`REQUIRES_NEW` tx.

```
1. Phase 1: closeStartedRoundsPastEnd
   1.1 schedulingRepo.findIdsToClose(Instant.now())
   1.2 For each id:
       try:
         event = roundTransitions.closeRound(id)   // own tx, lock, transition, publish event
         counters.roundsClosed++
         counters.affectedAuctions.add(event.auctionId())
       catch RoundAlreadyTransitionedException → DEBUG log, skip (benign race)
       catch Exception                          → ERROR log, counters.errorCount++

2. Phase 2: startScheduledRoundsAtTime
   Same shape as Phase 1, calling roundTransitions.startRound(id)

3. Phase 3: parent auction reconciliation
   For each auctionId in counters.affectedAuctions (deduplicated):
       try:
         statusReconciler.reconcile(auctionId)    // own tx
       catch Exception → ERROR log, counters.errorCount++

4. Return TickResult(counters) to scheduler for audit recording
```

### `RoundTransitionService.closeRound(id)`

```
@Transactional(propagation = REQUIRES_NEW)
1. SELECT … FROM auctions.scheduling_auctions WHERE id=? FOR UPDATE
2. If row.status != 'Started' OR row.end_datetime >= now():
     throw RoundAlreadyTransitionedException
3. row.round_status = 'Closed'
   row.changed_date = now()
   row.changed_by   = 'system:lifecycle-cron'
4. Register AFTER_COMMIT publication of
     RoundClosedEvent(row.id, row.round, row.auctionId, row.auction.weekId)
5. Return event for caller bookkeeping
```

`startRound(id)` mirrors with `Scheduled → Started` and
`row.start_datetime <= now()`.

The pessimistic lock is the second line of defense after ShedLock — it
guards against the case where two backend processes both believe they
hold the lock (clock skew + ShedLock TTL expiry mid-tick).

### `AuctionStatusReconciler.reconcile(auctionId)`

```
@Transactional(propagation = REQUIRES_NEW)
1. SELECT … FROM auctions.auctions WHERE id=? FOR UPDATE
2. rounds = SELECT round_status FROM auctions.scheduling_auctions
            WHERE auction_id=?
3. newStatus =
     all rounds Closed                                    → Closed
     any round Started                                    → Started
     else                                                 → leave unchanged
4. If newStatus != current:
     auction.auction_status = newStatus
     auction.changed_date   = now()
     auction.changed_by     = 'system:lifecycle-cron'
   Else: no-op (skip write, do not bump changed_date)
```

## 7. Stub listeners

Six stub `@Component` classes, each with one or two
`@TransactionalEventListener(phase = AFTER_COMMIT)` methods. They log at
INFO with structured fields and exit. They exist so:

1. The events have somewhere to go in unit/IT tests.
2. Sub-projects 1-6 each replace exactly one stub with a real listener
   in their own commit, with no churn outside their own bounded context.

| Stub class | Subscribes to | Round filter |
|---|---|---|
| `R1InitStubListener` | `RoundStartedEvent` | `round == 1` |
| `R2InitStubListener` | `RoundStartedEvent` | `round == 2` |
| `R3InitStubListener` | `RoundStartedEvent` | `round == 3` |
| `BidRankingStubListener` | `RoundClosedEvent` | `round == 1 \|\| round == 2` |
| `R3PreProcessStubListener` | `RoundClosedEvent` | `round == 2 \|\| round == 3` |
| `SnowflakePushStubListener` | both event types | none (fires for all) |

Each stub log line includes `auctionId`, `weekId`, `roundId`, `round`, and
the literal text `[stub]` so they're greppable when sub-projects 1-6 land
and remove the literal.

## 8. Configuration

```yaml
auctions:
  lifecycle:
    enabled: true              # default; set false in application-test.yml
    poll-ms: 60000             # 60s — overridable for slow envs
    lock:
      at-least-for: 10s        # min lock hold even if tick finishes in 100 ms — protects against clock skew between nodes
      at-most-for:  55s        # max hold before lock auto-expires — bounds crash recovery time. Less than poll-ms so a crashed leader's lock is reclaimable on the next tick. Pessimistic row locks in RoundTransitionService catch any double-processing if a tick exceeds 55s.
```

`SchedulingConfig` reads `auctions.lifecycle.enabled` via
`@ConditionalOnProperty` so the entire `@Scheduled` registration is
absent when disabled. Tests that do need to exercise the cron use
`@TestPropertySource(properties = "auctions.lifecycle.enabled=true")`.

## 9. Failure semantics

| Failure | Recorded as | Behavior |
|---|---|---|
| Per-row exception in `closeRound`/`startRound` | tick `OK`, `counters.errorCount++` | Other rows continue. SLF4J `ERROR` log includes round id and stack. |
| `RoundAlreadyTransitionedException` | tick `OK`, no counter bump | DEBUG log only — benign race after admin manually flipped the row between `findIds…` and the transition. |
| Reconciler throws | tick `OK`, `counters.errorCount++` | Other auctions reconciled. |
| Orchestration itself throws (DB down, bug) | tick `FAILED`, `error_message` set | Next tick will retry — no manual intervention needed. |
| Second JVM blocked by ShedLock | best-effort `SKIPPED_LOCKED` row | If ShedLock callback unavailable, no row written. |

The cron has no DLQ. A persistently failing row produces one `ERROR` log
per minute; monitoring should alert on `counters.errorCount > 0` over a
rolling window.

## 10. Testing strategy

### Unit tests (Mockito)

| Class | Cases |
|---|---|
| `RoundTransitionServiceTest` | close happy path; close when row no longer in `Started` (idempotent); start happy path; pessimistic lock acquired before status check (call order verified); event payload correct; `changed_by='system:lifecycle-cron'`. |
| `AuctionStatusReconcilerTest` | all-closed → `Closed`; any-started → `Started`; mixed → no-op; no-op skips DB write; no-op skips `changed_date` bump. |
| `AuctionLifecycleServiceTest` | both phases run; per-row exception swallowed and counted; `RoundAlreadyTransitionedException` logs DEBUG; affected auction set deduplicated; reconciler called once per affected auction. |
| `ScheduledJobRunRecorderTest` | begin writes `RUNNING` + node id + started_at; end writes OK/FAILED + finished_at + duration_ms + counters; node id format `hostname-pid`. |

### Integration tests (`@SpringBootTest` + Testcontainers Postgres)

| Test | Coverage |
|---|---|
| `tick_closesStartedRoundsPastEnd` | Seed Started round with end past; tick; assert row Closed + event captured + audit row OK. |
| `tick_startsScheduledRoundsAtTime` | Seed Scheduled round with start past; tick; assert row Started + event + audit row. |
| `tick_reconcilesParentToClosed` | 3 rounds, last one closes → parent Auction `Closed`. |
| `tick_reconcilesParentToStarted` | First round starts → parent Auction `Started`. |
| `tick_oneFailedRowDoesNotBlockOthers` | Mock middle row to throw; first and third still transition; audit `errorCount=1`. |
| `tick_idempotent_concurrentRunsAreSafe` | Run two `tick()` calls in parallel without ShedLock; pessimistic lock + status re-check prevent double-transition. |
| `shedLock_secondInstanceIsBlocked` | Two manual lock acquisitions on same name; second blocked. |

### Stub listener tests

One per stub, asserting:
- The right event triggers the stub.
- The wrong event (or wrong round) does not.

These six tests are the **contract** sub-projects 1-6 must preserve when
they replace each stub with a real listener.

### Coverage target

90%+ on `service/auctions/lifecycle/**` and `infra/scheduledjob/**`.
The project's floor is 80% — this code is too central to leave under-tested.

## 11. Documentation updates

When this lands:

- `docs/architecture/decisions.md` — new ADR `2026-04-20 — Auction lifecycle cron: per-row tx, ShedLock, event-driven downstream`.
- `docs/api/rest-endpoints.md` — no new endpoints; cron has no HTTP surface.
- `docs/app-metadata/scheduled-events.md` — new entry: `auctionLifecycle` job, 60 s, owned by `AuctionLifecycleScheduler`.
- `docs/deployment/environments.md` — note that `auctions.lifecycle.enabled` may be set false in disaster-recovery scenarios.
- `docs/business-logic/auction-flow.md` — short subsection: lifecycle is event-driven; see ADR.

## 12. Out of scope (future sub-projects)

Each of the six stubs created here will be replaced by a real listener
in a later sub-project. The contract — event shape, ordering guarantees,
post-commit semantics — is locked by this design. Downstream sub-projects
modify only the listener internals and may add their own services and
tables, but they will not change:

- The two event records (`RoundStartedEvent`, `RoundClosedEvent`).
- The fact that the cron itself ignores listener failures (a real
  listener throwing does not roll back the round transition).
- The audit row shape (sub-projects can add new counter keys to the
  JSONB but cannot rename the existing four).
