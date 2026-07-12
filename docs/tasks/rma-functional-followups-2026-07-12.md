# RMA #3 "Make RMA Functional" — Follow-ups from review (2026-07-12)

Phase 1 of #3 landed on `main` in logging/simulated mode (Oracle SIM in dev,
Snowflake logging writer, Deposco no-op client, email via `LoggingEmailSender`).
Everything below is **inert today** and surfaces only on a prod cutover. Captured
from the per-task Opus reviews so nothing rots in conversation.

## Pre-deploy blockers (must resolve before any prod `jdbc`/real-integration cutover)

### FU-1 (HIGH) — `snowflakeJdbcTemplate` bean is not defined in-repo
The RMA `JdbcRmaSnowflakeWriter` injects `@Qualifier("snowflakeJdbcTemplate")`,
mirroring `JdbcPurchaseOrderSnowflakeWriter` / `JdbcBidRankingSnowflakeWriter` /
`JdbcTargetPriceSnowflakeWriter`. A repo-wide search finds **only usages, never a
`@Bean` producing it** (`SnowflakeDataSourceConfig` defines `snowflakeDataSource`,
not the template). If it isn't supplied by a deploy-only/profile config outside
the repo, then setting `rma.sync.writer=jdbc` (**or any of the four sibling
Snowflake writers**) fails at startup with `NoSuchBeanDefinitionException`.
- **This is pre-existing and repo-wide**, not introduced by RMA #3 — the RMA
  writer faithfully mirrors the existing idiom.
- **Action:** confirm/define the `snowflakeJdbcTemplate` bean before enabling any
  `writer: jdbc`. Harmless at rest (all default `writer: logging`, `enabled:false`).

### FU-2 (MED) — Oracle RMA payload JSON key casing is best-effort
`RmaOraclePayloadBuilder` was built from `SUB_RMA_PrepareContentAndSendToOracle`
with the exact key casing not fully recoverable from `migration_context/`.
Dev SIMs the call. **Action:** validate the payload against the real Oracle QA
Create-RMA contract before the QA Oracle cutover (the user confirmed a QA Oracle
environment exists; see the fail-closed hardening in `OracleOrderClient`).

### FU-3 (MED) — RMA_Approved email copy + `approvedItemsSummary`
The V93 `RMA_Approved` template ships enabled, and the listener's
`approvedItemsSummary` is a modern text list (`imei — returnReason`, approved
lines only) rather than a byte-for-byte port of the legacy HTML device table
(joined Model/Carrier/Capacity/Color display names — the legacy copy was marked
best-effort/unrecoverable). Load-bearing figures (qty/skus/$total) travel in
dedicated scalar vars, so the summary is supplementary. **Action:** have ops
eyeball the `RMA_Approved` copy in the Email Admin editor before relying on it in
QA/prod (delivery already routes through `LoggingEmailSender` until real SMTP is
configured; `EMAIL_SMTP_PASSWORD` is env-only).

## Fidelity / architecture notes

### FU-4 (MED) — RMA Snowflake push vs Oracle-create ordering (decoupled-redesign consequence)
#3 splits the legacy sequential `ACT_RMADetails_CompleteReview` (Oracle-create →
then Snowflake-push, approved-branch nested inside Oracle-success) into two
**independent** `AFTER_COMMIT @Async` listeners on the shared
`RmaReviewCompletedEvent`, running on **different** pools (`ORACLE_EXECUTOR` vs
`SNOWFLAKE_EXECUTOR`) with no ordering guarantee. Consequences vs. legacy, on the
approved path once real Oracle + jdbc Snowflake are wired:
- **Race:** the Snowflake snapshot can read `oracle_*` before
  `RmaOracleCreateListener`'s `REQUIRES_NEW` tx commits them → an approved RMA can
  land in Snowflake with null Oracle linkage.
- **Over-push:** modern pushes an approved RMA to Snowflake even when the Oracle
  create ultimately fails; legacy pushed only on Oracle success.
- `@Order` does **not** fix this (pools run concurrently). A faithful fix chains
  the approved-path Snowflake push **after** the Oracle write — e.g.
  `RmaOracleCreateListener`, after writing `oracle_*`, publishes a second event
  that an approved-path Snowflake listener consumes; the declined path pushes
  directly on `RmaReviewCompletedEvent`.
- **Decision needed:** accept the divergence (decoupled resilience is arguably
  the better modern design) or re-couple for strict legacy fidelity. Deferred —
  no live impact until the jdbc Snowflake + real Oracle cutover (also gated by
  FU-1).

### FU-5 (LOW) — RMA Deposco poll skips `Declined` (intentional divergence)
Per product decision 2026-07-12, the Deposco status poll treats `Declined` as
terminal and never reopens it; legacy `ACT_UpdateRMAFromDeposco` polls `Declined`
and would flip `Declined → Received` on physical receipt. Documented in
`RmaDeposcoSyncService`. Deposco client is a logging no-op + `rma.deposco-sync`
defaults off, so inert until a real reverse-logistics client lands.

### FU-6 (LOW) — RMA resubmit endpoint is unguarded by RMA state
`POST /api/v1/pws/rma/{rmaId}/resubmit-oracle` re-sends unconditionally, faithful
to legacy `ACT_RMA_ReSubmitToOracle`. A state guard (only when the prior create
failed) is a small optional hardening.

## Minor test gaps (optional)
- `RmaApprovedEmailMigrationIT` seeds an RMA with no items → no test exercises a
  non-empty `approvedItemsSummary` through a Hibernate-managed lazy collection.
  LazyInit is safe today; a one-line "add an approved item, assert summary/to"
  would close it.
- `RmaSnowflakePushListenerTest.nullRmaId_skips` is weakly non-vacuous (a mocked
  `findById(null)` returns empty regardless of the guard).

## Status
Phase 1 complete + merged (`main`), full backend sweep green. None of the above
blocks the current logging/simulated build; FU-1 and FU-4 are the two that need a
conscious decision before a production Snowflake/Oracle cutover.
