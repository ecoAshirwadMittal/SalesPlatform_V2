# Design — Sub-project 5: R2 Buyer Assignment

**Status:** Draft (ready for review)
**Date:** 2026-05-06
**Parent gap analysis:** `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` §6 item #1
**Mendix sources:**
- `migration_context/backend/services/SUB_AssignRoundTwoBuyers.md`
- `migration_context/backend/services/SUB_GenerateRound2QualifiedBuyerCodes.md`
- `migration_context/backend/services/SUB_GenerateQualifiedBuyerCodes.md`
- `migration_context/backend/services/SUB_CreateQualifiedBuyersEntity.md`
- `migration_context/backend/services/SUB_ClearQualifiedBuyerList.md`
- `migration_context/backend/services/SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe.md`
- `migration_context/backend/services/SUB_IsSpecialTreatmentBuyer.md`
- `migration_context/backend/services/SUB_Round2AggregatedInventorySingleItem.md`
- `migration_context/backend/services/Sub_ProcessSpecialBuyers.md`
- `migration_context/backend/services/SUB_CreateBidDataForAllAE.md`
- `migration_context/backend/ACT_ListRound2BuyerCodesUsingAE.md`
- `migration_context/backend/Act_GetOrCreateBuyerCodeSubmitConfig.md`

**Depends on:**
- Sub-project 2 (R1 init — `Round1InitializationService` ships R1 QBC seeding via the same `qualified_buyer_codes` table). Production-correct.
- Sub-project 4C (recalc) — V82 status-column pattern + `RecalcStatusUpdater` are the templates this design extends.
- Schema V8 (`auctions_feature_config.calculate_round2_buyer_participation BOOLEAN NOT NULL DEFAULT TRUE`) — column already exists; only the JPA mapping is missing.
- Schema V9 (`buyer_mgmt.qualified_buyer_codes` + `qbc_buyer_codes` + `qbc_scheduling_auctions`).
- Schema V59 (`auctions.bid_round_selection_filters`).

---

## 1. Background

`R2InitStubListener` currently logs "would assign R2 buyers + process special
buyers" on `RoundStartedEvent(round=2)` and does nothing. As a result every
live R2 cycle sees zero qualified buyers — R2 bid submission relies on
QBC rows existing for the SA, and there are none. This is the most
critical end-to-end gap remaining after sub-project 4C.

The Mendix flow (per `R2InitStubListener`'s own javadoc) splits into two
parallel concerns:

1. **R2 buyer assignment** — port `SUB_AssignRoundTwoBuyers` +
   `SUB_GenerateRound2QualifiedBuyerCodes`. Computes which buyer codes
   carry into R2 based on R1 bid behaviour against the
   `BidRoundSelectionFilter[round=2]` qualification rules; writes
   `qualified_buyer_codes` rows for the R2 `SchedulingAuction`.
2. **Special-buyer bid-data seeding** — port `Sub_ProcessSpecialBuyers`.
   For buyers flagged `is_special_buyer = TRUE`, generate R2 `bid_data`
   rows for every aggregated-inventory line (regardless of R1 qualification),
   one set per qualifying DW/Wholesale code.

Both concerns ship in the same sub-project because they share the
`SchedulingAuction` lifecycle event and write to overlapping tables
(`qualified_buyer_codes` ↔ `bid_data`).

This design adopts the 4C two-process architecture wherever it applies:

- Status flag pattern on `auctions.scheduling_auctions` (`r2_init_status`).
- `REQUIRES_NEW` per-process transaction.
- `RecalcStatusUpdater` reused (extended by one new column-prefix).
- Status sub-tx pattern (FAILED row survives parent rollback).
- Admin recovery REST endpoint, gated by `RecalcAlreadyRunningException` → 409.

What 4C had that R2 buyer assignment does *not*:

- No Snowflake push — `qualified_buyer_codes` is not synced to Snowflake
  in legacy (no `AUCTIONS.QUALIFIED_BUYER_CODES` table). Out of scope.
- No frontend UI — admin-recovery is REST-only, mirroring 4C's
  `RecalcAdminController` design choice 3.3.

---

## 2. Scope

### In scope

- One Flyway migration adding four R2-init status columns to
  `auctions.scheduling_auctions`.
- Map the existing
  `auctions_feature_config.calculate_round2_buyer_participation` column
  on the `AuctionsFeatureConfig` entity.
- New `R2BuyerAssignmentService` (REQUIRES_NEW) implementing the
  five-phase flow (resolve config → qualify buyer codes → resolve special
  treatment → write QBCs → seed special-buyer bid_data).
- New `R2BuyerAssignmentListener` replacing `R2InitStubListener`.
- New `R2BuyerQualificationRepository` — single Postgres CTE porting
  `ACT_ListRound2BuyerCodesUsingAE` + `SUB_Round2AggregatedInventorySingleItem`
  semantics.
- New `R2SpecialBuyerRepository` — single Postgres CTE porting
  `SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe` +
  `SUB_IsSpecialTreatmentBuyer`.
- Reuse `QualifiedBuyerCodeRepository` for `deleteBySchedulingAuctionId`;
  add a `bulkInsertForSchedulingAuction` method for the three-set write.
- Extend `RecalcStatusUpdater.columnPrefix` to recognise `"R2_INIT"`.
- New `R2BuyerAssignmentCompletedEvent` for future downstream consumers
  (notifications, reporting). No listener bound to it inside 4D.
- New `R2BuyerAssignmentResult` record.
- New `R2BuyerAssignmentAdminController` exposing
  `POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers`.
- New `BidDataForAllAEService` (or extension of
  `BidDataCreationService`) ports `SUB_CreateBidDataForAllAE` for
  special-treatment buyers — bulk-insert R2 `bid_data` rows across every
  AE without R1-qualification gating.
- Tests at all four layers per §9 + 85%+ coverage target.
- ADR + docs updates per CLAUDE.md mandate.

### Out of scope (separate sub-projects)

- **Fix `bid_meets_threshold` + `row_visible` stubs in
  `BidDataCreationRepository.java:124-138`** (gap-analysis item #3).
  R2 non-special bidders rely on these for per-row gating. Mechanically
  separate from QBC computation; sub-project 5b.
- **`SUB_HandleSpecialTreatmentBuyerOnRoundStart`** (gap-analysis
  item #8). Different microflow; touches R2/R3 row-visibility and
  rerun semantics for STBs. Sub-project 5c.
- **R3 init / R3 pre-process** — owned by sub-project 6.
- **Buyer auction email notifications** (gap-analysis item #4). Schema
  slots exist; orthogonal.
- **R2 qualified-buyer-code admin result view** (gap-analysis frontend §4).
  Read-only listing of QBC rows for an R2 SA. Frontend follow-on.
- **Snowflake push of QBC rows.** Legacy doesn't sync them. If/when
  reporting needs it, model after `BidRankingSnowflakePushListener`.

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 3.1 | **Single process** `R2_INIT` (not two like 4C). All five phases run inside one `REQUIRES_NEW` tx. | Phases 4 (write QBCs) and 5 (seed special bid_data) share row-level dependencies — partial commits would leave the SA with QBCs but no bid_data, requiring a recovery path the orchestrator can't safely automate. One tx + admin re-fire is simpler. |
| 3.2 | **Status sub-tx pattern reused.** FAILED writes use `RecalcStatusUpdater.markFailed(REQUIRES_NEW)` so the row survives the parent rollback. | Same pattern as 4C decision 3.8; same idiom as `ReserveBidService`. |
| 3.3 | **`calculate_round2_buyer_participation = false` short-circuits.** Listener writes `r2_init_status = 'SUCCESS'` immediately with `r2_init_finished_at = NOW()`. No QBCs written, no bid_data written. | Mendix `SUB_AssignRoundTwoBuyers` decision branch returns empty in this case. Treat as a successful no-op so admins can distinguish "didn't run" from "ran and produced empty result". |
| 3.4 | **Set-based qualification CTE — no Java loops.** The Mendix per-buyer-per-AE loop in `SUB_Round2AggregatedInventorySingleItem` is a microflow idiom; modern Postgres expresses it as a single CTE with a CASE-driven predicate. | Mirrors 4C decision 3.7 — one bulk SQL, faster, atomic, easier to test. |
| 3.5 | **DENSE_RANK is not used.** Qualification is a per-(buyer_code, ecoid, grade) pass/fail predicate — there is no ranking to compute here. | Distinct from 4C; calling out to prevent the next implementer from over-fitting to the recalc CTE. |
| 3.5b | **CTE matches the new-stack enums, not the Mendix legacy enums.** `RegularBuyerQualification` is `{All_Buyers, Only_Qualified}` (V59 simplified Mendix's three-value `{All_Buyers, AllBidders, Only_Qualified}` by dropping `AllBidders`). `RegularBuyerInventoryOption` is `{InventoryRound1QualifiedBids, ShowAllInventory}` (V59 renamed Mendix's `InventoryRound1Bids` to `InventoryRound1QualifiedBids` and dropped the misspelled `ShowAllINventory`). The CTE in §7.1 uses the modern values; do not back-port Mendix enum strings into the SQL. | V59's CHECK constraints (`chk_brsf_regular_qual`, `chk_brsf_regular_inventory`) physically prevent the legacy values from existing in this schema. |
| 3.5c | **Buyer-code type filter is `('Wholesale','Data_Wipe')` only.** Purchasing-order code variants (`Purchasing_Order`, `Purchasing_Order_Data_Wipe`) do NOT participate in R2 buyer qualification — they are a separate (PO-commitment) bidding flow. | Matches Mendix `ACT_ListRound2BuyerCodesUsingAE` filter exactly. `BidDataCreationRepository` collapses all 4 types into a DW/Wholesale dimension for the bid-data write path; R2 buyer assignment is upstream of that and limits to the auction-bid types only. |
| 3.6 | **Three-set QBC write is one bulk INSERT** (not three separate INSERTs). The same CTE that computes qualified codes also computes the disjoint not-qualified set, and the special-treatment set is computed separately and union'd in. `qualification_type` and `is_special_treatment` are derived columns in the SELECT. | Reduces round trips; keeps audit-row counts consistent in one snapshot. |
| 3.7 | **Special-treatment is computed independently** of the regular qualification CTE. A buyer that is `is_special_buyer = TRUE` AND whose every DW/WH code is `SUB_IsSpecialTreatmentBuyer = TRUE` is added to the qualified set as `is_special_treatment = TRUE` regardless of R1 bid history. | Mendix `SUB_GenerateQualifiedBuyerCodes` `Union` step — special buyers join the qualified set even if `RegularBuyerQualification = Only_Qualified` would have excluded them. |
| 3.8 | **Special-buyer bid_data uses a bulk INSERT, not the existing `BidDataCreationRepository.generate(...)` method.** That method is per-(bid_round, buyer_code) and short-circuits on the QBC `included` gate; special buyers need a "give me every AE row regardless of R1 bid" semantics. | Mendix `SUB_CreateBidDataForAllAE` is functionally distinct from `SUB_CreateBidData`. Sharing them would force the existing method to grow a flag it doesn't need; better to keep concerns separate. |
| 3.9 | **Admin endpoint rejects 409 when `r2_init_status = 'RUNNING'`.** Allowed from any other state including SUCCESS (re-fire). | Same single-statement guard as 4C decision 3.6. |
| 3.10 | **Round 2 must be already initialised by the cron tick.** Listener gates on `RoundStartedEvent(round=2)`; admin endpoint validates `round = 2` and rejects 422 otherwise. | Prevents a misfire that writes R2 QBCs against an R1 or R3 SA. |
| 3.11 | **No automatic retry inside a run.** | Idempotent: rerun produces the same QBC rows after the DELETE-then-INSERT cycle. Admin endpoint is the recovery path. |
| 3.12 | **Listener is `@Async("snowflakeExecutor")` (existing pool).** Same pattern as `R1InitListener`. | The bulk CTE is bounded (≈600 buyer codes × ≈30k AEs joined), but offloading from the cron-tick post-commit thread keeps round-transition latency stable across listeners on the same event. |

---

## 4. Architecture

```
┌────────────────────────────────────────────────────────┐
│  RoundStartedEvent(round=2)                            │
│  AFTER_COMMIT, async on snowflakeExecutor               │
└────────────────────────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────────┐
        │  R2BuyerAssignmentListener        │
        │  - delegates to                   │
        │    R2BuyerAssignmentService       │
        └───────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────────┐
        │  R2BuyerAssignmentService         │
        │  REQUIRES_NEW                     │
        │  Phase 1: resolve config          │
        │     short-circuit if              │
        │     calculate_round2_..= FALSE    │
        │  Phase 2: status flip → RUNNING   │
        │  Phase 3: qualification CTE       │
        │  Phase 4: special-treatment CTE   │
        │  Phase 5: DELETE + INSERT QBCs    │
        │  Phase 6: bulk INSERT special     │
        │           bid_data (all AE)       │
        │  Phase 7: status → SUCCESS        │
        │  Phase 7':status → FAILED (catch) │
        │  publishEvent ↦                   │
        │    R2BuyerAssignmentCompleted     │
        └───────────────────────────────────┘
                        │
                        ▼ (no listeners bound today)
        ┌───────────────────────────────────┐
        │  R2BuyerAssignmentCompletedEvent  │
        └───────────────────────────────────┘
```

Admin recovery surface mirrors the cron path:

```
POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers
  → R2BuyerAssignmentService.recalculate(saId)  (just delegates to .run)
```

### Java package layout

```
service/auctions/r2init/
  R2BuyerAssignmentService.java
  R2BuyerAssignmentListener.java        // replaces R2InitStubListener
  R2BuyerAssignmentResult.java          // record(qualifiedCount, specialTreatmentCount,
                                        //        notQualifiedCount, specialBidDataCount,
                                        //        durationMs)
  BidDataForAllAEService.java           // ports SUB_CreateBidDataForAllAE
  R2BuyerAssignmentSkippedException.java  // unchecked; thrown if config gate FALSE
                                          // — never thrown today; reserved for clarity

repository/auctions/
  R2BuyerQualificationRepository.java   // single CTE: per-(buyer_code, AE) qualifies?
  R2SpecialBuyerRepository.java         // single CTE: special-treatment buyer codes
  BidDataForAllAERepository.java        // bulk INSERT INTO bid_data ... SELECT all AEs

event/
  R2BuyerAssignmentCompletedEvent.java  // record(saId, auctionId, weekId,
                                        //        qualifiedCount, specialTreatmentCount)

controller/admin/
  R2BuyerAssignmentAdminController.java

dto/admin/
  R2BuyerAssignmentResponse.java        // status, error, startedAt, finishedAt,
                                        // qualifiedCount, specialTreatmentCount,
                                        // notQualifiedCount, specialBidDataCount,
                                        // durationMs
```

`R2InitStubListener.java` is **deleted** in the same PR. `R3InitStubListener`
and `R3PreProcessStubListener` stay (sub-project 6).

`RecalcStatusUpdater.columnPrefix` extends to:

```java
case "R2_INIT" -> "r2_init";
```

`AuctionsFeatureConfig.java` gets one new field:

```java
@Column(name = "calculate_round2_buyer_participation", nullable = false)
private boolean calculateRound2BuyerParticipation = true;
```

---

## 5. Schema (V83)

Single Flyway migration:
`V83__auctions_r2_init_status.sql`. Additive only.

```sql
ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r2_init_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r2_init_error         TEXT,
    ADD COLUMN r2_init_started_at    TIMESTAMPTZ,
    ADD COLUMN r2_init_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r2_init_status
        CHECK (r2_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_status IS
    '5: PENDING (round not yet started) | RUNNING | SUCCESS | FAILED | SKIPPED (config gate FALSE)';
COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_error IS
    '5: exception class + message (truncated to 4000 chars) on FAILED';
```

`SKIPPED` is added to the enum to disambiguate "config gate disabled" (decision 3.3) from "ran successfully and produced rows". `RecalcStatusUpdater.markSuccess` cannot write `SKIPPED`; the service writes it directly via a small new helper method `markSkipped(saId, "R2_INIT")` to keep `RecalcStatusUpdater` the single source of truth for status transitions.

No data migration. Existing rows pick up `'PENDING'` via the column default — semantically correct (R2 has not been initialised on those rows).

`auctions.bid_ranking_config` and `auctions_feature_config` need no schema
changes. The `calculate_round2_buyer_participation` column exists in V8.

---

## 6. API surface

### 6.1 Endpoints

| Method | Path | Auth | Body | 200 | 4xx |
|---|---|---|---|---|---|
| POST | `/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers` | `ROLE_ADMIN` | none | `R2BuyerAssignmentResponse` | 404 unknown id; 409 status=RUNNING; 422 round ≠ 2 |

### 6.2 Response DTO

```java
public record R2BuyerAssignmentResponse(
    long schedulingAuctionId,
    String status,                      // "SUCCESS" | "FAILED" | "SKIPPED"
    String error,                       // nullable
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int qualifiedCount,                 // QBCs written with qualification_type=Qualified, is_special_treatment=false
    int specialTreatmentCount,          // QBCs written with is_special_treatment=true
    int notQualifiedCount,              // QBCs written with qualification_type=Not_Qualified
    int specialBidDataCount,            // bid_data rows created for special buyers
    long durationMs
) {}
```

### 6.3 Error mapping

- `EntityNotFoundException` for unknown `schedulingAuctionId` → existing
  404 mapping.
- `IllegalArgumentException` for `round ≠ 2` → existing 422 mapping.
- `RecalcAlreadyRunningException` (extended with `Process.R2_INIT`
  enum constant) → existing 409 mapping.

### 6.4 SecurityConfig delta

```java
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reassign-r2-buyers")
    .hasRole("ADMIN")
```

---

## 7. Data flow — SQL contracts

The Mendix flow has three logically separate SQL passes; each is one
native CTE in this design.

### 7.1 Phase 3 — qualification CTE

For closed round 1 of a specific auction, returns the set of buyer-code
ids that qualify for R2.

```sql
WITH params AS (
  SELECT
    sa.id          AS scheduling_auction_id,
    sa.auction_id  AS auction_id,
    a.week_id      AS week_id,
    sa.round       AS round,                          -- expected = 2
    brsf.regular_buyer_qualification        AS qual_mode,
    brsf.regular_buyer_inventory_options    AS inv_mode,
    brsf.target_percent                     AS target_pct,
    brsf.target_value                       AS target_val
  FROM auctions.scheduling_auctions sa
  JOIN auctions.auctions a              ON a.id = sa.auction_id
  JOIN auctions.bid_round_selection_filters brsf
    ON brsf.round = sa.round
  WHERE sa.id = CAST(:r2_sa_id AS bigint)
),
prior_sa AS (
  SELECT sa.id AS prev_sa_id
    FROM auctions.scheduling_auctions sa, params p
   WHERE sa.auction_id = p.auction_id
     AND sa.round      = p.round - 1
),
active_codes AS (
  -- All active wholesale/data-wipe buyer codes (PO variants are out of scope; see decision 3.5c)
  SELECT bc.id AS buyer_code_id, bc.buyer_code_type
    FROM buyer_mgmt.buyer_codes bc
    JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
    JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
   WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
     AND b.status = 'Active'
),
r1_bids AS (
  -- R1 submitted bids by buyer code, with target prices joined in
  SELECT bd.buyer_code_id,
         bd.ecoid,
         bd.merged_grade,
         bd.submitted_bid_amount AS bid_amount,
         CASE bd.buyer_code_type
           WHEN 'Data_Wipe' THEN ai.dw_avg_target_price
           ELSE                  ai.avg_target_price
         END AS r1_target_price
    FROM auctions.bid_data bd
    JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id AND br.submitted = TRUE
    JOIN prior_sa psa           ON psa.prev_sa_id = br.scheduling_auction_id
    JOIN auctions.aggregated_inventory ai
      ON ai.ecoid2       = bd.ecoid
     AND ai.merged_grade = bd.merged_grade
     AND ai.week_id      = (SELECT week_id FROM params)
   WHERE bd.submitted_bid_amount > 0
),
qualifies_per_ae AS (
  -- Per-(buyer_code, ecoid, grade) Only_Qualified predicate cascade (matches Mendix
  -- SUB_Round2AggregatedInventorySingleItem with the simplified V59 two-value enums per
  -- decision 3.5b). All_Buyers is handled separately in qualified_codes — never reaches here.
  SELECT r.buyer_code_id,
         CASE
           -- Threshold met: target=0 and bid>0
           WHEN r.r1_target_price = 0 AND r.bid_amount > 0 THEN TRUE
           -- Threshold met: percent band
           WHEN r.r1_target_price > 0
                AND r.bid_amount / r.r1_target_price >= 1 - p.target_pct THEN TRUE
           -- Threshold met: flat-value band
           WHEN (r.r1_target_price - r.bid_amount) <= p.target_val THEN TRUE
           -- Fallback: bid > 0 under "qualified bids" inventory mode
           WHEN p.inv_mode = 'InventoryRound1QualifiedBids' AND r.bid_amount > 0 THEN TRUE
           -- Fallback: ShowAllInventory admits regardless
           WHEN p.inv_mode = 'ShowAllInventory' THEN TRUE
           ELSE FALSE
         END AS qualifies
    FROM r1_bids r, params p
   WHERE p.qual_mode = 'Only_Qualified'
)
-- Final selection: All_Buyers shortcut OR Only_Qualified per-AE rule
SELECT ac.buyer_code_id
  FROM active_codes ac, params p
 WHERE p.qual_mode = 'All_Buyers'
UNION
SELECT q.buyer_code_id FROM qualifies_per_ae q WHERE q.qualifies = TRUE;
```

The result drives Phase 5 inserts. `regular_buyer_qualification` and
`regular_buyer_inventory_options` enums match the values stored in
`bid_round_selection_filters` (V59 enum-string + CHECK constraints
`chk_brsf_regular_qual` and `chk_brsf_regular_inventory`).

### 7.2 Phase 4 — special-treatment CTE

```sql
WITH params AS (
  SELECT
    sa.id                  AS scheduling_auction_id,
    sa.auction_id          AS auction_id,
    sa.round               AS round,                  -- expected = 2
    brsf.stb_allow_all_buyers_override
  FROM auctions.scheduling_auctions sa
  JOIN auctions.bid_round_selection_filters brsf
    ON brsf.round = sa.round
  WHERE sa.id = CAST(:r2_sa_id AS bigint)
),
special_buyers AS (
  -- All buyers with is_special_buyer = TRUE
  SELECT b.id AS buyer_id
    FROM buyer_mgmt.buyers b
   WHERE b.is_special_buyer = TRUE
),
special_dwwh_codes AS (
  -- Their DW/WH buyer codes, joined to active codes
  SELECT bcb.buyer_id, bc.id AS buyer_code_id
    FROM buyer_mgmt.buyer_code_buyers bcb
    JOIN special_buyers sb ON sb.buyer_id = bcb.buyer_id
    JOIN buyer_mgmt.buyer_codes bc
      ON bc.id = bcb.buyer_code_id
     AND bc.buyer_code_type IN ('Wholesale','Data_Wipe')
),
prior_round_bids AS (
  -- Count of submitted bids by (buyer, code) in any round prior to current
  SELECT bd.buyer_code_id, bcb2.buyer_id, COUNT(*) AS bid_count
    FROM auctions.bid_data bd
    JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id AND br.submitted = TRUE
    JOIN auctions.scheduling_auctions sa
      ON sa.id = br.scheduling_auction_id
    JOIN params p                    ON p.auction_id = sa.auction_id AND sa.round < p.round
    JOIN buyer_mgmt.buyer_code_buyers bcb2
      ON bcb2.buyer_code_id = bd.buyer_code_id
   GROUP BY bd.buyer_code_id, bcb2.buyer_id
),
code_is_stb AS (
  -- A code is special-treatment iff override OR no prior-round bids by this buyer
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
  -- A buyer is "stb-eligible" iff ALL their DW/WH codes are STB
  SELECT buyer_id
    FROM code_is_stb
   GROUP BY buyer_id
  HAVING bool_and(is_stb) = TRUE
)
SELECT cs.buyer_code_id
  FROM code_is_stb cs
  JOIN buyers_all_codes_stb bs ON bs.buyer_id = cs.buyer_id;
```

The result is the set of `buyer_code_id` values to insert as
`is_special_treatment = TRUE` in Phase 5.

### 7.3 Phase 5 — two-set QBC INSERT (V72-flattened)

**Note (V72):** This design originally specified a three-step write
that populated junction tables `buyer_mgmt.qbc_buyer_codes` and
`buyer_mgmt.qbc_scheduling_auctions`. Migration **V72**
(`buyer_mgmt_qbc_flatten`, see
`backend/src/main/resources/db/migration/V72__buyer_mgmt_qbc_flatten.sql`)
DROPPED both junction tables and added direct FK columns
`scheduling_auction_id` + `buyer_code_id` on
`buyer_mgmt.qualified_buyer_codes`. The single bulk INSERT below
populates those flattened columns directly, so no junction step is
needed. `QualifiedBuyerCodeRepositoryCustom.bulkInsertJunctions(saId)`
is retained as a documented no-op for spec parity but **must not** be
called from `R2BuyerAssignmentService` — the work already happened in
step 2.

Application code drives this in two steps inside the same tx (same
listener thread, REQUIRES_NEW):

```java
// 1. clear: DELETE all QBC rows for the SA (SUB_ClearQualifiedBuyerList parity)
qbcRepo.deleteBySchedulingAuctionId(saId);

// 2. bulk insert one row per active DW/WH code with derived columns:
//    qualification_type ∈ {'Qualified', 'Not_Qualified'}
//    included           ∈ {TRUE, FALSE}
//    is_special_treatment ∈ {TRUE, FALSE}
qbcRepo.bulkInsertForR2(saId, qualifiedCodeIds.toArray(new Long[0]),
                              specialTreatmentCodeIds.toArray(new Long[0]));
```

A single SQL statement implements (2):

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

`GROUP BY bc.id` collapses the M:M join to one row per code. The
flattened `scheduling_auction_id` + `buyer_code_id` columns are
populated directly — V72 removed the need for a junction-write step.

### 7.4 Phase 6 — special-buyer bid_data bulk INSERT

For every QBC row written with `is_special_treatment = TRUE`, generate
R2 `bid_data` rows for every AE in `aggregated_inventory` for the week.
Each special QBC produces one `bid_data` row per AE — the per-row
target price + max quantity is read from the AE columns
(DW vs Wholesale branches per `bd.buyer_code_type`).

```sql
WITH params AS (
  SELECT sa.id        AS scheduling_auction_id,
         sa.round     AS round,
         a.week_id    AS week_id
    FROM auctions.scheduling_auctions sa
    JOIN auctions.auctions a ON a.id = sa.auction_id
   WHERE sa.id = CAST(:r2_sa_id AS bigint)
),
special_qbcs AS (
  SELECT qbc.buyer_code_id, bc.code AS code_text, bc.buyer_code_type
    FROM buyer_mgmt.qualified_buyer_codes qbc
    JOIN buyer_mgmt.buyer_codes bc ON bc.id = qbc.buyer_code_id, params p
   WHERE qbc.scheduling_auction_id = p.scheduling_auction_id
     AND qbc.is_special_treatment = TRUE
),
ensure_bid_round AS (
  -- One bid_round per (sa, buyer_code) — INSERT…ON CONFLICT DO NOTHING
  -- followed by SELECT id. Done in Java for simplicity (no RETURNING…SELECT
  -- chain in pure SQL) — see BidDataForAllAEService.
  SELECT 1
)
INSERT INTO auctions.bid_data (
    bid_round_id, buyer_code_id, aggregated_inventory_id,
    ecoid, merged_grade, code, company_name,
    bid_quantity, bid_amount, target_price, maximum_quantity,
    buyer_code_type, bid_round, week_id, bid_data_doc_id,
    created_date, changed_date
)
SELECT :bid_round_id,
       sq.buyer_code_id,
       ai.id,
       ai.ecoid2,
       ai.merged_grade,
       sq.code_text,
       (SELECT b.company_name
          FROM buyer_mgmt.buyer_code_buyers bcb
          JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
         WHERE bcb.buyer_code_id = sq.buyer_code_id
         LIMIT 1),
       NULL,
       0,
       CASE sq.buyer_code_type
         WHEN 'Data_Wipe' THEN ai.dw_avg_target_price
         ELSE                  ai.avg_target_price
       END,
       CASE sq.buyer_code_type
         WHEN 'Data_Wipe' THEN ai.dw_total_quantity
         ELSE                  ai.total_quantity
       END,
       sq.buyer_code_type,
       (SELECT round FROM params),
       (SELECT CAST(week_id AS integer) FROM params),
       :bid_data_doc_id,
       NOW(), NOW()
  FROM special_qbcs sq
  CROSS JOIN auctions.aggregated_inventory ai
  JOIN params p ON p.week_id = ai.week_id
 WHERE ai.is_deprecated = FALSE
   AND CASE sq.buyer_code_type
         WHEN 'Data_Wipe' THEN ai.dw_total_quantity
         ELSE                  ai.total_quantity
       END > 0;
```

The `:bid_round_id` is resolved per-buyer-code in Java by
`BidDataForAllAEService` — it calls a small "get-or-create
`auctions.bid_rounds` row" helper (already exists for R1), then invokes
the bulk INSERT once per code. (Alternative: a single SQL with a
correlated `INSERT … RETURNING` chain. Per CLAUDE.md "no premature
abstraction", the per-code Java loop is fine — typical R2 has < 50
special buyers.)

### 7.5 State-flip UPDATE (entry guard)

```sql
UPDATE auctions.scheduling_auctions
   SET r2_init_status      = 'RUNNING',
       r2_init_started_at  = NOW(),
       r2_init_finished_at = NULL,
       r2_init_error       = NULL
 WHERE id = :sa_id
   AND r2_init_status <> 'RUNNING';
```

Zero rows-affected → throw `RecalcAlreadyRunningException(R2_INIT)`
→ 409 from admin endpoint; cron path swallows + logs.

---

## 8. Listener wiring

```java
@Component
@ConditionalOnProperty(
    name = "auctions.r2-init.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class R2BuyerAssignmentListener {

    private static final Logger log = LoggerFactory.getLogger(R2BuyerAssignmentListener.class);

    private final R2BuyerAssignmentService service;

    public R2BuyerAssignmentListener(R2BuyerAssignmentService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 2) return;
        try {
            service.run(event.roundId());
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("r2-init skipped — already running schedulingAuctionId={}",
                event.roundId());
        } catch (RuntimeException ex) {
            log.error("r2-init failed schedulingAuctionId={} error={}",
                event.roundId(), ex.toString(), ex);
        }
    }
}
```

Pattern matches `R1InitListener`. The service swallows all internal
exceptions to FAILED status writes; the listener catch is a wiring-bug
safety net.

---

## 9. Testing

### 9.1 Test matrix

| Layer | File | What it asserts |
|---|---|---|
| Repository (qualification CTE) | `R2BuyerQualificationRepositoryIT` | All four `regular_buyer_qualification` modes; `RegularBuyerInventoryOptions` toggles; DW vs Wholesale branch on target price; `target_percent` band; `target_value` band; rerun idempotent |
| Repository (special-treatment CTE) | `R2SpecialBuyerRepositoryIT` | `stb_allow_all_buyers_override = TRUE` short-circuits; zero-prior-bids → STB; mixed codes → not STB; `is_special_buyer = FALSE` excluded |
| Repository (QBC bulk INSERT) | `QualifiedBuyerCodeRepositoryIT` extension | Three-set INSERT writes correct counts; `bulkInsertJunctions` documented as no-op (V72 flattened); rerun via DELETE-then-INSERT idempotent |
| Repository (special bid_data) | `BidDataForAllAERepositoryIT` | Bulk INSERT writes one row per (special-QBC, AE); DW vs Wholesale price/qty branch correct; deprecated AEs excluded |
| Service | `R2BuyerAssignmentServiceTest` | Status flip PENDING→RUNNING→SUCCESS; `calculate_round2_buyer_participation = FALSE` → SKIPPED with no QBC writes; on repo throw → status flip RUNNING→FAILED with truncated error; event published only on SUCCESS; admin recalculate path rejects RUNNING |
| Listener | `R2BuyerAssignmentListenerTest` | Round 2 triggers service; rounds 1, 3 do not; service throw is logged but never propagated; `auctions.r2-init.enabled=false` short-circuits |
| Controller | `R2BuyerAssignmentAdminControllerIT` | `Administrator` role required (403); 200 + `R2BuyerAssignmentResponse` on success; 409 RUNNING; 404 unknown id; 422 round ≠ 2 |
| End-to-end | `R2BuyerAssignmentEndToEndIT` | Seed + simulated `RoundStartedEvent(round=2)` → all status columns + QBC rows + special bid_data rows + completion event |

### 9.2 Fixture

`backend/src/test/resources/fixtures/auctions/r2-init-seed.sql`:

- 1 auction week (existing seed)
- 1 `scheduling_auctions` row each for round 1 and round 2 (R1 closed, R2 just started)
- 1 `bid_round_selection_filters` row for round 2 with each of the four
  `regular_buyer_qualification` modes parameterised across test variants
- ~6 (ecoid, grade) rows in `aggregated_inventory` (mix of DW and WH
  target-price values, including a row with `dw_avg_target_price = 0`
  to exercise the divide-by-zero branch)
- ~5 buyers — 2 special, 3 regular
- ~10 buyer codes across them — mix of DW, Wholesale, and types that
  should be excluded (Inactive buyers, soft_deleted codes)
- ~12 R1 `bid_data` rows hitting each branch:
  - bid above target (Only_Qualified branch — qualifies)
  - bid below target by less than `target_percent` (qualifies)
  - bid below target by less than `target_value` (qualifies)
  - bid below both (does not qualify)
  - bid_amount = 0 (does not qualify under `Only_Qualified` unless `inv_mode = ShowAllInventory`)
- 1 special buyer with all DW/WH codes never having bid in any prior
  round (STB-eligible)
- 1 special buyer with at least one prior-round bid (NOT STB-eligible)

Reused across `R2BuyerQualificationRepositoryIT`,
`R2SpecialBuyerRepositoryIT`, and `R2BuyerAssignmentEndToEndIT`.

### 9.3 Coverage target

**85%+** matching 4A / 4B / 4C — see `docs/testing/coverage.md`.

### 9.4 Out of scope for tests

- Mendix parity at SQL byte level — specs cite Mendix shape; tests
  assert Postgres semantics.
- The `bid_meets_threshold` + `row_visible` stubs in
  `BidDataCreationRepository` (sub-project 5b) — these are not exercised
  by R2 buyer assignment because Phase 6 uses
  `BidDataForAllAERepository` directly and bypasses the existing CTE.

---

## 10. Docs updates (per CLAUDE.md mandate)

| Doc | Update |
|---|---|
| `docs/api/rest-endpoints.md` | Add the new admin endpoint with request/response shape + role requirements |
| `docs/architecture/decisions.md` | New ADR: "Sub-project 5 — R2 buyer assignment" recording the 12 numbered decisions in §3 |
| `docs/architecture/data-model.md` | Add `r2_init_*` status columns to `auctions.scheduling_auctions`; clarify the QBC three-set write semantics on `buyer_mgmt.qualified_buyer_codes` |
| `docs/app-metadata/modules.md` | New "R2 Buyer Assignment" entry under sub-project 5 |
| `docs/business-logic/index.md` | Add a new file `docs/business-logic/r2-buyer-assignment.md` describing the qualification rules + special-treatment semantics |
| `docs/business-logic/r2-buyer-assignment.md` | New |
| `docs/deployment/setup.md` | Add `auctions.r2-init.enabled` config key (defaulted true) |
| `docs/testing/coverage.md` | Add `auctions.r2init` entry — 85%+ target |
| `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` | Update §1 + §6 to mark item #1 as in-flight / shipped |

---

## 11. Risks + known gaps

| Risk | Mitigation |
|---|---|
| New-stack `RegularBuyerQualification` is `{All_Buyers, Only_Qualified}` (V59 dropped Mendix's `AllBidders`). The CTE has only two explicit qualification branches; `inv_mode` toggles the fallback. CHECK constraint `chk_brsf_regular_qual` blocks any other value at the DB layer, so the CTE has no defensive null/unknown handling. | If/when Mendix-prod data is ever loaded, V59 enforces the constraint at insert time — invalid rows fail loudly rather than producing silent ELSE-FALSE behaviour. |
| `r1_target_price` can be zero (no R1 target seeded). Division by zero would error. | CTE branches on `r.r1_target_price = 0` BEFORE the division — see Mendix `SUB_Round2AggregatedInventorySingleItem` line 34. |
| `target_percent` is `NUMERIC(10, 4)` and stores values like `0.0500` (5%). The threshold formula `bid / target >= 1 - 0.05` matches Mendix shape. | Validated by sample arithmetic in IT fixture. |
| Concurrent admin re-fire racing the cron tick | Q3.9 — state-flip UPDATE with `WHERE r2_init_status <> 'RUNNING'` is the single source of truth; second caller gets 0 rows-affected → 409. |
| QBC DELETE-then-INSERT inside a single tx — concurrent reads (BidderDashboard) could see "no QBCs" momentarily | The whole sequence is one REQUIRES_NEW tx; Postgres MVCC means readers see the prior committed snapshot until the new INSERT commits. No transient empty state visible to concurrent reads. |
| `qbc_query_helpers` Mendix session-cache rows (V9 §`qbc_query_helpers`) — legacy reads QBC via this cache, not directly | New stack does not use `qbc_query_helpers`; it reads `qualified_buyer_codes` directly via JPA. `BidderDashboardService` confirms this. |
| Special-buyer bid_data INSERT could race against admin manual-fire | Same status-flip guard — admin endpoint cannot proceed while listener path is RUNNING. |
| `auctions_feature_config` is a singleton row (id=1 by Mendix convention). New stack JPA entity has no integrity constraint enforcing singleton-ness. | Out of scope for sub-project 5; existing risk shared with `Act_GetOrCreateBuyerCodeSubmitConfig` port whenever it lands. Service reads `findById(1L)` defensively. |

---

## 12. Dependencies + ship order

- **Sub-project 2 (R1 init)** — shipped (`Round1InitializationService` writes R1 QBCs).
- **Sub-project 4C** — shipped (V82 status-column pattern + `RecalcStatusUpdater`).
- **Sub-project 5** — this design.

Ship order within sub-project 5:

1. Schema migration V83 (additive only — no data loss; safe to run
   ahead of code).
2. Map `calculate_round2_buyer_participation` on `AuctionsFeatureConfig`.
3. Native repositories — `R2BuyerQualificationRepository`,
   `R2SpecialBuyerRepository`, `BidDataForAllAERepository`, plus the
   `bulkInsertForR2` extension on `QualifiedBuyerCodeRepository`.
4. Extend `RecalcStatusUpdater.columnPrefix` for `R2_INIT`; add
   `markSkipped` helper.
5. `R2BuyerAssignmentService` + `R2BuyerAssignmentResult`.
6. `BidDataForAllAEService`.
7. `R2BuyerAssignmentListener` (replaces stub) + delete `R2InitStubListener`.
8. `R2BuyerAssignmentAdminController` + `SecurityConfig` matcher.
9. `R2BuyerAssignmentCompletedEvent` (no listeners bound today).
10. Tests in the order of §9.1.
11. Docs + ADR per §10.

The Snowflake push (which 4C had) is intentionally absent. If a future
sub-project needs QBC analytics in Snowflake, model after
`BidRankingSnowflakePushListener`.

---

## Appendix — relationship to remaining gaps

- **Sub-project 5b (item #3)** — the `bid_meets_threshold` + `row_visible`
  stubs in `BidDataCreationRepository.java:124-138` are NOT touched here.
  Sub-project 5 unblocks R2 by writing QBC rows; 5b makes the per-row
  R2 visibility correct for non-special bidders. Recommended ship order:
  5 → 5b. 5b can ship before 5 if needed but provides no value without
  QBCs in place.
- **Sub-project 5c (item #8)** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart`
  is a separate Mendix microflow that runs at round start and refines
  visibility for STB rows after they've been seeded. Out of scope.
- **Sub-project 6 (item #2)** — R3 init + R3 pre-process. Independent of 5.
