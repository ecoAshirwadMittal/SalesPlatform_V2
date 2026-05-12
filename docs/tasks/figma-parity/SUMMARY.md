# Partial Credit — Figma Parity Review · Master Triage

**Date:** 2026-05-12
**Method:** spec-against-code per `partial-credit-figma-parity-review-plan.md`
**Coverage:** 12 surfaces across 6 parallel subagent runs (2 batches × 3 agents)

## Headline

Partial Credit Phase 1 is **functionally complete** (124/124 backend tests
green, every endpoint working) but has **substantial design-parity drift**
from the Figma source of truth. The drift is dominated by a handful of
cross-cutting issues plus per-surface layout gaps — not by hundreds of
independent problems.

## Total findings by severity

| Severity | Count |
|---|---|
| **CRITICAL** | 31 |
| **HIGH** | 59 |
| **MEDIUM** | 71 |
| **LOW** | 32 |
| **Total** | **193** |

## Findings by surface

| # | Surface | CRIT | HIGH | MED | LOW | Total | Report |
|---|---|---:|---:|---:|---:|---:|---|
| 1 | Buyer landing | — | — | — | — | (rolled up below) | `buyer-landing-start-summary.md` |
| 2 | Wizard step 1 (Start) | — | — | — | — | (rolled up below) | `buyer-landing-start-summary.md` |
| 6 | Wizard step 5 (Summary) | — | — | — | — | (rolled up below) | `buyer-landing-start-summary.md` |
| 1+2+6 | **Buyer landing + Start + Summary** | **3** | **9** | **14** | **3** | **29** | `buyer-landing-start-summary.md` |
| 3+4+5 | **Buyer wizard reason steps (Missing/Wrong/Encumbered)** | **16** | **19** | **13** | **6** | **54** | `buyer-wizard-reasons.md` |
| 7 | **Buyer detail page** (Sprint 4) | **7** | **7** | **9** | **5** | **28** | `buyer-detail-page.md` |
| 8+10 | **Admin landing + Status config** | **2** | **11** | **15** | **6** | **34** | `admin-landing-status-config.md` |
| 9 | **Admin review detail** | **3** | **9** | **8** | **3** | **23** | `admin-review-detail.md` |
| 11+12 | **Email templates + On-behalf modal** | **0** | **4** | **12** | **9** | **25** | `email-templates-on-behalf.md` |

## Cross-cutting issues (fix once, helps every surface)

These appeared in **every single report**. Fixing them at the design-token
layer will close a large fraction of HIGH + MEDIUM findings in one pass.

### 1. Wrong font family (HIGH × ~6 surfaces)
- **Spec:** Founders Grotesk (declared in Figma, font file already loaded
  in the app via `@font-face`).
- **Code:** `--font-family-primary` in `frontend/src/app/globals.css` points
  at **Brandon Grotesque**. Wizard / admin / detail all inherit it.
- **Fix:** retarget `--font-family-primary` to Founders Grotesk, or
  introduce a `--font-family-partial-credit` and apply it on each
  module's root container.
- **Blast radius:** every partial-credit surface; verify it doesn't
  regress non-partial-credit pages that may have relied on the default.

### 2. `--color-brand-green` fallback is teal, not green (HIGH × ~5 surfaces)
- **Spec:** primary CTA + accent green is `#14AC36`.
- **Code:** the CSS-var fallback is `#407874` (teal) — older auction surface
  uses teal, partial-credit uses green. Multiple CSS modules use
  `var(--color-brand-green, #407874)` and the var likely isn't defined,
  so they all render teal.
- **Fix:** either define `--color-brand-green: #14AC36` in `globals.css`,
  or change every partial-credit fallback to `#14AC36`. The token
  approach is cleaner.

### 3. No Figma frame exists for 3 surfaces
- **Surface 10** (Status config), **Surface 11** (Email templates editor),
  **Surface 12** (On-behalf modal) have **no Figma artwork**. Sprint 3
  and Sprint 4 plans called for them but no designer frame was ever
  produced.
- **Implication:** "parity" reviews for these surfaces reduce to "matches
  the plan §5.2 / §5.3 design intent + stays coherent with the design
  system tokens". They cannot be pixel-perfect-compared.
- **Action:** if the team wants these surfaces designed properly, schedule
  Figma frames before any cosmetic refactor.

### 4. Plan doc had wrong Figma node ID for buyer detail
- Plan: `534-11895`. Actual: `534:11349` ("Credit Request Detail Page"
  with Wrong-Device / Missing-Device / Multi-Reasons variants).
- **Action:** fix the plan doc + sprint-4 implementation-plan reference.

## Top 10 fixes (sorted: CRITICAL first, then by blast radius)

| # | Surface | Severity | Issue | Estimated effort |
|---|---|---|---|---|
| 1 | Wizard step 5 (Summary) | CRITICAL | Structurally incomplete — missing meta card, sortable tables, Show/Hide Details toggle, per-group Edit button, bottom-row Edit + Cancel + Submit Request. Ships as a basic `<ul>` of barcodes. | L (½-1 day) |
| 2 | Buyer wizard (Missing/Wrong/Encumbered) | CRITICAL | Barcode-entry card layout broken on Wrong + Encumbered — Figma specifies textarea → OR divider → file dropzone; neither step has the dropzone. Missing has Sprint 4's additive file-drop above the textarea (correct in spirit but wrong placement vs Figma). Subtitle copy wrong on all three. | M (½ day) |
| 3 | Buyer wizard (Missing + Encumbered) | CRITICAL | In-step parsed review screens missing entirely. Wrong has them but lacks the Photos column, sort icons, count badge, row-delete `xmark`. The two related modals (`Add Missing Devices`, `Add/Edit Photos`) are not implemented. | L (1-2 days) |
| 4 | Buyer detail page | CRITICAL | Reason-table columns wrong in **every** variant: Missing needs Box Number + Model Description (drop Grade); Wrong needs Received IMEI/Serial + Photos column (drop Latest Price); Encumbered should collapse Brand+Model → "Device Description" and Amount Paid+Actual Value → "Credit Due". | M (½ day) |
| 5 | Buyer detail page | CRITICAL | Multi-reason variant must use the `Tabs - Partial Credit` + `Toggle - Partial Credit` components from Figma. Code stacks three sections vertically with intermixed decisions. | M (½ day) |
| 6 | Admin landing | CRITICAL | Table column set wrong — Figma: `Date Submitted \| Buyer \| Company \| Order Number \| Request Reason \| Status` (7 cols). Code ships 8 cols starting with Request #. Realign set, order, and copy. | S (1-2h) |
| 7 | Admin landing | CRITICAL | Empty-state copy: Figma `There are currently no Partial Credit Requests to approve`, code `No partial credit requests match your filters`. | XS (line fix) |
| 8 | Admin review detail | CRITICAL | HeaderStrip leaks `partyName` into both "Buyer" and "Company" fields — they must be different (Buyer = contact name, Company = party_name). `HeaderStrip.tsx:35-36`. | XS (line fix) |
| 9 | Admin review detail | CRITICAL | Global "Accept All / Decline All" bulk banner is **not in Figma** — spec is per-section bulk only. Remove `AdminReviewClient.tsx:226-244`. Also fix Accept/Approve verb mismatch. | S (1h) |
| 10 | Admin review detail | CRITICAL | Wrong-device table has wrong column shape — code uses combined Expected/Received cells and a `Recommendation` column that Figma §8.6 forbids. Spec is 10 separate columns; recommendation should default the Action dropdown + provide a tooltip, not own a column. | M (½ day) |

## Pervasive HIGH-severity themes

Beyond the cross-cutting issues + top-10 above, these patterns showed up
in multiple reports and are worth batching into a single design-token
sweep PR:

- **Step indicator uses UTF-8 `✓` instead of Font Awesome `check` SVG.**
  Connector renders per-segment instead of one continuous line.
- **Status pills use tinted backgrounds + dark text; Figma uses neutral
  chrome with coloured text.** Five locations.
- **Card border-radius is 12px; Figma is 8px.** Five locations.
- **Cards use border instead of box-shadow.** Four locations.
- **Section cards miss the cream `#F7F5F1` background** on the admin
  review surface.
- **Page headings inconsistent.** Buyer detail uses `28px` for the
  request number; Figma uses `40px Display/lg` with literal "Credit
  Request Details" copy. Admin review is entirely missing the page
  heading.

## Open questions

These need a product / design decision before fixes can land:

1. **Are surfaces 10 / 11 / 12 supposed to have Figma frames?** Right now
   they're spec'd against plan docs only. Without frames the "polish
   sweep" for these will be inconsistent across reviewers.
2. **Founders Grotesk vs Brandon Grotesque** — is the partial-credit
   font choice intentional drift or an oversight? The font file is
   already loaded; flipping the CSS var is trivial but affects every
   surface.
3. **`Add Missing Devices` + `Add/Edit Photos` modals** — were these
   intentionally deferred to Phase 2, or are they Phase 1 scope that
   slipped? Sprint 2 design notes treat them as Sprint 2 deliverables.
4. **Recommendation column on the admin Wrong table** — keep as a
   column (current code) or move to tooltip-only (Figma spec)? The
   column is more discoverable; the tooltip matches the spec.

## Per-surface reports

- [Buyer landing + Start + Summary](./buyer-landing-start-summary.md)
- [Buyer wizard reason steps (Missing / Wrong / Encumbered)](./buyer-wizard-reasons.md)
- [Buyer detail page (Sprint 4)](./buyer-detail-page.md)
- [Admin landing + Status config](./admin-landing-status-config.md)
- [Admin review detail](./admin-review-detail.md)
- [Email templates + On-behalf modal](./email-templates-on-behalf.md)

## What we'd recommend doing next

In rough order of return-on-effort:

1. **Cross-cutting token fixes** (font family + brand-green fallback). One
   small PR. Closes a large fraction of HIGH findings in one shot.
2. **Admin landing column realignment + empty-state copy fix.** Small PR,
   highest-visibility surface for sales ops.
3. **Admin review HeaderStrip + bulk banner removal + Wrong table
   shape.** Medium PR, fixes the three remaining admin CRITICALs.
4. **Buyer detail reason-table columns + Multi-reason Tabs/Toggle
   variant.** Medium PR.
5. **Buyer wizard reason-step layout + Summary step rebuild.** Largest
   surface — defer until 1-4 land so reviewers see what's left.
6. **Design decision on surfaces 10 / 11 / 12** — schedule Figma frames
   or document as "design-system-coherent only" in the plan.
