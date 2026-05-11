# Auction Scheduling left-menu redirect (QA-parity entry point)

**Date:** 2026-05-11
**Owner:** Ashirwad
**Status:** Planned

## Problem

Local left-nav item "Auction Scheduling" routes to
`/admin/auctions-data-center/schedule-auction`, which renders a bespoke
`<table>` listing every `SchedulingAuction` round (per-round transitions
with Start / Close / Re-rank / Recalc TP buttons).

QA's "Auction Scheduling" menu item does **not** show a list. It opens
the most recently created auction's **schedule editor** directly: title
"Auction 2026 / Wk19", an auction-switcher dropdown in the header, three
round fieldsets (R1/R2/R3 with date/time pickers, Selection Rules link,
round stats), and a paginated inventory preview underneath.

The editor surface already exists locally at
`/admin/auctions-data-center/auctions/{auctionId}/schedule` and is
near-pixel-parity with QA (auction switcher, round fieldsets, inventory
preview, Selection Rules links). The only gap is the **entry point**.

## Decision

Match QA: the left-nav menu item lands on the editor for the most
recently created auction. The bespoke scheduling-rows list is dropped as
the menu's landing surface but preserved at a non-menu route so the
operational controls (per-round Start/Close, Re-rank, Recalc TP, the
live "Next transition in Xm Ys" countdown) survive for SalesOps admin
recovery.

## Implementation

1. **`frontend/src/app/(dashboard)/admin/auctions-data-center/schedule-auction/page.tsx`**
   becomes a redirector:
   - On mount, call `listAuctions({ page: 0, pageSize: 1 })`. The
     backend sorts `ORDER BY a.created_date DESC, a.id DESC`, so the
     first row is the newest auction — same as QA.
   - If a row exists → `router.replace('/admin/auctions-data-center/auctions/{id}/schedule')`.
   - If the result is empty → render the existing `NoActiveAuctionModal`
     (informational dialog; OK button routes to `/inventory` so the admin
     can use the Create Auction button there).
   - Render a `Loading…` placeholder while the lookup is in flight.

2. **`NoActiveAuctionModal.tsx`** and **`list.module.css`** stay as-is.
   The modal still imports its styles from `list.module.css`; only the
   list-table classes inside that file become unused. We leave them
   in-place — the file is small and tree-shaking handles the unused CSS
   when nothing references it.

3. **Preserved operational surface:** the current list-table code moves
   to `frontend/src/app/(dashboard)/admin/auctions-data-center/scheduling-auctions/page.tsx`
   (plural, distinct from the singular `schedule-auction` redirector).
   This route stays **off the left nav** but is reachable for SalesOps
   when they need the Start/Close/Re-rank/Recalc TP buttons. A small
   link can be added to the editor header in a follow-up if SalesOps
   asks for it.

4. **No backend changes.** All needed endpoints already exist
   (`GET /api/v1/admin/auctions`, sorted newest-first).

5. **Left-nav config:** the `Auction Scheduling` href in `layout.tsx`
   continues to point at `/admin/auctions-data-center/schedule-auction`
   — the redirector handles the bounce internally so the URL the user
   types/bookmarks doesn't break.

## Out of scope

- Moving any of the recovery controls (Start / Close / Re-rank /
  Recalc TP) onto the editor itself. The /scheduling-auctions fallback
  covers it for now.
- Backend pagination/sort changes — the existing `created_date DESC, id
  DESC` order is what QA uses.
- Switching the editor's auction-switcher dropdown to a server-side
  search (it currently pulls a 200-row page).

## Verification

1. `npm run dev` in `frontend/`, log in as admin.
2. Click **Auction Scheduling** in the sidebar.
3. Confirm the URL bounces from `/schedule-auction` to
   `/auctions/{newestId}/schedule` with the editor rendered.
4. Confirm the auction-switcher dropdown lists recent auctions and
   pivoting changes the editor.
5. With an empty `auctions.auctions` table (or a filter that returns
   zero rows), confirm `NoActiveAuctionModal` shows and the OK button
   routes to `/inventory`.
6. Direct-navigate to `/admin/auctions-data-center/scheduling-auctions`
   (plural) and confirm the operational list still renders with all
   row actions working.
