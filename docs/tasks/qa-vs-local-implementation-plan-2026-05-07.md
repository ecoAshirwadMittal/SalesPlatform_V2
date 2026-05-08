# QA vs Local Pixel/Functional Gaps — Implementation Plan (2026-05-07)

**Source findings:** [`qa-vs-local-pixel-walkthrough-2026-05-07.md`](qa-vs-local-pixel-walkthrough-2026-05-07.md)
**Cross-references:** [`auction-flow-gap-analysis-2026-05-06.md`](auction-flow-gap-analysis-2026-05-06.md)

This plan turns the 23 gaps from the walkthrough into a prioritized, single-PR-per-item fix list. Items are
ordered by criticality first (CRITICAL fixes unblock the live lifecycle), then by effort within each tier.

---

## Tier 1 — CRITICAL (must ship before anyone can exercise a local lifecycle)

These are show-stoppers. Any fresh `salesplatform_dev` checkout cannot run an end-to-end auction today.

### Item C1 — Seed `auctions.bid_round_selection_filters` rows
**Severity:** CRITICAL
**Evidence:** `SELECT count(*) FROM auctions.bid_round_selection_filters;` returns 0; `BidRoundSelectionFilterService.get(int round)` (`backend/.../service/auctions/BidRoundSelectionFilterService.java:34-38`) throws `EntityNotFoundException` immediately.
**Affected flows:**
- Selection Rules page (R2 + R3) returns 500
- `R2BuyerAssignmentService` reads `findByRound(2)`; would fail at first R2 init
- `R3PreProcessService` reads `findByRound(3)`; would fail at first R3 pre-process

**Fix:** Add a Flyway migration `V86__seed_bid_round_selection_filters.sql` that inserts one row per round with Mendix-parity defaults:
- Round 2: `target_percent` and `target_value` matching Mendix `BidRoundSelectionFilter_Round2` defaults; R3-only columns (`bid_percentage_variation`, `bid_amount_variation`, `rank_qualification_limit`) NULL.
- Round 3: `target_percent` and `target_value` matching Mendix `_Round3` defaults; whole-percent convention per V84 comment.

**Verification:**
- Migration is idempotent (`ON CONFLICT (round) DO NOTHING` or guard with `WHERE NOT EXISTS`).
- Selection Rules page loads for both rounds without error.
- Add an integration test: `BidRoundSelectionFilterRepositoryIT#findByRound_returnsSeededRowOnFreshDb`.

**Effort:** 1–2 hours (lookup Mendix defaults from `migration_context/`, write migration, write test).

---

### Item C2 — Round-window default bug (schedule starts in past)
**Severity:** CRITICAL
**Evidence:** Screenshot `02b-local-after-create-schedule.png` shows R1 From = 05/04/2026 (3 days ago) when today is 05/07/2026. Compare QA `02a-qa-after-create-auction-scheduling.png`: R1 From = 05/07 7PM (today, future).
**Root cause:** `ScheduleAuctionService` (or wherever the default round windows are computed) anchors on `Week.startDate`, which is fixed at week-start regardless of when the user is creating the auction.

**Fix:** Default each round's `From` to `max(Week.startDate, now() + 10 minutes)`. Cascade `R2.From = R1.To + 10min`, `R3.From = R2.To + 10min` (matches QA's 10-minute inter-round gap). Round windows themselves should respect Mendix defaults: R1 = ~4 days, R2 = ~24h, R3 = ~24h.

**Verification:**
- Unit test: creating an auction for a past week defaults R1.From to `now() + 10 min`, not `Week.startDate`.
- Unit test: creating an auction for a future week defaults R1.From to `Week.startDate` (no clamp needed).
- Manual: create on local Wk19, observe R1 From ≥ now.

**Effort:** 2–3 hours.

---

### Item C3 — Sidebar nav 404 on `Auction Scheduling`
**Severity:** CRITICAL (cosmetic but visible — every admin hits it)
**Evidence:** Sidebar link href = `/auction-scheduling`; no page at that route. Correct route is `/admin/auctions-data-center/schedule-auction`.

**Fix:** Either (a) update sidebar nav config to point to the correct route, or (b) add a redirect from `/auction-scheduling` → `/admin/auctions-data-center/schedule-auction`. Prefer (a) for speed.

**Verification:** Click sidebar link from any page, lands on schedule-auction list (not 404).

**Effort:** 15 minutes.

---

## Tier 2 — HIGH (visible workflow gaps; visible to a SalesOps user trained on QA)

### Item H4 — Inventory filter comparator (Equal/Contains)
**Severity:** HIGH
**Evidence:** Screenshots `01a-qa-admin-landing.png` vs `01c-local-inventory.png` — QA filter row has comparator combobox + textbox; local has textbox only.
**Fix:** Extend the inventory filter component to render a comparator combobox per column (Equal / Contains / Greater / Less), wire to the existing query API. Mendix DataGrid 2 default behavior — easy to mimic.
**Effort:** 4–6 hours (component refactor + API filter param plumbing if not already there).

### Item H5 — Per-round inventory stats on schedule page
**Severity:** HIGH
**Evidence:** Screenshot `02a` shows "Buyers / Total / DW Only" inline next to each round header; `02b` does not.
**Fix:** Compute and render per-round buyer count, total inventory, DW-only inventory next to each round's date inputs on `/admin/auctions-data-center/auctions/[auctionId]/schedule`. Backend likely already has this data via `BidDataCreationService` queries; expose via a new GET endpoint returning summary per round.
**Effort:** 4–6 hours.

### Item H6 — Timezone label on schedule page (and across auction admin)
**Severity:** HIGH
**Evidence:** Screenshot `02a` shows "EST" next to every date/time row; `02b` has no timezone indicator.
**Fix:** Render the user's timezone (or system default) next to every datetime input on the schedule page. Reuse the user's timezone from `identity.user_timezones` (which V17 seeded). Default to "EST" if user has none.
**Effort:** 2–3 hours.

### Item H7 — Inventory preview table on schedule page
**Severity:** HIGH
**Evidence:** Screenshot `02a` has paginated inventory grid below rounds; `02b` does not.
**Fix:** Add a paginated inventory table below the round inputs on the schedule page, showing exactly the rows that will be auctioned (post-filter). Reuse the existing inventory grid component with a query parameter scoping to the auction.
**Effort:** 4–6 hours.

### Item H8 — Buyer Award Summary Report (entirely missing)
**Severity:** HIGH (gap-analysis §2 row 14)
**Evidence:** Mendix `SUB_LoadBuyerAwardsSummaryReport` + `EM_BuyerAwardsSummaryReport` not ported. No service, controller, or frontend page exists.
**Fix:** Port the Mendix Excel-export logic for buyer award summary. Includes:
- Service: `BuyerAwardSummaryService` reading from `auctions.bid_data` for the closed auction
- Controller: `GET /api/v1/auctions/{id}/buyer-award-summary` returning Excel
- Frontend: `/admin/auctions-data-center/buyer-award-summary/[auctionId]` page with download button

**Effort:** 1–2 days (full slice — service, controller, frontend, tests).

### Item H9 — Buyer notification emails (R3 start, end, reminder)
**Severity:** HIGH (gap-analysis §2 row 13)
**Evidence:** Mendix `ACT_Round3_StartNotification` not ported. Schema fields `is_start_notification_sent`, `is_end_notification_sent`, `is_reminder_notification_sent` exist on `SchedulingAuction.java:51-57` but no writes.
**Fix:** Add `BidderNotificationService` triggered by `RoundStartedEvent(round=3)`, `RoundClosedEvent(round=3)`, and a scheduled job for the reminder. Send via existing email infrastructure. Set the corresponding `is_*_sent` flags after successful send.
**Effort:** 1 day.

### Item H10 — R2 qualified-buyer-code result view
**Severity:** HIGH (gap-analysis §4)
**Evidence:** Admin can configure R2 criteria but cannot inspect which buyer codes were qualified for a given auction post R2-init.
**Fix:** Add `/admin/auctions-data-center/auctions/[auctionId]/r2-qualified-buyers` page rendering `qualified_buyer_codes` rows for the auction (qualification_type, included flag, special_treatment flag).
**Effort:** 4–6 hours (read-only page + new GET endpoint).

---

## Tier 3 — MEDIUM (UX divergence; not blocking but confusing)

### Bundle M11 — Schedule page polish
- M11a: Auction-switcher dropdown on schedule page (QA has it inline).
- M11b: "No active auction" empty-state modal on Auction Scheduling shortcut.
- M11c: Inflated-quantity warning copy in Create Auction modal.
- M11d: Standardize R3 vs Upsell terminology — pick one.
- M11e: Improve "BidRoundSelectionFilter not found: id=2" → "round=2" in `EntityNotFoundException` formatter.

**Combined effort:** ~1 day.

### Bundle M12 — Sidebar polish
- M12a: Add "Buyer User Guide" sidebar link (point to docs or external KB).
- M12b: Resolve currency formatting (integer dollars vs 2-decimal cents) — pick one and standardize across Inventory + reports.

**Combined effort:** ~3 hours.

### Bundle M13 — Inventory page styling
- M13a: Stats row layout — decide between QA inline labels vs local bordered cards. Spec the chosen one in `docs/architecture/decisions.md`.

**Effort:** ~3 hours.

---

## Tier 4 — LOW (cosmetic polish)

Single bundle: M14 — visual polish pass
- L19: Hammer icon on Create Auction / Schedule Auction buttons.
- L20: Button labels — "Schedule Auction" vs "Confirm", "Create Auction" vs "Create" — pick one across the app.
- L21: Sidebar color — accept divergence (local matches CLAUDE.md spec; QA is the legacy stale).
- L22: Filter row styling alignment.
- L23: Pagination wording / font alignment.

**Combined effort:** ~4 hours.

---

## Suggested PR sequencing

1. **Sprint 1 (this week):** C1 → C2 → C3 (~1 day total). Unblocks live lifecycle.
2. **Sprint 1 (this week):** Live walkthrough of phases 4–10 to surface bidder-side UI gaps that we couldn't see in this audit.
3. **Sprint 2:** H8 (Buyer Award Summary) + H10 (R2 QBC result view) — biggest functional gaps left.
4. **Sprint 2:** H9 (notifications) — important for production fidelity but not blocking happy path.
5. **Sprint 3:** Schedule page polish (H5, H6, H7) bundled into one PR — touches one page, easier to review together.
6. **Sprint 3:** H4 (filter comparator) — touches reusable component.
7. **Sprint 4:** Tier 3 + Tier 4 bundles in two cleanup PRs.

## Out of scope for this plan
- Bidder-side UI comparison (phases 4 + 9 of the walkthrough were deferred — needs a follow-up after C1+C2+C3 land).
- Round 1 init / inventory recompute admin views (gap-analysis §2 covers; not surfaced in this walkthrough).
- Snowflake sync depths beyond what gap-analysis §5 already enumerates.
- Mobile / responsive layout audit.

## Risks
- **C1 default values:** If the Mendix defaults for `target_percent` / `target_value` are wrong, the whole R2/R3 lifecycle ranks/qualifies on bad numbers. Cross-check the seed against `migration_context/database/queries/schema-AuctionUI.md` before merging.
- **C2 datetime arithmetic:** Timezone handling around "now() + 10 min" must match the cron's clock (UTC vs local). Existing `AuctionLifecycleScheduler` runs on `@Scheduled(fixedDelayString = "${auctions.lifecycle.poll-ms:60000}")` against `Instant.now()`; the schedule defaults must produce comparable instants. Add a unit test pinning the clock.
