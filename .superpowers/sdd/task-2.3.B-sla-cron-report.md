# Task 2.3 Chunk B — SLA-tag service + scheduled job — REPORT

- **STATUS:** DONE
- **Branch:** `worktree-agent-ae1a98f04e5b32fb5` (cut from an older commit at V100;
  merged current `main` @ `5c98e644` in first to pick up V101–V103 +
  `Offer.offerBeyondSla`, then built on top — 4 commits ahead of main)
- **Commit SHA:** `038b8c114a37e1b5cef69a24b7af779e9b3725b0` (branch HEAD). Commit range:
  - `08160858` feat(pws): V104 pws.company_holiday + CompanyHoliday/PwsConstants read models
  - `e2cc86e1` feat(pws): SlaTagService — business-day SLA cutoff + scheduled cron
  - `5dadac79` refactor(pws): route SLA-tag admin endpoints through SlaTagService (DRY)
  - `038b8c11` docs(pws): document SLA-tag service + V104 company_holiday
- **Test summary:** `./mvnw test -Dtest=SlaTagServiceTest,SlaTagServiceIT,V104MigrationIT,PWSAdminControllerTest -Dspring.profiles.active=pg-test` → **58/58 green** (SlaTagServiceTest 11 + SlaTagServiceIT 2 + V104MigrationIT 3 + PWSAdminControllerTest 42 regression). Live cutoff computed `2026-07-09` for today Mon 2026-07-13 / sla_days=2 — matches the unit-test expectation.
- **V-version used:** **V104** (`V104__pws_company_holiday.sql`). VERIFIED free — `main` highest was V103.
- **Holiday seed source:** **US-federal best-effort** (flagged in the migration + docs). The legacy `ecoatm_mdm$companyholiday` DATA rows were NOT in `migration_context/` (only the schema + 3 unaligned sample values). Seeded the 7 observed US-federal / corporate holidays (New Year, Memorial, Juneteenth, Independence, Labor, Thanksgiving, Christmas) × 2025–2027 = 21 rows. This matches the legacy set exactly where the samples were visible and the legacy row count (7).

## What shipped
- **V104** creates `pws.company_holiday` (`holiday_date DATE NOT NULL` UNIQUE + index, audit cols), idempotent (`IF NOT EXISTS` + `ON CONFLICT DO NOTHING`), seeded.
- **`CompanyHoliday`** + **`PwsConstants`** JPA read entities + repositories (`CompanyHolidayRepository.findHolidayDatesBetween` → `Set<LocalDate>`; `PwsConstantsRepository.findTopByOrderByIdAsc`). Two `@Modifying` methods on `OfferRepository` (`tagOverdueOffers`/`clearAllSlaTags`).
- **`SlaTagService`** — the single implementation both the manual admin buttons and the cron call. Business-day cutoff walks back `pws_constants.sla_days` business days (injected `Clock`), skipping weekends + company holidays (legacy `SUB_CalculateSLADate`); honors the configurable `sla_days` (default 2 when the row is missing / non-positive). `@Scheduled(fixedDelay 15m default)` + `@SchedulerLock(pwsSlaTag)` + `pws.sla-tag.enabled=false` short-circuit.
- **`PWSAdminController`** `setSLATags`/`removeSLATags` now delegate to the service (DRY — removed the hardcoded `NOW() - INTERVAL '2 days'`); endpoints + Administrator authz unchanged. `pws.sla-tag.{enabled,fixed-delay-ms}` added to `application.yml`.
- Docs updated: `data-model.md`, `modules.md`, `setup.md`, `coverage.md` (left `decisions.md` untouched per brief).

## Concerns
1. **Holiday seed is best-effort, not the authoritative legacy rows** — swap in the real `ecoatm_mdm$companyholiday` data if it becomes available. SLA math with default `sla_days=2` only reaches ~1 week back, so coverage across 2025–2027 is more than sufficient in practice.
2. **Shared dev DB was at Flyway version 105** (parallel in-flight work by other agents); my V104 applied cleanly out-of-order. On `main` V104 is the correct next number (main highest = V103), so a fresh/CI DB applies 1..104..(future 105) in order with no conflict. If another branch also claims V104 before this merges, that is a trivial rename-at-merge — no logic overlap.
3. **Tagging bumps `updated_date`** via the `trg_update_updated_date` BEFORE-UPDATE trigger (unavoidable, and matches legacy + the current endpoints). `offer_beyond_sla` is a one-shot flag so this is benign; noted for anyone who later builds reminder logic off `updated_date`.
4. **No email/Snowflake** (out of scope per brief — a later gap-2.3 chunk consumes these flags).
