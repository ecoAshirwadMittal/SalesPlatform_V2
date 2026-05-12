# Figma parity — Email templates editor + On-behalf modal

> Method: spec-against-code (no screenshots). Figma file
> `rYKB9vBqlJOFUuGN7GAgQS` ("2026 Auction: Partial Credit") was fully
> enumerated; no canvas or frame exists for either Sprint 4 surface (see
> §"Figma node resolution notes" at the end). Findings are therefore
> graded against (a) the design-intent doc
> `docs/tasks/partial-credit-sprint4-implementation-plan.md` §5.2 / §5.3
> and (b) the design-system tokens that *do* exist in Figma (canvases
> "Components", "Submit Credit Request", "Review Credit Requests") so the
> two new surfaces stay visually coherent with the partial-credit module
> they ship under.

---

## Surface 11: Email templates editor (`/admin/auctions-data-center/partial-credit/email-templates`)

**Figma node:** **No analog found.** The Figma file has no admin email-templates frame on the `213:610` "Review Credit Requests" canvas, the "Phase 2" canvas, or the dated working canvases ("May 8, 2026", "Feb 24, 2026"). §5.2 of the implementation plan calls out a deliberately simple admin surface ("raw HTML + Preview tab", §11 Q4). No design follow-up is recorded for this surface, so the review reduces to "matches the design intent in §5.2?".

**Local files:**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/EmailTemplateEditor.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/emailTemplates.module.css`

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| HIGH | Color | `.editButton`, `.buttonPrimary`, `.tabActive` border, `.pillOn`-decorated accent all use `var(--color-brand-green, #407874)`. `--color-brand-green` resolves to `#14AC36` (green) in `globals.css`; the **fallback `#407874` is teal** and is the wrong colour if the token is ever missing. | brand green `#14AC36` | fallback hex `#407874` (teal — `--color-brand-teal`) | Replace every `var(--color-brand-green, #407874)` with `var(--color-brand-green, #14AC36)` in `emailTemplates.module.css` (5 occurrences: `.editButton`, `.tabActive`, `.buttonPrimary` × 2 declarations). Same hardcoded-fallback drift exists in `onBehalfModal.module.css` (surface 12) — fix both files. |
| HIGH | Behaviour | Preview tab renders the **persisted** template, not the current draft (line 95-108 + the in-code comment line 96-99). When the admin has typed edits, the `.previewHint` banner says "Preview reflects the saved template — unsaved edits are not shown." but §5.2 of the plan says the preview should "render the current draft (server-side via the preview endpoint) so the admin sees exactly what the listener would build at send time." | Preview reflects current edits | Preview reflects last-saved row; warning banner shown when dirty | Either (a) add a `POST .../{id}/preview` body that accepts the draft fields (most useful — admins can iterate without saving) or (b) keep current behaviour and update the hint copy to "Save first to preview" so the contract isn't misleading. Code comment at line 96-99 acknowledges this gap explicitly — treat as a known follow-up. |
| MEDIUM | Behaviour | `.changedDate` is rendered raw (line 111 of `page.tsx`: `{row.changedDate ?? '—'}`). The buyer landing in the same module formats dates via `new Date(...).toLocaleDateString()`. Admin users will see an ISO timestamp string. | Locale-formatted date (e.g. `5/11/2026`) | Raw ISO string from server | Apply the same formatting as `wholesale/partial-credit/page.tsx` line 114: `{row.changedDate ? new Date(row.changedDate).toLocaleDateString() : '—'}`. |
| MEDIUM | Layout / Behaviour | Plan §5.2 specifies a "side panel / drawer" for the editor. The code uses an **inline row-expand** (line 92-101 of `page.tsx` swaps the whole row in-place via `colSpan={5}`). Functional, but the design intent is a drawer. | Side panel / drawer | Inline row expansion | Either update the plan to match (cheaper — inline already works) or refactor to a drawer using the same shell as the auctions-data-center drawer pattern. Low urgency. |
| MEDIUM | Copy | The intro line uses HTML-escaped curly braces (`{'{{varName}}'}`) so the admin sees the raw `{{varName}}` syntax — good. But there is no inline list of which variables each template accepts. Plan §6.1 documents this contract, but it's not surfaced anywhere in the editor. Admins typing `{{wrongDeviceLineDescription}}` into `ReviewCompleted_Approved` would silently render as empty string + a warn-log. | Visible variable contract per template | Only the global `{{varName}}` / `{{!varName}}` syntax shown; per-template variable list not exposed | Render a collapsible "Available variables" hint above the body textarea, populated from the per-template variable contract in plan §6.1. |
| MEDIUM | Color | `.error` and `.toast` use hardcoded hex (`#c0392b` / `#fdecea` / `#1f5a38` / `#e6f4ec` / `#2d7a4e`). No corresponding tokens exist in `globals.css` yet, but the wizard CSS uses `#EB3300` for error and `#FFF3EC`/`#F26B21` for warning — drift between admin and PWS surfaces. | Module-wide error/success palette | Bespoke palette per file | Either lift the wizard error palette into `globals.css` as `--color-error-bg` / `--color-error-border` / `--color-error-text` and reuse, or accept the admin/PWS visual split and document it. Low urgency. |
| MEDIUM | Typography | `.heading` is 28px/500 in Brandon Grotesque. The admin "Reserve Bids" page (the sibling under the same `/admin/auctions-data-center/...` shell) uses `clamp()`-scaled headings. Cross-check against the admin layout shell — heading scale may already be standardised at 24px or 32px. | Admin shell heading scale | 28px fixed | Verify via `frontend/src/app/(dashboard)/admin/.../layout.tsx` and align. Low urgency — 28px reads fine. |
| LOW | Color | `.rowEditing td` background `#fafafa` is hardcoded. `globals.css` has `--color-surface-2` (resolves to `#f6f6f6` per the fallback in this file) — could use that token for consistency. | Token-driven surface | Hardcoded `#fafafa` | Use `var(--color-surface-2, #fafafa)`. |
| LOW | Spacing | `.notice` / `.error` / `.toast` share a single rule (padding `8px 12px`, font-size 14px). The `.error` inside the editor (`.editor .error`) is the same size, but on the editor it sits at the bottom of the form near `actions` — adding 4-8px of bottom-margin (or moving it above the actions row) would improve scan. | n/a | shared single padding rule | Tighten editor error spacing — `margin-bottom: 0` is fine but the box currently butts against the action row. |
| LOW | Copy | `.toast` shows `Saved {templateKey}` — fine but the templateKey is a snake_case identifier (`ReviewCompleted_Approved`). Admin-friendly text would be "Saved Review Completed (Approved)" via a label map; or just "Template saved." | User-friendly status message | Raw template key | Either keep (it's an admin tool, raw key is fine) or add a label map keyed on `template_key`. |
| LOW | Behaviour | `.previewHint` text "Preview reflects the saved template — unsaved edits are not shown." is shown only when `hasChanges` is true (line 215). Good. Consider adding a "Refresh preview" button when the admin returns to the Preview tab after editing — currently the preview cache (line 81 `preview` state) is stale until tab switch. | n/a | Lazy fetch only on first switch | Add a "Reload preview" button or refetch on tab-switch when `hasChanges` is true. |

### Confirmed-OK summary

- Page-level structure (heading, intro, table of 3 rows, in-place editor) matches plan §5.2 line 449-454 verbatim — `templateKey` is read-only, no "New Template" affordance.
- Edit / Preview tabs match §5.2 + §11 Q4 (raw HTML textarea, no rich-text). `aria-selected` + `role="tab"` / `role="tabpanel"` correctly applied.
- Save button is **disabled iff no changes** (`!hasChanges || saving`) — matches §5.2 "Save calls `PATCH /email-templates/{id}` and shows a success toast."
- Diff logic (lines 57-70) correctly treats `null` `body_text` as no-change and empty-string as a real "clear" — matches the server contract called out in the in-code comment.
- Preview lazy-fetches on first switch to the Preview tab (line 112) — matches the design intent of not stalling the editor mount on a preview-render call.
- `PREVIEW_VARIABLES` map matches the documented variable contract in plan §6.1 (`requestNumber`, `orderNumber`, `approvedTotalDisplay`, `buyerName`, `reviewerName`, `reviewCompletedDate`, `detailUrl`, `wrongDeviceLineDescription`, `photoUploadDeadline`).
- `dangerouslySetInnerHTML` for preview HTML body is acceptable: the variables are stub values (`PREVIEW_VARIABLES`) — no buyer input ever flows through, and the server-side renderer is the same one the listener uses (in-code comment lines 230-235).
- `pillOn` / `pillOff` colour pair (`#e6f4ec` / `#1f5a38` on, `#f1f1f1` / `#6b6b6b` off) is internally consistent with the success-state palette used elsewhere in the module.
- `maxLength={255}` on the subject input mirrors the schema constraint (`VARCHAR(255)`).

---

## Surface 12: On-behalf modal (`/wholesale/partial-credit` — `OnBehalfModal.tsx`)

**Figma node:** **No analog found.** No modal / drawer / dialog frame for the sales-rep on-behalf flow exists on any of the 10 canvases. The only "SalesRep" reference in the entire file is `<SalesRepEmail>` placeholder text inside an unrelated R3 auction announcement email mock (line 146312, "Auction R3 Sending Bid File" canvas — different module). The "Components" canvas at `599:6551` defines a generic step-indicator component (`Step Horizontal`, states `Active` / `Editing` / `Done` / `Inactive`) that the modal's `<Steps>` component should match — this is the only Figma-side reference point and the basis for several findings below.

**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/OnBehalfModal.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/onBehalfModal.module.css`
- Trigger + integration: `frontend/src/app/(dashboard)/wholesale/partial-credit/page.tsx` (lines 60-69, 125-134)

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| HIGH | Color | Same `var(--color-brand-green, #407874)` fallback drift as surface 11 (`.pickerItem:hover`, `.stepCurrent .stepNum`, `.buttonPrimary`). Fallback is teal, not green. | brand green `#14AC36` | fallback hex `#407874` (teal) | Replace fallbacks with `#14AC36` in `onBehalfModal.module.css` (4 occurrences). |
| HIGH | Typography | Modal heading + body all use `var(--font-family-primary)` = **Brandon Grotesque**. The PWS surface (`buy-qa.ecoatmdirect.com/p/login/web`) and every other partial-credit buyer page use **Founders Grotesk** (see `globals.css` lines 5-18 + the wizard.module.css comment line 2). The Figma "Components" canvas confirms step labels are `Text/sm (strong)` = Founders Grotesk Medium 500 / 16px. | Founders Grotesk | Brandon Grotesque (via `--font-family-primary`) | Either (a) explicitly set `font-family: 'Founders Grotesk', ...` on `.heading` / `.itemPrimary` / `.fieldLabel` etc., or (b) inherit from the wizard's `font-family: inherit` cascade by removing the explicit `--font-family-primary` override. Pre-existing project ambiguity — the wizard already does this wrong (relies on body cascade which is Brandon Grotesque); resolve module-wide before fixing per-file. |
| HIGH | Layout | Modal heading is 20px (line 32-37 of CSS). The wizard "Submit a Credit Request" heading on the parent page is **40px / 500** (`wizard.module.css` line 27-32). Inconsistent header weight for the same flow's entry-points. Modal heading reads small for a 3-step picker that drives the entire wizard. | Wizard scale (24-40px) | 20px | Bump to 24px (matches `.cardHeading` in `wizard.module.css`). |
| MEDIUM | Layout | Modal width `min(520px, 92vw)` — slightly narrow for the picker rows (code + buyer name + email all on one row). Picker rows currently use `space-between` flex (line 149) — on a 520px modal with a long buyer name, `itemPrimary` and `itemSecondary` collide. | n/a | 520px max | Either widen to 640px or allow rows to wrap (`flex-wrap: wrap; gap: 4px;` on `.pickerItem`). |
| MEDIUM | Spacing | `.steps` uses `gap: 8px` and each step is `flex: 1 1 0` (line 56-60). Result is three equal-width step pills with green-on-cream backgrounds for current/done states. The Figma "Components" canvas uses a `Step Horizontal` pattern that's a **circle + label on the same baseline**, *not* a full-width pill background. Visually heavier than the Figma reference. | Circle + label inline, no pill background | Pill background spans the whole row | Drop the `background` + `padding` on `.stepCurrent` / `.stepDone` / `.stepPending`. Keep the green-circle treatment on `.stepNum`. Adds visual breathing-room. |
| MEDIUM | Color | `.stepDone .stepNum` uses `#1f5a38` (dark green) but `.stepCurrent .stepNum` uses `var(--color-brand-green)` (`#14AC36`). The Figma "Components" canvas uses the same green (`#14AC36` = `fill_X5EZST`) for Active AND Done (Done has a white pill + green check icon, Active has a green pill + white digit). | Active: green pill, white digit; Done: white pill, green border + green check | Active: green pill + white digit; Done: dark-green pill + white digit | Update `.stepDone` to use white pill + green border + green check icon (matches Figma `State=Done` component). Cheaper alternative: change `.stepDone .stepNum` background to `var(--color-brand-green, #14AC36)` so both states share the same green. |
| MEDIUM | Typography | `.itemPrimary` (14px / 600) + `.itemSecondary` (13px) — Figma `Text/sm (strong)` is 16px / 500, and `Desktop Web/P.secondary` is the standard secondary at 14px. Sizes are smaller than the design system's defaults. | 16px / 500 + 14px regular | 14px / 600 + 13px regular | Bump to 16px / 500 + 14px regular if the modal width is widened. Otherwise accept (modal-internal scale). |
| MEDIUM | Copy | Step labels: `Buyer code` / `Buyer user` / `Order number`. Modal heading: `Submit on behalf of a buyer`. Plan §5.3 line 462-465 doesn't fix the step labels but does call out the trigger button + entry banner. Minor: "Buyer user" reads awkwardly — could be "Buyer" or "User" or "Buyer contact". | n/a | "Buyer user" | Consider "Buyer contact" or "User" — low urgency. |
| MEDIUM | Copy | Order step summary banner (line 171-175): `Submitting on behalf of <strong>{name}</strong> for code <strong>{code}</strong>.` — matches plan §11 Q3 default. After confirm + draft creation, the wizard step 1 should *also* show an "on-behalf" banner (plan §5.3 line 468-469 + §11 Q3) — verify this is wired on `/wholesale/partial-credit/new?draftId=X`. Not visible in this file. | Banner on both modal AND wizard step 1 | Banner only on modal | Cross-check `wholesale/partial-credit/new/page.tsx` for the post-create banner. If missing, this becomes CRITICAL. |
| MEDIUM | Behaviour | Reset on close (lines 48-60): clears step, codes, users, selectedCode, selectedUser, orderNumber, error — but **only when `open` flips to false**. If the parent never unmounts the modal, this is fine; but if the parent uses `open && <OnBehalfModal />` (unmount when false), the reset never runs because the effect doesn't see the transition. The parent (`page.tsx` line 125-134) *does* always render `<OnBehalfModal open={...} />` (not conditionally), so the effect path works — but the in-code comment says "Reset state every time the modal is reopened" which is the wrong mental model. | Reset on close (works correctly) | Reset on `open` flip to false (works only because parent always renders) | Either (a) move state into the parent so unmount-on-close works, or (b) update the in-code comment to "Reset state every time the modal is closed". Behaviour is correct as-is. |
| MEDIUM | Behaviour | Back button on the ORDER step (line 188-195) goes to `'USER'` step and preserves `selectedUser`. Good. But there's **no Back button on the USER step** to return to CODE — the rep can only restart by closing the modal and reopening. | Back button on every non-first step | Back only on ORDER | Add a Back button on the USER step that returns to `'CODE'` and preserves the `users` cache (so re-picking the same code doesn't refetch). |
| LOW | Layout | Modal lacks a Cancel button on the CODE + USER steps. Closing requires the X icon (line 119-126). Plan §5.3 doesn't mandate Cancel buttons, but other modals in the project (e.g. wizard) use an explicit Cancel/Submit pair. | Cancel button + close X | Close X only | Add a "Cancel" footer button to CODE/USER for symmetry with the ORDER step's Back/Create pair. |
| LOW | Color | `.orderSummary` uses `#fff8d0` / `#ead17a` (warm yellow) — `globals.css` has no warning-yellow token. Same colour drift as the wizard's `.warningBanner` (which uses `#FFF3EC` / `#F26B21` — orange, not yellow). Two different "alert" palettes within the same module. | Single module-wide warning palette | Two ad-hoc palettes | Either pick one and apply consistently, or document the distinction (yellow = info, orange = warning). |
| LOW | Color | `.error` palette (`#c0392b` / `#fdecea`) — same hardcoded drift as surface 11. Lift to globals.css tokens. | Token-driven error palette | Hardcoded hex | Same fix as surface 11. |
| LOW | A11y | Step indicator (line 270-287) uses `<ol>` + per-step `<li>` with `aria-label="Modal progress"` — good. Picker uses `role="listbox"` + `role="option"` with `aria-selected={false}` (always false, line 245). The `aria-selected` should toggle once the rep picks an item (though picking immediately advances to the next step so the false value is technically correct — the listbox is single-select-then-advance, not a persistent selection). | n/a | `aria-selected={false}` always | Drop the `aria-selected` attribute entirely (since the listbox doesn't persist a selection state) or document that this is a "click-to-advance" listbox not a "click-to-select" one. |
| LOW | Behaviour | `confirm()` (line 93-110) trims `orderNumber` server-side, but the disabled state of the Create button (line 200) also calls `.trim()` on every keystroke — fine, just a redundant trim. | n/a | n/a | Cache the trimmed value in a `useMemo` if perf ever matters (it won't here). |

### Confirmed-OK summary

- Three-step flow (CODE → USER → ORDER) matches plan §5.3 line 462-465 verbatim.
- Lazy fetch contract is correct: `listEligibleBuyerCodes()` on open (line 48-72), `listBuyersForCode(codeId)` on code-pick (line 74-86) — no users list is fetched until the rep has picked a code.
- Reset on close (lines 48-60) clears every state slot, preventing stale data from bleeding across two on-behalf sessions.
- `confirm()` correctly calls `createDraftOnBehalf(orderNumber.trim(), buyerCodeId, buyerUserId)` (line 98-102) — matches the §3.2 service contract.
- After successful draft creation, parent navigates to `/wholesale/partial-credit/new?draftId=X` (page.tsx line 132) — matches plan §5.3 line 467 and the in-code comment about Sprint 4 chunk 8 wizard wiring.
- `aria-label="Submit on behalf"` on the backdrop (line 115) + `role="dialog"` is correct.
- `autoFocus` on the order-number input (line 184) is good — sends the rep straight into typing.
- `disabled={submitting}` on Back and `disabled={!orderNumber.trim() || submitting}` on Create — correct interactive states during the in-flight POST.
- Empty-state copy: "No buyer codes available." / "No buyer users associated with this code." — sensible defaults.
- Loading state ("Loading…") shown during both fetches.
- Backdrop styling (`rgba(0, 0, 0, 0.55)`, `z-index: 1000`) is sensible for a buyer-facing modal.

---

## Figma node resolution notes

**File enumerated.** Top-level canvases (10 total):
| Line | Node id | Canvas name | Relevant? |
|---|---|---|---|
| 408 | `35:2469` | 📕 Cover | No |
| 448 | `173:600` | Submit Credit Request | Buyer wizard frames — not Sprint 4 surfaces |
| 43965 | `213:610` | Review Credit Requests | Admin review detail — not the email-templates editor |
| 54150 | `599:4797` | Phase 2 | Future scope — no email-templates frame |
| 54706 | `593:7348` | Bi Weekly Claims Review Meeting | Meeting screenshots only |
| 54755 | `599:6551` | Components | **Useful reference** — step indicator pattern + token list |
| 55631 | `273:600` | --------- Archive --------- | Empty separator |
| 55634 | `526:5650` | May 8, 2026 | Working canvas — checked, no relevant frames |
| 102066 | `227:2038` | Feb 24, 2026 | Working canvas — checked, no relevant frames |
| 125486 | `0:1` | Auction R3 Sending Bid File | Unrelated module (R3 auction emails) |

**Searches run on the saved Figma payload:**
- `email | template | preview | enabled | toggle | subject` — only matches on R3 auction-announcement email frames (canvas `0:1`, line 146312 onwards) and the unrelated "Toggle - Partial Credit" component which is the buyer wizard's row toggle, not an admin editor.
- `partial credit | credit request | missing device | wrong device | encumbered` — 1,323 matches, all on the existing Sprint 2/3 buyer landing, wizard, and admin-review surfaces. None on an email-templates or on-behalf surface.
- `on behalf | submit on | salesrep | sales rep | act on behalf | impersonate` — only one match, line 146312, a `<SalesRepEmail>` placeholder inside the R3 auction email mock (unrelated module).
- `modal | dialog | drawer | stepper | step indicat | picker` — only matches the buyer-wizard step indicator (Components canvas) and the buyer-wizard "Critical Announcement" drawer (unrelated module).

**Conclusion:** neither surface has a Figma frame. Sprint 4 chunks 3 and 6 shipped without a design hand-off for these surfaces — the implementer worked from plan §5.2 / §5.3 verbatim. The review reduces to "matches the design intent in §5.2 / §5.3?" and "stays visually coherent with the design system tokens that the Figma file *does* define (Components canvas)?". Both findings tables above are graded against those two anchors.

---

## Executive summary

**Total findings (Surface 11 + Surface 12 combined):**

| Severity | vs Figma (Components canvas + tokens) | vs design-intent (§5.2 / §5.3) |
|---|---|---|
| CRITICAL | 0 | 0 |
| HIGH | 3 (color-fallback drift × 2 files; typography font-family on modal; modal heading size) | 1 (Preview tab renders persisted, not draft) |
| MEDIUM | 6 (step-indicator pill, step-done colour, picker typography, picker overflow, modal width, color/error palette drift) | 6 (date formatting, drawer-vs-inline, variable-contract hint, on-behalf wizard banner verification, back-button on USER step, "Buyer user" copy) |
| LOW | 6 | 3 |

**Top 3 things that need attention:**

1. **Preview tab on the email-templates editor previews the saved template, not the current draft** (HIGH, surface 11) — the in-code comment acknowledges this is wrong vs the plan §5.2 contract; admins typing an edit and switching to Preview see stale output. Either accept the gap and update the hint copy, or add a `POST .../preview` body that accepts the draft fields.

2. **`var(--color-brand-green, #407874)` fallback is teal, not green** (HIGH, both surfaces) — the CSS-var resolution path is correct in production (resolves to `#14AC36`), but the fallback hex is wrong for `--color-brand-green` and would silently break if the token is ever renamed or missing. Five replacements in `emailTemplates.module.css`, four in `onBehalfModal.module.css`.

3. **On-behalf modal uses Brandon Grotesque (admin font) on a PWS surface that should be Founders Grotesk** (HIGH, surface 12) — module-wide ambiguity (the wizard inherits from `body` which is Brandon, not the PWS Founders the Figma design system specifies). Resolve module-wide before patching per-file. The `--font-family-primary` global token currently encodes the admin font, not the PWS font — splitting into `--font-family-pws` + `--font-family-admin` would be the clean fix.
