# Sub-project 4A: EB Module Port — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete port of the Mendix `ecoatm_eb` (ExchangeBid) module to modern — schema, admin CRUD + Excel upload/download + audit viewer + bidirectional Snowflake sync — so sub-project 4C's target-price CTE can join against a real, populated, kept-in-sync `auctions.reserve_bid` table.

**Architecture:** 3-tier split per user choice (controller + single service + repositories). Full bidirectional Snowflake sync — push on write via event listener on `snowflakeExecutor`; pull on 30-minute cron with ShedLock and delete-all-then-reinsert semantics. `product_id` stored as VARCHAR(100) for direct join with `bid_data.ecoid`. No Delete-All button (operational risk). Feature guard via Spring property (`eb.sync.enabled`) since modern lacks a generic feature-flag service.

**Tech Stack:** Spring Boot 3 + Java 21 + JPA + Flyway + PostgreSQL + Snowflake JDBC + ShedLock + Apache POI (Excel) + Next.js 16 + Playwright.

**Design doc:** `docs/tasks/auction-eb-module-design.md`

---

## File Structure

### New backend files

```
backend/src/main/java/com/ecoatm/salesplatform/
├── controller/admin/
│   └── ReserveBidController.java
├── service/auctions/reservebid/
│   ├── ReserveBidService.java
│   ├── ReserveBidSyncScheduledJob.java
│   ├── ReserveBidExcelParser.java
│   ├── ReserveBidExcelWriter.java
│   ├── ReserveBidException.java
│   └── ReserveBidValidationException.java
├── service/auctions/snowflake/
│   ├── ReserveBidSnowflakePushListener.java
│   ├── ReserveBidSnowflakeWriter.java
│   ├── JdbcReserveBidSnowflakeWriter.java
│   ├── LoggingReserveBidSnowflakeWriter.java
│   ├── ReserveBidSnowflakeReader.java
│   └── ReserveBidSnowflakePayload.java
├── repository/auctions/
│   ├── ReserveBidRepository.java
│   ├── ReserveBidAuditRepository.java
│   └── ReserveBidSyncRepository.java
├── model/auctions/
│   ├── ReserveBid.java
│   ├── ReserveBidAudit.java
│   └── ReserveBidSync.java
├── dto/
│   ├── ReserveBidRow.java
│   ├── ReserveBidRequest.java
│   ├── ReserveBidUploadResult.java
│   ├── ReserveBidAuditRow.java
│   ├── ReserveBidAuditResponse.java
│   ├── ReserveBidListResponse.java
│   └── ReserveBidSyncStatus.java
└── event/
    └── ReserveBidChangedEvent.java
```

### Modified backend files
- `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java` — register `/api/v1/admin/reserve-bids/**` with `ROLE_Administrator`
- `backend/src/main/resources/application.yml` — add `eb.sync.*` config block

### New schema migrations
- `backend/src/main/resources/db/migration/V74__auctions_reserve_bid.sql`
- `backend/src/main/resources/db/migration/V75__data_auctions_reserve_bid.sql` (generated)

### New extractor script
- `migration_scripts/extract_eb_data.py`

### New frontend files
```
frontend/src/
├── lib/api/reserveBidClient.ts
├── lib/types/reserveBid.ts
└── app/(dashboard)/admin/auctions-data-center/reserve-bids/
    ├── page.tsx
    ├── [id]/page.tsx
    ├── [id]/audit/page.tsx
    └── upload/page.tsx
```

### Docs updates
- `docs/api/rest-endpoints.md` — append `## Reserve Bids (EB)` section
- `docs/architecture/decisions.md` — new ADR `2026-04-22 — Sub-project 4A: EB module port`
- `docs/architecture/data-model.md` — add reserve_bid tables
- `docs/app-metadata/modules.md` — add EB module entry
- `docs/business-logic/reserve-bid-sync.md` — new file
- `docs/deployment/setup.md` — add `eb.sync.*` config
- `docs/testing/coverage.md` — new package coverage

### New test fixtures
```
backend/src/test/resources/fixtures/
├── reserve-bid-sample.xlsx
├── reserve-bid-with-errors.xlsx
└── reserve-bid-round-trip.xlsx
```

---

## Task 1: V74 schema migration

**Files:**
- Create: `backend/src/main/resources/db/migration/V74__auctions_reserve_bid.sql`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/migration/V74MigrationTest.java`

- [ ] **Step 1: Write the failing migration test**

```java
package com.ecoatm.salesplatform.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class V74MigrationTest {

    @Autowired JdbcTemplate jdbc;

    @Test
    void reserveBidTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='reserve_bid'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void reserveBidAuditTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='reserve_bid_audit'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void reserveBidSyncSingletonSeeded() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.reserve_bid_sync", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void uniqueProductGradeConstraintEnforced() {
        jdbc.update("INSERT INTO auctions.reserve_bid (product_id, grade, bid) "
                  + "VALUES ('18509', 'A_YYY', 10.00)");
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.reserve_bid "
                               + "(product_id, grade, bid) VALUES ('18509', 'A_YYY', 20.00)"));
    }

    @Test
    void auditCascadesOnReserveBidDelete() {
        Long rbId = jdbc.queryForObject(
                "INSERT INTO auctions.reserve_bid (product_id, grade, bid) "
              + "VALUES ('99001', 'X_YYY', 5.00) RETURNING id", Long.class);
        jdbc.update("INSERT INTO auctions.reserve_bid_audit "
                  + "(reserve_bid_id, old_price, new_price) VALUES (?, 5.00, 6.00)", rbId);
        jdbc.update("DELETE FROM auctions.reserve_bid WHERE id = ?", rbId);
        Integer orphans = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.reserve_bid_audit WHERE reserve_bid_id = ?",
                Integer.class, rbId);
        assertThat(orphans).isZero();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=V74MigrationTest`
Expected: FAIL — `relation "auctions.reserve_bid" does not exist`

- [ ] **Step 3: Write the V74 migration**

Content (copy verbatim):

```sql
-- =============================================================================
-- V74: auctions — reserve_bid (ExchangeBid floor prices)
-- Source: ecoatm_eb$reservebid (15,875 rows), ecoatm_eb$reservedbidaudit (4),
--         ecoatm_eb$reservebidsync (1).
-- Drops: ecoatm_eb$reservebidfile (0 rows; modern streams download),
--        both junctions (empty or collapsed to FK).
-- Design: docs/tasks/auction-eb-module-design.md
-- =============================================================================

CREATE TABLE auctions.reserve_bid (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    product_id               VARCHAR(100)    NOT NULL,
    grade                    VARCHAR(200)    NOT NULL,
    brand                    VARCHAR(200),
    model                    VARCHAR(200),
    bid                      NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    last_update_datetime     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    last_awarded_min_price   NUMERIC(14, 4),
    last_awarded_week        VARCHAR(20),
    bid_valid_week_date      VARCHAR(20),
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_reserve_bid_product_grade UNIQUE (product_id, grade)
);

COMMENT ON TABLE  auctions.reserve_bid IS 'ExchangeBid reserve floor prices per (product_id, grade) (ecoatm_eb$reservebid)';
COMMENT ON COLUMN auctions.reserve_bid.product_id IS 'VARCHAR(100) matches auctions.bid_data.ecoid join key';
COMMENT ON COLUMN auctions.reserve_bid.bid IS 'Reserve floor — joined into target-price CTE GREATEST() by sub-project 4C';

CREATE INDEX idx_rb_product_grade  ON auctions.reserve_bid(product_id, grade);
CREATE INDEX idx_rb_last_update    ON auctions.reserve_bid(last_update_datetime DESC);

CREATE TABLE auctions.reserve_bid_audit (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    reserve_bid_id           BIGINT          NOT NULL
                                            REFERENCES auctions.reserve_bid(id)
                                            ON DELETE CASCADE,
    old_price                NUMERIC(14, 4)  NOT NULL,
    new_price                NUMERIC(14, 4)  NOT NULL,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE auctions.reserve_bid_audit IS 'Price-change audit trail (ecoatm_eb$reservedbidaudit)';

CREATE INDEX idx_rba_reserve_bid ON auctions.reserve_bid_audit(reserve_bid_id);
CREATE INDEX idx_rba_created     ON auctions.reserve_bid_audit(created_date DESC);

CREATE TABLE auctions.reserve_bid_sync (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    last_sync_datetime       TIMESTAMPTZ,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE auctions.reserve_bid_sync IS 'Singleton — last successful Snowflake→local pull watermark (ecoatm_eb$reservebidsync)';

INSERT INTO auctions.reserve_bid_sync (last_sync_datetime) VALUES (NULL);
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=V74MigrationTest`
Expected: PASS — all 5 assertions green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/resources/db/migration/V74__auctions_reserve_bid.sql \
        backend/src/test/java/com/ecoatm/salesplatform/migration/V74MigrationTest.java
git commit -m "feat(auctions): V74 reserve_bid + audit + sync schema"
```

---

## Task 2: Extractor script + V75 data migration

**Files:**
- Create: `migration_scripts/extract_eb_data.py`
- Generated: `backend/src/main/resources/db/migration/V75__data_auctions_reserve_bid.sql`

- [ ] **Step 1: Write the extractor skeleton**

```python
#!/usr/bin/env python3
"""
Extracts ecoatm_eb data from a Mendix source database and emits
V75__data_auctions_reserve_bid.sql. Mirrors extract_qa_data.py CLI + FK-remap pattern.
"""
import argparse
import os
import subprocess
import sys
from pathlib import Path

import psycopg2

DB_HOSTS = {
    "qa-0327": "localhost",
    "prod-0325": "localhost",
}

OUT_PATH = Path(__file__).parent.parent / "backend/src/main/resources/db/migration/V75__data_auctions_reserve_bid.sql"


def connect(db_name: str):
    return psycopg2.connect(
        host=DB_HOSTS[db_name],
        dbname=db_name,
        user="postgres",
        password=os.environ["PGPASSWORD"],
    )


def sql_literal(v):
    if v is None:
        return "NULL"
    if isinstance(v, (int, float)):
        return str(v)
    return "'" + str(v).replace("'", "''") + "'"


def extract_reserve_bids(conn):
    with conn.cursor() as cur:
        cur.execute("""
            SELECT id, productid, grade, brand, model, bid,
                   lastupdatedatetime, lastawardedminprice,
                   lastawardedweek, bidvalidweekdate,
                   createddate, changeddate
            FROM "ecoatm_eb$reservebid"
            ORDER BY id
        """)
        return cur.fetchall()


def extract_audit(conn):
    with conn.cursor() as cur:
        cur.execute("""
            SELECT a.id, a.oldprice, a.newprice, a.createddate, a.changeddate,
                   j."ecoatm_eb$reservebidid"
            FROM "ecoatm_eb$reservedbidaudit" a
            LEFT JOIN "ecoatm_eb$reservedbidaudit_reservebid" j
              ON j."ecoatm_eb$reservedbidauditid" = a.id
            ORDER BY a.id
        """)
        return cur.fetchall()


def extract_sync(conn):
    with conn.cursor() as cur:
        cur.execute("""
            SELECT id, lastsyncdatetime, createddate, changeddate
            FROM "ecoatm_eb$reservebidsync" LIMIT 1
        """)
        return cur.fetchone()


def emit_sql(reserve_bids, audit, sync):
    lines = [
        "-- =============================================================================",
        "-- V75: auctions — reserve_bid data load (GENERATED by extract_eb_data.py)",
        "-- =============================================================================",
        "",
        "-- reserve_bid rows — legacy_id preserved for FK remap",
        "INSERT INTO auctions.reserve_bid (legacy_id, product_id, grade, brand, model, bid, last_update_datetime, last_awarded_min_price, last_awarded_week, bid_valid_week_date, created_date, changed_date) VALUES",
    ]
    rb_vals = []
    for rb in reserve_bids:
        (rid, productid, grade, brand, model, bid,
         lut, lawmp, law, bvwd, cd, chd) = rb
        rb_vals.append(
            f"({rid}, {sql_literal(str(productid) if productid is not None else None)}, "
            f"{sql_literal(grade)}, {sql_literal(brand)}, {sql_literal(model)}, "
            f"{sql_literal(bid)}, {sql_literal(lut)}, {sql_literal(lawmp)}, "
            f"{sql_literal(law)}, {sql_literal(bvwd)}, "
            f"{sql_literal(cd)}, {sql_literal(chd)})"
        )
    lines.append(",\n".join(rb_vals) + ";")
    lines.append("")

    if audit:
        lines.append("-- reserve_bid_audit rows — reserve_bid_id remapped via legacy_id lookup")
        lines.append("INSERT INTO auctions.reserve_bid_audit (legacy_id, reserve_bid_id, old_price, new_price, created_date, changed_date)")
        lines.append("SELECT audit_raw.legacy_id, rb.id, audit_raw.old_price, audit_raw.new_price, audit_raw.created_date, audit_raw.changed_date")
        lines.append("FROM (VALUES")
        audit_vals = []
        for a in audit:
            aid, op, np, cd, chd, legacy_rb_id = a
            audit_vals.append(
                f"({aid}, {legacy_rb_id if legacy_rb_id else 'NULL'}, "
                f"{sql_literal(op)}, {sql_literal(np)}, "
                f"{sql_literal(cd)}, {sql_literal(chd)})"
            )
        lines.append(",\n".join(audit_vals))
        lines.append(") AS audit_raw(legacy_id, legacy_rb_id, old_price, new_price, created_date, changed_date)")
        lines.append("JOIN auctions.reserve_bid rb ON rb.legacy_id = audit_raw.legacy_rb_id;")
        lines.append("")

    if sync:
        sid, lsd, cd, chd = sync
        lines.append("-- reserve_bid_sync singleton (replace the V74 seed row)")
        lines.append("DELETE FROM auctions.reserve_bid_sync;")
        lines.append(
            f"INSERT INTO auctions.reserve_bid_sync (legacy_id, last_sync_datetime, created_date, changed_date) "
            f"VALUES ({sid}, {sql_literal(lsd)}, {sql_literal(cd)}, {sql_literal(chd)});"
        )
        lines.append("")

    return "\n".join(lines) + "\n"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-db", required=True, choices=DB_HOSTS.keys())
    args = parser.parse_args()

    with connect(args.source_db) as conn:
        rb = extract_reserve_bids(conn)
        audit = extract_audit(conn)
        sync = extract_sync(conn)

    OUT_PATH.write_text(emit_sql(rb, audit, sync))
    print(f"Wrote {len(rb)} reserve_bid + {len(audit)} audit + "
          f"{1 if sync else 0} sync rows to {OUT_PATH}")


if __name__ == "__main__":
    sys.exit(main())
```

- [ ] **Step 2: Run it against the QA source DB**

Run: `cd migration_scripts && PGPASSWORD='Agarwal1$' python extract_eb_data.py --source-db qa-0327`
Expected: `Wrote 15875 reserve_bid + 4 audit + 1 sync rows to ...V75__data_auctions_reserve_bid.sql`

- [ ] **Step 3: Verify V75 applies cleanly**

Run: `cd backend && mvn flyway:migrate`
Expected: Flyway applies V75 without error.

- [ ] **Step 4: Data-load verification test**

Add to `V74MigrationTest`:

```java
@Test
void dataLoaded() {
    Integer rb = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid", Integer.class);
    assertThat(rb).isGreaterThan(15000);  // 15,875 expected ± drift
    Integer audit = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid_audit", Integer.class);
    assertThat(audit).isGreaterThanOrEqualTo(4);
    // sync singleton still 1 row after replacement
    Integer sync = jdbc.queryForObject("SELECT COUNT(*) FROM auctions.reserve_bid_sync", Integer.class);
    assertThat(sync).isEqualTo(1);
}
```

Run: `cd backend && mvn test -Dtest=V74MigrationTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add migration_scripts/extract_eb_data.py \
        backend/src/main/resources/db/migration/V75__data_auctions_reserve_bid.sql \
        backend/src/test/java/com/ecoatm/salesplatform/migration/V74MigrationTest.java
git commit -m "feat(auctions): EB extractor + V75 data load (15,875 rows)"
```

---

## Task 3: JPA entities

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBid.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBidAudit.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBidSync.java`

- [ ] **Step 1: Write the ReserveBid entity**

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid", schema = "auctions",
       uniqueConstraints = @UniqueConstraint(name = "uq_reserve_bid_product_grade",
                                             columnNames = {"product_id", "grade"}))
public class ReserveBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;
    @Column(name = "product_id", nullable = false, length = 100) private String productId;
    @Column(nullable = false, length = 200) private String grade;
    @Column(length = 200) private String brand;
    @Column(length = 200) private String model;

    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal bid = BigDecimal.ZERO;
    @Column(name = "last_update_datetime", nullable = false) private Instant lastUpdateDatetime = Instant.now();

    @Column(name = "last_awarded_min_price", precision = 14, scale = 4)
    private BigDecimal lastAwardedMinPrice;

    @Column(name = "last_awarded_week", length = 20)   private String lastAwardedWeek;
    @Column(name = "bid_valid_week_date", length = 20) private String bidValidWeekDate;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getBid() { return bid; }
    public void setBid(BigDecimal bid) { this.bid = bid; }
    public Instant getLastUpdateDatetime() { return lastUpdateDatetime; }
    public void setLastUpdateDatetime(Instant v) { this.lastUpdateDatetime = v; }
    public BigDecimal getLastAwardedMinPrice() { return lastAwardedMinPrice; }
    public void setLastAwardedMinPrice(BigDecimal v) { this.lastAwardedMinPrice = v; }
    public String getLastAwardedWeek() { return lastAwardedWeek; }
    public void setLastAwardedWeek(String v) { this.lastAwardedWeek = v; }
    public String getBidValidWeekDate() { return bidValidWeekDate; }
    public void setBidValidWeekDate(String v) { this.bidValidWeekDate = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
```

- [ ] **Step 2: Write the ReserveBidAudit entity**

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid_audit", schema = "auctions")
public class ReserveBidAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;

    @Column(name = "reserve_bid_id", nullable = false) private Long reserveBidId;

    @Column(name = "old_price", nullable = false, precision = 14, scale = 4) private BigDecimal oldPrice;
    @Column(name = "new_price", nullable = false, precision = 14, scale = 4) private BigDecimal newPrice;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Long getReserveBidId() { return reserveBidId; }
    public void setReserveBidId(Long v) { this.reserveBidId = v; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal v) { this.oldPrice = v; }
    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal v) { this.newPrice = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
```

- [ ] **Step 3: Write the ReserveBidSync entity**

```java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid_sync", schema = "auctions")
public class ReserveBidSync {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;
    @Column(name = "last_sync_datetime")       private Instant lastSyncDatetime;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Instant getLastSyncDatetime() { return lastSyncDatetime; }
    public void setLastSyncDatetime(Instant v) { this.lastSyncDatetime = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
```

- [ ] **Step 4: Verify compile**

Run: `cd backend && mvn compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBid.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBidAudit.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/ReserveBidSync.java
git commit -m "feat(auctions): ReserveBid + audit + sync JPA entities"
```

---

## Task 4: Repositories

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/ReserveBidRepository.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/ReserveBidAuditRepository.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/ReserveBidSyncRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/ReserveBidRepositoryIT.java`

- [ ] **Step 1: Write the failing repository IT**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReserveBidRepositoryIT {

    @Autowired ReserveBidRepository repo;
    @Autowired ReserveBidAuditRepository auditRepo;
    @Autowired ReserveBidSyncRepository syncRepo;

    @Test
    void findByProductIdAndGrade() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("77001");
        rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("10.0000"));
        repo.save(rb);

        Optional<ReserveBid> found = repo.findByProductIdAndGrade("77001", "A_YYY");
        assertThat(found).isPresent();
        assertThat(found.get().getBid()).isEqualByComparingTo("10.00");
    }

    @Test
    void syncSingletonReachable() {
        Optional<ReserveBidSync> s = syncRepo.findFirstByOrderByIdAsc();
        assertThat(s).isPresent();
    }

    @Test
    void auditByReserveBidId() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("77002");
        rb.setGrade("B_NYY");
        rb.setBid(new BigDecimal("5"));
        repo.save(rb);

        ReserveBidAudit a = new ReserveBidAudit();
        a.setReserveBidId(rb.getId());
        a.setOldPrice(new BigDecimal("5"));
        a.setNewPrice(new BigDecimal("6"));
        auditRepo.save(a);

        assertThat(auditRepo.findByReserveBidIdOrderByCreatedDateDesc(rb.getId())).hasSize(1);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=ReserveBidRepositoryIT`
Expected: FAIL — repositories not defined.

- [ ] **Step 3: Write ReserveBidRepository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReserveBidRepository extends JpaRepository<ReserveBid, Long> {

    Optional<ReserveBid> findByProductIdAndGrade(String productId, String grade);

    boolean existsByProductIdAndGrade(String productId, String grade);

    @Query("""
        SELECT rb FROM ReserveBid rb
        WHERE (:productId IS NULL OR rb.productId = :productId)
          AND (:grade IS NULL OR LOWER(rb.grade) LIKE LOWER(CONCAT('%', :grade, '%')))
          AND (:minBid IS NULL OR rb.bid >= :minBid)
          AND (:maxBid IS NULL OR rb.bid <= :maxBid)
          AND (:updatedSince IS NULL OR rb.lastUpdateDatetime >= :updatedSince)
        """)
    Page<ReserveBid> search(@Param("productId") String productId,
                            @Param("grade") String grade,
                            @Param("minBid") BigDecimal minBid,
                            @Param("maxBid") BigDecimal maxBid,
                            @Param("updatedSince") Instant updatedSince,
                            Pageable pageable);

    @Query("SELECT MAX(rb.lastUpdateDatetime) FROM ReserveBid rb")
    Optional<Instant> findMaxLastUpdateDatetime();

    List<ReserveBid> findByProductIdInAndGradeIn(List<String> productIds, List<String> grades);

    @Modifying
    @Query(value = "DELETE FROM auctions.reserve_bid", nativeQuery = true)
    void deleteAllNative();
}
```

- [ ] **Step 4: Write ReserveBidAuditRepository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReserveBidAuditRepository extends JpaRepository<ReserveBidAudit, Long> {

    List<ReserveBidAudit> findByReserveBidIdOrderByCreatedDateDesc(Long reserveBidId);

    Page<ReserveBidAudit> findByReserveBidIdOrderByCreatedDateDesc(Long reserveBidId, Pageable pageable);
}
```

- [ ] **Step 5: Write ReserveBidSyncRepository**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveBidSyncRepository extends JpaRepository<ReserveBidSync, Long> {

    Optional<ReserveBidSync> findFirstByOrderByIdAsc();
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=ReserveBidRepositoryIT`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/ReserveBid*.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/ReserveBidRepositoryIT.java
git commit -m "feat(auctions): ReserveBid repositories with search + cascade-aware queries"
```

---

## Task 5: DTOs + exceptions + event

**Files:**
- Create: 7 DTOs, 2 exceptions, 1 event

- [ ] **Step 1: Write `ReserveBidRow`**

```java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReserveBidRow(
        long id,
        String productId,
        String grade,
        String brand,
        String model,
        BigDecimal bid,
        Instant lastUpdateDatetime,
        BigDecimal lastAwardedMinPrice,
        String lastAwardedWeek,
        String bidValidWeekDate,
        Instant changedDate) {}
```

- [ ] **Step 2: Write `ReserveBidRequest`**

```java
package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ReserveBidRequest(
        @NotBlank String productId,
        @NotBlank String grade,
        String brand,
        String model,
        @NotNull @DecimalMin("0") BigDecimal bid,
        BigDecimal lastAwardedMinPrice,
        String lastAwardedWeek,
        String bidValidWeekDate) {}
```

- [ ] **Step 3: Write `ReserveBidUploadResult`**

```java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidUploadResult(
        int created,
        int updated,
        int unchanged,
        int auditsGenerated,
        List<UploadError> errors) {

    public record UploadError(int rowNumber, String productId, String grade, String reason) {}
}
```

- [ ] **Step 4: Write the remaining DTOs**

`ReserveBidAuditRow.java`:
```java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReserveBidAuditRow(
        long id,
        long reserveBidId,
        String productId,
        String grade,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        Instant createdDate,
        String changedByUsername) {}
```

`ReserveBidAuditResponse.java`:
```java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidAuditResponse(List<ReserveBidAuditRow> rows, long total, int page, int size) {}
```

`ReserveBidListResponse.java`:
```java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidListResponse(List<ReserveBidRow> rows, long total, int page, int size) {}
```

`ReserveBidSyncStatus.java`:
```java
package com.ecoatm.salesplatform.dto;

import java.time.Duration;
import java.time.Instant;

public record ReserveBidSyncStatus(
        Instant lastSyncDatetime,
        Instant sourceMaxDatetime,
        Duration drift,
        String state) {

    public static final String IN_SYNC = "IN_SYNC";
    public static final String BEHIND_SOURCE = "BEHIND_SOURCE";
    public static final String NEVER_SYNCED = "NEVER_SYNCED";
}
```

- [ ] **Step 5: Write the exceptions**

`ReserveBidException.java`:
```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

public class ReserveBidException extends RuntimeException {
    private final String code;

    public ReserveBidException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() { return code; }
}
```

`ReserveBidValidationException.java`:
```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

public class ReserveBidValidationException extends ReserveBidException {
    public ReserveBidValidationException(String code, String message) {
        super(code, message);
    }
}
```

- [ ] **Step 6: Write `ReserveBidChangedEvent`**

```java
package com.ecoatm.salesplatform.event;

import java.util.List;

public record ReserveBidChangedEvent(List<Long> changedIds, Action action) {

    public enum Action { UPSERT, DELETE }
}
```

- [ ] **Step 7: Verify compile**

Run: `cd backend && mvn compile`
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/ReserveBid*.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBid*Exception.java \
        backend/src/main/java/com/ecoatm/salesplatform/event/ReserveBidChangedEvent.java
git commit -m "feat(auctions): ReserveBid DTOs, exceptions, change event"
```

---

## Task 6: ReserveBidService — CRUD

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java`

- [ ] **Step 1: Write the failing CRUD tests**

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.dto.ReserveBidRequest;
import com.ecoatm.salesplatform.dto.ReserveBidRow;
import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidSyncRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveBidServiceTest {

    @Mock ReserveBidRepository repo;
    @Mock ReserveBidAuditRepository auditRepo;
    @Mock ReserveBidSyncRepository syncRepo;
    @Mock ApplicationEventPublisher publisher;

    ReserveBidService service;

    @BeforeEach
    void setUp() {
        service = new ReserveBidService(repo, auditRepo, syncRepo, publisher, null, null);
    }

    @Test
    void create_persistsRowAndPublishesEvent() {
        when(repo.existsByProductIdAndGrade("77101", "A_YYY")).thenReturn(false);
        when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
            ReserveBid rb = inv.getArgument(0);
            rb.setId(42L);
            return rb;
        });

        ReserveBidRow created = service.create(99L,
                new ReserveBidRequest("77101", "A_YYY", "Apple", "iPhone",
                        new BigDecimal("10"), null, null, null));

        assertThat(created.id()).isEqualTo(42L);
        verify(auditRepo, never()).save(any());   // no audit on CREATE

        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.UPSERT);
        assertThat(evt.getValue().changedIds()).containsExactly(42L);
    }

    @Test
    void create_rejectsDuplicateProductGrade() {
        when(repo.existsByProductIdAndGrade("77102", "A_YYY")).thenReturn(true);

        assertThatThrownBy(() -> service.create(99L,
                new ReserveBidRequest("77102", "A_YYY", null, null, BigDecimal.ONE, null, null, null)))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "DUPLICATE_PRODUCT_GRADE");
    }

    @Test
    void update_writesAuditOnPriceChange() {
        ReserveBid existing = new ReserveBid();
        existing.setId(5L);
        existing.setProductId("77103");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 5L,
                new ReserveBidRequest("77103", "A_YYY", null, null,
                        new BigDecimal("12.00"), null, null, null));

        verify(auditRepo).save(argThat(a ->
                a.getOldPrice().compareTo(new BigDecimal("10.00")) == 0 &&
                a.getNewPrice().compareTo(new BigDecimal("12.00")) == 0));
    }

    @Test
    void update_skipsAuditWhenPriceUnchanged() {
        ReserveBid existing = new ReserveBid();
        existing.setId(6L);
        existing.setProductId("77104");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(6L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 6L,
                new ReserveBidRequest("77104", "A_YYY", "NewBrand", null,
                        new BigDecimal("10.00"), null, null, null));

        verify(auditRepo, never()).save(any());
    }

    @Test
    void delete_publishesDeleteEvent() {
        ReserveBid existing = new ReserveBid();
        existing.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        service.delete(7L);

        verify(repo).delete(existing);
        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.DELETE);
        assertThat(evt.getValue().changedIds()).containsExactly(7L);
    }

    @Test
    void delete_throwsOnMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "RESERVE_BID_NOT_FOUND");
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: FAIL — ReserveBidService not defined.

- [ ] **Step 3: Write ReserveBidService skeleton + CRUD methods**

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidSyncRepository;
import com.ecoatm.salesplatform.service.auctions.snowflake.ReserveBidSnowflakeReader;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReserveBidService {

    private final ReserveBidRepository repo;
    private final ReserveBidAuditRepository auditRepo;
    private final ReserveBidSyncRepository syncRepo;
    private final ApplicationEventPublisher publisher;
    private final ReserveBidSnowflakeReader snowflakeReader;   // nullable in unit tests
    private final ReserveBidExcelParser excelParser;            // nullable in unit tests

    public ReserveBidService(ReserveBidRepository repo,
                             ReserveBidAuditRepository auditRepo,
                             ReserveBidSyncRepository syncRepo,
                             ApplicationEventPublisher publisher,
                             ReserveBidSnowflakeReader snowflakeReader,
                             ReserveBidExcelParser excelParser) {
        this.repo = repo;
        this.auditRepo = auditRepo;
        this.syncRepo = syncRepo;
        this.publisher = publisher;
        this.snowflakeReader = snowflakeReader;
        this.excelParser = excelParser;
    }

    @Transactional
    public ReserveBidRow create(long userId, ReserveBidRequest req) {
        if (repo.existsByProductIdAndGrade(req.productId(), req.grade())) {
            throw new ReserveBidException("DUPLICATE_PRODUCT_GRADE",
                    "reserve_bid exists for product=" + req.productId() + " grade=" + req.grade());
        }

        ReserveBid rb = new ReserveBid();
        applyRequest(rb, req);
        rb.setOwnerId(userId);
        rb.setChangedById(userId);
        rb.setLastUpdateDatetime(Instant.now());
        ReserveBid saved = repo.save(rb);

        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(saved.getId()), ReserveBidChangedEvent.Action.UPSERT));
        return toDto(saved);
    }

    @Transactional
    public ReserveBidRow update(long userId, long id, ReserveBidRequest req) {
        ReserveBid rb = repo.findById(id).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));

        BigDecimal oldPrice = rb.getBid();
        applyRequest(rb, req);
        rb.setChangedById(userId);
        rb.setChangedDate(Instant.now());

        boolean priceChanged = oldPrice != null && req.bid() != null
                && oldPrice.compareTo(req.bid()) != 0;
        if (priceChanged) {
            rb.setLastUpdateDatetime(Instant.now());
            ReserveBidAudit a = new ReserveBidAudit();
            a.setReserveBidId(rb.getId());
            a.setOldPrice(oldPrice);
            a.setNewPrice(req.bid());
            a.setOwnerId(userId);
            a.setChangedById(userId);
            auditRepo.save(a);
        }
        ReserveBid saved = repo.save(rb);

        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(saved.getId()), ReserveBidChangedEvent.Action.UPSERT));
        return toDto(saved);
    }

    @Transactional
    public void delete(long id) {
        ReserveBid rb = repo.findById(id).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));
        repo.delete(rb);
        publisher.publishEvent(new ReserveBidChangedEvent(
                List.of(rb.getId()), ReserveBidChangedEvent.Action.DELETE));
    }

    @Transactional(readOnly = true)
    public ReserveBidRow findById(long id) {
        return repo.findById(id).map(ReserveBidService::toDto).orElseThrow(() ->
                new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + id));
    }

    @Transactional(readOnly = true)
    public ReserveBidListResponse search(String productId, String grade,
                                         BigDecimal minBid, BigDecimal maxBid,
                                         Instant updatedSince, int page, int size) {
        Page<ReserveBid> p = repo.search(productId, grade, minBid, maxBid, updatedSince,
                PageRequest.of(page, size));
        return new ReserveBidListResponse(
                p.getContent().stream().map(ReserveBidService::toDto).toList(),
                p.getTotalElements(), page, size);
    }

    private static void applyRequest(ReserveBid rb, ReserveBidRequest req) {
        rb.setProductId(req.productId());
        rb.setGrade(req.grade());
        rb.setBrand(req.brand());
        rb.setModel(req.model());
        rb.setBid(req.bid());
        rb.setLastAwardedMinPrice(req.lastAwardedMinPrice());
        rb.setLastAwardedWeek(req.lastAwardedWeek());
        rb.setBidValidWeekDate(req.bidValidWeekDate());
    }

    static ReserveBidRow toDto(ReserveBid rb) {
        return new ReserveBidRow(
                rb.getId(), rb.getProductId(), rb.getGrade(),
                rb.getBrand(), rb.getModel(), rb.getBid(),
                rb.getLastUpdateDatetime(), rb.getLastAwardedMinPrice(),
                rb.getLastAwardedWeek(), rb.getBidValidWeekDate(),
                rb.getChangedDate());
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: PASS — 6 tests green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java
git commit -m "feat(auctions): ReserveBidService CRUD with audit on price change"
```

---

## Task 7: Excel parser + upload logic

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidExcelParser.java`
- Modify: `ReserveBidService` (add `upload` method)
- Create: `backend/src/test/resources/fixtures/reserve-bid-sample.xlsx` + `reserve-bid-with-errors.xlsx`
- Test: extend `ReserveBidServiceTest`

- [ ] **Step 1: Generate test fixtures**

Create `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidFixtureGenerator.java`:

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.file.Path;

/** Runs once in CI: java ReserveBidFixtureGenerator backend/src/test/resources/fixtures */
public final class ReserveBidFixtureGenerator {

    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args[0]);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            header(s, "ProductID", "Grade", "ModelName", "Price");
            addRow(s, 1, 10001, "A_YYY", "Pixel 7",    50.25);
            addRow(s, 2, 10002, "A_YYY", "Pixel 8",    60.00);
            addRow(s, 3, 10003, "B_NYY", "Moto G",     25.75);
            try (FileOutputStream out = new FileOutputStream(dir.resolve("reserve-bid-sample.xlsx").toFile())) {
                wb.write(out);
            }
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            header(s, "ProductID", "Grade", "ModelName", "Price");
            addRow(s, 1, 20001, "A_YYY", "Good",  10.00);
            addRow(s, 2, 20001, "A_YYY", "DupSheet", 15.00);     // duplicate in sheet
            addRow(s, 3, 20002, "",      "MissingGrade", 20.00); // missing grade
            addRow(s, 4, 20003, "B_NYY", "NegPrice",  -5.00);     // negative price
            try (FileOutputStream out = new FileOutputStream(dir.resolve("reserve-bid-with-errors.xlsx").toFile())) {
                wb.write(out);
            }
        }
    }

    private static void header(Sheet s, String... names) {
        Row h = s.createRow(0);
        for (int i = 0; i < names.length; i++) h.createCell(i).setCellValue(names[i]);
    }

    private static void addRow(Sheet s, int rowIdx, int productId, String grade, String model, double price) {
        Row r = s.createRow(rowIdx);
        r.createCell(0).setCellValue(productId);
        r.createCell(1).setCellValue(grade);
        r.createCell(2).setCellValue(model);
        r.createCell(3).setCellValue(price);
    }
}
```

Run: `cd backend && mvn test-compile && java -cp target/test-classes:$(mvn -q dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=/dev/stdout) com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidFixtureGenerator src/test/resources/fixtures`

Expected: Two XLSX files written.

- [ ] **Step 2: Write the failing upload test**

Add to `ReserveBidServiceTest`:

```java
import com.ecoatm.salesplatform.dto.ReserveBidUploadResult;
import org.springframework.mock.web.MockMultipartFile;

@Test
void upload_createsNewRowsAndAuditsPriceChanges() throws Exception {
    when(repo.findByProductIdAndGrade("10001", "A_YYY")).thenReturn(Optional.of(existing("10001", "A_YYY", "40")));
    when(repo.findByProductIdAndGrade("10002", "A_YYY")).thenReturn(Optional.empty());
    when(repo.findByProductIdAndGrade("10003", "B_NYY")).thenReturn(Optional.empty());
    when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
        ReserveBid r = inv.getArgument(0);
        if (r.getId() == null) r.setId(100L);
        return r;
    });

    ReserveBidService real = new ReserveBidService(repo, auditRepo, syncRepo, publisher,
            null, new ReserveBidExcelParser());

    byte[] bytes = java.nio.file.Files.readAllBytes(
            java.nio.file.Path.of("src/test/resources/fixtures/reserve-bid-sample.xlsx"));
    ReserveBidUploadResult result = real.upload(99L,
            new MockMultipartFile("file", "sample.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes));

    assertThat(result.created()).isEqualTo(2);         // 10002, 10003
    assertThat(result.updated()).isEqualTo(1);         // 10001 (40 → 50.25)
    assertThat(result.auditsGenerated()).isEqualTo(1);
    assertThat(result.errors()).isEmpty();
}

@Test
void upload_reportsErrorsWithoutAborting() throws Exception {
    when(repo.findByProductIdAndGrade(anyString(), anyString())).thenReturn(Optional.empty());
    when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
        ReserveBid r = inv.getArgument(0);
        if (r.getId() == null) r.setId(200L);
        return r;
    });

    ReserveBidService real = new ReserveBidService(repo, auditRepo, syncRepo, publisher,
            null, new ReserveBidExcelParser());

    byte[] bytes = java.nio.file.Files.readAllBytes(
            java.nio.file.Path.of("src/test/resources/fixtures/reserve-bid-with-errors.xlsx"));
    ReserveBidUploadResult result = real.upload(99L,
            new MockMultipartFile("file", "errors.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes));

    assertThat(result.created()).isEqualTo(1);       // only 20001 row 1 succeeds
    assertThat(result.errors()).hasSize(3);          // dup-in-sheet, missing grade, negative price
    assertThat(result.errors()).anyMatch(e -> "DUPLICATE_IN_SHEET".equals(e.reason()));
    assertThat(result.errors()).anyMatch(e -> "MISSING_GRADE".equals(e.reason()));
    assertThat(result.errors()).anyMatch(e -> "NEGATIVE_PRICE".equals(e.reason()));
}

private static ReserveBid existing(String pid, String grade, String bid) {
    ReserveBid r = new ReserveBid();
    r.setId(Long.parseLong(pid));
    r.setProductId(pid);
    r.setGrade(grade);
    r.setBid(new BigDecimal(bid));
    return r;
}
```

- [ ] **Step 3: Write `ReserveBidExcelParser`**

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReserveBidExcelParser {

    public record ParsedRow(int rowNumber, String productId, String grade,
                            String modelName, BigDecimal price) {}

    public List<ParsedRow> parse(InputStream in) {
        List<ParsedRow> rows = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            int last = sheet.getLastRowNum();
            for (int i = 1; i <= last; i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                rows.add(new ParsedRow(
                        i + 1,  // 1-indexed human-readable row number
                        cellString(r.getCell(0)),
                        cellString(r.getCell(1)),
                        cellString(r.getCell(2)),
                        cellDecimal(r.getCell(3))));
            }
        } catch (IOException ex) {
            throw new ReserveBidValidationException("UPLOAD_PARSE_ERROR",
                    "Failed to read Excel: " + ex.getMessage());
        }
        return rows;
    }

    private static String cellString(Cell c) {
        if (c == null) return null;
        return switch (c.getCellType()) {
            case STRING  -> c.getStringCellValue();
            case NUMERIC -> new BigDecimal(c.getNumericCellValue()).toPlainString();
            case BLANK   -> null;
            default      -> c.toString();
        };
    }

    private static BigDecimal cellDecimal(Cell c) {
        if (c == null) return null;
        return switch (c.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(c.getNumericCellValue());
            case STRING  -> { try { yield new BigDecimal(c.getStringCellValue()); } catch (NumberFormatException e) { yield null; } }
            default      -> null;
        };
    }
}
```

- [ ] **Step 4: Add `upload` method to `ReserveBidService`**

```java
@Transactional
public ReserveBidUploadResult upload(long userId, org.springframework.web.multipart.MultipartFile file) {
    if (excelParser == null) {
        throw new ReserveBidException("PARSER_UNAVAILABLE", "excelParser not wired");
    }
    List<ReserveBidExcelParser.ParsedRow> parsed;
    try {
        parsed = excelParser.parse(file.getInputStream());
    } catch (java.io.IOException ex) {
        throw new ReserveBidValidationException("UPLOAD_PARSE_ERROR",
                "Cannot read upload: " + ex.getMessage());
    }

    List<ReserveBidUploadResult.UploadError> errors = new java.util.ArrayList<>();
    int created = 0, updated = 0, unchanged = 0, auditsGenerated = 0;
    List<Long> changedIds = new java.util.ArrayList<>();
    java.util.Set<String> seenKeys = new java.util.HashSet<>();

    for (var row : parsed) {
        String key = row.productId() + "|" + row.grade();
        if (row.grade() == null || row.grade().isBlank()) {
            errors.add(new ReserveBidUploadResult.UploadError(row.rowNumber(),
                    row.productId(), row.grade(), "MISSING_GRADE"));
            continue;
        }
        if (row.productId() == null || row.productId().isBlank()) {
            errors.add(new ReserveBidUploadResult.UploadError(row.rowNumber(),
                    row.productId(), row.grade(), "MISSING_PRODUCT_ID"));
            continue;
        }
        if (row.price() == null || row.price().signum() < 0) {
            errors.add(new ReserveBidUploadResult.UploadError(row.rowNumber(),
                    row.productId(), row.grade(), "NEGATIVE_PRICE"));
            continue;
        }
        if (!seenKeys.add(key)) {
            errors.add(new ReserveBidUploadResult.UploadError(row.rowNumber(),
                    row.productId(), row.grade(), "DUPLICATE_IN_SHEET"));
            continue;
        }

        Optional<ReserveBid> existing = repo.findByProductIdAndGrade(row.productId(), row.grade());
        ReserveBid rb;
        if (existing.isPresent()) {
            rb = existing.get();
            BigDecimal oldPrice = rb.getBid();
            if (oldPrice != null && oldPrice.compareTo(row.price()) == 0) {
                unchanged++;
                continue;
            }
            if (oldPrice != null) {
                ReserveBidAudit a = new ReserveBidAudit();
                a.setReserveBidId(rb.getId());
                a.setOldPrice(oldPrice);
                a.setNewPrice(row.price());
                a.setOwnerId(userId);
                a.setChangedById(userId);
                auditRepo.save(a);
                auditsGenerated++;
            }
            rb.setBid(row.price());
            rb.setModel(row.modelName() != null ? row.modelName() : rb.getModel());
            rb.setLastUpdateDatetime(Instant.now());
            rb.setChangedById(userId);
            rb.setChangedDate(Instant.now());
            repo.save(rb);
            updated++;
        } else {
            rb = new ReserveBid();
            rb.setProductId(row.productId());
            rb.setGrade(row.grade());
            rb.setModel(row.modelName());
            rb.setBid(row.price());
            rb.setLastUpdateDatetime(Instant.now());
            rb.setOwnerId(userId);
            rb.setChangedById(userId);
            repo.save(rb);
            created++;
        }
        changedIds.add(rb.getId());
    }

    if (!changedIds.isEmpty()) {
        publisher.publishEvent(new ReserveBidChangedEvent(
                changedIds, ReserveBidChangedEvent.Action.UPSERT));
    }
    return new ReserveBidUploadResult(created, updated, unchanged, auditsGenerated, errors);
}
```

Add imports at top of file:
```java
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: PASS — all upload tests green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidExcelParser.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidFixtureGenerator.java \
        backend/src/test/resources/fixtures/reserve-bid-sample.xlsx \
        backend/src/test/resources/fixtures/reserve-bid-with-errors.xlsx
git commit -m "feat(auctions): EB Excel upload with dedup + auto-audit"
```

---

## Task 8: Excel download (stream-based)

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidExcelWriter.java`
- Modify: `ReserveBidService` (add `download` method)

- [ ] **Step 1: Write the failing download test**

Add to `ReserveBidServiceTest`:

```java
@Test
void download_streamsCurrentRowsAsXlsx() throws Exception {
    when(repo.findAll()).thenReturn(java.util.List.of(
            existing("11001", "A_YYY", "10"),
            existing("11002", "B_NYY", "20")));

    ReserveBidExcelWriter writer = new ReserveBidExcelWriter();
    ReserveBidService svc = new ReserveBidService(repo, auditRepo, syncRepo, publisher, null, null);

    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    writer.writeAll(repo.findAll(), out);

    // reopen + assert
    try (org.apache.poi.ss.usermodel.Workbook wb =
                 new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(out.toByteArray()))) {
        org.apache.poi.ss.usermodel.Sheet s = wb.getSheetAt(0);
        assertThat(s.getPhysicalNumberOfRows()).isEqualTo(3);  // header + 2 rows
        assertThat(s.getRow(1).getCell(0).getStringCellValue()).isEqualTo("11001");
    }
}
```

- [ ] **Step 2: Write `ReserveBidExcelWriter`**

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class ReserveBidExcelWriter {

    private static final String[] HEADERS = {
            "ProductID", "Grade", "Brand", "Model", "Bid",
            "LastUpdateDatetime", "LastAwardedMinPrice", "LastAwardedWeek"
    };

    public void writeAll(List<ReserveBid> rows, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            Row header = s.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) header.createCell(i).setCellValue(HEADERS[i]);
            for (int i = 0; i < rows.size(); i++) {
                ReserveBid rb = rows.get(i);
                Row r = s.createRow(i + 1);
                r.createCell(0).setCellValue(rb.getProductId());
                r.createCell(1).setCellValue(rb.getGrade());
                if (rb.getBrand() != null) r.createCell(2).setCellValue(rb.getBrand());
                if (rb.getModel() != null) r.createCell(3).setCellValue(rb.getModel());
                r.createCell(4).setCellValue(rb.getBid() != null ? rb.getBid().doubleValue() : 0);
                if (rb.getLastUpdateDatetime() != null)
                    r.createCell(5).setCellValue(rb.getLastUpdateDatetime().toString());
                if (rb.getLastAwardedMinPrice() != null)
                    r.createCell(6).setCellValue(rb.getLastAwardedMinPrice().doubleValue());
                if (rb.getLastAwardedWeek() != null) r.createCell(7).setCellValue(rb.getLastAwardedWeek());
            }
            wb.write(out);
        }
    }
}
```

- [ ] **Step 3: Add `download` service method (streaming)**

Modify `ReserveBidService` — add constructor param for `ReserveBidExcelWriter` and a method:

```java
// Replace existing constructor
public ReserveBidService(ReserveBidRepository repo,
                         ReserveBidAuditRepository auditRepo,
                         ReserveBidSyncRepository syncRepo,
                         ApplicationEventPublisher publisher,
                         ReserveBidSnowflakeReader snowflakeReader,
                         ReserveBidExcelParser excelParser,
                         ReserveBidExcelWriter excelWriter) {
    this.repo = repo;
    this.auditRepo = auditRepo;
    this.syncRepo = syncRepo;
    this.publisher = publisher;
    this.snowflakeReader = snowflakeReader;
    this.excelParser = excelParser;
    this.excelWriter = excelWriter;
}

private final ReserveBidExcelWriter excelWriter;

@Transactional(readOnly = true)
public void downloadAll(java.io.OutputStream out) {
    if (excelWriter == null) throw new ReserveBidException("WRITER_UNAVAILABLE", "excelWriter not wired");
    try {
        excelWriter.writeAll(repo.findAll(), out);
    } catch (java.io.IOException ex) {
        throw new ReserveBidException("DOWNLOAD_FAILED", "Excel write: " + ex.getMessage());
    }
}
```

Update existing unit tests' constructor call to pass `null` for `excelWriter`.

- [ ] **Step 4: Run tests**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidExcelWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java
git commit -m "feat(auctions): EB Excel download streamed via Apache POI"
```

---

## Task 9: Audit query + sync status

**Files:**
- Modify: `ReserveBidService` — add `findAudit`, `syncStatus`
- Test: extend `ReserveBidServiceTest`

- [ ] **Step 1: Write the failing tests**

Add to `ReserveBidServiceTest`:

```java
@Test
void findAudit_returnsPagedTrail() {
    ReserveBid rb = existing("55001", "A_YYY", "10");
    when(repo.findById(55001L)).thenReturn(Optional.of(rb));

    ReserveBidAudit a = new ReserveBidAudit();
    a.setId(1L);
    a.setReserveBidId(55001L);
    a.setOldPrice(new BigDecimal("10"));
    a.setNewPrice(new BigDecimal("12"));
    a.setCreatedDate(Instant.now());
    when(auditRepo.findByReserveBidIdOrderByCreatedDateDesc(eq(55001L), any()))
            .thenReturn(new PageImpl<>(java.util.List.of(a), PageRequest.of(0, 20), 1));

    var resp = service.findAudit(55001L, 0, 20);
    assertThat(resp.rows()).hasSize(1);
    assertThat(resp.rows().get(0).newPrice()).isEqualByComparingTo("12");
}

@Test
void syncStatus_reportsNeverSyncedWhenNullWatermark() {
    ReserveBidSync sync = new ReserveBidSync();
    sync.setLastSyncDatetime(null);
    when(syncRepo.findFirstByOrderByIdAsc()).thenReturn(Optional.of(sync));

    ReserveBidSnowflakeReader reader = mock(ReserveBidSnowflakeReader.class);
    when(reader.fetchMaxUploadTime()).thenReturn(Optional.of(Instant.parse("2026-04-01T00:00:00Z")));

    ReserveBidService svc = new ReserveBidService(repo, auditRepo, syncRepo, publisher, reader, null, null);
    var status = svc.syncStatus();
    assertThat(status.state()).isEqualTo(ReserveBidSyncStatus.NEVER_SYNCED);
}
```

Add imports:
```java
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.dto.ReserveBidSyncStatus;
```

- [ ] **Step 2: Add methods to `ReserveBidService`**

```java
@Transactional(readOnly = true)
public com.ecoatm.salesplatform.dto.ReserveBidAuditResponse findAudit(long reserveBidId, int page, int size) {
    repo.findById(reserveBidId).orElseThrow(() ->
            new ReserveBidException("RESERVE_BID_NOT_FOUND", "reserve_bid not found: " + reserveBidId));

    org.springframework.data.domain.Page<ReserveBidAudit> p =
            auditRepo.findByReserveBidIdOrderByCreatedDateDesc(reserveBidId,
                    org.springframework.data.domain.PageRequest.of(page, size));

    ReserveBid rb = repo.findById(reserveBidId).get();
    var rows = p.getContent().stream().map(a -> new com.ecoatm.salesplatform.dto.ReserveBidAuditRow(
            a.getId(), a.getReserveBidId(), rb.getProductId(), rb.getGrade(),
            a.getOldPrice(), a.getNewPrice(), a.getCreatedDate(), null)).toList();
    return new com.ecoatm.salesplatform.dto.ReserveBidAuditResponse(rows, p.getTotalElements(), page, size);
}

@Transactional(readOnly = true)
public com.ecoatm.salesplatform.dto.ReserveBidSyncStatus syncStatus() {
    var sync = syncRepo.findFirstByOrderByIdAsc().orElseThrow(() ->
            new ReserveBidException("SYNC_NOT_INITIALIZED", "reserve_bid_sync singleton missing"));
    Instant local = sync.getLastSyncDatetime();
    Instant source = snowflakeReader != null ? snowflakeReader.fetchMaxUploadTime().orElse(null) : null;

    String state;
    java.time.Duration drift;
    if (local == null) {
        state = com.ecoatm.salesplatform.dto.ReserveBidSyncStatus.NEVER_SYNCED;
        drift = source != null ? java.time.Duration.between(Instant.EPOCH, source) : java.time.Duration.ZERO;
    } else if (source == null || !source.isAfter(local)) {
        state = com.ecoatm.salesplatform.dto.ReserveBidSyncStatus.IN_SYNC;
        drift = java.time.Duration.ZERO;
    } else {
        state = com.ecoatm.salesplatform.dto.ReserveBidSyncStatus.BEHIND_SOURCE;
        drift = java.time.Duration.between(local, source);
    }
    return new com.ecoatm.salesplatform.dto.ReserveBidSyncStatus(local, source, drift, state);
}
```

- [ ] **Step 3: Run tests**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java
git commit -m "feat(auctions): EB audit pagination + sync status endpoint"
```

---

## Task 10: Snowflake writer interface + logging impl

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePayload.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingReserveBidSnowflakeWriter.java`

- [ ] **Step 1: Write the payload record**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReserveBidSnowflakePayload(List<Row> rows, String actingUser, String action) {

    public record Row(String productId, String grade, String brand, String model,
                      BigDecimal bid, Instant lastUpdateDatetime,
                      BigDecimal lastAwardedMinPrice, String lastAwardedWeek) {}
}
```

- [ ] **Step 2: Write the writer interface**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface ReserveBidSnowflakeWriter {

    /** Returns the number of rows accepted by the stored proc (best effort). */
    int upsert(ReserveBidSnowflakePayload payload);

    int delete(ReserveBidSnowflakePayload payload);
}
```

- [ ] **Step 3: Write the logging impl**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "eb.sync.writer", havingValue = "logging", matchIfMissing = true)
public class LoggingReserveBidSnowflakeWriter implements ReserveBidSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingReserveBidSnowflakeWriter.class);

    @Override
    public int upsert(ReserveBidSnowflakePayload payload) {
        log.info("[logging-writer] UPSERT rowCount={} user={}", payload.rows().size(), payload.actingUser());
        return payload.rows().size();
    }

    @Override
    public int delete(ReserveBidSnowflakePayload payload) {
        log.info("[logging-writer] DELETE rowCount={} user={}", payload.rows().size(), payload.actingUser());
        return payload.rows().size();
    }
}
```

- [ ] **Step 4: Verify compile**

Run: `cd backend && mvn compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePayload.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingReserveBidSnowflakeWriter.java
git commit -m "feat(auctions): ReserveBid Snowflake writer interface + logging impl"
```

---

## Task 11: Snowflake push listener

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePushListener.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePushListenerTest.java`

- [ ] **Step 1: Write failing listener test**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveBidSnowflakePushListenerTest {

    @Mock ReserveBidRepository repo;
    @Mock ReserveBidSnowflakeWriter writer;
    @Mock Environment env;

    ReserveBidSnowflakePushListener listener;

    @BeforeEach
    void setUp() {
        when(env.getProperty("eb.sync.enabled", Boolean.class, true)).thenReturn(true);
        listener = new ReserveBidSnowflakePushListener(repo, writer, env);
    }

    @Test
    void upsertEvent_invokesWriter() {
        ReserveBid rb = new ReserveBid();
        rb.setId(1L); rb.setProductId("1"); rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("10"));
        when(repo.findAllById(List.of(1L))).thenReturn(List.of(rb));

        listener.onChanged(new ReserveBidChangedEvent(List.of(1L), ReserveBidChangedEvent.Action.UPSERT));

        ArgumentCaptor<ReserveBidSnowflakePayload> cap = ArgumentCaptor.forClass(ReserveBidSnowflakePayload.class);
        verify(writer).upsert(cap.capture());
        assertThat(cap.getValue().rows()).hasSize(1);
        assertThat(cap.getValue().action()).isEqualTo("UPSERT");
    }

    @Test
    void featureFlagOff_noWriterCall() {
        when(env.getProperty("eb.sync.enabled", Boolean.class, true)).thenReturn(false);
        ReserveBidSnowflakePushListener off = new ReserveBidSnowflakePushListener(repo, writer, env);

        off.onChanged(new ReserveBidChangedEvent(List.of(1L), ReserveBidChangedEvent.Action.UPSERT));

        verifyNoInteractions(writer);
    }

    @Test
    void deleteEvent_sendsEmptyRowsWithDeleteAction() {
        listener.onChanged(new ReserveBidChangedEvent(List.of(5L), ReserveBidChangedEvent.Action.DELETE));
        ArgumentCaptor<ReserveBidSnowflakePayload> cap = ArgumentCaptor.forClass(ReserveBidSnowflakePayload.class);
        verify(writer).delete(cap.capture());
        assertThat(cap.getValue().action()).isEqualTo("DELETE");
    }
}
```

- [ ] **Step 2: Write the listener**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.List;

@Component
public class ReserveBidSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSnowflakePushListener.class);

    private final ReserveBidRepository repo;
    private final ReserveBidSnowflakeWriter writer;
    private final Environment env;

    public ReserveBidSnowflakePushListener(ReserveBidRepository repo,
                                           ReserveBidSnowflakeWriter writer,
                                           Environment env) {
        this.repo = repo;
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(ReserveBidChangedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("eb.sync.enabled", Boolean.class, true))) {
            log.info("[eb-push] disabled; would {} ids={}", event.action(), event.changedIds());
            return;
        }
        try {
            if (event.action() == ReserveBidChangedEvent.Action.UPSERT) {
                List<ReserveBid> rows = repo.findAllById(event.changedIds());
                List<ReserveBidSnowflakePayload.Row> payloadRows = rows.stream()
                        .map(rb -> new ReserveBidSnowflakePayload.Row(
                                rb.getProductId(), rb.getGrade(), rb.getBrand(), rb.getModel(),
                                rb.getBid(), rb.getLastUpdateDatetime(),
                                rb.getLastAwardedMinPrice(), rb.getLastAwardedWeek()))
                        .toList();
                writer.upsert(new ReserveBidSnowflakePayload(payloadRows, actingUser(), "UPSERT"));
            } else {
                writer.delete(new ReserveBidSnowflakePayload(Collections.emptyList(), actingUser(), "DELETE"));
            }
        } catch (Exception ex) {
            log.error("[eb-push] failed action={} ids={} - swallowed so Postgres tx stands",
                    event.action(), event.changedIds(), ex);
        }
    }

    private static String actingUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
```

- [ ] **Step 3: Run tests to verify**

Run: `cd backend && mvn test -Dtest=ReserveBidSnowflakePushListenerTest`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePushListener.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakePushListenerTest.java
git commit -m "feat(auctions): EB Snowflake push listener (AFTER_COMMIT, async)"
```

---

## Task 12: Snowflake reader + JDBC writer impl

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakeReader.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcReserveBidSnowflakeWriter.java`

- [ ] **Step 1: Write the reader**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(value = "eb.sync.reader", havingValue = "jdbc", matchIfMissing = false)
public class ReserveBidSnowflakeReader {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSnowflakeReader.class);

    private final JdbcTemplate snowflakeJdbc;

    public ReserveBidSnowflakeReader(DataSource snowflakeDataSource) {
        this.snowflakeJdbc = new JdbcTemplate(snowflakeDataSource);
    }

    public Optional<Instant> fetchMaxUploadTime() {
        try {
            Instant t = snowflakeJdbc.queryForObject(
                    "SELECT MAX(LAST_UPDATE_DATETIME) FROM AUCTIONS.RESERVE_BID", Instant.class);
            return Optional.ofNullable(t);
        } catch (Exception ex) {
            log.warn("[eb-reader] fetchMaxUploadTime failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public List<ReserveBid> fetchAll() {
        try {
            return snowflakeJdbc.query(
                    "SELECT PRODUCT_ID, GRADE, BRAND, MODEL, BID, LAST_UPDATE_DATETIME, "
                  + "       LAST_AWARDED_MIN_PRICE, LAST_AWARDED_WEEK, BID_VALID_WEEK_DATE "
                  + "FROM AUCTIONS.RESERVE_BID",
                    (rs, rowNum) -> {
                        ReserveBid rb = new ReserveBid();
                        rb.setProductId(rs.getString("PRODUCT_ID"));
                        rb.setGrade(rs.getString("GRADE"));
                        rb.setBrand(rs.getString("BRAND"));
                        rb.setModel(rs.getString("MODEL"));
                        rb.setBid(rs.getBigDecimal("BID"));
                        java.sql.Timestamp ts = rs.getTimestamp("LAST_UPDATE_DATETIME");
                        if (ts != null) rb.setLastUpdateDatetime(ts.toInstant());
                        rb.setLastAwardedMinPrice(rs.getBigDecimal("LAST_AWARDED_MIN_PRICE"));
                        rb.setLastAwardedWeek(rs.getString("LAST_AWARDED_WEEK"));
                        rb.setBidValidWeekDate(rs.getString("BID_VALID_WEEK_DATE"));
                        return rb;
                    });
        } catch (Exception ex) {
            log.warn("[eb-reader] fetchAll failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
```

- [ ] **Step 2: Write JDBC writer impl**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConditionalOnProperty(value = "eb.sync.writer", havingValue = "jdbc", matchIfMissing = false)
public class JdbcReserveBidSnowflakeWriter implements ReserveBidSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(JdbcReserveBidSnowflakeWriter.class);

    private final JdbcTemplate snowflakeJdbc;
    private final ObjectMapper jsonMapper;

    public JdbcReserveBidSnowflakeWriter(DataSource snowflakeDataSource) {
        this.snowflakeJdbc = new JdbcTemplate(snowflakeDataSource);
        this.jsonMapper = new ObjectMapper();
    }

    @Override
    public int upsert(ReserveBidSnowflakePayload payload) {
        return callStoredProc("UPSERT", payload);
    }

    @Override
    public int delete(ReserveBidSnowflakePayload payload) {
        return callStoredProc("DELETE", payload);
    }

    private int callStoredProc(String action, ReserveBidSnowflakePayload payload) {
        try {
            String json = jsonMapper.writeValueAsString(payload.rows());
            snowflakeJdbc.update("CALL AUCTIONS.UPSERT_RESERVE_BID(?, ?)",
                    json, payload.actingUser());
            log.info("[eb-jdbc-writer] {} ok rows={} user={}", action, payload.rows().size(), payload.actingUser());
            return payload.rows().size();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Snowflake payload JSON encoding failed", ex);
        }
    }
}
```

- [ ] **Step 3: Verify compile**

Run: `cd backend && mvn compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/ReserveBidSnowflakeReader.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcReserveBidSnowflakeWriter.java
git commit -m "feat(auctions): EB Snowflake reader + JDBC writer (ConditionalOnProperty)"
```

---

## Task 13: Pull cron — service method + scheduled job

**Files:**
- Modify: `ReserveBidService` — add `runScheduledSync`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidSyncScheduledJob.java`
- Test: extend `ReserveBidServiceTest` + new IT

- [ ] **Step 1: Write failing sync tests**

```java
@Test
void runScheduledSync_skipsWhenSourceNotNewer() {
    ReserveBidSync sync = new ReserveBidSync();
    sync.setLastSyncDatetime(Instant.parse("2026-04-10T00:00:00Z"));
    when(syncRepo.findFirstByOrderByIdAsc()).thenReturn(Optional.of(sync));

    ReserveBidSnowflakeReader reader = mock(ReserveBidSnowflakeReader.class);
    when(reader.fetchMaxUploadTime()).thenReturn(Optional.of(Instant.parse("2026-04-10T00:00:00Z")));

    ReserveBidService svc = new ReserveBidService(repo, auditRepo, syncRepo, publisher, reader, null, null);
    int n = svc.runScheduledSync();

    assertThat(n).isZero();
    verify(repo, never()).deleteAllNative();
    verify(repo, never()).saveAll(any());
    verifyNoInteractions(publisher);   // echo prevention
}

@Test
void runScheduledSync_replacesAllWhenSourceNewer() {
    ReserveBidSync sync = new ReserveBidSync();
    sync.setLastSyncDatetime(Instant.parse("2026-04-01T00:00:00Z"));
    when(syncRepo.findFirstByOrderByIdAsc()).thenReturn(Optional.of(sync));

    ReserveBidSnowflakeReader reader = mock(ReserveBidSnowflakeReader.class);
    when(reader.fetchMaxUploadTime()).thenReturn(Optional.of(Instant.parse("2026-04-15T00:00:00Z")));
    when(reader.fetchAll()).thenReturn(List.of(existing("A", "G", "1"), existing("B", "G", "2")));

    ReserveBidService svc = new ReserveBidService(repo, auditRepo, syncRepo, publisher, reader, null, null);
    int n = svc.runScheduledSync();

    assertThat(n).isEqualTo(2);
    verify(repo).deleteAllNative();
    verify(repo).saveAll(any());
    verifyNoInteractions(publisher);   // still no echo
}
```

- [ ] **Step 2: Add `runScheduledSync` to `ReserveBidService`**

```java
@Transactional(timeout = 600)
public int runScheduledSync() {
    if (snowflakeReader == null) {
        throw new ReserveBidException("READER_UNAVAILABLE", "snowflakeReader not wired");
    }
    var sync = syncRepo.findFirstByOrderByIdAsc().orElseThrow(() ->
            new ReserveBidException("SYNC_NOT_INITIALIZED", "reserve_bid_sync singleton missing"));
    Instant local = sync.getLastSyncDatetime();
    Instant source = snowflakeReader.fetchMaxUploadTime().orElse(null);

    if (source == null) return 0;                // source unreachable; try again next tick
    if (local != null && !source.isAfter(local)) return 0;  // strict > per design §7.2

    List<ReserveBid> rows = snowflakeReader.fetchAll();
    repo.deleteAllNative();
    repo.saveAll(rows);
    sync.setLastSyncDatetime(source);
    sync.setChangedDate(Instant.now());
    syncRepo.save(sync);
    // NO ReserveBidChangedEvent — echo prevention (design §7.2)
    return rows.size();
}
```

- [ ] **Step 3: Write the scheduled job wrapper**

```java
package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRecorder;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class ReserveBidSyncScheduledJob {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSyncScheduledJob.class);
    public static final String JOB_NAME = "reserve-bid-sync";

    private final ReserveBidService service;
    private final ScheduledJobRunRecorder recorder;
    private final Environment env;

    public ReserveBidSyncScheduledJob(ReserveBidService service,
                                      ScheduledJobRunRecorder recorder,
                                      Environment env) {
        this.service = service;
        this.recorder = recorder;
        this.env = env;
    }

    @Scheduled(fixedDelayString = "${eb.sync.fixed-delay-ms:1800000}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = "PT20M", lockAtLeastFor = "PT1M")
    public void run() {
        if (!Boolean.TRUE.equals(env.getProperty("eb.sync.enabled", Boolean.class, true))) {
            log.info("[{}] disabled", JOB_NAME);
            return;
        }
        Instant started = Instant.now();
        try {
            int rows = service.runScheduledSync();
            recorder.recordSuccess(JOB_NAME, started, Duration.between(started, Instant.now()),
                    Map.of("rowsFetched", rows));
        } catch (Exception ex) {
            log.error("[{}] failed", JOB_NAME, ex);
            recorder.recordFailure(JOB_NAME, started, Duration.between(started, Instant.now()),
                    ex.getMessage());
        }
    }
}
```

- [ ] **Step 4: Run unit tests**

Run: `cd backend && mvn test -Dtest=ReserveBidServiceTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidService.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidSyncScheduledJob.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/reservebid/ReserveBidServiceTest.java
git commit -m "feat(auctions): EB scheduled pull sync (ShedLock, echo-prevented)"
```

---

## Task 14: REST controller + security wiring

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/ReserveBidController.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/ReserveBidControllerIT.java`

- [ ] **Step 1: Write failing controller IT**

```java
package com.ecoatm.salesplatform.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReserveBidControllerIT {

    @Autowired MockMvc mvc;

    @Test
    @WithMockUser(roles = "Administrator")
    void list_returnsPayload() throws Exception {
        mvc.perform(get("/api/v1/admin/reserve-bids?page=0&size=10"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.rows").exists())
           .andExpect(jsonPath("$.total").exists());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    void nonAdmin_gets403() throws Exception {
        mvc.perform(get("/api/v1/admin/reserve-bids"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void create_andFetchById() throws Exception {
        String body = """
                {"productId":"88001","grade":"A_YYY","brand":"Test","model":"T1",
                 "bid":5.00,"lastAwardedMinPrice":null,"lastAwardedWeek":null,"bidValidWeekDate":null}
                """;
        mvc.perform(post("/api/v1/admin/reserve-bids")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.productId").value("88001"));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void upload_samplePasses() throws Exception {
        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/fixtures/reserve-bid-sample.xlsx"));
        MockMultipartFile file = new MockMultipartFile("file", "sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        mvc.perform(multipart("/api/v1/admin/reserve-bids/upload").file(file))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.errors").isArray());
    }
}
```

- [ ] **Step 2: Write the controller**

```java
package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidException;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidService;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidValidationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/reserve-bids")
public class ReserveBidController {

    private final ReserveBidService service;

    public ReserveBidController(ReserveBidService service) { this.service = service; }

    @GetMapping
    public ReserveBidListResponse list(@RequestParam(required = false) String productId,
                                       @RequestParam(required = false) String grade,
                                       @RequestParam(required = false) BigDecimal minBid,
                                       @RequestParam(required = false) BigDecimal maxBid,
                                       @RequestParam(required = false) Instant updatedSince,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return service.search(productId, grade, minBid, maxBid, updatedSince, page, size);
    }

    @GetMapping("/{id}")
    public ReserveBidRow get(@PathVariable long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<ReserveBidRow> create(@RequestBody @Valid ReserveBidRequest req) {
        ReserveBidRow created = service.create(userId(), req);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ReserveBidRow update(@PathVariable long id, @RequestBody @Valid ReserveBidRequest req) {
        return service.update(userId(), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ReserveBidUploadResult upload(@RequestParam("file") MultipartFile file) {
        return service.upload(userId(), file);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.downloadAll(out);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reserve-bids-" + Instant.now().toString() + ".xlsx\"")
                .body(out.toByteArray());
    }

    @GetMapping("/{id}/audit")
    public ReserveBidAuditResponse audit(@PathVariable long id,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return service.findAudit(id, page, size);
    }

    @GetMapping("/sync")
    public ReserveBidSyncStatus syncStatus() { return service.syncStatus(); }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> triggerSync() {
        int rows = service.runScheduledSync();
        return ResponseEntity.accepted().body(Map.of("rowsFetched", rows));
    }

    @ExceptionHandler(ReserveBidValidationException.class)
    public ResponseEntity<Map<String, String>> onValidation(ReserveBidValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of("code", ex.code(), "message", ex.getMessage()));
    }

    @ExceptionHandler(ReserveBidException.class)
    public ResponseEntity<Map<String, String>> onDomain(ReserveBidException ex) {
        int status = switch (ex.code()) {
            case "RESERVE_BID_NOT_FOUND" -> 404;
            case "DUPLICATE_PRODUCT_GRADE" -> 409;
            case "SYNC_NOT_INITIALIZED" -> 503;
            default -> 400;
        };
        return ResponseEntity.status(status).body(Map.of("code", ex.code(), "message", ex.getMessage()));
    }

    private static long userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof com.ecoatm.salesplatform.model.User u
                ? u.getId() : 0L;
    }
}
```

- [ ] **Step 3: Update SecurityConfig**

Find the security-matchers block in `SecurityConfig.java` and add:

```java
.requestMatchers("/api/v1/admin/reserve-bids/**").hasRole("Administrator")
```

(Place BEFORE any generic `/api/v1/admin/**` matcher to avoid precedence conflicts.)

- [ ] **Step 4: Run IT**

Run: `cd backend && mvn test -Dtest=ReserveBidControllerIT`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/controller/admin/ReserveBidController.java \
        backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/admin/ReserveBidControllerIT.java
git commit -m "feat(auctions): EB REST controller + admin-role security wiring"
```

---

## Task 15: Application config

**Files:**
- Modify: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Add the EB sync config block**

Append under the existing top-level config:

```yaml
eb:
  sync:
    enabled: true
    fixed-delay-ms: 1800000         # 30 min default pull cadence
    snowflake-timeout-seconds: 60
    writer: logging                  # switch to 'jdbc' for prod
    reader: logging                  # switch to 'jdbc' for prod
```

- [ ] **Step 2: Verify app boots**

Run: `cd backend && mvn spring-boot:run` (separate terminal)
Curl: `curl http://localhost:8080/actuator/health`
Expected: `{"status":"UP"}`

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/application.yml
git commit -m "chore(auctions): EB sync config block with logging defaults"
```

---

## Task 16: Frontend types + API client

**Files:**
- Create: `frontend/src/lib/types/reserveBid.ts`
- Create: `frontend/src/lib/api/reserveBidClient.ts`
- Test: `frontend/src/lib/api/reserveBidClient.test.ts`

- [ ] **Step 1: Write types**

```typescript
// frontend/src/lib/types/reserveBid.ts
export interface ReserveBidRow {
  id: number;
  productId: string;
  grade: string;
  brand: string | null;
  model: string | null;
  bid: string;                  // BigDecimal serialized as string
  lastUpdateDatetime: string;
  lastAwardedMinPrice: string | null;
  lastAwardedWeek: string | null;
  bidValidWeekDate: string | null;
  changedDate: string;
}

export interface ReserveBidRequest {
  productId: string;
  grade: string;
  brand?: string | null;
  model?: string | null;
  bid: string;
  lastAwardedMinPrice?: string | null;
  lastAwardedWeek?: string | null;
  bidValidWeekDate?: string | null;
}

export interface ReserveBidListResponse {
  rows: ReserveBidRow[];
  total: number;
  page: number;
  size: number;
}

export interface UploadError {
  rowNumber: number;
  productId: string | null;
  grade: string | null;
  reason: string;
}

export interface ReserveBidUploadResult {
  created: number;
  updated: number;
  unchanged: number;
  auditsGenerated: number;
  errors: UploadError[];
}

export interface ReserveBidAuditRow {
  id: number;
  reserveBidId: number;
  productId: string;
  grade: string;
  oldPrice: string;
  newPrice: string;
  createdDate: string;
  changedByUsername: string | null;
}

export interface ReserveBidAuditResponse {
  rows: ReserveBidAuditRow[];
  total: number;
  page: number;
  size: number;
}

export interface ReserveBidSyncStatus {
  lastSyncDatetime: string | null;
  sourceMaxDatetime: string | null;
  drift: string | null;
  state: "IN_SYNC" | "BEHIND_SOURCE" | "NEVER_SYNCED";
}
```

- [ ] **Step 2: Write client**

```typescript
// frontend/src/lib/api/reserveBidClient.ts
import type {
  ReserveBidListResponse,
  ReserveBidRow,
  ReserveBidRequest,
  ReserveBidUploadResult,
  ReserveBidAuditResponse,
  ReserveBidSyncStatus,
} from "@/lib/types/reserveBid";

const BASE = "/api/v1/admin/reserve-bids";

async function req<T>(method: string, path: string, body?: unknown, init: RequestInit = {}): Promise<T> {
  const res = await fetch(path, {
    method,
    credentials: "include",
    headers: body instanceof FormData ? {} : { "Content-Type": "application/json" },
    body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
    ...init,
  });
  if (!res.ok) {
    const errJson = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(errJson.message || `HTTP ${res.status}`);
  }
  return (res.status === 204 ? undefined : await res.json()) as T;
}

export const reserveBidClient = {
  list: (params: { productId?: string; grade?: string; page?: number; size?: number } = {}) => {
    const q = new URLSearchParams(
      Object.entries(params).filter(([, v]) => v != null && v !== "").map(([k, v]) => [k, String(v)])
    );
    return req<ReserveBidListResponse>("GET", `${BASE}?${q}`);
  },
  get: (id: number) => req<ReserveBidRow>("GET", `${BASE}/${id}`),
  create: (body: ReserveBidRequest) => req<ReserveBidRow>("POST", BASE, body),
  update: (id: number, body: ReserveBidRequest) => req<ReserveBidRow>("PUT", `${BASE}/${id}`, body),
  remove: (id: number) => req<void>("DELETE", `${BASE}/${id}`),
  upload: (file: File) => {
    const fd = new FormData();
    fd.append("file", file);
    return req<ReserveBidUploadResult>("POST", `${BASE}/upload`, fd);
  },
  download: () =>
    fetch(`${BASE}/download`, { credentials: "include" }).then((r) => {
      if (!r.ok) throw new Error(`Download failed: ${r.status}`);
      return r.blob();
    }),
  audit: (id: number, page = 0, size = 20) =>
    req<ReserveBidAuditResponse>("GET", `${BASE}/${id}/audit?page=${page}&size=${size}`),
  syncStatus: () => req<ReserveBidSyncStatus>("GET", `${BASE}/sync`),
  triggerSync: () => req<{ rowsFetched: number }>("POST", `${BASE}/sync`),
};
```

- [ ] **Step 3: Write a small client test**

```typescript
// frontend/src/lib/api/reserveBidClient.test.ts
import { describe, expect, it, vi, beforeEach } from "vitest";
import { reserveBidClient } from "./reserveBidClient";

describe("reserveBidClient", () => {
  beforeEach(() => vi.restoreAllMocks());

  it("list builds query string from params", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true, status: 200,
      json: async () => ({ rows: [], total: 0, page: 0, size: 20 }),
    });
    global.fetch = fetchMock as never;

    await reserveBidClient.list({ productId: "77001", page: 2 });
    expect(fetchMock).toHaveBeenCalledOnce();
    const url = fetchMock.mock.calls[0][0];
    expect(url).toContain("productId=77001");
    expect(url).toContain("page=2");
  });

  it("upload sends multipart form-data", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true, status: 200,
      json: async () => ({ created: 0, updated: 0, unchanged: 0, auditsGenerated: 0, errors: [] }),
    });
    global.fetch = fetchMock as never;

    const file = new File(["x"], "t.xlsx");
    await reserveBidClient.upload(file);
    const init = fetchMock.mock.calls[0][1];
    expect(init.body).toBeInstanceOf(FormData);
  });
});
```

- [ ] **Step 4: Run client tests**

Run: `cd frontend && npm test -- reserveBidClient`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/lib/types/reserveBid.ts \
        frontend/src/lib/api/reserveBidClient.ts \
        frontend/src/lib/api/reserveBidClient.test.ts
git commit -m "feat(frontend): EB API client + types"
```

---

## Task 17: Frontend admin overview page

**Files:**
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/page.tsx`

- [ ] **Step 1: Write the overview page**

```tsx
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { reserveBidClient } from "@/lib/api/reserveBidClient";
import type { ReserveBidRow } from "@/lib/types/reserveBid";

export default function ReserveBidsPage() {
  const [rows, setRows] = useState<ReserveBidRow[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [productIdFilter, setProductIdFilter] = useState("");
  const [gradeFilter, setGradeFilter] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await reserveBidClient.list({
        productId: productIdFilter || undefined,
        grade: gradeFilter || undefined,
        page,
        size: 20,
      });
      setRows(res.rows);
      setTotal(res.total);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Load failed");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [page, productIdFilter, gradeFilter]);

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this reserve bid? This will drop its audit trail.")) return;
    try {
      await reserveBidClient.remove(id);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Delete failed");
    }
  };

  const handleDownload = async () => {
    const blob = await reserveBidClient.download();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `reserve-bids-${new Date().toISOString().slice(0, 10)}.xlsx`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div style={{ padding: "1.5rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1>Reserve Bids (EB)</h1>
        <div style={{ display: "flex", gap: "0.5rem" }}>
          <Link href="/admin/auctions-data-center/reserve-bids/upload">
            <button>Upload Excel</button>
          </Link>
          <button onClick={handleDownload}>Download Excel</button>
          <Link href="/admin/auctions-data-center/reserve-bids/new">
            <button>New</button>
          </Link>
        </div>
      </div>

      <div style={{ display: "flex", gap: "0.5rem", margin: "1rem 0" }}>
        <input placeholder="Filter productId..." value={productIdFilter}
               onChange={(e) => { setProductIdFilter(e.target.value); setPage(0); }} />
        <input placeholder="Filter grade contains..." value={gradeFilter}
               onChange={(e) => { setGradeFilter(e.target.value); setPage(0); }} />
      </div>

      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>Product ID</th><th>Grade</th><th>Brand</th><th>Model</th>
            <th>Bid</th><th>Last Updated</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td>{r.productId}</td><td>{r.grade}</td>
              <td>{r.brand ?? "—"}</td><td>{r.model ?? "—"}</td>
              <td>{r.bid}</td><td>{new Date(r.lastUpdateDatetime).toLocaleString()}</td>
              <td>
                <Link href={`/admin/auctions-data-center/reserve-bids/${r.id}`}>Edit</Link>{" "}
                <Link href={`/admin/auctions-data-center/reserve-bids/${r.id}/audit`}>Audit</Link>{" "}
                <button onClick={() => handleDelete(r.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem", alignItems: "center" }}>
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>Prev</button>
        <span>Page {page + 1} of {Math.max(1, Math.ceil(total / 20))}</span>
        <button disabled={(page + 1) * 20 >= total} onClick={() => setPage(page + 1)}>Next</button>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Verify app compiles**

Run: `cd frontend && npm run build`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add frontend/src/app/\(dashboard\)/admin/auctions-data-center/reserve-bids/page.tsx
git commit -m "feat(frontend): EB admin overview with filters, CRUD actions"
```

---

## Task 18: Frontend edit + new + upload + audit pages

**Files:**
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/[id]/page.tsx`
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/new/page.tsx`
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/upload/page.tsx`
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/[id]/audit/page.tsx`

- [ ] **Step 1: Write edit page**

```tsx
// .../[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/api/reserveBidClient";
import type { ReserveBidRow } from "@/lib/types/reserveBid";

export default function EditReserveBidPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const id = Number(params.id);
  const [row, setRow] = useState<ReserveBidRow | null>(null);
  const [bid, setBid] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    reserveBidClient.get(id).then((r) => {
      setRow(r);
      setBid(r.bid);
      setBrand(r.brand ?? "");
      setModel(r.model ?? "");
    }).catch((e) => setError(e.message));
  }, [id]);

  const save = async () => {
    if (!row) return;
    try {
      await reserveBidClient.update(id, {
        productId: row.productId,
        grade: row.grade,
        brand, model, bid,
        lastAwardedMinPrice: row.lastAwardedMinPrice,
        lastAwardedWeek: row.lastAwardedWeek,
        bidValidWeekDate: row.bidValidWeekDate,
      });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Save failed");
    }
  };

  if (!row) return <div>Loading...</div>;

  return (
    <div style={{ padding: "1.5rem", maxWidth: 520 }}>
      <h1>Edit Reserve Bid #{row.id}</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div><label>Product ID: <input value={row.productId} disabled /></label></div>
      <div><label>Grade: <input value={row.grade} disabled /></label></div>
      <div><label>Brand: <input value={brand} onChange={(e) => setBrand(e.target.value)} /></label></div>
      <div><label>Model: <input value={model} onChange={(e) => setModel(e.target.value)} /></label></div>
      <div><label>Bid: <input type="number" step="0.01" value={bid} onChange={(e) => setBid(e.target.value)} /></label></div>
      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem" }}>
        <button onClick={save}>Save</button>
        <button onClick={() => router.back()}>Cancel</button>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Write new page**

```tsx
// .../new/page.tsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/api/reserveBidClient";

export default function NewReserveBidPage() {
  const router = useRouter();
  const [productId, setProductId] = useState("");
  const [grade, setGrade] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [bid, setBid] = useState("");
  const [error, setError] = useState<string | null>(null);

  const create = async () => {
    try {
      await reserveBidClient.create({ productId, grade, brand, model, bid });
      router.push("/admin/auctions-data-center/reserve-bids");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Create failed");
    }
  };

  return (
    <div style={{ padding: "1.5rem", maxWidth: 520 }}>
      <h1>New Reserve Bid</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div><label>Product ID: <input value={productId} onChange={(e) => setProductId(e.target.value)} required /></label></div>
      <div><label>Grade: <input value={grade} onChange={(e) => setGrade(e.target.value)} required /></label></div>
      <div><label>Brand: <input value={brand} onChange={(e) => setBrand(e.target.value)} /></label></div>
      <div><label>Model: <input value={model} onChange={(e) => setModel(e.target.value)} /></label></div>
      <div><label>Bid: <input type="number" step="0.01" value={bid} onChange={(e) => setBid(e.target.value)} required /></label></div>
      <div style={{ marginTop: "1rem", display: "flex", gap: "0.5rem" }}>
        <button onClick={create}>Create</button>
        <button onClick={() => router.back()}>Cancel</button>
      </div>
    </div>
  );
}
```

- [ ] **Step 3: Write upload page**

```tsx
// .../upload/page.tsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { reserveBidClient } from "@/lib/api/reserveBidClient";
import type { ReserveBidUploadResult } from "@/lib/types/reserveBid";

export default function UploadReserveBidsPage() {
  const router = useRouter();
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<ReserveBidUploadResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const onUpload = async () => {
    if (!file) return;
    try {
      const r = await reserveBidClient.upload(file);
      setResult(r);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Upload failed");
    }
  };

  return (
    <div style={{ padding: "1.5rem", maxWidth: 720 }}>
      <h1>Upload Reserve Bids</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      <div>
        <input type="file" accept=".xlsx" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
        <button onClick={onUpload} disabled={!file}>Upload</button>
        <button onClick={() => router.push("/admin/auctions-data-center/reserve-bids")}>Back</button>
      </div>
      {result && (
        <div style={{ marginTop: "1rem" }}>
          <p>Created: {result.created}, Updated: {result.updated}, Unchanged: {result.unchanged},
             Audits: {result.auditsGenerated}</p>
          {result.errors.length > 0 && (
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr><th>Row</th><th>Product</th><th>Grade</th><th>Reason</th></tr>
              </thead>
              <tbody>
                {result.errors.map((e, i) => (
                  <tr key={i}>
                    <td>{e.rowNumber}</td><td>{e.productId ?? "—"}</td>
                    <td>{e.grade ?? "—"}</td><td>{e.reason}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 4: Write audit page**

```tsx
// .../[id]/audit/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { reserveBidClient } from "@/lib/api/reserveBidClient";
import type { ReserveBidAuditRow } from "@/lib/types/reserveBid";

export default function AuditPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [rows, setRows] = useState<ReserveBidAuditRow[]>([]);

  useEffect(() => {
    reserveBidClient.audit(id).then((r) => setRows(r.rows));
  }, [id]);

  return (
    <div style={{ padding: "1.5rem" }}>
      <h1>Audit Trail — Reserve Bid #{id}</h1>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr><th>Product</th><th>Grade</th><th>Old</th><th>New</th><th>Changed By</th><th>When</th></tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td>{r.productId}</td><td>{r.grade}</td>
              <td>{r.oldPrice}</td><td>{r.newPrice}</td>
              <td>{r.changedByUsername ?? "—"}</td>
              <td>{new Date(r.createdDate).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

- [ ] **Step 5: Build**

Run: `cd frontend && npm run build`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add frontend/src/app/\(dashboard\)/admin/auctions-data-center/reserve-bids/
git commit -m "feat(frontend): EB edit + new + upload + audit pages"
```

---

## Task 19: Playwright E2E

**Files:**
- Create: `frontend/e2e/reserveBid.spec.ts`

- [ ] **Step 1: Write the E2E test**

```typescript
import { test, expect } from "@playwright/test";

test.describe("Reserve Bids admin", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/login");
    await page.fill('input[name="email"]', "admin@test.com");
    await page.fill('input[name="password"]', "Admin123!");
    await page.click('button[type="submit"]');
    await page.waitForURL((u) => !u.pathname.includes("/login"));
  });

  test("overview loads and lists rows", async ({ page }) => {
    await page.goto("/admin/auctions-data-center/reserve-bids");
    await expect(page.locator("h1")).toContainText("Reserve Bids");
    await expect(page.locator("table tbody tr")).toHaveCount(20);
  });

  test("edit + audit flow", async ({ page }) => {
    await page.goto("/admin/auctions-data-center/reserve-bids");
    const firstEdit = page.locator("table tbody tr").first().getByText("Edit");
    await firstEdit.click();
    await page.waitForURL(/\/reserve-bids\/\d+$/);
    const bidInput = page.locator('input[type="number"]');
    const old = await bidInput.inputValue();
    await bidInput.fill((Number(old) + 1).toString());
    await page.getByText("Save").click();
    await page.waitForURL("**/reserve-bids");
    // audit should now have at least one entry for this row
  });
});
```

- [ ] **Step 2: Run E2E**

Run: `cd frontend && npx playwright test reserveBid.spec.ts`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
git add frontend/e2e/reserveBid.spec.ts
git commit -m "test(e2e): EB admin overview + edit + audit roundtrip"
```

---

## Task 20: Docs updates

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/architecture/data-model.md`
- Modify: `docs/app-metadata/modules.md`
- Create: `docs/business-logic/reserve-bid-sync.md`
- Modify: `docs/deployment/setup.md`
- Modify: `docs/testing/coverage.md`

- [ ] **Step 1: Append REST endpoints**

Append to `docs/api/rest-endpoints.md`:

```markdown
## Reserve Bids (EB)

All endpoints: `/api/v1/admin/reserve-bids/**` — role `Administrator`.

### GET /api/v1/admin/reserve-bids
Paginated list with filters (productId, grade, minBid, maxBid, updatedSince, page, size).

### GET /api/v1/admin/reserve-bids/{id}
Single row.

### POST /api/v1/admin/reserve-bids
Create (no audit — new row).

### PUT /api/v1/admin/reserve-bids/{id}
Full update; audit row auto-written on price change.

### DELETE /api/v1/admin/reserve-bids/{id}
Delete; audit trail cascade-dropped.

### POST /api/v1/admin/reserve-bids/upload
Bulk Excel upload (multipart `file`). Returns `ReserveBidUploadResult { created, updated, unchanged, auditsGenerated, errors[] }`.

### GET /api/v1/admin/reserve-bids/download
Streaming Excel export (`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`).

### GET /api/v1/admin/reserve-bids/{id}/audit
Per-row audit trail (paginated).

### GET /api/v1/admin/reserve-bids/sync
Current Snowflake sync watermark + drift state (IN_SYNC | BEHIND_SOURCE | NEVER_SYNCED).

### POST /api/v1/admin/reserve-bids/sync
Manual pull trigger; wraps the same service method as the 30-min cron. Returns `202 Accepted`.
```

- [ ] **Step 2: Prepend ADR**

Prepend to `docs/architecture/decisions.md`:

```markdown
## 2026-04-22 — Sub-project 4A: EB module port

**Decision:** Ported the Mendix `ecoatm_eb` module into `auctions.reserve_bid`
+ `reserve_bid_audit` + `reserve_bid_sync` with full bidirectional
Snowflake sync (push on write + 30-min pull cron).

**Rationale:** Sub-project 4C's target-price CTE joins against reserve
bids; that table must exist and be kept in sync with the external
pricing engine's authoritative Snowflake data.

**Key choices:**
- Schema lives in `auctions` (not a new `exchange_bid` namespace) — EB
  is consumed solely by auctions
- `product_id` as `VARCHAR(100)` to match `bid_data.ecoid` join key
- Dropped Delete-All bulk button (operational risk > parity value)
- Pull path uses delete-all + re-insert (matches Mendix); explicitly
  does NOT publish `ReserveBidChangedEvent` to prevent echo loops
- Feature guard via `eb.sync.enabled` Spring property (modern lacks a
  generic feature-flag service)

**Consequences:**
- Admin UI at `/admin/auctions-data-center/reserve-bids/**`
- New Flyway V74 (schema) + V75 (data load, 15,875 rows)
- Sub-project 4C unblocked on the EB dimension

---
```

- [ ] **Step 3: Append data-model, modules, business-logic, setup, coverage**

To `docs/architecture/data-model.md`:
```markdown
## auctions.reserve_bid / reserve_bid_audit / reserve_bid_sync
ExchangeBid reserve floor prices, price-change audit trail, and Snowflake sync watermark singleton. See `docs/tasks/auction-eb-module-design.md` for schema.
```

To `docs/app-metadata/modules.md`:
```markdown
## Exchange Bid (EB)
- Source module: `ecoatm_eb`
- Primary tables: `auctions.reserve_bid`, `auctions.reserve_bid_audit`, `auctions.reserve_bid_sync`
- Purpose: per-(product_id, grade) reserve floor prices consumed by sub-project 4C target-price recalc
- Admin surface: `/admin/auctions-data-center/reserve-bids/**`
```

Create `docs/business-logic/reserve-bid-sync.md`:
```markdown
# Reserve Bid Snowflake Sync

## Push (on every local write)
Writes fire `ReserveBidChangedEvent` at `AFTER_COMMIT`. A transactional event listener running on `snowflakeExecutor` invokes `AUCTIONS.UPSERT_RESERVE_BID(JSON_CONTENT, ACTING_USER)`. Failure is logged but not propagated — Postgres commit stands.

## Pull (every 30 min, ShedLock-guarded)
`ReserveBidSyncScheduledJob` reads Snowflake's `MAX(LAST_UPDATE_DATETIME)`. If source is strictly newer than `reserve_bid_sync.last_sync_datetime`, delete all local rows, bulk insert fresh copy, update watermark. Does NOT publish `ReserveBidChangedEvent` — would otherwise cause push-pull echo loop.

## Feature flag
`eb.sync.enabled=true` (default). When false, both paths log "would sync" and no-op.
```

To `docs/deployment/setup.md` (near config), append:
```markdown
### EB sync config
- `eb.sync.enabled` — default `true`; disables both push + pull
- `eb.sync.fixed-delay-ms` — default 30 min; pull cadence
- `eb.sync.writer` / `eb.sync.reader` — `logging` (default) or `jdbc` (prod)
```

To `docs/testing/coverage.md`:
```markdown
## auctions.reservebid (new 2026-04-22)
Target 85%+. Upload + sync branches are the load-bearing paths; see `ReserveBidServiceTest` + `ReserveBidRepositoryIT` + `ReserveBidControllerIT` + `reserveBid.spec.ts`.
```

- [ ] **Step 4: Commit**

```bash
git add docs/
git commit -m "docs: EB module — REST, ADR, data-model, modules, sync semantics, coverage"
```

---

## Self-Review Notes

**Spec coverage check:** Every section of `docs/tasks/auction-eb-module-design.md` maps to a task:

- §2 in-scope items → Tasks 1-2 (schema, data), 3-5 (entities, repos, DTOs), 6-9 (service CRUD/upload/download/audit/sync), 10-13 (Snowflake), 14-15 (controller + config), 16-18 (frontend), 19 (E2E), 20 (docs)
- §3 decisions → encoded in migration + entities + service tests
- §4 architecture → file inventory in File Structure section above
- §5 schema → Task 1
- §6 API surface → Task 14
- §7 Snowflake integration → Tasks 10-13
- §8 data + testing → Tasks 2, 4, 6-9, 11, 13, 14, 19
- §9 docs → Task 20
- §10 risks + gaps → documented in ADR (Task 20)

**Known gaps this plan accepts:**

- `@Async("snowflakeExecutor")` on the push listener assumes `AsyncConfig` already defines that bean — verified present in `backend/src/main/java/com/ecoatm/salesplatform/config/AsyncConfig.java`. No new async config needed.
- Snowflake `DataSource` bean for the JDBC writer/reader is assumed to exist (consumed by `JdbcAuctionStatusSnowflakeWriter` today). If missing, add via existing Snowflake config conditional; out of scope for this plan.
- Extractor `--source-db` DB_HOSTS map mirrors the sibling `extract_qa_data.py`. If host mapping differs in your environment, adjust Task 2 Step 2 command accordingly.
- `ReserveBidFixtureGenerator` (Task 7 Step 1) is a one-shot utility; it's committed for CI reproducibility but never runs on the normal test path.

**Type consistency:** `ReserveBidRow`, `ReserveBidRequest`, `ReserveBidUploadResult`, `ReserveBidAuditRow`, `ReserveBidAuditResponse`, `ReserveBidListResponse`, `ReserveBidSyncStatus`, `ReserveBidChangedEvent`, `ReserveBidSnowflakePayload` — referenced identically across backend tasks 5–14 and frontend tasks 16–18.

**Method signature consistency:**
- `ReserveBidService.create(long userId, ReserveBidRequest req)` — Tasks 6 + 14
- `ReserveBidService.update(long userId, long id, ReserveBidRequest req)` — Tasks 6 + 14
- `ReserveBidService.upload(long userId, MultipartFile file)` — Tasks 7 + 14
- `ReserveBidService.downloadAll(OutputStream out)` — Tasks 8 + 14
- `ReserveBidService.findAudit(long reserveBidId, int page, int size)` — Tasks 9 + 14
- `ReserveBidService.syncStatus()` — Tasks 9 + 14
- `ReserveBidService.runScheduledSync()` → returns `int rowsFetched` — Tasks 13 + 14

**Constructor evolution:** The `ReserveBidService` constructor grows across Tasks 6 → 8 → 13 (6 → 7 → 8 params). Engineers executing out of order must use the final signature from Task 13. Existing tests update the constructor call when they add new deps (explicit in Task 8 Step 3 and Task 13 Step 1).

---

## Execution Handoff

Plan saved to `docs/tasks/auction-eb-module-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task with two-stage review (spec compliance → code quality); fast iteration, preserves controller context.

**2. Inline Execution** — execute tasks in this session using `superpowers:executing-plans`, batch with checkpoints for review.

Which approach?
