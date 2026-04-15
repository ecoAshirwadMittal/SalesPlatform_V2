# Database Remediation Plan

> Generated: 2026-04-10 | Status: **Phase 5 Complete** | Findings: 24 (3 CRITICAL, 7 HIGH, 8 MEDIUM, 6 LOW)

---

## Migration Numbering Strategy

Current latest migration: **V45**. New migrations: **V46–V55**, grouped by phase.

---

## Phase 0: Configuration Only (No Schema Changes) ✅ COMPLETE

**Goal**: Fix config issues that require no schema or code changes beyond `application.yml`.

### Finding 19 — Remove redundant `connection-test-query` [LOW | Size S]

**File**: `backend/src/main/resources/application.yml`

**Change**: Remove `connection-test-query: SELECT 1` from `spring.datasource.hikari`. PostgreSQL JDBC is JDBC4 compliant — HikariCP uses `Connection.isValid()` by default, which is cheaper than a full round-trip SQL query.

### Finding 11 — Disable `out-of-order` for production profile [MEDIUM | Size S]

**File**: `backend/src/main/resources/application.yml`

**Change**: Add a `production` profile that overrides `flyway.out-of-order: false`. Keep `true` in dev.

```yaml
---
spring:
  config:
    activate:
      on-profile: production
  flyway:
    out-of-order: false
    validate-on-migrate: true
```

**Verification**: Start with `--spring.profiles.active=production`; confirm Flyway rejects out-of-order migrations.

**Risk**: None. Additive configuration.

---

## Phase 1: Critical Security Fixes ✅ COMPLETE

**Goal**: Eliminate credential storage and temporary password exposure. Production blockers.

### Finding 1 — Remove credential columns from integration tables [CRITICAL | Size M]

**Migration**: `V46__remove_integration_credentials.sql`

```sql
ALTER TABLE integration.oracle_config DROP COLUMN IF EXISTS password_hash;
ALTER TABLE integration.oracle_config DROP COLUMN IF EXISTS username;
ALTER TABLE integration.deposco_config DROP COLUMN IF EXISTS password_hash;
ALTER TABLE integration.deposco_config DROP COLUMN IF EXISTS username;
ALTER TABLE email.smtp_config DROP COLUMN IF EXISTS encrypted_password;
ALTER TABLE email.smtp_config DROP COLUMN IF EXISTS username;
```

**Java Changes**:
- Remove corresponding fields from `OracleConfig`, `DeposcoConfig`, `SmtpConfig` entity classes
- Confirm all integration services use `@Value` env var injection (AtpSyncService already does)

**Verification**: `\d integration.oracle_config` — columns gone. Backend starts cleanly. Integration services authenticate via env vars.

**Risk**: If any code reads credentials from DB tables, it will break. Grep all references first.

### Finding 2 — Add expiry/cleanup for `password_tmp` columns [CRITICAL | Size M]

**Migration**: `V47__password_tmp_constraints.sql`

```sql
ALTER TABLE user_mgmt.ecoatm_direct_users
  ADD COLUMN IF NOT EXISTS password_tmp_expires_at TIMESTAMP;

ALTER TABLE user_mgmt.ecoatm_direct_users
  ADD CONSTRAINT chk_password_tmp_requires_expiry
  CHECK (
    (password_tmp IS NULL AND password_tmp_expires_at IS NULL)
    OR (password_tmp IS NOT NULL AND password_tmp_expires_at IS NOT NULL)
  );

-- Clear stale migrated data
UPDATE user_mgmt.ecoatm_direct_users
SET password_tmp = NULL, password_confirm_tmp = NULL, password_tmp_expires_at = NULL
WHERE password_tmp IS NOT NULL;
```

**Java Changes**:
- Add `passwordTmpExpiresAt` field to `EcoAtmDirectUser` entity
- Add `@Scheduled(fixedRate = 900_000)` cleanup task that NULLs expired `password_tmp` rows
- Any password-change flow must set `password_tmp_expires_at = NOW() + 30 minutes`

**Verification**: Insert `password_tmp` without expiry → constraint violation. Wait for scheduled cleanup → confirmed NULLed.

### Finding 3 — Add FK and index on `pws.order.buyer_code_id` [CRITICAL | Size S]

**Migration**: `V48__order_buyer_code_fk_index.sql`

```sql
-- Pre-check: clean orphans first
-- SELECT DISTINCT buyer_code_id FROM pws."order"
--   WHERE buyer_code_id NOT IN (SELECT id FROM buyer_mgmt.buyer_codes);

ALTER TABLE pws."order"
  ADD CONSTRAINT fk_order_buyer_code
  FOREIGN KEY (buyer_code_id) REFERENCES buyer_mgmt.buyer_codes(id);

CREATE INDEX idx_order_buyer_code_id ON pws."order"(buyer_code_id);
```

**Java Changes**: None. Entity already maps `buyerCodeId` as plain `Long`.

**Verification**: Insert order with non-existent `buyer_code_id` → FK violation. `EXPLAIN` shows index scan.

**Risk**: Orphaned `buyer_code_id` values will block the ALTER. Run orphan check query first.

---

## Phase 2: Performance Indexes + Type Fixes ✅ COMPLETE

**Goal**: Add missing indexes and fix TIMESTAMP/TIMESTAMPTZ mismatch.

**Status**: Completed 2026-04-10. V49 (indexes) and V50 (TIMESTAMPTZ) migrations created. Used the simpler `hibernate.jdbc.time_zone=UTC` approach to keep `LocalDateTime` in Java entities — no entity class changes needed. Added `email` schema to Flyway schemas list.

### Findings 8, 9, 15 — Add missing indexes [HIGH | Size S]

**Migration**: `V49__add_missing_indexes.sql`

```sql
-- Finding 8: offer status filtering
CREATE INDEX idx_offer_status ON pws.offer(status);

-- Finding 15: offer listing ORDER BY
CREATE INDEX idx_offer_updated_date ON pws.offer(updated_date DESC);

-- Composite for common query pattern
CREATE INDEX idx_offer_status_updated ON pws.offer(status, updated_date DESC);

-- Finding 9: mdm.device FK columns for pricing filter JOINs
CREATE INDEX idx_device_brand_id ON mdm.device(brand_id);
CREATE INDEX idx_device_category_id ON mdm.device(category_id);
CREATE INDEX idx_device_model_id ON mdm.device(model_id);
CREATE INDEX idx_device_condition_id ON mdm.device(condition_id);
CREATE INDEX idx_device_capacity_id ON mdm.device(capacity_id);
CREATE INDEX idx_device_carrier_id ON mdm.device(carrier_id);
CREATE INDEX idx_device_color_id ON mdm.device(color_id);
CREATE INDEX idx_device_grade_id ON mdm.device(grade_id);
```

**Java Changes**: None. Indexes are transparent to the application layer.

**Verification**:
- `EXPLAIN ANALYZE SELECT * FROM pws.offer WHERE status = 'Sales_Review' ORDER BY updated_date DESC` → index scan
- `EXPLAIN ANALYZE SELECT d.* FROM mdm.device d JOIN mdm.brand b ON b.id = d.brand_id WHERE b.name = 'Apple'` → index scan

**Risk**: Minimal. `CREATE INDEX` briefly locks tables but completes in milliseconds at current data volumes (~22k devices, ~1.6k offers).

### Finding 4 — Convert TIMESTAMP to TIMESTAMPTZ [HIGH | Size L]

**Migration**: `V50__timestamp_to_timestamptz.sql`

This is the largest single migration. No production data yet — ideal time to fix.

**SQL Changes** (all affected columns across pws, mdm, integration, email schemas):

```sql
-- Generate full list with:
-- SELECT table_schema, table_name, column_name
-- FROM information_schema.columns
-- WHERE data_type = 'timestamp without time zone'
--   AND table_schema IN ('pws', 'mdm', 'integration', 'email');

-- Drop and recreate the view that depends on these columns
DROP VIEW IF EXISTS pws.offer_and_orders_view;

-- pws.offer
ALTER TABLE pws.offer ALTER COLUMN submission_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN sales_review_completed_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN canceled_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN final_offer_submitted_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN counter_response_submitted_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.offer_item
ALTER TABLE pws.offer_item ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer_item ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws."order"
ALTER TABLE pws."order" ALTER COLUMN order_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN ship_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.shipment_detail
ALTER TABLE pws.shipment_detail ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.shipment_detail ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws admin tables (feature_flag, pws_constants, order_status_config,
--   maintenance_mode, rma_status, rma_template, rma_reason, navigation_menu)
-- pws.rma, pws.rma_item

-- mdm.device
ALTER TABLE mdm.device ALTER COLUMN last_sync_time TYPE TIMESTAMPTZ;
ALTER TABLE mdm.device ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.device ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- mdm.price_history
ALTER TABLE mdm.price_history ALTER COLUMN expiration_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.price_history ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.price_history ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- integration.* and email.* tables
-- (enumerate all timestamp columns from information_schema query above)

-- Recreate the view
CREATE OR REPLACE VIEW pws.offer_and_orders_view AS ...;
```

**Java Entity Changes**:

| Entity | Fields to Change (LocalDateTime → OffsetDateTime) |
|--------|---------------------------------------------------|
| `model/pws/Offer.java` | `submissionDate`, `salesReviewCompletedOn`, `canceledOn`, `finalOfferSubmittedOn`, `counterResponseSubmittedOn`, `createdDate`, `updatedDate` |
| `model/pws/Order.java` | `orderDate`, `shipDate`, `createdDate`, `updatedDate` |
| `model/pws/OfferItem.java` | `createdDate`, `updatedDate` |
| `model/pws/ShipmentDetail.java` | `createdDate`, `updatedDate` |
| `model/mdm/Device.java` | `lastSyncTime`, `createdDate`, `updatedDate` |
| All other entities with timestamp columns | Same pattern |

**Additional Java Changes**:
- `@PrePersist`/`@PreUpdate` methods: `LocalDateTime.now()` → `OffsetDateTime.now()`
- DTOs exposing timestamps (e.g., `OrderHistoryResponse`, `OfferResponse`)
- Service comparisons (e.g., `OrderHistoryService:139` `LocalDateTime.now().minusDays(RECENT_DAYS)` → `OffsetDateTime`)
- `AtpSyncService:65,259`: `LocalDateTime syncBeginTime` → `OffsetDateTime`

**Alternative (simpler)**: Keep `LocalDateTime` in Java, set `spring.jpa.properties.hibernate.jdbc.time_zone=UTC` in `application.yml`. Less explicit but lower change footprint. Recommended: use `OffsetDateTime` for correctness.

**Verification**: Insert timestamp, read back from a different timezone client. Confirm `\d pws.offer` shows `timestamp with time zone`.

**Risk**: **Highest-risk change.** Every query, DTO, and test touching timestamps needs updating. Mitigated by no production data.

**Dependencies**: Must recreate the view from V40 within this migration.

---

## Phase 3: N+1 Query Fixes and Service Refactoring ✅ COMPLETE

**Goal**: Eliminate three major N+1 patterns. Java-only changes, no migrations.

**Completed**: 2026-04-10. All 4 findings fixed, 419 tests passing.

### Finding 5 — Fix N+1 in OrderHistoryService [HIGH | Size M]

**Files**:
- `backend/src/main/java/com/ecoatm/salesplatform/service/OrderHistoryService.java`
- `backend/src/main/java/com/ecoatm/salesplatform/repository/mdm/DeviceRepository.java`

**Current problem** (lines 77–94): `deviceRepository.findById(item.getDeviceId())` called per offer item in `.map()` stream. 50 items = 51 queries.

**Fix for `getDetailsBySku`**:

```java
public List<OrderDetailBySkuResponse> getDetailsBySku(Long offerId) {
    List<OfferItem> items = offerItemRepository.findByOfferId(offerId);

    // Batch-load all devices in one query
    Set<Long> deviceIds = items.stream()
            .map(OfferItem::getDeviceId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    Map<Long, Device> deviceMap = deviceIds.isEmpty()
            ? Map.of()
            : deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));

    return items.stream()
            .map(item -> {
                Device device = deviceMap.get(item.getDeviceId());
                return new OrderDetailBySkuResponse(
                        item.getId(),
                        item.getSku(),
                        device != null ? device.getDescription() : null,
                        item.getFinalOfferQuantity(),
                        item.getShippedQty(),
                        item.getFinalOfferPrice(),
                        item.getFinalOfferTotalPrice()
                );
            })
            .toList();
}
```

**Fix for `getDetailsByDevice`**: Same batch-load pattern.

**Verification**: Enable `spring.jpa.show-sql: true`, call endpoint with 50 items → confirm 2 SQL queries (items + devices), not 51.

### Finding 6 — Fix N+1 in OfferReviewService.getStatusSummaries [HIGH | Size M]

**Files**:
- `backend/src/main/java/com/ecoatm/salesplatform/service/OfferReviewService.java`
- `backend/src/main/java/com/ecoatm/salesplatform/repository/pws/OfferRepository.java`

**Current problem** (lines 66–98): For each of 5 statuses, loads ALL offers with that status, then iterates items (N+1 lazy load). ~1,585 offers = ~1,590 queries per request.

**Fix**: Add aggregate `@Query` to `OfferRepository`:

```java
@Query("""
    SELECT o.status,
           COUNT(DISTINCT o.id),
           COALESCE(SUM(oi.quantity), 0),
           COALESCE(SUM(o.totalQty), 0),
           COALESCE(SUM(o.totalPrice), 0)
    FROM Offer o
    LEFT JOIN o.items oi
    WHERE o.status IN :statuses
    GROUP BY o.status
    """)
List<Object[]> getStatusSummaries(@Param("statuses") List<String> statuses);
```

Refactor `getStatusSummaries()` to call this single query and map results.

**Verification**: SQL logging → 1 query instead of 5 + N. Compare output values.

### Finding 7 — Fix AtpSyncService double-loading 22k devices [HIGH | Size M]

**File**: `backend/src/main/java/com/ecoatm/salesplatform/service/AtpSyncService.java`

**Current problem**:
1. `fullInventorySync()` (line 72) loads all devices
2. `applyAtpUpdates()` (line 259) loads all devices **again**
3. `updateReservedQuantities()` (line 327) calls `saveAll` a second time

**Fix**: Refactor `fullInventorySync()` to delegate to `applyAtpUpdates()` instead of duplicating the load-compare-save logic. Merge `updateReservedQuantities()` into the main pass with a single `saveAll` at the end.

**Verification**: Debug logging confirming 1 `findByIsActiveTrue` call per sync cycle.

### Finding 17 — Remove pre-flight role-check in BuyerCodeService [MEDIUM | Size S]

**File**: `backend/src/main/java/com/ecoatm/salesplatform/service/BuyerCodeService.java`

**Current problem** (line 38): `isBuyerRole(userId)` makes a separate query before every buyer code load. Called on every order history page load.

**Fix**: Combine into a single query with CTE:

```java
public List<BuyerCodeResponse> getBuyerCodesForUser(Long userId) {
    List<Object[]> rows = em.createNativeQuery("""
        WITH user_role AS (
            SELECT COALESCE(edu.is_buyer_role, false) AS is_buyer
            FROM identity.users u
            LEFT JOIN user_mgmt.ecoatm_direct_users edu ON edu.user_id = u.id
            WHERE u.id = :userId
        )
        SELECT DISTINCT bc.id, bc.code, b.company_name, bc.buyer_code_type
        FROM buyer_mgmt.buyer_codes bc
        JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
        JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
        LEFT JOIN user_mgmt.user_buyers ub
            ON ub.buyer_id = b.id AND ub.user_id = :userId
        CROSS JOIN user_role ur
        WHERE bc.status = 'Active'
          AND bc.soft_delete = false
          AND b.status = 'Active'
          AND (
            (ur.is_buyer AND ub.user_id IS NOT NULL
             AND bc.buyer_code_type NOT IN ('Purchasing_Order', 'Purchasing_Order_Data_Wipe'))
            OR
            (NOT ur.is_buyer
             AND bc.buyer_code_type IN ('Premium_Wholesale', 'Wholesale'))
          )
        ORDER BY bc.code
        """).setParameter("userId", userId).getResultList();
    // map to BuyerCodeResponse
}
```

**Verification**: Same buyer codes returned for buyer vs non-buyer users. 1 SQL query per call.

**Risk**: Medium. Combined query is more complex. Careful testing with both user types required.

---

## Phase 4: Referential Integrity and Data Quality ✅ COMPLETE (2026-04-10 — 419 tests green)

**Goal**: Add missing FK constraints and fix data type issues.

### Findings 12, 16 — FK constraints + RMA types [MEDIUM | Size S]

**Migration**: `V51__add_fk_constraints_and_fix_types.sql`

```sql
-- Finding 12: offer_item.device_id FK
DELETE FROM pws.offer_item
WHERE device_id IS NOT NULL
  AND device_id NOT IN (SELECT id FROM mdm.device);

ALTER TABLE pws.offer_item
  ADD CONSTRAINT fk_offer_item_device
  FOREIGN KEY (device_id) REFERENCES mdm.device(id);

CREATE INDEX idx_offer_item_device_id ON pws.offer_item(device_id);

-- Finding 12: offer.sales_rep_id FK
UPDATE pws.offer SET sales_rep_id = NULL
WHERE sales_rep_id IS NOT NULL
  AND sales_rep_id NOT IN (SELECT id FROM buyer_mgmt.sales_representatives);

ALTER TABLE pws.offer
  ADD CONSTRAINT fk_offer_sales_rep
  FOREIGN KEY (sales_rep_id) REFERENCES buyer_mgmt.sales_representatives(id);

CREATE INDEX idx_offer_sales_rep_id ON pws.offer(sales_rep_id);

-- Finding 16: RMA monetary columns INTEGER → NUMERIC(14,2)
ALTER TABLE pws.rma ALTER COLUMN request_sales_total TYPE NUMERIC(14,2);
ALTER TABLE pws.rma ALTER COLUMN approved_sales_total TYPE NUMERIC(14,2);
ALTER TABLE pws.rma_item ALTER COLUMN sale_price TYPE NUMERIC(14,2);
```

**Java Changes**: RMA entity fields `Integer` → `BigDecimal` for monetary columns.

**Verification**: Insert offer_item with non-existent device_id → FK violation. Insert RMA with `sale_price = 99.99` → succeeds.

**Risk**: Orphan cleanup deletes/nulls data. Run check queries on dev data first.

### Findings 22, 23 — NOT NULL constraints on MDM [LOW | Size S]

**Migration**: `V52__mdm_not_null_constraints.sql`

```sql
-- Finding 22: device.sku NOT NULL
UPDATE mdm.device SET sku = 'UNKNOWN-' || id WHERE sku IS NULL;
ALTER TABLE mdm.device ALTER COLUMN sku SET NOT NULL;

-- Finding 23: lookup table name NOT NULL
UPDATE mdm.brand SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.brand ALTER COLUMN name SET NOT NULL;
-- Repeat for: category, model, condition, capacity, carrier, color, grade
```

**Verification**: `INSERT INTO mdm.brand (name) VALUES (NULL)` → NOT NULL violation.

---

## Phase 5: View, Schema, and Code Cleanup ✅ COMPLETE (2026-04-10 — 419 tests green)

**Goal**: Fix view inefficiency, clean up dead code, consolidate triggers, wire sequence.

### Finding 13 — Deduplicate view subqueries [MEDIUM | Size S]

**Migration**: `V53__fix_order_view_and_aliases.sql`

Replace two identical correlated subqueries with a single `LEFT JOIN LATERAL`:

```sql
CREATE OR REPLACE VIEW pws.offer_and_orders_view AS
SELECT
    COALESCE(o.id, -ofe.id) AS id,
    COALESCE(o.order_number, ofe.offer_number) AS order_number,
    ofe.submission_date AS offer_date,
    o.order_date,
    -- ... status CASE unchanged ...
    buyer_info.company_name AS buyer,
    buyer_info.company_name AS company,
    -- ... remaining columns ...
FROM pws.offer ofe
LEFT JOIN pws."order" o ON o.offer_id = ofe.id
LEFT JOIN LATERAL (
    SELECT b.company_name
    FROM buyer_mgmt.buyer_code_buyers bcb
    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
    WHERE bcb.buyer_code_id = ofe.buyer_code_id
    LIMIT 1
) buyer_info ON true
WHERE ofe.visible_in_history = true;
```

**Verification**: Compare `SELECT * FROM pws.offer_and_orders_view` before/after — results must be identical. `EXPLAIN` confirms single lateral join.

### Finding 10 — Reserved keyword `order` table [HIGH | Size M]

**Approach**: Document the quoting requirement. A full rename to `pws.purchase_order` would touch dozens of files and is deferred to a dedicated task. For now:
- Ensure `@Table(name = "\"order\"", schema = "pws")` is explicit in `Order.java`
- Add code comment documenting the reserved keyword issue

### Finding 14 — Consolidate trigger logic [MEDIUM | Size S]

- `R__apply_triggers.sql` is already the single source of truth (uses `DROP TRIGGER IF EXISTS` + `CREATE TRIGGER`)
- Add a comment to V11 that trigger block is superseded by the repeatable migration
- Extend `R__apply_triggers.sql` to cover pws/mdm/integration/email tables with `updated_date`

### Finding 18 — Wire `identity.users_id_seq` [MEDIUM | Size S]

**Migration**: `V54__wire_users_id_sequence.sql`

```sql
ALTER TABLE identity.users ALTER COLUMN id SET DEFAULT nextval('identity.users_id_seq');
ALTER SEQUENCE identity.users_id_seq OWNED BY identity.users.id;
```

**Verification**: Insert user without explicit `id` → auto-assigned from sequence.

### Finding 24 — Rename hex_code columns [LOW | Size S]

**Migration**: `V55__rename_css_columns_and_cleanup.sql`

```sql
ALTER TABLE pws.order_status_config RENAME COLUMN internal_hex_code TO internal_css_class;
ALTER TABLE pws.order_status_config RENAME COLUMN external_hex_code TO external_css_class;

-- Finding 21: Remove dead code
ALTER TABLE integration.api_log DROP CONSTRAINT IF EXISTS api_log_legacy_id_key;
ALTER TABLE integration.api_log DROP COLUMN IF EXISTS legacy_id;
```

**Java Changes**: Update entity field names and any DTO references.

### Finding 20 — Session cleanup [LOW | Size S]

No migration needed. Add Spring `@Scheduled` task:

```java
@Scheduled(fixedRate = 3_600_000) // hourly
public void cleanupExpiredSessions() {
    em.createNativeQuery("""
        DELETE FROM identity.sessions
        WHERE last_active < NOW() - INTERVAL '30 days'
        """).executeUpdate();
}
```

---

## Phase Dependency Graph

```
Phase 0 (config)          ← no dependencies, deploy first
    │
Phase 1 (security)        ← V46, V47, V48
    │
Phase 2 (indexes + types) ← V49, V50 (V50 recreates view)
    │
Phase 3 (N+1 fixes)       ← Java-only, no migration deps
    │                        Deploy AFTER Phase 2 if OffsetDateTime changes
    │
Phase 4 (FK + data types) ← V51, V52 (independent of Phase 3)
    │
Phase 5 (cleanup)         ← V53, V54, V55

Phases 3 and 4 can run in parallel.
```

---

## Summary Table

| # | Finding | Severity | Phase | Migration | Size | Key Files |
|---|---------|----------|-------|-----------|------|-----------|
| 19 | Redundant connection-test-query | LOW | 0 | — | S | application.yml |
| 11 | Flyway out-of-order in prod | MEDIUM | 0 | — | S | application.yml |
| 1 | Plaintext credential columns | CRITICAL | 1 | V46 | M | V46.sql, entity classes |
| 2 | password_tmp no expiry | CRITICAL | 1 | V47 | M | V47.sql, EcoAtmDirectUser, scheduled task |
| 3 | order.buyer_code_id no FK/index | CRITICAL | 1 | V48 | S | V48.sql |
| 8 | No index on offer(status) | HIGH | 2 | V49 | S | V49.sql |
| 15 | No index on offer(updated_date) | HIGH | 2 | V49 | S | V49.sql |
| 9 | No indexes on device FK columns | HIGH | 2 | V49 | S | V49.sql |
| 4 | TIMESTAMP vs TIMESTAMPTZ | HIGH | 2 | V50 | L | V50.sql, all entities/DTOs/services |
| 5 | N+1 in OrderHistoryService | HIGH | 3 | — | M | OrderHistoryService.java, DeviceRepository.java |
| 6 | N+1 in OfferReviewService | HIGH | 3 | — | M | OfferReviewService.java, OfferRepository.java |
| 7 | AtpSyncService double-load | HIGH | 3 | — | M | AtpSyncService.java |
| 17 | BuyerCodeService pre-flight query | MEDIUM | 3 | — | S | BuyerCodeService.java |
| 12 | Missing FK on offer_item.device_id | MEDIUM | 4 | V51 | S | V51.sql |
| 16 | RMA INTEGER monetary columns | MEDIUM | 4 | V51 | S | V51.sql, RMA entity |
| 22 | device.sku nullable | LOW | 4 | V52 | S | V52.sql |
| 23 | MDM lookup name nullable | LOW | 4 | V52 | S | V52.sql |
| 13 | Duplicate subquery in view | MEDIUM | 5 | V53 | S | V53.sql |
| 10 | Reserved keyword table name | HIGH | 5 | V53 | M | Order.java (document) |
| 14 | Trigger duplication | MEDIUM | 5 | — | S | R__apply_triggers.sql |
| 18 | users_id_seq not wired | MEDIUM | 5 | V54 | S | V54.sql |
| 24 | hex_code column misnaming | LOW | 5 | V55 | S | V55.sql, entity |
| 20 | sessions no TTL | LOW | 5 | — | S | Scheduled task class |
| 21 | api_log.legacy_id dead code | LOW | 5 | V55 | S | V55.sql |

---

## Verification Checklist

- [ ] Phase 0: Backend starts with production profile, Flyway validates strict order
- [ ] Phase 1: No credential columns in DB, FK on order.buyer_code_id enforced
- [ ] Phase 2: `EXPLAIN ANALYZE` shows index scans on offer/device queries, all timestamps are TIMESTAMPTZ
- [ ] Phase 3: SQL logging confirms batch queries (2 instead of N+1), sync loads devices once
- [ ] Phase 4: FK violations on orphaned device_id/sales_rep_id, RMA accepts decimal amounts
- [ ] Phase 5: View uses single lateral join, sequence auto-assigns user IDs, sessions cleaned up
