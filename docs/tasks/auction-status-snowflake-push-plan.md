# Auction Status Snowflake Push — Implementation Plan (Sub-project 1, Phase 1)

**Goal:** Replace `SnowflakePushStubListener` with a real listener + writer
abstraction + feature flag. The actual Snowflake `INSERT` is deferred — we
ship a logging-only writer that logs the fully-formed payload so the real
writer becomes a drop-in replacement later.

**Architecture:** Post-commit `RoundStartedEvent` / `RoundClosedEvent` from
sub-project 0 triggers a `@TransactionalEventListener` that loads the
auction + week aggregate in a short read-only REQUIRES_NEW tx, checks the
feature flag, builds an `AuctionStatusPushPayload`, and hands it to an
`AuctionStatusSnowflakeWriter`. The default implementation logs the payload;
the real Snowflake-bound implementation is a follow-up once the target
table shape is known.

**Scope owner:** Sub-project 1 of the seven-part port. This PR ships Phase 1
(listener wiring + deferred writer). Phase 2 (real `INSERT`) is a separate
plan scoped around the Snowflake schema.

**Tech stack:** Spring Boot 3.2.4, Java 21, `@TransactionalEventListener`,
`@Async("snowflakeExecutor")` (already provisioned by the agg-inventory
sync work), Mockito, AssertJ.

---

## Mendix parity source

`migration_context/backend/services/SUB_SendAuctionAndSchedulingActionToSnowflake_async.md`
is the 3-step microflow: export XML, resolve username, `ExecuteDatabaseQuery`.
Callers are `ACT_SetAuctionScheduleStarted` / `ACT_SetAuctionScheduleClosed`,
both of which gate the push on a feature flag
(`BuyerCodeSubmitConfig.SendAuctionDataToSnowflake`) and pass the
`Week` + `SchedulingAuction` + parent `Auction` aggregate.

We replicate the gate via `auctions.snowflake-push.enabled` in
`application.yml` rather than porting the DB-backed config entity — the
`AuctionsFeatureConfig` entity sits uncommitted on the parallel
auction-scheduling branch, and coupling the two is avoided by using yml.

---

## Module layout

| Path | Purpose |
|---|---|
| `service/auctions/snowflake/AuctionStatusAction.java` | Enum `STARTED \| CLOSED`. Reflects the round transition direction. |
| `service/auctions/snowflake/AuctionStatusPushPayload.java` | Immutable record `(auctionId, auctionTitle, weekId, weekDisplay, round, action, transitionedAt, actor)`. |
| `service/auctions/snowflake/AuctionStatusSnowflakeWriter.java` | Interface `void push(AuctionStatusPushPayload payload)`. |
| `service/auctions/snowflake/LoggingAuctionStatusSnowflakeWriter.java` | Default impl. Logs a single structured INFO line with marker `[deferred-writer]`. |
| `service/auctions/snowflake/AuctionStatusSnowflakePushListener.java` | `@Component` + `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`. Loads aggregate, checks flag, builds payload, calls writer. |
| `service/auctions/lifecycle/stub/SnowflakePushStubListener.java` | **Deleted** — replaced. |

Test mirrors under `src/test/java/...`:

| Test | Scope |
|---|---|
| `AuctionStatusPushPayloadTest` | Record invariants. |
| `LoggingAuctionStatusSnowflakeWriterTest` | Log line shape & marker. |
| `AuctionStatusSnowflakePushListenerTest` | 5 branch cases (started, closed, flag-off, auction-not-found, writer-throws). |
| `AuctionStatusSnowflakePushIT` | Cron-driven event reaches the writer end-to-end. |

---

## Task 1 — `AuctionStatusAction` + `AuctionStatusPushPayload`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusAction.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusPushPayload.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusPushPayloadTest.java`

- [ ] **Step 1: Write failing test.**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class AuctionStatusPushPayloadTest {

  @Test
  void payload_capturesAllCanonicalFields() {
    Instant ts = Instant.parse("2026-04-21T16:00:00Z");
    AuctionStatusPushPayload p = new AuctionStatusPushPayload(
        42L, "Auction 2026 / Wk17", 100L, "2026 / Wk17",
        1, AuctionStatusAction.STARTED, ts, "ashirwadmittal");

    assertThat(p.auctionId()).isEqualTo(42L);
    assertThat(p.auctionTitle()).isEqualTo("Auction 2026 / Wk17");
    assertThat(p.weekId()).isEqualTo(100L);
    assertThat(p.weekDisplay()).isEqualTo("2026 / Wk17");
    assertThat(p.round()).isEqualTo(1);
    assertThat(p.action()).isEqualTo(AuctionStatusAction.STARTED);
    assertThat(p.transitionedAt()).isEqualTo(ts);
    assertThat(p.actor()).isEqualTo("ashirwadmittal");
  }

  @Test
  void action_supportsStartedAndClosed() {
    assertThat(AuctionStatusAction.values()).containsExactly(
        AuctionStatusAction.STARTED, AuctionStatusAction.CLOSED);
  }
}
```

Run: `mvn test -Dtest='AuctionStatusPushPayloadTest'`
Expected: FAIL (symbols missing).

- [ ] **Step 2: Implement.**

```java
// AuctionStatusAction.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public enum AuctionStatusAction {
  STARTED, CLOSED
}
```

```java
// AuctionStatusPushPayload.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.time.Instant;

public record AuctionStatusPushPayload(
    long auctionId,
    String auctionTitle,
    long weekId,
    String weekDisplay,
    int round,
    AuctionStatusAction action,
    Instant transitionedAt,
    String actor
) {}
```

- [ ] **Step 3: Run — PASS.**

- [ ] **Step 4: Commit.**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusAction.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusPushPayload.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusPushPayloadTest.java
git commit -m "feat: add AuctionStatusAction + AuctionStatusPushPayload for sub-project 1"
```

---

## Task 2 — `AuctionStatusSnowflakeWriter` + logging implementation

**Files:**
- Create: `.../snowflake/AuctionStatusSnowflakeWriter.java`
- Create: `.../snowflake/LoggingAuctionStatusSnowflakeWriter.java`
- Test: `.../snowflake/LoggingAuctionStatusSnowflakeWriterTest.java`

- [ ] **Step 1: Write failing test.**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class LoggingAuctionStatusSnowflakeWriterTest {

  private ListAppender<ILoggingEvent> appender;
  private Logger logger;

  @BeforeEach
  void attach() {
    logger = (Logger) LoggerFactory.getLogger(LoggingAuctionStatusSnowflakeWriter.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void detach() { logger.detachAppender(appender); }

  @Test
  void push_emitsStructuredLineWithDeferredMarker() {
    AuctionStatusPushPayload p = new AuctionStatusPushPayload(
        42L, "Auction 2026 / Wk17", 100L, "2026 / Wk17", 1,
        AuctionStatusAction.STARTED, Instant.parse("2026-04-21T16:00:00Z"),
        "ashirwadmittal");

    new LoggingAuctionStatusSnowflakeWriter().push(p);

    assertThat(appender.list).hasSize(1);
    String msg = appender.list.get(0).getFormattedMessage();
    assertThat(msg)
        .contains("[deferred-writer]")
        .contains("action=STARTED")
        .contains("auctionId=42")
        .contains("weekId=100")
        .contains("round=1")
        .contains("actor=ashirwadmittal");
  }
}
```

Run: `mvn test -Dtest='LoggingAuctionStatusSnowflakeWriterTest'` → FAIL.

- [ ] **Step 2: Implement.**

```java
// AuctionStatusSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface AuctionStatusSnowflakeWriter {
  void push(AuctionStatusPushPayload payload);
}
```

```java
// LoggingAuctionStatusSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingAuctionStatusSnowflakeWriter implements AuctionStatusSnowflakeWriter {

  private static final Logger log = LoggerFactory.getLogger(LoggingAuctionStatusSnowflakeWriter.class);

  @Override
  public void push(AuctionStatusPushPayload p) {
    log.info("[deferred-writer] auction-snowflake-push action={} auctionId={} auctionTitle=\"{}\" "
            + "weekId={} weekDisplay=\"{}\" round={} transitionedAt={} actor={}",
        p.action(), p.auctionId(), p.auctionTitle(),
        p.weekId(), p.weekDisplay(), p.round(),
        p.transitionedAt(), p.actor());
  }
}
```

- [ ] **Step 3: Run — PASS.**

- [ ] **Step 4: Commit.**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingAuctionStatusSnowflakeWriter.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingAuctionStatusSnowflakeWriterTest.java
git commit -m "feat: add AuctionStatusSnowflakeWriter interface + logging-only default impl"
```

---

## Task 3 — `AuctionStatusSnowflakePushListener` + remove stub

**Files:**
- Create: `.../snowflake/AuctionStatusSnowflakePushListener.java`
- Delete: `.../lifecycle/stub/SnowflakePushStubListener.java`
- Delete: `.../lifecycle/stub/SnowflakePushStubListenerTest.java`
- Test: `.../snowflake/AuctionStatusSnowflakePushListenerTest.java`

- [ ] **Step 1: Write failing unit tests.**

Five cases:
1. `onRoundStarted_flagOn_invokesWriterWithStartedPayload`
2. `onRoundClosed_flagOn_invokesWriterWithClosedPayload`
3. `event_flagOff_writerNeverCalled`
4. `event_auctionNotFound_warnsAndReturns`
5. `event_writerThrows_isSwallowed`

Skeleton:

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionStatusSnowflakePushListenerTest {

  @Mock AuctionRepository auctionRepo;
  @Mock WeekRepository weekRepo;
  @Mock AuctionStatusSnowflakeWriter writer;

  AuctionStatusSnowflakePushListener listener;

  @BeforeEach
  void setUp() {
    listener = new AuctionStatusSnowflakePushListener(auctionRepo, weekRepo, writer, true);
  }

  private Auction auction(long id, long weekId) { /* builder w/ id, title, weekId */ }
  private Week week(long id, String display) { /* builder */ }

  @Test
  void onRoundStarted_flagOn_invokesWriterWithStartedPayload() {
    Instant ts = Instant.parse("2026-04-21T16:00:00Z");
    when(auctionRepo.findById(42L)).thenReturn(Optional.of(auction(42L, 100L)));
    when(weekRepo.findById(100L)).thenReturn(Optional.of(week(100L, "2026 / Wk17")));

    listener.onRoundStarted(new RoundStartedEvent(42L, 100L, 1, 301L, ts, "ashirwadmittal"));

    ArgumentCaptor<AuctionStatusPushPayload> cap = ArgumentCaptor.forClass(AuctionStatusPushPayload.class);
    verify(writer).push(cap.capture());
    assertThat(cap.getValue().action()).isEqualTo(AuctionStatusAction.STARTED);
    assertThat(cap.getValue().auctionId()).isEqualTo(42L);
    assertThat(cap.getValue().weekDisplay()).isEqualTo("2026 / Wk17");
    assertThat(cap.getValue().round()).isEqualTo(1);
  }

  // … analogous tests for Closed / flag off / not found / writer throws
}
```

Run: `mvn test -Dtest='AuctionStatusSnowflakePushListenerTest'` → FAIL.

- [ ] **Step 2: Implement.**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuctionStatusSnowflakePushListener {

  private static final Logger log = LoggerFactory.getLogger(AuctionStatusSnowflakePushListener.class);

  private final AuctionRepository auctionRepo;
  private final WeekRepository weekRepo;
  private final AuctionStatusSnowflakeWriter writer;
  private final boolean enabled;

  public AuctionStatusSnowflakePushListener(
      AuctionRepository auctionRepo,
      WeekRepository weekRepo,
      AuctionStatusSnowflakeWriter writer,
      @Value("${auctions.snowflake-push.enabled:false}") boolean enabled) {
    this.auctionRepo = auctionRepo;
    this.weekRepo = weekRepo;
    this.writer = writer;
    this.enabled = enabled;
  }

  @Async("snowflakeExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public void onRoundStarted(RoundStartedEvent event) {
    push(AuctionStatusAction.STARTED, event.auctionId(), event.weekId(),
        event.round(), event.transitionedAt(), event.actor());
  }

  @Async("snowflakeExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public void onRoundClosed(RoundClosedEvent event) {
    push(AuctionStatusAction.CLOSED, event.auctionId(), event.weekId(),
        event.round(), event.transitionedAt(), event.actor());
  }

  private void push(AuctionStatusAction action, long auctionId, long weekId,
                    int round, java.time.Instant ts, String actor) {
    if (!enabled) {
      log.debug("auction-snowflake-push skipped (flag off) action={} auctionId={}", action, auctionId);
      return;
    }
    Auction auction = auctionRepo.findById(auctionId).orElse(null);
    Week week = weekRepo.findById(weekId).orElse(null);
    if (auction == null || week == null) {
      log.warn("auction-snowflake-push aggregate missing action={} auctionId={} weekId={}",
          action, auctionId, weekId);
      return;
    }
    try {
      writer.push(new AuctionStatusPushPayload(
          auctionId, auction.getAuctionTitle(),
          weekId, week.getWeekDisplay(),
          round, action, ts, actor));
    } catch (RuntimeException ex) {
      log.error("auction-snowflake-push writer failed action={} auctionId={}", action, auctionId, ex);
    }
  }
}
```

- [ ] **Step 3: Delete the stub listener + test.**

```bash
git rm backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/SnowflakePushStubListener.java
git rm backend/src/test/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/SnowflakePushStubListenerTest.java
```

- [ ] **Step 4: Run — PASS.**

```bash
mvn test -Dtest='AuctionStatusSnowflakePushListenerTest'
```

- [ ] **Step 5: Commit.**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusSnowflakePushListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusSnowflakePushListenerTest.java
git commit -m "feat: replace SnowflakePushStubListener with real listener + deferred writer"
```

---

## Task 4 — Configuration flag

**Files:**
- Modify: `backend/src/main/resources/application.yml`
- Modify: `backend/src/test/resources/application-test.yml`

- [ ] **Step 1:** Append under existing `auctions:` block in `application.yml`:

```yaml
auctions:
  lifecycle:
    enabled: true
    poll-ms: 60000
  snowflake-push:
    enabled: false   # Phase 1: logging-only writer; flip when real writer ships.
```

- [ ] **Step 2:** Ensure `application-test.yml` keeps the flag off (default already `false`, so no change needed unless the key already exists — verify with grep).

- [ ] **Step 3:** Boot: `mvn spring-boot:run` briefly; confirm no startup error; Ctrl-C.

- [ ] **Step 4: Commit.**

```bash
git add backend/src/main/resources/application.yml
git commit -m "chore: add auctions.snowflake-push.enabled flag (default false)"
```

---

## Task 5 — Integration test

**Files:**
- Create: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusSnowflakePushIT.java`

- [ ] **Step 1: Write the IT.**

Extend the `AuctionLifecycleIT` pattern: seed an auction + a Scheduled round with `start_datetime` in the past, then invoke `lifecycleService.tick()` directly. Register a `@TestConfiguration` `@Primary` capturing writer that records payloads. Assert one payload with `action=STARTED`, the right `auctionId`, `weekId`, `round`.

Run: `mvn test -Dtest='AuctionStatusSnowflakePushIT'` → should PASS when flag is on in the test property source.

- [ ] **Step 2: Commit.**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/AuctionStatusSnowflakePushIT.java
git commit -m "test: add end-to-end IT for cron → event → Snowflake push listener"
```

---

## Task 6 — Documentation

**Files:**
- Modify: `docs/architecture/decisions.md` (new ADR at top)
- Modify: `docs/business-logic/auction-flow.md` (note the listener wiring)
- Modify: `docs/app-metadata/scheduled-events.md` (list the auction-push emitter)

- [ ] **Step 1:** Prepend a new ADR:

```markdown
## 2026-04-21 — Auction status Snowflake push: listener wired, writer deferred

**Status:** Accepted (Phase 1 of sub-project 1).

### Context
Mendix `SUB_SendAuctionAndSchedulingActionToSnowflake_async` pushes a row to
Snowflake on every round Start/Close transition. The body is opaque in the
microflow docs — we don't have the target table shape. Blocking sub-project 1
on schema archaeology would starve the listener contract the other sub-projects
need to co-exist with.

### Decision
Ship the listener, payload, writer interface, and feature flag now. Default the
writer to a logging-only impl that emits a structured `[deferred-writer]` line
containing every field the real writer will need. The real Snowflake-bound
impl is a follow-up once the target schema is confirmed.

### Consequences
- The six-listener contract from the 2026-04-20 cron ADR is unbroken: the push
  listener is wired, consumes the events, and is @Async on `snowflakeExecutor`.
- Flipping to real Snowflake delivery is a single-class change (a new
  `@Component @Primary` writer behind a profile or a second feature flag).
- The log line is the current audit surface; `integration.snowflake_sync_log`
  will be wired when the real writer lands.
```

- [ ] **Step 2:** Update `docs/business-logic/auction-flow.md`: in the round-lifecycle section, note `auction-snowflake-push` is live but writes to logs only (deferred-writer).

- [ ] **Step 3:** Update `docs/app-metadata/scheduled-events.md`: under `auctionLifecycle → Emits`, note that `RoundStartedEvent`/`RoundClosedEvent` are consumed by `AuctionStatusSnowflakePushListener` with a deferred writer.

- [ ] **Step 4: Commit.**

```bash
git add docs/architecture/decisions.md docs/business-logic/auction-flow.md docs/app-metadata/scheduled-events.md
git commit -m "docs: ADR + runbook notes for auction status Snowflake push (deferred writer)"
```

---

## Task 7 — Verification

- [ ] **7.1** `mvn test -Dtest='AuctionStatusPushPayloadTest,LoggingAuctionStatusSnowflakeWriterTest,AuctionStatusSnowflakePushListenerTest,AuctionStatusSnowflakePushIT'` — all green.
- [ ] **7.2** `mvn jacoco:report`; verify `service/auctions/snowflake` package ≥ 90%.
- [ ] **7.3** Boot backend, trigger one round transition manually (or wait for cron), grep log for `[deferred-writer] auction-snowflake-push`.
- [ ] **7.4** Confirm commit count on branch = 6 (Tasks 1-6); nothing outstanding.
