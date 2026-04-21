# Auction Flow

Business rules covering the auction lifecycle — creation, scheduling,
round transitions, and downstream side effects.

## Round lifecycle

The Mendix `ACT_ScheduleAuctionCheckStatus` 1-minute scheduled event is
ported as the `auctionLifecycle` Spring `@Scheduled` job. Each tick:

1. Closes Started rounds whose `end_datetime < now()`.
2. Starts Scheduled rounds whose `start_datetime ≤ now()`.
3. Reconciles each affected parent `Auction.auction_status`:
   all rounds Closed → `Closed`; any round Started → `Started`;
   else leave unchanged (matches Mendix `SUB_SetAuctionStatus`).

Per-round transitions are emitted as `RoundStartedEvent` /
`RoundClosedEvent` post-commit. The downstream Mendix sub-microflows
(R1/R2/R3 init, bid ranking, target price, special buyers, Snowflake
push) are implemented as event listeners. `auction-snowflake-push`
(sub-project 1) is **live**:
`service/auctions/snowflake/AuctionStatusSnowflakePushListener` consumes
both events, re-fetches the auction + week aggregate in a short
read-only REQUIRES_NEW tx, and hands an `AuctionStatusPushPayload` to
an `AuctionStatusSnowflakeWriter`. Phase 1 ships a logging-only
`LoggingAuctionStatusSnowflakeWriter` that emits a single structured
INFO line prefixed `[deferred-writer] auction-snowflake-push …` — the
real Snowflake-bound writer is a follow-up once the target table shape
is known. The feature flag `auctions.snowflake-push.enabled` (default
`false`) short-circuits the listener before any DB read; flip it on in
QA/prod to see the `[deferred-writer]` lines. The remaining five
listeners (R1/R2/R3 init, bid ranking, R3 pre-process) still live
under `service/auctions/lifecycle/stub/` as logging-only stubs that
sub-projects 2-6 will replace.

See ADR `2026-04-20 — Auction lifecycle cron` and ADR
`2026-04-21 — Auction status Snowflake push` for the full rationale.
