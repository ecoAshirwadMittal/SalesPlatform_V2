# Design — Sub-project 6: R3 Init + Pre-process

**Status:** Draft (ready for review)
**Date:** 2026-05-06
**Parent gap analysis:** `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` §6 item #2
**Mendix sources:**
- `migration_context/backend/ACT_Round3_SetStarted.md`
- `migration_context/backend/services/SUB_InitializeRound3.md`
- `migration_context/backend/services/SUB_Round3_PreProcessRoundData.md`
- `migration_context/backend/ACT_GenerateRound3_BidDataObjects.md` *(intentionally NOT ported — see §3.4)*
- `migration_context/backend/services/SUB_GenerateRound3QualifiedBuyerCodes.md`
- `migration_context/backend/services/SUB_ListRoundThreeBuyersDataForQualifiedBuyers.md`
- `migration_context/backend/services/SUB_Round2_DeleteUnsubmittedBids.md`
- `migration_context/backend/ACT_Generate_RoundThreeQualifiedBuyersReport.md`
- `migration_context/backend/services/Sub_ProcessSpecialBuyers.md` *(intentionally NOT ported — see §3.4)*

**Source SQL for R3 qualification rule:** supplied by product owner — not from a Mendix microflow file. Replaces the legacy "any nonzero submitted bid in any round qualifies" rule that `SUB_GenerateRound3QualifiedBuyerCodes` step 5 implemented. See §3.5 and §7.3.

**Depends on:**
- Sub-project 4C — shipped (V82; `RecalcStatusUpdater`, `RecalcAlreadyRunningException`, `round3_target_price` + `round3_bid_rank` columns on `aggregated_inventory` + `bid_data`)
- Sub-project 5 — shipped 2026-05-06 (V83; `QualifiedBuyerCodeRepository.bulkInsertForR2` to be renamed `bulkInsertForRound`; `RecalcStatus.SKIPPED`)
- Existing R3 reports infrastructure — V62 + `Round3BuyerDataReportRepository` + `Round3ReportService` + `Round3ReportController` (built read-only; sub-project 6 supplies the writes)
- Schema V59 (`auctions.bid_round_selection_filters`)

---

## 1. Background

Two stub listeners at `service/auctions/lifecycle/stub/`:
- `R3InitStubListener` — fires on `RoundStartedEvent(round=3)`; logs "would set Upsell round started"
- `R3PreProcessStubListener` — fires on `RoundClosedEvent(round ∈ {2,3})`; logs "would prep round-3 data"

While these stubs sit there, R3 (Upsell) bidding does not function:
- No `qualified_buyer_codes` rows for the R3 SA → bidder dashboard shows no inventory
- No `round3_buyer_data_reports` rows → the already-shipped admin Round 3 Bid Report page is empty

Sub-project 6 replaces both stubs with production logic. The work splits cleanly across the two events because they fire at different points in the lifecycle and target different concerns:

1. **R3 pre-process** (`RoundClosedEvent(round=2)`): heavy data prep that must complete before R3 opens — delete unsubmitted R2 bids, compute R3 QBCs (regular + STB), populate the round-3 reports table.
2. **R3 init** (`RoundStartedEvent(round=3)`): light status-flip work at the round boundary — flip `Round3InitStatus` to `Complete`. Notifications and dedicated Snowflake push are deferred to gap-analysis items #4 and #9.

What 4C **already covers** on R2 close (and so sub-project 6 does NOT re-run):
- `round3_target_price` (`TargetPriceRecalcRepository.java:198`)
- `round3_bid_rank` (`BidRankingRepository.java:120`)

What sub-project 6 **does not need to handle** (per product owner direction during design):
- R3 BidData carryover (`ACT_GenerateRound3_BidDataObjects`) — R3 BidData flows through the existing R1/R2 submission path via `BidDataCreationService`
- STB BidData seeding (`Sub_ProcessSpecialBuyers`) — `BidDataCreationService` handles STB visibility from the `qualified_buyer_codes.is_special_treatment` flag

This design adopts the sub-project 5 / 4C two-process scaffolding for both new services:
- Status flag pattern on `auctions.scheduling_auctions` (`r3_preprocess_*` and `r3_init_*` columns)
- `REQUIRES_NEW` per-process tx
- `RecalcStatusUpdater` reused (extended with two new column-prefixes)
- Status sub-tx pattern (FAILED row survives parent rollback)
- Admin recovery REST endpoints (one per service), gated by `RecalcAlreadyRunningException` → 409

---

## 2. Scope

### In scope

- One Flyway migration (V84) adding three R3-qualification filter columns to `auctions.bid_round_selection_filters` and eight R3-lifecycle status columns to `auctions.scheduling_auctions`
- Whole-percent **convention alignment** for R2's `target_percent` (was treated as decimal in sub-project 5; aligned with R3's whole-percent semantics) — small CTE-formula edit + fixture update + comment refresh
- New `R3PreProcessService` (REQUIRES_NEW) implementing the five-phase pre-process flow (delete unsubmitted R2 bids → regular qualification CTE → STB CTE → three-set QBC bulk INSERT → round-3 reports population)
- New `R3InitService` (REQUIRES_NEW) implementing the status-flip + predecessor-guard flow
- New `R3PreProcessListener` replacing `R3PreProcessStubListener`
- New `R3InitListener` replacing `R3InitStubListener`
- New `R3BuyerQualificationRepository` — single Postgres CTE porting the product-owner-supplied R3 qualification rule (latest-bid + three filter knobs + rank ceiling)
- New `R3SpecialBuyerRepository` — single Postgres CTE; parallel to sub-project 5's `R2SpecialBuyerRepository` with `prior_round ∈ {1, 2}`
- New `R3PreProcessSupportRepository` — bulk DELETE for unsubmitted R2 bids
- Extension to `Round3BuyerDataReportRepository` — `deleteBySchedulingAuctionId` + `bulkInsertForSchedulingAuction`
- Rename `QualifiedBuyerCodeRepository.bulkInsertForR2` → `bulkInsertForRound` (signature unchanged; one call site updated in sub-project 5's `R2BuyerAssignmentService`)
- Extend `RecalcStatusUpdater.columnPrefix` to recognise `R3_PREPROCESS` and `R3_INIT`
- Extend `RecalcAlreadyRunningException.Process` enum with `R3_PREPROCESS` and `R3_INIT`
- New `R3PreProcessCompletedEvent` and `R3InitCompletedEvent` records (no listeners bound today)
- New `R3LifecycleAdminController` exposing two `POST` endpoints
- Tests at all four layers per §9 + 85%+ coverage target
- ADR + docs updates per CLAUDE.md mandate

### Out of scope (separate sub-projects)

- **R3 start-notification email** (gap-analysis #4) — TODO hook in `R3InitService`
- **Dedicated R3 Snowflake push** (`SUB_Round3SendAuctionToSnowflake`, gap-analysis #9) — TODO hook in `R3InitService`. The existing `AuctionStatusSnowflakePushListener` already covers generic status-change pushes
- **R3 BidData carryover** (`ACT_GenerateRound3_BidDataObjects`) — flows through `BidDataCreationService` via the bidder dashboard
- **STB BidData seeding** (`Sub_ProcessSpecialBuyers`) — `BidDataCreationService` reads `qualified_buyer_codes.is_special_treatment` for STB visibility
- **Frontend `/round-filters/[round]` R3 page** — slot exists; populating it is a frontend follow-on
- **`bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository`** (gap-analysis #3) — sub-project 5b
- **`SUB_HandleSpecialTreatmentBuyerOnRoundStart`** (gap-analysis #8) — sub-project 5c

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 3.1 | **Two sibling services, not an orchestrator.** `R3PreProcessService` (RoundClosedEvent r=2) and `R3InitService` (RoundStartedEvent r=3) are independent — separate listeners, separate status columns, separate admin endpoints. | They fire on different events at different times, do non-overlapping work, and have independent recovery semantics. 4C's orchestrator pattern was for two processes sharing the same `RoundClosedEvent` — wrong shape here. |
| 3.2 | **Predecessor guard in code, not constraint.** `R3InitService.run` rejects with `IllegalStateException` (→ 422) unless `r3_preprocess_status = SUCCESS` on the same R3 SA row. Cron-path listener catches and logs warn; admin path returns 422. | If pre-process didn't succeed, R3 has no QBCs and bidders see empty inventory. Hard-rejecting at init time prevents masking the real failure. CHECK constraint can't express "this column requires that column to be SUCCESS". |
| 3.3 | **`has_round = false` on R3 SA → SKIPPED.** Pre-process gates on the R3 SA's `has_round` flag (mirroring Mendix `SUB_Round3_PreProcessRoundData` decision). FALSE → status flips PENDING → SKIPPED, no QBCs/reports written. | Mendix branches early in this case. SKIPPED disambiguates "config gate disabled" from "ran and produced empty result". Reuses the SKIPPED enum value added in sub-project 5. |
| 3.4 | **No R3 SA at all → silent skip, no row write.** Listener resolves R3 SA via `findByAuctionIdAndRound(auctionId, 3)`; empty result → log + return. No status row exists to write SKIPPED on. | Auction created with R1 + R2 only is a legitimate state. Silent log keeps audit trail; no writes prevents NULL-row pollution. |
| 3.5 | **No BidData generation in pre-process.** Sub-project 6 writes only QBCs + reports. R3 BidData is created on-demand via `BidDataCreationService` from the bidder dashboard — same path as R1 and R2. | Per product-owner direction during design. Mendix `ACT_GenerateRound3_BidDataObjects` and `Sub_ProcessSpecialBuyers` are NOT ported. The `qualified_buyer_codes.is_special_treatment` flag remains the source of truth `BidDataCreationService` consults for STB visibility. |
| 3.6 | **R3 qualification rule replaces Mendix's "any nonzero bid" filter.** New rule: per (ecoid, grade, buyer_code) take latest bid across rounds 1+2, then evaluate against three filter knobs (`bid_percentage_variation`, `bid_amount_variation`, `rank_qualification_limit`). All three NULL → fall-through qualify. ANY branch matches → qualify. | Product-owner-supplied SQL ports verbatim into the new CTE in §7.3. Replaces `SUB_GenerateRound3QualifiedBuyerCodes` step 5. |
| 3.7 | **Three new BRSF columns are nullable.** `bid_percentage_variation NUMERIC(10,4)`, `bid_amount_variation NUMERIC(14,2)`, `rank_qualification_limit INTEGER`. NULL on all three → all-buyers fall-through. | Each branch is gated on `IS NOT NULL`. The all-NULL fall-through is the Mendix-specified default behavior when criteria aren't configured. |
| 3.8 | **Whole-percent convention everywhere.** R3's `bid_percentage_variation` stores 5 for 5%; formula divides by 100. R2's `target_percent` was decimal (0.05) in sub-project 5; **sub-project 6 normalises R2 to whole-percent** by updating the R2 CTE formula and fixture values. | Mendix native convention is whole-percent. Extractor parity preserved. Single internal convention prevents future drift. No production data migration — only test fixtures touched (no real BRSF rows have been loaded yet). |
| 3.9 | **STB CTE retained for R3 QBC writes.** `is_special_treatment = TRUE` rows still get written to `qualified_buyer_codes` for the R3 SA, with `prior_round ∈ {1, 2}` semantics. | Per product-owner direction: `BidDataCreationService` reads this flag to grant STBs all-AE visibility. Drop the flag → STBs become invisible at R3. |
| 3.10 | **Pre-process is one REQUIRES_NEW tx.** All five phases (delete unsubmitted + regular qualification CTE + STB CTE + three-set QBC INSERT + reports) commit atomically. | Phases share row-level dependencies — partial commits would leave the SA with QBCs but no reports (or vice versa). One tx + admin re-fire is simpler than orchestrating partial recovery. |
| 3.11 | **Rerun is idempotent via DELETE-then-INSERT.** Pre-process deletes existing QBCs and reports for the R3 SA, then bulk-inserts. Init only flips an enum and (idempotent) status columns. | Both services can be rerun safely with no manual cleanup. Admin recovery is a simple `POST` per service. |
| 3.12 | **`ACT_ChangeSavedBidsToPreviouslySubmitted` is NOT ported.** Our schema tracks submission at `bid_rounds.submitted` boolean, not at per-row `bid_state`. After R2 close, all `submitted=TRUE` bid_data rows on R2 SA are implicitly "previously submitted" — no flag flip needed. | Mendix's per-row state machine doesn't apply to our normalised schema. Sub-project 4C's bid-rank pipeline already treats `submitted=TRUE` rows as authoritative R1/R2 history. |
| 3.13 | **Listener is `@Async("snowflakeExecutor")`.** Same pattern as `R1InitListener`, `R2BuyerAssignmentListener`, and the 4C recalc listener. | Offloads from cron-tick post-commit thread. The pre-process bulk CTE is bounded (≈600 buyer codes × ≈30k AEs joined); init is microseconds. |
| 3.14 | **Admin endpoints take R3 SA id directly.** Pre-process resolves R2 SA via `findByAuctionIdAndRound(auctionId, 2)`. Path id always matches the row whose status is being written. | Consistent path-to-row mapping. Listener does the R2→R3 lookup; admin path skips that hop because the R3 SA id is already known. |

---

## 4. Architecture

```
┌────────────────────────────────────────┐         ┌────────────────────────────────────────┐
│ RoundClosedEvent(round=2)              │         │ RoundStartedEvent(round=3)             │
│ AFTER_COMMIT, async on                 │         │ AFTER_COMMIT, async on                 │
│ snowflakeExecutor                       │         │ snowflakeExecutor                       │
└────────────────────────────────────────┘         └────────────────────────────────────────┘
                  │                                                    │
                  ▼                                                    ▼
  ┌──────────────────────────────┐                  ┌──────────────────────────────┐
  │ R3PreProcessListener         │                  │ R3InitListener               │
  │  - lookup R3 SA via auction  │                  │  - delegate to               │
  │  - silent skip if not found  │                  │    R3InitService             │
  │  - delegate to               │                  └──────────────────────────────┘
  │    R3PreProcessService       │                                    │
  └──────────────────────────────┘                                    ▼
                  │                                  ┌──────────────────────────────┐
                  ▼                                  │ R3InitService                │
  ┌──────────────────────────────┐                  │  REQUIRES_NEW                │
  │ R3PreProcessService          │                  │  - validate round = 3        │
  │  REQUIRES_NEW                │                  │  - predecessor guard         │
  │  - validate r2/r3 siblings   │                  │    (r3_preprocess_status =   │
  │  - has_round = false?        │                  │     SUCCESS)                 │
  │      → SKIPPED               │                  │  - status flip → RUNNING     │
  │  - status flip → RUNNING     │                  │  - flip round3InitStatus     │
  │  Phase 1: DELETE unsubmitted │                  │    → Complete                │
  │  Phase 2: qualification CTE  │                  │  Phase TODO: notifications   │
  │  Phase 3: STB CTE            │                  │  Phase TODO: dedicated       │
  │  Phase 4: DELETE + INSERT QBC│                  │    Snowflake push            │
  │  Phase 5: DELETE + INSERT    │                  │  - status → SUCCESS          │
  │           round3 reports     │                  │  - status → FAILED (catch)   │
  │  - status → SUCCESS          │                  │  publishEvent ↦              │
  │  - status → FAILED (catch)   │                  │    R3InitCompletedEvent      │
  │  publishEvent ↦              │                  └──────────────────────────────┘
  │    R3PreProcessCompletedEvent│
  └──────────────────────────────┘
```

**Admin recovery surface** mirrors the cron path:

```
POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/preprocess-r3
  → R3PreProcessService.recalculate(r3SaId)
    → resolves r2SaId via findByAuctionIdAndRound(auctionId, 2)
    → run(r2SaId, r3SaId)

POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/reinit-r3
  → R3InitService.recalculate(r3SaId) → run(r3SaId)
```

### Java package layout

```
service/auctions/r3init/
  R3PreProcessService.java
  R3InitService.java
  R3PreProcessListener.java         // replaces R3PreProcessStubListener
  R3InitListener.java               // replaces R3InitStubListener
  R3PreProcessResult.java           // record(qualifiedCount, specialTreatmentCount,
                                    //         notQualifiedCount, reportRowCount,
                                    //         deletedBidsCount, durationMs, skipped)
  R3InitResult.java                 // record(durationMs)

repository/auctions/
  R3BuyerQualificationRepository.java   // §7.3 regular CTE
  R3SpecialBuyerRepository.java         // §7.4 STB CTE (prior_round ∈ {1,2})
  R3PreProcessSupportRepository.java    // §7.2 bulk DELETE for unsubmitted R2 bids

(extended in this PR)
  Round3BuyerDataReportRepository.java  // add deleteBySchedulingAuctionId
                                        //   + bulkInsertForSchedulingAuction (§7.6)
  QualifiedBuyerCodeRepository.java     // rename bulkInsertForR2 → bulkInsertForRound

event/
  R3PreProcessCompletedEvent.java       // record(r3SaId, auctionId, qualifiedCount,
                                        //         specialTreatmentCount, reportRowCount)
  R3InitCompletedEvent.java             // record(r3SaId, auctionId)

controller/admin/
  R3LifecycleAdminController.java       // both /preprocess-r3 and /reinit-r3

dto/admin/
  R3PreProcessResponse.java
  R3InitResponse.java
```

`R3InitStubListener.java` and `R3PreProcessStubListener.java` are **deleted** in the same PR.

`RecalcStatusUpdater.columnPrefix` extends to:

```java
case "R3_PREPROCESS" -> "r3_preprocess";
case "R3_INIT"       -> "r3_init";
```

`RecalcAlreadyRunningException.Process` extends to:

```java
public enum Process { RANKING, TARGET_PRICE, R2_INIT, R3_PREPROCESS, R3_INIT }
```

---

## 5. Schema (V84)

Single Flyway migration: `V84__auctions_r3_lifecycle_status.sql`. Additive only.

```sql
-- V84: Sub-project 6 — R3 init + pre-process
-- Additive only. R2 column semantics-update is comment-only (no data change).

-- ─── R3 selection-criteria knobs on bid_round_selection_filters ──────────
ALTER TABLE auctions.bid_round_selection_filters
    ADD COLUMN bid_percentage_variation  NUMERIC(10, 4),
    ADD COLUMN bid_amount_variation      NUMERIC(14, 2),
    ADD COLUMN rank_qualification_limit  INTEGER;

COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_percentage_variation IS
    '6: R3 qualification — whole-percent threshold (5 = 5%). Branch active when NOT NULL: latest_bid >= round3_target_price - (round3_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_amount_variation IS
    '6: R3 qualification — flat amount. Branch active when NOT NULL: latest_bid >= round3_target_price - amount.';
COMMENT ON COLUMN auctions.bid_round_selection_filters.rank_qualification_limit IS
    '6: R3 qualification — rank ceiling. Branch active when NOT NULL: round3_bid_rank <= limit. All three branches NULL → qualify everyone.';

-- ─── R2 convention re-alignment to whole-percent (comment only) ──────────
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_percent IS
    '6: R2 qualification — whole-percent threshold (5 = 5%). Was treated as decimal in V59/sub-project 5; sub-project 6 normalises to whole-percent across both rounds. Formula: bid >= avg_target_price - (avg_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_value IS
    'R2 qualification — flat amount threshold (mirrors Mendix bidamountvariation semantics for R3).';

-- ─── R3 lifecycle status columns on scheduling_auctions ──────────────────
-- Both column groups live on the R3 SA row (the entity being initialized).
-- Pre-process listener takes R2 SA id from RoundClosedEvent and resolves
-- R3 SA via findByAuctionIdAndRound; admin endpoints take R3 SA id directly.
ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r3_preprocess_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_preprocess_error         TEXT,
    ADD COLUMN r3_preprocess_started_at    TIMESTAMPTZ,
    ADD COLUMN r3_preprocess_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_preprocess_status
        CHECK (r3_preprocess_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED')),

    ADD COLUMN r3_init_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_init_error               TEXT,
    ADD COLUMN r3_init_started_at          TIMESTAMPTZ,
    ADD COLUMN r3_init_finished_at         TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_init_status
        CHECK (r3_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED | SKIPPED (R3 SA exists with has_round=false). Lives on the R3 SA row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED. Init refuses to flip to SUCCESS unless r3_preprocess_status = SUCCESS on the same row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED, including the "predecessor not SUCCESS" guard message.';
```

`RecalcStatus` enum already includes SKIPPED from sub-project 5. R3_INIT will only ever write the four non-SKIPPED states; the V84 CHECK constraint on `r3_init_status` blocks SKIPPED at the DB layer if it ever sneaks through.

No new indexes — V59 already covers the listener-side joins on `bid_rounds(scheduling_auction_id)`, `(submitted)`, and `(scheduling_auction_id, buyer_code_id)`. Re-evaluate if perf issues surface.

No data migration on production rows — `target_percent` semantics change is comment-only because no production BRSF rows exist yet (only test fixtures). If extractor ever loads R2 rows from Mendix, those values are already whole-percent at source.

---

## 6. API surface

### 6.1 Endpoints

| Method | Path | Auth | Body | 200 | 4xx |
|---|---|---|---|---|---|
| POST | `/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3` | `ROLE_ADMIN` | none | `R3PreProcessResponse` | 404 unknown id; 409 status=RUNNING; 422 round ≠ 3 or no R2 sibling |
| POST | `/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3` | `ROLE_ADMIN` | none | `R3InitResponse` | 404 unknown id; 409 status=RUNNING; 422 round ≠ 3 or pre-process not SUCCESS |

Both keyed on R3 SA id.

### 6.2 Response DTOs

```java
public record R3PreProcessResponse(
    long schedulingAuctionId,         // R3 SA id
    String status,                    // "SUCCESS" | "FAILED" | "SKIPPED"
    String error,                     // nullable
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int qualifiedCount,               // QBCs written with qualification_type=Qualified, is_special_treatment=false
    int specialTreatmentCount,        // QBCs written with is_special_treatment=true
    int notQualifiedCount,            // QBCs written with qualification_type=Not_Qualified
    int reportRowCount,               // round3_buyer_data_reports rows created
    int deletedBidsCount,             // R2 bid_data rows deleted (bid_amount=0 OR NULL)
    long durationMs
) {}

public record R3InitResponse(
    long schedulingAuctionId,
    String status,                    // "SUCCESS" | "FAILED"
    String error,                     // nullable
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    long durationMs
) {}
```

### 6.3 Error mapping

| Cause | Exception | HTTP | Source |
|---|---|---|---|
| Unknown SA id | `EntityNotFoundException` | 404 | existing handler |
| Round ≠ 3 (path id), or ≠ 2 (resolved sibling) | `IllegalArgumentException` | 422 | existing handler |
| Sibling R2/R3 mismatch (different `auction_id`) | `IllegalArgumentException` | 422 | existing handler |
| No R2 sibling for R3 SA's auction | `IllegalStateException` | 422 | existing handler |
| Init's predecessor guard (`r3_preprocess_status` ≠ SUCCESS) | `IllegalStateException` | 422 | existing handler |
| Status RUNNING | `RecalcAlreadyRunningException` | 409 | existing handler (extended enum) |

### 6.4 SecurityConfig delta

```java
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/preprocess-r3").hasRole("ADMIN")
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reinit-r3").hasRole("ADMIN")
```

---

## 7. Data flow — SQL contracts

The pre-process flow has four logically separate SQL passes; init has one entity-level UPDATE.

### 7.1 Pre-process Phase 1 — delete unsubmitted R2 bids

Mendix `SUB_Round2_DeleteUnsubmittedBids`. One DELETE:

```sql
DELETE FROM auctions.bid_data bd
 USING auctions.bid_rounds br
 WHERE bd.bid_round_id = br.id
   AND br.scheduling_auction_id = CAST(:r2_sa_id AS bigint)
   AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL);
```

Returns affected count for the result record. Mendix's `ACT_ChangeSavedBidsToPreviouslySubmitted` is **not** ported (decision 3.12) — our schema's `bid_rounds.submitted` flag plus closed-round semantics handles "previously submitted" implicitly.

### 7.2 Pre-process Phase 2 — R3 regular qualification CTE

For the R3 SA, returns the set of buyer-code ids that qualify via the regular path (i.e., excluding STB qualification). Ported from product-owner-supplied SQL with V72-flattened junctions and our schema's column names:

```sql
WITH params AS (
  SELECT sa.id          AS r3_sa_id,
         sa.auction_id  AS auction_id,
         brsf.bid_percentage_variation  AS pct_var,
         brsf.bid_amount_variation      AS amt_var,
         brsf.rank_qualification_limit  AS rank_lim
    FROM auctions.scheduling_auctions sa
    JOIN auctions.bid_round_selection_filters brsf
      ON brsf.round = 3
   WHERE sa.id = CAST(:r3_sa_id AS bigint)
),
latest_bid AS (
  SELECT bd.ecoid,
         bd.merged_grade,
         bd.submitted_bid_amount,
         bd.round3_bid_rank,
         br.buyer_code_id,
         ai.round3_target_price,
         ROW_NUMBER() OVER (
           PARTITION BY bd.ecoid, bd.merged_grade, br.buyer_code_id
           ORDER BY bd.submitted_datetime DESC
         ) AS rn
    FROM auctions.bid_data bd
    JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id
    JOIN auctions.scheduling_auctions sa
      ON sa.id = br.scheduling_auction_id
    JOIN buyer_mgmt.buyer_codes bc   ON bc.id = br.buyer_code_id
    JOIN auctions.aggregated_inventory ai
      ON ai.id = bd.aggregated_inventory_id, params p
   WHERE sa.auction_id = p.auction_id
     AND sa.round IN (1, 2)
     AND br.submitted = TRUE
     AND bd.submitted_bid_amount > 0
     AND bc.buyer_code_type IN ('Data_Wipe','Wholesale')
),
filtered_latest AS (
  SELECT * FROM latest_bid WHERE rn = 1
)
SELECT DISTINCT fl.buyer_code_id
  FROM filtered_latest fl, params p
 WHERE
   -- All three branches NULL → fall-through, qualify everyone
   (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
   -- Percentage branch
   OR (p.pct_var IS NOT NULL
       AND fl.submitted_bid_amount
           >= fl.round3_target_price - (fl.round3_target_price * p.pct_var / 100))
   -- Flat-amount branch
   OR (p.amt_var IS NOT NULL
       AND fl.submitted_bid_amount >= fl.round3_target_price - p.amt_var)
   -- Rank branch
   OR (p.rank_lim IS NOT NULL
       AND fl.round3_bid_rank IS NOT NULL
       AND fl.round3_bid_rank <= p.rank_lim);
```

**Translation notes vs Mendix source SQL:**

- `auctionui$schedulingauction.auction_week_year` filter → `sa.auction_id = p.auction_id`. FK-clean; same selectivity.
- WHERE `bd.bidamount > 0` → `bd.submitted_bid_amount > 0` (matches sub-project 5 R2 CTE convention; submitted value is frozen at submit time).
- Mendix CASE/WHEN-cascade returning 1 on first match → flat OR cascade. Functionally identical (qualifies if **any** branch matches) and short-circuit-equivalent.
- `round3_bid_rank IS NOT NULL` guard added: 4C may not have written rank for every (buyer, AE) pair. Without the null-guard, the rank branch would silently exclude valid candidates that pass via the other branches.

Result feeds Phase 4.

### 7.3 Pre-process Phase 3 — R3 STB CTE

Parallel shape to sub-project 5's `R2SpecialBuyerRepository`, with `prior_round ∈ {1, 2}`:

```sql
WITH params AS (
  SELECT sa.id          AS r3_sa_id,
         sa.auction_id  AS auction_id,
         sa.round       AS round,                 -- expected = 3
         brsf.stb_allow_all_buyers_override
    FROM auctions.scheduling_auctions sa
    JOIN auctions.bid_round_selection_filters brsf
      ON brsf.round = sa.round
   WHERE sa.id = CAST(:r3_sa_id AS bigint)
),
special_buyers AS (
  SELECT b.id AS buyer_id
    FROM buyer_mgmt.buyers b
   WHERE b.is_special_buyer = TRUE
),
special_dwwh_codes AS (
  SELECT bcb.buyer_id, bc.id AS buyer_code_id
    FROM buyer_mgmt.buyer_code_buyers bcb
    JOIN special_buyers sb ON sb.buyer_id = bcb.buyer_id
    JOIN buyer_mgmt.buyer_codes bc
      ON bc.id = bcb.buyer_code_id
     AND bc.buyer_code_type IN ('Wholesale','Data_Wipe')
),
prior_round_bids AS (
  SELECT bd.buyer_code_id, bcb2.buyer_id, COUNT(*) AS bid_count
    FROM auctions.bid_data bd
    JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id AND br.submitted = TRUE
    JOIN auctions.scheduling_auctions sa
      ON sa.id = br.scheduling_auction_id
    JOIN params p
      ON p.auction_id = sa.auction_id
     AND sa.round < p.round                       -- prior_round ∈ {1, 2} when p.round = 3
    JOIN buyer_mgmt.buyer_code_buyers bcb2
      ON bcb2.buyer_code_id = bd.buyer_code_id
   GROUP BY bd.buyer_code_id, bcb2.buyer_id
),
code_is_stb AS (
  SELECT s.buyer_id, s.buyer_code_id,
         CASE
           WHEN p.stb_allow_all_buyers_override = TRUE THEN TRUE
           WHEN COALESCE(prb.bid_count, 0) = 0          THEN TRUE
           ELSE FALSE
         END AS is_stb
    FROM special_dwwh_codes s
    LEFT JOIN prior_round_bids prb
      ON prb.buyer_id = s.buyer_id
     AND prb.buyer_code_id = s.buyer_code_id
    CROSS JOIN params p
),
buyers_all_codes_stb AS (
  SELECT buyer_id
    FROM code_is_stb
   GROUP BY buyer_id
  HAVING bool_and(is_stb) = TRUE
)
SELECT cs.buyer_code_id
  FROM code_is_stb cs
  JOIN buyers_all_codes_stb bs ON bs.buyer_id = cs.buyer_id;
```

Result is the set of `buyer_code_id` values to insert as `is_special_treatment = TRUE` in Phase 4.

### 7.4 Pre-process Phase 4 — three-set QBC bulk INSERT (V72-flattened)

Identical idiom to sub-project 5 §7.3, just keyed on R3 SA id and the renamed `bulkInsertForRound`:

```java
qbcRepo.deleteBySchedulingAuctionId(r3SaId);
int totalRows = qbcRepo.bulkInsertForRound(
    r3SaId,
    qualified.toArray(new Long[0]),    // regular qualified set from §7.2
    special.toArray(new Long[0])        // STB set from §7.3
);
```

The bulk INSERT (verbatim from sub-project 5 §7.3 — single SQL, V72-flattened columns):

```sql
INSERT INTO buyer_mgmt.qualified_buyer_codes (
    scheduling_auction_id, buyer_code_id, qualification_type,
    included, is_special_treatment, created_date, changed_date
)
SELECT :sa_id,
       bc.id,
       CASE
         WHEN bc.id = ANY(CAST(:special_ids   AS bigint[])) THEN 'Qualified'
         WHEN bc.id = ANY(CAST(:qualified_ids AS bigint[])) THEN 'Qualified'
         ELSE 'Not_Qualified'
       END,
       CASE
         WHEN bc.id = ANY(CAST(:special_ids   AS bigint[])) THEN TRUE
         WHEN bc.id = ANY(CAST(:qualified_ids AS bigint[])) THEN TRUE
         ELSE FALSE
       END,
       (bc.id = ANY(CAST(:special_ids AS bigint[]))),
       NOW(),
       NOW()
  FROM buyer_mgmt.buyer_codes bc
  JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
  JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
 WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
   AND b.status = 'Active'
 GROUP BY bc.id;
```

### 7.5 Pre-process Phase 5 — Round-3 buyer-data reports population

Mendix `ACT_Generate_RoundThreeQualifiedBuyersReport` + `SUB_ListRoundThreeBuyersDataForQualifiedBuyers`:

```sql
DELETE FROM auctions.round3_buyer_data_reports
 WHERE scheduling_auction_id = CAST(:r3_sa_id AS bigint);

INSERT INTO auctions.round3_buyer_data_reports (
    scheduling_auction_id, company_name, buyer_codes,
    created_date, changed_date
)
SELECT :r3_sa_id,
       b.company_name,
       string_agg(bc.code, ',' ORDER BY bc.code)  AS buyer_codes,
       NOW(), NOW()
  FROM buyer_mgmt.qualified_buyer_codes qbc
  JOIN buyer_mgmt.buyer_codes bc        ON bc.id = qbc.buyer_code_id
  JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
  JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
 WHERE qbc.scheduling_auction_id = CAST(:r3_sa_id AS bigint)
   AND qbc.qualification_type    = 'Qualified'
   AND qbc.included              = TRUE
 GROUP BY b.company_name;
```

Verify column names against `Round3BuyerDataReport` entity in plan phase. Idempotent rerun via DELETE-then-INSERT. Runs inside the same REQUIRES_NEW tx as Phase 4.

### 7.6 State-flip UPDATE (entry guard, both services)

```sql
UPDATE auctions.scheduling_auctions
   SET <prefix>_status      = 'RUNNING',
       <prefix>_started_at  = NOW(),
       <prefix>_finished_at = NULL,
       <prefix>_error       = NULL
 WHERE id = :sa_id
   AND <prefix>_status <> 'RUNNING';
```

`<prefix>` is `r3_preprocess` or `r3_init`. Zero rows-affected → throw `RecalcAlreadyRunningException(R3_PREPROCESS|R3_INIT)` → 409 from admin endpoint; cron path swallows + logs.

### 7.7 Init Phase — entity status flip

```java
sa.setRound3InitStatus(ScheduleAuctionInitStatus.Complete);
saRepo.save(sa);
```

`SchedulingAuction.round3InitStatus` is the existing legacy enum field (already mapped). Notifications and dedicated R3 Snowflake push are TODO hooks — see §2 out-of-scope.

---

## 8. Listener wiring

```java
@Component
@ConditionalOnProperty(name = "auctions.r3-preprocess.enabled", havingValue = "true", matchIfMissing = true)
public class R3PreProcessListener {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessListener.class);

    private final R3PreProcessService service;
    private final SchedulingAuctionRepository saRepo;

    public R3PreProcessListener(R3PreProcessService service, SchedulingAuctionRepository saRepo) {
        this.service = service;
        this.saRepo  = saRepo;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 2) return;            // R3 pre-process is R2-close only
        long r2SaId = event.roundId();
        Optional<Long> r3SaId = saRepo.findByAuctionIdAndRound(event.auctionId(), 3)
            .map(SchedulingAuction::getId);
        if (r3SaId.isEmpty()) {
            log.info("R3_PREPROCESS not applicable — auctionId={} has no R3 SA", event.auctionId());
            return;
        }
        try {
            service.run(r2SaId, r3SaId.get());
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_PREPROCESS skipped — already running r3SaId={}", r3SaId.get());
        } catch (RuntimeException ex) {
            log.error("R3_PREPROCESS failed r3SaId={} error={}", r3SaId.get(), ex.toString(), ex);
        }
    }
}

@Component
@ConditionalOnProperty(name = "auctions.r3-init.enabled", havingValue = "true", matchIfMissing = true)
public class R3InitListener {

    private static final Logger log = LoggerFactory.getLogger(R3InitListener.class);

    private final R3InitService service;

    public R3InitListener(R3InitService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 3) return;
        long r3SaId = event.roundId();
        try {
            service.run(r3SaId);
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_INIT skipped — already running r3SaId={}", r3SaId);
        } catch (IllegalStateException ex) {
            // Predecessor guard fired — pre-process didn't succeed. Log loud but
            // don't propagate; admin must run /preprocess-r3 first.
            log.error("R3_INIT refused (predecessor guard) r3SaId={} — {}", r3SaId, ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("R3_INIT failed r3SaId={} error={}", r3SaId, ex.toString(), ex);
        }
    }
}
```

The current `R3PreProcessStubListener` gates on `round ∈ {2, 3}`. New listener drops the round=3 branch — that branch was dead code per Mendix semantics (R3 pre-process is a R2-close trigger only).

---

## 9. Testing

### 9.1 Test matrix

| Layer | File | What it asserts |
|---|---|---|
| Repository (R3 regular CTE) | `R3BuyerQualificationRepositoryIT` | All 4 filter modes — all-NULL fall-through; `bid_percentage_variation` branch; `bid_amount_variation` branch; `rank_qualification_limit` branch; ANY-branch-matches-→-qualifies; `ROW_NUMBER()` keeps R2 over R1 when both present; R1 fallback when no R2 row; `submitted=FALSE` excluded; `bid_amount=0` excluded; non-DW/WH buyer types excluded; `round3_bid_rank IS NULL` doesn't crash the rank branch; rerun idempotent |
| Repository (R3 STB CTE) | `R3SpecialBuyerRepositoryIT` | `stb_allow_all_buyers_override=TRUE` short-circuits; zero prior-round bids (R1 + R2) → STB; any R1 OR R2 prior bid disqualifies the buyer's whole DW/WH set; non-special buyers excluded |
| Repository (cleanup) | `R3PreProcessSupportRepositoryIT` | `deleteUnsubmittedBids` deletes only `bid_amount=0 OR NULL` rows on the R2 SA, returns affected count |
| Repository (R3 reports) | `Round3BuyerDataReportRepositoryR3IT` | `bulkInsertForSchedulingAuction` writes one row per company with `string_agg` codes alphabetically ordered; only `qualification_type=Qualified AND included=TRUE` QBCs feed in; rerun via DELETE-then-INSERT idempotent |
| Repository (QBC bulk INSERT) | extend `QualifiedBuyerCodeRepositoryR2IT` | One R3 case verifying `bulkInsertForRound` works for `round=3` SA |
| Service | `R3PreProcessServiceTest` | All 4 phases run in order on happy path; status flip PENDING→RUNNING→SUCCESS; `has_round=false` → SKIPPED no QBC writes; round mismatch (r2 sa or r3 sa) → IllegalArgumentException pre-flip; sibling mismatch → IllegalArgumentException pre-flip; repo throw → status flip RUNNING→FAILED with truncated error; event published only on SUCCESS; `recalculate` resolves R2 from R3's auction |
| Service | `R3InitServiceTest` | Status flip PENDING→RUNNING→SUCCESS; predecessor guard fires when `r3_preprocess_status` ∈ {PENDING, RUNNING, FAILED, SKIPPED} — IllegalStateException pre-flip; entity `round3InitStatus` flipped to `Complete` only on SUCCESS path; round != 3 → IllegalArgumentException pre-flip |
| Listener | `R3PreProcessListenerTest` | Round 2 close triggers service after R3 SA lookup; round 1 + round 3 close ignored; missing R3 SA → silent log + return (no service call); service throw is logged not propagated; `auctions.r3-preprocess.enabled=false` short-circuits |
| Listener | `R3InitListenerTest` | Round 3 start triggers service; rounds 1, 2 ignored; predecessor-guard `IllegalStateException` logged but never propagated; `auctions.r3-init.enabled=false` short-circuits |
| Controller | `R3LifecycleAdminControllerIT` | Admin can call both endpoints (200); non-admin → 403; preprocess endpoint: 409 RUNNING / 404 unknown id / 422 round≠3 / 422 no R2 sibling; reinit endpoint: 409 RUNNING / 404 unknown id / 422 round≠3 / 422 predecessor not SUCCESS |
| End-to-end | `R3LifecycleEndToEndIT` | Seed + simulated `RoundClosedEvent(round=2)` → R3 SA's pre-process columns + QBCs + reports populated; subsequent `RoundStartedEvent(round=3)` → R3 SA's init columns + `round3InitStatus=Complete`; both completion events fire |

### 9.2 Test fixture

`backend/src/test/resources/fixtures/auctions/r3-lifecycle-seed.sql`:

- 2 auctions — one with R1+R2+R3 SAs (full path), one with only R1+R2 (listener silent-skip)
- 1 R3 SA with `has_round=false` (SKIPPED path)
- BRSF rows for round=2 (re-using sub-project 5's fixture shape with `target_percent=5` per the R2 unit-alignment task) and round=3 with each filter knob parameterised across test variants
- ~6 (ecoid, grade) AE rows with `round3_target_price` + `round3_bid_rank` populated (mix of DW + WH; some with NULL rank)
- ~5 buyers (2 special — one STB-eligible by zero-bids, one disqualified by R1 prior bid)
- ~10 buyer codes
- R2 + R1 BidData spanning every R3-qualification branch:
  - latest R2 bid above target by 3% → qualifies via `bid_percentage_variation=5`
  - latest R2 bid above target by `(target − 1)` → qualifies via `bid_amount_variation=1`
  - rank=2 → qualifies via `rank_qualification_limit=3`
  - bid 50% below target, rank=10, no other branch → does NOT qualify
  - only R1 bid (no R2) → R1 used as latest
  - both R1 and R2 → R2 wins per `submitted_datetime DESC`
- ~2 R2 BidData rows with `bid_amount=0` and ~2 with `bid_amount=NULL` (Phase 1 deletion)

Reused across all four repository ITs and the end-to-end IT.

### 9.3 Coverage target

**85%+** matching sub-projects 4A / 4B / 4C / 5. New entry under `docs/testing/coverage.md` per §10.

### 9.4 Out of scope for tests

- Mendix parity at SQL byte level — specs cite Mendix shape; tests assert Postgres semantics with the new whole-percent convention
- Deferred R3 start-notification (gap-analysis #4) and dedicated R3 Snowflake push (gap-analysis #9) — TODO hooks only
- The `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository` (gap-analysis #3) — separate sub-project; R3 BidData generation flows through the existing dashboard path

---

## 10. Docs updates (per CLAUDE.md mandate)

| Doc | Update |
|---|---|
| `docs/api/rest-endpoints.md` | Add `/preprocess-r3` and `/reinit-r3` with request/response shape + role requirements |
| `docs/architecture/decisions.md` | New ADR: "Sub-project 6 — R3 init + pre-process" recording the 14 numbered decisions in §3 |
| `docs/architecture/data-model.md` | Add `r3_preprocess_*` and `r3_init_*` status columns to `auctions.scheduling_auctions`; document the three new BRSF columns + whole-percent convention update for `target_percent` |
| `docs/app-metadata/modules.md` | New "R3 Init + Pre-process (Sub-project 6)" entry mirroring the sub-project 5 R2 entry |
| `docs/business-logic/index.md` | Add link to new `docs/business-logic/r3-init-and-preprocess.md` |
| `docs/business-logic/r3-init-and-preprocess.md` | New — qualification rules (the four filter branches), STB inheritance, predecessor-guard semantics, admin recovery flow |
| `docs/business-logic/r2-buyer-assignment.md` | Update — note whole-percent convention change for `target_percent` (was 0.05, now 5) |
| `docs/deployment/setup.md` | Add `auctions.r3-preprocess.enabled` + `auctions.r3-init.enabled` config keys (defaulted true) |
| `docs/testing/coverage.md` | Add `auctions.r3lifecycle` entry — 85%+ target |
| `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` | Update §1 + §2 + §3 + §6 to mark item #2 as in-flight / shipped; remove `R3InitStubListener` and `R3PreProcessStubListener` from §3 |

---

## 11. Risks + known gaps

| Risk | Mitigation |
|---|---|
| **R2 unit-alignment changes a shipped formula.** Sub-project 5 shipped 2026-05-06 with `1 - p.target_pct` assuming decimal. Switch to `1 - (p.target_pct / 100)` re-interprets any existing BRSF row. | No production BRSF rows have been loaded yet — only test fixtures. Plan-phase task explicitly: update fixture values 0.05 → 5 in same commit as CTE-formula change, single atomic edit. Production extractor parity preserved (Mendix stores whole-percent). |
| **Predecessor-guard race on rerun.** Admin runs `/preprocess-r3` while listener path is in-flight from cron — second caller hits 409 from `tryFlipToRunning`. | Same single-statement guard as 4C and sub-project 5 — `WHERE r3_preprocess_status <> 'RUNNING'`; second caller gets 0 rows-affected → 409. |
| **R3 BidData empty at round start if `BidDataCreationService` doesn't auto-create on first dashboard view.** Bidders see empty inventory. | Dependency on existing service behavior — confirm `BidDataCreationService` handles on-demand creation for R3 in plan phase before merging. If gap, escalate to a new sub-project (likely 6b). |
| **Listener resilience to "no R3 SA".** Auction created with R1 + R2 only — listener must not crash. | Explicit `Optional<Long>` lookup with silent log + return; covered by `R3PreProcessListenerTest`. |
| **Concurrent QBC reads during DELETE-then-INSERT.** Bidder dashboard reads `qualified_buyer_codes` to gate visibility. | Same as sub-project 5 §11 — entire pre-process is one REQUIRES_NEW tx; MVCC means readers see prior committed snapshot until commit. No transient empty state. |
| **Round-2 close fires pre-process but R3 has been deleted manually.** | `findByAuctionIdAndRound` returns empty → silent skip. Same as the no-R3-SA case. |
| **`is_special_treatment` flag on QBC must remain authoritative.** If `BidDataCreationService` ever switches to reading `buyers.is_special_buyer` directly, R3 STB visibility breaks silently. | Documented in business-logic doc that `qualified_buyer_codes.is_special_treatment` is the per-SA STB flag; ADR records the design intent. |
| **`round3_bid_rank` may be NULL for many AE rows.** 4C only writes ranks for buyer-code/AE pairs that submitted bids; the rank branch must not silently exclude qualifying buyers. | CTE branch includes `IS NOT NULL` guard before the comparison; covered by `R3BuyerQualificationRepositoryIT`. |
| **`auctionui$schedulingauction.auction_week_year` filter dropped.** Mendix filtered by week display string; we filter by `auction_id` instead. | Same selectivity (one auction per week). FK-clean. Test fixture exercises a multi-week setup to verify cross-auction isolation. |

---

## 12. Dependencies + ship order

- **Sub-project 4C** — shipped (V82 status-column pattern, `RecalcStatusUpdater`, `round3_target_price` + `round3_bid_rank` writers).
- **Sub-project 5** — shipped 2026-05-06 (V83, `QualifiedBuyerCodeRepository.bulkInsertForR2`, `RecalcStatus.SKIPPED`).
- **Round 3 reports infra** — V62 (table) + `Round3BuyerDataReportRepository` + `Round3ReportService` + `Round3ReportController` (read-only today; sub-project 6 supplies the writes).

Ship order within sub-project 6:

1. Schema migration V84 (additive only).
2. **R2 unit alignment** (separate small commit) — update `R2BuyerQualificationRepository` CTE formula `1 - p.target_pct` → `1 - (p.target_pct / 100)`; update `r2-init-seed.sql` fixture values 0.05 → 5; refresh `target_percent` column comment; update `docs/business-logic/r2-buyer-assignment.md`.
3. `SchedulingAuction` entity — 8 new R3 status fields + getters/setters.
4. `RecalcStatusUpdater.columnPrefix` — add `R3_PREPROCESS`, `R3_INIT` cases.
5. `RecalcAlreadyRunningException.Process` — add R3_PREPROCESS, R3_INIT enum constants.
6. Rename `QualifiedBuyerCodeRepository.bulkInsertForR2` → `bulkInsertForRound`; update single call site in `R2BuyerAssignmentService`.
7. `R3PreProcessSupportRepository` — bulk DELETE for unsubmitted R2 bids.
8. `R3BuyerQualificationRepository` — regular CTE.
9. `R3SpecialBuyerRepository` — STB CTE.
10. `Round3BuyerDataReportRepository` extension — `bulkInsertForSchedulingAuction` + `deleteBySchedulingAuctionId`.
11. `R3PreProcessService` + `R3PreProcessResult` + `R3PreProcessCompletedEvent`.
12. `R3InitService` + `R3InitResult` + `R3InitCompletedEvent`.
13. `R3PreProcessListener` (replaces stub).
14. `R3InitListener` (replaces stub).
15. **Delete both stub listeners** in same commit as their replacements.
16. `R3LifecycleAdminController` + DTOs + `SecurityConfig` matchers.
17. Tests in §9.1 order.
18. Docs + ADR per §10.

---

## Appendix — relationship to remaining gaps

- **Sub-project 5b (gap-analysis #3)** — `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository`. R3 BidData generation flows through this path (sub-project 6 §3.5 decision). 5b makes per-row R3 visibility correct for non-special bidders. Recommended ship order: 6 → 5b. 5b can ship before 6 if needed but provides no value without R3 QBCs in place.
- **Sub-project 5c (gap-analysis #8)** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. Refines per-row visibility for STB rows after round start. Out of scope.
- **Gap-analysis #4 (R3 start-notification email)** — TODO hook in `R3InitService`. Schema slots already exist on `SchedulingAuction.java:51–57`.
- **Gap-analysis #9 (manual "send all bids" admin action + dedicated R3 Snowflake push)** — TODO hook in `R3InitService`. Existing `AuctionStatusSnowflakePushListener` covers generic status-change pushes; the dedicated R3 push is the residual.
