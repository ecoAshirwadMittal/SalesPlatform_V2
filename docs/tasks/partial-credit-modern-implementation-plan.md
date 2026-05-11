# Partial Credit Requests — Modern Implementation Plan

> **Status:** Drafting Sprint 1
> **Last updated:** 2026-05-11
> **Source plan:** [`../../../docs/tasks/partial-credit-execution-plan.md`](../../../docs/tasks/partial-credit-execution-plan.md) (Mendix-targeted)
> **Source spec:** [`../../../docs/tasks/partial-credit-confluence.md`](../../../docs/tasks/partial-credit-confluence.md)
> **Jira epic:** [SPKB-3653](https://gazelle.atlassian.net/browse/SPKB-3653)

This document translates the Mendix-targeted execution plan into the
modern Spring Boot + Next.js + Postgres stack. It is the source of
truth for code work on this branch; the Mendix plan stays in the
parent `mendix-extractor/docs/tasks/` directory as historical reference.

## 1. Translation table — Mendix → modern stack

| Mendix concept | Modern equivalent |
|---|---|
| Module `EcoATM_PartialCredit` | Postgres schema `partial_credit`; Spring package `com.ecoatm.salesplatform.{model,repository,service,controller}.partialcredit` |
| Domain model (Mendix entities) | JPA entities in `model/partialcredit/` + Flyway `V86__partial_credit_init.sql` |
| Enumerations | Java enums in `model/partialcredit/enums/` |
| Microflows (`ACT_*`, `SUB_*`, `DS_*`, `VAL_*`, `BFR_*`) | Spring `@Service` classes — split per concern (`CreditRequestService`, `CreditRequestValidator`, `CreditCalculationService`, `ActionRecommendationService`, etc.) |
| Pages (Mendix) | Next.js App Router routes under `frontend/src/app/(dashboard)/wholesale/partial-credit/` (buyer) and `/admin/auctions-data-center/partial-credit/` (sales ops). Reuse the shared `DataGrid` for any list/table |
| EDBC Snowflake | Snowflake JDBC reader (pattern: `ReserveBidSnowflakeReader`); writer not needed for Phase 1 |
| Mendix file documents | Postgres `bytea` column on a `*_uploads` table OR external object storage (S3-compatible). Use `bytea` for Phase 1 to stay self-contained; revisit at scale |
| Module roles | Spring Security `@PreAuthorize` annotations on controllers; new role rows in `identity.user_roles` seeded by Flyway |
| `BFR_CreditRequest_BeforeCommit` (audit stamping) | JPA `@PreUpdate` / `@PrePersist` listener on entity, or domain-event interceptor — to be decided per entity |
| Mendix `AfterStartup` seed of statuses | Flyway data migration |

## 2. Schema decisions

- **Schema:** new `partial_credit` schema (NOT inside `pws` or `auctions` — clean bound)
- **FK conventions:** `*_id BIGINT REFERENCES <schema>.<table>(id)`; match modern app convention
- **Audit columns:** `created_date`, `changed_date`, `created_by_id`, `changed_by_id` on every persisted entity (consistent with `auctions.reserve_bid_audit`, `buyer_mgmt.buyers`, etc.)
- **Status table:** seeded via Flyway, not microflow startup. 5 rows: Draft / PendingApproval / UnderReview / Approved / Declined.
- **Photos:** stored as `bytea` plus original filename + content-type + size. Phase 2 may externalize to S3 if file volume gets unwieldy.
- **Wrong-device pricing source:** local `auctions.bid_data` (already in our DB) aggregated live at review-open. Cached on `wrong_device_lines.latest_price`.

## 3. Sprint plan (modern)

Same 4 sprints as Mendix but re-shaped for our stack. Total est. ~3-4 weeks for one engineer.

### Sprint 1 — Foundations (this sub-project)

Goal: domain model committed, schema migrated, status table seeded, Snowflake reader stub, permissions wired. **No UI yet.**

| # | Task | File(s) | Effort |
|---|---|---|---|
| 1 | Flyway migration `V86__partial_credit_init.sql` — schema, 7 tables, 5 enums (Postgres CHECK constraints), indexes | `backend/src/main/resources/db/migration/V86__partial_credit_init.sql` | 4h |
| 2 | Seed migration `V87__partial_credit_status_seed.sql` — 5 status rows | same dir | 30m |
| 3 | JPA entities for 7 tables | `backend/src/main/java/.../model/partialcredit/*.java` | 3h |
| 4 | Repositories (`JpaRepository` interfaces) | `backend/src/main/java/.../repository/partialcredit/*.java` | 1h |
| 5 | Java enums + Postgres ↔ enum mapping | `backend/src/main/java/.../model/partialcredit/enums/*.java` | 1h |
| 6 | Module-role seed in Flyway — 4 new `identity.user_roles` rows | same migration as #1 | 30m |
| 7 | `CreditRequestSnowflakeReader` skeleton — 6 query methods stubbed against the documented view shape; live calls deferred to Sprint 2 | `backend/src/main/java/.../service/partialcredit/snowflake/CreditRequestSnowflakeReader.java` | 2h |
| 8 | Integration test for migration (PostgreSQL Testcontainers via Flyway test harness) | `backend/src/test/java/.../partialcredit/PartialCreditMigrationIT.java` | 1h |
| 9 | Verification — apply migration locally, confirm 5 status rows + indexes | manual / sql | 30m |

Total Sprint 1 estimate: **~13 hours** (~1.5–2 working days).

### Sprint 2 — Buyer wizard + validation + landing

Goal: a logged-in wholesale buyer can submit a credit request end-to-end through a 5-step wizard, and see it on a list page.

- Live Snowflake reader implementation (the stubbed methods from Sprint 1 #7)
- Submission validation service (`CreditRequestValidator`) — order ownership, 30-day window, no active duplicate, ≥1 reason, barcode reconciliation, damage Q&A
- REST endpoints: `POST /api/v1/buyer/partial-credit/draft`, `PATCH .../{id}`, `POST .../{id}/submit`, `GET /api/v1/buyer/partial-credit?status=...`, `GET .../{id}`
- 5 Next.js routes (buyer): `/wholesale/partial-credit` (landing), `/wholesale/partial-credit/new` (wizard step 1), wizard steps 2-5 as in-page state
- Email-send stubs (deferred until Sprint 4)

### Sprint 3 — Admin review + credit calc + action recommendation + bulk actions + Complete Review

Goal: sales ops can review any pending request, see the three reason sections, get a per-line recommendation, accept/decline (per line or in bulk), enter manual Prolog Result + Actual Value on encumbered lines, click Complete Review to flip the request to Approved or Declined.

- `CreditCalculationService`, `ActionRecommendationService`
- REST endpoints: `GET /api/v1/admin/partial-credit?...filters`, `GET .../{id}/review`, `POST .../{id}/lines/{lineId}/decision`, `POST .../{id}/sections/{kind}/decision`, `POST .../{id}/complete-review`
- Admin pages: `/admin/auctions-data-center/partial-credit` (landing with status counters) and `/admin/.../{id}` (review detail with 3 collapsible reason sections)

### Sprint 4 — Permissions, on-behalf, buyer detail, post-submit photos, Excel export, email templates

Goal: feature-complete Phase 1.

## 4. Entity → table mapping (Sprint 1 detail)

| Entity (Java class) | Table | Notes |
|---|---|---|
| `CreditRequest` | `partial_credit.credit_requests` | header — denormalizes order metadata at submit |
| `MissingDeviceLine` | `partial_credit.missing_device_lines` | one row per barcode entered for Missing reason |
| `WrongDeviceLine` | `partial_credit.wrong_device_lines` | one row per barcode entered for Wrong reason; `latest_price` is the bid-data aggregate cached at review-open |
| `EncumberedDeviceLine` | `partial_credit.encumbered_device_lines` | one row per barcode; `prolog_result` + `actual_value` are reviewer-entered in Phase 1 |
| `CreditRequestPhoto` | `partial_credit.credit_request_photos` | bytea file + kind (DamagePhoto / WrongDevicePhoto) + nullable FK to a wrong-device line |
| `CreditRequestUpload` | `partial_credit.credit_request_uploads` | original xlsx/csv barcode file the buyer uploaded |
| `CreditRequestStatus` | `partial_credit.credit_request_statuses` | config table, 5 seed rows |

## 5. Enums (Sprint 1 detail)

- `LineRowStatus` — `VALID, DUPLICATE, NOT_IN_ORDER` (string, CHECK constraint)
- `ReviewDecision` — `PENDING, ACCEPTED, DECLINED`
- `ShipStatus` — `SHIPPED, NOT_SHIPPED, UNKNOWN`
- `PrologResult` — `ENCUMBERED, NOT_ENCUMBERED, PENDING`
- `ActionRecommendation` — `ACCEPT, DECLINE` (no `MANUAL_REVIEW` per 2026-05-11 decision)
- `CreditRequestStatus.system_status` — `DRAFT, PENDING_APPROVAL, UNDER_REVIEW, APPROVED, DECLINED` (string, CHECK)
- `ShipmentDamaged` — `YES, NO, NOT_ANSWERED` (string, CHECK)
- `PhotoKind` — `DAMAGE, WRONG_DEVICE`
- `UploadKind` — `MISSING_BARCODES, WRONG_DEVICES, ENCUMBERED_BARCODES`

## 6. Module roles (Sprint 1 detail)

New `identity.user_roles` rows:
- `PartialCredit_Buyer` — own requests only
- `PartialCredit_SalesRep` — submit on behalf, view all
- `PartialCredit_SalesOps` — view + review + accept/decline all
- `PartialCredit_Admin` — full admin (status config, configuration page)

Mapping to existing global roles: TBD in Sprint 4 (`SPKB-3659`). Likely:
- All `Bidder` users → `PartialCredit_Buyer` if their `BuyerCode.code` has wholesale eligibility
- All `SalesRep` users → `PartialCredit_SalesRep`
- All `SalesOps` users → `PartialCredit_SalesOps`
- All `Administrator` users → `PartialCredit_Admin`

## 7. Out-of-scope (Phase 2, deliberately deferred)

- Automated Prolog encumbrance check
- RMA auto-creation for accepted encumbered lines
- Oracle write-back of approved credits
- Datadog dashboards & SLO alerts
- R-2 certification gating for encumbered (auto-block in wizard Step 1)

## 8. Verification (Sprint 1)

1. `mvn flyway:migrate` applies V86 + V87 cleanly on a fresh `salesplatform_dev` database.
2. `psql -c "\dn partial_credit"` confirms the schema exists with all 7 tables.
3. `psql -c "SELECT system_status FROM partial_credit.credit_request_statuses ORDER BY sort_order"` returns the 5 seed rows in the right order.
4. `mvn test -Dtest=PartialCreditMigrationIT` is green.
5. Backend boots cleanly with no JPA mapping errors.
