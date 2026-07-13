# Parity Findings Register

The single durable ledger for **every** finding the parity program produces. Chat transcripts and
the gitignored `tools/parity/out/` do not survive; this file does. Every harness run, manual
audit, or data-gate sweep appends here; implementation tasks are built by lifting entries out of
this register (grouped by page or by root cause).

**Lifecycle** (matches `parity-program-plan-2026-07-11.md` §4.2):
`open → in-progress → fixed | accepted (ADR ref) | wont-fix (rationale)`
Classification routes the work: `style-fix` → frontend · `data-fix` → backend ·
`feature-gap` → its own planned task · `env-noise` → harness mask/config ·
`infra` → migration/tooling. Severity uses the audit rubric (CRITICAL/HIGH/MEDIUM/LOW):
CRITICAL/HIGH block a page's sign-off.

**Entry format** — every finding records: id · date · page/surface · layer
(`pixel|text|data|function|env|infra`) · severity · classification · expected (legacy) vs actual
(new) · evidence path · suggested fix · route · status. When per-page manifests
(`docs/tasks/parity/pages/*.yaml`) exist, their `discrepancies[]` reference these ids — the
register stays the roll-up.

Evidence for the entries below: `docs/tasks/parity/evidence/h0-2026-07-11/` (persisted copies of
the harness captures + diff overlays). Regenerate any time with:
`cd frontend && NODE_PATH="$(pwd)/node_modules" npx playwright test -c ../tools/parity/playwright.parity.config.ts`
then `npx reg-cli tools/parity/out/new tools/parity/out/legacy-local ... -M 0.1`.

---

## Open findings

### LOGIN-P1 — login card geometry does not match legacy
- **Date:** 2026-07-11 · **Page:** `/login` (new) vs `/p/login/web` (legacy buyer login) · **Layer:** pixel · **Severity:** HIGH · **Class:** style-fix → frontend
- **Expected (legacy):** card anchored higher on the page (top ≈ 125px at 1920×1080), photo pane
  and form pane split at the horizontal midline (form pane starts ≈ x=960), card bottom ≈ 730px.
- **Actual (new):** card vertically centered lower (top ≈ 220px), narrower photo pane (form pane
  starts ≈ x=900), card bottom ≈ 860px — overall the card is displaced and proportioned
  differently.
- **Evidence:** `evidence/h0-2026-07-11/legacy-local-login-buyer.png` vs `new-login.png`;
  overlay `diff-parity-login-buyer.png`.
- **Fix:** match legacy's card position/size/split ratio exactly (measure via
  `getComputedStyle`/`getBoundingClientRect` on `:8082` per the audit playbook).
- **Status:** **FIXED + VERIFIED 2026-07-12** across four convergence passes (commits 46161e3c,
  dcbd139e-era pass 2, 7dbed04f, f233bf4c). Final harness overlay: structure, photo, headings,
  inputs, borders, buttons, footer all clean; ~361 solid px of documented irreducibles remain
  (1px Mendix photo-column overflow, eye-icon pixel-grid straddle, sub-pixel label AA) —
  recorded in `pages/auth-login.yaml` (status: green) + impl doc Pass 4. Notable root causes
  fixed en route: proxy.ts 307'd all /fonts/ on unauthenticated pages (cross-cutting — page
  rendered in Arial); missing Founders Grotesk assets; asymmetric legacy card radii (17/22/22/13);
  the input border is Atlas #898787, not the theme's #534F4C (render > source).

### LOGIN-P2 — login heading rendered in the wrong typeface (premise CORRECTED 2026-07-12)
- **Date:** 2026-07-11, corrected 2026-07-12 · **Page:** `/login` · **Layer:** pixel · **Severity:** HIGH · **Class:** style-fix (font) → frontend
- **CORRECTION:** the original entry assumed legacy used Brandon Grotesque here — the impl agent
  proved from the live theme (`theme.compiled.css` + `Login_New.page.xml`) that the legacy login
  card/heading/footer use **Founders Grotesk** (500/30px/#3C3C3C); the NEW app was the one
  wrongly rendering the heading in Brandon.
- **Fix applied (branch merged 46161e3c):** heading/card/footer moved to Founders via
  `--font-family-pws`; the full 12-file Brandon OTF set was still shipped + `@font-face` mapped
  to legacy's exact weights (cross-cutting benefit for every other page) and the
  `--font-family-primary` fallback stack aligned.
- **Status:** fixed — pending strict-pixel verification (see LOGIN-P1)

### LOGIN-P3 — footer missing (Privacy Policy + copyright)
- **Date:** 2026-07-11 · **Page:** `/login` · **Layer:** pixel + text · **Severity:** MEDIUM · **Class:** feature-gap (small) → frontend
- **Expected (legacy):** below the card: "Privacy Policy" link (left, underlined, white) and
  "© 2026 ecoATM, LLC. All Rights Reserved." (right).
- **Actual (new):** no footer at all.
- **Evidence:** same H0 pair.
- **Fix:** add the footer row with identical copy, link target, and placement.
- **Status:** **FIXED + VERIFIED 2026-07-12** — footer added (real href
  `https://www.ecoatm.com/pages/privacy-policy`, dynamic © year mirroring legacy's
  `formatDateTime`) and pixel-clean in the final harness overlay.

### ENV-1 — Studio Pro demo-user widget pollutes every legacy-local capture
- **Date:** 2026-07-11 · **Page:** all legacy-local pages (first seen on `/p/login/web`) · **Layer:** env · **Severity:** LOW (but touches every capture) · **Class:** env-noise → harness/user decision
- **Detail:** the local Mendix runtime shows Studio Pro's floating demo-user button on the right
  edge (small teal square, ~y=250 @1920×1080); hosted QA does not. Sole pixel difference on the
  otherwise-identical buyer-login certification pair.
- **Evidence:** `evidence/h0-2026-07-11/diff-cert-login-buyer.png` (the only lit region).
- **Options:** (a) user disables demo users in Studio Pro (App Security → demo users) — removes
  it at the source, preferred; (b) standing legacy-local-side mask in the harness with a
  `masks.registry.json` entry.
- **Status:** **DECIDED 2026-07-12: option (a)** — user will disable demo users in Studio Pro
  and re-run the app. The harness's `legacyLocalHideCss` stays as belt-and-braces until the
  widget is confirmed gone from a fresh legacy capture, then gets removed.

### RBL-D1 — reserve_bid row count off by 2 — RESOLVED AS CATALOGUED DELTA 2026-07-12
- Diagnosis (impl agent, `docs/tasks/parity/impl/reserve-bids-data-fixes.md`): exactly the two
  duplicate `(product_id, grade)` pairs the new UNIQUE constraint legitimately collapses —
  13038/F_NYN/H_NNN and 16456/E_YYN, each twin carrying an **identical bid**, most-recent row
  survived. No price data lost; no migration change. Catalogued (schema-map §4 #13).
- **Status:** accepted (catalogued delta)

### RBL-D2 — `product_id` is VARCHAR in `auctions.reserve_bid` → lexicographic sort/filter semantics
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list (+ anywhere product_id sorts/filters) · **Layer:** data+function · **Severity:** HIGH · **Class:** data-fix → backend
- Legacy `productid` is numeric; new column is text — ascending sort yields '1','10206','10211',
  …,'73' instead of 1,73,76…; numeric comparators ("=", ">") behave differently too.
- **Evidence:** `evidence/h1-2026-07-12/` grid pair; `information_schema` check.
- **Status:** **fixed 2026-07-12** — V94 (`product_id` → BIGINT, verified on reseeded dev DB)
  + full Java/TS ripple incl. `::text` casts at the 4 recalc ecoid-join sites and
  `FilterColumn.PRODUCT_ID` → NUMERIC. RBL-P1 fixed in the same branch: default order =
  `legacy_id ASC NULLS LAST, id ASC` (verified: yields legacy's 73, 76, 78, 79, 496 — plain
  `id ASC` would NOT have matched because V77 seeded in product_id order).

### RBL-P1 — default grid ordering differs from legacy
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list · **Layer:** pixel+function · **Severity:** HIGH · **Class:** style-fix → frontend/backend
- Legacy default = insert/object order (starts at product 73, no sort arrow active); new default =
  product_id ascending (lexicographic, see RBL-D2). First screen of rows is completely different
  despite near-identical data.
- **Status:** open

### RBL-P2 — date format mismatch on Last Updated
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list (systemic — every timestamp column) · **Layer:** text · **Severity:** HIGH · **Class:** style-fix → frontend
- Legacy: `12/09/25 at 02:17 PM EST` (2-digit year, "at", TZ label). New: `12/3/2025, 11:16:37 AM`
  (no zero-pad, 4-digit year, seconds, no TZ). Legacy format is the convention (plan DoD gate 3).
- **Status:** open

### RBL-P3 — per-row actions diverge (eye icon vs Edit/Audit/Delete links)
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list · **Layer:** pixel+function · **Severity:** HIGH · **Class:** feature-gap/product → decide match-legacy vs ADR
- Legacy: one eye icon per row (opens audit/view modal; RB-14 lineage). New: Edit + Audit + Delete
  text links under an "Audit" header column.
- **RULED 2026-07-12 (user): match legacy — eye icon only, audit as modal (RB-14 pattern).**
- **Status:** in-progress — reserve-bids page agent dispatched 2026-07-12 (also RBL-P2/P4/P5)

### RBL-P4 — toolbar composition/order differs
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list · **Layer:** pixel · **Severity:** MEDIUM · **Class:** style-fix + product
- Legacy: `[Download] [Upload EB Price]` + in-grid column-chooser eye. New:
  `[Upload EB Price] [Download] [New] [Columns]` — order flipped; **`New` is the RB-21 unowned
  addition, still unresolved** (remove or ADR); `Columns` replaces the in-grid chooser.
- **RULED 2026-07-12 (user): remove the New button + `/new` route** — preserves the legacy
  invariant "EB authored only via Excel upload" (closes RB-21/RB-3 per the original audit).
- **Status:** in-progress — same agent as RBL-P3

### RBL-P5 — grid density/pagination below the fold at 1080p
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list (likely all new grids) · **Layer:** pixel · **Severity:** MEDIUM · **Class:** style-fix → frontend
- Legacy fits 20 rows + pager ("1 to 20 of 14659") within 1080 viewport; new shows ~18 taller rows
  with the pager cut off below the fold.
- **Status:** open

### SHELL-P1 — admin shell deltas (sidebar + top bar) affect every admin page
- **Date:** 2026-07-12 · **Page:** shell (seen on both admin grids) · **Layer:** pixel · **Severity:** HIGH (multiplies into every capture) · **Class:** mixed — confirm intentional-vs-fix per item
- Deltas: (a) legacy sidebar has **Credit Requests** item — new lacks it; (b) new Admin submenu
  auto-expanded (4 control centers) vs legacy collapsed chevron; (c) active-item highlight logic
  differs (new highlights both the submenu entry and the page item); (d) top bar — legacy: logo in
  content area + green status dot; new: white bar with "Admin User" + avatar, logo in sidebar.
  Prior audits called some of these "intentional (token brief)" — each needs an ADR ref to move to
  `acceptedDivergences`, else it's a fix.
- **RULED 2026-07-12 (user, all four):** match legacy exactly — (1) add Credit Requests nav to
  BOTH shells, (2) remove the white top bar (logo in content, green dot, person identity per
  legacy), (3) Admin section collapsed + single highlight, (4) in-content Switch-Buyer-Code card.
  ADR superseding the token-brief divergences to be written by the impl agent.
- **Status:** **FIXED + VERIFIED (structure + geometry) 2026-07-12** — pass 1 (a6485aab +
  8a0df628: structure, ADR, SNP_UserInfoDisplay identity incl. bare-dot-when-nameless; legacy
  double-highlight recorded as a legacy bug per user) + pass 2 (0ee3f038: sidebar 232px, 64px
  item pitch, 16px/500 labels, switch-card/heading/panel x-exact on both shells; admin 13 item
  bands exact). Bidder full-frame diff ≈1%. Residuals split out as SHELL-P2 + BDD-P1 addendum.

### SHELL-P2 — shell text renders in a different typeface than legacy (hollow-outline ghosting)
- **Date:** 2026-07-12 (post pass-2 overlay) · **Page:** both shells + ended-panel text · **Layer:** pixel · **Severity:** MEDIUM · **Class:** style-fix → frontend
- Sidebar labels, page heading, ended-copy, and button labels ghost as hollow outlines at exact
  positions — the glyph faces differ. Suspect: shell text uses `--font-family-primary` (Brandon)
  while legacy Atlas/EcoAtm renders these in another face (check `.confirmationheader` /
  sidebar `.mx-navigationtree` families in theme.compiled.css — same trap as LOGIN-P2 where the
  render, not the theme source, is the spec).
- **Also in this band:** ended-panel inner content sits ~10px high (subtitle + download button
  y-offset — inner spacing of `endOfBiddingPanel.module.css` content block); 1px hairline double
  on the panel border; logo slight ghost (raster PNG vs legacy asset rendering).
- **Status:** **RESOLVED 2026-07-12 (premise corrected + fixed, commit 30f5eeb1 merged).**
  Render-verified: every shell text element was ALREADY Brandon — the ghosts were size/weight
  deltas (subtitle 14→16px, download label 14→18px, switch-name weight 400→500) + the panel
  sitting 5px low. Shell diff −75% (8,066 → 1,980 px excl. identity). Remaining floor, all
  documented in impl doc Pass 3: download-button label 1px hinting drift over 26 chars + odd
  43px legacy pill height; panel bottom border 856 vs 857; logo/heading AA. Identity masks
  authored in all page manifests (legacy `.mx-dataview:has(.usericon_settings)`, new
  `[class*="avatarWrapper"]` / `[class*="topBarRight"]`) — note: the two mask BOXES differ in
  size per side, so the painted rectangles themselves contribute a small constant diff strip
  (harness artifact; candidate H2 fix = same-region rectangle masking).

### NAV-1 — "Buyer User Guide" renders dimmed in the new buyer shell
- **Date:** 2026-07-12 (typeface-pass observation) · **Layer:** pixel+function · **Severity:** LOW · **Class:** feature-gap → frontend
- Legacy renders the item enabled; the new shell dims it (route `/buyer-user-guide` exists as a
  stub but the nav marks it unimplemented). Enable the nav state (and verify the stub page).
- **Status:** **FIXED 2026-07-12** (0b12d8b7 merged) — the bidder shell was dimming itself via a
  mount-time HEAD probe against the backend PDF endpoint (always 404 in dev); now a normal
  enabled Link to the stub. Label band diff 648 → 315 px. Same commit: ended-panel bottom border
  +1px landed on legacy's y857 (border row 2372 → 2 diff px; no content re-ghost).

### ICON-1 — sidebar icon glyphs differ (premise CORRECTED 2026-07-12)
- **Date:** 2026-07-12, corrected same day · **Layer:** pixel · **Severity:** LOW · **Class:** style-fix → frontend (needs ruling)
- **CORRECTION (third inverted premise after LOGIN-P2/SHELL-P2):** fresh captures prove
  **legacy = circled** (9/13 admin, 2/3 bidder glyphs, ~34px dia thin dim ring), **new = plain**.
  A faithful measured ring-add was implemented and REVERTED — it regressed both shells
  (bidder icon band 763→906, admin 7002→7745) because the dominant delta is glyph
  **shape/weight** (legacy's thin Mendix icon-font gavel/people/building/clipboard vs the app's
  bolder clock/people/briefcase/cube). A true fix = bespoke redraw of ~11 glyphs + matched rings
  + nulling a pre-existing 3.5px admin icon-column offset (geometry recorded in the shell impl
  doc Pass 4). Alternative: accept plain icons as an intentional divergence (ADR).
- **RULED 2026-07-12 (user): bespoke redraw to match legacy.**
- **Status:** **FIXED 2026-07-13** (4fbb0c3f merged) — legacy's ringed nav icons turned out to be
  IMAGE SVGs with the ring baked in, found on disk
  (`Auctions UI-Release10\deployment\web\img\AuctionUI$Image_collection$*.svg`); 12 real assets
  shipped via a shared `SidebarIcon`, filename mislabels caught by pixel-match. Admin icon band
  6010→3193 (−46%, 6 slots pixel-identical incl. Users/Buyers/ReserveBids/AuctionSched/Auction/
  Reports), bidder 795→343 (−56%, Auction = 0). Every slot improved, zero regressions. Residuals
  = AA/shape floor on the font-extracted/redrawn few (PO/Settings/Admin/BidAsBidder/Guide) +
  the ADR'd legacy double-highlight bg on Inventory. Full table: shell impl doc Pass 5.

### BDD-P3 addendum — identity chip env difference (harness mask needed)
The chip now renders real display names in legacy's style (widget parity done), but the two
sides authenticate different accounts (nadia vs dev-seed "Bidder User"/"Admin User") — the name
text will always differ. Author per-side masks for the identity chip once selectors are pinned;
until then it contributes a small constant diff on every page.

### POL-D1 — purchase-order data parity verified at count level
- **Date:** 2026-07-12 · **Page:** admin-purchase-orders-list · **Layer:** data · **Severity:** — · **Class:** verification note
- Legacy 5 = new 5 POs. Pixel diff pending detailed inspection (systemic classes RBL-P2/P5 +
  SHELL-P1 visibly apply). Evidence pair persisted.
- **Status:** open (inspection)

### BDD-D1 — auctions domain not snapshot-aligned: dashboards show different auction states
- **Date:** 2026-07-12 (bidder-dashboard first capture, code HN both sides) · **Layer:** data · **Severity:** CRITICAL for every bidder-dashboard state · **Class:** data-fix (fixtures) → backend
- **Legacy:** "Auction 2026 / Wk13" heading; ended-state panel "Bidding has ended. / Your bids
  from round 1 can be found below." + **Download your Round 1 Bids** button (snapshot's real
  auction + nadia's round-1 bids exist).
- **New:** "Bidding has ended. / **No scheduled auction is available.**" — `auctions.scheduling_auctions`
  is fixture-seeded (schema-map §3), and the fixtures don't mirror the snapshot's 2026/Wk13
  auction for HN. BD-24 lineage.
- **Fix:** author auction fixtures mirroring qa-0327's week/round/bid state for HN (or extend
  `extract_qa_data.py` to migrate the auction tables). Until then this page can only compare the
  no-auction branch.
- **Evidence:** `evidence/h1-2026-07-12/*bidder-dashboard__default.png`.
- **Status:** **RESOLVED (data layer) 2026-07-12** — V95–V98 merged (auctions core, 162,086
  bid_data incl. HN's 10,951 round-1 bids, 10,951 agg-inventory, QBC re-established at 1,644);
  verified on the reseeded dev DB. The visible gap remains because of app behavior, not data —
  re-scoped into BDD-P1: the new dashboard doesn't select/render the most-recent ENDED auction
  (legacy shows "Auction 2026 / Wk13" + "Download your Round 1 Bids"; new shows "No scheduled
  auction is available" even with the data present). BDD-P1 is now the actionable finding.

### BDD-P1 — ended-state UI diverges (panel, heading, download button)
- **Date:** 2026-07-12 · **Page:** bidder-dashboard · **Layer:** pixel+function · **Severity:** HIGH · **Class:** style-fix/feature-gap → frontend (re-triage after BDD-D1)
- Legacy renders the state inside a large bordered panel with the auction heading above and a
  "Download your Round 1 Bids" pill button; new renders two bare centered text lines — no panel,
  no heading, no download affordance.
- **RE-SCOPED 2026-07-12 (post data-fix):** with identical auction data on both sides, the root
  cause is the new app's **auction-selection logic ignoring ended auctions** (legacy renders the
  most-recent ended one; new only looks for live/scheduled) plus the impoverished ended-state UI.
  Note: the snapshot's round-3 SA is 'Scheduled' with a long-past window and legacy STILL shows
  the ended state — selection must follow the legacy microflows, not status alone.
- **Status:** **FIXED + VERIFIED 2026-07-12** (merged commit 6e009dc2, harness-confirmed on the
  live pair). Selection now mirrors the cited legacy microflows (`ACT_OpenBidderDashboard` /
  `ACT_GetMostRecentAuction` / `ACT_GetActiveSchedulingAuction`): most-recent auction by
  created_date; pivot = any round `Started`, else the ended/download page — heading
  "Auction {yr} / Wk{wk}", panel, per-participated-round "Download your Round N Bids" wired to
  the ownership-guarded export (live-verified: HN → 626KB xlsx of 10,951 R1 bids; R2 → 404;
  wrong-tenant → 403). Notes: subtitle copy + per-round visibility rule are inferred (not in the
  extracted microflows — matches the HN evidence; revisit if a multi-round auction surfaces).
  Residual pixel deltas on this page belong to BDD-P2/BDD-P3.

### RBL-D3 — reserve-bid Last Updated instants differ by ~6 hours (data, not format)
- **Date:** 2026-07-12 (post RBL-P2 — the format is now legacy-exact, exposing the value gap) · **Layer:** data · **Severity:** MEDIUM · **Class:** data-fix → backend
- Same rows, ~6h-shifted display: product 73 `02:17 PM EST` (legacy) vs `08:16 PM EST` (new);
  products 76/78 `10:53 AM` vs `04:52 PM` — a consistent ≈6h signature ⇒ timezone
  interpretation of the stored timestamp differs (suspects: V77 seed wrote naive timestamps that
  V50's TIMESTAMP→TIMESTAMPTZ conversion re-interpreted, or the API/entity assumes UTC where
  legacy stored local). Diagnose against `"ecoatm_eb$reservebid".lastupdatedatetime` raw values.
- **Status:** open

### RBL-P6 — reserve-bids page heading renders ~28px vs legacy ~44px
- **Date:** 2026-07-12 · **Layer:** pixel · **Severity:** MEDIUM · **Class:** style-fix → frontend
- RB-1 lineage (banked audit). Title text matches; size/weight don't. Fix alongside the grid
  fine-geometry pass (column widths + filter-row height also ghost by a few px).
- **Status:** open

### SEC-1 — authenticated wrong-role on bidder endpoints returns 401, not 403
- **Date:** 2026-07-12 (found during BDD-P1 verification) · **Layer:** function/security-hygiene · **Severity:** LOW (denial holds either way) · **Class:** route → SecurityConfig owner
- Live-verified: salesops with a valid cookie → **401** on `/api/v1/bidder/**` (expected 403 per
  the repo's own DoD language); admin → 200. Pre-existing, app-wide bidder-controller behavior —
  not introduced by BDD-P1. Wrong status-code semantics only.
- **Status:** open

### BDD-P2 — buyer shell missing "Credit Requests" nav item
- **Date:** 2026-07-12 · **Page:** buyer shell (auction variant) · **Layer:** pixel+function · **Severity:** HIGH · **Class:** feature-gap → frontend
- Legacy buyer sidebar: Auction · **Credit Requests** · Buyer User Guide. New: Auction · Buyer
  User Guide only — the partial-credit surface exists (`/wholesale/partial-credit`) but is not
  reachable from the bidder shell nav. Buyer-shell counterpart of SHELL-P1.
- **Status:** open

### BDD-P3 — Switch-Buyer-Code widget placement/format + identity chip
- **Date:** 2026-07-12 · **Page:** buyer shell · **Layer:** pixel · **Severity:** HIGH · **Class:** style-fix → frontend (+ harness note)
- Legacy: in-content "Switch Buyer Code" label above a card (buyer name small, **code large**);
  logo in content; top-right shows the person ("Nadia GmailOne" + initials). New: top-bar pill
  (code above name — inverted), ghost logo top-left, right shows "Bidder User".
- **Harness note:** the identity chip text will *always* differ on this page because the two
  sides authenticate different accounts (legacy nadia vs new dev-seed bidder) — mask the identity
  chip per side once selectors are pinned; the widget layout difference is real and stays.
- **Status:** open

---

## Resolved findings (program history)

### MIG-1 — fresh migration chain broken at V33 (V29/V33 table-shape collision) — FIXED 2026-07-11
- **Layer:** infra · **Severity:** CRITICAL (blocked any fresh DB — undetected since the repo's initial commit)
- V29 pre-created placeholder `pws.rma_status` (`status_group`, `bidder_message`) and
  `pws.rma_reason` (`valid_reason` singular); V33's `CREATE TABLE IF NOT EXISTS` silently
  no-opped, then its seed INSERT (`status_grouped_to`, `valid_reasons`) crashed.
- **Fix:** V33 now drops the V29 placeholders first and creates authoritatively (plain
  `CREATE TABLE`). File: `backend/src/main/resources/db/migration/V33__rma_tables.sql`.

### MIG-2 — committed V34 RMA data pointed user FKs at the wrong users — FIXED 2026-07-11
- **Layer:** data · **Severity:** CRITICAL (silent wrong data on every fresh migrate)
- The committed `V34__data_rma.sql` came from an older standalone generator mapped against a
  drifted target DB — `submitted_by_user_id`/`reviewed_by_user_id` were **shifted by one** vs the
  ids a fresh V17 produces (e.g. RMA22379252: 117/121 vs correct 116/120). Caught by the
  provenance diff: regenerated V16–V24 were byte-identical, V34 was not (708 diff lines, all FK
  columns).
- **Fix:** V34 replaced with the `extract_qa_data.py --source-db qa-0327` regeneration —
  self-consistent with V16–V24 by construction (same in-run id maps).

### MIG-3 — `R__apply_triggers.sql` referenced tables V92 renamed — FIXED 2026-07-11 (re-resolved 2026-07-12)
- **Layer:** infra · **Severity:** CRITICAL (broke every fresh chain at the repeatable step)
- Referenced `email.email_template`, dropped/renamed by V92; never failed on the old dev DB
  because repeatables only re-run on checksum change.
- **Fix:** email entries removed from the `updated_date` trigger list — V92's `smtp_config` /
  `template` / `log` carry `changed_date` stamped by the application services
  (runtime-verified via `information_schema`: no `updated_date` column exists).
- **Addendum 2026-07-12:** the RMA Task B0 merge re-introduced trigger entries for
  `('email','smtp_config')` and `('email','template')` in its branch; conflict resolved keeping
  the removal (their version would throw `record "new" has no field "updated_date"` on every
  UPDATE of those tables).

---

## Accepted / catalogued (not defects — see schema-map §4)

### DATA-1 — NULL device prices coerced to `0` by the migration generator — ACCEPTED
22 inactive, zero-qty devices: legacy `currentlistprice = NULL` → new `list_price = 0.00`
(`extract_qa_data.py` uses `or 0`). Catalogued as schema-map §4 #9. Watch item: if any UI surface
renders these devices, "—"-vs-"$0.00" becomes a text-layer finding on that page.

### DATA-2 — `qualified_buyer_codes` empty on fresh chain — SUPERSEDED 2026-07-12
Original state: V72 deleted all 378,755 V23 rows (SA ids never remapped). **Superseded by the
auctions data migration (V95–V98 + V23 remap):** a fresh chain now converges on **1,644** QBC
rows (548 codes × 3 live rounds; 377,111 legacy rows are true orphans pointing at purged
scheduling auctions — excluded by design, logged in the V-file headers). The auctions domain is
no longer fixture-driven; schema-map §3/§4 updated.

---

## Import queue — banked pre-program audit findings

The 2026-05 manual audits produced finding sets that predate this register. They get imported
(with register ids) into their page's manifest entry during H1 scaffolding — do **not**
re-discover them:

| Source doc | Scope | Notable |
|---|---|---|
| `docs/tasks/qa-vs-local-reserve-bids-walkthrough-2026-05-08.md` | RB-1…RB-27 (reserve bids list/audit/upload/detail) | RB-5 list-endpoint 500 (CRITICAL), RB-14 audit modal-vs-route, RB-6 comparator menus |
| `docs/tasks/qa-vs-local-po-walkthrough-2026-05-08.md` | Purchase orders | PK-leakage class ("Week from id 557") |
| `docs/tasks/qa-vs-local-pixel-walkthrough-2026-05-07.md` (+ implementation plan) | Cross-page: login §1.1, bidder dashboard, auction scheduling, round filters | BD-24 empty R2/R3 dashboards (CRITICAL, backend); login email-vs-username = accepted divergence |
| `docs/qa-reference/README.md` | 10 QA-reference surfaces w/ phase mapping | Free manifest-authoring input |

---

## Proposed implementation task #1 — "Login page to pixel-green" (ready to lift)

**Scope:** LOGIN-P1 + LOGIN-P2 + LOGIN-P3, plus the ENV-1 decision.
**Order:** P2 first (font asset is cross-cutting — every later page inherits it), then P1
(geometry), then P3 (footer).
**Acceptance gate:** re-run the H0 capture + `reg-cli` strict compare of `new/login` vs
`legacy-local/login-buyer`; page is green when zero pixels differ outside registered masks
(after the ENV-1 decision, ideally zero masks). Then mark the future `pages/auth-login.yaml`
`status: green`.
**Non-goals:** the admin `/login.html` visual (legacy admin login is a distinct classic page;
the new app deliberately unifies on one login — record as an accepted divergence with ADR if
product confirms).

---

*Register maintenance: append new findings at the bottom of their section; never delete —
move entries between sections as status changes. Update `lastCapturedAt`-style dates inline.*
