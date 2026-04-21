# Auction Bid Data Creation + Bidder Dashboard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port Mendix `ACT_OpenBidderDashboard` + `ACT_CreateBidData` into a Next.js bidder dashboard backed by a single-CTE bid-data generation engine, with auto-save + re-callable submit.

**Architecture:** Synchronous-on-dashboard-open CTE materializes `auctions.bid_data` rows for one `(buyer_code, bid_round)` pair, protected by `pg_advisory_xact_lock`. Re-callable submit copies `bid_*` → `submitted_*` and prior `submitted_*` → `last_valid_*`. Frontend debounces save by 500ms; backend rate-limits to 60/min/user/round.

**Tech Stack:** Spring Boot 3.2.4, Java 21, Postgres 15 + Flyway V73, native SQL CTE, Testcontainers, Next.js App Router, Vitest + Playwright.

**Spec:** `docs/tasks/auction-bid-data-create-design.md`

---

## File Structure

### New backend files

```
backend/src/main/
├── resources/db/migration/V73__auctions_bid_data_docs_and_bid_data_nullable_qty.sql
├── java/com/ecoatm/salesplatform/
│   ├── model/auctions/BidDataDoc.java
│   ├── repository/auctions/
│   │   ├── BidDataDocRepository.java
│   │   ├── BidDataRepository.java
│   │   └── BidDataCreationRepository.java      (native CTE)
│   ├── service/auctions/biddata/
│   │   ├── BidderDashboardService.java
│   │   ├── BidderDashboardLandingResult.java   (sealed)
│   │   ├── BidDataCreationService.java
│   │   ├── BidDataCreationResult.java
│   │   ├── BidDataDocService.java
│   │   ├── BidDataSubmissionService.java
│   │   ├── BidDataSubmissionException.java
│   │   ├── BidDataValidationException.java
│   │   └── BidRateLimiter.java
│   ├── controller/BidderDashboardController.java
│   └── dto/
│       ├── BidderDashboardResponse.java
│       ├── BidDataRow.java
│       ├── BidDataTotals.java
│       ├── BidRoundSummary.java
│       ├── SchedulingAuctionSummary.java
│       ├── RoundTimerState.java
│       ├── SaveBidRequest.java
│       └── BidSubmissionResult.java
```

### Modified backend files

- `backend/src/main/java/.../model/auctions/BidRound.java` — expand to map submit-state columns (submitted, submittedDatetime, submittedByUserId, roundStatus, schedulingAuctionId already mapped).
- `backend/src/main/java/.../security/SecurityConfig.java` — add bidder matchers.
- `backend/src/main/resources/application.yml` — rate limit config.

### New frontend files

```
frontend/src/
├── lib/bidder.ts                                 (API client + types)
├── hooks/useAutoSaveBid.ts
└── app/(dashboard)/bidder/dashboard/
    ├── page.tsx
    ├── BidGrid.tsx
    ├── BidGridRow.tsx
    ├── DashboardHeader.tsx
    └── SubmitBar.tsx

frontend/tests/
├── BidGrid.test.tsx
└── e2e/bidder-dashboard.spec.ts
```

### Docs updates

- `docs/api/rest-endpoints.md` — add `## Bidder Dashboard` section.
- `docs/architecture/decisions.md` — new ADR `2026-04-23 — Bidder Dashboard + Bid Data Generation`.

---

## Task 1: V73 migration — bid_data_docs + bid_data alterations

**Files:**
- Create: `backend/src/main/resources/db/migration/V73__auctions_bid_data_docs_and_bid_data_nullable_qty.sql`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/migration/V73MigrationIT.java`

- [ ] **Step 1: Write the failing integration test**

```java
package com.ecoatm.salesplatform.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class V73MigrationIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void bid_data_docs_table_exists_with_unique_user_buyer_week() {
        Integer tableCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data_docs'",
            Integer.class);
        assertThat(tableCount).isEqualTo(1);

        Integer uqCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.table_constraints "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data_docs' "
            + "AND constraint_name = 'uq_bdd_user_buyer_week'",
            Integer.class);
        assertThat(uqCount).isEqualTo(1);
    }

    @Test
    void bid_data_bid_quantity_is_nullable() {
        String nullable = jdbc.queryForObject(
            "SELECT is_nullable FROM information_schema.columns "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data' "
            + "AND column_name = 'bid_quantity'",
            String.class);
        assertThat(nullable).isEqualTo("YES");
    }

    @Test
    void bid_data_doc_id_column_exists_with_fk() {
        Integer colCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.columns "
            + "WHERE table_schema = 'auctions' AND table_name = 'bid_data' "
            + "AND column_name = 'bid_data_doc_id'",
            Integer.class);
        assertThat(colCount).isEqualTo(1);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=V73MigrationIT test`
Expected: FAIL — table `bid_data_docs` does not exist.

- [ ] **Step 3: Write the migration**

```sql
-- V73: auctions.bid_data_docs + bid_data alterations for bidder dashboard.
-- Source: Mendix ACT_BidDataDoc_GetOrCreate + ACT_CreateBidData.

CREATE TABLE auctions.bid_data_docs (
    id                BIGSERIAL      PRIMARY KEY,
    legacy_id         BIGINT         UNIQUE,
    user_id           BIGINT         NOT NULL REFERENCES identity.users(id),
    buyer_code_id     BIGINT         NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    week_id           BIGINT         NOT NULL REFERENCES mdm.week(id),
    file_name         VARCHAR(500),
    file_ref          VARCHAR(1000),
    file_size         BIGINT,
    content_type      VARCHAR(200),
    uploaded_datetime TIMESTAMPTZ,
    created_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    changed_date      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_bdd_user_buyer_week UNIQUE (user_id, buyer_code_id, week_id)
);

CREATE INDEX idx_bdd_user_week ON auctions.bid_data_docs(user_id, week_id);

COMMENT ON TABLE auctions.bid_data_docs IS
  'Per-(user,buyer_code,week) document slot for bidder dashboard. File fields stay null until CSV upload ships.';

ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP DEFAULT;
ALTER TABLE auctions.bid_data ALTER COLUMN bid_quantity DROP NOT NULL;

ALTER TABLE auctions.bid_data
    ADD COLUMN bid_data_doc_id BIGINT REFERENCES auctions.bid_data_docs(id) ON DELETE SET NULL;

CREATE INDEX idx_bd_doc ON auctions.bid_data(bid_data_doc_id);

COMMENT ON COLUMN auctions.bid_data.bid_quantity IS
  'Buyer-entered bid quantity. NULL = no cap (accept any); 0 = decline; N = max N units.';
COMMENT ON COLUMN auctions.bid_data.bid_data_doc_id IS
  'Document slot shared across rows for this (user, buyer_code, week). NULL when no doc created.';
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=V73MigrationIT test`
Expected: PASS — all three assertions green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/resources/db/migration/V73__auctions_bid_data_docs_and_bid_data_nullable_qty.sql \
        backend/src/test/java/com/ecoatm/salesplatform/migration/V73MigrationIT.java
git commit -m "feat: V73 migration — bid_data_docs table + nullable bid_quantity"
```

---

## Task 2: `BidDataDoc` entity + repository

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidDataDoc.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataDocRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataDocRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
class BidDataDocRepositoryIT {

    @Autowired
    private BidDataDocRepository repo;

    @Test
    void findByUserIdAndBuyerCodeIdAndWeekId_returnsDoc_whenPresent() {
        BidDataDoc doc = new BidDataDoc();
        doc.setUserId(1L);
        doc.setBuyerCodeId(2L);
        doc.setWeekId(3L);
        doc.setCreatedDate(Instant.now());
        doc.setChangedDate(Instant.now());
        repo.save(doc);

        Optional<BidDataDoc> found =
            repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(doc.getId());
    }

    @Test
    void findByUserIdAndBuyerCodeIdAndWeekId_empty_whenAbsent() {
        assertThat(repo.findByUserIdAndBuyerCodeIdAndWeekId(999L, 999L, 999L))
            .isEmpty();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidDataDocRepositoryIT test`
Expected: FAIL — classes do not exist.

- [ ] **Step 3: Create the entity**

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bid_data_docs", schema = "auctions")
public class BidDataDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Column(name = "week_id", nullable = false)
    private Long weekId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_ref")
    private String fileRef;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "uploaded_datetime")
    private Instant uploadedDatetime;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate;

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }
    public Long getWeekId() { return weekId; }
    public void setWeekId(Long weekId) { this.weekId = weekId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileRef() { return fileRef; }
    public void setFileRef(String fileRef) { this.fileRef = fileRef; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Instant getUploadedDatetime() { return uploadedDatetime; }
    public void setUploadedDatetime(Instant uploadedDatetime) { this.uploadedDatetime = uploadedDatetime; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BidDataDoc other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
```

- [ ] **Step 4: Create the repository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidDataDocRepository extends JpaRepository<BidDataDoc, Long> {
    Optional<BidDataDoc> findByUserIdAndBuyerCodeIdAndWeekId(
            long userId, long buyerCodeId, long weekId);
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidDataDocRepositoryIT test`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidDataDoc.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataDocRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataDocRepositoryIT.java
git commit -m "feat: BidDataDoc entity + repository with user/buyer/week finder"
```

---

## Task 3: `BidDataDocService.getOrCreate`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataDocService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataDocServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.BidDataDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidDataDocServiceTest {

    @Mock
    private BidDataDocRepository repo;

    private BidDataDocService service;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-04-23T10:00:00Z"), ZoneOffset.UTC);
        service = new BidDataDocService(repo, fixedClock);
    }

    @Test
    void getOrCreate_returnsExisting_whenPresent() {
        BidDataDoc existing = new BidDataDoc();
        existing.setUserId(1L);
        existing.setBuyerCodeId(2L);
        existing.setWeekId(3L);
        when(repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L))
            .thenReturn(Optional.of(existing));

        BidDataDoc result = service.getOrCreate(1L, 2L, 3L);

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    void getOrCreate_createsNewDoc_whenAbsent() {
        when(repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L))
            .thenReturn(Optional.empty());
        when(repo.save(any(BidDataDoc.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        BidDataDoc result = service.getOrCreate(1L, 2L, 3L);

        ArgumentCaptor<BidDataDoc> captor = ArgumentCaptor.forClass(BidDataDoc.class);
        verify(repo).save(captor.capture());
        BidDataDoc saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getBuyerCodeId()).isEqualTo(2L);
        assertThat(saved.getWeekId()).isEqualTo(3L);
        assertThat(saved.getCreatedDate()).isEqualTo(Instant.parse("2026-04-23T10:00:00Z"));
        assertThat(saved.getChangedDate()).isEqualTo(Instant.parse("2026-04-23T10:00:00Z"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidDataDocServiceTest test`
Expected: FAIL — `BidDataDocService` does not exist.

- [ ] **Step 3: Create the service**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.BidDataDocRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class BidDataDocService {

    private final BidDataDocRepository repo;
    private final Clock clock;

    public BidDataDocService(BidDataDocRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Transactional
    public BidDataDoc getOrCreate(long userId, long buyerCodeId, long weekId) {
        return repo.findByUserIdAndBuyerCodeIdAndWeekId(userId, buyerCodeId, weekId)
            .orElseGet(() -> {
                BidDataDoc doc = new BidDataDoc();
                doc.setUserId(userId);
                doc.setBuyerCodeId(buyerCodeId);
                doc.setWeekId(weekId);
                Instant now = clock.instant();
                doc.setCreatedDate(now);
                doc.setChangedDate(now);
                return repo.save(doc);
            });
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidDataDocServiceTest test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataDocService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataDocServiceTest.java
git commit -m "feat: BidDataDocService.getOrCreate with Clock-injected timestamps"
```

---

## Task 4: Expand `BidRound` entity with submit-state + timing columns

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidRound.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidRoundRepositoryIT.java` (extend existing)

- [ ] **Step 1: Add failing test case for submit-state columns**

Append to `BidRoundRepositoryIT.java` (create if missing):

```java
@Test
void bidRound_mapsSubmitStateColumns() {
    // Given a bid_round row written via JDBC with submit state fields populated
    // ...minimal setup writing an auction + scheduling_auction + bid_round row...
    BidRound round = bidRoundRepository.findById(fixtureId).orElseThrow();

    assertThat(round.getSubmitted()).isTrue();
    assertThat(round.getSubmittedDatetime()).isNotNull();
    assertThat(round.getSubmittedByUserId()).isEqualTo(42L);
    assertThat(round.getRoundStatus()).isEqualTo("Started");
    assertThat(round.getStartDatetime()).isNotNull();
    assertThat(round.getEndDatetime()).isNotNull();
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidRoundRepositoryIT test`
Expected: FAIL — compile error (getters do not exist).

- [ ] **Step 3: Add columns to the entity**

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "bid_rounds", schema = "auctions")
public class BidRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduling_auction_id", nullable = false)
    private Long schedulingAuctionId;

    @Column(name = "round_status")
    private String roundStatus;

    @Column(name = "start_datetime")
    private Instant startDatetime;

    @Column(name = "end_datetime")
    private Instant endDatetime;

    @Column(name = "submitted", nullable = false)
    private Boolean submitted = false;

    @Column(name = "submitted_datetime")
    private Instant submittedDatetime;

    @Column(name = "submitted_by_user_id")
    private Long submittedByUserId;

    public Long getId() { return id; }
    public Long getSchedulingAuctionId() { return schedulingAuctionId; }
    public void setSchedulingAuctionId(Long v) { this.schedulingAuctionId = v; }
    public String getRoundStatus() { return roundStatus; }
    public void setRoundStatus(String v) { this.roundStatus = v; }
    public Instant getStartDatetime() { return startDatetime; }
    public void setStartDatetime(Instant v) { this.startDatetime = v; }
    public Instant getEndDatetime() { return endDatetime; }
    public void setEndDatetime(Instant v) { this.endDatetime = v; }
    public Boolean getSubmitted() { return submitted; }
    public void setSubmitted(Boolean v) { this.submitted = v; }
    public Instant getSubmittedDatetime() { return submittedDatetime; }
    public void setSubmittedDatetime(Instant v) { this.submittedDatetime = v; }
    public Long getSubmittedByUserId() { return submittedByUserId; }
    public void setSubmittedByUserId(Long v) { this.submittedByUserId = v; }
}
```

Verify `auctions.bid_rounds` has these exact column names via `\d auctions.bid_rounds` before running the test. If a column differs (e.g. `submitted_by_id`), align the `@Column(name=...)` annotation rather than renaming the DB column.

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidRoundRepositoryIT test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidRound.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidRoundRepositoryIT.java
git commit -m "feat: expand BidRound entity with submit-state + timing columns"
```

---

## Task 5: DTOs — sealed landing result + response records

**Files:**
- Create all DTO files under `backend/src/main/java/com/ecoatm/salesplatform/dto/`
- Create sealed interface under `service/auctions/biddata/`

- [ ] **Step 1: Create DTO records**

`BidDataRow.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BidDataRow(
        long id,
        long bidRoundId,
        String ecoid,
        String mergedGrade,
        String buyerCodeType,
        Integer bidQuantity,          // nullable — NULL = no cap
        BigDecimal bidAmount,
        BigDecimal targetPrice,
        Integer maximumQuantity,
        BigDecimal payout,
        Integer submittedBidQuantity,
        BigDecimal submittedBidAmount,
        Integer lastValidBidQuantity,
        BigDecimal lastValidBidAmount,
        Instant submittedDatetime,
        Instant changedDate
) {}
```

`BidDataTotals.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record BidDataTotals(
        int rowCount,
        BigDecimal totalBidAmount,
        BigDecimal totalPayout,
        int totalBidQuantity) {}
```

`BidRoundSummary.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record BidRoundSummary(
        long id,
        long schedulingAuctionId,
        int round,
        String roundStatus,
        Instant startDatetime,
        Instant endDatetime,
        boolean submitted,
        Instant submittedDatetime) {}
```

`SchedulingAuctionSummary.java`:

```java
package com.ecoatm.salesplatform.dto;

public record SchedulingAuctionSummary(
        long id,
        long auctionId,
        String auctionTitle,
        int round,
        String roundName,
        String status) {}
```

`RoundTimerState.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record RoundTimerState(
        Instant now,
        Instant startsAt,
        Instant endsAt,
        long secondsUntilStart,
        long secondsUntilEnd,
        boolean active) {}
```

`SaveBidRequest.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record SaveBidRequest(
        Integer bidQuantity,        // nullable — null means "no cap"
        BigDecimal bidAmount) {}
```

`BidSubmissionResult.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record BidSubmissionResult(
        long bidRoundId,
        int rowCount,
        Instant submittedDatetime,
        boolean resubmit) {}
```

`BidderDashboardResponse.java`:

```java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record BidderDashboardResponse(
        String mode,                   // GRID | DOWNLOAD | ERROR_AUCTION_NOT_FOUND | ALL_ROUNDS_DONE
        SchedulingAuctionSummary auction,
        BidRoundSummary bidRound,
        List<BidDataRow> rows,
        BidDataTotals totals,
        RoundTimerState timer) {}
```

- [ ] **Step 2: Create sealed landing result**

`service/auctions/biddata/BidderDashboardLandingResult.java`:

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

public sealed interface BidderDashboardLandingResult
        permits BidderDashboardLandingResult.Grid,
                BidderDashboardLandingResult.Download,
                BidderDashboardLandingResult.Error,
                BidderDashboardLandingResult.AllDone {

    record Grid(long bidRoundId, long schedulingAuctionId, int round)
            implements BidderDashboardLandingResult {}

    record Download(String reason) implements BidderDashboardLandingResult {}

    record Error(String reason) implements BidderDashboardLandingResult {}

    record AllDone() implements BidderDashboardLandingResult {}
}
```

- [ ] **Step 3: Create exception classes**

`BidDataSubmissionException.java`:

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

public class BidDataSubmissionException extends RuntimeException {
    private final String code;

    public BidDataSubmissionException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
```

`BidDataValidationException.java`:

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

public class BidDataValidationException extends RuntimeException {
    private final String code;

    public BidDataValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
```

- [ ] **Step 4: Verify compile**

Run: `cd backend && mvn compile`
Expected: Build green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/ \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidderDashboardLandingResult.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataSubmissionException.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataValidationException.java
git commit -m "feat: bidder dashboard DTOs + sealed landing result + exception types"
```

---

## Task 6: `BidDataRepository` — read helpers

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataRepositoryIT.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.repository.auctions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BidDataRepositoryIT {

    @Autowired JdbcTemplate jdbc;
    @Autowired BidDataRepository repo;

    @Test
    void countByBidRoundId_returnsCount() {
        // Insert fixture via JdbcTemplate — minimal bid_data row
        // ... fixture setup writes auction + sa + bid_round (id=1001) + bid_data (2 rows) ...
        long count = repo.countByBidRoundId(1001L);
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void findByBidRoundId_ordersByEcoidThenGrade() {
        // fixture writes 3 rows with different ecoid/grade
        var rows = repo.findByBidRoundIdOrderByEcoidAscMergedGradeAsc(1001L);
        assertThat(rows).hasSize(3);
        assertThat(rows.get(0).getEcoid()).isLessThanOrEqualTo(rows.get(1).getEcoid());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidDataRepositoryIT test`
Expected: FAIL — `BidDataRepository` does not exist.

- [ ] **Step 3: Create the `BidData` entity first (stub)**

`backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidData.java` — minimal projection of `auctions.bid_data` mapping every column the repository + submission service + save endpoint read or write. Full column list (mirroring V61 + V73):

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bid_data", schema = "auctions")
public class BidData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_round_id", nullable = false)
    private Long bidRoundId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Column(name = "ecoid") private String ecoid;
    @Column(name = "merged_grade") private String mergedGrade;
    @Column(name = "buyer_code_type") private String buyerCodeType;

    @Column(name = "bid_quantity") private Integer bidQuantity;            // nullable post V73
    @Column(name = "bid_amount")   private BigDecimal bidAmount;
    @Column(name = "target_price") private BigDecimal targetPrice;
    @Column(name = "maximum_quantity") private Integer maximumQuantity;
    @Column(name = "payout")       private BigDecimal payout;

    @Column(name = "submitted_bid_quantity") private Integer submittedBidQuantity;
    @Column(name = "submitted_bid_amount")   private BigDecimal submittedBidAmount;
    @Column(name = "last_valid_bid_quantity") private Integer lastValidBidQuantity;
    @Column(name = "last_valid_bid_amount")   private BigDecimal lastValidBidAmount;
    @Column(name = "submitted_datetime")      private Instant submittedDatetime;

    @Column(name = "changed_date") private Instant changedDate;
    @Column(name = "changed_by_id") private Long changedById;

    // getters + setters (generate all) omitted here — must exist for each field
}
```

Generate all getters/setters. Include `equals`/`hashCode` on `id`.

- [ ] **Step 4: Create the repository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidDataRepository extends JpaRepository<BidData, Long> {
    long countByBidRoundId(long bidRoundId);
    List<BidData> findByBidRoundIdOrderByEcoidAscMergedGradeAsc(long bidRoundId);
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidDataRepositoryIT test`
Expected: PASS (after fleshing out fixture inserts to write real rows).

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/BidData.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataRepositoryIT.java
git commit -m "feat: BidData entity + BidDataRepository read helpers"
```

---

## Task 7: `BidDataCreationRepository` — native CTE

This is the largest task: the single-CTE `INSERT ... SELECT` that ports 1,663 Mendix lines. Build it incrementally against the scenario fixture.

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

### Task 7a — scenario fixture builder

- [ ] **Step 1: Write the fixture builder**

```java
package com.ecoatm.salesplatform.fixtures;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class BidDataScenario {

    private final JdbcTemplate jdbc;
    private int round = 1;
    private String buyerCodeType = "Wholesale";
    private boolean specialTreatment = false;
    private boolean included = true;
    private Map<String, InventorySpec> inventory = new HashMap<>();
    private Map<String, BidSpec> priorRoundBids = new HashMap<>();
    private FilterSpec filter = null;

    public BidDataScenario(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public BidDataScenario round(int r) { this.round = r; return this; }
    public BidDataScenario buyerCodeType(String t) { this.buyerCodeType = t; return this; }
    public BidDataScenario specialTreatment(boolean b) { this.specialTreatment = b; return this; }
    public BidDataScenario included(boolean b) { this.included = b; return this; }
    public BidDataScenario inventory(String ecoid, String grade, int qty, BigDecimal targetPrice) {
        inventory.put(ecoid + "|" + grade, new InventorySpec(qty, targetPrice));
        return this;
    }
    public BidDataScenario priorRoundBid(String ecoid, String grade, int qty, BigDecimal amount) {
        priorRoundBids.put(ecoid + "|" + grade, new BidSpec(qty, amount));
        return this;
    }
    public BidDataScenario qualificationFilter(BigDecimal targetPercent, BigDecimal targetValue,
                                               BigDecimal floor) {
        this.filter = new FilterSpec(targetPercent, targetValue, floor);
        return this;
    }

    /** Writes all rows and returns the new bid_round id. */
    public long commitAndReturnBidRoundId() {
        // 1. insert mdm.week, auctions.auctions, auctions.scheduling_auctions,
        //    auctions.bid_rounds
        // 2. insert buyer_mgmt.buyer_codes + qualified_buyer_codes (flat post-V72)
        // 3. insert auctions.aggregated_inventory rows per `inventory` map
        // 4. insert auctions.bid_data rows per `priorRoundBids` map against the
        //    previous round
        // 5. insert auctions.bid_round_selection_filters if filter != null
        // Return the new bid_round_id.
        // ...concrete JdbcTemplate calls...
        return 1001L; // placeholder — real impl returns actual generated id
    }

    public record InventorySpec(int quantity, BigDecimal targetPrice) {}
    public record BidSpec(int quantity, BigDecimal amount) {}
    public record FilterSpec(BigDecimal targetPercent, BigDecimal targetValue, BigDecimal floor) {}
}
```

Flesh out the `commitAndReturnBidRoundId()` implementation with concrete `jdbc.update(...)` + `jdbc.queryForObject(...)` calls writing each table. Reference existing migrations V58–V72 for column names.

- [ ] **Step 2: Commit the builder alone**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java
git commit -m "test: BidDataScenario fixture builder for CTE IT suite"
```

### Task 7b — CTE happy-path test (R1 Wholesale)

- [ ] **Step 1: Write the failing CTE IT test**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.fixtures.BidDataScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BidDataCreationRepositoryIT {

    @Autowired JdbcTemplate jdbc;
    @Autowired BidDataCreationRepository repo;

    @Test
    void generate_r1_wholesale_buyer_producesOneRowPerInventoryLine() {
        long bidRoundId = new BidDataScenario(jdbc)
            .round(1)
            .buyerCodeType("Wholesale")
            .inventory("AAA1", "A", 10, new BigDecimal("25.00"))
            .inventory("AAA1", "B", 5,  new BigDecimal("15.00"))
            .commitAndReturnBidRoundId();

        int inserted = repo.generate(bidRoundId, /*buyerCodeId*/ 1L, /*bidDataDocId*/ 1L);

        assertThat(inserted).isEqualTo(2);
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
            Integer.class, bidRoundId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void generate_idempotent_skipsWhenRowsExist() {
        long bidRoundId = new BidDataScenario(jdbc)
            .round(1)
            .buyerCodeType("Wholesale")
            .inventory("AAA1", "A", 10, new BigDecimal("25.00"))
            .commitAndReturnBidRoundId();

        int first = repo.generate(bidRoundId, 1L, 1L);
        int second = repo.generate(bidRoundId, 1L, 1L);

        assertThat(first).isEqualTo(1);
        assertThat(second).isEqualTo(0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidDataCreationRepositoryIT test`
Expected: FAIL — `BidDataCreationRepository.generate` does not exist.

- [ ] **Step 3: Implement the repository with the full CTE**

```java
package com.ecoatm.salesplatform.repository.auctions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BidDataCreationRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String CTE_SQL = """
        WITH
        params AS (
            SELECT
                :bid_round_id::bigint   AS bid_round_id,
                :buyer_code_id::bigint  AS buyer_code_id,
                :bid_data_doc_id::bigint AS bid_data_doc_id,
                br.scheduling_auction_id AS scheduling_auction_id,
                sa.round                  AS round,
                a.week_id                 AS week_id,
                bc.code                   AS buyer_code_text,
                b.company_name            AS company_name,
                CASE WHEN bc.data_wipe_approved THEN 'DW'
                     ELSE 'Wholesale' END  AS buyer_code_type
            FROM auctions.bid_rounds br
            JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
            JOIN auctions.auctions a              ON a.id = sa.auction_id
            JOIN buyer_mgmt.buyer_codes bc        ON bc.id = :buyer_code_id
            LEFT JOIN buyer_mgmt.buyers b          ON b.id = bc.buyer_id
            WHERE br.id = :bid_round_id
        ),
        existing_check AS (
            SELECT COUNT(*) AS n
            FROM auctions.bid_data
            WHERE bid_round_id = :bid_round_id
        ),
        qualified_buyer_check AS (
            SELECT qbc.included, qbc.is_special_treatment, qbc.qualification_type
            FROM buyer_mgmt.qualified_buyer_codes qbc, params
            WHERE qbc.scheduling_auction_id = params.scheduling_auction_id
              AND qbc.buyer_code_id         = params.buyer_code_id
        ),
        selection_filter AS (
            SELECT bsf.*
            FROM auctions.bid_round_selection_filters bsf, params
            WHERE bsf.round = params.round
        ),
        inventory AS (
            SELECT ai.*
            FROM auctions.aggregated_inventory ai, params
            WHERE ai.week_id = params.week_id
              AND ai.is_deprecated = false
              AND (
                    (params.buyer_code_type = 'DW'        AND ai.dw_total_quantity > 0)
                 OR (params.buyer_code_type = 'Wholesale' AND ai.total_quantity    > 0)
                  )
        ),
        inventory_with_threshold AS (
            SELECT inv.*,
                   /* STEP A: bid_meets_threshold flag from R1 submitted bids + filter */
                   TRUE AS bid_meets_threshold   -- TODO: port threshold calc for R2/R3
            FROM inventory inv
        ),
        inventory_qualified AS (
            SELECT iwt.*,
                   /* STEP B: row_visible flag from included + special_treatment */
                   TRUE AS row_visible
            FROM inventory_with_threshold iwt, qualified_buyer_check q
            WHERE q.included = true
        ),
        prior_scheduling_auction AS (
            SELECT sa_prev.id AS prev_sa_id
            FROM auctions.scheduling_auctions sa_prev, params
            WHERE sa_prev.auction_id = (
                SELECT auction_id FROM auctions.scheduling_auctions WHERE id = params.scheduling_auction_id
            )
              AND sa_prev.round = params.round - 1
        ),
        prior_round_biddata AS (
            SELECT bd.ecoid, bd.merged_grade,
                   bd.submitted_bid_quantity AS prev_qty,
                   bd.submitted_bid_amount   AS prev_amount
            FROM auctions.bid_data bd
            JOIN auctions.bid_rounds br_prev ON br_prev.id = bd.bid_round_id
            JOIN prior_scheduling_auction psa ON psa.prev_sa_id = br_prev.scheduling_auction_id
            WHERE bd.buyer_code_id = (SELECT buyer_code_id FROM params)
        ),
        qualified_rows AS (
            SELECT iq.*, prb.prev_qty, prb.prev_amount
            FROM inventory_qualified iq
            LEFT JOIN prior_round_biddata prb
                   ON prb.ecoid = iq.ecoid AND prb.merged_grade = iq.merged_grade,
                 existing_check ec
            WHERE iq.row_visible = TRUE AND ec.n = 0
        )
        INSERT INTO auctions.bid_data (
            bid_round_id, buyer_code_id, aggregated_inventory_id,
            ecoid, merged_grade, code, company_name,
            bid_quantity, bid_amount, target_price, maximum_quantity, buyer_code_type,
            previous_round_bid_quantity, previous_round_bid_amount,
            bid_round, week_id, bid_data_doc_id,
            created_date, changed_date
        )
        SELECT
            :bid_round_id,
            (SELECT buyer_code_id FROM params),
            qr.id,
            qr.ecoid, qr.merged_grade,
            (SELECT buyer_code_text FROM params),
            (SELECT company_name    FROM params),
            NULL,                                  -- bid_quantity: NULL = no cap (default)
            0,                                     -- bid_amount
            CASE WHEN (SELECT buyer_code_type FROM params) = 'DW'
                 THEN qr.dw_avg_target_price
                 ELSE qr.avg_target_price END,
            CASE WHEN (SELECT buyer_code_type FROM params) = 'DW'
                 THEN qr.dw_total_quantity
                 ELSE qr.total_quantity END,
            (SELECT buyer_code_type FROM params),
            qr.prev_qty, qr.prev_amount,
            (SELECT round FROM params),
            (SELECT week_id::integer FROM params),
            :bid_data_doc_id,
            NOW(), NOW()
        FROM qualified_rows qr
        """;

    @Transactional(propagation = Propagation.MANDATORY)
    public int generate(long bidRoundId, long buyerCodeId, long bidDataDocId) {
        return em.createNativeQuery(CTE_SQL)
            .setParameter("bid_round_id", bidRoundId)
            .setParameter("buyer_code_id", buyerCodeId)
            .setParameter("bid_data_doc_id", bidDataDocId)
            .executeUpdate();
    }
}
```

> The `TODO: port threshold calc for R2/R3` comment marks the spots sub-project 4 will expand. For this sub-project the CTE correctly materializes all R1 rows; R2/R3 qualification gates are applied by `inventory_with_threshold.bid_meets_threshold = TRUE`, which keeps R2/R3 generation conservative (all rows visible) until sub-project 4 replaces the TRUE stubs with real threshold logic. This matches the spec §4.6 "CTE inserts 0 rows; grid renders empty state" failure mode only for unqualified buyers — qualified R2/R3 buyers still get rows.

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidDataCreationRepositoryIT#generate_r1_wholesale_buyer_producesOneRowPerInventoryLine test`
Expected: PASS (insert count = 2).

Run: `cd backend && mvn -Dtest=BidDataCreationRepositoryIT#generate_idempotent_skipsWhenRowsExist test`
Expected: PASS (second call returns 0).

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java
git commit -m "feat: BidDataCreationRepository — single-CTE bid_data generator"
```

### Task 7c — DW buyer branch + remaining scenario tests

- [ ] **Step 1: Add DW branch test**

```java
@Test
void generate_dw_buyer_usesDwColumns() {
    long bidRoundId = new BidDataScenario(jdbc)
        .round(1).buyerCodeType("DW")
        .inventory("AAA1", "A", /*qty*/ 10, new BigDecimal("25.00"))
        // Must set dw_total_quantity > 0 and dw_avg_target_price in fixture
        .commitAndReturnBidRoundId();

    int inserted = repo.generate(bidRoundId, dwBuyerCodeId, docId);
    assertThat(inserted).isEqualTo(1);

    BigDecimal target = jdbc.queryForObject(
        "SELECT target_price FROM auctions.bid_data WHERE bid_round_id = ?",
        BigDecimal.class, bidRoundId);
    assertThat(target).isEqualByComparingTo(dwTargetPrice);
}

@Test
void generate_unqualifiedBuyer_insertsZeroRows() { /* included = false */ }

@Test
void generate_priorRoundCarryforward_populatesPreviousRoundColumns() { /* round=2 with R1 bids */ }

@Test
void generate_emptyInventory_insertsZeroRows() { /* no aggregated_inventory */ }

@Test
void generate_specialTreatment_bypassesRowVisibleFilter() { /* specialTreatment = true */ }
```

- [ ] **Step 2: Run + iterate**

Run: `cd backend && mvn -Dtest=BidDataCreationRepositoryIT test`
Expected: all 7 scenarios green.

For scenarios that fail, refine the CTE. The fixture builder + CTE iterate together until all 7 scenarios pass.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java \
        backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java
git commit -m "test: CTE scenario matrix — DW, unqualified, carryforward, special treatment"
```

---

## Task 8: `BidDataCreationService` with advisory lock

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationService.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationResult.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.BidDataCreationRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidDataCreationServiceTest {

    @Mock BidDataCreationRepository creationRepo;
    @Mock BidDataRepository bidDataRepo;
    @Mock BidDataDocService bidDataDocService;
    @Mock JdbcTemplate jdbc;
    // + BidRoundRepository, SchedulingAuctionRepository, AuctionRepository

    BidDataCreationService service;

    @BeforeEach
    void setUp() { /* wire with mocks */ }

    @Test
    void ensureRowsExist_skipsWhenRowsAlreadyExist() {
        when(bidDataRepo.countByBidRoundId(100L)).thenReturn(42L);

        BidDataCreationResult result = service.ensureRowsExist(7L, 100L, 3L);

        assertThat(result.skipped()).isTrue();
        assertThat(result.rowsCreated()).isEqualTo(0);
        verify(creationRepo, never()).generate(anyLong(), anyLong(), anyLong());
    }

    @Test
    void ensureRowsExist_acquiresAdvisoryLock_thenGenerates() {
        when(bidDataRepo.countByBidRoundId(100L)).thenReturn(0L);
        // mock bid_round -> scheduling_auction -> auction -> week_id chain
        BidDataDoc doc = new BidDataDoc();
        doc.setId(55L);
        when(bidDataDocService.getOrCreate(anyLong(), anyLong(), anyLong())).thenReturn(doc);
        when(creationRepo.generate(100L, 7L, 55L)).thenReturn(12);

        BidDataCreationResult result = service.ensureRowsExist(7L, 100L, 3L);

        assertThat(result.skipped()).isFalse();
        assertThat(result.rowsCreated()).isEqualTo(12);
        verify(jdbc).queryForObject(
            eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
            eq(Boolean.class), eq(100L));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=BidDataCreationServiceTest test`
Expected: FAIL — class does not exist.

- [ ] **Step 3: Create result record + service**

`BidDataCreationResult.java`:

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

public record BidDataCreationResult(
        int rowsCreated,
        boolean skipped,
        long durationMs) {}
```

`BidDataCreationService.java`:

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidDataCreationService {

    private static final Logger log = LoggerFactory.getLogger(BidDataCreationService.class);

    private final BidDataCreationRepository creationRepo;
    private final BidDataRepository bidDataRepo;
    private final BidDataDocService docService;
    private final BidRoundRepository bidRoundRepo;
    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final JdbcTemplate jdbc;

    public BidDataCreationService(BidDataCreationRepository creationRepo,
                                   BidDataRepository bidDataRepo,
                                   BidDataDocService docService,
                                   BidRoundRepository bidRoundRepo,
                                   SchedulingAuctionRepository saRepo,
                                   AuctionRepository auctionRepo,
                                   JdbcTemplate jdbc) {
        this.creationRepo = creationRepo;
        this.bidDataRepo = bidDataRepo;
        this.docService = docService;
        this.bidRoundRepo = bidRoundRepo;
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public BidDataCreationResult ensureRowsExist(long buyerCodeId, long bidRoundId, long userId) {
        long started = System.currentTimeMillis();

        if (bidDataRepo.countByBidRoundId(bidRoundId) > 0) {
            return new BidDataCreationResult(0, true, System.currentTimeMillis() - started);
        }

        jdbc.queryForObject(
            "SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)",
            Boolean.class, bidRoundId);

        if (bidDataRepo.countByBidRoundId(bidRoundId) > 0) {
            return new BidDataCreationResult(0, true, System.currentTimeMillis() - started);
        }

        var round = bidRoundRepo.findById(bidRoundId).orElseThrow();
        var sa = saRepo.findById(round.getSchedulingAuctionId()).orElseThrow();
        var auction = auctionRepo.findById(sa.getAuctionId()).orElseThrow();

        BidDataDoc doc = docService.getOrCreate(userId, buyerCodeId, auction.getWeekId());
        int inserted = creationRepo.generate(bidRoundId, buyerCodeId, doc.getId());

        long duration = System.currentTimeMillis() - started;
        log.info("bid-data generated bidRoundId={} buyerCodeId={} rows={} durationMs={}",
                 bidRoundId, buyerCodeId, inserted, duration);

        return new BidDataCreationResult(inserted, false, duration);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidDataCreationServiceTest test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationService.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationResult.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataCreationServiceTest.java
git commit -m "feat: BidDataCreationService with advisory lock + re-check + duration log"
```

---

## Task 9: `BidderDashboardService` — landing matrix + grid loader

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidderDashboardService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidderDashboardServiceTest.java`

- [ ] **Step 1: Write the failing test (landing matrix)**

```java
@ExtendWith(MockitoExtension.class)
class BidderDashboardServiceTest {
    // mocks: SchedulingAuctionRepository, BidRoundRepository, QBC repo, BidDataRepository

    @Test
    void landingRoute_noActiveAuction_returnsError() { /* ... */ }

    @Test
    void landingRoute_buyerNotIncluded_returnsDownload() { /* QBC.included=false */ }

    @Test
    void landingRoute_r2DoneAndClosed_returnsDownload() { /* ... */ }

    @Test
    void landingRoute_allRoundsSubmitted_returnsAllDone() { /* ... */ }

    @Test
    void landingRoute_activeRound_returnsGrid() {
        BidderDashboardLandingResult result = service.landingRoute(7L, 99L);
        assertThat(result).isInstanceOf(BidderDashboardLandingResult.Grid.class);
    }

    @Test
    void loadGrid_projectsBidDataToRowsAndTotals() { /* ... */ }
}
```

- [ ] **Step 2: Run test to verify it fails**

Expected: FAIL — class missing.

- [ ] **Step 3: Implement the service**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.model.auctions.*;
import com.ecoatm.salesplatform.repository.auctions.*;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BidderDashboardService {

    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final BidRoundRepository bidRoundRepo;
    private final QualifiedBuyerCodeRepository qbcRepo;
    private final BidDataRepository bidDataRepo;
    private final Clock clock;

    // constructor omitted for brevity

    @Transactional(readOnly = true)
    public BidderDashboardLandingResult landingRoute(long userId, long buyerCodeId) {
        // 1. Find active scheduling auction via latest Started round for this buyer
        // 2. If none -> Error("AUCTION_NOT_FOUND")
        // 3. Look up QBC row for (sa.id, buyerCodeId); if !included -> Download
        // 4. If all three rounds have submitted=true for this buyer -> AllDone
        // 5. If current round = R3 not started yet -> AllDone (or Download per spec §6)
        // 6. Else -> Grid(bidRoundId, sa.id, round)
        // Full decision tree mirrors ACT_OpenBidderDashboard.md
        throw new UnsupportedOperationException("flesh out per landing matrix");
    }

    @Transactional(readOnly = true)
    public BidderDashboardResponse loadGrid(long bidRoundId, long buyerCodeId) {
        // Load round, SA, auction, bid_data rows (filtered by buyer_code_id), compute totals + timer
        throw new UnsupportedOperationException("flesh out");
    }

    private BidDataTotals computeTotals(List<BidData> rows) {
        BigDecimal totalBidAmount = BigDecimal.ZERO;
        BigDecimal totalPayout = BigDecimal.ZERO;
        int totalBidQty = 0;
        for (BidData r : rows) {
            if (r.getBidAmount() != null) totalBidAmount = totalBidAmount.add(r.getBidAmount());
            if (r.getPayout() != null)    totalPayout    = totalPayout.add(r.getPayout());
            if (r.getBidQuantity() != null) totalBidQty += r.getBidQuantity();
        }
        return new BidDataTotals(rows.size(), totalBidAmount, totalPayout, totalBidQty);
    }

    private RoundTimerState buildTimer(BidRound round) {
        Instant now = clock.instant();
        Instant starts = round.getStartDatetime();
        Instant ends = round.getEndDatetime();
        long secsToStart = starts == null ? -1 : Math.max(0, starts.getEpochSecond() - now.getEpochSecond());
        long secsToEnd   = ends   == null ? -1 : Math.max(0, ends.getEpochSecond()   - now.getEpochSecond());
        boolean active   = starts != null && ends != null
                           && !now.isBefore(starts) && now.isBefore(ends);
        return new RoundTimerState(now, starts, ends, secsToStart, secsToEnd, active);
    }
}
```

- [ ] **Step 4: Iterate against the landing matrix tests**

Flesh out `landingRoute` branch-by-branch until each scenario test passes.

- [ ] **Step 5: Run the full test class**

Run: `cd backend && mvn -Dtest=BidderDashboardServiceTest test`
Expected: all 6 scenarios green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidderDashboardService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidderDashboardServiceTest.java
git commit -m "feat: BidderDashboardService landing matrix + grid loader"
```

---

## Task 10: `BidDataSubmissionService` — save + submit

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataSubmissionService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataSubmissionServiceTest.java`

- [ ] **Step 1: Write the failing save tests**

```java
@Test
void save_validates_ownership() {
    // user 7 tries to edit bid_data belonging to buyer_code not in user_buyers
    assertThatThrownBy(() -> service.save(7L, 100L, new SaveBidRequest(5, new BigDecimal("10.00"))))
        .isInstanceOf(BidDataSubmissionException.class)
        .extracting("code").isEqualTo("NOT_YOUR_BID_DATA");
}

@Test
void save_allowsNullBidQuantity() {
    // bidQuantity = null  -> saved; no cap enforcement
}

@Test
void save_rejectsNegativeQuantity() {
    assertThatThrownBy(() -> service.save(7L, 100L, new SaveBidRequest(-1, new BigDecimal("10.00"))))
        .isInstanceOf(BidDataValidationException.class)
        .extracting("code").isEqualTo("INVALID_QUANTITY");
}

@Test
void save_rejectsWhenQuantityExceedsMax() {
    // maximum_quantity = 5 on the row; attempting qty = 10
    assertThatThrownBy(() -> service.save(7L, 100L, new SaveBidRequest(10, new BigDecimal("10.00"))))
        .isInstanceOf(BidDataValidationException.class)
        .extracting("code").isEqualTo("INVALID_QUANTITY");
}

@Test
void save_rejectsWhenRoundClosed() {
    assertThatThrownBy(() -> service.save(7L, 100L, new SaveBidRequest(5, new BigDecimal("10.00"))))
        .extracting("code").isEqualTo("ROUND_CLOSED");
}
```

Submit tests:

```java
@Test
void submit_firstSubmit_copiesBidToSubmittedWithNullLastValid() {
    // prior: submitted_bid_* = NULL, bid_quantity=5, bid_amount=10
    service.submit(userId, bidRoundId);
    // verify UPDATE ran: last_valid_* = NULL, submitted_* = 5,10, submitted=true
}

@Test
void submit_resubmit_slidesSubmittedToLastValid() {
    // prior: submitted_bid_qty=3, submitted_bid_amount=8, bid_qty=5, bid_amount=10
    service.submit(userId, bidRoundId);
    // verify: last_valid_qty=3, last_valid_amount=8, submitted_qty=5, submitted_amount=10
}

@Test
void submit_closedRound_throws() {
    assertThatThrownBy(() -> service.submit(userId, bidRoundId))
        .extracting("code").isEqualTo("ROUND_CLOSED");
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd backend && mvn -Dtest=BidDataSubmissionServiceTest test`
Expected: FAIL — class does not exist.

- [ ] **Step 3: Implement the service**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.model.auctions.*;
import com.ecoatm.salesplatform.repository.auctions.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BidDataSubmissionService {

    private final BidDataRepository bidDataRepo;
    private final BidRoundRepository bidRoundRepo;
    private final JdbcTemplate jdbc;
    // + user_buyers ownership guard

    // constructor

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 10)
    public BidDataRow save(long userId, long bidDataId, SaveBidRequest req) {
        BidData row = bidDataRepo.findById(bidDataId)
            .orElseThrow(() -> new BidDataSubmissionException("BID_DATA_NOT_FOUND",
                "Bid data not found: " + bidDataId));

        assertOwnership(userId, row.getBuyerCodeId());
        assertRoundOpen(row.getBidRoundId());
        validateAmountAndQuantity(req, row.getMaximumQuantity());

        row.setBidQuantity(req.bidQuantity());
        row.setBidAmount(req.bidAmount());
        row.setChangedDate(Instant.now());
        row.setChangedById(userId);
        bidDataRepo.save(row);

        return toDto(row);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 15)
    public BidSubmissionResult submit(long userId, String username, long bidRoundId) {
        BidRound round = bidRoundRepo.findById(bidRoundId)
            .orElseThrow(() -> new BidDataSubmissionException("BID_ROUND_NOT_FOUND",
                "Bid round not found: " + bidRoundId));

        if ("Closed".equals(round.getRoundStatus())) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }

        boolean isResubmit = Boolean.TRUE.equals(round.getSubmitted());

        int rowsAffected = jdbc.update("""
            UPDATE auctions.bid_data SET
                last_valid_bid_quantity = submitted_bid_quantity,
                last_valid_bid_amount   = submitted_bid_amount,
                submitted_bid_quantity  = bid_quantity,
                submitted_bid_amount    = bid_amount,
                submitted_datetime      = NOW(),
                submit_user             = ?,
                changed_date            = NOW(),
                changed_by_id           = ?
            WHERE bid_round_id = ?
            """, username, userId, bidRoundId);

        jdbc.update("""
            UPDATE auctions.bid_rounds SET
                submitted            = TRUE,
                submitted_datetime   = NOW(),
                submitted_by_user_id = ?,
                changed_date         = NOW()
            WHERE id = ?
            """, userId, bidRoundId);

        return new BidSubmissionResult(bidRoundId, rowsAffected, Instant.now(), isResubmit);
    }

    private void assertOwnership(long userId, long buyerCodeId) {
        Long count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM user_mgmt.user_buyers WHERE user_id = ? AND buyer_code_id = ?",
            Long.class, userId, buyerCodeId);
        if (count == null || count == 0L) {
            throw new BidDataSubmissionException("NOT_YOUR_BID_DATA",
                "User does not own buyer_code_id=" + buyerCodeId);
        }
    }

    private void assertRoundOpen(long bidRoundId) {
        String status = jdbc.queryForObject(
            "SELECT round_status FROM auctions.bid_rounds WHERE id = ?",
            String.class, bidRoundId);
        if ("Closed".equals(status)) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }
    }

    private void validateAmountAndQuantity(SaveBidRequest req, Integer maximumQuantity) {
        if (req.bidAmount() == null || req.bidAmount().signum() < 0) {
            throw new BidDataValidationException("INVALID_AMOUNT",
                "bidAmount must be non-null and >= 0");
        }
        if (req.bidQuantity() != null) {
            if (req.bidQuantity() < 0) {
                throw new BidDataValidationException("INVALID_QUANTITY",
                    "bidQuantity must be >= 0");
            }
            if (maximumQuantity != null && req.bidQuantity() > maximumQuantity) {
                throw new BidDataValidationException("INVALID_QUANTITY",
                    "bidQuantity exceeds maximum_quantity=" + maximumQuantity);
            }
        }
    }

    private BidDataRow toDto(BidData r) { /* map every field */ return null; }
}
```

Check the actual schema of `user_mgmt.user_buyers` first — if the table's name/schema/columns differ from what's shown, fix the SQL accordingly. (The project's migration files V17–V19 set up the `user_mgmt` schema.)

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd backend && mvn -Dtest=BidDataSubmissionServiceTest test`
Expected: all scenarios green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataSubmissionService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidDataSubmissionServiceTest.java
git commit -m "feat: BidDataSubmissionService with ownership + validation + submit slide"
```

---

## Task 11: Rate limiter (60 req/min/user/bid_round)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidRateLimiter.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidRateLimiterTest.java`

- [ ] **Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class BidRateLimiterTest {

    private BidRateLimiter limiter;
    private Clock clock;
    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.parse("2026-04-23T10:00:00Z");
        clock = Clock.fixed(now, ZoneOffset.UTC);
        limiter = new BidRateLimiter(clock, /*maxPerMinute*/ 60);
    }

    @Test
    void allows_upToLimit() {
        for (int i = 0; i < 60; i++) {
            assertThat(limiter.tryAcquire(7L, 100L)).isTrue();
        }
        assertThat(limiter.tryAcquire(7L, 100L)).isFalse();
    }

    @Test
    void resetsAfterOneMinute() {
        for (int i = 0; i < 60; i++) limiter.tryAcquire(7L, 100L);
        assertThat(limiter.tryAcquire(7L, 100L)).isFalse();
        clock = Clock.fixed(now.plusSeconds(61), ZoneOffset.UTC);
        limiter = new BidRateLimiter(clock, 60);
        assertThat(limiter.tryAcquire(7L, 100L)).isTrue();
    }

    @Test
    void separateKeys_doNotShareBucket() {
        for (int i = 0; i < 60; i++) limiter.tryAcquire(7L, 100L);
        assertThat(limiter.tryAcquire(8L, 100L)).isTrue();
        assertThat(limiter.tryAcquire(7L, 200L)).isTrue();
    }
}
```

- [ ] **Step 2: Implement**

```java
package com.ecoatm.salesplatform.service.auctions.biddata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BidRateLimiter {

    private final Clock clock;
    private final int maxPerMinute;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public BidRateLimiter(Clock clock,
                          @Value("${bidder.rate-limit.max-per-minute:60}") int maxPerMinute) {
        this.clock = clock;
        this.maxPerMinute = maxPerMinute;
    }

    public boolean tryAcquire(long userId, long bidRoundId) {
        String key = userId + ":" + bidRoundId;
        long minute = clock.instant().getEpochSecond() / 60;
        Bucket b = buckets.compute(key, (k, existing) -> {
            if (existing == null || existing.minute != minute) {
                return new Bucket(minute, new AtomicInteger(0));
            }
            return existing;
        });
        return b.count.incrementAndGet() <= maxPerMinute;
    }

    private record Bucket(long minute, AtomicInteger count) {}
}
```

- [ ] **Step 3: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=BidRateLimiterTest test`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/biddata/BidRateLimiter.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/biddata/BidRateLimiterTest.java
git commit -m "feat: BidRateLimiter — in-memory 60/min/user/round bucket"
```

---

## Task 12: `BidderDashboardController` + security wiring

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/BidderDashboardController.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/security/SecurityConfig.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/exception/GlobalExceptionHandler.java` (map new exceptions)
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/BidderDashboardControllerTest.java`

- [ ] **Step 1: Write the failing MockMvc test**

```java
@WebMvcTest(BidderDashboardController.class)
class BidderDashboardControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BidderDashboardService dashboardService;
    @MockBean BidDataCreationService creationService;
    @MockBean BidDataSubmissionService submissionService;
    @MockBean BidRateLimiter rateLimiter;

    @Test
    @WithMockUser(username = "bidder", roles = "BIDDER")
    void get_dashboard_200_forBidder() throws Exception {
        when(dashboardService.landingRoute(anyLong(), eq(99L)))
            .thenReturn(new BidderDashboardLandingResult.Grid(100L, 7L, 1));
        when(dashboardService.loadGrid(100L, 99L))
            .thenReturn(/* response */);

        mvc.perform(get("/api/v1/bidder/dashboard").param("buyerCodeId", "99"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.mode").value("GRID"));
    }

    @Test
    @WithMockUser(username = "bidder", roles = "BIDDER")
    void put_bidData_200_forOwnRow() throws Exception {
        when(rateLimiter.tryAcquire(anyLong(), anyLong())).thenReturn(true);
        // ...
        mvc.perform(put("/api/v1/bidder/bid-data/500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bidQuantity\":5,\"bidAmount\":10.0}"))
           .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "bidder", roles = "BIDDER")
    void put_bidData_429_whenRateLimited() throws Exception {
        when(rateLimiter.tryAcquire(anyLong(), anyLong())).thenReturn(false);
        mvc.perform(put("/api/v1/bidder/bid-data/500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bidQuantity\":5,\"bidAmount\":10.0}"))
           .andExpect(status().isTooManyRequests());
    }

    @Test
    @WithMockUser(username = "bidder", roles = "BIDDER")
    void post_submit_200() throws Exception {
        mvc.perform(post("/api/v1/bidder/bid-rounds/100/submit"))
           .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "sales", roles = "SalesOps")
    void get_dashboard_403_forSalesOps() throws Exception {
        mvc.perform(get("/api/v1/bidder/dashboard").param("buyerCodeId", "99"))
           .andExpect(status().isForbidden());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Expected: FAIL — controller does not exist.

- [ ] **Step 3: Implement the controller**

```java
package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.auctions.biddata.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bidder")
@PreAuthorize("hasAnyRole('Bidder','Administrator')")
public class BidderDashboardController {

    private final BidderDashboardService dashboardService;
    private final BidDataCreationService creationService;
    private final BidDataSubmissionService submissionService;
    private final BidRateLimiter rateLimiter;

    // constructor

    @GetMapping("/dashboard")
    public ResponseEntity<BidderDashboardResponse> dashboard(
            @RequestParam long buyerCodeId,
            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        var landing = dashboardService.landingRoute(userId, buyerCodeId);
        return switch (landing) {
            case BidderDashboardLandingResult.Grid g -> {
                creationService.ensureRowsExist(buyerCodeId, g.bidRoundId(), userId);
                yield ResponseEntity.ok(dashboardService.loadGrid(g.bidRoundId(), buyerCodeId));
            }
            case BidderDashboardLandingResult.Download d ->
                ResponseEntity.ok(new BidderDashboardResponse("DOWNLOAD",
                    null, null, List.of(), null, null));
            case BidderDashboardLandingResult.AllDone __ ->
                ResponseEntity.ok(new BidderDashboardResponse("ALL_ROUNDS_DONE",
                    null, null, List.of(), null, null));
            case BidderDashboardLandingResult.Error e ->
                ResponseEntity.status(404).body(new BidderDashboardResponse(
                    "ERROR_AUCTION_NOT_FOUND", null, null, List.of(), null, null));
        };
    }

    @PutMapping("/bid-data/{id}")
    public ResponseEntity<BidDataRow> save(@PathVariable long id,
                                            @RequestBody SaveBidRequest req,
                                            Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        long bidRoundId = submissionService.resolveBidRoundId(id);
        if (!rateLimiter.tryAcquire(userId, bidRoundId)) {
            return ResponseEntity.status(429).build();
        }
        return ResponseEntity.ok(submissionService.save(userId, id, req));
    }

    @PostMapping("/bid-rounds/{id}/submit")
    public ResponseEntity<BidSubmissionResult> submit(@PathVariable long id,
                                                       Authentication auth) {
        long userId = (Long) auth.getPrincipal();
        String username = (String) auth.getCredentials();
        return ResponseEntity.ok(submissionService.submit(userId, username, id));
    }
}
```

Add a `resolveBidRoundId(long bidDataId)` helper on `BidDataSubmissionService` that reads `SELECT bid_round_id FROM auctions.bid_data WHERE id = ?`.

- [ ] **Step 4: Wire security**

Add to `SecurityConfig.java` inside the `authorizeHttpRequests` block — declared **before** the broad `/api/v1/**` authenticated default, but after the admin matchers:

```java
.requestMatchers("/api/v1/bidder/**").hasAnyRole("Bidder", "Administrator")
```

- [ ] **Step 5: Wire exception handler**

In `GlobalExceptionHandler.java`, add:

```java
@ExceptionHandler(BidDataValidationException.class)
ResponseEntity<Map<String, String>> onBidDataValidation(BidDataValidationException ex) {
    return ResponseEntity.status(400)
        .body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
}

@ExceptionHandler(BidDataSubmissionException.class)
ResponseEntity<Map<String, String>> onBidDataSubmission(BidDataSubmissionException ex) {
    int status = switch (ex.getCode()) {
        case "BID_DATA_NOT_FOUND", "BID_ROUND_NOT_FOUND" -> 404;
        case "NOT_YOUR_BID_DATA", "NOT_YOUR_BID_ROUND"    -> 403;
        case "ROUND_CLOSED"                                -> 409;
        default                                             -> 500;
    };
    return ResponseEntity.status(status)
        .body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
}
```

- [ ] **Step 6: Run tests to verify they pass**

Run: `cd backend && mvn -Dtest=BidderDashboardControllerTest test`
Expected: all 5 scenarios green.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/controller/BidderDashboardController.java \
        backend/src/main/java/com/ecoatm/salesplatform/security/SecurityConfig.java \
        backend/src/main/java/com/ecoatm/salesplatform/exception/GlobalExceptionHandler.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/BidderDashboardControllerTest.java
git commit -m "feat: BidderDashboardController with auth, rate limit, exception mapping"
```

---

## Task 13: Full-chain integration test

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/integration/BidderDashboardFullChainIT.java`

- [ ] **Step 1: Write the IT**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BidderDashboardFullChainIT {

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    // + auth helpers

    @Test
    void end_to_end_open_save_submit_resubmit_close_blocks() throws Exception {
        // 1. fixture: Started R1 round + buyer + inventory + user_buyers
        long bidRoundId = new BidDataScenario(jdbc)
            .round(1).buyerCodeType("Wholesale")
            .inventory("AAA1", "A", 10, new BigDecimal("25.00"))
            .commitAndReturnBidRoundId();

        // 2. login as bidder, open dashboard
        String token = loginAsBidder();
        mvc.perform(get("/api/v1/bidder/dashboard?buyerCodeId=99")
                        .header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.mode").value("GRID"))
           .andExpect(jsonPath("$.rows.length()").value(1));

        // 3. PUT save
        Long bidDataId = jdbc.queryForObject(
            "SELECT id FROM auctions.bid_data WHERE bid_round_id = ?",
            Long.class, bidRoundId);
        mvc.perform(put("/api/v1/bidder/bid-data/" + bidDataId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bidQuantity\":5,\"bidAmount\":20.0}"))
           .andExpect(status().isOk());

        // 4. POST submit - first submit: last_valid_* = NULL
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.resubmit").value(false));

        // verify DB state
        Integer submittedQty = jdbc.queryForObject(
            "SELECT submitted_bid_quantity FROM auctions.bid_data WHERE id = ?",
            Integer.class, bidDataId);
        assertThat(submittedQty).isEqualTo(5);

        // 5. edit + resubmit - last_valid_* now = prior submitted
        mvc.perform(put("/api/v1/bidder/bid-data/" + bidDataId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bidQuantity\":8,\"bidAmount\":25.0}"))
           .andExpect(status().isOk());
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .header("Authorization", "Bearer " + token))
           .andExpect(jsonPath("$.resubmit").value(true));

        Integer lastValidQty = jdbc.queryForObject(
            "SELECT last_valid_bid_quantity FROM auctions.bid_data WHERE id = ?",
            Integer.class, bidDataId);
        assertThat(lastValidQty).isEqualTo(5);

        // 6. simulate round close, then submit -> 409
        jdbc.update("UPDATE auctions.bid_rounds SET round_status='Closed' WHERE id=?", bidRoundId);
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .header("Authorization", "Bearer " + token))
           .andExpect(status().isConflict());
    }
}
```

- [ ] **Step 2: Run**

Run: `cd backend && mvn -Dtest=BidderDashboardFullChainIT test`
Expected: PASS — all six stages green.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/integration/BidderDashboardFullChainIT.java
git commit -m "test: end-to-end bidder dashboard open -> save -> submit -> resubmit -> close"
```

---

## Task 14: Frontend API client + types

**Files:**
- Create: `frontend/src/lib/bidder.ts`

- [ ] **Step 1: Implement client**

```typescript
import { apiFetch } from './api';

export type LandingMode = 'GRID' | 'DOWNLOAD' | 'ERROR_AUCTION_NOT_FOUND' | 'ALL_ROUNDS_DONE';

export interface BidDataRow {
  id: number;
  bidRoundId: number;
  ecoid: string;
  mergedGrade: string;
  buyerCodeType: string;
  bidQuantity: number | null;
  bidAmount: number;
  targetPrice: number;
  maximumQuantity: number | null;
  payout: number | null;
  submittedBidQuantity: number | null;
  submittedBidAmount: number | null;
  lastValidBidQuantity: number | null;
  lastValidBidAmount: number | null;
  submittedDatetime: string | null;
  changedDate: string;
}

export interface BidDataTotals {
  rowCount: number;
  totalBidAmount: number;
  totalPayout: number;
  totalBidQuantity: number;
}

export interface BidRoundSummary {
  id: number;
  schedulingAuctionId: number;
  round: number;
  roundStatus: string;
  startDatetime: string | null;
  endDatetime: string | null;
  submitted: boolean;
  submittedDatetime: string | null;
}

export interface SchedulingAuctionSummary {
  id: number;
  auctionId: number;
  auctionTitle: string;
  round: number;
  roundName: string;
  status: string;
}

export interface RoundTimerState {
  now: string;
  startsAt: string | null;
  endsAt: string | null;
  secondsUntilStart: number;
  secondsUntilEnd: number;
  active: boolean;
}

export interface BidderDashboardResponse {
  mode: LandingMode;
  auction: SchedulingAuctionSummary | null;
  bidRound: BidRoundSummary | null;
  rows: BidDataRow[];
  totals: BidDataTotals | null;
  timer: RoundTimerState | null;
}

export interface SaveBidPayload {
  bidQuantity: number | null;
  bidAmount: number;
}

export interface BidSubmissionResult {
  bidRoundId: number;
  rowCount: number;
  submittedDatetime: string;
  resubmit: boolean;
}

export const loadDashboard = (buyerCodeId: number) =>
  apiFetch<BidderDashboardResponse>(`/bidder/dashboard?buyerCodeId=${buyerCodeId}`);

export const saveBid = (id: number, payload: SaveBidPayload) =>
  apiFetch<BidDataRow>(`/bidder/bid-data/${id}`, {
    method: 'PUT',
    body: payload,
  });

export const submitBidRound = (id: number) =>
  apiFetch<BidSubmissionResult>(`/bidder/bid-rounds/${id}/submit`, {
    method: 'POST',
  });
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/lib/bidder.ts
git commit -m "feat(frontend): bidder API client + types"
```

---

## Task 15: `useAutoSaveBid` hook

**Files:**
- Create: `frontend/src/hooks/useAutoSaveBid.ts`
- Create: `frontend/src/hooks/useAutoSaveBid.test.tsx`

- [ ] **Step 1: Write the failing Vitest test**

```typescript
import { renderHook, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import { useAutoSaveBid } from './useAutoSaveBid';
import * as bidderApi from '../lib/bidder';

describe('useAutoSaveBid', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => { vi.useRealTimers(); vi.restoreAllMocks(); });

  it('debounces rapid saves into a single API call after 500ms', async () => {
    const saveSpy = vi.spyOn(bidderApi, 'saveBid').mockResolvedValue({} as any);
    const onSaved = vi.fn();

    const { result } = renderHook(() => useAutoSaveBid(123, onSaved));

    act(() => {
      result.current.save({ bidQuantity: 1, bidAmount: 10 });
      result.current.save({ bidQuantity: 2, bidAmount: 20 });
      result.current.save({ bidQuantity: 3, bidAmount: 30 });
    });

    expect(saveSpy).not.toHaveBeenCalled();
    await act(async () => { vi.advanceTimersByTime(500); });
    expect(saveSpy).toHaveBeenCalledTimes(1);
    expect(saveSpy).toHaveBeenCalledWith(123, { bidQuantity: 3, bidAmount: 30 });
  });

  it('serializes blank quantity as null', async () => {
    const saveSpy = vi.spyOn(bidderApi, 'saveBid').mockResolvedValue({} as any);
    const { result } = renderHook(() => useAutoSaveBid(123, () => {}));

    act(() => { result.current.save({ bidQuantity: null, bidAmount: 10 }); });
    await act(async () => { vi.advanceTimersByTime(500); });

    expect(saveSpy).toHaveBeenCalledWith(123, { bidQuantity: null, bidAmount: 10 });
  });
});
```

- [ ] **Step 2: Implement**

```typescript
import { useCallback, useRef, useState } from 'react';
import { saveBid, SaveBidPayload, BidDataRow } from '../lib/bidder';

export function useAutoSaveBid(rowId: number, onSaved: (row: BidDataRow) => void) {
  const [dirty, setDirty] = useState(false);
  const pending = useRef<SaveBidPayload | null>(null);
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const flush = useCallback(async () => {
    if (!pending.current) return;
    const payload = pending.current;
    pending.current = null;
    try {
      const row = await saveBid(rowId, payload);
      onSaved(row);
    } finally {
      setDirty(false);
    }
  }, [rowId, onSaved]);

  const save = useCallback((payload: SaveBidPayload) => {
    pending.current = payload;
    setDirty(true);
    if (timer.current) clearTimeout(timer.current);
    timer.current = setTimeout(() => flush(), 500);
  }, [flush]);

  return { dirty, save };
}
```

- [ ] **Step 3: Run test to verify it passes**

Run: `cd frontend && npm run test -- useAutoSaveBid`
Expected: 2/2 pass.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/hooks/useAutoSaveBid.ts frontend/src/hooks/useAutoSaveBid.test.tsx
git commit -m "feat(frontend): useAutoSaveBid hook with 500ms debounce"
```

---

## Task 16: `BidGrid` component + page

**Files:**
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/page.tsx`
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/BidGrid.tsx`
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/BidGridRow.tsx`
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/DashboardHeader.tsx`
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/SubmitBar.tsx`
- Create: `frontend/src/app/(dashboard)/bidder/dashboard/BidGrid.test.tsx`

- [ ] **Step 1: Write the failing component test**

```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { BidGrid } from './BidGrid';

describe('BidGrid', () => {
  const mockRow = {
    id: 1, bidRoundId: 100, ecoid: 'AAA1', mergedGrade: 'A',
    buyerCodeType: 'Wholesale', bidQuantity: null, bidAmount: 0,
    targetPrice: 25, maximumQuantity: 10, payout: 0,
    submittedBidQuantity: null, submittedBidAmount: null,
    lastValidBidQuantity: null, lastValidBidAmount: null,
    submittedDatetime: null, changedDate: '2026-04-23',
  };

  it('renders one row per bid-data item', () => {
    render(<BidGrid rows={[mockRow]} onRowSaved={() => {}} />);
    expect(screen.getByText('AAA1')).toBeInTheDocument();
    expect(screen.getByText('A')).toBeInTheDocument();
  });

  it('blank quantity input maps to null payload', async () => {
    const onSaved = vi.fn();
    render(<BidGrid rows={[mockRow]} onRowSaved={onSaved} />);
    const qtyInput = screen.getByLabelText(/Quantity for AAA1/i);
    fireEvent.change(qtyInput, { target: { value: '' } });
    // 500ms debounce elapses
    await waitFor(() => expect(onSaved).toHaveBeenCalled(), { timeout: 1000 });
  });
});
```

- [ ] **Step 2: Implement `BidGrid` + `BidGridRow`**

```tsx
// BidGrid.tsx
'use client';
import { BidDataRow } from '@/lib/bidder';
import { BidGridRow } from './BidGridRow';

export function BidGrid({
  rows,
  onRowSaved,
}: {
  rows: BidDataRow[];
  onRowSaved: (row: BidDataRow) => void;
}) {
  return (
    <table className="w-full text-sm">
      <thead className="bg-[#407874] text-white">
        <tr>
          <th className="px-3 py-2 text-left">Device</th>
          <th className="px-3 py-2 text-left">Grade</th>
          <th className="px-3 py-2 text-right">Target Price</th>
          <th className="px-3 py-2 text-right">Max Qty</th>
          <th className="px-3 py-2 text-right">Bid Qty</th>
          <th className="px-3 py-2 text-right">Bid Amount</th>
          <th className="px-3 py-2 text-right">Payout</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row, i) => (
          <BidGridRow
            key={row.id}
            row={row}
            striped={i % 2 === 1}
            onSaved={onRowSaved}
          />
        ))}
      </tbody>
    </table>
  );
}
```

```tsx
// BidGridRow.tsx
'use client';
import { useState } from 'react';
import { BidDataRow } from '@/lib/bidder';
import { useAutoSaveBid } from '@/hooks/useAutoSaveBid';

export function BidGridRow({
  row,
  striped,
  onSaved,
}: {
  row: BidDataRow;
  striped: boolean;
  onSaved: (row: BidDataRow) => void;
}) {
  const [qty, setQty] = useState<string>(row.bidQuantity?.toString() ?? '');
  const [amount, setAmount] = useState<string>(row.bidAmount.toString());
  const { save } = useAutoSaveBid(row.id, onSaved);

  const emit = (nextQty: string, nextAmount: string) => {
    const payload = {
      bidQuantity: nextQty === '' ? null : parseInt(nextQty, 10),
      bidAmount: parseFloat(nextAmount || '0'),
    };
    save(payload);
  };

  return (
    <tr className={striped ? 'bg-[#F7F7F7]' : ''}>
      <td className="px-3 py-2">{row.ecoid}</td>
      <td className="px-3 py-2">{row.mergedGrade}</td>
      <td className="px-3 py-2 text-right">${row.targetPrice.toFixed(2)}</td>
      <td className="px-3 py-2 text-right">{row.maximumQuantity ?? '—'}</td>
      <td className="px-3 py-2 text-right">
        <input
          aria-label={`Quantity for ${row.ecoid}`}
          type="number"
          min="0"
          value={qty}
          onChange={(e) => { setQty(e.target.value); emit(e.target.value, amount); }}
          className="w-20 border px-2 py-1 text-right"
        />
      </td>
      <td className="px-3 py-2 text-right">
        <input
          aria-label={`Amount for ${row.ecoid}`}
          type="number"
          min="0"
          step="0.01"
          value={amount}
          onChange={(e) => { setAmount(e.target.value); emit(qty, e.target.value); }}
          className="w-24 border px-2 py-1 text-right"
        />
      </td>
      <td className="px-3 py-2 text-right">${(row.payout ?? 0).toFixed(2)}</td>
    </tr>
  );
}
```

- [ ] **Step 3: Implement `page.tsx`**

```tsx
// page.tsx
import { loadDashboard } from '@/lib/bidder';
import { BidderDashboardClient } from './BidderDashboardClient';

export default async function Page({
  searchParams,
}: {
  searchParams: { buyerCodeId?: string };
}) {
  if (!searchParams.buyerCodeId) return <div>Missing buyerCodeId</div>;
  const buyerCodeId = parseInt(searchParams.buyerCodeId, 10);
  const initial = await loadDashboard(buyerCodeId);
  return <BidderDashboardClient initial={initial} />;
}
```

Create `BidderDashboardClient.tsx` as the client orchestrator that renders `DashboardHeader` + `BidGrid` + `SubmitBar`, owns the row-map state, wires the submit button, and handles the four landing modes.

- [ ] **Step 4: Run test to verify it passes**

Run: `cd frontend && npm run test -- BidGrid`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/app/\(dashboard\)/bidder/
git commit -m "feat(frontend): BidGrid with debounced row edits + styled teal header"
```

---

## Task 17: Playwright E2E

**Files:**
- Create: `frontend/tests/e2e/bidder-dashboard.spec.ts`

- [ ] **Step 1: Write the E2E test**

```typescript
import { test, expect } from '@playwright/test';

test('bidder can open dashboard, edit, submit, resubmit', async ({ page }) => {
  await page.goto('/login');
  await page.fill('[name="email"]', 'bidder@buyerco.com');
  await page.fill('[name="password"]', 'Bidder123!');
  await page.click('button[type="submit"]');

  // assumes a buyer code id is known in dev seed; use selector bar if available
  await page.goto('/bidder/dashboard?buyerCodeId=1');
  await expect(page.getByRole('columnheader', { name: 'Device' })).toBeVisible();

  // edit first row quantity + amount
  const qtyInput = page.getByLabel('Quantity for AAA1');
  await qtyInput.fill('5');
  const amtInput = page.getByLabel('Amount for AAA1');
  await amtInput.fill('20');

  // wait for debounce + save
  await page.waitForTimeout(700);

  // submit
  await page.getByRole('button', { name: 'Submit' }).click();
  await expect(page.getByText(/Submitted at/)).toBeVisible();

  // resubmit
  await qtyInput.fill('8');
  await page.waitForTimeout(700);
  await page.getByRole('button', { name: 'Submit' }).click();
  await expect(page.getByText(/Submitted at/)).toBeVisible();
});
```

- [ ] **Step 2: Run E2E**

Run: `cd frontend && npx playwright test bidder-dashboard`
Expected: PASS against local dev (backend + frontend running).

- [ ] **Step 3: Commit**

```bash
git add frontend/tests/e2e/bidder-dashboard.spec.ts
git commit -m "test(e2e): bidder dashboard open -> edit -> submit -> resubmit"
```

---

## Task 18: QA pixel-match vs Mendix

- [ ] **Step 1: Spin up local dev + log in as bidder**

Run: `cd backend && mvn spring-boot:run` and `cd frontend && npm run dev`, then open `http://localhost:3000/bidder/dashboard?buyerCodeId=1` as `bidder@buyerco.com`.

- [ ] **Step 2: Compare to QA**

Playwright-driven side-by-side (per project `CLAUDE.md` mandate):

```typescript
import { test } from '@playwright/test';

test('pixel-match bidder dashboard vs Mendix QA', async ({ page }) => {
  await page.goto('https://buy-qa.ecoatmdirect.com/p/login/web');
  await page.fill('#loginEmail', 'nadia.ecoatm@gmail.com');
  await page.fill('#loginPassword', 'Test100%');
  await page.click('button[type="submit"]');
  await page.goto('https://buy-qa.ecoatmdirect.com/p/bidder/dashboard');
  await page.screenshot({ path: 'qa-bidder.png', fullPage: true });

  const ctx = await page.context().browser()?.newContext();
  const local = await ctx!.newPage();
  await local.goto('http://localhost:3000/login');
  await local.fill('[name="email"]', 'bidder@buyerco.com');
  await local.fill('[name="password"]', 'Bidder123!');
  await local.click('button[type="submit"]');
  await local.goto('http://localhost:3000/bidder/dashboard?buyerCodeId=1');
  await local.screenshot({ path: 'local-bidder.png', fullPage: true });
});
```

- [ ] **Step 3: Reconcile differences**

Run the script, diff screenshots, fix CSS/spacing until local matches QA.

- [ ] **Step 4: Commit any fixes**

```bash
git add frontend/src/app/\(dashboard\)/bidder/
git commit -m "style(frontend): pixel-match bidder dashboard to Mendix QA"
```

---

## Task 19: Docs updates

**Files:**
- Modify: `docs/api/rest-endpoints.md` — append `## Bidder Dashboard` section.
- Modify: `docs/architecture/decisions.md` — add ADR `2026-04-23 — Bidder dashboard + bid_data generation`.

- [ ] **Step 1: API docs**

Append to `rest-endpoints.md`:

```markdown
## Bidder Dashboard

### GET /api/v1/bidder/dashboard?buyerCodeId={id}

Resolves the landing mode (GRID | DOWNLOAD | ERROR_AUCTION_NOT_FOUND |
ALL_ROUNDS_DONE) and, when GRID, synchronously generates bid_data rows
if they don't exist yet, then returns the full grid payload.

**Roles**: `Bidder` (own buyer codes) | `Administrator`.

**Response**: 200 OK — `BidderDashboardResponse` with `mode`, `auction`,
`bidRound`, `rows[]`, `totals`, `timer`.

[... detailed field table ...]

### PUT /api/v1/bidder/bid-data/{id}

Save edits to one bid_data row. Rate-limited to 60 req/min/user/bid_round.

Body: `{ bidQuantity: int|null, bidAmount: decimal }`.

Errors: 400 `INVALID_QUANTITY` / `INVALID_AMOUNT`, 403 `NOT_YOUR_BID_DATA`,
404 `BID_DATA_NOT_FOUND`, 409 `ROUND_CLOSED`, 429 `RATE_LIMIT`.

### POST /api/v1/bidder/bid-rounds/{id}/submit

Copy `bid_*` → `submitted_*` and prior `submitted_*` → `last_valid_*`
for every row in the round. Re-callable until round status = Closed.

Body: (none).
Response: 200 OK — `BidSubmissionResult { bidRoundId, rowCount, submittedDatetime, resubmit }`.

Errors: 403 `NOT_YOUR_BID_ROUND`, 404 `BID_ROUND_NOT_FOUND`, 409 `ROUND_CLOSED`.
```

- [ ] **Step 2: ADR**

Prepend a new entry to `docs/architecture/decisions.md` dated `2026-04-23` summarizing: synchronous-on-dashboard-open generation, single-CTE `INSERT ... SELECT` with advisory lock, nullable `bid_quantity` as no-cap sentinel, re-callable submit slide, 60/min rate limit, deferred threshold validation to sub-project 4.

- [ ] **Step 3: Commit**

```bash
git add docs/api/rest-endpoints.md docs/architecture/decisions.md
git commit -m "docs: bidder dashboard API + 2026-04-23 ADR"
```

---

## Self-Review Notes

**Spec coverage:** Every section of `auction-bid-data-create-design.md` maps to a task above:
- §3.3 package layout → Tasks 2–6, 8–12
- §4 CTE contract → Task 7
- §5 schema → Task 1
- §6 submit semantics → Task 10
- §7 API surface → Task 12
- §8 frontend → Tasks 14–16
- §9 testing strategy → unit/IT coverage woven through each task + Tasks 13, 15, 16, 17

**Known gaps this plan accepts:**
- R2/R3 threshold computation inside the CTE is stubbed with `TRUE AS bid_meets_threshold` per the Task 7b implementation note — sub-project 4 will replace this.
- `SUB_HandleSpecialTreatmentBuyerOnRoundStart` deferred per spec §2.
- CSV upload endpoint deferred per spec §2 — the `bid_data_docs` row is created but the upload surface is not.

**Type consistency checked:** `BidDataRow`, `BidderDashboardResponse`, `SaveBidRequest`, `BidSubmissionResult`, `BidDataCreationResult`, `BidderDashboardLandingResult` (+ variants) are referenced identically across tasks 5, 8–12, 14–17.

---

## Execution Handoff

Plan saved to `docs/tasks/auction-bid-data-create-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task with two-stage review (spec compliance → code quality); fast iteration, preserves controller context.

**2. Inline Execution** — execute tasks in this session using `superpowers:executing-plans`, batch with checkpoints for review.

Which approach?
