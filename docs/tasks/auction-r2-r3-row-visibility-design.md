# Design — Sub-project 5b: R2/R3 Per-Row Visibility

**Status:** Draft (ready for review)
**Date:** 2026-05-07
**Parent gap analysis:** `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` §6 item #3
**Mendix sources:**
- `migration_context/backend/services/SUB_Round2AggregatedInventorySingleItem.md` — R2 per-AE predicate
- (R3 has no Mendix lineage — sub-project 6 explicitly did NOT port `ACT_GenerateRound3_BidDataObjects`. R3 row visibility is the per-row form of the new R3 selection rule introduced by the product owner during sub-project 6 brainstorming.)

**Depends on:**
- Sub-project 5 — shipped 2026-05-06 (V83; QBC three-set write with `included` and `is_special_treatment` columns; whole-percent `target_percent` post-Task-2)
- Sub-project 6 — shipped 2026-05-07 (V84; three R3 BRSF columns `bid_percentage_variation`, `bid_amount_variation`, `rank_qualification_limit`; `round3_target_price` + `round3_bid_rank` written by 4C on R2 close)
- `BidDataCreationRepository` — sub-project 4 baseline; the file marks the two stubs at lines 130 + 138 as sub-project 4 (now 5b) territory in its implNote (line 247-253)

---

## 1. Background

`BidDataCreationRepository.generate(bidRoundId, buyerCodeId, bidDataDocId)` is the single-CTE writer that seeds `auctions.bid_data` rows for a given `(bid_round, buyer_code)` pair. It is called from `BidDataCreationService.ensureRowsExist(...)` (under a `pg_advisory_xact_lock` for concurrency safety) when a buyer's dashboard view triggers row creation, and during sub-project-3's R1 init seeding.

Two CTE blocks at lines 125-141 are stubs:

```sql
inventory_with_threshold AS (
    SELECT inv.*,
           /* TODO: sub-project 4 will replace this constant with a
              real R2/R3 threshold check derived from
              selection_filter and prior_round_biddata. */
           TRUE AS bid_meets_threshold
    FROM inventory inv
),
inventory_qualified AS (
    SELECT iwt.*,
           /* TODO: sub-project 4 will replace this with the
              special-treatment + included branching from
              qualified_buyer_check. */
           TRUE AS row_visible
    FROM inventory_with_threshold iwt, qualified_buyer_check q
    WHERE q.included = true
)
```

Effect today: every QBC `included=TRUE` buyer sees every AE row in R2 and R3, regardless of R1 rank, R1 bid amount, or whether they bid that AE at all. Functionally incorrect for production — the dashboard shows non-qualifying buyers inventory they should not see, and bid submission can record bids on AEs the buyer was supposed to be filtered out of.

This sub-project replaces both stubs with the real predicate cascades:
- **R2 cascade** — port of the per-AE predicate from `SUB_Round2AggregatedInventorySingleItem` (5 branches: target=0+nonzero-bid, percent band, value band, inv_mode fallback, ShowAllInventory).
- **R3 cascade** — per-row form of the product-owner-supplied R3 selection rule (4 branches: all-NULL fallthrough, percent variation, amount variation, rank ceiling).
- **STB shortcut** — `row_visible = is_special_treatment OR bid_meets_threshold`. Special-treatment buyers (per sub-project 5's `is_special_treatment=TRUE` flag on QBCs) see every row regardless of threshold.

---

## 2. Scope

### In scope

- Pure SQL refactor of `BidDataCreationRepository.CTE_SQL`. Three CTE blocks change:
  - `params` extends to LEFT JOIN `bid_round_selection_filters` for R2 + R3 inputs (NULL for R1).
  - `prior_round_biddata` rewrites to "latest submitted nonzero bid per (ecoid, merged_grade) across all prior rounds" — works for both R2 (R1 only) and R3 (R1+R2, R2 wins by `submitted_datetime`).
  - `inventory_with_threshold` replaces constant `TRUE` with `CASE WHEN p.round = 2 THEN <R2 cascade> WHEN p.round = 3 THEN <R3 cascade> ELSE TRUE END`. R1 fallthrough preserves R1 init behaviour.
  - `inventory_qualified` replaces constant `TRUE` with `(q.is_special_treatment OR iwt.bid_meets_threshold)`.
- The `prior_scheduling_auction` CTE is **deleted** (subsumed by the new round-agnostic `prior_round_biddata`).
- Roughly 20 new IT cases in `BidDataCreationRepositoryIT` (R2 Only_Qualified prior-bid × 7, R2 qual_mode shortcuts × 3, R3 cascade × 7, STB shortcut × 2, R1 regression × 1).
- ADR + business-logic + coverage docs.

### Out of scope (separate sub-projects or deferred cleanups)

- **LEFT JOIN refactor for "missing QBC"** at `BidDataCreationRepository.java:251` implNote. Currently CROSS JOIN against `qualified_buyer_check` produces zero rows when no QBC exists; same observable behaviour as a Not_Qualified QBC. Cosmetic distinction with no functional impact. Defer.
- **Sub-project 5c (gap-analysis #8)** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. Refines STB visibility *at round start* after admin actions. Different microflow.
- **Service-layer changes** — `BidDataCreationService` signature and lock semantics unchanged.
- **Schema changes** — no new migrations. V84 (sub-project 6) already added the R3 BRSF columns.
- **Frontend changes** — buyer dashboard re-runs `ensureRowsExist` on view; corrected backend visibility flows through automatically.

---

## 3. Decisions

| # | Decision | Rationale |
|---|---|---|
| 3.1 | **Single CTE, branch on `params.round`.** No new method, no new repository, no Java-side SQL building. | The repo's interface is round-agnostic by design (`generate(bidRoundId, buyerCodeId, bidDataDocId)`); the round is already derivable from `bid_rounds.scheduling_auction_id`. Keeping the SQL self-contained in one method preserves the existing caller contract and concurrency guarantees (`pg_advisory_xact_lock`). |
| 3.2 | **Both R2 and R3** in scope, not R2-only. | The stub serves both rounds; fixing R2 alone leaves R3 still always-TRUE. Sub-project 6 just shipped — R3 buyers will hit this code path imminently. Single coherent "row visibility" sub-project. |
| 3.3 | **R2 cascade ports `SUB_Round2AggregatedInventorySingleItem` per-AE predicate verbatim** (with whole-percent unit alignment from sub-project 6's Task 2). DW vs Wholesale branches on `buyer_code_type`, picking the correct target column. | Same logical predicate as sub-project 5's `R2BuyerQualificationRepository` — just exposed at per-row scope instead of being aggregated. |
| 3.4 | **R3 cascade is the per-row form of sub-project 6's R3 selection rule** (4 branches: all-NULL fallthrough → all rows, OR pct branch, OR amount branch, OR rank branch). No reference to `regular_buyer_inventory_options`. | User confirmed during brainstorming: R3 row visibility is purely the three filter knobs (`bid_percentage_variation`, `bid_amount_variation`, `rank_qualification_limit`). R3 does NOT inherit R2's inv_mode toggle. |
| 3.5 | **All-NULL R3 filters → all rows visible** (even AEs without prior bid). | Matches buyer-level inclusivity — at the buyer scope, all-NULL means "qualify everyone with any nonzero bid". At per-row scope, the simplest mental model is "no per-row filter to apply → no row excluded". |
| 3.6 | **`prior_round_biddata` rewrites to round-agnostic latest-per-AE.** Pulls all submitted nonzero bids from rounds `< params.round`, applies `DISTINCT ON (ecoid, merged_grade) ORDER BY submitted_datetime DESC`. | For R2 (round=2), only R1 SAs match `sa_prev.round < 2` → DISTINCT ON returns the unique R1 bid per AE. For R3 (round=3), R1+R2 SAs match → DISTINCT ON picks R2 over R1 by submit time. Same shape as sub-project 6's `R3BuyerQualificationRepository.latest_bid` CTE. The `prior_scheduling_auction` CTE is deleted (subsumed). |
| 3.7 | **STB shortcut**: `row_visible = is_special_treatment OR bid_meets_threshold`. | Sub-project 5's QBC bulk-INSERT writes `is_special_treatment=TRUE` for STB buyers. The OR shortcut means STBs see every row regardless of threshold — gap-analysis #8's basic STB-sees-all rule covered without touching `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. |
| 3.8 | **`WHERE q.included = TRUE` filter is preserved**, not replaced or removed. | Sub-project 5 writes `included=TRUE` for both Qualified and STB QBCs, `included=FALSE` only for Not_Qualified. The filter correctly excludes Not_Qualified buyers without dropping STB. |
| 3.9 | **`All_Buyers` qual_mode in R2 → all rows visible** (no per-row filtering). | Sub-project 5's R2 buyer CTE admits every active DW/WH code under `All_Buyers` mode. At per-row scope, the consistent extension is "show all rows to those buyers". |
| 3.10 | **R1 fallthrough returns TRUE.** Bid round 1 has no prior round; the `ELSE TRUE` branch in the outer CASE preserves R1-init behaviour exactly. | Existing R1-init IT cases must continue to pass. The BRSF table has no row for round=1 (V59 `chk_brsf_round CHECK (round IN (2,3))`); the `LEFT JOIN` produces NULL columns; the R1 branch never reads them. |
| 3.11 | **`prior_round_biddata` filters `submitted = TRUE` and `submitted_bid_amount > 0`** explicitly. | The current CTE doesn't filter submission state — it would have included unsubmitted/zero bids. The new threshold predicates assume only bid history that "counts" for qualification. Mendix `SUB_Round2AggregatedInventorySingleItem` filters `BidAmount > 0` and `Submitted=true`. |
| 3.12 | **No code reuse with `R2BuyerQualificationRepository` or `R3BuyerQualificationRepository`.** Predicate logic is duplicated inline. | The two CTEs operate at different scopes (this one inserts rows; those return Sets of buyer-code ids) and have different join shapes (LEFT JOIN to inventory vs CROSS JOIN to bid history). Extracting a shared SQL view or function adds complexity for marginal gain; the predicate is small enough to duplicate. Documented lineage in CTE comments. |
| 3.13 | **LEFT JOIN refactor for "missing QBC" deferred.** `inventory_qualified` keeps the implicit cross join. | "Missing QBC" and "Not_Qualified QBC" both produce zero rows today; no observable behaviour difference. Cosmetic distinction; defer to a future cleanup task. |
| 3.14 | **Service layer + concurrency unchanged.** `BidDataCreationService.ensureRowsExist` keeps its REQUIRES_NEW + advisory-lock contract. | The change is below the service layer; no semantic shift in caller contract. |

---

## 4. Architecture

```
┌──────────────────────────────────────────┐
│ Buyer dashboard / bid submission         │
│    → BidDataCreationService              │
│       .ensureRowsExist(buyerCode, round) │
│       (REQUIRES_NEW; pg_advisory_xact_   │
│        lock on bid_round_id)             │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│ BidDataCreationRepository.generate(...)  │
│   single CTE_SQL block, MANDATORY tx     │
│                                          │
│   params (← LEFT JOIN BRSF for R2+R3)    │
│        ↓                                 │
│   existing_check (idempotency guard)     │
│        ↓                                 │
│   qualified_buyer_check (QBC row)        │
│        ↓                                 │
│   inventory (week + DW/WH qty filter)    │
│        ↓                                 │
│   prior_round_biddata (latest per-AE,    │
│      round-agnostic across prior rounds) │
│        ↓                                 │
│   inventory_with_threshold (CASE round   │
│      → R2 cascade / R3 cascade / R1 TRUE)│
│        ↓                                 │
│   inventory_qualified (STB shortcut OR   │
│      bid_meets_threshold; included=TRUE) │
│        ↓                                 │
│   qualified_rows (visibility filter +    │
│      idempotency check)                  │
│        ↓                                 │
│   INSERT INTO auctions.bid_data ...      │
└──────────────────────────────────────────┘
```

No new files, no new beans, no listener changes, no controller changes.

### Where each concept lives in the SQL

- BRSF inputs: `params` CTE (extended)
- Latest prior bid per AE: `prior_round_biddata` CTE (rewritten)
- Per-row threshold cascade (R2 + R3): `inventory_with_threshold` CTE (rewritten)
- STB shortcut + Not_Qualified filter: `inventory_qualified` CTE (rewritten)
- Insert: unchanged

---

## 7. Data flow — SQL contracts

The full text of the four changed CTE blocks. Existing CTEs not listed (`existing_check`, `qualified_buyer_check`, `inventory`, `qualified_rows`, the INSERT itself) are unchanged.

### 7.1 — Extended `params` CTE

```sql
params AS (
    SELECT
        CAST(:bid_round_id    AS bigint) AS bid_round_id,
        CAST(:buyer_code_id   AS bigint) AS buyer_code_id,
        CAST(:bid_data_doc_id AS bigint) AS bid_data_doc_id,
        br.scheduling_auction_id, sa.round, a.week_id,
        bc.code AS buyer_code_text,
        (SELECT b.company_name FROM buyer_mgmt.buyer_code_buyers bcb
         JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
         WHERE bcb.buyer_code_id = bc.id ORDER BY bcb.buyer_id LIMIT 1) AS company_name,
        CASE WHEN bc.buyer_code_type IN ('Data_Wipe','Purchasing_Order_Data_Wipe')
             THEN 'DW' ELSE 'Wholesale' END AS buyer_code_type,
        -- 5b: R2 BRSF inputs (NULL when round=1; chk_brsf_round permits 2 or 3 only)
        brsf.target_percent                  AS target_pct,
        brsf.target_value                    AS target_val,
        brsf.regular_buyer_qualification     AS qual_mode,
        brsf.regular_buyer_inventory_options AS inv_mode,
        -- 5b: R3 BRSF inputs
        brsf.bid_percentage_variation        AS pct_var,
        brsf.bid_amount_variation            AS amt_var,
        brsf.rank_qualification_limit        AS rank_lim
    FROM auctions.bid_rounds br
    JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
    JOIN auctions.auctions a              ON a.id = sa.auction_id
    JOIN buyer_mgmt.buyer_codes bc        ON bc.id = :buyer_code_id
    LEFT JOIN auctions.bid_round_selection_filters brsf ON brsf.round = sa.round
    WHERE br.id = :bid_round_id
)
```

### 7.2 — Rewritten `prior_round_biddata` (round-agnostic latest-per-AE)

The `prior_scheduling_auction` CTE is **deleted**. Replace with a single CTE:

```sql
prior_round_biddata AS (
    -- Latest submitted nonzero bid per (ecoid, merged_grade) across ALL prior rounds.
    -- R2 (round=2): only R1 SAs match `sa_prev.round < 2` → DISTINCT ON returns the
    --   unique R1 bid per AE.
    -- R3 (round=3): R1+R2 SAs match → DISTINCT ON + ORDER BY submitted_datetime DESC
    --   picks R2 over R1 when both exist.
    -- R1 (round=1): no prior rounds → CTE returns zero rows; bid_meets_threshold
    --   defaults to TRUE in the outer CASE.
    SELECT DISTINCT ON (bd.ecoid, bd.merged_grade)
           bd.ecoid,
           bd.merged_grade,
           bd.submitted_bid_quantity AS prev_qty,
           bd.submitted_bid_amount   AS prev_amount,
           bd.round3_bid_rank        AS prev_rank
      FROM auctions.bid_data bd
      JOIN auctions.bid_rounds br_prev
        ON br_prev.id = bd.bid_round_id AND br_prev.submitted = TRUE
      JOIN auctions.scheduling_auctions sa_prev
        ON sa_prev.id = br_prev.scheduling_auction_id
      JOIN auctions.scheduling_auctions sa_cur
        ON sa_cur.id = (SELECT scheduling_auction_id FROM params)
       AND sa_prev.auction_id = sa_cur.auction_id
     WHERE bd.buyer_code_id = (SELECT buyer_code_id FROM params)
       AND sa_prev.round    < (SELECT round FROM params)
       AND bd.submitted_bid_amount > 0
     ORDER BY bd.ecoid, bd.merged_grade, bd.submitted_datetime DESC
)
```

### 7.3 — Rewritten `inventory_with_threshold` (the actual fix)

```sql
inventory_with_threshold AS (
    SELECT inv.*,
           prb.prev_qty, prb.prev_amount, prb.prev_rank,
           CASE
             -- ── R2 cascade (5 branches, DW vs Wholesale split) ────────────────
             WHEN p.round = 2 THEN
               CASE
                 -- All_Buyers admits regardless of R1 bid history
                 WHEN p.qual_mode = 'All_Buyers' THEN TRUE
                 -- Only_Qualified, no R1 bid → only ShowAllInventory inv_mode admits
                 WHEN prb.prev_amount IS NULL THEN
                   (p.inv_mode = 'ShowAllInventory')
                 -- Only_Qualified with R1 bid → DW vs Wholesale predicate cascade
                 WHEN p.buyer_code_type = 'DW' THEN
                      (inv.dw_avg_target_price = 0 AND prb.prev_amount > 0)
                   OR (inv.dw_avg_target_price > 0
                       AND prb.prev_amount / inv.dw_avg_target_price >= 1 - (p.target_pct / 100))
                   OR (inv.dw_avg_target_price - prb.prev_amount <= p.target_val)
                   OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                   OR (p.inv_mode = 'ShowAllInventory')
                 ELSE  -- Wholesale
                      (inv.avg_target_price = 0 AND prb.prev_amount > 0)
                   OR (inv.avg_target_price > 0
                       AND prb.prev_amount / inv.avg_target_price >= 1 - (p.target_pct / 100))
                   OR (inv.avg_target_price - prb.prev_amount <= p.target_val)
                   OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                   OR (p.inv_mode = 'ShowAllInventory')
               END
             -- ── R3 cascade (4 branches, all OR'd) ─────────────────────────────
             WHEN p.round = 3 THEN
               -- All-NULL filters → fallthrough TRUE for every row, even AEs without prior bid.
               -- Otherwise: ANY non-NULL branch matches → row visible.
               (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
               OR (p.pct_var IS NOT NULL
                   AND prb.prev_amount IS NOT NULL
                   AND prb.prev_amount
                       >= inv.round3_target_price - (inv.round3_target_price * p.pct_var / 100))
               OR (p.amt_var IS NOT NULL
                   AND prb.prev_amount IS NOT NULL
                   AND prb.prev_amount >= inv.round3_target_price - p.amt_var)
               OR (p.rank_lim IS NOT NULL
                   AND prb.prev_rank IS NOT NULL
                   AND prb.prev_rank <= p.rank_lim)
             -- ── R1: no prior round; preserve current always-TRUE behaviour ────
             ELSE TRUE
           END AS bid_meets_threshold
    FROM inventory inv
    CROSS JOIN params p
    LEFT JOIN prior_round_biddata prb
           ON prb.ecoid = inv.ecoid AND prb.merged_grade = inv.merged_grade
)
```

### 7.4 — Rewritten `inventory_qualified` (`row_visible` with STB shortcut)

```sql
inventory_qualified AS (
    SELECT iwt.*,
           (q.is_special_treatment OR iwt.bid_meets_threshold) AS row_visible
      FROM inventory_with_threshold iwt
      CROSS JOIN qualified_buyer_check q
     WHERE q.included = TRUE
)
```

The downstream `qualified_rows` CTE (with `WHERE iq.row_visible = TRUE AND ec.n = 0`) is unchanged.

---

## 9. Testing

### 9.1 Test matrix (all in `BidDataCreationRepositoryIT`)

| Cluster | Cases (≈20 new) |
|---|---|
| **R2 cascade — Only_Qualified, prior R1 bid exists** | (1) DW + bid above target via percent → visible; (2) DW + bid above target by exactly `target_value` → visible; (3) DW + AE target=0 + bid>0 → visible; (4) DW + bid below all thresholds + `inv_mode=InventoryRound1QualifiedBids` → visible (fallback branch); (5) DW + bid below all thresholds + `inv_mode=ShowAllInventory` → visible; (6) DW + bid below all thresholds + neither inv_mode admits → invisible; (7) Wholesale variant uses `avg_target_price` (not `dw_avg_target_price`) |
| **R2 cascade — `qual_mode` shortcuts** | (8) `All_Buyers` admits all rows regardless of R1 bid history; (9) `Only_Qualified` + no R1 bid + `ShowAllInventory` → visible; (10) `Only_Qualified` + no R1 bid + `InventoryRound1QualifiedBids` → invisible |
| **R3 cascade** | (11) all-NULL filters → all rows visible (even AEs without prior bid); (12) `pct_var=5` + latest bid above threshold → visible; (13) `amt_var=1` + latest bid above target by 1 → visible; (14) `rank_lim=3` + `round3_bid_rank=2` → visible; (15) all three set + buyer fails all branches → invisible; (16) `DISTINCT ON` picks R2 over R1 (R1 bid was below threshold, R2 bid above) → R2 wins → visible; (17) `prev_rank IS NULL` + only rank_lim configured → invisible (rank branch can't match) |
| **STB shortcut** | (18) `is_special_treatment=TRUE` + AE where buyer fails all threshold branches → visible (R2 case); (19) same in R3 — STB sees every row |
| **R1 regression** | (20) round=1 + no BRSF row + no prior bid → all rows visible (existing R1-init behaviour preserved) |

That's exactly 20 new IT cases. Existing R1-init cases stay; minor fixture tweaks may be needed to add R2 + R3 + STB rows.

### 9.2 Fixture strategy

Plan-phase decision between three options:

**A.** Extend the IT's existing fixture in place (whatever `BidDataCreationRepositoryIT` currently uses).
**B. (recommended)** Reuse `r3-lifecycle-seed.sql` from sub-project 6 — already seeds R1+R2 bids hitting R3 branches, R2 SA + R3 SA, BRSF rows, STB-eligible buyer. Extend with explicit R2-branch scenarios where missing.
**C.** New dedicated fixture `bid-data-row-visibility-seed.sql`.

Option B leverages existing scaffolding. Plan phase verifies whether sub-project 6's fixture covers enough R2-cascade cases or needs additions (e.g., target=0 row, target_value-band row).

### 9.3 Coverage target

**85%+** matching the rest of sub-project 5/6. Listed under `auctions.biddata.row-visibility` in `docs/testing/coverage.md`.

### 9.4 Service test

`BidDataCreationServiceTest` is mock-based against the repo signature. The signature does not change. **No service-test changes needed.**

### 9.5 Out of scope for tests

- Mendix byte-for-byte parity (cite Mendix shape; assert Postgres semantics with the documented unit conventions).
- LEFT JOIN refactor — deferred per decision 3.13.
- Sub-project 5c (STB rerun mid-round) — different microflow.

---

## 10. Docs updates (per CLAUDE.md mandate)

| Doc | Update |
|---|---|
| `docs/architecture/decisions.md` | New ADR: "Sub-project 5b — R2/R3 row visibility correctness" recording the 14 numbered decisions in §3 |
| `docs/business-logic/r2-buyer-assignment.md` | Add section: per-row R2 visibility now matches per-buyer R2 qualification (same predicate, different scope) |
| `docs/business-logic/r3-init-and-preprocess.md` | Add section: per-row R3 visibility uses the same 4-branch cascade as R3 buyer qualification |
| `docs/testing/coverage.md` | Add `auctions.biddata.row-visibility` entry — 85%+ target |
| `docs/tasks/auction-flow-gap-analysis-2026-05-06.md` | Mark item #3 shipped 2026-05-07; note items #8 (sub-project 5c) and the LEFT JOIN refactor remain |

No `docs/api/rest-endpoints.md` changes (no new endpoints). No `docs/architecture/data-model.md` changes (no new columns). No `docs/deployment/setup.md` changes (no new config). No `docs/app-metadata/modules.md` changes (no new module — pure SQL refactor of an existing module).

---

## 11. Risks + known gaps

| Risk | Mitigation |
|---|---|
| **Backward compat for R1 init.** Existing R1-init IT cases must continue to pass. | The R1 ELSE branch in the outer CASE returns `TRUE`, matching current always-TRUE behaviour. The `LEFT JOIN` to BRSF produces NULL for R1; the R1 branch never reads BRSF columns. Run all existing IT cases unchanged. |
| **Numeric division by zero.** R2 cascade does `prb.prev_amount / inv.dw_avg_target_price`. Could divide by zero if target=0. | The first sub-branch handles target=0 explicitly (`WHEN inv.dw_avg_target_price = 0 AND prb.prev_amount > 0 THEN TRUE`). Subsequent sub-branches use `inv.dw_avg_target_price > 0` guard. Cannot reach division-by-zero. |
| **`prev_amount = 0`.** Could `prior_round_biddata` ever produce a row with `prev_amount = 0`? | No — the CTE filters `WHERE bd.submitted_bid_amount > 0`. When the row exists, `prev_amount > 0` is guaranteed. |
| **`round3_target_price IS NULL`.** R3 cascade compares against this column; NULL would propagate through arithmetic. | 4C `TargetPriceRecalcRepository` writes `round3_target_price` on R2 close. By R3 dashboard time, the column is populated for all in-scope AEs. NULL would silently fail the threshold branches → row excluded — safe default. |
| **Concurrent execution.** `BidDataCreationService` already serializes per-bid-round via `pg_advisory_xact_lock`. | No new concurrency concern. The CTE rewrite is fully within the existing critical section. |
| **Test fixture interference.** Sub-project 5's `r2-init-seed.sql` is shared with `R2BuyerQualificationRepositoryIT`. If 5b extends this fixture for `BidDataCreationRepositoryIT`, the changes must not break sub-project 5's tests. | Plan phase verifies whether 5b extends existing fixtures or creates a dedicated one. Recommended: reuse `r3-lifecycle-seed.sql` (sub-project 6's, which is already comprehensive). Fixture changes scoped to non-conflicting ID ranges. |
| **Whole-percent unit alignment** from sub-project 6 Task 2: `target_pct` is whole-percent (5 = 5%). The CTE uses `1 - (target_pct / 100)`. | Documented convention; matches sub-project 5's already-fixed CTE. |
| **`q.is_special_treatment` is boolean.** Postgres native query yields boolean; chains correctly with `OR iwt.bid_meets_threshold`. | No type coercion needed. |
| **`DISTINCT ON` ordering stability** when two prior bids have the same `submitted_datetime` (rare). | Tie-breaker is implementation-defined. The fixture should not seed equal-timestamp duplicates; add a secondary `bd.id DESC` to `ORDER BY` if determinism is needed in tests. Plan phase decides. |

---

## 12. Dependencies + ship order

- **Sub-project 5** — shipped (V83 + QBC three-set write; whole-percent `target_percent` post-Task-2)
- **Sub-project 6** — shipped (V84 with three R3 BRSF columns; `round3_target_price`/`round3_bid_rank` written by 4C on R2 close; `r3-lifecycle-seed.sql` fixture)

Ship order within sub-project 5b:

1. Update `BidDataCreationRepository.CTE_SQL` — extend `params`, replace `prior_round_biddata`, rewrite `inventory_with_threshold` and `inventory_qualified`. Delete `prior_scheduling_auction`.
2. Extend `BidDataCreationRepositoryIT` — add ≈20 new test cases covering R2 + R3 + STB + R1 regression. Seed/extend fixture per plan phase decision.
3. Verify existing `BidDataCreationServiceTest` still passes (mock-based — no changes expected).
4. ADR + business-logic docs + coverage entry + gap-analysis update.

---

## Appendix — relationship to remaining gaps

- **Sub-project 5c (gap-analysis #8)** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. STB rerun semantics at round start. Different microflow; out of scope here.
- **Gap-analysis #4** — buyer auction email notifications. Schema slots exist; orthogonal to row visibility.
- **Gap-analysis #3 → fully addressed by this sub-project once shipped.**
- **LEFT JOIN refactor** at `BidDataCreationRepository.java:251` implNote — cosmetic distinction between "missing QBC" and "Not_Qualified QBC" since both produce zero rows today. Defer to a future cleanup task.
