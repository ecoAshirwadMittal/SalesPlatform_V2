# Make RMA Functional — Implementation Plan (gap-analysis #3)

> **For agentic workers:** execute with superpowers:subagent-driven-development, one fresh Opus agent per task + review gate. Steps use `- [ ]` checkboxes.

**Date:** 2026-07-11 · Source: `docs/gap-analysis/implementation-plan.md` §1.3 + §2.1, `docs/gap-analysis/_partials/rma.md` gaps #1/#3, open-question #3.

**Goal:** Complete the RMA module so `completeReview(Approved)` fires its real downstream effects — Oracle RMA-order creation (writing the scaffolded `oracle_*` columns), the buyer approval email (via the shipped email backbone), Snowflake sync — plus Oracle resubmit and Deposco status polling. RMA is a read/review shell today; this makes it operational.

**Architecture:** Mirror the app's proven "simulated/logging in dev, real in prod behind a toggle" pattern (email's `LoggingEmailSender`, the Snowflake `Jdbc*/Logging*Writer` pair, the Oracle `is_active` toggle). External endpoints not wired in dev (Oracle RMA endpoint is toggled off; no Deposco reverse-logistics client exists) are built behind toggles and run in simulated/stub mode locally, real when creds land.

**Tech stack:** Spring Boot, JPA, Flyway, `@Scheduled`+ShedLock, `EmailService.sendTemplated`, `OracleOrderClient`, JUnit5/Mockito/Testcontainers.

## Global constraints / decisions (locked with the human 2026-07-11)
- **open-Q3 — Oracle-off = DEV-ONLY simulated success.** `OracleOrderClient` keeps the `SIM-…`/`returnCode="00"` stub ONLY under the `local`/`dev` profile. Under the `production` profile, a toggle-off / missing-config / token-failure returns an **error** (empty `returnCode`, populated `returnMessage`) so the caller routes to Pending — **never** fake-creates a real order/RMA. This hardens the existing offer flow too, not just RMA.
- **Full build in simulated/logging mode** — all five effects (Oracle-off fix, Oracle RMA create + resubmit, approval email, Snowflake sync, Deposco polling) land this pass; Deposco + real-Oracle run in stub/simulated mode until endpoints/creds exist.
- **Reuse, don't invent:** the single `OracleConfig.is_active` toggle gates both order + RMA Oracle calls; `OracleConfig.create_rma_path` already exists (V-scaffolded); recipients resolve via the RMA's buyer `EcoATMDirectUser` emails (same resolver `ReviewCompletedEmailListener`/PWS use); the email goes through `EmailService.sendTemplated`; the Snowflake push reuses the `Jdbc*SnowflakeWriter`+`Logging*` + `@TransactionalEventListener(AFTER_COMMIT)` pattern.
- **`email.template` seed:** the RMA-approved template is seeded into `email.template` via the next free Flyway version (**V93** — 0.1/0.2 did not consume their reserved V93/V94), so the flow works out of the box; it's also editable in the Email Admin UI afterward.
- **Identity from JWT; every new admin/mutation endpoint gets an explicit `SecurityConfig` matcher + `@PreAuthorize`** (repo Security Rules). Reviews run on **Opus**.

---

## Task A — Oracle-off: dev-only simulated success (the open-Q3 correctness fix)
**Files:** `service/OracleOrderClient.java`; test `service/OracleOrderClientTest.java` (new or extend).

- **Scope:** In `submitOrder` (and the new `submitRma`, Task B), replace the unconditional toggle-off stub (`OracleOrderClient.java:65-72`) with a profile-gated one: inject `Environment`; when config is null/`!isActive` (or token/create fails) → if the active profiles contain `production`, return an **error** `OracleResponse` (no `returnCode`, `returnMessage="Oracle API is disabled"`); otherwise (dev/local) return the existing `SIM-…`/`00` stub. Factor the toggle-off + error construction into a shared private helper both `submitOrder`/`submitRma` use.
- **Acceptance:** unit tests — (dev profile, toggle off) → `returnCode="00"` SIM; (production profile, toggle off) → error response, no `returnCode`; (production, token failure) → error. `OfferService` order flow: a prod toggle-off routes the offer to `Pending_Order`, not `Ordered` (add/adjust an `OfferService`/client test). No behavior change in dev.
- **Why first:** shared client; unblocks Task B's simulated path and is a standalone latent-bug fix.

## Task B+C+D — complete-review side-effects (Oracle create + approval email + Snowflake) + Oracle resubmit
*(One cohesive task — all three hook `RmaService.completeReview`, so they share the method; plus the resubmit endpoint.)*
**Files:** `service/RmaService.java` (`completeReview` @230), `service/OracleOrderClient.java` (+`submitRma`), a new `service/rma/RmaOraclePayloadBuilder` + `service/rma/RmaSnowflake*Writer` (jdbc+logging) + an `RmaApprovedEmailListener` (or inline), `controller/RmaController.java` (+resubmit endpoint), `security/SecurityConfig.java` (matcher for resubmit), Flyway **V93** (seed the `RMA_Approved` `email.template`); tests alongside.

- **B — Oracle RMA create.** Add `OracleOrderClient.submitRma(String jsonPayload)` — same auth-token flow as `submitOrder` but POST to `config.getCreateRmaPath()`; reuse the Task-A toggle-off/error helper. On `completeReview` when the outcome is Approved (and status-gate per legacy), build the RMA payload (`SUB_RMA_PrepareContentAndSendToOracle` — research the exact shape in `migration_context/`), call `submitRma`, and **write the `oracle_*` columns** (`oracle_number`, `oracle_id`, `oracle_http_code`, `oracle_json_response`, `oracle_rma_status`) from the response. Gate on `OracleConfig.is_active` (dev → SIM writes a `SIM-…` oracle_number). 
- **B — Resubmit.** `POST /api/v1/pws/rma/{rmaId}/resubmit-oracle` (`ACT_RMA_ReSubmitToOracle`) for RMAs whose prior Oracle create failed (`returnCode != '00'`); internal-role only (mirror `completeReview`'s role gate + add the explicit `SecurityConfig` matcher + `@PreAuthorize`). Re-runs `submitRma` + rewrites `oracle_*`.
- **C — Approval email** (`SUB_SendEmail_RMAApproved`). On Approved complete-review, resolve the buyer's `EcoATMDirectUser` emails (same resolver the PWS/CreditRequest listeners use) and `emailService.sendTemplated("RMA_Approved", vars, new SendOverrides(recipients,null,null), new SourceRef("RMA", rmaId))`. Vars: rma number, buyer code, approved qty/total, item list. Runs on the async `EMAIL_EXECUTOR` + AFTER_COMMIT (mirror `ReviewCompletedEmailListener`; note the readOnly-tx gotcha — the send writes `email.log`, so its listener tx must not be `readOnly`). **V93 seeds the `RMA_Approved` `email.template`** (subject/body with `{{var}}`), enabled.
- **D — Snowflake sync** (`SUB_SendRMADetailsToSnowflake`). Push the RMA to Snowflake via a new `RmaSnowflakeWriter` (jdbc) / `LoggingRmaSnowflakeWriter` (default in dev) selected by config, fired AFTER_COMMIT — mirror the existing `*SnowflakePushListener` + `Jdbc*Writer`/`Logging*Writer` pattern exactly. (Included per the human's full-build choice; logging-only in dev.)
- **Ordering/tx:** the three effects must not roll back the review-completion tx and must not block the response — fire them on AFTER_COMMIT/async like the email listener. A failed Oracle create leaves the RMA Approved with a failed `oracle_rma_status` (resubmit path), does NOT fail the review.
- **Acceptance:** completing an Approved RMA (service+IT): writes `oracle_*` (SIM in dev); enqueues/sends the approval email with an `email.log` row (`source_module='RMA'`); emits the Snowflake push (logging writer asserts the row); a failed Oracle create is resubmittable and doesn't fail the review; wrong-role resubmit → 403.

## Task E — Deposco RMA status polling (`ACT_UpdateRMAFromDeposco` / `SUB_SyncRMAStatus`)
**Files:** new `service/rma/RmaDeposcoSyncService` (+ a stub `DeposcoRmaClient` interface with a logging/no-op default — no real endpoint exists) + `@Scheduled`+`@SchedulerLock` job; config toggle `rma.deposco-sync.enabled` (default false); repo finder for RMAs with a non-null `oracle_number` not yet `Received`; test.

- **Scope:** a single-leader scheduled job that, for each RMA with an `oracleNumber` and a non-terminal status, asks the Deposco reverse-logistics client for status and advances to `Received` when Deposco reports receipt. No real Deposco RMA endpoint exists → ship a `DeposcoRmaClient` interface with a logging/no-op impl (returns "no update") as the default, so the job is inert-but-wired until a real client lands (mirror `Logging*Sender`/`Logging*Writer`). Reuse `config/SchedulingConfig` ShedLock + `config/ClockConfig`.
- **Acceptance:** unit test with a stub client — a client "Received" advances the RMA to `Received` (+ persists), a "no update" leaves it, terminal RMAs are skipped, `@SchedulerLock` present, default toggle off. (Independent of B+C+D → can build in parallel.)

---

## Known unknowns (build against stub/simulated; wire real when available)
- **Oracle RMA payload shape** (`SUB_RMA_PrepareContentAndSendToOracle`) — research the exact JSON in `migration_context/`; if under-specified, build a faithful best-effort payload + log it (dev runs simulated anyway).
- **Deposco reverse-logistics endpoint** — none exists; Task E ships the interface + logging stub only.
- **Snowflake RMA target table** — confirm/log the target (open-Q4 scope); logging writer in dev regardless.

## Testing
Per-task service + IT (real Postgres via `PostgresIntegrationTest`) as above; assert `oracle_*` written, `email.log` RMA row, Snowflake logging-row, Deposco advance, and the 403 on resubmit. Full backend suite green before merge.

## Sequencing / execution
1. **Task A** (Oracle-off fix) — first (shared client).
2. **Task B+C+D** and **Task E** — parallel after A (disjoint: complete-review side-effects vs a new scheduled job), each a fresh Opus SDD agent in its own worktree.
3. Review each (Opus) → merge → push. Update `docs/api/rest-endpoints.md` (resubmit), `docs/app-metadata/modules.md` (RMA), `docs/business-logic/` (RMA flow), `docs/deployment/setup.md` (`rma.deposco-sync.*`, Oracle prod-error behavior), `docs/testing/coverage.md`.
