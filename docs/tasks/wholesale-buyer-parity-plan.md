# Wholesale Buyer Portal — Parity Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring the Next.js buyer portal to pixel-and-behavior parity with Mendix QA for the **auction-code-selected shell**, and stand up the routing / chrome split so the deferred PWS shell can plug in without rework.

**Source of truth:** `docs/tasks/wholesale-buyer-qa-analysis.md` (QA walkthrough, 2026-04-22). Screenshots in `.playwright-mcp/qa-01-*.png` through `qa-11-*.png`.

**Mendix source:** `migration_context/frontend/Pages_Bidder_Dashboard.md`, `Pages_Buyer_Code_Select.md`, `MB_*.md` for the layout shell. Backend parity: `ACT_OpenBidderDashboard.md`, `ACT_CreateBidData.md`, `ACT_SubmitBidRound.md`, `SUB_CreateQualifiedBuyersEntity.md`.

**Backend status:** Bidder REST surface is live — `GET /api/v1/bidder/dashboard`, `PUT /api/v1/bidder/bid-data/{id}`, `POST /api/v1/bidder/bid-rounds/{id}/submit` all documented in `docs/api/rest-endpoints.md` under "Bidder Dashboard" (2026-04-23 ADR). Missing endpoints: **Import xlsx**, **Export xlsx**, **Carryover**, **Buyer User Guide PDF**. All other work is frontend.

**Tech Stack:** Next.js 16 (App Router, Turbopack — see `frontend/AGENTS.md` — read `node_modules/next/dist/docs/` before writing routing/streaming code), TypeScript, CSS modules, Vitest + Playwright. Keep pnpm via existing project setup.

---

## What already exists (baseline audit)

| Surface | File(s) | State |
|---|---|---|
| Login | `app/(auth)/login/LoginForm.tsx` (190 LoC) | ✅ Parity strings present ("Premium Wholesale & Weekly Auctions", "Employee Login"). Needs pixel diff vs QA + font / token audit. |
| Buyer-code picker | `app/(dashboard)/buyer-select/page.tsx` (241 LoC) | ✅ Two-category layout (`Premium Wholesale Devices`, `Weekly Wholesale Auction`). **Needs conditional rendering** — only show categories where user has active codes. |
| Dashboard shell | `app/(dashboard)/layout.tsx` + admin sidebar | ⚠️ Admin-style shell; **no bidder-specific shell yet**. Needs a dedicated layout at `(dashboard)/bidder/layout.tsx`. |
| Bidder dashboard | `app/(dashboard)/bidder/dashboard/BidderDashboardClient.tsx` (251 LoC) + `BidGrid.tsx`, `BidGridRow.tsx`, `DashboardHeader.tsx`, `SubmitBar.tsx`, `EndOfBiddingPanel.tsx` | ⚠️ Functional minimum: handles all 5 modes, submit flow with 429/409/version-conflict. **Missing**: Brandon Grotesque, Carryover, Import/Export, "Minimum starting bid - $2.50" label, "Switch Buyer Code" link, avatar popover, 11-column grid, infinite scroll, column filters, column sort arrows, `Currently showing N of M` footer, `Ends in | D.D | H.hrs | M.min |` timer format, pill CTA styles. Font is `Trebuchet MS` (wrong). |
| PWS shell | `app/pws/layout.tsx` + 8 subdirs (cart / inventory / pricing / offer-review / order / orders / rma-*) | ⚠️ Standalone shell exists but is **not routed from the buyer-code picker**. Needs a separate walkthrough + plan (out of scope here). |

---

## Cross-cutting design decisions

1. **Two shells, one picker.** `BidderShell` (at `(dashboard)/bidder/layout.tsx`) and `PwsShell` (already at `app/pws/layout.tsx`) are sibling layouts. The buyer-code picker dispatches to one or the other based on the selected code's type. Shared chrome (logo, Switch Buyer Code link, avatar popover) lives in a tiny `BuyerPortalChrome` component consumed by both.
2. **Active-code context.** Selected-code state lives in `localStorage` + a URL param on bidder/PWS pages (`?buyerCodeId=…`). A `useActiveBuyerCode()` hook reads both and redirects to the picker if absent. Matches the existing `buyerCodeId` query param on the bidder dashboard.
3. **Token layer first.** Define design tokens in `frontend/src/app/globals.css` as CSS custom properties *before* touching components. All component CSS modules consume tokens; no hardcoded hexes.
4. **Font.** Brandon Grotesque is paid Monotype. **Open decision (Q1 below)** — either (a) license and embed, (b) approved substitute (recommend `Proxima Nova` / `Museo Sans` subject to design sign-off), or (c) system-font stack as a dev fallback gated by env. Block Phase 2 until this is decided.
5. **Grid virtualization.** The grid renders up to 10,404 rows. Use **TanStack Table + react-virtuoso** (or TanStack Virtual) — never render all rows in the DOM. Infinite-scroll the data source too (fetch a window, prepend on scroll up, append on scroll down). Do not switch to pagination — that's an explicit parity violation.
6. **Parity is per-selected-code, not per-role.** Nav items, landing route, and the avatar-popover contents must be driven by the active code's type.

---

## Resolved decisions

| # | Question | Decision | Notes |
|---|---|---|---|
| Q1 | Brandon Grotesque license or substitute? | **License Brandon Grotesque.** | Procure the web-font license, self-host the woff2 files under `frontend/public/fonts/`, and declare via `@font-face` in `globals.css`. Unblocks Phase 2 onward. |
| Q2 | Preserve Mendix typo `"3.Upload your file here"` in Import modal? | **Preserve verbatim.** | Match the Mendix copy exactly for pixel parity. Do not silently insert a space. |
| Q3 | Submit Bids confirmation dialog copy + button label. | **Capture via QA walkthrough** (do it now, not deferred). | Log in as `akshay+4@learnfastthinkslow.com`, open the AD bidder dashboard, click Submit Bids, screenshot the confirmation modal (without confirming), cancel out. Update Phase 8 with verbatim copy. |
| Q4 | DOWNLOAD mode page copy + button label (Round 1 closed, Round 2 not open). | **Defer to a second pass once R1 parity is green.** | Phase 11 stays as a placeholder. When R1 surface is clone-done, run another QA capture — by then more auctions will have cycled and DOWNLOAD state will be reachable. |
| Q5 | Round 3 in-page heading — `"Upsell Round"` vs `"Round 3"`. | **Render `"Round {n}"` everywhere. Do NOT use `"Upsell Round"`.** | **This reverses the 2026-04-20 ADR** (which said Round 3's display name is `"Upsell Round"`). A new ADR must be written capturing the reversal before Phase 6 ships. Backend impact: `docs/api/rest-endpoints.md` shows `name: "Upsell Round"` in the `rounds[]` payload; the frontend should **ignore `name`** and derive the label as `` `Round ${round}` `` — keeps the backend contract stable without a schema change. The `name` column in `auctions.scheduling_auctions` retains its seeded value for audit only. |
| Q6 | Buyer User Guide PDF — hosted where? | **Admin upload via a new Admin page: Auction Control Center → Userguide Configuration.** | See Phase 12 for the admin upload flow. The bidder sidebar link downloads whatever file is currently configured; admin can replace it at any time. Requires: new admin page, backend upload endpoint (multipart PDF, stored on disk or S3), new table `admin.buyer_user_guide` holding the active file reference + upload audit trail. |

---

## File structure

### New frontend files

```
frontend/src/
├── app/(dashboard)/admin/auction-control-center/
│   └── userguide-configuration/
│       ├── page.tsx                                    (admin upload UI)
│       ├── UploadGuideModal.tsx
│       └── userguide.module.css
├── app/(dashboard)/bidder/
│   ├── layout.tsx                                      (BidderShell)
│   └── dashboard/
│       ├── CarryoverButton.tsx
│       ├── CarryoverModal.tsx
│       ├── ImportBidsButton.tsx
│       ├── ImportBidsModal.tsx
│       ├── ExportBidsButton.tsx
│       ├── MinimumStartingBidLabel.tsx
│       ├── BidderTimer.tsx                             (Ends in | … format)
│       └── bidder-dashboard.module.css
├── components/chrome/
│   ├── BuyerPortalChrome.tsx                           (logo + switch link + avatar popover)
│   ├── BuyerCodeChip.tsx
│   ├── UserAvatarPopover.tsx                           (Submit Feedback / Logout only for auction shell)
│   ├── SidebarToggle.tsx
│   └── chrome.module.css
├── components/bidder/
│   ├── BidderSidebar.tsx                               (gradient sidebar: Auction / Buyer User Guide)
│   └── bidderSidebar.module.css
├── hooks/
│   ├── useActiveBuyerCode.ts                           (reads selected code, redirects to picker if absent)
│   ├── useInfiniteBidData.ts                           (virtualized data source)
│   └── useColumnFilters.ts                             (Brand / Model / Carrier / Added / Qty / Price / Grade filters)
├── lib/
│   ├── bidder.ts                                       (existing — extend with carryover/import/export)
│   └── buyer-code-types.ts                             (AUCTION vs PWS type resolver)
└── tests/e2e/
    ├── wholesale-buyer-login.spec.ts
    ├── wholesale-buyer-picker.spec.ts
    ├── wholesale-bidder-shell.spec.ts
    ├── wholesale-bid-grid.spec.ts
    ├── wholesale-carryover.spec.ts
    ├── wholesale-import-export.spec.ts
    └── wholesale-submit-bids.spec.ts
```

### Modified frontend files

- `app/globals.css` — add tokens (colors, fonts, radii, shadows, sidebar gradient).
- `app/(auth)/login/LoginForm.tsx` + `.module.css` — swap to token consumers, pixel-fix vs QA.
- `app/(dashboard)/buyer-select/page.tsx` — gate categories by user's code portfolio.
- `app/(dashboard)/bidder/dashboard/BidderDashboardClient.tsx` — drop `FONT_STACK` constant, consume shell layout, add Carryover / Import / Export / min-bid label integration.
- `app/(dashboard)/bidder/dashboard/BidGrid.tsx` + `BidGridRow.tsx` — rebuild: 11 cols, TanStack Table + Virtuoso, column filters, sort arrows, footer.
- `app/(dashboard)/bidder/dashboard/DashboardHeader.tsx` — swap timer format, add Export/Import buttons inline, style Submit Bids as green pill.
- `lib/bidder.ts` — extend with `fetchCarryover`, `importBids`, `exportBids`.

### New backend files

```
backend/src/main/
├── resources/db/migration/
│   └── V{NN}__admin_buyer_user_guide.sql
└── java/com/ecoatm/salesplatform/
    ├── model/admin/
    │   └── BuyerUserGuide.java
    ├── repository/admin/
    │   └── BuyerUserGuideRepository.java
    ├── service/auctions/biddata/
    │   ├── BidCarryoverService.java                    (copy last week's submitted_* → current bid_*)
    │   ├── BidImportService.java                       (parse .xlsx, validate, stage rows)
    │   └── BidExportService.java                       (stream .xlsx of current bid slice)
    ├── service/admin/
    │   └── BuyerUserGuideService.java                  (upload / download / activate / history)
    └── controller/
        ├── BidderDashboardController.java              (existing — add carryover / import / export / guide-download endpoints)
        └── BuyerUserGuideController.java               (new — admin upload + bidder download)
```

### New backend endpoints (add to `docs/api/rest-endpoints.md`)

- `POST /api/v1/bidder/bid-rounds/{id}/carryover?buyerCodeId=…` — idempotent carryover.
- `GET /api/v1/bidder/bid-rounds/{id}/export?buyerCodeId=…` — streams `.xlsx` of the slice.
- `POST /api/v1/bidder/bid-rounds/{id}/import?buyerCodeId=…` — multipart `.xlsx`; validates against row ownership and round-status invariants; returns counts.
- `GET /api/v1/admin/buyer-user-guide` — Administrator only. Current file metadata + upload history.
- `POST /api/v1/admin/buyer-user-guide` — Administrator only. Multipart `.pdf` upload, atomic active-flag flip.
- `DELETE /api/v1/admin/buyer-user-guide/{id}` — Administrator only. Soft-delete historical upload.
- `GET /api/v1/bidder/docs/buyer-guide` — Bidder or Administrator. Streams the currently-active PDF.

### Docs updates

- `docs/api/rest-endpoints.md` — new `## Bidder Import / Export / Carryover / Docs` section.
- `docs/architecture/decisions.md` — new ADR **"2026-04-NN — Two-shell buyer portal routing"** capturing the auction-vs-PWS shell split, the per-selected-code gating, and the Import xlsx endpoint contract.

---

## Phased plan

### Phase 0 — Foundation (prereq for everything below)

- [x] **Q1 resolved** — license + self-host Brandon Grotesque. Record in the closeout ADR.
- [ ] Procure Brandon Grotesque web license; obtain `woff2` + `woff` files for the weights we use (400 regular, 500 medium; confirm with design whether 700 is needed for any bold state).
- [ ] Drop fonts into `frontend/public/fonts/brandon-grotesque/`.
- [ ] Add `@font-face` declarations in `globals.css` with `font-display: swap`.
- [ ] Add design tokens to `frontend/src/app/globals.css`:
  - Colors: `--color-bg-body: #F7F7F7`, `--color-text-body: #3C3C3C`, `--color-text-muted: #666766`, `--color-brand-teal-dark: #112d32`, `--color-brand-teal: #407874`, `--color-brand-teal-mid: #00969F`, `--color-brand-green: #14AC36`, `--color-warning-red: #C21E1E` (TBD — eyedropper confirm), `--color-carryover-tint: rgba(0, 150, 159, 0.18)`.
  - Fonts: `--font-family-primary: 'Brandon Grotesque', <fallback stack>`.
  - Radii: `--radius-pill: 44px`, `--radius-input: 4px`.
  - Gradient: `--sidebar-gradient: linear-gradient(155.66deg, #14AC36 -12.99%, #00969F 83.48%)`.
- [ ] Define button primitives as CSS classes (`.btn-primary-green`, `.btn-primary-teal`, `.btn-outline`, `.btn-carryover`) consuming tokens.
- [ ] Define font-face (@font-face) for Brandon Grotesque per Q1 decision.
- [ ] Snapshot the current `/login` and `/dashboard/bidder` renders for before/after diff.

**Success:** tokens in `globals.css`, button primitives documented in a small `ui-primitives.md` under `docs/frontend/`, dev server boots with new tokens applied.

---

### Phase 1 — Login parity

- [ ] Replace hardcoded hexes in `LoginForm.tsx` / `login.module.css` with token consumers.
- [ ] Set `font-family: var(--font-family-primary)` on the form.
- [ ] Pixel-diff login vs QA (`qa-01-login.png`). Fix any deltas: card radius, photo crop, button padding, remember-me spacing, footer layout.
- [ ] Confirm the eye-toggle on the password field uses the same image-swap idiom (or SVG + `aria-pressed`). Must be keyboard-accessible.
- [ ] "Forgot Password?" link currently targets `#` in QA — keep the same behavior or wire to `/forgot-password` route; don't silently upgrade it.
- [ ] "Contact Us" pill — confirm target URL and open behavior.
- [ ] Add `tests/e2e/wholesale-buyer-login.spec.ts`: fills credentials, submits, asserts redirect to `/buyer-select` or deep-link to dashboard if single-code.

**Success:** login page passes a Playwright pixel-compare vs `qa-01-login.png` with ≤2% delta (resolution-aware).

---

### Phase 2 — Buyer-code picker parity

- [ ] Extend `GET /api/v1/auth/buyer-codes?userId=…` response (verify it already includes code type) to return `codeType: 'AUCTION' | 'PWS'`. If not present, add a backend slice.
- [ ] Gate category visibility in `buyer-select/page.tsx`: render `Premium Wholesale Devices` only if any code's `codeType === 'PWS'`; render `Weekly Wholesale Auction` only if any is `AUCTION`. A buyer with only auction codes sees exactly one section — matches `qa-02-landing-buyer-picker.png`.
- [ ] If the user has exactly **one** active code, skip the picker and deep-link to that code's shell landing.
- [ ] Card styling: dark navy pill (`var(--color-brand-teal-dark)`), green circular avatar with briefcase SVG, bold code on top, company underneath, `→` right-anchored.
- [ ] On card click: set active code in `localStorage`, then `router.push` to `/bidder/dashboard?buyerCodeId=…` (AUCTION) or `/pws?buyerCodeId=…` (PWS).
- [ ] Add `tests/e2e/wholesale-buyer-picker.spec.ts`: single-code → skip; two-of-same-type → one section; mixed → two sections; click routes to correct shell.

**Success:** picker matches `qa-02-landing-buyer-picker.png` for the auction-only user (single "Weekly Wholesale Auction" section with two codes). Skip-to-shell works for single-code users.

---

### Phase 3 — Shared chrome primitives

- [ ] Create `components/chrome/BuyerPortalChrome.tsx`: logo on left, "Switch Buyer Code" link + active buyer-code chip in the middle, user name + avatar on the right.
- [ ] `BuyerCodeChip.tsx`: framed pill showing `{code}` bold on top, `{companyName}` underneath, with the green-circle briefcase avatar on the left.
- [ ] `UserAvatarPopover.tsx`: two stacked dark pills (`Submit Feedback`, `Logout`) in a floating card; receives `items` prop so the PWS shell can inject more later.
- [ ] `SidebarToggle.tsx`: chevron button; toggles the sidebar collapsed state via context.
- [ ] `hooks/useActiveBuyerCode.ts`: reads `localStorage` + URL param, validates against `GET /auth/buyer-codes`, redirects to `/buyer-select` if missing / invalid.
- [ ] Unit tests for the chrome components (Vitest + React Testing Library).

**Success:** chrome renders against both shells without layout collisions; the auction-shell popover shows exactly two items.

---

### Phase 4 — Bidder shell

- [ ] `app/(dashboard)/bidder/layout.tsx` wraps children in `BidderShell`: gradient sidebar (54px collapsed / ~220px expanded) with `Auction` + `Buyer User Guide` items, top bar consuming `BuyerPortalChrome`, page content on the right.
- [ ] Sidebar menu items use icons (gavel, book); labels visible only when expanded. Collapsed width `54px`, expanded matches QA (measure from screenshot — ~220px).
- [ ] Sidebar applies `background: var(--sidebar-gradient)` to the container, not per-item.
- [ ] Persist sidebar collapse state in `localStorage` under `bidder.sidebarCollapsed`.
- [ ] Buyer User Guide item opens the PDF in a new tab (`target="_blank" rel="noopener"`), **does not** render inside the app.
- [ ] Add `tests/e2e/wholesale-bidder-shell.spec.ts`: collapse/expand, sidebar items, avatar popover contents, Switch Buyer Code returns to `/buyer-select`.

**Success:** shell matches `qa-03-ad-bidder-dashboard.png` and `qa-11-ddws-sidebar-collapsed.png`.

---

### Phase 5 — Dashboard header parity

- [ ] Two side-by-side `<h2>` headings: `Auction {YYYY} / Wk{NN}` and `Round {n}` — match exact spacing from `qa-03`.
- [ ] Inline outline buttons `↓ Export` and `↑ Import` using `.btn-outline` class.
- [ ] `BidderTimer.tsx` — consume `timer` from the dashboard payload, render as `Ends in | {D}D. | {H}hrs. | {M}min. |` (keep the trailing pipe). Re-compute every 60s from `timer.endsAt` rather than decrementing a local counter — avoids drift.
- [ ] Submit Bids button: green pill (`var(--color-brand-green)`), white text, 18px/400, `--radius-pill`, 152×43 box. The current dark-teal variant in `BidderDashboardClient.tsx` must be replaced.
- [ ] Row below header: left side red "Minimum starting bid - $2.50" (`var(--color-warning-red)`, 500 weight); right side `Carryover` button (`.btn-carryover`).
- [ ] Remove the `FONT_STACK = "'Trebuchet MS', sans-serif"` constant from `BidderDashboardClient.tsx` and the explicit `style={{ fontFamily: FONT_STACK }}`. Rely on tokens.

**Success:** header pixel-diffs within tolerance against `qa-03` / `qa-04`.

---

### Phase 6 — BidGrid rebuild (11 cols, infinite scroll, filters)

- [ ] Replace current `BidGrid.tsx` with a TanStack Table + react-virtuoso implementation.
- [ ] Columns in order: Product Id (right-align, numeric filter) / Brand (combobox) / Model (text filter) / Model Name (text filter, hidden at `<1100px`) / Grade (text filter) / Carrier (combobox) / Added (date-picker filter, with calendar icon trigger) / Avail. Qty (numeric filter) / Target Price (numeric filter, currency display) / **Price** (currency, editable) / **Qty. Cap** (integer, editable).
- [ ] Column sort arrows (`↑` / `↓`), cycle asc → desc → clear.
- [ ] Footer: `Currently showing {loaded} of {total}` + screen-reader duplicate `Showing{loaded} of {total}rows` (preserve the concatenation artifact).
- [ ] Column CSS class names mirror Mendix: `auction-price`, `auction-qtycap`, `auction-grade`, `auction-carrier`, `auction-added`, `auction-availqt`, `auction-targetp` — makes QA diff review cheaper.
- [ ] Infinite scroll implementation: extend `GET /bidder/dashboard` (or add a new endpoint) to paginate rows. Current endpoint returns all rows up front — for 10,404 rows that's ~2MB JSON. Two options:
  - (a) Add `?cursor=…&limit=100` to `GET /bidder/dashboard`; return `rows` in pages + a `nextCursor`.
  - (b) Keep the full-list load but hand the slice to virtuoso (cheap to render only viewport rows; payload is the concern).
  - **Recommend (b)** first — Mendix takes the full-list hit too and the payload is under 5MB gzip. Revisit if QA perf flags it.
- [ ] Round heading — render `` `Round ${round}` `` for **all rounds including Round 3**. Do **not** consume `bidRound.name` / `schedulingAuction.name` from the API response — ignore it, derive from the numeric `round` field. (Reverses the 2026-04-20 ADR's `"Upsell Round"` guidance per Q5.)
- [ ] Before this phase ships, write a new ADR `2026-04-NN — Round 3 label reverts to "Round 3"` in `docs/architecture/decisions.md` noting the reversal, the rationale (bidder-facing consistency), and that the backend's `name` column is preserved for audit only. Cross-link from the 2026-04-20 ADR.

**Success:** grid matches `qa-03`, `qa-04`, `qa-10`, `qa-11` with the same column set, sticky header+filter row, and infinite-scroll footer.

---

### Phase 7 — Bid cell inputs

- [ ] `PriceCell`: text input with dollar auto-format. Show `$ 0.00` as default, strip the `$` on focus for editing, re-apply on blur. Select-all on focus. Apply CSS class `auction-price` + `textbox-select-all` + `text-dollar`.
- [ ] `QtyCapCell`: integer-only input. Empty default (no-cap sentinel). Reject non-digits on keypress. Select-all on focus. Classes `auction-qtycap` + `only-numbers` + `textbox-select-all`.
- [ ] Debounce PUT by 500ms (already matches the 2026-04-23 ADR's frontend guidance).
- [ ] On 409 `ROUND_CLOSED`: refetch dashboard and disable editing; toast "This round has closed."
- [ ] On 409 version conflict: refetch, replace row, toast "Another save collided with yours."
- [ ] On 429: surface `RateLimitedError` with a "Saving paused — please slow down" toast; resume after backoff.
- [ ] Vitest unit tests for both cells: default values, select-all, dollar-format, empty-vs-zero distinction on `QtyCapCell`.

**Success:** bid cells match Mendix behavior exactly — `PriceCell` empty → `0.00` (not empty), `QtyCapCell` empty ≠ zero, both select-all on focus.

---

### Phase 8 — Submit Bids flow (post-submit modal + no-bids guard)

**Key finding from the QA walkthrough (2026-04-22):** Mendix does **not** show a pre-submit confirmation dialog. Clicking **Submit Bids** commits immediately — the round-submit POST fires on the click. Two modal states appear depending on context:

**State 1 — empty state (no bids entered):** clicking Submit Bids without any non-zero bid opens an informational modal.

- Mendix widget: `SubmitBidsEmptyState` (inferred)
- Modal width: 540px (same frame as success modal)
- **Title:** `No Bids to Submit` (with `Bids` styled in green `#14AC36`)
- **Body heading:** `Please add Bids by` (bold)
- **Body numbered list (verbatim — the lack of a space between `3.` and `Use` is a Mendix artifact; preserve it per the same spirit as Q2):**
  1. `Entering bids in the screen`
  2. `Use Export, add bids in the downloaded excel and import the file`
  3. `Use Carryover function to carry bids from last week and make necessary changes`
- **CTA:** green pill `Close` (`#14AC36`)
- No other actions; modal is purely informational.
- Screenshot reference: `.playwright-mcp/qa-12-submit-bids-dialog.png`.

**State 2 — post-submit success (first submit AND resubmit show the same modal):**

- Mendix widget: `AuctionUI.BidsSubmitted_ReturnToBidPage`
- Modal width: 540px, close button (×) top-right, accessible title `Bids submitted` (used as `aria-labelledby` for the hidden H4).
- **Visible heading:** three spans — `Your` `Bids` `have been Submitted!` where the middle `Bids` has class `confirmationheader confirmationheadercolor` rendering in green `rgb(20, 172, 54)` = `#14AC36`. Other words use `confirmationheader` (dark `#3C3C3C`, 30px / 500). Preserve the single-space separators; Mendix has a double-space between `Bids` and `have` in the HTML source — replicate literally.
- **Body copy:** `Please review your updated bids, quantity caps and resubmit for any changes.`
- **CTA:** green pill `Close` (`#14AC36`)
- **Resubmit path shows the identical modal** — no distinct "already submitted" copy. Our port must match.
- Screenshot references: `.playwright-mcp/qa-13-submit-bids-confirm-1.png` (first submit), `qa-14-submit-bids-resubmit.png` (resubmit).

**Implementation tasks:**

- [x] **Q3 resolved** (captured verbatim above).
- [ ] **Drop the planned pre-submit confirmation dialog.** Submit is a direct commit per QA parity. The `BidderDashboardClient.handleSubmit` already posts directly — keep that behavior.
- [ ] **Client-side no-bids guard:** before POST, scan `grid.rowsById` for any row with `bidAmount > 0`. If none found, open the empty-state modal (State 1 above) and **do not POST**. Matches Mendix: the empty-state modal fires client-side based on grid state.
- [ ] **Do NOT add a `bidAmount < 2.50` rejection gate.** QA accepts a $2.50 submit without complaint — the red "Minimum starting bid - $2.50" label is an advisory, not a hard gate. (Revise the earlier plan: the min-bid label guides the user, but the Mendix backend accepts any non-negative amount.) Clarify with product if a stricter rule is desired, but ship matching parity first.
- [ ] Create `SubmitBidsEmptyStateModal.tsx` — matches State 1 copy exactly.
- [ ] Create `BidsSubmittedModal.tsx` — matches State 2 copy exactly, including the green `Bids` span and the double space between `Bids` and `have`.
- [ ] Wire both modals into `BidderDashboardClient`: on click → if no bids, open State 1; else POST → on success open State 2. The existing `lastSubmittedAt` state becomes redundant once State 2 owns the post-submit UX; remove the standalone timestamp line from `SubmitBar` to avoid duplicate confirmation.
- [ ] On 429 / 409: the existing handler paths stay. A closed round still needs a dedicated error modal (not captured yet — schedule with the DOWNLOAD capture in Phase 11).
- [ ] Playwright e2e: (a) empty-state modal when no bids; (b) success modal on first submit; (c) success modal on resubmit; (d) resubmit after editing a row carries the new amount through.

**Success:** pixel-match both modals against QA fixtures; submit behavior is a single-click commit (no intermediate confirmation).

**Success:** submit flow matches Mendix; no bid below $2.50 can leave the browser.

---

### Phase 9 — Carryover flow

- [ ] Backend: `BidCarryoverService` + `POST /api/v1/bidder/bid-rounds/{id}/carryover?buyerCodeId=…`. Logic: for every row in the current round's slice, set `bid_quantity`, `bid_amount` to the previous week's same `(ecoid, merged_grade)` row's `submitted_bid_quantity`, `submitted_bid_amount`. Idempotent; returns counts `{ copied: int, notFound: int }`.
- [ ] Frontend: `CarryoverButton.tsx` + `CarryoverModal.tsx`. Button → POST → if result `copied === 0`, show empty-state modal with copy verbatim: `"You don't have bids from last week to carry over."` (match `qa-06`).
- [ ] Success state modal copy: `"Carried over {N} bids from Week {NN}."` (provisional — verify in future QA walkthrough when carryover has data; flag in Q-list).
- [ ] Tests: Vitest for the modal's two states, e2e for the HTTP flow.

**Success:** matches `qa-06-ad-carryover-modal.png` in empty state.

---

### Phase 10 — Import / Export xlsx

- [ ] Backend: `BidExportService` — `GET /api/v1/bidder/bid-rounds/{id}/export?buyerCodeId=…` streams `.xlsx` with the current round's slice. Columns match the grid exactly (Product Id / Brand / Model / Model Name / Grade / Carrier / Added / Avail. Qty / Target Price / Price / Qty. Cap). Content-Disposition `attachment; filename="Bids_{auctionTitle}_R{round}_{buyerCode}.xlsx"`.
- [ ] Backend: `BidImportService` — `POST .../import` accepts multipart `.xlsx`. Validates: (a) file is xlsx, (b) rows match row IDs owned by this `(user, round, buyer_code)` slice, (c) prices are non-negative numbers, (d) quantities are non-negative integers or blank. Applies in a single transaction; returns `{ updated: int, errors: [{row: int, message: string}] }`. Rate-limit the import against the same bucket as the save endpoint.
- [ ] Frontend: `ExportBidsButton.tsx` — single-click → hits the endpoint, streams download (trigger via `<a href>` with `download` attribute).
- [ ] Frontend: `ImportBidsModal.tsx` — 4-step instructions (preserve the Mendix step copy **including the "3.Upload your file here" artifact verbatim per Q2** — no silent space insertion), `.xlsx`-only file input, green pill `Import` CTA. Behavior: upload → show progress → render a results summary (updated count + per-row errors) — user clicks "Done" to close and refetch.
- [ ] Tests: backend MockMvc + service unit tests; frontend e2e drives a round trip (export → modify known cell → import → verify).

**Success:** Import + Export cycle on QA-sized grid (500 rows) completes in < 3s; matches `qa-07-ad-import-modal.png` layout.

---

### Phase 11 — DOWNLOAD / ALL_ROUNDS_DONE / ERROR_AUCTION_NOT_FOUND mode polish

- [ ] **Q4 deferred to post-R1 walkthrough** per the Decisions table — do not attempt to invent the copy. Keep the existing placeholder strings in `EndOfBiddingPanel` until R1 parity is green, then schedule a second QA capture.
- [ ] `EndOfBiddingPanel` already exists — verify copy matches QA (needs QA walkthrough when state is live). Replace the placeholder text until then.
- [ ] DOWNLOAD mode: button wires to the export endpoint (or a round-1-specific variant) instead of `onClick: () => {}`.
- [ ] ERROR_AUCTION_NOT_FOUND currently shows `"No scheduled auction is available."` — confirm QA's exact copy when the state is reachable; defer if unavailable.
- [ ] **Follow-up task:** after Phase 13 reports ≥98% pixel parity for R1/GRID mode, log a reminder to re-walk QA and capture DOWNLOAD (R1-closed-R2-not-open), ALL_ROUNDS_DONE, and ERROR_AUCTION_NOT_FOUND states.

**Success:** mode branches render the right copy + action per QA parity.

---

### Phase 12 — Buyer User Guide PDF (admin-upload flow)

Per Q6: the PDF is configured by admins via the existing **Auction Control Center** area, under a new **Userguide Configuration** page. The bidder's sidebar link downloads whatever file is currently active.

**Schema:**

- [ ] New Flyway migration (next available `V{NN}__admin_buyer_user_guide.sql`) creating:
  ```sql
  CREATE TABLE admin.buyer_user_guide (
    id          BIGSERIAL PRIMARY KEY,
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(512) NOT NULL,          -- local disk path or S3 key
    content_type VARCHAR(100) NOT NULL DEFAULT 'application/pdf',
    byte_size   BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL REFERENCES identity.users(id),
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    is_active   BOOLEAN NOT NULL DEFAULT FALSE
  );
  CREATE UNIQUE INDEX uq_buyer_user_guide_active
    ON admin.buyer_user_guide (is_active) WHERE is_active = TRUE;
  ```
  The partial unique index guarantees exactly one active row at a time. A new upload atomically flips the prior row's `is_active` → false inside a tx.

**Backend:**

- [ ] `BuyerUserGuideService` + `BuyerUserGuideRepository` + `BuyerUserGuide` entity.
- [ ] `BuyerUserGuideController` with:
  - `GET /api/v1/admin/buyer-user-guide` — Administrator only. Returns current active file metadata + upload history (last 10).
  - `POST /api/v1/admin/buyer-user-guide` — Administrator only. Multipart `.pdf` upload; validates `Content-Type`, file-size cap (e.g. 20MB), magic-byte check. Stores the file, atomically flips `is_active`.
  - `GET /api/v1/bidder/docs/buyer-guide` — Bidder or Administrator. Streams the currently-active PDF with `Content-Type: application/pdf` and `Content-Disposition: inline; filename="buyer-guide.pdf"`.
  - `DELETE /api/v1/admin/buyer-user-guide/{id}` — Administrator only. Soft-delete (keep file on disk for audit). Blocks deletion if it's the currently-active row.
- [ ] `SecurityConfig` matchers: admin endpoints behind `Administrator`; the bidder GET behind `Bidder` **or** `Administrator` (matches the `/api/v1/bidder/**` pattern).
- [ ] Docs in `docs/api/rest-endpoints.md` under a new "Buyer User Guide (Admin)" section.

**Admin frontend:**

- [ ] New page `app/(dashboard)/admin/auction-control-center/userguide-configuration/page.tsx`:
  - Header: "Userguide Configuration"
  - Current file card: file name, upload timestamp, uploader, byte size, "Download" button, "Replace" button
  - Upload modal: file input (`.pdf` only), drag-and-drop area, progress indicator, error surface for size / type violations
  - History table: last 10 uploads (read-only audit)
- [ ] Nav entry for `Auction Control Center → Userguide Configuration` in the admin sidebar. Confirm the Auction Control Center section exists (if not, scaffold it as part of this phase — small cost, keeps the nav consistent with Mendix).
- [ ] Administrator-only route guard.

**Bidder sidebar wiring:**

- [ ] `<a href="/api/v1/bidder/docs/buyer-guide" target="_blank" rel="noopener">Buyer User Guide</a>` in `BidderSidebar`. Do not wrap in Next.js `<Link>` — it's an external-style link that must bypass the SPA router.
- [ ] If no active guide exists, the backend returns `404` with `{ "message": "No buyer user guide configured" }`. The frontend catches this via a HEAD request on mount and disables the link with a tooltip "No guide configured yet" — cleaner than letting the user click through to a 404 page.

**Tests:**

- [ ] Backend: `BuyerUserGuideServiceTest` (upload flips active), `BuyerUserGuideControllerTest` (role gating, size/type rejection, download streams correct bytes).
- [ ] Frontend: Vitest for the upload modal; Playwright e2e — admin uploads a fixture PDF, bidder clicks sidebar link, tab opens with the PDF.

**Success:** admin can replace the buyer guide in the UI without a deploy; bidder's sidebar link always streams the current version.

---

### Phase 13 — E2E + pixel QA

- [ ] Full Playwright suite runs in CI against the local dev server.
- [ ] Pixel-compare fixtures: check in `qa-01` … `qa-11` screenshots as parity baselines under `frontend/tests/e2e/parity-baselines/`. Allow ≤2% delta with resolution-aware comparison.
- [ ] Run against both test accounts: single-code user → skip picker → land on dashboard; multi-code → picker → pick DDWS → dashboard → Switch Buyer Code → pick AD → dashboard.
- [ ] Accessibility: axe-core scan on the dashboard with no critical findings; keyboard navigation through the grid cells works; sidebar toggle has a visible focus ring.

**Success:** CI green, pixel suite green, zero critical a11y issues.

---

### Phase 14 — `/forgot-password` route implementation

Surfaced as a known gap during Phase 1: the login form's `<Link href="/forgot-password">` targets a route that does not yet exist. QA behaviour is a no-op (`href="#"`), but our Next.js `<Link>` idiom requires a real destination. Ship a minimal password-reset flow.

**Backend (likely already partially wired — verify first):**
- [ ] Check `backend/src/main/java/.../controller/AuthController.java` for an existing `/api/v1/auth/forgot-password` endpoint. If absent, add: `POST /api/v1/auth/forgot-password { email }` — enqueues a reset email, returns `200` regardless of whether the email exists (enumeration-resistant).
- [ ] Add `POST /api/v1/auth/reset-password { token, newPassword }` that validates the token, updates the user's BCrypt hash, expires the token.
- [ ] Token storage: table `identity.password_reset_tokens (id, user_id, token_hash, expires_at, consumed_at)`. Short-lived (30 min); one-time-use.
- [ ] Email template reuse: leverage the PWS email-delivery event pattern from the 2026-04-13 ADR (post-commit event + async executor) so it matches existing infra.
- [ ] Unit + MockMvc coverage for both endpoints.

**Frontend:**
- [ ] `app/(auth)/forgot-password/page.tsx` — form with email field + "Send reset link" button. On submit: POST to `/api/v1/auth/forgot-password`, always show a generic success toast "If an account exists with that email, a reset link has been sent." regardless of response.
- [ ] `app/(auth)/reset-password/page.tsx` — reads `?token=...` from URL, renders a form with new password + confirm password + "Reset" button. POSTs to `/api/v1/auth/reset-password`.
- [ ] Reuse Phase 1 login styling (same cream card + token primitives). Share the `LoginCard` layout if worth a small extraction.
- [ ] Playwright e2e: `/forgot-password` renders and submits to a mocked endpoint; `/reset-password?token=test` renders and validates password-match.

**Docs:**
- [ ] Update `docs/api/rest-endpoints.md` with both endpoints.
- [ ] Update `docs/business-logic/user-auth.md` (create if missing) with the password-reset flow.

**Success:** Login page's "Forgot Password?" link navigates cleanly (no 404), the full request→email→reset round-trip works end-to-end.

---

### Phase 15 — ADR + docs closeout

- [ ] Write ADR **"2026-04-NN — Two-shell buyer portal routing"** in `docs/architecture/decisions.md`. Capture: shell split decision, per-selected-code gating, Brandon Grotesque outcome (Q1), Import xlsx contract, Carryover endpoint shape, rate-limiter reuse.
- [ ] Update `docs/api/rest-endpoints.md` with the four new endpoints (Carryover, Export, Import, Buyer Guide).
- [ ] Archive this plan's completed phases into the ADR or a "2026-NN wholesale-buyer parity closeout" note. Leave the PWS-shell follow-up items as a separate plan stub.

**Success:** every endpoint in `rest-endpoints.md`; every non-obvious decision in `decisions.md`.

---

## Explicitly deferred (separate plan)

- **PWS shell parity.** Needs its own QA walkthrough with a PWS-code-attached test account. Covers: Shop landing, Orders, Offers, Pricing, Profile, Notifications surfaces; PWS-specific sidebar; any PWS-only items in the avatar popover. The routing split from Phase 3 + 4 leaves room for this to plug in without rework. Track under `docs/tasks/pws-shell-parity-plan.md` (not yet written).
- **Admin-side of the buyer portal.** Buyer-code creation / role management / notifications config. Out of wholesale-buyer parity scope.

---

## Risks

1. **Font licensing (Q1)** is the biggest unknown. If the answer is "licensed, but not yet embedded," Phase 2+ pixel parity is blocked until the font lands. Fallback: ship with a substitute under a feature flag so Phases 2-8 can proceed; swap on embed.
2. **Grid virtualization + column filters** is the largest single piece of work. Budget ~3-4 days of focused implementation + 1 day for filter UX polish.
3. **Import xlsx validation edge cases** — Mendix's original validation is opaque; the backend service needs to be defensive (file size cap, row count cap, cell-type checks, ownership check on every row). Budget a security-reviewer pass before shipping.
4. **Infinite scroll vs server pagination.** If QA perf surfaces a payload size issue at 10k+ rows, Phase 6 may need the cursor endpoint after all — that's a backend change mid-plan.
5. **PDF serving (Q6).** If the decision is "backend streams it and requires auth," we need a new controller; if "static asset," it's trivial. The plan assumes static — flag early if not.

---

## References

- Analysis: `docs/tasks/wholesale-buyer-qa-analysis.md`
- QA screenshots: `.playwright-mcp/qa-01-login.png` through `qa-11-ddws-sidebar-collapsed.png`
- Prior ADRs:
  - 2026-04-23 — Bidder dashboard + bid_data generation
  - 2026-04-20 — Auction lifecycle cron + event contract (for round-status gating)
  - 2026-04-19 — Admin security matcher ordering (same pattern for `/api/v1/bidder/**` matchers)
- Mendix sources: `migration_context/frontend/Pages_Bidder_Dashboard.md`, `Pages_Buyer_Code_Select.md`, `migration_context/backend/ACT_*.md`
- Backend contract: `docs/api/rest-endpoints.md#bidder-dashboard`
- Repo plan conventions: `docs/tasks/auction-bid-data-create-plan.md` (structure reference)
