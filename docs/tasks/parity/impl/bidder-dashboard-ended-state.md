# BDD-P1 — Bidder dashboard ended-state parity

**Finding:** `docs/tasks/parity/findings.md` § BDD-P1.
**Date:** 2026-07-12 · **Branch:** `worktree-agent-ab4d4c635561c431c` · **Layer:** function + pixel.

With identical auction data on both sides (V95–V98), buyer code **HN** on the
legacy bidder dashboard renders the most-recent **ended** auction — heading
"Auction 2026 / Wk13", a large bordered panel "Bidding has ended. / Your bids
from round 1 can be found below." and a **"Download your Round 1 Bids"** pill
button — while the new app renders two bare text lines "Bidding has ended. /
No scheduled auction is available."

Root cause: the new `BidderDashboardService.landingRoute` returned
`Error("AUCTION_NOT_FOUND")` whenever **no round is `Started`**, so HN
(rounds 1/2/3 all `Closed`) fell through to the truly-empty error branch
instead of the ended-download branch. The frontend `DOWNLOAD` mode existed but
was never reached for HN, and its panel lacked the auction heading + legacy
panel chrome.

---

## 1. Legacy selection logic (cited microflows)

Traced from `migration_context/` (orchestrator `backend/ACT_OpenBidderDashboard.md`).

| Legacy microflow (proof) | Rule |
|---|---|
| `backend/ACT_OpenBidderDashboard.md` L13-19: `DB Retrieve AuctionUI.SchedulingAuction`; `DECISION $SchedulingAuctionList != empty` → false → `Maps to Page AuctionUI.Error_Auction_Not_Found` | **Rule 0 — truly-empty:** no `SchedulingAuction` rows at all → the error page (new app's `ERROR_AUCTION_NOT_FOUND` / "No scheduled auction is available." — kept). |
| `backend/ACT_GetMostRecentAuction.md`: bare top-1 retrieve of `AuctionUI.Auction`; `Constants/KEEP_LATEST_AUCTIONS=5`; `auction.createddate` column | **Auction pick:** the single most-recent `Auction`. Ported as `ORDER BY created_date DESC LIMIT 1` (explicit sort *not* captured in docs — inferred; only 1 auction row exists today so it is unambiguous). |
| `backend/ACT_GetActiveSchedulingAuction.md`: `Filter … where enum_SchedulingAuctionStatus.Started`; `Head`; `DECISION $SchedulingAuctionStarted != empty` | **Live/ended pivot = `SchedulingAuction.RoundStatus == Started` ONLY.** Not a datetime-window comparison, not `Auction.auctionstatus`. A `Scheduled` round (even one with a past window) does **not** count as live for a regular Bidder. |
| `backend/ACT_OpenBidderDashboard.md` L35-42: `DECISION $SchedulingAuctionStartedRound != empty` → **false** → `Update Round1Status='AllRoundsDone'` → `Maps to Page BidDownloadOnBuyerCodeSelect` | **Rule 3 — ended (most-recent-ended):** most-recent auction exists but **no `Started` round** → the ended download page. **← HN lands here.** |
| same, L43+: → true → resolve current round → `PG_Bidder_Dashboard_HOT` (if `AuctionsFeature.LegacyAuctionDashboardActive`) else `_DG2` | **Rule 2 — live:** a `Started` round exists → the live bid grid. |
| `frontend/components/Pages_Page/BidDownloadOnBuyerCodeSelect.md` L26-32: `<span class='confirmationheader confirmationheadercolor'>Bidding </span> <span class='confirmationheader'>has ended.</span>` + per-round microflows `ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round{1,2,3}` | **Ended page:** the "Bidding has ended." panel with one download button **per round the buyer has bids for** (static R1/R2/R3 buttons gated on participation). HN participated in R1 only → one "Download your Round 1 Bids" button. |
| `backend/ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round1.md`: retrieve most-recent Auction → SchedulingAuction (Round=1) → BidRound[BuyerCode=code] → BidDataDoc → `DownloadFile` (xlsx) | **Download wiring:** serves this buyer code's submitted-bid xlsx for round N. New app equivalent = `BidExportService.export(bidRoundId, buyerCodeId)`. |
| `database/schema-auctionui.md` L531: `auction.auctiontitle` sample `Auction 2026 / Wk04` | **Heading** = `Auction.auction_title` verbatim ("Auction {year} / Wk{week}"). |

**Genuine gaps flagged (not invented):** (a) explicit sort on `ACT_GetMostRecentAuction`;
(b) exact per-round button `visible` expression; (c) exact subtext "Your bids from
round {N} can be found below." (only "Bidding has ended." is captured);
(d) `Error_Auction_Not_Found` body copy ("No scheduled auction is available." unverified —
kept as-is per mission). Elevated roles (SalesRep/Admin/SalesOps) get a closed-round
*preview* of the live grid instead of the ended page (`ACT_GetActiveSchedulingAuction`
L19-31) — out of scope here (a Bidder is the evidence account).

### Selection rules → new implementation

| Precedence | Condition | New `BidderDashboardLandingResult` | Frontend `mode` |
|---|---|---|---|
| Live | some SA `RoundStatus=Started` (+ QBC/round routing unchanged) | `Grid` / `Download` / `AllDone` (existing) | `GRID` / `DOWNLOAD` / `ALL_ROUNDS_DONE` |
| **Ended** (new) | no `Started` round **and** a most-recent `Auction` with rounds exists | **`Ended(auctionTitle, downloadRounds)`** | `DOWNLOAD` (+ `download` payload) |
| Truly-empty | no `Auction` / no `SchedulingAuction` at all | `Error("AUCTION_NOT_FOUND")` (existing) | `ERROR_AUCTION_NOT_FOUND` |

`downloadRounds` = the rounds of the most-recent auction for which this buyer code has a
`bid_rounds` row (proxy for legacy's "has a `BidDataDoc`"). HN → `[1]`.

---

## 2. HN ground-truth (dev DB, read-only)

- `auctions.auctions`: 1 row — id 1, "Auction 2026 / Wk13", created 2026-03-27.
- `auctions.scheduling_auctions`: rounds 1/2/3 **all `Closed`** (brief said R3 `Scheduled`;
  actual reseeded DB has R3 `Closed` — either way **no `Started` round**, so the pivot is
  identical).
- HN = `buyer_code_id 84`; one `bid_rounds` row (id 2, SA 1 / round 1, `submitted=true`)
  with **10,951** `bid_data` rows; no R2/R3 bid_rounds. → downloadRounds `[1]`.

---

## 3. Pixel values (measured from `evidence/h1-2026-07-12/legacy-local-bidder-dashboard__default.png`, 1920×1080; cross-checked vs `migration_context/styling/EcoAtm.css`)

| Element | Value | Token |
|---|---|---|
| Page bg | `#F7F7F7` | `--color-bg-body` |
| Panel bg | `#F2F1F0` | (local) |
| Panel border | `1px solid #D0D0D0` | `--color-input-border` |
| Panel box | x481–1670 × y265–857 = **1189×592**, centered column (≈249px symmetric margins) | — |
| Heading | "Auction {yr} / Wk{wk}", 32px/500/`#3C3C3C`, Brandon Grotesque | reuse `.auctionTitle` |
| Heading divider | 1px×~32px `#DDDDDD`, ~8px after heading text (x≈781) | (local) |
| "Bidding" | `#14AC36`, 35px/500 (`.confirmationheader.confirmationheadercolor`) | `--color-brand-green` |
| "has ended." | `#3C3C3C`, 35px/500 (`.confirmationheader`) | `--color-text-body` |
| Subtitle | "Your bids from round {N} can be found below.", 14px/`#3C3C3C` (`.confirmationSubHeader`) | — |
| Download button | pill: `#F7F7F7` bg, `1px #D0D0D0` border, `#3C3C3C` text, 14px/500, ~267×42 | `.btn-outline` + `--radius-pill` |

---

## 4. Change set

**Backend**
- `BidderDashboardLandingResult` — new `Ended(String auctionTitle, List<Integer> downloadRounds)`.
- `AuctionRepository.findFirstByOrderByCreatedDateDesc()`.
- `BidderDashboardService` — no-`Started`-round branch now `resolveEndedOrEmpty(...)`
  (ended vs truly-empty); `computeDownloadableRounds(...)`; `findDownloadableRound1BidRoundId`
  generalised to `findDownloadableRoundBidRoundId(userId, buyerCodeId, round)`.
- `dto/DownloadStatePayload(String auctionTitle, List<Integer> rounds)`; added as nullable
  `download` on `BidderDashboardResponse`.
- `BidderDashboardController` — `Ended` → `DOWNLOAD` response carrying `download`;
  `/download-round-1` generalised to `/download-round/{round}` (inherits `/api/v1/bidder/**`
  authz + class `@PreAuthorize`; ownership via `assertOwnership` → 403 `NOT_YOUR_BID_DATA`).

**Frontend**
- `lib/bidder.ts` — `DownloadStatePayload` schema + `download` field; `downloadRound1Bids`
  → `downloadRoundBids(round, buyerCodeId)` (`/download-round/{round}`).
- `BidderDashboardClient` — stores the `download` payload; DOWNLOAD branch renders the
  panel with heading + per-round buttons.
- `EndOfBiddingPanel` + `endOfBiddingPanel.module.css` — legacy panel chrome when an
  `auctionTitle` is present; bare centered text (unchanged) for ERROR/ALL_DONE.

**Scope note:** the two live-path `Download` cases (BUYER_NOT_INCLUDED, ROUND2_DOWNLOAD)
keep the bare layout (no evidence capture; enriching them to the full panel is a tracked
follow-up). BDD-P2 (sidebar) / BDD-P3 (Switch-Buyer-Code widget, top bar) untouched.

---

## 5. Tests & results

**Hardening found via live testing:** the download endpoint's not-found branch used
`HttpServletResponse.sendError(404)`, which triggers a container ERROR dispatch that re-runs the
security chain *without* the JWT filter (`OncePerRequestFilter.shouldNotFilterErrorDispatch`),
turning a legitimate 404 into a **401** in the real app. Switched to `setStatus(404)` (commits the
404 directly). The UI never hits this branch — it only renders a button per participated round —
but the status is now correct.

### Backend unit / slice (DB-free, runnable; Docker absent so no `PostgresIntegrationTest`)
- `BidderDashboardServiceTest` — **12/12 green**. New: `landingRoute_endedAuctionNoStartedRound_returnsEndedWithParticipatedRounds`
  (HN shape → `Ended("Auction 2026 / Wk13", [1])`), `_noSchedulingAuctionAtAll_returnsError`,
  `_mostRecentAuctionHasNoRounds_returnsError`, and 3 `findDownloadableRoundBidRoundId` cases
  (closed-round hit / no-closed-round / non-owner-throws-403).
- `BidderDashboardControllerTest` (`@WebMvcTest` + imported real `SecurityConfig` + auto
  `@RestControllerAdvice`) — **10/10 green**. New: Ended→`DOWNLOAD` body carrying
  `download.auctionTitle`/`download.rounds`; `/download-round/{round}` 200 / 404 / **403 wrong-role**
  (`@PreAuthorize`) / **403 wrong-tenant** (ownership guard → `GlobalExceptionHandler`).

### Frontend (`npm ci`, then vitest + tsc)
- `EndOfBiddingPanel.test.tsx` (3) + `bidder.test.ts` (13, +1 DOWNLOAD-parse) — **16/16 green**.
- `npx tsc --noEmit`: **zero errors in any touched file**; total is the 31 known pre-existing,
  unrelated errors (unchanged — the 2 transient ones my `download` field introduced in
  `bidder.test.ts` fixtures were fixed).

### Live end-to-end (scratch DB — safe, never touched `salesplatform_dev`)
Cloned `salesplatform_dev` → `parity_scratch_bdd` (pg_dump, ~9s), booted **this branch's backend**
on **`SERVER_PORT=18082`** against it (`SPRING_FLYWAY_ENABLED=false`, `AUCTIONS_LIFECYCLE_ENABLED=false`),
then dropped the DB + killed the process:

| Call (HN = buyer_code 84) | Result |
|---|---|
| `GET /dashboard?buyerCodeId=84` (admin) | `mode=DOWNLOAD`, `download={"auctionTitle":"Auction 2026 / Wk13","rounds":[1]}` ✓ (**the fix**) |
| `GET /download-round/1?buyerCodeId=84` | `200`, xlsx, **626 KB** (HN's 10,951 R1 bids) ✓ |
| `GET /download-round/2?buyerCodeId=84` | `404` (HN never bid R2; pre-`setStatus` this was 401) ✓ |
| bidder → `/dashboard` for a **non-owned** code | **403** ✓ (live wrong-tenant proof) |
| salesops (authenticated) → `/dashboard` | **401** — denied, but 401 not 403 (see below) |

**Open item — wrong-role status:** an authenticated non-Bidder/non-Admin (salesops) is *denied*
in the real app but with **401**, whereas the `@WebMvcTest` slice returns **403** (and the
existing pre-change `get_dashboard_403_forSalesOps` asserts 403). This 401-vs-403 is pre-existing,
app-wide bidder-controller behavior (my endpoint inherits the identical `@PreAuthorize` +
`/api/v1/bidder/**` matcher — unchanged here); the security goal (deny) holds. Wrong-tenant is a
clean 403 both live and in the slice. Flagged for the SecurityConfig owner, out of scope for BDD-P1.
