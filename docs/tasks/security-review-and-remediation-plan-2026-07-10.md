# Security Review & Remediation Plan — SalesPlatform_Modern

**Date:** 2026-07-10
**Scope:** Full application — Spring Boot backend (`backend/`) + Next.js frontend (`frontend/`)
**Method:** 4 parallel `security-reviewer` agents (auth/authz, injection, file-upload, frontend/secrets) + a dependency scan (`npm audit`, `pom.xml`), with the load-bearing CRITICALs personally verified against the code.
**Branch:** `main`

---

## 1. Executive summary

The **authentication core** of this app is genuinely well built — jjwt 0.12 `verifyWith` (alg-confusion-safe), BCrypt password hashing, `HttpOnly; SameSite=Strict` cookie the JWT never leaves via JS, stateless sessions, an enumeration-resistant + single-use + SHA-256-hashed password-reset token design, and a `GlobalExceptionHandler` that doesn't leak internals. **No SQL injection exists anywhere** — the dynamic-SQL subsystems correctly separate *SQL shape* (enum/switch-bound) from *values* (always bound params). The newest modules (bidder dashboard, partial-credit, PWS session) consistently derive the caller from the verified JWT and enforce per-buyer-code ownership at the service layer.

**The dominant weakness is authorization *coverage*, not authentication *mechanics*.** `SecurityConfig` only role-gates four URL namespaces; an entire tier of **older `/api/v1/pws/**` and `/api/v1/users/**` controllers falls through to bare "any authenticated user,"** and several of them additionally trust a client-supplied `userId`/`roleIds` instead of the JWT principal. The result is a **concrete privilege-escalation path to Administrator** plus **multiple cross-tenant IDOR paths** that let any low-privilege account (including the seeded dev `bidder@buyerco.com`) read and mutate other companies' financial transactions and force real order submissions to Oracle — **none of which requires breaking the JWT**; it needs only a valid login and knowledge of an unauthenticated URL.

**The reassuring part:** the correct remediation pattern (`Authentication`-derived `userId` + service-layer ownership check + a `SecurityConfig` matcher) **already exists in this repo** — `BidderDashboardController`, `BuyerPartialCreditController`, `PWSSessionController`, and `CreditRequestService.ensureBuyerCodeOwnership` all do it right. Remediation is largely *retrofitting a known-good in-repo pattern* onto the older controllers, not inventing anything.

### Findings by severity (after cross-agent dedup)

| Severity | Count | Headline |
|---|---:|---|
| **CRITICAL** | 4 | Priv-esc to Admin; JWT-secret forgery; PWS IDOR cluster; bid-import IDOR |
| **HIGH** | 11 | Unauthz PWS/pricing/inventory writes; token-in-logs; no auth throttling; enumeration; JWT revocation; upload DoS; dep upgrades |
| **MEDIUM** | 10 | Security headers; cookie Secure default; CORS; unsanitized HTML; content-type gaps |
| **LOW / INFO** | 13 | Dead CORS annotations; actuator detail; defense-in-depth; hardening |

### Cleared (reviewed, NOT vulnerabilities)
No SQLi anywhere · JWT alg-confusion safe · BCrypt hashing · `proxy.ts` is the correct Next 16 rename of `middleware.ts` (route-guard present + `returnTo` open-redirect-validated) · JWT never JS-readable · no git-tracked secrets · partial-credit module ownership solid (no IDOR) · no path traversal (no disk writes from user filenames) · exception handler doesn't leak internals.

---

## 2. Consolidated findings (severity-ranked)

> IDs are canonical for this doc. "Sources" notes which agent(s) raised it; **VERIFIED** = personally confirmed against the code this session.

### CRITICAL

**CR-1 — Privilege escalation to Administrator via `DirectUserController`** · **VERIFIED**
`/api/v1/users/**` has no `@PreAuthorize` and no `SecurityConfig` matcher → bare `authenticated()` (`SecurityConfig.java:103`). `DirectUserSaveRequest.roleIds` is client-controlled; `DirectUserService.updateDirectUser` (`DirectUserService.java:217-221`) deletes all `identity.user_role_assignments` for the target user and re-inserts the caller-supplied `roleIds` with no check the caller is an Admin or owns the account.
*Exploit:* log in as `bidder@buyerco.com` → `GET /api/v1/users/roles` (find Administrator id) → `PUT /api/v1/users/direct-users/{ownId}` with `{"roleIds":[adminId]}` → re-login → fresh JWT carries `roles:["Administrator"]` → full admin. Same endpoint also lets any user CRUD any other user's PII/accounts.
*Fix:* class-level `@PreAuthorize("hasRole('Administrator')")` + `SecurityConfig` matcher for `/api/v1/users/**`; validate `roleIds` server-side against what the *caller* may grant.

**CR-2 — Hardcoded fallback JWT signing secret + no fail-fast** · **VERIFIED (2 agents + code)**
`JwtService.java:22` `@Value("${app.jwt.secret:default-dev-secret-key-must-be-at-least-32-bytes-long!!}")`, mirrored at `application.yml:129`. No `@PostConstruct`/startup validation anywhere. If `JWT_SECRET` is ever unset in QA/prod (one ops mistake), the app boots silently and signs every JWT with this **git-committed** string.
*Exploit:* anyone who has seen the repo mints `{"userId":1,"roles":["Administrator"]}` signed with the known key → uncredentialed admin, no login.
*Fix:* remove the default (`@Value("${app.jwt.secret}")`, no fallback) so Spring fails to start without it, **or** `@PostConstruct` throw when secret equals the dev placeholder / < 32 bytes and profile ≠ local/test. Rotate the QA/prod key. Confirm the deploy pipeline injects it from a secret manager.

**CR-3 — Missing-authorization + IDOR cluster on PWS financial controllers** · **VERIFIED (SecurityConfig fall-through + zero `@PreAuthorize`)**
All reachable by any authenticated role (incl. dev Bidder); none covered by a `SecurityConfig` matcher; client-supplied `userId` trusted where present.
- `OfferController` (`/api/v1/pws/offers/**`) — `authorize()` (`OfferController.java:140-143`) returns `true` when `userId` is null (omit it → bypass); `submitOffer`/`submitOrder` have **no** check at all → force real Oracle order for any `offerId`. `BuyerCodeService.isUserAuthorizedForBuyerCode` also returns `true` for any non-buyer-role `userId`.
- `CounterOfferController` (`/api/v1/pws/counter-offers/**`) — **zero** ownership check anywhere; read/accept/cancel any company's counter-offer by enumerating sequential `offerId`.
- `OfferReviewController` (`/api/v1/pws/offer-review/**`) — internal sales workflow (`accept/decline/finalize/complete-review`) open to any role; `completeReview` trusts a client `userId` as the audit "reviewer."
- `RmaController` (`/api/v1/pws/rma/**`) — no role gate, no ownership; view/approve/decline/complete any RMA; `submit` trusts client `userId`/`buyerCodeId`.
*Fix:* one sweep — derive `userId` from `Authentication` (never a param), add mandatory service-layer buyer-code ownership (reuse `CreditRequestService.ensureBuyerCodeOwnership`), add `SecurityConfig` matchers, add class-level `@PreAuthorize` (internal roles for offer-review/rma review actions).

**CR-4 — Bid-import IDOR → sealed-bid tampering (`BidImportService`)** · Sources: upload agent (high-confidence — sibling regression)
`POST /api/v1/bidder/bid-rounds/{id}/import?buyerCodeId=B` (`BidderDashboardController.java:270-286`) — `BidImportService.importBids` never calls the ownership guard its three sibling services all use (`BidDataSubmissionService.assertOwnership`, `BidCarryoverService`, `BidderDashboardService`). Within the Bidder role, a bidder can upload an xlsx targeting a rival's `buyerCodeId` and overwrite their live `bid_amount`/`bid_quantity`.
*Fix:* add `ensureBuyerCodeOwnership(userId, buyerCodeId)` as the first statement of `importBids()`.

### HIGH

**H-1 — `PricingController`: global pricing catalog writable by any authenticated role.** (`/api/v1/pws/pricing/**`, no gate) — bulk-rewrite `futureListPrice`/`futureMinPrice` (≤1000/call) + CSV upload. *Same root cause/fix as CR-3; fold into that sweep.* (upload agent rated CRITICAL; auth agent HIGH — filed HIGH: financial-integrity corruption, no cross-tenant priv-esc.)

**H-2 — `InventoryController`: device catalog writable by any authenticated role.** `/api/v1/inventory/devices*`, `/case-lots` unmatched (only `/inventory/sync/**` is Admin-gated). Fix: gate Administrator/SalesOps.

**H-3 — `FuturePriceConfigController`: platform-wide "future price date" mutable by any authenticated role.** `PUT /api/v1/pws/pricing/config`. Fix: gate Administrator/SalesOps.

**H-4 — `OrderHistoryController`: cross-tenant financial-history read IDOR.** Optional client `userId`/`buyerCodeId`; `getDetailsBy*` take bare `offerId`. Read-only → HIGH. Fix: JWT-derive `userId`, verify ownership.

**H-5 — Password-reset token logged in cleartext, unconditional (CWE-532)** · **VERIFIED** · *agents split: frontend=CRITICAL, auth=MEDIUM → adjudicated HIGH / fix-now.* `PasswordResetService.java:93` `log.info("...token={}...", email, rawToken, ...)` — not profile-gated, the only delivery path. Anyone with INFO-log read access takes over any account within the 30-min TTL. Fix (**one line, do immediately**): drop `rawToken` from the log; wire the `TODO(email-infra)` post-commit event/listener before any shared log sink.

**H-6 — No brute-force / rate limiting on auth endpoints.** `/login`, `/forgot-password`, `/reset-password` are `permitAll` with no throttle; `identity.users.failed_logins` column exists but is never read/incremented. Fix: IP+account rate limiter (Bucket4j) on `/api/v1/auth/**`; wire `failed_logins` → lockout.

**H-7 — User enumeration on `/login`.** Three distinct messages ("No account with this email" / "Account is disabled or locked" / "Incorrect Password") — `AuthService.java:33,39,52`. Contradicts the enumeration-resistant `/forgot-password`. Fix: one generic "Invalid email or password."

**H-8 — JWT outlives its cookie; no revocation on logout/password-reset.** Cookie `Max-Age` 8h/24h vs token validity 24h/7d (`AuthController.java:29-30` vs `application.yml:130-131`); filter also accepts `Authorization: Bearer`. `logout` only clears the cookie; `confirmReset` doesn't invalidate existing tokens. Fix: align cookie/token TTL; add a "password-changed-at" claim check or server-side revocation list invalidated on reset+logout.

**H-9 — File-upload parsers lack size/row/entry caps (zip-bomb / heap DoS).** `POExcelParser`, `ReserveBidExcelParser`, `CreditRequestFileDropParser` use DOM-loading POI with no `file.getSize()` / row / paragraph bound (only `BidImportService` caps — 5 MB / 10k rows). `/parse-barcodes` is Bidder/SalesRep-reachable + unthrottled. Fix: mirror `BidImportService`'s caps; consider POI SAX/streaming.

**H-10 — No rate limiting on file-upload endpoints** (all except bid-import). Combined with H-9 → cheap DoS. Fix: per-user/IP limiter on all multipart endpoints.

**H-11 — Photo download served `inline` with attacker-declared content-type; no magic-byte validation.** Upload validates only the client MIME string (`CreditRequestPhotoService.java:108-114`); download echoes it with `Content-Disposition: inline` (`BuyerPartialCreditController.java:249-260`). Dampened today by Spring's default `nosniff` + a narrow image allowlist, but one config relaxation (e.g. adding `image/svg+xml`) → stored XSS. Fix: verify real image bytes (`ImageIO.read`, like `BuyerUserGuideService.validateMagicBytes` already does for PDFs); force `attachment` disposition; serve a sanitized content-type.

**H-12 — Frontend dependency CVEs (Next.js).** `npm audit`: 3 high incl. **Middleware/Proxy bypass** (`GHSA-492v-c6pp-mqqv`, `GHSA-267c-6grr-h53f`, `GHSA-36qx-fr4f-26g5`, `GHSA-26hh-7cqf-hhc6`), SSRF via WebSocket upgrades, RSC cache poisoning, image-opt DoS. Directly relevant given the `proxy.ts` BFF. Fix: planned Next.js upgrade (`audit fix --force` → `next@16.2.10`, a major bump) + regression pass.

**H-13 — Backend framework EOL.** Spring Boot `3.2.4` (early 2024) is past OSS end-of-life and misses a year+ of Spring Framework/Boot CVE patches. Fix: upgrade to a currently-supported patched line (3.3.x/3.4.x) + regression.

### MEDIUM

- **M-1 — No security headers on the Next.js origin** (CSP, X-Frame-Options, HSTS, X-Content-Type-Options, Referrer-Policy all absent — `next.config.ts` has no `headers()`). Offset but not eliminated by `SameSite=Strict`. Fix: add `headers()`.
- **M-2 — Auth cookie `Secure` fail-open default** — `application.yml:141` `secure: ${AUTH_COOKIE_SECURE:false}` unless `production` profile. Fix: default `true`, opt-out only for `local`.
- **M-3 — Unsanitized `dangerouslySetInnerHTML`** on admin email-log (`email-admin/page.tsx:664`). Backend endpoint not yet built → not live today, but fix (DOMPurify) before it is.
- **M-4 — CORS wildcard ngrok origins + `allowCredentials(true)`** as shipped default (`SecurityConfig.java:121-125`, `application.yml:147`). VERIFIED. Mitigated by `SameSite=Strict`. Fix: set `CORS_ALLOWED_ORIGINS` explicitly per env; exclude ngrok in QA/prod.
- **M-5 — DB password weak fallback default, no fail-fast** (`application.yml:36` `${DB_PASSWORD:salesplatform}`). Same class as CR-2, lower blast radius (needs network access to Postgres). Fix: drop default in non-local / startup assertion.
- **M-6 — Content-type validation gaps** — `PricingController` skips the allowlist when Content-Type is absent; `RmaController` has no content-type/size gate. Fix: treat missing type as rejection; add extension fallback + size cap.
- **M-7 — `Content-Disposition` filename built by raw string concat** (`BuyerPartialCreditController.java:257-258`, `BuyerUserGuideController.java:92-94`) — embedded `"` unescaped. Fix: `ContentDisposition.builder(...).filename(name, UTF_8)`.
- **M-8 — `OnBehalfSubmissionService` Phase-1 permissive scoping** — any SalesRep can file for any active buyer code (documented accepted gap, trusted role only). Track for Phase 2 (`sales_representatives.user_id` mapping).
- **M-9 — `snowflake-jdbc 3.16.1`** — check for a newer patch (past Snowflake JDBC CVEs).
- **M-10 — PostCSS `</style>` XSS** (moderate, bundled via Next) — resolves with the Next upgrade (H-12).

### LOW / INFO

- **L-1** Dead `@CrossOrigin("http://localhost:3000")` on 5 controllers (incl. `RmaController`) — inert vs the global CORS bean; false sense of security. Remove.
- **L-2** `/actuator/health` `show-details: when-authorized` visible to any authenticated user (no `management.endpoint.health.roles`). Set `roles: Administrator`.
- **L-3** `local` profile widens actuator exposure (`show-details: always`, flyway/mappings) — guard against `production,local` profile combos.
- **L-4** `AuctionScheduleService.java:492-505` builds an `IN (...)` list by string-joining `Long` IDs — safe today by Java typing only, not an allowlist; migrate to a bound `ANY(:ids)` array (defense-in-depth; the one injection-adjacent hardening item).
- **L-5** SSO `target` param inert today but an open-redirect magnet for the future SAML wiring — allowlist internal paths when implemented.
- **L-6** `PWSAdminController`, `ReserveBidController`, `AdminMasterDataController`, `OracleConfigController` rely on the URL matcher only — add class-level `@PreAuthorize` for defense-in-depth.
- **L-7** `AdminPartialCreditController` grants `SalesRep` config-edit rights (verify intent against the role matrix).
- **L-8** Email-template preview accepts admin-supplied variables; frontend comment overstates provenance (escaping is safe-by-default). Correct the comment; consider a key allowlist.
- **L-9** Redundant/dead `SecurityConfig` matchers (`re-rank`, `recalculate-target-price`, `reassign-r2-buyers`) already covered by the broader `/admin/auctions/**` rule — no risk, just cleanup.
- **L-10** `BidImportService.hasAdministratorRole()` defined but never called (fails closed — functional inconsistency, not a weakening).
- **L-11** `CreditRequestUpload` + repo unused → no durable audit trail of the raw evidence file (business-logic gap, not a vuln).
- **L-12** `BuyerUserGuideService` 20 MB cap unreachable (global multipart limit is 10 MB) — cosmetic, fails safe.
- **L-13** Add a scheduled `mvn org.owasp:dependency-check:check` (live CVE feed) — POI `5.2.5` + pinned `commons-compress 1.26.1` look intentional but confirm against current advisories.

---

## 3. Implementation plan (ordered by severity)

Each phase lists the findings closed, the core change, and the verification gate. Effort is engineer-days for one developer. **Phases 0–1 are release-blockers.**

### Phase 0 — Immediate hot-fixes (≤ 0.5 day) — *land today*
Trivial, high-value, low-risk edits that need no design:
1. **H-5** Strip `rawToken` from `PasswordResetService.java:93` (log `email` + expiry only). *One line.*
2. **CR-2 (interim)** Add a `@PostConstruct` guard in `JwtService` that throws when the configured secret equals the dev placeholder or is < 32 bytes and the active profile ≠ `local`/`test`. (Full env-injection wiring is ops-side.)
3. **M-2 / M-5** Flip fail-open config defaults to fail-closed: `AUTH_COOKIE_SECURE` → default `true` (opt-out only in `local`); drop the `DB_PASSWORD`/`JWT_SECRET` literal defaults in non-local profiles.
**Verify:** app refuses to start with the dev JWT secret under a non-local profile; reset flow logs no token; cookie ships `Secure` by default.

### Phase 1 — CRITICAL: authorization retrofit (2–3 days) — *release blocker*
The single most important phase. One coherent sweep applying the **in-repo known-good pattern** (`Authentication`-derived `userId` + service-layer `ensureBuyerCodeOwnership` + `SecurityConfig` matcher + class `@PreAuthorize`) across the ungated controllers.
- **CR-1** `DirectUserController` → `@PreAuthorize("hasRole('Administrator')")` + matcher + server-side `roleIds` validation. **(do first — priv-esc)**
- **CR-3 + H-1/H-2/H-3/H-4** `OfferController`, `CounterOfferController`, `OfferReviewController`, `RmaController`, `PricingController`, `InventoryController`, `OrderHistoryController`, `FuturePriceConfigController` → JWT-derive `userId` (delete client `userId`/`buyerCodeId` params), mandatory ownership checks, `SecurityConfig` matchers, class-level `@PreAuthorize` (internal roles for sales/rma review actions).
- **CR-4** `BidImportService.importBids` → add `ensureBuyerCodeOwnership` first.
**Verify (TDD):** for each controller add IT proving (a) a Bidder-role JWT gets **403** on admin/cross-tenant calls, (b) omitting `userId` no longer bypasses, (c) `offerId`/`rmaId` for another buyer code → 403. Re-run the full controller IT sweep. This is the gate for calling the app releasable.

### Phase 2 — HIGH: auth + upload hardening, dependency upgrades (3–4 days)
- **H-6/H-7** Auth rate limiter (Bucket4j) on `/api/v1/auth/**` + `failed_logins` lockout; collapse login errors to one generic message.
- **H-8** Align cookie/token TTL; add password-changed-at revocation check (invalidated on reset + logout).
- **H-9/H-10/H-11** Upload caps (size/row/paragraph) on the 3 parsers; rate-limit all multipart endpoints; magic-byte validation on photo upload + `attachment` disposition + sanitized content-type on download.
- **H-12** Next.js upgrade (major bump) + Playwright/visual regression pass. **H-13** Spring Boot 3.3.x/3.4.x upgrade + full backend IT sweep. **M-9/M-10** ride along.
**Verify:** rate-limit IT (429 after N); enumeration IT (identical message); revoked-token IT; oversized/zip-bomb upload rejected; `npm audit` + `mvn dependency:check` clean of highs; both app + backend regression suites green.

### Phase 3 — MEDIUM: headers, CORS, sanitization, content-type (1–2 days)
- **M-1** `next.config.ts` `headers()` — CSP, `X-Frame-Options: DENY`, `nosniff`, `Referrer-Policy`, HSTS.
- **M-3** DOMPurify on the admin email-log `dangerouslySetInnerHTML` (before its backend ships).
- **M-4** Lock CORS per-env (exclude ngrok in QA/prod).
- **M-6/M-7** Content-type/size gates on Pricing/RMA uploads; `ContentDisposition.builder` for filenames.
**Verify:** security-header assertion test; CSP doesn't break the app; malformed filename/type rejected.

### Phase 4 — LOW / INFO: cleanup + defense-in-depth (1 day)
L-1…L-13 — remove dead `@CrossOrigin`, restrict actuator health detail, defense-in-depth `@PreAuthorize` on URL-matcher-only admin controllers, bound the `AuctionScheduleService` `IN(...)` list, add the scheduled OWASP dependency-check, correct misleading comments, prune dead config.
**Verify:** cleanup causes no behavior change; dependency-check wired into CI.

### Sequencing
```
Phase 0 (today, ≤0.5d)  ── one-liners + fail-fast guards
     │
Phase 1 (2–3d) ★ BLOCKER ── authorization retrofit sweep  ← highest priority
     │
Phase 2 (3–4d) ── auth/upload hardening + dep upgrades (parallelizable: backend hardening ∥ frontend Next upgrade)
     │
Phase 3 (1–2d) ── headers / CORS / sanitization
     │
Phase 4 (1d)   ── cleanup + defense-in-depth
```
**Total:** ~8–11 engineer-days. Phases 0–1 (~3 days) close every CRITICAL.

---

## 4. Notes & open items for the deployment owner
- **CR-2 real exposure** can't be confirmed from this repo — verify the QA/prod pipeline injects a high-entropy `JWT_SECRET` from a secret manager, and **rotate** it if the dev default may ever have been active outside a dev machine.
- The **dev seeds** in `CLAUDE.md` (`admin@test.com` etc., `salesplatform`/`salesplatform`) are intentional and acceptable for local dev — the risk is *silent fallback* to them in a real deployment, which Phase 0's fail-fast guards address.
- Prior good precedent exists: `V46__remove_integration_credentials.sql` already stripped credential columns "must come from environment variables / secrets" — extend that discipline to JWT/DB fallbacks.
