# Environments & Feature Flags

Runtime configuration that varies per environment. Defaults in
`application.yml` are intentionally conservative — features that touch
customers (email, Snowflake, SSO) must be explicitly enabled per env.

## PWS Email Notifications

Mendix parity for the four PWS notification flows (offer confirmation,
order confirmation, adjusted-qty pending order, counter offer). Delivered
post-commit on the dedicated `emailExecutor` via a
`@TransactionalEventListener(AFTER_COMMIT)` bridge, so rollbacks never
produce customer-facing mail.

| Key | Default | Description |
|-----|---------|-------------|
| `pws.email.enabled` | `false` | Master flag. `false` → `LoggingEmailSender` (logs payload only). `true` → `SmtpEmailSender` (real delivery via `JavaMailSender`, retried 3× with 2s → 4s → 8s backoff on `MailException`/`MessagingException`). |
| `pws.email.from` | `noreply@ecoatmdirect.com` | `From` address on every outbound message. |
| `pws.email.sales-address` | `sales@ecoatmdirect.com` | CC target for `Order Pending — …` adjusted-quantity notifications. |
| `pws.email.counter-offer-url` | `https://buy.ecoatmdirect.com/p/counter-offer/` | Prefix for the "See Counter Offer" CTA button. The listener appends the offer ID to the end. |
| `spring.mail.host` | `localhost` | SMTP relay host. |
| `spring.mail.port` | `25` | SMTP relay port. |
| `spring.mail.username` | _(empty)_ | SMTP auth user. Leave blank for unauthenticated relays. |
| `spring.mail.password` | _(empty)_ | SMTP auth password. Inject via env/secret manager, never commit. |

### Enabling in an environment

1. Provide SMTP credentials via environment variables:
   ```bash
   export PWS_EMAIL_ENABLED=true
   export MAIL_HOST=smtp.office365.com
   export MAIL_PORT=587
   export MAIL_USERNAME=pws-notifier@ecoatm.com
   export MAIL_PASSWORD=<from secret manager>
   export PWS_EMAIL_FROM=noreply@ecoatmdirect.com
   export PWS_EMAIL_SALES=sales@ecoatmdirect.com
   export PWS_EMAIL_COUNTER_OFFER_URL=https://buy.ecoatmdirect.com/p/counter-offer/
   ```
2. Restart the backend. `SmtpEmailSender` replaces `LoggingEmailSender` via
   `@ConditionalOnProperty`. No code change or redeploy needed.
3. Trigger one offer submission as a QA user and verify the expected
   recipients receive the email — then repeat for an order and a counter
   offer. Rollback = unset `PWS_EMAIL_ENABLED` and restart.

### Environment defaults

| Env | `pws.email.enabled` | SMTP host | Rationale |
|-----|---------------------|-----------|-----------|
| local dev | `false` | `localhost:25` (unused) | Engineers see payloads in logs; no risk of spamming real buyers. |
| CI / test | `false` | GreenMail in-process | `PWSEmailServiceIT` flips an isolated sender to GreenMail on `ServerSetupTest.SMTP`. |
| QA (`buy-qa.ecoatmdirect.com`) | `true` (planned) | office365 relay | Enable after parity sign-off against `SUB_SendPWS*Email.md` microflows. |
| Prod | `true` (planned) | office365 relay | Follows QA. |

### Failure handling

- `PWSEmailService.safeSend` catches **all** exceptions per message, logs
  `PWS email delivery failed template={} offerId={} offerNumber={}`, and
  never rethrows — an SMTP outage cannot fail an offer submission.
- `SmtpEmailSender.send` is `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))`.
  After the final retry, the exception propagates into `safeSend`, is
  logged, and the event is dropped.
- No dead-letter queue yet — by design for Phase 1. When a notification
  retry budget is required, revisit once Snowflake (Theme 2) lands its
  `integration.snowflake_sync_log` pattern and reuse it.

## Auth Cookie (Theme 3)

The JWT is issued as an `HttpOnly; SameSite=Strict` cookie named
`auth_token` by `AuthController.loginExternalUser` (and `logout`, and
eventually the SSO callback — see `TODO(Theme 3)` in
`AuthController.handleSSORedirect`). The frontend sends it implicitly
via `credentials: 'include'`. No JS code path ever reads the token.

| Key | Default | Description |
|-----|---------|-------------|
| `auth.cookie.secure` | `false` (local), `true` (production profile) | Sets the `Secure` cookie attribute. Must be `false` over `http://localhost` so dev browsers accept the cookie; must be `true` anywhere the site is served over HTTPS — browsers drop `Secure` cookies on plaintext responses. Override with `AUTH_COOKIE_SECURE=true` env var in QA. |
| `app.jwt.expiration-ms` | `28800000` (8h) | JWT signing TTL. Cookie `Max-Age` is pinned to this for standard logins. |
| `app.jwt.remember-me-expiration-ms` | `86400000` (24h) | JWT TTL when `rememberMe=true`. Cookie `Max-Age` follows. |

### Registrable-domain parity (MUST verify before prod deploy)

`SameSite=Strict` blocks the cookie on any cross-site navigation, and
a cookie set on one host is only sent to hosts that share its
**registrable domain** (eTLD+1). The deployment plan must put the
Next.js frontend and the Spring backend on the **same eTLD+1** so the
browser presents the cookie to the API.

| Topology | Frontend | Backend | Cookie sent? |
|----------|----------|---------|--------------|
| ✅ Same host | `buy.ecoatmdirect.com` | `buy.ecoatmdirect.com/api` (reverse-proxied) | Yes |
| ✅ Same registrable domain | `buy.ecoatmdirect.com` | `api.ecoatmdirect.com` | Yes — both resolve to eTLD+1 `ecoatmdirect.com` |
| ❌ Cross-site | `buy.ecoatmdirect.com` | `api.ecoatm.com` | **No** — `SameSite=Strict` drops cross-site requests |

Verification checklist before enabling in an environment:

1. Confirm frontend and backend URLs share the same eTLD+1. If they do
   not, either reverse-proxy `/api/*` through the frontend origin
   (preferred — no CORS, no cookie-domain fiddling) or move one host
   under the other's domain.
2. If hosts differ but share eTLD+1, the cookie does **not** need an
   explicit `Domain=` attribute — browsers default to the exact host
   and `SameSite=Strict` evaluates against eTLD+1. Do not broaden the
   cookie with `Domain=.ecoatmdirect.com` unless another subdomain
   legitimately needs to present it.
3. From the target origin, run `curl -i -c - -X POST
   https://<frontend>/api/v1/auth/login …` and confirm the response
   carries `Set-Cookie: auth_token=...; HttpOnly; Secure; SameSite=Strict`.
4. From the browser, log in and then hit a protected route via
   DevTools → Network — the request must show the `auth_token` cookie
   attached. If it is missing, the two hosts are not sharing the
   registrable domain and the topology needs fixing before go-live.

## Snowflake Sync (Theme 2)

Wires `auctions.aggregated_inventory` to the upstream `Master_Inventory_List_Snapshot`
Snowflake table. Delivered post-commit on a dedicated `snowflakeExecutor` via a
`@TransactionalEventListener(AFTER_COMMIT)` bridge — `AggregatedInventoryController.triggerSync`
returns `202 Accepted` immediately and the actual pull runs asynchronously. Mirrors
the PWS email pattern (2026-04-13 ADR) and honors the admin-override flag from
the 2026-04-17 ADR.

| Var | Dev | QA | Prod | Description |
|-----|-----|----|------|-------------|
| `SNOWFLAKE_ENABLED` | `false` | `true` | `true` | Gates the entire Snowflake sync pipeline via `@ConditionalOnProperty`. When `false`, the trigger endpoint returns `{ "status": "SKIPPED_DISABLED" }` and no thread pool is provisioned. `SnowflakeSyncLogRepository` is always present so the status endpoint works in both modes. |
| `SNOWFLAKE_JDBC_URL` | `jdbc:snowflake://ecoatm.snowflakecomputing.com/?db=ECO_DEV&schema=AUCTIONS&TIMEZONE=UTC` | (QA DB) | (prod DB) | Override to swap `ECO_DEV` for `ECO_QA` / `ECO_PROD` without a code change. `TIMEZONE=UTC` is required for deterministic `Upload_Time` parsing against the watermark. |
| `SNOWFLAKE_USERNAME` | — | (set) | (set) | Service account user. Startup fails when `SNOWFLAKE_ENABLED=true` and this is blank. |
| `SNOWFLAKE_PASSWORD` | — | (set) | (set) | Service account password. Rotate per standard secret-rotation policy. |
| `SNOWFLAKE_POOL_MAX` | `3` | `3` | `5` | HikariCP max pool size for the Snowflake datasource. Tune based on concurrent admin sessions. |

### Fail-fast guard

If `SNOWFLAKE_ENABLED=true` and either `SNOWFLAKE_USERNAME` or
`SNOWFLAKE_PASSWORD` is blank, the app throws at startup rather than silently
returning `SKIPPED_DISABLED`. This prevents a deploy that intended to enable
sync from appearing healthy while producing no data.

### Enabling in an environment

1. Provide credentials via environment variables:
   ```bash
   export SNOWFLAKE_ENABLED=true
   export SNOWFLAKE_JDBC_URL='jdbc:snowflake://ecoatm.snowflakecomputing.com/?db=ECO_QA&schema=AUCTIONS&TIMEZONE=UTC'
   export SNOWFLAKE_USERNAME=salesplatform_sync
   export SNOWFLAKE_PASSWORD=<from secret manager>
   ```
2. Restart the backend. `SnowflakeDataSourceConfig`, the reader, the
   sync service, and the listener all come online via
   `@ConditionalOnProperty`. No code change or redeploy needed.
3. Open `/admin/auctions-data-center/inventory`, pick a week, and watch
   the sync banner. `GET /admin/inventory/weeks/{weekId}/sync/status`
   should progress `PENDING` → `STARTED` → `COMPLETED` within ~10s on a
   warm warehouse. Rollback = unset `SNOWFLAKE_ENABLED` and restart.

### Failure handling

- Per-run state lives in `integration.snowflake_sync_log`. `FAILED`
  rows carry `error_message`; the status endpoint surfaces it verbatim
  to the admin banner.
- Stale pulls short-circuit with `status=SKIPPED_STALE` when the
  Snowflake `MAX(Upload_Time)` is ≤ the stored watermark.
- No dead-letter queue in Phase 1 — same trade-off as the PWS email
  flow. Monitoring must tail the log table for `FAILED` rows.
