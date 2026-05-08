# QA vs Local — Pixel + Functional Walkthrough (2026-05-07)

**Scope:** Auction-lifecycle only (option **c** from scoping). Both admin
and bidder, end-to-end one auction. QA app vs local
(`localhost:3000` / `localhost:8080`).

**Method:** Create a fresh auction on both sides with short round windows
(10 min each) so R1 → R2 → R3 transitions can be exercised live in one
session. Walk admin + bidder side-by-side, capture screenshots into
`screenshots/`, log every functional delta and every visible visual
delta in the table per phase.

**Severity legend:**
- **CRITICAL** — local cannot perform action that QA can; user data integrity risk
- **HIGH** — local action exists but is materially different; missing field/control affects workflow
- **MEDIUM** — visible workflow delta or copy delta likely to confuse a SalesOps user trained on QA
- **LOW** — cosmetic only (icon, color, spacing)

**Test auction name suffix:** `QA-Local-Test`
- QA: Auction 2026 / Wk20 QA-Local-Test (Mendix backend on Wk20 inventory)
- Local: Auction 2026 / Wk19 QA-Local-Test (id 623; local DB latest week is Wk19)

---

## Phase 1 — Login + auction creation

### 1.1 Admin login
| Aspect | QA | Local | Severity | Note |
|---|---|---|---|---|
| URL | `/login.html` | `/login` | LOW | Path diff |
| Layout | Bare username/password card | Photo + branded "Premium Wholesale & Weekly Auctions" card | LOW | Local is the buyer/PWS login page reused for admin |
| Fields | User name + Password | Email + Password | **MEDIUM** | QA = legacy AD-style username; local = email-only |
| Extras | (none) | "Remember me?", "Forgot Password?", "Employee Login" SSO, "Contact Us" | LOW | Local extras are net additions, not gaps |

Screenshots: `01a-local-login.png`. (QA login page not screenshot'd — bare two-field form.)

### 1.2 Sidebar nav inventory
QA admin sidebar: Users, Buyers, Inventory, Purchase Order, Reserved Bids (EB), Auction Scheduling, Bid as Bidder, Auction, Reports, Settings, Admin, Buyer User Guide.

Local admin sidebar: Users, Buyers, Inventory, Purchase Order, Reserved Bids (EB), Auction Scheduling, Bid as Bidder, Auction, Reports, Settings, Admin (with sub-menu: Application Control Center / Auction Control Center / Auctions Data Center / PWS Data Center).

| Item | QA | Local | Severity |
|---|---|---|---|
| Buyer User Guide | ✅ Present | ❌ Missing | **MEDIUM** — gap (helpdesk link absent) |
| Admin sub-menu | (collapsed top-level) | Expandable with 4 children | LOW (better in local) |
| Sidebar color | Solid green | Teal gradient (`#407874` per CLAUDE.md spec) | LOW (tokens diverge from QA but match the spec) |

### 1.3 Inventory page
| Element | QA | Local | Severity |
|---|---|---|---|
| Header title | "Inventory" | "Inventory" | Match |
| Page `<title>` | "ecoATM Direct - Inventory Snowflake" | "ecoATM Sales Platform" | LOW |
| Week selector | Combobox styled as pill | Native `<select>` styled as pill | LOW |
| Create Auction button | Green pill **with hammer icon** | Dark teal rectangle, no icon | **MEDIUM** — icon missing |
| Refresh button | Green pill | Dark teal rectangle / outlined | **MEDIUM** |
| Stats row | Inline label-then-value, single row, no border | Bordered card boxes per stat | **MEDIUM** — visual layout diverges |
| Currency formatting | `$1,980,410` (no cents) | `$1,876,277.65` (2 decimals) | **MEDIUM** — rounding policy differs |
| Filter row per column | Comparator combobox (Equal/Contains) + textbox | Textbox only | **HIGH** — local missing comparator selector |
| Per-row Edit | (none — uses column-selector eye instead) | Edit link in trailing column | LOW (local additive) |
| Pagination wording | "Currently showing 1 to 20 of 11518" | Same, smaller font | LOW |
| Download button | Bottom right | Bottom left | LOW |

Screenshots: `01a-qa-admin-landing.png`, `01c-local-inventory.png`.

### 1.4 "No active auction" handling
QA: Clicking **Auction Scheduling** with no auction shows an "Information — No active auction" modal and bounces the user to Inventory. **Local: not tested** (different UX — local routes directly to scheduling-auction list).

| Behavior | QA | Local | Severity |
|---|---|---|---|
| Empty-state guard | Modal "No active auction" forces user to Inventory | Empty list rendered (no forcing modal) | **MEDIUM** — UX divergence; local may feel less guided |

### 1.5 Create Auction modal
| Field | QA | Local | Severity |
|---|---|---|---|
| Title | "Create Auction" | "Create Auction" | Match |
| Helper text | "The default name will follow the format Auction Year/Week. Alternatively, you can add a customized name next to the default one." | "An auction will be created for the selected week. You can add an optional suffix to distinguish auctions scheduled for the same week." | **LOW** — wording differs but intent matches |
| Field label | (none) | "Auction Name" | LOW (local additive) |
| Prefix display | "Auction 2026 / Wk20" (read-only segment in chip) | "Auction 2026 / Wk19" (read-only segment) | Match |
| Suffix textbox | No placeholder | Placeholder "Optional suffix" | LOW (local additive) |
| Live preview | (none) | "Preview: Auction 2026 / Wk19" | LOW (local additive) |
| Warning copy | 2 paragraphs: inflated-quantity warning + "cannot change inventory once created" | **Missing** | **MEDIUM** — local missing user warnings |
| Round-windows mention | (none) | "Three bidding rounds will be scheduled automatically based on the week start time. Round start and end times can be adjusted afterwards from the auction overview." | LOW (local additive) |
| Cancel button | Close X only | "Cancel" + close X | LOW |
| Submit label | "Create Auction" | "Create" | **LOW** — label inconsistent |

Screenshots: `01d-qa-create-auction-modal.png`, `01d-local-create-auction-modal.png`.

---

## Phase 2 — Schedule auction

### 2.1 Post-create navigation
- **QA:** redirects to **Auction Scheduling** page showing the new auction selected, all 3 rounds with editable date/time, and an inventory preview table at the bottom.
- **Local:** redirects to `/admin/auctions-data-center/auctions/{id}/schedule` showing 3 rounds with editable date/time. **No inventory preview.**

### 2.2 Schedule page comparison
| Element | QA | Local | Severity |
|---|---|---|---|
| Page title | "Auction 2026 / Wk20 QA-Local-Test" | "Auction 2026 / Wk19 QA-Local-Test" | Match |
| Auction-switcher dropdown (top-right) | Yes — combobox to swap between auctions in-place | **No** — back link only | **MEDIUM** — local missing inline auction switcher |
| Status badge | (not shown post-creation) | "Unscheduled" pill | LOW (local additive) |
| Back navigation | (none — uses sidebar) | "← Back to Inventory" link | LOW (local additive) |
| Round 1 stats (Buyers / Total / DW Only) | "Buyers All / Total 638 / DW Only 116" inline next to round header | **Not displayed** | **HIGH** — local missing inventory-impact summary per round |
| Round layout | R1 + R2 side-by-side, R3 below | All 3 rounds stacked vertically | **MEDIUM** — different information density |
| R3 label | "Round 3" | "Upsell Round" | **MEDIUM** — terminology divergence (legacy uses numbered, local uses semantic name) |
| R2/R3 active toggle | Checkbox next to header | Checkbox top-right of card with "Round 2 active" / "Upsell round active" labels | LOW |
| Date/time inputs | Two combined inputs per row (date+calendar icon, time) | Four separate inputs per round (From Date / From Time / To Date / To Time) | **MEDIUM** — different UX |
| **Timezone label** | "EST" displayed next to each row | **Missing** | **HIGH** — local does not show timezone; ambiguous for users |
| Selection Rules link | One link top-right of rounds area | Per-round "Selection Rules" link | **MEDIUM** — placement differs (impact on per-round criteria UX) |
| Inventory preview table | Paginated table below rounds (1 to 20 of 11,518) showing what will be auctioned | **Missing** | **HIGH** — local missing inventory preview on schedule page |
| Submit | "Schedule Auction" (green pill, hammer icon) | "Confirm" (dark teal, no icon) | **MEDIUM** — label diverges + missing icon |
| Cancel | (close via auction selector / nav) | "Cancel" button | LOW (local additive) |
| Delete | (must be elsewhere — Auction Scheduling list) | "Delete Auction" (red outline) inline | LOW (local additive — convenient placement) |
| Default round windows | R1: today 7PM → +4 days 12PM; R2: +4d 12:10PM → +5d 12PM; R3: +5d 12:10PM → +6d 12PM | R1: **3 days ago 4PM** → +4 days 7AM; R2/R3 follow with past starts | **HIGH BUG** — local default round windows start in the **past** |

Screenshots: `02a-qa-after-create-auction-scheduling.png`, `02b-local-after-create-schedule.png`.

### 2.3 Findings carried to implementation plan (phases 1–2)
- **HIGH BUG**: local default schedule windows start in the past. Mendix uses "now → +N days"; local appears to anchor on `week.startDate` of the selected week, which can already be in the past once we're past mid-week.
- **HIGH GAP**: per-round Buyers / Total / DW-Only summary missing on schedule page.
- **HIGH GAP**: timezone label missing on schedule page (and likely throughout the auction admin).
- **HIGH GAP**: inventory preview table missing on schedule page.
- **MEDIUM GAP**: auction-switcher dropdown missing on schedule page.
- **MEDIUM GAP**: "Buyer User Guide" sidebar link missing.
- **MEDIUM GAP**: inventory filter comparator (Equal/Contains) missing.
- **MEDIUM GAP**: "No active auction" empty-state guard modal not implemented for the Auction Scheduling shortcut.
- **MEDIUM GAP**: Create Auction modal missing inflated-quantity warning copy.
- **MEDIUM**: terminology — local says "Upsell Round", QA says "Round 3". Pick one and standardize.
- **LOW**: button labels and icons (hammer icon, "Schedule Auction" vs "Confirm", "Create Auction" vs "Create") inconsistent across pages.

---

## Phase 3 — Pre-R1 admin views (Auction Scheduling list, Selection Rules)

### 3.1 "Auction Scheduling" sidebar entry — different organization
| Aspect | QA | Local | Severity |
|---|---|---|---|
| Click target | Single-auction scheduling editor with auction-switcher dropdown | Multi-auction list (`/admin/auctions-data-center/schedule-auction`) | **HIGH** — fundamentally different IA |
| Sidebar nav target | Routes to scheduling editor with currently-active auction loaded | Routes to `/auction-scheduling` which **404s**; correct route is `/admin/auctions-data-center/schedule-auction` | **HIGH BUG** — sidebar link broken |
| Empty state | "Information — No active auction" modal forces user to Inventory | Empty list rendered (no forcing modal) | MEDIUM |
| List columns | (no list — single editor) | Auction ID, Auction Title, Auction Week, Round, Name, Start, End, Status, per-row actions | (local additive) |
| Per-row actions | n/a | "View" + "Start" (Scheduled rows); "View" + "Re-rank" + "Recalc TP" (Closed rows R1/R2) | (local additive — gap-analysis #7 shipped) |

Screenshots: `03b-local-schedule-auction-list.png`, `03c-qa-auction-scheduling-page.png`.

### 3.2 Selection Rules
| Aspect | QA | Local | Severity |
|---|---|---|---|
| Surface | Tooltip popover on the schedule editor — read-only summary of rules | Per-round link → opens dedicated editor page in a new tab | LOW (different UX, both functional) |
| Editor | (separate Mendix page not visible from main flow — possibly under Settings → Round Filters) | `/admin/auctions-data-center/auctions/round-filters/{round}` (round = 2 or 3) | LOW |
| **Page works on fresh local DB?** | n/a | **NO — errors out**: "BidRoundSelectionFilter not found: id=2" because there are zero `bid_round_selection_filters` rows in the seed | **CRITICAL BUG** |
| Error message quality | n/a | "id=2" is misleading — the parameter is the round number, not an entity id | MEDIUM |

Screenshots: `03d-qa-selection-rules.png`, `03e-local-round-filters-r2.png`.

### 3.3 Critical state observations (blockers for live walkthrough phases 4–10)

A walkthrough of phases 4–10 (R1 open → R3 close + reports) on a freshly-seeded
local DB is currently **blocked by three independent issues**, surfaced during
phase 1–3 setup:

1. **CRITICAL — Round-filter rows missing.** `auctions.bid_round_selection_filters`
   has zero rows (`SELECT count(*)` returned 0). No seed migration creates them
   (V59 only creates the table; V83/V84 only ALTER). Consequences:
   - Local Selection Rules page errors out for both R2 and R3.
   - R2 buyer assignment (`R2BuyerAssignmentService`) reads these rows; would
     fail at R1 close.
   - R3 pre-process (`R3PreProcessService`) reads them; would fail at R2 close.

2. **HIGH BUG — Default round windows start in the past.** When the user clicks
   "Create Auction" then "Confirm", the schedule defaults to dates derived from
   the selected `Week.startDate`. For Wk19 (a past week relative to today), R1
   opens immediately and ends a few hours later, R2/R3 open immediately and
   close immediately. Cron will speedrun the auction in the next 1–3 ticks
   instead of giving the admin time to verify. (QA defaults to "today + N days"
   which keeps R1 open for ~4 days — see screenshot `02a` vs `02b`.)

3. **HIGH BUG — Sidebar nav link "Auction Scheduling" 404s** (`/auction-scheduling`
   is the link target; correct route is
   `/admin/auctions-data-center/schedule-auction`). Affects every navigation
   from the admin shell.

These three together mean that, today, an admin who logs in to local and tries
to reproduce the QA happy path will hit a 404 (issue 3), then create an
auction whose default schedule is broken (issue 2), then try to view selection
rules and see a 500 (issue 1). The auction would technically progress through
the lifecycle but the cron transitions would error during R2 init.

### 3.4 Phases 4–10 — deferred from live walkthrough; cross-referenced to existing gap analysis

Because phases 4–10 require a clean lifecycle execution (which is blocked per
§3.3), the live walkthrough was capped at phase 3. The following table
cross-references each remaining phase to the existing
[`auction-flow-gap-analysis-2026-05-06.md`](auction-flow-gap-analysis-2026-05-06.md)
which already enumerates feature-level gaps with file/line evidence.

| Phase | Local feature state (per existing gap analysis) | Notable visual gap likely vs QA |
|---|---|---|
| 4 — R1 open + bidder R1 bid | ✅ Built — `BidderDashboardService`, `bidder/dashboard/**`, `BidImportService` | UNVERIFIED — bidder dashboard layout vs QA |
| 5 — R1 close → ranking + target-price | ✅ Built (4C) — listener wired; admin re-rank + recalc TP buttons present | Status indicators on schedule-auction list (per gap-analysis §4 — buttons added) |
| 6 — R2 init + R2 bid | ✅ Built (5) — `R2BuyerAssignmentListener`; admin recovery endpoint `POST /reassign-r2-buyers` | UNVERIFIED — no R2 qualified-buyer-code result view (gap-analysis §4: "No R2 qualified-buyer-code result view") |
| 7 — R2 close → ranking + target-price | ✅ Built (4C) — same listener handles round 2 | Same admin buttons as phase 5 |
| 8 — R3 pre-process | ✅ Built (6) — `R3PreProcessService`, V84/V85; admin recovery `POST /preprocess-r3`; reports populated | Round 3 Bid Report page exists at `/admin/auctions-data-center/round3-bid-report` |
| 9 — R3 open + bidder R3 bid | ✅ Built (5b/6) — R3 cascade in `BidDataCreationRepository` | UNVERIFIED — bidder R3/upsell view layout vs QA |
| 10 — R3 close + reports + post-auction | 🔴 Partial — Buyer Award Summary Report **not implemented** (gap-analysis §2 row 14); buyer notification emails (`is_start_notification_sent` etc.) **not wired** (§2 row 13); Snowflake re-sync ✅ shipped (§5 / gap-analysis #9) | n/a — the missing pages cannot be visually compared |

---

## Summary — what's missing on local vs QA

### Critical (blocks lifecycle exercise)
1. **Seed migration for `auctions.bid_round_selection_filters`** — needs at minimum a row for round=2 and a row for round=3 with sensible defaults (target_percent, target_value). Currently no seed exists; the page 500s on click.
2. **Round-window defaulting bug** — schedule defaults derive from `Week.startDate`; should derive from `now()` for any week whose start date is in the past, OR snap forward. Otherwise auctions speedrun on first cron tick.
3. **Sidebar nav 404** — `Auction Scheduling` link points to `/auction-scheduling`; should point to `/admin/auctions-data-center/schedule-auction`.

### High (visible workflow gaps)
4. **Inventory filter comparator missing** — QA offers Equal/Contains comparator dropdown per column; local has only freeform textbox.
5. **Per-round inventory stats missing on schedule page** — QA shows "Buyers / Total / DW Only" inline next to round header; local does not.
6. **Timezone label missing on schedule page** — QA shows "EST"; local has no indicator anywhere.
7. **Inventory preview table missing on schedule page** — QA shows what will be auctioned below the round inputs; local does not.
8. **Buyer Award Summary Report missing entirely** (gap-analysis §2 row 14).
9. **Buyer notification emails (R3 start, end, reminder) not wired** (gap-analysis §2 row 13). Schema fields exist; no writes.
10. **R2 qualified-buyer-code result view missing** (gap-analysis §4) — admin can configure R2 criteria but cannot inspect resulting QBC list per auction.

### Medium (UX divergence)
11. **Auction-switcher dropdown** missing on schedule page (QA has it; local routes back to list).
12. **"No active auction" empty-state guard modal** missing — QA forces user back to Inventory; local shows empty list.
13. **Create Auction modal missing inflated-quantity warning copy.**
14. **R3/Upsell terminology divergence** — QA uses "Round 3"; local uses "Upsell Round". Pick one and standardize.
15. **Selection Rules error message quality** — "id=2" misleading; should be "round=2".
16. **Buyer User Guide sidebar link missing.**
17. **Currency formatting inconsistent** — QA: integer dollars; local: 2-decimal cents. Pick one.
18. **Stats row layout** on Inventory page diverges (QA: inline labels; local: bordered cards).

### Low (cosmetic)
19. Hammer icon missing on Create Auction / Schedule Auction buttons.
20. Button label inconsistencies ("Schedule Auction" vs "Confirm", "Create Auction" vs "Create").
21. Sidebar gradient color (local: teal `#407874`; QA: solid green) — local matches the documented spec, so this is QA being stale, not a local gap.
22. Filter row styling differences.
23. Pagination wording / font differences.

---

## Implementation plan

See [`qa-vs-local-implementation-plan-2026-05-07.md`](qa-vs-local-implementation-plan-2026-05-07.md) for the prioritized fix plan.

---

## Phase 4–10 — Live lifecycle walk (post Tier-1 fixes)

After C1+C2+C3 shipped, a fresh auction (`625 — Live-Walk`) was created with
3-min rounds and 1-min inter-round gaps (config singleton temporarily set to
1 min for the walk, restored to 10 min after). Lifecycle progressed
end-to-end: R1 → recalc → R2 init (548 QBCs) → R2 → recalc → R3 pre-process
(548 QBCs + 17 reports) → R3 → R3 close. **All Tier-1 blockers cleared.**

### 4.1 R1 bidder dashboard (works)
- Login at `/login` as `bidder@buyerco.com` → routes to `/buyer-select`
- Pick auction-side buyer code (AADWC, id=73) → routes to `/bidder/dashboard`
- Page loaded once R1 transitioned to Started (~30s after scheduled start)
- Shows: round title, "Ends in" countdown, Submit Bids + Carryover buttons,
  Export/Import, "Minimum starting bid - $2.50" warning, paginated grid
  with Product Id / Brand / Model / Model Name / Grade / Carrier / Added /
  Avail. Qty / Target Price / Price (input) / Qty. Cap (input)
- Submitted bid $5.00 / qty 1 on first row → "Your Bids have been Submitted!" ✅

Screenshots: `04a-local-bidder-buyer-select.png`, `04b-local-bidder-dashboard-pre-r1.png`,
`04d-local-bidder-r1-open-loaded.png`, `04f-local-bidder-r1-submit2.png`.

### 4.2 NEW HIGH BUG — Auth session shared across tabs
The `auth_token` cookie is `HttpOnly; SameSite=Strict` and shared across
tabs in the same origin. Logging in as bidder in tab 2 invalidates the
admin session in tab 1 (admin pages immediately return 403). Reverse is
also true. Workaround: re-login on tab switch.

**Impact:** Admin + bidder cannot run side-by-side in the same browser. SalesOps doing
a live verification of a bidder-facing feature has to log out and back in
constantly. QA's Mendix shell uses session-id cookies the same way, so this
isn't a regression — but for the new admin-can-impersonate-bidder workflow
("Bid as Bidder" sidebar entry) it's worth providing a true impersonation
that doesn't trample the admin session.

### 5.1 R1 close → recalc — works perfectly
- Cron caught R1 end at 23:03:32 (32s lag past 23:03:00 — within tolerance)
- `ranking_status` flipped Closed → RUNNING → SUCCESS within ~3 seconds
- `target_price_status` SUCCESS shortly after
- Admin Schedule Auctions list correctly shows R1 row Closed with "View
  | Re-rank | Recalc TP" actions (gap-analysis #7 confirmed working)

Screenshot: `05b-local-admin-schedule-list-r1-closed.png`.

### 5.2 R2 init — 548 QBCs written, no errors
- `R2BuyerAssignmentListener` fired on R2's `RoundStartedEvent` (~30s after
  23:04:00 cron tick)
- 548 `qualified_buyer_codes` rows inserted for the R2 SA
- AADWC (the bidder's buyer code) confirmed `qualification_type =
  'Qualified'`, `included = true`, `is_special_treatment = false`

### 6.1 R2 bidder dashboard — NEW HIGH BUG (lazy bid_data not seeded)
Despite AADWC being qualified for R2, the bidder dashboard rendered the
empty-state "Bidding has ended. No scheduled auction is available."
**Root cause:** Zero `bid_rounds` and zero `bid_data` rows existed for AADWC
in R2. R2 init writes QBC rows but does not seed `bid_data` for regular
qualified buyers (only for special-treatment buyers via
`SUB_CreateBidDataForAllAE`). The bidder dashboard does not lazy-create
`bid_data` rows on first visit either, so the round looks empty.

Screenshot: `06a-local-bidder-r2-open.png`.

**Impact:** R2 (and R3 — same pattern, see 9.1) is functionally unusable
for regular Qualified buyers. Mendix presumably lazy-seeds bid_data when
the bidder lands on the round-2/round-3 page, or the R2-init service
seeds rows for all `included = true` QBCs regardless of special status.
Either approach would fix this. **Tier-1.5 fix.**

### 7.1 R2 close → recalc + R3 pre-process — works perfectly
- R2 closed at 23:07:30 (30s lag past 23:07:00)
- R2 ranking + target-price both SUCCESS
- R3 pre-process fired automatically on R2 close: 548 QBCs + 17
  `round3_buyer_data_reports` rows written

### 8.1 Round 3 Bid Report by Buyer — NEW HIGH BUG (filter mismatch)
- Page at `/admin/auctions-data-center/round3-bid-report` loads with
  "Choose a week" dropdown
- Selecting "2026 / Wk19" returns "**No data for the selected week.**"
- DB confirms 17 `round3_buyer_data_reports` rows exist for Wk19's R3 SA
- The query filters by something other than (or in addition to) week,
  but the UI presents week-only as the filter — contract mismatch

Screenshots: `08b-local-admin-round3-bid-report-loaded.png`,
`08c-local-admin-round3-bid-report-wk19.png`.

### 9.1 R3 bidder dashboard — same bug as R2
Same empty-state. `qualified_buyer_codes` row exists for AADWC R3
(`Qualified` / `included=true`); zero `bid_rounds` / `bid_data`.

Screenshot: `09a-local-bidder-r3-open.png`.

### 10.1 R3 close — by-design no recalc
- R3 closed at 23:11:25 (25s lag)
- `ranking_status` and `target_price_status` remained PENDING
- Confirmed by gap-analysis §2 row 11: `RecalcRoundClosedListener` gates
  on `round ∈ {1, 2}` — terminal-round design, not a gap.

### 10.2 Auctions list (Auctions Data Center) — works, with deltas
- Auction 625 shown as "Closed", Rounds = 3, Re-sync button present
- Per-row buttons: View + Re-sync (Re-sync = gap-analysis #9 shipped)
- "Created By" column shows raw user ID `9001` (numeric) instead of
  username — **MEDIUM gap**

Screenshot: `10a-local-admin-auctions-list-post-r3.png`.

### 10.3 NEW HIGH BUG — Auction detail page 404
- URL `/admin/auctions-data-center/auctions/{id}` returns 404
- The "View" button on the Auctions list opens `/auctions/{id}/schedule`
  instead — i.e., there is no dedicated auction detail/summary page
- Closed auctions land on the schedule editor with a "Closed" badge but
  with **Delete + Confirm + Cancel buttons still active** (potentially
  destructive UX on a closed auction)

Screenshots: `10b-local-admin-auction-625-detail.png`,
`10d-local-admin-view-625-after-close.png`.

### 10.4 Buyer Award Summary — confirmed missing (gap-analysis §2 row 14)
No link or page exists. Walkthrough cannot exercise this feature.

### 10.5 Snowflake Re-sync — present but not exercised live
"Re-sync" button on auctions list is the gap-analysis #9 shipped feature.
Did not click during this walk (would push to a real Snowflake endpoint).

---

## Phase 4-10 — New gap inventory (additive to phase 1-3 list)

### Tier 1.5 — Critical for live lifecycle UX (block multi-buyer R2/R3 demos)
24. **R2/R3 lazy bid_data not seeded for regular Qualified buyers.** Bidder
    dashboard returns empty-state for any non-special-treatment buyer in R2 or R3.
    Either (a) seed `bid_data` for all `included=true` QBCs at R2 init / R3
    init time, or (b) lazy-create on first dashboard load post-init.
25. **Round 3 Bid Report "No data" filter bug.** Page returns empty even when
    rows exist for the selected week. Service query filter mismatches the UI's
    filter contract.
26. **Auction detail page 404** at `/admin/auctions-data-center/auctions/{id}`.
    Either implement a real detail/summary page or redirect to `/schedule`.

### Tier 2 — High additions
27. **Closed auction shows active edit controls.** Delete Auction + Confirm
    buttons should be hidden or disabled when `auction_status = Closed`.
28. **Created By column shows raw numeric user ID** instead of email/name.

### Tier 3 — Medium additions
29. **Auth session shared across tabs.** Working as designed (HttpOnly cookie)
    but blocks the admin+bidder side-by-side workflow used during QA. Consider
    a true impersonation flow for "Bid as Bidder" instead of cookie-overwrite.
30. **Cron lag transparency.** Round transitions take up to ~30s past the
    scheduled time (one cron tick). Acceptable in production but a "next
    transition in X seconds" indicator on the admin schedule list would help
    SalesOps avoid clicking refresh.

---

## Final summary across all phases

- **3 CRITICAL fixes shipped** (C1+C2+C3, commit 58f4ef7) — Tier-1 blockers
- **3 NEW critical/high bugs surfaced in live walk** (#24 R2/R3 lazy bid_data,
  #25 R3 report filter, #26 auction detail 404) — these block real bidder
  usage in R2/R3 and auction-detail browsing
- **Total gap count: 30** (was 23 after phase 1-3; added 7 from phase 4-10)
- **Lifecycle confirmed working end-to-end** for cron + recalc + QBC writes;
  bidder UX past R1 is the next major work area

