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

---

## Pass 3 (2026-07-12) — typeface verification + panel/switch/sidebar convergence (SHELL-P2)

Pass 2 left the shell TEXT ghosting as hollow outlines at exact positions on
`bidder-dashboard__default` — the finding (SHELL-P2) **hypothesised the glyph
faces differed** (legacy Founders/Open Sans vs new Brandon via
`--font-family-primary`). Pass 3 tested that hypothesis under the harness raster
and **disproved it**: every shell-text element already renders in the correct
face (Brandon Grotesque). The ghosts were **size, weight, and 1px positional**
deltas, not face. Same lesson as LOGIN-P2, inverted — there the render was
Founders (theme was wrong); here the render is Brandon (the theme's dominant
Founders declarations + the `.confirmationSubHeader {font-family:"Brandon"…14px}`
rule are NOT what actually applies — `.confirmationheader` inherits Brandon from
the Atlas `body {font-family:"Brandon Grotesque"}` base, and the subtitle renders
16px).

### Method — the render IS the harness, proven (loop fidelity 0 px)

Throwaway `next dev -p 13000` (Turbopack) on this branch, captured with the exact
harness launch args + context (`--force-color-profile=srgb --disable-lcd-text
--font-render-hinting=none --hide-scrollbars`; DPR1, 1920×1080, light,
reduced-motion, en-US, America/New_York, fixed clock, kill-motion CSS, 750ms
settle) via the same pinned `@playwright/test` 1.59.1 the harness uses. The
baseline render is **byte-identical** to the harness's own
`tools/parity/out/new/bidder-dashboard__default.png` (`0` diff px at every
threshold) — so every number below is measured against the true ground truth.
The backend CORS allow-list admits `localhost:3000` only, so `/api/v1/**` was
proxied **Node-side** to `:8080` in the capture script (the browser's
`Origin: :13000` header is stripped/rewritten so the shared backend answers 200);
capture-script-only, no app/backend/DB change, `:3000/:8080/:8082` untouched.

Face determination used three converging harness-raster measurements, never a
CSS re-read: (a) **ink-width probe** — the test string rendered in Brandon vs
Founders at candidate px/weights and compared to the legacy ink width; (b)
**cross-correlation alignment** — legacy vs new crop diff over a (dx,dy) grid
(residual→0 at a pure shift ⇒ same face, only positional); (c) **shape overlay**
of a probe render on the legacy crop.

### Face table (element → legacy RENDERED face/weight/size → what the new app was → fix)

| Shell element (class) | Legacy rendered | New (before) | Face verdict | Fix |
|---|---|---|---|---|
| Sidebar nav labels, both shells (`.navItem`) | **Brandon 16px/500** | Brandon 16px/500 | ✅ already correct (Founders 16px = 52/106/115 ink vs legacy 49/100/109) | buyer 1px x-align only (`gap 19→20`) |
| Page heading "Auction 2026 / Wk13" (`.auctionTitle`) | **Brandon 500 35px** | Brandon 500 35px | ✅ correct (Brandon & Founders both 298 ink; align residual **0** at pure +1px = same face) | +1px vertical (see panel geometry) |
| Ended headline "Bidding has ended." (`.endedHeading` / legacy `.confirmationheader`) | **Brandon 500 35px** | Brandon 500 35px | ✅ correct (Brandon 35px = 253 ink = legacy; **Founders = 270, ruled out**; align residual 0 at +4px) | vertical only (panel spacing) |
| Subtitle "Your bids…" (`.subtitle` / legacy `.confirmationSubHeader`) | Brandon 400 **16px** | Brandon 400 **14px** | ✅ face correct, **size wrong** (legacy 264w/12h = Brandon 16px; 14px = 232/10; Founders 16px = 268/10) | **14→16px** |
| Download label "Download your Round N Bids" (`.downloadButton`) | Brandon 500 **18px** | Brandon 500 **14px** | ✅ face correct, **size wrong** (legacy 199w/18h = Brandon 18/500; Founders 17px = 196/14, too short) | **14→18px** + pill geometry |
| Switch-card label "Switch Buyer Code" (`.switchLabel`) | **Brandon 500 16px** | Brandon 500 16px | ✅ correct (119 ink = Brandon 16px; Founders = 126) | none |
| Switch-card company name (`.switchCardCompany`) | Brandon **500** 18px | Brandon **400** 18px | ✅ face correct, **weight wrong** (18/500 overlay = residual **0**; 18/400 = 462) | **weight 400→500** |
| Switch-card code "HN" (`.switchCardCode`) | Brandon 700 26px | Brandon 700 26px | ✅ correct (36 ink; overlay residual 0) | none |
| Identity name (`.avatarFullName`) | Brandon 400 16px | Brandon 400 16px | ✅ correct (text differs by ENV, not face — harness mask) | none |
| In-content logo PNG | 119×46, rendered 1:1 | 119×46, 1:1 | ✅ pixel-identical (align residual **0**, natural size 119×46) | none |

**Net: no `--font-family-*` token or `@font-face` change was needed or made.**
The SHELL-P2 "typeface" premise is a false alarm; the real deltas were subtitle
size, download size, switch-name weight, and ended-panel vertical spacing.

### Ended-panel geometry (SHELL-P2 items 2–4)

- **Item 2 (inner ~10px offset):** the subtitle+button block sat low. Trued via
  `.endedRoot` padding-top 24→23 (heading cap-top y217→**y216**), `.headingRow`
  margin-bottom 20→16 (panel top y270→**y265**), `.panel` dropped its uniform
  `gap:16` for per-child margins (`.subtitle` `margin-top:9` → legacy's 12px
  Bidding→subtitle ink gap; `.actionRow` `margin-top:23` → legacy's 31px
  subtitle→button gap) and an asymmetric `padding:22px 24px 26px` that nudges the
  vertically-centred block onto legacy's y. Result: "Bidding has ended." and the
  subtitle now land at **dY 0** (were +4 / +12).
- **Item 3 (panel border "hairline double"):** was **not** a border-width bug —
  it was the panel sitting 5px low, so the two 1px `#D0D0D0` frames didn't
  overlap. Fixing the panel top (now y265, bottom y856 vs legacy 857) collapses
  the double line.
- **Item 4 (logo ghost):** the in-content PNG is already **119×46 natural,
  rendered 1:1**, bbox pixel-identical to legacy (align residual 0). No change —
  the original "ghost" was pre-Pass-3 state.
- **Download pill:** legacy 268×43 label 199×18. Rebuilt to Brandon 18px/500,
  `padding:12px 29px 12px 37px` (asymmetric sides reproduce legacy's Mendix
  `.btn` label-right-of-centre offset — a harness sweep bottomed the button
  region at label-right +4), `margin-left:3px`, kept at 44px (an explicit
  `height:43px` shrinks the flex block 1px and re-ghosts the whole dense panel).

### Per-band before→after (diff>60 px vs the harness legacy PNG, by region)

| Region | Baseline (== harness `out/new`) | Pass 3 |
|---|---|---|
| in-content logo | 0 | **0** |
| sidebar nav labels (+circled icons) | 841 | **372** (label text now dX0; residue = same-face AA + the pre-existing circled-icon treatment, out of ruled scope) |
| switch-code card | 413 | **62** (name weight fix) |
| page heading | 822 | **0** ✓ |
| ended headline "Bidding has ended." | 2035 | **0** ✓ |
| subtitle | 1117 | **0** ✓ |
| download button | 1637 | **1038** (Brandon-18px label hinting drift 198-vs-199 + 1px pill edge — AA/hinting floor) |
| identity chip (ENV — masked) | 585 | 585 |
| **TOTAL excl-identity** | **8066** | **1980** (−75%) |

Per-element final (harness legacy vs this branch, all at 1920×1080):
sidebar/switch-label/switch-name/switch-HN/heading/Bidding/subtitle all **dW0
dX0 dY0**; panel frame top 265 (=), bottom 856 (−1); download label dX−3/dW−1
(hinting), pill 269×44 vs 268×43 (+1/+1). Evidence:
`docs/tasks/parity/evidence/shell-pass3-2026-07-12/{new,diff}-bidder-dashboard.png`.

### Identity-chip mask selectors (for the orchestrator's harness — BDD-P3)

The chip text will always differ (legacy "Nadia GmailOne" vs new dev-seed
"Bidder User" / admin "Admin User") — mask per side:

| Side | Selector | Notes |
|---|---|---|
| **New, buyer shell** (bidder-dashboard) | `[class*="avatarWrapper"]` | `UserAvatarPopover` root (`chrome.module.css .avatarWrapper`) wrapping `.avatarFullName` + `.avatarButton`. No `data-testid` exists; the CSS-module class embeds the literal `avatarWrapper`, so the `*=` attribute match is build-stable. |
| **New, admin shell** | `[class*="topBarRight"]` | `dashboard.module.css .topBarRight` (`.userName` + `.userIconWrapper`). |
| **Legacy, both shells** (SNP_UserInfoDisplay) | `.mx-dataview:has(.usericon_settings)` | STRUCTURAL, never `mx-name-*`. The top-right user-info DataView containing the 28px avatar circle `.usericon_settings` (+ name + `.usericon_settings_dropdown`/`_logout` sign-out menu) — classes confirmed in `theme.compiled.css`; widget = `SNP_UserInfoDisplay` DataView (KB `Pages_Snippet/SNP_UserInfoDisplay.md`, MF `ACT_GetCurrentUser` → SignOut). Live-DOM `:has()` verification was blocked by a stale cached `legacy-buyer` session and I did not re-login to `:8082` (read-only-GET rule); a harness run trivially confirms it. |

### Files changed (Pass 3)

- `frontend/src/app/(dashboard)/bidder/dashboard/endOfBiddingPanel.module.css` —
  subtitle 16px + margin-top 9; download 18px/500 + 268×43 pill (asymmetric
  padding + margin-left); panel per-child margins + asymmetric padding; endedRoot
  23 / headingRow 16 (panel frame to y265).
- `frontend/src/components/chrome/chrome.module.css` — `.switchCardCompany`
  weight 400→500.
- `frontend/src/components/bidder/bidderSidebar.module.css` — `.navItem` gap
  19→20 (buyer label ink x55→x56).
- `docs/tasks/parity/evidence/shell-pass3-2026-07-12/` — final new capture + diff
  overlay.

### Residuals left faithful (measured, not chased)

1. **Download button ~1038 px** — Brandon 18px label renders 198 vs legacy 199
   (a 1px width drift → progressive glyph AA across 26 chars) + the pill's odd
   43px height (padding lands 41/44, and an explicit height:43 destabilises the
   centred block). AA/hinting floor.
2. **Sidebar ~372 px** — same-face AA on the labels (position now exact) + the
   legacy circled-nav-icon treatment (the new app uses stroke icons; a
   pre-existing systemic delta explicitly out of the ruled shell scope, see §3
   "Out of scope").
3. **Identity chip 585 px** — env (different account); harness-masked per the
   selectors above.

### Gates
`npx tsc --noEmit` — 31 pre-existing errors, **0 in touched files** (all three
are CSS modules — no TS surface). `npx vitest run src/components/chrome
src/components/bidder src/app/(dashboard)/bidder` — **102/102 pass, 14/14 files**
(the `EndOfBiddingPanel`, `SwitchBuyerCodeCard`, `BuyerPortalChrome`, and sidebar
suites all green — the CSS-only changes touch no asserted DOM/structure).

---

## Pass 4 (2026-07-12) — quick wins (NAV-1, ICON-1, ended-panel 1px)

Three small parity findings, each **measurement-gated** (kept only when a harness
band-profile showed a net improvement vs the legacy PNG). Two shipped; one is
reverted with a corrected premise.

### Method (same rig as Pass 3, re-validated to 0 px)

Throwaway `next dev -p 13000` on this branch, captured with the exact harness
launch args + context (`--force-color-profile=srgb --disable-lcd-text
--font-render-hinting=none --hide-scrollbars`; DPR 1, 1920×1080, light,
reduced-motion, en-US, America/New_York, fixed clock `2026-07-11T12:00-05:00`,
kill-motion CSS, 750 ms settle) via the pinned `@playwright/test` 1.59.1. The
"before" render was **byte-identical** to `tools/parity/out/new/{bidder-dashboard,
admin-reserve-bids-list}__default.png` (**0 diff px** in the sidebar-icon and
panel bands) — so every number below is measured against the true harness legacy
capture. Diff metric: per-pixel `max|Δchannel| > 24`, counted per region.

**CORS note:** `next.config.ts` already server-side-rewrites `/api/v1/* → :8080`,
but the browser's same-origin **POST** to `/auth/login` carries `Origin: :13000`,
which the shared backend's CORS filter (allowlist `:3000`) 403s even through the
rewrite. Worked around exactly as Pass 2/3 documented: authenticate **Node-side**
(no Origin → 200), then inject the `auth_token` cookie + `auth_user` localStorage
into the context; the dashboard's own calls are same-origin GETs (no Origin) and
proxy fine. Capture-script only; `:3000/:8080/:8082` untouched, no app/DB change.

### NAV-1 — "Buyer User Guide" dimmed in the buyer shell → **FIXED**

The **admin** shell's Buyer User Guide already pointed at the `/buyer-user-guide`
stub (enabled). The **buyer (bidder)** shell's item instead pointed at the backend
PDF endpoint `/api/v1/bidder/docs/buyer-guide` and dimmed itself (`opacity:0.45`,
rendered as a non-interactive `<span>`) whenever a mount-time `HEAD` check 404'd —
which it always did in dev. Legacy renders the item **enabled**.

- **Fix** (`BidderSidebar.tsx`): the item now links to the in-app
  `/buyer-user-guide` stub via a normal `<Link>` (enabled, same-tab), and the
  `guideAvailable` state + `useEffect` HEAD probe + `apiFetch` import are gone.
  The book glyph stays plain (legacy renders it plain, un-ringed).
- **Measured** (bidder, "Buyer User Guide" label band x[50,240] y[205,235] vs
  legacy): **648 → 315** diff px (−333). The now-enabled label/icon renders full
  white like legacy instead of the dimmed grey; residual 315 is glyph/font AA.
- **Test:** `wholesale-bidder-shell.spec.ts` §3b rewritten — was
  `target=_blank` + href `…/bidder/docs/buyer-guide`; now asserts `href ===
  '/buyer-user-guide'`, `target` null, `aria-disabled` null. (Stub page body left
  untouched, as scoped.)

### Ended-panel bottom border +1px → **FIXED (kept — net improvement)**

`endOfBiddingPanel.module.css` `.panel` `min-height: 592 → 593`.

- **Measured** (bidder, vs legacy): bottom-border row (x[475,1672] y[850,862])
  **2372 → 2**; whole panel band (y[250,860]) **4236 → 1866** (−2370, the entire
  improvement is the border landing on legacy's y857 row).
- The Pass-3 worry ("a 1px block shift re-ghosts ~1200 px") **did not
  materialise**: `min-height` grows the panel box *downward* (top is pinned by
  the heading row above), so the `justify-content:center` content block moves
  only ~0.5 px — below the AA threshold, 0 re-ghost (the −2370 came entirely from
  the border row, with the rest of the band unchanged at 1866). Kept.

### ICON-1 — sidebar icon glyph treatment → **PREMISE CORRECTED; ring-add REVERTED (regresses)**

**The finding's premise is inverted** (the third such case in this program, cf.
LOGIN-P2, SHELL-P2). The finding/mission read "new sidebar icons render with a
circled treatment; legacy uses plain glyphs." The fresh harness captures show the
**opposite**:

- **Legacy = circled.** Bidder rings Auction + Credit Requests (the Buyer User
  Guide book is plain). Admin rings 9 of 13 — Users · Buyers · Inventory · PO ·
  RB · Auction Scheduling · Auction · Credit Requests · Reports — and leaves
  **Bid as Bidder · Settings · Admin · Buyer User Guide** plain.
- **New = plain.** Every SVG glyph is a bare stroke icon; only the PO/RB *text
  badges* carry a circle.

Ring geometry (harness-measured, for the record): outer **≈34 px** diameter,
centre **x≈26.5**, thin (~1 px) dim stroke (peak Δ≈119 vs a bright glyph stroke
Δ≈120–290); identical for the icon rings and the PO/RB badge rings.

To match legacy we therefore had to **add** rings (not remove them). Two attempts,
both **measured worse** vs legacy and reverted:

| Attempt | bidder icon band x[0,50] | admin icon band x[0,50] |
|---|---|---|
| before (plain, no ring) | **763** | **7002** |
| 33 px ring, 1.5 px / rgba .7 + PO·RB badge 28→33 | 957 (+194) | 8129 (+1127) |
| 33 px ring, 1 px / rgba .45 (tuned to legacy) | 906 (+143) | 7745 (+743) |

Per-item the story is uniform: Users +113, Buyers +114, PO badge +31, RB badge
+25, … every ringed item regressed. **Root cause:** the dominant delta is glyph
**shape + weight**, which a ring cannot touch — legacy's are thin Mendix
icon-font glyphs of different designs (gavel vs the app's clock, 3-people vs
2-people, building vs briefcase, clipboard vs cube, podium vs wrench, …). Adding a
ring lays *more* white ink at a radius that overlaps legacy's glyph edges and
mis-registers against legacy's thinner/dimmer ring, so it adds mismatch faster
than the ring-overlap resolves it. A secondary factor on admin: the new icon
column sits ~3.5 px right of legacy (glyph centre x30 vs 26.5 — a pre-existing
Pass-2 residual), so an admin ring centred on the glyph is 3.5 px off legacy's.

**Decision: reverted** (icons stay plain, == the before state). A faithful match
is a **dedicated bespoke pass**, not a Pass-4 quick win: redraw ~11 thin-stroke
glyphs to the legacy shapes, add matched concentric ~34 px rings **only** on the
ringed set above (never double-ring PO/RB; leave Bid-as-Bidder/Settings/Admin/
Buyer-User-Guide plain), and null the 3.5 px admin icon-column offset. ICON-1
stays **open** with this evidence + geometry recorded for that pass. (`:8082` was
down during Pass 4, so mirroring the legacy icon-font asset directly was not
possible; the recreate-to-match path is the remaining option.)

### Files changed (Pass 4)

- `frontend/src/components/bidder/BidderSidebar.tsx` — NAV-1 (internal enabled
  `/buyer-user-guide` link; dropped the HEAD-check dimming + `apiFetch`/effect/state).
- `frontend/src/app/(dashboard)/bidder/dashboard/endOfBiddingPanel.module.css` —
  `.panel` `min-height 592 → 593`.
- `frontend/tests/e2e/wholesale-bidder-shell.spec.ts` — §3b assertion updated to
  the NAV-1 behaviour.
- (ICON-1 ring edits to `dashboard.module.css`, `bidderSidebar.module.css`,
  `layout.tsx`, `BidderSidebarItem.tsx`, `lib/types.ts` were made, measured, and
  fully reverted — **no** net change to those files.)

### Gates (Pass 4)
`npx tsc --noEmit` — **0 errors in touched files** (31 pre-existing, unchanged).
`npx vitest run src/components/chrome src/components/bidder
src/app/(dashboard)/bidder` — **102/102 pass, 14/14 files**.

> **findings.md follow-up (owned by the register agent, not touched here):**
> NAV-1 → `fixed`; ICON-1 → keep `open` but correct the premise (legacy is
> circled, new is plain) and note that a ring-only fix measurably regresses —
> it needs the bespoke glyph+ring redraw described above.

---

## Pass 5 (2026-07-13) — bespoke icons (ICON-1, both shells)

Executes the Pass-4 decision: replace all 16 sidebar glyphs (admin 13 +
bidder 3) with the **real legacy Mendix nav art**, add the ring to exactly the
ringed set, and null the 3.5px admin icon-column offset. The prior ring-only
attempt regressed because the dominant delta is glyph **shape/weight**; Pass 5
fixes that by shipping the actual legacy vectors, not redraws, wherever they
exist on disk.

### Glyph source — the assets ARE the legacy art

The legacy sidebar uses **image-based nav items** (custom SVGs), not an icon
font, for the ringed set. Found on disk (read-only) at
`C:\Users\Ashirwad.Mittal\Mendix\Auctions UI-Release10\deployment\web\img\`.
Each ringed SVG is a 34×34 white glyph with the ring **baked in** exactly as
legacy authored it: `<circle opacity="0.33" cx="17" cy="17" r="16.5"
stroke="white"/>` (34px dia, ~1px dim stroke — the geometry Pass 4 measured).
The 4 plain glyphs came from the Atlas / FontAwesome-Pro fonts (extracted to
SVG via `fontTools` `SVGPathPen`) or, where no asset matched, one bespoke redraw.

| # | Admin item | Legacy glyph | Ring | `public/icons/sidebar/…` provenance |
|---|---|---|---|---|
| 1 | Users | 3-person group | ● | `users.svg` — **verbatim** `AuctionUI$…$UserManagement_3.svg` |
| 2 | Buyers | office building | ● | `buyers.svg` — **verbatim** `…$BuyerManagement.svg` (building — filenames mislead; the *Buyers* slot pixel-matches BuyerManagement, the *Inventory* slot matches InventoryManagement — verified, a latent swap avoided) |
| 3 | Inventory | clipboard + clip | ● | `inventory.svg` — **verbatim** `…$InventoryManagement_2.svg` (clipboard) |
| 4 | Purchase Order | "PO" letters | ● | `purchase-order.svg` — **verbatim** `…$PurchaseOrderLogo.svg` (34×35 art scaled into the 34-box, exactly as legacy's slot does) |
| 5 | Reserved Bids (EB) | "RB" letters | ● | `reserve-bids.svg` — **verbatim** `…$ReservedBidsLogo.svg` |
| 6 | Auction Scheduling | gavel + block | ● | `auction.svg` — **verbatim** `…$AuctionScheduling.svg` |
| 7 | Bid as Bidder | dome/podium lamp | ○ | `bid-as-bidder.svg` — **bespoke redraw** (no matching asset; FA `lamp-desk`/`lamp-floor`/`lamp-street` all measured wrong) |
| 8 | Auction | gavel + block | ● | `auction.svg` (reused) |
| 9 | Credit Requests | ↵ enter-arrow | ● | `credit-requests.svg` — FA-Pro **`arrow-turn-down-left`** (U+E2E1) extracted white + the legacy ring (reply_email was the wrong weight/shape) |
| 10 | Reports | bars + up-arrow | ● | `reports.svg` — **verbatim** `…$Reports.svg` |
| 11 | Settings | filled gear | ○ | `settings.svg` — `Email_Connector$…$settings_solid.svg` recolored white |
| 12 | Admin | filled chess king | ○ | `admin.svg` — FA-Pro **`chess-king`** (U+F43F) extracted white |
| 13 | Buyer User Guide | open book | ○ | `buyer-guide.svg` — Atlas **`book-open`** (`mx-icon-book-open`, U+E92F) extracted white, sized 16.5px |

**Bidder shell (3):** Auction → `auction.svg` (gavel, ●), Credit Requests →
`credit-requests.svg` (arrow, ●), Buyer User Guide → `buyer-guide.svg` (book,
○) — the same assets. `●` = ringed (9 admin / 2 bidder), `○` = plain.

### Geometry (offset null)

Shared `SidebarIcon` renders `<img src="/icons/sidebar/{name}.svg" width=34
height=34 alt="" aria-hidden>`. Both shells' `.navIcon` is now a **34×34** box
(= the legacy ring diameter); `.navItem` `padding-left: 9.5px` + `gap: 12.5px`
centres the glyph/ring at **x=26.5** (the legacy icon-column centre — nulls the
pre-existing 3.5px admin offset, glyph centre was x=30) while keeping the label
box at **x=56** (unchanged — labels do not move). Bidder was 20px @ centre 26 /
admin 28px @ centre 30 → both now 34px @ centre 26.5.

### Per-slot before/after — icon band x[0,50], diff-px vs the legacy PNG (`max|Δ| > 24`)

Measured under the harness rasterization (see Method). `before` = plain icons
(git-stashed this branch, identical rig); `after` = bespoke.

| Admin slot | before | after | | Bidder slot | before | after |
|---|---|---|---|---|---|---|
| Users | 357 | **0** | | Auction · | 274 | **0** |
| Buyers | 379 | **0** | | Credit Requests | 255 | **118** |
| Inventory · | 2199 | 2138 | | Buyer User Guide | 266 | **225** |
| Purchase Order | 335 | **185** | | **TOTAL** | **795** | **343** (−56%) |
| Reserved Bids | 334 | **0** | | | | |
| Auction Scheduling | 373 | **0** | | | | |
| Bid as Bidder | 223 | **188** | | | | |
| Auction | 326 | **0** | | | | |
| Credit Requests | 275 | **122** | | | | |
| Reports | 287 | **0** | | | | |
| Settings | 292 | **157** | | | | |
| Admin | 306 | **174** | | | | |
| Buyer User Guide | 324 | **229** | | | | |
| **TOTAL** | **6010** | **3193** (−46%) | | | | |

**Every slot improved; zero regressions** (inverts Pass 4's ring-only regress:
bidder 763→906, admin 7002→7745). 7 slots hit **0** (pixel-identical — the
verbatim assets). `·` Inventory (admin) + Auction (bidder) carry a **constant
highlight-background** mismatch — legacy's reserve-bids capture double-highlights
Inventory (a legacy bug we deliberately don't reproduce, ruling 3) and our
bidder capture is on the active Auction route — so that residual is nav-state,
not the icon (the `before→after` delta isolates the glyph, which still improved).
Excluding Inventory's bg artifact, admin pure-icon deltas fell ~72%. Remaining
non-zero residuals (PO 185, plain Settings/Admin/Bid-as-Bidder, book 225-229,
arrow 118-122) are AA/shape floor for the font-extracted + squished-PO +
redrawn glyphs — closest available real art, all net improvements.

Iteration notes (measurement-gated): PO first shipped viewBox-normalized (222);
**reverted to verbatim** → 185. Book first at 20px (265) → **16.5px** (229/225,
the measured best; 15px was worse at 231/230).

### Files changed (Pass 5)

- `frontend/public/icons/sidebar/*.svg` — **new**, 12 assets (7 verbatim legacy
  SVGs, `settings.svg` recolored, 3 font-extracted `credit-requests`/`admin`/
  `buyer-guide`, 1 redrawn `bid-as-bidder`).
- `frontend/src/components/chrome/SidebarIcon.tsx` — **new** shared `<img>`
  component (+ `SidebarIcon.test.tsx`, 15 RTL cases).
- `frontend/src/app/(dashboard)/layout.tsx` — admin nav uses `<SidebarIcon>`
  (dropped 11 inline SVGs + the 2 PO/RB text badges).
- `frontend/src/app/(dashboard)/dashboard.module.css` — `.navIcon` 34×34,
  `.navItem` pad-left 9.5 / gap 12.5 (centre 26.5), `.navIcon img`, dropped the
  orphaned `.textBadge`, collapsed rail re-centred.
- `frontend/src/components/bidder/BidderSidebar.tsx` — uses `<SidebarIcon>`.
- `frontend/src/components/bidder/BidderSidebarIcons.tsx` — **deleted**
  (orphaned; only BidderSidebar imported it).
- `frontend/src/components/bidder/bidderSidebar.module.css` — same 34px /
  centre-26.5 geometry + collapsed centring.

### Method / verification

Same rig as Pass 3/4, re-validated: throwaway `next dev -p 13000` on this
branch, captured with the exact harness launch args (`--force-color-profile=srgb
--disable-lcd-text --font-render-hinting=none --hide-scrollbars`; DPR 1,
1920×1080, light, reduced-motion, en-US, America/New_York, kill-motion CSS,
750ms settle) via pinned `@playwright/test` 1.59.1. `/api/v1` proxies Node-side
to `:8080` (auth POST done Node-side — no `Origin` → CORS-clean — then the
`auth_token` cookie + `auth_user` localStorage injected); bidder uses the HN
deep-link `?buyerCodeId=84` (resolved from `/auth/buyer-codes`). Before/after
from a `git stash` of this branch under the identical rig. `:3000/:8080/:8082`
untouched; no app/backend/DB change.

### Gates (Pass 5)

`npx tsc --noEmit` — **0 errors in touched files** (same 31 pre-existing
unrelated errors). `npx vitest run src/components/chrome src/components/bidder`
— **43/43 pass, 7/7 files** (incl. the new `SidebarIcon` suite; the bidder-shell
+ chrome suites unaffected — labels/testids unchanged).
