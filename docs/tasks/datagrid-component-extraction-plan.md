# DataGrid — extract as reusable component (plan, 2026-05-08)

**Status:** Approved 2026-05-08; Phase 1 + 2 to ship in this sprint.

**Trigger:** The Reserve Bids page now ships every QA Mendix DataGrid 2
affordance — comparator dropdowns, per-column filter row with a uniform
rhythm, column visibility selector, sortable headers, paginated footer
(First / Prev / "Showing N to M of T" / Next / Last), audit modal,
license-clean SVG glyphs, custom calendar popover. Most of that
functionality is already in `frontend/src/components/datagrid/`, but the
table chrome itself (header, body, sort triangles, column selector,
pagination footer) still lives inline in
`reserve-bids/page.tsx`. This plan extracts the chrome into a single
`<DataGrid>` component so every admin grid can adopt the same look + feel
in a few dozen lines per page.

---

## 1. Survey of grid pages

Grep finds 25 page-level files using `<table>` / `role="grid"`. Of those,
the **DataGrid 2 candidates** (server-paginated, filtered, sortable lists)
are roughly 10:

| Page | Module | Currently uses |
|---|---|---|
| `reserve-bids/page.tsx` | EB | inline grid + shared FilterCell |
| `inventory/page.tsx` | Aggregated Inventory | inline grid + shared FilterCell |
| `auctions/page.tsx` | Auction list | inline grid |
| `schedule-auction/page.tsx` | Auction Scheduling | inline grid |
| `bid-data/page.tsx` | Bid Data | inline grid |
| `round3-bid-report/page.tsx` | R3 Bid Report | inline grid |
| `pws-data-center/devices/page.tsx` | PWS devices | inline grid |
| `pws-data-center/offers/page.tsx` | PWS offers | inline grid |
| `pws-data-center/shipments/page.tsx` | PWS shipments | inline grid |
| `buyers/page.tsx` | Buyers | inline grid |
| `users/page.tsx` | Users | inline grid |

Other tables (settings/* admin tables, email-admin, error-messages,
RMA listings, etc.) are simple unpaginated tables and fall outside DG2
scope — they don't need the comparator/filter/sort affordances and
should keep their lighter inline implementations.

---

## 2. API design

Single component, generic over row type:

```tsx
<DataGrid<TRow>
  columns={ColumnDef<TRow>[]}
  fetcher={(args) => Promise<{ rows: TRow[]; total: number }>}
  rowKey={(row) => string | number}
  rowActions?={(row) => ReactNode}
  initialSort?={{ column, direction }}
  initialFilters?={Record<filterKey, ColumnFilter>}
  pageSize?={number}        // default 20
  topBarSlot?={ReactNode}    // page-specific buttons (Upload, Download, New, …)
  emptyMessage?={string}
  className?={string}
/>
```

`ColumnDef<TRow>`:

```ts
{
  key: string;                            // stable column id
  label: string;                          // visible header text
  accessor: (row: TRow) => ReactNode;    // cell renderer
  filter?: {
    kind: ColumnKind;                    // numeric / text / date
    filterKey: string;                   // wire-format param name
    availableOps?: FilterOp[];
    placeholder?: string;
  };
  sortKey?: string;                       // omit = unsortable
  numeric?: boolean;                      // right-align cells
  toggleable?: boolean;                   // show in column selector (default true)
  width?: string;                         // optional fixed width
  className?: string;                     // optional extra cell class
}
```

`fetcher` argument shape:

```ts
{
  filters: FilterStateMap;        // { [filterKey]: ColumnFilter }
  sort: { column: string; direction: 'asc' | 'desc' } | null;
  page: number;
  size: number;
  signal: AbortSignal;            // for cancelling stale requests
}
```

The component:
- Owns filter state + applied state + 400 ms debounce
- Owns sort state + click-to-toggle direction
- Owns hidden columns + Columns selector
- Owns pagination state + computes Showing N to M of T
- Calls `fetcher(args)` whenever any of the above change
- Cancels in-flight fetches via AbortSignal so rapid filter typing doesn't render stale rows
- Renders the same chrome that ships on Reserve Bids today (header divider, filter row rhythm, sort triangles, paginated footer, empty state)

---

## 3. Out-of-scope concerns (intentional)

The grid component is **presentational + state-managing only**. It does
NOT own:

- Page-level header (title + heading divider) — pages own that
- Top-bar buttons (Upload / Download / New / Refresh / etc.) — pages own those, optionally placed in a header row above the grid
- Row-level modals (audit, edit, upload) — pages own those, but `rowActions` is a slot for the row-level button group
- KPI cards (inventory has these) — pages own them above the grid
- Error / loading states wrapping the page — pages own these; the grid surfaces its own row-level loading + empty state inside the table

This split keeps the grid testable in isolation and lets each page wire
modals / KPIs / sync banners how it needs to.

---

## 4. Wire format compatibility

The grid serialises filter state via the existing `serializeFilter()`
helper from `filterModel.ts`. The result is a flat `Record<string, string>`
the host's fetcher converts into URL params. This means:

- Reserve Bids fetcher passes the flat record straight to
  `reserveBidClient.list(params)` — no translation needed.
- Inventory fetcher translates `{op, value}` to its legacy
  `gradesMode='contains'/'equals'` shape via the existing `modeForOp()`
  helper, same pattern as today.
- Future pages can adopt either: ship a backend that speaks the
  `column=op,value` wire format (matches reserve-bids) or translate
  client-side.

---

## 5. Migration sequence

| Phase | Work | Estimate | Status |
|---|---|---|---|
| 1 | Build `<DataGrid>` component + supporting types | 0.75 day | shipping this sprint |
| 2 | Migrate reserve-bids to `<DataGrid>` | 0.25 day | shipping this sprint |
| 3 | Migrate inventory | 0.25 day | follow-up sprint |
| 4 | Migrate auctions list, schedule-auction, bid-data | 0.5 day | follow-up sprint |
| 5 | Migrate round3-bid-report, buyers, users | 0.5 day | follow-up sprint |
| 6 | Migrate PWS Data Center grids (devices, offers, shipments) | 0.75 day | follow-up sprint |
| **Total** | | **~3 days across 3-4 sprints** | |

Each migration is a small, self-contained PR. Reverting one page
doesn't break others. Adoption is voluntary — pages keep their inline
grids until migrated.

---

## 6. Trade-offs

**Why not pull in TanStack Table?** Considered. Costs a 30 KB+ runtime
dependency, opinions about column virtualisation/grouping/grouping that
this admin app doesn't need, and we already have all the pieces (sort,
filter, pagination, selector) — the missing layer is a thin assembly
component. License-clean, dependency-free is the right call for this
shape of app.

**Generic over row type vs. dynamic any.** The grid takes a `<TRow>`
generic so callers get type-safe `accessor: (row: TRow) => ReactNode`.
Cost: one extra type parameter. Benefit: typos in `row.productId` fail
at compile time across ~10 pages.

**Filter wire format.** The component speaks the `{op, value}` shape.
Pages with legacy backends (inventory) translate at the fetcher
boundary. The alternative — a wire-format adapter prop on the grid —
would push the legacy concern into the shared component, and that's
the wrong direction.

**Server-side vs client-side sort/filter.** The grid is server-side
only (fetcher-driven). Client-side sort/filter would handle small
datasets nicely but doubles the surface area and forces a "small
dataset" mode that none of the DG2 candidate pages actually need.

---

## 7. References

- Existing primitives in scope: `frontend/src/components/datagrid/`
  - `ComparatorMenu.tsx`, `FilterCell.tsx`, `DatePopoverInput.tsx`
  - `filterModel.ts`, `icons/ComparatorIcons.tsx`
- Reserve Bids page (canonical implementation to extract):
  `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/page.tsx`
- Inventory page (next migration target):
  `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`
- Comparator dropdown plan (parent sprint):
  `docs/tasks/reserve-bids-comparator-dropdown-plan.md`
- Icon-font sprint plan: `docs/tasks/datagrid-filters-icon-font-plan.md`
