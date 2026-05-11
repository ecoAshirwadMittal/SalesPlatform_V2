# Reserve Bids (EB) — QA vs Local Walkthrough (2026-05-08)

> ### Status update — 2026-05-11
>
> Sprints A + B from §11 are essentially complete. The dev-scaffold state described
> below is **no longer current**; the surface has been rebuilt around the reusable
> `DataGrid` component (kind-scoped comparators, column selector, portal popovers,
> 11-op backend) plus an audit modal and an upload modal that match QA's mental model.
>
> | Gap | Status | Where |
> |---|---|---|
> | RB-1 heading sizing | DONE | DataGrid migration in `a444f14`-era |
> | RB-2 unstyled top-bar buttons | DONE | top-bar uses `btn-primary-green` / `btn-outline` |
> | RB-3 `/new` route divergence | ACCEPTED | ADR in `docs/architecture/decisions.md` |
> | RB-4 / RB-20 upload-button copy | DONE | `page.tsx:140` reads "Upload EB Price" |
> | RB-5 list 500 | DONE | data loads — verified via live probe |
> | RB-6 filter row | DONE | 11-op backend (`7e9e5f4`) + kind-scoped UI |
> | RB-7 sort indicators | DONE | DataGrid handles per-column sort |
> | RB-8 column selector | DONE | DataGrid eye-icon dropdown |
> | RB-9 column header "Audit" | DONE | `page.tsx:162` `rowActionsLabel="Audit"` |
> | RB-10 "Model Name" | DONE | `page.tsx:90` `label: "Model Name"` |
> | RB-11 pagination First/Last + total | DONE | DataGrid pagination component |
> | RB-12 date filter on Last Updated | DONE | column declares `filter: { kind: "date" }` — DataGrid's date comparator drives the popover |
> | RB-13 audit data missing | DONE | `cb81b3c` — `changedByUsername` populated from `identity.users.name` |
> | RB-14 audit modal vs route | DONE | `ReserveBidAuditModal.tsx` |
> | RB-15 redundant Product/Grade cols | DONE | modal scoped via trigger — 4 cols only |
> | RB-16 "Old" / "New" | DONE | modal headers say "Old price" / "New price" |
> | RB-17 "When" vs "Changed On" | DONE | modal header says "Changed On" |
> | RB-18 upload as route vs modal | DONE | `ReserveBidUploadModal.tsx` |
> | RB-19 upload page dev scaffold | DONE | shared `ReserveBidUploadForm` used by both modal and `/upload` deep-link |
> | RB-21 / RB-22 `/new` form | DONE | `new/page.tsx` has real inputs + create call |
> | RB-23 internal PK in heading | DONE | `[id]/page.tsx:65` heading reads `Product #${productId}`, internal id demoted to a muted line |
> | RB-24 detail page non-functional | DONE | `[id]/page.tsx` has editable inputs + save |
> | RB-25 / RB-26 / RB-27 divergences | ACCEPTED | as noted in the doc |
>
> Everything below the §10 gap inventory is preserved as the historical baseline. If
> you need to re-audit the page, run the playbook fresh — the screenshots referenced
> here (`local-12-audit-page.png`, `local-11-detail-edit.png`, etc.) predate the
> DataGrid migration and no longer reflect the live surface.

**Trigger:** User flagged a "huge UX difference" on the Reserve Bids page and asked for the
[QA-vs-local audit playbook](qa-vs-local-page-audit-playbook.md) to be run against it.

**Conclusion up front (historical, 2026-05-08):** The local Reserve Bids surface was in
**dev-scaffold state** across all five routes (`/`, `/upload`, `/new`, `/[id]`, `/[id]/audit`)
— the list endpoint returned HTTP 500, every page rendered as plain unstyled text (heading
`H1 16px` vs QA's `H2 42px`, buttons computed `0px padding, transparent background`), and the
architectural model diverged from QA in three structural ways: (1) Audit was a separate route
locally but a modal in QA, (2) local exposed a `/new` manual-create route QA did not have, (3)
local collapsed 6 per-column filters + 11 comparators + date-picker into 2 plain text inputs
above the grid. This was feature-class work, not pixel polish.

---

## 1. Page architecture

| Concept                       | QA (Mendix)                                                  | Local (Next.js)                                                              |
|---                            |---                                                           |---                                                                           |
| Route shape                   | Single `index.html` with Mendix nav-state                    | 5 routes: `/`, `/upload`, `/new`, `/[id]`, `/[id]/audit`                     |
| List endpoint                 | Mendix DataGrid 2 (server-side paged + filtered)             | `GET /api/v1/admin/reserve-bids?page=0&size=20` — currently **500**          |
| Identifier shown to user      | `product_id` (e.g. `73`, `2551`, `5488`)                     | Both: heading "Edit Reserve Bid **#73**" (internal PK) + body "Product ID: **7128**" (the Mendix product_id) |
| Audit history surface         | Modal dialog overlaying the list                             | Separate route `/admin/auctions-data-center/reserve-bids/[id]/audit`         |
| Manual-create flow            | Not present — Excel upload only                              | Present — `/new` route with 5-field scaffold form                            |
| Upload flow                   | Modal dialog: Browse + Upload                                | Separate route `/upload` with same 2 controls (Choose File / Upload / Back)  |
| Filter row                    | Per-column inside grid header (6 columns filterable)         | 2 plain text inputs above grid (`productId`, `grade contains`)               |
| Filter comparators            | 8 numeric (`>`, `≥`, `=`, `≠`, `<`, `≤`, Empty, Not empty), 11 text (Contains, Starts with, Ends with, +numeric) | None — substring match only                                                  |
| Date filter                   | Calendar popover on `Last Updated`                           | None                                                                         |
| Sort                          | Bidirectional triangle on every column header                | None — column headers are static text                                        |
| Column visibility selector    | Eye-icon toggle, 6 columns togglable                         | None                                                                         |
| Pagination                    | First / Prev / "1 to 20 of 14659" / Next / Last              | "Prev / Page 1 of 1 / Next" (both disabled because list is empty)            |
| Brand identity                | White sidebar background, dark teal `#00787F` primary        | Green gradient sidebar, primary missing on this page (no styled buttons)     |

The two surfaces solve the same problem (CRUD over the EB reserve-price catalog) with
different mental models. **QA treats Reserve Bids as a single-page table with rich in-grid
filtering**; **local treats it as a route hierarchy with separate pages for edit, audit,
upload, and create**. Both can work — but the local rebuild has not yet implemented the
in-grid table affordances QA users rely on, and the routes that exist locally are not
styled.

---

## 2. Top bar / page header

### QA — `qa-01-list.png`

- H2 heading "Reserve Bids (EB)" — Brandon Grotesque, 42px, weight 500, line-height 54.6px
- Two buttons right-aligned below the underlined heading:
  - **Download** (secondary, outlined): width 130px, height 44px, padding `10.8px 18px`, font 18px, bg `#F7F7F7`, border `1px solid #D0D0D0`, radius 3px, leading download icon
  - **Upload EB Price** (primary, filled teal): width 176px, height 44px, padding `10.8px 18px`, font 18px, bg `#00787F`, white text, leading upload icon
- Heading sits flush-left of the content area, with a 1px horizontal divider below it

### Local — `local-01-list.png`

- H1 heading "Reserve Bids (EB)" — Brandon Grotesque, 16px, weight 400, line-height 24px
  - Same font family, but **same size as body text** (no visual hierarchy)
- Three controls right-aligned (split across two columns visually):
  - "Upload Excel" — anchor wrapping a button, computed `0px padding`, no border, no background, font 16px (renders as plain text)
  - "Download Excel" — `<button>` with same null-styling
  - "New" — anchor wrapping a button, same null-styling
- "Internal server error" alert in red below the heading row (because the list `GET` is
  500ing — see §4)

### Severity

| ID    | Item                                  | Severity | Notes |
|---    |---                                    |---       |---    |
| RB-1  | Heading `H1 16px` vs QA `H2 42px`     | HIGH     | No visual page hierarchy locally; the page heading is indistinguishable from grid header text |
| RB-2  | Top-bar buttons render as plain text  | HIGH     | Computed styles confirm `padding: 0px`, `border: 0px`, `background: transparent` on all three buttons |
| RB-3  | Local exposes 3 actions vs QA's 2     | RESOLVED | The `/new` manual-create route does not exist in QA. Decision 2026-05-08: keep as accepted divergence — see ADR `docs/architecture/decisions.md` "Reserve Bids: intentional divergences from QA Mendix" decision 4. |
| RB-4  | Button copy drift                     | MEDIUM   | "Upload Excel" / "Download Excel" / "New" vs QA "Upload EB Price" / "Download". The QA copy is more specific to the business object |

---

## 3. Data grid

### QA — `qa-01-list.png`, `qa-02-sort-product-id-asc.png`, `qa-09-pagination-page-2.png`

- 8 columns: Product ID | Grade | Brand | Model Name | Bid | Last Updated | Audit | (column-selector)
- Numeric columns (Product ID, Bid) are right-aligned
- Header row is **two-line tall (73.84px)**: column label + filter cell underneath
- Each header carries a sort triangle button and a per-column filter
- Audit cell renders as a clickable eye icon
- "Last Updated" column shows formatted timestamps, e.g. `12/09/25 at 01:17 PM EST`
- Header `bg #F7F7F7`, padding `6px 6px 6px 15px`, font 16px (Brandon Grotesque)
- Body cell padding `6px 19px` (font 16px)
- Pagination footer: `Currently showing 1 to 20 of 14659` flanked by First / Prev / Next / Last icon buttons

### Local — `local-01-list.png`

- 7 columns: Product ID | Grade | Brand | Model | Bid | Last Updated | Actions
  - "Model" not "Model Name"; "Actions" not "Audit"
- Static `<th>` text — no sort, no per-column filter, no comparator
- Single header row (24px tall), header text font-weight 700
- Table grid container has 0 background, 0 border, 0 box-shadow
- 0 rows (because list endpoint is 500ing)
- Pagination footer: `Prev / Page 1 of 1 / Next` plain text with disabled buttons

### Severity

| ID    | Item                                                       | Severity | Notes |
|---    |---                                                         |---       |---    |
| RB-5  | List endpoint `GET /api/v1/admin/reserve-bids` returns 500 | CRITICAL | The page is unusable for SalesOps until this is fixed. Backend log capture required. |
| RB-6  | No per-column filter row                                   | HIGH     | QA users can filter by Product ID range, Grade contains, Brand starts with, Bid ≥ X, Last Updated ≥ date — local supports only `productId` substring + `grade contains` from the inputs above the table |
| RB-7  | No sort affordance on column headers                       | HIGH     | QA supports asc/desc sort on every column |
| RB-8  | No column-selector eye icon                                | MEDIUM   | QA users can toggle the 6 data columns on/off |
| RB-9  | "Audit" → "Actions" terminology drift                      | MEDIUM   | The legacy column header is "Audit"; renaming it to "Actions" hides the action's meaning behind a generic word |
| RB-10 | "Model Name" → "Model" copy drift                          | LOW      | Mendix uses "Model Name" — keep parity to avoid retraining users |
| RB-11 | Pagination missing First / Last buttons                    | MEDIUM   | QA has 4 nav buttons + total count; local has only Prev / Next + computed page count |

---

## 4. Filter affordances

### QA filter detail — `qa-03-bid-comparator-menu.png`, `qa-04-grade-comparator-menu.png`, `qa-05-last-updated-calendar.png`

- **Numeric comparator** (Product ID, Bid): 8 options
  - Greater than | Greater than or equal | Equal (default) | Not equal | Smaller than | Smaller than or equal | Empty | Not empty
- **Text comparator** (Grade, Brand, Model Name, Changed By): 11 options
  - Contains (default) | Starts with | Ends with | Greater than | Greater than or equal | Equal | Not equal | Smaller than | Smaller than or equal | Empty | Not empty
- **Date comparator** (Last Updated, Changed On): same 8 numeric ops, plus a calendar popover
  - Calendar header: month dropdown + year dropdown
  - **Week starts on Thursday** (Th, Fr, Sa, Su, Mo, Tu, We) — Mendix DataGrid 2 default; this is unusual and may be a UX gap of its own to flag in the spec
- Comparator button is a 37.25 × 30 px square with the operator symbol in `datagrid-filters` icon font (13px), border-radius `3px 0px 0px 3px` so it sits flush-left of the input
- Filter input is 121.75 × 30 px, border `#D0D0D0`, font 18px

### Local filter detail — `local-01-list.png`

- 2 unstyled `<input>` elements above the table:
  - `Filter productId...`
  - `Filter grade contains...`
- Computed: `0px padding, 0px border, transparent bg, font 16px` — invisible borders
- No comparator menu, no date picker, no per-column placement

### Severity

Already covered by RB-6 (HIGH). One sub-finding worth its own line:

| ID    | Item                                              | Severity | Notes |
|---    |---                                                |---       |---    |
| RB-12 | No date filter on Last Updated                    | HIGH     | "Last Updated since X" is the most common SalesOps query when triaging which products had price drift this week |

---

## 5. Audit history

### QA — `qa-07-audit-history.png`

- Click eye icon on any row → **modal dialog overlays the list**
- Dialog title "Audit"
- 5 columns: Old price | New price | Changed On | Changed By | (column-selector)
- Numeric columns sortable; date column has its own filter+calendar; text column has comparator
- ~15 entries visible for product 73, with prices ranging 10.23 → 0 → 1000 → 222.99 → … → 888.79 and changed-by emails alternating between two users
- Close via `×` button top-right of dialog

### Local — `local-12-audit-page.png`

- Separate route: `/admin/auctions-data-center/reserve-bids/[id]/audit`
- H1 heading "Audit Trail — Reserve Bid #73"
- 6 columns: Product | Grade | Old | New | Changed By | When
- Empty rowgroup (no data — likely because backend audit endpoint is unimplemented or
  returning 0 rows)
- No filter / sort / pagination

### Severity

| ID    | Item                                                | Severity | Notes |
|---    |---                                                  |---       |---    |
| RB-13 | Audit data missing — empty rowgroup                 | CRITICAL | QA has 15+ rows for the same product; local route renders, but data layer returns nothing. Backend or wiring gap. |
| RB-14 | Audit modeled as a route, not a modal               | HIGH     | Architectural divergence from QA. Either is defensible, but if pixel-parity is the goal, QA's modal is what users are trained on. |
| RB-15 | Adds redundant Product/Grade columns                | LOW      | When scoped to one product, "Product" and "Grade" columns repeat the same value on every row — QA's dialog is scoped via the trigger and doesn't carry these |
| RB-16 | "Old price"/"New price" → "Old"/"New"               | LOW      | QA has explicit "price" suffix; local omits it |
| RB-17 | "Changed On" → "When" copy drift                    | LOW      | "When" is colloquial; QA's "Changed On" is more formal-spec |

---

## 6. Upload flow

### QA — `qa-08-upload-modal.png`

- Click "Upload EB Price" (top-bar primary button) → **modal dialog "Upload File"**
- Modal body: read-only text input (`...` placeholder for selected filename) + "Browse..." button + "Upload" button (primary teal)
- No template-download link, no preview grid, no validation panel — Mendix file-manager primitive
- Close via `×` top-right

### Local — `local-08-upload-page.png`

- Click "Upload Excel" (top-bar text link) → **navigates to `/upload` route** (full-page change, not modal)
- Page body: heading "Upload Reserve Bids" + native `<input type="file">` ("Choose File / No file chosen") + "Upload" + "Back" buttons rendered as inline plain text
- No styling, no validation, no preview

### Severity

| ID    | Item                                            | Severity | Notes |
|---    |---                                              |---       |---    |
| RB-18 | Upload modeled as full route, not modal         | MEDIUM   | Same architectural pattern as RB-14. Modal feels lighter for an upload flow that should not lose grid context. |
| RB-19 | Upload page is a dev scaffold                   | HIGH     | No Excel template download link, no preview of the rows about to be inserted, no error reporting after upload |
| RB-20 | "Upload Excel" copy vs "Upload EB Price"        | MEDIUM   | The QA copy describes the business action; the local copy describes the file format |

---

## 7. New / manual create

### QA

- No equivalent. Reserve bids are exclusively created via Excel upload.

### Local — `local-10-new-page.png`

- Route `/admin/auctions-data-center/reserve-bids/new`
- Heading "New Reserve Bid"
- 5 plain-text labels: `Product ID:`, `Grade:`, `Brand:`, `Model:`, `Bid:`
- "Create" + "Cancel" rendered as inline plain text
- No form inputs visible at the captured resolution (or inputs collapsed to 0 width by the
  same null-styling problem affecting buttons)

### Severity

| ID    | Item                                  | Severity | Notes |
|---    |---                                    |---       |---    |
| RB-21 | `/new` route exists and routes from sidebar | RESOLVED | Decision 2026-05-08: kept as accepted divergence — same ADR as RB-3 (intentional-divergences ADR, decision 4). |
| RB-22 | Form inputs not rendered / 0-width    | CRITICAL | Page is non-functional. If kept, must be styled. |

---

## 8. Detail / edit

### QA

- No detail page. The list grid is the editing surface (inline edit was not in scope per
  what was visible during the QA walk; if QA supports double-click-to-edit it was not
  exercised).

### Local — `local-11-detail-edit.png`

- Route `/admin/auctions-data-center/reserve-bids/[id]`
- Heading "Edit Reserve Bid #73" — note "#73" is the **internal PK**, not the product_id
- 5 read-only text rows: `Product ID: 7128`, `Grade: A_YYY`, `Brand: Apple`, `Model: iPad 4`, `Bid: 4.09`
- "Save" + "Cancel" rendered as inline plain text — clearly not editable

### Severity

| ID    | Item                                                      | Severity | Notes |
|---    |---                                                        |---       |---    |
| RB-23 | Internal PK `#73` exposed in heading                      | MEDIUM   | Confuses users who know the catalog by `product_id` (7128) — this is the same gap pattern flagged in the [PO walkthrough](qa-vs-local-po-walkthrough-2026-05-08.md) re "Week from id 557" |
| RB-24 | Detail page is non-functional (no editable inputs)        | HIGH     | Either implement edit-in-place on the list grid (matches QA mental model) or build a real form here |

---

## 9. Sidebar / nav

### QA — `qa-01-list.png`

- Left sidebar 232px wide, white items on a dark green background gradient
- 12 menu items (top to bottom): Users, Buyers, Inventory, Purchase Order, **Reserved Bids (EB)**, Auction Scheduling, Bid as Bidder, Auction, Reports (with chevron submenu), Settings, Admin, Buyer User Guide
- Active item shows `rgba(0,0,0,0.3)` overlay
- Each item is 64px tall with 8px 0 padding

### Local — `local-01-list.png`

- Left sidebar 232px wide, similar green gradient, similar item heights
- 12 menu items but **Admin is a dropdown** that expands to 4 sub-items (Application Control Center / Auction Control Center / **Auctions Data Center** / PWS Data Center)
- "Reserved Bids (EB)" lives under the top-level "Reserved Bids (EB)" item — same as QA, not under the Admin submenu
- Top header bar shows "Admin User" + AU avatar (button "User menu") — QA shows just a green status indicator with no user identity surface

### Severity

| ID    | Item                                                           | Severity | Notes |
|---    |---                                                             |---       |---    |
| RB-25 | Admin sub-nav exists locally but not in QA                     | LOW      | Defensible if the Admin Control Center pages don't exist in QA. Worth an ADR. |
| RB-26 | Local exposes user identity in top bar                         | LOW      | An improvement over QA. Keep as intentional divergence. |
| RB-27 | Page is reachable from "Auctions Data Center" submenu but breadcrumb does not show that | LOW | No breadcrumb on either app. Consider adding one to local for the long route paths. |

---

## 10. Gap inventory (consolidated, sorted by severity)

**CRITICAL**
- **RB-5** List endpoint returns HTTP 500 — page is unusable
- **RB-13** Audit data missing — empty grid even with seeded test data
- **RB-22** `/new` form inputs not rendered (0-width)

**HIGH**
- **RB-1** Heading 16px vs 42px — no visual page hierarchy
- **RB-2** Top-bar buttons render as plain text (zero padding/border/bg)
- ~~**RB-3** `/new` route exists locally; QA has no manual create~~ — **RESOLVED 2026-05-08:** kept as accepted divergence; see ADR `docs/architecture/decisions.md` "Reserve Bids: intentional divergences" decision 4
- **RB-6** Filter row reduced from per-column + comparators to 2 plain inputs
- **RB-7** No sort indicators on column headers
- **RB-12** No date filter on Last Updated
- **RB-14** Audit modeled as route, not modal
- **RB-19** Upload page is a dev scaffold
- **RB-24** Detail page non-functional

**MEDIUM**
- **RB-4** Button copy: "Upload Excel" vs "Upload EB Price"
- **RB-8** Column-selector missing
- **RB-9** "Audit" column → "Actions"
- **RB-11** Pagination missing First/Last + total
- **RB-18** Upload as full page vs modal
- **RB-20** "Upload Excel" copy
- **RB-23** Internal PK `#73` shown in heading

**LOW**
- **RB-10** "Model Name" → "Model"
- **RB-15** Audit modal Product/Grade columns redundant
- **RB-16** "Old price" → "Old"
- **RB-17** "Changed On" → "When"
- **RB-25** Admin sub-nav divergence
- **RB-26** Top-bar user identity (intentional divergence)
- **RB-27** No breadcrumb

---

## 11. Suggested implementation plan

This is feature-class work. Realistic shape: **2 sprints**.

### Sprint A — Get the surface to "minimum viable QA parity" (1 sprint, ~5 days)

Deliverable: list page renders 14k+ rows, can be filtered/sorted/paginated, audit modal works, Excel upload functional.

| # | Task                                                                              | Est. (days) | Owns gaps   |
|---|---                                                                                |---          |---          |
| 1 | Diagnose + fix `GET /api/v1/admin/reserve-bids` 500                               | 0.5         | RB-5        |
| 2 | Fix audit endpoint wiring so per-product history returns rows                     | 0.5         | RB-13       |
| 3 | Replace dev-scaffold list page with TanStack Table or AG Grid skin                | 1.5         | RB-1, RB-2, RB-6, RB-7, RB-8, RB-9, RB-11, RB-12 |
| 4 | Convert Audit from `/[id]/audit` route to a modal triggered from row eye icon     | 0.5         | RB-14, RB-15, RB-16, RB-17 |
| 5 | Convert Upload from `/upload` route to a modal triggered from top-bar button      | 0.5         | RB-18, RB-19 |
| 6 | ~~Remove or ADR-justify the `/new` route~~ — **DONE 2026-05-08** (kept; see ADR)   | —           | RB-3, RB-21, RB-22 |
| 7 | Remove inline edit page (or wire it as a real form)                               | 0.5         | RB-23, RB-24 |

### Sprint B — Pixel parity polish (0.5 sprint, ~2 days)

Deliverable: tokens, button styles, filter/comparator menus, calendar popover match the
[styling spec](qa-vs-local-reserve-bids-styling-spec-2026-05-08.md).

| # | Task                                                                              | Est. (days) | Owns gaps   |
|---|---                                                                                |---          |---          |
| 1 | Apply tokens (color, type, spacing) from styling spec §1                          | 0.25        | All visual  |
| 2 | Skin top-bar buttons (primary teal `#00787F`, secondary outlined `#D0D0D0`)       | 0.5         | RB-2        |
| 3 | Build filter input + comparator combobox component (8 numeric / 11 text options)  | 1.0         | RB-6        |
| 4 | Build date-comparator with calendar popover (Mendix-style Th-We weekday header is intentional or not? — decide here) | 0.5 | RB-12 |
| 5 | Build column-selector eye-icon dropdown                                           | 0.5         | RB-8        |
| 6 | Style pagination footer with First/Prev/total/Next/Last + count format            | 0.25        | RB-11       |

### Out of scope (defer)

- Sidebar redesign (QA's sidebar look is distinctive but local's green gradient is intentional per CLAUDE.md token brief — keep)
- User identity in top bar (intentional divergence, keep)
- Breadcrumbs (not present in QA — defer)

---

## 12. Screenshots index

| File                                              | What it shows |
|---                                                |---            |
| `qa-01-list.png`                                  | QA Reserve Bids list — baseline, 14,659 total rows, 6 data columns + Audit + column-selector |
| `qa-02-sort-product-id-asc.png`                   | After clicking Product ID column header — rows already in default ascending order |
| `qa-03-bid-comparator-menu.png`                   | 8-option numeric comparator opened on Bid filter |
| `qa-04-grade-comparator-menu.png`                 | 11-option text comparator opened on Grade filter |
| `qa-05-last-updated-calendar.png`                 | Calendar popover on Last Updated date filter (Th-We weekday header is a Mendix DG2 default) |
| `qa-06-column-selector.png`                       | Eye-icon column-visibility dropdown (6 toggleable: Product ID / Grade / Brand / Model Name / Bid / Last Updated) |
| `qa-07-audit-history.png`                         | "Audit" modal triggered from row eye icon — 4-column grid (Old price, New price, Changed On, Changed By), ~15 rows for product 73 |
| `qa-08-upload-modal.png`                          | "Upload File" modal — file input + Browse + Upload button |
| `qa-09-pagination-page-2.png`                     | After clicking Next — pagination footer reads "21 to 40 of 14659" |
| `local-01-list.png`                               | Local Reserve Bids list — "Internal server error" alert, 0 rows, unstyled buttons |
| `local-08-upload-page.png`                        | Local `/upload` route — dev-scaffold file picker |
| `local-10-new-page.png`                           | Local `/new` route — 5 plain-text labels with no visible inputs |
| `local-11-detail-edit.png`                        | Local `/[id]` route — read-only "Edit Reserve Bid #73" page, internal PK in heading |
| `local-12-audit-page.png`                         | Local `/[id]/audit` route — empty 6-column grid |
| `qa-styles.json`                                  | `getComputedStyle()` capture for QA — body, heading, buttons, grid, filter, comparator, pagination, sidebar |
| `local-styles.json`                               | Same capture for local — confirms unstyled state |

---

## 13. References

- [QA-vs-Local Page Audit Playbook](qa-vs-local-page-audit-playbook.md) — methodology
- [PO walkthrough exemplar](qa-vs-local-po-walkthrough-2026-05-08.md) — same shape, different page
- [Reserve Bids styling spec](qa-vs-local-reserve-bids-styling-spec-2026-05-08.md) — pixel-parity tokens
- [EB module data model](../architecture/data-model.md#auctionsreserve_bid--reserve_bid_audit--reserve_bid_sync)
- [EB module overview](../app-metadata/modules.md#exchange-bid-eb)
