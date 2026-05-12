---
Source: Figma file rYKB9vBqlJOFUuGN7GAgQS, node 213-610 ("Review Credit Requests" CANVAS)
File metadata name: "2026 Auction: Partial Credit"
Pulled on: 2026-05-11 via Figma MCP (mcp__figma__get_figma_data)
Purpose: Reference for Sprint 3 admin-review implementation (Next.js admin pages + Spring controller)
Source dump: ~/.claude/projects/.../tool-results/mcp-figma-get_figma_data-1778547264554.txt (11,526 lines)
---

# Partial Credit Sprint 3 — Figma Design Notes

This document captures every load-bearing piece of copy, layout signal, and
design token surfaced by node 213-610 ("Review Credit Requests") of the
Partial Credit Figma file. It mirrors the shape of
`partial-credit-sprint2-design-notes.md` (which documents node 173-600 "Submit
Credit Request") and is intended as a paste-ready reference for the admin
review surface. Quotes are verbatim from the Figma export.

The Sprint 3 admin surface is **smaller in screen count** than Sprint 2 (~9 frames
vs. 34) but **heavier in interaction density** because every line row in the three
reason sections carries its own per-line Action dropdown plus optional Prolog
Result + Actual Value inputs.

---

## 1. Frame Inventory

### 1.1 Top-level CANVAS

```
- id: 213:610
  name: Review Credit Requests
  type: CANVAS
```

The CANVAS holds the entire admin surface — one shared header strip + a
landing page + several detail-page variants (one per reason being expanded)
+ a per-reason-pivoted landing variant.

### 1.2 All named screen frames (`name:` at indent 8 — one per visual screen)

Verbatim names, in document order (with approximate start line numbers from
the source dump):

| # | Frame name | Approx. start line | What it shows |
|---|---|---|---|
| 1 | `Review Partial Credit Requests Landing (Empty)` | 85 | Cross-buyer landing in empty state |
| 2 | `Review Partial Credit Requests Landing (Empty)` | 732 | Same as #1 — populated table header but rows still placeholder (`Buyer Name` / `Company Name` / `XXX-XXXXXXX`). **Duplicate name** in Figma. |
| 3 | `Review Partial Credit Requests Landing (Empty)` | 1859 | Admin Review Detail — **Missing Device** request, Pending Approval, Missing-section expanded as the only visible section, Wrong/Encumbered collapsed. **Duplicate name** in Figma (mis-labelled — it's actually the review detail). |
| 4 | `Review Partial Credit Requests Landing (Empty)` | 3023 | Admin Review Detail — **Missing Device + Wrong Device** multi-reason, both sections expanded. Wrong section shows the `Latest Price` + `Action Recommendation` columns. |
| 5 | `Review Partial Credit Requests Landing (Empty)` | 5524 | Admin Review Detail — **Encumbered Device** request, Encumbered section expanded with `Prolog Result` dropdown + `Actual Value` input. The header summary panel labels are `Requested RMA` / `Approved RMA` (legacy — see §8 Anomalies). |
| 6 | `Review Partial Credit Requests Landing (Empty)` | 6473 | Admin Review Detail — **Wrong Device only**, Wrong section expanded, both rows shown (one Accept, one Decline) |
| 7 | `Review Partial Credit Requests Landing (Empty)` | 7683 | **Result: Encumbered / Result: Not Encumbered** detail with `Approve All` + `Decline All` per-section bulk buttons. Mixed-reason content. This is the "bulk action" canonical frame. |
| 8 | `Review Partial Credit Requests Landing (Empty)` | 9160 | **Missing Device Credit Requests** — reason-pivoted landing (only Missing-reason rows). Adds `Box Number`, `Shipment Damaged?`, `Shipment Status` columns. |
| 9+ | (extras under same name beyond line 11154 are layout/style definitions, not screens) | — | — |

> **Anomaly:** every screen in the admin export is named
> `Review Partial Credit Requests Landing (Empty)` regardless of whether
> it is the landing, the review detail, or a reason-pivoted landing.
> Engineering should rely on **position + content**, not on the frame
> name. The implementation plan will use the inferred names below:
>
> | Inferred name | Frame # |
> |---|---|
> | `Admin Landing — Empty` | 1 |
> | `Admin Landing — Populated` | 2 |
> | `Admin Review Detail — Missing Only` | 3 |
> | `Admin Review Detail — Missing + Wrong` | 4 |
> | `Admin Review Detail — Encumbered Only` | 5 |
> | `Admin Review Detail — Wrong Only` | 6 |
> | `Admin Review Detail — Bulk Action Variant` | 7 |
> | `Reason-Pivoted Landing — Missing` | 8 |

### 1.3 Canvas title labels (the big purple banner labels above each section)

These are written into the canvas as TEXT nodes — they're the names a
designer would call out when discussing the design:

- `Missing Device` (line 3425) — review detail when Missing is the only/active reason
- `Wrong Device` (line 4090, 6991) — review detail when Wrong is the only/active reason
- `Encumbered Device` (line 4834, 5921) — review detail when Encumbered is the only/active reason
- `Missing Devices` (line 7686, 7889, 8896) — bulk-action variant + reason-pivoted landing heading
- `Wrong Devices` (line 7903) — bulk-action variant section heading
- `Encumbered Devices` (line 7917) — bulk-action variant section heading
- `Missing Device Credit Requests` (line 9335) — reason-pivoted landing page heading
- `Result: Encumbered` (line 7946) — sub-heading for the Encumbered bulk section
- `Result: Not Encumbered` (line 8596) — sub-heading for the same section when prolog says "Not Encumbered"
- `Other Requests` (line 7700) — secondary tab on the bulk-action variant frame (allows toggling between Missing Devices + Other Requests)

### 1.4 Apparent flow

```
Admin Landing (cross-buyer)
   → click eye icon on a row
      → Admin Review Detail (header strip + 3 collapsible reason sections)
         → expand a section
            → per-line accept/decline dropdown OR
            → per-section "Approve All" / "Decline All" bulk button OR
            → per-line Prolog Result dropdown + Actual Value input (Encumbered only)
         → click "Complete Review"
            → confirmation modal (implied — see §5)
            → status flips to APPROVED or DECLINED
            → buyer email sent
            → back to landing
```

---

## 2. Admin Landing Page

### 2.1 Page heading

- Heading text: `Partial Credit Requests` (line 260, line 907)
- Style: `Display/lg` — Founders Grotesk Medium, 40px, 110% line-height,
  align left, color `#1C1B1C`
- Subtitle: none (no description string under the heading)

### 2.2 Status-counter chips (top right of heading row)

The same `OfferStatus` component instance is reused for all four counter
chips. Each is a pill (`borderRadius: 6px`) with a 2px or 1px border. The
selected (= active filter) chip has a 2px border + `Light Yellow` fill
(`#FEF48F`); the others have a 1px border + `Light Gray` fill (`#EFECE4`).
The chip text uses placeholder `{ts2}Label: {/ts2}{ts1}0{/ts1}` —
where `{ts1}` is the count run with normal weight `#1C1B1C` and
`{ts2}` is the label run.

Verbatim chip text:

| # | Label (Figma text) | Default state |
|---|---|---|
| 1 | `Pending Approval: 0` (line 287) | **Selected by default** — `Light Yellow` fill + 2px border `#B7B5B5` |
| 2 | `Approved: 0` (line 304) | Unselected — `Light Gray` fill + 1px border |
| 3 | `Declined: 0` (line 321) | Unselected — `Light Gray` fill + 1px border |
| 4 | `All: 0` (line 338) | Unselected — `Light Gray` fill + 1px border |

The counter is the **system status** count for the current logged-in
sales-ops user. The `Pending Approval` count aggregates rows in
`PENDING_APPROVAL` **and** `UNDER_REVIEW` states (because external text
for `UNDER_REVIEW` is `Pending Approval` per V89 seed data).

> **Anomaly:** the chip components carry the names `Status=Sales Review,
> State=Selected` / `Status=Buyer Acceptance` / `Status=Ordered` / `Status=Total`
> from the offer-status design system. The labels were overridden in
> Partial Credit but the component names were never renamed.

### 2.3 "Download" button (right-aligned, next to chips)

- Frame: `Frame 14606929`
- Background fill: `#14AC36` (green primary) — but **opacity 0.4** in the
  empty state (frame 1) → effectively disabled
- Background fill: `#14AC36` solid in the populated state (frame 2)
- Padding: `8px 16px`
- Icon: Font Awesome `arrow-down-to-line` + text label `Download`
- Text style: `Text/sm (strong)` — Founders Grotesk Medium 16px
- Text color: `Light Gray` (`#EFECE4`)

The Download button exports the **current filtered grid** as a CSV/XLSX —
deferred to Sprint 4 per Sprint 2 closeout decision §5.

### 2.4 Table column headers

The header row is `Frame 14606911` (empty) / `Frame 14606912` (populated).
Each column is a `Frame 14606xxx` with `mode: column` layout, `8px`
padding, and a `#A8A7A6` 0.81px bottom border. Each header cell has:
- A label text (`style_4UU9I9` = Founders Grotesk Medium 14px, color `#1C1B1C`)
- A `font-awesome-icon` instance with icon-name `arrows-up-down` (sort affordance)
- A `Button` sub-frame containing the text `Ab` (filter dropdown trigger,
  underlined Inter Medium 11px color `#534F4C`) — represents a "type
  filter / contains" search box per column

The columns, in document order:

| # | Header label | Width hint (Figma frame ID) | Notes |
|---|---|---|---|
| 1 | `Date Submitted` (line 394) | `Frame 14606698` (slightly wider, `layout_1BBLFH` filter w/ `calendar-days` icon) | Filter is a date-picker (calendar icon instead of `Ab`) |
| 2 | `Buyer` (line 456) | `Frame 14606705` | Free-text filter |
| 3 | `Company` (line 508) | `Frame 14606655` | Free-text filter |
| 4 | `Order Number` (line 560) | `Frame 14606706` | Free-text filter |
| 5 | `Request Reason` (line 612) | `Frame 14606707` | Free-text filter — though in practice should be a dropdown of {Missing, Wrong, Encumbered} |
| 6 | `Status` (line 664) | `Frame 14606684` | Free-text filter — should be dropdown of statuses |
| 7 | (eye icon column — no header label) | `Frame 14606704` (`layout_TM4B6H`, fixed `hug` width) | The per-row action column |

### 2.5 Table rows

Each row is `Frame 14606xxx` with `layout_3EBMKI` (row, stretch, no gap),
each cell being a `Frame 14606xxx` with `0.81px` border `#A8A7A6` and
`8px` padding.

Placeholder cell content (verbatim):
- Date col: `mm/dd/yy` (style `Text/sm` = Founders Grotesk Regular 16px, align center)
- Buyer col: `Buyer Name`
- Company col: `Company Name`
- Order # col: `XXX-XXXXXXX`
- Reason col: `Missing Device` / `Wrong Device` / `Encumbered Device` / `Missing Device, Wrong Device, En...` (line 1732 — verbatim, the comma-joined list is truncated to about 32 characters with `…` ellipsis)
- Status col: `Pending Approval`
- Eye icon col: Font Awesome `eye` icon (no text), color `#534F4C`

Empty state: `There are currently no Partial Credit Requests to approve`
(line 728, style `Text/base` = Founders Grotesk Regular 18px, color
`#1C1B1C`). Rendered as a single text node centered below the table header
row.

### 2.6 Eye icon click behaviour

Implied: click the eye → navigate to `/admin/.../partial-credit/{id}`
(the review detail). On first open, the request transitions
`PENDING_APPROVAL → UNDER_REVIEW`. (Mendix plan §12 / impl-plan §16.)

---

## 3. Admin Review Detail Page

### 3.1 Page heading

Two-line heading block (`Frame 14606935`):

- **Breadcrumb / back link** (`Frame 2062`): a small left-arrow icon
  (`Shape` IMAGE-SVG with `#1C1B1C` stroke, ~9px wide) followed by the
  text:
  - `All Missing Device Credit Requests` (line 1943) — when arriving from a reason-pivoted landing
  - `All Partial Credit Requests` (line 3089, 5590, 6660) — when arriving from the global landing
  - Style: `Text/base (561:2872)` — Founders Grotesk Regular 18px, color `#1C1B1C`
- **Page heading** (`Frame 14606903`):
  - `Missing Device Details` (line 1955) — when only Missing reason
  - `Request Details` (line 3101, 5602, 6672) — when multi-reason or generic
  - Style: `Display/lg` — Founders Grotesk Medium 40px, color `#1C1B1C`

### 3.2 Header strip — request metadata (top of the page)

Three groups of 3 fields each, arranged as a single horizontal row
(`Frame 14606690` / `Frame 14606897` with `layout_W0XU4P` row + 48px gap).

Each field is `Frame 14606586` with `layout_A8C2N9` (column, 8px gap),
containing:
- Label text — style `style_4XMI23` = Founders Grotesk Regular 13px,
  line-height 100%, color `#6F6F6F` (`fill_T5QEJW`)
- Value text — style `style_OM5UY1` = Founders Grotesk Regular 18px,
  color `#1C1B1C`

**Group 1** (`Frame 14606897`):
| Label | Value placeholder |
|---|---|
| `Request Date` | `mm/dd/yyyy` |
| `Buyer` | `Jonathan Wildermeyer` |
| `Company` | `Acme Inc.` |

**Group 2** (`Frame 14606898`, narrower — placed right of Group 1 with `layout_73IAQ2` and `layout_DY34H1`/`layout_NSIE6G` width-fixed children):
| Label | Value placeholder |
|---|---|
| `Order Number` | `XXX-XXXXX` |
| `Request Reason` | `Missing Device` (or comma-joined when multi-reason) |
| `Status` | `Pending Approval` |

> The `Status` field here renders the **external status text** (`Pending Approval` for both PENDING_APPROVAL and UNDER_REVIEW per V89 seed) — same convention as Sprint 2 buyer detail.

### 3.3 Summary panels — `Requested Credit` + `Total Credit`

Below the header strip, two compact 3-column tables sit side-by-side
(`Frame 14606901` with `layout_C1N665` row + 24px gap).

Each panel is `Frame 14606697` (left) / `Frame 14606900` (right), each
wrapping a label + a mini table. The mini table is `Frame 2255`
(`layout_NCN8HI` row-wrap, 6px row gap, 24px col gap, 8px vert padding,
border `1px #B7B5B5`, radius `6px`, fixed width `200`).

Headers (top row): `SKUs` / `Qty` / `Total`
- Style `style_VXWA06` — Founders Grotesk Regular 11px, line-height 100%
- Color: `#1C1B1C`

A horizontal `LINE` divider (`Line 429`) sits between header and value
rows — 0.5px stroke `#B7B5B5`.

Value row: `XX` / `XX` (or `XXX`) / `$XX` (or `$XX,XXX`)
- Style `style_PKF98T` = Founders Grotesk Medium 15.31px
- Color: `#1C1B1C`
- The `Total` value uses `style_HNMADV` (Founders Grotesk Medium 15.31px,
  align LEFT) — different alignment than SKUs/Qty

Left panel label: `Reported Missing` (line 2116) — when Missing reason
                  · `Requested Credit` (line 6833) — when other reasons
                  · `Requested RMA` (line 3262, 5763) — **only on encumbered-only frames**, legacy from when encumbered routed to RMA. **Drop this per impl-plan §18 Q8 decision — always render `Requested Credit`.**

Right panel label: `Total Credit ` (line 2189, 3335, 6906) — note the
                  trailing space in the Figma export, do not preserve
                  · `Approved RMA` (line 5836) — **same legacy label as above; drop per Q8**

### 3.4 "Complete Review" button (top right of header strip)

- Frame: `Frame 14606xxx`, `layout_3RYEM8`, `fill_HX3N5P` (`#14AC36`)
- Padding: `8px 16px`
- Border radius: `4px`
- Text: `Complete Review` (line 2262, 3408, 5909, 6979)
- Text style: `Text/sm (strong)` (Founders Grotesk Medium 16px)
- Text color: `Light Gray` (`#EFECE4`)

Clicking this opens the confirmation modal — see §5.

### 3.5 Reason section — `Missing Devices`

Each reason section is a card (`Frame 14606923`, padding `24px 32px`,
fill `#C0543R` = `#FFFFFF`, border-radius 8px, drop shadow
`0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px 0px rgba(0,0,0,0.1)`).

#### 3.5.1 Section heading bar

Heading: `Missing Device` (line 3425) — top of the card
- Style: `Display/sm` — Founders Grotesk Medium 24px
- Right-aligned chevron icon: `chevron-up` (expanded) / `chevron-down` (collapsed)

Sibling text: `Frame 14606945` — appears beside the heading. When the
section is the ONLY section visible (single-reason request) it stands
alone; on multi-reason requests it's the title of the open section.

#### 3.5.2 Table columns (per Figma frame 3, line 2279-2563)

The table header row (`Frame 14606683`, fill `#F7F5F1`) renders columns
left → right:

| # | Header label | Cell style notes |
|---|---|---|
| 1 | `Barcode` (line 2300) | `layout_3V97R5` — fixed 197px wide. Sort icon present. |
| 2 | `Brand` (line 2336) | `layout_YR1WTD` — flex |
| 3 | `Model Description` (line 2372) | `layout_8CXCC1` — flex-fill |
| 4 | `Grade` (line 2408) | `layout_YR1WTD` |
| 5 | `Box Number` (line 2444) | `layout_YR1WTD` |
| 6 | `Amount Paid` (line 2480) | `layout_EEHKZR` |
| 7 | `Ship Status` (line 2516) | `layout_YR1WTD` |
| 8 | `Action` (line 2547) | `layout_71Q92S`, **green-tinted background** `#D8E5D9` (fill_N47Q64) — sticky action column |
| 9 | `Amount to Credit` (line 2561) | `layout_71Q92S`, **green-tinted background** `#D8E5D9` |

> **Sticky styling:** The last two columns (`Action`, `Amount to Credit`)
> have a green-tinted `#D8E5D9` background to distinguish them from the
> grid-line columns. Match this in CSS — these are the columns the
> reviewer interacts with.

#### 3.5.3 Per-row content (verbatim placeholders)

From `Frame 14606714` (first row, line 2565):

| Cell | Placeholder text | Style |
|---|---|---|
| Barcode | `3020250619182236283058` (line 2580) | Text/sm |
| Brand | `Apple` (line 2594) | Text/sm |
| Model Description | `iPhone XR 64GB Other A1984/A2105/A2106/A2108` (line 2608) | Text/sm |
| Grade | `C\_YNY` (line 2622) | Text/sm. **Note** the backslash — it's a Figma escape for `C_YNY`. |
| Box Number | `XX-2458` (line 2636) | Text/sm |
| Amount Paid | `$69.50` (line 2650) | Text/sm |
| Ship Status | `Not Shipped` (line 2664) — or `Shipped` / `N/A` | Text/sm |
| Action | dropdown showing `Select` (line 2684) — placeholder when no decision yet | Text/sm |
| Amount to Credit | `$XX` (line 2703) — or `$0` when declined | Text/sm |

The Action dropdown cell is `Frame 14606682` with a chevron-down icon at
the right edge — when the reviewer accepts/declines a line, the cell
text changes to `Accept` (with green `thumbs-up` icon, line 4582/4591)
or `Decline` (with red `thumbs-down` icon, line 4755/4764). See §6
for color tokens.

Each row also has a per-row delete affordance — `xmark` icon at far
right of `Frame 14606704` — but in admin context this is **disabled**
(reviewer cannot delete lines).

### 3.6 Reason section — `Wrong Devices`

#### 3.6.1 Section heading

- `Wrong Device` (line 4090, 6994) — same heading style as Missing
- Same card chrome (`Frame 14606923`, white fill, 8px radius, shadow)

#### 3.6.2 Table columns (per Figma frame 4, line 4140-4437)

| # | Header label | Cell style notes |
|---|---|---|
| 1 | `Expected Barcode` (line 4140) | flex |
| 2 | `Expected Device` (line 4176) | flex-fill — `Apple iPhone XR 64GB Other A1984/...` |
| 3 | `Grade` (line 4212) | flex |
| 4 | `Box No.` (line 4248) | flex. **Note** the abbreviation — Figma uses `Box No.` here vs. `Box Number` in Missing. |
| 5 | `Amount Paid` (line 4284) | flex |
| 6 | `Actual IMEI` (line 4320) | flex — placeholder `3020250619182236283058` |
| 7 | `Actual Device` (line 4356) | flex — placeholder `Google Pixel 64GB T-Mobile` |
| 8 | `Latest Price` (line 4392) | flex — placeholder `$XX` |
| 9 | `Action` (line 4423) | green-tint `#D8E5D9` |
| 10 | `Amount to Credit` (line 4437) | green-tint `#D8E5D9` |

> **No `Action Recommendation` column header.** The recommendation engine
> drives the **default selected value** in the `Action` dropdown — it does
> not get its own column. Per impl-plan §9 the dropdown defaults to
> `Accept` or `Decline` based on the recommendation; the reviewer can
> override. The label "Action Recommendation" was used in the prompt
> spec but the Figma export does not show a distinct column for it.
> **Plan accordingly: surface the recommendation as the default-selected
> value + an optional tooltip explaining the reasoning, not as a 10th
> column.**

#### 3.6.3 Per-row content (line 4456-4554)

Verbatim Row 1 (Accept variant):
- Expected Barcode: `3020250619182236283058`
- Expected Device: `Apple iPhone XR 64GB Other A1984/A2105/A2106/A2108`
- Grade: `C\_YNY`
- Box No.: `XX-XXXX`
- Amount Paid: `$XX`
- Actual IMEI: `3020250619182236283058`
- Actual Device: `Google Pixel 64GB T-Mobile`
- Latest Price: `$XX`
- Action: dropdown showing `Accept` (line 4591) with green `thumbs-up` icon
- Amount to Credit: `$XX`

Verbatim Row 2 (Decline variant, line 4629-4773):
- Same shape
- Latest Price: `$XX` (or `$XXX`)
- Action: dropdown showing `Decline` (line 4764) with red `thumbs-down` icon
- Amount to Credit: `$0`

### 3.7 Reason section — `Encumbered Devices`

#### 3.7.1 Section heading

- `Encumbered Device` (line 4837, 5921, 5719) — same heading style
- Multi-reason variant shows `Encumbered` in a small variant tag (line 5198, 6265)

#### 3.7.2 Table columns (per Figma frame 5, line 4869-5109)

| # | Header label | Cell style notes |
|---|---|---|
| 1 | `Barcode` (line 4884) | flex |
| 2 | `Device Description` (line 4920) | flex-fill |
| 3 | `Grade` (line 4956) | flex |
| 4 | `Box Number` (line 4992) | flex |
| 5 | `Amount Paid` (line 5028) | flex |
| 6 | `Prolog Result` (line 5064) **OR** `Prolog Check` (line 6134) | flex — reviewer-driven dropdown. Two variants of the label exist in different frames (the bulk-action variant uses `Prolog Result`, the single-row review variant uses `Prolog Check`). **Resolve:** use `Prolog Result` consistently — it matches the entity column name. |
| 7 | `Actual Value` (line 8210) — only on the bulk-action variant frame (line 7683) | flex — reviewer-driven input |
| 8 | `Action` (line 5095) | green-tint `#D8E5D9` |
| 9 | `Amount to Credit` (line 5109) | green-tint `#D8E5D9` |

> **Per the prompt brief**, the Encumbered table is expected to have
> **8 columns**: Barcode, Brand/Model, Grade, Box, Paid, Prolog Result,
> Actual Value, Action, Credit (= 9 with Credit). The Figma export shows
> the single-row variant with **no Actual Value column** (frame at line
> 4869-5099); the Actual Value column only appears on the bulk-action
> multi-row variant (frame at line 7683 onwards). **Decision for
> implementation:** always render the Actual Value column — its absence
> from the single-row variant is a Figma omission, not a spec decision.

#### 3.7.3 Per-row content (line 5128-5184 and 5273-5329)

Row 1 (Accept):
- Barcode: `3020250619182236283058`
- Device Description: `iPhone XR 64GB Other A1984/A2105/A2106/A2108`
- Grade: `C\_YNY`
- Box Number: `XX-2458`
- Amount Paid: `$XX`
- Prolog Result: dropdown showing `Encumbered` (line 5198)
- Actual Value: input showing `$XX` (only on bulk-variant frame)
- Action: dropdown showing `Accept` with `thumbs-up` icon
- Amount to Credit: `$XX`

Row 2 (Decline):
- Same shape
- Prolog Result: dropdown showing `Not Encumbered` (line 5343)
- Action: dropdown showing `Decline` with `thumbs-down` icon
- Amount to Credit: `$0`

### 3.8 Section toggle

Each section card has a single chevron icon (top-right of the heading
bar). When the section is expanded, the chevron is `chevron-up`; when
collapsed, `chevron-down`. The chevron sits in the same row as the
section heading and the per-section bulk action button (§4).

---

## 4. Bulk actions

### 4.1 Per-section bulk buttons

From `Frame 14606923` (bulk-action variant — line 7929-7962, 8595-8607):

| Frame # | Button text | Color | Position |
|---|---|---|---|
| line 7960 | `Approve All` | green `#14AC36` fill | Right of section heading, in the Missing/Wrong/Encumbered card |
| line 8607 | `Decine All` | red (fill not explicit but should be `#B3261E` based on Decline pill — see §6) | Right of section heading, **when the prolog result on a row is "Not Encumbered"** |

> **Typo:** Figma exports `Decine All` (line 8607) — clearly a typo for
> `Decline All`. **Decision:** use `Decline All` in implementation.

Per-section bulk buttons should:
- Set every line in the section that has `review_decision = PENDING` to
  `ACCEPTED` (Approve All) or `DECLINED` (Decline All).
- Skip lines that already have a decision (don't overwrite reviewer's
  per-line work).
- Recompute the header summary panels.

### 4.2 Global bulk buttons

The Figma export does **not** show a globally-scoped "Approve All
Sections" or "Decline All Sections" button. The bulk buttons are
per-section only. The closest thing is the `Complete Review` button
(§3.4) which finalises the request — but it does not blanket-accept
pending lines.

**Decision:** implement per-section bulk only. Global bulk is out of
scope per Figma. The prompt brief mentioned "global Approve All / Decline
All" — this is a misread of the Figma. Engineering should ship per-section
bulk + the global Complete Review (which forces a decision per line as
a precondition).

### 4.3 Bulk button copy (verbatim)

- `Approve All` (line 7960, 8390, 8553) — note: uses `Approve` not `Accept`
- `Decine All` (line 8607) — typo, render as `Decline All`

Per-line dropdown uses `Accept` / `Decline` (line 4591 / 4764), per-section
bulk uses `Approve All` / `Decline All`. **Both verbs map to the same
backend state** (`ACCEPTED` / `DECLINED` enum). The Figma split
("Approve" for bulk, "Accept" for single) is a UX choice — match the
copy literally.

---

## 5. Complete Review

### 5.1 Button copy (verbatim)

- `Complete Review` (line 2262, 3408, 5909, 6979) — top-right of the
  header strip on every detail variant
- Style: `Text/sm (strong)`, color `Light Gray` (`#EFECE4`), fill
  `#14AC36` (green primary)

### 5.2 Confirmation modal

The Figma export for node 213-610 does **not include** the confirmation
modal frame. The Sprint 2 design notes §2.5.5 reference a generic
"Submit Request" modal but that was for the buyer wizard. For the admin
"Complete Review" action, **infer from impl-plan §16** and the analogous
RMA pattern:

- Modal heading: `Are you sure?` (per impl-plan; not in Figma export but
  matches the RMA confirmation pattern)
- Body: `This will finalise the review and send the buyer a notification
  email.`
- Two buttons:
  - `Confirm` (green primary, `#14AC36`) — fires
    `POST /{id}/complete-review`
  - `Cancel` (white, gray border) — dismisses modal

> **Anomaly:** The prompt brief asked for "Complete-review modal
> confirmation copy". The Figma export does not contain it. The
> implementation plan will reference RMA's `Confirm`/`Cancel` modal as
> the template. **Flag for design follow-up.**

### 5.3 Outcome states

After Complete Review fires:
- Status: `UNDER_REVIEW → APPROVED` (if every line decision is `ACCEPTED`
  OR the reviewer explicitly chose Approved-Overall) **OR**
  `UNDER_REVIEW → DECLINED` (if every line decision is `DECLINED` OR
  reviewer chose Declined-Overall)
- Mixed-decision requests: per impl-plan §12, the final status is
  **binary** (no `Partially_Approved`). The reviewer's per-line decisions
  are persisted on each line entity; the request header carries the
  binary outcome.
- Email: `PartialCredit_ReviewCompleted_Approved` or
  `PartialCredit_ReviewCompleted_Declined` template fires to the buyer.

### 5.4 What the modal does NOT do

- No free-text "decline reason" input — per impl-plan §18 Q9 decision.
- No bulk-accept-pending behaviour — the modal expects every line to
  already have an explicit per-line decision.

---

## 6. Color tokens

Extracted verbatim from the `globalVars.styles` section of the Figma
export (lines 10500-11525). Lists every hex referenced on the admin
review surface.

### 6.1 Brand / status colors

| Hex | Token name in Figma | Used for |
|---|---|---|
| `#14AC36` | `fill_HX3N5P` | Primary green — `Complete Review` button, `Approve All` button, per-line `Accept` pill, **step indicator active circle** |
| `#1C1B1C` | `fill_4XBBOL` / `Blackish` | Primary text color, status counter `{ts1}` count text |
| `#3C3C3C` | `fill_K4GMTG` | "John Doe" user label in header |
| `#534F4C` | `fill_M83UPQ` | Column filter `Ab` button text, eye icon |
| `#6F6F6F` | `fill_T5QEJW` | Header strip field labels (`Request Date`, `Buyer`, etc.) |
| `#7D7B7A` | `fill_VLT5FL` | Date-filter calendar icon |
| `#A8A7A6` | `fill_K51TIL` | Table cell border (0.81px) |
| `#B7B5B5` | `fill_83HKM2` | Counter chip border, summary panel mini-table border, table divider line |
| `#EFECE4` | `Light Gray` | Counter chip unselected fill, button text color (on green buttons) |
| `#F0EBE3` | `fill_LVM9NV` | (request detail page background) |
| `#F26B21` | `fill_BGDU8E` | (orange brand accent — not used on this canvas but defined globally) |
| `#F7F5F1` | `fill_TPMBBC` | Reason section card background (the lighter cream) |
| `#FEF48F` | `Light Yellow` | Status counter chip — selected state fill |
| `#D8E5D9` | `fill_N47Q64` | **Sticky action column** background tint (Action + Amount to Credit cells) |
| `#407874` | `fill_AA7ALI` | Teal — defined globally but not directly on this surface (matches `UNDER_REVIEW` color in V89 seed) |
| `#888888` | (literal hex in V89 seed) | DRAFT status color (not visible on admin surface) |
| `#D08214` | (literal hex in V89 seed) | PENDING_APPROVAL status color (status pill on landing rows) |
| `#B3261E` | (literal hex in V89 seed) | DECLINED status color — also implied for `Decline All` button and decline-state pills |

### 6.2 Status pill colors (from V89 seed)

| System status | Color |
|---|---|
| `DRAFT` | `#888888` (gray) |
| `PENDING_APPROVAL` | `#D08214` (orange) |
| `UNDER_REVIEW` | `#407874` (teal) |
| `APPROVED` | `#14AC36` (green) |
| `DECLINED` | `#B3261E` (red) |

The status pill on the landing table renders the **external_status_text**
(so `UNDER_REVIEW` displays "Pending Approval" with the teal color or
the orange color depending on the seed — V89 seeds `UNDER_REVIEW` with
`color_hex = '#407874'`). **Decision:** render the color from V89 seed,
not from the system status string. This matches the existing Sprint 2
buyer landing pattern.

### 6.3 Action-pill colors (per-line decision state)

| State | Background | Icon | Text |
|---|---|---|---|
| Pending (`Select`) | white / `#FFFFFF` | none | `#534F4C` |
| Accept (selected) | `#14AC36` light tint (`#D8E5D9` is the cell tint, not the pill — the pill itself is green `thumbs-up` icon + `Accept` text) | green `thumbs-up` `#14AC36` | `#1C1B1C` |
| Decline (selected) | white | red `thumbs-down` (color not explicitly extracted — infer `#B3261E`) | `#1C1B1C` |

### 6.4 Header strip & gradient

The page-top auction gradient ribbon is `gradient/Auction Gradient` —
inherited from the existing app shell. Not a Sprint-3-specific token.

### 6.5 Expanded vs. collapsed chevron treatments

- Collapsed: `chevron-down` icon, color `#534F4C`
- Expanded: `chevron-up` icon, color `#534F4C`
- Both at 16px (style `style_K7Q6KX`)
- No animation specified in Figma — engineering should apply a CSS
  `transform: rotate(180deg)` with `transition: transform 0.2s ease-out`
  to match the rest of the app (Sprint 2 wizard uses the same pattern).

---

## 7. Typography

All copy is rendered in **Founders Grotesk** (Regular or Medium). The
specific styles referenced on this canvas:

| Style token | Family | Weight | Size | Line height | Used for |
|---|---|---|---|---|---|
| `Display/lg` | Founders Grotesk | Medium (500) | 40 | 110% | Page heading (`Partial Credit Requests`, `Request Details`) |
| `Display/sm` | Founders Grotesk | Medium (500) | 24 | 110% | Section heading (`Missing Device`, `Wrong Device`, `Encumbered Device`), summary panel sub-heading |
| `Text/base` | Founders Grotesk | Regular (400) | 18 | — | Empty state copy, breadcrumb |
| `Text/base (561:2872)` | Founders Grotesk | Regular (400) | 18 | — | Breadcrumb text (variant — left-align) |
| `Text/base (strong)` | Founders Grotesk | Medium (500) | 18 | — | (defined globally, not directly used on this surface) |
| `Text/sm` | Founders Grotesk | Regular (400) | 16 | — | Table cell value text (`Buyer Name`, `Apple`, etc.) |
| `Text/sm (strong)` | Founders Grotesk | Medium (500) | 16 | — | Button text (`Download`, `Complete Review`, `Approve All`) |
| `style_4UU9I9` | Founders Grotesk | Medium (500) | 14 | — | Column header text (`Date Submitted`, `Buyer`, etc.) |
| `style_OM5UY1` | Founders Grotesk | Regular (400) | 18 | — | Header strip field VALUES (`Jonathan Wildermeyer`, `Acme Inc.`) |
| `style_4XMI23` | Founders Grotesk | Regular (400) | 13 | 100% | Header strip field LABELS (`Request Date`, `Buyer`, `Company`) |
| `style_LPFT2I` | Founders Grotesk | Medium (500) | 16 | 100% | Status counter chip text |
| `style_VXWA06` | Founders Grotesk | Regular (400) | 11 | 100% | Summary panel mini-table headers (`SKUs`, `Qty`, `Total`) |
| `style_PKF98T` | Founders Grotesk | Medium (500) | 15.31 | 100% | Summary panel mini-table values (`XX`, `XXX`) |
| `style_HNMADV` | Founders Grotesk | Medium (500) | 15.31 | 100% | Summary panel `$XX,XXX` total — left-aligned |
| `style_LGAZC4` | Inter | Medium (500) | 11 | — | Filter button `Ab` text (column header filter trigger) — **only place Inter is used** |
| `style_9O18RZ` | Founders Grotesk | Medium (500) | 15 | — | (variant — defined but not directly used) |
| `style_K7Q6KX` | Font Awesome 6 Pro | Regular (400) | 16 | — | All icons (chevrons, eye, calendar, arrows) |

**Implementation note:** the project already loads Founders Grotesk via
`Brandon_Grotesque.css` and the Tailwind config. The Inter font is used
sparingly — for the column filter `Ab` trigger only. Match this exactly;
do not substitute.

---

## 8. Anomalies / Duplicates / Spec ambiguities

Flagged for the implementation plan §11 to resolve:

### 8.1 Every screen frame named `Review Partial Credit Requests Landing (Empty)`

The Figma export contains **9 frames** with the identical name
`Review Partial Credit Requests Landing (Empty)`. Only one of them is the
actual empty landing; the others are populated landings, review detail
variants, and reason-pivoted landings. **Use position + content to
disambiguate.** The inferred name table in §1.2 supersedes the Figma
names for engineering purposes.

### 8.2 Encumbered summary labels — `Requested RMA` / `Approved RMA`

The encumbered-only review detail frame (line 5524) renders the summary
panels as `Requested RMA` (line 3262, 5763) and `Approved RMA` (line
5836). This is **legacy from the original spec where encumbered devices
auto-created an RMA**. Per impl-plan §18 Q8 decision (2026-05-11), the
RMA integration is dropped from Phase 1 — there is no
`Requested RMA` / `Approved RMA` content to render.

**Decision:** always render `Requested Credit` / `Total Credit` on the
encumbered summary panels. Match Missing + Wrong.

### 8.3 `Decine All` typo

Frame at line 8607 renders `Decine All` instead of `Decline All`.
**Implementation:** use `Decline All`.

### 8.4 `Prolog Result` vs. `Prolog Check`

Two label variants exist in different frames:
- `Prolog Result` (line 5064, 8170) — bulk-action variant, also matches
  the V89 column name `prolog_result`
- `Prolog Check` (line 6134) — single-row encumbered-only variant

**Decision:** use `Prolog Result` everywhere — matches the entity.

### 8.5 Encumbered: `Actual Value` column missing from single-row variants

The Encumbered table on single-row review detail frames (lines 4869-5109)
shows columns: Barcode, Device Description, Grade, Box Number, Amount
Paid, Prolog Result, Action, Amount to Credit (8 cols, no Actual Value).
The bulk-variant frame (line 7683) adds an `Actual Value` input column
between Prolog Result and Action.

**Decision:** always render the Actual Value column. It is required for
the credit-calc engine to compute encumbered AmountToCredit (per
impl-plan §8 `EncumberedDeviceLine.AmountToCredit = ReviewerEnteredAmount`).

### 8.6 No `Action Recommendation` column in Wrong section

The prompt brief asked us to capture an `Action Recommendation` column
on the Wrong Device section. The Figma export does **not** show such a
column. The recommendation engine output drives the **default value of
the Action dropdown** — not a separate column.

**Decision:** surface the recommendation as the **default-selected**
value in the Action dropdown + a small `circle-info` tooltip on hover
explaining why (e.g. "Recommended: Decline — Latest Price $50 exceeds
Amount Paid $40"). Do not add a 10th column.

### 8.7 No global "Accept All Sections" / "Decline All Sections" button

The Figma export shows only per-section bulk buttons. The prompt brief
implied a global bulk button — that is a misread. **Decision:** per-section
bulk only. The global Complete Review button is the global-scope action.

### 8.8 Confirmation modal copy not in export

The Complete Review confirmation modal copy ("Are you sure?") is not
in the node 213-610 export. **Decision:** use the placeholder copy from
impl-plan §16 + RMA pattern; flag for design follow-up before Chunk 7
ships (see implementation plan §11).

### 8.9 Header strip `Status` field external-text rendering

The header strip Status field shows `Pending Approval` even when the
backend status is `UNDER_REVIEW`. This matches the V89 seed
(`UNDER_REVIEW.external_status_text = 'Pending Approval'`) and is
correct buyer-facing behaviour — but the admin reviewer should arguably
see the internal status (`Under Review`) so they know the request has
been opened by another reviewer. **Decision:** show the **internal**
status text on the admin detail header strip (so reviewer sees `Under
Review`), but show the **external** status text on the landing list
(matches buyer mental model). Flag for design clarification.

### 8.10 No "first-reviewer locks request" UI signal

The Figma export does not show any visual indicator when the request
is already open in another reviewer's session. Per impl-plan §12, the
transition `PENDING_APPROVAL → UNDER_REVIEW` happens on first open and
is irreversible. **Decision:** show the reviewing user's name + initials
in the header strip ("Reviewed by: Jane Doe") once status is
`UNDER_REVIEW`. This is implied by the `reviewed_by_id` column on V89's
`credit_requests` but not specified by Figma. **Add to Chunk 7 polish.**

### 8.11 Missing Devices reason-pivoted landing

Frame at line 9160 shows a `Missing Device Credit Requests` reason-
pivoted landing — only Missing-reason rows, with additional columns
(`Box Number`, `Shipment Damaged?`, `Shipment Status`). Per impl-plan
§16 line 462 / §11 ("decision 2026-05-11: no reason-pivoted landings"),
this view is **deferred to a later phase**. Sprint 3 ships the unified
admin landing only.

### 8.12 No "Reviewed By" column on the landing

The Figma landing shows no `Reviewed By` column. Per impl-plan §12 the
`reviewed_by_id` is tracked, but it's not surfaced on the table.
**Decision:** don't add it in Sprint 3. Could be a Sprint 4 polish.

### 8.13 No filter UI implementation in Figma

The column header `Ab` buttons are filter triggers in design intent but
the export does not show the filter pop-over expanded. **Decision:**
implement the filters as simple textbox-on-click inputs (free text
contains-match) for Buyer / Company / Order # / Reason / Status
columns, and a date-range picker for Date Submitted. Polish-level
detail; can be deferred to Sprint 4 if time-constrained.

---

## 9. Implementation cheat-sheet

For convenience — the bare-minimum DOM shape engineering needs for each
admin route:

### 9.1 Landing (`/admin/auctions-data-center/partial-credit`)

```
<AdminShell>
  <Heading>Partial Credit Requests</Heading>
  <StatusChipRow>
    <Chip selected>Pending Approval: {n}</Chip>
    <Chip>Approved: {n}</Chip>
    <Chip>Declined: {n}</Chip>
    <Chip>All: {n}</Chip>
  </StatusChipRow>
  <DownloadButton disabled={empty} />
  <Table>
    <TableHeader>
      Date Submitted | Buyer | Company | Order Number | Request Reason | Status | (eye)
    </TableHeader>
    <TableBody>
      {rows.map(r => <Row onClick={() => openReview(r.id)}>...</Row>)}
    </TableBody>
  </Table>
  <EmptyState>There are currently no Partial Credit Requests to approve</EmptyState>
</AdminShell>
```

### 9.2 Review detail (`/admin/auctions-data-center/partial-credit/{id}`)

```
<AdminShell>
  <Breadcrumb>All Partial Credit Requests</Breadcrumb>
  <Heading>Request Details</Heading>
  <HeaderStrip>
    <Field label="Request Date" value={fmt(r.requestDate)} />
    <Field label="Buyer" value={r.buyerName} />
    <Field label="Company" value={r.partyName} />
    <Field label="Order Number" value={r.orderNumber} />
    <Field label="Request Reason" value={joinReasons(r)} />
    <Field label="Status" value={r.status.internalStatusText} />
  </HeaderStrip>
  <SummaryPanels>
    <Panel title="Requested Credit" skus={r.requestedSkus} qty={r.requestedQty} total={r.requestedTotal} />
    <Panel title="Total Credit" skus={r.approvedSkus} qty={r.approvedQty} total={r.approvedTotal} />
  </SummaryPanels>
  <CompleteReviewButton onClick={openCompleteReviewModal} />
  {r.hasMissingDevice && <MissingSection lines={r.missingLines} ... />}
  {r.hasWrongDevice && <WrongSection lines={r.wrongLines} ... />}
  {r.hasEncumberedDevice && <EncumberedSection lines={r.encumberedLines} ... />}
  <CompleteReviewModal />
</AdminShell>
```

### 9.3 Reason section component (shared shape)

```
<SectionCard>
  <SectionHeading>{title}</SectionHeading>
  <BulkActionButton onClick={approveAll}>Approve All</BulkActionButton>
  <ChevronToggle />
  <Table>
    <TableHeader>{columns}</TableHeader>
    {lines.map(line => <LineRow line={line} onDecisionChange={...} />)}
  </Table>
</SectionCard>
```

---

## 10. End

This document captures everything load-bearing from node 213-610.
Sprint 3 implementation plan (sibling file
`partial-credit-sprint3-implementation-plan.md`) will reference this
document for every UI string + color + layout decision.
