# Auction Scheduling editor — QA pixel-clone plan

**Date:** 2026-05-11
**Owner:** Ashirwad
**Status:** Planned — implementation delegated to subagent
**Surface:** `/admin/auctions-data-center/auctions/{auctionId}/schedule`
**Reference QA shot:** `qa-auction-scheduling.png` (captured 2026-05-09)
**Reference local-before shot:** `local-auction-scheduling-redirect.png` (captured 2026-05-11)

## Goal

Bring the Auction Scheduling editor visually in line with QA's Mendix
`acc_AuctionSchedulePage`. This is a styling + layout pass, not a
behavior change. Backend, data shape, error handling, and form
semantics stay as they are.

## Files in scope

- `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/[auctionId]/schedule/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/[auctionId]/schedule/schedule.module.css`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/[auctionId]/schedule/RoundFieldset.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/[auctionId]/schedule/schedule-form.ts` (only if R3 label needs flipping — see item 9)

## Color tokens (from `migration_context/styling/EcoAtm.css`)

| Token | Hex | Use |
|---|---|---|
| Teal primary | `#407874` | Buttons, links |
| Dark text | `#112d32` | Body text, title |
| Greige surface | `#F7F7F7` | Page background, card subsurfaces |
| Label | `#514F4E` | Form labels |
| Border | `#B7B5B5` / `#e5e5e5` | Input + card borders |
| **Round heading accent** | `#713800` | Round 1/2/3 section titles (matches `.usericon_edit`/`.usericon_dashboard`) |
| HR / separator | `#C5C1C1` | Title underline (matches `.HeaderBar`) |

## Items to implement

All audit items grouped by impact. Subagent should ship them in three
sequential commits so a regression in one item is easy to bisect.

### Commit 1 — Title + header layout (audit items 1, 2, 10, 11)

- Title: bump `.title` to `font-size: clamp(2.25rem, 1.5rem + 2vw, 3rem); font-weight: 600; line-height: 1.1;`. Keep color `#112d32`. Remove `margin-right: auto;` — see below.
- New header structure inside `<header className={styles.header}>`:
  ```
  <div className={styles.titleRow}>
    <h1 className={styles.title}>{detail.auctionTitle}</h1>
    <select className={styles.titleSwitcher}>…</select>
  </div>
  <hr className={styles.headerRule} />
  <div className={styles.metaRow}>
    <Link className={styles.backLink}>← Back to Inventory</Link>
    <span className={styles.metaSeparator}>·</span>
    <span>{detail.weekDisplay}</span>
    <span className={styles.metaSeparator}>·</span>
    <StatusPill />
    {/* "View R2 qualified buyers" stays here too */}
  </div>
  ```
- Inline auction-switcher: drop the `SWITCH AUCTION` label, remove the
  `auctionSwitcherLabel` block. Inline the `<select>` next to the `<h1>`
  inside `.titleRow` (`display: flex; align-items: baseline; gap: 1rem`).
  Switcher styling: same border / radius as today but `min-width: 220px`,
  `max-width: 280px`, no upper label.
- `.headerRule`: `border: 0; border-top: 1px solid #C5C1C1; margin: .35rem 0 1rem;`
- `.metaRow`: small font (`0.85rem`), `color: #514F4E`, gap `0.5rem`,
  `flex-wrap: wrap`. Move the existing back link + status pill here.
  Status pill keeps its existing classes.

### Commit 2 — Two-column round grid + heading restyle (audit items 3, 4, 5)

- New wrapper `.roundsGrid` in `schedule.module.css`:
  ```
  .roundsGrid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    column-gap: 1rem;
    row-gap: 0.75rem;
    max-width: 1280px;
    margin-bottom: 1rem;
  }
  .roundsGrid > :nth-child(3) { grid-column: 1 / -1; max-width: calc(50% - 0.5rem); }
  @media (max-width: 960px) { .roundsGrid { grid-template-columns: 1fr; } .roundsGrid > :nth-child(3) { max-width: none; } }
  ```
- In `page.tsx` wrap the three `<RoundFieldset>` blocks in
  `<div className={styles.roundsGrid}>` and drop their individual
  `max-width: 820px` constraint (move that constraint up to the wrapper
  if needed).
- `.roundTitle`:
  ```
  font-size: 1.35rem;
  font-weight: 600;
  color: #713800;
  letter-spacing: 0.01em;
  ```
- `.roundCard`:
  ```
  max-width: none;        /* let the grid drive width */
  padding: 1.25rem 1.5rem;
  border: 1px solid #e5e5e5;
  background: #fff;
  border-radius: 6px;
  ```
- Round stats: confirm `detail.roundStats` is populated for R1 in the
  current data path. If the array is empty for R1, **do not invent
  stats** — log and ask. (Likely the `getAuctionDetail` response just
  isn't returning a R1 stats row pre-schedule; in that case the local
  parity gap is data, not UI.)

### Commit 3 — Round body row shape + Selection Rules glyph (audit items 6, 8, 12, 13)

- Replace the 4-column `.fieldGrid` with a row-based layout matching
  QA's `From: [date] [time]  EST` pill, then `To: [date] [time]  EST`.
  In `RoundFieldset.tsx`:
  - Render two `<div className={styles.timeRow}>` blocks, one for From
    and one for To. Each contains:
    - `<span className={styles.timeRowLabel}>From:</span>` (or `To:`)
    - the date `<input>`
    - the time `<input>`
    - the TZ tag at the row end
  - Remove the per-field `<label>` text (`From Date`, `To Date`, etc.) —
    the row label and field role replace it. Keep `aria-label` on each
    input for accessibility.
- New CSS:
  ```
  .timeRow {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.55rem 0.85rem;
    background: #F7F7F7;
    border: 1px solid #e5e5e5;
    border-radius: 6px;
    margin-bottom: 0.5rem;
  }
  .timeRowLabel {
    font-size: 0.95rem;
    font-weight: 600;
    color: #514F4E;
    min-width: 3.5rem;
  }
  .timeRow input[type="date"],
  .timeRow input[type="time"] { height: 32px; }
  .timeRow .tzTag { margin-left: auto; }
  ```
- Selection Rules glyph: prepend a chat-bubble SVG (inline, 14×14,
  `currentColor` stroke) before the link text. Tighten to
  `display: inline-flex; align-items: center; gap: 0.3rem; font-size: 0.85rem`.
- Bottom action row: gate `Delete Auction` behind a small kebab/overflow
  control (or leave it but soften the styling — defer if the agent
  judges the overflow control adds too much code; the prose audit
  flagged this as "consider", not required).

## Items deliberately skipped

- **Item 7** (custom AM/PM time picker) — would require pulling a
  third-party picker or rolling a custom one. Defer.
- **Item 9** (R3 label `Upsell Round` vs QA's `Round 3`) — Ashirwad to
  decide. Plan: leave `Upsell Round` for now; flip via single-line
  change to `schedule-form.ts` once decided.
- **Item 14** (truncated inventory headers) — local is better; skip.
- **Item 15** (`EST` hard-code vs browser TZ) — current behavior is
  correct; skip.

## Verification

1. `npm run dev` in `frontend/`. Log in as `admin@test.com / Admin123!`.
2. Click **Auction Scheduling** in the sidebar. Land on `/auctions/{id}/schedule`.
3. Take a full-page screenshot and side-by-side compare against
   `qa-auction-scheduling.png`. Items 1–8 should be visually matched.
4. Resize to 720px width — round grid should collapse to a single
   column, time rows should remain horizontal.
5. Re-run `npx tsc --noEmit` — must not introduce new errors. The
   pre-existing errors in `.next/types/validator.ts`,
   `PriceCell.test.tsx`, and `tests/e2e/*.spec.ts` are unrelated and
   stay as-is.
6. Sanity-check that the Confirm modal (`ConfirmModal.tsx`) and
   `ConfirmActionModal` still render — their styles live in the same
   CSS module.

## Non-goals

- No backend changes.
- No new dependencies.
- No changes to the redirector at `/schedule-auction` or the
  operational list at `/scheduling-auctions`.
- No changes to the inventory preview component, even if QA's
  rendering differs.
