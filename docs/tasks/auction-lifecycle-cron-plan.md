# Auction Lifecycle Cron Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port `AuctionUI.ACT_ScheduleAuctionCheckStatus` (Mendix 1-minute scheduled event) to a multi-instance-safe Spring Boot cron that transitions auction rounds (`Scheduled→Started`, `Started→Closed`), reconciles parent `Auction.status`, emits per-round events for downstream listeners, and records every tick in a reusable `infra.scheduled_job_run` audit table. Six stub listeners define the contract that sub-projects 1-6 will replace with real implementations.

**Architecture:** ShedLock (`infra.shedlock`) gives single-leader execution across JVMs; the orchestrator (`AuctionLifecycleService`) opens no transaction and delegates each row transition to `RoundTransitionService` running in `REQUIRES_NEW` with a pessimistic lock; `AuctionStatusReconciler` (also `REQUIRES_NEW`) updates parent status per affected auction; `ScheduledJobRunRecorder` writes one audit row per tick. Two event records (`RoundStartedEvent`, `RoundClosedEvent`) fire post-commit and feed six stub listeners.

**Tech Stack:** Java 21, Spring Boot 3.2.4, Spring Data JPA, Postgres 15, Flyway, ShedLock 5.13.0, JUnit 5, AssertJ, Mockito, Testcontainers (Postgres).

**Spec:** `docs/tasks/auction-lifecycle-cron-design.md` (read before starting).

---

## File structure

**New files (24):**

```
backend/src/main/resources/db/migration/
├─ V70__infra_schema_and_scheduled_job_run.sql
└─ V71__infra_shedlock.sql

backend/src/main/java/com/ecoatm/salesplatform/
├─ config/
│  └─ SchedulingConfig.java                        # @EnableScheduling + LockProvider
├─ infra/scheduledjob/
│  ├─ ScheduledJobRunStatus.java                   # enum
│  ├─ ScheduledJobRun.java                         # entity
│  ├─ ScheduledJobRunRepository.java
│  └─ ScheduledJobRunRecorder.java                 # begin/end + counters
├─ event/
│  ├─ RoundStartedEvent.java                       # record
│  └─ RoundClosedEvent.java                        # record
└─ service/auctions/lifecycle/
   ├─ TickCounters.java                            # mutable counter bag
   ├─ TickResult.java                              # record(counters)
   ├─ RoundTransitionService.java                  # REQUIRES_NEW, FOR UPDATE
   ├─ RoundAlreadyTransitionedException.java
   ├─ AuctionStatusReconciler.java                 # REQUIRES_NEW
   ├─ AuctionLifecycleService.java                 # tick() — no tx
   ├─ AuctionLifecycleScheduler.java               # @Scheduled + @SchedulerLock
   └─ stub/
      ├─ R1InitStubListener.java
      ├─ R2InitStubListener.java
      ├─ R3InitStubListener.java
      ├─ BidRankingStubListener.java
      ├─ R3PreProcessStubListener.java
      └─ SnowflakePushStubListener.java

backend/src/test/java/com/ecoatm/salesplatform/
├─ infra/scheduledjob/
│  └─ ScheduledJobRunRecorderTest.java
├─ service/auctions/lifecycle/
│  ├─ RoundTransitionServiceTest.java
│  ├─ AuctionStatusReconcilerTest.java
│  ├─ AuctionLifecycleServiceTest.java
│  ├─ AuctionLifecycleIT.java                      # @SpringBootTest + Testcontainers
│  ├─ AuctionLifecycleSchedulerIT.java             # ShedLock test
│  └─ stub/
│     ├─ R1InitStubListenerTest.java
│     ├─ R2InitStubListenerTest.java
│     ├─ R3InitStubListenerTest.java
│     ├─ BidRankingStubListenerTest.java
│     ├─ R3PreProcessStubListenerTest.java
│     └─ SnowflakePushStubListenerTest.java
```

**Modified files (5):**

- `backend/pom.xml` — add 2 ShedLock dependencies.
- `backend/src/main/resources/application.yml` — add `auctions.lifecycle.*` block.
- `backend/src/test/resources/application-test.yml` — disable lifecycle.
- `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java` — add 2 query methods.
- `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AuctionRepository.java` — add `findByIdForUpdate`.

**Documentation files updated (4):**

- `docs/architecture/decisions.md` — append new ADR.
- `docs/app-metadata/scheduled-events.md` — new entry (file may need creation).
- `docs/business-logic/auction-flow.md` — short subsection.
- `docs/api/rest-endpoints.md` — note that no new endpoints are added.

---

## Task 1: Flyway migrations and ShedLock dependencies

**Files:**
- Create: `backend/src/main/resources/db/migration/V70__infra_schema_and_scheduled_job_run.sql`
- Create: `backend/src/main/resources/db/migration/V71__infra_shedlock.sql`
- Modify: `backend/pom.xml`

- [ ] **Step 1.1: Add ShedLock dependencies to `backend/pom.xml`**

Insert after the `spring-aspects` dependency (around line 78):

```xml
<!-- ShedLock: multi-instance leader election for @Scheduled jobs -->
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>5.13.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>5.13.0</version>
</dependency>
```

- [ ] **Step 1.2: Add `infra` schema to Flyway plugin schemas list in `backend/pom.xml`**

Modify the `<schemas>` block (around line 211). Add `<schema>infra</schema>`:

```xml
<schemas>
    <schema>identity</schema>
    <schema>user_mgmt</schema>
    <schema>buyer_mgmt</schema>
    <schema>sso</schema>
    <schema>pws</schema>
    <schema>mdm</schema>
    <schema>integration</schema>
    <schema>infra</schema>
</schemas>
```

- [ ] **Step 1.3: Create `V70__infra_schema_and_scheduled_job_run.sql`**

```sql
-- Infra schema for platform-level cross-cutting tables (job audit, locks, etc.)
CREATE SCHEMA IF NOT EXISTS infra;

CREATE TABLE infra.scheduled_job_run (
    id              BIGSERIAL PRIMARY KEY,
    job_name        VARCHAR(80)  NOT NULL,
    started_at      TIMESTAMPTZ  NOT NULL,
    finished_at     TIMESTAMPTZ,
    status          VARCHAR(20)  NOT NULL,
    node_id         VARCHAR(120) NOT NULL,
    duration_ms     INTEGER,
    error_message   TEXT,
    counters        JSONB,
    CONSTRAINT chk_sjr_status
      CHECK (status IN ('RUNNING','OK','FAILED','SKIPPED_LOCKED'))
);

CREATE INDEX idx_sjr_job_started
    ON infra.scheduled_job_run (job_name, started_at DESC);

COMMENT ON TABLE infra.scheduled_job_run IS
  'One row per cron tick. status RUNNING → OK|FAILED|SKIPPED_LOCKED.';
COMMENT ON COLUMN infra.scheduled_job_run.counters IS
  'JSONB free-form per-job payload, e.g. {"roundsStarted":2,"roundsClosed":1,...}';
```

- [ ] **Step 1.4: Create `V71__infra_shedlock.sql`**

```sql
-- ShedLock JDBC provider table — column shape required by net.javacrumbs.shedlock 5.x
CREATE TABLE infra.shedlock (
    name        VARCHAR(64)  PRIMARY KEY,
    lock_until  TIMESTAMP    NOT NULL,
    locked_at   TIMESTAMP    NOT NULL,
    locked_by   VARCHAR(255) NOT NULL
);
```

- [ ] **Step 1.5: Verify migrations apply cleanly**

Run: `cd backend && mvn -DskipTests spring-boot:run`
Expected: Console contains `Successfully applied 2 migrations to schema "public"` and `infra` schema exists.
Then `Ctrl+C` to stop.

Verify directly: `PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev -c "\dt infra.*"`
Expected: lists `infra.scheduled_job_run` and `infra.shedlock`.

- [ ] **Step 1.6: Commit**

```bash
git add backend/pom.xml backend/src/main/resources/db/migration/V70__infra_schema_and_scheduled_job_run.sql backend/src/main/resources/db/migration/V71__infra_shedlock.sql
git commit -m "feat: add infra schema with scheduled_job_run + shedlock tables"
```

---

## Task 2: `ScheduledJobRunStatus` enum, entity, and repository

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunStatus.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRun.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunRepository.java`

- [ ] **Step 2.1: Create the status enum**

```java
package com.ecoatm.salesplatform.infra.scheduledjob;

public enum ScheduledJobRunStatus {
    RUNNING,
    OK,
    FAILED,
    SKIPPED_LOCKED
}
```

- [ ] **Step 2.2: Create the `ScheduledJobRun` entity**

```java
package com.ecoatm.salesplatform.infra.scheduledjob;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "scheduled_job_run", schema = "infra")
public class ScheduledJobRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 80)
    private String jobName;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ScheduledJobRunStatus status;

    @Column(name = "node_id", nullable = false, length = 120)
    private String nodeId;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "counters", columnDefinition = "jsonb")
    private Map<String, Object> counters;

    public Long getId() { return id; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }

    public ScheduledJobRunStatus getStatus() { return status; }
    public void setStatus(ScheduledJobRunStatus status) { this.status = status; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Map<String, Object> getCounters() { return counters; }
    public void setCounters(Map<String, Object> counters) { this.counters = counters; }
}
```

- [ ] **Step 2.3: Create the repository**

```java
package com.ecoatm.salesplatform.infra.scheduledjob;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledJobRunRepository extends JpaRepository<ScheduledJobRun, Long> {
}
```

- [ ] **Step 2.4: Verify compilation**

Run: `cd backend && mvn -DskipTests compile`
Expected: `BUILD SUCCESS`.

- [ ] **Step 2.5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/
git commit -m "feat: add ScheduledJobRun entity, status enum, and repository"
```

---

## Task 3: `ScheduledJobRunRecorder` (TDD)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunRecorder.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunRecorderTest.java`

- [ ] **Step 3.1: Write the failing test**

```java
package com.ecoatm.salesplatform.infra.scheduledjob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledJobRunRecorderTest {

    @Mock
    private ScheduledJobRunRepository repository;

    private ScheduledJobRunRecorder recorder;

    @BeforeEach
    void setUp() {
        Clock fixed = Clock.fixed(Instant.parse("2026-04-20T12:00:00Z"), ZoneOffset.UTC);
        recorder = new ScheduledJobRunRecorder(repository, fixed);
    }

    @Test
    void begin_writesRunningRowAndReturnsHandle() {
        ScheduledJobRun saved = new ScheduledJobRun();
        when(repository.save(any(ScheduledJobRun.class))).thenReturn(saved);

        ScheduledJobRunRecorder.Handle handle = recorder.begin("auctionLifecycle");

        ArgumentCaptor<ScheduledJobRun> captor = ArgumentCaptor.forClass(ScheduledJobRun.class);
        verify(repository).save(captor.capture());
        ScheduledJobRun row = captor.getValue();
        assertThat(row.getJobName()).isEqualTo("auctionLifecycle");
        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.RUNNING);
        assertThat(row.getStartedAt()).isEqualTo(Instant.parse("2026-04-20T12:00:00Z"));
        assertThat(row.getNodeId()).contains("-");   // hostname-pid
        assertThat(handle).isNotNull();
    }

    @Test
    void end_writesOkStatusAndCounters() {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setJobName("auctionLifecycle");
        row.setStartedAt(Instant.parse("2026-04-20T11:59:59Z"));
        row.setStatus(ScheduledJobRunStatus.RUNNING);
        ScheduledJobRunRecorder.Handle handle = new ScheduledJobRunRecorder.Handle(row);

        recorder.end(handle, ScheduledJobRunStatus.OK, null,
                Map.of("roundsStarted", 2, "roundsClosed", 1, "auctionsAffected", 2, "errorCount", 0));

        verify(repository).save(row);
        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.OK);
        assertThat(row.getFinishedAt()).isEqualTo(Instant.parse("2026-04-20T12:00:00Z"));
        assertThat(row.getDurationMs()).isEqualTo(1000);
        assertThat(row.getErrorMessage()).isNull();
        assertThat(row.getCounters()).containsEntry("roundsStarted", 2);
    }

    @Test
    void end_writesFailedStatusWithErrorMessage() {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setStartedAt(Instant.parse("2026-04-20T12:00:00Z"));
        ScheduledJobRunRecorder.Handle handle = new ScheduledJobRunRecorder.Handle(row);

        recorder.end(handle, ScheduledJobRunStatus.FAILED, "DB unavailable", null);

        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.FAILED);
        assertThat(row.getErrorMessage()).isEqualTo("DB unavailable");
        assertThat(row.getDurationMs()).isZero();
    }
}
```

- [ ] **Step 3.2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=ScheduledJobRunRecorderTest test`
Expected: FAIL with "ScheduledJobRunRecorder cannot be resolved".

- [ ] **Step 3.3: Write the implementation**

```java
package com.ecoatm.salesplatform.infra.scheduledjob;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Component
public class ScheduledJobRunRecorder {

    private final ScheduledJobRunRepository repository;
    private final Clock clock;
    private final String nodeId;

    public ScheduledJobRunRecorder(ScheduledJobRunRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
        this.nodeId = computeNodeId();
    }

    public Handle begin(String jobName) {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setJobName(jobName);
        row.setStartedAt(clock.instant());
        row.setStatus(ScheduledJobRunStatus.RUNNING);
        row.setNodeId(nodeId);
        repository.save(row);
        return new Handle(row);
    }

    public void end(Handle handle, ScheduledJobRunStatus status,
                    String errorMessage, Map<String, Object> counters) {
        ScheduledJobRun row = handle.row();
        Instant now = clock.instant();
        row.setFinishedAt(now);
        row.setStatus(status);
        row.setErrorMessage(errorMessage);
        row.setCounters(counters);
        row.setDurationMs((int) (now.toEpochMilli() - row.getStartedAt().toEpochMilli()));
        repository.save(row);
    }

    private static String computeNodeId() {
        String name = ManagementFactory.getRuntimeMXBean().getName(); // pid@hostname
        int at = name.indexOf('@');
        if (at < 0) return name;
        String pid = name.substring(0, at);
        String host = name.substring(at + 1);
        return host + "-" + pid;
    }

    public record Handle(ScheduledJobRun row) {}
}
```

- [ ] **Step 3.4: Add a `Clock` bean**

The recorder takes `Clock` for testability. Spring Boot does NOT auto-register one; create or extend an existing config. Check first:

Run: `grep -r "Bean.*Clock" backend/src/main/java/com/ecoatm/salesplatform/config/`
Expected: no matches (file doesn't exist yet).

Create `backend/src/main/java/com/ecoatm/salesplatform/config/ClockConfig.java`:

```java
package com.ecoatm.salesplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
```

- [ ] **Step 3.5: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=ScheduledJobRunRecorderTest test`
Expected: `Tests run: 3, Failures: 0`.

- [ ] **Step 3.6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunRecorder.java backend/src/main/java/com/ecoatm/salesplatform/config/ClockConfig.java backend/src/test/java/com/ecoatm/salesplatform/infra/scheduledjob/ScheduledJobRunRecorderTest.java
git commit -m "feat: add ScheduledJobRunRecorder with begin/end + counters"
```

---

## Task 4: Event records

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/RoundStartedEvent.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/RoundClosedEvent.java`

- [ ] **Step 4.1: Create `RoundStartedEvent`**

```java
package com.ecoatm.salesplatform.event;

/**
 * Published post-commit when a SchedulingAuction transitions
 * Scheduled → Started via the lifecycle cron.
 *
 * Listeners must register @TransactionalEventListener(phase = AFTER_COMMIT).
 * A listener throwing does NOT roll back the round transition.
 */
public record RoundStartedEvent(long roundId, int round, long auctionId, long weekId) {}
```

- [ ] **Step 4.2: Create `RoundClosedEvent`**

```java
package com.ecoatm.salesplatform.event;

/**
 * Published post-commit when a SchedulingAuction transitions
 * Started → Closed via the lifecycle cron.
 *
 * Listeners must register @TransactionalEventListener(phase = AFTER_COMMIT).
 * A listener throwing does NOT roll back the round transition.
 */
public record RoundClosedEvent(long roundId, int round, long auctionId, long weekId) {}
```

- [ ] **Step 4.3: Verify compilation**

Run: `cd backend && mvn -DskipTests compile`
Expected: `BUILD SUCCESS`.

- [ ] **Step 4.4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/event/
git commit -m "feat: add RoundStartedEvent and RoundClosedEvent records"
```

---

## Task 5: Repository extensions

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AuctionRepository.java`

- [ ] **Step 5.1: Add `findIdsToClose` and `findIdsToStart` to `SchedulingAuctionRepository`**

Open the file. Inside the interface body, before the closing brace, add:

```java
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

// ... inside interface body ...

/**
 * Lifecycle cron — Phase 1 selector. Returns ids of rounds that should
 * transition Started → Closed (end_datetime in the past).
 */
@Query("""
    select s.id from SchedulingAuction s
    where s.roundStatus = com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus.Started
      and s.endDatetime < :now
""")
List<Long> findIdsToClose(@Param("now") Instant now);

/**
 * Lifecycle cron — Phase 2 selector. Returns ids of rounds that should
 * transition Scheduled → Started (start_datetime ≤ now).
 */
@Query("""
    select s.id from SchedulingAuction s
    where s.roundStatus = com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus.Scheduled
      and s.startDatetime <= :now
""")
List<Long> findIdsToStart(@Param("now") Instant now);

/**
 * Acquire a pessimistic write lock on a single SchedulingAuction row.
 * Used by RoundTransitionService to guard the status re-check + update
 * against concurrent ticks.
 */
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select s from SchedulingAuction s where s.id = :id")
Optional<SchedulingAuction> findByIdForUpdate(@Param("id") Long id);
```

Note: the imports above must be added at the top of the file. The complete imports block becomes:

```java
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
```

- [ ] **Step 5.2: Add `findByIdForUpdate` to `AuctionRepository`**

Open `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AuctionRepository.java` and read it first to see existing imports and methods. Then add (using whatever method is missing):

```java
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

// ... inside interface body ...

/**
 * Pessimistic write lock on an Auction row. Used by AuctionStatusReconciler.
 */
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select a from Auction a where a.id = :id")
Optional<Auction> findByIdForUpdate(@Param("id") Long id);
```

If `findByIdForUpdate` already exists (was added in an earlier sprint), skip this step.

- [ ] **Step 5.3: Verify compilation**

Run: `cd backend && mvn -DskipTests compile`
Expected: `BUILD SUCCESS`.

- [ ] **Step 5.4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/SchedulingAuctionRepository.java backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AuctionRepository.java
git commit -m "feat: add lifecycle selectors and pessimistic-lock queries to auction repos"
```

---

## Task 6: `RoundTransitionService` and `RoundAlreadyTransitionedException` (TDD)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundAlreadyTransitionedException.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundTransitionService.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundTransitionServiceTest.java`

- [ ] **Step 6.1: Create the exception class**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

/**
 * Thrown when a round picked up by findIdsToClose/findIdsToStart no longer
 * matches the predicate after the FOR UPDATE re-read. Benign — indicates
 * another tick (or admin action) already transitioned the row.
 */
public class RoundAlreadyTransitionedException extends RuntimeException {
    public RoundAlreadyTransitionedException(long roundId) {
        super("Round already transitioned: id=" + roundId);
    }
}
```

- [ ] **Step 6.2: Write the failing test**

We need an entity stub plus a `Week` lookup. Check whether `WeekRepository` exists:

Run: `grep -r "interface WeekRepository" backend/src/main/java/`
Expected: a result like `repository/mdm/WeekRepository.java`. If not present, the test will need to inline the weekId resolution differently — adjust by reading the related Auction's `weekId` instead of querying Week. The plan below assumes weekId is derived from the parent `Auction.weekId` (which is the actual source of truth per the existing entity).

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoundTransitionServiceTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private AuctionRepository auctionRepo;
    @Mock private ApplicationEventPublisher eventPublisher;

    private RoundTransitionService service;

    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        service = new RoundTransitionService(schedulingRepo, auctionRepo, eventPublisher, clock);
    }

    @Test
    void closeRound_happyPath_flipsStatusAndPublishesEvent() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Started,
                /*end*/ now.minusSeconds(60), 100L);
        Auction parent = auctionFixture(100L, 999L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));
        when(auctionRepo.findById(100L)).thenReturn(Optional.of(parent));

        RoundClosedEvent event = service.closeRound(11L);

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Closed);
        assertThat(round.getChangedDate()).isEqualTo(now);
        assertThat(round.getUpdatedBy()).isEqualTo("system:lifecycle-cron");
        verify(schedulingRepo).save(round);
        assertThat(event).isEqualTo(new RoundClosedEvent(11L, 1, 100L, 999L));
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void closeRound_whenNotStarted_throwsRoundAlreadyTransitioned() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Closed,
                now.minusSeconds(60), 100L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.closeRound(11L))
                .isInstanceOf(RoundAlreadyTransitionedException.class)
                .hasMessageContaining("11");
        verify(schedulingRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void closeRound_whenEndStillFuture_throwsRoundAlreadyTransitioned() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Started,
                now.plusSeconds(60), 100L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.closeRound(11L))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
    }

    @Test
    void startRound_happyPath_flipsStatusAndPublishesEvent() {
        SchedulingAuction round = roundFixture(22L, 2, SchedulingAuctionStatus.Scheduled,
                now.plusSeconds(3600), 200L);
        round.setStartDatetime(now.minusSeconds(1));
        Auction parent = auctionFixture(200L, 555L);
        when(schedulingRepo.findByIdForUpdate(22L)).thenReturn(Optional.of(round));
        when(auctionRepo.findById(200L)).thenReturn(Optional.of(parent));

        RoundStartedEvent event = service.startRound(22L);

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Started);
        verify(schedulingRepo).save(round);
        assertThat(event).isEqualTo(new RoundStartedEvent(22L, 2, 200L, 555L));
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void startRound_whenStartFuture_throws() {
        SchedulingAuction round = roundFixture(22L, 2, SchedulingAuctionStatus.Scheduled,
                now.plusSeconds(3600), 200L);
        round.setStartDatetime(now.plusSeconds(60));
        when(schedulingRepo.findByIdForUpdate(22L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.startRound(22L))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
    }

    @Test
    void closeRound_whenRowMissing_throwsIllegalState() {
        when(schedulingRepo.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.closeRound(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99");
    }

    private SchedulingAuction roundFixture(long id, int round, SchedulingAuctionStatus status,
                                           Instant endDt, long auctionId) {
        SchedulingAuction r = new SchedulingAuction();
        // id is generated, but tests need to set it. Use reflection via a setter shortcut:
        try {
            java.lang.reflect.Field f = SchedulingAuction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(r, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        r.setRound(round);
        r.setRoundStatus(status);
        r.setEndDatetime(endDt);
        r.setStartDatetime(now.minusSeconds(7200));
        r.setAuctionId(auctionId);
        return r;
    }

    private Auction auctionFixture(long id, long weekId) {
        Auction a = new Auction();
        try {
            java.lang.reflect.Field f = Auction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(a, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        a.setWeekId(weekId);
        a.setAuctionStatus(AuctionStatus.Started);
        return a;
    }
}
```

- [ ] **Step 6.3: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=RoundTransitionServiceTest test`
Expected: FAIL with "RoundTransitionService cannot be resolved".

- [ ] **Step 6.4: Implement `RoundTransitionService`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class RoundTransitionService {

    private static final String SYSTEM_ACTOR = "system:lifecycle-cron";

    private final SchedulingAuctionRepository schedulingRepo;
    private final AuctionRepository auctionRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public RoundTransitionService(SchedulingAuctionRepository schedulingRepo,
                                  AuctionRepository auctionRepo,
                                  ApplicationEventPublisher eventPublisher,
                                  Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.auctionRepo = auctionRepo;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoundClosedEvent closeRound(long roundId) {
        SchedulingAuction round = lockRound(roundId);
        Instant now = clock.instant();
        if (round.getRoundStatus() != SchedulingAuctionStatus.Started
                || round.getEndDatetime() == null
                || !round.getEndDatetime().isBefore(now)) {
            throw new RoundAlreadyTransitionedException(roundId);
        }
        applyTransition(round, SchedulingAuctionStatus.Closed, now);
        long weekId = resolveWeekId(round.getAuctionId());
        RoundClosedEvent event = new RoundClosedEvent(round.getId(), round.getRound(),
                round.getAuctionId(), weekId);
        eventPublisher.publishEvent(event);
        return event;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoundStartedEvent startRound(long roundId) {
        SchedulingAuction round = lockRound(roundId);
        Instant now = clock.instant();
        if (round.getRoundStatus() != SchedulingAuctionStatus.Scheduled
                || round.getStartDatetime() == null
                || round.getStartDatetime().isAfter(now)) {
            throw new RoundAlreadyTransitionedException(roundId);
        }
        applyTransition(round, SchedulingAuctionStatus.Started, now);
        long weekId = resolveWeekId(round.getAuctionId());
        RoundStartedEvent event = new RoundStartedEvent(round.getId(), round.getRound(),
                round.getAuctionId(), weekId);
        eventPublisher.publishEvent(event);
        return event;
    }

    private SchedulingAuction lockRound(long roundId) {
        return schedulingRepo.findByIdForUpdate(roundId)
                .orElseThrow(() -> new IllegalStateException(
                        "Round disappeared between selector and lock: id=" + roundId));
    }

    private void applyTransition(SchedulingAuction round, SchedulingAuctionStatus newStatus, Instant now) {
        round.setRoundStatus(newStatus);
        round.setChangedDate(now);
        round.setUpdatedBy(SYSTEM_ACTOR);
        schedulingRepo.save(round);
    }

    private long resolveWeekId(Long auctionId) {
        Auction parent = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Parent auction missing: id=" + auctionId));
        Long weekId = parent.getWeekId();
        return weekId == null ? 0L : weekId;
    }
}
```

- [ ] **Step 6.5: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=RoundTransitionServiceTest test`
Expected: `Tests run: 6, Failures: 0`.

- [ ] **Step 6.6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundTransitionService.java backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundAlreadyTransitionedException.java backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/RoundTransitionServiceTest.java
git commit -m "feat: add RoundTransitionService for cron-driven round status flips"
```

---

## Task 7: `AuctionStatusReconciler` (TDD)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionStatusReconciler.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionStatusReconcilerTest.java`

- [ ] **Step 7.1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionStatusReconcilerTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private AuctionRepository auctionRepo;

    private AuctionStatusReconciler reconciler;
    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        reconciler = new AuctionStatusReconciler(schedulingRepo, auctionRepo,
                Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void reconcile_allRoundsClosed_setsAuctionClosed() {
        Auction parent = auctionFixture(100L, AuctionStatus.Started);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Closed)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Closed);
        assertThat(parent.getChangedDate()).isEqualTo(now);
        assertThat(parent.getUpdatedBy()).isEqualTo("system:lifecycle-cron");
        verify(auctionRepo).save(parent);
    }

    @Test
    void reconcile_anyRoundStarted_setsAuctionStarted() {
        Auction parent = auctionFixture(100L, AuctionStatus.Scheduled);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Started),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Started);
        verify(auctionRepo).save(parent);
    }

    @Test
    void reconcile_allScheduled_isNoOp() {
        Auction parent = auctionFixture(100L, AuctionStatus.Scheduled);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Scheduled);
        verify(auctionRepo, never()).save(any());
    }

    @Test
    void reconcile_alreadyMatchesComputedStatus_skipsWrite() {
        Auction parent = auctionFixture(100L, AuctionStatus.Started);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Started),
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        verify(auctionRepo, never()).save(any());
        assertThat(parent.getChangedDate()).isNotEqualTo(now);
    }

    @Test
    void reconcile_missingAuction_throws() {
        when(auctionRepo.findByIdForUpdate(404L)).thenReturn(Optional.empty());

        assertThat(catchThrowable(() -> reconciler.reconcile(404L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("404");
    }

    private static Throwable catchThrowable(ThrowingRunnable r) {
        try { r.run(); return null; } catch (Throwable t) { return t; }
    }
    private interface ThrowingRunnable { void run() throws Throwable; }

    private Auction auctionFixture(long id, AuctionStatus status) {
        Auction a = new Auction();
        try {
            java.lang.reflect.Field f = Auction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(a, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        a.setAuctionStatus(status);
        return a;
    }

    private SchedulingAuction roundWith(SchedulingAuctionStatus status) {
        SchedulingAuction r = new SchedulingAuction();
        r.setRoundStatus(status);
        return r;
    }
}
```

(Replace `catchThrowable` shim with `org.assertj.core.api.Assertions.catchThrowable` — drop the helper and use `assertThatThrownBy(...)` for cleaner asserts.)

- [ ] **Step 7.2: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=AuctionStatusReconcilerTest test`
Expected: FAIL with "AuctionStatusReconciler cannot be resolved".

- [ ] **Step 7.3: Implement `AuctionStatusReconciler`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
public class AuctionStatusReconciler {

    private static final String SYSTEM_ACTOR = "system:lifecycle-cron";

    private final SchedulingAuctionRepository schedulingRepo;
    private final AuctionRepository auctionRepo;
    private final Clock clock;

    public AuctionStatusReconciler(SchedulingAuctionRepository schedulingRepo,
                                   AuctionRepository auctionRepo,
                                   Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.auctionRepo = auctionRepo;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reconcile(long auctionId) {
        Auction auction = auctionRepo.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Auction missing during reconcile: id=" + auctionId));

        List<SchedulingAuction> rounds = schedulingRepo.findByAuctionIdOrderByRoundAsc(auctionId);
        AuctionStatus computed = computeAuctionStatus(rounds, auction.getAuctionStatus());

        if (computed == auction.getAuctionStatus()) {
            return;  // no-op skip
        }
        auction.setAuctionStatus(computed);
        auction.setChangedDate(clock.instant());
        auction.setUpdatedBy(SYSTEM_ACTOR);
        auctionRepo.save(auction);
    }

    /**
     * Mendix SUB_SetAuctionStatus rule:
     *   - All rounds Closed       → Closed
     *   - Any round Started       → Started
     *   - Otherwise               → leave unchanged
     */
    private AuctionStatus computeAuctionStatus(List<SchedulingAuction> rounds, AuctionStatus current) {
        if (rounds.isEmpty()) return current;
        boolean allClosed = rounds.stream()
                .allMatch(r -> r.getRoundStatus() == SchedulingAuctionStatus.Closed);
        if (allClosed) return AuctionStatus.Closed;
        boolean anyStarted = rounds.stream()
                .anyMatch(r -> r.getRoundStatus() == SchedulingAuctionStatus.Started);
        if (anyStarted) return AuctionStatus.Started;
        return current;
    }
}
```

- [ ] **Step 7.4: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=AuctionStatusReconcilerTest test`
Expected: `Tests run: 5, Failures: 0`.

- [ ] **Step 7.5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionStatusReconciler.java backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionStatusReconcilerTest.java
git commit -m "feat: add AuctionStatusReconciler for parent-status updates after round transitions"
```

---

## Task 8: `AuctionLifecycleService.tick()` (TDD)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/TickCounters.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/TickResult.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleService.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleServiceTest.java`

- [ ] **Step 8.1: Create `TickCounters` and `TickResult`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class TickCounters {
    int roundsStarted;
    int roundsClosed;
    int errorCount;
    final Set<Long> affectedAuctions = new HashSet<>();

    Map<String, Object> toJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("roundsStarted", roundsStarted);
        m.put("roundsClosed", roundsClosed);
        m.put("auctionsAffected", affectedAuctions.size());
        m.put("errorCount", errorCount);
        return m;
    }
}
```

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import java.util.Map;

public record TickResult(Map<String, Object> counters, int errorCount) {
}
```

- [ ] **Step 8.2: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionLifecycleServiceTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private RoundTransitionService roundTransitions;
    @Mock private AuctionStatusReconciler statusReconciler;

    private AuctionLifecycleService service;
    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        service = new AuctionLifecycleService(schedulingRepo, roundTransitions, statusReconciler,
                Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void tick_runsCloseThenStartThenReconcile() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of(22L));
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.startRound(22L)).thenReturn(new RoundStartedEvent(22L, 2, 100L, 999L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isEqualTo(1);
        assertThat(result.counters().get("roundsStarted")).isEqualTo(1);
        assertThat(result.counters().get("auctionsAffected")).isEqualTo(1);
        assertThat(result.errorCount()).isZero();
        verify(statusReconciler).reconcile(100L);
    }

    @Test
    void tick_swallowsPerRowExceptionAndCountsError() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L, 12L, 13L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.closeRound(12L)).thenThrow(new RuntimeException("boom"));
        when(roundTransitions.closeRound(13L)).thenReturn(new RoundClosedEvent(13L, 3, 100L, 999L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isEqualTo(2);
        assertThat(result.errorCount()).isEqualTo(1);
        verify(statusReconciler).reconcile(100L);
    }

    @Test
    void tick_alreadyTransitionedException_isBenign() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L))
                .thenThrow(new RoundAlreadyTransitionedException(11L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isZero();
        assertThat(result.errorCount()).isZero();      // benign — not counted as error
        verify(statusReconciler, never()).reconcile(anyLong());
    }

    @Test
    void tick_deduplicatesAffectedAuctions() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L, 12L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of(33L));
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.closeRound(12L)).thenReturn(new RoundClosedEvent(12L, 2, 100L, 999L));
        when(roundTransitions.startRound(33L)).thenReturn(new RoundStartedEvent(33L, 1, 200L, 888L));

        service.tick();

        verify(statusReconciler).reconcile(100L);
        verify(statusReconciler).reconcile(200L);
        verify(statusReconciler, times(2)).reconcile(anyLong());
    }

    @Test
    void tick_reconcilerFailureIsCountedNotPropagated() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        doThrow(new RuntimeException("DB down")).when(statusReconciler).reconcile(100L);

        TickResult result = service.tick();

        assertThat(result.errorCount()).isEqualTo(1);
        assertThat(result.counters().get("roundsClosed")).isEqualTo(1);
    }
}
```

- [ ] **Step 8.3: Run test to verify it fails**

Run: `cd backend && mvn -Dtest=AuctionLifecycleServiceTest test`
Expected: FAIL with "AuctionLifecycleService cannot be resolved".

- [ ] **Step 8.4: Implement `AuctionLifecycleService`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class AuctionLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(AuctionLifecycleService.class);

    private final SchedulingAuctionRepository schedulingRepo;
    private final RoundTransitionService roundTransitions;
    private final AuctionStatusReconciler statusReconciler;
    private final Clock clock;

    public AuctionLifecycleService(SchedulingAuctionRepository schedulingRepo,
                                   RoundTransitionService roundTransitions,
                                   AuctionStatusReconciler statusReconciler,
                                   Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.roundTransitions = roundTransitions;
        this.statusReconciler = statusReconciler;
        this.clock = clock;
    }

    /**
     * Single tick of the lifecycle cron. Opens NO transaction itself —
     * each per-row call uses {@code REQUIRES_NEW}.
     */
    public TickResult tick() {
        Instant now = clock.instant();
        TickCounters counters = new TickCounters();

        // Phase 1 — close Started rounds whose end time has passed
        List<Long> toClose = schedulingRepo.findIdsToClose(now);
        for (Long roundId : toClose) {
            try {
                RoundClosedEvent event = roundTransitions.closeRound(roundId);
                counters.roundsClosed++;
                counters.affectedAuctions.add(event.auctionId());
            } catch (RoundAlreadyTransitionedException e) {
                log.debug("Round {} already transitioned (close), skipping", roundId);
            } catch (Exception e) {
                log.error("Failed to close round {}", roundId, e);
                counters.errorCount++;
            }
        }

        // Phase 2 — start Scheduled rounds whose start time has arrived
        List<Long> toStart = schedulingRepo.findIdsToStart(now);
        for (Long roundId : toStart) {
            try {
                RoundStartedEvent event = roundTransitions.startRound(roundId);
                counters.roundsStarted++;
                counters.affectedAuctions.add(event.auctionId());
            } catch (RoundAlreadyTransitionedException e) {
                log.debug("Round {} already transitioned (start), skipping", roundId);
            } catch (Exception e) {
                log.error("Failed to start round {}", roundId, e);
                counters.errorCount++;
            }
        }

        // Phase 3 — reconcile parent auction status per affected auction
        for (Long auctionId : counters.affectedAuctions) {
            try {
                statusReconciler.reconcile(auctionId);
            } catch (Exception e) {
                log.error("Failed to reconcile auction {}", auctionId, e);
                counters.errorCount++;
            }
        }

        return new TickResult(counters.toJson(), counters.errorCount);
    }
}
```

- [ ] **Step 8.5: Run test to verify it passes**

Run: `cd backend && mvn -Dtest=AuctionLifecycleServiceTest test`
Expected: `Tests run: 5, Failures: 0`.

- [ ] **Step 8.6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/TickCounters.java backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/TickResult.java backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleService.java backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleServiceTest.java
git commit -m "feat: add AuctionLifecycleService.tick() with per-phase orchestration"
```

---

## Task 9: `SchedulingConfig` and `AuctionLifecycleScheduler`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/config/SchedulingConfig.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleScheduler.java`

- [ ] **Step 9.1: Create `SchedulingConfig` (enables @Scheduled + ShedLock LockProvider)**

```java
package com.ecoatm.salesplatform.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT55S")
@ConditionalOnProperty(prefix = "auctions.lifecycle", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName("infra.shedlock")
                .usingDbTime()
                .build()
        );
    }
}
```

- [ ] **Step 9.2: Create `AuctionLifecycleScheduler`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRecorder;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "auctions.lifecycle", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuctionLifecycleScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionLifecycleScheduler.class);
    private static final String JOB_NAME = "auctionLifecycle";

    private final AuctionLifecycleService lifecycleService;
    private final ScheduledJobRunRecorder recorder;

    public AuctionLifecycleScheduler(AuctionLifecycleService lifecycleService,
                                     ScheduledJobRunRecorder recorder) {
        this.lifecycleService = lifecycleService;
        this.recorder = recorder;
    }

    @Scheduled(fixedDelayString = "${auctions.lifecycle.poll-ms:60000}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = "PT10S", lockAtMostFor = "PT55S")
    public void runTick() {
        ScheduledJobRunRecorder.Handle handle = recorder.begin(JOB_NAME);
        try {
            TickResult result = lifecycleService.tick();
            recorder.end(handle, ScheduledJobRunStatus.OK, null, result.counters());
            log.debug("Lifecycle tick complete counters={}", result.counters());
        } catch (Exception e) {
            log.error("Lifecycle tick failed", e);
            recorder.end(handle, ScheduledJobRunStatus.FAILED, e.getMessage(), null);
        }
    }
}
```

- [ ] **Step 9.3: Verify compilation**

Run: `cd backend && mvn -DskipTests compile`
Expected: `BUILD SUCCESS`.

- [ ] **Step 9.4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/config/SchedulingConfig.java backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleScheduler.java
git commit -m "feat: add AuctionLifecycleScheduler with @Scheduled + ShedLock leader election"
```

---

## Task 10: Six stub listeners with tests

**Files:** (12 total — one Java file + one test per stub)
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R1InitStubListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R2InitStubListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/BidRankingStubListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/SnowflakePushStubListener.java`
- Create the matching `*Test.java` files under `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/`.

All six follow the same shape. Steps 10.1-10.6 are template-driven — copy/paste/edit per stub.

- [ ] **Step 10.1: Create `R1InitStubListener`**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 3 will replace this with the real Round 1 init logic
 * (per Mendix SUB_InitializeRound1). Until then, this listener only logs.
 */
@Component
public class R1InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R1InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 1) return;
        log.info("[stub] R1 init — would snapshot inventory auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
```

- [ ] **Step 10.2: Create `R2InitStubListener` (same shape, filter `round == 2`)**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 5 will replace this with real R2 buyer assignment
 * (per Mendix SUB_AssignRoundTwoBuyers + Sub_ProcessSpecialBuyers).
 */
@Component
public class R2InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R2InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 2) return;
        log.info("[stub] R2 init — would assign R2 buyers + process special buyers auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
```

- [ ] **Step 10.3: Create `R3InitStubListener` (filter `round == 3`)**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 6 will replace this with the real R3 (Upsell) init
 * (per Mendix ACT_Round3_SetStarted).
 */
@Component
public class R3InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R3InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 3) return;
        log.info("[stub] R3 init — would set Upsell round started auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
```

- [ ] **Step 10.4: Create `BidRankingStubListener` (filter `round == 1 || round == 2`, on `RoundClosedEvent`)**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 4 will replace this with real bid ranking + target
 * price calc (per Mendix ACT_TriggerBidRankingCalculation_TryCatch and
 * ACT_CalculateTargetPrice).
 */
@Component
public class BidRankingStubListener {

    private static final Logger log = LoggerFactory.getLogger(BidRankingStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 1 && event.round() != 2) return;
        log.info("[stub] Bid ranking — would rank bids + calc target price auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
```

- [ ] **Step 10.5: Create `R3PreProcessStubListener` (filter `round == 2 || round == 3`, on `RoundClosedEvent`)**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 6 will replace this with real R3 pre-process logic
 * (per Mendix SUB_Round3_PreProcessRoundData).
 */
@Component
public class R3PreProcessStubListener {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 2 && event.round() != 3) return;
        log.info("[stub] R3 pre-process — would prep round-3 data auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
```

- [ ] **Step 10.6: Create `SnowflakePushStubListener` (no round filter; subscribes to BOTH events)**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 1 will replace this with the real Snowflake push
 * (per Mendix SUB_SendAuctionAndSchedulingActionToSnowflake_async).
 */
@Component
public class SnowflakePushStubListener {

    private static final Logger log = LoggerFactory.getLogger(SnowflakePushStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        log.info("[stub] Snowflake push (started) — would push auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        log.info("[stub] Snowflake push (closed) — would push auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
```

- [ ] **Step 10.7: Write tests for each stub (one shared template — instantiate per stub)**

Below is the test for `R1InitStubListener`. Copy this and adjust class names + filter assertions for the other five.

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class R1InitStubListenerTest {

    private R1InitStubListener listener;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        listener = new R1InitStubListener();
        Logger l = (Logger) LoggerFactory.getLogger(R1InitStubListener.class);
        appender = new ListAppender<>();
        appender.start();
        l.addAppender(appender);
        l.setLevel(Level.INFO);
    }

    @Test
    void onRoundStarted_round1_logsStub() {
        listener.onRoundStarted(new RoundStartedEvent(11L, 1, 100L, 999L));
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage())
                .contains("[stub] R1 init")
                .contains("auctionId=100").contains("weekId=999").contains("roundId=11");
    }

    @Test
    void onRoundStarted_round2_isIgnored() {
        listener.onRoundStarted(new RoundStartedEvent(22L, 2, 100L, 999L));
        assertThat(appender.list).isEmpty();
    }
}
```

For the other tests:
- `R2InitStubListenerTest` — match `round=2`, ignore round=1 and round=3.
- `R3InitStubListenerTest` — match `round=3`, ignore round=1 and round=2.
- `BidRankingStubListenerTest` — uses `RoundClosedEvent`, matches round=1 and round=2, ignores round=3.
- `R3PreProcessStubListenerTest` — uses `RoundClosedEvent`, matches round=2 and round=3, ignores round=1.
- `SnowflakePushStubListenerTest` — has TWO test methods (one per event type), both match all rounds.

- [ ] **Step 10.8: Run the stub tests**

Run: `cd backend && mvn -Dtest='*StubListenerTest' test`
Expected: `Tests run: 13, Failures: 0` (six stubs × ~2 tests each, plus Snowflake's two-event coverage).

- [ ] **Step 10.9: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/ backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/
git commit -m "feat: add six stub listeners with logging-only contracts for sub-projects 1-6"
```

---

## Task 11: Application config

**Files:**
- Modify: `backend/src/main/resources/application.yml`
- Modify or create: `backend/src/test/resources/application-test.yml`

- [ ] **Step 11.1: Read current `application.yml`**

Run: `head -100 backend/src/main/resources/application.yml`
Locate where to add the new block (typically alongside other feature flags like `pws.email.enabled` or `snowflake.enabled`).

- [ ] **Step 11.2: Add the lifecycle block to `application.yml`**

Append (or insert near other feature-toggle blocks):

```yaml
auctions:
  lifecycle:
    enabled: true             # set false to disable the cron entirely
    poll-ms: 60000            # 60s — match Mendix scheduled-event interval
```

- [ ] **Step 11.3: Disable lifecycle in `application-test.yml`**

Read first: `cat backend/src/test/resources/application-test.yml` (create if absent).

Add:

```yaml
auctions:
  lifecycle:
    enabled: false            # tests call AuctionLifecycleService.tick() directly
```

- [ ] **Step 11.4: Verify start-up still works**

Run: `cd backend && mvn -DskipTests spring-boot:run` (let it boot, then `Ctrl+C`).
Expected: console contains `Started SalesplatformApplication` and no schema errors.

- [ ] **Step 11.5: Commit**

```bash
git add backend/src/main/resources/application.yml backend/src/test/resources/application-test.yml
git commit -m "chore: add auctions.lifecycle.enabled config (true in app, false in tests)"
```

---

## Task 12: End-to-end integration tests

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleIT.java`
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleSchedulerIT.java`

- [ ] **Step 12.1: Write `AuctionLifecycleIT`**

This test boots the full Spring context with a Testcontainers Postgres, seeds rows directly via JPA, calls `service.tick()` and asserts both the row state and the audit row.

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRun;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRepository;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import com.ecoatm.salesplatform.model.auctions.*;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class AuctionLifecycleIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("auctions.lifecycle.enabled", () -> "false");  // we drive tick() manually
    }

    @TestConfiguration
    static class TestEventCapture {
        final List<ApplicationEvent> events = new ArrayList<>();
        @Bean
        TestEventCapture eventCapture() { return this; }
        @EventListener
        void capture(RoundStartedEvent e) { events.add(e); }
        @EventListener
        void capture(RoundClosedEvent e) { events.add(e); }
    }

    @Autowired private AuctionLifecycleService service;
    @Autowired private SchedulingAuctionRepository schedulingRepo;
    @Autowired private AuctionRepository auctionRepo;
    @Autowired private ScheduledJobRunRepository runRepo;
    @Autowired private TestEventCapture capture;

    @BeforeEach
    void clear() {
        schedulingRepo.deleteAll();
        auctionRepo.deleteAll();
        runRepo.deleteAll();
        capture.events.clear();
    }

    @Test
    void tick_closesStartedRoundsPastEnd() {
        Auction a = persistAuction(AuctionStatus.Started);
        SchedulingAuction r = persistRound(a.getId(), 1,
                SchedulingAuctionStatus.Started,
                Instant.now().minusSeconds(7200),     // start 2h ago
                Instant.now().minusSeconds(60));      // end 1m ago

        service.tick();

        SchedulingAuction reloaded = schedulingRepo.findById(r.getId()).orElseThrow();
        assertThat(reloaded.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Closed);
        assertThat(capture.events).anyMatch(e -> e instanceof RoundClosedEvent);
    }

    @Test
    void tick_startsScheduledRoundsAtTime() {
        Auction a = persistAuction(AuctionStatus.Scheduled);
        SchedulingAuction r = persistRound(a.getId(), 1,
                SchedulingAuctionStatus.Scheduled,
                Instant.now().minusSeconds(10),       // start 10s ago
                Instant.now().plusSeconds(3600));     // end +1h

        service.tick();

        assertThat(schedulingRepo.findById(r.getId()).orElseThrow().getRoundStatus())
                .isEqualTo(SchedulingAuctionStatus.Started);
        assertThat(capture.events).anyMatch(e -> e instanceof RoundStartedEvent);
    }

    @Test
    void tick_reconcilesParentToClosedWhenAllRoundsClosed() {
        Auction a = persistAuction(AuctionStatus.Started);
        persistRound(a.getId(), 1, SchedulingAuctionStatus.Closed,
                Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600));
        persistRound(a.getId(), 2, SchedulingAuctionStatus.Closed,
                Instant.now().minusSeconds(3600), Instant.now().minusSeconds(1800));
        persistRound(a.getId(), 3, SchedulingAuctionStatus.Started,
                Instant.now().minusSeconds(1800), Instant.now().minusSeconds(60));

        service.tick();

        assertThat(auctionRepo.findById(a.getId()).orElseThrow().getAuctionStatus())
                .isEqualTo(AuctionStatus.Closed);
    }

    @Test
    void tick_writesAuditRowOnSuccess() {
        // need to invoke the scheduler path (not service.tick()) to record audit row
        // — see AuctionLifecycleSchedulerIT for full coverage
    }

    private Auction persistAuction(AuctionStatus status) {
        Auction a = new Auction();
        a.setAuctionTitle("IT test " + System.nanoTime());
        a.setAuctionStatus(status);
        a.setWeekId(1L);
        a.setCreatedDate(Instant.now());
        a.setChangedDate(Instant.now());
        a.setCreatedBy("it");
        a.setUpdatedBy("it");
        return auctionRepo.save(a);
    }

    private SchedulingAuction persistRound(Long auctionId, int round, SchedulingAuctionStatus status,
                                           Instant start, Instant end) {
        SchedulingAuction r = new SchedulingAuction();
        r.setAuctionId(auctionId);
        r.setRound(round);
        r.setRoundStatus(status);
        r.setStartDatetime(start);
        r.setEndDatetime(end);
        r.setCreatedDate(Instant.now());
        r.setChangedDate(Instant.now());
        r.setCreatedBy("it");
        r.setUpdatedBy("it");
        return schedulingRepo.save(r);
    }
}
```

- [ ] **Step 12.2: Write `AuctionLifecycleSchedulerIT` — covers ShedLock + audit row**

```java
package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRun;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRepository;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "auctions.lifecycle.enabled=true")
@Testcontainers
class AuctionLifecycleSchedulerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        // very short interval so the test doesn't wait a full minute
        r.add("auctions.lifecycle.poll-ms", () -> "300");
    }

    @Autowired private AuctionLifecycleScheduler scheduler;
    @Autowired private ScheduledJobRunRepository runRepo;

    @Test
    void scheduler_runsTickAndWritesAuditRow() {
        scheduler.runTick();   // direct invocation — bypasses ShedLock proxy by design

        List<ScheduledJobRun> rows = runRepo.findAll();
        assertThat(rows).isNotEmpty();
        ScheduledJobRun row = rows.get(rows.size() - 1);
        assertThat(row.getJobName()).isEqualTo("auctionLifecycle");
        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.OK);
        assertThat(row.getDurationMs()).isNotNull();
        assertThat(row.getNodeId()).contains("-");
    }
}
```

- [ ] **Step 12.3: Run integration tests**

Run: `cd backend && mvn -Dtest='AuctionLifecycle*IT' test`
Expected: all tests pass. First run is slow (~30s) because Testcontainers pulls the Postgres image.

If a test reports "ApplicationContext failure" with a missing bean, ensure `Clock` bean is registered (Task 3.4). If the audit row isn't written, the scheduler is failing under `@SchedulerLock`'s self-invocation rule — verify by calling `scheduler.runTick()` directly (already in the test), not from inside another @Scheduled bean.

- [ ] **Step 12.4: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleIT.java backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/AuctionLifecycleSchedulerIT.java
git commit -m "test: add Testcontainers integration tests for lifecycle cron and scheduler"
```

---

## Task 13: Documentation

**Files:**
- Modify: `docs/architecture/decisions.md`
- Modify or create: `docs/app-metadata/scheduled-events.md`
- Modify: `docs/business-logic/auction-flow.md`

- [ ] **Step 13.1: Append ADR to `docs/architecture/decisions.md`**

Insert at the very top, above the existing 2026-04-20 ADR:

```markdown
## 2026-04-20 — Auction lifecycle cron: per-row tx, ShedLock leader election, event-driven downstream

**Status:** Accepted (sub-project 0 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `AuctionUI.ACT_ScheduleAuctionCheckStatus` runs every minute and
transitions auction rounds (`Scheduled→Started`, `Started→Closed`),
fanning out into Round 1/2/3 init, bid ranking, target price calc,
special-buyer processing, and Snowflake push. The downstream tree is the
entire Mendix bid engine. We split the port into seven sub-projects;
this ADR covers sub-project 0 — the cron skeleton and event contract
that the other six will subscribe to.

### Decision

- **Multi-instance safety via ShedLock + `infra.shedlock`.** Spring
  `@Scheduled` runs on every JVM by default; ShedLock's
  `@SchedulerLock(name="auctionLifecycle", lockAtLeastFor=10s,
  lockAtMostFor=55s)` ensures only one node executes per tick.
  `lockAtMostFor < poll-ms` so a crashed leader is reclaimable on the
  next minute.
- **Per-row REQUIRES_NEW transactions.** The orchestrator
  (`AuctionLifecycleService.tick()`) opens no tx itself; each row
  transition runs in its own short tx via `RoundTransitionService`,
  guarded by a `SELECT ... FOR UPDATE` re-check. One bad row logs at
  ERROR and is skipped; others proceed. Trades the legacy Mendix
  all-or-nothing atomicity for forward-progress under partial failure.
- **Hybrid event contract.** Cron-driven transitions emit
  `RoundStartedEvent` / `RoundClosedEvent` (post-commit). The existing
  `AuctionScheduledEvent` / `AuctionUnscheduledEvent` continue to fire
  from the admin HTTP flows. Downstream listeners filter on `round`
  field instead of subscribing to a generic transition event.
- **Reusable `infra` schema.** `infra.scheduled_job_run` records every
  tick (status, duration, counters JSONB, node id) and is reusable for
  any future cron job. `infra.shedlock` is the ShedLock JDBC table.
  Audit shape is *lightweight + payload counters* — same write cost as
  status-only, ~10× more useful for forensic debugging.
- **Six stub listeners ship with this PR.** They log "would do X" and
  define the contract sub-projects 1-6 must preserve when they replace
  each stub with real logic (Snowflake push, R1/R2/R3 init, bid
  ranking, special-buyer processing, R3 pre-process).

### Alternatives considered

- **Postgres advisory lock** (`pg_try_advisory_lock`) — simpler than
  ShedLock, no new dependency. Rejected because the user explicitly
  anticipates many more cron jobs, and ShedLock provides per-job naming
  and TTL semantics out of the box.
- **Single tx for the whole tick** (Mendix shape) — preserves legacy
  atomicity. Rejected because one bad row would block all others on
  every tick until manually cleared; per-row tx prefers liveness.
- **Reuse `AuctionScheduleService` for the cron entry point.**
  Rejected — it conflates user-driven actions (HTTP, validates against
  bids/started rounds) with system-driven actions (cron, no validation,
  just transitions). Different invariants, different audit needs,
  different test surface.

### Consequences

- A round picked up by the selector but flipped between selector and
  `FOR UPDATE` re-check throws `RoundAlreadyTransitionedException`,
  which the orchestrator treats as benign and logs at DEBUG only.
- Listener failures do NOT roll back the round transition (they fire
  post-commit). Sub-projects 1-6 must each handle their own retry /
  DLQ semantics.
- The `counters` JSONB allows future jobs to add their own keys without
  schema migrations — but the existing four (`roundsStarted`,
  `roundsClosed`, `auctionsAffected`, `errorCount`) are part of the
  audit contract and cannot be renamed.
- Tests disable the cron via `application-test.yml`; integration tests
  drive `service.tick()` (or `scheduler.runTick()`) directly.
- Coverage target on `service/auctions/lifecycle/**` and
  `infra/scheduledjob/**` is 90%+ — central code, must stay tested.

### References

- Plan: `docs/tasks/auction-lifecycle-cron-plan.md`
- Spec: `docs/tasks/auction-lifecycle-cron-design.md`
- Schema: `V70__infra_schema_and_scheduled_job_run.sql`,
  `V71__infra_shedlock.sql`
- Mendix source: `migration_context/backend/ACT_ScheduleAuctionCheckStatus.md`,
  `ACT_SetAuctionScheduleClosed.md`, `ACT_SetAuctionScheduleStarted.md`,
  `services/SUB_SetAuctionStatus.md`

---
```

- [ ] **Step 13.2: Update `docs/app-metadata/scheduled-events.md`**

Read first (`cat docs/app-metadata/scheduled-events.md`); create if absent. Add an entry:

```markdown
## auctionLifecycle

| Property | Value |
|---|---|
| **Owner class** | `com.ecoatm.salesplatform.service.auctions.lifecycle.AuctionLifecycleScheduler` |
| **Trigger** | `@Scheduled(fixedDelayString = "${auctions.lifecycle.poll-ms:60000}")` |
| **Default interval** | 60s |
| **Multi-instance safety** | ShedLock — `name="auctionLifecycle"`, `lockAtLeastFor=10s`, `lockAtMostFor=55s` |
| **Audit table** | `infra.scheduled_job_run` (one row per tick, `counters` JSONB) |
| **Feature flag** | `auctions.lifecycle.enabled` (default `true`; `false` in `application-test.yml`) |
| **Mendix parity** | `AuctionUI.ACT_ScheduleAuctionCheckStatus` (1-minute scheduled event) |
| **Emits** | `RoundStartedEvent`, `RoundClosedEvent` (post-commit) |

Counters JSONB shape:

```json
{ "roundsStarted": 0, "roundsClosed": 0, "auctionsAffected": 0, "errorCount": 0 }
```
```

- [ ] **Step 13.3: Update `docs/business-logic/auction-flow.md`**

Read first; if absent, create a stub. Add a section:

```markdown
## Round lifecycle

The Mendix `ACT_ScheduleAuctionCheckStatus` 1-minute scheduled event is
ported as the `auctionLifecycle` Spring `@Scheduled` job. Each tick:

1. Closes Started rounds whose `end_datetime < now()`.
2. Starts Scheduled rounds whose `start_datetime ≤ now()`.
3. Reconciles each affected parent `Auction.auction_status`:
   all rounds Closed → `Closed`; any round Started → `Started`;
   else leave unchanged (matches Mendix `SUB_SetAuctionStatus`).

Per-round transitions are emitted as `RoundStartedEvent` /
`RoundClosedEvent` post-commit. The downstream Mendix sub-microflows
(R1/R2/R3 init, bid ranking, target price, special buyers, Snowflake
push) are implemented as event listeners under
`service/auctions/lifecycle/stub/` — currently logging-only stubs that
sub-projects 1-6 will replace.

See ADR `2026-04-20 — Auction lifecycle cron` for the full rationale.
```

- [ ] **Step 13.4: Note in `docs/api/rest-endpoints.md`**

The lifecycle cron has no HTTP surface, but document it in the prose so future API consumers know status changes can happen out-of-band. Add at the end of the **Auctions (Admin)** section:

```markdown
> **Out-of-band status changes:** the `auctionLifecycle` cron job
> (see `docs/app-metadata/scheduled-events.md`) transitions
> `auction_status` and `round_status` automatically every minute when a
> round's start or end time has passed. API consumers should treat these
> fields as eventually consistent — a `Scheduled` auction may flip to
> `Started` without any HTTP call having been made.
```

- [ ] **Step 13.5: Commit**

```bash
git add docs/architecture/decisions.md docs/app-metadata/scheduled-events.md docs/business-logic/auction-flow.md docs/api/rest-endpoints.md
git commit -m "docs: ADR + scheduled-events + business-logic for auction lifecycle cron"
```

---

## Task 14: Final verification

- [ ] **Step 14.1: Run full test suite**

Run: `cd backend && mvn test`
Expected: all tests green (existing + new). New count delta ≈ 30+ tests.

- [ ] **Step 14.2: Run JaCoCo coverage report and inspect lifecycle package**

Run: `cd backend && mvn test jacoco:report`
Open: `backend/target/site/jacoco/index.html`
Expected: `com.ecoatm.salesplatform.service.auctions.lifecycle` ≥ 90% line coverage; `com.ecoatm.salesplatform.infra.scheduledjob` ≥ 90%.

- [ ] **Step 14.3: Boot backend, observe one tick, verify audit row**

Run: `cd backend && mvn -DskipTests spring-boot:run`
Wait ≥ 70 seconds, then:

```bash
PGPASSWORD=salesplatform psql -h localhost -U salesplatform -d salesplatform_dev \
  -c "SELECT id, job_name, status, duration_ms, counters FROM infra.scheduled_job_run ORDER BY id DESC LIMIT 3;"
```
Expected: at least one row with `job_name='auctionLifecycle'`, `status='OK'`, `counters` is non-empty JSON.

`Ctrl+C` to stop the backend.

- [ ] **Step 14.4: Final commit (if anything outstanding) and push**

```bash
git status                                   # should be clean
git log --oneline main..HEAD                # confirm 13 expected commits
```

If anything is dirty, commit it. Then ask the user before pushing — don't push without explicit go-ahead per the project's git-workflow rules.

---

## Out-of-scope reminders

- **Bid ranking, target price, R1/R2/R3 init, special buyers, Snowflake push** — sub-projects 1-6. Each will replace exactly one stub listener.
- **Admin force-close / force-open buttons** — not in this plan; `RoundTransitionService` is reusable for them when they land.
- **Frontend changes** — none. The existing schedule/unschedule UI continues to work; only the autonomous status flips are new.
