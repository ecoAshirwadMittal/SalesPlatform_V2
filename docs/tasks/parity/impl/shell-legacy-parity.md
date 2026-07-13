# Shell chrome → legacy parity (SHELL-P1, BDD-P2, BDD-P3)

**Date:** 2026-07-12 · **Findings:** SHELL-P1, BDD-P2, BDD-P3
(`docs/tasks/parity/findings.md`) · **ADR:** `docs/architecture/decisions.md`
→ "2026-07-12 — Shell chrome matches legacy exactly".

Rebuilds the admin + buyer (auction) app-shell chrome to match the legacy
Mendix app exactly, per the user's four rulings on 2026-07-12. These supersede
the prior informal "token-brief" divergences (RB-25/RB-26 in the 2026-05-08
Reserve Bids ADR).

---

## 1. Decision table (user rulings, 2026-07-12)

| # | Ruling | Admin shell | Buyer (auction) shell |
|---|--------|-------------|-----------------------|
| 1 | **Credit Requests nav — add to both** | item between "Auction" and "Reports" → `/admin/auctions-data-center/partial-credit` | item between "Auction" and "Buyer User Guide" → `/wholesale/partial-credit` |
| 2 | **Top bar → match legacy (remove white bar)** | ecoATM DIRECT logo in content top-left; `SNP_UserInfoDisplay` identity top-right — **name + green initials circle**, degrading to a **bare green dot** when the account has no display name (see §2 refinement) | logo in content top-left; person **full name + green initials avatar** top-right (same widget) |
| 3 | **Admin section collapsed + single highlight** | Admin/Reports/Settings collapsed by default (expand on click only); single highlight = longest-prefix leaf (no submenu+item double-highlight) | n/a (flat 3-item sidebar) |
| 4 | **Switch-Buyer-Code → in-content card** | n/a | top-bar pill removed; in-content block: "Switch" (green) + "Buyer Code" (dark) label over a bordered card (buyer name small over CODE large) |

All four are **visually verified** (throwaway `:13000` render under the harness
rasterization args — see §5) and match the legacy captures.

---

## 2. Extracted legacy values (ground truth)

Measured from the H1 evidence captures (1920×1080, DPR 1):
`docs/tasks/parity/evidence/h1-2026-07-12/legacy-local-admin-reserve-bids-list__default.png`
and `…-bidder-dashboard__default.png` (PIL pixel sampling).

### Sidebar (both shells)
- Width **232px** (legacy, both shells). Gradient `linear-gradient(155.66deg, #14AC36 -12.99%, #00969F 83.48%)` — top `#11A84A`, bottom `#00969F`. (Already matched in-app.)
- Admin nav item height 64px; 13-item order: Users · Buyers · Inventory · Purchase Order · Reserved Bids (EB) · Auction Scheduling · Bid as Bidder · Auction · **Credit Requests** · Reports› · Settings› · Admin› · Buyer User Guide.
- Buyer nav order: Auction · **Credit Requests** · Buyer User Guide.
- Collapse toggle at the top-right of the sidebar.
- **Note (legacy quirk):** the reserve-bids capture actually shows *two* dark-highlighted items (Inventory **and** Reserved Bids (EB)). **User-confirmed 2026-07-12: this is a bug in legacy.** Ruling 3's **single** highlight deliberately does not reproduce it — implemented as a longest-prefix match, so on `/…/reserve-bids` only "Reserved Bids (EB)" lights up.

### Logo (in content, both shells)
- Two-tone "ecoATM DIRECT" — green `#14AC36` + teal `#00969F`.
- Bounding box **x=[248, 367), y=[14, 60) → 119×46 px**, identical on both shells. Left edge x=248 (16px right of the 232px sidebar).
- Not reachable via an unauthenticated GET on `:8082` (it is a Mendix static-image widget in the content layout, not a theme-CSS asset). **Extracted from the DPR-1 legacy capture** and shipped as a transparent PNG (`public/images/ecoatm-direct-logo.png`, 119×46, background keyed to alpha at ≤8 delta from `#F7F7F7`).

### Top-right identity
- **Admin capture:** solid green (`#14AC36`) circle, **28×28**, bbox x=[1860,1887] y=[23,50]. No name, no initials *visible*.
- **Buyer capture:** "{full name}" (dark `#3C3C3C`, ~14px) + green (`#14AC36`) **28×28** circle with **white** initials. Avatar right edge x=1887.
- **Refinement (user clarification + KB verification, 2026-07-12):** both pages use the **same layout** — `ecoAtm_Atlas_Default` (`ai_knowledge_base_Release10/Pages_Page/ReserveBid_Overview.md` and `PG_Bidder_Dashboard_DG2.md` both cite it; `Pages_Snippet/SNP_UserInfoDisplay.md` is the identity DataView with the Switch-to-Premium / Submit-Feedback / SignOut actions). The admin capture's bare dot is this same widget rendering an account with **no display name** — not a distinct admin treatment. Implemented accordingly: both shells render name + white initials in the 28px green circle, degrading to a bare green circle when the account has neither (the new dev admin "Admin User" therefore shows `Admin User (AU)` — a per-side account-data difference for the harness identity mask, same class as the buyer side's nadia-vs-bidder note).

### Switch-Buyer-Code block (buyer only)
- Label y=[90,105] (~15px): **"Switch"** = green `#14AC36`, **"Buyer Code"** = dark `#3C3C3C`. (Mission said "teal" — evidence corrected it to **green**.)
- Card outer bbox **x=[477,777] y=[113,167] → 300×54**, border `#D0D0D0`, ~4px radius, **no fill** (interior `#F7F7F7`).
- Empty left cell ~40px with a vertical **divider at x=517** (`#D0D0D0`).
- Info cell: company name `#3C3C3C` (~14px) over CODE `#3C3C3C` bold (~26px, glyph height 27px).
- Content column is centered at **max-width 1189px** (matches the BDD-P1 ended-panel container → card left-aligns with the page content at x≈475).

---

## 3. Files changed (shell only)

### Admin shell
- `frontend/src/app/(dashboard)/layout.tsx` — added Credit Requests nav item; removed sidebar logo; added `SidebarToggle` (functional whole-sidebar collapse, session-only state); removed auto-expand-on-path; single-highlight via `computeActiveHref` (longest-prefix leaf); top bar → logo-left + `SNP_UserInfoDisplay` identity right (name + white initials in the green circle when the account has them, bare green circle otherwise; click-to-open dropdown).
- `frontend/src/app/(dashboard)/dashboard.module.css` — transparent 74px top band (no border), `.topBarLogo`, `.userName` + `.userIconWrapper` (28px `#14AC36`, white 11px initials), collapse-toggle header, `.sidebarCollapsed` rail styles.

### Buyer (auction) shell
- `frontend/src/components/bidder/BidderSidebar.tsx` — added Credit Requests item (`/wholesale/partial-credit`) between Auction and Buyer User Guide.
- `frontend/src/components/bidder/BidderSidebarIcons.tsx` — new `CreditRequestIcon` (reply/return arrow).
- `frontend/src/components/chrome/BuyerPortalChrome.tsx` — reduced to logo + identity (dropped the mid switch-code region + `activeBuyerCode`/`onSwitchBuyerCode` props); switched to the DIRECT logo.
- `frontend/src/components/chrome/SwitchBuyerCodeCard.tsx` — **new** in-content card (label + bordered card).
- `frontend/src/components/chrome/chrome.module.css` — transparent chrome band; green (`#14AC36`) + white avatar (was soft-teal); new `.switch*` card styles; kept `.chip*` (buyer-select) intact.
- `frontend/src/app/(dashboard)/bidder/layout.tsx` — renders `<SwitchBuyerCodeCard>` at the top of the content; updated chrome props.

### Assets
- `frontend/public/images/ecoatm-direct-logo.png` — **new** (119×46 transparent, extracted from legacy).

### Tests
- `frontend/src/components/chrome/BuyerPortalChrome.test.tsx` — rewritten (logo + identity only; asserts the switch-code control is **not** in the chrome).
- `frontend/src/components/chrome/SwitchBuyerCodeCard.test.tsx` — **new** (4 cases: name+code render, "Switch Buyer Code" button name intact, onSwitch fires, hidden when no active code).
- `frontend/tests/e2e/wholesale-bidder-shell.spec.ts` — semantic assertions: added `sidebar-item-credit-requests`; corrected the "top-bar chrome" comments (switch control now in-content).

### Docs
- `docs/architecture/decisions.md` — ADR (supersedes RB-26, amends RB-25); also removed a stray committed `<<<<<<< HEAD` conflict marker.
- `docs/tasks/parity/findings.md` — SHELL-P1 / BDD-P2 / BDD-P3 → fixed.
- `docs/testing/coverage.md` — shell-parity frontend test entry.

### Out of scope (flagged)
- **PWS buyer shell** (`app/pws/layout.tsx`) — independent dark-top-bar chrome validated against a different QA surface; **no parity evidence**, left untouched.
- **Sidebar width delta (buyer):** new bidder sidebar is 220px vs legacy 232px. Pre-existing (BDD-P1 chose 220 and centered content to legacy's x≈477 via the 1189px max-width). Changing it would misalign the already-merged ended-panel, so left at 220. 12px sidebar-region delta noted.
- **Sidebar icon treatment:** legacy circles many nav icons (Users/Buyers/Inventory/Auction/Credit Requests…); the new app uses plain stroke icons. Pre-existing systemic delta, outside the ruled scope. Credit Requests uses a plain reply-arrow to match its siblings.
- **Sidebar collapse toggle glyph:** legacy uses a bordered panel-collapse square; the app's shared `SidebarToggle` is a plain chevron (already used on the buyer shell). Kept for consistency.

---

## 4. Affected screenshot baselines

**No active Playwright `toHaveScreenshot` baseline is invalidated:**
- All 10 pixel-compare tests across the `wholesale-*.spec.ts` suite are `test.fixme(...)` (disabled) and there are **no committed `*-snapshots/` directories**.
- The only committed PNGs under `frontend/tests/` are unasserted manual references in `tests/e2e/parity-baselines/` (`before-phase0-bidder-dashboard.png`, `before-phase0-login.png`, `after-phase1-login.png`) — none are referenced by test code.

**Forward-looking (do NOT regenerate here — wrong OS):**
- `tests/e2e/parity-baselines/before-phase0-bidder-dashboard.png` depicts the **old** bidder chrome (top-bar pill + "Bidder User"). When the `.fixme` bidder-shell pixel tests are enabled, their baselines must be captured against the **new** chrome on the harness (Linux) OS.
- The parity harness `new-*` captures (`docs/tasks/parity/evidence/h1-2026-07-12/new-admin-reserve-bids-list__default.png`, `new-bidder-dashboard__default.png`) are point-in-time and pre-date this fix; a fresh harness run will show the new chrome.

---

## 5. Verification

- **Visual:** throwaway Next dev server on `:13000` (killed after), rendered with the exact harness rasterization args (`--force-color-profile=srgb --disable-lcd-text --font-render-hinting=none --hide-scrollbars`, DPR 1, 1920×1080). Buyer shell + admin chrome + collapsed admin sidebar all match legacy. Screenshots: `docs/tasks/parity/evidence/shell-parity-2026-07-12/new-{buyer-shell,admin-chrome,admin-collapsed}-impl.png` (note: these are throwaway renders on the pre-BDD-P1 base — "Loading dashboard…" / Next dev "1 Issue" overlay are dev artifacts, not chrome).
- **Admin identity, both states:** `new-admin-identity-named.png` (dev "Admin User" → name + white `AU` initials in the green circle) and `new-admin-identity-nameless.png` (account with no display name → bare green dot, pixel-matching the legacy capture's admin corner) — proves the §2 refinement's degradation behaviour.
- **Types:** `npx tsc --noEmit` — 31 pre-existing errors (unrelated files: `wholesale-submit-bids.spec.ts`, `admin-purchase-orders.spec.ts`, `AdminReviewClient.tsx`, `partial-credit/new`), **0 in touched files**.
- **Unit/RTL:** `npx vitest run` — **286/288 pass, 32/33 files**. The 2 failures are the known pre-existing `apiFetch-guard.test.ts` cases (violation list is all `lib/*` files, none touched here). Chrome+bidder targeted run: **28/28**.

---

## Pass 2 (2026-07-12) — geometry convergence

Pass 1 landed the right structure; the fresh harness overlay
(`tools/parity/out/diff/bidder-dashboard__default.png`) showed uniform
**displacement** ghosts. Pass 2 converges the shell geometry to the legacy
captures at the band level (±1px per element), measured with PIL band
profiling under the exact harness rasterization (pinned Chromium,
`--force-color-profile=srgb --disable-lcd-text --font-render-hinting=none
--hide-scrollbars`, DPR 1, 1920×1080, fixed clock, kill-motion CSS, 750ms
settle) on a throwaway `:13000` render of this branch.

### Root causes fixed

1. **Buyer sidebar 220px → 232px.** The 12px delta displaced all page
   content. The Pass-1 compensation (1189px centered content column tuned
   against the 220 sidebar) was re-trued, not stacked: the legacy content
   column is **1198px centered** in the `[232,1920]` region (left edge x=477
   — card border/label/heading ink), and the legacy ended-panel is a
   **1190px band inset 4px** inside it (borders x=481/x=1670).
2. **Sidebar item rhythm.** Legacy = **64px item pitch** on *both* shells
   (admin capture: 13 items span 770.5px → 64.04 pitch; the buyer capture's
   3 item text bands are pixel-identical to the admin's first 3). Buyer items
   were 48px/14px/gap-12; admin items sat 2px high with labels 8px right.
3. **Switch-card text metrics.** Legacy name is **18px** (ink 146px wide vs
   113px at 14px), card is exactly **54px** tall (y[113,166]), name/code ink
   at y[117,134]/y[140,158], text at x528 (divider x=517 + 10px inset).
4. **Identity chip.** Legacy name is **16px** (caps band y[30,41]) with a
   **16px** gap to the avatar (ink gap 17); right-anchored at avatar
   x[1860,1887].

### Per-element before/after — bidder shell (legacy = `out/legacy-local`, before = `out/new`, after = this branch under identical raster)

| Element | Legacy (target) | Before (Pass 1) | After (Pass 2) |
|---|---|---|---|
| Sidebar width | 232 | 220 | **232** ✓ |
| Item text cap-tops | y85 / y149 / y213 (64px pitch) | y69 / y117 / y165 (48px pitch) | **y85 / y149 / y213** ✓ |
| Item label font | 16px/500 (12px caps) | 14px/400 | **16px/500** ✓ |
| Item label ink x | 56 | 52 | **56** ✓ (gap 12→19) |
| Toggle center | y33.5 | y23.5 | **y33.5** ✓ |
| Logo | x[248,366] y[14,59] | x[248,366] y[14,59] | same ✓ (pad-left 28→16 re-anchors after width change) |
| Identity name | 16px, band y[30,41], right ink 1843, 17px ink gap | 14px, y[32,41], right 1847, 13px gap | **y[30,41], right 1843, 17px gap** ✓ |
| Avatar | x[1860,1887] y[23,50] | same | same ✓ |
| Switch label ink | y[90,106], x[478,596], 16px | y[96,110], x[476,588], 15px | **y[90,106], x[478,596]** ✓ |
| Card border box | y[113,166] x[477,776] (54px) | y[119,178] x[476,775] (60px) | **y[113,166] x[477,776]** ✓ |
| Card divider | x517 | x516 | **x517** ✓ |
| Name ink | y[117,134] x528 (18px) | y[129,140] x532 (14px) | **y[117,134] x528** ✓ |
| Code ink (26px) | y[140,158] | y[147,165] | **y[140,158]** ✓ |
| Heading ink | cap-top 216, left x477 | 212, x476 | **216, x477** ✓ |
| Panel borders | x[481,1670], y[265,857] | x[476,1664], y[250,841] | **x[481,1670]** ✓, y[254,845] (see residuals) |

### Per-element before/after — admin shell (reserve-bids pair; purchase-orders pair verified identical)

| Element | Legacy (target) | Before | After |
|---|---|---|---|
| All 13 item text bands | y[85,96] … y[853,869] (64px pitch) | each exactly 2px high (y[83,94] …) | **all 13 exact** ✓ (`.sidebarNav` padding-top 2px) |
| Item label ink x | 57 | 65 | **57** ✓ (pad-left 20→16, `.navIcon` margin-right 4→0) |
| Toggle center | y33.5 | y28.5 | **y33.5** ✓ (header padding-top 9px, height still 58) |
| Logo / status dot | x[248,366] y[14,59] / x[1860,1887] y[23,50] | already exact | unchanged ✓ |

Final pixel-diff vs the legacy capture: **1.01%** differing pixels
(threshold >24), all attributable to the residuals below plus font-raster AA
and the logged-in user's name differing between environments
("Nadia GmailOne" legacy vs the dev-seed "Bidder User").

### Files changed

- `frontend/src/components/bidder/bidderSidebar.module.css` — width 232,
  toggle-row pad 20px top, item height 64 / gap 19 / 16px 500.
- `frontend/src/components/chrome/chrome.module.css` — chrome pad-left 16;
  `.switchBlock` 1198px centered, padding 11/0/20; label 16px, margin-bottom
  7; card fixed 54px; info top-anchored, padding 1px 16px 0 8px; company
  18px/1.1; code margin-top 2; avatar gap 16; identity name 16px.
- `frontend/src/app/(dashboard)/bidder/dashboard/endOfBiddingPanel.module.css`
  — **container geometry only**: `.endedRoot` 1189→1198, `.panel` margin
  0 4px.
- `frontend/src/app/(dashboard)/dashboard.module.css` — `.sidebarNav`
  padding-top 2; `.navItem` pad-left 16; `.navIcon` margin-right removed;
  `.sidebarHeader` padding-top 9.

### Content-CSS residuals (out of Pass 2 scope — flagged for a content pass)

- **Dashboard heading font-size**: legacy renders "Auction 2026 / Wk13" at
  **35px** (25px caps, ink to x774); `.auctionTitle` is 32px (23px caps, ink
  to x748). Cap-top and left edge now match exactly; the ink bottom is -2 and
  glyph x-positions diverge rightward.
- **Panel top/bottom** sit ~11px high (y[254,845] vs legacy y[265,857]): the
  gap between the heading and the panel is ~12px larger in legacy (35px
  heading line box + its margins). The panel copy ("Bidding has ended." etc.)
  cascades with the panel box. Both belong to
  `endOfBiddingPanel.module.css` typography (BDD-P1's values), not the shell.
- **Icon glyphs** (pre-existing Pass 1 flags, unchanged): admin 28px icon
  boxes vs legacy ~20px circled glyphs (admin icon ink now [20,39] vs legacy
  [18,35] after the pad fix); both shells' collapse toggle renders a small
  chevron vs legacy's 20px bordered panel-collapse square (centers now match
  exactly).

### Method / verification notes

- Every number above was measured under the harness rasterization; the
  worktree backend was not needed — the `:13000` dev server proxied
  `/api/v1` to the running backend on 8080, so all data-dependent regions
  (buyer-select → HN journey, ended-panel, admin grids) rendered fully.
  Nothing was left unverifiable.
- The backend CORS allowlist admits `localhost:3000` only, so the capture
  script authenticates directly against the backend from Node (no Origin
  header) and injects the `auth_token` cookie + `auth_user` localStorage into
  the browser context. Capture-script-side only; no app or backend change.
- Gates: `npx tsc --noEmit` — same 31 pre-existing errors, 0 in touched
  files. Targeted `npx vitest run src/components/chrome
  src/components/bidder` — 28/28.
