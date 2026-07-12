# Auctions-domain data migration (kills parity finding BDD-D1)

**Date:** 2026-07-12 · **Author:** parity data-migration agent · **Source snapshot:** `qa-0327`

## Problem

The legacy Mendix bidder dashboard renders the snapshot's real auction state —
*"Auction 2026 / Wk13"*, round 1 ended, *"Download your Round 1 Bids"* for buyer
code **HN** (nadia). The new app showed **"No scheduled auction is available"**
because the entire `auctions.*` domain was **empty on a fresh Flyway chain** —
no migration seeded `auctions.scheduling_auctions` / `bid_data` /
`aggregated_inventory` (the schema-map called it "fixture-driven", but a grep
proved there are **zero** `INSERT INTO auctions.(auctions|scheduling_auctions|
bid_rounds|bid_data|aggregated_inventory)` statements anywhere in
`db/migration`). This is register finding **BDD-D1** (CRITICAL).

## Fix

Extend `migration_scripts/extract_qa_data.py` to migrate the AUCTIONS domain
from the snapshot, emitting four new Flyway files (**V95–V98**) and remapping
the QBC junctions in the existing **V23**. A fresh chain now converges on the
snapshot's auction state.

| File | Table(s) | Rows |
|---|---|---|
| `V95__data_auctions_core.sql` | `auctions.auctions` (1), `scheduling_auctions` (3), `bid_rounds` (27), `bid_round_selection_filters` (2) | 33 |
| `V96__data_aggregated_inventory.sql` | `auctions.aggregated_inventory` | 10,951 |
| `V97__data_bid_data.sql` | `auctions.bid_data` | 162,086 |
| `V98__data_qbc_reseed.sql` | `buyer_mgmt.qualified_buyer_codes` (1,644) + `qbc_bid_rounds` (20) | 1,664 |

(V96 = aggregated_inventory and V97 = bid_data — **swapped** from the brief's
suggested numbering because `bid_data.aggregated_inventory_id` FKs
`aggregated_inventory`, so the parent must load first. V98 added for the QBC
reseed — see "QBC ordering".)

## Scope — bounded by the snapshot, no arbitrary cap

The snapshot holds exactly **one** auction, *"Auction 2026 / Wk13"* (status
`Started`), with **3** scheduling auctions (round 1 `Closed`, round 2 `Closed`,
round 3 `Scheduled`). Two source tables are enormous —
`auctionui$biddata` = **1,532,795** rows, `auctionui$aggregatedinventory` =
**683,373** rows — so both had to be scoped. The scope is **not** an arbitrary
cap; it is the natural boundary of *live* auction data:

- `auctionui$schedulingauction` contains only the 3 current SAs, but
  `auctionui$bidround_schedulingauction` references **7 distinct** SA ids — the
  other 4 are **dangling** (their parent SA was purged from the snapshot). Only
  bid rounds whose scheduling auction still exists are migratable
  (`bid_rounds.scheduling_auction_id` is `NOT NULL`). That is **27 bid rounds**
  (20 + 6 + 1 across rounds 1/2/3).
- `bid_data` is scoped to those 27 bid rounds → **162,086 rows** (the complete
  set for the current auction; the ~1.37M rows tied to purged SAs are excluded).
- `aggregated_inventory` is scoped to the rows referenced by that bid_data →
  **10,951 rows** (all in week 2026/Wk13).

Both scoped counts are **well under the 500K threshold**, so each is migrated in
**full** — no truncation. Buyer code **HN** (`id 27021597765478872`) has exactly
one bid round (round 1) with **10,951 bid_data rows** — precisely the round-1
bids the legacy dashboard offers for download. **This is the data BDD-D1 needs.**

> The excluded 1.37M bid_data / dangling-SA rows are logged in the V97 file
> header and here — no silent truncation.

## Status-column derivations (V82 / V83 / V84 — not in Mendix)

The 4C recalc (`ranking_status`, `target_price_status`), R2-init
(`r2_init_status`), and R3-lifecycle (`r3_preprocess_status`, `r3_init_status`)
status columns **do not exist in the Mendix source**. They are derived from the
legacy round state, faithful to how the modern services write them (verified in
`BidRankingService`/`TargetPriceRecalcService` — "only valid for closed round 1
or 2"; `R2BuyerAssignmentService`; `R3PreProcessService`/`R3InitService`;
`RecalcStatusUpdater` column mapping). All `*_error` / `*_started_at` /
`*_finished_at` are left **NULL** (the snapshot carries no recalc-run audit trail
to source a real timestamp from — inventing one would be fabrication).

Generator logic: `derive_sa_status(round, round_status, r2_closed)`.

| SA (round / legacy status) | ranking | target_price | r2_init | r3_preprocess | r3_init |
|---|---|---|---|---|---|
| Round 1 — `Closed` | **SUCCESS** | **SUCCESS** | PENDING | PENDING | PENDING |
| Round 2 — `Closed` | **SUCCESS** | **SUCCESS** | **SUCCESS** | PENDING | PENDING |
| Round 3 — `Scheduled` | PENDING | PENDING | PENDING | **SUCCESS** | PENDING |

Rationale, per column:
- **ranking / target_price** run on `RoundClosedEvent(round ∈ {1,2})` → SUCCESS
  on the two closed rounds; round 3 never closed → PENDING. (Corroborated by
  ranked bid_data: `display_round2_bid_rank` values `1/2/3` are populated.)
- **r2_init** runs on `RoundStartedEvent(round=2)`, lives on the round-2 SA;
  round 2 started (now closed) → SUCCESS there; PENDING elsewhere.
- **r3_preprocess** runs on `RoundClosedEvent(round=2)`, lives on the round-3 SA;
  round 2 is closed → it ran → SUCCESS. (Corroborated: round-3 QBCs exist in the
  snapshot — the QBC→SA junction has 548 links to the round-3 SA — which
  R3 pre-process is what generates.)
- **r3_init** runs on `RoundStartedEvent(round=3)`, lives on the round-3 SA;
  round 3 is only `Scheduled` (not started) → PENDING. This **agrees with the
  native legacy column** `round3initstatus = 'Pending'` migrated verbatim.

The native `scheduling_auctions` columns (`round_status`, `round3_init_status`,
`email_reminders`, `has_round`, notification flags, `snowflake_json`, timing)
are migrated **verbatim** from the legacy row.

### Other derived / defaulted decisions (documented)

1. **Week FK resolution.** `mdm.week` is seeded programmatically by V65 (ISO
   weeks, `week_id = ISOYEAR*100 + ISOWEEK`), **not** migrated — its surrogate
   ids don't exist at generation time. The auction / aggregated_inventory week
   FK is emitted as an **apply-time subquery** (`RawSql`) resolved via a per-file
   `_wk` TEMP table that joins the legacy `ecoatm_mdm$week (year, week_number)`
   onto the V65 rows. All scoped data is a **single week** (2026 / Wk13), which
   matches V65's `week_display = '2026 / Wk13'` (year 2026, week_number 13). Bid
   rounds carry **no** week link in the snapshot → `week_id` NULL (the column is
   nullable). `bid_data.week_id` is a denormalized `INTEGER` (not an FK) and is
   passed through verbatim as the legacy business week id (`905`).
2. **`aggregated_inventory` dimension FKs left NULL.** The legacy
   `aggregatedinventory_{brand,model,carrier}` junctions target
   **`ecoatm_mdm$brand`** — a *different* id space than `mdm.brand`, which V21
   seeds from `ecoatm_pwsmdm$brand`. Cross-mapping the two by name is fragile and
   unnecessary: the denormalized `brand` / `model` / `carrier` **TEXT** columns
   (migrated verbatim) carry the display values, and `brand_id`/`model_id`/
   `carrier_id` are nullable. They are set NULL; `week_id` is resolved.
3. **Type conversions.** `ecoid`/`ecoid2` int → VARCHAR (`str()`);
   `datawipe` legacy `'DW'`/`''` VARCHAR → BOOLEAN (`== 'DW'`);
   `display_round{2,3}_bid_rank` legacy VARCHAR (`'1'`,`'2'`,…) → INTEGER
   (`parse_int`, non-numeric → NULL); `bid_amount` NULL → `0` (`NOT NULL
   DEFAULT 0`); `bid_quantity` stays nullable (V73 dropped its NOT NULL);
   `r{2,3}_target_price_factor_type` empty → NULL so the CHECK passes.
4. **Selection-filter enum defaults.** The round-3 `bidroundselectionfilter` row
   has **empty-string** `regularbuyerqualification` / `regularbuyerinventoryoptions`;
   these violate the new CHECK domains, so they map to the schema defaults
   (`Only_Qualified` / `InventoryRound1QualifiedBids`). The legacy
   `'ShowAllINventory'` typo → `'ShowAllInventory'`. `target_percent` is already
   whole-percent in the snapshot (15 for round 2), matching the V84 convention.
5. **`bid_rounds.submitted_by_user_id`** is set only when the legacy
   `bidround_submittedby` direct-user was actually migrated by V19 (guards the
   FK to `user_mgmt.ecoatm_direct_users`); 18 of 27 rounds carry a submitter.
6. Native fixture "clears": V95 `DELETE`s the auction-core tables first (empty on
   a fresh chain, but also removes the **V86/V87-seeded** round-filter rows this
   migration replaces) — the same DELETE-first pattern V34 uses for V33.

## QBC remap + the ordering reality (V23 / V72 / V98)

**Task-4 change to `gen_v23`:** the QBC↔SchedulingAuction and QBC↔BidRound
junctions previously wrote **raw Mendix ids** ("kept as-is, FK deferred"). They
are now **remapped** through the new `scheduling_auctions` / `bid_rounds` id maps,
dropping links whose SA/BR was not migrated (dangling parents). Effect on V23
(byte-diff vs the old generator): `qbc_scheduling_auctions` shrinks from
**2,192 → 1,644** rows (the 548 dangling-SA links dropped; the rest remapped to
SA ids 1/2/3); `qbc_bid_rounds` stays **20** (all valid), remapped to BR ids
1..27. Everything else in V23 is byte-identical.

**The ordering reality (important):** V72 (the QBC flatten + orphan-delete) runs
at **position 72**, *before* the auction core lands at V95. On a pure sequential
Flyway chain, `auctions.scheduling_auctions` is still **empty** when V72
executes, so `scheduling_auction_id NOT IN (SELECT id FROM
auctions.scheduling_auctions)` is true for every row and V72 **still deletes all
QBCs** — the gen_v23 remap alone cannot beat the file-number ordering (it makes
V72 *semantically* correct: it would preserve exactly the 1,644 rows **if** the
auction core preceded it, e.g. under the original "extractor-runs-before-Flyway"
model that V64's header describes — but that model is dead now that every data
load is a V-numbered file).

**Resolution (V98):** the auction migration **authoritatively re-establishes**
the QBC snapshot state after the core exists — `V98__data_qbc_reseed.sql` reloads
exactly the rows that *would* have survived V72: one per **(live scheduling
auction × buyer code)** pair. This is consistent with the brief's framing that
the V95–V98 files "converge on snapshot data": `qualified_buyer_codes` is part of
the auction domain, and V98 owns its final state the same way V95 owns
`scheduling_auctions`.

### Surviving QBC count — **1,644** (predicted and verified)

`378,755` total QBCs − `377,111` true orphans = **1,644** survivors:

| bucket | QBCs | reason |
|---|---|---|
| no SA link at all | 376,563 | orphaned in Mendix (never had a SchedulingAuction) |
| dangling-SA link | 548 | parent SA purged from the snapshot |
| **live-SA link (survivors)** | **1,644** | 548 codes × 3 live rounds; all 1,644 have a resolvable buyer code; **0** duplicate (SA, code) pairs (so the V72 `UNIQUE(scheduling_auction_id, buyer_code_id)` holds) |

Qualification split of the 1,644: **597 `Qualified` / `included=true`** +
**1,047 `Not_Qualified` / `included=false`**. Their 20 `qbc_bid_rounds` links are
reseeded too (all reference live bid rounds).

## Generation-order contract

`main()`'s `OrderedDict` **generation** order ≠ Flyway apply order. gen_v95/96/97
build the `scheduling_auctions` / `bid_rounds` / `agg_inventory` id maps and must
run **before** gen_v23 (which consumes them) and gen_v98. They still write to
V95–V98 files that Flyway applies last. Verified: original-vs-modified generator
output is **byte-identical for V16–V22, V24, V34** (my edits touch nothing but
V23's two junction blocks + the four new files).

## Verification (fresh Flyway V1–V98 on `parity_scratch_auctions`)

Booted the backend on `SERVER_PORT=18080` against a freshly-created
`parity_scratch_auctions` with `AUCTIONS_LIFECYCLE_ENABLED=false`. Flyway applied
**V1–V98 all green** (V95 15 ms, V96 6.5 s, V97 162K rows in 50 s, V98 188 ms),
and the app reached **`{"status":"UP"}` on `:18080`** — so Hibernate
`ddl-auto: validate` **passed**, proving the migrated `auctions.*` schema is
consistent with the JPA entities (not just that Flyway ran). Server killed and
scratch DB dropped afterward.

> Note: a **pre-existing** MIG-1 break at V33 (`column "status_grouped_to" of
> relation "rma_status" does not exist` — V29 placeholder collides with V33's
> `CREATE TABLE IF NOT EXISTS`) blocks *any* fresh chain on this branch, before
> the auction tail. It is unrelated to this task (the findings-register MIG-1
> fix was documented but never applied to this branch). To verify V95–V98 I
> applied the documented MIG-1 fix to V33 **temporarily** and **reverted it**
> before committing — this task ships **no** V33 change. The orchestrator must
> land the MIG-1 fix (drop V29 placeholders in V33) for the fresh chain to reach
> V95 at all. See open questions.

### Paired counts — legacy(scoped) vs scratch — **8/8 PASS**

| table | legacy (scoped) | scratch | |
|---|---|---|---|
| `auctions.auctions` | 1 | 1 | PASS |
| `auctions.scheduling_auctions` | 3 | 3 | PASS |
| `auctions.bid_rounds` | 27 | 27 | PASS |
| `auctions.bid_round_selection_filters` | 2 | 2 | PASS |
| `auctions.aggregated_inventory` | 10,951 | 10,951 | PASS |
| `auctions.bid_data` | 162,086 | 162,086 | PASS |
| `buyer_mgmt.qualified_buyer_codes` | 1,644 | **1,644** | PASS (was 0) |
| `buyer_mgmt.qbc_bid_rounds` | 20 | 20 | PASS |

### BDD-D1 acceptance proofs

- **`scheduling_auctions` carries the 2026/Wk13 auction** — "Auction 2026 /
  Wk13" (`Started`); the `week_id` FK resolves to the V65 `mdm.week` row
  `2026 / Wk13`; the three rounds are `Closed` / `Closed` / `Scheduled` with the
  derived status columns exactly as tabulated above (round 1 `ranking=SUCCESS,
  target_price=SUCCESS`; round 2 adds `r2_init=SUCCESS`; round 3
  `r3_preprocess=SUCCESS, r3_init=PENDING`, agreeing with the migrated native
  `round3_init_status='Pending'`).
- **HN's round-1 bids are present** — buyer code `HN` has **10,951** `bid_data`
  rows at `bid_round = 1`, all 10,951 with a resolved `aggregated_inventory_id`.
  This is exactly what the legacy "Download your Round 1 Bids" affordance reads.
- **QBC populated + correct split** — 1,644 rows = 548 per live round; total
  **597 `Qualified`/included** + **1,047 `Not_Qualified`** (round 1: all 548
  Qualified; rounds 2/3: 24/25 Qualified, rest Not_Qualified — the real snapshot
  qualification state).
- **`bid_data` integrity** — all 162,086 rows have `bid_round_id`,
  `buyer_code_id`, **and** `aggregated_inventory_id` resolved; single denormalized
  `week_id = 905` (legacy business week id, passthrough); **0** rows fail the
  `bid_round BETWEEN 1 AND 3` CHECK.

### Generator surgical-ness

Original-script vs modified-script output is **byte-identical for V16–V22, V24,
V34** (only V23's two junction blocks change + the four new V95–V98 files).
`qbc_scheduling_auctions` in V23 goes 2,192 → 1,644 rows (548 dangling-SA links
dropped, the rest remapped to SA ids 1/2/3); `qbc_bid_rounds` stays 20, remapped.

## Orchestrator integration notes

1. **Regenerate + place files.** `cd migration_scripts && python extract_qa_data.py
   --source-db qa-0327` writes V16–V24, V34, **V95–V98** into
   `backend/src/main/resources/db/migration`. Only **V23** (junction remap) and
   the four new **V95–V98** files change vs. the committed set; V16–V22/V24 stay
   byte-identical.
2. **Fresh-chain reseed.** Integration is a wipe + reseed of `salesplatform_dev`
   (already a consented pattern): drop/recreate the DB, then let Flyway apply
   V1–V98. `flyway.out-of-order: true` is already set, so the V95–V98 tail (and
   the absent V94, owned by a parallel agent) apply cleanly.
3. **File size.** V97 (bid_data, 162K rows) is ~58 MB and V23 ~57 MB — large but
   consistent with the existing V23 (378K QBC rows). No LFS needed today.
4. **Pre-existing note (not this task):** the committed `V34__data_rma.sql` is
   from an **older standalone generator** (header "Generated from Mendix source
   on 2026-04-08 … Standalone generator"), not `gen_v34` — so a regenerated V34
   differs from the committed one. This predates this task (git: only touched in
   the initial commit) and is unrelated to the AUCTIONS work; the findings-register
   MIG-2 fix was documented but never actually applied to this branch. **Left
   untouched** — do not ship a regenerated V34 as part of this change unless the
   RMA owners confirm.
5. **Lifecycle flag.** Validate with `AUCTIONS_LIFECYCLE_ENABLED=false` so the R2/R3
   listeners don't rewrite QBC/bid_data on boot (matches the parity validation
   window in schema-map §1).

## Schema-map / register follow-ups (for the orchestrator)

- `docs/tasks/parity/schema-map.md` §3 and §4 #12, and `findings.md` DATA-2, state
  auctions is fixture-driven and QBC is "empty by design". **Both are now stale**
  — auctions is snapshot-derived and QBC converges on 1,644. Update those
  sections and mark **BDD-D1** `fixed` (and re-triage BDD-P1 as a pure frontend
  panel/heading/download-button task, now that the data is present).
