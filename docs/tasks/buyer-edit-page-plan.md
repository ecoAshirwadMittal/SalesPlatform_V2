# Buyer Edit / New Page — Migration Plan

**Date:** 2026-04-15
**Status:** Phase 3.3 implemented (complete)
**Depends on:** `docs/tasks/buyers-overview-migration-plan.md` Phase 1 (read-only grid) merged
**Routes:** `/buyers/new`, `/buyers/[id]` (admin + compliance, role-gated behavior)

---

## 1. Legacy sources of truth

Three Mendix pages collapse into **one** page in the new app with role-driven field locking:

| Mendix page | Role | Key difference | File |
|---|---|---|---|
| `Buyer_New` | Administrator | No status toggle (new buyers always `Active`); no sales-rep dropdown lock | `migration_context/frontend/components/Pages_Page/Buyer_New.md` |
| `Buyer_Edit` | Administrator | Full edit: status toggle, sales rep, special-buyer flag, buyer codes (create, type, budget, soft-delete); two save paths (`EditSave_Admin`, `EditSave_Compliance`) | `migration_context/frontend/components/Pages_Page/Buyer_Edit.md` |
| `Buyer_Edit_Compliance` | Compliance (+ most other roles) | Sales-rep dropdown **read-only unless Admin** (`editable if $UserRoleAdmin != empty`); buyer-code Type dropdown read-only except when `isNew`; budget editable; single save (`ACT_Buyer_EditSave`) | `migration_context/frontend/components/Pages_Page/Buyer_Edit_Compliance.md` |

**Decision:** merge into a single `/buyers/[id]` page with server-derived `canEditSalesRep`, `canToggleStatus`, `canEditBuyerCodeType` flags based on the caller's role. Avoids cloning three React pages that are 90 % identical.

### Legacy widget inventory (union of the three pages)
- `Hotel` icon (edit) / `user_plus` icon (new) — header
- "Added" / Status label + clickable toggle → `AuctionUI.ACT_ToggleBuyerStatus` (edit only)
- Status dropdown (`enum_BuyerStatus`: `Active`, `Disabled`) — admin-editable in Edit_Compliance
- "Apply special buyer treatment" switch → `Buyer.isSpecialBuyer`
- Sales rep combo box — associations via `Buyer_SalesRepresentative`, caption `SalesRepFirstName + ' ' + SalesRepLastName`, checkbox multi-select
- Buyer-codes data grid with inline editing:
  | Column | Source | Editable | Notes |
  |---|---|---|---|
  | Buyer Codes | `BuyerCode.Code` | text input | Class `not-unique-buyer-code` when `codeUniqueValid = false` |
  | Type | `BuyerCode.BuyerCodeType` | dropdown | Editable only when `isNew($currentObject)` (enum `Wholesale`, `Data_Wipe`, `Purchasing_Order_Data_Wipe`, `Purchasing_Order`); hidden when `softDelete = true` |
  | Budget ($) | `BuyerCode.Budget` | numeric input | Validated on Enter via `NF_ValidateBuyerCodeBudget` |
  | Delete | — | button | `Buyer_New` → hard delete; `Buyer_Edit*` → `ACT_SoftDelete_BuyerCode` |
- Footer buttons:
  - **Add Buyer Code** → `ACT_BuyerCode_Create` (creates a row with `Status=Active`, `BuyerCode_Buyer=$Buyer`)
  - **Cancel Changes** → discard
  - **Save** → role-dependent microflow:
    - `Buyer_New` → `ACT_Buyer_NewSave` (or `ACT_Buyer_SaveBuyerFromUser` if launched from the user edit flow — deferred)
    - `Buyer_Edit` Admin button → `ACT_Buyer_EditSave_Admin`
    - `Buyer_Edit` Compliance button → `ACT_Buyer_EditSave_Compliance`
    - `Buyer_Edit_Compliance` save → `ACT_Buyer_EditSave`

### Legacy microflow behavior (canonical logic)

**`ACT_Buyer_EditSave`** (`migration_context/backend/ACT_Buyer_EditSave.md`)
1. `Val_CheckDisablingBuyer($Buyer)` — guard rail, aborts save if a disabled buyer still has active buyer codes in open auctions.
2. `SUB_Buyer_Save($Buyer)` — persistence; `false` short-circuits the whole flow.
3. On success: push buyer to Snowflake (`SUB_SendBuyerToSnowflake`) and close the popup.
4. Return boolean (true = saved).

**`ACT_ToggleBuyerStatus`** (`migration_context/backend/ACT_ToggleBuyerStatus.md`) — flip between `Active` and `Disabled`. No side effects beyond the status update; the disable guard lives in `Val_CheckDisablingBuyer`, which runs on save.

**`ACT_BuyerCode_Create`** — create a `BuyerCode` with `Status=Active`, associated to `$Buyer`. The code/type/budget are filled inline before save.

**`ACT_SoftDelete_BuyerCode`** — set `soft_delete = true`; no cascade. Hard delete is only used from `Buyer_New` where the buyer codes haven't been persisted yet.

**`NF_ValidateBuyerCodeBudget`** — nanoflow that validates budget on Enter. Need to re-read the file to confirm exact rule (likely `>= 0` and numeric); plan locks it as a TODO for the implementer.

---

## 2. Current state of the target app

### Backend — exists
- Tables: `buyer_mgmt.buyers`, `buyer_mgmt.buyer_codes`, `buyer_mgmt.buyer_sales_reps`, `buyer_mgmt.sales_representatives` (V8, seeded V18).
  - `buyer_codes` already has `code`, `buyer_code_type`, `status`, `budget`, `soft_delete`.
- `BuyerCodeService` / `BuyerCodeLookupService` — reusable for code lookups, not for edit flow.
- Phase 1 read-only surface (from the Buyers Overview plan): `Buyer` entity, `BuyerRepository`, `BuyerService.search`, `BuyerController GET /api/v1/admin/buyers`.

### Backend — missing
- `BuyerDetailResponse` (full aggregate: buyer fields + sales-rep IDs + buyer codes).
- `BuyerUpsertRequest` / `BuyerCodeUpsertRequest` DTOs.
- `BuyerEditService` (the Mendix `SUB_Buyer_Save` + `Val_CheckDisablingBuyer` surface).
- Endpoints: `GET /{id}`, `POST /` (new), `PUT /{id}` (edit), `PATCH /{id}/status` (toggle), buyer-code sub-resource.
- Snowflake sync on save — re-uses the `buyers.snowflake.enabled` flag from Phase 2 of the overview plan.

### Frontend — exists
- `(dashboard)/buyers` list route (after overview Phase 1).
- No `/buyers/[id]` or `/buyers/new` route yet.

---

## 3. Target design

### 3.1 Backend

**DTOs (records)**
```java
public record BuyerDetailResponse(
    Long id,
    String companyName,
    BuyerStatus status,
    boolean isSpecialBuyer,
    List<SalesRepSummary> salesReps,        // id, firstName, lastName
    List<BuyerCodeDetail> buyerCodes,       // id, code, type, budget, softDelete, isUniqueValid
    BuyerPermissions permissions            // canEditSalesRep, canToggleStatus, canEditBuyerCodeType
) {}

public record BuyerUpsertRequest(
    String companyName,                     // required, trimmed, 1–200 chars
    BuyerStatus status,                     // null on create, defaults Active
    boolean isSpecialBuyer,
    List<Long> salesRepIds,                 // absolute set (replaces existing)
    List<BuyerCodeUpsertRequest> buyerCodes // absolute set; unmatched rows soft-deleted
) {}

public record BuyerCodeUpsertRequest(
    Long id,                  // null = create
    String code,              // required
    String buyerCodeType,     // enum: Wholesale | Data_Wipe | Purchasing_Order_Data_Wipe | Purchasing_Order
    Integer budget,           // nullable, >= 0 per NF_ValidateBuyerCodeBudget
    boolean softDelete
) {}
```

**Service — `BuyerEditService`**
- `BuyerDetailResponse get(Long id, Authentication auth)` — populate `permissions` from role.
- `BuyerDetailResponse create(BuyerUpsertRequest req, Authentication auth)` — admin only. Creates buyer + associations + buyer codes in one tx. Status forced to `Active`.
- `BuyerDetailResponse update(Long id, BuyerUpsertRequest req, Authentication auth)` — runs:
  1. **Disable guard** (port of `Val_CheckDisablingBuyer`): if incoming `status == Disabled` and any `buyer_codes` row has `status = Active` **and** is referenced by an open scheduling auction (`qualifiedbuyercodes_schedulingauction` → new `buyer_mgmt.qbc_*` tables from V23), reject with `409 CONFLICT` + message listing blocking auctions.
  2. **Field-level auth**: if caller lacks `ROLE_ADMIN`, ignore `salesRepIds` changes, buyer-code `Type` changes on existing rows, and status flips. Don't throw — silently drop per Mendix parity (the fields are hidden/disabled in the UI, but API must still be safe).
  3. Persist via one tx (`@Transactional`): buyer fields, sales-rep join table diff, buyer-code upserts (new rows created, existing updated, omitted-from-payload rows soft-deleted).
  4. Post-commit event → `PwsBuyerSnowflakeEvent` handled by `BuyerSnowflakeListener` (AFTER_COMMIT, @Async, gated by `buyers.snowflake.enabled`), mirroring the pattern from `docs/architecture/decisions.md#2026-04-13--pws-email-delivery...`. Don't block the save.
- `BuyerDetailResponse toggleStatus(Long id, Authentication auth)` — admin only; runs the disable guard if flipping to Disabled. Lightweight endpoint for the inline "Added" toggle.

**Controller — `BuyerController` (extends Phase 1)**
| Method | Path | Roles | Notes |
|---|---|---|---|
| `GET` | `/{id}` | Admin, Compliance | Returns `BuyerDetailResponse` |
| `POST` | `/` | Admin | Create; body `BuyerUpsertRequest`; 201 + Location |
| `PUT` | `/{id}` | Admin, Compliance | Full update; field-level auth in service |
| `PATCH` | `/{id}/status` | Admin | Toggles status; body `{ "status": "Active" \| "Disabled" }` or empty to flip |
| `POST` | `/{id}/buyer-codes` | Admin, Compliance | Convenience append (optional — the PUT supports it) |
| `DELETE` | `/{id}/buyer-codes/{codeId}` | Admin, Compliance | Soft delete; sets `soft_delete = true` |

**Validation**
- Bean Validation on DTOs (`@NotBlank companyName`, `@Size(max=200)`, `@PositiveOrZero budget`).
- Uniqueness: `code` must be unique across active buyer codes (not soft-deleted). Surface as `codeUniqueValid=false` on the response row so the UI can render the `not-unique-buyer-code` class for parity.
- Enum parsing for `buyerCodeType` with a 400 on unknown values.

**Tests**
- Unit: `BuyerEditServiceTest` — disable guard (pass/fail), field-level role filtering, code diff (add/update/omit→soft-delete), Snowflake event published exactly once per successful save.
- Slice: `BuyerControllerTest` — 401/403 matrix across admin and compliance, 409 on disable-with-active-auction, 201 on create.
- Integration: `BuyerEditServiceIT` (Testcontainers PG) — full update round-trip using V18 seed buyers; verify join-table diff and soft-delete behavior.
- Coverage target: ≥ 80 %.

### 3.2 Frontend

**Routes**
- `frontend/src/app/(dashboard)/buyers/new/page.tsx` — admin only (middleware 403 otherwise).
- `frontend/src/app/(dashboard)/buyers/[id]/page.tsx` — admin + compliance.

Both render the same `<BuyerEditForm>` client component, differing only in the `mode` prop (`"new" | "edit"`) and the role-derived `permissions` object pulled from the `GET /{id}` response or from `/auth/me` on create.

**Components**
- `BuyerEditForm.tsx` — React Hook Form + Zod schema mirroring `BuyerUpsertRequest`. Manages dirty state so Cancel re-fetches. Submits to `POST /` or `PUT /{id}`.
- `BuyerCodeRowsEditor.tsx` — editable table (not the users-page shell — this one is inline editing, more like `admin/pws-data-center/devices` price-grid). Handles:
  - Inline code / type / budget edits
  - Add row (locally, only persisted on save)
  - Delete row (new → remove from local state; existing → flag `softDelete=true`, row greys out, stays visible until save to match Mendix)
  - `not-unique-buyer-code` class on rows where `codeUniqueValid=false` from the server
- `SalesRepMultiSelect.tsx` — checkbox multi-select via shadcn `Command` + `Popover`, captioned `firstName lastName`, loads options from `GET /api/v1/admin/sales-representatives` (new endpoint — trivial list, add to Phase 1 list if cheaper).
- `BuyerStatusToggle.tsx` — inline "Added / Disabled" clickable label that calls `PATCH /{id}/status`. Disabled unless `permissions.canToggleStatus`. On 409, show a toast with the blocking auctions list from the error body.
- `SpecialBuyerSwitch.tsx` — plain switch wired into the form state.

**Layout & styling**
- Popup layout (Mendix `ecoATM_Popup_Layout`) → modal dialog over the list, **or** a full `/buyers/[id]` page. Pick **full page**: cleaner deep links, matches how the users edit page is already built, avoids nested-state bugs from modal + form + table.
- Icon: `hotel` for edit header, `user_plus` for new. Copy from the legacy theme into `frontend/public/images/buyers/` alongside the status icons from Phase 1.
- Tokens per `migration_context/styling/EcoAtm.css`. No modern overlays.

**QA parity**
- Admin view: `https://buy-qa.ecoatmdirect.com/login.html` → Buyers → open one → screenshot at 1440.
- Compliance view: log out, log in with a compliance account (need QA creds — flag as open question if not available).
- Local screenshots of `/buyers/new` and `/buyers/[id]` for both role variants. Diff header, switches, sales-rep dropdown lock state, buyer-code grid inline editors, save button placement.

---

## 4. Phased delivery

### Phase 3.1 — Read path + compliance edit
1. Backend: `BuyerEditService.get` + `update` (compliance-safe subset — no sales-rep mutation, no status flip). `BuyerController GET` + `PUT`.
2. Frontend: `/buyers/[id]` route for compliance. Form is read-mostly — only budget, code list, and special-buyer flag are editable.
3. Tests + QA parity (compliance role).
4. Exit: compliance user can edit a real seeded buyer end-to-end and the change persists + passes QA screenshot diff.

### Phase 3.2 — Admin create + status toggle
1. Backend: `create`, `toggleStatus`, disable guard (`Val_CheckDisablingBuyer` port). Wire sales-rep diff.
2. Frontend: `/buyers/new` route + inline status toggle on edit page + sales-rep multi-select.
3. Tests — the disable guard needs a seeded auction to assert the 409 path; add a small fixture migration under `src/test/resources/db/fixtures/` or use a Testcontainers bootstrap.
4. Exit: admin can create a buyer, toggle a buyer between Active/Disabled, and hit the guard when disabling a buyer with open-auction codes.

### Phase 3.3 — Snowflake sync + row-click wiring
1. Backend: `BuyerSnowflakeListener` (AFTER_COMMIT @Async) gated by `buyers.snowflake.enabled`. Same event pattern as PWS emails.
2. Frontend: wire row-click on the `/buyers` list to `/buyers/[id]`. Enable "Create New From Buyer" toolbar action (it's just `POST /` with a pre-filled form state from the source buyer).
3. Exit: full Mendix parity for the Administrator and Compliance Buyer Edit surfaces.

---

## 5. Risks & open questions

1. **`Val_CheckDisablingBuyer` body not yet read.** The plan assumes "no active code in an open scheduling auction" — implementer MUST read `migration_context/backend/Val_CheckDisablingBuyer.md` (or grep for it) before coding Phase 3.2. If the rule is stricter, the service and 409 payload change.
2. **`NF_ValidateBuyerCodeBudget` exact rule** unread — the plan assumes `budget >= 0`. Confirm and tighten the Zod + Bean Validation rules.
3. **Compliance role in the new app.** Phase 1 of the overview plan already needs a `ROLE_COMPLIANCE` mapping; Phase 3 depends on it being real. If the migration hasn't seeded a compliance user, add one to `V15__seed_dev_roles_and_users.sql` so QA parity testing is possible locally.
4. **Sales-rep options endpoint.** `GET /api/v1/admin/sales-representatives` does not exist yet. Small addition — either ship it in Phase 3.1 (cheap) or carve a mini-PR before.
5. **Buyer-code uniqueness scope.** The Mendix `codeUniqueValid` expression's scope isn't documented here. Default to "unique among non-soft-deleted codes platform-wide" and confirm against QA before closing Phase 3.2.
6. **`ACT_Buyer_SaveBuyerFromUser`** — launched from the user-edit flow, not from Buyer_New directly. Deferred until the User management migration lands; call out in `docs/tasks/todos-resolution-plan.md` as a cross-feature follow-up.
7. **Field-level auth vs. 403.** Plan chooses silent-drop for non-admin-only fields to match Mendix's hide-or-disable UX. Alternative: return 403 with the offending fields listed. Silent-drop is cheaper and matches parity; revisit if an audit rule forbids it.
8. **Create-from-buyer clone semantics.** The Mendix `ACT_Buyer_CreateNewFromBuyer` copies a buyer but the exact fields it carries over (sales reps? codes? special-buyer flag?) need verification against the microflow file before Phase 3.3.

---

## 6. File manifest (expected diff)

### New — backend
- `backend/src/main/java/com/ecoatm/salesplatform/dto/BuyerDetailResponse.java`
- `.../dto/BuyerUpsertRequest.java`
- `.../dto/BuyerCodeUpsertRequest.java`
- `.../dto/BuyerCodeDetail.java`
- `.../dto/BuyerPermissions.java`
- `.../dto/SalesRepSummary.java`
- `.../service/BuyerEditService.java`
- `.../service/BuyerDisableGuard.java` (port of `Val_CheckDisablingBuyer`)
- `.../events/BuyerSnowflakeEvent.java`
- `.../events/BuyerSnowflakeListener.java`
- `.../test/.../service/BuyerEditServiceTest.java`
- `.../test/.../service/BuyerDisableGuardTest.java`
- `.../test/.../controller/BuyerControllerEditTest.java`
- `.../test/.../integration/BuyerEditServiceIT.java`

### New — frontend
- `frontend/src/app/(dashboard)/buyers/new/page.tsx`
- `frontend/src/app/(dashboard)/buyers/[id]/page.tsx`
- `frontend/src/features/buyers/BuyerEditForm.tsx`
- `frontend/src/features/buyers/BuyerCodeRowsEditor.tsx`
- `frontend/src/features/buyers/SalesRepMultiSelect.tsx`
- `frontend/src/features/buyers/BuyerStatusToggle.tsx`
- `frontend/src/features/buyers/useBuyerDetail.ts`
- `frontend/src/features/buyers/buyerSchema.ts` (Zod)
- `frontend/src/features/buyers/buyers.module.css`
- `frontend/public/images/buyers/hotel.svg`
- `frontend/public/images/buyers/user_plus.svg`

### Changed
- `backend/.../controller/BuyerController.java` (adds GET/POST/PUT/PATCH)
- `backend/.../repository/BuyerRepository.java` (eager fetch for detail; sales-rep + code joins)
- `backend/.../security/SecurityConfig.java` (method-level `@PreAuthorize` on edit endpoints)
- `frontend/src/app/(dashboard)/buyers/BuyersGrid.tsx` (row click → `/buyers/[id]`; enable toolbar actions)
- `docs/api/rest-endpoints.md` (append Buyers edit endpoints)
- `docs/architecture/decisions.md` (ADR entry: single edit page with role-derived field locks vs. three pages)
- `docs/app-metadata/modules.md` (mark Buyers surface complete)
