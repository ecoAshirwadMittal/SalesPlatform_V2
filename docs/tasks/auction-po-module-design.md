# Design — Sub-project 4B: PurchaseOrder (PO) Module Complete Port

**Status:** Approved (ready for implementation plan)
**Date:** 2026-04-24
**Sub-project:** 4B of the sub-project 4 umbrella
(`docs/tasks/auction-sub4-umbrella-design.md`)
**Mendix source:** `ecoatm_po$purchaseorder` (13 rows),
`ecoatm_po$podetail` (9,895 rows); microflows `ACT_CreateNewPO`,
`ACT_UpdatePO`, `ACT_GETWeeklyPO`, `NAV_CreatePO`, `NAV_PurchaseOrder`,
`SUB_GetOrCreatePOHelper`, `SUB_CreatePODetail`, `SUB_ImportCreatePODetails`,
`SUB_ImportUpdatePODetails`, `SUB_UploadPOToSnowFlake`, `VAL_BuyerCode_PO`,
`VAL_WeekRange_PO`, `DS_FromWeekPO`, `DS_ToWeekPO`, `DeleteAll_PO`;
pages `PurchaseOrder_Main`, `Create_PO_Week_Prompt`,
`PurchaseOrder_Confirmation`.

---

## 1. Background

The Mendix `ecoatm_po` module stores **PurchaseOrder** records authored by
ops via Excel upload. Each PO covers a contiguous range of `mdm.week`
(`week_from`, `week_to`) and carries a list of `PODetail` rows — one
per (product_id, grade, buyer_code) — with a target price and quantity
cap. Sub-project 4C's target-price recalc joins `po_detail.price` into
its `GREATEST(...)` term so target prices never fall below committed
purchase commitments.

Unlike EB (sub-project 4A), PO data is **authored in Mendix/modern**
and pushed to Snowflake — Snowflake is a downstream read replica, not
an authoritative source. There is no external pricing engine writing
POs into Snowflake, and Mendix has no PO pull-cron equivalent of
`SCH_UpdateEBPrice`.

Sub-project 4B ports the PO module — schema, admin surface, bulk Excel
import/export, push-only Snowflake sync — so sub-project 4C's
target-price CTE can join against a real, populated `po_detail` table.

---

## 2. Scope

### In scope
- Schema: `auctions.purchase_order` + `auctions.po_detail` (V78)
- Data load: `migration_scripts/extract_po_data.py` →
  `V79__data_auctions_purchase_order.sql` — 13 POs + 9,895 detail rows
- JPA entities, repositories, service, controller
- Admin HTTP surface: list / single / create / update / delete +
  per-PO bulk Excel upload (wipe-and-replace) + per-PO Excel download
  (**no per-row PODetail CRUD; no Delete-All button**)
- Push-only Snowflake sync:
  - `@TransactionalEventListener(AFTER_COMMIT)` on `snowflakeExecutor`
  - Writer interface + `LoggingPurchaseOrderSnowflakeWriter` (default,
    `matchIfMissing=true`) / `JdbcPurchaseOrderSnowflakeWriter` (gated
    by `po.sync.writer=jdbc`)
  - Per-PO `po_refresh_timestamp` column as watermark
- Admin Next.js pages under
  `/admin/auctions-data-center/purchase-orders/**`
- Role gate: `Administrator` + `SalesOps`
- Tests: unit + repository IT + controller IT + Snowflake push IT +
  Playwright E2E
- Documentation: REST endpoints, ADR, data-model, modules,
  business-logic, deployment config, coverage

### Out of scope (explicit)
- ❌ Target-price recalc consumption — **sub-project 4C** joins this
  table into CTE B; 4B only owns the port
- ❌ EB integration — independent domain (**sub-project 4A**)
- ❌ PO lifecycle automation (auto-close past-dated POs) — umbrella
  deferred
- ❌ `weekly_po` fulfillment tracker (12,384 rows in Mendix) — 4C only
  needs `po_detail` headline price
- ❌ `week_period` junction (54 rows) — derivable from `week_from_id` /
  `week_to_id` at query time
- ❌ `purchase_order_doc` blob storage (82 rows) — modern streams
  uploads to the parser without persisting the file (4A pattern)
- ❌ `pohelper` (21 rows) — Mendix client-side UX scratch; modern is
  stateless HTTP
- ❌ Explicit `status` enum / state machine — lifecycle is derived
  from `week_from` / `week_to`
- ❌ Scheduled pull cron + echo prevention — push-only (Mendix has
  no PO pull either)
- ❌ Per-row PODetail inline CRUD — wipe-and-replace per Mendix
- ❌ Delete-All button — operational risk > parity value (4A pattern)
- ❌ SharePoint integration — `Apache POI` in-process build
- ❌ `ACT_GET_CurrentUser` — `SecurityContextHolder` directly

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 1 | Schema in `auctions.*` (not a new `purchase_order.*` schema) | EB lives in `auctions` (4A); 4C's CTE stays single-schema; no new grantable role needed |
| 2 | `product_id VARCHAR(100)` (not Mendix's `INTEGER`) | Matches `bid_data.ecoid` / `aggregated_inventory.ecoid2` so 4C's CTE B join is plain string equality. 4A pattern |
| 3 | Multi-junction `purchaseorder_week_from` / `purchaseorder_week_to` collapsed to direct FKs `week_from_id` / `week_to_id` | Junctions are N:1 in legacy; collapse matches sub-project 3 + 4A pattern |
| 4 | Junction `podetail_buyercode` collapsed to direct FK `buyer_code_id` | Same N:1 collapse; simplifies upload + JPA |
| 5 | Unique `(purchase_order_id, product_id, grade, buyer_code_id)` constraint | Mendix has no explicit constraint, but the upload flow treats this quartet as the natural key. Making it explicit prevents race-condition duplicates |
| 6 | `week_range_label VARCHAR(200)` denormalized | Matches Mendix `weekrange` column; cheap read-side display label; rebuilt on every header upsert |
| 7 | Week-range ordering enforced at **service layer** (not via DB CHECK) | `mdm.week.id` is BIGSERIAL and monotonic-by-seed-order today, but that's a fragile invariant. Service-layer validator compares `week_from.weekId <= week_to.weekId` (business key `YYYYNN`). Matches Mendix `VAL_WeekRange_PO` |
| 8 | Per-row `po_refresh_timestamp` as Snowflake push watermark | Matches Mendix `porefreshtimestamp`; no need for a singleton `purchase_order_sync` table because there's no scheduled pull |
| 9 | **Push-only** Snowflake sync (no pull cron) | Mendix has only `SUB_UploadPOToSnowFlake`; no `SCH_UpdatePOPrice` equivalent. Snowflake is a downstream read replica for PO; adding pull is YAGNI |
| 10 | Push grain = **per PO** (one event, one stored proc call, one payload with all details) | Matches Mendix `SUB_UploadPOToSnowFlake`'s `$PODetailList` whole-PO argument; simpler than per-detail and matches the natural transaction boundary |
| 11 | Push failure logged + swallowed (no retry, no rollback) | 4A pattern. Recovery = admin re-uploads the Excel (Mendix has the same recovery model) |
| 12 | No `po_detail_audit` table | Mendix has none; uploads are wipe-and-replace so per-row audit is meaningless |
| 13 | Wipe-and-replace upload semantics (no inline per-row CRUD) | Mendix `SUB_ImportUpdatePODetails` deletes existing rows before re-inserting. Strict parity unblocks the natural ops workflow without introducing partial-state failure modes |
| 14 | Strict upload rejection on any row error or missing buyer_code | Wipe-and-replace + partial success is dangerous (final PO would be missing typo'd rows). Matches Mendix `VAL_BuyerCode_PO` short-circuit |
| 15 | Lifecycle state derived (`DRAFT` / `ACTIVE` / `CLOSED`), never stored | Mendix has no status column. Computed from `today` vs `week_from.startDate` / `week_to.endDate`. 4C's CTE filters on dates directly |
| 16 | Admin role: `Administrator` **+** `SalesOps` for all writes | Mendix `NAV_PurchaseOrder` allows both roles. `ROLE_SalesOps` is already seeded in modern (V15/V16); only the controller wiring is new |
| 17 | No Delete-All button | 4A pattern — operational risk > parity value |
| 18 | `purchase_order_doc` (file blob) dropped | 4A pattern for empty `reservebidfile`; Mendix has 82 rows but no ops use case has been cited for downloading historical upload blobs. Re-source from the parsed `po_detail` rows or via export |
| 19 | `temp_buyer_code` column preserved | Snowflake payload parity (Mendix denorm). Drop in a follow-up if QA confirms `AUCTIONS.UPSERT_PURCHASE_ORDER` doesn't read it |
| 20 | `po.sync.enabled` checked at call time (not via `@ConditionalOnProperty`) | Allows runtime toggle without restart. 4A pattern (`eb.sync.enabled`) |

---

## 4. Architecture

### 4.1 Package layout

```
backend/src/main/java/com/ecoatm/salesplatform/
├── controller/admin/
│   └── PurchaseOrderController.java
├── service/auctions/purchaseorder/
│   ├── PurchaseOrderService.java           ── header CRUD + event publish
│   ├── PODetailService.java                ── upload parse + wipe-replace
│   ├── PurchaseOrderValidator.java         ── week-range + buyer-code checks
│   ├── POExcelParser.java                  ── Apache POI stream parser
│   ├── POExcelBuilder.java                 ── Apache POI export
│   ├── PurchaseOrderException.java
│   └── PurchaseOrderValidationException.java
├── service/auctions/snowflake/
│   ├── PurchaseOrderSnowflakePushListener.java
│   ├── PurchaseOrderSnowflakeWriter.java   ── interface
│   ├── JdbcPurchaseOrderSnowflakeWriter.java
│   ├── LoggingPurchaseOrderSnowflakeWriter.java
│   └── PurchaseOrderSnowflakePayload.java
├── repository/auctions/
│   ├── PurchaseOrderRepository.java
│   └── PODetailRepository.java
├── model/auctions/
│   ├── PurchaseOrder.java
│   └── PODetail.java
├── dto/
│   ├── PurchaseOrderRow.java
│   ├── PurchaseOrderRequest.java
│   ├── PurchaseOrderListResponse.java
│   ├── PODetailRow.java
│   ├── PODetailListResponse.java
│   ├── PODetailUploadResult.java
│   └── PurchaseOrderLifecycleState.java    ── enum DRAFT|ACTIVE|CLOSED
└── event/
    └── PurchaseOrderChangedEvent.java      ── { purchaseOrderId, action }

frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/
├── page.tsx                     ── list grid (filters: year, state, week)
├── new/page.tsx                 ── create PO + first upload
└── [id]/page.tsx                ── edit week range + re-upload + export
```

Constructor injection only — no `@Autowired` on fields, no Lombok.

### 4.2 Dataflow — write path (header + upload share the same path)

```
HTTP (admin: Administrator | SalesOps)
        │
        ▼
PurchaseOrderController / PODetailController
        │
        ▼
PurchaseOrderService / PODetailService  (@Transactional)
        ├─► PurchaseOrderValidator (week-range + buyer-code short-circuit)
        ├─► PurchaseOrderRepository (header upsert)
        ├─► PODetailRepository (for upload: deleteAllByPurchaseOrderId + saveAll)
        ├─► PO.totalRecords + poRefreshTimestamp = NOW()
        │
        ▼  publish post-commit
PurchaseOrderChangedEvent { purchaseOrderId, action: UPSERT|DELETE }
        │
        ▼  @TransactionalEventListener(AFTER_COMMIT) on snowflakeExecutor
PurchaseOrderSnowflakePushListener
        │
        ▼
PurchaseOrderSnowflakeWriter.upsert(payload)
        │
        ▼
integration.snowflake_sync_log + AUCTIONS.UPSERT_PURCHASE_ORDER stored proc
```

Failure of the Snowflake push **does not** roll back the Postgres
commit. Recovery = admin re-uploads. There is no pull to reconcile
from (push-only).

### 4.3 Dataflow — CTE B consumer (sub-project 4C)

```
auctions.po_detail        ◄── LEFT JOIN
auctions.purchase_order        ON pd.purchase_order_id = po.id
                              AND po.week_from_id <= today_week_id
                              AND po.week_to_id   >= today_week_id
                              ON pd.product_id    = bd.ecoid
                              AND pd.grade        = bd.merged_grade
                              AND pd.buyer_code_id = bd.buyer_code_id
                          (contributes pd.price to GREATEST(...))
```

### 4.4 Lifecycle derivation

```java
public enum PurchaseOrderLifecycleState {
    DRAFT, ACTIVE, CLOSED;

    public static PurchaseOrderLifecycleState derive(
            LocalDate today, Week from, Week to) {
        if (today.isBefore(from.getStartDate())) return DRAFT;
        if (today.isAfter(to.getEndDate()))      return CLOSED;
        return ACTIVE;
    }
}
```

Computed at DTO-mapping time, never stored. 4C's CTE filters on dates
directly — does not read this enum.

---

## 5. Schema (V78)

File: `backend/src/main/resources/db/migration/V78__auctions_purchase_order.sql`

```sql
CREATE TABLE auctions.purchase_order (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    week_from_id            BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_to_id              BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_range_label        VARCHAR(200)    NOT NULL,
    valid_year_week         BOOLEAN         NOT NULL DEFAULT TRUE,
    total_records           INTEGER         NOT NULL DEFAULT 0,
    po_refresh_timestamp    TIMESTAMPTZ,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id)
);

CREATE INDEX idx_po_week_from    ON auctions.purchase_order(week_from_id);
CREATE INDEX idx_po_week_to      ON auctions.purchase_order(week_to_id);
CREATE INDEX idx_po_changed_date ON auctions.purchase_order(changed_date DESC);

CREATE TABLE auctions.po_detail (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    purchase_order_id       BIGINT          NOT NULL
                                            REFERENCES auctions.purchase_order(id)
                                            ON DELETE CASCADE,
    buyer_code_id           BIGINT          NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    product_id              VARCHAR(100)    NOT NULL,
    grade                   VARCHAR(200)    NOT NULL,
    model_name              VARCHAR(200),
    price                   NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    qty_cap                 INTEGER,
    price_fulfilled         NUMERIC(14, 4),
    qty_fulfilled           INTEGER,
    temp_buyer_code         VARCHAR(200),
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_po_detail UNIQUE (purchase_order_id, product_id, grade, buyer_code_id)
);

CREATE INDEX idx_pod_po             ON auctions.po_detail(purchase_order_id);
CREATE INDEX idx_pod_buyer_code     ON auctions.po_detail(buyer_code_id);
CREATE INDEX idx_pod_product_grade  ON auctions.po_detail(product_id, grade);
```

**Flyway safety check:** before implementation start, run
`ls backend/src/main/resources/db/migration/ | tail -5`. Current last
is `V77__data_auctions_reserve_bid.sql`. If V78/V79 are taken, shift
up.

---

## 6. API surface

All endpoints: `/api/v1/admin/purchase-orders/**`
Role gate: `@PreAuthorize("hasAnyRole('Administrator','SalesOps')")`

| Method | Path | Purpose | Response |
|---|---|---|---|
| `GET` | `/` | Paginated list (`yearFrom`, `yearTo`, `state`, `weekFromId`, `weekToId`, `page`, `size`, `sort`) | `PurchaseOrderListResponse` |
| `GET` | `/{id}` | Single PO header + lifecycle state + detail count | `PurchaseOrderRow` |
| `POST` | `/` | Create PO header (week_from/to) | `201 PurchaseOrderRow` |
| `PUT` | `/{id}` | Update week range + metadata (publishes `UPSERT` event) | `200 PurchaseOrderRow` |
| `DELETE` | `/{id}` | Delete PO (cascades details; publishes `DELETE` event) | `204` |
| `GET` | `/{id}/details` | Paginated detail grid (filters: `productId`, `grade`, `buyerCodeId`) | `PODetailListResponse` |
| `POST` | `/{id}/details/upload` | Bulk Excel wipe-and-replace upload | `PODetailUploadResult` |
| `GET` | `/{id}/details/download` | Excel export of detail grid (streamed) | `200 application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |

### DTO shapes

```java
public record PurchaseOrderRow(
    long id,
    long weekFromId, String weekFromLabel,
    long weekToId,   String weekToLabel,
    String weekRangeLabel,
    PurchaseOrderLifecycleState state,
    int totalRecords,
    Instant poRefreshTimestamp,
    Instant changedDate,
    String changedByUsername
) {}

public record PurchaseOrderRequest(
    @NotNull Long weekFromId,
    @NotNull Long weekToId
) {}

public record PODetailRow(
    long id, long purchaseOrderId,
    long buyerCodeId, String buyerCode,
    String productId, String grade, String modelName,
    BigDecimal price, Integer qtyCap,
    BigDecimal priceFulfilled, Integer qtyFulfilled
) {}

public record PODetailUploadResult(
    int createdCount,
    int deletedCount,
    int skippedCount,
    List<UploadError> errors
) {
    public record UploadError(int rowNumber, String productId, String grade,
                              String buyerCode, String reason) {}
}
```

### Error codes

| HTTP | Code | Thrown when |
|---|---|---|
| 400 | `INVALID_REQUEST` | Bean-validation failure |
| 400 | `INVALID_WEEK_RANGE` | `weekFrom > weekTo` (by `mdm.week.weekId`) or week id not found |
| 400 | `UPLOAD_PARSE_ERROR` | Excel unreadable / missing required columns / bad header |
| 400 | `MISSING_BUYER_CODE` | Excel references buyer_code(s) that don't exist (whole upload rejected, missing codes listed) |
| 400 | `UPLOAD_ROW_ERRORS` | Any row-level validation failure rejects the whole upload |
| 404 | `PURCHASE_ORDER_NOT_FOUND` | id does not exist |
| 409 | `DUPLICATE_PO_DETAIL` | Violates `uq_po_detail` (defensive — shouldn't happen post-wipe) |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | Upload not `multipart/form-data` |

### Upload semantics (strict wipe-and-replace)

1. Validate Excel structure (headers: `ProductID`, `Grade`, `ModelName`,
   `Price`, `QtyCap`, `BuyerCode`)
2. Validate every referenced buyer code exists — if any missing, reject
   entire upload with `MISSING_BUYER_CODE` listing offenders (matches
   `VAL_BuyerCode_PO`)
3. Validate week range still valid (matches `VAL_WeekRange_PO`)
4. Validate every row parses cleanly — any row error rejects the whole
   upload (matches Mendix "invalid file" popup behavior)
5. `@Transactional`:
   - `podetailRepository.deleteAllByPurchaseOrderId(poId)`
   - `saveAll(parsedRows)`
   - Update PO `total_records` + `po_refresh_timestamp`
6. Publish `PurchaseOrderChangedEvent(poId, UPSERT)` at `AFTER_COMMIT`
7. Listener pushes full PO (header + all details) to Snowflake

---

## 7. Snowflake integration (push-only)

### 7.1 Push on write

- Event: `PurchaseOrderChangedEvent { purchaseOrderId: long, action: UPSERT|DELETE }`
- Listener: `@TransactionalEventListener(AFTER_COMMIT)` on
  `snowflakeExecutor` (shared with 4A)
- Writer: `PurchaseOrderSnowflakeWriter` interface
  - `LoggingPurchaseOrderSnowflakeWriter` (default,
    `@ConditionalOnProperty(name="po.sync.writer", havingValue="logging", matchIfMissing=true)`)
  - `JdbcPurchaseOrderSnowflakeWriter` (gated by
    `@ConditionalOnProperty(name="po.sync.writer", havingValue="jdbc")`)
- Stored proc (Mendix `SUB_UploadPOToSnowFlake`):
  ```
  CALL AUCTIONS.UPSERT_PURCHASE_ORDER(
      JSON_CONTENT VARIANT,      -- { header, details[] }
      ACTING_USER  STRING,       -- SecurityContext email | "Scheduler"
      FROM_YEAR    INTEGER,
      FROM_WEEK    INTEGER,
      TO_YEAR      INTEGER,
      TO_WEEK      INTEGER
  )
  ```
  Signature confirmed at implementation time against
  `SUB_UploadPOToSnowFlake.md`. If QA proc differs, update writer +
  contract test in lockstep before flipping `po.sync.writer=jdbc` in
  any non-dev env.
- Failure: logged at WARN + recorded in `integration.snowflake_sync_log`,
  **not** propagated. Recovery = admin re-upload.
- Push grain = **per PO** (one event publishes one upsert call with
  the whole detail list). Matches Mendix.
- Bulk upload publishes ONE event at tx commit — not one per row.

### 7.2 Payload shape

```java
public record PurchaseOrderSnowflakePayload(
    long purchaseOrderId, long legacyId,
    WeekRef weekFrom, WeekRef weekTo,
    String weekRangeLabel,
    int totalRecords,
    Instant pushTimestamp,
    List<DetailPayload> details
) {
    public record WeekRef(long id, int year, int weekNumber) {}
    public record DetailPayload(
        long detailId, long legacyId,
        String productId, String grade, String modelName,
        BigDecimal price, Integer qtyCap,
        BigDecimal priceFulfilled, Integer qtyFulfilled,
        String buyerCode, String tempBuyerCode
    ) {}
}
```

### 7.3 Enable/disable guard

`po.sync.enabled` Spring property checked at call time inside the
listener body (not via `@ConditionalOnProperty`) so runtime toggling
via `application-*.yml` reload works without restart. Matches
4A's `eb.sync.enabled` pattern.

### 7.4 Config

```yaml
po:
  sync:
    enabled: true
    writer: logging                  # logging (default) | jdbc
    snowflake-timeout-seconds: 60
```

### 7.5 What's NOT in the sync surface

- ❌ `PurchaseOrderSnowflakeReader` interface
- ❌ `PurchaseOrderSyncScheduledJob` / `@Scheduled` / ShedLock
- ❌ `POST /admin/purchase-orders/sync` manual trigger
- ❌ `purchase_order_sync` singleton table
- ❌ Echo-prevention flag on events

Rationale: Mendix has no PO pull cron, Snowflake is downstream-only,
and there is no reconciling state to pull back from. Adding pull is
YAGNI — revisit only if a separate ops tool starts writing POs
directly to Snowflake.

---

## 8. Data load + testing

### 8.1 Data load

- `migration_scripts/extract_po_data.py` — new extractor script
- Mirrors `extract_eb_data.py` CLI:
  `--source-db qa-0327 | prod-0325`
- Generates `V79__data_auctions_purchase_order.sql` with:
  - 13 `purchase_order` rows (legacy_id preserved; `week_from_id` /
    `week_to_id` resolved via `mdm.week.legacy_id`; `week_range_label`
    rebuilt from week rows; `total_records` recomputed)
  - 9,895 `po_detail` rows (legacy_id preserved; `purchase_order_id` +
    `buyer_code_id` remapped via legacy_id)
- Rows with unresolvable `buyer_code` → skipped with WARN to stderr;
  CI assertion `skip_count <= 10` (4A pattern)
- `pohelper*`, `weeklypo*`, `weekperiod*`, `purchaseorderdoc*`
  intentionally not extracted

### 8.2 Test pyramid

| Layer | Framework | What gets tested |
|---|---|---|
| Unit | JUnit 5 + Mockito + AssertJ | `PurchaseOrderService` create/update/delete + invalid week range; `PODetailService.upload` wipe-and-replace; `POExcelParser` headers + row validation + buyer-code short-circuit; lifecycle-state derivation across DRAFT/ACTIVE/CLOSED; `PurchaseOrderSnowflakePushListener` swallows writer exceptions; payload builder shape stable |
| Repository IT | Testcontainers Postgres | Unique constraint (`uq_po_detail`) violation; `deleteAllByPurchaseOrderId` cascade safety; bulk insert of 10k detail rows < 5s; `findByWeekRangeOverlapping(today)` correctness for 4C-consumer preview |
| Controller IT | `@SpringBootTest` + MockMvc | Role gate: Administrator and SalesOps both pass; Bidder/SalesRep 403; every endpoint happy path; multipart upload with missing buyer_code rejects whole upload; upload with in-row validation error rejects whole upload; export streaming; `DELETE` cascades |
| Snowflake push | Unit (mocked writer) + contract IT (logging writer pins JSON) | AFTER_COMMIT ordering; payload shape stable; writer exception does not roll back Postgres; `po.sync.enabled=false` short-circuits cleanly |
| Config toggles | `@SpringBootTest` with property overrides | `po.sync.writer=logging` (default) wires `LoggingPurchaseOrderSnowflakeWriter`; `po.sync.writer=jdbc` wires `JdbcPurchaseOrderSnowflakeWriter`; conditions are mutually exclusive |
| E2E | Playwright | Administrator: create PO → upload Excel → grid populates → edit week range → re-upload wipes-and-replaces → export roundtrip → delete cascades. SalesOps: same flow passes. Bidder: 403 on every endpoint |

### 8.3 Coverage targets

- `PurchaseOrderService` + `PODetailService` — 85%+
- `PurchaseOrderController` — 85%+
- `PurchaseOrderSnowflakePushListener` + writers — 85%+
- `POExcelParser` — 90%+
- Repositories — 80%+
- Entities + DTOs — not enforced

### 8.4 Test fixtures

```
backend/src/test/resources/fixtures/
├── po-upload-sample.xlsx                ── 20 valid rows, 2 PODetail per buyer_code
├── po-upload-missing-buyer-code.xlsx    ── all-valid rows + 3 rows with unknown buyer_code
├── po-upload-bad-price.xlsx             ── 18 valid + 2 rows with non-numeric Price
└── po-upload-roundtrip.xlsx             ── download → reupload parity
```

### 8.5 Concurrency check

Synthetic IT: 20 concurrent uploads against the same `purchaseOrderId`
— asserts wipe-and-replace serializes cleanly (no partial-wipe
visible to readers), exactly one final row set, event count exactly
matches upload count.

---

## 9. Docs updates (per CLAUDE.md mandate)

| File | Change |
|---|---|
| `docs/api/rest-endpoints.md` | Append `## Purchase Orders (PO)` section — 8 endpoints, role gate, upload semantics |
| `docs/architecture/decisions.md` | ADR `2026-04-24 — Sub-project 4B: PO module port (push-only sync, derived lifecycle, wipe-and-replace upload)` |
| `docs/architecture/data-model.md` | Add `auctions.purchase_order` + `auctions.po_detail` |
| `docs/app-metadata/modules.md` | New `Purchase Order (PO)` entry |
| `docs/business-logic/purchase-order-sync.md` | Push-only semantics, AFTER_COMMIT contract, recovery model, contrast vs EB bidirectional |
| `docs/deployment/setup.md` | Add `po.sync.enabled` / `po.sync.writer` config |
| `docs/testing/coverage.md` | New entry — `auctions.purchaseorder` package, target 85%+ |
| `docs/tasks/auction-po-module-design.md` | This document (overwrites stub) |

---

## 10. Risks + known gaps

### 10.1 Risks

| Risk | Mitigation |
|---|---|
| Snowflake stored proc signature drift | Pinned in §7.1; `LoggingPurchaseOrderSnowflakeWriter` test asserts payload shape across refactors. If QA proc differs, update writer + test in lockstep before flipping `po.sync.writer=jdbc` in non-dev |
| Snowflake push fails silently and admin doesn't notice | `integration.snowflake_sync_log` records every attempt + outcome; PO list grid surfaces `po_refresh_timestamp` so stale POs are visible |
| Wipe-and-replace upload loses fulfillment data (`price_fulfilled`, `qty_fulfilled` reset) | Documented behavior — matches Mendix. Fulfillment columns are externally written by the analytics pipeline and re-overwritten on next sync. Modern preserves them by **not** including them in the Excel column set (parser ignores unknown columns) |
| Admin uploads against an out-of-range week (Mendix `DS_FromWeekPO` clamps to ±42d/+70d) | Service validates `week_from_id` / `week_to_id` are in `mdm.week`; modern does **not** enforce the Mendix dropdown clamp (it's a UI affordance, not a hard rule). Service trusts whatever week ids the client sends |
| 9,895-row data load + 13-PO seed slow at startup | Bulk INSERT in V79 with chunks of 1k rows; expected import < 10s on local Postgres (4A's V77 with 15k rows runs in ~12s) |
| `temp_buyer_code` column carrying redundant data | Documented as Snowflake-payload-parity scratch; if QA confirms `AUCTIONS.UPSERT_PURCHASE_ORDER` doesn't read it, drop in a follow-up V8x migration |
| Race: two admins upload to same PO simultaneously | `@Transactional` + `deleteAllByPurchaseOrderId` runs inside the upload tx; second upload waits on row-level locks, then runs its own wipe-and-replace. Last writer wins; both `UPSERT` events fire post-commit |

### 10.2 Known gaps accepted

- No SharePoint export (`ACT_ExportPOtoExcel` references
  `DataWidgets.Export_To_Excel` JS action). Modern uses Apache POI
  in-process — same column set + headers.
- No `ACT_GET_CurrentUser` — `SecurityContextHolder` directly.
- `pohelper` validation flags (`MissingBuyerCodeValidation`,
  `InValidPOPeriod`, `InvalidFileValidation`) gone — surfaced as HTTP
  400 error codes (`MISSING_BUYER_CODE`, `INVALID_WEEK_RANGE`,
  `UPLOAD_PARSE_ERROR`) instead.
- `SUB_GetOrCreatePOHelper` per-account scratch space — gone
  (stateless HTTP).
- `weekly_po` fulfillment tracker (12k Mendix rows) — gone. 4C only
  needs `po_detail.price`. If per-week fulfillment reporting becomes
  an ops ask, port as a follow-up.
- No PO lifecycle automation (auto-close past-dated POs) — umbrella
  deferred.
- `weekrange` Mendix dropdown clamp (±42d / +70d) — modern does not
  enforce in service layer; UI dropdown can re-implement the clamp
  client-side without a backend change.

---

## 11. Dependencies + ship order

- **Independent of 4A** — no shared tables, entities, or services
- **Blocks 4C** — sub-project 4C's target-price CTE joins against
  `auctions.po_detail` (price, grade, buyer_code) via
  `auctions.purchase_order` (week_from_id, week_to_id); both tables
  + a non-empty data load are required before 4C can ship
- **Until 4B lands:** no behavioral regression. Sub-project 3 currently
  uses no PO data; the stub ranking listener doesn't read PO either.

### 4C contract — column names locked here

For 4C's CTE B `LEFT JOIN GREATEST(...)` term, the columns 4C reads:

| Column | Type | Source table | Purpose |
|---|---|---|---|
| `purchase_order.week_from_id` | `BIGINT` | `auctions.purchase_order` | Effective-week filter (`<= today_week_id`) |
| `purchase_order.week_to_id` | `BIGINT` | `auctions.purchase_order` | Effective-week filter (`>= today_week_id`) |
| `po_detail.purchase_order_id` | `BIGINT` | `auctions.po_detail` | Join to PO header |
| `po_detail.product_id` | `VARCHAR(100)` | `auctions.po_detail` | Join to `bid_data.ecoid` |
| `po_detail.grade` | `VARCHAR(200)` | `auctions.po_detail` | Join to `bid_data.merged_grade` |
| `po_detail.buyer_code_id` | `BIGINT` | `auctions.po_detail` | Join to `bid_data.buyer_code_id` |
| `po_detail.price` | `NUMERIC(14, 4)` | `auctions.po_detail` | Contributes to `GREATEST(...)` |

These names are **frozen** for 4C to consume. Any rename during
implementation must update this table + notify 4C brainstorm.

### Related ADRs

- `docs/tasks/auction-sub4-umbrella-design.md` (umbrella)
- `docs/tasks/auction-eb-module-design.md` (4A — reference style)
- `docs/tasks/auction-eb-module-plan.md` (4A — reference plan)
- `docs/tasks/auction-lifecycle-cron-design.md` (sub-project 0)
- `docs/tasks/auction-bid-data-create-design.md` (sub-project 3)
- *(to be produced)* `docs/tasks/auction-bid-ranking-design.md` (4C)

### Implementation plan

Produced by `superpowers:writing-plans` after this spec is approved by
the user. Saved to `docs/tasks/auction-po-module-plan.md`.
