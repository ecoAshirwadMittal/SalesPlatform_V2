# Sub-project 5: R2 Buyer Assignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace `R2InitStubListener` with a single-process service that ports Mendix `SUB_AssignRoundTwoBuyers` + `SUB_GenerateRound2QualifiedBuyerCodes` + `Sub_ProcessSpecialBuyers`. The service writes `qualified_buyer_codes` rows for the R2 `SchedulingAuction` and seeds R2 `bid_data` for special-treatment buyers.

**Architecture:** One Flyway migration (V83) adds R2-init status flags to `auctions.scheduling_auctions`. Three native-CTE repositories own the SQL — qualification, special-treatment, and special-buyer bid_data bulk insert. One service runs in `REQUIRES_NEW`; the listener is `@Async("snowflakeExecutor") @AFTER_COMMIT`. One admin REST endpoint reruns the process, gated by `RecalcAlreadyRunningException(R2_INIT)` → 409. `RecalcStatusUpdater` is reused with a new `R2_INIT` column-prefix branch. No Snowflake push.

**Tech Stack:** Spring Boot 3.x, Java 21, JPA/Hibernate, native PostgreSQL CTEs via `EntityManager`, Flyway, JUnit 5 + AssertJ + Mockito, Spring `@TransactionalEventListener`, `MockMvc` for controller IT, Testcontainers PostgreSQL for repository IT.

**Spec:** `docs/tasks/auction-r2-buyer-assignment-design.md`

---

## File Structure

### New files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  event/
    R2BuyerAssignmentCompletedEvent.java         -- record event
  service/auctions/r2init/
    R2BuyerAssignmentService.java                -- five-phase R2_INIT process
    R2BuyerAssignmentListener.java               -- replaces R2InitStubListener
    R2BuyerAssignmentResult.java                 -- record(qualifiedCount, ...)
    BidDataForAllAEService.java                  -- ports SUB_CreateBidDataForAllAE
  repository/auctions/
    R2BuyerQualificationRepository.java          -- qualification CTE (native)
    R2SpecialBuyerRepository.java                -- special-treatment CTE (native)
    BidDataForAllAERepository.java               -- bulk INSERT bid_data for STB QBCs
  controller/admin/
    R2BuyerAssignmentAdminController.java        -- POST /reassign-r2-buyers
  dto/admin/
    R2BuyerAssignmentResponse.java               -- response shape
```

### Modified files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  model/buyermgmt/
    AuctionsFeatureConfig.java                   -- map calculate_round2_buyer_participation
  model/auctions/
    SchedulingAuction.java                       -- 4 new R2_INIT status fields
  service/auctions/recalc/
    RecalcStatusUpdater.java                     -- add R2_INIT column-prefix; markSkipped helper
  exception/
    RecalcAlreadyRunningException.java           -- add Process.R2_INIT enum constant
  repository/
    QualifiedBuyerCodeRepository.java            -- add bulkInsertForR2 + bulkInsertJunctions
  config/
    SecurityConfig.java                          -- admin matcher for /reassign-r2-buyers
```

### Deleted files

```
backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/
  R2InitStubListener.java                        -- replaced by R2BuyerAssignmentListener
```

(R3 stubs stay — sub-project 6.)

### Schema migration

```
backend/src/main/resources/db/migration/
  V83__auctions_r2_init_status.sql               -- additive only
```

### Config

```
backend/src/main/resources/
  application.yml                                -- add auctions.r2-init.enabled (default true)
```

### Tests

```
backend/src/test/
  java/com/ecoatm/salesplatform/
    repository/auctions/
      R2BuyerQualificationRepositoryIT.java
      R2SpecialBuyerRepositoryIT.java
      BidDataForAllAERepositoryIT.java
      QualifiedBuyerCodeRepositoryR2IT.java     -- bulkInsertForR2 + junctions
    service/auctions/r2init/
      R2BuyerAssignmentServiceTest.java
      BidDataForAllAEServiceTest.java
      R2BuyerAssignmentListenerTest.java
      R2BuyerAssignmentEndToEndIT.java
    controller/admin/
      R2BuyerAssignmentAdminControllerIT.java
  resources/fixtures/auctions/
    r2-init-seed.sql
```

### Documentation

```
docs/
  api/rest-endpoints.md                          -- /reassign-r2-buyers
  app-metadata/modules.md                        -- sub-project 5 entry
  architecture/data-model.md                     -- new r2_init_* columns
  architecture/decisions.md                      -- ADR for sub-project 5
  business-logic/index.md                        -- link r2-buyer-assignment.md
  business-logic/r2-buyer-assignment.md          -- new
  deployment/setup.md                            -- auctions.r2-init.enabled key
  testing/coverage.md                            -- auctions.r2init entry
  tasks/auction-flow-gap-analysis-2026-05-06.md  -- mark item #1 in-flight/shipped
```

---

## Task 1: Schema migration V83 (additive)

**Files:**
- Create: `backend/src/main/resources/db/migration/V83__auctions_r2_init_status.sql`
- Test: existing Flyway boot test exercises this on `mvn test`

- [ ] **Step 1: Write the migration**

```sql
-- V83: 5 — R2 Buyer Assignment — status flags
-- Additive only.

ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN r2_init_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN r2_init_error         TEXT,
    ADD COLUMN r2_init_started_at    TIMESTAMPTZ,
    ADD COLUMN r2_init_finished_at   TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_r2_init_status
        CHECK (r2_init_status IN ('PENDING','RUNNING','SUCCESS','FAILED','SKIPPED'));

COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_status IS
    '5: PENDING (round not yet started) | RUNNING | SUCCESS | FAILED | SKIPPED (config gate FALSE)';
COMMENT ON COLUMN auctions.scheduling_auctions.r2_init_error IS
    '5: exception class + message (truncated to 4000 chars) on FAILED';
```

- [ ] **Step 2: Verify Flyway picks it up**

Run: `cd backend && mvn -q -DskipTests=false test -Dtest='*FlywayMigrationsIT*'`
Expected: PASS — no migration ordering or syntax errors.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V83__auctions_r2_init_status.sql
git commit -m "feat(5): V83 add r2_init_* status columns to scheduling_auctions"
```

---

## Task 2: Map `calculate_round2_buyer_participation` on `AuctionsFeatureConfig`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfig.java`
- Test: extend existing `AuctionsFeatureConfigTest` (or create a `ConfigMappingIT` if absent)

- [ ] **Step 1: Write the failing test**

```java
// backend/src/test/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfigIT.java
@DataJpaTest
class AuctionsFeatureConfigIT {

    @Autowired EntityManager em;

    @Test
    @DisplayName("loads calculate_round2_buyer_participation as a boolean field")
    void loads_calculateRound2_field() {
        AuctionsFeatureConfig cfg = em.find(AuctionsFeatureConfig.class, 1L);
        assertThat(cfg).isNotNull();
        assertThat(cfg.isCalculateRound2BuyerParticipation())
            .as("V8 default is TRUE")
            .isTrue();
    }
}
```

- [ ] **Step 2: Run test — should fail with "no such method isCalculateRound2BuyerParticipation"**

Run: `cd backend && mvn -q test -Dtest=AuctionsFeatureConfigIT`
Expected: FAIL — getter does not exist.

- [ ] **Step 3: Add the field + accessor**

In `AuctionsFeatureConfig.java`, add after `minimumAllowedBid`:

```java
@Column(name = "calculate_round2_buyer_participation", nullable = false)
private boolean calculateRound2BuyerParticipation = true;

public boolean isCalculateRound2BuyerParticipation() {
    return calculateRound2BuyerParticipation;
}

public void setCalculateRound2BuyerParticipation(boolean calculateRound2BuyerParticipation) {
    this.calculateRound2BuyerParticipation = calculateRound2BuyerParticipation;
}
```

- [ ] **Step 4: Run tests — should pass**

Run: `cd backend && mvn -q test -Dtest=AuctionsFeatureConfigIT`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfig.java \
        backend/src/test/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfigIT.java
git commit -m "feat(5): map calculate_round2_buyer_participation on AuctionsFeatureConfig"
```

---

## Task 3: Add R2_INIT status fields to `SchedulingAuction`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java`
- Reuse: `RecalcStatus` enum (already exists from 4C)

- [ ] **Step 1: Add four new fields**

In `SchedulingAuction.java`, after the `target_price_*` block (line 108), add:

```java
@Enumerated(EnumType.STRING)
@Column(name = "r2_init_status", length = 20, nullable = false)
private RecalcStatus r2InitStatus = RecalcStatus.PENDING;

@Column(name = "r2_init_error", columnDefinition = "TEXT")
private String r2InitError;

@Column(name = "r2_init_started_at")
private Instant r2InitStartedAt;

@Column(name = "r2_init_finished_at")
private Instant r2InitFinishedAt;
```

Add the matching getters and setters at the bottom of the file (mirrors the 4C ranking/target_price accessors).

- [ ] **Step 2: Extend `RecalcStatus` enum to include `SKIPPED`**

In `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/RecalcStatus.java`:

```java
public enum RecalcStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    SKIPPED
}
```

The check constraint on V83 already permits `SKIPPED`. The 4C
`chk_sa_ranking_status` does NOT — that's fine because RANKING and
TARGET_PRICE never produce SKIPPED.

- [ ] **Step 3: Run the entity-load smoke test**

Run: `cd backend && mvn -q test -Dtest='SchedulingAuctionRepositoryIT*'`
Expected: PASS — JPA mapping resolves cleanly against V83.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/RecalcStatus.java
git commit -m "feat(5): add r2_init_* status fields to SchedulingAuction + SKIPPED enum"
```

---

## Task 4: Extend `RecalcStatusUpdater` for `R2_INIT`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java`
- Test: extend existing `RecalcStatusUpdaterTest`

- [ ] **Step 1: Write the failing test**

```java
// backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdaterTest.java
@Test
@DisplayName("tryFlipToRunning routes R2_INIT to r2_init_status column")
void flips_r2_init_to_running() {
    long saId = seedSchedulingAuctionWithR2InitStatus("PENDING");

    boolean flipped = updater.tryFlipToRunning(saId, "R2_INIT");

    assertThat(flipped).isTrue();
    assertThat(currentStatus(saId, "r2_init_status")).isEqualTo("RUNNING");
}

@Test
@DisplayName("markSkipped writes SKIPPED + finished_at + clears error")
void marks_skipped() {
    long saId = seedSchedulingAuctionWithR2InitStatus("RUNNING");

    updater.markSkipped(saId, "R2_INIT");

    assertThat(currentStatus(saId, "r2_init_status")).isEqualTo("SKIPPED");
}
```

- [ ] **Step 2: Run — should fail with "Unknown recalc process: R2_INIT"**

Run: `cd backend && mvn -q test -Dtest=RecalcStatusUpdaterTest`
Expected: FAIL.

- [ ] **Step 3: Extend `columnPrefix` + add `markSkipped`**

In `RecalcStatusUpdater.java`:

```java
private static String columnPrefix(String process) {
    return switch (process) {
        case "RANKING"      -> "ranking";
        case "TARGET_PRICE" -> "target_price";
        case "R2_INIT"      -> "r2_init";
        case null -> throw new IllegalArgumentException("process must not be null");
        default -> throw new IllegalArgumentException("Unknown recalc process: " + process);
    };
}

@Transactional(propagation = Propagation.MANDATORY)
public void markSkipped(long schedulingAuctionId, String process) {
    String column = columnPrefix(process);
    String sql = """
        UPDATE auctions.scheduling_auctions
           SET %s_status      = 'SKIPPED',
               %s_finished_at = NOW(),
               %s_error       = NULL
         WHERE id = ?
        """.formatted(column, column, column);
    jdbc.update(sql, schedulingAuctionId);
}
```

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=RecalcStatusUpdaterTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdaterTest.java
git commit -m "feat(5): RecalcStatusUpdater handles R2_INIT process + markSkipped helper"
```

---

## Task 5: Add `Process.R2_INIT` to `RecalcAlreadyRunningException`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java`

- [ ] **Step 1: Add the enum constant**

```java
public enum Process { RANKING, TARGET_PRICE, R2_INIT }
```

The existing `GlobalExceptionHandler` mapping (409) needs no change.

- [ ] **Step 2: Smoke compile**

Run: `cd backend && mvn -q compile`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java
git commit -m "feat(5): RecalcAlreadyRunningException supports R2_INIT process"
```

---

## Task 6: `R2BuyerQualificationRepository` (qualification CTE)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepositoryIT.java`
- Test fixture: `backend/src/test/resources/fixtures/auctions/r2-init-seed.sql`

- [ ] **Step 1: Write the seed fixture**

The fixture seeds:
- 1 auction week, 1 auction
- 2 scheduling_auctions (R1 closed, R2 just-started)
- 1 `bid_round_selection_filters` row for round 2 with
  `regular_buyer_qualification = 'Only_Qualified'`,
  `regular_buyer_inventory_options = 'InventoryRound1QualifiedBids'`,
  `target_percent = 0.05`, `target_value = 1.00`
- 6 (ecoid, grade) rows in `aggregated_inventory` (mix of DW + WH;
  one row with `dw_avg_target_price = 0` for divide-by-zero branch)
- 5 buyers (2 special) and 10 buyer codes
- 12 R1 `bid_data` rows hitting all four predicate branches

```sql
-- backend/src/test/resources/fixtures/auctions/r2-init-seed.sql
-- (full fixture contents — write per design §9.2)
INSERT INTO mdm.weeks (id, year, week, ...) VALUES (501, 2026, 18, ...);
-- ... etc.
```

(Fill in per design §9.2; reuse existing seed scaffolding from
`backend/src/test/resources/fixtures/auctions/recalc-seed.sql`.)

- [ ] **Step 2: Write the failing test**

```java
@SpringBootTest
@AutoConfigureTestEntityManager
@Sql(scripts = "/fixtures/auctions/r2-init-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R2BuyerQualificationRepositoryIT {

    @Autowired R2BuyerQualificationRepository repo;

    @Test
    @DisplayName("Only_Qualified — bid above target qualifies")
    void only_qualified_bid_above_target_qualifies() {
        long r2SaId = 502L;  // seeded R2 SA id

        Set<Long> qualifiedCodeIds = repo.qualifiedBuyerCodes(r2SaId);

        assertThat(qualifiedCodeIds).contains(/* seeded code that bid above target */ 1001L);
        assertThat(qualifiedCodeIds).doesNotContain(/* code that bid 0 */ 1009L);
    }

    @Test
    @DisplayName("All_Buyers — every active DW/WH code qualifies")
    void all_buyers_qualifies_everyone() {
        // override the seed BRSF to All_Buyers; assert .size() == count of active DW/WH codes
    }

    @Test
    @DisplayName("Only_Qualified + ShowAllInventory — non-threshold-meeting bidders still qualify")
    void only_qualified_show_all_inventory_qualifies_nonthreshold() { /* ... */ }

    @Test
    @DisplayName("Only_Qualified + InventoryRound1QualifiedBids — bid > 0 fallback qualifies")
    void only_qualified_round1_qualified_bids_fallback() { /* ... */ }

    @Test
    @DisplayName("DW vs Wholesale uses different target-price column")
    void dw_uses_dw_avg_target_price() { /* ... */ }

    @Test
    @DisplayName("rerun is idempotent — same SA id produces same set")
    void rerun_is_idempotent() {
        Set<Long> a = repo.qualifiedBuyerCodes(502L);
        Set<Long> b = repo.qualifiedBuyerCodes(502L);
        assertThat(a).isEqualTo(b);
    }
}
```

- [ ] **Step 3: Run — should fail (class does not exist)**

Run: `cd backend && mvn -q test -Dtest=R2BuyerQualificationRepositoryIT`
Expected: FAIL — class not found.

- [ ] **Step 4: Implement `R2BuyerQualificationRepository`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns the set of active wholesale/data-wipe buyer-code ids that
 * qualify for round 2 of the supplied scheduling auction. Implements
 * the per-AE Mendix predicate from
 * {@code SUB_Round2AggregatedInventorySingleItem} as one Postgres CTE.
 */
@Repository
public class R2BuyerQualificationRepository {

    @PersistenceContext private EntityManager em;

    private static final String CTE_SQL = """
        WITH params AS (
          SELECT sa.id AS scheduling_auction_id,
                 sa.auction_id, a.week_id, sa.round,
                 brsf.regular_buyer_qualification     AS qual_mode,
                 brsf.regular_buyer_inventory_options AS inv_mode,
                 brsf.target_percent                  AS target_pct,
                 brsf.target_value                    AS target_val
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
            JOIN auctions.bid_round_selection_filters brsf ON brsf.round = sa.round
           WHERE sa.id = CAST(:r2_sa_id AS bigint)
        ),
        prior_sa AS (
          SELECT sa.id AS prev_sa_id
            FROM auctions.scheduling_auctions sa, params p
           WHERE sa.auction_id = p.auction_id
             AND sa.round = p.round - 1
        ),
        active_codes AS (
          -- Auction-bid types only — see design decision 3.5c.
          SELECT bc.id AS buyer_code_id, bc.buyer_code_type
            FROM buyer_mgmt.buyer_codes bc
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
            JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
           WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
             AND b.status = 'Active'
        ),
        r1_bids AS (
          SELECT bd.buyer_code_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS bid_amount,
                 CASE bd.buyer_code_type
                   WHEN 'Data_Wipe' THEN ai.dw_avg_target_price
                   ELSE                  ai.avg_target_price
                 END AS r1_target_price
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id AND br.submitted = TRUE
            JOIN prior_sa psa           ON psa.prev_sa_id = br.scheduling_auction_id
            JOIN auctions.aggregated_inventory ai
              ON ai.ecoid2 = bd.ecoid AND ai.merged_grade = bd.merged_grade
             AND ai.week_id = (SELECT week_id FROM params)
           WHERE bd.submitted_bid_amount > 0
        ),
        qualifies_per_ae AS (
          -- Only_Qualified predicate cascade. The new-stack enums (V59) are 2-value
          -- only — RegularBuyerQualification ∈ {All_Buyers, Only_Qualified} and
          -- RegularBuyerInventoryOption ∈ {InventoryRound1QualifiedBids, ShowAllInventory}.
          -- All_Buyers is handled separately in the UNION below — never reaches here.
          SELECT r.buyer_code_id,
                 CASE
                   WHEN r.r1_target_price = 0 AND r.bid_amount > 0 THEN TRUE
                   WHEN r.r1_target_price > 0
                        AND r.bid_amount / r.r1_target_price >= 1 - p.target_pct THEN TRUE
                   WHEN (r.r1_target_price - r.bid_amount) <= p.target_val THEN TRUE
                   WHEN p.inv_mode = 'InventoryRound1QualifiedBids' AND r.bid_amount > 0 THEN TRUE
                   WHEN p.inv_mode = 'ShowAllInventory' THEN TRUE
                   ELSE FALSE
                 END AS qualifies
            FROM r1_bids r, params p
           WHERE p.qual_mode = 'Only_Qualified'
        )
        SELECT ac.buyer_code_id
          FROM active_codes ac, params p
         WHERE p.qual_mode = 'All_Buyers'
        UNION
        SELECT q.buyer_code_id FROM qualifies_per_ae q WHERE q.qualifies = TRUE
        """;

    public Set<Long> qualifiedBuyerCodes(long r2SchedulingAuctionId) {
        @SuppressWarnings("unchecked")
        List<Number> rows = em.createNativeQuery(CTE_SQL)
            .setParameter("r2_sa_id", r2SchedulingAuctionId)
            .getResultList();
        Set<Long> ids = new HashSet<>(rows.size());
        for (Number n : rows) ids.add(n.longValue());
        return ids;
    }
}
```

- [ ] **Step 5: Run tests — should pass**

Run: `cd backend && mvn -q test -Dtest=R2BuyerQualificationRepositoryIT`
Expected: PASS — all five test cases.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R2BuyerQualificationRepositoryIT.java \
        backend/src/test/resources/fixtures/auctions/r2-init-seed.sql
git commit -m "feat(5): R2BuyerQualificationRepository ports SUB_Round2AggregatedInventorySingleItem CTE"
```

---

## Task 7: `R2SpecialBuyerRepository` (special-treatment CTE)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2SpecialBuyerRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R2SpecialBuyerRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

```java
@SpringBootTest
@Sql(scripts = "/fixtures/auctions/r2-init-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class R2SpecialBuyerRepositoryIT {

    @Autowired R2SpecialBuyerRepository repo;

    @Test
    @DisplayName("STB override: stb_allow_all_buyers_override=TRUE returns all special-buyer codes")
    void override_returns_all_codes() { /* ... */ }

    @Test
    @DisplayName("zero prior-round bids → STB-eligible")
    void no_prior_bids_is_stb() { /* ... */ }

    @Test
    @DisplayName("any prior-round bid disqualifies the buyer's whole DW/WH set")
    void any_prior_bid_disqualifies() { /* ... */ }

    @Test
    @DisplayName("non-special buyers excluded entirely")
    void non_special_buyers_excluded() { /* ... */ }
}
```

- [ ] **Step 2: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=R2SpecialBuyerRepositoryIT`
Expected: FAIL.

- [ ] **Step 3: Implement `R2SpecialBuyerRepository`**

Use the CTE from design §7.2. Returns `Set<Long>` of `buyer_code_id`.

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R2SpecialBuyerRepositoryIT`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/R2SpecialBuyerRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/R2SpecialBuyerRepositoryIT.java
git commit -m "feat(5): R2SpecialBuyerRepository ports SUB_ListBuyerCodesForSpecialBuyers CTE"
```

---

## Task 8: Extend `QualifiedBuyerCodeRepository` — bulk insert + junctions

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryR2IT.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
@DisplayName("bulkInsertForR2 writes three sets — qualified, special, not_qualified")
void bulkInsertForR2_writes_three_sets() {
    long saId = 502L;
    Set<Long> qualified = Set.of(1001L, 1002L);
    Set<Long> special   = Set.of(1003L);

    int rowsWritten = repo.bulkInsertForR2(saId, qualified, special);

    // assertions: 3 inserted as Qualified (1001, 1002, 1003), 1003 with is_special_treatment=TRUE,
    // remaining active DW/WH codes inserted as Not_Qualified
    assertThat(rowsWritten).isEqualTo(/* total active DW/WH codes seeded */ 8);
    assertThat(repo.findBySchedulingAuctionId(saId))
        .filteredOn(qbc -> qbc.getBuyerCodeId() == 1003L)
        .singleElement()
        .satisfies(qbc -> {
            assertThat(qbc.isSpecialTreatment()).isTrue();
            assertThat(qbc.getQualificationType()).isEqualTo(QualificationType.Qualified);
        });
}

@Test
@DisplayName("bulkInsertJunctions populates qbc_buyer_codes + qbc_scheduling_auctions")
void junctions_populated() { /* ... */ }
```

- [ ] **Step 2: Run — should fail**

Run: `cd backend && mvn -q test -Dtest=QualifiedBuyerCodeRepositoryR2IT`
Expected: FAIL.

- [ ] **Step 3: Add `bulkInsertForR2` + `bulkInsertJunctions` + `findBySchedulingAuctionId`**

```java
@Modifying
@Transactional(propagation = Propagation.MANDATORY)
@Query(value = """
    INSERT INTO buyer_mgmt.qualified_buyer_codes (
        scheduling_auction_id, buyer_code_id, qualification_type,
        included, is_special_treatment, created_date, changed_date
    )
    SELECT :saId, bc.id,
           CASE WHEN bc.id = ANY(CAST(:specialIds   AS bigint[])) THEN 'Qualified'
                WHEN bc.id = ANY(CAST(:qualifiedIds AS bigint[])) THEN 'Qualified'
                ELSE 'Not_Qualified' END,
           CASE WHEN bc.id = ANY(CAST(:specialIds   AS bigint[])) THEN TRUE
                WHEN bc.id = ANY(CAST(:qualifiedIds AS bigint[])) THEN TRUE
                ELSE FALSE END,
           bc.id = ANY(CAST(:specialIds AS bigint[])),
           NOW(), NOW()
      FROM buyer_mgmt.buyer_codes bc
      JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
      JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
     WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
       AND b.status = 'Active'
     GROUP BY bc.id
    """, nativeQuery = true)
int bulkInsertForR2(@Param("saId") Long saId,
                    @Param("qualifiedIds") Long[] qualifiedIds,
                    @Param("specialIds")   Long[] specialIds);

@Modifying
@Transactional(propagation = Propagation.MANDATORY)
@Query(value = """
    INSERT INTO buyer_mgmt.qbc_buyer_codes (qualified_buyer_code_id, buyer_code_id)
    SELECT qbc.id, qbc.buyer_code_id
      FROM buyer_mgmt.qualified_buyer_codes qbc
     WHERE qbc.scheduling_auction_id = :saId
       AND NOT EXISTS (
         SELECT 1 FROM buyer_mgmt.qbc_buyer_codes j
          WHERE j.qualified_buyer_code_id = qbc.id
            AND j.buyer_code_id = qbc.buyer_code_id);
    INSERT INTO buyer_mgmt.qbc_scheduling_auctions (qualified_buyer_code_id, scheduling_auction_id)
    SELECT qbc.id, :saId
      FROM buyer_mgmt.qualified_buyer_codes qbc
     WHERE qbc.scheduling_auction_id = :saId
       AND NOT EXISTS (
         SELECT 1 FROM buyer_mgmt.qbc_scheduling_auctions j
          WHERE j.qualified_buyer_code_id = qbc.id
            AND j.scheduling_auction_id = :saId);
    """, nativeQuery = true)
int bulkInsertJunctions(@Param("saId") Long saId);

List<QualifiedBuyerCode> findBySchedulingAuctionId(Long schedulingAuctionId);
```

Service callers convert `Set<Long>` → `Long[]` before passing.

- [ ] **Step 4: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=QualifiedBuyerCodeRepositoryR2IT`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryR2IT.java
git commit -m "feat(5): bulkInsertForR2 + bulkInsertJunctions on QualifiedBuyerCodeRepository"
```

---

## Task 9: `BidDataForAllAERepository` (special-buyer bid_data bulk INSERT)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataForAllAERepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataForAllAERepositoryIT.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
@DisplayName("inserts one bid_data row per (special-QBC, non-deprecated AE)")
void inserts_one_per_qbc_per_ae() {
    long saId      = 502L;
    long codeId    = 1003L;  // seeded special-treatment QBC's buyer_code
    long bidRoundId = ensureBidRound(saId, codeId);  // helper
    long bidDataDocId = ensureBidDataDoc(bidRoundId);  // helper

    int rowsInserted = repo.insertForSpecialBuyer(saId, codeId, bidRoundId, bidDataDocId);

    assertThat(rowsInserted).isEqualTo(/* count of non-deprecated AE for the seeded week */ 5);
    // verify DW vs WH branch of target_price + maximum_quantity
}

@Test
@DisplayName("excludes deprecated AEs")
void excludes_deprecated_aes() { /* ... */ }
```

- [ ] **Step 2: Implement the bulk INSERT (per design §7.4)**

Use the SQL from design §7.4 with `:bid_round_id`, `:bid_data_doc_id`,
and `:r2_sa_id` parameters and a `WHERE qbc.buyer_code_id = :buyer_code_id`
filter so the call is per-buyer-code.

- [ ] **Step 3: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=BidDataForAllAERepositoryIT`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataForAllAERepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataForAllAERepositoryIT.java
git commit -m "feat(5): BidDataForAllAERepository ports SUB_CreateBidDataForAllAE bulk INSERT"
```

---

## Task 10: `BidDataForAllAEService`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/BidDataForAllAEService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r2init/BidDataForAllAEServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class BidDataForAllAEServiceTest {

    @Mock BidDataForAllAERepository repo;
    @Mock BidRoundService bidRoundService;
    @Mock BidDataDocService bidDataDocService;

    @Test
    @DisplayName("for each special QBC, ensures bid_round + bid_data_doc and inserts bid_data rows")
    void per_qbc_invocation() {
        long saId = 502L;
        Set<Long> specialCodes = Set.of(1003L, 1004L);
        when(bidRoundService.getOrCreate(eq(saId), anyLong())).thenReturn(7001L, 7002L);
        when(bidDataDocService.getOrCreate(anyLong())).thenReturn(8001L, 8002L);
        when(repo.insertForSpecialBuyer(eq(saId), anyLong(), anyLong(), anyLong()))
            .thenReturn(5);

        int total = service.generateForSpecialBuyers(saId, specialCodes);

        assertThat(total).isEqualTo(10);
        verify(repo, times(2)).insertForSpecialBuyer(eq(saId), anyLong(), anyLong(), anyLong());
    }
}
```

- [ ] **Step 2: Implement `BidDataForAllAEService`**

```java
@Service
@Transactional(propagation = Propagation.MANDATORY)
public class BidDataForAllAEService {

    private final BidDataForAllAERepository repo;
    private final BidRoundService bidRoundService;
    private final BidDataDocService bidDataDocService;

    public BidDataForAllAEService(BidDataForAllAERepository repo,
                                  BidRoundService bidRoundService,
                                  BidDataDocService bidDataDocService) {
        this.repo = repo;
        this.bidRoundService = bidRoundService;
        this.bidDataDocService = bidDataDocService;
    }

    public int generateForSpecialBuyers(long r2SchedulingAuctionId, Set<Long> buyerCodeIds) {
        int total = 0;
        for (Long codeId : buyerCodeIds) {
            long bidRoundId    = bidRoundService.getOrCreate(r2SchedulingAuctionId, codeId);
            long bidDataDocId  = bidDataDocService.getOrCreate(bidRoundId);
            total += repo.insertForSpecialBuyer(r2SchedulingAuctionId, codeId, bidRoundId, bidDataDocId);
        }
        return total;
    }
}
```

`BidRoundService.getOrCreate` and `BidDataDocService.getOrCreate` are
existing methods used by `Round1InitializationService` — reuse rather
than duplicate.

- [ ] **Step 3: Run — should pass**

- [ ] **Step 4: Commit**

```bash
git commit -m "feat(5): BidDataForAllAEService — per-QBC bid_data generation for special buyers"
```

---

## Task 11: `R2BuyerAssignmentService` + `R2BuyerAssignmentResult`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentService.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentResult.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/R2BuyerAssignmentCompletedEvent.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentServiceTest.java`

- [ ] **Step 1: Write `R2BuyerAssignmentResult` record**

```java
public record R2BuyerAssignmentResult(
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int specialBidDataCount,
    long durationMs,
    boolean skipped
) {}
```

- [ ] **Step 2: Write the event record**

```java
public record R2BuyerAssignmentCompletedEvent(
    long schedulingAuctionId,
    long auctionId,
    Long weekId,
    int qualifiedCount,
    int specialTreatmentCount
) {}
```

- [ ] **Step 3: Write the failing service tests**

```java
@ExtendWith(MockitoExtension.class)
class R2BuyerAssignmentServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock AuctionsFeatureConfigRepository cfgRepo;
    @Mock R2BuyerQualificationRepository qualRepo;
    @Mock R2SpecialBuyerRepository       specialRepo;
    @Mock QualifiedBuyerCodeRepository   qbcRepo;
    @Mock BidDataForAllAEService         specialBidDataService;
    @Mock RecalcStatusUpdater            statusUpdater;
    @Mock ApplicationEventPublisher      events;

    @InjectMocks R2BuyerAssignmentService service;

    @Test
    @DisplayName("happy path — flips RUNNING, computes sets, writes QBCs, seeds special bid_data, marks SUCCESS")
    void happy_path() { /* ... */ }

    @Test
    @DisplayName("calculate_round2_buyer_participation = FALSE → SKIPPED, no QBC writes")
    void config_gate_false_short_circuits_to_skipped() { /* ... */ }

    @Test
    @DisplayName("round != 2 throws IllegalArgumentException before status flip")
    void rejects_wrong_round() { /* ... */ }

    @Test
    @DisplayName("repo throw → FAILED status (truncated error) + propagate")
    void repo_throw_marks_failed() { /* ... */ }

    @Test
    @DisplayName("status already RUNNING → RecalcAlreadyRunningException")
    void already_running_throws() { /* ... */ }

    @Test
    @DisplayName("event published only on SUCCESS path")
    void event_only_on_success() { /* ... */ }
}
```

- [ ] **Step 4: Implement `R2BuyerAssignmentService`**

```java
@Service
public class R2BuyerAssignmentService {

    private static final String PROCESS = "R2_INIT";
    private static final long FEATURE_CONFIG_ID = 1L;  // singleton row

    private static final Logger log = LoggerFactory.getLogger(R2BuyerAssignmentService.class);

    private final SchedulingAuctionRepository saRepo;
    private final AuctionsFeatureConfigRepository cfgRepo;
    private final R2BuyerQualificationRepository qualRepo;
    private final R2SpecialBuyerRepository       specialRepo;
    private final QualifiedBuyerCodeRepository   qbcRepo;
    private final BidDataForAllAEService         specialBidDataService;
    private final RecalcStatusUpdater            statusUpdater;
    private final ApplicationEventPublisher      events;

    // ... constructor

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R2BuyerAssignmentResult run(long schedulingAuctionId) {
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + schedulingAuctionId));

        if (sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "R2 buyer assignment only valid for round 2; was " + sa.getRound());
        }

        Long weekId = saRepo.findWeekIdById(schedulingAuctionId);  // existing helper

        AuctionsFeatureConfig cfg = cfgRepo.findById(FEATURE_CONFIG_ID)
            .orElseThrow(() -> new IllegalStateException("auctions_feature_config singleton missing"));

        if (!cfg.isCalculateRound2BuyerParticipation()) {
            statusUpdater.markSkipped(schedulingAuctionId, PROCESS);
            log.info("R2_INIT skipped (config gate FALSE) schedulingAuctionId={}", schedulingAuctionId);
            return new R2BuyerAssignmentResult(0, 0, 0, 0, 0, true);
        }

        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R2_INIT, schedulingAuctionId);
        }

        long start = System.currentTimeMillis();
        try {
            Set<Long> qualified = qualRepo.qualifiedBuyerCodes(schedulingAuctionId);
            Set<Long> special   = specialRepo.specialTreatmentBuyerCodes(schedulingAuctionId);

            // Phase 5: clear + bulk insert.
            // V72 flattened the M:M junctions; bulkInsertForR2 writes the
            // direct FK columns. No bulkInsertJunctions call needed.
            qbcRepo.deleteBySchedulingAuctionId(schedulingAuctionId);
            int totalRows = qbcRepo.bulkInsertForR2(schedulingAuctionId,
                qualified.toArray(new Long[0]), special.toArray(new Long[0]));

            int qualifiedCount = qualified.size() + special.size();  // both branches Qualified
            int specialCount   = special.size();
            int notQualified   = totalRows - qualifiedCount;

            // Phase 6: special-buyer bid_data
            int specialBidData = specialBidDataService.generateForSpecialBuyers(
                schedulingAuctionId, special);

            statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

            long durationMs = System.currentTimeMillis() - start;
            log.info("R2_INIT success schedulingAuctionId={} qualified={} special={} notQual={} bidData={} ms={}",
                schedulingAuctionId, qualifiedCount, specialCount, notQualified, specialBidData, durationMs);

            events.publishEvent(new R2BuyerAssignmentCompletedEvent(
                schedulingAuctionId, sa.getAuctionId(), weekId, qualifiedCount, specialCount));

            return new R2BuyerAssignmentResult(
                qualifiedCount, specialCount, notQualified, specialBidData, durationMs, false);
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("R2_INIT failed schedulingAuctionId={}", schedulingAuctionId, ex);
            throw ex;
        }
    }

    public R2BuyerAssignmentResult recalculate(long schedulingAuctionId) {
        return run(schedulingAuctionId);
    }
}
```

- [ ] **Step 5: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R2BuyerAssignmentServiceTest`
Expected: PASS — all six cases.

- [ ] **Step 6: Commit**

```bash
git commit -m "feat(5): R2BuyerAssignmentService — five-phase port of SUB_AssignRoundTwoBuyers"
```

---

## Task 12: `R2BuyerAssignmentListener` + delete stub

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentListener.java`
- Delete: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R2InitStubListener.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentListenerTest.java`

- [ ] **Step 1: Write the failing listener test**

```java
@ExtendWith(MockitoExtension.class)
class R2BuyerAssignmentListenerTest {

    @Mock R2BuyerAssignmentService service;
    @InjectMocks R2BuyerAssignmentListener listener;

    @Test
    @DisplayName("round=2 triggers service")
    void round_2_triggers_service() {
        listener.onRoundStarted(new RoundStartedEvent(1L, 2, 100L, 999L));
        verify(service).run(1L);
    }

    @Test
    @DisplayName("round=1 does NOT trigger")
    void round_1_skipped() {
        listener.onRoundStarted(new RoundStartedEvent(1L, 1, 100L, 999L));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException is logged, not propagated")
    void already_running_swallowed() { /* ... */ }

    @Test
    @DisplayName("any RuntimeException is logged, not propagated")
    void unexpected_exception_swallowed() { /* ... */ }
}
```

- [ ] **Step 2: Implement `R2BuyerAssignmentListener`** (per design §8)

- [ ] **Step 3: Delete `R2InitStubListener.java`**

```bash
rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R2InitStubListener.java
```

- [ ] **Step 4: Add the config key**

In `backend/src/main/resources/application.yml`, add (mirroring the
existing `auctions.r1-init.enabled` block):

```yaml
auctions:
  r2-init:
    enabled: true
```

- [ ] **Step 5: Run — should pass**

Run: `cd backend && mvn -q test -Dtest=R2BuyerAssignmentListenerTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat(5): R2BuyerAssignmentListener replaces R2InitStubListener"
```

---

## Task 13: Admin REST endpoint

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/R2BuyerAssignmentAdminController.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/R2BuyerAssignmentResponse.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/R2BuyerAssignmentAdminControllerIT.java`

- [ ] **Step 1: Write the response DTO**

```java
public record R2BuyerAssignmentResponse(
    long schedulingAuctionId,
    String status,
    String error,
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int specialBidDataCount,
    long durationMs
) {}
```

- [ ] **Step 2: Write the failing controller test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class R2BuyerAssignmentAdminControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("ROLE_ADMIN — POST returns 200 + RecalcResponse on success")
    @WithMockUser(roles = "ADMIN")
    void admin_can_reassign() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers", 502L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("non-admin → 403")
    @WithMockUser(roles = "BUYER")
    void non_admin_forbidden() throws Exception { /* ... */ }

    @Test
    @DisplayName("status RUNNING → 409")
    void already_running_returns_409() throws Exception { /* ... */ }

    @Test
    @DisplayName("unknown id → 404")
    void unknown_id_404() throws Exception { /* ... */ }

    @Test
    @DisplayName("round != 2 → 422")
    void wrong_round_422() throws Exception { /* ... */ }
}
```

- [ ] **Step 3: Implement the controller**

```java
@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class R2BuyerAssignmentAdminController {

    private final R2BuyerAssignmentService service;
    private final SchedulingAuctionRepository saRepo;

    // ... constructor

    @PostMapping("/{id}/reassign-r2-buyers")
    @PreAuthorize("hasRole('ADMIN')")
    public R2BuyerAssignmentResponse reassign(@PathVariable("id") long saId) {
        Instant before = Instant.now();
        R2BuyerAssignmentResult result = service.recalculate(saId);
        // re-read SA to get the persisted started/finished_at
        SchedulingAuction sa = saRepo.findById(saId).orElseThrow();
        return new R2BuyerAssignmentResponse(
            saId,
            sa.getR2InitStatus().name(),
            sa.getR2InitError(),
            sa.getR2InitStartedAt() != null ? sa.getR2InitStartedAt().atOffset(ZoneOffset.UTC) : null,
            sa.getR2InitFinishedAt() != null ? sa.getR2InitFinishedAt().atOffset(ZoneOffset.UTC) : null,
            result.qualifiedCount() - result.specialTreatmentCount(),
            result.specialTreatmentCount(),
            result.notQualifiedCount(),
            result.specialBidDataCount(),
            result.durationMs()
        );
    }
}
```

- [ ] **Step 4: Add the SecurityConfig matcher**

```java
.requestMatchers("/api/v1/admin/auctions/scheduling-auctions/*/reassign-r2-buyers")
    .hasRole("ADMIN")
```

- [ ] **Step 5: Run — should pass**

- [ ] **Step 6: Commit**

```bash
git commit -m "feat(5): admin REST endpoint /reassign-r2-buyers"
```

---

## Task 14: End-to-end IT — listener + service + DB

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r2init/R2BuyerAssignmentEndToEndIT.java`

- [ ] **Step 1: Write the test**

```java
@SpringBootTest
@AutoConfigureTestEntityManager
@Sql(scripts = "/fixtures/auctions/r2-init-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class R2BuyerAssignmentEndToEndIT {

    @Autowired ApplicationEventPublisher events;
    @Autowired QualifiedBuyerCodeRepository qbcRepo;
    @Autowired BidDataRepository           bidDataRepo;
    @Autowired SchedulingAuctionRepository saRepo;

    @Test
    @DisplayName("RoundStartedEvent(round=2) → QBCs + special bid_data + SUCCESS status")
    void full_path() {
        long r2SaId = 502L;

        // simulate cron round-start
        events.publishEvent(new RoundStartedEvent(r2SaId, 2, 501L, 999L));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            SchedulingAuction sa = saRepo.findById(r2SaId).orElseThrow();
            assertThat(sa.getR2InitStatus()).isEqualTo(RecalcStatus.SUCCESS);
            assertThat(qbcRepo.findBySchedulingAuctionId(r2SaId))
                .as("QBCs written")
                .hasSizeGreaterThan(0);
            assertThat(bidDataRepo.countByBidRound_SchedulingAuctionId(r2SaId))
                .as("special-buyer bid_data seeded")
                .isGreaterThan(0);
        });
    }
}
```

- [ ] **Step 2: Run — should pass**

- [ ] **Step 3: Commit**

```bash
git commit -m "test(5): end-to-end IT covers listener → service → DB writes"
```

---

## Task 15: Docs + ADR

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/app-metadata/modules.md`
- Modify: `docs/architecture/data-model.md`
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/business-logic/index.md`
- Create: `docs/business-logic/r2-buyer-assignment.md`
- Modify: `docs/deployment/setup.md`
- Modify: `docs/testing/coverage.md`
- Modify: `docs/tasks/auction-flow-gap-analysis-2026-05-06.md`

- [ ] **Step 1: Update each per design §10**

- [ ] **Step 2: Commit**

```bash
git commit -m "docs(5): R2 buyer assignment — ADR, business logic, data model, coverage"
```

---

## Self-Review

**Spec coverage:** every section of `auction-r2-buyer-assignment-design.md` maps to a task above:

- §5 Schema → Task 1
- §3.x AuctionsFeatureConfig field → Task 2
- §3.x SchedulingAuction fields → Task 3
- §3.2 RecalcStatusUpdater extension → Task 4
- §6.3 RecalcAlreadyRunningException.R2_INIT → Task 5
- §7.1 qualification CTE → Task 6
- §7.2 special-treatment CTE → Task 7
- §7.3 QBC three-set INSERT → Task 8
- §7.4 special bid_data INSERT → Task 9
- §3.8 BidDataForAllAEService → Task 10
- §4 service + §3.3 SKIPPED + §3.10 round gate → Task 11
- §8 listener wiring → Task 12
- §6.1 admin endpoint → Task 13
- §9 end-to-end IT → Task 14
- §10 docs → Task 15

**Type consistency:** `Set<Long>` returned from both qualification/special CTEs; `RecalcAlreadyRunningException.Process.R2_INIT` reused everywhere; `RecalcStatusUpdater` process string is consistently `"R2_INIT"`.

---

## Execution Handoff

Plan complete and saved to `docs/tasks/auction-r2-buyer-assignment-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks, fast iteration.

**2. Inline Execution** — execute tasks in this session using `superpowers:executing-plans`, batch execution with checkpoints.

Which approach?
