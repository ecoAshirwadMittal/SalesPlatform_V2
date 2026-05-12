# Figma parity — Admin landing + Status config

**Drafted:** 2026-05-12
**Method:** spec-against-code (no screenshots). Source of truth for
Surface 8 is `docs/tasks/partial-credit-sprint3-design-notes.md` §2
(which itself is a verbatim dump of Figma node `213-610`, frames 1-2,
pulled 2026-05-11). Surface 10 has no Figma frame in node `213-610`;
review against `partial-credit-sprint3-implementation-plan.md` §5.4 +
§7 (SPKB-3664 intent).

> Note: a live `mcp__figma__get_figma_data(rYKB9vBqlJOFUuGN7GAgQS,
> 213-610)` call returned HTTP 429 (Figma seat rate-limited). The cached
> design-notes dump is exhaustive for node `213-610` and is treated as
> canonical for this review.

---

## Surface 8: Admin landing (/admin/auctions-data-center/partial-credit)

**Figma node:** `213-610` §2 (per design notes — frames 1 "Admin Landing —
Empty" and 2 "Admin Landing — Populated").
**Local files:**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/admin.module.css`

**Variants reviewed:**
- Empty state (Figma frame 1, design notes §2.5 empty-state copy)
- Populated state (Figma frame 2, design notes §2.4 column list)

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Layout / Copy | Table column list does not match Figma. Figma defines 7 columns: Date Submitted, Buyer, Company, Order Number, Request Reason, Status, (eye). Code renders 8 columns: Request #, Order #, Buyer (with sub-line for company), Reasons, Status, Total, Submitted, (eye). | `Date Submitted \| Buyer \| Company \| Order Number \| Request Reason \| Status \| (eye)` (design notes §2.4) | `Request # \| Order # \| Buyer \| Reasons \| Status \| Total \| Submitted \| (eye)` (page.tsx L294-301) | Restore Figma column order: drop Request #, drop right-aligned Total, split Buyer + Company into two cells, rename "Reasons"→"Request Reason", "Submitted"→"Date Submitted", "Order #"→"Order Number". |
| CRITICAL | Copy | Empty-state copy diverges from Figma. | `There are currently no Partial Credit Requests to approve` (design notes §2.5, Figma line 728) | `No partial credit requests match your filters` (page.tsx L285) | Use the Figma string verbatim. Filter-narrowed empty state is fine as a secondary branch, but the default empty state must use the Figma copy. |
| CRITICAL | Copy / Layout | Per-column sort + filter affordances missing. Figma headers each carry an `arrows-up-down` sort icon and an `Ab` filter trigger (Inter Medium 11px, `#534F4C`) — Date Submitted uses a `calendar-days` icon variant for date filtering. Code renders plain `<th>` text only and exposes filters in a separate row above the table. | Inline per-header sort icon + `Ab` filter button (design notes §2.4) | Plain `<th>`; filters live in `.filterRow` above the table (page.tsx L221-277, admin.module.css L146-194) | Add per-column sort icon + filter trigger UI, or document an explicit deviation. Acceptable to keep the dedicated filter row, but flag as Figma-intent miss. |
| HIGH | Layout | Download button placement: Figma puts the Download button **right-aligned next to the chip row** in the same heading row. Code places it inside `headingRowEnd` after the chip row, which matches Figma — verify by visual check that ordering is `chips → Download` (not reversed). Currently `headingRowEnd` flexes chips before button, which is correct. | Chips left, Download right (design notes §2.3) | `chipRow` → `downloadButton` inside same `.headingRowEnd` flex (page.tsx L176-205) | Confirmed OK — included as a check, not a defect. |
| HIGH | Color | Download button background fill mismatched. Figma uses `#14AC36` (primary green, opacity 0.4 when no rows; solid when populated). Code uses `var(--color-brand-green, #407874)` — the token resolves to `#14AC36` in `globals.css` L72 so the fallback is misleading, but the rendered color is correct. Verify the token. | `#14AC36` solid (populated) / 0.4 alpha (empty) (design notes §2.3) | `var(--color-brand-green)` → `#14AC36`. No opacity-shift in empty state (uses `disabled` opacity 0.6 instead). | Either drop the misleading `#407874` fallback in `admin.module.css` L51 (it's teal, not green) **or** confirm the token always resolves. Add a `rows.length === 0` disabled-look that matches Figma's 0.4 opacity. |
| HIGH | Color | Chip border colour drift. Figma: unselected chips use a 1px `#B7B5B5` border; selected chip uses a 2px `#B7B5B5` border. | `1px solid #B7B5B5` (unselected) / `2px solid #B7B5B5` (selected, same colour) (design notes §2.2) | `1px solid #B7B5B5` / `border-width: 2px` keeps same colour ✓ (admin.module.css L113-138) | Confirmed OK. |
| HIGH | Color | Chip selected-state background. Figma fills selected chip with `Light Yellow #FEF48F`; unselected with `Light Gray #EFECE4`. | `#FEF48F` / `#EFECE4` (design notes §2.2) | `#FEF48F` / `#EFECE4` ✓ (admin.module.css L117, 133) | Confirmed OK. |
| HIGH | Typography | Heading does not declare `font-family`. The page heading inherits the body font cascade. Figma is `Display/lg` = Founders Grotesk Medium 40px (mapped to Brandon Grotesque in this project per `globals.css` L91). Confirm inheritance gives Brandon Grotesque, otherwise the heading falls back to system fonts. | Founders Grotesk Medium 40px 110% line-height (design notes §2.1, §7) | `font-size:40px; font-weight:500; letter-spacing:-0.01em; line-height: (default)` — no explicit `font-family` and no explicit `line-height` (admin.module.css L20-26) | Add `font-family: var(--font-family-primary)` and `line-height: 1.1` to `.heading` so the heading style is stable regardless of body inheritance. |
| MEDIUM | Copy | Chip label punctuation: Figma renders `Pending Approval: 0` with a single colon-space separator and weight contrast between label and count (`{ts2}Label:{/ts2}{ts1}0{/ts1}` with normal weight on the count). Code splits into two `<span>`s with `gap: 6px` and renders `Pending Approval:` and `0` separately, both at weight 500. | `"Pending Approval: 0"` with label-strong / count-normal style split (design notes §2.2) | `<span>{label}:</span><span className={chipCount}>{count}</span>` with `gap: 6px`; both weight 500 (page.tsx L189-192, admin.module.css L119-142) | Either drop weight 500 on `.chipCount`, or accept the deviation — Figma's `{ts1}` count run is normal weight (`#1C1B1C`). Current visual is bolder. |
| MEDIUM | Copy | "Filters" label and "Clear filters" link are not in Figma. The Figma filter-affordance lives **inline per column** (`Ab` button on each `<th>`). Code introduces a banner-style filter row with an uppercase "FILTERS" label and a right-aligned "Clear filters" underline link. | Per-column inline `Ab` filter buttons only (design notes §2.4) | Standalone `.filterRow` with `FILTERS` label + 5 inputs + `Clear filters` link (page.tsx L221-277) | Acceptable additive UX, but should be documented as a deliberate deviation. Long-term: collapse into per-column controls to match Figma. |
| MEDIUM | Layout | Buyer column wraps two values (buyer-code top, party-name bottom) into a single cell. Figma uses **two separate columns** — Buyer and Company. | Two columns: `Buyer Name` + `Company Name` (design notes §2.5) | One cell with a stacked flex of code + party name (page.tsx L355-358) | Split into two `<td>`s and add a Company `<th>`. |
| MEDIUM | Copy | Status filter dropdown copy. The reason filter options say `Missing Device`, `Wrong Device`, `Encumbered Device` — these match Figma. However, the "All reasons" placeholder is Sprint-4-additive and not in Figma. Low risk. | (no filter UI in Figma) | `<option value="">All reasons</option>` (page.tsx L249) | Confirmed OK — UX-driven, no Figma rule. |
| MEDIUM | Spacing | `.gridTable th, .gridTable td` padding is `10px 14px`. Figma cells use `8px` padding. ≥4px drift in horizontal padding. | `8px` padding per cell (design notes §2.4 "each column ... 8px padding") | `padding: 10px 14px` (admin.module.css L214) | Reduce to `padding: 8px` (or `8px 10px` to keep readable width on the eye column). |
| MEDIUM | Color | Table cell border colour drift. Figma uses `0.81px #A8A7A6` per cell. Code uses `1px #E6E5E4` on the landing table (this matches the wider admin card pattern but not Figma's table-cell line). | `0.81px solid #A8A7A6` (design notes §2.5) | `1px solid #E6E5E4` (admin.module.css L215, 226) | Either swap to `#A8A7A6` to match Figma exactly, or accept as an intentional softer admin look. Note: the per-section table in the **detail** page already uses `0.81px #A8A7A6` (admin.module.css L468, 473) — keep landing consistent. |
| MEDIUM | Layout / Copy | Sprint-4 additive Download xlsx button and dismissible over-cap toast: not in Figma. Per the task brief these are additive features, **not** mismatches. Flagged here per the rubric instruction. | (Download button present in Figma but as a CSV/XLSX placeholder per §2.3) | `Download` button + `exportToast` over-cap branch (page.tsx L196-219) | No action — additive, intentional. |
| LOW | Copy | The breadcrumb row above the heading (`Admin › Auctions Data Center › Partial Credit`) is not in the Figma export — Figma jumps straight to the page heading. Reasonable shell-level addition. | (none — Figma frame starts at heading) | `<div className={styles.breadcrumb}>` (page.tsx L168-171) | Confirmed OK — shell convention, low concern. |
| LOW | Copy | Status `title` tooltip on the pill shows `row.systemStatus` (e.g. `UNDER_REVIEW`). Figma does not specify, but the impl plan §8.9 anomaly suggests admin reviewers want the internal status text on detail; this tooltip approach is a pragmatic compromise. | (not specified) | `title={row.systemStatus}` (page.tsx L366) | Confirmed OK. |
| LOW | Spacing | `.headingRow` has `margin-bottom: 24px`. Figma does not specify; matches the rest of the admin family. | (not explicit) | `margin-bottom: 24px` (admin.module.css L34) | Confirmed OK. |
| LOW | Behaviour | Chip hover state: code adds `background: #E5E2DA` on hover. Figma does not specify a hover state for the chips. | (none) | `background: #E5E2DA` (admin.module.css L128-130) | Confirmed OK — designed-state polish. |

### Confirmed-OK summary

- Page heading copy "Partial Credit Requests" matches Figma verbatim ✓ (design notes §2.1).
- Status chip set (4 chips: Pending Approval, Approved, Declined, All) matches Figma count + label set ✓.
- `Pending Approval` chip selected by default ✓ (matches design notes §2.2 default).
- Chip fill colours (`#FEF48F` selected / `#EFECE4` unselected) match Figma tokens ✓.
- Chip border colour `#B7B5B5` matches ✓; selected chip width-jitter compensation in `.chipActive` (padding adjustment) is a good detail.
- Status pill colour pulled live from `row.statusColorHex` per §11.Q5 ✓.
- Eye icon column rendered as a fixed-width action column ✓ (matches Figma `layout_TM4B6H` hug-width).
- Empty / loading / error banners present (additive over Figma — acceptable).
- Pagination footer present (additive — Figma frames have no pagination).
- Download button uses primary green token ✓ (after token mapping).

---

## Surface 10: Status config (/admin/auctions-data-center/partial-credit/statuses)

**Figma node:** `213-610` — **no resolved sub-frame**. See "Figma node
resolution notes" below.
**Local files:**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/statuses/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/statuses/statusConfig.module.css`

**Variants reviewed:** non-edit (read-only) row and inline-edit row.
Review is against `partial-credit-sprint3-implementation-plan.md` §5.4
(spec intent for the SPKB-3664 page) and §7 (effort/scope) — no Figma
counterpart exists for this surface.

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| HIGH | Color | Save button uses teal `#00969F` (and `#007680` hover). This is **not** a token from the partial-credit design system. The admin family of buttons in this codebase uses `--color-brand-green` (`#14AC36`) for primary CTAs (see `admin.module.css` L518 `.buttonPrimary`). | _no Figma_ — but partial-credit primary CTA is `#14AC36` (design notes §6.1) | `background: #00969F` hover `#007680` (statusConfig.module.css L177-185) | Switch `.buttonPrimary` to `background: var(--color-brand-green)` (`#14AC36`) with a `filter: brightness(0.9)` hover, matching the admin landing's `.downloadButton` pattern. |
| HIGH | Typography | Heading size is `28px` here vs. `40px` (`Display/lg`) on every other admin page. No spec rationale — the impl plan does not call for a smaller heading. | _no Figma_ — but admin family uses `Display/lg` 40px (design notes §7) | `font-size: 28px` (statusConfig.module.css L21) | Bump to `font-size: 40px; line-height: 1.1` to match the rest of the admin partial-credit family, **or** explicitly document a smaller subordinate-page heading style. |
| HIGH | Color | Default badge palette uses `#1F5A38` text on `#E6F4EC` background with a `#2D7A4E` border. None of these are tokens in the partial-credit token list (§6.1). Reads as "generic Bootstrap green". | _no Figma_ — but in-family green is `#14AC36` | `.defaultBadge { background:#E6F4EC; color:#1F5A38; border:1px solid #2D7A4E; }` (statusConfig.module.css L154-163) | Either retint to `--color-brand-green` family (e.g. `background: #D8E5D9; color: #14AC36; border: 1px solid #14AC36`) or replace with the standard pill chrome used on the landing. |
| HIGH | Color | Toast (success) palette also uses the `#2D7A4E`/`#E6F4EC`/`#1F5A38` Bootstrap-green family rather than the admin design tokens. | _no Figma_ — but admin success colour is `#14AC36` | `.toast { border:1px solid #2D7A4E; background:#E6F4EC; color:#1F5A38; }` (statusConfig.module.css L36-44) | Retint to brand-green tokens or reuse a shared `.toast` class from the admin module to keep the admin family visually unified. |
| HIGH | Layout | Heading row has a 1px bottom border `var(--color-input-border)`. Other admin partial-credit pages (landing + detail) do **not** use an underline beneath the heading row — they rely on whitespace + the table card border. | _no Figma_ — admin family uses whitespace separation | `.headerRow { border-bottom: 1px solid var(--color-input-border); padding-bottom: 12px; }` (statusConfig.module.css L13-17) | Drop the `border-bottom` to match the family treatment. Acceptable to keep if intentional, but flag explicit deviation. |
| MEDIUM | Typography | `font-family` is inherited (via cascade from `<body>`). The page does declare `var(--font-family-primary)` on individual elements (`.heading`, `.description`, `.grid`) which is good. Confirmed consistent. | (admin family uses Brandon Grotesque) | `font-family: var(--font-family-primary)` on `.heading`, `.description`, `.grid`, `.textInput`, etc. (statusConfig.module.css L20, 30, 66) | Confirmed OK — declarations are present and use the token. |
| MEDIUM | Typography | Table header uppercase + letter-spacing treatment (`text-transform: uppercase; letter-spacing: 0.04em`) is not used in the admin landing table. The landing uses regular-case `<th>` at 13px medium. | (admin landing uses `font-weight: 500; font-size: 13px; (regular case)` per admin.module.css L218-223) | `.grid th { font-weight:600; font-size:13px; text-transform:uppercase; letter-spacing:0.04em; }` (statusConfig.module.css L91-96) | Align with the admin landing: drop the uppercase + letter-spacing, drop `font-weight:600` → `500`. |
| MEDIUM | Copy | Page heading says "Partial Credit — Status Configuration" (em-dash separator + "Configuration"). Other admin partial-credit headings are short noun phrases (`Partial Credit Requests`, `Request Details`). Reasonable but heavier than the family. | _no Figma_ | `"Partial Credit — Status Configuration"` (page.tsx L137) | Optionally shorten to `Status Configuration` (the route context already discloses "Partial Credit"), or accept current copy as informative for a directly-navigated tool page. |
| MEDIUM | Copy | Description copy is a sentence of admin guidance not present in any plan doc. Reads well but verify with PO. | _no Figma; not in impl plan §5.4 either_ | "Edit the display text and color for each system status. The system status itself is fixed by the application — only the cosmetic fields can change." (page.tsx L139-143) | Confirmed OK as helper text. Optional: add the impl-plan §1074-style sentence "Color changes propagate immediately to the buyer landing without redeploy" for reviewer confidence. |
| MEDIUM | Color | `.systemStatusCell` uses `'Courier New'` monospace at 13px. This communicates "code value" intent, but adds a third typeface to the admin family (after Brandon Grotesque and the Font Awesome icon set). | (admin family uses Brandon Grotesque only) | `font-family: 'Courier New', monospace; font-size:13px` (statusConfig.module.css L98-102) | Either keep as a deliberate "this is a code constant" affordance (then document) or replace with a label-style chip (e.g. `font-size:12px; padding:2px 6px; background:#F0EBE3; border-radius:4px`). |
| MEDIUM | Behaviour | Inline-edit uses Save / Cancel buttons; no save-on-blur or pencil affordance. Impl plan §5.4 spec says "Save-on-blur → calls PATCH". Code uses explicit click-Save. | impl plan §5.4 "Save-on-blur → calls PATCH `/api/v1/admin/partial-credit/statuses/{id}`" | `Save` button gated on validation, separate `Cancel` button (page.tsx L278-294) | Explicit Save matches typical inline-edit UX and avoids accidental PATCH on focus loss. Recommend keeping current behaviour and updating the impl plan to reflect — but flag the divergence from spec. |
| MEDIUM | Color | Invalid-color input state uses `#FDECEA` background + `#C0392B` border. Not a design token, but matches the `.errorAlert` chrome on the same page. | (admin family error tone is `#FCE6E1`/`#B12D00` per admin.module.css L631-635) | `.colorHexInputInvalid { border-color:#C0392B; background:#FDECEA; }` (statusConfig.module.css L149-152) | Either retint to the admin family error tone (`#FCE6E1`/`#B12D00`) or accept as locally-coherent. |
| LOW | Spacing | Page padding uses `clamp(20px, 2vw, 32px) clamp(24px, 3vw, 40px)`. Other admin pages use `24px 32px 80px`. | (admin family uses `padding: 24px 32px 80px` per admin.module.css L18) | `padding: clamp(20px, 2vw, 32px) clamp(24px, 3vw, 40px)` (statusConfig.module.css L6) | Align with `padding: 24px 32px 80px` for consistency with the landing + detail pages. |
| LOW | Spacing | Color swatch is 24×24px. Reasonable default; no spec drift. | _no Figma_ | `width:24px; height:24px` (statusConfig.module.css L131-132) | Confirmed OK. |
| LOW | Copy | "No status rows found." appears when the list returns zero rows. Defensive copy for an unreachable state (5 rows are seeded by V89). | _no Figma_ | `"No status rows found."` (page.tsx L170) | Confirmed OK — defensive. |
| LOW | Behaviour | Toast auto-dismisses after 3000ms via `window.setTimeout`. No `aria-live` region; assistive tech may miss the announcement. | _no Figma_ | Plain `<div class={styles.toast}>{toast}</div>` (page.tsx L145) | Add `role="status"` (polite live region) so screen readers announce the success. Cheap win. |

### Confirmed-OK summary

- Inline-edit pattern with per-row Save / Cancel works ✓ (per impl plan §5.4 intent).
- Hex validation regex `/^#[0-9A-Fa-f]{6}$/` is correct + uppercases on save ✓.
- 100-char max length enforced on internal/external text fields ✓.
- Read-only `system_status` cell — matches spec ("system_status is read-only", impl plan §5.4) ✓.
- Show-in-counters checkbox in edit mode, Yes/No in read mode ✓.
- Optimistic local-state update + server-snapshot replacement on save ✓.
- Error display gates: row-level error in colour cell, page-level loadError banner ✓.
- Color swatch live-previews the draft hex (gated on `isColorValid`) ✓ — the right UX for a colour edit.

### Figma node resolution notes

The Figma file (`rYKB9vBqlJOFUuGN7GAgQS`, canvas `213-610` "Review Credit
Requests") does **not contain a frame for the status-config admin
surface**. The Sprint 3 design notes inventory all 9 frames under that
canvas (design notes §1.2): every frame is a partial-credit review
detail or landing variant; none is a status-config grid.

The parity review plan (`partial-credit-figma-parity-review-plan.md` §2)
optimistically marks this surface as `213-610 (SPKB-3664 sub-frame)`,
but that sub-frame does not exist in the canvas. The implementation
plan (`partial-credit-sprint3-implementation-plan.md` §5.4 + §7) treats
this as a code-only admin tool with intent documented in prose:

> Simple grid: one row per status (5 rows from V89 seed). Editable
> cells for `internal_status_text`, `external_status_text`, `color_hex`
> (color picker), `sort_order`, `show_in_user_counters` (toggle).
> `system_status` is read-only.

**Decision for this review:** review against (a) the admin design
family already established for partial-credit landing + detail
(`admin.module.css` tokens + chrome) and (b) the impl-plan §5.4 spec
intent. All findings above use that combined reference.

Live Figma confirmation was attempted (`mcp__figma__get_figma_data`)
but returned a 429 rate-limit error for the current seat. The cached
design-notes dump (pulled 2026-05-11) is exhaustive for the children
of `213-610` and was used as the authoritative source.

**Recommendation for the team:** either (a) commission a Figma frame
for this surface, or (b) update the parity-review plan §2 to remove
the speculative `213-610` reference for surface 10 and explicitly
mark it as "code-only / spec-driven".

