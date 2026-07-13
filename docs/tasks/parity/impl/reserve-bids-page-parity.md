# Reserve-bids page → legacy parity (RBL-P2 / P3 / P4 / P5)

**Date:** 2026-07-12 · **Findings:** RBL-P2, RBL-P3, RBL-P4, RBL-P5
(`docs/tasks/parity/findings.md`) · **ADR:** `docs/architecture/decisions.md`
→ "2026-07-12 — Date/time display convention: legacy `MM/DD/YY at hh:mm A z`".

Closes the four page-level UI findings on `admin-reserve-bids-list` after the
data + shell were already parity-fixed (RBL-D1/D2/P1, SHELL-P1..P2). The page
we match is the legacy Mendix **`ReserveBid_Overview`** page
(`ai_knowledge_base_Release10/Pages_Page/ReserveBid_Overview.md`), roles
Administrator / SalesLeader / SalesOps — **not** the separate Administrator-only
`ReserveBid_Admin_Overview` (which carries Edit/Delete/New; see RBL-P3).

The **render is the spec**: every value below was measured off the legacy H1
capture `docs/tasks/parity/evidence/h1-2026-07-12/legacy-local-admin-reserve-bids-list__default.png`
(PIL band profiling) and re-verified against a throwaway `:13007` render of this
branch under the exact harness rasterization (§ Method).

---

## 1. Extracted legacy values (ground truth, 1920×1080 DPR 1)

| Element | Legacy (PIL-measured) |
|---|---|
| Data row pitch | **35px** (rows y306→1008, 20 rows; text cap y317–329) |
| Body cell font | **16px** (cap height ~12px) |
| Toolbar `Download` (secondary) | outlined, x≈[265,391] **126×43**, `#F7F7F7` bg + `#D0D0D0` border, download icon, left-aligned |
| Toolbar `Upload EB Price` (primary) | filled teal **175×43** x[397,572], teal **#009098 ≈ `--color-brand-teal-mid` #00969F**, white text, upload icon |
| Toolbar alignment | **left** (buttons start x≈265, flush with the grid) |
| Column-chooser eye | in the **last (action) column header**, x[1851,1866] (not a toolbar button) |
| Per-row action | **single eye** per row (opens the audit view); no Edit/Delete |
| Pager | right-aligned, `[First][Prev]  1 to 20 of 14659  [Next][Last]`, icon buttons, transparent (no gray footer bar); label has **no "Showing", no thousands comma** |
| Last Updated format | `12/09/25 at 02:17 PM EST` (`MM/DD/YY at hh:mm A z`) |
| Bid format | `$888.79` right-aligned |

---

## 2. Per-finding before → after

### RBL-P2 — date format (SYSTEMIC)
- **Before:** grid + audit modal used `new Date(iso).toLocaleString()` →
  `12/9/2025, 8:16:54 PM` (no zero-pad, 4-digit year, seconds, no zone).
- **After:** new shared `frontend/src/lib/format/legacyDateTime.ts`
  `formatLegacyDateTime()` → `12/09/25 at 08:16 PM EST`, applied to the grid
  **Last Updated** column and the audit modal **Changed On** column. ADR added
  so every other page adopts the same util as driven to parity.
- **Render-verified:** grid cell `"12/09/25 at 08:16 PM EST"`, audit modal
  `"12/09/25 at 08:16 PM EST"` — exact legacy pattern.
- **Note (out of scope, flagged):** the underlying **value** differs from legacy
  for some rows (product 73 = 08:16 PM new vs 02:17 PM legacy) — a data-layer
  timestamp difference, not a format one. RBL-P2 is format-only.

### RBL-P3 — per-row actions → single audit eye
- **Before:** `Edit` (link) + `Audit` (button) + `Delete` (button) text links
  under an "Audit" header column.
- **After:** a single eye-icon button per row → opens the audit modal
  (`ReserveBidAuditModal`, the modern port of the Mendix `ReserveBid_Audit_View`
  popup). The "Audit" header text is replaced by the column-chooser eye
  (RBL-P4). Edit/Delete are removed from the row.
- **KB basis:** `ReserveBid_Overview`'s trailing column is `showContentAs:
  customContent`, `hidable: no`, whose only action is
  `Page: ReserveBid_Audit_View` — **no Edit, no Delete**. Edit/Delete live on the
  separate Administrator-only `ReserveBid_Admin_Overview` (row content =
  `ReserveBid_Admin_NewEdit` + `Delete`), which the new app does not surface.
- **Decision:** removed Edit/Delete from the overview row (they were never on
  this page in legacy) but **kept the `/[id]` edit route** — it is the analog of
  `ReserveBid_Admin_NewEdit`, "edit capability legacy exposes elsewhere"; it is
  simply no longer linked from the overview row. See § Open questions.
- **Render-verified:** one eye per row; audit modal opens with columns
  Old price / New price / Changed On / Changed By (matches `ReserveBid_Audit_View`).

### RBL-P4 — toolbar
- **Before:** `[Upload EB Price] [Download] [New] [Columns]`, right-aligned.
- **After:** `[Download] [Upload EB Price]`, **left-aligned**. `Download` =
  secondary/outlined (download icon); `Upload EB Price` = primary teal
  (`--color-brand-teal-mid` = the rendered #009098) filled (upload icon), 43px
  tall / 18px label. **`New` button + the `/new` route + its test removed**
  (EB is authored via Excel upload only — closes RB-21/RB-3). The toolbar
  **`Columns` button is gone**; the column-visibility eye moved into the grid's
  action-column header (matches the legacy in-grid header eye at x[1851,1866]).
- **Render-verified:** toolbar reads `[Download] [Upload EB Price]` left-aligned;
  no `New`; the header eye toggles column visibility; `/new` 404s.

### RBL-P5 — density + pagination
- **Before:** ~44px rows (the audit-eye button drove row height), pager below
  the 1080 fold in a gray full-width footer bar with `Showing 1 to 20 of 14,657`.
- **After:** **35px rows** (16px body font, `line-height:18px` + `8px 15px` cell
  padding + 1px border; the eye button capped at ≤18px so it does not drive the
  row taller), **20 rows** fit with the pager on-screen. Pager is right-aligned,
  transparent, `1 to 20 of 14657` (no "Showing", no comma) with First/Prev/
  Next/Last icon buttons — matching legacy composition/placement.
- **Render-verified (live DOM):** `rowHeights = [35,35,…]`, `rowCount = 20`,
  `firstRowTop = 318`, `lastRowBottom = 1017`, `pagerText = "1 to 20 of 14657"`,
  pager visible bottom-right (well within 1080). (Total 14657 vs legacy 14659 is
  the accepted RBL-D1 2-row catalogued delta, not a UI issue.)

---

## 3. Architecture — opt-in `DataGrid variant`, inventory untouched

`components/datagrid/DataGrid` is shared by **reserve-bids _and_ inventory**
(`inventory/page.tsx`); PurchaseOrderEditor uses only the shared `<FilterCell>`
primitive, not `<DataGrid>`. To avoid regressing the out-of-scope inventory
grid, the legacy chrome is a single **opt-in `variant?: "app" | "legacy"`** prop
(default `"app"`). `variant="legacy"` switches, additively:
- dense 35px rows (`.gridLegacy tbody td`),
- right-aligned transparent pager + the plain `"N to M of T"` label,
- the column-visibility eye folded into the action-column header (`iconOnly`
  `ColumnSelector`) instead of a toolbar `Columns` button,
- left-aligned toolbar.

Only the reserve-bids page passes `variant="legacy"`. The default path is byte-
identical for inventory — pinned by `DataGrid.legacy.test.tsx` (a default-variant
case asserts `Showing 1 to 20 of 1,234` + a toolbar `Columns` button + an
`Actions` header still render). The shared `.filterCell`/`.comparatorButton`/
`.dateWrap`/calendar classes in `datagrid.module.css` (consumed by
PurchaseOrderEditor via `<FilterCell>`) were **not** touched — only new
`.*Legacy` classes were appended.

---

## 4. Files changed

**New**
- `frontend/src/lib/format/legacyDateTime.ts` — shared formatter (RBL-P2).
- `frontend/src/lib/format/legacyDateTime.test.ts` — 10 unit cases.
- `frontend/src/components/datagrid/DataGrid.legacy.test.tsx` — 3 RTL cases
  (legacy pager/eye-in-header + default-variant unchanged).

**Modified**
- `frontend/src/components/datagrid/DataGrid.tsx` — `variant` prop;
  `columnSelectorInHeader`; `iconOnly` ColumnSelector; legacy pager label;
  extracted `handleColumnToggle`.
- `frontend/src/components/datagrid/datagrid.module.css` — appended legacy
  classes (`.toolbarLegacy`, `.gridWrapLegacy`, `.gridLegacy*`,
  `.actionHeaderLegacy`, `.columnSelectorEye`, `.paginationLegacy`).
- `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/page.tsx`
  — legacy date formatter; single audit-eye row action; `[Download][Upload EB
  Price]` toolbar (no New); `variant="legacy"`; inline eye/download/upload icons.
- `.../reserve-bids/reserveBidsList.module.css` — replaced dead row-action link
  styles with `.rowAuditButton`/`.rowAuditIcon` + `.toolbarBtn*` (measured
  geometry).
- `.../reserve-bids/ReserveBidAuditModal.tsx` — Changed On uses
  `formatLegacyDateTime`.
- `frontend/tests/e2e/reserveBid.spec.ts` — replaced the removed "Edit link"
  flow with a toolbar-composition test + an audit-eye→modal test.
- `docs/architecture/decisions.md` — ADR (RBL-P2 date convention).
- `docs/testing/coverage.md` — this task's coverage entry.

**Deleted**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/new/page.tsx`
  (+ its `new/` dir) — the RB-21 unowned manual-create route.

**Kept (deliberately, RBL-P3):** `.../reserve-bids/[id]/page.tsx` (edit) +
`reserveBidForm.module.css` — the `ReserveBid_Admin_NewEdit` analog; unlinked
from the overview row but not deleted.

**Backend:** none (the audit modal already had its endpoint).

---

## 5. Method / verification

- **Render loop:** throwaway `next dev -p 13007` in this worktree (Next 16
  Turbopack; `:3000/:8080/:8082` untouched). Next's own `next.config.ts` rewrite
  proxies `/api/v1/*` → `:8080` server-side (no Origin header, so the backend
  CORS allow-list is bypassed). A Node script logged into `:8080` (no Origin),
  injected the `auth_token` cookie + `auth_user` localStorage into a Playwright
  context launched with the **exact** harness args (`--force-color-profile=srgb
  --disable-lcd-text --font-render-hinting=none --hide-scrollbars`; DPR 1,
  1920×1080, light, reduced-motion, en-US, America/New_York), navigated to the
  page, and measured the live DOM + screenshotted. Read-only throughout.
- **Gates:** `npx tsc --noEmit` — **31 pre-existing errors, 0 in touched files**
  (the known unrelated `admin-purchase-orders.spec.ts` /
  `wholesale-submit-bids.spec.ts` + partial-credit test files). Targeted
  `npx vitest run` (legacyDateTime + DataGrid.legacy + reserveBidClient) —
  **15/15 green**.
- **Final `reg-cli`/strict-pixel sign-off** belongs to the orchestrator's
  harness (same pinned Chromium both sides); the throwaway render confirms
  structure, density, and every extracted value at the DOM level.

---

## 6. Open questions / flags

1. **Last Updated value mismatch (data, not format).** Legacy product 73 shows
   `02:17 PM EST`; the new DB returns `08:16 PM EST` for the same row. The
   format is now legacy-exact; the differing **value** is a data-layer question
   (reserve_bid timestamps seeded differently) outside RBL-P2. Flag for a data
   pass if the harness wants that column pixel-green.
2. **`/[id]` edit route unlinked.** RBL-P3 rules "do not delete edit capability
   legacy exposes elsewhere." Legacy exposes create+edit on the separate
   `ReserveBid_Admin_Overview` (via `ReserveBid_Admin_NewEdit`). Per the two
   explicit rulings we **deleted `/new`** (create) but **kept `/[id]`** (edit),
   reachable only by direct URL. A future `ReserveBid_Admin_Overview` rebuild
   would re-home both under an Administrator-only surface — flagged, not guessed.
3. **Heading size.** Legacy heading is ~44px; the new page keeps ~28px. Not one
   of RBL-P2..P5 and not a separate open finding — left untouched (it does not
   affect the 20-rows-fit budget). Noted in case a heading-typography finding is
   opened later.
4. **Identity chip.** The throwaway render shows "Admin User / AU" top-right
   (dev account) where legacy shows a bare green dot — the SHELL-P2/BDD-P3
   identity-mask env difference, masked by the harness. Out of scope.
