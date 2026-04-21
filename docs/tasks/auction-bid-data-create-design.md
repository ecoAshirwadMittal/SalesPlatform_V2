# Auction Bid Data Creation + Bidder Dashboard — Design Spec

**Status:** Draft — pending user review
**Date:** 2026-04-21
**Sub-project:** 3 of the auction-lifecycle-cron decomposition
**Source microflows:** `ACT_OpenBidderDashboard`, `ACT_CreateBidData`,
`ACT_CreateBidDataHelper_AggregatedList`, `ACT_BidDataDoc_GetOrCreate`,
`ACT_Round2AggregatedInventory`
**Depends on:** sub-project 0 (cron skeleton), sub-project 2 (R1 init + QBC
schema flatten — V72). Does not depend on sub-project 4 (bid ranking).

---

## 1. Goal

Bidders land on their dashboard and see an editable grid of devices they
can bid on, pre-populated with target prices and maximum quantities. They
auto-save edits as they type and submit the whole round when ready. The
round-level submit is re-callable until the round transitions to `Closed`.

The grid rows are materialized on first dashboard open by a single
CTE-based native SQL query that ports the branching logic of the
1,663-line Mendix `ACT_CreateBidData` microflow into one atomic
`INSERT ... SELECT` per `(buyer_code_id, bid_round_id)`.

---

## 2. Scope

**In scope (sub-project 3):**

- `BidderDashboardService.landingRoute` — ports `ACT_OpenBidderDashboard`
  decision tree (error / download / all-rounds-done / grid).
- `BidDataCreationService.ensureRowsExist` — single-CTE engine that
  materializes `auctions.bid_data` rows for one `(buyer_code, bid_round)`
  pair. Idempotent: skips when rows already exist.
- `BidDataSubmissionService` — save (`PUT`) + submit (`POST`) endpoints.
- `BidderDashboardController` — the five HTTP endpoints below.
- V73 migration — adds `auctions.bid_data_docs` table, drops NOT NULL on
  `bid_data.bid_quantity`, adds nullable FK `bid_data.bid_data_doc_id`.
- Next.js bidder dashboard page (`/bidder/dashboard?buyerCodeId=…`) with
  debounced auto-save and submit button.
- Unit + integration + Playwright test coverage to 85%+ on
  `service/auctions/biddata/**`.

**Out of scope (deferred to later sub-projects):**

- **Sub-project 4 (bid ranking):** threshold validation on submit,
  target-price factor recalc, round-2/round-3 rank computation,
  highest-bid flag maintenance.
- **CSV upload/download:** the `bid_data_docs` entity is created and
  linked per-row, but the file-upload endpoint and blob storage are a
  separate sub-project.
- **Special-treatment buyer setup:** `SUB_HandleSpecialTreatmentBuyerOnRoundStart`
  stays deferred from sub-project 2. The CTE already reads
  `is_special_treatment` flags; once the setup flow lands, no CTE change
  is required.
- **Admin "unlock after submit" endpoint:** submit is naturally
  re-callable until round close, so this Mendix flow is not needed.
- **Bidder dashboard legacy (`PG_Bidder_Dashboard_HOT`) route:** ignored
  — the Mendix flag that routes to it is effectively always false in our
  port.

---

## 3. Architecture

### 3.1 Data flow (bidder dashboard open)

```
bidder logs in (BIDDER role)
   → GET /api/v1/bidder/dashboard?buyerCodeId={id}
       → BidderDashboardService.landingRoute(userId, buyerCodeId)
           resolves one of:
             - errorAuctionNotFound
             - downloadPage (buyer not included, or R2 done + closed)
             - allRoundsDone
             - grid (current round to bid on)
       → if mode=GRID:
           BidDataCreationService.ensureRowsExist(buyerCodeId, bidRoundId)
               short tx, REQUIRES_NEW, timeout=30s
               acquire pg_advisory_xact_lock(hashtext('bid_data_gen'), bid_round_id)
               if count(bid_data where bid_round_id = :id) > 0: skipped
               else: run single CTE native query → N rows inserted
       → BidderDashboardService.loadGrid(bidRoundId, buyerCodeId)
           reads: bid_data rows + round timer state + submit state
   → Next.js page renders BidderDashboardResponse
```

### 3.2 Write path

```
PUT /api/v1/bidder/bid-data/{id}
    body: { bidQuantity: int|null, bidAmount: decimal }
    effect: UPDATE bid_quantity, bid_amount, changed_date, changed_by_id

POST /api/v1/bidder/bid-rounds/{id}/submit
    effect: snapshot bid_* → submitted_*, prior submitted_* → last_valid_*
            bump submitted_datetime, set submitted=true (sticky)
            409 if round_status = 'Closed'
```

### 3.3 Package layout (backend)

```
service/auctions/biddata/
├── BidderDashboardService.java
├── BidderDashboardLandingResult.java       (sealed: Grid | Download | Error | AllDone)
├── BidDataCreationService.java
├── BidDataCreationResult.java              (rowsCreated, skipped, durationMs)
├── BidDataSubmissionService.java
├── BidDataDocService.java                  (wraps repo with getOrCreate semantics)
├── BidDataSubmissionException.java         (409s)
├── BidDataValidationException.java         (400s)
repository/auctions/
├── BidDataCreationRepository.java          (native CTE)
├── BidDataRepository.java                  (CRUD + findByBidRoundId + totals)
├── BidDataDocRepository.java
controller/
└── BidderDashboardController.java
model/auctions/
└── BidDataDoc.java
dto/
├── BidderDashboardResponse.java
├── BidDataRow.java
├── BidDataTotals.java
├── BidRoundSummary.java
├── SchedulingAuctionSummary.java
├── RoundTimerState.java
├── SaveBidRequest.java
└── BidSubmissionResult.java
```

### 3.4 Existing schema reuse

- `auctions.bid_data` (V61) — already has every column the CTE writes,
  plus all submit/last-valid columns.
- `auctions.bid_rounds` (V59) — already tracks `submitted`,
  `submitted_datetime`, `submitted_by_user_id`.
- `auctions.bid_round_selection_filters` (V59) — R2/R3 qualification
  rules, singleton per round.
- `auctions.bid_data_quantity_overrides` (V59) — per-ecoid grade
  quantity overrides.
- `auctions.target_price_factors` + `target_price_factor_filters` (V59)
  — read-only in this sub-project.
- `buyer_mgmt.qualified_buyer_codes` (post-V72 flat shape with direct
  `scheduling_auction_id` + `buyer_code_id` columns).
- `auctions.aggregated_inventory` (V60).

### 3.5 New schema (V73)

Covered in §5.

---

## 4. CTE contract + idempotency

### 4.1 Input parameters

| Param | Source | Notes |
|---|---|---|
| `:bid_round_id` | arg | `auctions.bid_rounds.id` being generated |
| `:scheduling_auction_id` | looked up from bid_round | QBC filter key |
| `:buyer_code_id` | looked up from bid_round | single buyer_code target |
| `:round` | looked up from scheduling_auction | 1 / 2 / 3 |
| `:week_id` | looked up via auction → week | inventory key |
| `:bid_data_doc_id` | BidDataDoc.getOrCreate(user, buyer, week) | FK for inserts |

The service reads the `bid_round` row once, derives the other params,
then binds all six into the CTE.

### 4.2 CTE stages

```
params                       bind inputs, derive buyer_code_type (DW|Wholesale)
existing_check               count(*) from bid_data where bid_round_id = :bid_round_id
qualified_buyer_check        QBC row exists? included? special_treatment?
selection_filter             R2/R3 rules (target_percent, target_value, floors, grades)
round1_submitted_bids        R1 submitted rows for threshold calc
latest_submitted_bids        most-recent submitted row per (ecoid, grade)
buyer_has_prior_bids         boolean: any prior-round submit?
inventory                    aggregated_inventory filtered by week + DW/non-DW branch
inventory_with_threshold     STEP A: per-row bid_meets_threshold flag
inventory_qualified          STEP B: row_visible flag (special / manual / regular)
qty_config                   bid_data_quantity_overrides lookup
prior_scheduling_auction     immediately-prior round's SA id
prior_round_biddata          prior round's bid_data rows for carryforward
biddata_rows                 projection of every column for INSERT
qualified_rows               final filter: row_visible AND existing_check.count = 0

INSERT INTO auctions.bid_data (...) SELECT ... FROM qualified_rows
```

### 4.3 Idempotency invariant

```
COUNT(*) FROM auctions.bid_data WHERE bid_round_id = :bid_round_id
  = 0  →  proceed with INSERT
  > 0  →  no-op, return rowsCreated=0, skipped=true
```

The check runs in Java (pre-query, returns `skipped=true` early) AND
inside the CTE (`existing_check` gate filters out `qualified_rows` if
count > 0) — belt-and-suspenders against concurrent dashboard opens.

### 4.4 Concurrency

Two bidders (same buyer code) opening simultaneously both enter
`ensureRowsExist`. Without a lock, both see `existing_check=0` and both
attempt the INSERT. Prevention:

- `pg_advisory_xact_lock(hashtext('bid_data_gen'), :bid_round_id)` at
  the start of the tx. Automatic release on commit/rollback.
- Second waiter blocks ~milliseconds, acquires lock after first commits,
  re-checks `existing_check`, sees `> 0`, returns `skipped=true`.

Simpler than `SELECT ... FOR UPDATE` on `bid_rounds` (would also block
submits) or a unique constraint (would force swallowing DuplicateKeyEx).

### 4.5 Transaction shape

- `BidDataCreationService.ensureRowsExist`: `@Transactional(REQUIRES_NEW, timeout=30)`.
- Single tx: advisory lock → existence check → CTE execution.
  All-or-nothing.
- Controller's own tx is separate; generation is nested `REQUIRES_NEW`.

### 4.6 Failure modes

| Failure | Handling |
|---|---|
| Buyer code not qualified (no QBC or included=false) | CTE inserts 0 rows; service returns `rowsCreated=0`; controller routes to `DOWNLOAD` mode |
| Scheduling auction not Started | `landingRoute` resolves to `ERROR_AUCTION_NOT_FOUND` before generation runs |
| No aggregated_inventory for this week | CTE inserts 0 rows; grid renders empty state |
| CTE throws | Tx rolls back; controller returns `500`; ERROR log `bid-data generation failed bidRoundId={} buyerCodeId={}` |

---

## 5. Schema (V73 migration)

### 5.1 `auctions.bid_data_docs`

```sql
CREATE TABLE auctions.bid_data_docs (
    id                BIGSERIAL      PRIMARY KEY,
    legacy_id         BIGINT         UNIQUE,
    user_id           BIGINT         NOT NULL REFERENCES identity.users(id),
    buyer_code_id     BIGINT         NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    week_id           BIGINT         NOT NULL REFERENCES mdm.week(id),
    file_name         VARCHAR(500),
    file_ref          VARCHAR(1000),
    file_size         BIGINT,
    content_type      VARCHAR(200),
    uploaded_datetime TIMESTAMPTZ,
    created_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    changed_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_bdd_user_buyer_week UNIQUE (user_id, buyer_code_id, week_id)
);

CREATE INDEX idx_bdd_user_week ON auctions.bid_data_docs(user_id, week_id);

COMMENT ON TABLE auctions.bid_data_docs IS
  'Per-(user,buyer_code,week) document slot for bidder dashboard. File fields stay null until CSV upload ships.';
```

### 5.2 `auctions.bid_data` alterations

```sql
-- Allow null bid_quantity: NULL = no-cap sentinel (bidder accepts any quantity)
ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP DEFAULT;
ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP NOT NULL;

-- Link to bid_data_docs (nullable — not every bid_data row has a doc)
ALTER TABLE auctions.bid_data
    ADD COLUMN bid_data_doc_id BIGINT REFERENCES auctions.bid_data_docs(id) ON DELETE SET NULL;

CREATE INDEX idx_bd_doc ON auctions.bid_data(bid_data_doc_id);

COMMENT ON COLUMN auctions.bid_data.bid_quantity IS
  'Buyer-entered bid quantity. NULL = no cap (accept any); 0 = decline; N = max N units.';
COMMENT ON COLUMN auctions.bid_data.bid_data_doc_id IS
  'Document slot shared across rows for this (user, buyer_code, week). NULL when no doc created.';
```

### 5.3 `BidDataDoc` entity + repository

```java
@Entity @Table(name = "bid_data_docs", schema = "auctions")
public class BidDataDoc {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long legacyId;
    private Long userId;
    private Long buyerCodeId;
    private Long weekId;
    private String fileName;
    private String fileRef;
    private Long fileSize;
    private String contentType;
    private Instant uploadedDatetime;
    private Instant createdDate;
    private Instant changedDate;
    // getters, setters, equals/hashCode on id
}

public interface BidDataDocRepository extends JpaRepository<BidDataDoc, Long> {
    Optional<BidDataDoc> findByUserIdAndBuyerCodeIdAndWeekId(long userId, long buyerCodeId, long weekId);
}
```

### 5.4 GetOrCreate semantics

Lives on `BidDataDocService`. The repository exposes only the finder; the
upsert gets/creates logic stays in the service so the `@Transactional`
boundary is explicit and testable.

```java
@Service
public class BidDataDocService {
    private final BidDataDocRepository repo;
    private final Clock clock;

    public BidDataDocService(BidDataDocRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Transactional
    public BidDataDoc getOrCreate(long userId, long buyerCodeId, long weekId) {
        return repo.findByUserIdAndBuyerCodeIdAndWeekId(userId, buyerCodeId, weekId)
            .orElseGet(() -> {
                BidDataDoc d = new BidDataDoc();
                d.setUserId(userId);
                d.setBuyerCodeId(buyerCodeId);
                d.setWeekId(weekId);
                Instant now = clock.instant();
                d.setCreatedDate(now);
                d.setChangedDate(now);
                return repo.save(d);
            });
    }
}
```

Called once per `ensureRowsExist` invocation, before the CTE runs. The
resolved `id` is bound into the CTE as `:bid_data_doc_id`.

---

## 6. Submit semantics

### 6.1 Save endpoint (auto-save)

```
PUT /api/v1/bidder/bid-data/{id}
Body: { bidQuantity: int | null, bidAmount: decimal }
```

Validation:
- Bidder's user ∈ `user_buyers(buyer_code_id = bid_data.buyer_code_id)`
  → else 403 `NOT_YOUR_BID_DATA`.
- `bid_round.round_status != 'Closed'` → else 409 `ROUND_CLOSED`.
- `bidAmount != null AND bidAmount >= 0` → else 400 `INVALID_AMOUNT`.
- If `bidQuantity != null`: `bidQuantity >= 0` AND
  `bidQuantity <= maximum_quantity` → else 400 `INVALID_QUANTITY`.
  (If `bidQuantity = null`, skip cap check — no cap.)

Updates: `bid_quantity`, `bid_amount`, `changed_date`, `changed_by_id`.
Returns updated `BidDataRow`.

Rate-limited to 60 req/min/user/bid_round to cap auto-save abuse.

### 6.2 Submit endpoint

```
POST /api/v1/bidder/bid-rounds/{id}/submit
Body: (none)
```

Validation:
- Bidder owns buyer_code on this bid_round → else 403 `NOT_YOUR_BID_ROUND`.
- `round_status != 'Closed'` → else 409 `ROUND_CLOSED`.

Effect (single `@Transactional(REQUIRES_NEW, timeout=15)`). `:user_id`
and `:username` are resolved from the `SecurityContextHolder` principal
(`User.id` and `User.email` — same pattern as the existing
`AuctionService.scheduleAuction` flow):

```sql
UPDATE auctions.bid_data SET
    last_valid_bid_quantity = submitted_bid_quantity,   -- NULL on first submit
    last_valid_bid_amount   = submitted_bid_amount,
    submitted_bid_quantity  = bid_quantity,
    submitted_bid_amount    = bid_amount,
    submitted_datetime      = NOW(),
    submit_user             = :username,
    changed_date            = NOW(),
    changed_by_id           = :user_id
WHERE bid_round_id = :bid_round_id;

UPDATE auctions.bid_rounds SET
    submitted            = TRUE,
    submitted_datetime   = NOW(),
    submitted_by_user_id = :user_id,
    changed_date         = NOW()
WHERE id = :bid_round_id;
```

### 6.3 Re-submit behavior

| Prior state | Action | Post state |
|---|---|---|
| submitted=false | submit | submitted=true, last_valid_* = NULL, submitted_* = bid_* |
| submitted=true, user edits, resubmits | save* → submit | last_valid_* = prior submitted_*, submitted_* = new bid_*, submitted_datetime bumped |
| submitted=true, round closes, user retries | submit | 409 ROUND_CLOSED, no writes |
| submitted=false, round closes | (nothing) | row unchanged; buyer declined |

`*` save is triggered automatically by each UI edit, not explicitly.

### 6.4 Round-close behavior (handled by sub-project 4)

At close, whatever is in `submitted_*` is final. `last_valid_*` is
preserved for forensic reference. `bid_*` working values are ignored —
unsaved edits are lost by design.

### 6.5 Validation scope — this sub-project only

- Non-negative quantity + amount.
- `bidQuantity` cap: `null` (no cap) OR `[0, maximum_quantity]`.
- Round not closed.

**Deferred to sub-project 4 (bid ranking):**

- Target-price threshold checks (`bid_amount >= target_price`).
- Minimum-bid floor validation.
- Aggregate value floors (`total_value_floor`).
- Round 2/3 qualification gates based on R1 performance.

### 6.6 Permission model

- `BIDDER` role: can call save/submit only for rows whose
  `buyer_code_id ∈ user_buyers(user_id=me)`. Enforced in service layer
  with a single guard query per request.
- `Administrator`: can call the same endpoints (support case coverage).
- `SalesOps` and other roles: 403.

---

## 7. API surface

### 7.1 GET /api/v1/bidder/dashboard

Query: `buyerCodeId` (long, required).
Roles: BIDDER (own buyer codes) | Administrator.

Flow:
1. `BidderDashboardService.landingRoute(userId, buyerCodeId)` →
   `BidderDashboardLandingResult`.
2. If mode = GRID:
   `BidDataCreationService.ensureRowsExist(buyerCodeId, bidRoundId)` inline.
3. `BidderDashboardService.loadGrid(bidRoundId, buyerCodeId)` →
   `BidderDashboardResponse`.

Response: 200 OK, `BidderDashboardResponse`:

```java
record BidderDashboardResponse(
    LandingMode mode,
    SchedulingAuctionSummary auction,
    BidRoundSummary bidRound,
    List<BidDataRow> rows,
    BidDataTotals totals,
    RoundTimerState timer
) {}

enum LandingMode { GRID, DOWNLOAD, ERROR_AUCTION_NOT_FOUND, ALL_ROUNDS_DONE }
```

Errors:
- 400 `MISSING_BUYER_CODE` — buyerCodeId omitted.
- 403 `NOT_YOUR_BUYER_CODE`.
- 404 `AUCTION_NOT_FOUND` — no active scheduling auction.
- 500 `GENERATION_FAILED` — CTE threw; body has log correlation id.

### 7.2 PUT /api/v1/bidder/bid-data/{id}

Body: `{ bidQuantity: int|null, bidAmount: decimal }`.
Roles: BIDDER (own row) | Administrator.

Response: 200 OK, `BidDataRow` (updated).

Errors:
- 400 `INVALID_QUANTITY`, 400 `INVALID_AMOUNT`.
- 403 `NOT_YOUR_BID_DATA`.
- 404 `BID_DATA_NOT_FOUND`.
- 409 `ROUND_CLOSED`.
- 429 `RATE_LIMIT` — > 60 req/min/user/bid_round.

### 7.3 POST /api/v1/bidder/bid-rounds/{id}/submit

Body: (none).
Roles: BIDDER (own round) | Administrator.

Response: 200 OK:

```java
record BidSubmissionResult(
    long bidRoundId,
    int rowCount,
    Instant submittedDatetime,
    boolean resubmit
) {}
```

Errors:
- 403 `NOT_YOUR_BID_ROUND`.
- 404 `BID_ROUND_NOT_FOUND`.
- 409 `ROUND_CLOSED`.

### 7.4 No admin recovery endpoint

Per the synchronous-on-open trigger choice, generation runs every time
a bidder opens the dashboard. If it fails, the bidder refreshes. No
`/admin/.../generate` surface is needed.

---

## 8. Frontend (Next.js bidder dashboard)

### 8.1 Route

`frontend/src/app/(dashboard)/bidder/dashboard/page.tsx`.

Guard: existing `(dashboard)/layout.tsx` redirects unauthed users. New
role check: BIDDER / Administrator only; other roles → 404.

### 8.2 Component tree

```
BidderDashboardPage                    server component, initial fetch
  BuyerCodeSelector                    dropdown, persists in URL
  DashboardHeader                      client
    RoundTimer                         countdown via Clock.instant() + setInterval
    SubmitStateBadge                   "Not submitted" / "Submitted at HH:MM"
  BidGrid                              client; editable table
    BidGridRow[]                       per device-grade
      BidQuantityCell                  nullable int input; blank = no cap
      BidAmountCell                    non-null decimal input
  SubmitBar                            sticky footer
    TotalPayoutDisplay
    SubmitButton                       disabled when round closed
```

### 8.3 Auto-save hook

```typescript
// hooks/useAutoSaveBid.ts
function useAutoSaveBid(rowId: number, onSaved: (row: BidDataRow) => void) {
  const [pending, setPending] = useState<SaveBidPayload | null>(null);
  const debounced = useDebouncedCallback(async (payload: SaveBidPayload) => {
    const row = await apiFetch<BidDataRow>(`/bidder/bid-data/${rowId}`, {
      method: 'PUT',
      body: payload,
    });
    onSaved(row);
    setPending(null);
  }, 500);
  return {
    dirty: pending !== null,
    save: (payload: SaveBidPayload) => {
      setPending(payload);
      debounced(payload);
    },
  };
}
```

### 8.4 State model

Page-level `useState<Map<bidDataId, BidDataRow>>` seeded from the server
payload. Each row's save callback updates its map entry on successful
PUT response. Submit button reads the full map to compute running
`totalPayout`. No optimistic updates — 500ms debounce is fast enough that
server-authoritative state works cleanly.

### 8.5 Styling parity

Match Mendix `PG_Bidder_Dashboard_DG2`:

- Teal header (`#407874`).
- Row striping (`#F7F7F7`).
- Submit button primary teal.
- Grid column order + widths match legacy QA at
  `https://buy-qa.ecoatmdirect.com/p/bidder/dashboard`.

Playwright visual-regression baseline captured during Phase 6.

---

## 9. Testing strategy

Target: 85%+ coverage on `service/auctions/biddata/**` and
`repository/auctions/BidDataCreationRepository.java`.

### 9.1 Backend unit tests

| Class | Scenarios |
|---|---|
| `BidDataCreationServiceTest` | existence pre-check short-circuits; advisory lock acquired; CTE invoked with all six bound params; result carries `rowsCreated`/`skipped`/`durationMs` |
| `BidderDashboardServiceTest` | landing matrix: no-auction, R1 not started, buyer not included, R2 done + R2 closed + R2 active + currentRound != 3, all-rounds-done, healthy → GRID |
| `BidDataSubmissionServiceTest` | save: ownership guard, quantity range, blank allowed, amount validation; submit: first submit has null last_valid_*, resubmit copies prior submitted → last_valid, closed round rejects |
| `BidDataDocServiceTest` | getOrCreate returns existing on second call; uq constraint enforced |

### 9.2 Backend integration tests (Testcontainers Postgres)

| Class | Scenarios |
|---|---|
| `BidDataCreationRepositoryIT` | R1 DW buyer, R1 Wholesale buyer, R2 qualified (target_percent met), R2 unqualified (target_value miss), R3 with criteria (bid_percentage_variation), R3 without criteria, special-treatment buyer (bypass filters), manual qualification (bypass threshold), prior-round carryforward (previous_round_* populated), empty inventory, existing rows = skip, concurrent generation = advisory lock blocks second |
| `BidDataSubmissionIT` | save updates only bid_*, submit copies correctly across first + resubmit, round close blocks further submits |
| `BidderDashboardFullChainIT` | full Spring context: round start → listener no-op (deferred) → GET /bidder/dashboard → rows generated → PUT save → POST submit → POST resubmit → simulated round close → POST submit rejected |

### 9.3 Controller tests (MockMvc)

`BidderDashboardControllerTest`:
- 200 happy path (BIDDER role, own buyer code).
- 403 wrong user (BIDDER role, someone else's buyer code).
- 404 no active auction.
- 409 closed round on save + submit.
- 400 invalid payloads (negative quantity, negative amount, missing amount).
- 429 rate limit after 60 PUT/min.

### 9.4 CTE fixture builder

```java
// test/java/.../fixtures/BidDataScenario.java
public final class BidDataScenario {
    public BidDataScenario round(int round) { ... }
    public BidDataScenario buyerCodeType(BuyerCodeType t) { ... }
    public BidDataScenario specialTreatment(boolean b) { ... }
    public BidDataScenario inventory(Map<String, InventorySpec> rows) { ... }
    public BidDataScenario priorRoundBids(Map<String, BidSpec> prior) { ... }
    public BidDataScenario qualificationFilter(FilterSpec f) { ... }
    public long commitAndReturnBidRoundId() { ... }
}
```

Every `BidDataCreationRepositoryIT` test uses this builder; scenarios
read as declarative statements, not 40-line raw INSERTs.

### 9.5 Frontend tests

- `BidGrid.test.tsx` — Vitest + Testing Library: debounced save fires
  exactly once for rapid keystrokes; blank `bidQuantity` serializes as
  `null`; 429 response surfaces inline row-level error.
- `bidder-dashboard.spec.ts` — Playwright: login as
  `bidder@buyerco.com`, navigate to
  `/bidder/dashboard?buyerCodeId=…`, edit rows, submit, resubmit,
  verify final state. Runs against seeded dev fixtures.

---

## 10. Non-goals + risks

### 10.1 Non-goals

- Threshold/ranking validation (sub-project 4).
- CSV upload/download (separate sub-project).
- Admin unlock-after-submit (not needed — submit is naturally re-callable).
- Special-treatment buyer setup flow (remains deferred from sub-project 2).
- Email notifications on submit (deferred; existing `PWSEmailService`
  pattern will be reused when the notification sub-project lands).

### 10.2 Risks + mitigations

| Risk | Mitigation |
|---|---|
| CTE rewrite drift from Mendix logic | `BidDataCreationRepositoryIT` scenario matrix covers every branch; fixture builder keeps tests declarative so branches are easy to add |
| Advisory-lock contention on high-traffic round start | Lock held only for the duration of one INSERT; ~O(ms). Two dashboards opening within ms of each other serialize cleanly |
| Auto-save PUT storm on slow network | 500ms debounce + 60 req/min/user/round rate limit. Backend returns 429, UI surfaces inline error without blocking edit |
| NULL `bid_quantity` semantics confused downstream | Explicit COMMENT on column; sub-project 4 ranking code must treat NULL as "no cap" via `COALESCE(bid_quantity, :infinity)` in its queries |
| `bid_data_docs` row written without CSV upload ever happening | Bounded write cost: ~580 buyer codes × N users × 52 weeks/yr. Acceptable; rows are ~200 bytes each |
| Sub-project 4 needs submit-time validation we deferred | Submit endpoint's 15s tx has headroom to add threshold checks; service layer gets new `SubmitValidationService` injected; no API shape change |

---

## 11. Open questions

None at spec-review time. Update this section if review surfaces new
decisions.

---

## 12. References

- Mendix source (all in `migration_context/backend/`):
  - `ACT_OpenBidderDashboard.md`
  - `ACT_CreateBidData.md`
  - `ACT_CreateBidDataHelper_AggregatedList.md`
  - `ACT_CreateBidDataHelper_2.md`
  - `ACT_BidDataDoc_GetOrCreate.md`
  - `ACT_Round2AggregatedInventory.md`
  - `services/SUB_CheckRoundIncluded.md`
  - `services/SUB_AuctionTimerHelper.md`
- Schema:
  - `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`
  - `V59__auctions_bid_rounds_and_configuration.sql`
  - `V60__auctions_aggregated_inventory.sql`
  - `V61__auctions_bid_data.sql`
  - `V72__buyer_mgmt_qbc_flatten.sql`
  - (new) `V73__auctions_bid_data_docs_and_bid_data_nullable_qty.sql`
- Related ADRs:
  - 2026-04-22 (sub-project 2: R1 init + QBC flatten)
  - 2026-04-21 (sub-project 1: auction status Snowflake push)
  - 2026-04-20 (sub-project 0: auction lifecycle cron)
- Sub-project plan doc: `docs/tasks/auction-bid-data-create-plan.md`
  (to be produced by `superpowers:writing-plans` after this spec is
  approved).
