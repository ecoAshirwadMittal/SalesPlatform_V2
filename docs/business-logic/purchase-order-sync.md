# Purchase Order — Snowflake sync

**Direction:** push-only. Mendix authoring + modern admin UI write
PostgreSQL; the `AUCTIONS.UPSERT_PURCHASE_ORDER` stored proc replicates
to Snowflake. No pull cron, no scheduled reconciliation.

**Trigger:** every successful header CRUD + every successful Excel
upload publishes `PurchaseOrderChangedEvent(poId, action)`. Listener
runs `@TransactionalEventListener(AFTER_COMMIT)` on the
`snowflakeExecutor` thread pool (shared with EB).

**Failure posture:** writer exception is logged at WARN, the
`integration.snowflake_sync_log` row records the attempt, and the
Postgres commit stands. Recovery is "admin re-uploads the Excel" — the
re-upload produces a fresh `UPSERT` event.

**Watermark:** per-row `purchase_order.po_refresh_timestamp` records
the last successful push. Surfaced in the admin grid so stale POs are
visible.

**Toggle:** `po.sync.enabled` checked at call-time; runtime-toggleable
via property reload. `po.sync.writer=logging|jdbc` selects the impl
via `@ConditionalOnProperty`.

**Contrast vs EB (sub-project 4A):** EB is bidirectional because
Snowflake is the authoritative source (external pricing engine writes
it). PO is push-only because authoring happens in modern; Snowflake is
a downstream read replica with no upstream-truth role.
