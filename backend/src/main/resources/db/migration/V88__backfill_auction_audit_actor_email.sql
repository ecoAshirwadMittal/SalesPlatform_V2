-- ============================================================================
-- V88: Backfill audit-actor columns from numeric user-id to email
-- ----------------------------------------------------------------------------
-- Purpose:
--   Pre-fix (gap H28): AuctionService + AuctionController + Scheduling-
--   AuctionController stamped audit columns with `Authentication.getName()`,
--   which resolves to the principal's `toString()` — the JWT filter sets the
--   principal to the numeric user-id (Long), so rows ended up with values
--   like "9001" instead of "admin@test.com". The Auctions list rendered the
--   raw numeric value in the "Created By" column.
--
--   Source fix updates each call site to use CurrentPrincipal.displayName(),
--   which prefers the email (credentials) over the numeric principal. This
--   migration backfills any rows that were created before the source fix
--   shipped — safe to no-op if all values are already non-numeric.
--
--   Limited to numeric values: tokens like "system:lifecycle-cron" stay as
--   they are (they're already human-readable). The
--   `created_by ~ '^\d+$'` regex is the gate.
-- ----------------------------------------------------------------------------

UPDATE auctions.auctions a
   SET created_by = u.name
  FROM identity.users u
 WHERE a.created_by ~ '^\d+$'
   AND CAST(a.created_by AS bigint) = u.id;

UPDATE auctions.auctions a
   SET updated_by = u.name
  FROM identity.users u
 WHERE a.updated_by ~ '^\d+$'
   AND CAST(a.updated_by AS bigint) = u.id;

UPDATE auctions.scheduling_auctions sa
   SET created_by = u.name
  FROM identity.users u
 WHERE sa.created_by ~ '^\d+$'
   AND CAST(sa.created_by AS bigint) = u.id;

UPDATE auctions.scheduling_auctions sa
   SET updated_by = u.name
  FROM identity.users u
 WHERE sa.updated_by ~ '^\d+$'
   AND CAST(sa.updated_by AS bigint) = u.id;
