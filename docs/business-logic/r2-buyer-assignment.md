# R2 Buyer Assignment

When a Round 2 scheduling auction transitions `Scheduled → Started`, the
`R2BuyerAssignmentListener` fires (replacing the prior
`R2InitStubListener`) and writes one set of `qualified_buyer_codes` rows
per active wholesale/data-wipe buyer code, plus seeds `bid_data` rows for
special-treatment buyers across every aggregated-inventory line for the
week.

Without this step the R2 round would have zero qualified buyers — the
bid-submission flow keys off of QBC rows, and there would be none.

## What runs

Two logically separate concerns execute inside one
`@Transactional(REQUIRES_NEW)` service call:

1. **Buyer-code qualification** — port of `SUB_AssignRoundTwoBuyers` +
   `SUB_GenerateRound2QualifiedBuyerCodes`. Decides which buyer codes
   carry into R2 based on R1 bid behaviour against the
   `BidRoundSelectionFilter[round=2]` configuration.
2. **Special-buyer bid-data seeding** — port of `Sub_ProcessSpecialBuyers`
   + `SUB_CreateBidDataForAllAE`. For buyers flagged `is_special_buyer`,
   generates R2 `bid_data` rows for every AE line regardless of R1
   qualification — one row per (special-treatment QBC, AE).

## Qualification modes

The `bid_round_selection_filters[round=2]` row drives the predicate:

| `regular_buyer_qualification` | Behaviour |
|---|---|
| `All_Buyers` | Every active WH/DW buyer code is qualified — short-circuits the per-AE predicate. |
| `Only_Qualified` | A buyer code qualifies iff at least one (ecoid, grade) tuple passes the threshold cascade below. |

## Inventory-option modes

When `regular_buyer_qualification = Only_Qualified`, the
`regular_buyer_inventory_options` value adjusts the fallback branch:

| `regular_buyer_inventory_options` | Fallback branch |
|---|---|
| `InventoryRound1QualifiedBids` | A code qualifies on any `bid_amount > 0` against an AE that did not pass the threshold. |
| `ShowAllInventory` | A code qualifies on any AE row in the prior round, regardless of bid amount. |

## Threshold cascade

For each (buyer_code, ecoid, grade) tuple in the prior round's submitted
bids, the predicate cascade is (first match wins):

1. `r1_target_price = 0 AND bid_amount > 0` → qualifies
2. `r1_target_price > 0 AND bid_amount / r1_target_price >= 1 - (target_percent / 100)` → qualifies (whole-percent: `target_percent = 5` means 5%)
3. `(r1_target_price - bid_amount) <= target_value` → qualifies
4. Inventory-option fallback (above) — qualifies
5. Otherwise — does not qualify

`r1_target_price` is read from `aggregated_inventory.dw_avg_target_price`
or `avg_target_price` based on `bid_data.buyer_code_type`.

**Convention (sub-project 6, 2026-05-07):** `target_percent` values are stored as whole percent (e.g., `5` for 5%), aligned with Mendix native convention and R3's `bid_percentage_variation`. The CTE formula divides by 100: `bid >= target - (target * pct / 100)`.

## Special-treatment semantics

Special-treatment buyers join the qualified set even when
`Only_Qualified` would have excluded them. The rule (decision 3.7 of
the design doc):

> A buyer code is **special-treatment** iff the buyer's `is_special_buyer = TRUE` AND **every** DW/WH code owned by that buyer passes the STB check.

The STB check itself (per code):

```
stb_allow_all_buyers_override = TRUE         → STB
OR  no submitted bids in any prior round     → STB
ELSE                                         → not STB
```

The "all codes must qualify" rule means a buyer with one STB-eligible
code and one non-eligible code yields **zero** special-treatment codes —
the buyer is treated as a regular buyer for R2.

Special-treatment QBC rows are written with `is_special_treatment = TRUE`
and `qualification_type = 'Qualified'`. They additionally trigger the
bulk `bid_data` seed (Phase 6 of the design doc).

## V72 schema notes

Migration **V72** flattened the legacy Mendix M:M junctions
`buyer_mgmt.qbc_buyer_codes` and `buyer_mgmt.qbc_scheduling_auctions`
into direct FK columns `scheduling_auction_id` + `buyer_code_id` on
`buyer_mgmt.qualified_buyer_codes`, plus a unique constraint on the
pair. The R2 phase 5 INSERT writes those flattened columns directly —
no junction-write step is required. The Mendix-shaped junction tables
no longer exist.

## Status lifecycle

Schema migration V83 added four columns to
`auctions.scheduling_auctions`:

| Column | Type | Meaning |
|---|---|---|
| `r2_init_status` | `VARCHAR(20)` | `PENDING` / `RUNNING` / `SUCCESS` / `FAILED` / `SKIPPED` |
| `r2_init_error` | `TEXT` | Exception class + message (truncated to 4000 chars) on FAILED |
| `r2_init_started_at` | `TIMESTAMPTZ` | When the run flipped to RUNNING |
| `r2_init_finished_at` | `TIMESTAMPTZ` | When the run reached terminal state |

`SKIPPED` distinguishes "config gate disabled, no rows written" from
"ran successfully and produced rows" — see config gate below.

The status sub-tx pattern (4C decision 3.8 / 5 decision 3.2) ensures
FAILED writes survive the parent rollback: `RecalcStatusUpdater.markFailed`
runs in a `REQUIRES_NEW` sub-tx so the row is auditable even when the
service body rolled back.

## Config gate

`auctions_feature_config.calculate_round2_buyer_participation` (boolean,
default `TRUE`) controls whether the listener actually computes
qualifications. When `FALSE`, the service short-circuits to
`r2_init_status = 'SKIPPED'` with `r2_init_finished_at = NOW()` — no QBC
or bid_data rows are written. This matches Mendix
`SUB_AssignRoundTwoBuyers` returning empty when the config flag is
unchecked.

## Trigger and admin recovery

The cron tick path:

```
RoundStartedEvent(round=2)
  → R2BuyerAssignmentListener (AFTER_COMMIT, @Async snowflakeExecutor)
  → R2BuyerAssignmentService.run(saId)
```

The admin recovery path (matches 4C `re-rank` shape):

```
POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers
  → R2BuyerAssignmentService.run(saId)
```

Both paths reject with 409 when `r2_init_status = 'RUNNING'` — the
state-flip UPDATE includes `WHERE r2_init_status <> 'RUNNING'`, and zero
rows-affected throws `RecalcAlreadyRunningException(R2_INIT)`. The admin
endpoint also validates `round = 2` (400 otherwise).

Re-firing from any other terminal state (SUCCESS, FAILED, SKIPPED) is
allowed — the QBC write is idempotent (DELETE-then-INSERT for the SA),
and the special-buyer bid_data write is keyed on the `(bid_round,
buyer_code, ecoid, merged_grade)` tuple via the existing get-or-create
helper.

## Snowflake parity

None. Legacy Mendix never synced `qualified_buyer_codes` or the special
`bid_data` rows to Snowflake (no `AUCTIONS.QUALIFIED_BUYER_CODES` table
exists). If/when reporting needs QBC analytics in Snowflake, model after
`BidRankingSnowflakePushListener` — but that is a follow-on, not part of
sub-project 5.

## Per-row R2 visibility (sub-project 5b, 2026-05-07)

`BidDataCreationRepository` now applies the same per-AE predicate cascade as
`R2BuyerQualificationRepository` (sub-project 5), just at row scope rather than
buyer scope. The cascade has 5 branches:

1. `qual_mode = 'All_Buyers'` → row visible regardless of R1 bid
2. No R1 bid + `inv_mode = 'ShowAllInventory'` → visible; otherwise invisible
3. R1 bid + target = 0 + bid > 0 → visible
4. R1 bid + bid/target ≥ 1 - (target_pct / 100) → visible (percent band)
5. R1 bid + (target - bid) ≤ target_value → visible (flat band)

Plus inv_mode fallback: `InventoryRound1QualifiedBids` admits any positive R1 bid.

DW vs Wholesale buyer codes use `dw_avg_target_price` vs `avg_target_price`
respectively.

**STB shortcut:** if `qualified_buyer_codes.is_special_treatment = TRUE` for the
buyer code, `row_visible = TRUE` regardless of threshold.

## Related sub-projects

- **5c** — port `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. Refines
  R2/R3 row-visibility and rerun semantics for STBs after they've been
  seeded.
- **6** — R3 init + R3 pre-process. Independent of 5; ports
  `SUB_Round3_PreProcessRoundData` + `ACT_GenerateRound3_BidDataObjects`.
