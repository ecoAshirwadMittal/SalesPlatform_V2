# Architecture Decisions Log

Running log of notable technical decisions. Each entry is lightweight
ADR-style: context, decision, consequences. Newest first.

---

## 2026-04-18 — Aggregated Inventory: Snowflake sync via post-commit event + watermark-based incremental pull

**Status:** Accepted (Phases 0–8 of `docs/tasks/aggregated-inventory-snowflake-sync-plan.md`).

### Context

The Mendix legacy flow synced `AggregatedInventory` from the Snowflake
`Master_Inventory_List_Snapshot` table on every `AggregatedInventory_Home`
page open via a synchronous `JA_SnowflakeToMendix` microflow. The port needs to:

1. Avoid blocking the admin page on Snowflake response time (the legacy
   synchronous UX regularly stalled 30s+ on the first visit each day).
2. Not overwrite admin edits that flipped `is_total_quantity_modified = true`
   (honors the 2026-04-17 ADR).
3. Skip work when Snowflake has no newer data (Mendix used
   `AggregatedInventoryTotals.MaxUploadTime` for this; we need an equivalent).
4. Gate prod-only wiring so local dev / CI never hit Snowflake.

### Decision

- **Post-commit event bridge.** `AggregatedInventoryController.triggerSync`
  publishes `AggInventorySyncRequestedEvent`. `AggInventorySyncListener`
  handles it via `@TransactionalEventListener(phase = AFTER_COMMIT)` +
  `@Async("snowflakeExecutor")`. HTTP returns `202 Accepted` immediately;
  the actual sync runs on a dedicated 3-thread executor. Mirrors the
  2026-04-13 PWS email pattern.
- **Watermark-based incremental pull.** New table
  `auctions.week_sync_watermark (week_id, source, last_source_upload_at,
  last_synced_at, row_count)` PK `(week_id, source)`. Before each sync,
  `AggregatedInventorySnowflakeSyncService` runs a cheap
  `SELECT MAX(Upload_Time) FROM Master_Inventory_List_Snapshot
  WHERE Auction_Week=? AND Auction_Year=?`. If the returned timestamp is
  ≤ the stored `last_source_upload_at`, the sync short-circuits with
  status `SKIPPED_STALE`. Otherwise it runs the paginated data query and
  bumps the watermark on success.
- **Admin-override preservation.** The upsert filters out rows where
  `is_total_quantity_modified = true`. Those rows keep their
  admin-entered `total_quantity` and `dw_total_quantity`; all other
  columns still refresh from Snowflake. Matches the legacy Mendix
  `SUB_UpdateAggregatedInventory` branch that skipped the quantity
  overwrite when the flag was set.
- **Drop the `DEVICE_ID='Total'` row at the reader layer.** The
  Snowflake SQL appends a grand-total row via `UNION ALL`. Per the
  2026-04-17 ADR we compute totals at read time, so
  `SnowflakeAggInventoryReader` filters the Total row before batching.
- **Feature flag.** `snowflake.enabled` (default `false`) gates
  `SnowflakeDataSourceConfig`, the reader, the sync service, and the
  listener via `@ConditionalOnProperty`. When disabled, the trigger
  endpoint returns `{ "status": "SKIPPED_DISABLED" }` and no thread pool
  is provisioned. `SnowflakeSyncLogRepository` is always present so the
  status endpoint works in both modes.
- **Fail-fast on misconfigured prod.** `SnowflakeDataSourceConfig`
  throws at startup when `snowflake.enabled=true` and
  `username`/`password` are blank. Prevents silent "sync always returns
  `SKIPPED_DISABLED`" in a deploy that intended the opposite.
- **Frontend fire-and-forget.** `/admin/auctions-data-center/inventory`
  posts to the trigger endpoint on every week change and polls the
  status endpoint every 3s for up to 90s. `403` from the trigger
  (non-admin role) is swallowed so the read-only view still renders.

### Alternatives considered

- **Scheduled nightly pull (cron).** Rejected: admins expect the page
  to reflect the current day's Snowflake state, not yesterday's. A
  per-view trigger is cheaper overall because most weeks don't change
  intraday (the watermark short-circuits the work).
- **Synchronous pull inside the page request (Mendix 1:1).** Rejected
  — 30s+ latency on first visit each day is unacceptable and the
  Mendix UX regularly hung on it.
- **Materialize totals in `aggregated_inventory_totals` and have the
  sync write that too.** Rejected — the 2026-04-17 ADR already
  established read-time computation as the source of truth; writing
  both invites the exact drift bug the 2026-04-15 ADR fixed.
- **Revive Mendix-style full-table replace (truncate + insert).**
  Rejected — breaks admin-override preservation; an idempotent upsert
  keyed on `(week_id, product_id, merged_grade, datawipe)` is the
  correct shape.

### Consequences

- Admin inventory page latency is bounded by a single `202 Accepted`
  response and a 3s poll cadence; Snowflake outages never block the
  page render.
- An admin that edits a row mid-sync is safe: the override flag
  protects their quantity; all other fields will still catch up on the
  next sync.
- No DLQ in Phase 1. Failed syncs log via
  `integration.snowflake_sync_log` (status=`FAILED`, `error_message`
  populated). The status endpoint surfaces the failure; monitoring
  must tail the log table.
- Enabling prod delivery is a config-only change (`SNOWFLAKE_ENABLED=true`
  + credentials) with no redeploy.
- Test coverage: unit tests on reader, service (6 scenarios), listener
  (commit fires + rollback does not), controller (MockMvc
  `202`/`200`/`403`). Integration test is gated on
  `@EnabledIfEnvironmentVariable("SNOWFLAKE_IT", "true")` so it only
  runs in envs with real Snowflake creds.

### References

- Plan: `docs/tasks/aggregated-inventory-snowflake-sync-plan.md`
- Schema: `backend/src/main/resources/db/migration/V67__auctions_week_sync_watermark.sql`
- Related ADRs: 2026-04-17 (read-time totals computation),
  2026-04-13 (PWS email async pattern template)
- Mendix source:
  `migration_context/backend/services/JavaActions_JavaAction/JA_SnowflakeToMendix.md`,
  `SUB_UpdateAggregatedInventory.md`,
  `SUB_LoadAggregatedInventoryTotals.md`

---

## 2026-04-17 — Aggregated Inventory: compute totals at read time + keep quantity override flag

**Status:** Accepted (Phases 3.2 and 5.1 of `docs/tasks/aggregated-inventory-page-plan.md`).

### Context

Mendix `AggregatedInventoryTotals` stored one row per week with precomputed
sums. Mendix also exposed `isTotalQuantityModified` on `AggregatedInventory`
so a nightly sync could tell whether to overwrite an admin-edited quantity.
Porting 1:1 would duplicate state and require a scheduled refresher. The
legacy bug surface is the same one the buyer-overview ADR (2026-04-15) fixed.

### Decision

- **Totals computed at read time** from `auctions.aggregated_inventory`
  with weighted averages (`SUM(price * qty) / SUM(qty)`) rather than
  reading `auctions.aggregated_inventory_totals`. The totals table stays
  in the schema for future Snowflake export parity but is not read by the
  page.
- **Preserve `is_total_quantity_modified`** exactly as the legacy column.
  The PUT handler flips it to `true` on save, and the (future) inventory
  sync must honor the flag.
- **Excluded rows:** `is_deprecated = true` rows never appear in the grid
  or the KPI strip.

### Alternatives considered

- **Read `aggregated_inventory_totals` directly.** Rejected for the same
  reason as the buyer overview: the denormalized row drifts whenever an
  admin edit lands out-of-band. Recomputing is O(N) over ~87k rows
  filtered by `week_id` — sub-second with the `idx_agi_week` index.
- **Materialized view.** Over-engineered for 87k rows. Revisit if
  `EXPLAIN ANALYZE` regresses past 200 ms.

### Consequences

- The totals table is now informational only. A follow-up can either
  drop it or wire the Snowflake sync to populate it from the same query.
- Create Auction remains a stub until the auction scheduling module
  lands — the page's button shows a placeholder dialog rather than a
  404 or a dead action.

### References

- Plan: `docs/tasks/aggregated-inventory-page-plan.md`
- Schema: `backend/src/main/resources/db/migration/V60__auctions_aggregated_inventory.sql`
- Mendix source: `migration_context/frontend/components/Pages_Page/PG_AggregatedInventory.md`

---

## 2026-04-13 — Auth token moved from localStorage to HttpOnly cookie

**Status:** Accepted — backend portion shipped (Phase 3.1 of Theme 3,
`docs/tasks/todos-resolution-plan.md`). Frontend atomic migration
(Phase 3.2) tracked separately; must ship in the same deploy window.

### Context

The frontend stored the JWT in `localStorage` and attached it via an
`Authorization: Bearer …` header. Any XSS vulnerability — including
transitive ones in third-party React components — would allow an
attacker to exfiltrate the token and impersonate the user for its full
lifetime (up to 7 days with "remember me"). `TODO(H-5)` in
`frontend/src/app/(auth)/login/LoginForm.tsx:46` flagged this.

### Decision

- **Cookie-issued JWT.** `AuthController.loginExternalUser` now returns
  a `Set-Cookie: auth_token=…; HttpOnly; Secure; SameSite=Strict;
  Path=/; Max-Age=…` header on successful login. The token is no longer
  serialized in the JSON body (`LoginResponse.token` is `@JsonIgnore`d —
  kept internally as a carrier between `AuthService` and the controller).
- **TTL.** 8 hours for standard login, 24 hours when `rememberMe=true`.
  The JWT signing expiry (`app.jwt.*-expiration-ms`) is unchanged and
  remains the authoritative clock — the cookie `Max-Age` just stops the
  browser from presenting a token past the point the server would reject
  it anyway.
- **`Secure` flag is configurable.** `auth.cookie.secure` defaults to
  `false` so `http://localhost` dev works; the `production` profile and
  QA env var `AUTH_COOKIE_SECURE=true` flip it on. `HttpOnly` and
  `SameSite=Strict` are enforced in code and not configurable.
- **Filter fallback.** `JwtAuthenticationFilter` extracts the token from
  `Authorization` header first (server-to-server, tests, transitional
  clients), then from the `auth_token` cookie. Both paths feed the same
  `jwtService.isValid` + `SecurityContextHolder` pipeline.
- **Logout.** New `POST /api/v1/auth/logout` (permitAll) returns
  `204 No Content` with `Set-Cookie: auth_token=; Max-Age=0; …` to
  expire the cookie in the browser. The JWT itself is not revoked —
  without a denylist, logout is best-effort client-side.
- **CSRF stays disabled.** `SameSite=Strict` blocks cross-site form
  submissions from carrying the cookie, which is the realistic CSRF
  vector for a cookie-auth API. A full double-submit token scheme would
  add friction without meaningful risk reduction for this app's flows.

### Alternatives considered

- **Keep `localStorage` + add CSP.** Rejected: CSP reduces XSS surface
  but does not eliminate it; any inline-script escape still exposes the
  token. `HttpOnly` is a structurally stronger guarantee.
- **`SameSite=Lax`.** Considered for the eventual email-link flow (R9
  in the plan) where a GET navigation from an email client should
  arrive authed. Deferred: none of the current flows require it, and
  `Strict` is the stronger default. Revisit if marketing-link flows
  need it.
- **Revoke JWT on logout via denylist.** Rejected for Phase 1: no Redis
  in the stack yet, and the 8h/24h ceilings limit the blast radius.

### Consequences

- Frontend must stop reading/writing the token in any form —
  `apiFetch.ts` switches to `credentials: 'include'`, SSR fetches
  forward the `Cookie` header, and a ~12-file audit strips
  `localStorage.getItem('auth_token')` call sites. This work is Phase
  3.2 and must ship in the **same deploy** as the backend — half-deploys
  break login.
- Backward compatibility: the `Authorization` header path still works,
  so Postman collections, integration tests, and any lingering
  server-to-server consumer keep functioning during the transition.
- Test coverage: `AuthControllerTest` adds four new assertions
  (`login_withValidCredentials_setsHttpOnlyCookieAndOmitsTokenFromBody`,
  `login_withRememberMe_setsLongerMaxAge`, `logout_clearsCookie`,
  `me_withCookieToken_returnsUserInfo`). 15/15 green.
- Follow-ups:
  - **SSO callback cookie issuance.** `AuthController.handleSSORedirect`
    is still a pre-auth outbound redirect stub — there is no token to
    cookie yet. When the real callback lands it MUST reuse
    `buildAuthCookie(token, DEFAULT_TTL)` and attach the `Set-Cookie`
    header on the post-auth redirect. An inline `TODO(Theme 3)` now
    sits above the stub so the next SSO PR cannot miss it.
  - **Registrable-domain parity.** `SameSite=Strict` requires the
    frontend and backend in prod to share the same eTLD+1 for the
    browser to attach the cookie. Approved topologies and a
    verification checklist live in `docs/deployment/environments.md`
    under *Auth Cookie (Theme 3) → Registrable-domain parity*.
    Preferred deployment shape: reverse-proxy `/api/*` through the
    frontend origin so the question never arises.

### References

- Plan: `docs/tasks/todos-resolution-plan.md` (Theme 3, Phase 3.1)
- Source TODO: `frontend/src/app/(auth)/login/LoginForm.tsx:46`
- Files: `controller/AuthController.java`, `dto/LoginResponse.java`,
  `security/JwtAuthenticationFilter.java`, `security/SecurityConfig.java`,
  `resources/application.yml`

---

## 2026-04-13 — PWS email delivery: post-commit event + async + feature flag

**Status:** Accepted (Theme 1 of `docs/tasks/todos-resolution-plan.md`).

### Context

Mendix parity requires four buyer-facing notifications on the PWS flow:
offer confirmation, order confirmation, pending-order (adjusted quantity),
and counter offer. The legacy Mendix microflows (`SUB_SendPWS*Email.md`)
fire synchronously inside the offer/order transaction. Porting that shape
1:1 exposes three risks:

1. **R1 — transactional coupling:** an SMTP outage would fail an offer
   submission that the DB already accepted.
2. **R3 — wrong recipients in lower envs:** dev/CI must never reach real
   buyers, and QA must be enablable independently of prod.
3. **R4 — rollback leakage:** a notification sent before the tx commits
   would be visible to customers even if the offer rolls back.

### Decision

- **Post-commit event bridge.** `OfferService` / `OfferReviewService`
  publish a sealed `PwsOfferEmailEvent` via `ApplicationEventPublisher`
  from inside the business tx. `PwsOfferEmailListener` handles each variant
  with `@TransactionalEventListener(phase = AFTER_COMMIT)` +
  `@Transactional(propagation = REQUIRES_NEW, readOnly = true)`. Rollbacks
  drop the event — no mail is sent.
- **Async executor.** The listener hands the materialized aggregate to
  `PWSEmailService`, whose send methods run on a dedicated
  `emailExecutor` (`AsyncConfig.EMAIL_EXECUTOR`) via `@Async`. Offer
  submission latency is unaffected by SMTP response time.
- **Eager fetch across async boundary.** `OfferRepository.findByIdWithItems`
  uses `LEFT JOIN FETCH o.items` so the listener's short read-only tx
  produces a fully-initialized `Offer` before the aggregate crosses
  threads — avoiding `LazyInitializationException` on the @Async worker.
- **Feature flag.** `pws.email.enabled` (default `false`) swaps
  `LoggingEmailSender` ↔ `SmtpEmailSender` via `@ConditionalOnProperty`.
  Local dev and CI log payloads only; QA and prod enable per
  `docs/deployment/environments.md`.
- **Retry + swallow.** `SmtpEmailSender.send` is
  `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))`
  on `MailException`/`MessagingException`. After the final retry,
  `PWSEmailService.safeSend` catches the exception, logs
  `PWS email delivery failed template={} offerId={} offerNumber={}`, and
  never rethrows — addressing R1.
- **Recipient resolution via native queries.**
  `EcoATMDirectUserRepository.findActiveEmailsByBuyerCodeId` and
  `findBuyerCompanyNameByBuyerCodeId` encode the Mendix recipient rules.
  The adjusted-quantity template CCs `pws.email.sales-address`.
- **Templates.** Thymeleaf HTML templates under
  `resources/templates/email/pws-*.html`, rendered with Mendix-parity
  styling tokens (`Trebuchet MS`, `#514F4E`, `#B7B5B5`, `#2CB34A`,
  `ecoATM, LLC` footer).

### Alternatives considered

- **Synchronous in-tx send (Mendix 1:1).** Rejected: couples offer
  submission availability to SMTP, and rollbacks would leak mail.
- **Outbox table + poller.** Heavier than needed for Phase 1 and
  duplicates work that Theme 2 (`integration.snowflake_sync_log`) will
  introduce for a different purpose. Revisit if a retry budget / DLQ is
  required — see "No DLQ yet" in `environments.md`.
- **Spring `ApplicationEventPublisher` without `AFTER_COMMIT`.** Rejected:
  default sync listeners fire before commit, re-introducing R4.

### Consequences

- Offer submission latency is bounded by DB commit, not SMTP.
- An SMTP outage drops notifications silently after 3 retries; there is
  no dead-letter queue in Phase 1. Monitoring must watch the
  `PWS email delivery failed` log line.
- Enabling prod delivery is a config-only change (`PWS_EMAIL_ENABLED=true`
  + SMTP env vars) with no redeploy — see
  `docs/deployment/environments.md`.
- Test coverage: 6 unit tests (`PWSEmailServiceTest`) mock the template
  engine and sender to assert recipient branching and failure-swallowing;
  1 end-to-end test (`PWSEmailServiceIT`) wires a real `TemplateEngine` +
  `SmtpEmailSender` → GreenMail on `ServerSetupTest.SMTP` and asserts
  Mendix-parity markers in the rendered body.

### References

- Plan: `docs/tasks/todos-resolution-plan.md` (Theme 1)
- Config: `docs/deployment/environments.md`
- Mendix source: `migration_context/backend/SUB_SendPWSOfferEmail.md`,
  `SUB_SendPWSOrderEmail.md`, `SUB_SendPWSPendingOrderEmail.md`,
  `SUB_SendPWSCounterOfferEmail.md`
