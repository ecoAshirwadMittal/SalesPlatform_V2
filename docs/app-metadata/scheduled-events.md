# Scheduled Events

Inventory of background `@Scheduled` jobs in the backend. One section
per job. Add new entries newest-first as cron jobs are introduced.

---

## auctionLifecycle

| Property | Value |
|---|---|
| **Owner class** | `com.ecoatm.salesplatform.service.auctions.lifecycle.AuctionLifecycleScheduler` |
| **Trigger** | `@Scheduled(fixedDelayString = "${auctions.lifecycle.poll-ms:60000}")` |
| **Default interval** | 60s |
| **Multi-instance safety** | ShedLock — `name="auctionLifecycle"`, `lockAtLeastFor=10s`, `lockAtMostFor=55s` |
| **Audit table** | `infra.scheduled_job_run` (one row per tick, `counters` JSONB) |
| **Feature flag** | `auctions.lifecycle.enabled` (default `true`; `false` in `application-test.yml`) |
| **Mendix parity** | `AuctionUI.ACT_ScheduleAuctionCheckStatus` (1-minute scheduled event) |
| **Emits** | `RoundStartedEvent`, `RoundClosedEvent` (post-commit) |
| **Consumers** | `AuctionStatusSnowflakePushListener` (sub-project 1, live, deferred-writer); `R1InitListener` (sub-project 2, live — target-price clamp + QBC rewrite); `R2InitStubListener`, `R3InitStubListener`, `BidRankingStubListener`, `R3PreProcessStubListener` (logging-only stubs pending sub-projects 3-6) |

Counters JSONB shape:

```json
{ "roundsStarted": 0, "roundsClosed": 0, "auctionsAffected": 0, "errorCount": 0 }
```

`AuctionStatusSnowflakePushListener` is gated by
`auctions.snowflake-push.enabled` (default `false`). When enabled, it
emits a single INFO line per event prefixed
`[deferred-writer] auction-snowflake-push …` containing the full
payload the real Snowflake writer will eventually persist. See ADR
`2026-04-21 — Auction status Snowflake push` for the rationale.

See ADR `2026-04-20 — Auction lifecycle cron` in
`docs/architecture/decisions.md` for the full rationale on the cron
itself.
