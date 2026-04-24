# Design — Sub-project 4B: PurchaseOrder (PO) Module Complete Port

**Status:** 🟡 **Not started** — stub placeholder.
**Sub-project:** 4B of the sub-project 4 umbrella
(`docs/tasks/auction-sub4-umbrella-design.md`)
**Depends on:** none (parallel-safe with 4A).
**Blocks:** 4C (ranking + target-price recalc).
**Mendix source:** `ecoatm_po` module — `purchaseorder`, `podetail`,
junctions `purchaseorder_week_from`, `purchaseorder_week_to`,
`podetail_purchaseorder`, `podetail_buyercode`.

---

## 1. Resume here

Start a fresh Claude Code session in the repo root and run:

```
Brainstorm sub-project 4B (PO module port) per the decomposition in
docs/tasks/auction-sub4-umbrella-design.md. Use the 4A spec at
docs/tasks/auction-eb-module-design.md as a reference style (4A just
merged; 4B is parallel-safe and independent from 4C). Follow
superpowers:brainstorming → writing-plans → subagent-driven-development.
```

This fills in the rest of this document and produces the matching
`docs/tasks/auction-po-module-plan.md`.

---

## 2. Scope locked in by the umbrella

Per the umbrella doc (§2, §5), 4B covers:

- `ecoatm_po` → modern schema:
  - `auctions.purchase_order` (multi-junction `week_from` / `week_to`
    collapsed to direct FK columns, per project convention)
  - `auctions.po_detail` (+ buyer_code junction collapsed to direct FK)
- JPA entities, repositories, services
- Admin CRUD + Next.js UI (PO list, PO edit, PO-detail grid, week-range
  editor, per-row buyer-code assignment)
- Lifecycle: draft → active → closed state transitions + effective-week
  resolution (`week_from ≤ current ≤ week_to`)
- PO → Snowflake sync (mirror 4A's writer-interface + logging-default +
  `ConditionalOnProperty` JDBC impl pattern)
- Extractor script → new Flyway V-migration for bulk historical load
- Tests (unit + repository IT + controller IT + Snowflake push/pull IT +
  scheduled-job IT + Playwright E2E)
- Documentation (REST endpoints, ADR, data-model, modules,
  business-logic doc, deployment config, coverage)

**Explicitly out of scope:**
- Target-price recalc consumption (owned by 4C)
- Cross-dependency with EB (independent domains)
- PO lifecycle automation (auto-close past-dated POs) — deferred

---

## 3. Patterns to mirror from 4A

When 4B brainstorming begins, these 4A decisions are the starting
defaults — override only with explicit reason:

- **Schema namespace:** `auctions.*` (not a new `purchase_order.*`
  schema). EB lives in `auctions`; PO is symmetric.
- **Full bidirectional Snowflake sync** — push on write via
  `@TransactionalEventListener(AFTER_COMMIT)` on `snowflakeExecutor`,
  pull on schedule via `@Scheduled` + `@SchedulerLock` + echo-prevented
  delete-all-then-reinsert.
- **Writer / reader interface + 2 impls each** — `LoggingXxx` as
  `matchIfMissing = true` default, `JdbcXxx` gated by
  `po.sync.writer=jdbc` / `po.sync.reader=jdbc`.
- **No Delete-All button** — operational risk > parity value.
- **Constructor injection only** — no `@Autowired` on fields; no Lombok.
- **Flyway test isolation:** `@AfterEach` cleanup that DELETEs only the
  test-inserted rows (use sentinel product_ids that are guaranteed not
  to collide with V<n+1> loaded data).

---

## 4. Pre-start gotchas

### 4.1 Flyway V-numbers

Main currently ends at **V77** (4A's reserve_bid data load, merged
2026-04-24). The 4B brainstorm should allocate **V78** for the PO
schema and **V79** for the PO data load. Before any implementer
dispatches, run:

```bash
ls backend/src/main/resources/db/migration/ | tail -5
```

and confirm the next available number — don't assume V78+ is free if
another branch has merged between now and when 4B starts.

### 4.2 Worktree setup

4A ran in an isolated worktree at
`C:/Users/Ashirwad.Mittal/mendix-extractor/SalesPlatform_Modern-4A`.
4B should do the same. Create with:

```bash
git -C <main-repo-path> worktree add <sibling-path>-4B -b feat/sub4b-po-module
```

before starting implementation.

### 4.3 Snowflake stored proc

4A uses `AUCTIONS.UPSERT_RESERVE_BID(JSON_CONTENT, ACTING_USER)`. 4B
will presumably call `AUCTIONS.UPSERT_PURCHASE_ORDER` (or similar) —
the Mendix source `SUB_PurchaseOrderData_UpdateSnowflake.md` (or
equivalent — confirm during brainstorm's context-exploration step) is
authoritative for the exact proc name + signature.

---

## 5. Related artifacts

- Umbrella: `docs/tasks/auction-sub4-umbrella-design.md`
- 4A spec (reference style): `docs/tasks/auction-eb-module-design.md`
- 4A plan (reference style): `docs/tasks/auction-eb-module-plan.md`
- 4A merge commit on `main`: `c7e292a`
- Mendix schema ref: `migration_context/database/schema-ecoatm_po.md`
  (confirm path on resume)

---

*This stub stays here until 4B brainstorming overwrites it with the
full design. Grep `docs/tasks/` for `Not started` to find open
sub-projects.*
