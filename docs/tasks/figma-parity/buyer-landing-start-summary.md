# Figma parity — Buyer landing + Start + Summary

Source of truth: `docs/tasks/partial-credit-sprint2-design-notes.md` (Figma file
`rYKB9vBqlJOFUuGN7GAgQS`, canvas node `173:600` — "Submit Credit Request").
Method: spec-against-code (no live screenshots).
Reviewed: 2026-05-12.

Severity scale: CRITICAL · HIGH · MEDIUM · LOW.

---

## Surface 1: Buyer landing (/wholesale/partial-credit)

**Figma node:** `173:600` § "Credit Requests Landing" (lines 12246–13544 of design dump)
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Filled (line 12254), Empty (line 12990)

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| HIGH | Typography | Body inherits `--font-family-primary` from `globals.css`, which is **Brandon Grotesque**. Figma specifies **Founders Grotesk** for every text style on this surface. `wizard.module.css` declares no `font-family`, so the wizard renders in the wrong typeface. | Founders Grotesk (Regular 400 / Medium 500) | Brandon Grotesque (inherited from `body` in `globals.css`) | Add `font-family: 'Founders Grotesk', 'Brandon Grotesque', sans-serif;` to `.page` in `wizard.module.css`, or scope a wizard-specific token. The `@font-face` for Founders Grotesk is already declared in `globals.css`. |
| HIGH | Copy | Primary CTA on the landing chrome reads `+ Submit a Credit Request` with a literal "+" prefix. Figma button label is just `Submit a Credit Request` — no plus glyph. | `Submit a Credit Request` | `+ Submit a Credit Request` (line 76 of `page.tsx`) | Drop the `+ ` prefix. If a leading plus icon is desired, use the same SVG `Plus` glyph used in the Figma "Add More Devices" button, not a text character. |
| HIGH | Layout | Breadcrumb on landing reads `All Buyer Codes › Credit Requests`. There is no breadcrumb on the Figma landing page — only the purple Title banner `Credit Requests Landing Page` and the `Credit Requests` H1. The "All Credit Requests" breadcrumb only appears on the wizard subpages. | (none — page is top-level in the buyer shell) | `All Buyer Codes › Credit Requests` | Either remove the breadcrumb (preferred — matches Figma) or downgrade to match the Title-banner pattern. The current breadcrumb implies an ancestor route that doesn't exist in the buyer journey. |
| MEDIUM | Layout | Filled state is missing a fifth column for the per-row "eye" view action. Figma shows column 5 = single `eye` icon per row → routes to Credit Request Detail Page. | `Date Submitted · Order Number · Request Reasons · Status · (eye)` | `Date Submitted · Order Number · Request Reasons · Status` (4 columns, line 102–108) | Add an action column. Code already has a comment noting this is deferred to Sprint 4 ("row click navigation is intentionally omitted") — acknowledge as known-deferred but flagging because the detail page now exists at `partial-credit/[id]`. |
| MEDIUM | Behaviour | Column-header filter chips (`Ab` chips for Order Number / Request Reasons / Status and `calendar-days` icon picker for Date Submitted) are not rendered. | Filter chips on 3 columns + calendar picker on Date column (design notes §5.5) | No chips, no picker (TODO comment at line 91–92) | Tracked in code as Sprint 3 TODO; flag as known-deferred. |
| MEDIUM | Color | Status pill treatment uses bespoke pastel backgrounds (`#FFF3EC` / `#E6F6EB` / `#FCE6E1`). Figma renders all status pills with the same neutral light pill chrome + a 0.81px stroke `#A8A7A6`, with status-specific *text* color only (Approved green / Declined red / Pending orange-warning). | Neutral pill chrome + colored text only | Tinted background per status (`statusApproved` / `statusDeclined` / `statusPending` classes) | Either align to neutral pill chrome (matches Figma slice) or keep the tinted variant and add the `0.81px stroke #A8A7A6` border so they don't read as solid badges. |
| MEDIUM | Copy | Empty state row is rendered inside a card div, **not** as a full-row table message. Figma renders the same table chrome with a single full-row message inside the tbody. | Empty message lives inside the table chrome as a full-row | Renders as a plain card outside any table (line 96–98) | Render the table headers in the empty state and put the message in a `<tr><td colspan="N">…</td></tr>` row. Keeps the chrome visually consistent. |
| MEDIUM | Color | Landing R-2 / RMA banner uses a neutral cream surface `#FBFAF8` with light border `#E6E5E4`. Figma uses the `Subtle off-white surface` token `#FBFAF8` — matches — but no border treatment is specified; the Figma banner sits flush with the page background. | Light off-white pad, no visible border | `#FBFAF8` background with `1px solid #E6E5E4` border | Either drop the border or confirm Figma actually had a thin divider (the design notes don't show one). LOW-MEDIUM polish. |
| LOW | Spacing | `.page` padding is `24px 32px 80px`. Figma layout spec says `24px` container padding all around and `32px` between page sections (design notes §3.7). Top/horizontal match; bottom `80px` is custom. | 24px container padding | `24px 32px 80px` | Acceptable variance (bottom safe-area). Confirm intentional. |
| LOW | Spacing | `.heading` `margin: 0 0 32px`. Figma scaffolds `32px` between sections — but the landing has the heading-row and the banner stacked directly with `16px` between (design notes §5.1 / §3.7). | 16px gap heading-row → banner | 32px margin under heading via `.heading` selector | Remove `margin-bottom` on `.heading` on landing (the heading is inside a flex row already), let `.landingHeadingRow + .landingBanner` margin handle the gap. |

### Sprint 4 additive (not parity defects)

- `Submit on behalf` ghost button visible only when `roles.includes('SalesRep')`. Confirmed not in Figma; this is the documented Sprint 4 extension and should remain as is.

### Confirmed-OK summary

- Layout: page background `#F7F7F7` (matches `fill_82WWSQ`).
- Layout: heading row is `flex` with `justify-content: space-between` — matches Figma row.
- Layout: landing R-2 / RMA banner copy is verbatim from design notes line 855 (matches the "If you are not R-2 certified…" sentence).
- Typography: heading uses `font-size: 40px` + `font-weight: 500` + `color: #1C1B1C` — matches `Display/lg` token spec (font family is wrong; see HIGH finding).
- Color: heading color `#1C1B1C` matches `Blackish` (`fill_Z5L1IT`).
- Color: primary CTA fill `#14AC36` matches `Eco Green` (`fill_CRTE1J`).
- Color: ghost button border + text `#14AC36` matches "Eco Green" — but this is the Sprint 4-only "Submit on behalf" affordance, additive to Figma.
- Copy: empty state `There are currently no Partial Credit Requests` matches line 856 verbatim.
- Copy: column headers `Date Submitted`, `Order Number`, `Request Reasons`, `Status` match design notes §5.2 (and use the plural "Reasons" per anomaly note #4).
- Copy: `Credit Request Policy` link text matches line 853 verbatim.
- Copy: row reasons join: `Missing Device, Wrong Device, Encumbered Device` — matches Figma sample row formatting.

---

## Surface 2: Wizard step 1 (Start) (/wholesale/partial-credit/new)

**Figma node:** `173:600` § "Start Request (R2 Certified)" (lines 171–1369 of design dump)
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/StepIndicator.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Start Credit Request (initial empty, line 179) + Start Credit Request (populated, line 735)

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Behaviour | The wizard step 1 has a `Cancel` button in the bottom-right cluster. Per design notes anomaly #15: **"The wizard `Cancel` only appears on Summary, not on intermediate steps. On Steps 1–4, the only 'back-out' affordance is the `Back` button"** — and Step 1 has no Back. Figma shows only the right-aligned `Next` button on Step 1 (`Back` not yet present because Overview is first step). | Only `Next` (single right-aligned button) | `Cancel` + `Next` (lines 173–189) | Remove `Cancel`; let the breadcrumb `All Credit Requests` be the abandon affordance, per the design-notes rule. (If product wants a Cancel on Step 1 for usability, raise it explicitly — it diverges from Figma.) |
| HIGH | Typography | Same root cause as Surface 1 — wizard inherits `Brandon Grotesque` from body. Figma specifies Founders Grotesk on every text style here (`Display/lg`, `Display/sm`, `Text/base`, `Text/sm`). | Founders Grotesk | Brandon Grotesque (inherited) | Same fix as Surface 1 HIGH — set `font-family` on `.page`. |
| HIGH | Behaviour | `Done` step indicator state renders as a UTF-8 `✓` character inside the circle. Figma spec uses the Font Awesome solid `check` icon (design notes §3.1 third bullet). | Font Awesome `check` glyph in green circle | Plain text `✓` (line 51 of `StepIndicator.tsx`) | Inline an SVG check matching the FA solid path (the Summary page already inlines a `CircleCheckIcon` for the confirmation modal — follow that pattern for consistency). |
| HIGH | Color | Step indicator connector line is `1px` flat `#A8A7A6` between every node. Figma renders a single continuous horizontal line behind the entire step row (one line, not per-segment dividers). | Continuous horizontal divider behind nodes, full width 800px | Per-gap `stepDivider` segment, `width: 32px; height: 1px` | Replace `.stepDivider` with a single `::before` line absolutely positioned behind the `.stepIndicator` flex row, or use `border-bottom` on a centered horizontal track. |
| MEDIUM | Color | Step indicator inactive circle color tokens are correct (`#A8A7A6` border, `#7D7B7A` numeral) and active is `#14AC36`. **However**, the inactive numeral color in code uses `#7D7B7A` but the label color is `#3C3C3C`. Figma spec for inactive label: `#534F4C` (design notes §3.1). | Inactive label `#534F4C` | `#3C3C3C` (line 76) | Change `.stepLabel` color to `#534F4C`. |
| MEDIUM | Typography | `.fieldLabel` is `font-size: 14px; font-weight: 600`. Figma `Text/base (strong)` for "Order Number" label is `fontSize 18; weight 500`. | 18px / 500 | 14px / 600 (line 112–118) | Bump to `font-size: 18px; font-weight: 500`. |
| MEDIUM | Typography | `.cardSubheading` (`Select all that apply`) is `font-size: 14px`. Figma `Text/sm` subtitle is `fontSize 16` (design notes §3.6 typography table). | 16px Regular 400 | 14px Regular | Bump to `16px`. |
| MEDIUM | Typography | `.helperText` (`Partial credit requests must be made within 30 days…`) is `font-size: 13px; color: #534F4C`. Figma `Text/sm` for helper text is `fontSize 16` per typography table; the design notes' Step 1 spec (§2.1 Card 1) shows the 30-day copy as helper text under the input. Token suggests 16px, code is 13px. | 16px (Text/sm token) | 13px | Bump to `16px` to match the body-sm token. |
| MEDIUM | Spacing | `.card` padding is `28px 32px`. Figma card padding is `24px 32px` (design notes §3.7). | `24px 32px` | `28px 32px` | Drop top/bottom padding by 4px. |
| MEDIUM | Layout | `.card` has a `1px solid #E6E5E4` border. Figma spec is shadowed card without a border: `box-shadow: 0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px 0px rgba(0,0,0,0.1)` (design notes §3.2 and §3.8). | Box-shadow only, no border | 1px border + no shadow | Remove the border and add the dual box-shadow. |
| MEDIUM | Layout | `.card` border-radius is `12px`. Figma spec is `radius: 8px` (design notes §3.2). | 8px | 12px | Drop to `8px`. |
| MEDIUM | Behaviour | Step indicator initial-state numbering uses `idx + 1` for every non-active/done node — which prints `2` next to `Device Details` and `3` next to `Summary` on the entry frame. Figma's initial frame uses `2` and `3` on those placeholders (matches), but on the active frame after reasons are selected, the placeholder collapses into named steps (`Missing Device`, `Wrong Device`, `Encumbered Device`, `Summary`) and Figma re-numbers as the user advances. The code preserves the index numbering — verified correct. **Flag only if the StepIndicator is asked to render after a partial transition where the Done indicator should hide the number.** No fix needed; verifying. | Numeric or Done-check, mutually exclusive | Same | — |
| LOW | Spacing | `.buttonRow` has `margin-top: 32px; gap: 12px`. Figma button-group gap is `8px` (design notes §3.7). | Gap 8px | Gap 12px | Drop to 8px. |
| LOW | Layout | Buttons are `padding: 10px 20px` (Secondary) and `10px 24px` (Primary). Figma spec is uniform `200px × 40px` with `8px 16px` padding (design notes §3.7 + anomaly #14). | Uniform `200px × 40px` | `auto × auto` with bespoke padding | Set `min-width: 200px; height: 40px; padding: 8px 16px`. |

### Sprint 4 additive (not parity defects)

- `?draftId=X` resume path (lines 25–58 in `page.tsx`) — Sprint 4 chunk 8 addition, not in Figma. Confirmed additive.
- `Submit on behalf` data flow from the landing modal — additive.

### Confirmed-OK summary

- Layout: page background, breadcrumb, H1, step indicator, two cards (Order + Reasons), button row ordering match Figma.
- Copy: H1 `Submit a Credit Request` matches design notes verbatim.
- Copy: Card 1 heading `What order is the request for?` — verbatim.
- Copy: Order Number field label `Order Number` — verbatim.
- Copy: Placeholder `XX-XXXX` — verbatim (matches populated-state placeholder in Figma).
- Copy: Helper `Partial credit requests must be made within 30 days of the order shipment date.` — verbatim from design notes line 793.
- Copy: Card 2 heading `Why are you requesting credit?` — verbatim.
- Copy: Subheading `Select all that apply` — verbatim.
- Copy: `Missing Device`, `Wrong Device (model, carrier, or capacity)`, `Encumbered (iCloud locked, MDM locked, or blocklisted)` — verbatim from §2.1 Card 2.
- Color: Primary CTA `#14AC36` and disabled opacity `0.4` match Figma.
- Color: Card surface `#FFFFFF`, page background `#F7F7F7`, heading color `#1C1B1C` all correct.
- Behaviour: `Next` disabled until orderNumber AND ≥1 reason — matches Figma "empty has no order # so it's grayed" rule.
- Behaviour: Step indicator collapses to 3 nodes (Overview + Device Details + Summary) before any reason is selected, then expands — matches anomaly #8.

---

## Surface 6: Wizard step 5 (Summary) (/wholesale/partial-credit/new/summary)

**Figma node:** `173:600` § "Summary" (lines 1370–11154 of design dump)
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/summary/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/summary/SummaryStep.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Request Credit - Summary (default view, line 1378), Edit variants (lines 2867 / 6422 / 11157), Confirmation overlay (line 11097).

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy | H1 reads `Review and submit`. Figma keeps the wizard-wide H1 `Submit a Credit Request` and uses the purple Title banner `Summary` to denote the step. There is no `Review and submit` string anywhere in the Figma slice. | `Submit a Credit Request` (page H1) + `Summary` (section title banner) | `Review and submit` (line 117) | Restore the wizard H1 `Submit a Credit Request`. The "step name" lives in the StepIndicator's active node, not in the page H1. |
| CRITICAL | Layout | The summary metadata card (top white card with `Order Number`, `Request Reasons`, `Total Devices`) is missing. Figma shows a three-row labelled meta card before any group cards (design notes §2.5.1). | Three labeled rows: `Order Number → XXXXXX-XX`, `Request Reasons → Missing Device, Wrong Device, Encumbered Device`, `Total Devices → 49` | Code renders an "Order" section with `<strong>orderNumber</strong> · partyName` (lines 126–132) — no Request Reasons row, no Total Devices row | Add the three-row meta card with the design-notes labels: `Order Number`, `Request Reasons`, `Total Devices`. Compute `Total Devices = missingLines.length + wrongLines.length + encumberedLines.length` per anomaly #9. |
| CRITICAL | Layout | Group sections render as simple `<ul>` lists, not as the collapsible cards with sort-able tables specified in Figma. Figma columns per group: Missing → `Barcode · Device Description · Amount Paid`; Wrong → `Expected Device · Received Device (IMEI or model name) · Photos (optional)`; Encumbered → `Barcode · Device Description`. | Per-group `<table>` with named columns, expand/collapse via `Show Details` / `Hide Details` toggle, `Pen + Edit` button per group | `<ul><li>{barcodeSubmitted}</li></ul>` per group (lines 137–141, 152–158, 165–169) | Replace lists with tables matching the column sets above. Add the `Show Details / Hide Details` chevron toggle (`Hide Details` for default-expanded, `Show Details` for collapsed). Add per-group `Edit` button → routes to the matching `/new/{reason}?id=X` step. |
| HIGH | Layout | Bottom button row is `Back` + `Submit Request`. Figma confirmation (pre-submit) variant shows `Edit` + `Cancel` + `Submit Request` in that order in the bottom-right cluster (design notes §2.5.4 and §3.4). | `Edit` (gray + Pen icon) + `Cancel` (gray) + `Submit Request` (green primary) | `Back` (gray) + `Submit Request` (green) | Replace `Back` with `Cancel` (route to landing) and add `Edit` (collapses summary back to a non-confirmation state). If the "expanded confirmation" variant is out of scope, at minimum rename `Back` to `Cancel` to match the design-notes rule that intermediate-step `Back` does not exist on Summary. |
| HIGH | Copy | Submitted-state modal has a `Back to Credit Requests` button. Figma confirmation modal has **no buttons** — just the icon + `Request submitted!` heading (design notes §2.5.5 + §4: "There are **no buttons rendered in the modal**"). | Icon + heading only, dismiss by scrim / Escape / auto-route | Icon + heading + `Back to Credit Requests` button (lines 211–221) | Remove the button. Keep the scrim-click and Escape dismissals already wired up. The design notes call out this is intentional — auto-dismiss after a short delay + scrim/Esc dismissal. Optionally add a `setTimeout` auto-redirect after ~2s. |
| HIGH | Typography | Same root cause as Surfaces 1 + 2 — wizard inherits Brandon Grotesque from body. Figma specifies Founders Grotesk on every Summary text style including the `Request submitted!` heading (Founders Grotesk Medium 26 per typography table). | Founders Grotesk | Brandon Grotesque (inherited) | Same fix — declare `font-family: 'Founders Grotesk', ...` on `.page`. The `.confirmHeading` rule sets `font-family: inherit` so fixing the root cascades. |
| MEDIUM | Layout | `.summarySection` border-radius is `12px`. Figma spec card radius is `8px` (design notes §3.2). Same defect as the Start step `.card`. | 8px | 12px | Drop to 8px. |
| MEDIUM | Layout | `.summarySection` has `border: 1px solid #E6E5E4`. Figma uses shadow-only treatment, no border. | Box-shadow only | 1px border, no shadow | Replace border with `box-shadow: 0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px 0px rgba(0,0,0,0.1)`. |
| MEDIUM | Typography | `.summarySection h3` uses `font-size: 18px`. Figma group-header style (`Missing Devices (25)`) is `Display/sm = 24px Medium 500`. | 24px / 500 | 18px / 500 | Bump to 24px. |
| MEDIUM | Copy | Group headings use `Missing Devices ({count})`. Figma format is `Missing Devices (25)` — pluralization matches, count formatting matches. **However**, when a group is collapsed (default per design notes §2.5.1), the count badge still shows the parenthesized number. Code already prints `({len})`, which matches. No fix needed; verifying. | `(count)` parenthesized after name | Same | — |
| MEDIUM | Behaviour | Missing-devices section helper `Shipment damaged: <strong>{detail.shipmentDamaged}</strong>`. Figma renders this on the Credit Request **Detail page** (read-only post-submit view), not on the wizard summary review (which shows the in-wizard editable structure — damage prompt belongs to the missing-step card). | Damage prompt lives in Step 2 review block, not in Summary | Rendered in Summary (line 142–144) | Either remove from Summary (matches Figma summary view) or relocate to a more appropriate place. The Detail page label is `Shipment damaged?` not `Shipment damaged:` (verbatim from design notes line 712). |
| MEDIUM | Color | Confirmation modal panel `#F0F6EF`, border `12px solid #F7F5F1`, scrim `rgba(28,27,28,0.5)` — all match Figma. ✓ Verified. | All match | All match | — |
| LOW | Layout | `.confirmModal` z-index 50 vs Figma's overlay layering — not directly specified, acceptable. | — | z-index 50 | — |
| LOW | Spacing | Bottom button-row gap is `12px`, Figma is `8px`. Same nit as Surface 2. | 8px | 12px | Drop to 8px. |

### Sprint 4 additive (not parity defects)

- `partyName` rendered next to order number in the "Order" section — additive context not in Figma, acceptable enrichment.
- `Please fix the following before submitting:` validation banner — additive; Figma has no submit-time validation banner because the model is "validation happens server-side and you'd surface a toast". Acceptable.

### Confirmed-OK summary

- Layout: page background, breadcrumb, step indicator (Summary node active), bottom button row ordering (gray secondary + green primary) match Figma.
- Color: confirmation modal panel `#F0F6EF`, halo border `12px #F7F5F1`, scrim `rgba(28,27,28,0.5)`, success heading color `#1C1B1C` all match Figma color tokens.
- Color: `CircleCheckIcon` fill `#14AC36` matches `Eco Green` (`fill_CRTE1J`).
- Typography: confirmation heading `font-size: 26px; font-weight: 500` matches the Figma `style_CAKW1V` token (family is wrong cascade, see HIGH).
- Copy: confirmation heading `Request submitted!` — verbatim from design notes line 849.
- Copy: `Submit Request` primary button label — verbatim.
- Behaviour: scrim-click and Escape-key dismissal both wired up — matches design notes §2.5.5 ("auto-dismiss / click scrim to dismiss"), with the documented accessibility justification for the explicit dismiss control (which itself is the HIGH finding — the dismiss button should be removed but the keyboard/scrim handlers kept).

---

## Executive summary

**Total findings across 3 surfaces:** 3 CRITICAL · 9 HIGH · 14 MEDIUM · 3 LOW.

**Top 3 things that need attention:**

1. **Summary step is structurally incomplete.** The metadata card (`Order Number / Request Reasons / Total Devices`), the per-group sortable tables, the `Show/Hide Details` toggle, the per-group `Edit` button, and the `Edit + Cancel + Submit Request` bottom row are all missing or wrong. The summary currently renders a stripped-down `<ul>` list of barcodes that doesn't match the Figma data-grid pattern. This is a multi-finding cluster — fix as a single re-skin of `SummaryStep.tsx`. The wrong H1 (`Review and submit`) and the extra modal dismiss button are part of the same cluster.

2. **Wrong typeface everywhere.** `wizard.module.css` never sets `font-family`, so the entire feature inherits Brandon Grotesque from the global body rule. Figma is explicit: Founders Grotesk on every text style (already loaded in `globals.css`). One-line fix on `.page` cascades correctly.

3. **Step 1 has a Cancel button it shouldn't have.** Figma design notes anomaly #15 calls out that Cancel only appears on Summary; intermediate steps rely on the breadcrumb. Plus the landing primary CTA label has a stray `+ ` prefix that isn't in Figma. Both are 1-line edits and worth doing together for copy parity.
