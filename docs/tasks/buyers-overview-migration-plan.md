# Buyers Overview — Migration Plan

**Date:** 2026-04-15
**Status:** Proposed
**Owner:** Platform / Admin UI
**Route:** `http://localhost:3000/buyers` (admin-only)

---

## 1. Legacy source of truth

### Page metadata
- **File:** `migration_context/frontend/components/Pages_Page/Buyer_Overview.md`
- **Mendix page:** `AuctionUI.Buyer_Overview`
- **Allowed roles:** `AuctionUI.Administrator`, `AuctionUI.Compliance`
- **Layout:** `ecoAtm_Atlas_Default` (sidebar + header shell — matches our `(dashboard)` group)
- **Widget:** single server-paged Data grid 2, `pageSize=20`, paging at bottom, row selection by checkbox.

### Grid columns (exact order, per Mendix)
| # | Header | Source attribute | Content |
|---|--------|------------------|---------|
| 1 | Buyer Name | `Buyer.CompanyName` | `hotel` icon + company name, text filter (contains, 500 ms debounce) |
| 2 | Buyer Codes | `Buyer.BuyerCodesDisplay` | Comma-separated buyer codes string, text filter (contains) |
| 3 | Status | `Buyer.Status` | `active_rollover.png` or `Disabled_Rollover.png` image (conditional on `Active` / `Disabled` / empty), dropdown filter; row click → `ACT_OpenBuyerEditPage` |

### Toolbar actions (Active Menu Selector, Buyers menu item)
- **Send All Buyers to Snowflake** → `EcoATM_MDM.ACT_SendAllBuyerstoSnowflake`
- **Create New From Buyer** → `EcoATM_MDM.ACT_Buyer_CreateNewFromBuyer` (requires selected row)

### Schema mapping (legacy → new)
Legacy: `ecoatm_buyermanagement$buyer` (876 rows) — `migration_context/database/schema-ecoatm_buyermanagement.md:76`.
New: `buyer_mgmt.buyers` — `backend/src/main/resources/db/migration/V8__buyer_mgmt_core.sql:44`.

| Legacy column | New column | Notes |
|---|---|---|
| `id` | `buyer_mgmt.buyers.id` | Remapped during V18 migration |
| `companyname` | `company_name` | 1:1 |
| `status` | `status` | `Active` / `Disabled` |
| `buyercodesdisplay` | **Not stored** | Must compute at read time from `buyer_mgmt.buyer_code_buyers` → `buyer_codes.name` |
| `isspecialbuyer` | `is_special_buyer` | For future detail page |
| `entityowner` | `entity_owner` | For future detail page |

**Key decision:** do NOT denormalize `buyer_codes_display`. Compute it in a read projection via `string_agg(bc.name, ', ' ORDER BY bc.name)` joined through `buyer_code_buyers`. This avoids a Mendix-style stale-cache field and matches the QA grid output.

---

## 2. Current state of the target app

### Backend (what exists)
- Table: `buyer_mgmt.buyers` ✅ (V8, seeded in V18)
- DTO stub: `backend/src/main/java/com/ecoatm/salesplatform/dto/BuyerResponse.java` — only `id`, `companyName`. Needs `buyerCodesDisplay` and `status`.
- `BuyerCodeService` / `BuyerCodeLookupService` exist (buyer code selector flow) — reusable only for the code-name lookup, not for this overview.

### Backend (what's missing)
- `Buyer` JPA entity
- `BuyerRepository` (Spring Data JPA, page + filter)
- `BuyerService` (paging, filter, status transitions)
- `BuyerController` at `/api/v1/admin/buyers`
- Security mapping (Admin + Compliance roles)

### Frontend (what exists)
- `frontend/src/app/(dashboard)/buyer-select/page.tsx` — buyer **code** selector, not the Buyer Overview.
- **No `/buyers` route yet.** (The user said "already exists" but the directory is absent — this plan creates it.)
- Reusable primitives: `_shared` helpers in `admin/pws-data-center`, the users page `page.tsx` (has a similar server-paged grid pattern worth copying for layout/filter plumbing).

---

## 3. Target design

### 3.1 Backend

**Entity — `model/buyermgmt/Buyer.java`**
- Map `buyer_mgmt.buyers` columns: `id`, `companyName`, `status` (enum `BuyerStatus { ACTIVE, DISABLED }`), `isSpecialBuyer`, `createdDate`, `changedDate`, `entityOwner`.
- No JPA association to `BuyerCode` (kept shallow to avoid pulling the 655-row sales rep graph on every list query).

**DTO — update `BuyerResponse` (record)**
```java
public record BuyerResponse(
    Long id,
    String companyName,
    String buyerCodesDisplay,
    String status
) {}
```

**Repository — `BuyerRepository extends JpaRepository<Buyer, Long>, JpaSpecificationExecutor<Buyer>`**
- Custom `@Query` with a projection returning `BuyerListRow(id, companyName, status, buyerCodesDisplay)` using `string_agg` over a correlated subquery or a `LEFT JOIN` + `GROUP BY`. Prefer a native query or `@Query` with `jakarta.persistence.Tuple` since `string_agg` is PG-specific.
- Support `Pageable` + optional filters: `companyNameContains`, `buyerCodesContains`, `status`.

**Service — `BuyerService`**
- `Page<BuyerResponse> search(BuyerSearchCriteria, Pageable)`
- `void sendAllToSnowflake(actorId)` — stub that logs + returns a job id. Real Snowflake sink is Theme 2 of `todos-resolution-plan.md`; we wire the endpoint but keep the implementation a no-op behind a feature flag `buyers.snowflake.enabled=false`.
- `Long createFromBuyer(sourceBuyerId, actorId)` — deferred (return 501 until the edit page lands); document as out of scope.

**Controller — `BuyerController` at `/api/v1/admin/buyers`**
| Method | Path | Notes |
|---|---|---|
| `GET` | `/` | Query params: `page`, `size`, `companyName`, `buyerCodes`, `status`. Returns `Page<BuyerResponse>`. |
| `POST` | `/snowflake-sync` | Triggers `sendAllToSnowflake`. 202 + job id. |
| `POST` | `/{id}/clone` | 501 Not Implemented for now — reserved for `ACT_Buyer_CreateNewFromBuyer`. |

**Security** — extend `SecurityConfig` to require `ROLE_ADMIN` or `ROLE_COMPLIANCE` on `/api/v1/admin/buyers/**`. Map Mendix role names in the role-assignment migration (already loaded in V17/V18).

**Tests**
- Unit: `BuyerServiceTest` (paging, filter combinations, empty buyer-codes → empty string display).
- Slice: `BuyerControllerTest` with `@WebMvcTest` + MockMvc (auth 401, role 403, 200 with page payload).
- Integration: `BuyerRepositoryIT` with Testcontainers PG to verify `string_agg` projection against V18 seed data (assert known buyer → expected comma-joined buyer codes).
- Coverage target: ≥ 80 % on new classes.

### 3.2 Frontend

**Route — `frontend/src/app/(dashboard)/buyers/page.tsx`**
- Server component that renders a client grid. Reuse the grid pattern from `users/page.tsx` for pagination + filter plumbing so we don't fork another table shell.
- Columns in the **exact Mendix order**: Buyer Name / Buyer Codes / Status. Honor `migration_context/styling/EcoAtm.css` tokens (teal `#407874`, dark `#112d32`, background `#F7F7F7`). No modern overlays.
- Status cell renders the legacy `active_rollover.png` / `Disabled_Rollover.png` assets (copy to `frontend/public/images/buyers/` from the legacy asset dump, or re-export at the same pixel size). Do NOT substitute a modern chip.
- Row click → `/buyers/[id]` (edit page — out of scope for this migration, stub a 404 for now and link-only).
- Toolbar:
  - **Send All to Snowflake** button → `POST /api/v1/admin/buyers/snowflake-sync`, disabled unless `buyers.snowflake.enabled`.
  - **Create New From Buyer** button → disabled with tooltip "Coming with Buyer Edit page" for Phase 1.
- Filters: text input for Buyer Name (500 ms debounce to match Mendix), text input for Buyer Codes, dropdown for Status (`All / Active / Disabled`). Push state to the URL (`?companyName=…&status=Active&page=0`) per `rules/web/patterns.md` "URL As State".
- Pagination: 20 per page, bottom buttons.

**Data hook — `frontend/src/features/buyers/useBuyers.ts`**
- TanStack Query `useQuery(['buyers', criteria], () => apiFetch('/admin/buyers', …))`. Uses cookie-auth path from `docs/architecture/decisions.md#2026-04-13--auth-token-moved-from-localstorage-to-httponly-cookie`.

**No mock data** — if the backend endpoint isn't ready, the frontend PR is blocked on the backend PR landing first. (CLAUDE.md rule.)

### 3.3 QA parity check (mandatory, per CLAUDE.md)
After implementation:
1. Log into `https://buy-qa.ecoatmdirect.com/login.html` with `ashirwadmittal / Password123#`.
2. Navigate to the Buyers overview (Admin Control Center → Buyers).
3. Screenshot viewport at 1440 px.
4. Screenshot `http://localhost:3000/buyers` at 1440 px.
5. Diff: column order, header copy, row icon dimensions, filter layout, pagination footer, sidebar highlight.
6. Fix discrepancies before closing the PR.

Use Playwright MCP tools (`browser_navigate`, `browser_snapshot`, `browser_take_screenshot`) — both pages live.

---

## 4. Phased delivery

### Phase 1 — Read-only grid (MVP)
1. Backend entity + repository + service + controller + tests. Ship behind role guard.
2. Frontend `/buyers` route with filter, paging, status icons. No toolbar actions yet.
3. QA parity pass.
4. Docs: update `docs/api/rest-endpoints.md`, `docs/app-metadata/modules.md`, add ADR snippet in `docs/architecture/decisions.md` for the `buyer_codes_display` read-projection decision.

**Exit criteria:** grid matches QA for at least 5 sampled rows; filters and paging work; role guard verified; ≥ 80 % coverage on new code.

### Phase 2 — Toolbar actions (follow-up, not this PR)
- `POST /snowflake-sync` real implementation (gated by `buyers.snowflake.enabled`, wired into the Theme 2 outbox once it lands).
- `POST /{id}/clone` once the Buyer Edit page is scoped.
- Row-click navigation to `/buyers/[id]` once the edit page ships.

### Phase 3 — Buyer Edit / New page
See `docs/tasks/buyer-edit-page-plan.md`. Merges `Buyer_New`, `Buyer_Edit`, and `Buyer_Edit_Compliance` into a single `/buyers/[id]` + `/buyers/new` surface with role-derived field locks. Ports `ACT_Buyer_EditSave`, `ACT_ToggleBuyerStatus`, `ACT_BuyerCode_Create`, `ACT_SoftDelete_BuyerCode`, `Val_CheckDisablingBuyer`, and `NF_ValidateBuyerCodeBudget`. Depends on Phase 1 of this plan being merged.

---

## 5. Risks & open questions

1. **`buyer_codes_display` performance.** `string_agg` over `buyer_code_buyers` (1,383 rows) and `buyer_codes` (653 rows) for 876 buyers is cheap today, but confirm with `EXPLAIN ANALYZE` once the query is written. If it degrades, add a materialized view refreshed on buyer-code change.
2. **Role names.** Mendix roles (`Administrator`, `Compliance`) must map to the new `identity.user_roles` rows loaded in V17. Verify the names before writing `hasAnyRole(...)`.
3. **Status enum values.** Schema allows empty string (sample values show `Active`, `Disabled` only, but the Mendix `showContentAs` branches on `Active/Disabled/(empty)`). Treat null/empty as "Disabled" in the projection to avoid a missing icon.
4. **Icons.** Need to export `active_rollover.png` and `Disabled_Rollover.png` from the Mendix theme — track which asset bundle they live in before the frontend PR starts.
5. **User said `/buyers` "already exists".** It does not exist in `frontend/src/app/(dashboard)/`. Confirm with the user whether a stale branch has it, or if they meant the backend role mapping. Plan assumes we create the route.

---

## 6. File manifest (expected diff)

### New
- `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/Buyer.java`
- `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/BuyerStatus.java`
- `backend/src/main/java/com/ecoatm/salesplatform/repository/BuyerRepository.java`
- `backend/src/main/java/com/ecoatm/salesplatform/service/BuyerService.java`
- `backend/src/main/java/com/ecoatm/salesplatform/controller/BuyerController.java`
- `backend/src/main/java/com/ecoatm/salesplatform/dto/BuyerSearchCriteria.java`
- `backend/src/test/java/com/ecoatm/salesplatform/service/BuyerServiceTest.java`
- `backend/src/test/java/com/ecoatm/salesplatform/controller/BuyerControllerTest.java`
- `backend/src/test/java/com/ecoatm/salesplatform/repository/BuyerRepositoryIT.java`
- `frontend/src/app/(dashboard)/buyers/page.tsx`
- `frontend/src/app/(dashboard)/buyers/BuyersGrid.tsx`
- `frontend/src/app/(dashboard)/buyers/buyers.module.css`
- `frontend/src/features/buyers/useBuyers.ts`
- `frontend/public/images/buyers/active_rollover.png`
- `frontend/public/images/buyers/Disabled_Rollover.png`

### Changed
- `backend/src/main/java/com/ecoatm/salesplatform/dto/BuyerResponse.java` (add fields)
- `backend/src/main/java/com/ecoatm/salesplatform/security/SecurityConfig.java` (role guard)
- `docs/api/rest-endpoints.md` (new Buyers section)
- `docs/architecture/decisions.md` (projection ADR entry)
- `docs/app-metadata/modules.md` (list Buyers module surface)
