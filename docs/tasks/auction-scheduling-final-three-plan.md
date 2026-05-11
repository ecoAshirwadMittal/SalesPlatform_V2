# Auction Scheduling editor — finish the last three QA-parity items

**Date:** 2026-05-11
**Owner:** Ashirwad
**Status:** Planned

## Context

After the pixel-clone pass (commits `a444f14` / `7a96ca1` / `46b14dd` /
`a0b7bbf` / `f1a949f`), the editor surface visually matches QA except
for three intentional differences flagged as deferrable. The user has
asked to close them out.

## Item 1 — TZ label: drop browser detection, render `EST`

**File:** `frontend/src/.../schedule/RoundFieldset.tsx`

QA's Mendix page hard-codes "EST" because the eco team operates in
Eastern. Locally we currently detect the browser's TZ via `Intl` and
render whatever the user's machine is (`CDT` for Ashirwad). For QA
parity we revert to a static `EST` literal.

**Caveat the comment must call out:** the underlying `<input type="time">`
still records browser-local wall-clock time. We are NOT converting to
Eastern Time on submit — the `isoFromFields` helper takes the typed
date+time, builds a `Date`, and uses `toISOString()` which interprets
the inputs in browser-local. Changing the on-screen TZ chip from CDT to
EST therefore creates a small lie when the admin is outside Eastern;
QA had the same lie. If we ever want true Eastern handling, that's a
separate project (would need to interpret + serialize all dates in the
`America/New_York` zone).

Implementation: replace the `useTimezoneAbbrev` hook entirely with a
constant `EST` string. Drop the hook + the `useState`/`useEffect`
machinery and the comment block since the behavior no longer needs
explanation (just a one-liner saying "QA parity, see plan").

## Item 2 — Per-round stats on Unscheduled auctions

**File:** `backend/.../AuctionScheduleService.java`
**Test file:** `backend/.../AuctionScheduleServiceTest.java` or
`AuctionScheduleServiceIT.java` (whichever covers `getAuctionDetail`)

QA shows `Buyers All / Total 638 / DW Only 116` next to Round 1 on
Unscheduled auctions. Local shows nothing because
`computeRoundStats(weekId, rounds)` returns `List.of()` early when
`rounds.isEmpty()` (an Unscheduled auction has no `scheduling_auctions`
rows yet — those are created at `POST /api/v1/admin/auctions/{id}/schedule`).

**Fix:** keep the weekly inventory totals query running always, and
synthesize three placeholder `RoundStatsView` entries (rounds 1, 2, 3)
with `buyerCount=null` (renders as "All") and the computed
totalQuantity / dwTotalQuantity. When the auction is already scheduled
the existing per-SA buyer-count query continues to drive the values.

Sketch:

```java
private List<RoundStatsView> computeRoundStats(Long weekId, List<SchedulingAuction> rounds) {
    // weekly inventory totals — always compute (cheap single query, same
    // shape regardless of scheduling state)
    Object[] totals = ...;
    long totalQuantity = ...;
    long dwTotalQuantity = ...;

    if (rounds.isEmpty()) {
        // Unscheduled auction: synthesize R1/R2/R3 placeholders so the
        // schedule editor renders the same Buyers/Total/DW-Only line QA
        // shows pre-scheduling. buyerCount=null -> frontend renders "All".
        return List.of(
            new RoundStatsView(1, null, totalQuantity, dwTotalQuantity),
            new RoundStatsView(2, null, totalQuantity, dwTotalQuantity),
            new RoundStatsView(3, null, totalQuantity, dwTotalQuantity)
        );
    }

    // existing per-SA buyer-count path
    ...
}
```

Doc update: `RoundStatsView.java` javadoc currently says "Returns an
empty list when `rounds` is empty (Unscheduled auction)" — needs to
flip to the new behavior.

Test: add a unit/integration case `getAuctionDetail_returnsR1R2R3StatsForUnscheduledAuction`
asserting `roundStats.size() == 3` and each entry has
`buyerCount=null` and matches the weekly aggregated_inventory totals.

## Item 3 — Soften time-input picker indicator

**File:** `frontend/src/.../schedule/schedule.module.css`

The Chrome `<input type="time">` already renders 12-hour AM/PM in the
admin's locale, so the value display already matches QA. The remaining
gap is the prominent picker indicator glyph (clock icon at the right
edge of every time input, calendar icon on date inputs). QA's older
Mendix datepicker uses a softer styling.

We do **not** roll a custom widget. Instead we apply two CSS rules to
the `.timeRow` time/date inputs:

```css
.timeRow input[type="time"]::-webkit-calendar-picker-indicator,
.timeRow input[type="date"]::-webkit-calendar-picker-indicator {
  opacity: 0.45;
  cursor: pointer;
}
```

That keeps the click-target (admin can still click to open the picker)
but de-emphasizes the glyph so the field reads more like text. If pixel
parity ever requires removing the glyph entirely, switch
`opacity: 0` and the field becomes click-anywhere-to-open.

The "custom picker" path (3-segment HH/MM/AM-PM dropdowns or a JS
library) is explicitly **out of scope** — it would balloon the editor
LOC and add a dependency for a marginal visual gain.

## Commit plan

Three commits, smallest blast radius first:

1. `style(scheduling): soften time/date picker indicators`
2. `style(scheduling): match QA's static EST timezone label`
3. `feat(scheduling): show R1/R2/R3 stats on unscheduled auctions`

## Verification

1. Type-check: `npx tsc --noEmit --pretty false` clean (modulo
   pre-existing baseline errors).
2. Backend test: `mvn test -Dtest=AuctionScheduleServiceTest` (or the
   IT class — whichever owns the new assertion) passes.
3. Visual: re-screenshot `/admin/auctions-data-center/auctions/629/schedule`
   on an Unscheduled auction and confirm:
   - TZ chip on each row reads `EST`
   - R1/R2/R3 cards each show a stats line (`Buyers All · Total N · DW Only N`) next to the title
   - Time/date inputs are not visually dominated by the picker glyph
