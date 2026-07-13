# Local Development Setup

## Prerequisites

| Tool | Required Version | Verify |
|------|-----------------|--------|
| Java (OpenJDK) | 21+ | `java -version` |
| Maven | 3.9+ | `mvn --version` |
| Node.js | 24+ | `node --version` |
| npm | 11+ | `npm --version` |
| PostgreSQL | 15+ | `psql --version` |

> Docker is **optional** — only needed if you prefer containerised Postgres/pgAdmin instead of a local install.

## 1. Database Setup

PostgreSQL must be running on `localhost:5432`.

### Option A: Local PostgreSQL (recommended)

Run the bootstrap script as the `postgres` superuser:

```bash
# Windows
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -f bootstrap.sql

# Linux/Mac
psql -U postgres -f bootstrap.sql
```

This creates:
- **Role**: `salesplatform` (password: `salesplatform`)
- **Database**: `salesplatform_dev` (owner: `salesplatform`)

### Option B: Docker Compose

```bash
docker compose up -d postgres
# Optional: pgAdmin at http://localhost:5050 (admin@ecoatm.com / admin)
docker compose up -d pgadmin
```

## 2. Backend (Spring Boot)

```bash
cd backend

# Run Flyway migrations + start the app (port 8080)
mvn spring-boot:run
```

Flyway auto-runs on startup and manages schemas: `identity`, `user_mgmt`, `buyer_mgmt`, `sso`, `pws`, `mdm`, `integration`.

### Verify

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP","components":{"db":{"status":"UP",...}}}
```

### Spring Security Note

Dev mode generates a random password on each startup (printed in console). The security config will be replaced with proper auth as the project progresses.

## 3. Frontend (Next.js)

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start dev server (port 3000)
npm run dev
```

### Verify

Open http://localhost:3000 in a browser — you should see the landing page.

## Quick Start (both services)

From the project root (`SalesPlatform_Modern/`):

```bash
# Terminal 1 — Backend
cd backend && mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend && npm run dev
```

## Ports Summary

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | `jdbc:postgresql://localhost:5432/salesplatform_dev` |
| Spring Boot API | 8080 | http://localhost:8080 |
| Actuator health | 8080 | http://localhost:8080/actuator/health |
| Next.js frontend | 3000 | http://localhost:3000 |
| pgAdmin (Docker only) | 5050 | http://localhost:5050 |

## Database Credentials (Dev)

| Key | Value |
|-----|-------|
| DB name | `salesplatform_dev` |
| DB user | `salesplatform` |
| DB password | `salesplatform` |
| JDBC URL | `jdbc:postgresql://localhost:5432/salesplatform_dev` |

These are defined in [application.yml](../../backend/src/main/resources/application.yml) and [docker-compose.yml](../../docker-compose.yml).

## Configuration Files

| File | Purpose |
|------|---------|
| `bootstrap.sql` | One-time DB/role creation |
| `backend/src/main/resources/application.yml` | Spring Boot config (datasource, Flyway, Actuator) |
| `backend/src/main/resources/db/migration/` | Flyway SQL migrations (V1–V14+) |
| `docker-compose.yml` | Containerised Postgres + pgAdmin |
| `frontend/package.json` | Next.js scripts and dependencies |

## Troubleshooting

### Backend fails to start
- Ensure PostgreSQL is running: `pg_isready -h localhost -p 5432`
- Ensure `salesplatform_dev` DB exists: run `bootstrap.sql`
- Check port 8080 is free: `netstat -an | grep 8080`

### Frontend fails to start
- Run `npm install` in `frontend/` first
- Check port 3000 is free
- Ignore the turbopack lockfile warning (cosmetic only)

## EB sync config
- `eb.sync.enabled` — default `true`; disables both push + pull
- `eb.sync.fixed-delay-ms` — default 30 min; pull cadence
- `eb.sync.writer` / `eb.sync.reader` — `logging` (default) or `jdbc` (prod)

## PO sync config
- `po.sync.enabled` — default `true`; disables push when false
- `po.sync.writer` — `logging` (default) or `jdbc` (prod)
- `po.sync.snowflake-timeout-seconds` — default 60

## Recalc (4C) sync config
- `recalc.snowflake.bid-ranking-writer` — `logging` (default) or `jdbc`
- `recalc.snowflake.target-price-writer` — `logging` (default) or `jdbc`

## R2 Buyer Assignment (5) config
- `auctions.r2-init.enabled` — default `true`; when `false`, the
  `R2BuyerAssignmentListener` short-circuits and does not write QBCs
  or special-buyer bid_data on `RoundStartedEvent(round=2)`. The admin
  recovery endpoint is unaffected.

## R3 Init + Pre-process (6) config
- `auctions.r3-preprocess.enabled` — default `true`; when `false`, the
  `R3PreProcessListener` short-circuits on `RoundClosedEvent(round=2)` and
  does not write R3 QBCs or `round3_buyer_data_reports`. The admin
  `/preprocess-r3` recovery endpoint is unaffected.
- `auctions.r3-init.enabled` — default `true`; when `false`, the
  `R3InitListener` short-circuits on `RoundStartedEvent(round=3)` and
  does not flip the `Round3InitStatus`. The admin `/reinit-r3` recovery
  endpoint is unaffected.

## Email retry worker config
- `email.retry.fixed-delay-ms` — default `120000` (2 min); `EmailRetryWorker`
  scheduled-tick cadence. Each tick rescues stale-PENDING rows and re-drives
  due `FAILED` rows (design doc §5 "Auto-retry worker (D3)" —
  `docs/tasks/email-management-design-2026-07-10.md`).
- `email.retry.stale-pending-min` — default `5`; a `PENDING` `email.log` row
  older than this many minutes with no resolution is treated as orphaned
  (app crashed between the log insert and the send attempt) and flipped to
  `FAILED` with `next_attempt_at=now` so it falls into the same tick's retry
  pass.

## Partial credit review-completed email config
- `partial-credit.review-completed-email.enabled` — default `false`; when
  `false`, `ReviewCompletedEmailListener` logs the intended send (slf4j INFO)
  on every `ReviewCompletedEvent` but does NOT touch `EmailSender`. Flip
  enabled=true in QA/prod once the buyer-facing copy has been reviewed
  with ops. The listener still subscribes when the flag is off so it can
  log the intent — flipping the flag does not require a bean restart.
  Overridable via env: `PARTIAL_CREDIT_REVIEW_EMAIL_ENABLED=true`.

## Partial credit submitted (confirmation) email config
- `partial-credit.submitted-email.enabled` — default `true`; when `false`,
  `CreditRequestSubmittedEmailListener` logs the intended send (slf4j INFO)
  on every `CreditRequestSubmittedEvent` (published by
  `CreditRequestService.submit` on DRAFT → PENDING_APPROVAL) but does NOT
  call `EmailService`. The listener still subscribes when the flag is off so
  it can log the intent — flipping the flag does not require a bean restart.
  Overridable via env: `PARTIAL_CREDIT_SUBMITTED_EMAIL_ENABLED=false`.

## Partial credit accounting-notification email config (gap 2.5 Task 4)
- `partial-credit.accounting-email.recipients` — comma-separated distribution
  list for the **manual** admin action
  `POST /api/v1/admin/partial-credit/{id}/send-accounting-email`
  (`AccountingEmailService`, legacy `ACT_SendCreditRequestAccountingEmail`).
  **No shipped default** — the accounting address is not in any migrated Mendix
  source. Bound as a `List<String>` via `@Value`. When unset/empty the action
  **fails safe**: it returns `409` "recipients are not configured" rather than
  sending to nobody or a hard-coded address. Set per environment via
  `PARTIAL_CREDIT_ACCOUNTING_EMAIL_RECIPIENTS` (e.g.
  `accounting@ecoatm.com,ap@ecoatm.com`). There is **no** enable flag — the
  template's own `email.template.enabled` column (V102 seeds it `true`) and the
  dev `LoggingEmailSender` gate delivery, same as the RMA/manual-qualification
  emails.

## JPA / Hibernate config
- `spring.jpa.open-in-view: false` — added in sub-project 6 (Task 16).
  Disables the Open-Session-In-View anti-pattern. Without this setting,
  Hibernate's L1 cache in admin response paths serves a stale entity
  snapshot (pre-JDBC-write) when the controller reads the SA back to build
  the response DTO — masking JDBC-written status updates from
  `RecalcStatusUpdater`. This is the recommended production setting for a
  pure REST API; lazy-load-outside-tx patterns would surface as
  `LazyInitializationException`, but the full controller IT sweep confirms
  none exist today.

## RMA Oracle create config (RMA #3 Task B0)
- `rma.oracle-create.enabled` — default `true`; when `false`, the
  `RmaOracleCreateListener` short-circuits on an APPROVED
  `RmaReviewCompletedEvent` and does NOT create the RMA in Oracle. The admin
  `POST /api/v1/pws/rma/{rmaId}/resubmit-oracle` recovery endpoint is
  unaffected. Env override: `RMA_ORACLE_CREATE_ENABLED=false`. Independent of
  the Oracle client's own toggle (`OracleConfig.is_active` + the profile gate,
  which SIM-succeeds only in local dev and fails closed in qa/staging/prod).

## RMA Snowflake sync config (RMA #3 Task D)
- `rma.sync.enabled` — default `true`; when `false`, the
  `RmaSnowflakePushListener` short-circuits on a `RmaReviewCompletedEvent`
  (any outcome) before building the snapshot or invoking the writer. Env
  override: `RMA_SYNC_ENABLED=false`.
- `rma.sync.writer` — `logging` (default) or `jdbc`. `logging` is a no-op that
  logs the row it would push (keeps dev/test off a live Snowflake DataSource);
  `jdbc` calls the `AUCTIONS.UPSERT_RMA_DATA(?)` stored proc via the
  `snowflakeJdbcTemplate` in prod. Mirrors `po.sync.writer` /
  `recalc.snowflake.*`. Independent of `rma.oracle-create.*` — Oracle create
  and Snowflake push are separate AFTER_COMMIT listeners on the same event.

## PWS SLA-tag cron config (gap 2.3 sub-feature 2)
- `pws.sla-tag.enabled` — default `false`; when `false`, `SlaTagService`'s
  `@Scheduled` tick short-circuits before any DB read (the manual admin
  `POST /api/v1/admin/sla-tags/{set,remove}` buttons still work — they call the
  same service directly). Flip `true` per environment to auto-flag overdue
  offers. Env override: `PWS_SLA_TAG_ENABLED=true`.
- `pws.sla-tag.fixed-delay-ms` — default `900000` (15 min); scheduled-tick
  cadence. Env override: `PWS_SLA_TAG_FIXED_DELAY_MS`.
- The overdue cutoff walks back `pws_constants.sla_days` **business** days from
  today (injected `Clock`), skipping weekends + `pws.company_holiday` dates.
  Requires the `pws.company_holiday` table (V104) to be seeded for holiday
  parity; without holiday rows it degrades to a weekends-only business-day walk.

## PWS counter-offer reminder config (gap 2.3 sub-feature 1)
- `pws.counter-reminder.enabled` — default `false`; when `false`, the
  `CounterOfferReminderService` scheduled tick short-circuits before any DB read.
  Flip `true` in QA/prod only after ops reviews the best-effort V105 email copy
  (`PwsCounterOfferFirstReminder` / `PwsCounterOfferSecondReminder`). Env
  override: `PWS_COUNTER_REMINDER_ENABLED=true`.
- `pws.counter-reminder.fixed-delay-ms` — default `3600000` (hourly); the sweep
  cadence. Each tick evaluates every `Buyer_Acceptance` offer with a reminder
  still unsent, applies the `pws_constants` thresholds/toggles, and sends the
  1st/2nd reminder to buyer users via `EmailService.sendTemplated`. Single-leader
  via `@SchedulerLock` (`infra.shedlock`), per-row exception isolation, one-shot
  via the V103 `first_reminder_sent`/`second_reminder_sent` flags.
