# Unified Email Management — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking. Source spec: `docs/tasks/email-management-design-2026-07-10.md`.

**Goal:** A single, Mendix-style email capability — UI template setup (subject/body + editable to/cc/bcc/from/reply-to), UI SMTP config (password from env), and full sent/failed tracking with manual resend + auto-retry — with Partial Credit migrated onto it.

**Architecture:** New `email` Flyway schema (`smtp_config`, `template`, `log`). A general `EmailService` renders a DB template, resolves recipients, sends via the existing `EmailSender`/`SmtpEmailSender`, and records one `email.log` row (rendered snapshot + status). A ShedLock'd `EmailRetryWorker` auto-retries failures and rescues orphaned PENDING rows. `AdminEmailController` fills the already-stubbed `/api/v1/admin/email/**` UI. Partial Credit is repointed onto `EmailService`.

**Tech Stack:** Spring Boot 3.3.13, Java 21, JPA/Hibernate, Flyway, `spring-boot-starter-mail` (`JavaMailSender`), ShedLock, JUnit 5 + Mockito + AssertJ + Testcontainers-Postgres, Next.js 16 + React Testing Library.

## Global Constraints

- **Backend package root:** `com.ecoatm.salesplatform` under `backend/src/main/java/…`. Tests mirror the package under `backend/src/test/java/…`.
- **New schema name:** `email` (already in `application.yml` Flyway `schemas` list). Migration is **`V92__email_management.sql`** (V91 is the current max).
- **SMTP password NEVER in DB or API** — only from env `spring.mail.password` (`EMAIL_SMTP_PASSWORD`). `GET /smtp` must never return a password; `PUT /smtp` must ignore any password field. Mirror `security/JwtSecretValidator.java` for the fail-fast.
- **Authz:** every `/api/v1/admin/email/**` endpoint is `@PreAuthorize("hasRole('Administrator')")` + a `SecurityConfig` matcher. Outbound-triggering endpoints (`/smtp/test`, `/templates/{id}/send-test`) are rate-limited via the existing `UploadRateLimiter`-style limiter.
- **Async + tx safety:** sends run on the existing `AsyncConfig.EMAIL_EXECUTOR` after `AFTER_COMMIT`, and never throw into the business transaction (mirror `ReviewCompletedEmailListener`).
- **Template substitution:** `{{var}}` HTML-escaped, `{{!var}}` raw opt-in, missing var → empty + warn log (identical semantics to today's `EmailTemplateServiceImpl`).
- **Commit style:** `feat(email): …` / `test(email): …`; commit at the end of each task.
- **Address lists** (`to_default`/`cc_default`/`bcc_default`, and API `to`/`cc`/`bcc`) are comma-separated strings, trimmed, empties dropped.

---

## File Structure

**Backend — new:**
- `…/resources/db/migration/V92__email_management.sql` — the 3 tables + seed smtp_config + copy PC templates.
- `…/model/email/{SmtpConfig,EmailTemplate,EmailLog}.java` — JPA entities.
- `…/model/email/EmailStatus.java` — enum `PENDING, SENT, FAILED`.
- `…/repository/email/{SmtpConfigRepository,EmailTemplateRepository,EmailLogRepository}.java`.
- `…/service/email/TemplateRenderer.java` — extracted render engine.
- `…/service/email/SmtpConfigService.java` — cached read of the singleton config.
- `…/service/email/EmailService.java` — `sendTemplated(...)` + `resend(logId)`.
- `…/service/email/EmailRetryWorker.java` — scheduled retry + PENDING sweep.
- `…/security/EmailSmtpValidator.java` — startup fail-fast.
- `…/controller/admin/AdminEmailController.java` — `/api/v1/admin/email/**`.
- `…/dto/email/{SmtpConfigView,SmtpConfigUpdate,EmailTemplateView,EmailTemplateUpsert,EmailLogView,SendTestRequest,PreviewRequest}.java` — records.

**Backend — modify:**
- `…/service/email/EmailMessage.java` — add `from`, `replyTo`, `bcc`.
- `…/service/email/SmtpEmailSender.java` — set from/replyTo/bcc; read host/port/from from `SmtpConfigService`.
- `…/security/SecurityConfig.java` — matcher for `/api/v1/admin/email/**`.
- `…/listener/partialcredit/ReviewCompletedEmailListener.java` — send via `EmailService`.
- `…/controller/admin/AdminPartialCreditController.java` — drop the PC template-editor endpoints (consolidated).

**Frontend — modify:**
- `frontend/src/app/(dashboard)/admin/app-control-center/email-admin/page.tsx` — the 3 tabs already call the endpoints; verify payload shapes + replace the regex `sanitizeEmailHtml` with a real sanitizer.
- Retire `…/admin/auctions-data-center/partial-credit/email-templates/` (PC editor) — remove route + its launcher link.

**Sequence / dependencies:** T1→T2→(T3,T4)→T5→T6; T2,T4→T7; T2,T4→T8; T2,T5→T9; T9→T10; T5→T11. Tasks 7/8/9 may run in parallel after their deps; keep 1-6 and 11 sequential.

---

### Task 1: `V92` email schema migration

**Files:**
- Create: `backend/src/main/resources/db/migration/V92__email_management.sql`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/email/V92MigrationIT.java`

**Interfaces — Produces:** tables `email.smtp_config`, `email.template`, `email.log`; one seeded `smtp_config` row (id=1, enabled=false); PC templates copied into `email.template`.

- [ ] **Step 1: Write the failing test** (`@SpringBootTest` + Testcontainers/Flyway, mirror `V90MigrationIT`)

```java
@Test void createsThreeTablesSeedAndCopiesPcTemplates() {
  assertThat(tableExists("email","smtp_config")).isTrue();
  assertThat(tableExists("email","template")).isTrue();
  assertThat(tableExists("email","log")).isTrue();
  Long cfg = jdbc.queryForObject("SELECT count(*) FROM email.smtp_config", Long.class);
  assertThat(cfg).isEqualTo(1L);
  assertThat(jdbc.queryForObject("SELECT enabled FROM email.smtp_config WHERE id=1", Boolean.class)).isFalse();
  // 3 PC templates copied over
  Long tpls = jdbc.queryForObject("SELECT count(*) FROM email.template WHERE template_key IN (SELECT template_key FROM partial_credit.email_templates)", Long.class);
  assertThat(tpls).isEqualTo(jdbc.queryForObject("SELECT count(*) FROM partial_credit.email_templates", Long.class));
}
```
(Reuse the `tableExists(schema,name)` helper pattern from `V90MigrationIT`.)

- [ ] **Step 2: Run to verify it fails** — `./mvnw -o test -Dtest=V92MigrationIT` → FAIL (relation `email.smtp_config` does not exist).

- [ ] **Step 3: Write the migration**

```sql
-- V92__email_management.sql
CREATE TABLE email.smtp_config (
  id                  BIGINT PRIMARY KEY DEFAULT 1 CHECK (id = 1),
  server_host         VARCHAR(255),
  server_port         INT          NOT NULL DEFAULT 587,
  protocol            VARCHAR(20)  NOT NULL DEFAULT 'SMTP',
  from_address        VARCHAR(255),
  from_display_name   VARCHAR(255),
  reply_to            VARCHAR(255),
  use_ssl             BOOLEAN      NOT NULL DEFAULT FALSE,
  use_tls             BOOLEAN      NOT NULL DEFAULT TRUE,
  enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
  max_retry_attempts  INT          NOT NULL DEFAULT 3,
  timeout_ms          INT          NOT NULL DEFAULT 10000,
  created_date        TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_date        TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_by_id       BIGINT REFERENCES identity.users(id)
);
INSERT INTO email.smtp_config (id) VALUES (1);

CREATE TABLE email.template (
  id                BIGSERIAL PRIMARY KEY,
  template_key      VARCHAR(80)  NOT NULL UNIQUE CHECK (template_key ~ '^[A-Za-z0-9_]+$'),
  template_name     VARCHAR(160) NOT NULL,
  subject           VARCHAR(255) NOT NULL,
  content_html      TEXT         NOT NULL,
  content_plain     TEXT,
  from_address      VARCHAR(255),
  from_display_name VARCHAR(255),
  reply_to          VARCHAR(255),
  to_default        VARCHAR(2000),
  cc_default        VARCHAR(2000),
  bcc_default       VARCHAR(2000),
  has_attachment    BOOLEAN      NOT NULL DEFAULT FALSE,
  enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
  description       VARCHAR(500),
  created_date      TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_date      TIMESTAMPTZ  NOT NULL DEFAULT now(),
  created_by_id     BIGINT REFERENCES identity.users(id),
  changed_by_id     BIGINT REFERENCES identity.users(id)
);

CREATE TABLE email.log (
  id              BIGSERIAL PRIMARY KEY,
  template_key    VARCHAR(80),
  from_address    VARCHAR(255),
  to_address      VARCHAR(2000) NOT NULL,
  cc              VARCHAR(2000),
  bcc             VARCHAR(2000),
  subject         VARCHAR(255),
  content_html    TEXT,
  status          VARCHAR(10)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SENT','FAILED')),
  error_message   TEXT,
  retry_count     INT          NOT NULL DEFAULT 0,
  next_attempt_at TIMESTAMPTZ,
  source_module   VARCHAR(60),
  source_id       BIGINT,
  sent_date       TIMESTAMPTZ,
  created_date    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_email_log_retry ON email.log (status, next_attempt_at);
CREATE INDEX idx_email_log_sent  ON email.log (sent_date DESC);
CREATE INDEX idx_email_log_src   ON email.log (source_module, source_id);
CREATE INDEX idx_email_log_key   ON email.log (template_key);

-- Copy the live Partial-Credit templates into the unified store (D1/D5).
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description, created_date, changed_date)
SELECT template_key, template_key, subject, body_html, body_text, enabled, description, created_date, changed_date
FROM partial_credit.email_templates;
```

- [ ] **Step 4: Run to verify it passes** — `./mvnw -o test -Dtest=V92MigrationIT` → PASS.
- [ ] **Step 5: Commit** — `git add …/V92__email_management.sql …/V92MigrationIT.java && git commit -m "feat(email): V92 email schema (smtp_config, template, log) + PC template copy"`

---

### Task 2: JPA entities + repositories

**Files:** Create `model/email/{EmailStatus,SmtpConfig,EmailTemplate,EmailLog}.java`, `repository/email/{SmtpConfigRepository,EmailTemplateRepository,EmailLogRepository}.java`. Test: `repository/email/EmailRepositoryIT.java`.

**Interfaces — Produces:**
- `EmailStatus { PENDING, SENT, FAILED }`.
- `SmtpConfig` entity (`@Table(schema="email", name="smtp_config")`) fields matching Task-1 columns; getters/setters.
- `EmailTemplate` entity (`email.template`) fields matching columns.
- `EmailLog` entity (`email.log`) fields matching columns; `status` mapped `@Enumerated(STRING)`.
- `SmtpConfigRepository extends JpaRepository<SmtpConfig,Long>` (singleton, findById(1L)).
- `EmailTemplateRepository extends JpaRepository<EmailTemplate,Long>` + `Optional<EmailTemplate> findByTemplateKey(String)`.
- `EmailLogRepository extends JpaRepository<EmailLog,Long>` + `List<EmailLog> findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(EmailStatus, Instant, int)` and a paged `findByStatus(...)` / filter query (used by T9).

- [ ] **Step 1: Failing test** — `EmailRepositoryIT` (`@DataJpaTest` w/ Testcontainers or `@SpringBootTest`): persist a template + a log row, read back; assert `findByTemplateKey` and the retry finder return them. Assert the seeded `smtp_config` (id=1) loads.
- [ ] **Step 2: Run → FAIL** (entities absent).
- [ ] **Step 3: Implement** the four entities (standard JPA — mirror `model/partialcredit/EmailTemplate.java` style; `@Enumerated(EnumType.STRING)` on `EmailLog.status`; `Instant` for tz columns) + the three repositories with the finders above.
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): JPA entities + repositories for email schema`.

---

### Task 3: Extend `EmailMessage` + wire `SmtpEmailSender`

**Files:** Modify `service/email/EmailMessage.java`, `service/email/SmtpEmailSender.java`. Create `security/EmailSmtpValidator.java`. Test: `service/email/EmailMessageTest.java`, `service/email/SmtpEmailSenderTest.java`, `security/EmailSmtpValidatorTest.java`.

**Interfaces — Produces:**
- `EmailMessage(List<String> to, List<String> cc, List<String> bcc, String from, String replyTo, String subject, String htmlBody, String textBody)` — `to` non-empty; `cc`/`bcc` default `List.of()`; `from`/`replyTo` nullable. Keep a static `EmailMessage.of(to, cc, subject, html, text)` compat factory (from=null) so existing callers compile.
- `SmtpEmailSender.send(EmailMessage)` sets `From`(+display) / `Reply-To` / `Cc` / `Bcc`, reading host/port/from-fallback/timeout from `SmtpConfigService` (Task 4 — inject it; if that task lands after, inject and use in the same PR ordering, else read static `spring.mail.*` and switch in T4). Password stays from `spring.mail.password`.

- [ ] **Step 1: Failing tests**

```java
// EmailMessageTest
@Test void ccAndBccDefaultToEmpty_fromOptional() {
  var m = new EmailMessage(List.of("a@x.com"), null, null, null, null, "s", "<p>h</p>", null);
  assertThat(m.cc()).isEmpty(); assertThat(m.bcc()).isEmpty(); assertThat(m.from()).isNull();
}
@Test void emptyTo_throws() {
  assertThatThrownBy(() -> new EmailMessage(List.of(), List.of(), List.of(), null, null, "s", "<p>h</p>", null))
    .isInstanceOf(IllegalArgumentException.class);
}
// SmtpEmailSenderTest (mock JavaMailSender + capture MimeMessageHelper effects)
@Test void setsFromReplyToCcBcc() { /* verify helper.setFrom/​setReplyTo/​setCc/​setBcc called with resolved values */ }
```

- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** the record change (+ compat factory) and the sender wiring (`MimeMessageHelper`: `setFrom`, `setReplyTo`, `setTo`, `setCc`, `setBcc`, `setSubject`, `setText(html,true)` + plain alt). Add `EmailSmtpValidator` (`@PostConstruct`, mirror `JwtSecretValidator`): if the resolved `enabled=true` and `spring.mail.password` blank → throw under the `production` profile, warn otherwise.
- [ ] **Step 4: Run → PASS** (all three test classes).
- [ ] **Step 5: Commit** — `feat(email): EmailMessage from/replyTo/bcc + SmtpEmailSender wiring + SMTP secret fail-fast`.

---

### Task 4: `TemplateRenderer` + `SmtpConfigService`

**Files:** Create `service/email/TemplateRenderer.java`, `service/email/SmtpConfigService.java`. Test: `service/email/TemplateRendererTest.java`, `service/email/SmtpConfigServiceTest.java`.

**Interfaces — Produces:**
- `TemplateRenderer.render(String template, Map<String,Object> vars) : String` — replaces `{{key}}` (HTML-escaped) and `{{!key}}` (raw); missing key → "" + `log.warn`. Handles `$` in substitutions safely (no `Matcher.appendReplacement` breakage — mirror the existing regression guard in `EmailTemplateServiceImpl`).
- `SmtpConfigService.get() : SmtpConfig` (cached; `invalidate()` on update); `resolvedFromAddress()` helper.

- [ ] **Step 1: Failing tests**

```java
@Test void escapesByDefault_rawOptIn_missingEmpty() {
  var r = new TemplateRenderer();
  assertThat(r.render("Hi {{name}}", Map.of("name","<b>&x</b>")))
      .isEqualTo("Hi &lt;b&gt;&amp;x&lt;/b&gt;");
  assertThat(r.render("{{!raw}}", Map.of("raw","<a href=\"#\">L</a>"))).isEqualTo("<a href=\"#\">L</a>");
  assertThat(r.render("[{{missing}}]", Map.of())).isEqualTo("[]");
}
@Test void dollarInValueDoesNotBreak() {
  assertThat(new TemplateRenderer().render("Owed {{amt}}", Map.of("amt","$5"))).isEqualTo("Owed $5");
}
```

- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** `TemplateRenderer` by lifting the substitution logic out of `EmailTemplateServiceImpl` (keep it identical; use `Matcher.quoteReplacement`). Implement `SmtpConfigService` (inject `SmtpConfigRepository`, cache the id=1 row).
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): shared TemplateRenderer + SmtpConfigService`.

---

### Task 5: `EmailService.sendTemplated` + `resend`

**Files:** Create `service/email/EmailService.java`. Test: `service/email/EmailServiceTest.java`.

**Interfaces — Consumes:** `EmailTemplateRepository`, `EmailLogRepository`, `SmtpConfigService`, `TemplateRenderer`, `EmailSender`. **Produces:**
- `sendTemplated(String templateKey, Map<String,Object> vars, SendOverrides overrides, SourceRef source) : EmailLog` where `SendOverrides(List<String> to, List<String> cc, List<String> bcc)` (all nullable) and `SourceRef(String module, Long id)` (nullable).
- `resend(Long logId) : EmailLog` (rebuild `EmailMessage` from the log snapshot, re-send, on success SENT / on failure FAILED + reset `next_attempt_at`).
- Recipient resolution: `to = overrides.to ?? split(template.to_default)` (throw `IllegalArgumentException` if empty); cc/bcc likewise; `from = template.from_address ?? smtpConfig.from_address`.

- [ ] **Step 1: Failing tests** (Mockito; stub template, sender, repos)

```java
@Test void success_writesSentLog_callsSender() {
  // template found; sender.send() ok
  EmailLog log = svc.sendTemplated("K", Map.of("x","1"), null, new SourceRef("PARTIAL_CREDIT", 7L));
  assertThat(log.getStatus()).isEqualTo(EmailStatus.SENT);
  assertThat(log.getSentDate()).isNotNull();
  verify(emailSender).send(any(EmailMessage.class));
  verify(emailLogRepository, times(2)).save(any()); // PENDING then SENT
}
@Test void senderThrows_writesFailed_setsNextAttempt() {
  doThrow(new RuntimeException("smtp down")).when(emailSender).send(any());
  EmailLog log = svc.sendTemplated("K", Map.of(), null, null);
  assertThat(log.getStatus()).isEqualTo(EmailStatus.FAILED);
  assertThat(log.getErrorMessage()).contains("smtp down");
  assertThat(log.getNextAttemptAt()).isNotNull();
}
@Test void overrideBeatsDefault_fromFallsBackToSmtp() { /* to override used; from=smtp when template.from null */ }
@Test void noRecipients_throws() { /* template to_default null + no override → IllegalArgumentException, no send */ }
```

- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** `EmailService`: load+validate template → `TemplateRenderer.render(subject/html/plain)` → resolve recipients/from → save `EmailLog(status=PENDING, snapshot)` → `try { emailSender.send(msg); status=SENT; sentDate=now } catch { status=FAILED; error; nextAttemptAt=now+backoff(0) }` → save. `backoff(n)` = `Duration.ofMinutes(2^n)` capped (shared static, reused by T6). Address split helper: `Arrays.stream(csv.split(",")).map(trim).filter(notBlank)`.
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): general EmailService (render → resolve → send → audit)`.

---

### Task 6: `EmailRetryWorker`

**Files:** Create `service/email/EmailRetryWorker.java`. Test: `service/email/EmailRetryWorkerTest.java`.

**Interfaces — Consumes:** `EmailLogRepository`, `SmtpConfigService`, `EmailService.resend`, a `Clock`. **Produces:** `@Scheduled` + `@SchedulerLock` method `retryPending()`.

- [ ] **Step 1: Failing tests**

```java
@Test void rescuesStalePending_thenRetriesFailed() {
  // one PENDING created 10 min ago → flipped to FAILED w/ next_attempt_at=now
  // one FAILED, retry_count=1 (< max 3), next_attempt_at in past → resend() called
  worker.retryPending();
  verify(emailService).resend(failedRow.getId());
  assertThat(stalePending.getStatus()).isEqualTo(EmailStatus.FAILED);
}
@Test void skipsWhenRetryCountAtMax() { /* retry_count=3, max=3 → not selected, resend never called */ }
@Test void skipsFutureNextAttempt() { /* next_attempt_at in future → not selected */ }
```

- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** `EmailRetryWorker.retryPending()`: (a) `emailLogRepository` update PENDING rows with `created_date < now - staleThreshold` → FAILED + `next_attempt_at=now`; (b) fetch `findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(FAILED, now, smtpConfig.maxRetryAttempts)`; for each call `emailService.resend(id)`. Annotate `@Scheduled(fixedDelayString="${email.retry.fixed-delay-ms:120000}")` + `@SchedulerLock(name="emailRetry", …)` (reuse the app's ShedLock config). `resend` must bump `retry_count` and set the next backoff on failure. Config `email.retry.stale-pending-min:5`.
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): ShedLock auto-retry worker + stale-PENDING rescue`.

---

### Task 7: Admin REST — SMTP config

**Files:** Modify `security/SecurityConfig.java`; Create `controller/admin/AdminEmailController.java` (SMTP methods only this task), `dto/email/{SmtpConfigView,SmtpConfigUpdate}.java`. Test: `controller/admin/AdminEmailControllerSmtpIT.java` (`@WebMvcTest` + imported `SecurityConfig`, mirror `RmaControllerTest` auth setup).

**Interfaces — Produces:** `GET /api/v1/admin/email/smtp` → `SmtpConfigView` (**no password field**); `PUT /api/v1/admin/email/smtp` (body `SmtpConfigUpdate` — ignores any password); `POST /api/v1/admin/email/smtp/test` → `{success, message}` (calls `JavaMailSender.testConnection()`), rate-limited.

- [ ] **Step 1: Failing tests**

```java
@Test void getSmtp_neverReturnsPassword() throws Exception {
  mvc.perform(get("/api/v1/admin/email/smtp").with(admin()))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$.password").doesNotExist())
     .andExpect(jsonPath("$.encryptedPassword").doesNotExist());
}
@Test void putSmtp_ignoresPasswordField() throws Exception { /* body incl. encryptedPassword; verify service update called WITHOUT it */ }
@Test void nonAdmin_returns403() throws Exception { /* Bidder token → 403 */ }
@Test void test_rateLimited_returns429_whenLimiterDenies() { /* stub limiter false → 429 */ }
```

- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** class-level `@PreAuthorize("hasRole('Administrator')")` + `@RequestMapping("/api/v1/admin/email")`; SMTP GET/PUT/test. Add `SecurityConfig` matcher `.requestMatchers("/api/v1/admin/email/**").hasRole("Administrator")` (before `anyRequest`). `SmtpConfigView` omits password entirely; `SmtpConfigUpdate` has no password field. `/test` gated by the rate limiter (inject `UploadRateLimiter` or a shared limiter, `clientIp` → 429).
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): admin SMTP config endpoints (password never exposed) + authz + rate-limit`.

---

### Task 8: Admin REST — Templates

**Files:** Modify `controller/admin/AdminEmailController.java`; Create `dto/email/{EmailTemplateView,EmailTemplateUpsert,PreviewRequest,SendTestRequest}.java`. Test: `controller/admin/AdminEmailControllerTemplatesIT.java`.

**Interfaces — Produces:** `GET /templates`, `POST /templates`, `GET/PUT/DELETE /templates/{id}`, `POST /templates/{id}/preview` (body `PreviewRequest{vars}` → rendered `{subject,html,text}`), `POST /templates/{id}/send-test` (body `SendTestRequest{toAddress, vars}` → sends via `EmailService`, rate-limited).

- [ ] **Step 1: Failing tests** — CRUD happy paths (create returns id; get lists; put updates `changed_date`; delete 204); preview renders `{{var}}`; send-test 200 + verifies `EmailService.sendTemplated` called; non-admin 403; send-test 429 when limiter denies.
- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** the template endpoints (standard CRUD delegating to `EmailTemplateRepository` + `TemplateRenderer` for preview + `EmailService` for send-test). `EmailTemplateView`/`EmailTemplateUpsert` records mapping all editable columns incl. to/cc/bcc/from/reply-to.
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): admin template CRUD + preview + send-test`.

---

### Task 9: Admin REST — Log (list / detail / resend)

**Files:** Modify `controller/admin/AdminEmailController.java`; Create `dto/email/EmailLogView.java`; add a paged filter query to `EmailLogRepository`. Test: `controller/admin/AdminEmailControllerLogIT.java`.

**Interfaces — Produces:** `GET /log?status=&from=&to=&templateKey=&page=&size=` → `Page<EmailLogView>`; `GET /log/{id}` → `EmailLogView` (incl. rendered `content_html`); `POST /log/{id}/resend` → `EmailLogView` (calls `EmailService.resend`).

- [ ] **Step 1: Failing tests** — list filters by status + date range (seed 3 rows, assert filter); detail returns the snapshot html; resend calls `EmailService.resend(id)` and returns the updated row; non-admin 403.
- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** the log endpoints; repository filter via `Specification` or a `@Query` with optional params + `Pageable`.
- [ ] **Step 4: Run → PASS.**
- [ ] **Step 5: Commit** — `feat(email): admin email-log list/detail/resend`.

---

### Task 10: Frontend wiring + M-3 sanitization

**Files:** Modify `frontend/.../email-admin/page.tsx` (+ any tab subcomponents). Test: `frontend/src/__tests__/emailAdmin.*.test.tsx` (RTL).

**Interfaces — Consumes:** the `/api/v1/admin/email/**` contract from Tasks 7-9.

- [ ] **Step 1: Failing tests (RTL)** — SMTP tab loads+saves (mock fetch), Templates tab lists+edits, Log tab lists+opens a detail; the detail view renders sanitized HTML (a `<script>` in the body is stripped).
- [ ] **Step 2: Run → FAIL** — `npm test -- emailAdmin`.
- [ ] **Step 3: Implement** — verify each tab's request/response shapes match the DTOs (align field names — the stub used snake_case; confirm vs the Java records and fix whichever drifts, preferring the backend contract). **Replace the regex `sanitizeEmailHtml`** with a real sanitizer: add `dompurify` + `@types/dompurify` (the one intentional new frontend dep — call it out in the commit) OR consume a server-sanitized `content_html`. Wire the SMTP `enabled` toggle + `send-test` + log `resend` buttons.
- [ ] **Step 4: Run → PASS** + `npm run build` succeeds.
- [ ] **Step 5: Commit** — `feat(email): wire Email Admin UI to backend + real HTML sanitization (M-3)`.

---

### Task 11: Migrate Partial Credit onto the unified system

**Files:** Modify `listener/partialcredit/ReviewCompletedEmailListener.java`, `controller/admin/AdminPartialCreditController.java`; remove the PC email-template route `frontend/.../partial-credit/email-templates/` + its launcher link. Test: update `ReviewCompletedEmailListenerTest` + add `PartialCreditEmailMigrationIT`.

**Interfaces — Consumes:** `EmailService.sendTemplated`.

- [ ] **Step 1: Update/failing test** — `ReviewCompletedEmailListenerTest` asserts the review-completed email now goes through `EmailService.sendTemplated(templateKey, vars, null, new SourceRef("PARTIAL_CREDIT", requestId))` and that an `email.log` row is written with `source_module='PARTIAL_CREDIT'`. `PartialCreditEmailMigrationIT`: after V92, the 3 PC template keys exist in `email.template`; completing a review writes to `email.log` (not `partial_credit.email_audit`).
- [ ] **Step 2: Run → FAIL.**
- [ ] **Step 3: Implement** — repoint the listener to `EmailService` (drop the direct `EmailSender`+`EmailTemplateService` path); remove the PC-specific template endpoints from `AdminPartialCreditController` (now in `AdminEmailController`); delete the PC template-editor route + nav link. Leave `partial_credit.email_audit` and its old rows untouched (D5). Keep the `partial-credit.review-completed-email.enabled` flag semantics.
- [ ] **Step 4: Run → PASS** — the full partial-credit suite + the new IT green.
- [ ] **Step 5: Commit** — `feat(email): migrate Partial Credit onto unified EmailService; retire PC template editor`.

---

## Final integration gate (after all tasks)
- [ ] `./mvnw -o test` — full backend suite green (incl. V92 + email + partial-credit).
- [ ] `cd frontend && npm run build && npm test` — green.
- [ ] Manual smoke (LoggingEmailSender): create a template in Email Admin → send-test → see an `email.log` SENT row; force a failure (bad recipient) → FAILED row → auto-retry worker flips/retries; PC review-completed still emails.
