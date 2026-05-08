# Reserve Bids — per-column comparator dropdown (plan, 2026-05-08)

**Status:** Approved 2026-05-08 — proceed with all eight recommended decisions.

**Approved scope adjustments:**
- **Cross-page extraction is up-front** (Q2 → yes). The Comparator component
  lives in `frontend/src/components/datagrid/` from the first commit and is
  consumed by both reserve-bids and inventory in this work. Inventory's
  backend stays on its current `gradesMode`/`brandMode`/etc. wire format —
  the shared frontend component restricts inventory's `availableOps` to
  `contains`/`equals` so the visual is consistent without forcing a backend
  refactor in this sprint. Inventory backend migration to `FilterSpec` is a
  follow-up.
- **Icon-font work is split out** (Q3 → yes). See
  `docs/tasks/datagrid-filters-icon-font-plan.md` for the separate sprint
  plan. This sprint ships ASCII / unicode glyphs as a stopgap.
- **URL persistence is deferred** (Q4 → defer). FilterState lives in
  component state only.

**Trigger:** After landing the uniform filter row (commit `8e5cc21`), each
column header still renders a static comparator glyph (`=` / `Ab` / `≥`).
QA's Mendix DataGrid 2 makes that glyph a real dropdown — clicking it opens
an 8- to 11-option menu so SalesOps can pick `Greater than`, `Empty`, etc.
This plan covers what it would take to reach that level of parity.

---

## 1. QA reference (captured 2026-05-08)

Source screenshots in `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/`:

| Column        | Kind     | Default | Menu options                                                                                                | Screenshot |
|---            |---       |---      |---                                                                                                          |---         |
| Product ID    | numeric  | Equal   | Greater than · Greater than or equal · Equal · Not equal · Smaller than · Smaller than or equal · Empty · Not empty (8) | n/a (same as Bid) |
| Grade / Brand / Model Name | text | Contains | Contains · Starts with · Ends with · + the 8 numeric ops above (11)                              | `qa-04-grade-comparator-menu.png` |
| Bid           | numeric  | Equal   | Same 8 numeric ops                                                                                          | `qa-03-bid-comparator-menu.png` |
| Last Updated  | date     | Equal   | Same 8 numeric ops + a calendar popover anchored next to the input                                          | `qa-05-last-updated-calendar.png` |

The trigger button uses Mendix's `datagrid-filters` icon font (13 px,
37.25 × 30 px box, border-radius `3px 0 0 3px` so it sits flush-left of
the input). The dropdown menu is a vanilla listbox with a leading glyph
per option (`>`, `≥`, `=`, `≠`, `<`, `≤`, `=̄`, `≠̄`, `Ab`, etc.).

---

## 2. Goals & non-goals

**Goals**

- G1. Every data column carries a real comparator dropdown that mirrors QA's
  option set (8 numeric / 11 text / 8 date).
- G2. Picking an op narrows the query without a page reload (debounced,
  same flow as today).
- G3. `Empty` / `Not empty` disable the value input and submit a
  `value-less` filter.
- G4. `Last Updated` carries a calendar popover for value entry, anchored
  to the input.
- G5. Default ops match QA: `Equal` (numeric / date), `Contains` (text).

**Non-goals**

- NG1. Saved filter presets / per-user preferences. Out of scope.
- NG2. URL-encoded filter state for shareable links. Nice-to-have, defer.
- NG3. Multi-value ops like `In` / `Between`. Not in QA, skip.
- NG4. Replicating Mendix's `datagrid-filters` icon font. ASCII glyphs are
  acceptable until the icon-font sourcing question is resolved (see
  styling spec §5.5 unresolved).

---

## 3. Decision register (to confirm before coding)

| #   | Decision                                                                 | Recommendation | Rationale |
|---  |---                                                                       |---             |---        |
| D1  | Wire format: structured `column=op,value` vs explicit per-op params      | **Structured** (`productId=eq,73`) | Stable param surface as ops grow; one parser. Per-op params would mean 8 × 6 = 48 controller args. |
| D2  | Backend implementation: parse-and-build SQL string vs JPA Criteria       | **Parse-and-build, native SQL** | Keeps the existing native-SQL pattern (one query, one count query, ORDER BY enhancement works). Criteria would force a rewrite of `search()` and lose the `CAST(:param AS type) IS NULL` symmetry. |
| D3  | Whitelist enforcement                                                    | **Strong enum on backend**       | Op + column whitelisted in service-layer enum (matches the existing `SORTABLE_COLUMNS` set). Anything off the list → 400. Prevents SQL injection through op or column. |
| D4  | Frontend dropdown: native `<select>` vs custom popover                   | **Custom popover**               | QA renders ops with leading glyphs (`>`, `≥`, `Ab`, etc.) which native `<select>` can't style. Custom popover already needed for the column selector — share the wrapper styles. |
| D5  | State shape per column                                                   | `{ op: Op, value: string }`      | Single shape for every column. `value` ignored when op is `Empty`/`NotEmpty`. |
| D6  | Backwards-compat with current API                                        | **Keep both for one release**    | Old shape (`productId=73`) keeps working; new shape (`productId=eq,73`) takes precedence when both present. Lets the inventory page keep its current client. Sunset old shape on next major refactor. |
| D7  | Date comparator: single-input + popover, or text-input + calendar icon   | **Single input + calendar icon** | Matches QA exactly. The `<input type="date">` browser default we ship today is one of the LOW-priority intentional divergences flagged by the spec. |
| D8  | Whether to ship all 11 text ops or a subset                              | **All 11**                       | Already designed; trimming to 5 saves UI work but creates a "matches QA only sometimes" footgun. The 8 numeric ops on text columns are useful (e.g. `Empty` finds rows missing a Model). |

---

## 4. Backend changes

### 4.1 Filter spec

```java
public enum FilterOp {
    EQ("eq"),         // numeric, text, date
    NEQ("neq"),
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte"),
    EMPTY("empty"),       // value ignored
    NOT_EMPTY("notEmpty"),
    CONTAINS("contains"), // text only
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith");

    public static Optional<FilterOp> parse(String token) { ... }
}

public record FilterSpec(String column, FilterOp op, String value) {}
```

### 4.2 Controller

`ReserveBidController.list()` currently has 8 `@RequestParam` slots. Replace
with a single `Map<String,String> queryParams` injected by Spring, parsed
at the service boundary. Keep the legacy params (`productId`, `grade`,
`brand`, `model`, `minBid`, `maxBid`, `updatedSince`) and translate them
into `FilterSpec` instances internally so existing clients keep working
(D6).

### 4.3 Service

`ReserveBidService.search()` accepts `List<FilterSpec> filters`. It:

1. Validates each spec's `column` against a whitelist
   (`product_id, grade, brand, model, bid, last_update_datetime`).
2. Validates each spec's `op` against the column's `kind` (numeric / text /
   date — only certain ops are legal).
3. Coerces `value` per kind (numeric → BigDecimal, date → Instant from
   `YYYY-MM-DD`). Coercion errors → 400.
4. Generates a SQL fragment per spec and concatenates into the WHERE
   clause. Each fragment binds parameters by **named placeholder**
   (`:p_0`, `:p_1`, …) and uses `CAST(:p_n AS <type>)` to keep the existing
   bytea-inference fix.

Example fragments:

| Op           | Numeric                        | Text                                             | Date                                   |
|---           |---                             |---                                               |---                                     |
| EQ           | `c = CAST(:p AS numeric)`      | `LOWER(c) = LOWER(CAST(:p AS text))`             | `c = CAST(:p AS timestamptz)`         |
| GTE          | `c >= CAST(:p AS numeric)`     | `LOWER(c) >= LOWER(CAST(:p AS text))`            | `c >= CAST(:p AS timestamptz)`        |
| EMPTY        | `c IS NULL`                    | `c IS NULL OR c = ''`                            | `c IS NULL`                           |
| NOT_EMPTY    | `c IS NOT NULL`                | `c IS NOT NULL AND c <> ''`                      | `c IS NOT NULL`                       |
| CONTAINS     | (illegal)                      | `LOWER(c) LIKE LOWER(CONCAT('%', CAST(:p AS text), '%'))` | (illegal)                  |
| STARTS_WITH  | (illegal)                      | `LOWER(c) LIKE LOWER(CONCAT(CAST(:p AS text), '%'))`      | (illegal)                  |
| ENDS_WITH    | (illegal)                      | `LOWER(c) LIKE LOWER(CONCAT('%', CAST(:p AS text)))`      | (illegal)                  |

### 4.4 Repository

The current `@Query` annotation can't host a dynamic WHERE. Two
alternatives:

- **A — Drop the `@Query` annotation** and use `EntityManager.createNativeQuery()`
  built at runtime. Lose Spring's Pageable-driven count-query
  enhancement; build COUNT manually. ✅ Recommended.
- **B — Switch to JPA Criteria/Specifications.** More work, but cleaner.
  Defer.

### 4.5 Tests

- IT per (kind × op) — 8 numeric × 1 column + 11 text × 3 columns + 8
  date × 1 column = 49 tests, plus combinations. Realistic baseline:
  one positive + one negative IT per op (16 tests for numeric, 22 for
  text, 16 for date — too many).
- Realistic: **one IT per op covering each kind once** = 11 tests. Plus
  4 mixed-filter combinations (multi-column AND, conflicting ops, empty
  result, all-rows result).
- 400-path tests: invalid op, op-kind mismatch, unknown column,
  unparseable value.

---

## 5. Frontend changes

### 5.1 Component shape

```
reserve-bids/
├── filters/
│   ├── ComparatorMenu.tsx       (NEW) — popover listbox of ops
│   ├── filterModel.ts           (NEW) — type defs, default ops, kind→ops map
│   └── HeaderFilterCell.tsx     (NEW) — replaces inline FilterCell in page.tsx
├── page.tsx                     (refactored — owns filter state, delegates rendering)
└── reserveBidsList.module.css   (extend — popover, listbox, op-glyph styles)
```

### 5.2 State shape

```ts
type Op =
  | "eq" | "neq" | "gt" | "gte" | "lt" | "lte"
  | "empty" | "notEmpty"
  | "contains" | "startsWith" | "endsWith";

interface ColumnFilter { op: Op; value: string }

type FilterState = Record<keyof Filters, ColumnFilter>;
```

The page-level `Filters` interface collapses from 6 string fields to a
single `FilterState`. Default ops per column come from `filterModel.ts`.

### 5.3 ComparatorMenu component

- Triggered button styled like the existing comparator span (kept the
  same 38 × 28 px footprint).
- Click opens a popover anchored below-left.
- Popover renders a list of `{ op, label, glyph }` items, filtered by the
  column's `kind`.
- Selecting an op updates state and closes the menu; if the new op is
  `Empty` / `NotEmpty`, the input below dims and clears.
- Esc / outside-click closes.

### 5.4 Wire format

Single function `serialize(state: FilterState): URLSearchParams`:

- For each column with a non-default op or a non-empty value, emit a
  query param `column=op,value` (or `column=op` for the value-less ops).
- Old shape (`productId=73`) is no longer emitted.

### 5.5 Date picker

Replace `<input type="date">` with a custom calendar popover anchored to
the comparator button. Existing PO module has a date popover — check
`PurchaseOrderEditor` for a reusable component before building from
scratch.

### 5.6 Tests

- Unit: comparator menu opens, lists correct options per kind, selection
  updates state, Empty/NotEmpty disables the input.
- E2E: typing into Bid + selecting `≥` returns rows where `bid ≥ value`;
  selecting `Empty` on Brand returns rows with `brand IS NULL`.
- Visual: screenshot the open menu against `qa-03-bid-comparator-menu.png`
  for parity.

---

## 6. Implementation sequence (3 commits)

1. **Backend** — FilterOp enum, FilterSpec record, service refactor,
   repository switch to dynamic WHERE, IT regression suite (~11 tests).
2. **Frontend** — ComparatorMenu component, FilterState refactor,
   serialize() helper, page.tsx wiring. Verify visually against QA
   screenshots.
3. **Polish** — date picker upgrade, ASCII → icon font (if `datagrid-filters`
   is sourceable) or formal divergence ADR if not. Cross-page
   consistency check (does inventory want the same component? probably
   yes — extract to `frontend/src/components/datagrid/Comparator.tsx`).

---

## 7. Estimate

| Phase                               | Estimate |
|---                                  |---       |
| Backend — FilterOp + service refactor + IT  | 1.0 day  |
| Frontend — ComparatorMenu + state refactor  | 1.0 day  |
| Visual polish + cross-page extraction       | 0.5 day  |
| **Total**                                   | **~2.5 days** |

---

## 8. Risks

| Risk                                                                   | Mitigation |
|---                                                                     |---         |
| Dynamic WHERE building reintroduces SQL injection through `column` arg | Whitelist enforced before SQL string concat (D3). Only the value is parameterised; the column + op are validated against enums. |
| Op-on-wrong-kind (e.g. Contains on numeric) silently drops the filter  | Service rejects with HTTP 400 + structured error code. Frontend constrains the menu to legal ops per kind, so the only way to hit this is hand-crafted URL. |
| Inventory page wants the same component                                | Extract to `components/datagrid/` in phase 3. Acceptable to ship reserve-bids-only first and refactor later. |
| Date popover increases bundle size                                     | Check existing date-pickers in the project before adding a dep. |
| `Empty` op on bid (numeric NOT NULL column) returns nothing forever    | Acceptable — surfaces a real DB invariant. Could grey out the option client-side per column nullability. |

---

## 9. Open questions

- Q1. Should the wire format be `column=op,value` (compact, URL-friendly) or
  `?filters=[{column,op,value}]` JSON-encoded (richer)? Compact is simpler;
  JSON would let us add per-column boolean trees later. **Default: compact.**
- Q2. Does inventory want this component on the same release? If yes, do
  the extraction up front. If no, defer.
- Q3. The Mendix `datagrid-filters` icon font is the visual signature of
  every QA grid. Sourcing it would close every "filter glyph rendered as
  ASCII" gap across the admin app. Worth doing as a separate sprint.
- Q4. URL persistence — should the active filter spec live in the URL so
  shareable links work? Nice-to-have, deferred per NG2.

---

## 10. References

- QA computed-styles capture: `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-styles.json`
- QA dropdown screenshots: `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-03-bid-comparator-menu.png`, `qa-04-grade-comparator-menu.png`
- Styling spec: `docs/tasks/qa-vs-local-reserve-bids-styling-spec-2026-05-08.md` §5.5–§5.7
- Walkthrough: `docs/tasks/qa-vs-local-reserve-bids-walkthrough-2026-05-08.md` §4
- Existing reference patterns:
  - Backend FilterMode pattern: `backend/.../AggregatedInventoryController.java` (lighter, only contains/equals)
  - Backend native-SQL CAST trick: `backend/.../ReserveBidRepository.java` `search()`
  - Frontend HeaderCell pattern: `frontend/.../inventory/page.tsx` `HeaderCell` (also lighter — 2 ops only)
