# Design — Sub-project 4C: Bid Ranking + Target-Price Recalc

**Status:** Approved (ready for implementation plan)
**Date:** 2026-04-30
**Parent umbrella:** `docs/tasks/auction-sub4-umbrella-design.md`
**Mendix sources:**
- `migration_context/backend/ACT_TriggerBidRankingCalculation_TryCatch.md`
- `migration_context/backend/ACT_TriggerBidRankingCalculation.md`
- `migration_context/backend/ACT_CalculateTargetPrice.md`
- `migration_context/backend/ACT_UpdateTargetPrice.md`
- `migration_context/backend/ACT_ListHighBids.md`
- `migration_context/backend/services/SUB_UpdateAETargetPriceMaxBid.md`

**Depends on:** 4A (EB module — shipped; `auctions.reserve_bid` + `ecoatm_eb` schema in V76/V77) and 4B (PO module — shipped; `auctions.purchase_order` + `auctions.po_detail` in V80/V81). Both modules' schema is final and listed as inputs to this design.

---

## 1. Background

Sub-project 3 (`auction-bid-data-create-plan.md`) shipped the bidder dashboard
and synchronous `bid_data` generation but explicitly deferred bid ranking and
target-price recalc to "sub-project 4". The umbrella decomposition
(`auction-sub4-umbrella-design.md`) carved that work into three siblings:

- **4A** ported `ecoatm_eb` (reserve floors).
- **4B** ported `ecoatm_po` (purchase-order floors).
- **4C** (this doc) replaces `BidRankingStubListener` with the real
  ranking + target-price calc, consuming EB + PO floors from 4A + 4B.

`BidRankingStubListener.java` currently logs "would rank bids + calc target
price" on `RoundClosedEvent` (round ∈ {1, 2}) and does nothing. Until 4C
lands, downstream Snowflake reporting against `aggregated_inventory` and
`bid_data` rank columns is stale.

The umbrella locks down the process model (§4):

- Two independent processes — `RANKING` and `TARGET_PRICE`.
- Each runs in its own `@Transactional(REQUIRES_NEW)`.
- Each writes its own success/failure status flags on
  `auctions.scheduling_auctions`.
- One process failing does NOT stop the other.

This document specifies the implementation of those two processes plus
their admin recovery endpoints, Snowflake push events, and schema
additions.

---

## 2. Scope

### In scope

- One Flyway migration adding eight status columns to
  `auctions.scheduling_auctions` and one config column to
  `auctions.bid_ranking_config`.
- `RecalcOrchestrator` + `BidRankingService` + `TargetPriceRecalcService`
  classes implementing the two-process model.
- `RecalcRoundClosedListener` replacing `BidRankingStubListener`.
- Two native-SQL repositories — `BidRankingRepository` (DENSE_RANK CTE)
  and `TargetPriceRecalcRepository` (GREATEST CTE).
- `BidRankingUpdatedEvent` + `TargetPriceRecalculatedEvent` records.
- `BidRankingSnowflakePushListener` +
  `TargetPriceSnowflakePushListener` — full week MERGE pushes against
  Snowflake `AUCTIONS.BUYER_BID` + `AUCTIONS.TARGET_PRICE_AUDIT`.
- `RecalcAdminController` exposing two REST endpoints:
  - `POST /api/v1/admin/auctions/scheduling-auctions/{id}/re-rank`
  - `POST /api/v1/admin/auctions/scheduling-auctions/{id}/recalculate-target-price`
- Tests at all four layers per §10 + 85%+ coverage target.
- ADR + docs updates per CLAUDE.md mandate.

### Out of scope (deferred or owned elsewhere)

- EB or PO schema changes (4A / 4B own these).
- R3-close processing — R3 is terminal; no R4 to rank/recalc for.
  `RecalcRoundClosedListener` gates on `round() ∈ {1, 2}`.
- R2 buyer assignment (`SUB_AssignRoundTwoBuyers`) — separate sub-project,
  triggered by `RoundStartedEvent(round=2)`. `R2InitStubListener` stays.
- R3 init / R3 pre-process — separate sub-project. `R3InitStubListener` +
  `R3PreProcessStubListener` stay.
- Threshold validation on submit (dropped per umbrella §6).
- `highest_bid` flag maintenance (dropped — ranking supersedes).
- CSV upload for `bid_data_docs` (separate sub-project).
- Frontend admin UI for the two endpoints — REST-only in 4C; UI buttons
  ship as a follow-on if ops asks.

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 3.1 | **Reserve-floor inclusion in ranking is configurable** via `auctions.bid_ranking_config.include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE`. | Matches the two Mendix query variants (`ExcludeEB` / `IncludeEB`) without locking ops in. Default TRUE matches the most-common Mendix-prod observation. |
| 3.2 | **Synchronous execution on the listener thread.** Both processes run inside `@TransactionalEventListener(AFTER_COMMIT)` on the cron tick's listener thread. Each is a `REQUIRES_NEW` tx. | Both queries are bounded bulk SQL; cron has ShedLock; sync gives deterministic test ordering. Move to async if profiling shows lag. |
| 3.3 | **Two admin endpoints, not one.** `re-rank` and `recalculate-target-price` are independent. | Mirrors the two-process model. Lets ops fix exactly the failed process when EB/PO data changes after a bad first run. |
| 3.4 | **Per-process Snowflake events.** `BidRankingUpdatedEvent` after RANKING success; `TargetPriceRecalculatedEvent` after TARGET_PRICE success. Two listeners. | Honours independent-failure semantics: ranking-correct data reaches Snowflake even if target-price is broken. Mirrors 4A/4B's event-per-write pattern. |
| 3.5 | **Snowflake payload = full week refresh per process.** Each listener stages the entire `(week, R+1)` slice and MERGEs. | Bounded payload (few thousand rows); stateless listener; recovery is "fire the event again". |
| 3.6 | **Admin endpoints reject 409 when status = `RUNNING`.** Allow from any other state. | Single-statement guard via conditional UPDATE. Prevents accidental double-fire. |
| 3.7 | **TARGET_PRICE is one bulk CTE UPDATE — no Java loop.** | Mendix does it per-row in a microflow loop because that's the platform's idiom; modern Postgres does it in one statement. Faster, atomic, easier to test. |
| 3.8 | **Status sub-tx pattern for FAILED writes.** Failure path opens a nested `REQUIRES_NEW` to write `*_status='FAILED'` + `*_error` even when the recalc tx rolls back. Same idiom as `ReserveBidService`. | Without this, the rolled-back recalc UPDATE also rolls back the FAILED status row — leaving status stuck at `RUNNING`. |
| 3.9 | **No automatic retry inside a run.** | Both UPDATEs are idempotent. Cron-tick retry isn't free (round closed once). Recovery path is the admin endpoint. |

---

## 4. Architecture

```
┌────────────────────────────────────────────────────────┐
│  RoundClosedEvent (round ∈ {1, 2})                     │
│  AFTER_COMMIT, listener thread                         │
└────────────────────────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────────┐
        │  RecalcRoundClosedListener        │
        │  - delegates to RecalcOrchestrator│
        └───────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────────┐
        │  RecalcOrchestrator               │
        │  - runs RANKING (REQUIRES_NEW)    │
        │  - runs TARGET_PRICE (REQUIRES_NEW)│
        │  - one failing does NOT stop other│
        └───────────────────────────────────┘
                  │              │
        ┌─────────┘              └─────────┐
        ▼                                  ▼
┌───────────────────┐           ┌───────────────────────┐
│ BidRankingService │           │ TargetPriceRecalcSvc  │
│ status flip RUN   │           │ status flip RUNNING   │
│ DENSE_RANK CTE    │           │ single CTE UPDATE     │
│ status SUCCESS or │           │ status SUCCESS or     │
│ FAILED            │           │ FAILED                │
│ publishEvent ↦    │           │ publishEvent ↦        │
│ BidRankingUpd…    │           │ TargetPriceRecalc…    │
└───────────────────┘           └───────────────────────┘
        │                                  │
        ▼                                  ▼
┌───────────────────┐           ┌───────────────────────┐
│ AFTER_COMMIT push │           │ AFTER_COMMIT push     │
│ BidRankingSnowfl… │           │ TargetPriceSnowfl…    │
│ Listener — full   │           │ Listener — full       │
│ week MERGE into   │           │ week MERGE into       │
│ Snowflake         │           │ Snowflake             │
│ AUCTIONS.BUYER_BID│           │ TARGET_PRICE_AUDIT    │
└───────────────────┘           └───────────────────────┘
```

Two parallel admin REST surfaces mirror the two processes:

- `POST /api/v1/admin/auctions/scheduling-auctions/{id}/re-rank` →
  `BidRankingService.recalculate(roundId)` only.
- `POST /api/v1/admin/auctions/scheduling-auctions/{id}/recalculate-target-price`
  → `TargetPriceRecalcService.recalculate(roundId)` only.

Both endpoints reject with 409 if the relevant `*_status = 'RUNNING'`;
otherwise flip to `RUNNING` and proceed.

### Java package layout

```
service/auctions/recalc/
  RecalcOrchestrator.java
  BidRankingService.java
  TargetPriceRecalcService.java
  RecalcStatus.java                     // enum: PENDING, RUNNING, SUCCESS, FAILED
  RecalcRoundClosedListener.java        // replaces BidRankingStubListener

repository/auctions/
  BidRankingRepository.java             // single DENSE_RANK CTE (native query)
  TargetPriceRecalcRepository.java      // single GREATEST CTE (native query)

event/
  BidRankingUpdatedEvent.java           // record(roundId, round, weekId, auctionId)
  TargetPriceRecalculatedEvent.java     // record(roundId, round, weekId, auctionId)

service/auctions/snowflake/
  BidRankingSnowflakePushListener.java
  TargetPriceSnowflakePushListener.java

controller/admin/
  RecalcAdminController.java

dto/admin/
  RecalcResponse.java                   // status, error, startedAt, finishedAt
```

`BidRankingStubListener.java` is deleted in the same PR.
`R2InitStubListener`, `R3InitStubListener`, and `R3PreProcessStubListener`
are explicitly out of scope and stay as-is.

---

## 5. Schema (V82)

Single Flyway migration: `V82__auctions_recalc_status_and_config.sql`.

### 5.1 `auctions.scheduling_auctions` — eight new columns

```sql
ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN ranking_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN ranking_error               TEXT,
    ADD COLUMN ranking_started_at          TIMESTAMPTZ,
    ADD COLUMN ranking_finished_at         TIMESTAMPTZ,
    ADD COLUMN target_price_status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN target_price_error          TEXT,
    ADD COLUMN target_price_started_at     TIMESTAMPTZ,
    ADD COLUMN target_price_finished_at    TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_ranking_status
        CHECK (ranking_status      IN ('PENDING','RUNNING','SUCCESS','FAILED')),
    ADD CONSTRAINT chk_sa_target_price_status
        CHECK (target_price_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.ranking_status IS
    '4C recalc: PENDING (round not yet closed) | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.target_price_status IS
    '4C recalc: PENDING | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.ranking_error IS
    '4C recalc: exception class + message (truncated to 4000 chars) on FAILED';
```

Rationale for `NOT NULL DEFAULT 'PENDING'`: every existing row gets a sane
starting state without a separate backfill statement. `PENDING` is the
correct semantic — those rounds have not gone through 4C recalc.

### 5.2 `auctions.bid_ranking_config` — one new column

```sql
ALTER TABLE auctions.bid_ranking_config
    ADD COLUMN include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN auctions.bid_ranking_config.include_reserve_floor IS
    '4C: TRUE → reserve_bid rows participate in DENSE_RANK as priority bidders; FALSE → ranking is bid_data only';
```

### 5.3 No data migration

The singleton `bid_ranking_config` row picks up `TRUE` via the column
default. No `UPDATE` statement needed.

---

## 6. API surface

### 6.1 Endpoints

| Method | Path | Auth | Body | 200 | 4xx |
|---|---|---|---|---|---|
| POST | `/api/v1/admin/auctions/scheduling-auctions/{id}/re-rank` | `ROLE_ADMIN` | none | `RecalcResponse` | 404 unknown id; 409 status=RUNNING; 422 round ≠ 1,2 |
| POST | `/api/v1/admin/auctions/scheduling-auctions/{id}/recalculate-target-price` | `ROLE_ADMIN` | none | `RecalcResponse` | 404 unknown id; 409 status=RUNNING; 422 round ≠ 1,2 |

### 6.2 Response DTO

```java
public record RecalcResponse(
    long schedulingAuctionId,
    int closedRound,                  // 1 or 2
    String status,                    // "SUCCESS" | "FAILED"
    String error,                     // nullable
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int rowsAffected,
    long durationMs
) {}
```

### 6.3 Error mapping

Reuses existing infrastructure where possible:

- New exception `RecalcAlreadyRunningException` (mirrors the
  `RoundAlreadyTransitionedException` shape from P8 lifecycle work).
  Mapped 409 in `GlobalExceptionHandler` via a new `@ExceptionHandler`.
- `EntityNotFoundException` for unknown `schedulingAuctionId` → existing
  404 mapping.
- `IllegalArgumentException` for `round ∉ {1, 2}` → existing 422 mapping.

### 6.4 SecurityConfig delta

Add matcher in `SecurityConfig.java`:

```java
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/re-rank",
                 "/api/v1/admin/auctions/scheduling-auctions/*/recalculate-target-price")
    .hasRole("ADMIN")
```

---

## 7. Snowflake integration (push-only)

### 7.1 Targets

| Listener | Snowflake table | Slice key | Operation |
|---|---|---|---|
| `BidRankingSnowflakePushListener` | `AUCTIONS.BUYER_BID` | `(week, R+1)` | MERGE updating `ROUND{R+1}_RANK` for all rows in the slice |
| `TargetPriceSnowflakePushListener` | `AUCTIONS.TARGET_PRICE_AUDIT` | `(week, R+1)` | UPDATE prior slice `IS_ACTIVE = FALSE`; INSERT new slice rows with incremented `RUN_VERSION` and `IS_ACTIVE = TRUE` |

`AUCTIONS.BUYER_BID` and `AUCTIONS.TARGET_PRICE_AUDIT` are pre-existing
Snowflake tables; see `docs/data-warehouse/snowflake-auctions-tables.md`.

### 7.2 Listener pattern

Both follow the 4A/4B pattern:

```java
@Component
public class BidRankingSnowflakePushListener {
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async(AsyncConfig.SNOWFLAKE_EXECUTOR)
    public void onBidRankingUpdated(BidRankingUpdatedEvent event) {
        if (!snowflakeEnabled) {
            log.info("Snowflake push skipped (disabled) — event={}", event);
            return;
        }
        try {
            writer.pushBidRankings(event.weekId(), event.round() + 1);
        } catch (RuntimeException ex) {
            log.error("Snowflake push failed", ex);
            syncLogRepo.recordFailure("BUYER_BID", event.weekId(), ex);
        }
    }
}
```

### 7.3 Writer interface

```java
public interface BidRankingSnowflakeWriter {
    void pushBidRankings(long weekId, int targetRound);
}
public interface TargetPriceSnowflakeWriter {
    void pushTargetPrices(long weekId, int targetRound);
}
```

Default impls (`logging.*`) used when `snowflake.enabled=false` — log
the slice metadata + row count, no JDBC. JDBC impls (`jdbc.*`) gated
by `snowflake.enabled=true` per existing `application.yml` convention.

### 7.4 Configuration

Reuses existing `snowflake.*` config from Theme 2 (`todos-resolution-plan.md`
Phase 2.1). Adds two writer toggles in `application.yml`:

```yaml
recalc:
  snowflake:
    bid-ranking-writer: logging      # logging | jdbc
    target-price-writer: logging     # logging | jdbc
```

---

## 8. Data flow — SQL contracts

### 8.1 RANKING — DENSE_RANK CTE

For closed round R, ranks the bids carrying into round R+1.

```sql
WITH params AS (
  SELECT
    sa.id            AS scheduling_auction_id,
    sa.auction_id    AS auction_id,
    a.week_id        AS week_id,
    sa.round         AS closed_round,
    cfg.minimum_bid           AS min_bid,
    cfg.display_rank          AS min_display_rank,
    cfg.maximum_rank          AS max_display_rank,
    cfg.include_reserve_floor AS include_eb
  FROM auctions.scheduling_auctions sa
  JOIN auctions.auctions a ON a.id = sa.auction_id
  CROSS JOIN auctions.bid_ranking_config cfg
  WHERE sa.id = CAST(:round_id AS bigint)
),
candidates AS (
  -- Auction-bidder rows (always in)
  SELECT bd.id AS bid_data_id, bd.ecoid, bd.merged_grade, bd.submitted_bid_amount AS amount
    FROM auctions.bid_data bd
    JOIN params p ON p.week_id = bd.week_id
   WHERE bd.bid_round = p.closed_round
     AND bd.submitted_bid_amount >= p.min_bid

  UNION ALL

  -- Reserve-bid rows (only when include_eb is true)
  SELECT NULL::bigint AS bid_data_id, rb.product_id AS ecoid, rb.grade AS merged_grade, rb.bid AS amount
    FROM auctions.reserve_bid rb
    JOIN params p ON p.include_eb = TRUE
),
ranked AS (
  SELECT bid_data_id,
         DENSE_RANK() OVER (PARTITION BY ecoid, merged_grade ORDER BY amount DESC) AS calc_rank
    FROM candidates
)
UPDATE auctions.bid_data target
   SET round{R+1}_bid_rank         = r.calc_rank,
       display_round{R+1}_bid_rank = CASE
         WHEN r.calc_rank BETWEEN p.min_display_rank AND p.max_display_rank THEN r.calc_rank
         ELSE NULL
       END
  FROM ranked r, params p
 WHERE r.bid_data_id IS NOT NULL
   AND target.id = r.bid_data_id;
```

R+1 is interpolated in Java — two SQL constants, `RANKING_SQL_R2` /
`RANKING_SQL_R3`. DENSE_RANK is deterministic; rerun is naturally
idempotent.

### 8.2 TARGET_PRICE — GREATEST CTE

For closed round R, computes target prices for round R+1 in one bulk
UPDATE:

```sql
WITH params AS (
  SELECT a.week_id AS week_id, sa.round AS closed_round
    FROM auctions.scheduling_auctions sa
    JOIN auctions.auctions a ON a.id = sa.auction_id
   WHERE sa.id = CAST(:round_id AS bigint)
),
max_bids AS (
  SELECT bd.ecoid, bd.merged_grade, MAX(bd.submitted_bid_amount) AS max_bid
    FROM auctions.bid_data bd
    JOIN params p ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
   WHERE bd.submitted_bid_amount > 0
   GROUP BY bd.ecoid, bd.merged_grade
),
buyer_codes AS (
  SELECT bd.ecoid, bd.merged_grade,
         STRING_AGG(DISTINCT bd.code, ',' ORDER BY bd.code) AS codes
    FROM auctions.bid_data bd
    JOIN max_bids mb USING (ecoid, merged_grade)
    JOIN params   p  ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
   WHERE bd.submitted_bid_amount = mb.max_bid
   GROUP BY bd.ecoid, bd.merged_grade
),
factors AS (
  SELECT mb.ecoid, mb.merged_grade,
         tpf.factor_amount, tpf.factor_type
    FROM max_bids mb
    JOIN auctions.target_price_factors tpf
      ON mb.max_bid BETWEEN tpf.minimum_value AND tpf.maximum_value
    JOIN auctions.target_price_factor_filters tpff
      ON tpff.target_price_factor_id = tpf.id
    JOIN auctions.bid_round_selection_filters brf
      ON brf.id = tpff.bid_round_selection_filter_id
    JOIN params p ON brf.round = p.closed_round + 1
),
eb AS (
  SELECT product_id AS ecoid, grade AS merged_grade, bid AS reserve_value
    FROM auctions.reserve_bid
),
po_max AS (
  SELECT pod.product_id AS ecoid, pod.grade AS merged_grade, MAX(pod.price) AS po_price
    FROM auctions.po_detail pod
    JOIN auctions.purchase_order po ON po.id = pod.purchase_order_id
    JOIN params p
      ON p.week_id BETWEEN po.week_from_id AND po.week_to_id
   GROUP BY pod.product_id, pod.grade
),
evaluated AS (
  SELECT
    mb.ecoid, mb.merged_grade,
    mb.max_bid,
    bc.codes        AS max_buyer_codes,
    f.factor_amount,
    f.factor_type,
    eb.reserve_value,
    po.po_price,
    CASE
      WHEN f.factor_type = 'Percentage_Factor'
        THEN ROUND(mb.max_bid * f.factor_amount / 100, 2)
      WHEN f.factor_type = 'Flat_Amount'
        THEN ROUND(mb.max_bid + f.factor_amount,        2)
      ELSE mb.max_bid
    END AS max_bid_plus_factor
    FROM max_bids   mb
    LEFT JOIN buyer_codes bc USING (ecoid, merged_grade)
    LEFT JOIN factors     f  USING (ecoid, merged_grade)
    LEFT JOIN eb              USING (ecoid, merged_grade)
    LEFT JOIN po_max      po USING (ecoid, merged_grade)
)
UPDATE auctions.aggregated_inventory ai
   SET round{R}_max_bid                  = e.max_bid,
       round{R}_max_bid_buyer_code       = e.max_buyer_codes,
       round{R+1}_target_price           = GREATEST(
                                              e.max_bid_plus_factor,
                                              COALESCE(e.reserve_value, 0),
                                              COALESCE(e.po_price, 0)
                                           ),
       r{R+1}_target_price_factor        = e.factor_amount,
       r{R+1}_target_price_factor_type   = e.factor_type,
       round{R+1}_eb_for_target          = COALESCE(e.reserve_value, 0)
  FROM evaluated e, params p
 WHERE ai.week_id = p.week_id
   AND ai.ecoid2 = e.ecoid              -- aggregated_inventory's column is `ecoid2` (not `ecoid`)
   AND ai.merged_grade = e.merged_grade;
```

Note: `auctions.aggregated_inventory` uses `ecoid2 VARCHAR(100)` whereas
`auctions.bid_data` and `auctions.reserve_bid` use `ecoid` / `product_id`.
The CTE aliases everything to a uniform `ecoid` column inside the join
chain, then qualifies the final WHERE with `ai.ecoid2`. Existing
`BidDataCreationRepository` uses the same alias pattern.

R / R+1 interpolated in Java — two SQL constants:
`TARGET_PRICE_SQL_R1_TO_R2` / `TARGET_PRICE_SQL_R2_TO_R3`.

When R = 2 the SQL writes `round2_max_bid` + `round2_max_bid_buyer_code` +
`round3_target_price`. There is no `round3_max_bid` because R3 is
terminal (umbrella §5).

### 8.3 State-flip UPDATE (entry guard)

```sql
UPDATE auctions.scheduling_auctions
   SET ranking_status      = 'RUNNING',
       ranking_started_at  = NOW(),
       ranking_finished_at = NULL,
       ranking_error       = NULL
 WHERE id = :round_id
   AND ranking_status <> 'RUNNING';
```

Zero rows-affected → throw
`RecalcAlreadyRunningException` → 409 from admin
endpoint; orchestrator path swallows + logs.

The same shape applies to `target_price_*`.

---

## 9. Testing

### 9.1 Test matrix

| Layer | File | What it asserts |
|---|---|---|
| Repository (RANKING) | `BidRankingRepositoryIT` | DENSE_RANK over seeded `bid_data`; reserve-floor branch toggled by `include_reserve_floor`; rerun idempotent; ranks below `min_bid` excluded; ranks outside display window clamp display column to NULL |
| Repository (TARGET_PRICE) | `TargetPriceRecalcRepositoryIT` | Correct `round{R}_max_bid` / buyer codes / target price; `GREATEST` picks max of factor-adjusted bid vs EB vs PO; missing factor band leaves target = MaxBid; missing EB → COALESCE 0; missing PO → COALESCE 0; PO active-week filter respected |
| Service (BidRanking) | `BidRankingServiceTest` | Status flip PENDING→RUNNING→SUCCESS; on repo throw, status flip RUNNING→FAILED with truncated error; event published only on SUCCESS; admin recalculate path rejects RUNNING |
| Service (TargetPriceRecalc) | `TargetPriceRecalcServiceTest` | Same shape |
| Service (Orchestrator) | `RecalcOrchestratorTest` | RANKING failure does NOT prevent TARGET_PRICE from running; both processes' state changes survive a thrown unchecked exception in either |
| Listener | `RecalcRoundClosedListenerTest` | Round 1 + 2 trigger orchestrator; round 3 does not; orchestrator throw is logged but never propagated |
| Controller | `RecalcAdminControllerIT` | ADMIN role required (403); 200 + RecalcResponse on success; 409 RUNNING; 404 unknown id; 422 round ∉ {1,2} |
| Snowflake push | `BidRankingSnowflakePushListenerTest` + `TargetPriceSnowflakePushListenerTest` | snowflake.enabled=false short-circuits; enabled writes correct MERGE shape; failure logs to `snowflake_sync_log` |
| End-to-end | `RecalcEndToEndIT` | Seed + simulated `RoundClosedEvent` → all status columns + ranked + target-price columns + both events |

### 9.2 Fixture

`backend/src/test/resources/fixtures/auctions/recalc-seed.sql`:

- 1 auction week (existing seed)
- 1 scheduling_auctions row per round (3 rows)
- ~6 (ecoid, grade) rows in `aggregated_inventory`
- ~12 `bid_data` rows across 4 buyer codes — bids hit each branch:
  above/below `min_bid`; ranks inside/outside display window; ties
  producing comma-joined buyer codes
- 2 `reserve_bid` rows (one matches a bid_data ecoid, one stand-alone
  for the UNION ALL branch)
- 2 `purchase_order` rows × 2 `po_detail` rows each — one PO covering
  the auction week (active), one outside (inactive control)
- 3 `target_price_factors` bands (low/mid/high) with
  `bid_round_selection_filters` for both round 2 and round 3

Reused across `BidRankingRepositoryIT`, `TargetPriceRecalcRepositoryIT`,
and `RecalcEndToEndIT`.

### 9.3 Coverage target

**85%+** matching 4A (`auctions.reservebid`) and 4B
(`auctions.purchaseorder`) — see `docs/testing/coverage.md`.

### 9.4 Out of scope for tests

- Mendix parity at SQL byte level — specs cite Mendix shape; tests
  assert Postgres semantics.
- Live Snowflake JDBC path — `snowflake.enabled=false` in test profile;
  full Snowflake push exercised manually in QA per `eb.sync.writer=jdbc`
  precedent.

---

## 10. Docs updates (per CLAUDE.md mandate)

| Doc | Update |
|---|---|
| `docs/api/rest-endpoints.md` | Add the two new admin endpoints with request/response shape + role requirements |
| `docs/architecture/decisions.md` | New ADR: "Sub-project 4C — Bid ranking + target-price recalc" recording the 9 numbered decisions in §3 |
| `docs/architecture/data-model.md` | Add status-columns description to `auctions.scheduling_auctions` entry; add `include_reserve_floor` to `auctions.bid_ranking_config` |
| `docs/app-metadata/modules.md` | New "Bid Ranking + Target-Price Recalc" entry under sub-project 4 |
| `docs/business-logic/index.md` | Add a new file `docs/business-logic/bid-ranking-and-target-price.md` describing the two-process model + reserve-floor inclusion + PO active-week semantics |
| `docs/deployment/setup.md` | Add `recalc.snowflake.*` config keys |
| `docs/testing/coverage.md` | Add `auctions.recalc` entry — 85%+ target |
| `docs/tasks/auction-sub4-umbrella-design.md` | Update §7 spec/plan locations to mark 4C design + plan as drafted |

---

## 11. Risks + known gaps

| Risk | Mitigation |
|---|---|
| Mendix's `target_price_factors` band match assumes `MaximumValue` may be empty (legacy). Modern schema has `NUMERIC(14, 2) NOT NULL` on `maximum_value` — no NULL handling needed. | Confirmed in V59 schema; the CTE uses `BETWEEN` which is safe. |
| `target_price_factor_filters` has rounds 2 and 3 represented; if a band is missing for one round the LEFT JOIN gives NULL factor — CTE uses `ELSE mb.max_bid` so target falls back to the unadjusted max bid. | Tested explicitly in `TargetPriceRecalcRepositoryIT` "missing factor band" case. |
| `po_detail` price column is `NUMERIC(14, 2)` whereas `aggregated_inventory.round{N}_target_price` is `NUMERIC(14, 4)`. Coerce in the CTE — Postgres widens automatically; no explicit cast needed. | Validated by sample `EvaluatedBid` arithmetic in IT fixture. |
| Two parallel recalc tx attempts (cron tick + admin manual-fire racing) | Q3.6 — state-flip UPDATE with `WHERE *_status <> 'RUNNING'` is the single source of truth; second caller gets 0 rows-affected → 409. |
| Snowflake table column drift (`AUCTIONS.BUYER_BID` rank columns renamed) | Fixed by the writer interface; renames live in one JDBC impl class. Failure = log + `snowflake_sync_log` FAILED row. Local Postgres unaffected. |
| Status row stuck at `RUNNING` if JVM dies mid-tx | The status flip + recalc are in the same `REQUIRES_NEW` tx — a JVM crash before commit rolls back the flip too. Status stays at the prior state. Admin endpoint can reset by re-firing. |

---

## 12. Dependencies + ship order

- **4A** — shipped (V76, V77 + `auctions.reserve_bid`).
- **4B** — shipped (V80, V81 + `auctions.purchase_order` + `auctions.po_detail`).
- **4C** — this design.

Ship order within 4C:

1. Schema migration V82 (additive only — no data loss; safe to run
   ahead of code).
2. Native repositories + entity status-column extension on
   `SchedulingAuction`.
3. `RecalcStatus` enum + `BidRankingService` + `TargetPriceRecalcService`.
4. `RecalcOrchestrator` + `RecalcRoundClosedListener` (replaces stub).
5. `RecalcAdminController` + `SecurityConfig` matcher.
6. Two `*SnowflakePushListener`s + writer interfaces + logging impls.
7. Tests in the order of §9.1.
8. JDBC writer impls (gated by `recalc.snowflake.*-writer=jdbc`).
9. Docs + ADR per §10.

The JDBC writer impls (step 8) are deferrable — the rest of 4C is
production-correct with the logging writers, matching the 4A / 4B
ship pattern.
