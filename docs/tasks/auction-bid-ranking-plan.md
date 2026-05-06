# Sub-project 4C: Bid Ranking + Target-Price Recalc Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace `BidRankingStubListener` with two independent, transactionally-isolated processes (RANKING + TARGET_PRICE) triggered by `RoundClosedEvent` for round ∈ {1, 2}, plus admin recovery endpoints, plus per-process Snowflake push events. Drives the `auctions.aggregated_inventory` round-N+1 target-price columns and `auctions.bid_data` round-N+1 rank columns.

**Architecture:** One Flyway migration (V82) adds status flags to `auctions.scheduling_auctions` and a config column to `auctions.bid_ranking_config`. Two native-CTE repositories own the SQL. Two services run in `REQUIRES_NEW` transactions; an orchestrator wraps them so one failing doesn't stop the other. Each success publishes a per-process event consumed by an `@Async("snowflakeExecutor")` Snowflake push listener (full week MERGE). Two admin REST endpoints rerun each process independently, gated by `RecalcAlreadyRunningException` → 409.

**Tech Stack:** Spring Boot 3.x, Java 21, JPA/Hibernate, native PostgreSQL CTEs via `EntityManager`, Flyway, JUnit 5 + AssertJ + Mockito, Spring `@TransactionalEventListener`, `MockMvc` for controller IT, Testcontainers PostgreSQL for repository IT.

**Spec:** `docs/tasks/auction-bid-ranking-design.md`

---

## File Structure

### New files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  event/
    BidRankingUpdatedEvent.java                    -- record event
    TargetPriceRecalculatedEvent.java              -- record event
  exception/
    RecalcAlreadyRunningException.java             -- 409 mapping
  model/auctions/
    RecalcStatus.java                              -- PENDING/RUNNING/SUCCESS/FAILED
  repository/auctions/
    BidRankingRepository.java                      -- DENSE_RANK CTE (native)
    TargetPriceRecalcRepository.java               -- GREATEST CTE (native)
  service/auctions/recalc/
    BidRankingService.java                         -- RANKING process
    TargetPriceRecalcService.java                  -- TARGET_PRICE process
    RecalcOrchestrator.java                        -- runs both, isolates failures
    RecalcRoundClosedListener.java                 -- replaces BidRankingStubListener
    RecalcStatusUpdater.java                       -- REQUIRES_NEW status sub-tx
  service/auctions/snowflake/
    BidRankingSnowflakeWriter.java                 -- writer interface
    LoggingBidRankingSnowflakeWriter.java          -- default impl
    JdbcBidRankingSnowflakeWriter.java             -- gated by recalc.snowflake.bid-ranking-writer=jdbc
    BidRankingSnowflakePushListener.java           -- @Async @AFTER_COMMIT
    TargetPriceSnowflakeWriter.java                -- writer interface
    LoggingTargetPriceSnowflakeWriter.java         -- default impl
    JdbcTargetPriceSnowflakeWriter.java            -- gated by recalc.snowflake.target-price-writer=jdbc
    TargetPriceSnowflakePushListener.java          -- @Async @AFTER_COMMIT
  controller/admin/
    RecalcAdminController.java                     -- two POST endpoints
  dto/admin/
    RecalcResponse.java                            -- response shape
```

### Modified files (Java)

```
backend/src/main/java/com/ecoatm/salesplatform/
  model/auctions/
    SchedulingAuction.java                         -- add 8 new status fields
    BidRankingConfig.java                          -- add include_reserve_floor (or create if missing)
  exception/
    GlobalExceptionHandler.java                    -- add @ExceptionHandler for RecalcAlreadyRunningException
  config/
    SecurityConfig.java                            -- add admin matchers for the two endpoints
```

### Deleted files

```
backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/
  BidRankingStubListener.java                      -- replaced by RecalcRoundClosedListener
```

(R2/R3 stubs stay.)

### Schema migration

```
backend/src/main/resources/db/migration/
  V82__auctions_recalc_status_and_config.sql       -- additive only
```

### Config

```
backend/src/main/resources/
  application.yml                                  -- add recalc.snowflake.* keys
```

### Tests

```
backend/src/test/
  java/com/ecoatm/salesplatform/
    repository/auctions/
      BidRankingRepositoryIT.java                  -- DENSE_RANK semantics
      TargetPriceRecalcRepositoryIT.java           -- GREATEST semantics
    service/auctions/recalc/
      BidRankingServiceTest.java                   -- mocked deps
      TargetPriceRecalcServiceTest.java            -- mocked deps
      RecalcOrchestratorTest.java                  -- isolation
      RecalcRoundClosedListenerTest.java           -- listener gating
      RecalcEndToEndIT.java                        -- full happy path
    service/auctions/snowflake/
      BidRankingSnowflakePushListenerTest.java     -- enabled / disabled / failure
      TargetPriceSnowflakePushListenerTest.java    -- enabled / disabled / failure
    controller/admin/
      RecalcAdminControllerIT.java                 -- 200 / 403 / 404 / 409 / 422
  resources/fixtures/auctions/
    recalc-seed.sql                                -- shared fixture
```

### Documentation

```
docs/
  api/rest-endpoints.md                            -- two new endpoints
  app-metadata/modules.md                          -- 4C entry
  architecture/data-model.md                       -- new status columns
  architecture/decisions.md                        -- new ADR for 4C
  business-logic/index.md                          -- link new doc
  business-logic/bid-ranking-and-target-price.md   -- new doc
  deployment/setup.md                              -- recalc.snowflake.* keys
  testing/coverage.md                              -- 4C target line
  tasks/auction-sub4-umbrella-design.md            -- mark 4C drafted
```

---

## Pre-flight

- [ ] **Step 1: Verify dev DB is up and current**

```bash
docker compose ps postgres
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -c "SELECT MAX(version::text) FROM flyway_schema_history;"
```

Expected: container `Up`; max version `81` (4B). If a higher migration exists, tell the user before proceeding (V82 number may collide).

- [ ] **Step 2: Verify the umbrella + spec are committed**

```bash
git log --oneline -5 docs/tasks/auction-sub4-umbrella-design.md docs/tasks/auction-bid-ranking-design.md
```

Expected: both files are in git history; spec was committed in the planning session.

- [ ] **Step 3: Verify 4A and 4B are merged**

```bash
ls backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBid.java \
   backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrder.java \
   backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PODetail.java
```

Expected: all three exist. If any missing, stop — 4C depends on 4A and 4B schemas.

- [ ] **Step 4: Confirm test runner is green before edits**

```bash
cd backend && mvn -DfailIfNoTests=false -Dtest='!*IT' test -q
```

Expected: BUILD SUCCESS. If failing, fix or escalate before adding new tests on top.

---

## Task 1: V82 schema migration — additive columns

**Files:**
- Create: `backend/src/main/resources/db/migration/V82__auctions_recalc_status_and_config.sql`

- [ ] **Step 1: Write the migration**

```sql
-- V82: 4C — Bid Ranking + Target-Price Recalc — status flags + config
-- Additive only. NOT NULL DEFAULT semantics give every existing row a
-- sane starting state without a separate backfill statement.

ALTER TABLE auctions.scheduling_auctions
    ADD COLUMN ranking_status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN ranking_error               TEXT,
    ADD COLUMN ranking_started_at          TIMESTAMPTZ,
    ADD COLUMN ranking_finished_at         TIMESTAMPTZ,
    ADD COLUMN target_price_status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN target_price_error          TEXT,
    ADD COLUMN target_price_started_at     TIMESTAMPTZ,
    ADD COLUMN target_price_finished_at    TIMESTAMPTZ,
    ADD CONSTRAINT chk_sa_ranking_status
        CHECK (ranking_status      IN ('PENDING','RUNNING','SUCCESS','FAILED')),
    ADD CONSTRAINT chk_sa_target_price_status
        CHECK (target_price_status IN ('PENDING','RUNNING','SUCCESS','FAILED'));

COMMENT ON COLUMN auctions.scheduling_auctions.ranking_status IS
    '4C recalc: PENDING (round not yet closed) | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.target_price_status IS
    '4C recalc: PENDING | RUNNING | SUCCESS | FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.ranking_error IS
    '4C recalc: exception class + message (truncated to 4000 chars) on FAILED';
COMMENT ON COLUMN auctions.scheduling_auctions.target_price_error IS
    '4C recalc: exception class + message (truncated to 4000 chars) on FAILED';

ALTER TABLE auctions.bid_ranking_config
    ADD COLUMN include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN auctions.bid_ranking_config.include_reserve_floor IS
    '4C: TRUE -> reserve_bid rows participate in DENSE_RANK as priority bidders; FALSE -> ranking is bid_data only';
```

- [ ] **Step 2: Run app to apply migration**

```bash
cd backend && mvn spring-boot:run
```

Expected: Flyway log line `Successfully applied 1 migration to schema "public" (execution time ...): V82`. Stop the app after migration applies (Ctrl+C).

- [ ] **Step 3: Verify columns landed**

```bash
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -c "\d auctions.scheduling_auctions" | grep -E "ranking_|target_price_"
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -c "\d auctions.bid_ranking_config" | grep "include_reserve_floor"
```

Expected: 8 status columns + 2 check constraints + 1 boolean column listed.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V82__auctions_recalc_status_and_config.sql
git commit -m "feat(4c): V82 — recalc status flags + reserve-floor toggle"
```

---

## Task 2: `RecalcStatus` enum + `BidRankingConfig` + `SchedulingAuction` extensions

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/RecalcStatus.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java`
- Modify (or create): `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidRankingConfig.java`

- [ ] **Step 1: Write `RecalcStatus`**

```java
package com.ecoatm.salesplatform.model.auctions;

public enum RecalcStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED
}
```

- [ ] **Step 2: Locate the existing `BidRankingConfig` entity (if any)**

```bash
find backend/src -name "BidRankingConfig.java"
```

If it exists, modify; if not, create it.

- [ ] **Step 3: Create or extend `BidRankingConfig`**

If creating new:

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bid_ranking_config", schema = "auctions")
public class BidRankingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "display_rank", nullable = false)
    private int displayRank;

    @Column(name = "minimum_bid", nullable = false, precision = 14, scale = 2)
    private BigDecimal minimumBid = BigDecimal.ZERO;

    @Column(name = "maximum_rank", nullable = false)
    private int maximumRank;

    @Column(name = "include_reserve_floor", nullable = false)
    private boolean includeReserveFloor = true;

    public Long getId() { return id; }
    public int getDisplayRank() { return displayRank; }
    public BigDecimal getMinimumBid() { return minimumBid; }
    public int getMaximumRank() { return maximumRank; }
    public boolean isIncludeReserveFloor() { return includeReserveFloor; }
    public void setIncludeReserveFloor(boolean v) { this.includeReserveFloor = v; }
}
```

If the entity already exists, only add the `includeReserveFloor` field + getter + setter to it. **Do not** rewrite the file — use `Edit` to insert the field next to the existing fields and the getter/setter alongside the existing accessors.

- [ ] **Step 4: Add 8 status fields to `SchedulingAuction`**

Add these after the existing `snowflakeJson` field:

```java
@Enumerated(EnumType.STRING)
@Column(name = "ranking_status", length = 20, nullable = false)
private RecalcStatus rankingStatus = RecalcStatus.PENDING;

@Column(name = "ranking_error", columnDefinition = "TEXT")
private String rankingError;

@Column(name = "ranking_started_at")
private java.time.Instant rankingStartedAt;

@Column(name = "ranking_finished_at")
private java.time.Instant rankingFinishedAt;

@Enumerated(EnumType.STRING)
@Column(name = "target_price_status", length = 20, nullable = false)
private RecalcStatus targetPriceStatus = RecalcStatus.PENDING;

@Column(name = "target_price_error", columnDefinition = "TEXT")
private String targetPriceError;

@Column(name = "target_price_started_at")
private java.time.Instant targetPriceStartedAt;

@Column(name = "target_price_finished_at")
private java.time.Instant targetPriceFinishedAt;
```

Add getters and setters for each. Keep them grouped together at the bottom of the existing accessor block.

- [ ] **Step 5: Compile**

```bash
cd backend && mvn compile -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/RecalcStatus.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidRankingConfig.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java
git commit -m "feat(4c): RecalcStatus + status fields on SchedulingAuction + include_reserve_floor"
```

---

## Task 3: Events + `RecalcAlreadyRunningException` + `GlobalExceptionHandler` mapping

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/BidRankingUpdatedEvent.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/TargetPriceRecalculatedEvent.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Write `BidRankingUpdatedEvent`**

```java
package com.ecoatm.salesplatform.event;

/**
 * Published AFTER_COMMIT of {@link com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService}
 * once the DENSE_RANK UPDATE succeeds.
 *
 * @param schedulingAuctionId the scheduling_auctions row that just closed
 * @param closedRound         1 or 2 — the round that closed; ranks were
 *                            written to round{closedRound + 1} columns
 * @param weekId              auctions.auctions.week_id (= mdm.week.id)
 * @param auctionId           parent auction id
 */
public record BidRankingUpdatedEvent(
        long schedulingAuctionId,
        int closedRound,
        long weekId,
        long auctionId) {}
```

- [ ] **Step 2: Write `TargetPriceRecalculatedEvent`**

```java
package com.ecoatm.salesplatform.event;

/**
 * Published AFTER_COMMIT of {@link com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService}
 * once the GREATEST UPDATE succeeds.
 */
public record TargetPriceRecalculatedEvent(
        long schedulingAuctionId,
        int closedRound,
        long weekId,
        long auctionId) {}
```

- [ ] **Step 3: Write `RecalcAlreadyRunningException`**

```java
package com.ecoatm.salesplatform.exception;

/**
 * Thrown by RANKING / TARGET_PRICE services when their state-flip UPDATE
 * matches 0 rows — meaning another caller (cron tick or admin endpoint)
 * already flipped the status to RUNNING. Maps to HTTP 409 in
 * GlobalExceptionHandler.
 */
public class RecalcAlreadyRunningException extends RuntimeException {

    public enum Process { RANKING, TARGET_PRICE }

    private final Process process;
    private final long schedulingAuctionId;

    public RecalcAlreadyRunningException(Process process, long schedulingAuctionId) {
        super(process + " recalc already running: schedulingAuctionId=" + schedulingAuctionId);
        this.process = process;
        this.schedulingAuctionId = schedulingAuctionId;
    }

    public Process getProcess() { return process; }
    public long getSchedulingAuctionId() { return schedulingAuctionId; }
}
```

- [ ] **Step 4: Add the 409 mapping in `GlobalExceptionHandler`**

Locate the existing `handleRoundAlreadyTransitioned` method and add the new handler immediately after it:

```java
@ExceptionHandler(com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException.class)
public ResponseEntity<Map<String, Object>> handleRecalcAlreadyRunning(
        com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(errorBody(HttpStatus.CONFLICT, ex.getMessage(), null));
}
```

- [ ] **Step 5: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/event/BidRankingUpdatedEvent.java \
        backend/src/main/java/com/ecoatm/salesplatform/event/TargetPriceRecalculatedEvent.java \
        backend/src/main/java/com/ecoatm/salesplatform/exception/RecalcAlreadyRunningException.java \
        backend/src/main/java/com/ecoatm/salesplatform/exception/GlobalExceptionHandler.java
git commit -m "feat(4c): events + RecalcAlreadyRunningException 409 mapping"
```

---

## Task 4: Test fixture `recalc-seed.sql`

**Files:**
- Create: `backend/src/test/resources/fixtures/auctions/recalc-seed.sql`

- [ ] **Step 1: Write the fixture**

The fixture seeds enough state to exercise both processes against round 1 close (R = 1, computing ranks + target prices for round 2). All IDs are stable so tests can assert against them directly.

```sql
-- Fixture for 4C recalc tests. Seeds:
--  • 1 mdm.week (id 9001, week_number 14, year 2026)
--  • 1 auctions.auctions (id 9001) tied to that week
--  • 3 auctions.scheduling_auctions (ids 9001, 9002, 9003) — one per round
--  • 1 bid_round per scheduling_auction (ids 9001/9002/9003)
--  • 1 bid_ranking_config singleton (assumed pre-seeded by V63 — assert below)
--  • 6 aggregated_inventory rows for 3 ecoid x 2 grade combos
--  • 12 bid_data rows in round 1 across 4 buyer codes — covering rank-eligible
--    + below-min-bid + tie-on-max-bid scenarios
--  • 2 reserve_bid rows (one matching an ecoid, one stand-alone — exercises
--    the include_reserve_floor branch)
--  • 2 target_price_factors bands + filters for both round 2 and round 3
--  • 1 active purchase_order + 2 po_detail rows (one PO covering week 14)
--  • 1 inactive purchase_order outside the week range (control)

INSERT INTO mdm.week (id, week_id, year, week_number, week_display, week_display_short, week_number_string)
VALUES (9001, 202614, 2026, 14, '2026 / Wk14', 'Wk14', '14');

INSERT INTO auctions.auctions (id, auction_title, auction_status, week_id)
VALUES (9001, '4C Test Auction', 'Started', 9001);

INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, ranking_status, target_price_status)
VALUES
  (9001, 9001, 1, 'Closed',   'PENDING', 'PENDING'),
  (9002, 9001, 2, 'Started',  'PENDING', 'PENDING'),
  (9003, 9001, 3, 'Unscheduled', 'PENDING', 'PENDING');

INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, round)
VALUES
  (9001, 9001, 1),
  (9002, 9002, 2),
  (9003, 9003, 3);

-- bid_ranking_config singleton expected to exist from V63 seed; assert it
-- and tighten thresholds for deterministic tests.
UPDATE auctions.bid_ranking_config
   SET minimum_bid = 100.00,
       display_rank = 1,
       maximum_rank = 5,
       include_reserve_floor = TRUE
 WHERE id = 1;

-- 6 aggregated_inventory rows
INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
VALUES
  (9001, 'ECO-A', 9001, 'A', 100, 50),
  (9002, 'ECO-A', 9001, 'B', 100, 50),
  (9003, 'ECO-B', 9001, 'A', 100, 50),
  (9004, 'ECO-B', 9001, 'B', 100, 50),
  (9005, 'ECO-C', 9001, 'A', 100, 50),
  (9006, 'ECO-C', 9001, 'B', 100, 50);

-- 12 bid_data rows in round 1 across 4 buyer codes (ids 91/92/93/94 from V18 seed)
-- Covering: above-min ranks, tied max bids on (ECO-A, A), below-min bid
INSERT INTO auctions.bid_data
  (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name, buyer_code_type,
   submitted_bid_amount, bid_round, week_id)
VALUES
  -- (ECO-A, A): tie at 500 between codes 91 and 92, 300 from 93, below-min 50 from 94
  (9001, 9001, 91, 'ECO-A', 'A', 'B91', 'Co91', 'Wholesale', 500.00, 1, 9001),
  (9002, 9001, 92, 'ECO-A', 'A', 'B92', 'Co92', 'Wholesale', 500.00, 1, 9001),
  (9003, 9001, 93, 'ECO-A', 'A', 'B93', 'Co93', 'Wholesale', 300.00, 1, 9001),
  (9004, 9001, 94, 'ECO-A', 'A', 'B94', 'Co94', 'Wholesale',  50.00, 1, 9001),
  -- (ECO-A, B): single bidder
  (9005, 9001, 91, 'ECO-A', 'B', 'B91', 'Co91', 'Wholesale', 200.00, 1, 9001),
  -- (ECO-B, A): three rank tiers
  (9006, 9001, 91, 'ECO-B', 'A', 'B91', 'Co91', 'Wholesale', 800.00, 1, 9001),
  (9007, 9001, 92, 'ECO-B', 'A', 'B92', 'Co92', 'Wholesale', 600.00, 1, 9001),
  (9008, 9001, 93, 'ECO-B', 'A', 'B93', 'Co93', 'Wholesale', 400.00, 1, 9001),
  -- (ECO-B, B): empty
  -- (ECO-C, A): single bidder, low
  (9009, 9001, 91, 'ECO-C', 'A', 'B91', 'Co91', 'Wholesale', 150.00, 1, 9001),
  -- (ECO-C, B): three bidders, all > min
  (9010, 9001, 91, 'ECO-C', 'B', 'B91', 'Co91', 'Wholesale', 250.00, 1, 9001),
  (9011, 9001, 92, 'ECO-C', 'B', 'B92', 'Co92', 'Wholesale', 200.00, 1, 9001),
  (9012, 9001, 93, 'ECO-C', 'B', 'B93', 'Co93', 'Wholesale', 175.00, 1, 9001);

-- 2 reserve_bid rows
--   one matches (ECO-A, A) at 700 — should rank #1 ahead of bidders' 500 tie
--   one stand-alone (ECO-D, A) at 999 — exercises priority-1 rank where
--   no auction bidders compete
INSERT INTO auctions.reserve_bid (id, product_id, grade, brand, model, bid)
VALUES
  (9001, 'ECO-A', 'A', 'BrandX', 'ModelX', 700.0000),
  (9002, 'ECO-D', 'A', 'BrandY', 'ModelY', 999.0000);

-- target_price_factors — three bands matching round 2 and round 3 filters
-- Bands: low (0-200) +10%, mid (200-1000) +5 flat, high (1000+) +2%
INSERT INTO auctions.target_price_factors (id, minimum_value, maximum_value, factor_type, factor_amount)
VALUES
  (9001,    0.00,  200.00, 'Percentage_Factor', 10.0000),
  (9002,  200.00, 1000.00, 'Flat_Amount',         5.0000),
  (9003, 1000.00, 9999999.00, 'Percentage_Factor', 2.0000);

-- bid_round_selection_filters — one each for round 2 and round 3
-- (assumes filter table has a `round` column; legacy id-style; keep consistent
-- with V59 schema)
INSERT INTO auctions.bid_round_selection_filters (id, scheduling_auction_id, round)
VALUES
  (9002, 9002, 2),
  (9003, 9003, 3);

-- Apply the three bands to both round 2 and round 3 filter rows
INSERT INTO auctions.target_price_factor_filters (target_price_factor_id, bid_round_selection_filter_id)
VALUES
  (9001, 9002), (9002, 9002), (9003, 9002),
  (9001, 9003), (9002, 9003), (9003, 9003);

-- Active purchase_order — covers week 9001 (week_from=week_to=9001)
INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
VALUES (9001, 9001, 9001, 'Wk14 2026', TRUE, 2);

INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
VALUES
  -- ECO-A grade A: PO floor 750 → wins over MaxBid+factor (500+5=505) AND reserve_bid (700)
  (9001, 9001, 91, 'ECO-A', 'A', 'ModelX', 750.0000, 50),
  -- ECO-B grade A: PO floor 100 (below-everything; MaxBid+factor 805 wins)
  (9002, 9001, 91, 'ECO-B', 'A', 'ModelY', 100.0000, 25);

-- Inactive purchase_order — outside week range; should be ignored by CTE
INSERT INTO mdm.week (id, week_id, year, week_number, week_display, week_display_short, week_number_string)
VALUES (9002, 202612, 2026, 12, '2026 / Wk12', 'Wk12', '12');

INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
VALUES (9002, 9002, 9002, 'Wk12 2026 (inactive)', TRUE, 1);

INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
VALUES (9003, 9002, 91, 'ECO-A', 'A', 'ModelX', 9999.0000, 1);
```

- [ ] **Step 2: Sanity-check the SQL parses against dev DB**

```bash
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -v ON_ERROR_STOP=1 -1 -c "BEGIN; SELECT 'noop'; ROLLBACK;" \
  -f backend/src/test/resources/fixtures/auctions/recalc-seed.sql 2>&1 | tail -20
```

If buyer codes 91-94 don't exist in your dev DB, the FK INSERTs into `auctions.bid_data` and `auctions.po_detail` will fail. Adjust the IDs to match real seeded `buyer_mgmt.buyer_codes.id` values from V18 (`grep -n "INSERT INTO buyer_mgmt.buyer_codes" backend/src/main/resources/db/migration/V18__buyer_mgmt_data.sql | head -5`). The fixture is intentionally compact — pick any 4 codes; `STRING_AGG` order is alphabetical so test assertions reference `B91,B92` etc. Update `code` strings to match real codes if you change IDs.

Expected: psql exits 0; the ROLLBACK undoes everything so the fixture data does not persist. (`-1` wraps everything in one transaction.)

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/resources/fixtures/auctions/recalc-seed.sql
git commit -m "test(4c): recalc-seed.sql fixture for ranking + target-price IT"
```

---

## Task 5: `BidRankingRepository` — DENSE_RANK CTE

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidRankingRepository.java`

- [ ] **Step 1: Write the repository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Native CTE for round-N+1 bid rank computation.
 *
 * <p>Ports Mendix {@code ACT_TriggerBidRankingCalculation}. Single bulk
 * UPDATE with DENSE_RANK over (ecoid, merged_grade) ORDER BY
 * submitted_bid_amount DESC. The reserve-floor branch is selected by
 * {@code auctions.bid_ranking_config.include_reserve_floor}.
 *
 * <p>Two SQL constants because the round-suffix columns must be statically
 * spelled — Postgres does not have dynamic column references in UPDATE
 * SET targets. R = 1 → writes round2 columns; R = 2 → writes round3 columns.
 *
 * <p>{@code @Transactional(propagation = MANDATORY)} — caller (BidRankingService)
 * already owns the REQUIRES_NEW boundary.
 */
@Repository
public class BidRankingRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String RANKING_SQL_R2 = """
        WITH params AS (
          SELECT
            sa.id            AS scheduling_auction_id,
            a.week_id        AS week_id,
            sa.round         AS closed_round,
            cfg.minimum_bid           AS min_bid,
            cfg.display_rank          AS min_display_rank,
            cfg.maximum_rank          AS max_display_rank,
            cfg.include_reserve_floor AS include_eb
          FROM auctions.scheduling_auctions sa
          JOIN auctions.auctions a ON a.id = sa.auction_id
          CROSS JOIN auctions.bid_ranking_config cfg
          WHERE sa.id = CAST(:round_id AS bigint)
        ),
        candidates AS (
          SELECT bd.id AS bid_data_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS amount
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
           WHERE bd.bid_round = p.closed_round
             AND bd.submitted_bid_amount >= p.min_bid

          UNION ALL

          SELECT NULL::bigint AS bid_data_id, rb.product_id AS ecoid,
                 rb.grade AS merged_grade, rb.bid AS amount
            FROM auctions.reserve_bid rb
            JOIN params p ON p.include_eb = TRUE
        ),
        ranked AS (
          SELECT bid_data_id,
                 DENSE_RANK() OVER (
                   PARTITION BY ecoid, merged_grade
                   ORDER BY amount DESC
                 ) AS calc_rank
            FROM candidates
        )
        UPDATE auctions.bid_data target
           SET round2_bid_rank         = r.calc_rank,
               display_round2_bid_rank = CASE
                 WHEN r.calc_rank BETWEEN p.min_display_rank AND p.max_display_rank
                   THEN r.calc_rank
                 ELSE NULL
               END
          FROM ranked r, params p
         WHERE r.bid_data_id IS NOT NULL
           AND target.id = r.bid_data_id
        """;

    private static final String RANKING_SQL_R3 = """
        WITH params AS (
          SELECT
            sa.id            AS scheduling_auction_id,
            a.week_id        AS week_id,
            sa.round         AS closed_round,
            cfg.minimum_bid           AS min_bid,
            cfg.display_rank          AS min_display_rank,
            cfg.maximum_rank          AS max_display_rank,
            cfg.include_reserve_floor AS include_eb
          FROM auctions.scheduling_auctions sa
          JOIN auctions.auctions a ON a.id = sa.auction_id
          CROSS JOIN auctions.bid_ranking_config cfg
          WHERE sa.id = CAST(:round_id AS bigint)
        ),
        candidates AS (
          SELECT bd.id AS bid_data_id, bd.ecoid, bd.merged_grade,
                 bd.submitted_bid_amount AS amount
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
           WHERE bd.bid_round = p.closed_round
             AND bd.submitted_bid_amount >= p.min_bid

          UNION ALL

          SELECT NULL::bigint AS bid_data_id, rb.product_id AS ecoid,
                 rb.grade AS merged_grade, rb.bid AS amount
            FROM auctions.reserve_bid rb
            JOIN params p ON p.include_eb = TRUE
        ),
        ranked AS (
          SELECT bid_data_id,
                 DENSE_RANK() OVER (
                   PARTITION BY ecoid, merged_grade
                   ORDER BY amount DESC
                 ) AS calc_rank
            FROM candidates
        )
        UPDATE auctions.bid_data target
           SET round3_bid_rank         = r.calc_rank,
               display_round3_bid_rank = CASE
                 WHEN r.calc_rank BETWEEN p.min_display_rank AND p.max_display_rank
                   THEN r.calc_rank
                 ELSE NULL
               END
          FROM ranked r, params p
         WHERE r.bid_data_id IS NOT NULL
           AND target.id = r.bid_data_id
        """;

    /**
     * Runs the DENSE_RANK UPDATE for the round that just closed.
     *
     * @param schedulingAuctionId scheduling_auctions.id of the closed round
     * @param closedRound         1 or 2; throws IAE otherwise
     * @return number of bid_data rows updated
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int rankClosedRound(long schedulingAuctionId, int closedRound) {
        String sql = switch (closedRound) {
            case 1 -> RANKING_SQL_R2;
            case 2 -> RANKING_SQL_R3;
            default -> throw new IllegalArgumentException(
                "closedRound must be 1 or 2: was " + closedRound);
        };
        Query q = em.createNativeQuery(sql);
        q.setParameter("round_id", schedulingAuctionId);
        return q.executeUpdate();
    }
}
```

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidRankingRepository.java
git commit -m "feat(4c): BidRankingRepository — DENSE_RANK CTE (R2 + R3 variants)"
```

---

## Task 6: `BidRankingRepositoryIT` — DENSE_RANK semantics

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidRankingRepositoryIT.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class BidRankingRepositoryIT extends PostgresIntegrationTest {

    @Autowired BidRankingRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    void ranks_round1_close_into_round2_columns_with_reserve_floor() {
        int updated = repo.rankClosedRound(9001L, 1);

        assertThat(updated).isPositive();

        // (ECO-A, A): with include_reserve_floor=TRUE, reserve_bid 700 ranks #1.
        // Bidders' 500/500 tie ranks #2 (DENSE_RANK ties get same rank, then 300 = #3).
        // 50 below min_bid (100) → excluded → no rank.
        assertRank(9001L, 2);   // bidder 91 @ 500
        assertRank(9002L, 2);   // bidder 92 @ 500 (tie)
        assertRank(9003L, 3);   // bidder 93 @ 300
        assertRank(9004L, null); // bidder 94 @ 50 (below min_bid)
    }

    @Test
    void display_rank_clamps_to_null_outside_window() {
        // bid_ranking_config: display_rank=1, maximum_rank=5
        // (ECO-A, A) with reserve floor: ranks 1,2,2,3 — all within [1,5].
        // Tighten to make ranks fall outside: set max_display_rank=1.
        jdbc.update("UPDATE auctions.bid_ranking_config SET maximum_rank = 1");

        repo.rankClosedRound(9001L, 1);

        // Calculated rank survives in round2_bid_rank; display_round2_bid_rank
        // is NULL when calc_rank > maximum_rank.
        Integer calc91 = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 9001", Integer.class);
        Integer disp91 = jdbc.queryForObject(
            "SELECT display_round2_bid_rank FROM auctions.bid_data WHERE id = 9001", Integer.class);

        assertThat(calc91).isEqualTo(2);  // raw rank still computed
        assertThat(disp91).isNull();       // but clamped out of display window
    }

    @Test
    void exclude_reserve_floor_drops_eb_rows_from_ranking() {
        jdbc.update("UPDATE auctions.bid_ranking_config SET include_reserve_floor = FALSE");

        repo.rankClosedRound(9001L, 1);

        // (ECO-A, A): without EB, the 500/500 tie is now rank #1.
        assertRank(9001L, 1);
        assertRank(9002L, 1);
        assertRank(9003L, 2);
    }

    @Test
    void rerunning_is_idempotent() {
        repo.rankClosedRound(9001L, 1);
        Integer firstRun = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 9001", Integer.class);

        repo.rankClosedRound(9001L, 1);
        Integer secondRun = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 9001", Integer.class);

        assertThat(secondRun).isEqualTo(firstRun);
    }

    @Test
    void closed_round_2_writes_round3_rank_columns() {
        // Promote round 2 bids by copying the round 1 fixture rows into round 2.
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            SELECT id + 1000, 9002, buyer_code_id, ecoid, merged_grade, code, company_name,
                   buyer_code_type, submitted_bid_amount, 2, week_id
              FROM auctions.bid_data WHERE bid_round = 1
            """);

        repo.rankClosedRound(9002L, 2);

        Integer rank = jdbc.queryForObject(
            "SELECT round3_bid_rank FROM auctions.bid_data WHERE id = 10001", Integer.class);

        assertThat(rank).isPositive();
    }

    @Test
    void rejects_invalid_round() {
        assertThatThrownBy(() -> repo.rankClosedRound(9001L, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("closedRound must be 1 or 2");
    }

    private void assertRank(long bidDataId, Integer expected) {
        Integer actual = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = ?",
            Integer.class, bidDataId);
        assertThat(actual).as("rank for bid_data id=%s", bidDataId).isEqualTo(expected);
    }
}
```

- [ ] **Step 2: Run tests — expect them to pass given Task 5 implementation**

```bash
cd backend && mvn -Dtest=BidRankingRepositoryIT verify -q
```

Expected: 6 tests pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidRankingRepositoryIT.java
git commit -m "test(4c): BidRankingRepositoryIT — DENSE_RANK semantics"
```

---

## Task 7: `TargetPriceRecalcRepository` — GREATEST CTE

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/TargetPriceRecalcRepository.java`

- [ ] **Step 1: Write the repository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Native CTE for round-N+1 target-price recalc.
 *
 * <p>Single bulk UPDATE that, for the closed round R:
 * <ol>
 *   <li>Computes MaxBid per (ecoid, merged_grade) over closed-round bids.</li>
 *   <li>Aggregates buyer codes that hit the MaxBid (comma-joined).</li>
 *   <li>Looks up matching {@code target_price_factors} band for round R+1.</li>
 *   <li>Joins {@code auctions.reserve_bid} (4A) for EB floor.</li>
 *   <li>Joins {@code auctions.po_detail} (4B) — max price across active POs
 *       overlapping the auction week.</li>
 *   <li>Computes {@code GREATEST(MaxBid+factor, EB, PO)}; UPDATEs
 *       {@code aggregated_inventory} columns for round R + R+1.</li>
 * </ol>
 *
 * <p>Two SQL constants — R = 1 → writes round1_max_bid + round2_target_price
 * + r2_target_price_factor[_type] + round2_eb_for_target. R = 2 → writes
 * round2_max_bid + round3_target_price + r3_* + round3_eb_for_target.
 */
@Repository
public class TargetPriceRecalcRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String TARGET_PRICE_SQL_R1_TO_R2 = """
        WITH params AS (
          SELECT a.week_id AS week_id, sa.round AS closed_round
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
           WHERE sa.id = CAST(:round_id AS bigint)
        ),
        max_bids AS (
          SELECT bd.ecoid, bd.merged_grade,
                 MAX(bd.submitted_bid_amount) AS max_bid
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
                         AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount > 0
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        buyer_codes AS (
          SELECT bd.ecoid, bd.merged_grade,
                 STRING_AGG(DISTINCT bd.code, ',' ORDER BY bd.code) AS codes
            FROM auctions.bid_data bd
            JOIN max_bids mb USING (ecoid, merged_grade)
            JOIN params   p  ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount = mb.max_bid
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        factors AS (
          SELECT mb.ecoid, mb.merged_grade,
                 tpf.factor_amount, tpf.factor_type
            FROM max_bids mb
            JOIN auctions.target_price_factors tpf
              ON mb.max_bid BETWEEN tpf.minimum_value AND tpf.maximum_value
            JOIN auctions.target_price_factor_filters tpff
              ON tpff.target_price_factor_id = tpf.id
            JOIN auctions.bid_round_selection_filters brf
              ON brf.id = tpff.bid_round_selection_filter_id
            JOIN params p ON brf.round = p.closed_round + 1
        ),
        eb AS (
          SELECT product_id AS ecoid, grade AS merged_grade,
                 bid AS reserve_value
            FROM auctions.reserve_bid
        ),
        po_max AS (
          SELECT pod.product_id AS ecoid, pod.grade AS merged_grade,
                 MAX(pod.price) AS po_price
            FROM auctions.po_detail pod
            JOIN auctions.purchase_order po ON po.id = pod.purchase_order_id
            JOIN params p
              ON p.week_id BETWEEN po.week_from_id AND po.week_to_id
           GROUP BY pod.product_id, pod.grade
        ),
        evaluated AS (
          SELECT mb.ecoid, mb.merged_grade,
                 mb.max_bid,
                 bc.codes        AS max_buyer_codes,
                 f.factor_amount,
                 f.factor_type,
                 eb.reserve_value,
                 po.po_price,
                 CASE
                   WHEN f.factor_type = 'Percentage_Factor'
                     THEN ROUND(mb.max_bid * f.factor_amount / 100, 2)
                   WHEN f.factor_type = 'Flat_Amount'
                     THEN ROUND(mb.max_bid + f.factor_amount, 2)
                   ELSE mb.max_bid
                 END AS max_bid_plus_factor
            FROM max_bids   mb
            LEFT JOIN buyer_codes bc USING (ecoid, merged_grade)
            LEFT JOIN factors     f  USING (ecoid, merged_grade)
            LEFT JOIN eb              USING (ecoid, merged_grade)
            LEFT JOIN po_max      po USING (ecoid, merged_grade)
        )
        UPDATE auctions.aggregated_inventory ai
           SET round1_max_bid                = e.max_bid,
               round1_max_bid_buyer_code     = e.max_buyer_codes,
               round2_target_price           = GREATEST(
                                                 e.max_bid_plus_factor,
                                                 COALESCE(e.reserve_value, 0),
                                                 COALESCE(e.po_price, 0)
                                               ),
               r2_target_price_factor        = e.factor_amount,
               r2_target_price_factor_type   = e.factor_type,
               round2_eb_for_target          = COALESCE(e.reserve_value, 0)
          FROM evaluated e, params p
         WHERE ai.week_id = p.week_id
           AND ai.ecoid2 = e.ecoid
           AND ai.merged_grade = e.merged_grade
        """;

    private static final String TARGET_PRICE_SQL_R2_TO_R3 = """
        WITH params AS (
          SELECT a.week_id AS week_id, sa.round AS closed_round
            FROM auctions.scheduling_auctions sa
            JOIN auctions.auctions a ON a.id = sa.auction_id
           WHERE sa.id = CAST(:round_id AS bigint)
        ),
        max_bids AS (
          SELECT bd.ecoid, bd.merged_grade,
                 MAX(bd.submitted_bid_amount) AS max_bid
            FROM auctions.bid_data bd
            JOIN params p ON p.week_id = bd.week_id
                         AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount > 0
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        buyer_codes AS (
          SELECT bd.ecoid, bd.merged_grade,
                 STRING_AGG(DISTINCT bd.code, ',' ORDER BY bd.code) AS codes
            FROM auctions.bid_data bd
            JOIN max_bids mb USING (ecoid, merged_grade)
            JOIN params   p  ON p.week_id = bd.week_id AND bd.bid_round = p.closed_round
           WHERE bd.submitted_bid_amount = mb.max_bid
           GROUP BY bd.ecoid, bd.merged_grade
        ),
        factors AS (
          SELECT mb.ecoid, mb.merged_grade,
                 tpf.factor_amount, tpf.factor_type
            FROM max_bids mb
            JOIN auctions.target_price_factors tpf
              ON mb.max_bid BETWEEN tpf.minimum_value AND tpf.maximum_value
            JOIN auctions.target_price_factor_filters tpff
              ON tpff.target_price_factor_id = tpf.id
            JOIN auctions.bid_round_selection_filters brf
              ON brf.id = tpff.bid_round_selection_filter_id
            JOIN params p ON brf.round = p.closed_round + 1
        ),
        eb AS (
          SELECT product_id AS ecoid, grade AS merged_grade, bid AS reserve_value
            FROM auctions.reserve_bid
        ),
        po_max AS (
          SELECT pod.product_id AS ecoid, pod.grade AS merged_grade,
                 MAX(pod.price) AS po_price
            FROM auctions.po_detail pod
            JOIN auctions.purchase_order po ON po.id = pod.purchase_order_id
            JOIN params p
              ON p.week_id BETWEEN po.week_from_id AND po.week_to_id
           GROUP BY pod.product_id, pod.grade
        ),
        evaluated AS (
          SELECT mb.ecoid, mb.merged_grade,
                 mb.max_bid,
                 bc.codes        AS max_buyer_codes,
                 f.factor_amount,
                 f.factor_type,
                 eb.reserve_value,
                 po.po_price,
                 CASE
                   WHEN f.factor_type = 'Percentage_Factor'
                     THEN ROUND(mb.max_bid * f.factor_amount / 100, 2)
                   WHEN f.factor_type = 'Flat_Amount'
                     THEN ROUND(mb.max_bid + f.factor_amount, 2)
                   ELSE mb.max_bid
                 END AS max_bid_plus_factor
            FROM max_bids   mb
            LEFT JOIN buyer_codes bc USING (ecoid, merged_grade)
            LEFT JOIN factors     f  USING (ecoid, merged_grade)
            LEFT JOIN eb              USING (ecoid, merged_grade)
            LEFT JOIN po_max      po USING (ecoid, merged_grade)
        )
        UPDATE auctions.aggregated_inventory ai
           SET round2_max_bid                = e.max_bid,
               round2_max_bid_buyer_code     = e.max_buyer_codes,
               round3_target_price           = GREATEST(
                                                 e.max_bid_plus_factor,
                                                 COALESCE(e.reserve_value, 0),
                                                 COALESCE(e.po_price, 0)
                                               ),
               r3_target_price_factor        = e.factor_amount,
               r3_target_price_factor_type   = e.factor_type,
               round3_eb_for_target          = COALESCE(e.reserve_value, 0)
          FROM evaluated e, params p
         WHERE ai.week_id = p.week_id
           AND ai.ecoid2 = e.ecoid
           AND ai.merged_grade = e.merged_grade
        """;

    @Transactional(propagation = Propagation.MANDATORY)
    public int recalcClosedRound(long schedulingAuctionId, int closedRound) {
        String sql = switch (closedRound) {
            case 1 -> TARGET_PRICE_SQL_R1_TO_R2;
            case 2 -> TARGET_PRICE_SQL_R2_TO_R3;
            default -> throw new IllegalArgumentException(
                "closedRound must be 1 or 2: was " + closedRound);
        };
        Query q = em.createNativeQuery(sql);
        q.setParameter("round_id", schedulingAuctionId);
        return q.executeUpdate();
    }
}
```

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/TargetPriceRecalcRepository.java
git commit -m "feat(4c): TargetPriceRecalcRepository — GREATEST CTE (R1→R2 + R2→R3 variants)"
```

---

## Task 8: `TargetPriceRecalcRepositoryIT` — GREATEST semantics

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/TargetPriceRecalcRepositoryIT.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class TargetPriceRecalcRepositoryIT extends PostgresIntegrationTest {

    @Autowired TargetPriceRecalcRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    void writes_round1_max_bid_and_round2_target_price() {
        repo.recalcClosedRound(9001L, 1);

        // (ECO-A, A): MaxBid 500, factor (Flat_Amount, 5) → MaxBid+factor = 505
        // EB = 700; PO = 750. GREATEST(505, 700, 750) = 750.
        assertTargetPrice("ECO-A", "A", new BigDecimal("750.0000"));

        // round1_max_bid + round1_max_bid_buyer_code: B91, B92 tied at 500
        BigDecimal maxBid = jdbc.queryForObject(
            "SELECT round1_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        String codes = jdbc.queryForObject(
            "SELECT round1_max_bid_buyer_code FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            String.class, "ECO-A", "A");
        assertThat(maxBid).isEqualByComparingTo("500.00");
        assertThat(codes).isEqualTo("B91,B92");
    }

    @Test
    void greatest_picks_eb_when_eb_beats_max_bid_plus_factor_and_po() {
        // (ECO-D, A): EB only (999), no bid_data, no PO. aggregated_inventory
        // does not have an ECO-D row in the fixture, so this exercises that
        // ECO-D rows update only if they exist. Add the row first.
        jdbc.update("""
            INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
            VALUES (9099, 'ECO-D', 9001, 'A', 100, 50)
            """);

        // ECO-D has no bid_data row in round 1, so MaxBid is null and the row
        // would be skipped. Add a single bid_data row to force the path.
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            VALUES (9099, 9001, 91, 'ECO-D', 'A', 'B91', 'Co91', 'Wholesale', 200.00, 1, 9001)
            """);

        repo.recalcClosedRound(9001L, 1);

        // (ECO-D, A): MaxBid=200, factor (Flat_Amount, 5) → 205. EB=999. PO=none.
        // GREATEST(205, 999, 0) = 999.
        assertTargetPrice("ECO-D", "A", new BigDecimal("999.0000"));
    }

    @Test
    void greatest_picks_max_bid_plus_factor_when_eb_and_po_are_zero() {
        repo.recalcClosedRound(9001L, 1);

        // (ECO-B, A): MaxBid=800 (B91), factor (Flat_Amount, 5) → 805.
        // No EB row for ECO-B; PO floor 100 (below). GREATEST(805, 0, 100) = 805.
        assertTargetPrice("ECO-B", "A", new BigDecimal("805.0000"));
    }

    @Test
    void factor_band_lookup_respects_round_filter() {
        repo.recalcClosedRound(9001L, 1);

        // (ECO-C, B): MaxBid=250, factor matched via Flat_Amount band [200, 1000]
        //   → 250 + 5 = 255. EB=0; PO=0. → 255.
        assertTargetPrice("ECO-C", "B", new BigDecimal("255.0000"));
    }

    @Test
    void no_factor_match_falls_back_to_max_bid() {
        // Delete the band-filter for round 2 to force "no factor matched"
        jdbc.update("DELETE FROM auctions.target_price_factor_filters WHERE bid_round_selection_filter_id = 9002");

        repo.recalcClosedRound(9001L, 1);

        // (ECO-B, A): MaxBid=800; factor → null; CASE → fallback 800.
        // EB=0; PO=100. GREATEST(800, 0, 100) = 800.
        assertTargetPrice("ECO-B", "A", new BigDecimal("800.0000"));
    }

    @Test
    void inactive_po_outside_week_range_is_ignored() {
        // The inactive PO at 9999 is for week 9002 (Wk12). Confirm it does NOT
        // leak into the target price for week 9001 (Wk14).
        repo.recalcClosedRound(9001L, 1);

        // If the inactive PO leaked, ECO-A target would be 9999. Verify it's 750.
        assertTargetPrice("ECO-A", "A", new BigDecimal("750.0000"));
    }

    @Test
    void writes_round3_columns_when_round_2_closes() {
        // Promote the round-1 fixture rows into round-2 territory
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            SELECT id + 1000, 9002, buyer_code_id, ecoid, merged_grade, code, company_name,
                   buyer_code_type, submitted_bid_amount, 2, week_id
              FROM auctions.bid_data WHERE bid_round = 1
            """);
        // Mark round 2 closed so it lines up
        jdbc.update("UPDATE auctions.scheduling_auctions SET round_status='Closed' WHERE id=9002");

        repo.recalcClosedRound(9002L, 2);

        BigDecimal r3 = jdbc.queryForObject(
            "SELECT round3_target_price FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        assertThat(r3).isEqualByComparingTo("750.0000");

        BigDecimal r2max = jdbc.queryForObject(
            "SELECT round2_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        assertThat(r2max).isEqualByComparingTo("500.00");
    }

    private void assertTargetPrice(String ecoid, String grade, BigDecimal expected) {
        BigDecimal actual = jdbc.queryForObject(
            "SELECT round2_target_price FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, ecoid, grade);
        assertThat(actual)
            .as("round2_target_price for (%s, %s)", ecoid, grade)
            .isEqualByComparingTo(expected);
    }
}
```

- [ ] **Step 2: Run tests**

```bash
cd backend && mvn -Dtest=TargetPriceRecalcRepositoryIT verify -q
```

Expected: 7 tests pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/TargetPriceRecalcRepositoryIT.java
git commit -m "test(4c): TargetPriceRecalcRepositoryIT — GREATEST semantics"
```

---

## Task 9: `RecalcStatusUpdater` — REQUIRES_NEW status sub-tx

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java`

The status sub-tx pattern: when the recalc tx rolls back (failure path), the FAILED status row would also roll back. To survive a rollback, the FAILED-status write must run in a separate `REQUIRES_NEW` tx that commits independently.

- [ ] **Step 1: Write the helper**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Writes recalc status fields in a REQUIRES_NEW transaction so the status
 * survives even when the surrounding recalc tx rolls back.
 *
 * <p>Used by {@link BidRankingService} and {@link TargetPriceRecalcService}
 * for the FAILED + RUNNING-clear paths. The SUCCESS path can use the
 * surrounding REQUIRES_NEW tx (no need for a sub-tx since the tx commits).
 */
@Component
public class RecalcStatusUpdater {

    private final JdbcTemplate jdbc;

    public RecalcStatusUpdater(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Atomic state-flip from any non-RUNNING state to RUNNING. Returns true
     * if the row flipped (caller proceeds), false if it was already RUNNING
     * (caller throws RecalcAlreadyRunningException).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryFlipToRunning(long schedulingAuctionId, String process) {
        String column = process.equals("RANKING") ? "ranking" : "target_price";
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'RUNNING',
                   %s_started_at  = NOW(),
                   %s_finished_at = NULL,
                   %s_error       = NULL
             WHERE id = ?
               AND %s_status <> 'RUNNING'
            """.formatted(column, column, column, column, column);
        int rows = jdbc.update(sql, schedulingAuctionId);
        return rows == 1;
    }

    /**
     * Mark SUCCESS. Called from inside the recalc tx — uses MANDATORY so
     * a missing parent tx is a wiring bug.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void markSuccess(long schedulingAuctionId, String process) {
        String column = process.equals("RANKING") ? "ranking" : "target_price";
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'SUCCESS',
                   %s_finished_at = NOW(),
                   %s_error       = NULL
             WHERE id = ?
            """.formatted(column, column, column);
        jdbc.update(sql, schedulingAuctionId);
    }

    /**
     * Mark FAILED in a REQUIRES_NEW tx so the row survives the parent
     * recalc tx rolling back. Caller is in the catch block of the recalc
     * service and the parent tx is doomed.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(long schedulingAuctionId, String process, String errorText) {
        String column = process.equals("RANKING") ? "ranking" : "target_price";
        String truncated = errorText == null
            ? null
            : errorText.length() > 4000 ? errorText.substring(0, 4000) : errorText;
        String sql = """
            UPDATE auctions.scheduling_auctions
               SET %s_status      = 'FAILED',
                   %s_finished_at = NOW(),
                   %s_error       = ?
             WHERE id = ?
            """.formatted(column, column, column);
        jdbc.update(sql, truncated, schedulingAuctionId);
    }

    Instant now() { return Instant.now(); }   // package-private for test override
}
```

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcStatusUpdater.java
git commit -m "feat(4c): RecalcStatusUpdater — REQUIRES_NEW status sub-tx helper"
```

---

## Task 10: `BidRankingService` — RANKING process

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingService.java`

- [ ] **Step 1: Write the service**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.BidRankingRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidRankingService {

    private static final Logger log = LoggerFactory.getLogger(BidRankingService.class);
    private static final String PROCESS = "RANKING";

    private final BidRankingRepository repo;
    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater statusUpdater;
    private final ApplicationEventPublisher events;

    public BidRankingService(BidRankingRepository repo,
                             SchedulingAuctionRepository saRepo,
                             RecalcStatusUpdater statusUpdater,
                             ApplicationEventPublisher events) {
        this.repo = repo;
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs RANKING for a closed round. Throws
     * {@link RecalcAlreadyRunningException} if status already RUNNING.
     * Throws {@link IllegalArgumentException} for closed round &notin; {1,2}.
     * On any other failure, status flips to FAILED in a sub-tx and the
     * exception is rethrown.
     *
     * <p>{@code REQUIRES_NEW} so each process is independent of orchestrator
     * failure modes.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(long schedulingAuctionId) {
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + schedulingAuctionId));

        if (sa.getRound() != 1 && sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "RANKING only valid for closed round 1 or 2; was " + sa.getRound());
        }

        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.RANKING, schedulingAuctionId);
        }

        long start = System.currentTimeMillis();
        int rows;
        try {
            rows = repo.rankClosedRound(schedulingAuctionId, sa.getRound());
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("RANKING failed schedulingAuctionId={} round={}",
                schedulingAuctionId, sa.getRound(), ex);
            throw ex;
        }

        statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

        long durationMs = System.currentTimeMillis() - start;
        log.info("RANKING success schedulingAuctionId={} round={} rows={} durationMs={}",
            schedulingAuctionId, sa.getRound(), rows, durationMs);

        events.publishEvent(new BidRankingUpdatedEvent(
            schedulingAuctionId, sa.getRound(), sa.getAuctionWeekId(), sa.getAuctionId()));
    }

    /**
     * Admin re-rank entry point. Same shape as {@link #run} but the caller
     * is a controller; just delegates.
     */
    public void recalculate(long schedulingAuctionId) {
        run(schedulingAuctionId);
    }
}
```

> **Note on `getAuctionWeekId()`:** `SchedulingAuction` does not have a direct `weekId` field — it's reachable via `auction.weekId`. Add the helper getter on the entity:
>
> ```java
> @Transient
> public long getAuctionWeekId() {
>     // Caller responsibility to ensure auction is loaded; service uses
>     // EntityGraph or accesses via repository. Simpler approach: add a
>     // join column or a helper method on SchedulingAuctionRepository.
>     throw new UnsupportedOperationException("call SchedulingAuctionRepository.findWeekIdById instead");
> }
> ```
>
> Better: add a `findWeekIdById(long)` query to `SchedulingAuctionRepository` returning the long. Update the service:
>
> ```java
> long weekId = saRepo.findWeekIdById(schedulingAuctionId);
> events.publishEvent(new BidRankingUpdatedEvent(schedulingAuctionId, sa.getRound(), weekId, sa.getAuctionId()));
> ```

- [ ] **Step 2: Add `findWeekIdById` to `SchedulingAuctionRepository`**

In `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java`, add:

```java
@Query("SELECT a.weekId FROM SchedulingAuction sa JOIN Auction a ON sa.auctionId = a.id WHERE sa.id = :id")
Long findWeekIdById(@Param("id") long schedulingAuctionId);
```

(Adjust to match the existing JPA mapping style — if the repo uses native queries, use a native equivalent.)

- [ ] **Step 3: Replace the `getAuctionWeekId()` placeholder in `BidRankingService`**

Replace the publishEvent line with:

```java
long weekId = saRepo.findWeekIdById(schedulingAuctionId);
events.publishEvent(new BidRankingUpdatedEvent(
    schedulingAuctionId, sa.getRound(), weekId, sa.getAuctionId()));
```

- [ ] **Step 4: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingService.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java
git commit -m "feat(4c): BidRankingService — RANKING process with status sub-tx"
```

---

## Task 11: `BidRankingServiceTest` — service unit tests

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingServiceTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.BidRankingRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidRankingServiceTest {

    @Mock BidRankingRepository repo;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater statusUpdater;
    @Mock ApplicationEventPublisher events;

    @InjectMocks BidRankingService service;

    private SchedulingAuction sa;

    @BeforeEach
    void setUp() {
        sa = new SchedulingAuction();
        sa.setId(9001L);
        sa.setRound(1);
        sa.setAuctionId(9001L);
    }

    @Test
    void happy_path_flips_runs_marks_success_publishes_event() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        when(repo.rankClosedRound(9001L, 1)).thenReturn(8);

        service.run(9001L);

        verify(statusUpdater).tryFlipToRunning(9001L, "RANKING");
        verify(repo).rankClosedRound(9001L, 1);
        verify(statusUpdater).markSuccess(9001L, "RANKING");

        ArgumentCaptor<BidRankingUpdatedEvent> captor =
            ArgumentCaptor.forClass(BidRankingUpdatedEvent.class);
        verify(events).publishEvent(captor.capture());
        BidRankingUpdatedEvent e = captor.getValue();
        assertThat(e.schedulingAuctionId()).isEqualTo(9001L);
        assertThat(e.closedRound()).isEqualTo(1);
        assertThat(e.weekId()).isEqualTo(9001L);
        assertThat(e.auctionId()).isEqualTo(9001L);
    }

    @Test
    void rejects_when_status_already_running() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(false);

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(RecalcAlreadyRunningException.class);

        verify(repo, never()).rankClosedRound(anyLong(), anyInt());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void marks_failed_and_rethrows_when_repo_throws() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        RuntimeException boom = new RuntimeException("DB exploded");
        when(repo.rankClosedRound(9001L, 1)).thenThrow(boom);

        assertThatThrownBy(() -> service.run(9001L))
            .isSameAs(boom);

        verify(statusUpdater).markFailed(eq(9001L), eq("RANKING"),
            org.mockito.ArgumentMatchers.argThat(s -> s.contains("DB exploded")));
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejects_round_3() {
        sa.setRound(3);
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("only valid for closed round 1 or 2");

        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
    }

    @Test
    void truncates_long_error_message_to_4000_chars() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        String huge = "x".repeat(5000);
        when(repo.rankClosedRound(9001L, 1)).thenThrow(new RuntimeException(huge));

        assertThatThrownBy(() -> service.run(9001L)).isInstanceOf(RuntimeException.class);

        ArgumentCaptor<String> errCaptor = ArgumentCaptor.forClass(String.class);
        verify(statusUpdater).markFailed(eq(9001L), eq("RANKING"), errCaptor.capture());
        assertThat(errCaptor.getValue()).hasSize(4000);
    }

    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
```

- [ ] **Step 2: Run tests**

```bash
cd backend && mvn -Dtest=BidRankingServiceTest test -q
```

Expected: 5 tests pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingServiceTest.java
git commit -m "test(4c): BidRankingServiceTest"
```

---

## Task 12: `TargetPriceRecalcService` — TARGET_PRICE process

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcService.java`

- [ ] **Step 1: Write the service**

Mirror `BidRankingService` exactly except:
- Different repository: `TargetPriceRecalcRepository`
- Different process constant: `"TARGET_PRICE"`
- Different event class: `TargetPriceRecalculatedEvent`
- Different exception process value: `RecalcAlreadyRunningException.Process.TARGET_PRICE`
- Different log prefix: `"TARGET_PRICE"`

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.TargetPriceRecalcRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TargetPriceRecalcService {

    private static final Logger log = LoggerFactory.getLogger(TargetPriceRecalcService.class);
    private static final String PROCESS = "TARGET_PRICE";

    private final TargetPriceRecalcRepository repo;
    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater statusUpdater;
    private final ApplicationEventPublisher events;

    public TargetPriceRecalcService(TargetPriceRecalcRepository repo,
                                    SchedulingAuctionRepository saRepo,
                                    RecalcStatusUpdater statusUpdater,
                                    ApplicationEventPublisher events) {
        this.repo = repo;
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(long schedulingAuctionId) {
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + schedulingAuctionId));

        if (sa.getRound() != 1 && sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "TARGET_PRICE only valid for closed round 1 or 2; was " + sa.getRound());
        }

        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.TARGET_PRICE, schedulingAuctionId);
        }

        long start = System.currentTimeMillis();
        int rows;
        try {
            rows = repo.recalcClosedRound(schedulingAuctionId, sa.getRound());
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("TARGET_PRICE failed schedulingAuctionId={} round={}",
                schedulingAuctionId, sa.getRound(), ex);
            throw ex;
        }

        statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

        long durationMs = System.currentTimeMillis() - start;
        log.info("TARGET_PRICE success schedulingAuctionId={} round={} rows={} durationMs={}",
            schedulingAuctionId, sa.getRound(), rows, durationMs);

        long weekId = saRepo.findWeekIdById(schedulingAuctionId);
        events.publishEvent(new TargetPriceRecalculatedEvent(
            schedulingAuctionId, sa.getRound(), weekId, sa.getAuctionId()));
    }

    public void recalculate(long schedulingAuctionId) {
        run(schedulingAuctionId);
    }
}
```

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcService.java
git commit -m "feat(4c): TargetPriceRecalcService — TARGET_PRICE process"
```

---

## Task 13: `TargetPriceRecalcServiceTest`

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcServiceTest.java`

- [ ] **Step 1: Write the tests**

Same shape as `BidRankingServiceTest` but for the TargetPriceRecalcService. Mirror all 5 tests:
- happy path → SUCCESS + `TargetPriceRecalculatedEvent` published
- already running → `RecalcAlreadyRunningException` + no repo call + no event
- repo throws → markFailed + rethrow + no event
- round 3 → `IllegalArgumentException`
- long error message → truncated to 4000

Replace mocks/captures with `TargetPriceRecalcRepository` + `TargetPriceRecalculatedEvent`. Assert process constant `"TARGET_PRICE"`.

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.TargetPriceRecalcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetPriceRecalcServiceTest {

    @Mock TargetPriceRecalcRepository repo;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater statusUpdater;
    @Mock ApplicationEventPublisher events;

    @InjectMocks TargetPriceRecalcService service;

    private SchedulingAuction sa;

    @BeforeEach
    void setUp() {
        sa = new SchedulingAuction();
        sa.setId(9001L);
        sa.setRound(1);
        sa.setAuctionId(9001L);
    }

    @Test
    void happy_path_publishes_target_price_event() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        when(repo.recalcClosedRound(9001L, 1)).thenReturn(6);

        service.run(9001L);

        verify(statusUpdater).markSuccess(9001L, "TARGET_PRICE");
        ArgumentCaptor<TargetPriceRecalculatedEvent> c =
            ArgumentCaptor.forClass(TargetPriceRecalculatedEvent.class);
        verify(events).publishEvent(c.capture());
        assertThat(c.getValue().closedRound()).isEqualTo(1);
    }

    @Test
    void rejects_when_already_running() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(false);

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(RecalcAlreadyRunningException.class);
        verify(repo, never()).recalcClosedRound(anyLong(), anyInt());
    }

    @Test
    void marks_failed_and_rethrows() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        RuntimeException boom = new RuntimeException("kaboom");
        when(repo.recalcClosedRound(9001L, 1)).thenThrow(boom);

        assertThatThrownBy(() -> service.run(9001L)).isSameAs(boom);
        verify(statusUpdater).markFailed(eq(9001L), eq("TARGET_PRICE"),
            org.mockito.ArgumentMatchers.argThat(s -> s.contains("kaboom")));
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejects_round_3() {
        sa.setRound(3);
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(IllegalArgumentException.class);
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
    }

    @Test
    void truncates_long_error() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        String huge = "y".repeat(5000);
        when(repo.recalcClosedRound(9001L, 1)).thenThrow(new RuntimeException(huge));

        assertThatThrownBy(() -> service.run(9001L)).isInstanceOf(RuntimeException.class);
        ArgumentCaptor<String> err = ArgumentCaptor.forClass(String.class);
        verify(statusUpdater).markFailed(eq(9001L), eq("TARGET_PRICE"), err.capture());
        assertThat(err.getValue()).hasSize(4000);
    }
}
```

- [ ] **Step 2: Run tests**

```bash
cd backend && mvn -Dtest=TargetPriceRecalcServiceTest test -q
```

Expected: 5 tests pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcServiceTest.java
git commit -m "test(4c): TargetPriceRecalcServiceTest"
```

---

## Task 14: `RecalcOrchestrator` + test

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestrator.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestratorTest.java`

- [ ] **Step 1: Write the orchestrator**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecalcOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RecalcOrchestrator.class);

    private final BidRankingService rankingService;
    private final TargetPriceRecalcService targetPriceService;

    public RecalcOrchestrator(BidRankingService rankingService,
                              TargetPriceRecalcService targetPriceService) {
        this.rankingService = rankingService;
        this.targetPriceService = targetPriceService;
    }

    /**
     * Runs both processes for a closed round. Each is wrapped in try/catch so
     * one failing does NOT prevent the other from running. Per design §4 each
     * service is itself REQUIRES_NEW so the rollback boundaries are
     * independent.
     */
    public void runForClosedRound(long schedulingAuctionId) {
        try {
            rankingService.run(schedulingAuctionId);
        } catch (RuntimeException ex) {
            log.error("RANKING failed in orchestrator schedulingAuctionId={}",
                schedulingAuctionId, ex);
        }

        try {
            targetPriceService.run(schedulingAuctionId);
        } catch (RuntimeException ex) {
            log.error("TARGET_PRICE failed in orchestrator schedulingAuctionId={}",
                schedulingAuctionId, ex);
        }
    }
}
```

- [ ] **Step 2: Write the test**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecalcOrchestratorTest {

    @Mock BidRankingService rankingService;
    @Mock TargetPriceRecalcService targetPriceService;

    @InjectMocks RecalcOrchestrator orchestrator;

    @Test
    void runs_both_in_happy_path() {
        orchestrator.runForClosedRound(9001L);

        verify(rankingService).run(9001L);
        verify(targetPriceService).run(9001L);
    }

    @Test
    void target_price_still_runs_when_ranking_throws() {
        doThrow(new RuntimeException("ranking exploded"))
            .when(rankingService).run(9001L);

        orchestrator.runForClosedRound(9001L);

        verify(targetPriceService).run(9001L);  // still called
    }

    @Test
    void ranking_runs_first_even_if_target_price_throws() {
        doThrow(new RuntimeException("tp exploded"))
            .when(targetPriceService).run(9001L);

        orchestrator.runForClosedRound(9001L);

        verify(rankingService).run(9001L);
    }

    @Test
    void no_throw_propagates_when_both_fail() {
        doThrow(new RuntimeException("a")).when(rankingService).run(9001L);
        doThrow(new RuntimeException("b")).when(targetPriceService).run(9001L);

        // Must not throw — orchestrator swallows both.
        orchestrator.runForClosedRound(9001L);
    }
}
```

- [ ] **Step 3: Run tests**

```bash
cd backend && mvn -Dtest=RecalcOrchestratorTest test -q
```

Expected: 4 tests pass.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestrator.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestratorTest.java
git commit -m "feat(4c): RecalcOrchestrator + test — process isolation"
```

---

## Task 15: `RecalcRoundClosedListener` (replaces stub) + test

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcRoundClosedListener.java`
- Delete: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/BidRankingStubListener.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcRoundClosedListenerTest.java`

- [ ] **Step 1: Write the listener**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Replaces {@code BidRankingStubListener}. Triggers RANKING + TARGET_PRICE
 * on round close for round ∈ {1, 2}.
 *
 * <p>Listener thread is the cron-tick post-commit thread. The orchestrator
 * swallows per-process failures; this listener also catches anything
 * unexpected (wiring bug) so it never propagates to other AFTER_COMMIT
 * listeners on the same event (e.g. {@code R3PreProcessStubListener}).
 */
@Component
public class RecalcRoundClosedListener {

    private static final Logger log = LoggerFactory.getLogger(RecalcRoundClosedListener.class);

    private final RecalcOrchestrator orchestrator;

    public RecalcRoundClosedListener(RecalcOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 1 && event.round() != 2) {
            return;
        }
        try {
            orchestrator.runForClosedRound(event.roundId());
        } catch (RuntimeException ex) {
            log.error("Recalc orchestrator threw unexpectedly roundId={}",
                event.roundId(), ex);
        }
    }
}
```

- [ ] **Step 2: Delete the stub**

```bash
git rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/BidRankingStubListener.java
```

- [ ] **Step 3: Write the test**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecalcRoundClosedListenerTest {

    @Mock RecalcOrchestrator orchestrator;

    @InjectMocks RecalcRoundClosedListener listener;

    @Test
    void triggers_for_round_1() {
        listener.onRoundClosed(new RoundClosedEvent(9001L, 1, 9001L, 9001L));

        verify(orchestrator).runForClosedRound(9001L);
    }

    @Test
    void triggers_for_round_2() {
        listener.onRoundClosed(new RoundClosedEvent(9002L, 2, 9001L, 9001L));

        verify(orchestrator).runForClosedRound(9002L);
    }

    @Test
    void skips_round_3() {
        listener.onRoundClosed(new RoundClosedEvent(9003L, 3, 9001L, 9001L));

        verify(orchestrator, never()).runForClosedRound(anyLong());
    }

    @Test
    void swallows_orchestrator_throw() {
        doThrow(new RuntimeException("boom"))
            .when(orchestrator).runForClosedRound(9001L);

        // Must not throw — would corrupt other AFTER_COMMIT listeners.
        listener.onRoundClosed(new RoundClosedEvent(9001L, 1, 9001L, 9001L));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
}
```

- [ ] **Step 4: Run tests**

```bash
cd backend && mvn -Dtest=RecalcRoundClosedListenerTest test -q
```

Expected: 4 tests pass.

- [ ] **Step 5: Verify the stub is gone and not referenced**

```bash
grep -r "BidRankingStubListener" backend/src/
```

Expected: no matches.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcRoundClosedListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcRoundClosedListenerTest.java
git commit -m "feat(4c): RecalcRoundClosedListener replaces BidRankingStubListener"
```

---

## Task 16: `RecalcResponse` DTO + `RecalcAdminController` + SecurityConfig

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/admin/RecalcResponse.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminController.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`

- [ ] **Step 1: Write `RecalcResponse`**

```java
package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

public record RecalcResponse(
        long schedulingAuctionId,
        int closedRound,
        String status,        // "SUCCESS"
        String error,         // always null on success path; FAILED is reflected via 5xx mapping
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        int rowsAffected,
        long durationMs) {}
```

- [ ] **Step 2: Write the controller**

```java
package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.RecalcResponse;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService;
import com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class RecalcAdminController {

    private final BidRankingService rankingService;
    private final TargetPriceRecalcService targetPriceService;
    private final SchedulingAuctionRepository saRepo;

    public RecalcAdminController(BidRankingService rankingService,
                                 TargetPriceRecalcService targetPriceService,
                                 SchedulingAuctionRepository saRepo) {
        this.rankingService = rankingService;
        this.targetPriceService = targetPriceService;
        this.saRepo = saRepo;
    }

    @PostMapping("/{id}/re-rank")
    @PreAuthorize("hasRole('ADMIN')")
    public RecalcResponse reRank(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        rankingService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        int round = saRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("not found: " + id))
            .getRound();

        // rowsAffected isn't returned through the service for cleanliness; use 0
        // here and let callers consult the status columns. (Future enhancement:
        // thread the count through.)
        return new RecalcResponse(id, round, "SUCCESS", null,
            startedAt, finishedAt, 0, System.currentTimeMillis() - start);
    }

    @PostMapping("/{id}/recalculate-target-price")
    @PreAuthorize("hasRole('ADMIN')")
    public RecalcResponse recalculateTargetPrice(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        targetPriceService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        int round = saRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("not found: " + id))
            .getRound();

        return new RecalcResponse(id, round, "SUCCESS", null,
            startedAt, finishedAt, 0, System.currentTimeMillis() - start);
    }
}
```

- [ ] **Step 3: Wire SecurityConfig**

In `SecurityConfig.java` add to the existing `requestMatchers` chain (within the `.authorizeHttpRequests` block):

```java
.requestMatchers(
    "/api/v1/admin/auctions/scheduling-auctions/*/re-rank",
    "/api/v1/admin/auctions/scheduling-auctions/*/recalculate-target-price"
).hasRole("ADMIN")
```

(Place it adjacent to similar admin matchers — search the file for a similar `hasRole("ADMIN")` line and add right after.)

- [ ] **Step 4: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/admin/RecalcResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminController.java \
        backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java
git commit -m "feat(4c): admin endpoints — re-rank + recalculate-target-price"
```

---

## Task 17: `RecalcAdminControllerIT` — controller integration tests

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminControllerIT.java`

- [ ] **Step 1: Write the integration test**

```java
package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class RecalcAdminControllerIT extends PostgresIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void re_rank_admin_returns_200_with_success_body() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/9001/re-rank"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedulingAuctionId").value(9001))
            .andExpect(jsonPath("$.closedRound").value(1))
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.startedAt").exists())
            .andExpect(jsonPath("$.finishedAt").exists());
    }

    @Test
    @WithMockUser(roles = "USER")   // not ADMIN
    void re_rank_non_admin_403() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/9001/re-rank"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void re_rank_unknown_id_404() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/999999/re-rank"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void re_rank_round_3_returns_422() throws Exception {
        // SchedulingAuction 9003 is round 3
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/9003/re-rank"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Autowired JdbcTemplate jdbc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void re_rank_returns_409_when_already_running() throws Exception {
        // Pre-flip status to RUNNING via JdbcTemplate so the state-flip UPDATE
        // in BidRankingService sees a RUNNING row and short-circuits to 409.
        jdbc.update("UPDATE auctions.scheduling_auctions SET ranking_status = 'RUNNING' WHERE id = 9001");

        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/9001/re-rank"))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void recalculate_target_price_admin_returns_200() throws Exception {
        mvc.perform(post(
                "/api/v1/admin/auctions/scheduling-auctions/9001/recalculate-target-price"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
```

Make sure the test class imports `org.springframework.jdbc.core.JdbcTemplate`.

- [ ] **Step 2: Run tests**

```bash
cd backend && mvn -Dtest=RecalcAdminControllerIT verify -q
```

Expected: 6 tests pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminControllerIT.java
git commit -m "test(4c): RecalcAdminControllerIT — auth + status mappings"
```

---

## Task 18: Snowflake writer interfaces + Logging impls

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingBidRankingSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingTargetPriceSnowflakeWriter.java`

- [ ] **Step 1: Write `BidRankingSnowflakeWriter`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface BidRankingSnowflakeWriter {
    /**
     * Push the full {@code (weekId, targetRound)} slice of bid ranks to
     * Snowflake AUCTIONS.BUYER_BID. Implementations decide how — JDBC MERGE
     * for prod; logging-only for dev/test.
     */
    void pushBidRankings(long weekId, int targetRound);
}
```

- [ ] **Step 2: Write `LoggingBidRankingSnowflakeWriter`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "bid-ranking-writer",
                       havingValue = "logging", matchIfMissing = true)
public class LoggingBidRankingSnowflakeWriter implements BidRankingSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingBidRankingSnowflakeWriter.class);

    @Override
    public void pushBidRankings(long weekId, int targetRound) {
        log.info("[snowflake-bid-ranking] LOGGING IMPL — would push weekId={} round={}", weekId, targetRound);
    }
}
```

- [ ] **Step 3: Write the same pair for target price**

```java
// TargetPriceSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface TargetPriceSnowflakeWriter {
    void pushTargetPrices(long weekId, int targetRound);
}
```

```java
// LoggingTargetPriceSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "target-price-writer",
                       havingValue = "logging", matchIfMissing = true)
public class LoggingTargetPriceSnowflakeWriter implements TargetPriceSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingTargetPriceSnowflakeWriter.class);

    @Override
    public void pushTargetPrices(long weekId, int targetRound) {
        log.info("[snowflake-target-price] LOGGING IMPL — would push weekId={} round={}", weekId, targetRound);
    }
}
```

- [ ] **Step 4: Compile**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingBidRankingSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingTargetPriceSnowflakeWriter.java
git commit -m "feat(4c): snowflake writer interfaces + logging impls"
```

---

## Task 19: Snowflake push listeners + tests

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListener.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListenerTest.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListenerTest.java`

- [ ] **Step 1: Write `BidRankingSnowflakePushListener`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BidRankingSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(BidRankingSnowflakePushListener.class);

    private final BidRankingSnowflakeWriter writer;
    private final Environment env;

    public BidRankingSnowflakePushListener(BidRankingSnowflakeWriter writer, Environment env) {
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBidRankingUpdated(BidRankingUpdatedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("snowflake.enabled", Boolean.class, false))) {
            log.info("[snowflake] disabled; skipping bid-ranking push for event={}", event);
            return;
        }
        try {
            writer.pushBidRankings(event.weekId(), event.closedRound() + 1);
            log.info("[snowflake] bid-ranking pushed weekId={} targetRound={}",
                event.weekId(), event.closedRound() + 1);
        } catch (RuntimeException ex) {
            log.error("[snowflake] bid-ranking push failed for event={}", event, ex);
            // future: write a FAILED row to integration.snowflake_sync_log
        }
    }
}
```

- [ ] **Step 2: Write `TargetPriceSnowflakePushListener`**

Same shape against `TargetPriceSnowflakeWriter` + `TargetPriceRecalculatedEvent`.

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TargetPriceSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(TargetPriceSnowflakePushListener.class);

    private final TargetPriceSnowflakeWriter writer;
    private final Environment env;

    public TargetPriceSnowflakePushListener(TargetPriceSnowflakeWriter writer, Environment env) {
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTargetPriceRecalculated(TargetPriceRecalculatedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("snowflake.enabled", Boolean.class, false))) {
            log.info("[snowflake] disabled; skipping target-price push for event={}", event);
            return;
        }
        try {
            writer.pushTargetPrices(event.weekId(), event.closedRound() + 1);
            log.info("[snowflake] target-price pushed weekId={} targetRound={}",
                event.weekId(), event.closedRound() + 1);
        } catch (RuntimeException ex) {
            log.error("[snowflake] target-price push failed for event={}", event, ex);
        }
    }
}
```

- [ ] **Step 3: Write `BidRankingSnowflakePushListenerTest`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidRankingSnowflakePushListenerTest {

    @Mock BidRankingSnowflakeWriter writer;
    @Mock Environment env;

    @Test
    void short_circuits_when_snowflake_disabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(false);
        var listener = new BidRankingSnowflakePushListener(writer, env);

        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer, never()).pushBidRankings(anyLong(), anyInt());
    }

    @Test
    void pushes_when_enabled_passing_target_round() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        var listener = new BidRankingSnowflakePushListener(writer, env);

        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer).pushBidRankings(9001L, 2);
    }

    @Test
    void swallows_writer_exception() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("snowflake unreachable"))
            .when(writer).pushBidRankings(anyLong(), anyInt());
        var listener = new BidRankingSnowflakePushListener(writer, env);

        // Must not throw
        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
```

- [ ] **Step 4: Write `TargetPriceSnowflakePushListenerTest`**

Same three tests against `TargetPriceSnowflakePushListener` + `TargetPriceRecalculatedEvent`.

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetPriceSnowflakePushListenerTest {

    @Mock TargetPriceSnowflakeWriter writer;
    @Mock Environment env;

    @Test
    void short_circuits_when_snowflake_disabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(false);
        var listener = new TargetPriceSnowflakePushListener(writer, env);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer, never()).pushTargetPrices(anyLong(), anyInt());
    }

    @Test
    void pushes_target_round_when_enabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        var listener = new TargetPriceSnowflakePushListener(writer, env);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer).pushTargetPrices(9001L, 2);
    }

    @Test
    void swallows_writer_exception() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("network err"))
            .when(writer).pushTargetPrices(anyLong(), anyInt());
        var listener = new TargetPriceSnowflakePushListener(writer, env);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
```

- [ ] **Step 5: Run tests**

```bash
cd backend && mvn -Dtest='*SnowflakePushListenerTest' test -q
```

Expected: 6 tests pass.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListener.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListenerTest.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListenerTest.java
git commit -m "feat(4c): Snowflake push listeners + unit tests"
```

---

## Task 20: `RecalcEndToEndIT` — full happy path

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcEndToEndIT.java`

- [ ] **Step 1: Write the integration test**

```java
package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class RecalcEndToEndIT extends PostgresIntegrationTest {

    @Autowired ApplicationEventPublisher publisher;
    @Autowired JdbcTemplate jdbc;
    @Autowired EventCapture capture;

    @Test
    void round_close_runs_both_processes_and_publishes_both_events() {
        capture.clear();

        publisher.publishEvent(new RoundClosedEvent(9001L, 1, 9001L, 9001L));

        // Status columns
        assertThat(jdbc.queryForObject(
            "SELECT ranking_status FROM auctions.scheduling_auctions WHERE id = 9001",
            String.class)).isEqualTo("SUCCESS");
        assertThat(jdbc.queryForObject(
            "SELECT target_price_status FROM auctions.scheduling_auctions WHERE id = 9001",
            String.class)).isEqualTo("SUCCESS");

        // Spot-check ranks landed
        Integer rankRow = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 9001", Integer.class);
        assertThat(rankRow).isPositive();

        // Spot-check target prices landed
        java.math.BigDecimal target = jdbc.queryForObject(
            "SELECT round2_target_price FROM auctions.aggregated_inventory WHERE ecoid2='ECO-A' AND merged_grade='A'",
            java.math.BigDecimal.class);
        assertThat(target).isPositive();

        // Both events published
        assertThat(capture.bidRanking).hasSize(1);
        assertThat(capture.targetPrice).hasSize(1);
    }

    @Component
    static class EventCapture {
        final List<BidRankingUpdatedEvent> bidRanking = new ArrayList<>();
        final List<TargetPriceRecalculatedEvent> targetPrice = new ArrayList<>();

        @EventListener
        void onBR(BidRankingUpdatedEvent e) { bidRanking.add(e); }

        @EventListener
        void onTP(TargetPriceRecalculatedEvent e) { targetPrice.add(e); }

        void clear() {
            bidRanking.clear();
            targetPrice.clear();
        }
    }
}
```

> **Note:** This test relies on the listeners executing synchronously when published from inside the test transaction. `RecalcRoundClosedListener` is `AFTER_COMMIT`, so it would only fire after the test tx commits — which never happens with `@Transactional` on the test class. Two options:
>
> 1. Remove `@Transactional` and clean state between tests via a `@BeforeEach` truncate.
> 2. Use `TransactionTemplate` to wrap the publishEvent in a committed sub-tx.
>
> Pick (2) for portability:
>
> ```java
> @Autowired TransactionTemplate txTemplate;
>
> @Test
> void round_close_runs_both_processes_and_publishes_both_events() {
>     capture.clear();
>
>     txTemplate.execute(status -> {
>         publisher.publishEvent(new RoundClosedEvent(9001L, 1, 9001L, 9001L));
>         return null;
>     });
>     // Now AFTER_COMMIT listeners have fired in the same thread (sync executors).
>
>     // ... assertions
> }
> ```
>
> The `snowflakeExecutor` is async — to make the Snowflake push listeners observable in this test, either inject a synchronous executor in the test profile, or assert only on the local Postgres state + event capture (the events are published synchronously; only the Snowflake push happens async).
>
> This test focuses on the local-DB happy path and event capture. The Snowflake push is verified separately in the listener unit tests.

- [ ] **Step 2: Apply the TransactionTemplate fix**

Replace the body to wrap `publisher.publishEvent(...)` in a `txTemplate.execute(...)` block per the note. Add the field:

```java
@Autowired org.springframework.transaction.support.TransactionTemplate txTemplate;
```

And remove `@Transactional` from the class — use the test's own `txTemplate` for the inner commit and accept that fixture data persists between tests (clean via `@AfterEach` if needed; for this single-test class it's fine).

- [ ] **Step 3: Run the test**

```bash
cd backend && mvn -Dtest=RecalcEndToEndIT verify -q
```

Expected: 1 test passes.

- [ ] **Step 4: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcEndToEndIT.java
git commit -m "test(4c): RecalcEndToEndIT — RoundClosedEvent → both processes + events"
```

---

## Task 21: `application.yml` config

**Files:**
- Modify: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Add the recalc snowflake keys**

Locate the existing `eb.sync.*` and `po.sync.*` blocks. Add immediately after them:

```yaml
recalc:
  snowflake:
    bid-ranking-writer: logging      # logging | jdbc
    target-price-writer: logging     # logging | jdbc
```

If a `pg-test` profile exists in `application-pg-test.yml`, mirror the keys there with `logging` to keep tests deterministic.

- [ ] **Step 2: Smoke-test the app starts**

```bash
cd backend && mvn spring-boot:run
```

Expected: app starts; logs include
`Successfully applied 1 migration` (V82) on first start, otherwise `Already applied`.
Stop after a few seconds (Ctrl+C).

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/application.yml
git commit -m "feat(4c): application.yml — recalc.snowflake.* config keys"
```

---

## Task 22: JDBC Snowflake writer impls (deferrable but recommended)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcBidRankingSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcTargetPriceSnowflakeWriter.java`

These are gated by `recalc.snowflake.bid-ranking-writer=jdbc` and
`recalc.snowflake.target-price-writer=jdbc`, both default `logging`. They
exercise the `@ConditionalOnProperty(havingValue="jdbc")` branch.

> **Reference:** mirror `JdbcReserveBidSnowflakeWriter` (4A) and
> `JdbcPurchaseOrderSnowflakeWriter` (4B) — same structure: inject a
> `@Qualifier("snowflakeJdbcTemplate") JdbcTemplate`, build a MERGE
> statement, execute. Both consume the existing `snowflakeDataSource`
> bean from Theme 2 Phase 2.3.

- [ ] **Step 1: Write `JdbcBidRankingSnowflakeWriter`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "bid-ranking-writer", havingValue = "jdbc")
public class JdbcBidRankingSnowflakeWriter implements BidRankingSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final JdbcTemplate localJdbc;

    public JdbcBidRankingSnowflakeWriter(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc,
                                          JdbcTemplate localJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
        this.localJdbc = localJdbc;
    }

    @Override
    public void pushBidRankings(long weekId, int targetRound) {
        // Stage rows from local Postgres for the (week, targetRound) slice.
        String sourceSql = """
            SELECT bd.id AS bid_data_id,
                   bd.ecoid,
                   bd.merged_grade,
                   bd.code AS buyer_code,
                   bd.submitted_bid_amount,
                   CASE WHEN ? = 2 THEN bd.round2_bid_rank
                        WHEN ? = 3 THEN bd.round3_bid_rank END AS rank,
                   CASE WHEN ? = 2 THEN bd.display_round2_bid_rank
                        WHEN ? = 3 THEN bd.display_round3_bid_rank END AS display_rank
              FROM auctions.bid_data bd
             WHERE bd.week_id = ?
               AND bd.bid_round = ? - 1
            """;
        var rows = localJdbc.queryForList(
            sourceSql, targetRound, targetRound, targetRound, targetRound, weekId, targetRound);

        if (rows.isEmpty()) return;

        // Snowflake MERGE — upsert rank columns on AUCTIONS.BUYER_BID by
        // (week, round, bid_data_id). Adapt column names if your warehouse
        // schema differs.
        String mergeSql = """
            MERGE INTO AUCTIONS.BUYER_BID t
            USING (SELECT ? AS BID_DATA_ID, ? AS RANK, ? AS DISPLAY_RANK) s
              ON t.BID_DATA_ID = s.BID_DATA_ID
            WHEN MATCHED THEN UPDATE SET
                ROUND%d_RANK         = s.RANK,
                DISPLAY_ROUND%d_RANK = s.DISPLAY_RANK
            """.formatted(targetRound, targetRound);

        for (var row : rows) {
            snowflakeJdbc.update(mergeSql,
                row.get("bid_data_id"),
                row.get("rank"),
                row.get("display_rank"));
        }
    }
}
```

- [ ] **Step 2: Write `JdbcTargetPriceSnowflakeWriter`**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "target-price-writer", havingValue = "jdbc")
public class JdbcTargetPriceSnowflakeWriter implements TargetPriceSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final JdbcTemplate localJdbc;

    public JdbcTargetPriceSnowflakeWriter(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc,
                                          JdbcTemplate localJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
        this.localJdbc = localJdbc;
    }

    @Override
    public void pushTargetPrices(long weekId, int targetRound) {
        // Active-row swap: mark prior slice IS_ACTIVE = FALSE, then INSERT
        // new slice rows with incremented RUN_VERSION + IS_ACTIVE = TRUE.
        // RUN_VERSION = (existing max for this (week, round)) + 1.

        Integer nextRunVersion = snowflakeJdbc.queryForObject("""
            SELECT COALESCE(MAX(RUN_VERSION), 0) + 1
              FROM AUCTIONS.TARGET_PRICE_AUDIT
             WHERE WEEK_ID = ? AND ROUND = ?
            """, Integer.class, weekId, targetRound);

        snowflakeJdbc.update("""
            UPDATE AUCTIONS.TARGET_PRICE_AUDIT
               SET IS_ACTIVE = FALSE
             WHERE WEEK_ID = ? AND ROUND = ? AND IS_ACTIVE = TRUE
            """, weekId, targetRound);

        // Pull the slice from local Postgres. Column names are interpolated
        // because round-suffixed columns can't bind as SQL parameters.
        String sourceSql = """
            SELECT ai.ecoid2 AS ecoid,
                   ai.merged_grade,
                   ai.round%d_target_price       AS target_price,
                   ai.r%d_target_price_factor    AS factor_amount,
                   ai.r%d_target_price_factor_type AS factor_type,
                   ai.round%d_eb_for_target      AS eb_for_target
              FROM auctions.aggregated_inventory ai
             WHERE ai.week_id = ?
               AND ai.is_deprecated = FALSE
            """.formatted(targetRound, targetRound, targetRound, targetRound);
        var rows = localJdbc.queryForList(sourceSql, weekId);

        if (rows.isEmpty()) return;

        for (var row : rows) {
            snowflakeJdbc.update("""
                INSERT INTO AUCTIONS.TARGET_PRICE_AUDIT (
                    WEEK_ID, ROUND, ECOID, MERGED_GRADE,
                    TARGET_PRICE, FACTOR_AMOUNT, FACTOR_TYPE, EB_FOR_TARGET,
                    RUN_VERSION, IS_ACTIVE, RECORDED_AT
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP())
                """,
                weekId, targetRound,
                row.get("ecoid"), row.get("merged_grade"),
                row.get("target_price"), row.get("factor_amount"),
                row.get("factor_type"), row.get("eb_for_target"),
                nextRunVersion);
        }
    }
}
```

- [ ] **Step 3: Compile**

```bash
cd backend && mvn compile -q
```

Note: this task is gated by `recalc.snowflake.*-writer=jdbc` (default
`logging`), so the rest of 4C ships production-correct without these
classes loaded. Defer Task 22 to a follow-up PR if scheduling pressure
demands — the listener wiring + business logic in earlier tasks is the
primary value.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcBidRankingSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcTargetPriceSnowflakeWriter.java
git commit -m "feat(4c): JDBC Snowflake writer impls (gated by recalc.snowflake.*-writer=jdbc)"
```

---

## Task 23: Documentation

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/app-metadata/modules.md`
- Modify: `docs/architecture/data-model.md`
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/business-logic/index.md`
- Create: `docs/business-logic/bid-ranking-and-target-price.md`
- Modify: `docs/deployment/setup.md`
- Modify: `docs/testing/coverage.md`
- Modify: `docs/tasks/auction-sub4-umbrella-design.md`

- [ ] **Step 1: Add the two endpoints to `docs/api/rest-endpoints.md`**

Append to the "Auctions / Admin" section (or create one if missing):

````markdown
### POST /api/v1/admin/auctions/scheduling-auctions/{id}/re-rank
**Auth:** ROLE_ADMIN
**Description:** Re-runs bid ranking for the closed round (round ∈ {1, 2}).
Requires `ranking_status` ≠ `RUNNING` — otherwise 409.
**Response 200:** `RecalcResponse` `{ schedulingAuctionId, closedRound, status: "SUCCESS", error: null, startedAt, finishedAt, rowsAffected, durationMs }`
**Response 409:** Recalc already running.
**Response 404:** Unknown id.
**Response 422:** Round ∉ {1, 2}.

### POST /api/v1/admin/auctions/scheduling-auctions/{id}/recalculate-target-price
Same shape, drives the TARGET_PRICE process.
````

- [ ] **Step 2: Add 4C entry to `docs/app-metadata/modules.md`**

```markdown
## Bid Ranking + Target-Price Recalc (4C)
- Source modules: AuctionUI (`ACT_TriggerBidRankingCalculation`, `ACT_CalculateTargetPrice`)
- Primary tables: `auctions.scheduling_auctions` (status flags), `auctions.bid_ranking_config` (`include_reserve_floor`), `auctions.bid_data` (rank columns), `auctions.aggregated_inventory` (target-price columns)
- Trigger: `RoundClosedEvent` for round ∈ {1, 2}
- Admin recovery: `/admin/auctions/scheduling-auctions/{id}/re-rank` and `.../recalculate-target-price`
- Snowflake sync: per-process push of full `(week, R+1)` slice to `AUCTIONS.BUYER_BID` and `AUCTIONS.TARGET_PRICE_AUDIT`
```

- [ ] **Step 3: Update `docs/architecture/data-model.md`**

Add to the existing `auctions.scheduling_auctions` entry:

```markdown
4C adds 8 status columns: `ranking_status` / `target_price_status` (`PENDING`/`RUNNING`/`SUCCESS`/`FAILED`), each with `_error` (TEXT), `_started_at`, `_finished_at` (TIMESTAMPTZ).
```

Add to `auctions.bid_ranking_config`:

```markdown
4C adds `include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE` — toggles whether `auctions.reserve_bid` rows participate in DENSE_RANK.
```

- [ ] **Step 4: Add ADR to `docs/architecture/decisions.md`**

Append at the top (newest first):

```markdown
## 2026-04-30 — Sub-project 4C: Bid Ranking + Target-Price Recalc

**Status:** Accepted.

### Context
Mendix `ACT_TriggerBidRankingCalculation` + `ACT_CalculateTargetPrice` are the two heaviest queries on round close. Sub-project 3 stubbed them via `BidRankingStubListener`. 4A (EB) and 4B (PO) shipped the data inputs; 4C ports the queries themselves.

### Decisions
1. Two independent processes (RANKING + TARGET_PRICE), each in its own `REQUIRES_NEW` tx.
2. Reserve-floor inclusion in ranking is configurable on `bid_ranking_config.include_reserve_floor`.
3. Synchronous on the cron-tick listener thread; ShedLock on the cron prevents parallel ticks.
4. Two admin recovery endpoints (re-rank, recalculate-target-price), each rejecting 409 when status=`RUNNING`.
5. Per-process Snowflake events; full week refresh per push; bounded payload (~few-thousand rows).
6. Status writes that survive rollback live in a `REQUIRES_NEW` sub-tx (`RecalcStatusUpdater`).

### Consequences
- One-process failure does not stop the other; ops can fix and re-fire each independently.
- Cron ticks see deterministic ordering — RANKING then TARGET_PRICE per closed round.
- Schema migration V82 is purely additive.

### References
- `docs/tasks/auction-bid-ranking-design.md` — full spec
- `docs/tasks/auction-bid-ranking-plan.md` — implementation plan
- `docs/tasks/auction-sub4-umbrella-design.md` — parent decomposition
```

- [ ] **Step 5: Create `docs/business-logic/bid-ranking-and-target-price.md`**

```markdown
# Bid Ranking + Target-Price Recalc

When a round of an auction closes (round 1 or 2), two follow-on processes run:

## RANKING
For every (ecoid, merged_grade) in scope, computes a DENSE_RANK on
`submitted_bid_amount DESC` over the just-closed round's bids. The result
populates `bid_data.round{N+1}_bid_rank` and a clamped
`display_round{N+1}_bid_rank` (NULL when calculated rank falls outside
`[bid_ranking_config.display_rank, .maximum_rank]`).

When `bid_ranking_config.include_reserve_floor = TRUE`, `auctions.reserve_bid`
rows participate in the rank as priority bidders — i.e. the EB floor for
(ecoid, grade) sits at the top of the rank if higher than every auction
bidder.

## TARGET_PRICE
For every (ecoid, merged_grade) in scope, computes:

```
MaxBid           = MAX(submitted_bid_amount) over closed round
MaxBidPlusFactor = MaxBid + matching target_price_factor band amount
EvaluatedBid     = GREATEST(MaxBidPlusFactor, COALESCE(EB, 0), COALESCE(PO_MAX, 0))
```

Result lands on `aggregated_inventory.round{N+1}_target_price` plus the
factor + EB columns. Round R's max-bid + max-bid-buyer-code are recorded.

## Recovery
Each process has its own status flags on `scheduling_auctions`. If RANKING
fails but TARGET_PRICE succeeds, ops re-run only the failed process via
`POST /api/v1/admin/auctions/scheduling-auctions/{id}/re-rank`. The
endpoint rejects 409 if a recalc is already running.
```

Then add a link in `docs/business-logic/index.md`:

```markdown
- [Bid ranking + target-price recalc](bid-ranking-and-target-price.md)
```

- [ ] **Step 6: Update `docs/deployment/setup.md`**

Add a new section:

```markdown
## Recalc (4C) sync config
- `recalc.snowflake.bid-ranking-writer` — `logging` (default) or `jdbc`
- `recalc.snowflake.target-price-writer` — `logging` (default) or `jdbc`
```

- [ ] **Step 7: Update `docs/testing/coverage.md`**

Append:

```markdown
## auctions.recalc (new 2026-04-30)
Target 85%+. RANKING + TARGET_PRICE are the load-bearing branches; see
`BidRankingRepositoryIT` + `TargetPriceRecalcRepositoryIT` +
`BidRankingServiceTest` + `TargetPriceRecalcServiceTest` +
`RecalcOrchestratorTest` + `RecalcRoundClosedListenerTest` +
`RecalcAdminControllerIT` + `RecalcEndToEndIT` +
`BidRankingSnowflakePushListenerTest` + `TargetPriceSnowflakePushListenerTest`.
```

- [ ] **Step 8: Mark 4C drafted in umbrella**

In `docs/tasks/auction-sub4-umbrella-design.md` §7, update:

```markdown
| 4C | `docs/tasks/auction-bid-ranking-design.md` *(drafted 2026-04-30)* |
```

and similarly for the plan path.

- [ ] **Step 9: Commit**

```bash
git add docs/api/rest-endpoints.md docs/app-metadata/modules.md \
        docs/architecture/data-model.md docs/architecture/decisions.md \
        docs/business-logic/index.md docs/business-logic/bid-ranking-and-target-price.md \
        docs/deployment/setup.md docs/testing/coverage.md \
        docs/tasks/auction-sub4-umbrella-design.md
git commit -m "docs(4c): REST endpoints + ADR + data-model + business-logic + coverage"
```

---

## Final verification

- [ ] **Step 1: Full backend test suite green**

```bash
cd backend && mvn -DfailIfNoTests=false verify -q
```

Expected: BUILD SUCCESS. All new tests included.

- [ ] **Step 2: Coverage report**

```bash
cd backend && mvn jacoco:report -q
```

Open `backend/target/site/jacoco/index.html`. Confirm
`auctions.recalc` package coverage ≥ 85%.

- [ ] **Step 3: Lint Flyway**

```bash
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -c "SELECT version, success FROM flyway_schema_history ORDER BY version::text DESC LIMIT 5;"
```

Expected: V82 listed with `success = t`.

- [ ] **Step 4: Smoke-test the app**

```bash
cd backend && mvn spring-boot:run
```

Then in another terminal:

```bash
curl -i -u admin@test.com:Admin123! \
  -X POST http://localhost:8080/api/v1/admin/auctions/scheduling-auctions/9001/re-rank
```

(Adjust the auth method to match your dev environment — cookie or header.)

Expected: HTTP 200 with a `RecalcResponse` body. (Or 404 if no scheduling auction with id 9001 exists in your dev DB — that's fine; the endpoint wiring is the actual signal.)

Stop the app (Ctrl+C).

- [ ] **Step 5: Review the docs render**

```bash
ls docs/business-logic/bid-ranking-and-target-price.md docs/architecture/decisions.md
```

Open both in a markdown previewer; confirm the ADR + business-logic
prose read clearly.

---

## Spec coverage cross-check

Run this list against the design doc:

| Spec section | Implemented in |
|---|---|
| §3.1 reserve-floor configurable | Task 2 (column) + Task 5 (CTE branch) |
| §3.2 synchronous execution | Task 15 (listener), Task 14 (orchestrator) |
| §3.3 two admin endpoints | Task 16 (controller) |
| §3.4 per-process Snowflake events | Task 3 (events), Task 19 (listeners) |
| §3.5 full week refresh | Task 18 (writer interface), Task 22 (JDBC impl) |
| §3.6 reject 409 RUNNING | Task 9 (state-flip) |
| §3.7 single CTE UPDATE | Tasks 5, 7 (repositories) |
| §3.8 status sub-tx | Task 9 (`RecalcStatusUpdater`) |
| §3.9 no auto-retry | All services — no `@Retryable` annotations |
| §5 V82 schema | Task 1 |
| §6 API surface | Tasks 16, 17 |
| §7 Snowflake integration | Tasks 18, 19, 22 |
| §8.1 RANKING SQL | Task 5 |
| §8.2 TARGET_PRICE SQL | Task 7 |
| §8.3 state-flip UPDATE | Task 9 |
| §9 test matrix | Tasks 6, 8, 11, 13, 14, 15, 17, 19, 20 |
| §10 docs updates | Task 23 |

If any row is missing a task, add the task before declaring this plan
complete.
