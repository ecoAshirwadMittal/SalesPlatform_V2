# Reserve Bid Snowflake Sync

## Push (on every local write)
Writes fire `ReserveBidChangedEvent` at `AFTER_COMMIT`. A transactional event listener running on `snowflakeExecutor` invokes `AUCTIONS.UPSERT_RESERVE_BID(JSON_CONTENT, ACTING_USER)`. Failure is logged but not propagated — Postgres commit stands.

## Pull (every 30 min, ShedLock-guarded)
`ReserveBidSyncScheduledJob` reads Snowflake's `MAX(LAST_UPDATE_DATETIME)`. If source is strictly newer than `reserve_bid_sync.last_sync_datetime`, delete all local rows, bulk insert fresh copy, update watermark. Does NOT publish `ReserveBidChangedEvent` — would otherwise cause push-pull echo loop.

## Feature flag
`eb.sync.enabled=true` (default). When false, both paths log "would sync" and no-op.
