# Unified Email Management — Phase 2 Follow-ups

**Date:** 2026-07-11
**Context:** The Phase-1 unified email-management build (Tasks 1–11, branch
`feat/email-management`) is complete, reviewed, and validated (full backend
suite green; a full-context `AdminEmailControllerSmokeIT` proves the admin
GET path against real Postgres; a live send-test wrote an `email.log` SENT
row). These items were **deliberately deferred** — none block Phase 1.
Sources: the whole-branch code review + per-task review deferrals.

## Architecture

### P2-1. Move the SMTP send outside the persistence transaction; collapse the double-retry
`EmailService.sendTemplated` currently INSERTs the `PENDING` row, calls
`emailSender.send(...)`, and UPDATEs to `SENT`/`FAILED` all inside **one**
`@Transactional`. Consequences:
- A pooled DB connection is held across the entire SMTP round-trip (in prod,
  `SmtpEmailSender.@Retryable` blocks up to ~6s: 2s×2 backoff × 3).
- Because the insert and the status update commit together, a **committed
  `PENDING` row never occurs** — so `EmailRetryWorker.rescueStalePending`
  (and its `findByStatusAndCreatedDateBefore` finder) guards a state that
  cannot arise (effectively dead code).
- `SmtpEmailSender.@Retryable` (3 attempts) now **stacks** on top of the
  `EmailRetryWorker` retry loop — two independent retry mechanisms.

**Fix:** commit `PENDING` → send **outside** the persistence tx → update status
in a second tx. Drop `SmtpEmailSender.@Retryable` (the worker owns retries).
This frees the connection during I/O and makes the stale-PENDING rescue
meaningful. Behavior change — needs its own tests.

## Security

### P2-2. `UploadRateLimiter` trusts a spoofable `X-Forwarded-For` (systemic)
Pre-existing from the 2026-07-10 security work (H-10). The first XFF hop is
client-controllable with no trusted-proxy allowlist, so every per-IP limit
(login, uploads, `/api/v1/admin/email/smtp/test`) is bypassable by rotating
the header. `/send-test` and `/log/{id}/resend` already key on the JWT
user-id (spoof-proof), so email's outbound-recipient triggers are covered;
the residual is the IP-keyed `/smtp/test` (a connection test, no arbitrary
recipient) and the other app-wide limiters. **Fix:** `ForwardedHeaderFilter`
behind a known trusted-proxy set, or bind XFF trust to the LB/ingress.

### P2-3. If SMTP host/port ever become admin-writable live targets, guard credential exfil
Design §4/§50 anticipates wiring `smtp_config.server_host`/`server_port` as
the live connection target. Today they are **display-only** (correct). If
wired naively, an Administrator (or a compromised/replayed admin session)
could `PUT /smtp` a `server_host` pointing at an attacker listener, and the
next send would hand it the **env SMTP credential** (`EMAIL_SMTP_PASSWORD`)
during AUTH — exfiltrating it without ever appearing in an API response (D2
still holds but is insufficient once the *destination* is attacker-steerable).
**Mitigation options:** keep the connection target env-pinned (DB row stays
display/from-address/retry only); OR host-allowlist; OR a distinct audited
"host change" approval flow. Wire `smtp_config.enabled` as a real send gate
at the same time (see P2-4).

### P2-4. Make `smtp_config.enabled` a real gate (Phase-1 left it inert)
`EmailService.sendTemplated` gates only on the **template's** `enabled`; the
actual Phase-1 send gate is the `pws.email.enabled` deploy property (selects
`SmtpEmailSender` vs `LoggingEmailSender` at startup). The `smtp_config.enabled`
column is read only by the view DTO. Phase 1 relabeled the admin toggle to be
honest ("SMTP Config Enabled … live sending controlled by deployment config").
Phase 2: converge on `smtp_config.enabled` as a runtime send gate (tie in with
P2-3), and retire the `pws.email.enabled` bridge.

## Cleanup

### P2-5. Remove orphaned frozen Partial-Credit symbols
Post-T11, these have **no runtime caller** (frozen per D5): `EmailAuditService`
(+ its IT), `model.partialcredit.EmailTemplate` entity, and
`repository.partialcredit.EmailTemplateRepository`. Dead code retained only for
historical `partial_credit.email_audit` rows. **Latent risk:** the frozen
`@Entity` still maps `partial_credit.email_templates`, so dropping that table
later would break startup. Remove the entity + repo + service (and optionally
drop the now-redundant `partial_credit.email_templates` table in a migration —
its 3 rows were copied into `email.template` by V92).

### P2-6. `(status, created_date)` partial index on `email.log`
The stale-PENDING rescue query (`WHERE status='PENDING' AND created_date < …`)
is unindexed. Fine for Phase 1 (PENDING is a tiny transient set). Add a partial
index `... ON email.log (created_date) WHERE status='PENDING'` if PENDING
volume ever grows.

### P2-7. `resendLog` non-transactional edge (note only)
`POST /log/{id}/resend` commits the `retry_count=0` reset, then calls `resend`
in a separate tx. If the final `save` inside `resend` throws (rare), the row is
left `retry_count=0, next_attempt_at=null` — which the worker's
`next_attempt_at <= now` finder excludes (NULL), so it isn't auto-retried until
another manual resend. Not a data leak; very low probability.

## Deploy

### P2-8. Inject `EMAIL_SMTP_PASSWORD` when enabling email in QA/prod
When email is enabled under the `production` profile, `EmailSmtpValidator`
**fails startup** on a blank `spring.mail.password`. Inject
`EMAIL_SMTP_PASSWORD` (bound to `spring.mail.password`) as part of the
pre-deploy secret checklist — alongside the JWT secret rotation from the
2026-07-10 security review.

## Pre-existing (not email; being fixed alongside this branch)
- Two frontend TS build errors (`AdminReviewClient.tsx` type mismatch;
  `useSearchParams()` Suspense boundary on `/wholesale/partial-credit/new`),
  most likely from the earlier H-12 Next.js upgrade — being fixed so
  `npm run build` goes green (they may have been red on `main` too).
- Two pre-existing `apiFetch-guard` frontend test failures (unrelated).
