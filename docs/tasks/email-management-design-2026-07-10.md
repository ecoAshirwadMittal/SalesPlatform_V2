# Unified Email Management — Design Spec

**Date:** 2026-07-10
**Status:** Design (awaiting review → implementation plan)
**Goal:** A single, Mendix-style email-management capability: set up templates from the UI (subject, body, and editable to/cc/from/reply-to), configure SMTP from the UI, and track every send (sent / failed) with resend + auto-retry — for the **whole app**, with Partial Credit migrated onto it.

---

## 1. Context — most of this is scaffolded, not greenfield

Discovered during brainstorming (cite real paths):
- **Transport already exists.** `service/email/SmtpEmailSender.java` (JavaMailSender), `spring-boot-starter-mail` in `pom.xml`, `spring.mail` + `pws.email.enabled` (default `false`) in `application.yml`. `service/email/LoggingEmailSender.java` is the default when disabled. `EmailSender.send(EmailMessage)` is the seam.
- **`EmailMessage`** (`service/email/EmailMessage.java`) is an immutable record with `to`, `cc`, `subject`, `htmlBody`, `textBody` — **no `from`/`replyTo`/`bcc`** yet.
- **A live, PC-scoped template system:** `partial_credit.email_templates` (subject + `body_html` + `body_text`, `{{var}}`/`{{!var}}` substitution) via `EmailTemplateServiceImpl`, and `partial_credit.email_audit` (recipient, success, error_message). Consumed by `ReviewCompletedEmailListener` on the async `EMAIL_EXECUTOR` + `@TransactionalEventListener(AFTER_COMMIT)` pattern.
- **A stubbed general Admin UI with no backend:** `frontend/.../admin/app-control-center/email-admin/page.tsx` — three tabs (SMTP Settings / Email Templates / Email Log) that call `/api/v1/admin/email/**`, which **does not exist**. Its TypeScript interfaces already model the target: `SmtpConfig`, a general `EmailTemplate` (with `to_default/cc_default/bcc_default/from_address/reply_to`), and `EmailLogEntry` (`status`, `error_message`, `retry_count`).
- **An `email` schema is already provisioned** in Flyway (`application.yml` schemas list: `…,integration,email,admin`) but has no tables.

**So the build = fill the `email` schema + a general `EmailService` + the admin backend for the stub UI + migrate PC onto it.** Transport is done.

## 2. Decisions (locked in brainstorming)

| # | Decision |
|---|---|
| D1 | **One unified system.** Migrate Partial Credit's emails onto the general module. |
| D2 | **SMTP settings in UI, password from env.** `email.smtp_config` holds host/port/from/tls/etc.; the password comes only from `EMAIL_SMTP_PASSWORD` (mapped to `spring.mail.password`). Fail-fast at startup if `enabled=true` but no password (mirrors `JwtSecretValidator`). |
| D3 | **Manual resend + auto-retry.** A `Resend` action on failed rows, plus a scheduled worker that auto-retries transient failures up to `max_retry_attempts` with backoff. |
| D4 | **Consolidate the PC editor** into the one Email Admin screen (retire the PC-specific template editor). |
| D5 | **Leave old PC audit rows** in place (`partial_credit.email_audit` frozen, historical); all new sends write `email.log`. |

## 3. Data model — `email` schema (new tables; Flyway `V92`)

### `email.smtp_config` (singleton, id=1)
`id, server_host, server_port, protocol, from_address, from_display_name, reply_to, use_ssl BOOLEAN, use_tls BOOLEAN, enabled BOOLEAN, max_retry_attempts INT, timeout_ms INT, created_date, changed_date, changed_by_id`.
**No password column** (D2). One seeded disabled row.

### `email.template`
`id, template_key VARCHAR UNIQUE (^[A-Za-z0-9_]+$), template_name, subject, content_html TEXT, content_plain TEXT, from_address, from_display_name, reply_to, to_default, cc_default, bcc_default, has_attachment BOOLEAN DEFAULT false, enabled BOOLEAN DEFAULT true, description, created_date, changed_date, created_by_id, changed_by_id`.
- `to_default/cc_default/bcc_default` are comma-separated address lists (the editable recipients). Nullable — a programmatic send may supply them instead.
- `from_address` nullable → falls back to `smtp_config.from_address`.
- Keeps `{{varName}}` (escaped) / `{{!varName}}` (raw opt-in) substitution.

### `email.log`
`id, template_key, from_address, to_address, cc, bcc, subject, content_html TEXT (rendered snapshot), status VARCHAR (PENDING|SENT|FAILED), error_message TEXT, retry_count INT DEFAULT 0, next_attempt_at TIMESTAMPTZ, source_module VARCHAR, source_id BIGINT, sent_date TIMESTAMPTZ, created_date TIMESTAMPTZ DEFAULT now()`.
- Stores the **rendered snapshot** (subject + content + resolved recipients + from) so resend/retry is faithful even if the template later changes.
- `source_module`/`source_id` generalize PC's `credit_request_id` FK (e.g. `PARTIAL_CREDIT`/`<id>`).
- Indexes: `(status, next_attempt_at)` for the retry worker; `(sent_date DESC)`, `(source_module, source_id)`, `(template_key)`.

## 4. Transport changes (small)
- Extend `EmailMessage` → add `from`, `replyTo`, `bcc` (validated; `from`/`replyTo` optional, `bcc` defaults empty). Backward-compatible factory for existing callers.
- `SmtpEmailSender`: read `host/port/from/timeout/tls` from `email.smtp_config` (cached, invalidated on PUT) instead of only static `spring.mail.*`; **password stays from env** (`spring.mail.password`). Active when `smtp_config.enabled=true`; else `LoggingEmailSender`.
- Keep the `pws.email.enabled` flag working during migration (bridge), then converge on `smtp_config.enabled`.

## 5. `EmailService` — the general core (`service/email/EmailService`)
`sendTemplated(templateKey, Map<String,Object> variables, SendOverrides overrides, SourceRef source)`:
1. Load `email.template` (throw if missing/disabled).
2. Render `subject` + `content_html`/`content_plain` via the `{{var}}`/`{{!var}}` engine (moved out of `EmailTemplateServiceImpl` into a shared `TemplateRenderer`).
3. Resolve recipients: `overrides.to ?? template.to_default` (same for cc/bcc); `from = template.from_address ?? smtp_config.from_address`. Fail if no `to`.
4. Insert an `email.log` row `status=PENDING` (rendered snapshot).
5. `EmailSender.send(EmailMessage)`; on success → `status=SENT, sent_date=now`; on exception → `status=FAILED, error_message`, set `next_attempt_at` = `now + backoff(retry_count)`.
6. Runs on the existing async `EMAIL_EXECUTOR` + `AFTER_COMMIT` so a send never affects the business transaction.

### Auto-retry worker (D3)
`EmailRetryWorker` — `@Scheduled(fixedDelay)` + ShedLock (reuse the app's lock config), single-leader so retries never double-send. Each tick: **(a)** re-queue **orphaned `PENDING`** rows (created more than a few minutes ago and never resolved — e.g. an app crash between the log insert and the send) by flipping them to `FAILED` with `next_attempt_at=now`; then **(b)** select retryable failures — `WHERE status='FAILED' AND retry_count < smtp_config.max_retry_attempts AND next_attempt_at <= now()` — re-send from the snapshot; on success → SENT; on failure → `retry_count++`, new `next_attempt_at` (exponential backoff). When `retry_count` hits max the row stays FAILED and is no longer picked up (terminal). Manual `Resend` bypasses the count (admin-forced) and re-queues. This makes the log the single source of truth for delivery state — no email is silently lost.

## 6. Admin REST + UI — backend for the existing stub
Base `/api/v1/admin/email`, all `@PreAuthorize("hasRole('Administrator')")` (per the repo's Security Rules) + a SecurityConfig matcher.
- **SMTP:** `GET /smtp` (config, password never returned), `PUT /smtp` (settings; ignores any password field), `POST /smtp/test` (JavaMailSender connection test; rate-limited).
- **Templates:** `GET /templates`, `POST /templates`, `GET/PUT/DELETE /templates/{id}`, `POST /templates/{id}/preview` (render with sample vars), `POST /templates/{id}/send-test` (render + send to a supplied address; rate-limited).
- **Log:** `GET /log?status=&from=&to=&templateKey=&page=` (filtered/paged), `GET /log/{id}` (detail incl. rendered HTML), `POST /log/{id}/resend`.
- **Frontend:** the stub page already calls these paths — wire it up. **Apply the M-3 fix**: replace the regex `sanitizeEmailHtml` stopgap on the log HTML view with proper sanitization (DOMPurify or server-sanitized), now that real HTML flows.

## 7. Partial-Credit migration
- Flyway data migration: copy the 3 `partial_credit.email_templates` rows → `email.template` (map `body_html`→`content_html`, `body_text`→`content_plain`; leave address defaults null → uses smtp_config from).
- Repoint `ReviewCompletedEmailListener` + `AdminPartialCreditController` template endpoints to `EmailService` / the general template store; delete the PC-specific `EmailTemplateServiceImpl` template path (keep the render engine as the shared `TemplateRenderer`).
- New PC sends write `email.log` with `source_module='PARTIAL_CREDIT'`, `source_id=creditRequestId`. `partial_credit.email_audit` is frozen (D5), old rows remain queryable.
- Retire the PC template-editor screen (D4); its templates now appear in Email Admin.
- **Acceptance:** the PC review-completed email still sends end-to-end (existing `ReviewCompletedEmailListenerTest` + IT green after the repoint).

## 8. Security & consistency
- SMTP password **only** from env; `EmailSmtpValidator` (`@PostConstruct`) throws under the `production` profile if `smtp_config.enabled=true` and no password (mirrors `JwtSecretValidator`).
- All admin endpoints Administrator-gated + matcher; `/smtp/test` and `/send-test` rate-limited (reuse `AuthRateLimiter`/`UploadRateLimiter` pattern) since they trigger outbound mail.
- Log detail HTML sanitized before render (closes the M-3 follow-up).

## 9. Testing
- **Unit:** `TemplateRenderer` (escape default, `{{!var}}` raw, missing-var warn, `$`-in-substitution regression), recipient resolution (override vs default vs smtp fallback), backoff schedule, smtp-config validation.
- **IT:** template CRUD (`AdminEmailControllerIT`), `email.log` written on send success + failure, resend + auto-retry worker (with `LoggingEmailSender`/a throwing stub), `V92` migration IT, PC-migration IT (review email still fires).
- **Frontend:** RTL for the three tabs' happy paths + the sanitized log view.

## 10. Out of scope (YAGNI — confirmed)
Attachments (keep `has_attachment` column, no upload mgmt), template versioning, WYSIWYG editor (plain HTML textarea as today), bulk/marketing campaigns, inbound email / bounce handling.

## 11. Risks / unknowns
- **SMTP creds in a real env** — needs `EMAIL_SMTP_PASSWORD` + a reachable server to truly validate `/smtp/test`; dev uses `LoggingEmailSender`.
- **Migration ordering** — repointing the live PC listener must not drop in-flight emails; do it behind `smtp_config.enabled=false` (logging) until verified.
- **`email` vs `admin` schema** — both are pre-provisioned; confirm nothing else claims `email` before adding tables.
- Auto-retry worker must be idempotent under ShedLock (single leader) to avoid double-sends.
