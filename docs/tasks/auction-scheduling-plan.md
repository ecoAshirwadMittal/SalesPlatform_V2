# Auction Scheduling (Create → Schedule → Confirm) — Build Plan

**Owner:** Ashirwad Mittal  •  **Created:** 2026-04-20  •  **Status:** Draft (pre-implementation)

Scope: Port the full Mendix `AuctionUI` auction-lifecycle flow that runs
from the admin inventory page through Auction Scheduling and the
Confirm modal. Covers Create Auction (re-shaped), Auction Scheduling
page, Save (Confirm) write path, Round 2 Selection Rules (Criteria),
Unschedule, and Delete. Round status transitions (Scheduled → Started
→ Closed) and the per-round edit modal are explicitly deferred.

> **Supersedes in part:** the 2026-04-19 ADR (*Create Auction: dedicated
> endpoint + in-tx round creation + enum-as-varchar*). Round-creation
> timing and the Round 3 display name described there are wrong vs
> Mendix source. Phase A below documents the ADR amendment.

---

## 1. Mendix source of truth

The flow spans five microflows plus two pages. Legacy artifact paths
are given relative to `migration_context/`.

### 1.1 User flow (QA walkthrough, verified 2026-04-20)

```
/admin/AggregatedInventory
    ├─ [Create Auction] button           (visible when: HasInventory && IsCurrentWeek && !HasAuction)
    └─► ACT_PreCreateAuction
        └─► Create_Auction popup
            ├─ "Title (optional)" text input → writes AggInventoryHelper.AuctionName
            └─ [Create] → ACT_Create_Auction
                ├─ creates ONE Auction row (status=Unscheduled)
                ├─ populates SchedulingAuction_Helper (transient) with round dates
                ├─ flips AggInventoryHelper.HasAuction=true, HasSchedule=false
                └─► maps to Inventory_Auction_Overview page
                    └─ [Schedule Auction] button
                        └─► ACT_LoadScheduleAuction_Helper
                            └─► Auction_Scheduling page
                                ├─ R1: From Date+Time, To Date+Time (4 inputs, always editable)
                                ├─ R2: checkbox isActive | To Date+Time (2 inputs when active)
                                ├─ R3: checkbox isActive | To Date+Time (2 inputs when active)
                                ├─ [Selection Rules] link (per round) → acc_RoundTwoCriteriaPage
                                └─ [Confirm] → VAL_Schedule_Auction
                                    └─► ScheduleAuction_Confirm modal
                                        ├─ shows computed R1/R2/R3 start/end summary
                                        └─ [Looks good, Schedule!] → ACT_SaveScheduleAuction
                                            ├─ creates 3 SchedulingAuction rows (delete-and-recreate if existing)
                                            ├─ flips Auction.status=Scheduled
                                            ├─ flips AggInventoryHelper.HasSchedule=true
                                            └─ if BuyerCodeSubmitConfig.SendAuctionDataToSnowflake
                                                → SUB_SendAuctionAndSchedulingActionToSnowflake_async
```

### 1.2 Key microflow contracts

| Mendix | What it does | New-app equivalent |
|---|---|---|
| `ACT_PreCreateAuction` | Opens the Create popup; no persistence. | `CreateAuctionModal` already exists (inventory page). |
| `ACT_Create_Auction` | Persists **Auction row only**; sets `WasTitleEmpty`, `HasAuction=true`, `HasSchedule=false`; populates transient round dates. | **Refactor target** — our service over-writes 3 rounds here. |
| `ACT_Create_SchedulingAuction_Helper_Default` | Recomputes `R2_Start = WSD + 60·(SHO+87) + AuctionRound2MinutesOffset` and `R3_Start = WSD + 60·(SHO+112) + AuctionRound3MinutesOffset` using `BuyerCodeSubmitConfig`. Called at load of the scheduling page. | New endpoint `GET /admin/auctions/{id}/schedule-defaults`. |
| `ACT_LoadScheduleAuction_Helper` | Populates helper from existing rounds if rescheduling; otherwise delegates to `Helper_Default`. | Same endpoint — branch on existing rounds. |
| `VAL_Schedule_Auction` | Per active round: `End > Start` or accumulate comma-separated error string. | Service-layer validation on `PUT /schedule`. |
| `ACT_SaveScheduleAuction` | Delete existing `SchedulingAuction` rows, create 3 fresh ones (`"Round 1"`, `"Round 2"`, `"Upsell Round"`), flip `Auction.status → Scheduled`, fire Snowflake audit if config says so. | **New** `PUT /admin/auctions/{id}/schedule`. |
| `ACT_UnscheduleAuction` | If no round has status `Started`: flip auction + all rounds to `Unscheduled`; fire Snowflake audit. | **New** `POST /admin/auctions/{id}/unschedule`. |
| `ACT_Delete_AuctionFromAdmin` | Delete auction + two `ExecuteDatabaseQuery` cleanup steps (purge dependent rows). | **New** `DELETE /admin/auctions/{id}`. |
| Round 2/3 Criteria page | Edits `BidRoundSelectionFilter` (round 2 + round 3 singleton-per-round filter rows). | **New** `PUT /admin/auctions/round-filters/{round}`. |

### 1.3 Round offset math (authoritative)

`StartHourOffset = 16` (legacy hardcode in `ACT_Create_Auction`; the
`Helper_Default` microflow overrides it to `TimezoneOffset + 11` for TZ
correction — irrelevant on a UTC-stored `week_start_datetime`).

| Round | Start (hours from `week_start_datetime`) | End (hours from `week_start_datetime`) | Mendix name |
|---|---|---|---|
| 1 | +16 | +103 | `"Round 1"` |
| 2 | +104 (= SHO+88; overridable via `auction_round2_minutes_offset`) | +128 (= SHO+112) | `"Round 2"` |
| 3 | +129 (= SHO+113; overridable via `auction_round3_minutes_offset`) | +156 (= SHO+140) | **`"Upsell Round"`** |

Config overrides come from `buyer_mgmt.auctions_feature_config`
(existing singleton, see `V8__buyer_mgmt_core.sql:134-148` —
`auction_round2_minutes_offset` default 360, `auction_round3_minutes_offset`
default 180). The R2/R3 *Start* values are the only ones that shift
when config changes; *End* values are fixed. R2/R3 *Ends* are what the
admin actually edits on the Auction Scheduling page.

### 1.4 Title composition

```
AuctionTitle = "Auction " + Week.weekDisplay
             + (" " + trim(customSuffix) if non-blank)
```

`Week.weekDisplay` is already seeded in `"YYYY / WkNN"` format (see
`V58:29` comment and `V65__seed_mdm_week.sql:42`). Legacy rows in
`auctions.auctions` have `auctiontitle = "Auction 2026 / Wk04"` (not
`"Auction Week 04 2026"`), so the case-insensitive uniqueness check
must operate against that shape. Our current code produces the correct
shape — **the 2026-04-19 ADR example string is wrong, not the code**.

### 1.5 Invariants we must preserve

1. **One auction per week**, enforced by `existsByWeekId` inside the tx.
2. **Case-insensitive title uniqueness** across all of `auctions.auctions`.
3. **Round-creation timing**: 3 `SchedulingAuction` rows must exist
   *after* the Confirm step, not after Create. An auction in status
   `Unscheduled` must have **zero** rounds; an auction in status
   `Scheduled` / `Started` / `Closed` must have exactly **3** rounds.
4. **Delete-and-recreate on reschedule**: re-submitting `Confirm` on an
   already-scheduled auction must delete the 3 existing rows and insert
   3 fresh ones. `scheduling_auction_id` FKs in downstream tables
   (`bid_rounds`, `bid_data`) mean we must **block reschedule once
   bids exist** — add a guard in the service layer.
5. **Unschedule is gated** by `no round is in status Started`. A
   started auction cannot be unscheduled — UI and service both enforce
   this.
6. **Round name wording** is customer-facing on the scheduling page and
   in email templates: `"Round 1"`, `"Round 2"`, **`"Upsell Round"`**.

---

## 2. Gap analysis vs new-app

| Topic | Current state | Required | Severity |
|---|---|---|---|
| Round creation timing | 3 rows created at POST `/admin/auctions` | 0 rows at POST; 3 rows at PUT `/schedule` | **High** — architectural |
| Round 3 display name | `"Round 3"` (`AuctionService:139`) | `"Upsell Round"` | **High** — customer-facing |
| R2/R3 active toggles | Always created with `has_round=true`, `status=Scheduled` | Driven by `Round2_isActive` / `isRound3Active` from helper | **High** |
| R2/R3 minute offsets | Not read from config | Read `auctions_feature_config.auction_round2_minutes_offset` etc. for R2/R3 Start default on load | Medium |
| Auction status on save | Stays `Unscheduled` forever | Flip to `Scheduled` in `ACT_SaveScheduleAuction` port | **High** |
| Auction Scheduling page | Does not exist | New route `/admin/auctions-data-center/auctions/[id]/schedule` | **High** |
| Confirm modal | Does not exist | Rendered inside scheduling page | **High** |
| Snowflake audit push on save/unschedule | Not wired | Post-commit event + `@Async` listener gated by `auctions_feature_config.send_auction_data_to_snowflake` | Medium (follow existing pattern) |
| `BidRoundSelectionFilter` entity/service/API | Table exists (V59), no Java model | Entity + repo + service + PUT endpoint + Round 2 Criteria page | Medium |
| Unschedule endpoint | Missing | New `POST /admin/auctions/{id}/unschedule` | Medium |
| Delete endpoint | Missing | New `DELETE /admin/auctions/{id}` with cascade purge | Medium |
| GET `/admin/auctions/{id}` | Missing | Needed by scheduling page load | **High** |
| ADR 2026-04-19 accuracy | Documents wrong title example and wrong creation-timing rationale | Amend section noting supersession | Medium |
| Offsets in `AuctionService` constants | **Correct** (matches Mendix fixed defaults) | No change | — |
| `AuctionService` title builder | **Correct** | No change | — |
| `SchedulingAuction` JPA entity | Fully populated (has `round3_init_status`, `email_reminders`, `has_round`, `notifications_enabled`, `snowflake_json`) | No change | — |

---

## 3. Phased build plan

### Phase A — ADR amendment (paperwork, no code change)

**Deliverable:** Append a dated correction section to
`docs/architecture/decisions.md` that:

1. States the 2026-04-19 ADR is **partially superseded** by this plan.
2. Corrects the title example: `"Auction 2026 / Wk17"`, not
   `"Auction Week 17 2026"`.
3. Records the architectural revision: **Create Auction persists only
   the `Auction` row; three `SchedulingAuction` rows are persisted by
   the later Save Schedule write.** Justification: mirrors Mendix; an
   `Unscheduled` auction with no rounds is a legitimate intermediate
   state the UI already treats specially (`HasSchedule=false`).
4. Records the Round 3 name correction: `"Upsell Round"`.

No migrations or data changes required — no auction has been created
in prod/QA through our new-app endpoint yet. Existing seeded auctions
from `V22__data_pws.sql`/`V58` seed data are pre-created via the
extractor and already have 3 rounds each; they're unaffected.

### Phase B — Refactor Create Auction (shrink to auction-only write)

**Goal:** `POST /admin/auctions` creates the `Auction` row alone.

**Files:**

- `backend/.../service/auctions/AuctionService.java`
  - Remove `SchedulingAuctionRepository` dependency.
  - Drop the `ROUND*_OFFSET_HOURS` constants and the `buildRound`
    helper (move them into `AuctionScheduleService` — Phase C).
  - `createAuction` returns a `CreateAuctionResponse` with no `rounds`
    field (breaking schema change — update DTO + test).
- `backend/.../dto/CreateAuctionResponse.java`
  - Remove nested `Round` record and `rounds` list.
- `backend/.../controller/AuctionController.java`
  - Unchanged signature, just smaller response body.
- `backend/.../test/.../AuctionServiceTest.java`
  - Remove 6 round-assertion cases; keep the 3 existence/duplicate cases.
- `backend/.../test/.../AuctionControllerTest.java`
  - Update JSON body expectations.
- `frontend/src/lib/auctions.ts`
  - Drop `rounds` from `CreateAuctionResponse` type; callers don't use it.
- `docs/api/rest-endpoints.md`
  - Strip the round table from the POST response block.

**Risk:** If a later session adds a UI that reads the `rounds` field
from the create response, it'll break. Mitigation: the scheduling page
(Phase C) fetches rounds via `GET /admin/auctions/{id}`, so no client
needs the rounds inline with create.

### Phase C — Auction Scheduling page + Save endpoint

**Goal:** Admin can edit R1 start/end + R2/R3 active flags and end
times, confirm, and persist 3 rounds atomically.

#### C.1 Entity layer (no changes — `SchedulingAuction` already has all fields)

Verify Flyway `V58:102-135` has `name VARCHAR(50)` with no CHECK
constraint restricting values — `"Upsell Round"` will fit.

#### C.2 Repository additions

- `SchedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(Long auctionId)` — existing.
- `SchedulingAuctionRepository.deleteByAuctionId(Long auctionId)` — new `@Modifying` query for the delete-and-recreate path.
- `SchedulingAuctionRepository.existsByAuctionIdAndRoundStatus(Long auctionId, SchedulingAuctionStatus status)` — for the Started guard.
- New `BidRoundRepository.existsByAuctionId(Long auctionId)` — for the
  **bids-already-placed** guard on reschedule (cross-table check to
  `auctions.bid_rounds`).

#### C.3 New service: `AuctionScheduleService`

Lives in `backend/.../service/auctions/AuctionScheduleService.java`.

```
public record ScheduleDefaults(
    Instant round1Start, Instant round1End,
    Instant round2Start, Instant round2End,
    Instant round3Start, Instant round3End,
    boolean round2Active, boolean round3Active,
    int round2MinutesOffset, int round3MinutesOffset
) {}

public record ScheduleAuctionRequest(
    Instant round1Start, Instant round1End,
    Instant round2Start, Instant round2End, boolean round2Active,
    Instant round3Start, Instant round3End, boolean round3Active
) {}

public record ScheduleAuctionResponse(Long auctionId, String status, List<RoundView> rounds) {}
```

Methods:

1. `loadScheduleDefaults(Long auctionId)` — `ACT_LoadScheduleAuction_Helper` + `Helper_Default` port.
   - Fetch `Auction`, `Week`, `AuctionsFeatureConfig` (singleton).
   - If `scheduling_auctions` rows exist → populate from them.
   - Else compute fresh using Mendix formulas, substituting config minute offsets.
2. `saveSchedule(Long auctionId, ScheduleAuctionRequest req, String actor)` — `ACT_SaveScheduleAuction` port.
   - Validate `VAL_Schedule_Auction`: for each active round `End > Start`; accumulate comma-separated errors → throw `ValidationException`.
   - Reject reschedule if `bid_rounds` exist for any round of this auction (new invariant — Mendix didn't need this because Mendix deletes cascaded freely; we have FK constraints).
   - Reject if any round is `Started` (mirrors Unschedule gate applied to Save).
   - Inside `@Transactional`: `deleteByAuctionId`, then insert 3 fresh rows with names `"Round 1"`, `"Round 2"`, `"Upsell Round"` and statuses per `isActive` flags (`Scheduled` when active, `Unscheduled` when not). Set `has_round` accordingly.
   - Flip `Auction.status` to `Scheduled`. Touch `updated_by`/`changed_date`.
   - **Publish `AuctionScheduledEvent` for the Snowflake audit push** (post-commit async, reuses the 2026-04-18 Snowflake executor pattern).
3. `unschedule(Long auctionId, String actor)` — `ACT_UnscheduleAuction` port.
   - Reject if any round is `Started`.
   - Flip auction + all rounds to `Unscheduled` in one tx.
   - Publish `AuctionUnscheduledEvent` (same pattern).
4. `delete(Long auctionId)` — `ACT_Delete_AuctionFromAdmin` port.
   - Reject if any round is `Started`.
   - Cascade: delete `bid_rounds` → `scheduling_auctions` → `auction`. The two Mendix `ExecuteDatabaseQuery` steps are cleanup of `bid_rounds` and downstream audit rows; our V59 has `ON DELETE CASCADE` on `bid_rounds.scheduling_auction_id` so a single `DELETE FROM auctions.auctions` handles the chain, provided `auctions.scheduling_auctions.auction_id` also cascades (verify in V58; if not, add V69 migration to add `ON DELETE CASCADE`).

#### C.4 New event listener: `AuctionSnowflakeAuditListener`

Lives in `backend/.../service/auctions/AuctionSnowflakeAuditListener.java`.

- `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async("snowflakeExecutor")` (executor from 2026-04-18 ADR).
- Gated by `auctions_feature_config.send_auction_data_to_snowflake`.
- Writes a JSON payload to `scheduling_auctions.snowflake_json` (column already exists in entity) or to a new `snowflake_audit_log` table — **deferred to separate ADR**; this plan only publishes the event and wires a no-op listener. The push implementation will be a follow-up so we're not blocked on Snowflake schema design.

#### C.5 Controller endpoints

Add to existing `AuctionController`:

| Method | Path | Auth | Request | Response |
|---|---|---|---|---|
| `GET` | `/api/v1/admin/auctions/{id}` | Administrator, SalesOps | — | `AuctionDetailResponse { id, title, status, weekId, weekDisplay, rounds[] }` |
| `GET` | `/api/v1/admin/auctions/{id}/schedule-defaults` | Administrator, SalesOps | — | `ScheduleDefaults` |
| `PUT` | `/api/v1/admin/auctions/{id}/schedule` | Administrator, SalesOps | `ScheduleAuctionRequest` | `ScheduleAuctionResponse` (200) |
| `POST` | `/api/v1/admin/auctions/{id}/unschedule` | Administrator, SalesOps | — | `AuctionDetailResponse` (200) |
| `DELETE` | `/api/v1/admin/auctions/{id}` | Administrator | — | 204 |

`SecurityConfig`: no changes — `/api/v1/admin/**` rule already covers.

#### C.6 Exception → HTTP mapping

Add to `GlobalExceptionHandler`:

- `ValidationException` → 400 with `{ "message": "End must be after Start on Round 2, Round 3" }` (comma-joined).
- `AuctionAlreadyStartedException` → 409.
- `AuctionHasBidsException` → 409 with `"Bids have already been submitted; reschedule is not available"`.

#### C.7 Frontend

New route: `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/[auctionId]/schedule/page.tsx`.

- Loads `AuctionDetailResponse` + `ScheduleDefaults` on mount.
- Form with R1 (4 fields), R2 (checkbox + 2 fields; From is read-only and derived), R3 (checkbox + 2 fields; From is read-only and derived).
  - R2/R3 From are recomputed client-side whenever the *previous* round's End changes, using the config minute offsets from `ScheduleDefaults`.
- `[Confirm]` → validate client-side (`End > Start` per active round), open confirm modal showing final times and `[Looks good, Schedule!]` button that fires `PUT /schedule`.
- Success: redirect back to `/admin/auctions-data-center/inventory` (route of the Inventory_Auction_Overview port — verify current path).
- Failure: surface server 400/409 inline.

Inventory page change: the existing `[Schedule Auction]` button (wherever it is post-create today) navigates to the new route. The `[Create Auction]` modal's success callback now navigates to the schedule route rather than just closing, mirroring Mendix's "Create → land on Scheduling page" behavior.

Styling parity: reference `migration_context/styling/EcoAtm.css` and
the QA page at
`https://buy-qa.ecoatmdirect.com/p/auction/admin/schedule/<id>` (pulled
via Playwright during implementation, per CLAUDE.md QA rule).

#### C.8 Tests

- `AuctionScheduleServiceTest` — 10 scenarios: happy path (Unscheduled→Scheduled), reschedule (delete-and-recreate), reschedule blocked by bids, reschedule blocked by Started round, validation errors per round, unschedule happy path, unschedule blocked by Started, delete happy path, delete blocked by Started, loadDefaults branches (fresh vs existing).
- `AuctionControllerTest` — MockMvc for the 4 new endpoints: admin 200, SalesOps 200 (except DELETE which is admin-only → 403 for SalesOps), bidder 403 across all, 404 for unknown id, 400 on invalid body.
- Integration test: `@SpringBootTest` that posts Create → puts Schedule → asserts 3 rounds + `Auction.status=Scheduled`.

### Phase D — Round 2/3 Selection Rules (Criteria)

**Goal:** Admin can edit `BidRoundSelectionFilter` rows (one per round).

#### D.1 Entity + repo

New `backend/.../model/auctions/BidRoundSelectionFilter.java` (maps to
`auctions.bid_round_selection_filters`, which already exists in V59).
Fields per schema (round, target_percent, target_value,
total_value_floor, merged_grade1..3, stb_allow_all_buyers_override,
stb_include_all_inventory, regular_buyer_qualification,
regular_buyer_inventory_options).

Enums:

- `RegularBuyerQualification { Only_Qualified, All_Buyers }`
- `RegularBuyerInventoryOption { InventoryRound1QualifiedBids, ShowAllInventory }`

New `BidRoundSelectionFilterRepository.findByRound(int round)`.

#### D.2 Service

`BidRoundSelectionFilterService` with `get(int round)` / `update(int
round, BidRoundSelectionFilterRequest)`.

#### D.3 Endpoints

| Method | Path | Auth |
|---|---|---|
| `GET` | `/api/v1/admin/auctions/round-filters/{round}` | Administrator, SalesOps |
| `PUT` | `/api/v1/admin/auctions/round-filters/{round}` | Administrator |

Round parameter validated `∈ {2, 3}` to match the CHECK constraint.

#### D.4 Frontend

New route:
`frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/round-filters/[round]/page.tsx`
matching the Mendix `acc_RoundTwoCriteriaPage` layout: buyer
qualification select, inventory qualification select, special
treatment radio/toggle group, save button.

Link from the Scheduling page's "Selection Rules" per-round link opens
this route in a new tab (or modal — TBD during UI build; match QA).

#### D.5 Tests

- Service test: get returns seeded row; update persists + updates changed_date.
- Controller test: round out of range → 400; bidder → 403; SalesOps PUT → 403 (admin-only write).

### Phase E — Round-level status transitions (deferred)

Out of scope for this plan. Covers:

- `ACT_SetAuctionScheduleStarted` / `ACT_SetAuctionScheduleClosed` (per-round status writes).
- Email reminder scheduler (`ReminderEmails` enum + `round3_init_status`).
- Per-round edit modal (`ACT_SchedulingAuction_Save_Admin`).
- Bid submission gating by round status.

Track as a separate plan under `docs/tasks/auction-round-lifecycle-plan.md` when bidding is ported.

### Phase F — Snowflake audit push implementation (deferred)

Out of scope — this plan only fires the event and leaves a no-op
listener. Separate follow-up ADR covers:

- `SUB_SendAuctionAndSchedulingActionToSnowflake_async` port target
  (reuses `snowflakeExecutor` from 2026-04-18).
- Snowflake payload shape and target table.
- Failure handling / retry budget.

---

## 4. Data-layer changes summary

| Change | Why | Reversibility |
|---|---|---|
| New Flyway `V69__auctions_cascade_delete.sql` *(only if V58 missing cascade on `scheduling_auctions.auction_id`)* | Enables `DELETE /admin/auctions/{id}` without manual cleanup | Forward-only |
| No new tables | `bid_round_selection_filters` already exists (V59); `auctions_feature_config` already exists (V8) | — |
| No data backfill | Existing auctions already have 3 rounds each from extractor | — |

---

## 5. Cross-cutting concerns

- **Concurrency.** Two admins clicking `Save` on the same auction
  race on the delete-then-insert. Acceptable: pessimistic lock via
  `SELECT ... FOR UPDATE` on the auction row at start of the save tx.
  Cheap and matches Mendix's implicit single-active-session assumption.
- **Audit.** `created_by` / `updated_by` come from the existing
  `currentUsername()` helper in `AuctionService` — lift to a shared
  `AuditUtil` so both services use one source.
- **Timezone.** `Week.week_start_datetime` is stored in UTC. Round
  offsets are UTC hours. Frontend converts for display using the
  existing page-level timezone handling in
  `/admin/auctions-data-center/inventory`. No new TZ work.
- **Button visibility contract.** Keep
  `hasInventory && isCurrentWeek && !hasAuction` as the Create Auction
  gate (2026-04-19 ADR). Add a new `hasSchedule` boolean to
  `/admin/inventory/totals` so the parent page can toggle between
  `[Create Auction]` / `[Schedule Auction]` / `[Unschedule]` buttons
  without a second fetch. Helper-flag semantics live in the
  existing 2026-04-18 helper-flags ADR.

---

## 6. Risks and open questions

| Risk | Mitigation |
|---|---|
| FK cascade on `scheduling_auctions.auction_id` may already exist or may not | Verify V58 before Phase C.1; add V69 only if needed |
| Mendix delete purges bid_rounds despite FK; we need to block or cascade | Phase C.3 `delete()` — cascade via existing `ON DELETE CASCADE` on `bid_rounds.scheduling_auction_id` |
| Reschedule on an auction with bids is destructive in Mendix (silent) | We reject with 409 — safer; verify with product before shipping |
| Config minute offsets change between Create and Save | Re-read config at Save time; don't trust what the page sent |
| ADR 2026-04-19 references live elsewhere in docs | Grep and update: `docs/api/rest-endpoints.md` already describes R2/R3 in response — Phase B drops |

---

## 7. Definition of done

- [ ] ADR amendment committed (Phase A).
- [ ] `POST /admin/auctions` returns only `{ id, title, status, weekId, weekDisplay }`.
- [ ] `GET /admin/auctions/{id}` returns auction + empty rounds when Unscheduled, 3 rounds when Scheduled/Started/Closed.
- [ ] `PUT /admin/auctions/{id}/schedule` persists 3 rounds with names `Round 1` / `Round 2` / `Upsell Round`, flips status, fires event.
- [ ] Scheduling page pixel-matches Mendix QA for both themes of the edit form and the Confirm modal.
- [ ] Unschedule and Delete endpoints + admin UI actions wired on the inventory page.
- [ ] Selection Rules page (Round 2/3) renders and persists.
- [ ] All new tests green; 80%+ coverage on new service classes.
- [ ] `docs/api/rest-endpoints.md` updated for every endpoint added/modified.
- [ ] `docs/architecture/decisions.md` carries the new ADR.

---

## 8. References

- Mendix: `migration_context/backend/ACT_Create_Auction.md`,
  `ACT_SaveScheduleAuction.md`,
  `ACT_Create_SchedulingAuction_Helper_Default.md`,
  `ACT_LoadScheduleAuction_Helper.md`,
  `ACT_UnscheduleAuction.md`,
  `ACT_Delete_AuctionFromAdmin.md`,
  `domain/VAL_Schedule_Auction.md`,
  `domain/VAL_Create_Auction.md`
- Mendix pages:
  `migration_context/tests/code/src-pages-auction-adminpages-accauctionschedulepage.md`,
  `src-pages-auction-adminpages-accroundtwocriteriapage.md`
- Schema: `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`,
  `V59__auctions_bid_rounds_and_configuration.sql`,
  `V8__buyer_mgmt_core.sql` (lines 134-148 — `auctions_feature_config`)
- Current code: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AuctionService.java`,
  `controller/AuctionController.java`,
  `model/auctions/SchedulingAuction.java`
- Related ADRs (in `docs/architecture/decisions.md`):
  2026-04-19 (partially superseded by this plan),
  2026-04-18 (Snowflake sync pattern — reused for audit push),
  2026-04-13 (PWS email async pattern — template for post-commit events)
