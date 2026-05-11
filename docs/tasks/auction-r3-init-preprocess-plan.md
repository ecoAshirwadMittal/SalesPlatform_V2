# Sub-project 6: R3 Init + Pre-process Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace `R3InitStubListener` and `R3PreProcessStubListener` with two sibling services that port Mendix `SUB_Round3_PreProcessRoundData` (minus BidData generation) and `ACT_Round3_SetStarted`. Pre-process writes R3 `qualified_buyer_codes` rows + `round3_buyer_data_reports`; init flips the legacy `Round3InitStatus` enum and emits a completion event.

**Architecture:** Two REQUIRES_NEW services + two `@Async @AFTER_COMMIT` listeners + two admin REST endpoints. Five-phase pre-process (delete unsubmitted R2 bids → regular qualification CTE → STB CTE → three-set QBC bulk INSERT → round-3 reports). Init uses a predecessor guard (refuses unless `r3_preprocess_status = SUCCESS` on the same R3 SA row). Status flags on `auctions.scheduling_auctions` mirror sub-project 5's V83 pattern. R2 retroactively aligned to whole-percent convention to match R3.

**Tech Stack:** Spring Boot 3.x, Java 21, JPA/Hibernate, native PostgreSQL CTEs via `EntityManager`, Flyway, JUnit 5 + AssertJ + Mockito, Spring `@TransactionalEventListener`, `MockMvc` for controller IT, Testcontainers PostgreSQL for repository IT.

**Spec:** `docs/tasks/auction-r3-init-preprocess-design.md`

---

## File Structure

### New files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  event/
    R3PreProcessCompletedEvent.java              -- record event (after pre-process SUCCESS)
    R3InitCompletedEvent.java                    -- record event (after init SUCCESS)
  service/auctions/r3init/
    R3PreProcessService.java                     -- 5-phase REQUIRES_NEW orchestrator
    R3InitService.java                           -- status flip with predecessor guard
    R3PreProcessListener.java                    -- replaces R3PreProcessStubListener
    R3InitListener.java                          -- replaces R3InitStubListener
    R3PreProcessResult.java                      -- record(qualifiedCount, ..., skipped)
    R3InitResult.java                            -- record(durationMs)
  repository/auctions/
    R3BuyerQualificationRepository.java          -- regular CTE (native)
    R3SpecialBuyerRepository.java                -- STB CTE (native, prior_round ∈ {1,2})
    R3PreProcessSupportRepository.java           -- bulk DELETE for unsubmitted R2 bids
  controller/admin/
    R3LifecycleAdminController.java              -- POST /preprocess-r3 + /reinit-r3
  dto/admin/
    R3PreProcessResponse.java                    -- response shape (preprocess)
    R3InitResponse.java                          -- response shape (init)
```

### Modified files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  model/auctions/
    SchedulingAuction.java                       -- add 8 R3-lifecycle status fields
  service/auctions/recalc/
    RecalcStatusUpdater.java                     -- add R3_PREPROCESS + R3_INIT cases
  exception/
    RecalcAlreadyRunningException.java           -- add R3_PREPROCESS + R3_INIT enum constants
  repository/
    QualifiedBuyerCodeRepository.java            -- rename bulkInsertForR2 → bulkInsertForRound
    QualifiedBuyerCodeRepositoryCustom.java      -- rename in fragment interface
    QualifiedBuyerCodeRepositoryImpl.java        -- rename in impl
  repository/auctions/
    Round3BuyerDataReportRepository.java         -- add deleteBySchedulingAuctionId
                                                 --   + bulkInsertForSchedulingAuction
  service/auctions/r2init/
    R2BuyerAssignmentService.java                -- update single call site after rename
  config/
    SecurityConfig.java                          -- admin matchers for /preprocess-r3 + /reinit-r3
```

### Modified files (R2 unit alignment, separate commit)

```
backend/src/main/java/com/ecoatm/salesplatform/
  repository/auctions/
    R2BuyerQualificationRepository.java          -- formula: 1 - p.target_pct → 1 - (p.target_pct / 100)
backend/src/test/resources/fixtures/auctions/
  r2-init-seed.sql                               -- target_percent values 0.05 → 5
docs/business-logic/
  r2-buyer-assignment.md                         -- note whole-percent convention update
```

### Deleted files

```
backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/
  R3InitStubListener.java                        -- replaced by R3InitListener
  R3PreProcessStubListener.java                  -- replaced by R3PreProcessListener
```

### Schema migration

```
backend/src/main/resources/db/migration/
  V84__auctions_r3_lifecycle_status.sql          -- additive only
```

### Config

```
backend/src/main/resources/
  application.yml                                -- add auctions.r3-preprocess.enabled
                                                 --   + auctions.r3-init.enabled (default true)
```

### Tests

```
backend/src/test/
  java/com/ecoatm/salesplatform/
    repository/auctions/
      R3PreProcessSupportRepositoryIT.java
      R3BuyerQualificationRepositoryIT.java
      R3SpecialBuyerRepositoryIT.java
      Round3BuyerDataReportRepositoryR3IT.java
      QualifiedBuyerCodeRepositoryR2IT.java        -- extended with one R3 case
    service/auctions/r3init/
      R3PreProcessServiceTest.java
      R3InitServiceTest.java
      R3PreProcessListenerTest.java
      R3InitListenerTest.java
      R3LifecycleEndToEndIT.java
    controller/admin/
      R3LifecycleAdminControllerIT.java
  resources/fixtures/auctions/
    r3-lifecycle-seed.sql
```

### Documentation

```
docs/
  api/rest-endpoints.md                          -- /preprocess-r3 + /reinit-r3
  app-metadata/modules.md                        -- sub-project 6 entry
  architecture/data-model.md                     -- new r3_*_* columns + BRSF columns
  architecture/decisions.md                      -- ADR for sub-project 6
  business-logic/index.md                        -- link r3-init-and-preprocess.md
  business-logic/r3-init-and-preprocess.md       -- new
  business-logic/r2-buyer-assignment.md          -- note whole-percent convention update
  deployment/setup.md                            -- auctions.r3-{preprocess,init}.enabled keys
  testing/coverage.md                            -- auctions.r3lifecycle entry
  tasks/auction-flow-gap-analysis-2026-05-06.md  -- mark item #2 in-flight/shipped
```

---

## Task 1: Schema migration V84 (additive)

**Files:**
- Create: `backend/src/main/resources/db/migration/V84__auctions_r3_lifecycle_status.sql`
- Test: existing Flyway boot test exercises this on `mvn test`

- [ ] **Step 1: Write the migration**

Create `backend/src/main/resources/db/migration/V84__auctions_r3_lifecycle_status.sql`:

```sql
-- V84: Sub-project 6 — R3 init + pre-process — schema additions
-- Additive only. R2 column semantics-update is comment-only (no data change).

-- ─── R3 selection-criteria knobs on bid_round_selection_filters ──────────
ALTER TABLE auctions.bid_round_selection_filters
    ADD COLUMN bid_percentage_variation  NUMERIC(10, 4),
    ADD COLUMN bid_amount_variation      NUMERIC(14, 2),
    ADD COLUMN rank_qualification_limit  INTEGER;

COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_percentage_variation IS
    '6: R3 qualification — whole-percent threshold (5 = 5%). Branch active when NOT NULL: latest_bid >= round3_target_price - (round3_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.bid_amount_variation IS
    '6: R3 qualification — flat amount. Branch active when NOT NULL: latest_bid >= round3_target_price - amount.';
COMMENT ON COLUMN auctions.bid_round_selection_filters.rank_qualification_limit IS
    '6: R3 qualification — rank ceiling. Branch active when NOT NULL: round3_bid_rank <= limit. All three branches NULL → qualify everyone.';

-- ─── R2 convention re-alignment to whole-percent (comment only) ──────────
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_percent IS
    '6: R2 qualification — whole-percent threshold (5 = 5%). Was treated as decimal in V59/sub-project 5; sub-project 6 normalises to whole-percent across both rounds. Formula: bid >= avg_target_price - (avg_target_price * pct / 100).';
COMMENT ON COLUMN auctions.bid_round_selection_filters.target_value IS
    'R2 qualification — flat amount threshold (mirrors Mendix bidamountvariation semantics for R3).';

-- ─── R3 lifecycle status columns on scheduling_auctions ──────────────────
ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r3_preprocess_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_preprocess_error         TEXT,
    ADD COLUMN r3_preprocess_started_at    TIMESTAMPTZ,
    ADD COLUMN r3_preprocess_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_preprocess_status
        CHECK (r3_preprocess_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED')),

    ADD COLUMN r3_init_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r3_init_error               TEXT,
    ADD COLUMN r3_init_started_at          TIMESTAMPTZ,
    ADD COLUMN r3_init_finished_at         TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r3_init_status
        CHECK (r3_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED | SKIPPED (R3 SA exists with has_round=false). Lives on the R3 SA row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_preprocess_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_status IS
    '6: PENDING | RUNNING | SUCCESS | FAILED. Init refuses to flip to SUCCESS unless r3_preprocess_status = SUCCESS on the same row.';
COMMENT ON COLUMN auctions.scheduling_auctions.r3_init_error IS
    '6: exception class + message (truncated to 4000 chars) on FAILED, including the "predecessor not SUCCESS" guard message.';
```

- [ ] **Step 2: Verify Flyway picks it up**

Run: `cd backend && mvn -q -DskipTests=false test -Dtest='*FlywayMigrationsIT*'`
Expected: PASS — no migration ordering or syntax errors.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V84__auctions_r3_lifecycle_status.sql
git commit -m "feat(6): V84 add r3 lifecycle status columns + R3 selection criteria filters"
```

---

## Task 2: R2 unit alignment — whole-percent convention

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java`
- Modify: `backend/src/test/resources/fixtures/auctions/r2-init-seed.sql`
- Modify: `docs/business-logic/r2-buyer-assignment.md`
- Test: existing `R2BuyerQualificationRepositoryIT` should still pass with the new convention

- [ ] **Step 1: Update fixture values from decimal to whole percent**

In `backend/src/test/resources/fixtures/auctions/r2-init-seed.sql`, find every BRSF row insert and change `target_percent` values:

```sql
-- BEFORE (any line like)
INSERT INTO auctions.bid_round_selection_filters (round, target_percent, target_value, ...) VALUES
    (2, 0.05, 1.00, ...);

-- AFTER
INSERT INTO auctions.bid_round_selection_filters (round, target_percent, target_value, ...) VALUES
    (2, 5, 1.00, ...);
```

Read the existing fixture first (`Read` tool) and edit each `target_percent = X.XX` value to its whole-percent equivalent (multiply by 100). Typical: `0.05` → `5`, `0.10` → `10`, `0.025` → `2.5`.

- [ ] **Step 2: Update R2 CTE formula**

In `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java`, find the `qualifies_per_ae` CTE branch:

```java
// BEFORE
WHEN r.r1_target_price > 0
     AND r.bid_amount / r.r1_target_price >= 1 - p.target_pct THEN TRUE

// AFTER
WHEN r.r1_target_price > 0
     AND r.bid_amount / r.r1_target_price >= 1 - (p.target_pct / 100) THEN TRUE
```

- [ ] **Step 3: Run R2 IT to verify still green**

Run: `cd backend && mvn -q test -Dtest=R2BuyerQualificationRepositoryIT`
Expected: PASS — fixture updates and formula change are aligned (5/100 = 0.05 = previous decimal).

- [ ] **Step 4: Update R2 business-logic doc**

In `docs/business-logic/r2-buyer-assignment.md`, find any mention of `target_percent` and add a note:

```markdown
**Convention update (2026-05-07, sub-project 6):** `target_percent` values are stored as whole percent (5 = 5%), aligned with Mendix native convention and R3's `bid_percentage_variation`. The CTE formula divides by 100: `bid >= target - (target * pct / 100)`.
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java \
        backend/src/test/resources/fixtures/auctions/r2-init-seed.sql \
        docs/business-logic/r2-buyer-assignment.md
git commit -m "fix(6): R2 target_percent normalised to whole-percent convention (5 = 5%)"
```

---

## Task 3: Add R3 status fields to `SchedulingAuction`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java`
- Test: existing `SchedulingAuctionRepositoryIT` exercises JPA mapping

- [ ] **Step 1: Add eight new fields**

In `SchedulingAuction.java`, after the R2_INIT block (added by sub-project 5 V83), add:

```java
@Enumerated(EnumType.STRING)
@Column(name = "r3_preprocess_status", length = 20, nullable = false)
private RecalcStatus r3PreprocessStatus = RecalcStatus.PENDING;

@Column(name = "r3_preprocess_error", columnDefinition = "TEXT")
private String r3PreprocessError;

@Column(name = "r3_preprocess_started_at")
private Instant r3PreprocessStartedAt;

@Column(name = "r3_preprocess_finished_at")
private Instant r3PreprocessFinishedAt;

@Enumerated(EnumType.STRING)
@Column(name = "r3_init_status", length = 20, nullable = false)
private RecalcStatus r3InitStatus = RecalcStatus.PENDING;

@Column(name = "r3_init_error", columnDefinition = "TEXT")
private String r3InitError;

@Column(name = "r3_init_started_at")
private Instant r3InitStartedAt;

@Column(name = "r3_init_finished_at")
private Instant r3InitFinishedAt;
```

Add matching getters and setters at the bottom of the file (mirror the R2_INIT accessor pattern from sub-project 5):

```java
public RecalcStatus getR3PreprocessStatus() { return r3PreprocessStatus; }
public void setR3PreprocessStatus(RecalcStatus s) { this.r3PreprocessStatus = s; }

public String getR3PreprocessError() { return r3PreprocessError; }
public void setR3PreprocessError(String e) { this.r3PreprocessError = e; }

public Instant getR3PreprocessStartedAt() { return r3PreprocessStartedAt; }
public void setR3PreprocessStartedAt(Instant t) { this.r3PreprocessStartedAt = t; }

public Instant getR3PreprocessFinishedAt() { return r3PreprocessFinishedAt; }
public void setR3PreprocessFinishedAt(Instant t) { this.r3PreprocessFinishedAt = t; }

public RecalcStatus getR3InitStatus() { return r3InitStatus; }
public void setR3InitStatus(RecalcStatus s) { this.r3InitStatus = s; }

public String getR3InitError() { return r3InitError; }
public void setR3InitError(String e) { this.r3InitError = e; }

public Instant getR3InitStartedAt() { return r3InitStartedAt; }
public void setR3InitStartedAt(Instant t) { this.r3InitStartedAt = t; }

public Instant getR3InitFinishedAt() { return r3InitFinishedAt; }
public void setR3InitFinishedAt(Instant t) { this.r3InitFinishedAt = t; }
```

Note: `RecalcStatus` enum (already includes `SKIPPED` from sub-project 5) is reused for both fields. The V84 CHECK constraint on `r3_init_status` blocks SKIPPED at the DB layer — service code never writes SKIPPED for init.

- [ ] **Step 2: Run the entity-load smoke test**

Run: `cd backend && mvn -q test -Dtest='SchedulingAuctionRepositoryIT*'`
Expected: PASS — JPA mapping resolves cleanly against V84.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java
git commit -m "feat(6): add r3_preprocess_* + r3_init_* status fields to SchedulingAuction"
```

---

## Task 4: Extend `RecalcStatusUpdater` for R3 processes

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java`
- Test: extend existing `RecalcStatusUpdaterTest`

- [ ] **Step 1: Write the failing test**

Append to `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdaterTest.java`:

```java
@Test
@DisplayName("tryFlipToRunning routes R3_PREPROCESS to r3_preprocess_status column")
void flips_r3_preprocess_to_running() {
    long saId = seedSchedulingAuctionWithStatus("r3_preprocess_status", "PENDING");

    boolean flipped = updater.tryFlipToRunning(saId, "R3_PREPROCESS");

    assertThat(flipped).isTrue();
    assertThat(currentStatus(saId, "r3_preprocess_status")).isEqualTo("RUNNING");
}

@Test
@DisplayName("tryFlipToRunning routes R3_INIT to r3_init_status column")
void flips_r3_init_to_running() {
    long saId = seedSchedulingAuctionWithStatus("r3_init_status", "PENDING");

    boolean flipped = updater.tryFlipToRunning(saId, "R3_INIT");

    assertThat(flipped).isTrue();
    assertThat(currentStatus(saId, "r3_init_status")).isEqualTo("RUNNING");
}

@Test
@DisplayName("markSkipped writes SKIPPED on r3_preprocess_status (has_round=false branch)")
void marks_r3_preprocess_skipped() {
    long saId = seedSchedulingAuctionWithStatus("r3_preprocess_status", "PENDING");

    updater.markSkipped(saId, "R3_PREPROCESS");

    assertThat(currentStatus(saId, "r3_preprocess_status")).isEqualTo("SKIPPED");
}
```

(The test class's existing `seedSchedulingAuctionWithStatus(column, status)` helper already takes a column parameter from sub-project 5's tests — reuse.)

- [ ] **Step 2: Run — should fail with "Unknown recalc process: R3_PREPROCESS"**

Run: `cd backend && mvn -q test -Dtest=RecalcStatusUpdaterTest`
Expected: FAIL — both new R3 cases throw `IllegalArgumentException`.

- [ ] **Step 3: Extend `columnPrefix`**

In `RecalcStatusUpdater.java`, update the switch:

```java
private static String columnPrefix(String process) {
    return switch (process) {
        case "RANKING"        -> "ranking";
        case "TARGET_PRICE"   -> "target_price";
        case "R2_INIT"        -> "r2_init";
        case "R3_PREPROCESS"  -> "r3_preprocess";
        case "R3_INIT"        -> "r3_init";
        case null -> throw new IllegalArgumentException(
            "process must not be null");
        default -> throw new IllegalArgumentException(
            "Unknown recalc process: " + process);
    };
}
```

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=RecalcStatusUpdaterTest`
Expected: PASS — all six cases (3 existing + 3 new).

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdaterTest.java
git commit -m "feat(6): RecalcStatusUpdater handles R3_PREPROCESS + R3_INIT processes"
```

---

## Task 5: Add `Process.R3_PREPROCESS` and `Process.R3_INIT`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java`

- [ ] **Step 1: Add the enum constants**

In `RecalcAlreadyRunningException.java`:

```java
public enum Process { RANKING, TARGET_PRICE, R2_INIT, R3_PREPROCESS, R3_INIT }
```

Update the class-level Javadoc to reference the new processes:

```java
/**
 * Thrown by RANKING / TARGET_PRICE / R2_INIT / R3_PREPROCESS / R3_INIT services when their
 * state-flip UPDATE matches 0 rows — meaning another caller (cron tick or admin endpoint)
 * already flipped the status to RUNNING. Maps to HTTP 409 in
 * GlobalExceptionHandler.
 */
```

The existing `GlobalExceptionHandler` mapping (409) needs no change.

- [ ] **Step 2: Smoke compile**

Run: `cd backend && mvn -q compile`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java
git commit -m "feat(6): RecalcAlreadyRunningException supports R3_PREPROCESS + R3_INIT"
```

---

## Task 6: Rename `bulkInsertForR2` → `bulkInsertForRound`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryCustom.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryImpl.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentService.java`
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryR2IT.java`

- [ ] **Step 1: Rename method in fragment interface**

In `QualifiedBuyerCodeRepositoryCustom.java`:

```java
// BEFORE
int bulkInsertForR2(Long saId, Long[] qualifiedIds, Long[] specialIds);

// AFTER
/**
 * Round-agnostic three-set QBC bulk INSERT. Used by both R2 buyer assignment
 * (sub-project 5) and R3 pre-process (sub-project 6). Writes one row per active
 * Wholesale/Data_Wipe buyer code with derived qualification_type, included,
 * and is_special_treatment columns.
 */
int bulkInsertForRound(Long saId, Long[] qualifiedIds, Long[] specialIds);
```

- [ ] **Step 2: Rename method in impl**

In `QualifiedBuyerCodeRepositoryImpl.java`, rename the method body. SQL is unchanged:

```java
@Override
@Transactional(propagation = Propagation.MANDATORY)
public int bulkInsertForRound(Long saId, Long[] qualifiedIds, Long[] specialIds) {
    // ... existing JDBC body unchanged ...
}
```

- [ ] **Step 3: Update the R2 service call site**

In `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentService.java`, find the `bulkInsertForR2(...)` call and rename to `bulkInsertForRound(...)`. Signature unchanged.

- [ ] **Step 4: Update test references**

In `backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryR2IT.java`, rename every `bulkInsertForR2(...)` call to `bulkInsertForRound(...)`.

- [ ] **Step 5: Run R2 + QBC tests to verify rename clean**

Run: `cd backend && mvn -q test -Dtest='QualifiedBuyerCodeRepositoryR2IT,R2BuyerAssignmentServiceTest,R2BuyerAssignmentEndToEndIT'`
Expected: PASS — three test classes, all green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryCustom.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryImpl.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentService.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryR2IT.java
git commit -m "refactor(6): rename QualifiedBuyerCodeRepository.bulkInsertForR2 → bulkInsertForRound"
```

---

## Task 7: Test fixture for R3 lifecycle

**Files:**
- Create: `backend/src/test/resources/fixtures/auctions/r3-lifecycle-seed.sql`

This fixture is reused across Tasks 8, 9, 10, 11, and the end-to-end IT (Task 18). Build it once, here.

- [ ] **Step 1: Read the existing R2 seed for shape parity**

Use the `Read` tool on `backend/src/test/resources/fixtures/auctions/r2-init-seed.sql`. Mirror its structure (week → auction → SAs → BRSF → AE → buyers → buyer codes → bid_data) and extend.

- [ ] **Step 2: Write the fixture**

Create `backend/src/test/resources/fixtures/auctions/r3-lifecycle-seed.sql`:

```sql
-- R3 lifecycle test fixture — seeds two auctions:
--   Auction 600 (full path): R1 + R2 + R3 SAs (R3 has has_round=true)
--   Auction 601 (no R3):      R1 + R2 SAs only (listener silent-skip)
-- Plus auction 602 with R3 SA where has_round=false (SKIPPED path).
-- IDs are chosen to not collide with sub-project 5's r2-init-seed.sql.

-- ─── weeks + auctions ────────────────────────────────────────────────────
INSERT INTO mdm.week (id, year, week, week_year, created_date, changed_date) VALUES
    (601, 2026, 19, '2026-19', NOW(), NOW()),
    (602, 2026, 20, '2026-20', NOW(), NOW()),
    (603, 2026, 21, '2026-21', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO auctions.auctions (id, week_id, name, status, created_date, changed_date) VALUES
    (600, 601, 'R3-Test-Full',     'IN_PROGRESS', NOW(), NOW()),
    (601, 602, 'R3-Test-NoR3',     'IN_PROGRESS', NOW(), NOW()),
    (602, 603, 'R3-Test-DisabledR3','IN_PROGRESS', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── scheduling auctions ─────────────────────────────────────────────────
-- Auction 600: R1 closed, R2 closed, R3 just-started (has_round=true)
INSERT INTO auctions.scheduling_auctions
    (id, auction_id, round, has_round, round3_init_status, created_date, changed_date) VALUES
    (6001, 600, 1, true, 'Pending', NOW(), NOW()),
    (6002, 600, 2, true, 'Pending', NOW(), NOW()),
    (6003, 600, 3, true, 'Pending', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Auction 601: R1 + R2 only (no R3 SA at all)
INSERT INTO auctions.scheduling_auctions
    (id, auction_id, round, has_round, round3_init_status, created_date, changed_date) VALUES
    (6011, 601, 1, true, 'Pending', NOW(), NOW()),
    (6012, 601, 2, true, 'Pending', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Auction 602: R3 SA exists but has_round=false
INSERT INTO auctions.scheduling_auctions
    (id, auction_id, round, has_round, round3_init_status, created_date, changed_date) VALUES
    (6021, 602, 1, true,  'Pending', NOW(), NOW()),
    (6022, 602, 2, true,  'Pending', NOW(), NOW()),
    (6023, 602, 3, false, 'Pending', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── bid_round_selection_filters ─────────────────────────────────────────
-- Round 2: target_percent=5 (whole-percent per sub-project 6 alignment), target_value=1
-- Round 3: bid_percentage_variation=5, bid_amount_variation=1, rank_qualification_limit=3
INSERT INTO auctions.bid_round_selection_filters
    (id, round, target_percent, target_value, regular_buyer_qualification,
     regular_buyer_inventory_options, stb_allow_all_buyers_override,
     bid_percentage_variation, bid_amount_variation, rank_qualification_limit,
     created_date, changed_date) VALUES
    (601, 2, 5, 1.00, 'Only_Qualified', 'InventoryRound1QualifiedBids', false, NULL, NULL, NULL, NOW(), NOW()),
    (602, 3, NULL, NULL, 'Only_Qualified', 'InventoryRound1QualifiedBids', false, 5, 1.00, 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── aggregated inventory ────────────────────────────────────────────────
-- 6 (ecoid, grade) rows for week 601, mix of DW + WH; one with NULL round3_bid_rank
INSERT INTO auctions.aggregated_inventory
    (id, week_id, ecoid2, merged_grade, total_quantity, dw_total_quantity,
     avg_target_price, dw_avg_target_price, round3_target_price,
     is_deprecated, created_date, changed_date) VALUES
    (60001, 601, 'ECO-A', 'Grade_A', 100, 50, 100.00, 90.00, 100.00, false, NOW(), NOW()),
    (60002, 601, 'ECO-B', 'Grade_A', 200, 80, 50.00,  45.00,  50.00, false, NOW(), NOW()),
    (60003, 601, 'ECO-C', 'Grade_B', 150, 60, 75.00,  70.00,  75.00, false, NOW(), NOW()),
    (60004, 601, 'ECO-D', 'Grade_B', 100, 40, 25.00,  22.00,  25.00, false, NOW(), NOW()),
    (60005, 601, 'ECO-E', 'Grade_C',  80, 30, 40.00,  38.00,  40.00, false, NOW(), NOW()),
    (60006, 601, 'ECO-F', 'Grade_C',  60, 20, 60.00,  55.00,  60.00, true,  NOW(), NOW())  -- deprecated
ON CONFLICT (id) DO NOTHING;

-- ─── buyers (5: 2 special, 3 regular) ────────────────────────────────────
INSERT INTO buyer_mgmt.buyers (id, company_name, status, is_special_buyer, created_date, changed_date) VALUES
    (6001, 'Acme Corp',     'Active', false, NOW(), NOW()),
    (6002, 'Beta Inc',      'Active', false, NOW(), NOW()),
    (6003, 'Gamma LLC',     'Active', false, NOW(), NOW()),
    (6004, 'Delta SpecialA', 'Active', true,  NOW(), NOW()),  -- STB-eligible (no prior bids)
    (6005, 'Epsilon SpecialB','Active',true,  NOW(), NOW())   -- NOT STB-eligible (will have prior bid)
ON CONFLICT (id) DO NOTHING;

-- ─── buyer codes (mix of DW, Wholesale; some Inactive-buyer to exclude) ──
INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, soft_deleted, created_date, changed_date) VALUES
    (60001, 'ACME-WH',  'Wholesale',  false, NOW(), NOW()),
    (60002, 'ACME-DW',  'Data_Wipe',  false, NOW(), NOW()),
    (60003, 'BETA-WH',  'Wholesale',  false, NOW(), NOW()),
    (60004, 'GAMMA-WH', 'Wholesale',  false, NOW(), NOW()),
    (60005, 'DELTA-WH', 'Wholesale',  false, NOW(), NOW()),
    (60006, 'DELTA-DW', 'Data_Wipe',  false, NOW(), NOW()),
    (60007, 'EPSI-WH',  'Wholesale',  false, NOW(), NOW()),
    (60008, 'EPSI-DW',  'Data_Wipe',  false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES
    (60001, 6001), (60002, 6001),
    (60003, 6002),
    (60004, 6003),
    (60005, 6004), (60006, 6004),  -- Delta SpecialA (STB-eligible)
    (60007, 6005), (60008, 6005)   -- Epsilon SpecialB (will have prior bid)
ON CONFLICT DO NOTHING;

-- ─── bid_rounds (per buyer code + SA) ────────────────────────────────────
-- R1 + R2 bid_rounds for auction 600
INSERT INTO auctions.bid_rounds
    (id, scheduling_auction_id, buyer_code_id, submitted, submitted_datetime, created_date, changed_date) VALUES
    (60001, 6001, 60001, true, '2026-05-01 10:00:00+00', NOW(), NOW()),  -- ACME-WH R1
    (60002, 6001, 60002, true, '2026-05-01 10:00:00+00', NOW(), NOW()),  -- ACME-DW R1
    (60003, 6001, 60003, true, '2026-05-01 10:00:00+00', NOW(), NOW()),  -- BETA-WH  R1
    (60004, 6001, 60004, true, '2026-05-01 10:00:00+00', NOW(), NOW()),  -- GAMMA-WH R1
    (60005, 6001, 60007, true, '2026-05-01 10:00:00+00', NOW(), NOW()),  -- EPSI-WH  R1 (Epsilon prior bid)
    (60006, 6002, 60001, true, '2026-05-02 10:00:00+00', NOW(), NOW()),  -- ACME-WH R2
    (60007, 6002, 60002, true, '2026-05-02 10:00:00+00', NOW(), NOW()),  -- ACME-DW R2
    (60008, 6002, 60003, true, '2026-05-02 10:00:00+00', NOW(), NOW()),  -- BETA-WH  R2
    (60009, 6002, 60004, true, '2026-05-02 10:00:00+00', NOW(), NOW())   -- GAMMA-WH R2
    -- Note: DELTA-WH/DW have NO prior bid_rounds → STB-eligible
    -- Note: EPSI-DW has NO prior bid_rounds, but EPSI-WH does → Epsilon NOT STB-eligible (any code disqualifies)
ON CONFLICT (id) DO NOTHING;

-- ─── bid_data ────────────────────────────────────────────────────────────
-- Hits each R3 qualification branch:
--   ACME-WH: latest R2 bid 105 (above target 100) → qualifies via pct (5%)
--   ACME-DW: latest R2 bid 88  (above DW target 90 - 2 = 88) → qualifies via amount (1) ... actually 88 < 89; uses pct 5% → 90*0.05=4.5; 90-4.5=85.5; 88 >= 85.5 → qualifies via pct
--   BETA-WH: rank=2, no other branch matches → qualifies via rank
--   GAMMA-WH: bid below all thresholds, rank=10 → does NOT qualify
--   EPSI-WH: prior bid exists → only Epsilon's regular qualification matters; latest bid below threshold → not qualified, NOT STB
-- Plus deletion-target rows on R2 SA with bid_amount=0 and bid_amount=NULL
INSERT INTO auctions.bid_data
    (id, bid_round_id, buyer_code_id, aggregated_inventory_id, ecoid, merged_grade,
     code, bid_amount, submitted_bid_amount, submitted_datetime, bid_quantity, target_price,
     buyer_code_type, bid_round, week_id, round3_bid_rank, highest_bid,
     created_date, changed_date) VALUES
    -- ACME-WH bids: R1=80, R2=105 (latest, qualifies via pct)
    (60001, 60001, 60001, 60001, 'ECO-A', 'Grade_A', 'ACME-WH',  80, 80,  '2026-05-01 10:00:00+00', 10, 100.00, 'Wholesale', 1, 601, NULL, false, NOW(), NOW()),
    (60002, 60006, 60001, 60001, 'ECO-A', 'Grade_A', 'ACME-WH', 105, 105, '2026-05-02 10:00:00+00', 10, 100.00, 'Wholesale', 2, 601, 5,    false, NOW(), NOW()),
    -- ACME-DW: R2=88 (qualifies via pct on dw_avg=90)
    (60003, 60007, 60002, 60001, 'ECO-A', 'Grade_A', 'ACME-DW',  88, 88,  '2026-05-02 10:00:00+00',  5,  90.00, 'Data_Wipe', 2, 601, 4,    false, NOW(), NOW()),
    -- BETA-WH: R2=20 (well below target 50 by both pct and amount), rank=2 (qualifies via rank<=3)
    (60004, 60008, 60003, 60002, 'ECO-B', 'Grade_A', 'BETA-WH',  20, 20,  '2026-05-02 10:00:00+00',  5,  50.00, 'Wholesale', 2, 601, 2,    false, NOW(), NOW()),
    -- GAMMA-WH: R2=10 (well below target 75), rank=10 (above limit 3) → does NOT qualify
    (60005, 60009, 60004, 60003, 'ECO-C', 'Grade_B', 'GAMMA-WH', 10, 10,  '2026-05-02 10:00:00+00',  3,  75.00, 'Wholesale', 2, 601, 10,   false, NOW(), NOW()),
    -- EPSI-WH: R1=15 (Epsilon's prior bid that disqualifies STB), well below target → not qualified
    (60006, 60005, 60007, 60004, 'ECO-D', 'Grade_B', 'EPSI-WH',  15, 15,  '2026-05-01 10:00:00+00',  2,  25.00, 'Wholesale', 1, 601, 12,   false, NOW(), NOW()),
    -- Deletion targets on R2 SA: bid_amount=0 and bid_amount=NULL
    (60007, 60006, 60001, 60005, 'ECO-E', 'Grade_C', 'ACME-WH',  0,  NULL, NULL, 0, 40.00, 'Wholesale', 2, 601, NULL, false, NOW(), NOW()),
    (60008, 60008, 60003, 60005, 'ECO-E', 'Grade_C', 'BETA-WH',  NULL, NULL, NULL, 0, 40.00, 'Wholesale', 2, 601, NULL, false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
```

(Verify column names against the production `auctions.bid_data` and `auctions.aggregated_inventory` schemas before running. If a column was added/renamed since this fixture was drafted, update accordingly. This is a known plan-phase verification step — see design §11.)

- [ ] **Step 3: Smoke-load via a temp Flyway-migrated DB**

```bash
cd backend && mvn -q -Dtest='*FlywayMigrationsIT*' test
```

Then in a manual psql session against the dev DB (or the testcontainer used by ITs):

```bash
psql -h localhost -U salesplatform -d salesplatform_dev \
  -f src/test/resources/fixtures/auctions/r3-lifecycle-seed.sql
```

Expected: no errors. Verify rows exist with `SELECT COUNT(*) FROM auctions.scheduling_auctions WHERE id IN (6001,6002,6003,6011,6012,6021,6022,6023);` → 8.

- [ ] **Step 4: Commit**

```bash
git add backend/src/test/resources/fixtures/auctions/r3-lifecycle-seed.sql
git commit -m "test(6): add r3-lifecycle-seed.sql fixture"
```

---

## Task 8: `R3PreProcessSupportRepository` (bulk DELETE)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

Create `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepositoryIT.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R3PreProcessSupportRepositoryIT {

    @Autowired R3PreProcessSupportRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    @DisplayName("deleteUnsubmittedBids removes only bid_amount=0 OR NULL rows on the R2 SA")
    void deletes_only_unsubmitted() {
        long r2SaId = 6002L;
        Integer beforeUnsubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)",
            Integer.class, r2SaId);
        assertThat(beforeUnsubmitted).isEqualTo(2);

        int deleted = repo.deleteUnsubmittedBids(r2SaId);

        assertThat(deleted).isEqualTo(2);

        Integer afterUnsubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)",
            Integer.class, r2SaId);
        assertThat(afterUnsubmitted).isZero();
    }

    @Test
    @DisplayName("deleteUnsubmittedBids leaves submitted (bid_amount > 0) rows untouched")
    void leaves_submitted_alone() {
        long r2SaId = 6002L;
        Integer beforeSubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND bd.bid_amount > 0",
            Integer.class, r2SaId);

        repo.deleteUnsubmittedBids(r2SaId);

        Integer afterSubmitted = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data bd " +
            "JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id " +
            "WHERE br.scheduling_auction_id = ? AND bd.bid_amount > 0",
            Integer.class, r2SaId);
        assertThat(afterSubmitted).isEqualTo(beforeSubmitted);
    }

    @Test
    @DisplayName("rerun is idempotent — second call returns 0")
    void rerun_idempotent() {
        repo.deleteUnsubmittedBids(6002L);
        int second = repo.deleteUnsubmittedBids(6002L);
        assertThat(second).isZero();
    }
}
```

- [ ] **Step 2: Run — should fail (class does not exist)**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessSupportRepositoryIT`
Expected: FAIL — class not found.

- [ ] **Step 3: Implement `R3PreProcessSupportRepository`**

Create `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepository.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pre-process Phase 1 — delete unsubmitted R2 bids.
 *
 * <p>Ports Mendix {@code SUB_Round2_DeleteUnsubmittedBids}: removes
 * bid_data rows on the R2 SA where {@code bid_amount = 0 OR bid_amount IS NULL}.
 * Joins through {@code auctions.bid_rounds} to scope to the R2 SA's bid_rounds.
 */
@Repository
public class R3PreProcessSupportRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String DELETE_UNSUBMITTED_SQL = """
        DELETE FROM auctions.bid_data bd
         USING auctions.bid_rounds br
         WHERE bd.bid_round_id = br.id
           AND br.scheduling_auction_id = CAST(:r2_sa_id AS bigint)
           AND (bd.bid_amount = 0 OR bd.bid_amount IS NULL)
        """;

    @Transactional(propagation = Propagation.MANDATORY)
    public int deleteUnsubmittedBids(long r2SchedulingAuctionId) {
        return em.createNativeQuery(DELETE_UNSUBMITTED_SQL)
            .setParameter("r2_sa_id", r2SchedulingAuctionId)
            .executeUpdate();
    }
}
```

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessSupportRepositoryIT`
Expected: PASS — three test cases.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3PreProcessSupportRepositoryIT.java
git commit -m "feat(6): R3PreProcessSupportRepository ports SUB_Round2_DeleteUnsubmittedBids"
```

---

## Task 9: `R3BuyerQualificationRepository` (regular CTE)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

Create `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepositoryIT.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R3BuyerQualificationRepositoryIT {

    @Autowired R3BuyerQualificationRepository repo;
    @Autowired JdbcTemplate jdbc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("ACME-WH qualifies via percentage variation (R2 bid 105 vs target 100, pct=5)")
    void percentage_branch_qualifies() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60001L);  // ACME-WH
    }

    @Test
    @DisplayName("ACME-DW qualifies via percentage variation (R2 bid 88 vs DW target 90, pct=5)")
    void percentage_branch_dw_uses_dw_target() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60002L);  // ACME-DW
    }

    @Test
    @DisplayName("BETA-WH qualifies via rank limit (rank=2 <= limit=3)")
    void rank_branch_qualifies() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60003L);  // BETA-WH
    }

    @Test
    @DisplayName("GAMMA-WH does NOT qualify — bid below pct/amt thresholds AND rank>limit")
    void no_branch_matches_excluded() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).doesNotContain(60004L);  // GAMMA-WH
    }

    @Test
    @DisplayName("ROW_NUMBER() keeps R2 over R1 when buyer has both")
    void latest_bid_is_r2_when_both_exist() {
        // ACME-WH has R1 (80) + R2 (105). The CTE should use 105 (R2 latest).
        // 105 >= 100 - (100*5/100) = 95 → qualifies
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60001L);
    }

    @Test
    @DisplayName("rerun is idempotent — same R3 SA produces same set")
    void rerun_idempotent() {
        Set<Long> a = repo.qualifiedBuyerCodes(R3_SA_ID);
        Set<Long> b = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("all-NULL filters → fall-through qualify everyone with bids")
    void all_null_filters_qualifies_all() {
        // Override BRSF round=3 to NULL all three
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET bid_percentage_variation = NULL, bid_amount_variation = NULL, " +
                    "    rank_qualification_limit = NULL " +
                    "WHERE round = 3");

        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        // Everyone with any submitted nonzero bid in R1/R2 is in (DISTINCT buyer_code_id):
        // ACME-WH (R1+R2), ACME-DW (R2), BETA-WH (R2), GAMMA-WH (R2), EPSI-WH (R1)
        assertThat(ids).contains(60001L, 60002L, 60003L, 60004L, 60007L);
    }

    @Test
    @DisplayName("amount-variation branch qualifies when latest_bid >= target - amount")
    void amount_branch_qualifies() {
        // Set up: only amount branch active
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET bid_percentage_variation = NULL, bid_amount_variation = 1.00, " +
                    "    rank_qualification_limit = NULL " +
                    "WHERE round = 3");

        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        // ACME-WH (105 >= 100-1=99) qualifies
        assertThat(ids).contains(60001L);
        // GAMMA-WH (10 < 75-1=74) does not
        assertThat(ids).doesNotContain(60004L);
    }

    @Test
    @DisplayName("non-DW/WH buyer codes are excluded")
    void non_dw_wh_excluded() {
        // No PO-type buyer codes seeded in fixture; sanity check that all returned ids are DW/WH
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        Integer nonDwWhCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM buyer_mgmt.buyer_codes WHERE id = ANY(?) AND buyer_code_type NOT IN ('Wholesale','Data_Wipe')",
            Integer.class, ids.toArray(new Long[0]));
        assertThat(nonDwWhCount).isZero();
    }
}
```

- [ ] **Step 2: Run — should fail (class does not exist)**

Run: `cd backend && mvn -q test -Dtest=R3BuyerQualificationRepositoryIT`
Expected: FAIL — class not found.

- [ ] **Step 3: Implement `R3BuyerQualificationRepository`**

Create `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepository.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of active wholesale/data-wipe buyer-code ids that qualify
 * for round 3 of the supplied scheduling auction via the regular path.
 *
 * <p>Implements the product-owner-supplied R3 qualification rule (see design
 * §7.2): per (ecoid, grade, buyer_code) take latest bid across R1+R2,
 * compare against round3_target_price + round3_bid_rank, qualify if any of
 * three filter branches matches. All-NULL filters fall through to qualify.
 */
@Repository
public class R3BuyerQualificationRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id          AS r3_sa_id,
                 sa.auction_id  AS auction_id,
                 brsf.bid_percentage_variation  AS pct_var,
                 brsf.bid_amount_variation      AS amt_var,
                 brsf.rank_qualification_limit  AS rank_lim
            FROM auctions.scheduling_auctions sa
            JOIN auctions.bid_round_selection_filters brsf
              ON brsf.round = 3
           WHERE sa.id = CAST(:r3_sa_id AS bigint)
        ),
        latest_bid AS (
          SELECT bd.ecoid,
                 bd.merged_grade,
                 bd.submitted_bid_amount,
                 bd.round3_bid_rank,
                 br.buyer_code_id,
                 ai.round3_target_price,
                 ROW_NUMBER() OVER (
                   PARTITION BY bd.ecoid, bd.merged_grade, br.buyer_code_id
                   ORDER BY bd.submitted_datetime DESC
                 ) AS rn
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id
            JOIN auctions.scheduling_auctions sa
              ON sa.id = br.scheduling_auction_id
            JOIN buyer_mgmt.buyer_codes bc   ON bc.id = br.buyer_code_id
            JOIN auctions.aggregated_inventory ai
              ON ai.id = bd.aggregated_inventory_id, params p
           WHERE sa.auction_id = p.auction_id
             AND sa.round IN (1, 2)
             AND br.submitted = TRUE
             AND bd.submitted_bid_amount > 0
             AND bc.buyer_code_type IN ('Data_Wipe','Wholesale')
        ),
        filtered_latest AS (
          SELECT * FROM latest_bid WHERE rn = 1
        )
        SELECT DISTINCT fl.buyer_code_id
          FROM filtered_latest fl, params p
         WHERE
           (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
           OR (p.pct_var IS NOT NULL
               AND fl.submitted_bid_amount
                   >= fl.round3_target_price - (fl.round3_target_price * p.pct_var / 100))
           OR (p.amt_var IS NOT NULL
               AND fl.submitted_bid_amount >= fl.round3_target_price - p.amt_var)
           OR (p.rank_lim IS NOT NULL
               AND fl.round3_bid_rank IS NOT NULL
               AND fl.round3_bid_rank <= p.rank_lim)
        """;

    public Set<Long> qualifiedBuyerCodes(long r3SchedulingAuctionId) {
        @SuppressWarnings("unchecked")
        List<Number> rows = em.createNativeQuery(CTE_SQL)
            .setParameter("r3_sa_id", r3SchedulingAuctionId)
            .getResultList();
        Set<Long> ids = new HashSet<>(rows.size());
        for (Number n : rows) ids.add(n.longValue());
        return ids;
    }
}
```

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3BuyerQualificationRepositoryIT`
Expected: PASS — all nine cases.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3BuyerQualificationRepositoryIT.java
git commit -m "feat(6): R3BuyerQualificationRepository ports new R3 selection criteria CTE"
```

---

## Task 10: `R3SpecialBuyerRepository` (STB CTE)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

Create `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepositoryIT.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R3SpecialBuyerRepositoryIT {

    @Autowired R3SpecialBuyerRepository repo;
    @Autowired JdbcTemplate jdbc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("Delta SpecialA's codes (no R1/R2 prior bids) qualify as STB")
    void zero_prior_bids_is_stb() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // Delta SpecialA: DELTA-WH (60005), DELTA-DW (60006)
        assertThat(ids).contains(60005L, 60006L);
    }

    @Test
    @DisplayName("Epsilon SpecialB has prior R1 bid on EPSI-WH → entire DW/WH set disqualified")
    void any_prior_bid_disqualifies_whole_set() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // Epsilon SpecialB: EPSI-WH (60007) had R1 bid → both EPSI-WH and EPSI-DW excluded
        assertThat(ids).doesNotContain(60007L, 60008L);
    }

    @Test
    @DisplayName("Non-special buyers (Acme/Beta/Gamma) excluded entirely")
    void non_special_buyers_excluded() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        assertThat(ids).doesNotContain(60001L, 60002L, 60003L, 60004L);
    }

    @Test
    @DisplayName("stb_allow_all_buyers_override=TRUE returns all special-buyer codes regardless of prior bids")
    void override_returns_all_special_codes() {
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET stb_allow_all_buyers_override = TRUE WHERE round = 3");

        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // Both Delta + Epsilon's codes returned despite Epsilon's prior bid
        assertThat(ids).contains(60005L, 60006L, 60007L, 60008L);
    }

    @Test
    @DisplayName("rerun is idempotent")
    void rerun_idempotent() {
        Set<Long> a = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        Set<Long> b = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        assertThat(a).isEqualTo(b);
    }
}
```

- [ ] **Step 2: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3SpecialBuyerRepositoryIT`
Expected: FAIL — class not found.

- [ ] **Step 3: Implement `R3SpecialBuyerRepository`**

Create `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepository.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of special-treatment buyer-code ids for round 3.
 *
 * <p>A buyer code is STB-eligible iff:
 * <ul>
 *   <li>The buyer is flagged {@code is_special_buyer = TRUE}, AND</li>
 *   <li>EVERY DW/WH code owned by that buyer satisfies one of:
 *     <ul>
 *       <li>{@code stb_allow_all_buyers_override = TRUE} on round-3 BRSF, OR</li>
 *       <li>The buyer has zero submitted bids on that code in any prior round
 *           (round &lt; 3, i.e. rounds 1 and 2)</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * Mirror of sub-project 5's {@code R2SpecialBuyerRepository} with the prior-round
 * predicate widened to include round 2.
 */
@Repository
public class R3SpecialBuyerRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id          AS r3_sa_id,
                 sa.auction_id  AS auction_id,
                 sa.round       AS round,
                 brsf.stb_allow_all_buyers_override
            FROM auctions.scheduling_auctions sa
            JOIN auctions.bid_round_selection_filters brsf
              ON brsf.round = sa.round
           WHERE sa.id = CAST(:r3_sa_id AS bigint)
        ),
        special_buyers AS (
          SELECT b.id AS buyer_id
            FROM buyer_mgmt.buyers b
           WHERE b.is_special_buyer = TRUE
        ),
        special_dwwh_codes AS (
          SELECT bcb.buyer_id, bc.id AS buyer_code_id
            FROM buyer_mgmt.buyer_code_buyers bcb
            JOIN special_buyers sb ON sb.buyer_id = bcb.buyer_id
            JOIN buyer_mgmt.buyer_codes bc
              ON bc.id = bcb.buyer_code_id
             AND bc.buyer_code_type IN ('Wholesale','Data_Wipe')
        ),
        prior_round_bids AS (
          SELECT bd.buyer_code_id, bcb2.buyer_id, COUNT(*) AS bid_count
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br      ON br.id = bd.bid_round_id AND br.submitted = TRUE
            JOIN auctions.scheduling_auctions sa
              ON sa.id = br.scheduling_auction_id
            JOIN params p
              ON p.auction_id = sa.auction_id
             AND sa.round < p.round
            JOIN buyer_mgmt.buyer_code_buyers bcb2
              ON bcb2.buyer_code_id = bd.buyer_code_id
           GROUP BY bd.buyer_code_id, bcb2.buyer_id
        ),
        code_is_stb AS (
          SELECT s.buyer_id, s.buyer_code_id,
                 CASE
                   WHEN p.stb_allow_all_buyers_override = TRUE THEN TRUE
                   WHEN COALESCE(prb.bid_count, 0) = 0          THEN TRUE
                   ELSE FALSE
                 END AS is_stb
            FROM special_dwwh_codes s
            LEFT JOIN prior_round_bids prb
              ON prb.buyer_id = s.buyer_id
             AND prb.buyer_code_id = s.buyer_code_id
            CROSS JOIN params p
        ),
        buyers_all_codes_stb AS (
          SELECT buyer_id
            FROM code_is_stb
           GROUP BY buyer_id
          HAVING bool_and(is_stb) = TRUE
        )
        SELECT cs.buyer_code_id
          FROM code_is_stb cs
          JOIN buyers_all_codes_stb bs ON bs.buyer_id = cs.buyer_id
        """;

    public Set<Long> specialTreatmentBuyerCodes(long r3SchedulingAuctionId) {
        @SuppressWarnings("unchecked")
        List<Number> rows = em.createNativeQuery(CTE_SQL)
            .setParameter("r3_sa_id", r3SchedulingAuctionId)
            .getResultList();
        Set<Long> ids = new HashSet<>(rows.size());
        for (Number n : rows) ids.add(n.longValue());
        return ids;
    }
}
```

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3SpecialBuyerRepositoryIT`
Expected: PASS — all five cases.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R3SpecialBuyerRepositoryIT.java
git commit -m "feat(6): R3SpecialBuyerRepository ports STB CTE for prior_round in {1,2}"
```

---

## Task 11: Extend `Round3BuyerDataReportRepository` — bulk insert + delete

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepositoryR3IT.java`

- [ ] **Step 1: Read the existing repo to understand its shape**

Use the `Read` tool on `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepository.java`. Note its current method signatures and whether it's a Spring Data interface or a custom class. The two new methods follow whichever pattern is in place.

- [ ] **Step 2: Write the failing test**

Create `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepositoryR3IT.java`:

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class Round3BuyerDataReportRepositoryR3IT {

    @Autowired Round3BuyerDataReportRepository repo;
    @Autowired JdbcTemplate jdbc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("bulkInsertForSchedulingAuction writes one row per company with comma-joined codes")
    void writes_one_row_per_company() {
        // Seed QBC rows directly (this test isolates the report generation)
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes (scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) VALUES " +
            "(?, 60001, 'Qualified', TRUE, FALSE, NOW(), NOW())," +
            "(?, 60002, 'Qualified', TRUE, FALSE, NOW(), NOW())," +
            "(?, 60003, 'Qualified', TRUE, FALSE, NOW(), NOW())",
            R3_SA_ID, R3_SA_ID, R3_SA_ID);

        int inserted = repo.bulkInsertForSchedulingAuction(R3_SA_ID);

        // 60001+60002 belong to Acme; 60003 to Beta → 2 rows
        assertThat(inserted).isEqualTo(2);
        List<Round3BuyerDataReport> rows = repo.findBySchedulingAuctionId(R3_SA_ID);
        assertThat(rows).hasSize(2);

        Round3BuyerDataReport acme = rows.stream()
            .filter(r -> r.getCompanyName().equals("Acme Corp")).findFirst().orElseThrow();
        assertThat(acme.getBuyerCodes()).isEqualTo("ACME-DW,ACME-WH");  // alphabetical
    }

    @Test
    @DisplayName("only Qualified+included rows feed in (Not_Qualified excluded)")
    void filters_not_qualified() {
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes (scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) VALUES " +
            "(?, 60001, 'Qualified',     TRUE,  FALSE, NOW(), NOW())," +
            "(?, 60003, 'Not_Qualified', FALSE, FALSE, NOW(), NOW())",
            R3_SA_ID, R3_SA_ID);

        repo.bulkInsertForSchedulingAuction(R3_SA_ID);
        List<Round3BuyerDataReport> rows = repo.findBySchedulingAuctionId(R3_SA_ID);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getCompanyName()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("deleteBySchedulingAuctionId clears reports for that SA only")
    void delete_clears_only_target_sa() {
        jdbc.update(
            "INSERT INTO buyer_mgmt.qualified_buyer_codes (scheduling_auction_id, buyer_code_id, qualification_type, included, is_special_treatment, created_date, changed_date) VALUES (?, 60001, 'Qualified', TRUE, FALSE, NOW(), NOW())",
            R3_SA_ID);
        repo.bulkInsertForSchedulingAuction(R3_SA_ID);

        int deleted = repo.deleteBySchedulingAuctionId(R3_SA_ID);

        assertThat(deleted).isGreaterThan(0);
        assertThat(repo.findBySchedulingAuctionId(R3_SA_ID)).isEmpty();
    }
}
```

- [ ] **Step 3: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=Round3BuyerDataReportRepositoryR3IT`
Expected: FAIL — methods don't exist.

- [ ] **Step 4: Add the two methods to `Round3BuyerDataReportRepository`**

If it's a Spring Data interface, add `@Modifying @Query` methods:

```java
@Modifying
@Transactional(propagation = Propagation.MANDATORY)
@Query(value = """
    INSERT INTO auctions.round3_buyer_data_reports (
        scheduling_auction_id, company_name, buyer_codes, created_date, changed_date
    )
    SELECT :saId,
           b.company_name,
           string_agg(bc.code, ',' ORDER BY bc.code),
           NOW(), NOW()
      FROM buyer_mgmt.qualified_buyer_codes qbc
      JOIN buyer_mgmt.buyer_codes bc        ON bc.id = qbc.buyer_code_id
      JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
      JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
     WHERE qbc.scheduling_auction_id = :saId
       AND qbc.qualification_type    = 'Qualified'
       AND qbc.included              = TRUE
     GROUP BY b.company_name
    """, nativeQuery = true)
int bulkInsertForSchedulingAuction(@Param("saId") Long saId);

@Modifying
@Transactional(propagation = Propagation.MANDATORY)
@Query(value = "DELETE FROM auctions.round3_buyer_data_reports WHERE scheduling_auction_id = :saId",
       nativeQuery = true)
int deleteBySchedulingAuctionId(@Param("saId") Long saId);

List<Round3BuyerDataReport> findBySchedulingAuctionId(Long schedulingAuctionId);
```

If `findBySchedulingAuctionId` already exists in the repo, skip that line. If the column name on the entity differs (e.g., `companyName` vs `company_name`), adjust accordingly — verify by reading the `Round3BuyerDataReport` entity first.

- [ ] **Step 5: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=Round3BuyerDataReportRepositoryR3IT`
Expected: PASS — all three cases.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/Round3BuyerDataReportRepositoryR3IT.java
git commit -m "feat(6): Round3BuyerDataReportRepository — bulkInsertForSchedulingAuction + deleteBySchedulingAuctionId"
```

---

## Task 12: `R3PreProcessService` + `R3PreProcessResult` + completion event

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessService.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessResult.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/R3PreProcessCompletedEvent.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessServiceTest.java`

- [ ] **Step 1: Write the result record**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessResult.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

public record R3PreProcessResult(
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int reportRowCount,
    int deletedBidsCount,
    long durationMs,
    boolean skipped
) {}
```

- [ ] **Step 2: Write the event record**

Create `backend/src/main/java/com/ecoatm/salesplatform/event/R3PreProcessCompletedEvent.java`:

```java
package com.ecoatm.salesplatform.event;

public record R3PreProcessCompletedEvent(
    long schedulingAuctionId,
    long auctionId,
    int qualifiedCount,
    int specialTreatmentCount,
    int reportRowCount
) {}
```

- [ ] **Step 3: Write the failing service tests**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessServiceTest.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3PreProcessCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.R3BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R3PreProcessSupportRepository;
import com.ecoatm.salesplatform.repository.auctions.R3SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3PreProcessServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock R3PreProcessSupportRepository supportRepo;
    @Mock R3BuyerQualificationRepository qualRepo;
    @Mock R3SpecialBuyerRepository       specialRepo;
    @Mock QualifiedBuyerCodeRepository   qbcRepo;
    @Mock Round3BuyerDataReportRepository reportRepo;
    @Mock RecalcStatusUpdater            statusUpdater;
    @Mock ApplicationEventPublisher      events;

    @InjectMocks R3PreProcessService service;

    private SchedulingAuction r2Sa(long id, long auctionId) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(auctionId); sa.setRound(2); sa.setHasRound(true);
        return sa;
    }
    private SchedulingAuction r3Sa(long id, long auctionId, boolean hasRound) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(auctionId); sa.setRound(3); sa.setHasRound(hasRound);
        return sa;
    }

    @Test
    @DisplayName("happy path — all 5 phases run, status flip RUNNING→SUCCESS, event published")
    void happy_path() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(supportRepo.deleteUnsubmittedBids(r2)).thenReturn(2);
        when(qualRepo.qualifiedBuyerCodes(r3)).thenReturn(Set.of(60001L, 60002L));
        when(specialRepo.specialTreatmentBuyerCodes(r3)).thenReturn(Set.of(60005L));
        when(qbcRepo.bulkInsertForRound(eq(r3), any(Long[].class), any(Long[].class))).thenReturn(8);
        when(reportRepo.bulkInsertForSchedulingAuction(r3)).thenReturn(2);

        R3PreProcessResult result = service.run(r2, r3);

        assertThat(result.qualifiedCount()).isEqualTo(3);  // 2 qual + 1 special
        assertThat(result.specialTreatmentCount()).isEqualTo(1);
        assertThat(result.notQualifiedCount()).isEqualTo(5);  // 8 total - 3 qualified
        assertThat(result.reportRowCount()).isEqualTo(2);
        assertThat(result.deletedBidsCount()).isEqualTo(2);
        assertThat(result.skipped()).isFalse();

        verify(statusUpdater).markSuccess(r3, "R3_PREPROCESS");
        verify(events).publishEvent(any(R3PreProcessCompletedEvent.class));
    }

    @Test
    @DisplayName("has_round=false on R3 SA → SKIPPED, no QBC writes, no event")
    void has_round_false_skips() {
        long r2 = 6022L, r3 = 6023L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 602L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 602L, false)));

        R3PreProcessResult result = service.run(r2, r3);

        assertThat(result.skipped()).isTrue();
        assertThat(result.qualifiedCount()).isZero();
        verify(statusUpdater).markSkipped(r3, "R3_PREPROCESS");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), eq("R3_PREPROCESS"));
        verify(qbcRepo, never()).bulkInsertForRound(anyLong(), any(), any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("R2 SA round != 2 → IllegalArgumentException pre-flip")
    void r2_round_mismatch_throws() {
        long r2 = 6001L, r3 = 6003L;
        SchedulingAuction wrongRound = new SchedulingAuction();
        wrongRound.setId(r2); wrongRound.setAuctionId(600L); wrongRound.setRound(1);
        when(saRepo.findById(r2)).thenReturn(Optional.of(wrongRound));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("round-2");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), any());
    }

    @Test
    @DisplayName("R3 SA round != 3 → IllegalArgumentException pre-flip")
    void r3_round_mismatch_throws() {
        long r2 = 6002L, r3 = 6002L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        // r3 same id as r2 → round=2
        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("R2/R3 SAs from different auctions → IllegalArgumentException")
    void sibling_mismatch_throws() {
        long r2 = 6002L, r3 = 6023L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));   // auction 600
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 602L, true)));  // auction 602

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("siblings");
    }

    @Test
    @DisplayName("status already RUNNING → RecalcAlreadyRunningException")
    void already_running_throws() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(false);

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(RecalcAlreadyRunningException.class);
    }

    @Test
    @DisplayName("repo throw → status flip RUNNING→FAILED with truncated error, exception propagates")
    void repo_throw_marks_failed() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(supportRepo.deleteUnsubmittedBids(r2))
            .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("boom");

        verify(statusUpdater).markFailed(eq(r3), eq("R3_PREPROCESS"),
            org.mockito.ArgumentMatchers.contains("boom"));
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("recalculate(r3SaId) resolves R2 SA via findByAuctionIdAndRound")
    void recalculate_resolves_r2_sibling() {
        long r3 = 6003L, r2 = 6002L;
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(saRepo.findByAuctionIdAndRound(600L, 2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(qbcRepo.bulkInsertForRound(eq(r3), any(), any())).thenReturn(0);

        service.recalculate(r3);

        verify(supportRepo).deleteUnsubmittedBids(r2);
    }

    @Test
    @DisplayName("recalculate throws when no R2 sibling exists")
    void recalculate_no_r2_sibling_throws() {
        long r3 = 6003L;
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(saRepo.findByAuctionIdAndRound(600L, 2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recalculate(r3))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("R2 SA");
    }

    @Test
    @DisplayName("unknown R3 SA id → EntityNotFoundException")
    void unknown_id_throws() {
        when(saRepo.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.run(6002L, 99999L))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
```

- [ ] **Step 4: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessServiceTest`
Expected: FAIL — `R3PreProcessService` class not found.

- [ ] **Step 5: Implement `R3PreProcessService`**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessService.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3PreProcessCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.R3BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R3PreProcessSupportRepository;
import com.ecoatm.salesplatform.repository.auctions.R3SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class R3PreProcessService {

    private static final String PROCESS = "R3_PREPROCESS";
    private static final Logger log = LoggerFactory.getLogger(R3PreProcessService.class);

    private final SchedulingAuctionRepository saRepo;
    private final R3PreProcessSupportRepository supportRepo;
    private final R3BuyerQualificationRepository qualRepo;
    private final R3SpecialBuyerRepository       specialRepo;
    private final QualifiedBuyerCodeRepository   qbcRepo;
    private final Round3BuyerDataReportRepository reportRepo;
    private final RecalcStatusUpdater            statusUpdater;
    private final ApplicationEventPublisher      events;

    public R3PreProcessService(SchedulingAuctionRepository saRepo,
                               R3PreProcessSupportRepository supportRepo,
                               R3BuyerQualificationRepository qualRepo,
                               R3SpecialBuyerRepository specialRepo,
                               QualifiedBuyerCodeRepository qbcRepo,
                               Round3BuyerDataReportRepository reportRepo,
                               RecalcStatusUpdater statusUpdater,
                               ApplicationEventPublisher events) {
        this.saRepo = saRepo;
        this.supportRepo = supportRepo;
        this.qualRepo = qualRepo;
        this.specialRepo = specialRepo;
        this.qbcRepo = qbcRepo;
        this.reportRepo = reportRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3PreProcessResult run(long r2SaId, long r3SaId) {
        SchedulingAuction r2Sa = saRepo.findById(r2SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r2SaId));
        SchedulingAuction r3Sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r3SaId));

        if (r2Sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "R3 pre-process expects round-2 source SA; was " + r2Sa.getRound());
        }
        if (r3Sa.getRound() != 3) {
            throw new IllegalArgumentException(
                "R3 pre-process expects round-3 target SA; was " + r3Sa.getRound());
        }
        if (!r2Sa.getAuctionId().equals(r3Sa.getAuctionId())) {
            throw new IllegalArgumentException(
                "R2/R3 SAs not siblings: r2.auctionId=" + r2Sa.getAuctionId()
                    + " r3.auctionId=" + r3Sa.getAuctionId());
        }

        if (!r3Sa.isHasRound()) {
            statusUpdater.markSkipped(r3SaId, PROCESS);
            log.info("R3_PREPROCESS skipped — has_round=false on r3SaId={}", r3SaId);
            return new R3PreProcessResult(0, 0, 0, 0, 0, 0L, true);
        }

        if (!statusUpdater.tryFlipToRunning(r3SaId, PROCESS)) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_PREPROCESS, r3SaId);
        }

        long start = System.currentTimeMillis();
        try {
            int deletedBids = supportRepo.deleteUnsubmittedBids(r2SaId);

            Set<Long> qualified = qualRepo.qualifiedBuyerCodes(r3SaId);
            Set<Long> special   = specialRepo.specialTreatmentBuyerCodes(r3SaId);

            qbcRepo.deleteBySchedulingAuctionId(r3SaId);
            int totalRows = qbcRepo.bulkInsertForRound(r3SaId,
                qualified.toArray(new Long[0]), special.toArray(new Long[0]));

            int qualifiedCount = qualified.size() + special.size();
            int specialCount   = special.size();
            int notQualified   = totalRows - qualifiedCount;

            reportRepo.deleteBySchedulingAuctionId(r3SaId);
            int reportRows = reportRepo.bulkInsertForSchedulingAuction(r3SaId);

            statusUpdater.markSuccess(r3SaId, PROCESS);
            long durationMs = System.currentTimeMillis() - start;
            log.info("R3_PREPROCESS success r3SaId={} qualified={} special={} notQual={} reports={} deleted={} ms={}",
                r3SaId, qualifiedCount, specialCount, notQualified, reportRows, deletedBids, durationMs);

            events.publishEvent(new R3PreProcessCompletedEvent(
                r3SaId, r3Sa.getAuctionId(), qualifiedCount, specialCount, reportRows));

            return new R3PreProcessResult(
                qualifiedCount, specialCount, notQualified, reportRows, deletedBids, durationMs, false);
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(r3SaId, PROCESS, msg);
            log.error("R3_PREPROCESS failed r3SaId={}", r3SaId, ex);
            throw ex;
        }
    }

    /** Admin-recovery entrypoint — takes R3 SA id only, resolves R2 SA from sibling lookup. */
    public R3PreProcessResult recalculate(long r3SaId) {
        SchedulingAuction r3Sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r3SaId));
        long r2SaId = saRepo.findByAuctionIdAndRound(r3Sa.getAuctionId(), 2)
            .map(SchedulingAuction::getId)
            .orElseThrow(() -> new IllegalStateException(
                "No R2 SA found for auctionId=" + r3Sa.getAuctionId()));
        return run(r2SaId, r3SaId);
    }
}
```

- [ ] **Step 6: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessServiceTest`
Expected: PASS — all 9 cases.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessService.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessResult.java \
        backend/src/main/java/com/ecoatm/salesplatform/event/R3PreProcessCompletedEvent.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessServiceTest.java
git commit -m "feat(6): R3PreProcessService — 5-phase port of SUB_Round3_PreProcessRoundData"
```

---

## Task 13: `R3InitService` + `R3InitResult` + completion event

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitService.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitResult.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/R3InitCompletedEvent.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitServiceTest.java`

- [ ] **Step 1: Write the result + event records**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitResult.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

public record R3InitResult(long durationMs) {}
```

Create `backend/src/main/java/com/ecoatm/salesplatform/event/R3InitCompletedEvent.java`:

```java
package com.ecoatm.salesplatform.event;

public record R3InitCompletedEvent(
    long schedulingAuctionId,
    long auctionId
) {}
```

- [ ] **Step 2: Write the failing service tests**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitServiceTest.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3InitCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.ScheduleAuctionInitStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3InitServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater         statusUpdater;
    @Mock ApplicationEventPublisher   events;

    @InjectMocks R3InitService service;

    private SchedulingAuction r3Sa(long id, RecalcStatus preprocessStatus) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(600L); sa.setRound(3);
        sa.setR3PreprocessStatus(preprocessStatus);
        sa.setRound3InitStatus(ScheduleAuctionInitStatus.Pending);
        return sa;
    }

    @Test
    @DisplayName("happy path — predecessor SUCCESS, status RUNNING→SUCCESS, round3InitStatus=Complete, event")
    void happy_path() {
        long id = 6003L;
        SchedulingAuction sa = r3Sa(id, RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(true);

        R3InitResult result = service.run(id);

        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
        assertThat(sa.getRound3InitStatus()).isEqualTo(ScheduleAuctionInitStatus.Complete);
        verify(saRepo).save(sa);
        verify(statusUpdater).markSuccess(id, "R3_INIT");
        verify(events).publishEvent(any(R3InitCompletedEvent.class));
    }

    @Test
    @DisplayName("predecessor PENDING → IllegalStateException pre-flip, no save, no event")
    void predecessor_pending_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.PENDING)));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PENDING");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("predecessor FAILED → IllegalStateException")
    void predecessor_failed_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.FAILED)));
        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("predecessor SKIPPED → IllegalStateException")
    void predecessor_skipped_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.SKIPPED)));
        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("round != 3 → IllegalArgumentException pre-flip")
    void wrong_round_throws() {
        long id = 6002L;
        SchedulingAuction r2 = new SchedulingAuction();
        r2.setId(id); r2.setRound(2); r2.setR3PreprocessStatus(RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(r2));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("round 3");
    }

    @Test
    @DisplayName("status RUNNING → RecalcAlreadyRunningException")
    void already_running_throws() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.SUCCESS)));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(false);

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(RecalcAlreadyRunningException.class);
    }

    @Test
    @DisplayName("save throw → markFailed and propagate, round3InitStatus left at Complete on entity (set before save)")
    void save_throw_marks_failed() {
        long id = 6003L;
        SchedulingAuction sa = r3Sa(id, RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(true);
        when(saRepo.save(sa)).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("db down");
        verify(statusUpdater).markFailed(eq(id), eq("R3_INIT"),
            org.mockito.ArgumentMatchers.contains("db down"));
    }
}
```

- [ ] **Step 3: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3InitServiceTest`
Expected: FAIL — class not found.

- [ ] **Step 4: Implement `R3InitService`**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitService.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3InitCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.ScheduleAuctionInitStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class R3InitService {

    private static final String PROCESS = "R3_INIT";
    private static final Logger log = LoggerFactory.getLogger(R3InitService.class);

    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater         statusUpdater;
    private final ApplicationEventPublisher   events;

    public R3InitService(SchedulingAuctionRepository saRepo,
                         RecalcStatusUpdater statusUpdater,
                         ApplicationEventPublisher events) {
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3InitResult run(long r3SaId) {
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r3SaId));

        if (sa.getRound() != 3) {
            throw new IllegalArgumentException(
                "R3 init only valid for round 3; was " + sa.getRound());
        }
        if (sa.getR3PreprocessStatus() != RecalcStatus.SUCCESS) {
            throw new IllegalStateException(
                "R3 init refused — r3_preprocess_status is " + sa.getR3PreprocessStatus()
                    + " (expected SUCCESS)");
        }

        if (!statusUpdater.tryFlipToRunning(r3SaId, PROCESS)) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_INIT, r3SaId);
        }

        long start = System.currentTimeMillis();
        try {
            sa.setRound3InitStatus(ScheduleAuctionInitStatus.Complete);
            saRepo.save(sa);

            // TODO(gap-analysis #4): R3 start-notification email
            // TODO(gap-analysis #9): SUB_Round3SendAuctionToSnowflake dedicated push

            statusUpdater.markSuccess(r3SaId, PROCESS);
            long durationMs = System.currentTimeMillis() - start;
            log.info("R3_INIT success r3SaId={} ms={}", r3SaId, durationMs);

            events.publishEvent(new R3InitCompletedEvent(r3SaId, sa.getAuctionId()));
            return new R3InitResult(durationMs);
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(r3SaId, PROCESS, msg);
            log.error("R3_INIT failed r3SaId={}", r3SaId, ex);
            throw ex;
        }
    }

    public R3InitResult recalculate(long r3SaId) { return run(r3SaId); }
}
```

- [ ] **Step 5: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3InitServiceTest`
Expected: PASS — all 7 cases.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitService.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitResult.java \
        backend/src/main/java/com/ecoatm/salesplatform/event/R3InitCompletedEvent.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitServiceTest.java
git commit -m "feat(6): R3InitService — status flip with predecessor guard"
```

---

## Task 14: `R3PreProcessListener` + delete stub

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListener.java`
- Delete: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java`
- Modify: `backend/src/main/resources/application.yml`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListenerTest.java`

- [ ] **Step 1: Write the failing listener test**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListenerTest.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3PreProcessListenerTest {

    @Mock R3PreProcessService service;
    @Mock SchedulingAuctionRepository saRepo;
    @InjectMocks R3PreProcessListener listener;

    private SchedulingAuction r3Sa(long id) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setRound(3); sa.setAuctionId(600L);
        return sa;
    }

    @Test
    @DisplayName("round 2 close + R3 SA exists → service.run(r2, r3)")
    void round_2_triggers_service() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 601L, 600L));
        verify(service).run(6002L, 6003L);
    }

    @Test
    @DisplayName("round 1 close → no service call")
    void round_1_skipped() {
        listener.onRoundClosed(new RoundClosedEvent(6001L, 1, 601L, 600L));
        verifyNoInteractions(service, saRepo);
    }

    @Test
    @DisplayName("round 3 close → no service call (R3 pre-process is R2-close only)")
    void round_3_skipped() {
        listener.onRoundClosed(new RoundClosedEvent(6003L, 3, 601L, 600L));
        verifyNoInteractions(service, saRepo);
    }

    @Test
    @DisplayName("no R3 SA for the auction → silent log + return, no service call")
    void no_r3_sa_silent_skip() {
        when(saRepo.findByAuctionIdAndRound(601L, 3)).thenReturn(Optional.empty());
        listener.onRoundClosed(new RoundClosedEvent(6012L, 2, 602L, 601L));
        verify(service, never()).run(anyLong(), anyLong());
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException is logged, not propagated")
    void already_running_swallowed() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        when(service.run(6002L, 6003L)).thenThrow(
            new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_PREPROCESS, 6003L));
        // Should not throw out of listener
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 601L, 600L));
    }

    @Test
    @DisplayName("any RuntimeException is logged, not propagated")
    void unexpected_exception_swallowed() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        when(service.run(6002L, 6003L)).thenThrow(new RuntimeException("boom"));
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 601L, 600L));
    }
}
```

(Note: `RoundClosedEvent` constructor signature `(roundId, round, weekId, auctionId)` — verify exact field order against the existing record before running.)

- [ ] **Step 2: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessListenerTest`
Expected: FAIL — class not found.

- [ ] **Step 3: Implement `R3PreProcessListener`**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListener.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
@ConditionalOnProperty(
    name = "auctions.r3-preprocess.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class R3PreProcessListener {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessListener.class);

    private final R3PreProcessService service;
    private final SchedulingAuctionRepository saRepo;

    public R3PreProcessListener(R3PreProcessService service, SchedulingAuctionRepository saRepo) {
        this.service = service;
        this.saRepo  = saRepo;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 2) return;
        long r2SaId = event.roundId();
        Optional<Long> r3SaId = saRepo.findByAuctionIdAndRound(event.auctionId(), 3)
            .map(SchedulingAuction::getId);
        if (r3SaId.isEmpty()) {
            log.info("R3_PREPROCESS not applicable — auctionId={} has no R3 SA", event.auctionId());
            return;
        }
        try {
            service.run(r2SaId, r3SaId.get());
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_PREPROCESS skipped — already running r3SaId={}", r3SaId.get());
        } catch (RuntimeException ex) {
            log.error("R3_PREPROCESS failed r3SaId={} error={}", r3SaId.get(), ex.toString(), ex);
        }
    }
}
```

- [ ] **Step 4: Delete the stub**

```bash
rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java
```

- [ ] **Step 5: Add the config key**

In `backend/src/main/resources/application.yml`, find the existing `auctions.r2-init.enabled` block and add:

```yaml
auctions:
  r2-init:
    enabled: true
  r3-preprocess:
    enabled: true
  r3-init:
    enabled: true
```

- [ ] **Step 6: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3PreProcessListenerTest`
Expected: PASS — all six cases.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessListenerTest.java \
        backend/src/main/resources/application.yml
git rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java
git commit -m "feat(6): R3PreProcessListener replaces R3PreProcessStubListener"
```

---

## Task 15: `R3InitListener` + delete stub

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListener.java`
- Delete: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListenerTest.java`

- [ ] **Step 1: Write the failing listener test**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListenerTest.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3InitListenerTest {

    @Mock R3InitService service;
    @InjectMocks R3InitListener listener;

    @Test
    @DisplayName("round 3 start triggers service")
    void round_3_triggers_service() {
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
        verify(service).run(6003L);
    }

    @Test
    @DisplayName("round 1 + 2 start ignored")
    void other_rounds_ignored() {
        listener.onRoundStarted(new RoundStartedEvent(6001L, 1, 601L, 600L));
        listener.onRoundStarted(new RoundStartedEvent(6002L, 2, 601L, 600L));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("predecessor-guard IllegalStateException logged but not propagated")
    void predecessor_guard_swallowed() {
        when(service.run(6003L)).thenThrow(
            new IllegalStateException("R3 init refused — r3_preprocess_status is PENDING"));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException logged but not propagated")
    void already_running_swallowed() {
        when(service.run(6003L)).thenThrow(
            new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_INIT, 6003L));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }

    @Test
    @DisplayName("RuntimeException logged but not propagated")
    void unexpected_exception_swallowed() {
        when(service.run(6003L)).thenThrow(new RuntimeException("boom"));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }
}
```

- [ ] **Step 2: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3InitListenerTest`
Expected: FAIL — class not found.

- [ ] **Step 3: Implement `R3InitListener`**

Create `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListener.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(
    name = "auctions.r3-init.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class R3InitListener {

    private static final Logger log = LoggerFactory.getLogger(R3InitListener.class);

    private final R3InitService service;

    public R3InitListener(R3InitService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 3) return;
        long r3SaId = event.roundId();
        try {
            service.run(r3SaId);
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_INIT skipped — already running r3SaId={}", r3SaId);
        } catch (IllegalStateException ex) {
            log.error("R3_INIT refused (predecessor guard) r3SaId={} — {}", r3SaId, ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("R3_INIT failed r3SaId={} error={}", r3SaId, ex.toString(), ex);
        }
    }
}
```

- [ ] **Step 4: Delete the stub**

```bash
rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java
```

- [ ] **Step 5: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3InitListenerTest`
Expected: PASS — all five cases.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitListenerTest.java
git rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java
git commit -m "feat(6): R3InitListener replaces R3InitStubListener"
```

---

## Task 16: Admin REST endpoints — `R3LifecycleAdminController`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminController.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3PreProcessResponse.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3InitResponse.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminControllerIT.java`

- [ ] **Step 1: Write the response DTOs**

Create `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3PreProcessResponse.java`:

```java
package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

public record R3PreProcessResponse(
    long schedulingAuctionId,
    String status,
    String error,
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int reportRowCount,
    int deletedBidsCount,
    long durationMs
) {}
```

Create `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3InitResponse.java`:

```java
package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

public record R3InitResponse(
    long schedulingAuctionId,
    String status,
    String error,
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    long durationMs
) {}
```

- [ ] **Step 2: Write the failing controller IT**

Create `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminControllerIT.java`:

```java
package com.ecoatm.salesplatform.controller.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class R3LifecycleAdminControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("ROLE_ADMIN — POST /preprocess-r3 returns 200 + R3PreProcessResponse")
    @WithMockUser(roles = "ADMIN")
    void admin_can_preprocess() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 6003L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("ROLE_ADMIN — POST /reinit-r3 fails 422 if pre-process not yet SUCCESS")
    @WithMockUser(roles = "ADMIN")
    void reinit_predecessor_guard() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", 6003L))
           .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("ROLE_ADMIN — POST /reinit-r3 succeeds after preprocess")
    @WithMockUser(roles = "ADMIN")
    void reinit_succeeds_after_preprocess() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 6003L))
           .andExpect(status().isOk());
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", 6003L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("non-admin → 403 on both endpoints")
    @WithMockUser(roles = "BUYER")
    void non_admin_forbidden() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 6003L))
           .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", 6003L))
           .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("unknown id → 404")
    @WithMockUser(roles = "ADMIN")
    void unknown_id_404() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 99999L))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("round != 3 → 422")
    @WithMockUser(roles = "ADMIN")
    void wrong_round_422() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 6002L))
           .andExpect(status().isUnprocessableEntity());
    }
}
```

- [ ] **Step 3: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R3LifecycleAdminControllerIT`
Expected: FAIL — controller not found.

- [ ] **Step 4: Implement the controller**

Create `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminController.java`:

```java
package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.R3InitResponse;
import com.ecoatm.salesplatform.dto.admin.R3PreProcessResponse;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.r3init.R3InitResult;
import com.ecoatm.salesplatform.service.auctions.r3init.R3InitService;
import com.ecoatm.salesplatform.service.auctions.r3init.R3PreProcessResult;
import com.ecoatm.salesplatform.service.auctions.r3init.R3PreProcessService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class R3LifecycleAdminController {

    private final R3PreProcessService preProcessService;
    private final R3InitService       initService;
    private final SchedulingAuctionRepository saRepo;

    public R3LifecycleAdminController(R3PreProcessService preProcessService,
                                      R3InitService initService,
                                      SchedulingAuctionRepository saRepo) {
        this.preProcessService = preProcessService;
        this.initService = initService;
        this.saRepo = saRepo;
    }

    @PostMapping("/{id}/preprocess-r3")
    @PreAuthorize("hasRole('ADMIN')")
    public R3PreProcessResponse preprocess(@PathVariable("id") long r3SaId) {
        R3PreProcessResult result = preProcessService.recalculate(r3SaId);
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r3SaId));
        return new R3PreProcessResponse(
            r3SaId,
            sa.getR3PreprocessStatus().name(),
            sa.getR3PreprocessError(),
            sa.getR3PreprocessStartedAt() != null
                ? sa.getR3PreprocessStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR3PreprocessFinishedAt() != null
                ? sa.getR3PreprocessFinishedAt().atOffset(ZoneOffset.UTC) : null,
            result.qualifiedCount() - result.specialTreatmentCount(),
            result.specialTreatmentCount(),
            result.notQualifiedCount(),
            result.reportRowCount(),
            result.deletedBidsCount(),
            result.durationMs()
        );
    }

    @PostMapping("/{id}/reinit-r3")
    @PreAuthorize("hasRole('ADMIN')")
    public R3InitResponse reinit(@PathVariable("id") long r3SaId) {
        R3InitResult result = initService.recalculate(r3SaId);
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException("scheduling_auction not found: id=" + r3SaId));
        return new R3InitResponse(
            r3SaId,
            sa.getR3InitStatus().name(),
            sa.getR3InitError(),
            sa.getR3InitStartedAt() != null
                ? sa.getR3InitStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR3InitFinishedAt() != null
                ? sa.getR3InitFinishedAt().atOffset(ZoneOffset.UTC) : null,
            result.durationMs()
        );
    }
}
```

- [ ] **Step 5: Add the SecurityConfig matchers**

In `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`, add to the request-matcher chain:

```java
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/preprocess-r3").hasRole("ADMIN")
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reinit-r3").hasRole("ADMIN")
```

- [ ] **Step 6: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3LifecycleAdminControllerIT`
Expected: PASS — all six cases.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminController.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3PreProcessResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R3InitResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminControllerIT.java
git commit -m "feat(6): admin REST endpoints /preprocess-r3 + /reinit-r3"
```

---

## Task 17: End-to-end IT — listener + service + DB

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3LifecycleEndToEndIT.java`

- [ ] **Step 1: Write the test**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3LifecycleEndToEndIT.java`:

```java
package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.ScheduleAuctionInitStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class R3LifecycleEndToEndIT {

    @Autowired ApplicationEventPublisher events;
    @Autowired SchedulingAuctionRepository saRepo;
    @Autowired QualifiedBuyerCodeRepository qbcRepo;
    @Autowired Round3BuyerDataReportRepository reportRepo;

    @Test
    @DisplayName("RoundClosedEvent(round=2) → R3 SA's preprocess columns + QBCs + reports populated")
    void preprocess_full_path() {
        long r2 = 6002L, r3 = 6003L, auctionId = 600L, weekId = 601L;

        events.publishEvent(new RoundClosedEvent(r2, 2, weekId, auctionId));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            SchedulingAuction sa = saRepo.findById(r3).orElseThrow();
            assertThat(sa.getR3PreprocessStatus()).isEqualTo(RecalcStatus.SUCCESS);
            assertThat(qbcRepo.findBySchedulingAuctionId(r3))
                .as("QBCs written for R3 SA")
                .isNotEmpty();
            assertThat(reportRepo.findBySchedulingAuctionId(r3))
                .as("round3_buyer_data_reports written for R3 SA")
                .isNotEmpty();
        });
    }

    @Test
    @DisplayName("RoundStartedEvent(round=3) after preprocess → r3InitStatus=SUCCESS, round3InitStatus=Complete")
    void init_after_preprocess() {
        long r2 = 6002L, r3 = 6003L, auctionId = 600L, weekId = 601L;

        // Pre-process first
        events.publishEvent(new RoundClosedEvent(r2, 2, weekId, auctionId));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(saRepo.findById(r3).orElseThrow().getR3PreprocessStatus())
                .isEqualTo(RecalcStatus.SUCCESS);
        });

        // Then init
        events.publishEvent(new RoundStartedEvent(r3, 3, weekId, auctionId));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            SchedulingAuction sa = saRepo.findById(r3).orElseThrow();
            assertThat(sa.getR3InitStatus()).isEqualTo(RecalcStatus.SUCCESS);
            assertThat(sa.getRound3InitStatus()).isEqualTo(ScheduleAuctionInitStatus.Complete);
        });
    }

    @Test
    @DisplayName("RoundClosedEvent(round=2) for auction without R3 SA → no writes anywhere")
    void no_r3_sa_silent_skip() {
        long r2 = 6012L, auctionId = 601L, weekId = 602L;

        events.publishEvent(new RoundClosedEvent(r2, 2, weekId, auctionId));

        // Wait long enough that the listener would have run
        try { Thread.sleep(2000); } catch (InterruptedException e) { /* ignore */ }

        // Auction 601 has no R3 SA — no QBC writes, no report writes
        assertThat(qbcRepo.findBySchedulingAuctionId(6012L)).isEmpty();
    }

    @Test
    @DisplayName("RoundClosedEvent(round=2) for has_round=false R3 SA → SKIPPED, no QBCs/reports")
    void has_round_false_skipped() {
        long r2 = 6022L, r3 = 6023L, auctionId = 602L, weekId = 603L;

        events.publishEvent(new RoundClosedEvent(r2, 2, weekId, auctionId));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            SchedulingAuction sa = saRepo.findById(r3).orElseThrow();
            assertThat(sa.getR3PreprocessStatus()).isEqualTo(RecalcStatus.SKIPPED);
            assertThat(qbcRepo.findBySchedulingAuctionId(r3)).isEmpty();
            assertThat(reportRepo.findBySchedulingAuctionId(r3)).isEmpty();
        });
    }
}
```

- [ ] **Step 2: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R3LifecycleEndToEndIT`
Expected: PASS — all four cases.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r3init/R3LifecycleEndToEndIT.java
git commit -m "test(6): end-to-end IT covers listener → service → DB writes for both R3 lifecycle phases"
```

---

## Task 18: Docs + ADR

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/app-metadata/modules.md`
- Modify: `docs/architecture/data-model.md`
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/business-logic/index.md`
- Create: `docs/business-logic/r3-init-and-preprocess.md`
- Modify: `docs/deployment/setup.md`
- Modify: `docs/testing/coverage.md`
- Modify: `docs/tasks/auction-flow-gap-analysis-2026-05-06.md`

- [ ] **Step 1: Update each doc per design §10**

For each doc, follow the pattern established by sub-project 5 (`auction-r2-buyer-assignment-design.md` §10 + the matching commit `f2c63e7` and `741ce83`). Detailed updates:

**`docs/api/rest-endpoints.md`** — add a new section under "Admin endpoints":

```markdown
### POST /api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3
**Auth:** `ROLE_ADMIN`
**Path:** R3 SchedulingAuction id
**Body:** none
**200:** `R3PreProcessResponse` — status, counts, durations
**4xx:** 404 unknown id; 409 RUNNING; 422 round ≠ 3 or no R2 sibling

### POST /api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3
**Auth:** `ROLE_ADMIN`
**Path:** R3 SchedulingAuction id
**Body:** none
**200:** `R3InitResponse` — status, duration
**4xx:** 404 unknown id; 409 RUNNING; 422 round ≠ 3 or pre-process not SUCCESS
```

**`docs/architecture/decisions.md`** — append new ADR:

```markdown
## ADR — Sub-project 6: R3 init + pre-process (2026-05-07)

**Status:** Accepted

**Context:** R3 (Upsell) bidding non-functional pre-2026-05-07 because two stub listeners (`R3InitStubListener`, `R3PreProcessStubListener`) replaced no production logic. Sub-project 6 ports Mendix `SUB_Round3_PreProcessRoundData` (minus BidData generation) and `ACT_Round3_SetStarted`.

**Decisions:** see `docs/tasks/auction-r3-init-preprocess-design.md` §3 for the full list of 14 numbered decisions. Highlights:
- Two sibling services, not an orchestrator (decision 3.1)
- Predecessor guard in code, not constraint (decision 3.2)
- New R3 qualification rule replaces "any nonzero bid" (decision 3.6)
- Whole-percent convention everywhere; R2 retroactively aligned (decision 3.8)
- STB tracking retained on QBCs for `BidDataCreationService` consumption (decision 3.9)
- `ACT_ChangeSavedBidsToPreviouslySubmitted` not ported (decision 3.12)

**Consequences:** R3 BidData empty until first dashboard view triggers `BidDataCreationService` on-demand creation. Predecessor-guard failure leaves R3 in PENDING state — admin must re-run `/preprocess-r3` first.
```

**`docs/architecture/data-model.md`** — add a new section under `auctions.scheduling_auctions`:

```markdown
## auctions.scheduling_auctions (8 R3-lifecycle status fields, V84)

Sub-project 6 (V84) adds eight columns:
- `r3_preprocess_status` (`PENDING`/`RUNNING`/`SUCCESS`/`FAILED`/`SKIPPED`)
- `r3_preprocess_error` (TEXT)
- `r3_preprocess_started_at`, `r3_preprocess_finished_at` (TIMESTAMPTZ)
- `r3_init_status` (`PENDING`/`RUNNING`/`SUCCESS`/`FAILED`; no SKIPPED)
- `r3_init_error` (TEXT)
- `r3_init_started_at`, `r3_init_finished_at` (TIMESTAMPTZ)

Both column groups live on the R3 SA row. Pre-process listener takes R2 SA id from `RoundClosedEvent` and resolves R3 SA via `findByAuctionIdAndRound`; admin endpoints take R3 SA id directly. `SKIPPED` on `r3_preprocess_status` indicates `has_round=false` on the R3 SA.

## auctions.bid_round_selection_filters (3 R3 columns, V84)

V84 adds three nullable R3-qualification knobs:
- `bid_percentage_variation NUMERIC(10, 4)` — whole percent (5 = 5%)
- `bid_amount_variation NUMERIC(14, 2)` — flat amount
- `rank_qualification_limit INTEGER` — rank ceiling

All three NULL → R3 qualifies all buyers with any submitted nonzero bid. ANY non-NULL branch matches → buyer code qualifies.

V84 also normalises `target_percent` to whole-percent convention (5 = 5%), aligned with `bid_percentage_variation`. Was treated as decimal in V59/sub-project 5; sub-project 6 updated R2's CTE formula to divide by 100.
```

**`docs/app-metadata/modules.md`** — add new entry mirroring R2:

```markdown
## R3 Init + Pre-process (Sub-project 6)
- Source modules: AuctionUI (`SUB_Round3_PreProcessRoundData`, `ACT_Round3_SetStarted`, `SUB_GenerateRound3QualifiedBuyerCodes`, `SUB_Round2_DeleteUnsubmittedBids`, `ACT_Generate_RoundThreeQualifiedBuyersReport`)
- Primary tables: `auctions.scheduling_auctions` (R3 status flags from V84), `buyer_mgmt.qualified_buyer_codes` (V72-flattened — three-set write per R3 SA), `auctions.bid_round_selection_filters` (3 new R3 columns from V84), `auctions.round3_buyer_data_reports` (V62 table; sub-project 6 supplies the writes)
- Triggers: `RoundClosedEvent` for round 2 (pre-process); `RoundStartedEvent` for round 3 (init)
- Admin recovery: `POST /api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3` and `POST /.../reinit-r3`
- Snowflake sync: deferred (gap-analysis #9)
- Out of scope: BidData generation (handled by existing `BidDataCreationService`); STB BidData seeding (same)
```

**`docs/business-logic/index.md`** — add a link:

```markdown
- [R3 init and pre-process](r3-init-and-preprocess.md)
```

**`docs/business-logic/r3-init-and-preprocess.md`** — new file:

```markdown
# R3 Init + Pre-process

**Sub-project 6 — shipped 2026-05-07. Replaces `R3InitStubListener` and `R3PreProcessStubListener`.**

## Lifecycle overview

Two sibling services fire on different events:

| Service | Event | Purpose |
|---|---|---|
| `R3PreProcessService` | `RoundClosedEvent(round=2)` | 5-phase data prep before R3 opens |
| `R3InitService` | `RoundStartedEvent(round=3)` | status flip + (deferred) notifications/Snowflake |

`R3InitService` refuses to flip to SUCCESS unless `r3_preprocess_status = SUCCESS` on the same R3 SA row (predecessor guard).

## R3 qualification rule (the four filter branches)

For each `(ecoid, merged_grade, buyer_code)` take the buyer's latest submitted bid across rounds 1+2 (`ROW_NUMBER() OVER (PARTITION BY ecoid, grade, buyer_code ORDER BY submitted_datetime DESC)`). Compare against `aggregated_inventory.round3_target_price` (which 4C wrote on R2 close).

A buyer code qualifies if **any** of its latest-bid rows passes one of:

1. **All three filters NULL** → fall-through, qualify
2. **`bid_percentage_variation` NOT NULL** → `latest_bid ≥ round3_target_price − (round3_target_price × pct ÷ 100)` (whole percent)
3. **`bid_amount_variation` NOT NULL** → `latest_bid ≥ round3_target_price − amount` (flat)
4. **`rank_qualification_limit` NOT NULL** → `round3_bid_rank ≤ limit` (rank ceiling)

Special-treatment buyers union onto the qualified set independently — see "STB inheritance" below.

## STB inheritance

A buyer code qualifies as `is_special_treatment = TRUE` iff the buyer is flagged `is_special_buyer` AND every DW/WH code owned by that buyer satisfies one of:
- `stb_allow_all_buyers_override = TRUE` on round-3 BRSF, OR
- The buyer has zero submitted bids on that code in any prior round (round ∈ {1, 2})

The `is_special_treatment` flag on `qualified_buyer_codes` is the source of truth `BidDataCreationService` reads to grant STBs all-AE visibility at R3 submission time.

## Admin recovery

Two endpoints, both `ROLE_ADMIN`:

- `POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/preprocess-r3` — reruns the 5-phase pre-process. Resolves R2 SA via `findByAuctionIdAndRound(auctionId, 2)`. Idempotent (DELETE-then-INSERT for QBCs and reports).
- `POST /api/v1/admin/auctions/scheduling-auctions/{r3SaId}/reinit-r3` — reruns the init flip. Refuses with 422 if pre-process isn't SUCCESS.

## Configuration

- `auctions.r3-preprocess.enabled` (default true) — disables `R3PreProcessListener` cron path
- `auctions.r3-init.enabled` (default true) — disables `R3InitListener` cron path

Admin endpoints are unaffected by these flags.
```

**`docs/business-logic/r2-buyer-assignment.md`** — add a paragraph noting the convention update (per Task 2 step 4 above; ensure consistency).

**`docs/deployment/setup.md`** — add a new section parallel to the R2 entry:

```markdown
## R3 Pre-process (6) config
- `auctions.r3-preprocess.enabled` — default `true`; disables `R3PreProcessListener` on `RoundClosedEvent(round=2)`. Admin endpoint unaffected.
- `auctions.r3-init.enabled` — default `true`; disables `R3InitListener` on `RoundStartedEvent(round=3)`. Admin endpoint unaffected.
```

**`docs/testing/coverage.md`** — add new entry:

```markdown
## auctions.r3lifecycle (new 2026-05-07)
Target 85%+. R3 qualification CTE + STB CTE + R3 reports population +
predecessor guard are the load-bearing branches; see
R3BuyerQualificationRepositoryIT + R3SpecialBuyerRepositoryIT +
R3PreProcessSupportRepositoryIT + Round3BuyerDataReportRepositoryR3IT +
R3PreProcessServiceTest + R3InitServiceTest +
R3PreProcessListenerTest + R3InitListenerTest +
R3LifecycleAdminControllerIT + R3LifecycleEndToEndIT.
```

**`docs/tasks/auction-flow-gap-analysis-2026-05-06.md`** — update §1 status table for sub-project 6:

```markdown
| **6** | **R3 init + pre-process** | **Yes** — V84; `R3PreProcessListener` (replaced `R3PreProcessStubListener`) + `R3InitListener` (replaced `R3InitStubListener`); two services + predecessor-guard pattern; admin POST endpoints; full test suite | `docs/tasks/auction-r3-init-preprocess-design.md` / `-plan.md` | gap-analysis items #4 (R3 start-notification) + #9 (dedicated R3 Snowflake push) remain as TODO hooks |
```

Update §2 lifecycle gaps (rows for "R3 init", "R3 pre-process") and §3 stub listeners table to remove both R3 stubs.

- [ ] **Step 2: Commit**

```bash
git add docs/
git commit -m "docs(6): R3 init + pre-process — ADR, business logic, data model, coverage, gap-analysis update"
```

---

## Self-Review

**Spec coverage:** every section of `auction-r3-init-preprocess-design.md` maps to a task above:

- §5 Schema (V84) → Task 1
- §3.8 R2 unit alignment → Task 2
- §3 SchedulingAuction status fields → Task 3
- §3.2 RecalcStatusUpdater extension → Task 4
- §6.3 RecalcAlreadyRunningException.R3_PREPROCESS + R3_INIT → Task 5
- §3.10 + §3.11 QBC bulkInsertForRound rename → Task 6
- §9.2 Test fixture → Task 7
- §7.1 delete unsubmitted CTE → Task 8
- §7.2 R3 regular qualification CTE → Task 9
- §7.3 R3 STB CTE → Task 10
- §7.5 round3 reports population → Task 11
- §4.2 R3PreProcessService + 5 phases + recalculate → Task 12
- §4.3 R3InitService + predecessor guard → Task 13
- §8 R3PreProcessListener wiring → Task 14
- §8 R3InitListener wiring → Task 15
- §6.1 admin endpoints → Task 16
- §9 end-to-end IT → Task 17
- §10 docs → Task 18

**Type consistency:**
- `RecalcStatus` enum values used: `PENDING`, `RUNNING`, `SUCCESS`, `FAILED`, `SKIPPED` — all in scope from sub-project 5.
- `ScheduleAuctionInitStatus.Complete` (legacy enum on entity) used in Task 13.
- `RecalcAlreadyRunningException.Process` constants used: `R3_PREPROCESS`, `R3_INIT` — added in Task 5.
- `qbcRepo.bulkInsertForRound(saId, qualified[], special[])` signature consistent across Task 6 (rename) and Task 12 (caller).
- `findByAuctionIdAndRound` (existing on `SchedulingAuctionRepository`) used in Tasks 12, 14.
- `submitted_datetime` (DB column) / `submittedDatetime` (entity field) used consistently in Tasks 7 (fixture) and 9 (CTE).

**Placeholder scan:**
- "TODO(gap-analysis #4)" and "TODO(gap-analysis #9)" in Task 13 are deliberate hooks for deferred work, documented in design §2 out-of-scope. NOT placeholder-rot.
- "verify column name against the production schema" notes in Tasks 7 and 11 are explicit plan-phase verification steps, surfaced from design §11 risks.
- No `TBD` / `implement later` / `fill in details` strings.

**Note for executor:** Task 7's fixture references `auctions.bid_data` columns including `bid_round`, `week_id`, `round3_bid_rank`, `highest_bid`. If any of these column names diverge from the production schema (entity vs DB column casing), update the fixture inserts before running Task 8's IT.

---

## Execution Handoff

Plan complete and saved to `docs/tasks/auction-r3-init-preprocess-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks, fast iteration. Required sub-skill: `superpowers:subagent-driven-development`.

**2. Inline Execution** — execute tasks in this session using `superpowers:executing-plans`, batch execution with checkpoints.

Which approach?
