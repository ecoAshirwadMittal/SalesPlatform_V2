# Purchase Order — QA vs Local Walkthrough (2026-05-08)

> ### Status update — 2026-05-11
>
> Sprint A from §10 has shipped: the landing page now defaults to a PO picker
> (week-range combobox), the editor is a real DataGrid via the shared
> `PurchaseOrderEditor` component, and the new-PO + edit-PO pages have real
> inputs and persist. The `Week range = UNKNOWN` data bug is fixed at the
> service layer.
>
> | Gap | Status | Where |
> |---|---|---|
> | PO-1 Edit page dev scaffold | DONE | `[id]/page.tsx` is now a 54-line shell over `<PurchaseOrderEditor>` |
> | PO-2 New PO page dev scaffold | DONE | `new/page.tsx` rebuilt (247 lines, real inputs) |
> | PO-3 no filter row | DONE | DataGrid migration (`7e9e5f4` 11-op + `c397b45` PO scoping) |
> | PO-4 week-range selector + Create modal idiom | DONE | `page.tsx:127-150` PO picker combobox |
> | PO-7 `Week range = UNKNOWN` on every list row | DONE | `PurchaseOrderService.toRow()` derives label from joined `mdm.week` entities; stored column ignored. Live probe confirms `"2025 / Wk15 - 2025 / Wk15"`, `"2026 / Wk01 - 2026 / Wk08"`, etc. |
> | PO-8 comparator surface limited | DONE | 11-op backend + kind-scoped UI |
> | PO-9 column visibility selector | DONE | DataGrid eye-icon dropdown |
> | PO-12 title pluralization | DONE | `page.tsx:92` heading reads "Purchase Order" (singular) |
> | PO-13 pagination run-on string | DONE | DataGrid pagination component |
> | **PO-5** top-bar Export / Refresh Packout / Import | **OPEN** | not built — needs port of Mendix `RefreshPackout` microflow + Export endpoint surfacing |
> | **PO-6** `PriceFulfilled` / `QtyFulfilled` columns | **OPEN** | DTO + grid column not yet added; depends on PO-5 backend work |
> | **PO-10** "Fulfilled as of" timestamp indicator | **OPEN** | `po_refresh_timestamp` is present on the API row; surface it in the UI |
> | PO-11 browser tab title generic | DONE | `purchase-orders/layout.tsx` server-component sets `metadata.title = "Purchase Order — ecoATM Sales Platform"`; verified via live browser tab |
>
> The bulk of §1-§8 below is preserved as historical baseline — screenshots
> (`po/local-01-list.png`, `po/local-02-edit-po.png`, `po/local-03-new-po.png`)
> predate the PO rebuild and no longer reflect the live surface.

**Trigger:** User flagged "huge difference in UX" on the Purchase Order page. Walkthrough captures every clickable element on QA and the local equivalent (or absence).

**Conclusion up front (historical, 2026-05-08):** The two implementations expressed **fundamentally different mental models**. QA's PO page is a single line-item editor scoped by week-range with a polished MxDataGrid 2 grid; local's was a list-of-PO-headers UX with a dev-scaffold Edit page missing essentially every grid affordance QA shipped. Closing this gap was feature-class work, not polish.

---

## 1. Page architecture (CRITICAL — different data model exposed to user)

| Concept | QA | Local |
|---|---|---|
| Landing page | Single grid showing PO line items aggregated for a selected week range | Table of PO HEADER rows (id / week range / state / total rows / last refresh / actions) |
| PO entity exposed in URL | No — week range IS the key | Yes — `/purchase-orders/[id]` |
| Browser tab title | "ecoATM Direct - Create PO" | "ecoATM Sales Platform" |
| Page heading | "Purchase Order" (singular) | "Purchase Orders" (plural) |
| User mental model | "Show me PO commitments for week N — add/edit/delete rows" | "Show me my PO records — open one to manage its rows" |

**Takeaway:** QA hides the PO header concept entirely; the line-item grid IS the PO. Local exposes the header as a record with state/timestamps. Neither is wrong, but every downstream UX delta ladders up from this divergence.

Screenshots: `po/qa-01-list.png` vs `po/local-01-list.png`.

---

## 2. Top bar / page header

### QA
- **"Purchase Order"** title
- **Week range selector** — combobox showing e.g. "2026/21-2026/27" with:
  - Inline X (clear)
  - Right-edge ▾ chevron (opens dropdown of week ranges)
- **▼ chevron button** *next to the selector* — opens "Create/Update Purchase Order" modal (xlsx upload)
- Right-aligned action cluster:
  - **Export** (down-arrow icon + label)
  - **Refresh Packout**
  - **Import** (up-arrow icon + label)
- Below the action row: small **"Fulfilled as of: EST"** indicator (presumably fulfilled-quantity sync timestamp; mostly empty here because no week was selected)

### Local
- **"Purchase Orders"** title (lowercase weight, no header treatment)
- **No week range selector** — list is global
- Right-aligned: single **"+ New PO"** button (dark teal, navigates to `/purchase-orders/new`)
- No Export, no Refresh Packout, no Import on the list page
- No "Fulfilled as of" indicator anywhere

### Severity

| Item | Severity | Notes |
|---|---|---|
| Week-range selector + Create modal entry point | **HIGH** | QA's primary navigation idiom is missing |
| Export / Import / Refresh Packout buttons | **HIGH** | Power-user actions absent from list page |
| "Fulfilled as of" indicator | MEDIUM | Operational context — packout sync staleness signal |

Screenshots: `po/qa-03-week-chevron-clicked.png` (Create modal), `po/qa-06-refresh-packout.png` (Refresh Packout button location), `po/local-01-list.png`.

---

## 3. Data grid (line items)

### QA columns (8)
ProductID · Grade · ModelName · BuyerCode · Price · QtyCap · PriceFulfilled · QtyFullfiled *(typo preserved from Mendix)*

Each column has:
- Sort up/down arrows in the header (toggle ascending/descending)
- A filter row with a **comparator combobox + textbox**

### Local columns (Edit page only — list page shows no line items)
Product · Grade · Model · Price · QtyCap · Buyer

- No sort
- No filter row
- No pagination — all 33 rows rendered raw
- No edit-in-place affordance

### Comparator surface (gap H4 was supposed to close this)

| Column type | QA options | Local (post H4) |
|---|---|---|
| **Numeric** (ProductID, Price, QtyCap, etc.) | 8 — Greater than · Greater or equal · Equal · Not equal · Smaller than · Smaller or equal · Empty · Not empty | 1 — Equal-only (no operator selector at all) |
| **Text** (Grade, ModelName, BuyerCode) | 11 — Contains · Starts with · Ends with · Greater than · Greater or equal · Equal · Not equal · Smaller than · Smaller or equal · Empty · Not empty | 2 — Contains \| Equals |

H4 closed the comparator gap on the **Inventory** page only. The PO list/edit pages don't even have a filter row.

### Severity

| Item | Severity |
|---|---|
| PO grid missing entirely on list page (only on Edit page, and that's a dev scaffold) | **CRITICAL** |
| Sort headers absent | HIGH |
| Filter row absent on PO grid | HIGH |
| Comparator parity (8/11 options vs 0/2) | HIGH |
| `PriceFulfilled` / `QtyFullfiled` columns absent (these track packout fulfillment) | **HIGH** |
| Pagination absent — 33 rows OK, but breaks at 437 (PO id 1's row count) | HIGH |

Screenshots: `po/qa-02-week-dropdown-open.png` (numeric comparator: 8 options), `po/qa-04-grade-comparator.png` (text comparator: 11 options), `po/local-02-edit-po.png` (local edit scaffold).

---

## 4. Column visibility (eye icon)

### QA
Eye icon on the grid header opens a checklist of all 8 columns; admin can hide any. Column state persists at least within session.

### Local
No equivalent control on either list or Edit page.

**Severity:** MEDIUM — power-user convenience.

Screenshot: `po/qa-05-eye-clicked.png`.

---

## 5. Per-row interactions

### QA
Row click behavior was not exercised; row appears non-clickable in this view (the grid is the editor). Cell-level edit is presumably inline.

### Local list page
- Row → not clickable
- **Edit** link → `/purchase-orders/[id]` (dev scaffold)
- **Delete** link → ?? not exercised, presumably a confirm modal

### Local Edit page (`/purchase-orders/5`)
- Title: **"PO #5 — UNKNOWN"** (Week range = UNKNOWN even though the row has IDs 557 / 101)
- Bare-text labels: "Week from id 557", "Week to id 101", "Save header"
- "Details (33)" plain count
- "Upload Excel" / "Download Excel" rendered as plain text links (not buttons)
- 33 rows displayed flat — no header styling, no sort, no filter, no pagination, no edit affordance

This page is unmistakably a developer-only scaffold. Compare QA's polished grid editor in §3.

### Severity

| Item | Severity |
|---|---|
| Local Edit page is a dev scaffold (no real UX) | **CRITICAL** |
| "Week from id 557" / "Week to id 101" — raw FK ids exposed instead of week display | **HIGH** |
| "UNKNOWN" rendered for week range (data resolution issue) | **HIGH** |

---

## 6. Create / Update PO flow

### QA
**One modal** — accessed from the chevron next to the week selector:
- From Week dropdown
- To Week dropdown
- File browse with "Supported format: .xlsx" hint
- "Create Purchase Order" green pill button (centered)

Single-step. The xlsx is the source of truth for line items.

### Local
**Two-step:**
1. List page → "+ New PO" → `/purchase-orders/new` (dev scaffold: "New Purchase Order" / "Week from (mdm.week.id)" / "Week to (mdm.week.id)" / Create button — **no actual form inputs, no xlsx field**)
2. After creation → must navigate to Edit page to find Upload Excel link

### Severity

| Item | Severity |
|---|---|
| New PO page is a dev scaffold with no real inputs | **CRITICAL** |
| xlsx upload only available from Edit page (deeper drill) | HIGH |
| "Create" + "Upload" not unified into one modal | MEDIUM |

Screenshots: `po/qa-03-week-chevron-clicked.png`, `po/local-03-new-po.png`.

---

## 7. Local-only items (not in QA)

| Item | Notes |
|---|---|
| `+ New PO` button on list | Reasonable affordance but routes to a broken page (§6) |
| Per-PO `Delete` action | QA may not expose delete at all (line-items get replaced by re-uploading the xlsx) |
| `State` column (ACTIVE / CLOSED pill) | QA derives state from week-range vs today; local stores it explicitly |
| `Total rows` / `Last refresh` columns | Useful metadata; QA exposes "Fulfilled as of" instead |

Local's list view conveys some data QA doesn't, but loses everything else QA does.

---

## 8. Pagination + footer

### QA
"1 to 5 of 5" with First / Prev / Next / Last arrow buttons (4 distinct icons, properly disabled at edges).

### Local
"Showing 5 of 5.PrevNext" — text reads as one run-on string. Suggests missing whitespace/gap in the JSX template; Prev/Next aren't visually separated from the count or each other.

**Severity:** LOW — visual bug.

---

## 9. New gap inventory (additive to existing walkthrough)

**Critical (block users from doing real work):**
- **PO-1** Local Edit page is a dev scaffold — no grid, no sort, no filter, no pagination, raw FK ids displayed (`Week from id 557`)
- **PO-2** Local New PO page is a dev scaffold — labels show `(mdm.week.id)` placeholder text, no week dropdowns, no xlsx upload field
- **PO-3** PO grid line items have no filter row at all — admins cannot search/filter/sort 437-row POs

**High (visible in every QA-trained workflow):**
- **PO-4** Week-range selector + Create-PO modal entry point missing on list page
- **PO-5** Export / Refresh Packout / Import buttons absent
- **PO-6** `PriceFulfilled` / `QtyFulfilled` (packout fulfillment) columns missing — Mendix `RefreshPackout` action not ported
- **PO-7** "Week range = UNKNOWN" on every list row (data resolution path broken — ids resolve to weeks but the join produces null)
- **PO-8** Comparator surface limited (Inventory has 2 options via H4; PO grid has 0 — needs a generic implementation to share)

**Medium:**
- **PO-9** Column visibility selector (eye icon) absent
- **PO-10** "Fulfilled as of" timestamp indicator absent
- **PO-11** Browser tab title generic ("ecoATM Sales Platform") instead of page-specific ("Create PO")
- **PO-12** Title pluralization (singular vs plural) — pick one (QA: singular)

**Low:**
- **PO-13** Pagination footer renders as run-on string ("Showing 5 of 5.PrevNext")

---

## 10. Suggested implementation plan

The honest answer: **the local PO surface needs a feature-class rewrite, not a polish pass.** Bundle into a sub-project with its own design doc; do not ship piecemeal because the data model question (PO-as-header vs PO-as-week-scope) drives every other decision.

**Recommended sequencing:**

### Sprint A — Decide the data model + ship the editor
1. **Decision point:** keep local's PO-as-header model (status quo) **OR** refactor to QA's PO-as-week-scope model. Recommendation: **keep header** (id + state + audit) but make the **grid the primary surface** (default to most-recent or active PO when admin lands on the page; week-range becomes a filter-style picker that selects the underlying header). This preserves the existing PO id surface (and its V21 migration data) while matching QA's UX idiom.
2. Build the **PO grid** as a real DataGrid component:
   - 8 columns with `PriceFulfilled` + `QtyFulfilled` added to the schema
   - Sort headers (asc/desc toggle)
   - Filter row with the operator-select component (extract from H4 inventory work — generalize to support 8/11-option mode)
   - Pagination (server-side, reuse the existing PO-detail endpoint)
   - Column-visibility selector
3. Replace the dev-scaffold Edit page entirely.

### Sprint B — Ship the Create + Upload modal
4. Single modal with `From Week` / `To Week` dropdowns + xlsx file picker. Replaces both the broken `/new` page and the buried Upload-Excel link on Edit.
5. Delete `purchase-orders/new/page.tsx` once the modal lands.

### Sprint C — Action toolbar
6. Wire **Export** (xlsx download — should already exist on the backend; thread to a button)
7. Wire **Refresh Packout** — port Mendix `RefreshPackout` microflow which presumably re-pulls packout fulfillment counts from Snowflake / inventory and updates the `*Fulfilled` columns + the "Fulfilled as of" timestamp
8. **Import** — already exists via Upload Excel link; surface as a top-bar button.

### Effort
- Sprint A: 2–3 days (one full feature, mostly frontend + filter-component extraction)
- Sprint B: 1 day (modal + file upload to existing endpoint)
- Sprint C: 2 days (Refresh Packout port + Export + Import surfacing)
- **Total: ~1 week of focused work.**

### Out of scope / follow-ups
- The existing V21 PO migration's `Week range = UNKNOWN` data quality issue (PO-7) is its own backfill — likely a missing FK resolution on `mdm.week.week_display`. Independent fix.
- The existing `+ New PO` and Edit pages can stay live until Sprint A lands; mark them as "scaffold" with a banner so SalesOps know to wait.

---

## 11. Screenshots index

| File | What it shows |
|---|---|
| `po/qa-01-list.png` | QA PO grid landing — week range selected, 5 line items, action toolbar |
| `po/qa-02-week-dropdown-open.png` | Numeric column comparator (8 options) |
| `po/qa-03-week-chevron-clicked.png` | Create/Update PO modal (xlsx upload) |
| `po/qa-04-grade-comparator.png` | Text column comparator (11 options) |
| `po/qa-05-eye-clicked.png` | Column visibility selector |
| `po/qa-06-refresh-packout.png` | Refresh Packout button location (greyed without week selected) |
| `po/local-01-list.png` | Local list of PO headers — different data model |
| `po/local-02-edit-po.png` | Local PO Edit page — dev-scaffold quality |
| `po/local-03-new-po.png` | Local New PO page — placeholder text instead of inputs |
