# Reserve Bids (EB) — data + parity fixes (RBL-D1, RBL-D2, RBL-P1)

Implements three findings from `docs/tasks/parity/findings.md`:
RBL-D1 (row-count delta), RBL-D2 (`product_id` type), RBL-P1 (default grid order).
Migration: **V94** (`backend/src/main/resources/db/migration/V94__reserve_bid_parity_fixes.sql`).

Evidence gathered read-only against `qa-0327` (legacy) and `salesplatform_dev`
(new) on 2026-07-12.

---

## RBL-D1 — the 2 "missing" rows (14,657 new vs 14,659 legacy) — CATALOGUED, no data change

The delta is **exactly** the two duplicate `(productid, grade)` business-key pairs
that legacy carried and the new `UNIQUE(product_id, grade)` constraint legitimately
collapses. New count 14,657 == the count of **distinct** legacy `(productid, grade)`
pairs (14,659 rows − 2 collapsed duplicates). No other legacy row is missing.

Legacy duplicates (`qa-0327."ecoatm_eb$reservebid"`):

| productid | grade | legacy id | bid | lastupdatedatetime | createddate | survives in new? |
|---|---|---|---|---|---|---|
| 13038 | F_NYN/H_NNN | 109212291169864305 | 25.01 | NULL | 2025-06-24 18:00:17 | dropped |
| 13038 | F_NYN/H_NNN | 109212291169864786 | 25.01 | NULL | 2025-06-24 18:00:29 | **kept** |
| 16456 | E_YYN | 109212291170504291 | 9.78 | 2025-12-06 00:05:20 | 2025-06-24 18:00:29 | **kept** |
| 16456 | E_YYN | 109212291170504528 | 9.78 | 2025-12-03 22:16:58 | 2025-06-24 18:00:37 | dropped |

Verified in `salesplatform_dev`: only `legacy_id` `109212291169864786` and
`109212291170504291` are present (the other two legacy ids are absent).

**Which row won, and is it stale?**
- Pair `13038/F_NYN/H_NNN`: survivor `…864786` is the more-recent of the pair
  (later `createddate`; both `lastupdatedatetime` are NULL). Both rows carry the
  **same** bid (25.01).
- Pair `16456/E_YYN`: survivor `…504291` is the most-recent by `lastupdatedatetime`
  (2025-12-06 > 2025-12-03). Both rows carry the **same** bid (9.78).

In **both** pairs the two rows have an **identical bid**, so collapsing them loses
no price information, and the surviving row is the most-recent one either way.
**Therefore no V94 correction is warranted** and the dropped twins are NOT
re-inserted (they would violate the unique business key). This is a catalogued
expected delta, not a defect.

Diagnosis was via the `extract_eb_data.py`-generated seed being deduped upstream
(the V77 header + `V76MigrationTest.dataLoaded` already note "14,657 actual …
(deduped)").

---

## RBL-D2 — `product_id` VARCHAR → BIGINT

**Why:** legacy `ecoatm_eb$reservebid.productid` is `integer`; the new column was
`VARCHAR(100)`, so the admin grid sorted/filtered lexicographically
(`'1','10206','10211',…,'73'`) and `>`/`<` were string comparisons.

**Safety of the cast (verified against `salesplatform_dev`):**
`SELECT count(*) … WHERE product_id !~ '^[0-9]+$'` = **0** (every value numeric);
range `1 … 30002466` (max 8 digits — trivially within `BIGINT`). Only
`auctions.reserve_bid.product_id` is affected — `reserve_bid_audit` has **no**
`product_id` (normalised via `reserve_bid_id`); `po_detail.product_id` is a
different module (PO), deliberately VARCHAR, out of scope. No view/generated
column depends on the column (checked `pg_depend`).

### V94 does
- `ALTER TABLE auctions.reserve_bid ALTER COLUMN product_id TYPE BIGINT USING product_id::bigint`
  — the two dependent indexes (`idx_rb_product_grade` + the unique index backing
  `uq_reserve_bid_product_grade`) auto-rebuild; both plus the FK/unique
  constraints were verified present post-ALTER.
- Refreshes the column comment (the old one claimed VARCHAR-to-match-ecoid).

### Type-change ripple (kept minimal — no refactors)

**Critical join note:** the device-identity join key across the rest of the
auctions domain is VARCHAR by design — `bid_data.ecoid`,
`aggregated_inventory.ecoid2`, `po_detail.product_id` are all `VARCHAR(100)`.
`reserve_bid.product_id` is projected `AS ecoid` into the 4C recalc CTEs and
`UNION`/`USING`-joined against those VARCHAR columns. To keep those joins
byte-for-byte unchanged (a `bigint` in a `UNION`/`USING` with `varchar` errors),
`product_id` is **cast back to `::text`** at the 4 CTE projection sites.

Backend (main):
- `model/auctions/ReserveBid.java` — `productId` `String` → `Long` (drop `length`).
- `dto/ReserveBidRow.java`, `dto/ReserveBidRequest.java` (`@NotNull @Positive Long`),
  `dto/ReserveBidAuditRow.java` — `productId` → numeric.
- `repository/auctions/ReserveBidRepository.java` — `findByProductIdAndGrade`,
  `existsByProductIdAndGrade`, `findByProductIdInAndGradeIn` take `Long` / `List<Long>`.
- `service/auctions/reservebid/ReserveBidService.java` — upload flow parses the raw
  Excel cell string to `Long` (new `INVALID_PRODUCT_ID` error for a non-numeric
  cell, appended after the existing validations so the existing error semantics
  are unchanged).
- `service/auctions/reservebid/filter/FilterColumn.java` — `PRODUCT_ID` `Kind.TEXT`
  → `Kind.NUMERIC`. **Required for correctness**, not just semantics: the TEXT
  branch of the dynamic-WHERE builder wraps the column in `LOWER(...)`, which
  errors on a `bigint`. NUMERIC also gives true numeric `>`/`<` and the correct
  legacy-numeric default op (EQ).
- `repository/auctions/ReserveBidRepositoryImpl.java` — see RBL-P1.
- `repository/auctions/BidRankingRepository.java` (R2 + R3 CTEs) and
  `repository/auctions/TargetPriceRecalcRepository.java` (R1→R2 + R2→R3 `eb` CTEs)
  — `rb.product_id::text AS ecoid` (4 sites) so the `UNION`/`USING` joins to the
  VARCHAR ecoid domain stay string-typed.
- Snowflake: `ReserveBidSnowflakePushListener` sends `String.valueOf(productId)`
  (payload `Row.productId` stays `String` — the `UPSERT_RESERVE_BID` JSON contract
  is unverifiable from here, so its wire format is intentionally unchanged);
  `JdbcReserveBidSnowflakeReader` reads `rs.getLong("PRODUCT_ID")`;
  `ReserveBidExcelWriter` writes `String.valueOf(productId)` (download's ProductID
  column keeps its text format).

Frontend (`frontend/src/`):
- `lib/reserveBidTypes.ts` — `ReserveBidRow.productId`, `ReserveBidRequest.productId`,
  `ReserveBidAuditRow.productId` → `number` (`UploadError.productId` stays
  `string | null` — it's the raw echo of a possibly-bad cell).
- `reserve-bids/page.tsx` — the Product ID column filter is now `kind: "numeric"`
  (dropped the `PRODUCT_ID_OPS` text-op restriction + the now-unused `FilterOp`
  import); `auditTarget.productId` → `number`.
- `reserve-bids/ReserveBidAuditModal.tsx` — `productId` prop → `number`.
- `reserve-bids/new/page.tsx` + `[id]/page.tsx` — the text input is loaded via
  `String(r.productId)` and sent via `Number(productId)`.

---

## RBL-P1 — default grid order

Legacy default order is Mendix object/insert order (`ORDER BY id` on the legacy
table): `73, 76, 78, 79, 496, …`. The new grid defaulted to `product_id` ascending
(lexicographic, per RBL-D2).

**Decision — deviation from the task's suggested `ORDER BY id ASC`, with evidence:**
`ORDER BY id ASC` on the **new** table does NOT reproduce the legacy first screen —
V77 seeded rows in **product_id** order, so the new `BIGSERIAL id` is product_id-
ordered and `ORDER BY id` yields `1, 2, 2, 73, …`. The faithful insertion-order
proxy is the preserved **`legacy_id`** (= the Mendix id). Verified in
`salesplatform_dev`:

| order by | first 5 product_id |
|---|---|
| `id ASC` | 1, 2, 2, 73, 73 |
| `legacy_id ASC NULLS LAST, id ASC` | **73, 76, 78, 79, 496** ✅ matches legacy |

So `ReserveBidRepositoryImpl.renderOrderBy` now defaults an **unsorted** page to
`ORDER BY rb.legacy_id ASC NULLS LAST, rb.id ASC`. `NULLS LAST` keeps app-created
rows (`legacy_id` NULL — e.g. after a Snowflake pull) after the seeded set;
`id ASC` is a stable tiebreak. A client-supplied sort still wins (preserved).
Frontend `page.tsx` drops the forced `initialSort={{column:"product_id"…}}`
(`initialSort={null}`), so with no user sort the backend default applies.

---

## Tests

Updated suites: `ReserveBidServiceTest`, `ReserveBidRepositoryIT` (+ new
`searchDynamic_defaultOrder_isLegacyInsertionOrder` for RBL-P1 and
`searchDynamic_productId_numericRange_notLexicographic` for RBL-D2),
`ReserveBidControllerIT`, `ReserveBidSnowflakePushListenerTest`, `V76MigrationTest`
(+ new `productIdColumnIsBigintAfterV94`), and — **additional discovered ripple**
— the shared recalc fixture `recalc-seed.sql` plus `TargetPriceRecalcRepositoryIT`
/ `RecalcEndToEndIT`. Those recalc tests seed `reserve_bid` rows with symbolic
non-numeric ecoids (`'ECO-A'`, `'ECO-D'`) that can no longer be inserted into a
`bigint` column; the two reserve-bid-joined devices were renamed to numeric
(`ECO-A → 1001`, `ECO-D → 1004`) — all arithmetic/rankings unchanged, `ECO-B`/
`ECO-C` (no reserve_bid) stay symbolic. Frontend `reserveBid.spec.ts` (E2E) is
order-independent and unchanged.

Results (all green):
- Backend unit: `ReserveBid*Test` **20/20**; `ReserveBidControllerIT` (`@WebMvcTest`,
  no DB) **4/4**.
- Backend DB ITs (real Postgres): `V76MigrationTest` **7/7**,
  `ReserveBidRepositoryIT` **17/17**, `BidRankingRepositoryIT` **6/6**,
  `TargetPriceRecalcRepositoryIT` **8/8**, `RecalcEndToEndIT` **1/1**.
- V94 applied cleanly to the IT DB: `product_id` → `bigint`, count 14,657 unchanged,
  indexes + unique constraint intact, `ORDER BY legacy_id ASC NULLS LAST, id ASC`
  → 73,76,78,79,496.
- Frontend: `npx tsc --noEmit` — **0** errors in touched files (the 31 project
  errors are pre-existing, in unrelated partial-credit/bidder test files);
  `reserveBidClient.test.ts` vitest **2/2**.

**IT DB note:** the ITs were run against a scratch DB `parity_scratch_eb`
(created, used, then dropped). A **fresh** Flyway chain currently breaks at a
**pre-existing** defect — `V33__rma_tables.sql` fails with
`column "status_grouped_to" of relation "rma_status" does not exist` (the V29/V33
placeholder-shape collision, MIG-1). That is **not** introduced by this work and
V94 never runs on a fresh chain because V33 dies first. To validate, the scratch
DB was cloned from the already-migrated `salesplatform_dev` (read-only `pg_dump`,
V93) and V94 applied on top with Flyway disabled. `PostgresIntegrationTest`'s
no-Docker fallback was temporarily pointed at the scratch DB for the run and
reverted (not committed).

---

## Integration notes
- V94 applies to `salesplatform_dev` on the next backend restart (Flyway picks it
  up after V93). It is idempotent-safe on any DB whose `reserve_bid.product_id`
  is still VARCHAR-of-numeric-values.
- A **fresh** migration chain would apply V77 (seed) then V94 — **once the
  pre-existing V33 fresh-chain break (MIG-1) is resolved** (owned by other work;
  see the IT DB note above).

## Open questions
- **Snowflake `UPSERT_RESERVE_BID` JSON contract** — the push payload keeps
  `product_id` as a JSON **string** (`String.valueOf`) to avoid changing the
  stored-proc's expected shape, which is not verifiable from this repo. If the
  Snowflake `AUCTIONS.RESERVE_BID.PRODUCT_ID` column is numeric and the proc
  expects a number, revisit. (Dev/QA default `eb.sync.writer=logging`, so no real
  push happens today.)
- **Fresh-chain V33 (MIG-1)** — blocks a from-scratch migrate; out of this task's
  scope (V33 is owned elsewhere). Flagged so a fresh-DB deploy path is not assumed
  to work end-to-end yet.
