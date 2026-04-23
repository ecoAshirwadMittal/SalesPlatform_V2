# Design — Sub-project 4A: ExchangeBid (EB) Module Complete Port

**Status:** Approved (ready for implementation plan)
**Date:** 2026-04-22
**Sub-project:** 4A of the sub-project 4 umbrella
(`docs/tasks/auction-sub4-umbrella-design.md`)
**Mendix source:** `ecoatm_eb$reservebid` (15,875 rows),
`ecoatm_eb$reservedbidaudit` (4 rows), `ecoatm_eb$reservebidsync` (1 row);
microflows `ACT_OpenReservedBidOverviewPage`,
`ACT_GetMostRecentReserveBidRefresh`, `ACT_GetOrCreateReserveBidSync`,
`ACT_UploadReserveBidFile`, `ACT_DownloadReserveBids`, `SUB_RefreshEBPrice`,
`SUB_ReserveBid_DeleteAll`, `SUB_ReserveBidData_UpdateSnowflake`,
`SCH_UpdateEBPrice`; pages `ReserveBid_Overview`, `ReserveBid_Admin_Overview`,
`ReserveBid_Admin_NewEdit`, `ReserveBid_Audit_View`, `ReserveBid_File_Upload`.

---

## 1. Background

The Mendix `ecoatm_eb` module stores ExchangeBid **reserve floor prices**
used as one input to auction target-price recalc. Sub-project 4C's
target-price CTE joins `auctions.reserve_bid` into its `GREATEST(...)`
term to ensure target prices never drop below the ExchangeBid floor.

EB data is authored **externally** (by a pricing engine that writes to
Snowflake) and **locally** (by admins who upload Excel files or edit
single rows). Mendix reconciles the two via a bidirectional sync:
push on local write, pull on schedule when Snowflake has newer data.

Sub-project 4A ports this module completely — schema, admin surface,
bulk import/export, bidirectional Snowflake sync — so sub-project 4C's
target-price CTE can join against a real, populated, kept-in-sync
table.

---

## 2. Scope

### In scope
- Schema: `auctions.reserve_bid` + `auctions.reserve_bid_audit` +
  `auctions.reserve_bid_sync` (V74)
- Data load: extractor script (`migration_scripts/extract_eb_data.py`)
  producing `V75__data_auctions_reserve_bid.sql` — 15k rows
- JPA entities, repositories, service, controller
- Admin HTTP surface: list / single / create / update / delete + bulk
  Excel upload + Excel download + audit trail viewer + sync status +
  manual sync trigger (**no Delete-All button**)
- Bidirectional Snowflake sync:
  - Push on write (event-driven, async, via
    `AUCTIONS.UPSERT_RESERVE_BID` stored proc)
  - Pull on schedule (every 30 min by default, via
    `ReserveBidSyncScheduledJob` + ShedLock)
- Feature-flag guard on both Snowflake paths (mirrors Mendix)
- Admin Next.js pages under
  `/admin/auctions-data-center/reserve-bids/**`
- Tests: unit + repository IT + controller IT + Snowflake push/pull IT +
  scheduled-job IT + Playwright E2E
- Documentation: REST endpoints, ADR, data model, business-logic doc,
  deployment config

### Out of scope (explicit)
- ❌ Target-price recalc consumption — **sub-project 4C** joins this
  table into CTE B; 4A only owns the port
- ❌ Bid-ranking integration — ranking does not read `reserve_bid`
- ❌ PO (PurchaseOrder) integration — **sub-project 4B**
- ❌ **Delete-All** bulk-wipe button — operational risk deemed greater
  than parity value
- ❌ `reservebidfile` table — empty in prod; modern streams the download
  response body without persisting the file
- ❌ SharePoint integration for the XLSReport template — modern builds
  the Excel in-process with Apache POI
- ❌ `ACT_GET_CurrentUser` microflow — modern uses `SecurityContextHolder`

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 1 | Schema in `auctions.reserve_bid` (not `exchange_bid.*`) | EB is consumed only by auctions; no other domain reads it. Avoids a new schema, new grantable role, and cross-schema join in CTE B |
| 2 | `product_id VARCHAR(100)` (not Mendix's `INTEGER`) | Matches `bid_data.ecoid` / `aggregated_inventory.ecoid2` type so the CTE B join is plain string equality. Forward-proof if ecoids ever go alphanumeric |
| 3 | Unique `(product_id, grade)` constraint | Implicit in Mendix upload semantics ("find existing by pair, else create"); making it explicit prevents race-condition duplicates on concurrent uploads |
| 4 | Junction `reservedbidaudit_reservebid` → direct FK `reserve_bid_id` with `ON DELETE CASCADE` | Junction was already N:1 in legacy; collapse matches the sub-project 3 pattern |
| 5 | Drop `reservebidfile` table | Zero rows in prod; modern streams downloads via response body instead of persisting blobs |
| 6 | **Full bidirectional Snowflake sync** (push + pull cron) | Mendix parity. Snowflake is the authoritative source (external pricing engine writes it); admin UI writes are reconciled back |
| 7 | Pull path uses **delete-all + re-insert** | Matches Mendix's `SUB_ReserveBid_DeleteAll` + `SCH_UpdateEBPrice` pattern. 16k rows is small enough that diff-based upsert adds complexity without a throughput win |
| 8 | Pull path does **not** emit `ReserveBidChangedEvent` | Echo prevention — would otherwise cause Postgres-write → Snowflake-push → source-bumps → next-pull-sees-newer → infinite loop |
| 9 | Default pull interval: **30 minutes** | Tunable via `eb.sync.fixed-delay-ms`. EB prices change at most daily per the awarded-week cadence |
| 10 | ShedLock via existing `infra.shedlock` table | Matches auction-lifecycle-cron pattern; guarantees single-instance execution in HA |
| 11 | Admin role: **`ROLE_Administrator` only** for writes | Matches Mendix `ReserveBid_Admin_*` page role. Read-only `ROLE_SalesLeader` / `ROLE_SalesOps` deferred to when those roles land in modern |
| 12 | No Delete-All button | Operational risk > parity value. If a wipe is genuinely needed, psql or a dedicated ops job is safer |
| 13 | Audit trail regenerated **only on price change** during upload, PUT | No audit on CREATE (nothing to audit), no audit on DELETE (cascade drops the trail), no audit on pull (mirrors Mendix delete-all-then-reinsert dropping history) |

---

## 4. Architecture

### 4.1 Package layout

```
backend/src/main/java/com/ecoatm/salesplatform/
├── controller/admin/
│   └── ReserveBidController.java
├── service/auctions/reservebid/
│   ├── ReserveBidService.java           ── all business logic
│   ├── ReserveBidSyncScheduledJob.java  ── @Scheduled + @SchedulerLock wrapper
│   ├── ReserveBidException.java
│   └── ReserveBidValidationException.java
├── service/auctions/snowflake/
│   ├── ReserveBidSnowflakePushListener.java
│   ├── ReserveBidSnowflakeWriter.java   ── interface
│   ├── JdbcReserveBidSnowflakeWriter.java
│   ├── LoggingReserveBidSnowflakeWriter.java
│   ├── ReserveBidSnowflakeReader.java
│   └── ReserveBidSnowflakePayload.java
├── repository/auctions/
│   ├── ReserveBidRepository.java
│   ├── ReserveBidAuditRepository.java
│   └── ReserveBidSyncRepository.java
├── model/auctions/
│   ├── ReserveBid.java
│   ├── ReserveBidAudit.java
│   └── ReserveBidSync.java
├── dto/
│   ├── ReserveBidRow.java
│   ├── ReserveBidRequest.java
│   ├── ReserveBidUploadResult.java
│   ├── ReserveBidAuditRow.java
│   ├── ReserveBidAuditResponse.java
│   ├── ReserveBidListResponse.java
│   └── ReserveBidSyncStatus.java
└── event/
    └── ReserveBidChangedEvent.java

frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/
├── page.tsx                     ── admin overview grid
├── [id]/page.tsx                 ── edit popup (or route)
└── [id]/audit/page.tsx           ── audit viewer
```

### 4.2 Dataflow — write path

```
HTTP (admin, ROLE_Administrator)
        │
        ▼
ReserveBidController
        │
        ▼
ReserveBidService  (@Transactional)
        ├─► ReserveBidRepository (upsert + audit generation)
        │
        ▼  publish post-commit
ReserveBidChangedEvent { changedIds[], action }
        │
        ▼  @TransactionalEventListener(AFTER_COMMIT) on snowflakeExecutor
ReserveBidSnowflakePushListener
        │
        ▼
ReserveBidSnowflakeWriter.upsert(...)
        │
        ▼
integration.snowflake_sync_log + AUCTIONS.UPSERT_RESERVE_BID stored proc
```

Failure of the Snowflake push **does not** roll back the Postgres
commit. The next pull cron reconciles when Snowflake is refreshed by
the external pricing engine.

### 4.3 Dataflow — pull path

```
@Scheduled(fixedDelayString = "${eb.sync.fixed-delay-ms:1800000}")
@SchedulerLock(name = "reserve-bid-sync", lockAtMostFor = "PT20M")
ReserveBidSyncScheduledJob.run()
        │
        ▼
ReserveBidService.runScheduledSync()
        ├─► ReserveBidSnowflakeReader.fetchMaxUploadTime()
        ├─► compare vs ReserveBidSync.lastSyncDatetime
        ├─► if source newer OR local NULL:
        │       ├─► fetchAll()
        │       ├─► @Transactional(timeout=600) {
        │       │      deleteAllInBatch()
        │       │      saveAll(rows)
        │       │      reserveBidSync.lastSyncDatetime = sourceMax
        │       │   }
        │       └─► (NO ReserveBidChangedEvent — echo prevention)
        └─► infra.scheduled_job_run row
             (job_name=reserve-bid-sync, status, rowsFetched, ...)
```

### 4.4 Dataflow — CTE B consumer (sub-project 4C)

```
auctions.reserve_bid  ◄── LEFT JOIN
                          ON rb.product_id = bd.ecoid
                         AND rb.grade      = bd.merged_grade
                      (contributes rb.bid to GREATEST(...))
```

---

## 5. Schema (V74)

File: `backend/src/main/resources/db/migration/V74__auctions_reserve_bid.sql`

```sql
CREATE TABLE auctions.reserve_bid (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    product_id               VARCHAR(100)    NOT NULL,
    grade                    VARCHAR(200)    NOT NULL,
    brand                    VARCHAR(200),
    model                    VARCHAR(200),
    bid                      NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    last_update_datetime     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    last_awarded_min_price   NUMERIC(14, 4),
    last_awarded_week        VARCHAR(20),
    bid_valid_week_date      VARCHAR(20),
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_reserve_bid_product_grade UNIQUE (product_id, grade)
);

CREATE INDEX idx_rb_product_grade  ON auctions.reserve_bid(product_id, grade);
CREATE INDEX idx_rb_last_update    ON auctions.reserve_bid(last_update_datetime DESC);

CREATE TABLE auctions.reserve_bid_audit (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    reserve_bid_id           BIGINT          NOT NULL
                                            REFERENCES auctions.reserve_bid(id)
                                            ON DELETE CASCADE,
    old_price                NUMERIC(14, 4)  NOT NULL,
    new_price                NUMERIC(14, 4)  NOT NULL,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

CREATE INDEX idx_rba_reserve_bid ON auctions.reserve_bid_audit(reserve_bid_id);
CREATE INDEX idx_rba_created     ON auctions.reserve_bid_audit(created_date DESC);

CREATE TABLE auctions.reserve_bid_sync (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    last_sync_datetime       TIMESTAMPTZ,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

INSERT INTO auctions.reserve_bid_sync (last_sync_datetime) VALUES (NULL);
```

---

## 6. API surface

All endpoints: `/api/v1/admin/reserve-bids/**`, role `ROLE_Administrator`.

| Method | Path | Purpose | Response |
|---|---|---|---|
| GET | `/` | Paginated list with filters (productId, grade, minBid, maxBid, updatedSince, page, size, sort) | `ReserveBidListResponse` |
| GET | `/{id}` | Single row | `ReserveBidRow` |
| POST | `/` | Create single (no audit — new row) | `201 ReserveBidRow` |
| PUT | `/{id}` | Full update (audit on price change) | `200 ReserveBidRow` |
| DELETE | `/{id}` | Delete single (cascades audit) | `204` |
| POST | `/upload` | Bulk Excel upload (multipart `file`) | `ReserveBidUploadResult { created, updated, unchanged, auditsGenerated, errors[] }` |
| GET | `/download` | Excel export (streamed) | `200 application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| GET | `/{id}/audit` | Per-row audit trail (paginated) | `ReserveBidAuditResponse` |
| GET | `/sync` | Current sync watermark | `ReserveBidSyncStatus { lastSyncDatetime, sourceMaxDatetime, drift, state }` |
| POST | `/sync` | Trigger manual pull | `202 Accepted { runId }` |

### DTO shapes

```java
public record ReserveBidRow(long id, String productId, String grade,
    String brand, String model, BigDecimal bid, Instant lastUpdateDatetime,
    BigDecimal lastAwardedMinPrice, String lastAwardedWeek,
    String bidValidWeekDate, Instant changedDate) {}

public record ReserveBidRequest(
    @NotBlank String productId,
    @NotBlank String grade,
    String brand, String model,
    @NotNull @DecimalMin("0") BigDecimal bid,
    BigDecimal lastAwardedMinPrice,
    String lastAwardedWeek,
    String bidValidWeekDate) {}

public record ReserveBidUploadResult(int created, int updated, int unchanged,
    int auditsGenerated, List<UploadError> errors) {
    public record UploadError(int rowNumber, String productId, String grade, String reason) {}
}

public record ReserveBidAuditRow(long id, long reserveBidId, String productId,
    String grade, BigDecimal oldPrice, BigDecimal newPrice,
    Instant createdDate, String changedByUsername) {}

public record ReserveBidSyncStatus(Instant lastSyncDatetime,
    Instant sourceMaxDatetime, Duration drift, String state) {}
```

### Error codes

| HTTP | Code | Thrown when |
|---|---|---|
| 400 | `INVALID_REQUEST` | Bean-validation failure |
| 400 | `UPLOAD_PARSE_ERROR` | Excel unreadable or missing columns |
| 404 | `RESERVE_BID_NOT_FOUND` | id does not exist |
| 409 | `DUPLICATE_PRODUCT_GRADE` | Violates `uq_reserve_bid_product_grade` |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | Upload not `multipart/form-data` |
| 503 | `SNOWFLAKE_UNAVAILABLE` | Manual `POST /sync` can't reach Snowflake |

### Upload semantics

- Dup `(productId, grade)` within same sheet → surfaced in `errors[]`
  with reason `DUPLICATE_IN_SHEET`; first occurrence wins
- Non-numeric `productId` accepted (VARCHAR schema); logged WARN for
  drift detection
- Audit row written only when price changes (PUT, upload)
- POST CREATE — no audit
- DELETE — no audit (cascade drops)

---

## 7. Snowflake integration

### 7.1 Push on write

- Event: `ReserveBidChangedEvent(changedIds: List<Long>, action: UPSERT|DELETE)`
- Listener: `@TransactionalEventListener(AFTER_COMMIT)` on `snowflakeExecutor`
- Writer: `ReserveBidSnowflakeWriter` interface
  - `JdbcReserveBidSnowflakeWriter` — prod
  - `LoggingReserveBidSnowflakeWriter` — dev/test
- Stored proc (unchanged from Mendix):
  ```
  CALL AUCTIONS.UPSERT_RESERVE_BID(
      JSON_CONTENT VARIANT,    -- array of rows
      ACTING_USER STRING       -- from SecurityContext
  )
  ```
- Failure: logged, **not** propagated. Next pull cron reconciles.
- Bulk upload publishes ONE event with all changed ids at tx commit —
  not one event per row.

### 7.2 Pull on schedule

- `@Scheduled(fixedDelayString = "${eb.sync.fixed-delay-ms:1800000}")`
- `@SchedulerLock(name = "reserve-bid-sync", lockAtMostFor = "PT20M", lockAtLeastFor = "PT1M")`
- Reader: `ReserveBidSnowflakeReader`
  - `fetchMaxUploadTime()` — `SELECT MAX(last_update_datetime) FROM AUCTIONS.RESERVE_BID`
  - `fetchAll()` — full table read
- Compare `sourceMax` vs `ReserveBidSync.lastSyncDatetime`
- If source newer OR local NULL:
  - `@Transactional(timeout = 600)` { deleteAllInBatch + saveAll + updateLastSync }
  - **No** `ReserveBidChangedEvent` (echo prevention)
- Record in `infra.scheduled_job_run`
- Manual trigger: `POST /sync` wraps the same service method, returns `202` + runId

### 7.3 Enable/disable guard

Both paths check the `eb.sync.enabled` Spring property (Mendix used
`Eco_Core.ACT_FeatureFlag_RetrieveOrCreate`; modern doesn't have a
generic feature-flag service yet, so a dedicated property is the
simplest equivalent). If `false`, the listener and scheduled job both
log "would push/pull" and no-op. Property is checked at call time
(not via `@ConditionalOnProperty`) so runtime toggling via
`application-*.yml` reload works without a restart.

### 7.4 Config

```yaml
eb:
  sync:
    enabled: true
    fixed-delay-ms: 1800000         # 30 min
    snowflake-timeout-seconds: 60
```

---

## 8. Data load + testing

### 8.1 Data load

- `migration_scripts/extract_eb_data.py` — new extractor script
- Mirrors `extract_qa_data.py` CLI: `--source-db qa-0327 | prod-0325`
- Generates `V75__data_auctions_reserve_bid.sql` with:
  - 15,875 reserve_bid rows (legacy_id preserved)
  - 4 reserve_bid_audit rows (reserve_bid_id remapped via legacy_id)
  - 1 reserve_bid_sync row

### 8.2 Test pyramid

| Layer | Framework | What gets tested |
|---|---|---|
| Unit | JUnit 5 + Mockito + AssertJ | Service methods with mocked deps: audit generation on price change, upload dedup, validation, push payload shape, pull source-newer logic |
| Repository IT | Testcontainers Postgres | `findByProductIdAndGrade`, unique-constraint violation, cascade-delete-audit, bulk upsert on 15k rows |
| Controller IT | `@SpringBootTest` + MockMvc | Every endpoint end-to-end: admin role gate, multipart upload happy + error, download streaming, audit pagination |
| Snowflake push | Unit (mocked writer) + contract IT (logging writer pins JSON shape) | AFTER_COMMIT ordering, payload matches stored-proc contract, failure logs don't roll back Postgres |
| Snowflake pull | Unit (mocked reader) | Source-newer triggers delete+reinsert, source-equal is no-op, NO echo event, `scheduled_job_run` row written |
| Scheduled job | Unit + IT | ShedLock prevents concurrent runs, feature-flag off is no-op, `POST /sync` invokes same code path |
| E2E | Playwright | Upload Excel → grid refresh → audit visible → download roundtrip → single-row edit creates audit → delete cascades audit |

### 8.3 Coverage targets

- `ReserveBidService` — 90%+ (load-bearing; upload + sync are branchy)
- `ReserveBidController` — 85%+ (every endpoint + every error)
- `ReserveBidSnowflakePushListener` + `ReserveBidSyncScheduledJob` — 85%+
- Repositories — 80%+ (mostly via IT)
- Entities + DTOs — not enforced

### 8.4 Test fixtures

```
backend/src/test/resources/fixtures/
├── reserve-bid-sample.xlsx           ── 10 valid rows
├── reserve-bid-with-errors.xlsx      ── mixed valid + 3 error rows
└── reserve-bid-round-trip.xlsx       ── download→reupload parity
```

### 8.5 Concurrency check

Synthetic IT simulating 50 concurrent uploads against the same
`(product_id, grade)` — asserts unique constraint holds, no duplicate
audits, exactly one winner.

---

## 9. Docs updates (per CLAUDE.md mandate)

| File | Change |
|---|---|
| `docs/api/rest-endpoints.md` | Append `## Reserve Bids (EB)` section |
| `docs/architecture/decisions.md` | ADR `2026-04-22 — Sub-project 4A: EB module port` |
| `docs/architecture/data-model.md` | Add reserve_bid, reserve_bid_audit, reserve_bid_sync |
| `docs/app-metadata/modules.md` | New EB module entry |
| `docs/business-logic/reserve-bid-sync.md` | Push/pull semantics + echo prevention |
| `docs/deployment/setup.md` | `eb.sync.fixed-delay-ms` config |
| `docs/testing/coverage.md` | New package coverage |

---

## 10. Risks + known gaps

### 10.1 Risks

| Risk | Mitigation |
|---|---|
| Snowflake stored proc contract drift | Contract pinned in Section 7.1; LoggingWriter test pins payload shape |
| Concurrent admin upload + pull cron race | ShedLock serializes pull; upload runs in own tx + unique constraint prevents dup creation |
| Echo loop between push and pull | Pull explicitly does NOT publish `ReserveBidChangedEvent` |
| Large pull (16k rows) timing out | `@Transactional(timeout=600)` gives 10 min; typical measured run expected < 30s |
| Audit history lost on every pull | Matches Mendix. Documented; if history preservation is needed later, diff-based upsert replaces delete-all |

### 10.2 Known gaps accepted

- `SUB_RefreshEBPrice` uses `!=` equality for source-vs-local time; we
  use `>` strict. If Snowflake time ever moves backwards (shouldn't),
  modern won't resync. Acceptable — backward motion is a Snowflake data
  integrity bug, not a sync concern.
- No SharePoint `EBPrice` XLSReport template integration. Modern builds
  Excel in-process with Apache POI; column order + headers match
  legacy template output.
- No `ACT_GET_CurrentUser` port — modern uses `SecurityContextHolder`
  directly; same effective behavior.
- `ROLE_SalesLeader` / `ROLE_SalesOps` read-only access deferred —
  those roles don't exist in modern yet. Admin-only for now.

---

## 11. Dependencies + ship order

- **Independent of 4B** — no shared tables, entities, or services
- **Blocks 4C** — sub-project 4C's target-price CTE joins against
  `auctions.reserve_bid`; that table and some test data must exist
  before 4C can ship
- **Until 4A lands:** no behavioral regression. Sub-project 3 currently
  uses no EB data; the stub ranking listener doesn't read EB either.

### Related ADRs

- `docs/tasks/auction-sub4-umbrella-design.md` (umbrella)
- `docs/tasks/auction-lifecycle-cron-design.md` (sub-project 0)
- `docs/tasks/auction-bid-data-create-design.md` (sub-project 3)
- *(to be produced)* `docs/tasks/auction-po-module-design.md` (4B)
- *(to be produced)* `docs/tasks/auction-bid-ranking-design.md` (4C)

### Implementation plan

Produced by `superpowers:writing-plans` after this spec is approved by
the user. Saved to `docs/tasks/auction-eb-module-plan.md`.
