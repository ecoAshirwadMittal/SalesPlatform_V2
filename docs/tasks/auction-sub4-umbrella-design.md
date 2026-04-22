# Umbrella Design — Sub-project 4: Bid Ranking + Target-Price Recalc (with EB + PO port)

**Status:** Approved (ready for child brainstorming)
**Date:** 2026-04-22
**Parent:** Sub-project 4 of the auction-lifecycle-cron decomposition
(`docs/tasks/auction-lifecycle-cron-design.md`)
**Decision authority:** this document decomposes the scope; each child spec
owns its own implementation details.

---

## 1. Background

Sub-project 3 (`docs/tasks/auction-bid-data-create-plan.md`) landed the bidder
dashboard and synchronous `bid_data` generation. It explicitly deferred three
items to "sub-project 4":

- R2/R3 threshold computation (Task 7b CTE stubbed `TRUE AS bid_meets_threshold`)
- Target-price factor recalc
- R2/R3 rank computation

During scope discussion two items were dropped and two were added:

- **Dropped:** Target-price threshold check on submit — not required by ops.
- **Dropped:** `highest_bid` flag maintenance — bid ranking supersedes it.
- **Added:** Full port of the Mendix `ecoatm_eb` (ExchangeBid / reserve bid)
  module, including admin surface. Needed because the target-price CTE
  consumes EB reserve floors via `GREATEST(...)`.
- **Added:** Full port of the Mendix `ecoatm_po` (PurchaseOrder + PODetail)
  module, including admin surface. Same reason — PO max prices feed into the
  same `GREATEST(...)`.

The EB and PO ports are genuinely unrelated business domains. Bundling them
into a single sub-project-4 plan would produce an unreviewable mega-plan and
delay all three deliverables to the slowest path. This umbrella decomposes
sub-project 4 into three sibling children with clear boundaries.

---

## 2. Children

| # | Name | Scope summary | Depends on |
|---|---|---|---|
| **4A** | EB module complete port | `ecoatm_eb` → modern schema + JPA + admin CRUD + Next.js UI + write-path microflows + EB→Snowflake sync + extractor + tests + ADR | — |
| **4B** | PO module complete port | `ecoatm_po` → modern schema (multi-junction week_from/week_to + buyer_code) + JPA + admin CRUD + Next.js UI + lifecycle (draft→active→closed) + PO→Snowflake sync + extractor + tests + ADR | — |
| **4C** | Bid ranking + target-price recalc | Replace `BidRankingStubListener`. Two independent processes (`RANKING`, `TARGET_PRICE`) with their own transactions + status flags on `scheduling_auctions`. Target-price CTE joins real EB + PO floors from 4A + 4B. Admin re-rank endpoint. `BidRankingUpdatedEvent` + Snowflake listener. | 4A, 4B |

---

## 3. Dependency graph + ship order

```text
           ┌──────────────┐
           │      4A      │
           │  EB module   │─────┐
           └──────────────┘     │
                                ▼
           ┌──────────────┐    ┌──────────────┐
           │      4B      │───▶│      4C      │
           │  PO module   │    │ Rank + recalc│
           └──────────────┘    └──────────────┘
```

- **4A and 4B** run in parallel (independent domains, no shared code).
- **4C** blocks on both — its target-price CTE joins against real EB +
  PO tables and expects both modules' data to be loaded.
- **Until 4C lands**, `BidRankingStubListener` continues logging
  "would rank bids + calc target price" exactly as today. Zero
  behavioral regression on the ranking path while 4A + 4B ship.
- **4A and 4B ship admin value independently** — ops can manage reserve
  bids and purchase orders through the modern UI even before 4C exists.

---

## 4. Process model for 4C (recorded here so 4A + 4B know the contract)

Post-round-close processing splits into two independent processes. Each
runs in its own `@Transactional(REQUIRES_NEW)` boundary and writes its
own success/failure flag on `scheduling_auctions`. One process failing
does NOT stop the other.

| Process | Reads from | Writes to | Status column |
|---|---|---|---|
| `RANKING` | `bid_data` (week + closed round scope) | `bid_data.round{N}_bid_rank` + `display_round{N}_bid_rank` | `scheduling_auctions.ranking_status` |
| `TARGET_PRICE` | `bid_data` + `aggregated_inventory` + `target_price_factors` + `reserve_bid` (4A) + `po_detail` (4B) | `aggregated_inventory.round{N}_target_price` + `round{N}_max_bid` + `round{N}_max_bid_buyer_code` + factor columns + EB/PO derivative columns | `scheduling_auctions.target_price_status` |

EB and PO contribute via LEFT JOIN into the target-price CTE's
`GREATEST(...)` term — they are **not** separate processes. They are
query inputs to the single `TARGET_PRICE` CTE.

Status values: `PENDING` / `RUNNING` / `SUCCESS` / `FAILED`. Each
`*_status` column gets companion `*_error` (text) + `*_started_at` +
`*_finished_at` (timestamptz) columns. This is new surface — it does
NOT exist in Mendix.

---

## 5. Per-child scope boundaries (what each child does NOT own)

### 4A — EB module

**Does not own:**
- Target-price recalc consumption — 4C wires EB floors into the CTE
- Bid-ranking integration — ranking doesn't read `reserve_bid`
- Any cross-dependency with PO

### 4B — PO module

**Does not own:**
- Target-price recalc consumption — 4C owns the CTE integration
- Any cross-dependency with EB
- PO lifecycle automation (auto-close past-dated POs) — deferred

### 4C — Ranking + recalc

**Does not own:**
- EB or PO schema changes — 4A and 4B own those
- Threshold validation on submit — dropped
- `highest_bid` flag maintenance — dropped (ranking supersedes)
- R3-close processing — R3 is terminal, no R4 to rank/recalc for
- `SUB_HandleSpecialTreatmentBuyerOnRoundStart` — separate sub-project
- CSV upload for `bid_data_docs` — separate sub-project

---

## 6. Mapping to sub-project 3's deferred-gap list

| Deferred item from `auction-bid-data-create-plan.md` | Resolved by |
|---|---|
| R2/R3 threshold computation (Task 7b `TRUE AS bid_meets_threshold`) | **4C** (the ranking CTE supersedes this semantic) |
| Target-price factor recalc | **4C** (with real EB + PO floors from 4A + 4B) |
| R2/R3 rank computation | **4C** |
| Highest-bid flag maintenance | Dropped — ranking supersedes |
| Target-price threshold check on submit | Dropped |
| `SUB_HandleSpecialTreatmentBuyerOnRoundStart` | Separate sub-project (not 4A/4B/4C) |
| CSV upload for `bid_data_docs` | Separate sub-project |

---

## 7. Handoff rules

- This umbrella doc is **reference-only**. No code, no schema changes,
  no ADR flow directly from it.
- Each child gets its own brainstorm → spec → plan → execute cycle.
- Revise this umbrella **only** if the decomposition itself changes
  (a fourth child gets carved out, two children merge, or ship order
  reverses).
- ADRs are written per-child, not at the umbrella level.

### Spec locations

| Spec | Path |
|---|---|
| Umbrella (this doc) | `docs/tasks/auction-sub4-umbrella-design.md` |
| 4A — EB module | `docs/tasks/auction-eb-module-design.md` *(future)* |
| 4B — PO module | `docs/tasks/auction-po-module-design.md` *(future)* |
| 4C — Ranking + recalc | `docs/tasks/auction-bid-ranking-design.md` *(future)* |

### Plan locations (written after each child's spec is approved)

| Plan | Path |
|---|---|
| 4A | `docs/tasks/auction-eb-module-plan.md` *(future)* |
| 4B | `docs/tasks/auction-po-module-plan.md` *(future)* |
| 4C | `docs/tasks/auction-bid-ranking-plan.md` *(future)* |

---

## 8. Risks + mitigations

| Risk | Mitigation |
|---|---|
| EB or PO port schedule slips and blocks 4C indefinitely | 4A and 4B ship admin value on their own; `BidRankingStubListener` continues producing today's no-op behavior until 4C lands |
| 4C's target-price CTE accidentally written against wrong EB/PO column names | Section 4 records the contract inputs; 4A and 4B specs must document their final column names back to this doc before 4C brainstorm starts |
| Cross-child scope creep (4A starts wiring into target-price recalc) | Section 5 explicitly records what each child does NOT own. Any PR breaking a boundary gets bounced back to re-scope |
| Mendix write-path microflows for EB/PO more complex than estimated | 4A and 4B brainstorms will surface this; if either child balloons beyond ~25 tasks, consider splitting admin-surface from read-only-schema-port into two sub-projects |

---

## 9. Next step

Start child brainstorming. Suggested order:

1. **Pick 4A or 4B first** (user chooses — they're parallel-safe)
2. After picking, enter a fresh `brainstorming` flow for that child
3. The child's spec + plan ship independently
4. Once both 4A and 4B specs are drafted (or both plans landed), start
   4C brainstorming
