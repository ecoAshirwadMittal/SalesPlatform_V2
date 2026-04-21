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
push) are implemented as event listeners under
`service/auctions/lifecycle/stub/` — currently logging-only stubs that
sub-projects 1-6 will replace.

See ADR `2026-04-20 — Auction lifecycle cron` for the full rationale.
