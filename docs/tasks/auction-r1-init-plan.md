# Auction R1 Initialization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace `R1InitStubListener` with real Mendix-parity Round 1 initialization — clamp aggregated-inventory target prices to the `minimum_allowed_bid` floor and rewrite the Qualified Buyer Codes (QBC) set for the auction when a Round 1 `SchedulingAuction` transitions `Scheduled → Started`.

**Architecture:** Post-commit `@TransactionalEventListener` on `RoundStartedEvent` (gated by `auctions.r1-init.enabled`) invokes a single `@Transactional(REQUIRES_NEW, timeout=30)` `Round1InitializationService.initialize(saId)` that (a) clamps non-DW + DW target prices, (b) deletes stale QBCs, (c) inserts one QBC per active wholesale/data-wipe buyer code. Admin recovery endpoint (`POST /api/v1/admin/auctions/{id}/rounds/1/init`, Administrator-only, synchronous) calls the same service. A new Flyway migration (V72) flattens the QBC schema by adding `scheduling_auction_id` and `buyer_code_id` directly to `buyer_mgmt.qualified_buyer_codes` and dropping the `qbc_scheduling_auctions` / `qbc_buyer_codes` junctions, enabling one-hop delete and a clean unique constraint.

**Tech Stack:** Java 21, Spring Boot 3.2.4, Spring Data JPA (Hibernate), Flyway, PostgreSQL 15+, Lombok, JUnit 5 + Mockito + AssertJ, Testcontainers (Postgres), Awaitility, MockMvc.

**Related docs:**
- Spec: `docs/tasks/auction-r1-init-design.md`
- Prior sub-projects: `docs/tasks/auction-lifecycle-cron-design.md` (sub-project 0), `docs/tasks/auction-status-snowflake-push-plan.md` (sub-project 1)
- ADRs to write after merge: one new ADR in `docs/architecture/decisions.md` (dated 2026-04-22 — "Auction R1 init: listener + admin endpoint + QBC flattening")

---

## File Structure

### Created

```
backend/src/main/resources/db/migration/
  V72__buyer_mgmt_qbc_flatten.sql

backend/src/main/java/com/ecoatm/salesplatform/
  model/buyermgmt/QualificationType.java
  model/buyermgmt/QualifiedBuyerCode.java
  repository/QualifiedBuyerCodeRepository.java
  service/auctions/r1init/Round1InitializationService.java
  service/auctions/r1init/Round1InitializationResult.java
  service/auctions/r1init/R1InitListener.java
  service/auctions/r1init/SchedulingAuctionNotFoundException.java
  service/buyermgmt/AuctionsFeatureConfigService.java

backend/src/test/java/com/ecoatm/salesplatform/
  service/auctions/r1init/Round1InitializationServiceTest.java
  service/auctions/r1init/R1InitListenerTest.java
  service/buyermgmt/AuctionsFeatureConfigServiceTest.java
  repository/BuyerCodeRepositoryIT.java
  repository/QualifiedBuyerCodeRepositoryIT.java
  integration/R1InitializationIT.java
```

### Modified

```
backend/src/main/java/com/ecoatm/salesplatform/
  model/buyermgmt/AuctionsFeatureConfig.java            # + minimumAllowedBid field + setters
  repository/BuyerCodeRepository.java                   # + findActiveWholesaleOrDataWipe()
  repository/auctions/AggregatedInventoryRepository.java# + clampNonDwTargetPrice, clampDwTargetPrice
  repository/auctions/SchedulingAuctionRepository.java  # + findByAuctionIdAndRound
  controller/AuctionController.java                     # + POST /{auctionId}/rounds/1/init
  security/SecurityConfig.java                          # + matcher for R1 init endpoint (Admin only)
  src/main/resources/application.yml                    # + auctions.r1-init.enabled=true
  src/main/resources/application-test.yml               # + auctions.r1-init.enabled=false

backend/src/test/java/com/ecoatm/salesplatform/
  controller/AuctionControllerTest.java                 # + r1InitEndpoint* cases
```

### Deleted

```
backend/src/main/java/com/ecoatm/salesplatform/
  service/auctions/lifecycle/stub/R1InitStubListener.java
```

---

## Phase 1 — Schema + entity scaffolding

### Task 1: V72 migration — flatten QBC schema

**Files:**
- Create: `backend/src/main/resources/db/migration/V72__buyer_mgmt_qbc_flatten.sql`

- [ ] **Step 1: Write the migration SQL**

```sql
-- =============================================================================
-- V72: Flatten QBC junction tables into direct FK columns on
--      buyer_mgmt.qualified_buyer_codes.
--
-- Rationale: The Mendix source modeled SchedulingAuction ↔ QBC and
-- BuyerCode ↔ QBC as M:N associations. In reality each QBC row belongs to
-- exactly one SchedulingAuction and one BuyerCode; the junctions are
-- redundant. Flattening enables one-hop delete on SA rewrite
-- (SUB_ClearQualifiedBuyerList parity) and clean uniqueness enforcement.
--
-- Not dropped: buyer_mgmt.qbc_bid_rounds — QBC participates in state
-- tracking across multiple bid rounds, which is a legitimate M:N.
-- =============================================================================

ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ADD COLUMN scheduling_auction_id BIGINT,
    ADD COLUMN buyer_code_id         BIGINT;

-- Backfill direct FKs from V9 junction tables (V23 seed populated them).
UPDATE buyer_mgmt.qualified_buyer_codes qbc
   SET scheduling_auction_id = qsa.scheduling_auction_id
  FROM buyer_mgmt.qbc_scheduling_auctions qsa
 WHERE qsa.qualified_buyer_code_id = qbc.id;

UPDATE buyer_mgmt.qualified_buyer_codes qbc
   SET buyer_code_id = qbcbc.buyer_code_id
  FROM buyer_mgmt.qbc_buyer_codes qbcbc
 WHERE qbcbc.qualified_buyer_code_id = qbc.id;

-- Any QBC row without a SchedulingAuction or BuyerCode was orphaned in
-- the Mendix source. Drop those before enforcing NOT NULL.
DELETE FROM buyer_mgmt.qualified_buyer_codes
 WHERE scheduling_auction_id IS NULL
    OR buyer_code_id IS NULL;

ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ALTER COLUMN scheduling_auction_id SET NOT NULL,
    ALTER COLUMN buyer_code_id         SET NOT NULL,
    ADD CONSTRAINT fk_qbc_scheduling_auction
        FOREIGN KEY (scheduling_auction_id)
        REFERENCES auctions.scheduling_auctions(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_qbc_buyer_code
        FOREIGN KEY (buyer_code_id)
        REFERENCES buyer_mgmt.buyer_codes(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_qbc_sa_bc UNIQUE (scheduling_auction_id, buyer_code_id);

CREATE INDEX idx_qbc_sa ON buyer_mgmt.qualified_buyer_codes(scheduling_auction_id);
CREATE INDEX idx_qbc_bc ON buyer_mgmt.qualified_buyer_codes(buyer_code_id);

-- Drop redundant junctions (data preserved on QBC row directly).
DROP TABLE buyer_mgmt.qbc_scheduling_auctions;
DROP TABLE buyer_mgmt.qbc_buyer_codes;

-- Attach id sequence (V9 declared BIGINT PRIMARY KEY without IDENTITY).
CREATE SEQUENCE IF NOT EXISTS buyer_mgmt.qualified_buyer_codes_id_seq;
SELECT setval(
  'buyer_mgmt.qualified_buyer_codes_id_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM buyer_mgmt.qualified_buyer_codes), 0), 1)
);
ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ALTER COLUMN id SET DEFAULT nextval('buyer_mgmt.qualified_buyer_codes_id_seq');
ALTER SEQUENCE buyer_mgmt.qualified_buyer_codes_id_seq
    OWNED BY buyer_mgmt.qualified_buyer_codes.id;

COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.scheduling_auction_id IS
  'Flattened from V9 qbc_scheduling_auctions junction (V72). One QBC belongs to exactly one SchedulingAuction.';
COMMENT ON COLUMN buyer_mgmt.qualified_buyer_codes.buyer_code_id IS
  'Flattened from V9 qbc_buyer_codes junction (V72). One QBC represents exactly one BuyerCode.';
```

- [ ] **Step 2: Run Flyway + verify**

Run: `mvn -q -DskipTests spring-boot:run` in another terminal, wait for `Successfully applied 1 migration to schema "public", now at version v72`. Then `Ctrl+C`.

Verify in psql:

```bash
psql -U salesplatform -d salesplatform_dev -c "\d buyer_mgmt.qualified_buyer_codes"
```

Expected: columns `scheduling_auction_id`, `buyer_code_id` present, NOT NULL, with FK constraints and `uq_qbc_sa_bc` unique index. `qbc_scheduling_auctions` and `qbc_buyer_codes` no longer exist.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V72__buyer_mgmt_qbc_flatten.sql
git commit -m "feat: flatten QBC junctions to direct FKs on qualified_buyer_codes"
```

---

### Task 2: `QualificationType` enum

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/QualificationType.java`

- [ ] **Step 1: Write the enum**

```java
package com.ecoatm.salesplatform.model.buyermgmt;

public enum QualificationType {
    Qualified,
    Not_Qualified,
    Manual
}
```

Note: Identifier spelling (`Not_Qualified` with underscore) matches the Mendix enum and the V9 CHECK constraint. Keeps `@Enumerated(STRING)` lossless.

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/QualificationType.java
git commit -m "feat: add QualificationType enum for QBC entity"
```

---

### Task 3: `QualifiedBuyerCode` entity

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/QualifiedBuyerCode.java`

- [ ] **Step 1: Write the entity**

```java
package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qualified_buyer_codes", schema = "buyer_mgmt")
@Getter
@Setter
public class QualifiedBuyerCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "scheduling_auction_id", nullable = false)
    private Long schedulingAuctionId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "qualification_type", nullable = false, length = 13)
    private QualificationType qualificationType = QualificationType.Not_Qualified;

    @Column(name = "included", nullable = false)
    private boolean included;

    @Column(name = "submitted", nullable = false)
    private boolean submitted;

    @Column(name = "submitted_datetime")
    private LocalDateTime submittedDatetime;

    @Column(name = "opened_dashboard", nullable = false)
    private boolean openedDashboard;

    @Column(name = "opened_dashboard_datetime")
    private LocalDateTime openedDashboardDatetime;

    @Column(name = "is_special_treatment", nullable = false)
    private boolean specialTreatment;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "changed_date", nullable = false)
    private LocalDateTime changedDate = LocalDateTime.now();

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;
}
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/QualifiedBuyerCode.java
git commit -m "feat: add QualifiedBuyerCode entity mapping flattened QBC table"
```

---

### Task 4: `QualifiedBuyerCodeRepository`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryIT.java`

- [ ] **Step 1: Write the failing IT**

```java
package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class QualifiedBuyerCodeRepositoryIT {

    @Autowired
    QualifiedBuyerCodeRepository repo;

    @Test
    void deleteBySchedulingAuctionId_removesOnlyTargetSaRows() {
        QualifiedBuyerCode a = save(300L, 1L);
        QualifiedBuyerCode b = save(300L, 2L);
        QualifiedBuyerCode c = save(301L, 1L);

        int deleted = repo.deleteBySchedulingAuctionId(300L);

        assertThat(deleted).isEqualTo(2);
        assertThat(repo.findById(a.getId())).isEmpty();
        assertThat(repo.findById(b.getId())).isEmpty();
        assertThat(repo.findById(c.getId())).isPresent();
    }

    private QualifiedBuyerCode save(long saId, long bcId) {
        QualifiedBuyerCode qbc = new QualifiedBuyerCode();
        qbc.setSchedulingAuctionId(saId);
        qbc.setBuyerCodeId(bcId);
        qbc.setQualificationType(QualificationType.Qualified);
        qbc.setIncluded(true);
        return repo.save(qbc);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=QualifiedBuyerCodeRepositoryIT test`
Expected: FAIL — `QualifiedBuyerCodeRepository` does not exist.

- [ ] **Step 3: Write the repository**

```java
package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QualifiedBuyerCodeRepository extends JpaRepository<QualifiedBuyerCode, Long> {

    @Modifying
    @Query(value = """
            DELETE FROM buyer_mgmt.qualified_buyer_codes
             WHERE scheduling_auction_id = :saId
            """, nativeQuery = true)
    int deleteBySchedulingAuctionId(@Param("saId") Long saId);
}
```

**Note on IT bootstrap:** This test requires a real Postgres instance (V72 applied). If the repo already has a `BaseRepositoryIT` Testcontainers parent, extend it instead of `@DataJpaTest`. If no such parent exists yet, the test assumes `application-test.yml` points at a running `salesplatform_dev` DB on `localhost:5432` with migrations applied. Verify with the existing repository IT suites (if any fail the same way, add a Testcontainers parent as a follow-up).

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=QualifiedBuyerCodeRepositoryIT test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/QualifiedBuyerCodeRepositoryIT.java
git commit -m "feat: add QualifiedBuyerCodeRepository with deleteBySchedulingAuctionId"
```

---

### Task 5: `BuyerCodeRepository.findActiveWholesaleOrDataWipe`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/BuyerCodeRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/BuyerCodeRepositoryIT.java`

- [ ] **Step 1: Write the failing IT**

```java
package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BuyerCodeRepositoryIT {

    @Autowired
    BuyerCodeRepository buyerCodeRepo;

    @Test
    void findActiveWholesaleOrDataWipe_returnsOnlyActiveWholesaleAndDataWipeBuyerCodes() {
        List<BuyerCode> codes = buyerCodeRepo.findActiveWholesaleOrDataWipe();

        assertThat(codes).allSatisfy(bc -> {
            assertThat(bc.getBuyerCodeType()).isIn("Wholesale", "Data_Wipe");
            assertThat(bc.isSoftDelete()).isFalse();
        });
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=BuyerCodeRepositoryIT test`
Expected: FAIL — method `findActiveWholesaleOrDataWipe` not found.

- [ ] **Step 3: Add the query**

In `BuyerCodeRepository.java`, add:

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;

// ... inside the interface:

@Query(value = """
        SELECT DISTINCT bc.* FROM buyer_mgmt.buyer_codes bc
        JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
        JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
        WHERE bc.buyer_code_type IN ('Data_Wipe','Wholesale')
          AND bc.soft_delete = false
          AND b.status = 'Active'
        """, nativeQuery = true)
List<BuyerCode> findActiveWholesaleOrDataWipe();
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=BuyerCodeRepositoryIT test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/BuyerCodeRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/BuyerCodeRepositoryIT.java
git commit -m "feat: add findActiveWholesaleOrDataWipe to BuyerCodeRepository"
```

---

### Task 6: `AggregatedInventoryRepository` target-price clamp methods

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepository.java`

- [ ] **Step 1: Add the two clamp updates + supporting imports**

Inside `AggregatedInventoryRepository.java`, add above the existing `existsAuctionForWeek` method:

```java
import org.springframework.data.jpa.repository.Modifying;
import java.math.BigDecimal;

// ... inside the interface:

@Modifying
@Query(value = """
        UPDATE auctions.aggregated_inventory
           SET avg_target_price = :min
         WHERE week_id = :weekId
           AND avg_target_price < :min
           AND total_quantity > 0
        """, nativeQuery = true)
int clampNonDwTargetPrice(@Param("weekId") Long weekId,
                          @Param("min") BigDecimal min);

@Modifying
@Query(value = """
        UPDATE auctions.aggregated_inventory
           SET dw_avg_target_price = :min
         WHERE week_id = :weekId
           AND dw_avg_target_price < :min
           AND dw_total_quantity > 0
        """, nativeQuery = true)
int clampDwTargetPrice(@Param("weekId") Long weekId,
                       @Param("min") BigDecimal min);
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepository.java
git commit -m "feat: add target-price clamp queries for R1 init floor enforcement"
```

> Behavioral coverage for these queries lives in `Round1InitializationServiceTest` (Task 12) and `R1InitializationIT` (Task 15) which exercise them end-to-end. A separate repository-only IT would duplicate effort.

---

### Task 7: `SchedulingAuctionRepository.findByAuctionIdAndRound`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java`

- [ ] **Step 1: Add derived query method**

Inside `SchedulingAuctionRepository.java`, add (check first — if already present skip this task):

```java
import java.util.Optional;

// ... inside the interface:

Optional<SchedulingAuction> findByAuctionIdAndRound(Long auctionId, int round);
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java
git commit -m "feat: add findByAuctionIdAndRound derived query for admin R1 init endpoint"
```

---

### Task 8: `AuctionsFeatureConfig.minimumAllowedBid`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfig.java`

- [ ] **Step 1: Add the field + getter/setter**

Inside `AuctionsFeatureConfig.java`, add after the `sendAuctionDataToSnowflake` field:

```java
import java.math.BigDecimal;

// ... in the class body:

@Column(name = "minimum_allowed_bid", nullable = false)
private BigDecimal minimumAllowedBid;

public BigDecimal getMinimumAllowedBid() {
    return minimumAllowedBid;
}

public void setMinimumAllowedBid(BigDecimal minimumAllowedBid) {
    this.minimumAllowedBid = minimumAllowedBid;
}
```

Also add setters for `auctionRound2MinutesOffset`, `auctionRound3MinutesOffset`, `sendAuctionDataToSnowflake`, and `id` (needed by `AuctionsFeatureConfigService.getOrCreate` in Task 11 for fresh-row creation):

```java
public void setId(Long id) {
    this.id = id;
}

public void setAuctionRound2MinutesOffset(int v) {
    this.auctionRound2MinutesOffset = v;
}

public void setAuctionRound3MinutesOffset(int v) {
    this.auctionRound3MinutesOffset = v;
}

public void setSendAuctionDataToSnowflake(boolean v) {
    this.sendAuctionDataToSnowflake = v;
}
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/buyermgmt/AuctionsFeatureConfig.java
git commit -m "feat: map minimum_allowed_bid on AuctionsFeatureConfig for R1 clamp"
```

---

## Phase 2 — Service layer

### Task 9: `SchedulingAuctionNotFoundException`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/SchedulingAuctionNotFoundException.java`

- [ ] **Step 1: Write the exception**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

public class SchedulingAuctionNotFoundException extends RuntimeException {
    private final Long schedulingAuctionId;

    public SchedulingAuctionNotFoundException(Long schedulingAuctionId) {
        super("SchedulingAuction not found: id=" + schedulingAuctionId);
        this.schedulingAuctionId = schedulingAuctionId;
    }

    public Long getSchedulingAuctionId() {
        return schedulingAuctionId;
    }
}
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/SchedulingAuctionNotFoundException.java
git commit -m "feat: add SchedulingAuctionNotFoundException for R1 init path"
```

---

### Task 10: `Round1InitializationResult` record

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationResult.java`

- [ ] **Step 1: Write the record**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

public record Round1InitializationResult(
        long schedulingAuctionId,
        long auctionId,
        long weekId,
        int clampedNonDw,
        int clampedDw,
        int qbcsCreated,
        long durationMs
) {}
```

- [ ] **Step 2: Compile check**

Run: `mvn -q compile`
Expected: build success.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationResult.java
git commit -m "feat: add Round1InitializationResult record"
```

---

### Task 11: `AuctionsFeatureConfigService.getOrCreate`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/buyermgmt/AuctionsFeatureConfigService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/buyermgmt/AuctionsFeatureConfigServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionsFeatureConfigServiceTest {

    @Mock
    AuctionsFeatureConfigRepository repo;

    AuctionsFeatureConfigService service;

    @BeforeEach
    void setUp() {
        service = new AuctionsFeatureConfigService(repo);
    }

    @Test
    void getOrCreate_returnsExistingSingleton() {
        AuctionsFeatureConfig existing = new AuctionsFeatureConfig();
        existing.setId(1L);
        existing.setMinimumAllowedBid(new BigDecimal("2.50"));
        when(repo.findSingleton()).thenReturn(Optional.of(existing));

        AuctionsFeatureConfig result = service.getOrCreate();

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    void getOrCreate_insertsDefaultsWhenMissing() {
        when(repo.findSingleton()).thenReturn(Optional.empty());
        when(repo.save(any(AuctionsFeatureConfig.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AuctionsFeatureConfig result = service.getOrCreate();

        ArgumentCaptor<AuctionsFeatureConfig> captor =
                ArgumentCaptor.forClass(AuctionsFeatureConfig.class);
        verify(repo).save(captor.capture());
        AuctionsFeatureConfig saved = captor.getValue();

        assertThat(saved.getMinimumAllowedBid()).isEqualByComparingTo("2.00");
        assertThat(saved.getAuctionRound2MinutesOffset()).isEqualTo(360);
        assertThat(saved.getAuctionRound3MinutesOffset()).isEqualTo(180);
        assertThat(saved.isSendAuctionDataToSnowflake()).isFalse();
        assertThat(result).isSameAs(saved);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=AuctionsFeatureConfigServiceTest test`
Expected: FAIL — `AuctionsFeatureConfigService` not found.

- [ ] **Step 3: Write the service**

```java
package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuctionsFeatureConfigService {

    private static final BigDecimal DEFAULT_MINIMUM_ALLOWED_BID = new BigDecimal("2.00");
    private static final int DEFAULT_ROUND2_OFFSET_MINUTES = 360;
    private static final int DEFAULT_ROUND3_OFFSET_MINUTES = 180;

    private final AuctionsFeatureConfigRepository repo;

    public AuctionsFeatureConfigService(AuctionsFeatureConfigRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AuctionsFeatureConfig getOrCreate() {
        return repo.findSingleton().orElseGet(this::insertDefaults);
    }

    private AuctionsFeatureConfig insertDefaults() {
        AuctionsFeatureConfig config = new AuctionsFeatureConfig();
        config.setMinimumAllowedBid(DEFAULT_MINIMUM_ALLOWED_BID);
        config.setAuctionRound2MinutesOffset(DEFAULT_ROUND2_OFFSET_MINUTES);
        config.setAuctionRound3MinutesOffset(DEFAULT_ROUND3_OFFSET_MINUTES);
        config.setSendAuctionDataToSnowflake(false);
        return repo.save(config);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=AuctionsFeatureConfigServiceTest test`
Expected: PASS — both scenarios.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/buyermgmt/AuctionsFeatureConfigService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/buyermgmt/AuctionsFeatureConfigServiceTest.java
git commit -m "feat: add AuctionsFeatureConfigService.getOrCreate with defaults"
```

---

### Task 12: `Round1InitializationService`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.buyermgmt.AuctionsFeatureConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Round1InitializationServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock AuctionRepository auctionRepo;
    @Mock AggregatedInventoryRepository aggInvRepo;
    @Mock QualifiedBuyerCodeRepository qbcRepo;
    @Mock BuyerCodeRepository buyerCodeRepo;
    @Mock AuctionsFeatureConfigService configService;

    Round1InitializationService service;

    private static final long SA_ID = 301L;
    private static final long AUCTION_ID = 101L;
    private static final long WEEK_ID = 42L;
    private static final BigDecimal MIN_BID = new BigDecimal("2.00");

    @BeforeEach
    void setUp() {
        service = new Round1InitializationService(
                saRepo, auctionRepo, aggInvRepo, qbcRepo, buyerCodeRepo, configService);
    }

    @Test
    void initialize_happyPath_clampsBothAndCreatesQbcs() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(5);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(3);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(0);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe())
                .thenReturn(List.of(buyerCode(10L), buyerCode(11L), buyerCode(12L)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.clampedNonDw()).isEqualTo(5);
        assertThat(result.clampedDw()).isEqualTo(3);
        assertThat(result.qbcsCreated()).isEqualTo(3);
        assertThat(result.auctionId()).isEqualTo(AUCTION_ID);
        assertThat(result.weekId()).isEqualTo(WEEK_ID);

        ArgumentCaptor<QualifiedBuyerCode> qbcCaptor = ArgumentCaptor.forClass(QualifiedBuyerCode.class);
        verify(qbcRepo, org.mockito.Mockito.times(3)).save(qbcCaptor.capture());
        assertThat(qbcCaptor.getAllValues()).allSatisfy(q -> {
            assertThat(q.getSchedulingAuctionId()).isEqualTo(SA_ID);
            assertThat(q.getQualificationType()).isEqualTo(QualificationType.Qualified);
            assertThat(q.isIncluded()).isTrue();
            assertThat(q.isSpecialTreatment()).isFalse();
        });
        assertThat(qbcCaptor.getAllValues())
                .extracting(QualifiedBuyerCode::getBuyerCodeId)
                .containsExactlyInAnyOrder(10L, 11L, 12L);
    }

    @Test
    void initialize_emptyBuyerList_createsZeroQbcsAndDoesNotThrow() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(7);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe()).thenReturn(List.of());

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.qbcsCreated()).isZero();
        verify(qbcRepo, never()).save(any());
    }

    @Test
    void initialize_missingSchedulingAuction_throws() {
        when(saRepo.findById(SA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.initialize(SA_ID))
                .isInstanceOf(SchedulingAuctionNotFoundException.class)
                .hasMessageContaining(String.valueOf(SA_ID));

        verify(aggInvRepo, never()).clampNonDwTargetPrice(any(), any());
    }

    @Test
    void initialize_nothingBelowFloor_stillRewritesQbcs() {
        stubSaAndAuction();
        stubConfig();
        when(aggInvRepo.clampNonDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(aggInvRepo.clampDwTargetPrice(WEEK_ID, MIN_BID)).thenReturn(0);
        when(qbcRepo.deleteBySchedulingAuctionId(SA_ID)).thenReturn(0);
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe())
                .thenReturn(List.of(buyerCode(10L)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));

        Round1InitializationResult result = service.initialize(SA_ID);

        assertThat(result.clampedNonDw()).isZero();
        assertThat(result.clampedDw()).isZero();
        assertThat(result.qbcsCreated()).isEqualTo(1);
        verify(qbcRepo).deleteBySchedulingAuctionId(SA_ID);
    }

    @Test
    void initialize_callsServicesInExpectedOrder() {
        stubSaAndAuction();
        stubConfig();
        when(buyerCodeRepo.findActiveWholesaleOrDataWipe()).thenReturn(List.of());

        service.initialize(SA_ID);

        org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(
                aggInvRepo, qbcRepo, buyerCodeRepo);
        inOrder.verify(aggInvRepo).clampNonDwTargetPrice(WEEK_ID, MIN_BID);
        inOrder.verify(aggInvRepo).clampDwTargetPrice(WEEK_ID, MIN_BID);
        inOrder.verify(qbcRepo).deleteBySchedulingAuctionId(SA_ID);
        inOrder.verify(buyerCodeRepo).findActiveWholesaleOrDataWipe();
    }

    private void stubSaAndAuction() {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(SA_ID);
        sa.setAuctionId(AUCTION_ID);
        sa.setRound(1);
        when(saRepo.findById(SA_ID)).thenReturn(Optional.of(sa));

        Auction auction = new Auction();
        auction.setId(AUCTION_ID);
        auction.setWeekId(WEEK_ID);
        when(auctionRepo.findById(AUCTION_ID)).thenReturn(Optional.of(auction));
    }

    private void stubConfig() {
        AuctionsFeatureConfig config = new AuctionsFeatureConfig();
        config.setMinimumAllowedBid(MIN_BID);
        when(configService.getOrCreate()).thenReturn(config);
    }

    private BuyerCode buyerCode(long id) {
        BuyerCode bc = new BuyerCode();
        bc.setId(id);
        return bc;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=Round1InitializationServiceTest test`
Expected: FAIL — `Round1InitializationService` not found.

- [ ] **Step 3: Write the service**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.buyermgmt.AuctionsFeatureConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class Round1InitializationService {

    private static final Logger log = LoggerFactory.getLogger(Round1InitializationService.class);

    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final AggregatedInventoryRepository aggInvRepo;
    private final QualifiedBuyerCodeRepository qbcRepo;
    private final BuyerCodeRepository buyerCodeRepo;
    private final AuctionsFeatureConfigService configService;

    public Round1InitializationService(
            SchedulingAuctionRepository saRepo,
            AuctionRepository auctionRepo,
            AggregatedInventoryRepository aggInvRepo,
            QualifiedBuyerCodeRepository qbcRepo,
            BuyerCodeRepository buyerCodeRepo,
            AuctionsFeatureConfigService configService) {
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
        this.aggInvRepo = aggInvRepo;
        this.qbcRepo = qbcRepo;
        this.buyerCodeRepo = buyerCodeRepo;
        this.configService = configService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public Round1InitializationResult initialize(Long schedulingAuctionId) {
        long startedAt = System.currentTimeMillis();

        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
                .orElseThrow(() -> new SchedulingAuctionNotFoundException(schedulingAuctionId));
        Auction auction = auctionRepo.findById(sa.getAuctionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Auction not found for scheduling_auction id=" + schedulingAuctionId
                                + " auctionId=" + sa.getAuctionId()));
        Long weekId = auction.getWeekId();

        AuctionsFeatureConfig config = configService.getOrCreate();
        BigDecimal minBid = config.getMinimumAllowedBid();

        int clampedNonDw = aggInvRepo.clampNonDwTargetPrice(weekId, minBid);
        int clampedDw = aggInvRepo.clampDwTargetPrice(weekId, minBid);

        qbcRepo.deleteBySchedulingAuctionId(schedulingAuctionId);

        List<BuyerCode> buyerCodes = buyerCodeRepo.findActiveWholesaleOrDataWipe();
        for (BuyerCode bc : buyerCodes) {
            QualifiedBuyerCode qbc = new QualifiedBuyerCode();
            qbc.setSchedulingAuctionId(schedulingAuctionId);
            qbc.setBuyerCodeId(bc.getId());
            qbc.setQualificationType(QualificationType.Qualified);
            qbc.setIncluded(true);
            qbc.setSpecialTreatment(false);
            qbcRepo.save(qbc);
        }

        long durationMs = System.currentTimeMillis() - startedAt;
        log.info("r1-init completed auctionId={} schedulingAuctionId={} weekId={} "
                        + "clampedNonDw={} clampedDw={} qbcsCreated={} durationMs={}",
                auction.getId(), schedulingAuctionId, weekId,
                clampedNonDw, clampedDw, buyerCodes.size(), durationMs);

        return new Round1InitializationResult(
                schedulingAuctionId,
                auction.getId(),
                weekId,
                clampedNonDw,
                clampedDw,
                buyerCodes.size(),
                durationMs);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=Round1InitializationServiceTest test`
Expected: PASS — all 5 scenarios.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r1init/Round1InitializationServiceTest.java
git commit -m "feat: add Round1InitializationService for Mendix SUB_InitializeRound1 parity"
```

---

## Phase 3 — Listener

### Task 13: `R1InitListener` + delete stub

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/R1InitListener.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r1init/R1InitListenerTest.java`
- Delete: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R1InitStubListener.java`

- [ ] **Step 1: Write the failing listener test**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R1InitListenerTest {

    @Mock
    Round1InitializationService service;

    R1InitListener listener;

    @BeforeEach
    void setUp() {
        listener = new R1InitListener(service);
    }

    @Test
    void onRoundStarted_ignoresNonRoundOneEvents() {
        listener.onRoundStarted(new RoundStartedEvent(301L, 2, 101L, 42L));
        verify(service, never()).initialize(anyLong());
    }

    @Test
    void onRoundStarted_callsServiceWithRoundId() {
        when(service.initialize(301L))
                .thenReturn(new Round1InitializationResult(301L, 101L, 42L, 0, 0, 0, 10));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));

        verify(service).initialize(301L);
    }

    @Test
    void onRoundStarted_schedulingAuctionNotFound_doesNotRethrow() {
        when(service.initialize(301L))
                .thenThrow(new SchedulingAuctionNotFoundException(301L));

        // Must not throw — swallowed as WARN.
        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));
    }

    @Test
    void onRoundStarted_runtimeException_doesNotRethrow() {
        when(service.initialize(301L))
                .thenThrow(new RuntimeException("boom"));

        // Must not throw — swallowed as ERROR so executor thread is not poisoned.
        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=R1InitListenerTest test`
Expected: FAIL — `R1InitListener` not found.

- [ ] **Step 3: Write the listener**

```java
package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "auctions.r1-init.enabled", havingValue = "true")
public class R1InitListener {

    private static final Logger log = LoggerFactory.getLogger(R1InitListener.class);

    private final Round1InitializationService service;

    public R1InitListener(Round1InitializationService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 1) {
            return;
        }
        try {
            service.initialize(event.roundId());
        } catch (SchedulingAuctionNotFoundException ex) {
            log.warn("r1-init skipped auctionId={} schedulingAuctionId={} reason=SCHEDULING_AUCTION_NOT_FOUND",
                    event.auctionId(), event.roundId());
        } catch (RuntimeException ex) {
            log.error("r1-init failed auctionId={} schedulingAuctionId={} weekId={} error={}",
                    event.auctionId(), event.roundId(), event.weekId(), ex.toString(), ex);
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=R1InitListenerTest test`
Expected: PASS — all 4 scenarios.

- [ ] **Step 5: Delete the stub**

```bash
git rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R1InitStubListener.java
```

- [ ] **Step 6: Full test-suite sanity check**

Run: `mvn -q test`
Expected: PASS — no other test referenced the stub class (it was logging-only).

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r1init/R1InitListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/r1init/R1InitListenerTest.java
git commit -m "feat: replace R1InitStubListener with real R1InitListener (feature-flagged)"
```

---

## Phase 4 — Admin endpoint

### Task 14: `POST /admin/auctions/{auctionId}/rounds/1/init`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/controller/AuctionController.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/security/SecurityConfig.java`
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/controller/AuctionControllerTest.java`

- [ ] **Step 1: Write failing controller tests**

In `AuctionControllerTest.java`, add these cases inside the existing test class (preserve existing setup / MockMvc wiring / `@MockBean` declarations):

```java
@Test
@WithMockUser(roles = "Administrator")
void r1InitEndpoint_administrator_returnsResult() throws Exception {
    SchedulingAuction sa = new SchedulingAuction();
    sa.setId(301L);
    sa.setAuctionId(101L);
    sa.setRound(1);
    when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
            .thenReturn(Optional.of(sa));
    when(round1InitializationService.initialize(301L))
            .thenReturn(new Round1InitializationResult(301L, 101L, 42L, 3, 1, 579, 187));

    mockMvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.qbcsCreated").value(579))
            .andExpect(jsonPath("$.clampedNonDw").value(3))
            .andExpect(jsonPath("$.clampedDw").value(1));
}

@Test
@WithMockUser(roles = "SalesOps")
void r1InitEndpoint_salesOps_forbidden() throws Exception {
    mockMvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init"))
            .andExpect(status().isForbidden());
}

@Test
@WithMockUser(roles = "Bidder")
void r1InitEndpoint_bidder_forbidden() throws Exception {
    mockMvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init"))
            .andExpect(status().isForbidden());
}

@Test
@WithMockUser(roles = "Administrator")
void r1InitEndpoint_missingRound1_returnsNotFound() throws Exception {
    when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
            .thenReturn(Optional.empty());

    mockMvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init"))
            .andExpect(status().isNotFound());
}

@Test
@WithMockUser(roles = "Administrator")
void r1InitEndpoint_serviceThrows_propagatesAs500() throws Exception {
    SchedulingAuction sa = new SchedulingAuction();
    sa.setId(301L);
    sa.setAuctionId(101L);
    when(schedulingAuctionRepository.findByAuctionIdAndRound(101L, 1))
            .thenReturn(Optional.of(sa));
    when(round1InitializationService.initialize(301L))
            .thenThrow(new RuntimeException("boom"));

    mockMvc.perform(post("/api/v1/admin/auctions/101/rounds/1/init"))
            .andExpect(status().isInternalServerError());
}
```

Add `@MockBean Round1InitializationService round1InitializationService;` and (if not already present) `@MockBean SchedulingAuctionRepository schedulingAuctionRepository;` to the test class. Add imports:

```java
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationResult;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn -q -Dtest=AuctionControllerTest#r1InitEndpoint* test`
Expected: FAIL — compile errors on `Round1InitializationService` import in controller (endpoint not added yet).

- [ ] **Step 3: Add the endpoint**

In `AuctionController.java`, add the field + constructor parameter for the new service and repository (if the repository isn't already injected), plus the method:

```java
// field
private final Round1InitializationService round1InitializationService;
private final SchedulingAuctionRepository schedulingAuctionRepository;

// add to constructor signature and assignment
// (merge with existing constructor — do not add a second one)

// new endpoint method — place after the existing methods:
@PostMapping("/{auctionId}/rounds/1/init")
@PreAuthorize("hasRole('Administrator')")
public ResponseEntity<Round1InitializationResult> initializeRound1(@PathVariable Long auctionId) {
    SchedulingAuction sa = schedulingAuctionRepository
            .findByAuctionIdAndRound(auctionId, 1)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Round 1 scheduling auction not found for auctionId=" + auctionId));
    Round1InitializationResult result = round1InitializationService.initialize(sa.getId());
    return ResponseEntity.ok(result);
}
```

Imports to add:

```java
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService;
import com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
```

- [ ] **Step 4: Add SecurityConfig matcher**

In `SecurityConfig.java`, find the `.authorizeHttpRequests(...)` block and add — **before** the existing `/api/v1/admin/auctions/**` matcher that allows `Administrator` or `SalesOps`:

```java
.requestMatchers(HttpMethod.POST, "/api/v1/admin/auctions/*/rounds/1/init")
    .hasRole("Administrator")
```

Add import if missing: `import org.springframework.http.HttpMethod;`

The ordering matters — the more specific POST matcher must match first, otherwise the broader admin-or-salesops rule would let SalesOps through at the filter-chain layer (ahead of `@PreAuthorize`). This follows the 2026-04-19 ADR pattern.

- [ ] **Step 5: Run controller tests**

Run: `mvn -q -Dtest=AuctionControllerTest#r1InitEndpoint* test`
Expected: PASS — all 5 scenarios.

- [ ] **Step 6: Run full test suite**

Run: `mvn -q test`
Expected: PASS — no existing controller test regressed (SecurityConfig matcher is additive).

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/controller/AuctionController.java \
        backend/src/main/java/com/ecoatm/salesplatform/security/SecurityConfig.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/AuctionControllerTest.java
git commit -m "feat: add admin POST /auctions/{id}/rounds/1/init recovery endpoint"
```

---

## Phase 5 — Integration test

### Task 15: `R1InitializationIT` — full-chain

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/integration/R1InitializationIT.java`

- [ ] **Step 1: Write the integration test**

```java
package com.ecoatm.salesplatform.integration;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auctions.r1-init.enabled=true",
        "auctions.lifecycle.enabled=false"
})
class R1InitializationIT {

    @Autowired AuctionRepository auctionRepo;
    @Autowired SchedulingAuctionRepository saRepo;
    @Autowired AggregatedInventoryRepository aggInvRepo;
    @Autowired BuyerRepository buyerRepo;
    @Autowired BuyerCodeRepository buyerCodeRepo;
    @Autowired QualifiedBuyerCodeRepository qbcRepo;
    @Autowired ApplicationEventPublisher eventPublisher;
    @Autowired TransactionTemplate tx;

    @Test
    void roundStartedEvent_triggersR1InitEndToEnd() {
        Long weekId = seedExistingWeekId();
        Auction auction = tx.execute(s -> {
            Auction a = new Auction();
            a.setAuctionTitle("IT R1 auction " + System.nanoTime());
            a.setWeekId(weekId);
            a.setAuctionStatus(AuctionStatus.Scheduled);
            return auctionRepo.save(a);
        });

        SchedulingAuction sa = tx.execute(s -> {
            SchedulingAuction r = new SchedulingAuction();
            r.setAuctionId(auction.getId());
            r.setRound(1);
            r.setName("Round 1");
            r.setStartDatetime(Instant.now().minusSeconds(10));
            r.setEndDatetime(Instant.now().plusSeconds(3600));
            r.setRoundStatus(SchedulingAuctionStatus.Started);
            r.setHasRound(true);
            return saRepo.save(r);
        });

        long belowFloorId = seedAggInv(weekId, new BigDecimal("1.00"), 10, new BigDecimal("1.50"), 5);
        long aboveFloorId = seedAggInv(weekId, new BigDecimal("5.00"), 10, new BigDecimal("5.50"), 5);

        seedBuyerCode("WS-ACTIVE", "Wholesale", BuyerStatus.Active, false);
        seedBuyerCode("DW-ACTIVE", "Data_Wipe", BuyerStatus.Active, false);
        seedBuyerCode("WS-INACTIVE", "Wholesale", BuyerStatus.Inactive, false);
        seedBuyerCode("DW-SOFTDEL", "Data_Wipe", BuyerStatus.Active, true);
        seedBuyerCode("RETAIL-ACTIVE", "Retail", BuyerStatus.Active, false);

        eventPublisher.publishEvent(
                new RoundStartedEvent(sa.getId(), 1, auction.getId(), weekId));

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    long qbcCount = tx.execute(s ->
                            qbcRepo.findAll().stream()
                                    .filter(q -> q.getSchedulingAuctionId().equals(sa.getId()))
                                    .count());
                    assertThat(qbcCount).isEqualTo(2L);
                });

        AggregatedInventory below = aggInvRepo.findById(belowFloorId).orElseThrow();
        AggregatedInventory above = aggInvRepo.findById(aboveFloorId).orElseThrow();
        assertThat(below.getAvgTargetPrice()).isEqualByComparingTo("2.00");
        assertThat(below.getDwAvgTargetPrice()).isEqualByComparingTo("2.00");
        assertThat(above.getAvgTargetPrice()).isEqualByComparingTo("5.00");
        assertThat(above.getDwAvgTargetPrice()).isEqualByComparingTo("5.50");
    }

    /**
     * V65 seeds several weeks. Pick the most recent seeded week so we can
     * operate on pre-existing referential data without migrating more seed
     * fixtures into this test.
     */
    private Long seedExistingWeekId() {
        return tx.execute(s -> {
            // Rely on V65's seed; the first row is sufficient for this IT.
            return ((Number) s.createNativeQuery(
                    "SELECT id FROM mdm.week ORDER BY week_start_datetime LIMIT 1")
                    .getSingleResult()).longValue();
        });
    }

    private long seedAggInv(Long weekId, BigDecimal nonDw, int nonDwQty,
                            BigDecimal dw, int dwQty) {
        return tx.execute(s -> {
            AggregatedInventory row = new AggregatedInventory();
            row.setWeekId(weekId);
            row.setEcoid2("IT-" + System.nanoTime());
            row.setMergedGrade("A");
            row.setDatawipe(false);
            row.setTotalQuantity(nonDwQty);
            row.setDwTotalQuantity(dwQty);
            row.setAvgTargetPrice(nonDw);
            row.setDwAvgTargetPrice(dw);
            return aggInvRepo.save(row).getId();
        });
    }

    private void seedBuyerCode(String code, String type, BuyerStatus status, boolean soft) {
        tx.executeWithoutResult(s -> {
            Buyer buyer = new Buyer();
            buyer.setName(code + "-buyer");
            buyer.setStatus(status);
            buyerRepo.save(buyer);

            BuyerCode bc = new BuyerCode();
            bc.setCode(code + "-" + System.nanoTime());
            bc.setBuyerCodeType(type);
            bc.setSoftDelete(soft);
            BuyerCode saved = buyerCodeRepo.save(bc);

            s.createNativeQuery(
                    "INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES (?, ?)")
                    .setParameter(1, saved.getId())
                    .setParameter(2, buyer.getId())
                    .executeUpdate();
        });
    }
}
```

**Note:** This IT exercises the full async chain (event publish → `@Async` listener on `snowflakeExecutor` → service tx). The `@TestPropertySource` flips the lifecycle cron off (so it doesn't race the test) and R1 init on (so the listener is wired). Field / constructor names for `Auction`, `SchedulingAuction`, `AggregatedInventory`, `Buyer`, `BuyerCode` should match existing entity signatures — adjust getter/setter names if the existing entities use different property names (run `mvn compile` to spot mismatches).

- [ ] **Step 2: Run the IT**

Run: `mvn -q -Dtest=R1InitializationIT test`
Expected: PASS. If the Awaitility poll times out, check that `snowflakeExecutor` is present in the test context (it's declared by `AsyncConfig` per sub-project 1).

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/integration/R1InitializationIT.java
git commit -m "test: add end-to-end R1 init integration test across async executor"
```

---

## Phase 6 — Configuration + documentation

### Task 16: Feature flag configuration

**Files:**
- Modify: `backend/src/main/resources/application.yml`
- Modify: `backend/src/main/resources/application-test.yml`

- [ ] **Step 1: Add flag to `application.yml`**

Add under an `auctions:` block (merge with existing `auctions.lifecycle.*` and `auctions.snowflake-push.*` keys):

```yaml
auctions:
  r1-init:
    enabled: true
```

- [ ] **Step 2: Add flag to `application-test.yml`**

```yaml
auctions:
  r1-init:
    enabled: false
```

- [ ] **Step 3: Verify full suite still passes**

Run: `mvn -q test`
Expected: PASS — `R1InitListener` bean is excluded in `application-test.yml` thanks to `@ConditionalOnProperty`, and `R1InitializationIT` opts in via `@TestPropertySource`.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/application.yml \
        backend/src/main/resources/application-test.yml
git commit -m "chore: wire auctions.r1-init.enabled flag (dev on, test off by default)"
```

---

### Task 17: Documentation updates

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/business-logic/auction-flow.md`
- Modify: `docs/app-metadata/scheduled-events.md`
- Modify: `docs/architecture/decisions.md`

- [ ] **Step 1: Update `docs/api/rest-endpoints.md`**

Under `## Auctions (Admin)`, add after the DELETE endpoint (before the `> **Out-of-band status changes:**` callout):

```markdown
### POST /admin/auctions/{auctionId}/rounds/1/init

Manually (re)initialize Round 1 for the given auction. Ports Mendix
`SUB_InitializeRound1` — clamps aggregated-inventory target prices to
the configured `minimum_allowed_bid` floor (both non-DW and DW variants)
and rewrites the Qualified Buyer Codes (QBC) set. Idempotent: running
twice in a row produces identical state.

Normally triggered automatically by the `auctionLifecycle` cron when a
Round 1 `SchedulingAuction` transitions `Scheduled → Started`. This
endpoint is the admin recovery lever — use it when the listener was
disabled at the time of transition, or to force a re-initialization.

**Roles**: `Administrator` only. `SalesOps` and all other roles → `403`.
An explicit matcher at `POST /api/v1/admin/auctions/*/rounds/1/init` in
`SecurityConfig` is required to restrict writes, declared **before** the
broader `/api/v1/admin/auctions/**` matcher that also permits SalesOps.

**Response**: `200 OK` with `Round1InitializationResult`:

```json
{
  "schedulingAuctionId": 301,
  "auctionId": 101,
  "weekId": 42,
  "clampedNonDw": 3,
  "clampedDw": 1,
  "qbcsCreated": 579,
  "durationMs": 187
}
```

**Errors**:

| Status | Cause |
|---|---|
| `403` | Role is not Administrator |
| `404` | Auction has no Round 1 scheduling auction |
| `500` | Unexpected runtime error — tx is rolled back; check logs for `r1-init failed ...` |
```

- [ ] **Step 2: Update `docs/business-logic/auction-flow.md`**

Replace the paragraph describing R1/R2/R3 init stubs with a note that R1 init is now live (mirroring sub-project 1's callout):

```markdown
`r1-init` (sub-project 2) is **live**:
`service/auctions/r1init/R1InitListener` consumes `RoundStartedEvent`
(only when `event.round() == 1`), re-fetches the scheduling auction +
parent auction + week in a REQUIRES_NEW tx via
`Round1InitializationService.initialize`, and performs three effects
atomically: (1) clamp non-DW `avg_target_price` to `minimum_allowed_bid`
where below floor and `total_quantity > 0`; (2) clamp DW
`dw_avg_target_price` the same way; (3) delete stale QBCs for the
scheduling auction and insert one QBC per active wholesale/data-wipe
buyer code with `qualification_type='Qualified'`, `included=true`,
`is_special_treatment=false`. The feature flag `auctions.r1-init.enabled`
(default `true` in dev) gates the listener; the admin endpoint
`POST /admin/auctions/{id}/rounds/1/init` is never gated — it's the
recovery lever. The remaining four listeners (R2/R3 init, bid ranking,
R3 pre-process) still live under `service/auctions/lifecycle/stub/` as
logging-only stubs that sub-projects 3-6 will replace.
```

- [ ] **Step 3: Update `docs/app-metadata/scheduled-events.md`**

Under the `## auctionLifecycle` section's `Consumers` row, rewrite:

```markdown
| **Consumers** | `AuctionStatusSnowflakePushListener` (sub-project 1, live, deferred-writer); `R1InitListener` (sub-project 2, live — target-price clamp + QBC rewrite); `R2InitStubListener`, `R3InitStubListener`, `BidRankingStubListener`, `R3PreProcessStubListener` (logging-only stubs pending sub-projects 3-6) |
```

- [ ] **Step 4: Add ADR to `docs/architecture/decisions.md`**

Prepend to the ADR log (after the document title and `---` separator, before the most recent ADR):

```markdown
## 2026-04-22 — Auction R1 init: listener + admin endpoint + QBC schema flattening

**Status:** Accepted (sub-project 2 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `SUB_InitializeRound1` fires when a Round 1 scheduling auction
transitions `Scheduled → Started`. It performs three effects:
(1) clamp aggregated-inventory non-DW `avg_target_price` below the
`minimum_allowed_bid` floor; (2) clamp DW `dw_avg_target_price` the
same way; (3) rewrite the Qualified Buyer Codes set — delete any
existing QBCs for the scheduling auction, insert one fresh QBC per
active wholesale/data-wipe buyer code with
`qualification_type='Qualified'`, `included=true`,
`is_special_treatment=false`. Ports `ACT_UpdateRound1TargetPrice_MinBid`
+ `SUB_CreateQualifiedBuyersEntity` + `SUB_ClearQualifiedBuyerList`
into our listener + service model, skipping
`SUB_HandleSpecialTreatmentBuyerOnRoundStart` (deferred).

### Decision

- **Post-commit listener + admin recovery endpoint.** `R1InitListener`
  is a `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`
  consumer of `RoundStartedEvent`, gated by `auctions.r1-init.enabled`
  via `@ConditionalOnProperty`. Admin endpoint
  `POST /api/v1/admin/auctions/{id}/rounds/1/init` (Administrator only,
  synchronous) calls the same `Round1InitializationService.initialize`
  entry point and is never gated.
- **Single `@Transactional(REQUIRES_NEW, timeout=30)` method owns all
  three effects.** All-or-nothing: any failure rolls back the clamp
  and QBC rewrite together. The round transition itself has already
  committed by the time this runs, so a failure here does not undo
  Started status — matches the 2026-04-20 cron ADR.
- **QBC schema flattening (V72).** Mendix modeled SA ↔ QBC and
  BuyerCode ↔ QBC as M:N junctions (`qbc_scheduling_auctions`,
  `qbc_buyer_codes`). Each QBC row in practice belongs to exactly one
  SA and one BuyerCode. V72 adds `scheduling_auction_id` +
  `buyer_code_id` directly on `qualified_buyer_codes`, backfills from
  the junctions, drops both junction tables, and enforces
  `UNIQUE(scheduling_auction_id, buyer_code_id)`. Enables one-hop
  delete on the SA rewrite (`SUB_ClearQualifiedBuyerList` parity) and
  eliminates a degenerate 2-hop join for "which auction is this QBC
  for".
- **Direct SA ↔ BuyerCode association (`SchedulingAuction_QualifiedBuyers`)
  dropped.** The QBC graph already encodes the same set; the Mendix
  association was historical modeling duplication.
- **Dev-first rollout.** `auctions.r1-init.enabled=true` in dev
  `application.yml`; `false` in `application-test.yml` so unit/IT
  suites stay deterministic (the full-chain IT opts in via
  `@TestPropertySource`). QA enables via env var after one full
  manual dev cycle + intentional failure drill.

### Alternatives considered

- **Split into three services** (clamp, clear QBCs, create QBCs).
  Rejected — YAGNI. The three effects are cohesive by contract and
  Mendix treats them as one microflow. Nothing else in the port needs
  any step in isolation.
- **Per-listener audit table** (e.g., `auctions.round_init_run`).
  Deferred — logs are the audit surface for Phase 1, consistent with
  sub-project 1's `[deferred-writer]` stance. `integration.scheduled_job_run`
  already captures the tick-level aggregate.
- **Keep the Mendix `SchedulingAuction_QualifiedBuyers` association as
  a parallel junction.** Rejected — duplicates state that the
  flattened QBC row already encodes; a QBC rewrite would have to stay
  in sync with two tables.
- **Rename `snowflakeExecutor` → `auctionDownstreamExecutor`.** YAGNI
  until 2–3 more listeners share the pool.

### Consequences

- One R1 listener invocation performs at most two UPDATEs + one DELETE
  + N INSERTs (one per active wholesale/data-wipe buyer code — ~580
  in prod). All within a single tx bounded by 30s.
- Listener failures are logged at ERROR and swallowed — the async
  executor thread is not poisoned. Monitoring must watch the
  `r1-init failed ...` log line.
- Admin endpoint is the recovery path. Running it a second time
  against the same auction is a no-op on clamp counts (nothing below
  floor anymore) but fully rewrites the QBC set.
- Unique constraint `uq_qbc_sa_bc` means a concurrent double-fire
  (listener + admin) would throw and roll back the later run — safe
  by construction.
- Special-treatment buyer handling (Mendix
  `SUB_HandleSpecialTreatmentBuyerOnRoundStart`) is deferred to a
  later sub-project.

### References

- Plan: `docs/tasks/auction-r1-init-plan.md`
- Spec: `docs/tasks/auction-r1-init-design.md`
- Schema: `V72__buyer_mgmt_qbc_flatten.sql`
- Related ADRs: 2026-04-21 (Snowflake push pattern + executor),
  2026-04-20 (cron + event contract), 2026-04-19 (admin security
  matcher ordering, entity-less FK)
- Mendix source:
  `migration_context/backend/services/SUB_InitializeRound1.md`,
  `ACT_UpdateRound1TargetPrice_MinBid.md`,
  `services/SUB_CreateQualifiedBuyersEntity.md`,
  `services/SUB_ClearQualifiedBuyerList.md`,
  `Act_GetOrCreateBuyerCodeSubmitConfig.md`

---
```

- [ ] **Step 5: Commit**

```bash
git add docs/api/rest-endpoints.md \
        docs/business-logic/auction-flow.md \
        docs/app-metadata/scheduled-events.md \
        docs/architecture/decisions.md
git commit -m "docs: record R1 init endpoint, listener, QBC flattening, and ADR"
```

---

## Self-review (controller did this before handoff)

**Spec coverage check (every section of `auction-r1-init-design.md`):**
- §1 Goal — Tasks 12, 13, 14 deliver clamp + QBC rewrite, both listener and admin paths.
- §2 Mendix parity mapping — Task 5 (active wholesale/data-wipe query), Task 6 (clamp queries), Task 4 (`deleteBySchedulingAuctionId`), Task 11 (`getOrCreate`), Task 12 orchestrates.
- §3 Architecture — Task 13 wires the listener + feature flag; Task 14 wires the admin endpoint; Task 12 enforces single `REQUIRES_NEW` tx; executor reuse (`snowflakeExecutor`) is explicit in Task 13.
- §4 Schema changes — Task 1 (V72).
- §5 Entity / repository changes — Tasks 2, 3, 4, 5, 6, 7, 8.
- §6 File layout — all paths match §6; stub deleted in Task 13.
- §7 Runtime flow — §7.1 listener path covered by Tasks 13 + listener tests; §7.2 admin path by Task 14; §7.3 service contract by Task 12.
- §8 Error handling — each row in §8's table has a test case in Task 12 (not-found, DB errors via `RuntimeException`, empty buyer list, concurrent uniqueness) + Task 13 (listener swallow) + Task 14 (admin 404/500).
- §9 Configuration — Task 16.
- §10 Testing — Tasks 4 (QBC repo IT), 5 (BuyerCode repo IT), 11, 12, 13, 14, 15 match the strategy table.
- §11 Rollout — Task 16 sets dev-on / test-off; Phase A manual verification happens after Task 16 commit; Phases B/C are post-merge ops actions, not plan tasks.
- §12 Out of scope — explicitly not covered.
- §13 References — linked throughout; ADR in Task 17 closes the loop.

**Placeholder scan:** no TBD / TODO / "add appropriate X" / "similar to Task N" markers. Every code step has full source.

**Type consistency:** service class is `Round1InitializationService`; result record is `Round1InitializationResult`; exception is `SchedulingAuctionNotFoundException` — all three referenced consistently in Tasks 10, 12, 13, 14. Listener method signature (`onRoundStarted(RoundStartedEvent)`) matches `event.round()` + `event.roundId()` + `event.auctionId()` + `event.weekId()` per the existing `RoundStartedEvent` record. Repository method names (`clampNonDwTargetPrice`, `clampDwTargetPrice`, `deleteBySchedulingAuctionId`, `findActiveWholesaleOrDataWipe`, `findByAuctionIdAndRound`) are spelled identically wherever used. Enum constants `QualificationType.Qualified` / `Not_Qualified` / `Manual` match the V9 CHECK constraint and Mendix source. Entity field `specialTreatment` (boolean) maps to column `is_special_treatment` in Task 3 and is read as `isSpecialTreatment()` by Lombok.
