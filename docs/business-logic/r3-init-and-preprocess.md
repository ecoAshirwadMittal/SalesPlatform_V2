# R3 Init + Pre-process

When a Round 2 scheduling auction transitions to `Closed`, the
`R3PreProcessListener` fires (replacing the prior `R3PreProcessStubListener`)
and writes R3 `qualified_buyer_codes` rows plus `round3_buyer_data_reports`
for the corresponding Round 3 scheduling auction. Later, when Round 3
transitions to `Started`, the `R3InitListener` fires (replacing the prior
`R3InitStubListener`) and flips the `Round3InitStatus` to `Complete`.

Without these steps, R3 (Upsell) bidding does not function:
- No `qualified_buyer_codes` rows for the R3 SA → the bidder dashboard shows
  no inventory and `BidDataCreationService` produces no bid_data rows.
- No `round3_buyer_data_reports` rows → the already-shipped Round 3 Bid Report
  admin page stays empty.

## Two independent services

Sub-project 6 uses two sibling services rather than an orchestrator (design
decision 3.1):

| Service | Triggered by | Concern |
|---|---|---|
| `R3PreProcessService` | `RoundClosedEvent(round=2)` | Heavy data-prep: delete unsubmitted R2 bids, compute R3 QBCs, populate reports |
| `R3InitService` | `RoundStartedEvent(round=3)` | Lightweight status-flip: flip `Round3InitStatus → Complete` with predecessor guard |

Both run `@Async("snowflakeExecutor")` as `AFTER_COMMIT` listeners and execute
their business logic inside a `@Transactional(REQUIRES_NEW)` service call.

## Pre-process: five phases

`R3PreProcessService.run(r2SaId, r3SaId)` executes five phases atomically in
one `REQUIRES_NEW` transaction:

1. **Delete unsubmitted R2 bids** — removes `bid_data` rows from the R2 SA
   where `submitted = FALSE`. Ports `SUB_Round2_DeleteUnsubmittedBids`.
2. **Regular qualification CTE** — computes which (buyer_code, ecoid, grade)
   tuples qualify for R3 based on the latest bid across rounds 1+2 and the
   three filter knobs from `bid_round_selection_filters`.
3. **STB CTE** — computes special-treatment buyers (`prior_round ∈ {1, 2}`
   semantics). `is_special_treatment = TRUE` rows grant STB all-AE visibility
   via `BidDataCreationService`.
4. **DELETE + INSERT QBCs** — deletes existing `qualified_buyer_codes` rows
   for the R3 SA, then bulk-inserts the three-set result (Qualified,
   Not_Qualified, STB). Reuses the renamed `QualifiedBuyerCodeRepository.bulkInsertForRound`.
5. **DELETE + INSERT round-3 reports** — deletes existing
   `round3_buyer_data_reports` rows for the R3 SA, then bulk-inserts one row
   per company with a comma-joined `buyer_codes` column. Populates the
   already-shipped Round 3 Bid Report admin page.

### Has-round gate

If the R3 SA's `has_round = false`, pre-process short-circuits and flips
`r3_preprocess_status → SKIPPED`. No QBCs or reports are written. This
mirrors Mendix `SUB_Round3_PreProcessRoundData`'s early exit.

### No BidData generation

Sub-project 6 does **not** port `ACT_GenerateRound3_BidDataObjects` or
`Sub_ProcessSpecialBuyers`. R3 BidData is created on demand by
`BidDataCreationService` when a bidder opens the dashboard — the same path
used for R1 and R2. `BidDataCreationService` reads
`qualified_buyer_codes.is_special_treatment` to grant STBs all-AE visibility.

## R3 qualification rule

The new qualification rule (product-owner-supplied SQL, replacing Mendix
`SUB_GenerateRound3QualifiedBuyerCodes` step 5) takes the latest bid per
(ecoid, grade, buyer_code) across rounds 1+2 and evaluates three independent
branches from `bid_round_selection_filters[round=3]`:

| Column | Branch (active when NOT NULL) |
|---|---|
| `bid_percentage_variation` | `latest_bid >= round3_target_price - (round3_target_price × pct / 100)` |
| `bid_amount_variation` | `latest_bid >= round3_target_price - amount` |
| `rank_qualification_limit` | `round3_bid_rank <= limit` |

Any branch that matches → qualifies. All three NULL → fall-through qualify
(every buyer qualifies by default when no criteria are configured).

**Convention (sub-project 6, 2026-05-07):** `bid_percentage_variation` is
stored as whole percent (e.g., `5` for 5%). R2's `target_percent` was
retroactively aligned to the same convention (see
`docs/business-logic/r2-buyer-assignment.md`).

## Init: predecessor guard

`R3InitService.run(r3SaId)` validates two conditions before flipping:

1. **Round must be 3** — rejects with `R3LifecycleValidationException` (422)
   otherwise.
2. **Predecessor guard** — `r3_preprocess_status` on the same R3 SA row must
   be `SUCCESS`. If not, the service rejects with 422 (cron path logs warn;
   admin path returns 422).

On success, the service flips the SA's `Round3InitStatus` enum to `Complete`
and writes `r3_init_status = SUCCESS`.

## Status lifecycle

Schema migration V84 added eight columns to `auctions.scheduling_auctions`
(all on the R3 SA row):

| Column | Type | States |
|---|---|---|
| `r3_preprocess_status` | `VARCHAR(20)` | `PENDING` / `RUNNING` / `SUCCESS` / `FAILED` / `SKIPPED` |
| `r3_preprocess_error` | `TEXT` | Exception class + message (truncated to 4000 chars) on FAILED |
| `r3_preprocess_started_at` | `TIMESTAMPTZ` | When the run flipped to RUNNING |
| `r3_preprocess_finished_at` | `TIMESTAMPTZ` | When the run reached terminal state |
| `r3_init_status` | `VARCHAR(20)` | `PENDING` / `RUNNING` / `SUCCESS` / `FAILED` |
| `r3_init_error` | `TEXT` | Exception class + message; includes predecessor-guard message on FAILED |
| `r3_init_started_at` | `TIMESTAMPTZ` | When the run flipped to RUNNING |
| `r3_init_finished_at` | `TIMESTAMPTZ` | When the run reached terminal state |

The status sub-tx pattern (4C decision 3.8) ensures FAILED writes survive
the parent rollback: `RecalcStatusUpdater.markFailed` runs in a `REQUIRES_NEW`
sub-tx. `RecalcStatusUpdater.columnPrefix` recognises `R3_PREPROCESS` →
`"r3_preprocess"` and `R3_INIT` → `"r3_init"`.

## Config gates

- `auctions.r3-preprocess.enabled` (default `true`) — when `false`, the
  `R3PreProcessListener` short-circuits on `RoundClosedEvent(round=2)` and
  does not delegate to the service. The admin recovery endpoint is unaffected.
- `auctions.r3-init.enabled` (default `true`) — when `false`, the
  `R3InitListener` short-circuits on `RoundStartedEvent(round=3)`. The admin
  recovery endpoint is unaffected.

## Trigger and admin recovery

The cron-tick path for pre-process:

```
RoundClosedEvent(round=2)
  → R3PreProcessListener (AFTER_COMMIT, @Async snowflakeExecutor)
  → resolves R3 SA via findByAuctionIdAndRound(auctionId, 3)
  → R3PreProcessService.run(r2SaId, r3SaId)
```

The cron-tick path for init:

```
RoundStartedEvent(round=3)
  → R3InitListener (AFTER_COMMIT, @Async snowflakeExecutor)
  → R3InitService.run(r3SaId)
```

The admin recovery paths:

```
POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/preprocess-r3
  → R3PreProcessService.recalculate(r3SaId)
    → resolves r2SaId via findByAuctionIdAndRound(auctionId, 2)
    → run(r2SaId, r3SaId)

POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/reinit-r3
  → R3InitService.recalculate(r3SaId) → run(r3SaId)
```

Both paths reject with 409 when the corresponding `_status = 'RUNNING'`. The
admin endpoint also validates `round = 3` (422 otherwise via
`R3LifecycleValidationException`).

Re-firing from any terminal state (SUCCESS, FAILED, SKIPPED) is allowed — the
QBC write is idempotent (DELETE-then-INSERT for the R3 SA).

## V85 schema note

Migration V85 adds `scheduling_auction_id` (FK → `auctions.scheduling_auctions`)
and `buyer_codes VARCHAR(1000)` to `auctions.round3_buyer_data_reports` (V62
base table). These columns were required by Phase 5 of `R3PreProcessService`
but were absent from the original V62 schema. See
`docs/architecture/data-model.md` for the full column list.

## Snowflake parity

None. Legacy Mendix never pushed R3 QBC rows or round-3 reports to Snowflake.
If/when reporting needs these in Snowflake, model after
`BidRankingSnowflakePushListener`.

## Per-row R3 visibility (sub-project 5b, 2026-05-07)

`BidDataCreationRepository` applies the per-row form of sub-project 6's R3
selection rule. For each (ecoid, merged_grade) row, take the buyer's latest
submitted nonzero bid across rounds 1+2 (`DISTINCT ON ... ORDER BY
submitted_datetime DESC`) and apply the 4-branch OR cascade:

1. All three filter knobs NULL → row visible regardless
2. `bid_percentage_variation` set + latest_bid ≥ round3_target_price × (1 - pct/100)
3. `bid_amount_variation` set + latest_bid ≥ round3_target_price - amt
4. `rank_qualification_limit` set + round3_bid_rank ≤ limit

ANY branch matches → row visible.

**STB shortcut:** same as R2 — `is_special_treatment=TRUE` admits regardless.

## Related sub-projects

- **4C** — ships `round3_target_price` and `round3_bid_rank` on R2 close;
  these are the inputs to the R3 qualification CTE.
- **5** — ships R2 QBC writes and the `bulkInsertForRound` rename.
- **5c** — ports `SUB_HandleSpecialTreatmentBuyerOnRoundStart`. Refines STB
  row-visibility post-seed at R3.
