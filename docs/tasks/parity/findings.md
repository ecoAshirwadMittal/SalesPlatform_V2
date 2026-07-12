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
- **Status:** in-progress — impl branch merged 2026-07-12 (46161e3c): card frame now exact
  (900×609 @ y=124, 50/50 split, footer added); harness re-run shows residual element-level
  deltas (~8–14px vertical offsets in the form stack, input box heights, photo-scale edge
  ghosting, footer x-offsets, Contact-Us size) — agent iterating to ±1px per element

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
- **Status:** open

### ENV-1 — Studio Pro demo-user widget pollutes every legacy-local capture
- **Date:** 2026-07-11 · **Page:** all legacy-local pages (first seen on `/p/login/web`) · **Layer:** env · **Severity:** LOW (but touches every capture) · **Class:** env-noise → harness/user decision
- **Detail:** the local Mendix runtime shows Studio Pro's floating demo-user button on the right
  edge (small teal square, ~y=250 @1920×1080); hosted QA does not. Sole pixel difference on the
  otherwise-identical buyer-login certification pair.
- **Evidence:** `evidence/h0-2026-07-11/diff-cert-login-buyer.png` (the only lit region).
- **Options:** (a) user disables demo users in Studio Pro (App Security → demo users) — removes
  it at the source, preferred; (b) standing legacy-local-side mask in the harness with a
  `masks.registry.json` entry.
- **Status:** open — **decision needed (user)**

### RBL-D1 — reserve_bid row count off by 2 (14,657 new vs 14,659 legacy)
- **Date:** 2026-07-12 (H1 run) · **Page:** admin-reserve-bids-list · **Layer:** data · **Severity:** MEDIUM · **Class:** data-fix → backend
- Spot-check passed (73/A_YYY = $888.79 both sides; `legacy_id` present) — V77 is qa-0327-derived,
  but 2 legacy rows are missing (likely duplicate (product_id, grade) pairs collapsed by a unique
  constraint). Identify the 2 and either restore or catalogue.
- **Status:** open

### RBL-D2 — `product_id` is VARCHAR in `auctions.reserve_bid` → lexicographic sort/filter semantics
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list (+ anywhere product_id sorts/filters) · **Layer:** data+function · **Severity:** HIGH · **Class:** data-fix → backend
- Legacy `productid` is numeric; new column is text — ascending sort yields '1','10206','10211',
  …,'73' instead of 1,73,76…; numeric comparators ("=", ">") behave differently too.
- **Evidence:** `evidence/h1-2026-07-12/` grid pair; `information_schema` check.
- **Status:** open

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
- **Status:** open

### RBL-P4 — toolbar composition/order differs
- **Date:** 2026-07-12 · **Page:** admin-reserve-bids-list · **Layer:** pixel · **Severity:** MEDIUM · **Class:** style-fix + product
- Legacy: `[Download] [Upload EB Price]` + in-grid column-chooser eye. New:
  `[Upload EB Price] [Download] [New] [Columns]` — order flipped; **`New` is the RB-21 unowned
  addition, still unresolved** (remove or ADR); `Columns` replaces the in-grid chooser.
- **Status:** open

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
- **Status:** open — needs product/ADR pass

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
- **Status:** open

### BDD-P1 — ended-state UI diverges (panel, heading, download button)
- **Date:** 2026-07-12 · **Page:** bidder-dashboard · **Layer:** pixel+function · **Severity:** HIGH · **Class:** style-fix/feature-gap → frontend (re-triage after BDD-D1)
- Legacy renders the state inside a large bordered panel with the auction heading above and a
  "Download your Round 1 Bids" pill button; new renders two bare centered text lines — no panel,
  no heading, no download affordance.
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

### DATA-2 — `qualified_buyer_codes` is empty on a fresh chain — ACCEPTED (by design)
V72 deletes all 378,755 V23-seeded rows (legacy scheduling-auction ids were never remapped;
V64 added the FK `NOT VALID` for exactly this reason). QBC is rewritten per scheduling auction by
the R2/R3 services → fixture-driven parity contract (schema-map §3, §4 #12).

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
