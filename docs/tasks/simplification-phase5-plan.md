# Simplification — Remaining Work Plan (Phases 5–9)

_Created 2026-04-14. Source: `docs/tasks/simplification-analysis.md` (Phases 1–4 complete)._

**Status — 2026-04-14:** Phase 5 PARTIAL shipped — `OracleOrderClient` (179 lines) + `OfferNumberGenerator` (61 lines) extracted; OfferService 1,046 → 868 lines; 444/444 tests green. Phase 6 PARTIAL shipped — `pricing/page.tsx` 741 → 458 (extracted `PricingGrid`, `PriceHistoryModal`, `FutureDateModal`, `UploadCsvModal`); `order/page.tsx` 1,029 → 527 (extracted `InventoryGrid`, `CaseLotsGrid`, `ShopHeader`, `orderExports` util, generic `useListing` hook collapsing duplicated filter/sort/pagination); `tsc --noEmit` green. Phases 7–9 pending.

This plan covers the deferred items from the simplification analysis.
Phases 1–4 (H-1, H-3, H-4, H-5, F-H-1, F-H-3, F-M-1..6, F-L-1..4, M-1,
M-2 partial, M-3, M-4, M-5, M-6, M-10, L-3, L-4) are already shipped.

---

## Pending Items

### Backend
| ID  | Item | Effort | Risk |
|-----|------|--------|------|
| H-2 | Split `OfferService.java` (1,062 lines) → `OracleOrderClient` + `OfferNumberGenerator` | 1d | High |
| M-2 | Lombok-ify JPA entities: `Rma` (212), `RmaItem` (120), `Order` (147), `OracleConfig` (63) | 0.5d | Medium |
| M-7 | Move `BuyerCodeService` native queries → `EcoATMDirectUserRepository` | 0.5d | Low |
| M-8 | `RmaResponse.fromEntity` null-guards → DB defaults + primitive fields | 0.5d | Medium (schema) |
| M-9 | `OfferReviewService.getStatusSummaries` stream/reduce | — | **SKIP** — not worth churn |

### Frontend
| ID    | Item | Effort | Risk |
|-------|------|--------|------|
| F-H-2 | Split `pricing/page.tsx` (762) and `order/page.tsx` (1,046) | 1d | Medium |

---

## Phase 5 — Backend `OfferService` Split (H-2)

**Goal:** Reduce `OfferService.java` from 1,062 → <600 lines by extracting
two cohesive collaborators.

### Steps
1. **Extract `OracleOrderClient`** (~200 lines, current `OfferService:650-850`)
   - Owns: Oracle HTTP call, request builder, response parsing, retry/error mapping.
   - Inject `RestTemplate` (or `WebClient`), `OracleConfigRepository`, `ErrorMappingRepository`.
   - Public surface: `OracleOrderResponse submitOrder(OracleOrderRequest req)`.
2. **Extract `OfferNumberGenerator`** (~40 lines, current `generateOfferNumber:960-976`)
   - Wraps the Phase-4 `INSERT ... ON CONFLICT ... RETURNING max_sequence` round-trip.
   - Public surface: `String next(LocalDate date)`.
3. **`OfferService` keeps:** cart mgmt, status transitions, order submission orchestration,
   CSV export, device reservation. Delegates HTTP + number gen.

### Tests
- `OfferServiceTest` — rewire mock router to mock `OracleOrderClient` directly; collapses the 4-query Oracle stub into one `when(oracleOrderClient.submitOrder(...)).thenReturn(...)`.
- **New** `OracleOrderClientTest` — `MockRestServiceServer` covering happy path, 5xx retry, error-mapping translation.
- **New** `OfferNumberGeneratorTest` — extend `NativeQuerySmokeIT` fixture; one test for fresh-day insert, one for same-day increment.

### Gate
- `mvn test` — all 444+ green.
- Manual smoke: local offer submission against stubbed Oracle (or QA if available).

---

## Phase 6 — Frontend Page Splits (F-H-2)

**Goal:** Break the two oversized pages into cohesive components; each page shell <500 lines.

### `pricing/page.tsx` (762 → target <400)
Extract to `frontend/src/components/pricing/`:
- `UploadCsvModal.tsx` — CSV upload dialog + result panel.
- `PriceHistoryModal.tsx` — history grid (uses existing `GET /pws/pricing/devices/{id}/history`).
- `FutureDateModal.tsx` — future-price activation date picker.
- `PricingFilters.tsx` — filter bar (SKU, category, brand, etc.).
- Page shell retains: grid, pagination, error banner wiring, modal open/close state.

### `order/page.tsx` (1,046 → target <500)
Extract to `frontend/src/components/order/`:
- `InventoryGrid.tsx` — device list + search/filter.
- `CartPanel.tsx` — cart rows + totals.
- `SubmitModal.tsx` — final submission confirmation.
- `AlmostDoneModal.tsx` — post-submit success state.
- Page shell retains: buyer-code guard, data fetch orchestration, state wiring between grid ↔ cart.

### Tests
- Existing Playwright `frontend/tests/pws/inventory-cart.spec.ts` + pricing specs are the regression gate — no new specs required.
- `tsc --noEmit --pretty false` must stay green.

### Gate
- Playwright PWS suite green locally.
- Visual parity check against `https://buy-qa.ecoatmdirect.com` (per `CLAUDE.md` styling QA rule).

---

## Phase 7 — JPA Entity Lombok (M-2 remaining)

**Goal:** Remove hand-written boilerplate from four entities.

### Steps
1. Verify each entity's Hibernate access strategy. If using **field access**
   (annotations on fields, no `@Access(AccessType.PROPERTY)`), `@Getter @Setter`
   is safe. Otherwise skip that entity.
2. Add `@Getter @Setter` and delete hand-written accessors on:
   - `Rma.java` (212 lines → ~120)
   - `RmaItem.java` (120 → ~60)
   - `Order.java` (147 → ~85)
   - `OracleConfig.java` (63 → ~35)
3. Do **not** add `@Data` — avoids `equals/hashCode` over JPA-managed fields.

### Gate
- `mvn test` green.
- Explicit verification: `RmaServiceIT`, any `Order`-touching repository tests, `OracleConfigControllerTest`.

---

## Phase 8 — Schema Cleanup (M-8)

**Goal:** Drop seven null-guard ternaries in `RmaResponse.fromEntity` by
pushing defaults into the schema.

### Steps
1. **Migration `V25__rma_nullable_defaults.sql`** in `backend/src/main/resources/db/migration/`:
   - `UPDATE pws.rma SET <col> = 0 WHERE <col> IS NULL;` for each numeric column.
   - `ALTER TABLE pws.rma ALTER COLUMN <col> SET DEFAULT 0, SET NOT NULL;`
   - Repeat for `pws.rma_item` numeric columns.
   - Identify columns via `RmaResponse.fromEntity:37-43` null-guard list.
2. Change `Rma` / `RmaItem` numeric fields from `Integer`/`BigDecimal` → `int`/`BigDecimal` (BigDecimal can't be primitive; keep boxed but assert non-null via `@Column(nullable = false)`).
3. Delete the seven `!= null ? x : 0` ternaries.

### Gate
- Migration applies cleanly against fresh `salesplatform_dev` (`psql -f bootstrap.sql && mvn spring-boot:run`).
- `RmaServiceTest`, `RmaControllerTest` green.
- Production-DB-safety note: the `UPDATE ... WHERE IS NULL` step is idempotent and runs before the `NOT NULL` constraint.

---

## Phase 9 — Repository Consolidation (M-7)

**Goal:** Remove `EntityManager` injection from `BuyerCodeService`.

### Steps
1. Identify each `em.createNativeQuery(...)` call site in `BuyerCodeService`.
2. For each, add an equivalent `@Query(value = "...", nativeQuery = true)` method on `EcoATMDirectUserRepository` (or a new `BuyerCodeQueryRepository` if the queries don't belong on the user repo).
3. Delete `@PersistenceContext EntityManager em` field.
4. Update call sites to use the repo method.

### Gate
- `mvn test` green.
- Any existing `BuyerCodeService` unit tests cover the paths.

---

## Sequencing

```
Phase 5 (OfferService split)  ─┐
                               ├── independent — can run in parallel
Phase 6 (Frontend page splits) ─┘

Phase 7 (Entity Lombok) ─→ Phase 8 (Schema/null-guards) ─→ Phase 9 (Repo consolidation)
```

- **Phase 5 + 6** first — highest visibility (file-size ceiling compliance, smaller PR diffs going forward).
- **Phase 7 → 8** sequential: Phase 8's primitive/boxed field changes land cleaner on a Lombok-ified entity.
- **Phase 9** last — small, low-risk cleanup; good "cooldown" task.

### Skipped
- **M-9** — `OfferReviewService.getStatusSummaries`. Current 30-line loop is more readable than a stream/reduce collapse. Not worth the churn.
- **L-1, L-2, L-5** — already verified as stale observations in Phase 4.

---

## Definition of Done (per phase)

- [ ] All listed items in the phase marked DONE or DEFERRED with rationale.
- [ ] `mvn test` green (backend phases).
- [ ] `tsc --noEmit --pretty false` green (frontend phases).
- [ ] Relevant Playwright suite green (Phase 6).
- [ ] `simplification-analysis.md` progress section updated with the phase's outcome.
- [ ] No new items added to the deferred list without justification.
