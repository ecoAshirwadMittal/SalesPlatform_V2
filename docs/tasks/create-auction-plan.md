# Create Auction — Implementation Plan

**Date:** 2026-04-19
**Owner:** Ashirwad Mittal
**Status:** Shipped — Phases 1-5 complete (2026-04-19)

## 1. Scope

Port the Mendix **Create Auction** flow from the admin Inventory page
(`PG_AggregatedInventory`) into the Modern app. Replace the placeholder
`alert()` stub in the `Create Auction` header button with a real
pixel-matched modal that creates:

1. One `auctions.auctions` row for the selected week.
2. Three `auctions.scheduling_auctions` rows (rounds 1–3) with Mendix
   time offsets relative to `week_start_datetime`.

Out of scope for this task (tracked separately):
- Round-level edit screen (`ACC_AuctionSchedulePage`).
- Email reminder scheduling (`SUB_SendAuctionEmail*`).
- Snowflake push of round audit JSON (`snowflake_json`).

## 2. Mendix Source-of-Truth

| Artifact | File |
|---|---|
| Page | `migration_context/frontend/components/Pages_Page/Create_Auction.md` |
| Launch microflow | `migration_context/backend/ACT_PreCreateAuction.md` |
| Save microflow | `migration_context/backend/ACT_Create_Auction.md` |
| Title validation | `migration_context/backend/domain/VAL_Create_Auction.md` |

### 2.1 Rules baked into `ACT_Create_Auction`

- `startHourOffset = 16h` from `week_start_datetime`.
- Round 1: `start = weekStart + 16h`, `end = weekStart + 103h` (+87h window).
- Round 2: `start = weekStart + 104h`, `end = weekStart + 128h`.
- Round 3: `start = weekStart + 129h`, `end = weekStart + 156h`.
- All three rounds start in status `Scheduled`.
- Auction title: `"Auction " + weekDisplay` or
  `"Auction " + weekDisplay + " " + customSuffix` (trimmed).
- Title must be **unique**, case-insensitive, across `auctions.auctions`.

## 3. Architecture

### 3.1 Backend (`backend/`)

| Layer | File | Purpose |
|---|---|---|
| Model | `model/auctions/Auction.java` | New JPA entity → `auctions.auctions`. |
| Model | `model/auctions/SchedulingAuction.java` | New JPA entity → `auctions.scheduling_auctions`. |
| Model | `model/auctions/AuctionStatus.java` | Enum: `Unscheduled`, `Scheduled`, `Started`, `Closed`. |
| Model | `model/auctions/SchedulingAuctionStatus.java` | Enum with same four values (Mendix `enum_SchedulingAuctionStatus`). |
| Repo | `repository/auctions/AuctionRepository.java` | `existsByWeekId(Long)`, `existsByAuctionTitleIgnoreCase(String)`. |
| Repo | `repository/auctions/SchedulingAuctionRepository.java` | Basic CRUD; list-by-auction for later round-edit screen. |
| DTO | `dto/CreateAuctionRequest.java` | `record CreateAuctionRequest(Long weekId, String customSuffix)`. |
| DTO | `dto/CreateAuctionResponse.java` | `record CreateAuctionResponse(Long id, String auctionTitle, Long weekId, String weekDisplay, List<Round> rounds)` + nested `Round(Long id, int round, Instant start, Instant end, String status)`. |
| Service | `service/auctions/AuctionService.java` | `@Transactional createAuction(req)`. |
| Exception | `exception/DuplicateAuctionTitleException.java` | → 409. |
| Exception | `exception/WeekNotFoundException.java` | → 404. |
| Exception | `exception/AuctionAlreadyExistsException.java` | → 409. |
| Controller | `controller/AuctionController.java` | `POST /api/v1/admin/auctions`. `@PreAuthorize("hasAnyRole('Administrator','SalesOps')")`. |

Controller placement: new file rather than bolting onto
`AggregatedInventoryController` because the resource is `auctions`, not
`inventory`.

### 3.2 Frontend (`frontend/`)

| Layer | File | Purpose |
|---|---|---|
| Types/API | `src/lib/auctions.ts` | New. `createAuction(req)` + Zod schemas. |
| Page | `src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx` | Replace `alert()` with modal mount. Gate button on `hasInventory && isCurrentWeek && !hasAuction`. Refetch totals on success so `hasAuction` flips and the button disappears. |
| Component | `src/app/(dashboard)/admin/auctions-data-center/inventory/CreateAuctionModal.tsx` | New. Pixel-match QA screenshot in `qa-create-auction-modal.png`. |
| Styles | `src/app/(dashboard)/admin/auctions-data-center/inventory/inventory.module.css` | Add modal + input-with-prefix styles, helper-text blocks. |

## 4. Phases

### Phase 1 — Backend domain + service

1. Add enums `AuctionStatus`, `SchedulingAuctionStatus`,
   `ScheduleAuctionInitStatus`, `ReminderEmails`. Stored as
   `VARCHAR` with `@Enumerated(EnumType.STRING)` (match
   V58 `CHECK` constraints).
2. Add `Auction` + `SchedulingAuction` entities. Bidirectional
   mapping NOT needed yet — load scheduling rows via repository when
   the round-edit screen lands.
3. Add repos with the duplicate-title and existence queries listed
   above. Use `JpaRepository<T, Long>`.
4. Implement `AuctionService.createAuction`:
   - Load `Week` by id → throw `WeekNotFoundException` if missing.
   - `existsByWeekId` → throw `AuctionAlreadyExistsException` (one
     auction per week is a Mendix invariant — `hasAuction` helper
     flag already reflects this on the page).
   - Compute `auctionTitle` — mirror Mendix (`trim`, prefix by
     `"Auction " + weekDisplay`).
   - `existsByAuctionTitleIgnoreCase` → throw
     `DuplicateAuctionTitleException`.
   - Save `Auction`; then save 3 `SchedulingAuction` rows with the
     time offsets. Use `weekStart.plus(Duration.ofHours(n))`.
   - Return `CreateAuctionResponse`.
5. Wire `@ControllerAdvice` mappings for the three new exceptions so
   the controller stays thin. Existing `GlobalExceptionHandler`
   should be extended, not replaced.

### Phase 2 — Controller + tests

6. `AuctionController.create` — call service, return `201 Created`
   with `Location` header pointing at `/api/v1/admin/auctions/{id}`
   (GET endpoint can land with the round-edit screen). Body is
   `CreateAuctionResponse`.
7. Unit tests (`AuctionServiceTest`):
   - happy path → verifies title, 3 rounds, offsets, `Scheduled`
     status.
   - custom suffix → title includes suffix with single space.
   - whitespace-only suffix → treated as empty (trim).
   - missing week → `WeekNotFoundException`.
   - auction already exists for week → `AuctionAlreadyExistsException`.
   - duplicate title (case-insensitive match) →
     `DuplicateAuctionTitleException`.
8. Controller tests (`AuctionControllerTest` via MockMvc):
   - Admin role + valid body → `201`.
   - SalesOps role + valid body → `201` (matches page role gate).
   - Bidder role → `403`.
   - Duplicate title → `409` with JSON error body.
9. Verify no regression in `AggregatedInventoryControllerTest` — the
   `hasAuction` helper flag relies on `auctions.auctions`
   `EXISTS`, which is unaffected.

### Phase 3 — Frontend modal + wiring

10. `src/lib/auctions.ts` — Zod schemas + `createAuction` call
    against `POST /api/v1/admin/auctions` with `credentials: 'include'`
    via shared `apiFetch`.
11. `CreateAuctionModal.tsx`:
    - Props: `{ weekDisplay, weekId, onClose, onCreated }`.
    - Focus trap + Escape to close + click-outside to close
      (match existing `EditModal` pattern).
    - Prefix `"Auction {weekDisplay}"` is a read-only panel; textbox
      next to it captures the optional suffix.
    - Helper text blocks pulled verbatim from QA screenshot.
    - Submit: disables inputs, shows spinner on button, calls
      `createAuction`, then `onCreated`.
    - On duplicate-title (409) → show inline error
      `"An auction with this name already exists."` under input.
    - On network error → generic error banner above button.
12. `inventory/page.tsx`:
    - Replace `onClick={alert}` with `onClick={() => setShowCreate(true)}`.
    - Render modal when `showCreate && weekId && totals`.
    - `onCreated` → `setShowCreate(false)` + `refresh()` (flips
      `hasAuction` → hides button next render).
    - Hide "Create Auction" button entirely when
      `!totals?.hasInventory || !totals?.isCurrentWeek || totals?.hasAuction`.
      Mirrors the Mendix visibility expression.

### Phase 4 — QA pixel-match + smoke

13. Local: start backend + frontend. Log in as
    `admin@test.com / Admin123!`. Open
    `/admin/auctions-data-center/inventory`, pick the current week.
14. Open modal → compare to `qa-create-auction-modal.png`
    side-by-side. Tweak spacing / font sizes until pixel-match.
15. Submit with empty suffix → verify in DB:
    - `auctions.auctions` has one row with `auction_title = 'Auction ' || week.week_display` and status `Unscheduled`.
    - `auctions.scheduling_auctions` has 3 rows with correct offsets
      and status `Scheduled`.
16. Re-open page → verify Create Auction button is hidden and
    `hasAuction = true` in `/totals`.
17. Attempt a second create on the same week via REST (curl) →
    expect `409 AuctionAlreadyExistsException`.
18. Attempt duplicate title via suffix match against existing auction
    (if any in DB) → `409 DuplicateAuctionTitleException`.

### Phase 5 — Docs + ADR

19. `docs/api/rest-endpoints.md` — new `## Auctions` section for
    `POST /admin/auctions` with request/response and error codes.
20. `docs/architecture/decisions.md` — new ADR explaining:
    - Placing the endpoint under `/admin/auctions` vs bolting onto
      `/admin/inventory`.
    - Writing the 3 rounds inside the same tx as the parent
      auction (no post-commit event — the record is pure write, no
      side effects like email).
    - Storing enums as `VARCHAR` with `@Enumerated(EnumType.STRING)`
      to match V58 CHECK constraints.

## 5. Non-goals / Follow-ups

- **Round start/end editor.** Mendix routes from Create Auction →
  `Inventory_Auction_Overview` → round-edit. This plan returns the
  user to the Inventory grid; a later task will add the overview
  page and round editor.
- **Email reminders scaffold.** `is_*_notification_sent` columns
  already exist (V58); we leave them default `false` here.
- **Snapshot push to Snowflake** (`snowflake_json`) — nightly job,
  separate ADR.
- **Unscheduling / deletion.** `ACT_UnscheduleAuction` exists in
  Mendix; a separate plan will port it once the round editor lands.

## 6. Risk Notes

| Risk | Mitigation |
|---|---|
| TZ drift on offsets | `Week.weekStartDateTime` is `Instant`/`TIMESTAMPTZ`; `plus(Duration.ofHours(n))` is absolute-time math — no zone skew. |
| Concurrent double-click creates two auctions | DB-level race — a unique partial index on `(week_id) WHERE auction_status <> 'Closed'` would kill the race. Not shipping in this task — `existsByWeekId` check in the tx is enough for single-user admin flow; revisit if QA reveals it. |
| Duplicate-title race | `existsByAuctionTitleIgnoreCase` inside tx + DB-side uniqueness is overkill for 5 rows/year. Accepted risk. |
| Bidder sees the button | Backend `@PreAuthorize` is authoritative; frontend hides the button for UX only. Role gate already proven on `/admin/inventory` and `/admin/inventory/{id}` PUT. |

## 7. Verification Checklist

- [x] `mvn -pl backend test` green — `AuctionServiceTest` + `AuctionControllerTest`.
- [x] `npm run lint` + `npx tsc --noEmit` green in `frontend/`.
- [x] Local smoke: modal opens (`Auction 2026 / Wk16` prefix), pixel-matches
      QA, submit creates 1 auction + 3 `Scheduled` rounds, button hides on
      refetch. Verified via Playwright + psql:
      - Wk17 (id 544) auction created via REST smoke → `409` on retry.
      - Wk16 (id 383) auction created via UI smoke → button disappeared.
- [x] `docs/api/rest-endpoints.md` updated.
- [x] ADR added to `docs/architecture/decisions.md`.

### Drive-by fix

- `AggregatedInventoryService.java:84` — the `ORDER BY a.ecoid2::bigint`
  cast introduced in commit `241a98b` was being parsed by Hibernate's
  named-parameter scanner as a malformed `:bigint` placeholder, producing
  `syntax error at or near ":"` on every admin-inventory list call.
  Replaced with `CAST(a.ecoid2 AS bigint)`. This blocked Phase 4 UI smoke
  until fixed.
