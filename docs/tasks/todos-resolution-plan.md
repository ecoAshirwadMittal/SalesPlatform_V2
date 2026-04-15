# Implementation Plan: Three-Theme TODO Resolution (Email, Snowflake, Auth Cookies)

## 1. Overview

This plan sequences three remediation themes derived from 8 TODOs found in the codebase (7 backend, 1 frontend).

| Theme | Why | Outcome |
|-------|-----|---------|
| 1. Email Delivery | `PWSEmailService` currently no-ops; buyers receive no offer/order/counter-offer/pending-order mail, breaking Mendix parity | SMTP-backed, async, retryable, post-commit email via Thymeleaf templates matching legacy styling |
| 2. Snowflake Sync | Legacy `SUB_Offer_UpdateSnowflake` never ported; analytics warehouse is stale | Per-offer push on commit + admin catch-up endpoint, durable `snowflake_sync_log` audit with idempotent MERGE |
| 3. Auth Cookie Hardening | JWT stored in `localStorage` is XSS-exfiltratable | JWT moved to HttpOnly/Secure/SameSite=Strict cookie; frontend no longer touches token |

### Source TODOs

**Backend (7):**
1. `backend/src/main/java/com/ecoatm/salesplatform/service/PWSEmailService.java:253` — Wire JavaMailSender / SMTP
2. `backend/src/main/java/com/ecoatm/salesplatform/controller/PWSAdminController.java:303` — Implement Snowflake sync
3. `backend/src/main/java/com/ecoatm/salesplatform/service/OfferService.java:349` — HTML offer email
4. `backend/src/main/java/com/ecoatm/salesplatform/service/OfferService.java:797` — Order confirmation email
5. `backend/src/main/java/com/ecoatm/salesplatform/service/OfferService.java:800` — Snowflake offer sync
6. `backend/src/main/java/com/ecoatm/salesplatform/service/OfferService.java:829` — Pending-order email
7. `backend/src/main/java/com/ecoatm/salesplatform/service/OfferReviewService.java:432` — PWS counter-offer email

**Frontend (1):**
8. `frontend/src/app/(auth)/login/LoginForm.tsx:46` — TODO(H-5): Migrate to HttpOnly Set-Cookie

**Shared foundation:** Themes 1 and 2 both need async execution + `@TransactionalEventListener(AFTER_COMMIT)`. Build this once in **Phase 0** before either theme begins.

---

## 2. Theme Plans

### Phase 0 — Shared Event/Async Foundation (blocks Theme 1 & 2) ✅ COMPLETE

Validated 2026-04-13. Shipped; Theme 1 & 2 unblocked.

| # | Task | Status | Evidence |
|---|------|--------|----------|
| 0.1 | `spring-retry` + `spring-aspects` deps | ✅ | `backend/pom.xml:67,71` |
| 0.2 | `AsyncConfig` with `emailExecutor` (2/5/100), `@EnableAsync`, `@EnableRetry` | ✅ | `backend/src/main/java/com/ecoatm/salesplatform/config/AsyncConfig.java:17-36` — bean name constant `EMAIL_EXECUTOR` |
| 0.3 | `snowflakeExecutor` bean (core=1, max=3, queue=200) | ✅ | `AsyncConfig.java:38-49` — queue capacity 200 (plan did not specify; kept) |
| 0.4 | `@TransactionalEventListener(AFTER_COMMIT)` smoke test — commit fires, rollback suppresses | ✅ | `backend/src/test/java/com/ecoatm/salesplatform/config/AsyncEventFoundationTest.java` — three `@Test`s: `executors_loaded`, `listener_firesOnCommit`, `listener_doesNotFireOnRollback` (uses `REQUIRES_NEW` + forced `IllegalStateException`) |

**Notes for downstream phases:**
- Inject executors via `@Qualifier(AsyncConfig.EMAIL_EXECUTOR)` / `@Qualifier(AsyncConfig.SNOWFLAKE_EXECUTOR)` — constants already exposed.
- `@Async("emailExecutor")` / `@Async("snowflakeExecutor")` string form also works.
- Smoke test uses `PostgresIntegrationTest` base — reuse for any follow-on event listener integration tests.

---

### Theme 1 — Email Delivery ✅ COMPLETE (2026-04-13)

Shipped: SMTP-backed PWS emails with Thymeleaf templates, async delivery on
`emailExecutor`, post-commit event dispatch, feature-flagged behind
`pws.email.enabled`. 83 tests green including a full-stack GreenMail E2E.
See `docs/deployment/environments.md` and `docs/architecture/decisions.md`
for operational details. Default in all environments is `pws.email.enabled=false`
(LoggingEmailSender) until QA sign-off; flip the flag + provide SMTP creds to
activate SmtpEmailSender.

| Phase | Status | Evidence |
|-------|--------|----------|
| 1.1 Dependencies & config | ✅ | `pom.xml` (mail, thymeleaf, greenmail, ognl); `application.yml` `pws.email.*` + `spring.mail.*` |
| 1.2 EmailSender abstraction | ✅ | `service/email/EmailSender.java`, `EmailMessage.java`, `LoggingEmailSender.java`, `SmtpEmailSender.java` (+ `@Retryable`); `SmtpEmailSenderTest`, `LoggingEmailSenderTest`, `EmailMessageTest` |
| 1.3 Thymeleaf templates | ✅ | `resources/templates/email/_layout.html`, `_rows.html`, `pws-offer-confirmation.html`, `pws-order-confirmation.html`, `pws-pending-order.html`, `pws-counter-offer.html`; `EmailTemplateRenderTest` (4 tests) |
| 1.4 PWSEmailService refactor | ✅ | `PWSEmailService.java` — 4 `@Async` methods, safeSend swallows failures; `PWSEmailServiceTest` (6 tests) |
| 1.5 Event wiring | ✅ | `service/email/PwsOfferEmailEvent.java` (sealed), `PwsOfferEmailListener.java` (`@TransactionalEventListener(AFTER_COMMIT)` + `REQUIRES_NEW`); publishers at `OfferService.submitOffer` (offerConfirmation), Oracle success (orderConfirmation), Oracle error (pendingOrder), `OfferReviewService.completeReview` counter path (counterOffer) |
| 1.6 E2E + docs | ✅ | `PWSEmailServiceIT` drives template + SMTP + GreenMail end-to-end |

<details><summary>Original sub-phase breakdown (historical)</summary>

#### Phase 1.1 — Dependencies & Config

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.1.1 | Add `spring-boot-starter-mail`, `spring-boot-starter-thymeleaf`, `greenmail-junit5` (test) | `backend/pom.xml` | Build green | n/a | Revert pom |
| 1.1.2 | Add `pws.email.*` config keys (`enabled=false`, `from`, `sales-address`, `counter-offer-url`) and `spring.mail.*` placeholders | `application.yml`, `application-dev.yml` | App starts with defaults | Config binding test | Revert yml |

#### Phase 1.2 — EmailSender abstraction

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.2.1 | Define `EmailSender` interface + `EmailMessage` record (to, cc, subject, htmlBody, textBody) | `service/email/EmailSender.java`, `service/email/EmailMessage.java` | Compiles | n/a | Delete |
| 1.2.2 | Implement `LoggingEmailSender` (`@ConditionalOnProperty pws.email.enabled=false matchIfMissing=true`) | `service/email/LoggingEmailSender.java` | Logs full message at INFO | Unit test asserts log output via `CapturedOutput` | Delete |
| 1.2.3 | Implement `SmtpEmailSender` using `JavaMailSender` (`@ConditionalOnProperty pws.email.enabled=true`) with `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))` | `service/email/SmtpEmailSender.java` | Sends MIME multipart HTML | GreenMail integration test: send → assert inbox | Delete file + flip flag |

#### Phase 1.3 — Thymeleaf templates (Mendix parity)

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.3.1 | Create `_layout.html` fragment with Trebuchet MS / `#514F4E` / `#2CB34A` inline styles | `resources/templates/email/_layout.html` | Rendered HTML byte-matches legacy snapshot | Snapshot test against fixture HTML from `migration_context/backend/services/SUB_SendPWSCounterOfferEmail.md` | Delete |
| 1.3.2 | Create `pws-offer-confirmation.html` | same dir | Renders with offer model | Thymeleaf render test + snapshot | Delete |
| 1.3.3 | Create `pws-order-confirmation.html` | same dir | Renders | Render test | Delete |
| 1.3.4 | Create `pws-pending-order.html` | same dir | Renders | Render test | Delete |
| 1.3.5 | Create `pws-counter-offer.html` with counter-offer URL | same dir | Link uses `pws.email.counter-offer-url` | Render test | Delete |

#### Phase 1.4 — PWSEmailService refactor + recipient resolution

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.4.1 | Refactor `PWSEmailService` to four methods (`sendOfferConfirmationEmail`, `sendOrderConfirmationEmail`, `sendPendingOrderEmail`, `sendCounterOfferEmail`), each: resolve recipients via `EcoATMDirectUserRepository.findByBuyerCodeId`, render template, delegate to `EmailSender` | `service/PWSEmailService.java` | Old no-op stubs replaced | Unit tests per method with mock `EmailSender` + mock repo; assert recipient list matches `SUB_SendPWSCounterOfferEmail.md` rules | Revert file |
| 1.4.2 | Mark methods `@Async("emailExecutor")`; failures logged, never thrown to caller | same | Exceptions caught, counter metric incremented | Test: `EmailSender` throws → service logs, does not propagate | Revert |

#### Phase 1.5 — Event wiring at call sites

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.5.1 | Define `OfferEmailEvent` sealed type with subtypes: `OfferConfirmation`, `OrderConfirmation`, `PendingOrder`, `CounterOffer` | `event/OfferEmailEvent.java` | Compiles | n/a | Delete |
| 1.5.2 | Create `OfferEmailEventListener` with `@TransactionalEventListener(AFTER_COMMIT)` dispatching to `PWSEmailService` | `event/OfferEmailEventListener.java` | Listener bean loads | Test: publish each subtype, verify right service method invoked | Delete |
| 1.5.3 | `OfferService.java:349` — replace direct call with `applicationEventPublisher.publishEvent(new OfferConfirmation(...))` | `service/OfferService.java` | No inline email | Existing offer tests still pass; new test asserts event published | Revert line |
| 1.5.4 | `OfferService.java:797` — publish `OrderConfirmation` event | same | Same | Test | Revert |
| 1.5.5 | `OfferService.java:829` — publish `PendingOrder` event | same | Same | Test | Revert |
| 1.5.6 | `OfferReviewService.java:432` — publish `CounterOffer` event | `service/OfferReviewService.java` | Same | Test | Revert |

#### Phase 1.6 — Enable & verify

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 1.6.1 | End-to-end GreenMail test: place offer → assert 1 email in inbox with expected subject/body | `test/.../EmailE2ETest.java` | Green | Integration | Disable test |
| 1.6.2 | Document SMTP config & `pws.email.enabled` flag | `docs/deployment/environments.md`, `docs/architecture/decisions.md` | Doc updated | n/a | Revert md |

</details>

---

### Theme 2 — Snowflake Sync

#### Phase 2.1 — Dependencies & config scaffold

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.1.1 | Add `net.snowflake:snowflake-jdbc:3.16.1` | `pom.xml` | Build green | n/a | Revert |
| 2.1.2 | Add `snowflake.*` config keys (`enabled=false`, jdbc-url, user, password, warehouse, database, schema, role, pool.maximum-size=3) | `application.yml`, `application-dev.yml` | Keys bind | Config test | Revert |

#### Phase 2.2 — Database schema

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.2.1 | Flyway `V25__create_snowflake_sync_log.sql` — table `integration.snowflake_sync_log(id, offer_id, status, attempt_count, last_error, synced_at, created_at)` + index on `(status, created_at)` | `db/migration/V25__...sql` | Migration runs | Flyway test | Manual `DROP TABLE` (new env only) |
| 2.2.2 | Flyway `V26__create_snowflake_config.sql` — singleton row mirroring `OracleConfig` schema | `V26__...sql` | Runs | Flyway test | Drop |
| 2.2.3 | JPA entities `SnowflakeSyncLog`, `SnowflakeConfig` + repositories (`findByStatusIn`, `findSingleton`) | `model/integration/`, `repository/integration/` | CRUD works | Repo `@DataJpaTest` | Delete |

#### Phase 2.3 — Secondary DataSource

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.3.1 | Mark existing Postgres `DataSource`/`EntityManagerFactory`/`TransactionManager` `@Primary` | `config/DataSourceConfig.java` | App still starts; existing tests pass | Full suite | Revert annotation |
| 2.3.2 | Create `SnowflakeDataSourceConfig` (`@ConditionalOnProperty snowflake.enabled=true`) with HikariCP pool (max 3), exposes `snowflakeDataSource` bean | `config/SnowflakeDataSourceConfig.java` | Bean loads only when enabled | Context test with property toggled | Delete |

#### Phase 2.4 — SnowflakeSyncService

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.4.1 | `SnowflakeSyncService.syncOffer(Long offerId)`: load offer, build `PreparedStatement` with `MERGE INTO PWS.OFFERS_FACT USING (...) ON offer_id = ?` matching target columns from legacy `SUB_Offer_UpdateSnowflake` | `service/SnowflakeSyncService.java` | Idempotent upsert | Unit test with mock JDBC asserting SQL shape; integration test with Snowflake emulator or tagged manual | Delete |
| 2.4.2 | Wrap sync with log lifecycle: insert PENDING → on success update SYNCED + `synced_at` → on exception update FAILED + `last_error`, increment `attempt_count` | same | Log row always reflects latest | Unit tests for success/failure paths | Revert |
| 2.4.3 | `runBulkCatchup()`: find `status IN ('FAILED','PENDING')`, re-call `syncOffer` sequentially, return summary | same | Returns counts `{attempted, succeeded, failed}` | Unit test with seeded log rows | Revert |

#### Phase 2.5 — Event wiring (per-offer)

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.5.1 | Define `OfferOrderedEvent(offerId)` | `event/OfferOrderedEvent.java` | Compiles | n/a | Delete |
| 2.5.2 | `SnowflakeSyncEventListener` with `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`; inserts PENDING log row then calls `syncOffer` | `event/SnowflakeSyncEventListener.java` | Post-commit only | Test: commit fires, rollback does not | Delete |
| 2.5.3 | `OfferService.java:800` — publish `OfferOrderedEvent` | `service/OfferService.java` | Event published | Test | Revert |

#### Phase 2.6 — Admin bulk endpoint

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 2.6.1 | `PWSAdminController.java:303` → add `POST /api/v1/pws/admin/snowflake/sync` calling `runBulkCatchup`, role-gated (`@PreAuthorize("hasRole('ADMIN')")`) | `controller/PWSAdminController.java` | 200 returns summary; 403 for non-admin | MockMvc tests for both cases | Revert controller |
| 2.6.2 | Document endpoint | `docs/api/rest-endpoints.md` | Doc updated | n/a | Revert |

---

### Theme 3 — Auth Cookie Hardening ✅ CODE COMPLETE (2026-04-13)

Shipped: JWT moved from `localStorage` to an HttpOnly / SameSite=Strict
`auth_token` cookie. Backend sets the cookie at `/auth/login` and expires
it at new `/auth/logout`; filter accepts either the Authorization header
or the cookie. Frontend `apiFetch` uses `credentials: 'include'`,
`LoginForm` no longer touches the token, both dashboard + pws layout
logout handlers POST to `/auth/logout` + broadcast across tabs. 15/15
backend auth tests and 90/90 frontend unit tests green. Playwright spec
`tests/auth/auth-cookie.spec.ts` is committed and parses; **live run
must happen in the atomic deploy window** with both servers up.

| Phase | Status | Evidence |
|-------|--------|----------|
| 3.1 Backend cookie issuance | ✅ | `AuthController` (`/login` sets cookie, `/logout` expires it), `LoginResponse.token` `@JsonIgnore`d, `JwtAuthenticationFilter` cookie fallback, `SecurityConfig` permits `/logout`, `application.yml` `auth.cookie.secure` (default false, production profile true); `AuthControllerTest` 15/15 (added `login_*_setsHttpOnlyCookie*`, `login_withRememberMe_setsLongerMaxAge`, `logout_clearsCookie`, `me_withCookieToken_returnsUserInfo`); ADR in `docs/architecture/decisions.md` |
| 3.2 Frontend atomic migration | ✅ | `lib/apiFetch.ts` → `credentials: 'include'` (no Authorization header); `LoginForm.tsx` drops `localStorage.setItem('auth_token')` and `document.cookie`; `app/pws/layout.tsx` + `app/(dashboard)/layout.tsx` logout = `POST /auth/logout` + `BroadcastChannel('auth')`; `proxy.ts` comment updated (cookie path unchanged); `apiFetch-guard.test.ts` green; `tsc --noEmit` clean; 90/90 vitest |
| 3.3 E2E cookie verification | ⏸ spec committed | `frontend/tests/auth/auth-cookie.spec.ts` — 2 tests (HttpOnly + no localStorage; logout expiry + redirect). Awaits live run with backend up — part of deploy verification, not local CI |

Open items to close before the deploy window:
- SSO callback (`AuthController.handleSSORedirect` is still a stub) must
  also issue the same cookie when the real Azure AD flow lands.
- Confirm frontend and backend share a registrable domain in prod so
  `SameSite=Strict` still sends the cookie cross-subdomain (plan Open Q 3.1).

<details><summary>Original sub-phase breakdown (historical)</summary>

#### Phase 3.1 — Backend cookie issuance

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 3.1.1 | In `AuthController.loginExternalUser`, build `ResponseCookie.from("auth_token", token).httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(rememberMe ? Duration.ofHours(24) : Duration.ofHours(8))`; add via `Set-Cookie` header | `controller/AuthController.java` | Response has Set-Cookie | MockMvc test asserts cookie attrs | Revert |
| 3.1.2 | Remove `token` field from `LoginResponse`; keep user/roles | `dto/LoginResponse.java`, `AuthController.java` | Body has no token | Test | Revert |
| 3.1.3 | Add `auth.cookie.secure` config, `true` in `application.yml`, `false` in `application-dev.yml` | yml files | Dev can log in on `http://localhost` | Manual | Revert |
| 3.1.4 | `JwtAuthenticationFilter`: header lookup first, fall back to `auth_token` cookie via `request.getCookies()` | `security/JwtAuthenticationFilter.java` | Both work | Unit tests: header only, cookie only, both, neither | Revert |
| 3.1.5 | Add `POST /api/v1/auth/logout` returning `Set-Cookie: auth_token=; Max-Age=0; Path=/` | `AuthController.java`, `SecurityConfig.java` (permitAll) | 204 + expired cookie | MockMvc test | Revert |
| 3.1.6 | Document CSRF-disabled rationale (SameSite=Strict mitigates) | `docs/architecture/decisions.md` | ADR added | n/a | Revert |

#### Phase 3.2 — Frontend atomic migration

| # | Task | Files | Acceptance | Tests | Rollback |
|---|------|-------|------------|-------|----------|
| 3.2.1 | `apiFetch.ts`: remove `Authorization` header, add `credentials: 'include'` | `frontend/src/lib/apiFetch.ts` | All callers send cookies | Unit test mocking fetch | Revert |
| 3.2.2 | `LoginForm.tsx`: drop `localStorage.setItem('auth_token')` and `document.cookie` writes; rely on server-set cookie | `frontend/src/app/(auth)/login/LoginForm.tsx` | Login flow works E2E | Playwright login test | Revert |
| 3.2.3 | Audit & remove `localStorage.getItem('auth_token')` from ~12 files (grep confirms zero hits after) | list TBD via grep | `rg "auth_token"` returns only legit refs | Playwright smoke per affected page | Revert per file |
| 3.2.4 | Create `frontend/src/lib/serverFetch.ts` reading `cookies()` from `next/headers`, forwarding `Cookie: auth_token=...` | new file | SSR pages authed | RSC page render test | Delete |
| 3.2.5 | Migrate SSR call sites to `serverFetch` | various RSC pages | Pages render authed | Playwright | Revert |
| 3.2.6 | Update Next.js middleware to read cookie (already-working path) and verify signature path documented | `frontend/middleware.ts` | Middleware gates routes | Playwright | Revert |
| 3.2.7 | Logout: `POST /auth/logout` then `router.push('/login')`; add `BroadcastChannel('auth')` to sync multi-tab logout | `frontend/src/components/...` | All tabs log out | Manual multi-tab test | Revert |
| 3.2.8 | Update `proxy.ts` (if any) to forward `Cookie` header | `frontend/src/lib/proxy.ts` | Proxy flows work | Test | Revert |

#### Phase 3.3 — E2E verification

| # | Task | Acceptance |
|---|------|------------|
| 3.3.1 | Playwright login → protected page → logout → protected page redirects to /login | Green |
| 3.3.2 | Verify `document.cookie` in devtools shows NO `auth_token` (HttpOnly) | Confirmed |
| 3.3.3 | Verify no `localStorage.auth_token` | Confirmed |

---

## 3. Cross-Theme Sequencing

### Dependency Graph

```
        ┌──────────────────────────┐
        │ Phase 0: Async + Events  │
        │   (AsyncConfig,          │
        │    @TxnEventListener     │
        │    smoke test)           │
        └────────────┬─────────────┘
                     │
         ┌───────────┴──────────┐
         ▼                      ▼
  ┌─────────────┐        ┌─────────────┐         ┌──────────────┐
  │  Theme 1    │        │  Theme 2    │         │   Theme 3    │
  │  Email      │        │  Snowflake  │         │  Auth Cookie │
  │  (1.1→1.6)  │        │  (2.1→2.6)  │         │  (3.1→3.3)   │
  └─────────────┘        └─────────────┘         └──────────────┘
     depends on 0           depends on 0          INDEPENDENT
```

### Ship order & parallelism

| Slot | Ship | Why |
|------|------|-----|
| Can ship now, in parallel with everything | **Theme 3** (Auth cookies) | Zero backend service overlap, pure security fix. Must ship **atomically** (backend + frontend in one deploy window). |
| Must ship first among backend-services work | **Phase 0** foundation | Blocks Themes 1 & 2 |
| After Phase 0, parallelizable | **Theme 1** and **Theme 2** | Independent services, different files; one dev each |
| Theme 1 internal ordering | 1.1 → 1.2 → 1.3 → 1.4 → 1.5 → 1.6 | Templates and sender needed before service refactor; service needed before call sites |
| Theme 2 internal ordering | 2.1 → 2.2 → 2.3 → 2.4 → 2.5 → 2.6 | DataSource + schema before service; service before event wiring before admin endpoint |

### Atomic-deploy callouts

- **Theme 3** cannot go out in two deploys. Backend + frontend must land together or login breaks.
- **Themes 1 & 2** are feature-flagged (`pws.email.enabled`, `snowflake.enabled`) — safe to merge dark, enable per environment.

---

## 4. Risk Register

| ID | Risk | Theme | Severity | Mitigation |
|----|------|-------|----------|------------|
| R1 | Thymeleaf templates drift from Mendix styling pixel-by-pixel | 1 | High | Snapshot tests against fixture HTML from `migration_context`; manual diff review in PR |
| R2 | Async email failures silently lost | 1 | Medium | Add Micrometer counter + log at ERROR; future: `email_log` audit table (open Q) |
| R3 | Wrong recipients (spam buyers) | 1 | Critical | Exhaustive unit tests cross-referencing `SUB_SendPWSCounterOfferEmail.md`; keep `pws.email.enabled=false` until QA sign-off |
| R4 | Email failure rolls back Oracle order | 1 | Critical | `@TransactionalEventListener(AFTER_COMMIT)` + `@Async`; listener catches all exceptions |
| R5 | Snowflake duplicate rows from at-least-once delivery | 2 | High | `MERGE ON offer_id` idempotency; nightly dedupe job as fallback |
| R6 | Second DataSource breaks Spring Data JPA repositories | 2 | High | Mark Postgres beans `@Primary` before adding Snowflake bean; full regression suite |
| R7 | Per-row INSERT becomes bottleneck at scale | 2 | Medium | Start simple; plan COPY INTO migration at 100× volume |
| R8 | Partial deploy of Theme 3 breaks login | 3 | Critical | Single atomic deploy, feature branch merged only when both sides ready |
| R9 | SameSite=Strict breaks email-link flows (users arrive unauthed) | 3 | Medium | Document behavior in ADR; consider `Lax` for marketing links if needed post-launch |
| R10 | SSR pages render logged-out because cookie not forwarded | 3 | High | `serverFetch.ts` forwards cookie; Playwright RSC test coverage |
| R11 | Multi-tab logout leaves other tabs with stale session | 3 | Low | `BroadcastChannel('auth')` broadcast on logout |
| R12 | `@TransactionalEventListener` fires in wrong phase and sends pre-commit | 1,2 | High | Phase 0 smoke test explicitly validates rollback suppression |

---

## 5. Open Questions

### Theme 1 — Email
1. Which SMTP provider for QA/Prod (SES, SendGrid, on-prem Exchange)? Affects credentials, rate limits, bounce handling.
2. Should pending-order mail CC the buyer, or internal sales only? Mendix parity check required.
3. Do we need an `email_log` audit table for deliverability tracking?

### Theme 2 — Snowflake
1. Exact target table/schema from legacy `SUB_Offer_UpdateSnowflake` microflow? Need column list.
2. One denormalized fact table or multiple fact tables (offer/offer_item)?
3. Should admin bulk endpoint be role-gated to `ADMIN` only or also `SALES_OPS`?
4. Snowflake auth: username/password vs key-pair (JWT)?

### Theme 3 — Auth Cookies
1. Are prod frontend and backend on the same registrable domain (required for cookie to be sent cross-origin with SameSite=Strict)?
2. SSO callback path — must also issue the same cookie; which controller/filter handles that today?
3. "Remember me" legacy duration — 24h confirmed, or longer?
4. Next.js middleware currently does JWT signature verification? If so, how is the signing key distributed to the edge?
