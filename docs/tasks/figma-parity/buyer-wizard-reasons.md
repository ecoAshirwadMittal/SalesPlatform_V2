# Figma parity — Buyer wizard reason steps (Missing / Wrong / Encumbered)

Source: Figma file `rYKB9vBqlJOFUuGN7GAgQS` ("2026 Auction: Partial Credit"), canvas `173-600` ("Submit Credit Request").
Reference dump: `docs/tasks/partial-credit-sprint2-design-notes.md`.
Method: spec-against-code (no screenshots). Pulled 2026-05-12.

Shared CSS module under review: `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
Shared step indicator: `frontend/src/app/(dashboard)/wholesale/partial-credit/StepIndicator.tsx`
Global tokens: `frontend/src/app/globals.css` (body uses `--font-family-primary = 'Brandon Grotesque'`; wizard inherits via `font-family: inherit`).

---

## Surface 3: Wizard step 2 — Missing (/wholesale/partial-credit/new/missing)
**Figma node:** 173-600 § "Missing Devices" (lines 31308–43480; frames `Missing Device - Start` line 31352, `Start Errors` 32024, `File Uploaded` 32715, `Photo Required Error` 33465, `Barcodes Entered` 34199, `Yes Damage` 34866, `No Damage` 35593, `Yes Damage - Photo Added` 36272, `Barcode List` 37065, `Add Missing Devices` 38947).
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/missing/MissingDevicesStep.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/missing/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Start, Start Errors, File Uploaded, Photo Required Error, Barcodes Entered, Yes Damage, No Damage, Yes Damage - Photo Added, Barcode List, Add Missing Devices modal.

### Findings
| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy | Card subtitle wording does not match Figma | `Copy and paste the barcodes into the text field below or upload a file listing the barcodes.` (`Text/base` 18px) | `Paste the barcodes below, or drop in an xlsx / csv / docx file and we'll extract them.` | Replace string in `MissingDevicesStep.tsx` line 134-136 with the verbatim Figma copy. |
| CRITICAL | Copy / Layout | Missing **OR** divider between textarea and dropzone | `**OR**` (bold divider sits between textarea and dropzone) | No divider rendered — file-drop row sits above the textarea with no separator | Add a centered bold "OR" divider element between the dropzone block and the textarea. |
| CRITICAL | Layout | Section ordering is inverted | Order: textarea → `OR` divider → dropzone (single barcode-entry card). Damage question lives in a **separate card** below | Order: dropzone row → textarea (same card). Damage question correctly in second card | Reorder JSX so textarea comes first inside the card, then OR divider, then dropzone. |
| CRITICAL | Behaviour | Photo-required validation branch is not implemented | After "Yes" damage with no photo, page should expose `Add photos of the damaged shipment` heading + dropzone, and on submit show inline error `Add a photo of the damaged shipment` | Code shows only a helper note `Photo uploads will be available in a future update. You can still proceed.` and lets Next proceed without photos | Sprint 4 scope — already flagged. Replace placeholder copy with the eventual Figma photo dropzone. (Flag, not a parity miss for current sprint.) |
| CRITICAL | Behaviour | Inline validation errors not rendered | Two red `Text/sm` errors: `Enter or upload the missing device barcodes` (under barcodes) and `Select an answer` (under damage radio) | No inline error UI — Next button is just disabled until valid | Wire `.errorText` (already in CSS) under each card; expose validation state when Next is clicked while invalid. |
| CRITICAL | Behaviour | Parsed barcode-list review screen missing | After Next, design shows `Missing Devices (25)` parsed-list table with sortable column `Missing Device Barcode`, row delete `xmark`, `Add More Devices` button, and the duplicate/invalid banner *inside this view* | Code routes straight to next step (wrong / encumbered / summary) — no in-step parsed review | Add a parsed-review stage (mirror `WrongDevicesStep`'s two-stage pattern). |
| CRITICAL | Behaviour | `Add Missing Devices` modal not implemented | Modal: heading `Add Missing Devices`, subtitle as Figma copy, textarea + OR + dropzone, `Add` CTA, top-right `xmark` close | No modal exists in this surface | Build the modal (Sprint 4 / closeout scope per implementation plan). |
| HIGH | Typography | Body font family is wrong | Founders Grotesk (entire wizard — design notes §3.6) | `font-family: inherit` resolves to `Brandon Grotesque` from `--font-family-primary` (globals.css line 91) | Either set `font-family: 'Founders Grotesk', 'Brandon Grotesque', sans-serif;` on `.page` / `.heading` / `.card` or introduce a new wizard-scoped variable. |
| HIGH | Typography | Field label weight too heavy | `Text/base (strong)` — Founders Grotesk Medium **500**, fontSize **18px** | `.fieldLabel` is `font-weight: 600`, `font-size: 14px` | Update to `font-weight: 500; font-size: 18px;` to match Figma. |
| HIGH | Typography | Card subheading sizing too small | `Text/base` regular — fontSize **18px**, `#1C1B1C` | `.cardSubheading` `font-size: 14px; color: #534F4C;` | Bump to 18px and `#1C1B1C` (or `#3C3C3C`). |
| HIGH | Layout | No semantic field-label "Barcodes" in Figma | Figma uses the card subtitle as the only label above the textarea (no separate `Barcodes` label) | Renders an explicit `Barcodes` label above the textarea | Remove the explicit `<label htmlFor="missing-barcodes">Barcodes</label>` — replace with the subtitle copy alone (a `sr-only` label is fine for a11y). |
| HIGH | Copy | Textarea placeholder wrong | `Enter barcodes` | `Enter barcodes (one per line or comma-separated)` | Trim placeholder to `Enter barcodes`. |
| HIGH | Color | Card border treatment | Figma cards have **no visible border**, only a subtle drop shadow `0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px 0px rgba(0,0,0,0.1)`, radius **8px** | `.card` has `border: 1px solid #E6E5E4; border-radius: 12px;` and no shadow | Drop the border, set `border-radius: 8px`, add the design-notes shadow. |
| HIGH | Spacing | Card padding off | Figma: `24px 32px` | `.card`: `28px 32px` | Change vertical padding to 24px. |
| HIGH | Color | Warning banner palette wrong | Figma banner uses neutral surface (`#F26B21` accent only on inline copy or as left-bar). The "Removed N duplicate…" banner is shown as a soft warning, not as an orange-stroked box. | `.warningBanner` uses `background: #FFF3EC; border: 1px solid #F26B21;` — heavy orange box that also doubles as the inline error surface | Add a separate `.errorBanner` (red) for `error` state and keep `.warningBanner` for the reconciliation message; tone the warning down to neutral pill or use `#FBFAF8` background per design notes §3. |
| MEDIUM | Spacing | Heading margin-bottom oversized | Implicit Figma gap from heading → step indicator → card is **32px** between major sections, **16px** between heading and step row | `.heading` has `margin: 0 0 32px;` then `.stepIndicator` has `margin-bottom: 40px;` — total gap ≈72px before first card | Reduce `.stepIndicator` margin-bottom to 32px. |
| MEDIUM | Behaviour | Helper "{n} barcode(s) entered" not in Figma | Figma shows no live count under the textarea | `<p className={styles.helperText}>{barcodes.length} barcode(s) entered</p>` | Either remove or move to count badge `(N)` next to a section heading per Figma. |
| MEDIUM | Copy | Breadcrumb separator wrong glyph | Figma uses chevron icon, breadcrumb label is just `All Credit Requests` | Code uses `›` with `&nbsp;` padding and appends `Missing Devices` | Use the chevron from icon set; drop the literal trailing segment or add it as plain non-link text only if Figma does (Figma shows only the back-link). |
| MEDIUM | Spacing | Step indicator gap between nodes | Figma `Step Horizontal` row width fixed 800px with `space-between` distribution and a continuous 1px line behind circles | Indicator uses flex `gap: 12px` per node, no fixed width, divider rendered as a 32px line **between** nodes | Re-implement as fixed-800px row with absolute-positioned line or flex with auto-spacing. |
| MEDIUM | Color | Step circle size | Figma circle implied ~32px (design notes §3.7 button height 40px; circles smaller) — borderless when active/done; 1.5px stroke when inactive | `.stepCircle` is 32px with `border: 1.5px solid #A8A7A6` even on active/done — circles override border in `.stepCircleActive/Done` so OK, but inactive font color `#7D7B7A` matches | Confirmed-OK for inactive; verify active variant pixel-matches Figma green. |
| LOW | Color | Disabled primary CTA opacity | Figma: opacity 0.4 | `.buttonPrimary:disabled { opacity: 0.4; }` | Confirmed-OK. |
| LOW | Spacing | Button width | Figma: all CTA buttons fixed 200×40 (design notes §3.7) | Code `.buttonPrimary` and `.buttonSecondary` use intrinsic width (padding `10px 20-24px`) | Apply `min-width: 200px; height: 40px` if pixel parity required. |
| LOW | Behaviour | Hover state on primary | Figma: no documented hover state in slice | `.buttonPrimary:hover { background: #0F9C2E; }` | Confirmed-OK (sensible default). |

### Confirmed-OK summary
- Page background `#F7F7F7` matches Figma `fill_82WWSQ`.
- Primary CTA color `#14AC36` matches `Eco Green`.
- Disabled CTA opacity 0.4 matches Figma.
- Step circle done/active fill `#14AC36` matches.
- Heading size 40px + weight 500 + color `#1C1B1C` matches `Display/lg` (modulo font-family swap above).
- Radio accent-color `#14AC36` matches.
- Breadcrumb color `#3C3C3C` matches secondary text token.
- Damage question card placement (separate card) matches Figma.

---

## Surface 4: Wizard step 3 — Wrong (/wholesale/partial-credit/new/wrong)
**Figma node:** 173-600 § "Wrong Device" (lines 21890–31307; frames `Wrong Device` 21898, `Barcodes Entered` 22502, `Actual Device Details` 23124/25071, `Add Photos` 27068, `Edit Photos` 29122).
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/wrong/WrongDevicesStep.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/wrong/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Entry, Barcodes Entered, Actual Device Details (default + populated), Add Photos modal, Edit Photos modal.

### Findings
| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy | Card subtitle wording wrong | `Copy and paste the barcodes into the text field below or upload a file listing the barcodes.` | `Copy and paste the expected barcodes into the text field below.` | Replace string in `WrongDevicesStep.tsx` line 114 with the verbatim Figma copy. |
| CRITICAL | Layout | Missing dropzone + OR divider on entry | Entry frame shows textarea, `**OR**` divider, file dropzone with `Click or drag and drop file here to upload` / `Accepted formats: .xlsx, .csv, .docx` | Wrong-step entry has only a textarea — no dropzone, no OR | Add the dropzone (reuse the file-drop block from `MissingDevicesStep` once the textarea→OR→dropzone pattern is consolidated). |
| CRITICAL | Copy | Stage-2 heading deviates from Figma | Figma keeps the heading as `Which devices were you expecting?` across entry AND parsed-table stage; the table itself carries the section heading `Wrong Devices (N)` | Code swaps heading to `What did you receive instead?` once `stage === 'details'` | Keep the heading static; add a section header `Wrong Devices (N)` above the table instead. |
| CRITICAL | Layout | Missing `Photos` column with optional helper | Table columns: `Expected Device`, `Received Device (IMEI or model name)`, `Photos (optional)` + per-row `xmark` row-delete | Table renders only `Expected Device` and `Received Device (IMEI or model name)` — no Photos column, no row-delete `xmark` | Add a third `<th>Photos <span class="muted">(optional)</span></th>` column and a per-row `xmark` delete affordance. Photo button is Sprint 4 scope but the column placeholder belongs to the Figma layout. |
| CRITICAL | Behaviour | `Add Photos` / `Edit Photos` modals not implemented | Modal heading `Add Photos` (`Display/sm`), subheading `Received Device: <model>`, dropzone, CTA `Add`. Edit variant adds file thumbnails with `xmark` to remove and CTA `Save`. | No modal code in this surface | Build both modals (Sprint 4 scope per plan — flag, not parity miss for current sprint). |
| CRITICAL | Behaviour | Sort affordances absent on table | Each column header has `arrows-up-down` sort icon | Plain `<th>` text only | Add sort-icon buttons or note as Sprint 4 if not required at parity. |
| CRITICAL | Behaviour | Count badge missing | Section header should read `Wrong Devices (16)` (count of rows) | No section header at all above the table | Add a section heading element above the table when in stage 'details'. |
| HIGH | Typography | Body font family wrong (same as Surface 3) | Founders Grotesk | Inherits Brandon Grotesque from globals body | Same fix as Surface 3. |
| HIGH | Typography | Table header style off | Figma `Text/sm (strong)` Founders Grotesk Medium 500 / 16px on `#1C1B1C` | `.gridTable th` is 14px, weight 500, color `#3C3C3C`, background `#F7F7F7` | Match: 16px, color `#1C1B1C`, white background. |
| HIGH | Copy | Textarea placeholder | `Enter barcodes` | `Enter barcodes (one per line or comma-separated)` | Trim placeholder. |
| HIGH | Copy | Per-row input placeholder | Figma row input shows `Enter IMEI or model name` (per the parsed-table populated frame, line 23124+) | Code uses `Enter IMEI or model name` | Confirmed-OK. |
| HIGH | Color | Card border / shadow same issue as Surface 3 | Shadow only, radius 8px | 1px border, radius 12px | Same fix. |
| HIGH | Spacing | Card padding same issue as Surface 3 | `24px 32px` | `28px 32px` | Same fix. |
| MEDIUM | Behaviour | "Back" semantics | Figma Back goes one frame back (entry → previous step; details → entry) | Code: `stage === 'details'` → `setStage('enter')`; otherwise `router.back()` — matches | Confirmed-OK. |
| MEDIUM | Behaviour | Reconciliation banner placement | Figma renders `Removed N duplicate and M barcodes that were not in the original order` above the parsed table | Banner shown above textarea on entry AND above table on details — both correct after `setWrongLines` returns | Confirmed-OK. |
| MEDIUM | Layout | Stage label changes heading instead of using a sub-heading row | (see Critical above) Figma keeps heading and adds section title row | Heading mutates | Reverted under Critical fix. |
| LOW | Behaviour | Disabled state on Next | Figma disables Next until ≥1 row populated (implied) | Code disables only on `barcodes.length === 0` (entry) and `submitting` (details). No validation that each row has a `actualImeiOrModel`. | Add per-row required validation if Figma intends "all rows must identify received device". |
| LOW | Color | Row hover not styled | Figma slice shows no hover styling | No hover rules | Confirmed-OK. |

### Confirmed-OK summary
- Two-stage flow (entry → parsed details) mirrors Figma sequence.
- StepIndicator usage matches (`current="wrong"`).
- Breadcrumb pattern matches Surface 3.
- Card shell + back/next buttons reuse shared wizard CSS.

---

## Surface 5: Wizard step 4 — Encumbered (/wholesale/partial-credit/new/encumbered)
**Figma node:** 173-600 § "Encumbered Devices" (lines 18979–21889; frames `Encumbered Device` 18987, `File Uploaded` 19601, `Barcodes Entered` 20316, `Encumbered Device Summary` 20931).
**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/encumbered/EncumberedDevicesStep.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/encumbered/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/wizard.module.css`
**Variants reviewed:** Entry, File Uploaded, Barcodes Entered, Encumbered Device Summary.

### Findings
| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy | Card subtitle wording wrong | `Copy and paste the barcodes into the text field below or upload a file listing the barcodes.` | `Copy and paste the barcodes into the text field below.` | Append `or upload a file listing the barcodes.` so the copy matches exactly. |
| CRITICAL | Layout | Missing dropzone + OR divider on entry | Entry frame: textarea, `**OR**` divider, dropzone `Click or drag and drop file here to upload` / `Accepted formats: .xlsx, .csv, .docx` | Only a textarea is rendered — no dropzone, no OR divider | Add the dropzone block (consolidate with the missing-step file-drop pattern; encumbered should also support file upload at parity). |
| CRITICAL | Behaviour | Parsed summary table missing | Figma `Encumbered Device Summary` frame: `Encumbered Devices (N)` count header, warning banner `Removed N duplicate…`, `Add More Devices` button, single-column table `Encumbered Device Barcode` with per-row `xmark` delete | Code routes straight to summary after `setEncumberedLines` — no in-step parsed list | Add the parsed-summary stage (mirror Wrong-step two-stage flow). |
| CRITICAL | Behaviour | `Add More Devices` modal not implemented | Implicit modal mirroring `Add Missing Devices` (heading + dropzone + Add CTA) | Not implemented | Build modal (Sprint 4 closeout scope per plan). |
| CRITICAL | Behaviour | Reviewer-only fields verification | Encumbered admin review page is expected to expose `Actual Value` and `Prolog Result` inputs. **These must NOT appear on the wizard step.** | Wizard renders neither — only barcode entry. | Confirmed-OK — wizard correctly omits reviewer-only inputs. |
| HIGH | Typography | Body font family wrong (same as Surface 3) | Founders Grotesk | Inherits Brandon Grotesque from globals body | Same fix as Surface 3. |
| HIGH | Color | Card border / shadow same issue as Surface 3 | Shadow only, radius 8px | 1px border, radius 12px | Same fix. |
| HIGH | Spacing | Card padding same issue as Surface 3 | `24px 32px` | `28px 32px` | Same fix. |
| HIGH | Copy | Textarea placeholder | `Enter barcodes` | `Enter barcodes (one per line or comma-separated)` | Trim placeholder. |
| HIGH | Copy | Field label "Barcodes" not in Figma | Card subtitle is the only label above the textarea | Renders explicit `<label htmlFor="encumbered-barcodes">Barcodes</label>` | Remove label or convert to `sr-only`. |
| MEDIUM | Behaviour | Helper "{n} barcode(s) entered" not in Figma | No live count | `<p className={styles.helperText}>{barcodes.length} barcode(s) entered</p>` | Remove or move to summary count badge. |
| MEDIUM | Layout | Breadcrumb trailing text appended | Figma breadcrumb shows only `All Credit Requests` link with chevron | Code appends `Encumbered Devices` after `›` | Drop the trailing text or only render it as plain non-link breadcrumb on the parsed-summary stage. |
| MEDIUM | Copy | Sprint 4 R-2 / RMA warning intentionally omitted from wizard | Per design-notes §6 note 2, the R-2 warning lives on the landing banner, not inside this step | Code correctly omits it | Confirmed-OK. |
| LOW | Behaviour | Hover state on primary CTA | Figma slice shows no documented hover | `.buttonPrimary:hover { background: #0F9C2E; }` | Confirmed-OK. |
| LOW | Color | Disabled CTA opacity 0.4 | Matches Figma | Confirmed-OK. |

### Confirmed-OK summary
- No damage question card (correctly omitted — Figma has it only on Missing).
- StepIndicator usage matches (`current="encumbered"`).
- Submit-pipeline routes to summary after persist.
- Page background `#F7F7F7` matches.
- Primary CTA `#14AC36` matches.
- Reviewer-only Actual Value / Prolog Result inputs correctly absent from wizard.

---

## Cross-surface shared findings (apply to all three)

These are issues that originate from `wizard.module.css` or `StepIndicator.tsx` and surface on every wizard step. Fix once, propagates everywhere.

| Severity | Dimension | Finding | Figma | Code | Fix hint |
|---|---|---|---|---|---|
| HIGH | Typography | Wrong font family across the wizard | Founders Grotesk Regular 400 / Medium 500 for body, headings, labels, buttons | Inherits Brandon Grotesque via `--font-family-primary` (globals.css) | Add `font-family: 'Founders Grotesk', 'Brandon Grotesque', sans-serif;` to `.page`, `.heading`, `.card`, and button classes — or introduce `--font-family-wizard`. |
| HIGH | Color | Card chrome wrong | Shadow `0 1px 2px -1px rgba(0,0,0,0.1), 0 1px 3px 0 rgba(0,0,0,0.1)`, radius **8px**, no border | `border: 1px solid #E6E5E4; border-radius: 12px;` no shadow | Update `.card`. |
| HIGH | Spacing | Card padding | `24px 32px` | `28px 32px` | Update `.card`. |
| MEDIUM | Layout | Step indicator width / divider line | Fixed 800px wide row, single continuous 1px line behind nodes via `space-between` | Per-node 32px divider between flex items | Re-implement geometry per design notes §3.1. |
| MEDIUM | Color | Warning banner overloaded | Reconciliation banner is a soft warning; error states should be red | Single `.warningBanner` with orange stroke serves both reconciliation banner and inline error | Split into `.warningBanner` (neutral) and `.errorBanner` (`#EB3300`). |
| MEDIUM | Spacing | Form max-width | Form-card max width 800px (design notes §3.7) | `.card` max-width 880px | Reduce to 800px for parity. |

---

## Executive summary

**Total findings across the three wizard reason surfaces (incl. cross-surface shared block):**

- **CRITICAL:** 16 (5 on Missing, 7 on Wrong, 4 on Encumbered)
- **HIGH:** 16 (6 on Missing, 5 on Wrong, 5 on Encumbered) + 3 cross-surface
- **MEDIUM:** 10 (3 on Missing, 3 on Wrong, 2 on Encumbered) + 3 cross-surface (counted once)
- **LOW:** 6 (3 on Missing, 2 on Wrong, 2 on Encumbered)

**Top 3 things that need attention:**

1. **Missing barcode-entry pattern parity** — every reason step is supposed to render `textarea → "OR" divider → file dropzone` inside one card. Wrong and Encumbered steps have no dropzone at all; Missing has the file-drop chip but no OR divider and the wrong copy. The verbatim card subtitle (`Copy and paste the barcodes into the text field below or upload a file listing the barcodes.`) is also wrong on all three steps.
2. **In-step parsed review screens are completely missing on Missing and Encumbered.** Figma defines `Missing Device - Barcode List` and `Encumbered Device Summary` as in-step stages with a section heading + count, the reconciliation banner, an `Add More Devices` button, and a single-column delete-able table. Today the code persists and immediately routes to the next step, skipping that review. Wrong step has a two-stage flow but is missing the Photos column, sort icons, and count badge.
3. **Typography drift.** Every wizard surface inherits Brandon Grotesque from `--font-family-primary`, but Figma specifies Founders Grotesk across the canvas. Combined with the field-label weight/size mismatch (Figma `Text/base (strong)` = Medium 500 / 18px; code = 600 / 14px) and the card subheading size (18 vs 14), the wizard reads visually lighter and smaller than the design.
