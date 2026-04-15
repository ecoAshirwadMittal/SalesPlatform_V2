# Repo Simplification Analysis

_Generated 2026-04-13. Analysis only — no files modified._

Scope: `backend/` (Spring Boot Java) + `frontend/` (Next.js TS). Excludes migrations, generated files, `node_modules`, `target/`, `.next/`, `migration_context/`.

---

## Backend

### High

- **H-1. Duplicated PWS status constants across services.** Same string literals (`"Draft"`, `"Sales_Review"`, `"Buyer_Acceptance"`, `"Ordered"`, `"Pending_Order"`, `"Declined"`, `"Accept"`, `"Counter"`, `"Decline"`) declared in three places:
  - `OfferService.java:47-70`
  - `OfferReviewService.java:34-44`
  - `CounterOfferService.java:31-44`

  Extract to a single `PwsOfferStatus` constants class.

- **H-2. `OfferService.java` is 1,062 lines** — exceeds 800-line ceiling. Handles cart mgmt, order submission, Oracle HTTP, offer-number generation, device reservation, error lookup, CSV export. Extract `OracleOrderClient` (~lines 650-850) and `OfferNumberGenerator` (~lines 940-980).

- **H-3. `loadDeviceMap` duplicated byte-for-byte** in `OfferService.java:1044-1053` and `OfferReviewService.java:449-458`.

- **H-4. Hardcoded ngrok URL in CORS allowlist.** `SecurityConfig.java:64` contains `"https://designatory-scrawnier-rocco.ngrok-free.dev"`. Move to `cors.allowed-origins` config property.

- **H-5. `GlobalExceptionHandler` detects 404 via string scan.** `GlobalExceptionHandler.java:57`:
  ```java
  if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
  ```
  Introduce typed `EntityNotFoundException` + dedicated `@ExceptionHandler`.

### Medium

- **M-1.** 29 uses of `Collectors.toList()` — migrate to Java 21's `Stream.toList()`.
- **M-2.** Inconsistent DTO style: `BuyerCodeResponse`, `LoginResponse`, `DirectUserDetailResponse` use Lombok `@Data`; `DeviceResponse` (93 lines), `OfferItemResponse` (123), `RmaResponse` (85), `RmaItem` (120), `OracleConfig` (63), `Order` (147), `Rma` (212) use hand-written getters/setters. `Rma` alone has 90 lines of boilerplate for 16 fields. Convert pure-read DTOs to records; add `@Getter @Setter` to entities.
- **M-3.** `OracleConfigController.java:173-184` — inner public-field DTO class. Extract to record in `dto/`.
- **M-4.** `OracleConfigController.java:32` constructs `new ObjectMapper()` as a field — bypasses Spring Boot Jackson config. Inject the shared bean.
- **M-5.** `OfferService.generateOfferNumber:960-976` — upsert + separate select; collapse via PostgreSQL `INSERT ... ON CONFLICT ... RETURNING`.
- **M-6.** `AuthService.buildUserInfo:106-120` — nested ternary cascade for initials. Refactor to early returns.
- **M-7.** `BuyerCodeService` uses raw `EntityManager.createNativeQuery` where `EcoATMDirectUserRepository` could host the queries (enables uniform caching).
- **M-8.** `RmaResponse.fromEntity:37-43` — seven `!= null ? x : 0/BigDecimal.ZERO` guards. Fix nullable columns with DB defaults + primitive fields.
- **M-9.** `OfferReviewService.getStatusSummaries:72-104` — 30-line LinkedHashMap-then-loop. Collapse into a stream with reduce.
- **M-10.** `ApiLog.java` entity has no getters — write-only. Replace with `JdbcTemplate` insert or add `@Getter`.

### Low

- **L-1.** `@EnableScheduling` in `SalesPlatformApplication` only feeds `SessionCleanupTask`; verify it's not dead code on stateless JWT.
- **L-2.** `BuyerCodeLookupService.findCodeAndCompanyByIds:54-70` — unused.
- **L-3.** `OfferItemRepository.findByOffer(Offer)` — unused; services call `findByOfferId` instead.
- **L-4.** `PWSAdminController.java:97-102, 144-149` — insert then redundantly re-select. Use `RETURNING *`.
- **L-5.** `SecurityConfig.java:66-67` — `Arrays.asList` → `List.of` (inconsistent with line 62).

---

## Frontend

### High

- **F-H-1. Session helpers copy-pasted across pages.** `getBuyerCodeId()` / `getUserId()` duplicated in:
  - `cart/page.tsx:51-67`
  - `counter-offers/page.tsx:22-31`
  - `order/page.tsx:95-113`
  - `orders/page.tsx:63-78`

  Extract to `src/lib/session.ts`. `order/page.tsx:641,693` also has two inline IIFEs re-reading `sessionStorage`.

- **F-H-2. Oversized page files.** `pricing/page.tsx` = 762 lines; `order/page.tsx` = 1,046 lines (exceeds 800). Extract upload modal, price-history modal, future-date modal (pricing); inventory grid, cart section, submit/almost-done modals (order).

- **F-H-3. Manual debounce pattern repeated.** `pricing/page.tsx:111-138` (twice) and `inventory/page.tsx:69-74` use `useRef<ReturnType<typeof setTimeout>>` + effect. Extract `useDebounce` hook.

### Medium

- **F-M-1.** `API_BASE` redeclared in 17 files with inconsistent values (`/api/v1`, `/api/v1/pws`, `/api/v1/admin`). Centralize in `src/lib/apiRoutes.ts`.
- **F-M-2.** `console.error` swallowed with no user-facing error state across `pricing/page.tsx:167,204,312,372,397,415`, `orders/page.tsx:104,125`, `cart/page.tsx:130,200,213,232`, `inventory/page.tsx:62`.
- **F-M-3.** `(dashboard)/layout.tsx:130` — `const [collapsed] = useState(false)` setter destructured away; never mutated.
- **F-M-4.** `PageResponse<T>` interface declared locally in `orders/page.tsx:32`, `pricing/page.tsx:29`, `(dashboard)/users/page.tsx:19`. Move to `src/lib/types.ts`.
- **F-M-5.** `LoginForm.tsx:34` uses bare `fetch` with manual `credentials: 'include'` instead of shared `apiFetch`.
- **F-M-6.** `pws/layout.tsx:170,198,215,242` re-reads and re-parses `sessionStorage.getItem('selectedBuyerCode')` four times in the same component.

### Low

- **F-L-1.** `order/page.tsx:641,693` — inline IIFE sessionStorage reads for `.code` (file already has `getBuyerCodeId` for `.id` at line 95).
- **F-L-2.** `orders/page.tsx:133,145` — two `eslint-disable-next-line react-hooks/exhaustive-deps` suppressions.
- **F-L-3.** `AuthUser` interface duplicated in `pws/layout.tsx:142-150` and `(dashboard)/layout.tsx:118-125`.
- **F-L-4.** `NavItem` interface duplicated in both layouts with slight field differences.

---

## Progress — 2026-04-13

- **H-1 DONE** — `PwsOfferStatus` constants class in `model/pws/`; all three services point at it.
- **H-3 DONE** — `OfferItemDeviceLoader` component; `OfferService` + `OfferReviewService` delegate to it.
- **H-4 DONE** — ngrok origin removed; `cors.allowed-origins` property in `application.yml` (override via `CORS_ALLOWED_ORIGINS`).
- **H-5 DONE** — typed `EntityNotFoundException` + dedicated handler. Legacy string-scan kept as fallback until call sites migrate.
- **F-H-1 DONE** — `src/lib/session.ts`; cart, counter-offers, order, orders pages import from it.
- **F-H-3 DONE** — `src/lib/useDebounce.ts`; pricing + inventory pages use the hook (four ad-hoc timer blocks removed).
- **H-2 DEFERRED** — `OfferService` still 1,062 lines. Split into `OracleOrderClient` + `OfferNumberGenerator` is a large surgical refactor; tackle separately with dedicated tests.
- **F-H-2 DEFERRED** — `pricing/page.tsx` and `order/page.tsx` splits. Deferred with H-2 for the same reason.
- **M-1 DONE** — 29 `.collect(Collectors.toList())` → `.toList()` across 9 service/controller files; 7 unused `Collectors` imports removed. `mvn compile` green.

Backend `mvn clean compile` and frontend `tsc --noEmit` both green after these changes.

## Progress — 2026-04-14 (Phase 1: frontend cleanup cluster)

- **F-M-1 DONE** — `src/lib/apiRoutes.ts` exports shared `API_BASE`. All 17 pages that previously redeclared `const API_BASE` now import it; suffixed bases (`/pws`, `/pws/rma`, `/pws/offer-review`, `/admin`) derive a local `BASE` const from `API_BASE` so call sites stay readable.
- **F-M-4 DONE** — `src/lib/types.ts` owns `PageResponse<T>`, `NavItem`, `SubNavItem`. `orders/page.tsx`, `pricing/page.tsx`, `(dashboard)/layout.tsx`, and `pws/layout.tsx` import from it. `users/page.tsx` keeps its custom (non-Spring) page envelope local.
- **F-L-3 DONE** — `AuthUser` interface centralized in `src/lib/session.ts` with the full profile shape (firstName, lastName, fullName, email, initials, roles?). Both layouts import from there; duplicate interfaces removed.
- **F-L-4 DONE** — shared `NavItem` in `src/lib/types.ts` (expandable/children optional so PWS and dashboard layouts both satisfy it).
- **F-M-3 DONE** — removed dead `const [collapsed] = useState(false)` from `(dashboard)/layout.tsx`; unwrapped three `!collapsed &&` guards that were always-true.
- **F-M-5 DONE** — `LoginForm.tsx` switched from bare `fetch('/api/v1/auth/login', { credentials: 'include' })` to `apiFetch(\`${API_BASE}/auth/login\`)`.
- **F-M-6 DONE** — `pws/layout.tsx` no longer re-parses `sessionStorage.getItem('selectedBuyerCode')` inline. Initial state, auto-select branch, and the `buyerCodeChanged` handler all go through `getSelectedBuyerCode()` from `session.ts`.
- **F-L-1 DONE** — `order/page.tsx:624,676` inline IIFEs replaced with `getSelectedBuyerCode()?.code ?? null`.

`tsc --noEmit --pretty false` green after Phase 1.

## Progress — 2026-04-14 (Phase 2: frontend error surfacing)

- **F-M-2 DONE** — swallowed `console.error` replaced with user-visible errors across pricing/orders/cart/inventory.
  - New `src/lib/errors.ts` exports `getErrorMessage(err, fallback)` — narrows `unknown` caught values to a displayable string.
  - New `src/components/ErrorBanner.tsx` — small dismissible red banner. Style-inline so pages without a shared CSS module can drop it in.
  - `pricing/page.tsx`: 6 sites wired (load, save future date, save updates, export, upload CSV, price history) → `errorMsg` state + `<ErrorBanner>`.
  - `orders/page.tsx`: 2 sites wired (load orders, load counts) → `errorMsg` state + `<ErrorBanner>`.
  - `cart/page.tsx`: 4 sites wired (load, remove item, reset, export) → existing `saveError` state + existing banner.
  - `inventory/page.tsx`: 1 site wired (load inventory) → `errorMsg` state + `<ErrorBanner>`.
- **F-L-2 DONE** — `orders/page.tsx` three `// eslint-disable-next-line react-hooks/exhaustive-deps` suppressions removed by consolidating the mount/tab/page effects into two proper-dep effects (`[activeTab, page, fetchOrders]` and `[fetchCounts]`); `handleTabClick` no longer calls the fetchers directly — the effect picks it up from the state change.

`tsc --noEmit --pretty false` green after Phase 2.

## Progress — 2026-04-14 (Phase 3: backend DTO/entity normalization)

- **M-2 PARTIAL** — boilerplate getter/setter blocks replaced with Lombok across four hand-written DTOs:
  - `DeviceResponse` (93 → 75 lines): `@Getter @Setter`. Tests still call `r.getSku()` / `r.setSku()` so no test churn.
  - `OfferItemResponse` (123 → 96 lines): `@Getter @Setter`.
  - `RmaResponse` (85 → 60 lines): `@Getter @Setter`. The two enrichment setters (`setBuyerName`/`setCompanyName`) are now generated by Lombok and `RmaService` continues to call them with the same signature.
  - `RmaItemResponse` (63 → 46 lines): `@Getter @Setter`. Same enrichment pattern.
  - **Deferred:** entity-level conversion for `Rma` (212 lines), `RmaItem` (120), `Order` (147), `OracleConfig` (63). JPA entities are higher-risk to retrofit because Hibernate uses the field/property accessors directly — needs separate ORM verification pass.
- **M-3 DONE** — `OracleConfigController.OracleConfigDto` inner public-field class extracted to `dto/OracleConfigDto.java` as a record. Controller swapped `dto.field` access for `dto.field()` accessor calls.
- **M-4 DONE** — `OracleConfigController` no longer creates `new ObjectMapper()` as a field. Spring's shared bean is constructor-injected via Lombok `@RequiredArgsConstructor` (added `private final ObjectMapper objectMapper`). Inherits the project's Jackson configuration automatically.
- **M-10 DONE** — `ApiLog` entity now has `@Getter @Setter`. Was previously write-only with no accessors at all; this makes the table usable from any future writer without touching the entity again.
- **M-8 DEFERRED** — `RmaResponse.fromEntity` null-guard cluster requires DB column defaults + primitive field migration. Schema-touching change with non-trivial migration risk; tackle in a dedicated pass alongside any Rma read-path work.

`mvn test` green after Phase 3 — 444 tests pass, 0 failures.

## Progress — 2026-04-14 (Phase 4: backend polish)

- **M-5 DONE** — `OfferService.generateOfferNumber` now collapses the upsert + read-back into a single `INSERT ... ON CONFLICT ... DO UPDATE ... RETURNING max_sequence` round-trip. `OfferServiceTest.stubGenerateOfferNumber` mock router collapsed from 4 query stubs to 3 (no separate `max_sequence` SELECT). `NativeQuerySmokeIT` consolidated the two `offer_id_sequence` smoke tests into one `offerIdSequenceUpsertReturning`.
- **M-6 DONE** — `AuthService.buildUserInfo` initials cascade extracted to `private static String computeInitials(...)` with early returns. Behavior unchanged; the call site is now one line.
- **L-3 DONE** — removed unused `OfferItemRepository.findByOffer(Offer)`. Production code already uses `findByOfferId(Long)`. `Offer` import retained for `findByOfferAndSku` / `deleteByOffer`.
- **L-4 DONE** — `PWSAdminController.getPWSConstants` and `getMaintenanceMode` collapse the seed-INSERT + re-SELECT into a single `INSERT ... RETURNING *` via `jdbc.queryForList`. `PWSAdminControllerTest` updated to verify `queryForList(contains("INSERT ..."))` instead of `update(...)`.
- **L-1 KEEP** — `@EnableScheduling` is real: `SessionCleanupTask` runs hourly against `identity.sessions` (a real table; auth is JWT but sessions table backs other flows). No change.
- **L-5 NOOP** — `SecurityConfig.java:65-67` already uses `List.copyOf` / `List.of`. The original observation was stale.
- **L-2 KEEP** — `BuyerCodeLookupService.findCodeAndCompanyByIds` is actually called by `RmaService:387`. The original observation was stale; method is in active use.
- **M-9 DEFERRED** — `OfferReviewService.getStatusSummaries` cleanup. Current shape is readable and accumulates totals + builds list in one pass; collapsing to a stream/reduce trades a 30-line loop for less obvious code. Not worth the churn.
- **M-7 DEFERRED** — Moving `BuyerCodeService` raw native queries into `EcoATMDirectUserRepository` is a wider refactor that needs ORM verification; not in scope for the polish pass.

`mvn test` green after Phase 4 — 444 tests pass, 0 failures.

## Progress — 2026-04-14 (Phase 5: OfferService split)

- **H-2 PARTIAL** — `OfferService` 1,046 → 868 lines (−178). Two collaborators extracted, both unit-testable in isolation:
  - **`OracleOrderClient`** (new, 179 lines) — owns the full Oracle ERP HTTP surface: config read, toggle-off simulated response, CWS_PostToken call, CWS_PostCreateOrder call, response parsing (camelCase + PascalCase). Public API: `OracleResponse submitOrder(String jsonPayload)`. Never throws — failures surface as an `OracleResponse` with `returnMessage` set. Holds `@Value oracle.username` / `oracle.password`, `OracleConfigRepository`, `ObjectMapper`.
  - **`OfferNumberGenerator`** (new, 61 lines) — owns `ACr_UpdateOfferID` parity. Public API: `String next(Long buyerCodeId)`. Wraps the atomic `INSERT ... ON CONFLICT ... RETURNING` from Phase 4. Holds `EntityManager` + `BuyerCodeLookupService`.
  - `OfferService` loses `oracleUsername`/`oraclePassword` `@Value` fields, `OracleConfigRepository` dep, and the four extracted methods (`sendOrderToOracle`, `fetchOracleToken`, `postCreateOrder`, `generateOfferNumber`). All three call sites (`submitCart`, `submitOffer`, `submitOrder`) now delegate.
- **Tests** — `OfferServiceTest` rewired:
  - Dropped `OracleConfigRepository` mock + 6 `when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList())` stubs.
  - Added `@Mock OracleOrderClient` + `@Mock OfferNumberGenerator` with setUp defaults: `offerNumberGenerator.next(anyLong()) -> "BC00126001"` and `oracleOrderClient.submitOrder(any()) -> simulatedOracleSuccess()`. Uses `any()` not `anyString()` because the mocked `ObjectMapper.writeValueAsString` returns null.
  - `stubGenerateOfferNumber(String)` renamed to `stubResolveUserName()` and simplified — only routes `identity.users` now that the offer number generation and buyer_codes query paths are out of scope. All 11 call sites migrated.
- **Tests — still green:** `mvn clean test` 444/444 pass, 0 failures.
- **Follow-up (deferred):** H-2 not fully resolved — OfferService still 868 > 800 target. Further trims would extract `prepareOraclePayload` (~80 lines) and `handleOracleResponse` (~90 lines), but those carry workflow state (offer mutation, event publishing, drawer-status updates) and belong with the orchestrator. Tackling them would mean introducing an `OrderSubmissionWorkflow` collaborator, which is a separate refactor with different risk profile. Noting as Phase 5b candidate if the 800 ceiling becomes load-bearing.
- **Follow-up (deferred):** `OracleOrderClientTest` with `MockRestServiceServer` not yet written. Current coverage is via `OfferServiceTest` exercising the mocked client; the HTTP layer itself has no direct unit test. Low priority because the extracted code is a 1:1 move with no behavior change — add the dedicated test when Oracle integration is re-touched.

---

## Top 5 Recommended Changes

Priority by impact-to-effort ratio:

1. **Extract `PwsOfferStatus` constants** (H-1) — one new file; eliminates silent drift risk across the most critical business logic.
2. **Create `src/lib/session.ts` + `src/lib/types.ts`** (F-H-1, F-M-1, F-M-4, F-L-3) — kills the biggest frontend duplication cluster in one pass.
3. **Introduce typed `EntityNotFoundException`** (H-5) — removes fragile string-scan 404 routing.
4. **Add `useDebounce` hook** (F-H-3) — removes four identical timer blocks, shrinks `pricing/page.tsx` toward 800-line target.
5. **Surface errors + drop dead `collapsed` state** (F-M-2, F-M-3) — quick observability wins.
