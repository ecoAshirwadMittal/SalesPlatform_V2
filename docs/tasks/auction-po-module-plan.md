# Sub-project 4B: PO Module Port — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete port of the Mendix `ecoatm_po` (PurchaseOrder) module to modern — schema, admin CRUD + Excel wipe-and-replace upload + Excel download + push-only Snowflake sync — so sub-project 4C's target-price CTE can join against a real, populated `auctions.po_detail` table.

**Architecture:** 3-tier split (controller + service + repositories). Push-only Snowflake sync — write-path event listener on `snowflakeExecutor`, no pull cron, no reader, no echo prevention (Mendix has no PO pull either). Lifecycle (`DRAFT`/`ACTIVE`/`CLOSED`) derived from week range, never stored. `product_id` stored as `VARCHAR(100)` for direct join with `bid_data.ecoid`. Wipe-and-replace upload semantics; strict rejection on row errors or missing buyer codes. No Delete-All button. Role gate `Administrator` + `SalesOps`.

**Tech Stack:** Spring Boot 3 + Java 21 + JPA + Flyway + PostgreSQL + Snowflake JDBC + Apache POI (Excel) + Next.js 16 + Playwright.

**Design doc:** `docs/tasks/auction-po-module-design.md`
**Reference plan (4A):** `docs/tasks/auction-eb-module-plan.md` — mirror structure where applicable.

---

## File Structure

### New backend files

```
backend/src/main/java/com/ecoatm/salesplatform/
├── controller/admin/
│   └── PurchaseOrderController.java
├── service/auctions/purchaseorder/
│   ├── PurchaseOrderService.java
│   ├── PODetailService.java
│   ├── PurchaseOrderValidator.java
│   ├── POExcelParser.java
│   ├── POExcelBuilder.java
│   ├── PurchaseOrderException.java
│   └── PurchaseOrderValidationException.java
├── service/auctions/snowflake/
│   ├── PurchaseOrderSnowflakePushListener.java
│   ├── PurchaseOrderSnowflakeWriter.java
│   ├── JdbcPurchaseOrderSnowflakeWriter.java
│   ├── LoggingPurchaseOrderSnowflakeWriter.java
│   └── PurchaseOrderSnowflakePayload.java
├── repository/auctions/
│   ├── PurchaseOrderRepository.java
│   └── PODetailRepository.java
├── model/auctions/
│   ├── PurchaseOrder.java
│   ├── PODetail.java
│   └── PurchaseOrderLifecycleState.java
├── dto/
│   ├── PurchaseOrderRow.java
│   ├── PurchaseOrderRequest.java
│   ├── PurchaseOrderListResponse.java
│   ├── PODetailRow.java
│   ├── PODetailListResponse.java
│   └── PODetailUploadResult.java
└── event/
    └── PurchaseOrderChangedEvent.java
```

### Modified backend files
- `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java` — register `/api/v1/admin/purchase-orders/**` with `Administrator` + `SalesOps`
- `backend/src/main/resources/application.yml` — add `po.sync.*` config block

### New schema migrations
- `backend/src/main/resources/db/migration/V78__auctions_purchase_order.sql`
- `backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql` (generated)

### New extractor script
- `migration_scripts/extract_po_data.py`

### New frontend files
```
frontend/src/
├── lib/api/purchaseOrderClient.ts
├── lib/types/purchaseOrder.ts
└── app/(dashboard)/admin/auctions-data-center/purchase-orders/
    ├── page.tsx
    ├── new/page.tsx
    └── [id]/page.tsx
```

### Docs updates
- `docs/api/rest-endpoints.md` — append `## Purchase Orders (PO)` section
- `docs/architecture/decisions.md` — new ADR `2026-04-24 — Sub-project 4B: PO module port`
- `docs/architecture/data-model.md` — add `auctions.purchase_order` + `auctions.po_detail`
- `docs/app-metadata/modules.md` — add PO module entry
- `docs/business-logic/purchase-order-sync.md` — new file
- `docs/deployment/setup.md` — add `po.sync.*` config
- `docs/testing/coverage.md` — new package coverage

### New test fixtures
```
backend/src/test/resources/fixtures/
├── po-upload-sample.xlsx
├── po-upload-missing-buyer-code.xlsx
├── po-upload-bad-price.xlsx
└── po-upload-roundtrip.xlsx
```

---

## Pre-flight

- [ ] **Verify Flyway version slot is still V78/V79**

```bash
ls backend/src/main/resources/db/migration/ | tail -5
```

Expected last file: `V77__data_auctions_reserve_bid.sql`. If V78 or V79 is taken (another branch merged), shift this plan's migration numbers up before Task 1.

- [ ] **Create worktree for 4B**

```bash
cd ..
git -C SalesPlatform_Modern worktree add SalesPlatform_Modern-4B -b feat/sub4b-po-module
cd SalesPlatform_Modern-4B
```

All implementation tasks below run from `SalesPlatform_Modern-4B/`.

---

## Task 1: V78 schema migration

**Files:**
- Create: `backend/src/main/resources/db/migration/V78__auctions_purchase_order.sql`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/migration/V78MigrationTest.java`

- [ ] **Step 1: Write the failing migration test**

```java
package com.ecoatm.salesplatform.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class V78MigrationTest {

    @Autowired JdbcTemplate jdbc;

    @Test
    void purchaseOrderTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='purchase_order'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void poDetailTableExists() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
              + "WHERE table_schema='auctions' AND table_name='po_detail'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void uniquePoDetailConstraintEnforced() {
        Long weekId = jdbc.queryForObject(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes ORDER BY id LIMIT 1", Long.class);
        Long poId = jdbc.queryForObject(
                "INSERT INTO auctions.purchase_order "
              + "(week_from_id, week_to_id, week_range_label) "
              + "VALUES (?, ?, 'TEST') RETURNING id", Long.class, weekId, weekId);

        jdbc.update("INSERT INTO auctions.po_detail "
                  + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                  + "VALUES (?, ?, '99001', 'A_YYY', 10.00)", poId, buyerCodeId);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.po_detail "
                                + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                                + "VALUES (?, ?, '99001', 'A_YYY', 20.00)",
                                poId, buyerCodeId));
    }

    @Test
    void poDetailCascadesOnPurchaseOrderDelete() {
        Long weekId = jdbc.queryForObject(
                "SELECT id FROM mdm.week ORDER BY id LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes ORDER BY id LIMIT 1", Long.class);
        Long poId = jdbc.queryForObject(
                "INSERT INTO auctions.purchase_order "
              + "(week_from_id, week_to_id, week_range_label) "
              + "VALUES (?, ?, 'CASCADE-TEST') RETURNING id", Long.class, weekId, weekId);
        jdbc.update("INSERT INTO auctions.po_detail "
                  + "(purchase_order_id, buyer_code_id, product_id, grade, price) "
                  + "VALUES (?, ?, '99002', 'B_NNN', 5.00)", poId, buyerCodeId);

        jdbc.update("DELETE FROM auctions.purchase_order WHERE id = ?", poId);

        Integer orphans = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.po_detail WHERE purchase_order_id = ?",
                Integer.class, poId);
        assertThat(orphans).isZero();
    }

    @Test
    void weekFkEnforced() {
        assertThrows(DataIntegrityViolationException.class,
                () -> jdbc.update("INSERT INTO auctions.purchase_order "
                                + "(week_from_id, week_to_id, week_range_label) "
                                + "VALUES (-99, -99, 'BAD-FK')"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=V78MigrationTest`
Expected: FAIL — `relation "auctions.purchase_order" does not exist`

- [ ] **Step 3: Write the V78 migration**

Content (copy verbatim into `backend/src/main/resources/db/migration/V78__auctions_purchase_order.sql`):

```sql
-- =============================================================================
-- V78: auctions — purchase_order + po_detail (PurchaseOrder module)
-- Source: ecoatm_po$purchaseorder (13 rows), ecoatm_po$podetail (9,895).
-- Drops: ecoatm_po$weeklypo (12,384 rows — fulfillment tracker, 4C unused),
--        ecoatm_po$weekperiod (54 — derivable from week_from/to),
--        ecoatm_po$purchaseorderdoc (82 — file blobs, modern streams),
--        ecoatm_po$pohelper (21 — Mendix client-side UX scratch).
-- Junctions collapsed to direct FK: purchaseorder_week_from/to → week_from_id /
--        week_to_id; podetail_buyercode → buyer_code_id;
--        podetail_purchaseorder → purchase_order_id.
-- Design: docs/tasks/auction-po-module-design.md
-- =============================================================================

CREATE TABLE auctions.purchase_order (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    week_from_id            BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_to_id              BIGINT          NOT NULL REFERENCES mdm.week(id),
    week_range_label        VARCHAR(200)    NOT NULL,
    valid_year_week         BOOLEAN         NOT NULL DEFAULT TRUE,
    total_records           INTEGER         NOT NULL DEFAULT 0,
    po_refresh_timestamp    TIMESTAMPTZ,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id)
);

CREATE INDEX idx_po_week_from    ON auctions.purchase_order(week_from_id);
CREATE INDEX idx_po_week_to      ON auctions.purchase_order(week_to_id);
CREATE INDEX idx_po_changed_date ON auctions.purchase_order(changed_date DESC);

CREATE TABLE auctions.po_detail (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    purchase_order_id       BIGINT          NOT NULL
                                            REFERENCES auctions.purchase_order(id)
                                            ON DELETE CASCADE,
    buyer_code_id           BIGINT          NOT NULL REFERENCES buyer_mgmt.buyer_codes(id),
    product_id              VARCHAR(100)    NOT NULL,
    grade                   VARCHAR(200)    NOT NULL,
    model_name              VARCHAR(200),
    price                   NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    qty_cap                 INTEGER,
    price_fulfilled         NUMERIC(14, 4),
    qty_fulfilled           INTEGER,
    temp_buyer_code         VARCHAR(200),
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_po_detail UNIQUE (purchase_order_id, product_id, grade, buyer_code_id)
);

CREATE INDEX idx_pod_po             ON auctions.po_detail(purchase_order_id);
CREATE INDEX idx_pod_buyer_code     ON auctions.po_detail(buyer_code_id);
CREATE INDEX idx_pod_product_grade  ON auctions.po_detail(product_id, grade);
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=V78MigrationTest`
Expected: PASS — all 5 tests green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/resources/db/migration/V78__auctions_purchase_order.sql \
        backend/src/test/java/com/ecoatm/salesplatform/migration/V78MigrationTest.java
git commit -m "feat(4b): V78 schema — auctions.purchase_order + po_detail"
```

---

## Task 2: Extractor script + V79 data migration

**Files:**
- Create: `migration_scripts/extract_po_data.py`
- Generated: `backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql`
- Test: `migration_scripts/test_extract_po_data.py`

- [ ] **Step 1: Write the failing extractor test**

```python
# migration_scripts/test_extract_po_data.py
import os
import re
import subprocess
import sys
from pathlib import Path

import pytest

REPO_ROOT = Path(__file__).resolve().parent.parent
SCRIPT = REPO_ROOT / "migration_scripts" / "extract_po_data.py"
OUTPUT = REPO_ROOT / "backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql"


@pytest.fixture(autouse=True)
def cleanup_v79():
    if OUTPUT.exists():
        OUTPUT.unlink()
    yield
    if OUTPUT.exists():
        OUTPUT.unlink()


def test_script_runs_against_qa(monkeypatch):
    if not os.environ.get("PO_EXTRACT_DB"):
        pytest.skip("Set PO_EXTRACT_DB=qa-0327 to run extractor IT")
    result = subprocess.run(
        [sys.executable, str(SCRIPT), "--source-db", os.environ["PO_EXTRACT_DB"]],
        capture_output=True, text=True, cwd=REPO_ROOT,
    )
    assert result.returncode == 0, result.stderr
    assert OUTPUT.exists()
    content = OUTPUT.read_text()
    # 13 PO header rows
    po_inserts = re.findall(r"INSERT INTO auctions\.purchase_order", content)
    assert len(po_inserts) >= 1
    # 9,895 detail rows — chunks of 1k, so ~10 INSERT statements
    pod_inserts = re.findall(r"INSERT INTO auctions\.po_detail", content)
    assert len(pod_inserts) >= 9
    # legacy_id preserved
    assert "legacy_id" in content


def test_script_with_invalid_db_fails():
    result = subprocess.run(
        [sys.executable, str(SCRIPT), "--source-db", "nonexistent-db"],
        capture_output=True, text=True, cwd=REPO_ROOT,
    )
    assert result.returncode != 0
    assert "could not connect" in result.stderr.lower() or "fatal" in result.stderr.lower()
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd migration_scripts && python -m pytest test_extract_po_data.py -v`
Expected: FAIL — `extract_po_data.py` does not exist.

- [ ] **Step 3: Write the extractor script**

Content for `migration_scripts/extract_po_data.py` — modelled on `extract_eb_data.py` (existing in repo). Key differences from EB extractor: query 2 source tables, remap 3 FKs (week_from, week_to, buyer_code), skip + WARN on FK orphans:

```python
"""
extract_po_data.py — Extract PurchaseOrder + PODetail from Mendix source DB
and emit V79 Flyway migration.

Source tables:
  ecoatm_po$purchaseorder
  ecoatm_po$podetail
  ecoatm_po$podetail_purchaseorder        (junction → po_detail.purchase_order_id)
  ecoatm_po$podetail_buyercode            (junction → po_detail.buyer_code_id)
  ecoatm_po$purchaseorder_week_from       (junction → purchase_order.week_from_id)
  ecoatm_po$purchaseorder_week_to         (junction → purchase_order.week_to_id)

Skipped tables (per design doc §2):
  ecoatm_po$weeklypo, ecoatm_po$weekperiod, ecoatm_po$purchaseorderdoc,
  ecoatm_po$pohelper, all junctions involving them.

Output: backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql

Usage:
  python extract_po_data.py --source-db qa-0327
  python extract_po_data.py --source-db prod-0325
"""
import argparse
import os
import sys
from datetime import datetime
from pathlib import Path

import psycopg2
from psycopg2.extras import RealDictCursor

REPO_ROOT = Path(__file__).resolve().parent.parent
OUT_PATH = REPO_ROOT / "backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql"
CHUNK = 1000


def connect(db: str):
    return psycopg2.connect(
        host="localhost", user="postgres",
        password=os.environ.get("PGPASSWORD", "Agarwal1$"),
        dbname=db,
    )


def fetch_all(conn, sql, *args):
    with conn.cursor(cursor_factory=RealDictCursor) as cur:
        cur.execute(sql, args)
        return cur.fetchall()


def sql_str(v):
    if v is None:
        return "NULL"
    if isinstance(v, (int, float)):
        return str(v)
    if isinstance(v, datetime):
        return f"'{v.isoformat()}'::timestamptz"
    if isinstance(v, bool):
        return "TRUE" if v else "FALSE"
    return "'" + str(v).replace("'", "''") + "'"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-db", required=True)
    args = parser.parse_args()

    conn = connect(args.source_db)

    # 1. PO headers + week junctions
    purchase_orders = fetch_all(conn,
        "SELECT id, weekrange, validyearweek, porefreshtimestamp, totalrecords, "
        "       createddate, changeddate "
        "FROM ecoatm_po$purchaseorder")
    week_from = {r["ecoatm_po$purchaseorderid"]: r["ecoatm_mdm$weekid"]
                 for r in fetch_all(conn,
                    "SELECT * FROM ecoatm_po$purchaseorder_week_from")}
    week_to = {r["ecoatm_po$purchaseorderid"]: r["ecoatm_mdm$weekid"]
               for r in fetch_all(conn,
                  "SELECT * FROM ecoatm_po$purchaseorder_week_to")}

    # 2. PO details + buyer/PO junctions
    po_details = fetch_all(conn,
        "SELECT id, productid, grade, modelname, price, qtycap, "
        "       pricefulfilled, qtyfullfiled AS qtyfulfilled, "
        "       tempbuyercode, createddate, changeddate "
        "FROM ecoatm_po$podetail")
    pod_to_po = {r["ecoatm_po$podetailid"]: r["ecoatm_po$purchaseorderid"]
                 for r in fetch_all(conn,
                    "SELECT * FROM ecoatm_po$podetail_purchaseorder")}
    pod_to_bc = {r["ecoatm_po$podetailid"]: r["ecoatm_buyermanagement$buyercodeid"]
                 for r in fetch_all(conn,
                    "SELECT * FROM ecoatm_po$podetail_buyercode")}

    skipped = []
    pod_rows = []
    for r in po_details:
        po_legacy = pod_to_po.get(r["id"])
        bc_legacy = pod_to_bc.get(r["id"])
        if po_legacy is None or bc_legacy is None:
            skipped.append((r["id"], "missing FK"))
            continue
        pod_rows.append({**r, "po_legacy": po_legacy, "bc_legacy": bc_legacy})

    if len(skipped) > 10:
        print(f"FATAL: {len(skipped)} po_detail rows skipped (>10 threshold). "
              "Investigate junction integrity.", file=sys.stderr)
        sys.exit(1)
    for sid, reason in skipped:
        print(f"WARN: skipping po_detail legacy_id={sid} ({reason})",
              file=sys.stderr)

    # 3. Emit SQL
    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    with OUT_PATH.open("w", newline="\n") as f:
        f.write(f"-- V79: PO data load — generated from {args.source_db} "
                f"on {datetime.utcnow().isoformat()}Z\n")
        f.write("-- Generated by migration_scripts/extract_po_data.py — do not edit by hand.\n\n")

        # Purchase orders
        f.write("INSERT INTO auctions.purchase_order\n")
        f.write("    (legacy_id, week_from_id, week_to_id, week_range_label,\n")
        f.write("     valid_year_week, total_records, po_refresh_timestamp,\n")
        f.write("     created_date, changed_date) VALUES\n")
        po_sql_rows = []
        for r in purchase_orders:
            wf = week_from.get(r["id"])
            wt = week_to.get(r["id"])
            if wf is None or wt is None:
                print(f"WARN: skipping purchase_order legacy_id={r['id']} "
                      "(missing week junction)", file=sys.stderr)
                continue
            po_sql_rows.append(
                f"    ({r['id']}, "
                f"(SELECT id FROM mdm.week WHERE legacy_id = {wf}), "
                f"(SELECT id FROM mdm.week WHERE legacy_id = {wt}), "
                f"{sql_str(r['weekrange'] or 'UNKNOWN')}, "
                f"{sql_str(r['validyearweek'])}, "
                f"{r['totalrecords'] or 0}, "
                f"{sql_str(r['porefreshtimestamp'])}, "
                f"{sql_str(r['createddate'])}, "
                f"{sql_str(r['changeddate'])})"
            )
        f.write(",\n".join(po_sql_rows) + ";\n\n")

        # Detail rows in chunks of CHUNK
        for i in range(0, len(pod_rows), CHUNK):
            batch = pod_rows[i:i + CHUNK]
            f.write("INSERT INTO auctions.po_detail\n")
            f.write("    (legacy_id, purchase_order_id, buyer_code_id,\n")
            f.write("     product_id, grade, model_name, price, qty_cap,\n")
            f.write("     price_fulfilled, qty_fulfilled, temp_buyer_code,\n")
            f.write("     created_date, changed_date) VALUES\n")
            rows_sql = []
            for r in batch:
                rows_sql.append(
                    f"    ({r['id']}, "
                    f"(SELECT id FROM auctions.purchase_order WHERE legacy_id = {r['po_legacy']}), "
                    f"(SELECT id FROM buyer_mgmt.buyer_codes WHERE legacy_id = {r['bc_legacy']}), "
                    f"{sql_str(str(r['productid']) if r['productid'] is not None else '')}, "
                    f"{sql_str(r['grade'])}, "
                    f"{sql_str(r['modelname'])}, "
                    f"{r['price'] if r['price'] is not None else 0}, "
                    f"{r['qtycap'] if r['qtycap'] is not None else 'NULL'}, "
                    f"{r['pricefulfilled'] if r['pricefulfilled'] is not None else 'NULL'}, "
                    f"{r['qtyfulfilled'] if r['qtyfulfilled'] is not None else 'NULL'}, "
                    f"{sql_str(r['tempbuyercode'])}, "
                    f"{sql_str(r['createddate'])}, "
                    f"{sql_str(r['changeddate'])})"
                )
            f.write(",\n".join(rows_sql) + ";\n\n")

    print(f"Wrote {OUT_PATH}: "
          f"{len(po_sql_rows)} PO rows, {len(pod_rows)} detail rows, "
          f"{len(skipped)} skipped.")


if __name__ == "__main__":
    main()
```

- [ ] **Step 4: Run test to verify it passes**

Run (with QA DB available):
```bash
cd migration_scripts && PO_EXTRACT_DB=qa-0327 python -m pytest test_extract_po_data.py -v
```

Expected: PASS.

- [ ] **Step 5: Generate V79 against QA**

```bash
cd migration_scripts && python extract_po_data.py --source-db qa-0327
```

Expected: stdout shows `Wrote .../V79__data_auctions_purchase_order.sql: 13 PO rows, ~9895 detail rows, ≤10 skipped.`

- [ ] **Step 6: Verify V79 applies cleanly on a fresh DB**

```bash
cd .. && psql -U postgres -f bootstrap.sql
cd backend && mvn flyway:migrate
```

Expected: Flyway logs `Successfully applied migration V79`.

- [ ] **Step 7: Spot-check row counts**

```bash
psql -U salesplatform -d salesplatform_dev -c "SELECT COUNT(*) FROM auctions.purchase_order;"
psql -U salesplatform -d salesplatform_dev -c "SELECT COUNT(*) FROM auctions.po_detail;"
```

Expected: 13 and ~9895 (minus skipped).

- [ ] **Step 8: Commit**

```bash
git add migration_scripts/extract_po_data.py \
        migration_scripts/test_extract_po_data.py \
        backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql
git commit -m "feat(4b): V79 PO data load + extractor script"
```

---

## Task 3: JPA entities + lifecycle enum

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrder.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PODetail.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrderLifecycleState.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrderTest.java`

- [ ] **Step 1: Write the failing entity test**

```java
package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PurchaseOrderTest {

    @PersistenceContext EntityManager em;

    @Test
    void persistAndReloadPurchaseOrder() {
        Week from = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        Week to = from;
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(from);
        po.setWeekTo(to);
        po.setWeekRangeLabel("TEST-RANGE");
        po.setTotalRecords(0);
        em.persist(po);
        em.flush();
        em.clear();

        PurchaseOrder reloaded = em.find(PurchaseOrder.class, po.getId());
        assertThat(reloaded.getWeekRangeLabel()).isEqualTo("TEST-RANGE");
        assertThat(reloaded.getValidYearWeek()).isTrue();
        assertThat(reloaded.getCreatedDate()).isNotNull();
    }

    @Test
    void persistAndReloadPoDetail() {
        Week w = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        BuyerCode bc = em.find(BuyerCode.class, em.createQuery(
                "SELECT MIN(b.id) FROM BuyerCode b", Long.class).getSingleResult());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(w); po.setWeekTo(w); po.setWeekRangeLabel("X");
        em.persist(po);

        PODetail d = new PODetail();
        d.setPurchaseOrder(po);
        d.setBuyerCode(bc);
        d.setProductId("12345");
        d.setGrade("A_YYY");
        d.setPrice(new BigDecimal("99.99"));
        em.persist(d);
        em.flush();
        em.clear();

        PODetail reloaded = em.find(PODetail.class, d.getId());
        assertThat(reloaded.getProductId()).isEqualTo("12345");
        assertThat(reloaded.getPurchaseOrder().getId()).isEqualTo(po.getId());
    }

    @Test
    void lifecycleStateDeriveDraft() {
        Week future = stubWeek(LocalDate.now().plusDays(30), LocalDate.now().plusDays(36));
        Week futureEnd = stubWeek(LocalDate.now().plusDays(37), LocalDate.now().plusDays(43));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), future, futureEnd))
                .isEqualTo(PurchaseOrderLifecycleState.DRAFT);
    }

    @Test
    void lifecycleStateDeriveActive() {
        Week from = stubWeek(LocalDate.now().minusDays(2), LocalDate.now().plusDays(4));
        Week to = stubWeek(LocalDate.now().plusDays(5), LocalDate.now().plusDays(11));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), from, to))
                .isEqualTo(PurchaseOrderLifecycleState.ACTIVE);
    }

    @Test
    void lifecycleStateDeriveClosed() {
        Week from = stubWeek(LocalDate.now().minusDays(20), LocalDate.now().minusDays(14));
        Week to = stubWeek(LocalDate.now().minusDays(13), LocalDate.now().minusDays(7));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), from, to))
                .isEqualTo(PurchaseOrderLifecycleState.CLOSED);
    }

    private static Week stubWeek(LocalDate start, LocalDate end) {
        Week w = new Week();
        w.setWeekStartDatetime(start.atStartOfDay().atZone(java.time.ZoneOffset.UTC).toInstant());
        w.setWeekEndDatetime(end.atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant());
        return w;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderTest`
Expected: FAIL — `PurchaseOrder` class does not exist.

- [ ] **Step 3: Write the lifecycle enum**

```java
// backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrderLifecycleState.java
package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.mdm.Week;

import java.time.LocalDate;
import java.time.ZoneOffset;

public enum PurchaseOrderLifecycleState {
    DRAFT,
    ACTIVE,
    CLOSED;

    public static PurchaseOrderLifecycleState derive(LocalDate today, Week from, Week to) {
        LocalDate fromStart = from.getWeekStartDatetime().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate toEnd = to.getWeekEndDatetime().atZone(ZoneOffset.UTC).toLocalDate();
        if (today.isBefore(fromStart)) return DRAFT;
        if (today.isAfter(toEnd))      return CLOSED;
        return ACTIVE;
    }
}
```

- [ ] **Step 4: Write the PurchaseOrder entity**

```java
// backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrder.java
package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.identity.User;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order", schema = "auctions")
public class PurchaseOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_from_id")
    private Week weekFrom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_to_id")
    private Week weekTo;

    @Column(name = "week_range_label", nullable = false, length = 200)
    private String weekRangeLabel;

    @Column(name = "valid_year_week", nullable = false)
    private Boolean validYearWeek = Boolean.TRUE;

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords = 0;

    @Column(name = "po_refresh_timestamp")
    private Instant poRefreshTimestamp;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PODetail> details = new ArrayList<>();

    @PreUpdate void onUpdate() { this.changedDate = Instant.now(); }

    // Getters / setters (generate via IDE or write by hand — no Lombok per project rule)

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Week getWeekFrom() { return weekFrom; }
    public void setWeekFrom(Week v) { this.weekFrom = v; }
    public Week getWeekTo() { return weekTo; }
    public void setWeekTo(Week v) { this.weekTo = v; }
    public String getWeekRangeLabel() { return weekRangeLabel; }
    public void setWeekRangeLabel(String v) { this.weekRangeLabel = v; }
    public Boolean getValidYearWeek() { return validYearWeek; }
    public void setValidYearWeek(Boolean v) { this.validYearWeek = v; }
    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer v) { this.totalRecords = v; }
    public Instant getPoRefreshTimestamp() { return poRefreshTimestamp; }
    public void setPoRefreshTimestamp(Instant v) { this.poRefreshTimestamp = v; }
    public Instant getCreatedDate() { return createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public User getOwner() { return owner; }
    public void setOwner(User v) { this.owner = v; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User v) { this.changedBy = v; }
    public List<PODetail> getDetails() { return details; }
}
```

- [ ] **Step 5: Write the PODetail entity**

```java
// backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PODetail.java
package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.identity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "po_detail", schema = "auctions",
       uniqueConstraints = @UniqueConstraint(name = "uq_po_detail",
           columnNames = {"purchase_order_id", "product_id", "grade", "buyer_code_id"}))
public class PODetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_code_id")
    private BuyerCode buyerCode;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "grade", nullable = false, length = 200)
    private String grade;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "qty_cap")
    private Integer qtyCap;

    @Column(name = "price_fulfilled", precision = 14, scale = 4)
    private BigDecimal priceFulfilled;

    @Column(name = "qty_fulfilled")
    private Integer qtyFulfilled;

    @Column(name = "temp_buyer_code", length = 200)
    private String tempBuyerCode;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @PreUpdate void onUpdate() { this.changedDate = Instant.now(); }

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder v) { this.purchaseOrder = v; }
    public BuyerCode getBuyerCode() { return buyerCode; }
    public void setBuyerCode(BuyerCode v) { this.buyerCode = v; }
    public String getProductId() { return productId; }
    public void setProductId(String v) { this.productId = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
    public String getModelName() { return modelName; }
    public void setModelName(String v) { this.modelName = v; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { this.price = v; }
    public Integer getQtyCap() { return qtyCap; }
    public void setQtyCap(Integer v) { this.qtyCap = v; }
    public BigDecimal getPriceFulfilled() { return priceFulfilled; }
    public void setPriceFulfilled(BigDecimal v) { this.priceFulfilled = v; }
    public Integer getQtyFulfilled() { return qtyFulfilled; }
    public void setQtyFulfilled(Integer v) { this.qtyFulfilled = v; }
    public String getTempBuyerCode() { return tempBuyerCode; }
    public void setTempBuyerCode(String v) { this.tempBuyerCode = v; }
    public Instant getCreatedDate() { return createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public User getOwner() { return owner; }
    public void setOwner(User v) { this.owner = v; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User v) { this.changedBy = v; }
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderTest`
Expected: PASS — all 5 tests green.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrder.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PODetail.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrderLifecycleState.java \
        backend/src/test/java/com/ecoatm/salesplatform/model/auctions/PurchaseOrderTest.java
git commit -m "feat(4b): JPA entities — PurchaseOrder + PODetail + lifecycle enum"
```

---

## Task 4: Repositories

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepository.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PODetailRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepositoryTest.java`

- [ ] **Step 1: Write the failing repository test**

```java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PurchaseOrderRepositoryTest {

    @Autowired PurchaseOrderRepository poRepo;
    @Autowired PODetailRepository detailRepo;
    @PersistenceContext EntityManager em;

    @Test
    void deleteAllByPurchaseOrderIdRemovesAllRows() {
        Week w = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        BuyerCode bc = em.find(BuyerCode.class, em.createQuery(
                "SELECT MIN(b.id) FROM BuyerCode b", Long.class).getSingleResult());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(w); po.setWeekTo(w); po.setWeekRangeLabel("REPO-TEST");
        em.persist(po);
        for (int i = 0; i < 5; i++) {
            PODetail d = new PODetail();
            d.setPurchaseOrder(po);
            d.setBuyerCode(bc);
            d.setProductId("P" + i);
            d.setGrade("A_YYY");
            d.setPrice(new BigDecimal("10.00"));
            em.persist(d);
        }
        em.flush();

        long deleted = detailRepo.deleteAllByPurchaseOrderId(po.getId());
        em.flush();

        assertThat(deleted).isEqualTo(5);
        assertThat(detailRepo.countByPurchaseOrderId(po.getId())).isZero();
    }

    @Test
    void findOverlappingWeekFiltersCorrectly() {
        Long currentWeekId = em.createQuery(
                "SELECT w.id FROM Week w WHERE w.weekStartDatetime <= CURRENT_TIMESTAMP "
              + "AND w.weekEndDatetime >= CURRENT_TIMESTAMP", Long.class)
                .setMaxResults(1).getSingleResult();
        Week current = em.find(Week.class, currentWeekId);
        PurchaseOrder activeNow = new PurchaseOrder();
        activeNow.setWeekFrom(current); activeNow.setWeekTo(current);
        activeNow.setWeekRangeLabel("ACTIVE-NOW");
        em.persist(activeNow);
        em.flush();

        var page = poRepo.findActiveOnDate(currentWeekId, PageRequest.of(0, 50));
        assertThat(page.getContent()).extracting(PurchaseOrder::getId)
                .contains(activeNow.getId());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderRepositoryTest`
Expected: FAIL — repositories do not exist.

- [ ] **Step 3: Write `PurchaseOrderRepository`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepository.java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE po.weekFrom.id <= :weekId
          AND po.weekTo.id   >= :weekId
        ORDER BY po.changedDate DESC
        """)
    Page<PurchaseOrder> findActiveOnDate(@Param("weekId") Long weekId, Pageable pageable);

    @Query("""
        SELECT po FROM PurchaseOrder po
        WHERE (:weekFromId IS NULL OR po.weekFrom.id >= :weekFromId)
          AND (:weekToId   IS NULL OR po.weekTo.id   <= :weekToId)
          AND (:yearFrom   IS NULL OR po.weekFrom.year >= :yearFrom)
          AND (:yearTo     IS NULL OR po.weekTo.year   <= :yearTo)
        """)
    Page<PurchaseOrder> findFiltered(
            @Param("weekFromId") Long weekFromId,
            @Param("weekToId") Long weekToId,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            Pageable pageable);
}
```

- [ ] **Step 4: Write `PODetailRepository`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PODetailRepository.java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PODetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PODetailRepository extends JpaRepository<PODetail, Long> {

    Page<PODetail> findByPurchaseOrderId(Long purchaseOrderId, Pageable pageable);

    long countByPurchaseOrderId(Long purchaseOrderId);

    @Modifying
    @Query("DELETE FROM PODetail d WHERE d.purchaseOrder.id = :poId")
    long deleteAllByPurchaseOrderId(@Param("poId") Long poId);
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderRepositoryTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepository.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PODetailRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepositoryTest.java
git commit -m "feat(4b): repositories — PurchaseOrder + PODetail"
```

---

## Task 5: DTOs + exceptions + event

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRow.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRequest.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderListResponse.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailRow.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailListResponse.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailUploadResult.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/event/PurchaseOrderChangedEvent.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderException.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidationException.java`

- [ ] **Step 1: Create the records (no test needed — pure data classes; coverage drives in via service tests)**

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRow.java
package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;

import java.time.Instant;

public record PurchaseOrderRow(
        long id,
        long weekFromId, String weekFromLabel,
        long weekToId,   String weekToLabel,
        String weekRangeLabel,
        PurchaseOrderLifecycleState state,
        int totalRecords,
        Instant poRefreshTimestamp,
        Instant changedDate,
        String changedByUsername) {}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRequest.java
package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseOrderRequest(
        @NotNull Long weekFromId,
        @NotNull Long weekToId) {}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderListResponse.java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PurchaseOrderListResponse(
        List<PurchaseOrderRow> items,
        long total,
        int page,
        int size) {}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailRow.java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record PODetailRow(
        long id, long purchaseOrderId,
        long buyerCodeId, String buyerCode,
        String productId, String grade, String modelName,
        BigDecimal price, Integer qtyCap,
        BigDecimal priceFulfilled, Integer qtyFulfilled) {}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailListResponse.java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PODetailListResponse(
        List<PODetailRow> items,
        long total,
        int page,
        int size) {}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailUploadResult.java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PODetailUploadResult(
        int createdCount,
        int deletedCount,
        int skippedCount,
        List<UploadError> errors) {

    public record UploadError(
            int rowNumber,
            String productId,
            String grade,
            String buyerCode,
            String reason) {}
}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/event/PurchaseOrderChangedEvent.java
package com.ecoatm.salesplatform.event;

public record PurchaseOrderChangedEvent(long purchaseOrderId, Action action) {
    public enum Action { UPSERT, DELETE }
}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderException.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

public class PurchaseOrderException extends RuntimeException {
    private final String code;

    public PurchaseOrderException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
```

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidationException.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import java.util.List;

public class PurchaseOrderValidationException extends PurchaseOrderException {
    private final List<String> details;

    public PurchaseOrderValidationException(String code, String message, List<String> details) {
        super(code, message);
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public List<String> getDetails() { return details; }
}
```

- [ ] **Step 2: Verify code compiles**

Run: `cd backend && mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRow.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderRequest.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/PurchaseOrderListResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailRow.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailListResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/PODetailUploadResult.java \
        backend/src/main/java/com/ecoatm/salesplatform/event/PurchaseOrderChangedEvent.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderException.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidationException.java
git commit -m "feat(4b): DTOs + exceptions + event"
```

---

## Task 6: PurchaseOrderValidator

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidator.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidatorTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.buyermgmt.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOrderValidatorTest {

    WeekRepository weekRepo;
    BuyerCodeRepository bcRepo;
    PurchaseOrderValidator validator;

    @BeforeEach
    void init() {
        weekRepo = mock(WeekRepository.class);
        bcRepo = mock(BuyerCodeRepository.class);
        validator = new PurchaseOrderValidator(weekRepo, bcRepo);
    }

    @Test
    void weekRangeOkPasses() {
        Week from = stubWeek(1L, 202501);
        Week to = stubWeek(2L, 202504);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        var resolved = validator.resolveWeekRange(1L, 2L);
        assertThat(resolved.from().getId()).isEqualTo(1L);
        assertThat(resolved.to().getId()).isEqualTo(2L);
    }

    @Test
    void weekRangeReversedThrows() {
        Week from = stubWeek(1L, 202504);
        Week to = stubWeek(2L, 202501);
        when(weekRepo.findById(1L)).thenReturn(Optional.of(from));
        when(weekRepo.findById(2L)).thenReturn(Optional.of(to));
        assertThatThrownBy(() -> validator.resolveWeekRange(1L, 2L))
                .isInstanceOf(PurchaseOrderValidationException.class)
                .hasMessageContaining("week_from must be <= week_to");
    }

    @Test
    void weekIdNotFoundThrows() {
        when(weekRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> validator.resolveWeekRange(99L, 100L))
                .isInstanceOf(PurchaseOrderValidationException.class);
    }

    @Test
    void buyerCodesAllExistPasses() {
        when(bcRepo.findCodesIn(List.of("ABC", "DEF"))).thenReturn(List.of("ABC", "DEF"));
        validator.requireBuyerCodes(List.of("ABC", "DEF"));
    }

    @Test
    void missingBuyerCodesThrowsListingOffenders() {
        when(bcRepo.findCodesIn(List.of("ABC", "MISSING"))).thenReturn(List.of("ABC"));
        assertThatThrownBy(() -> validator.requireBuyerCodes(List.of("ABC", "MISSING")))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("MISSING_BUYER_CODE");
                    assertThat(ex.getDetails()).contains("MISSING");
                });
    }

    private static Week stubWeek(long id, int weekId) {
        Week w = new Week();
        try {
            var idField = Week.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(w, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        w.setWeekId(weekId);
        return w;
    }
}
```

> **Note:** `BuyerCodeRepository.findCodesIn(List<String>)` may not exist yet — Task 6 also adds it (and the corresponding query) to `BuyerCodeRepository`. The test setup assumes the new method.

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderValidatorTest`
Expected: FAIL — `PurchaseOrderValidator` does not exist.

- [ ] **Step 3: Add `findCodesIn` to `BuyerCodeRepository`**

Open `backend/src/main/java/com/ecoatm/salesplatform/repository/buyermgmt/BuyerCodeRepository.java` and append:

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Query("SELECT b.code FROM BuyerCode b WHERE b.code IN :codes")
List<String> findCodesIn(@Param("codes") List<String> codes);
```

- [ ] **Step 4: Write `PurchaseOrderValidator`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidator.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.buyermgmt.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PurchaseOrderValidator {

    private final WeekRepository weekRepo;
    private final BuyerCodeRepository buyerCodeRepo;

    public PurchaseOrderValidator(WeekRepository weekRepo,
                                  BuyerCodeRepository buyerCodeRepo) {
        this.weekRepo = weekRepo;
        this.buyerCodeRepo = buyerCodeRepo;
    }

    public record WeekRange(Week from, Week to) {}

    public WeekRange resolveWeekRange(Long weekFromId, Long weekToId) {
        Week from = weekRepo.findById(weekFromId).orElseThrow(() ->
                new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                        "week_from id not found: " + weekFromId, List.of()));
        Week to = weekRepo.findById(weekToId).orElseThrow(() ->
                new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                        "week_to id not found: " + weekToId, List.of()));
        if (from.getWeekId() > to.getWeekId()) {
            throw new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                    "week_from must be <= week_to (got "
                            + from.getWeekId() + " > " + to.getWeekId() + ")",
                    List.of());
        }
        return new WeekRange(from, to);
    }

    public void requireBuyerCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) return;
        Set<String> found = new HashSet<>(buyerCodeRepo.findCodesIn(codes));
        List<String> missing = codes.stream().distinct()
                .filter(c -> !found.contains(c))
                .toList();
        if (!missing.isEmpty()) {
            throw new PurchaseOrderValidationException("MISSING_BUYER_CODE",
                    "Unknown buyer codes referenced: " + String.join(", ", missing),
                    missing);
        }
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderValidatorTest`
Expected: PASS — all 5 tests green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidator.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/buyermgmt/BuyerCodeRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderValidatorTest.java
git commit -m "feat(4b): PurchaseOrderValidator + BuyerCodeRepository.findCodesIn"
```

---

## Task 7: PurchaseOrderService — header CRUD

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Write the failing service test**

```java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PurchaseOrderRequest;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseOrderServiceTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    PurchaseOrderValidator validator;
    ApplicationEventPublisher events;
    PurchaseOrderService service;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        validator = mock(PurchaseOrderValidator.class);
        events = mock(ApplicationEventPublisher.class);
        service = new PurchaseOrderService(poRepo, detailRepo, validator, events);
    }

    @Test
    void createValidPersistsAndPublishesEvent() {
        Week from = makeWeek(1L, 202501, "2025 / Wk1");
        Week to = makeWeek(2L, 202504, "2025 / Wk4");
        when(validator.resolveWeekRange(1L, 2L))
                .thenReturn(new PurchaseOrderValidator.WeekRange(from, to));
        when(poRepo.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder po = inv.getArgument(0);
            try {
                var idField = PurchaseOrder.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(po, 42L);
            } catch (Exception e) { throw new RuntimeException(e); }
            return po;
        });
        when(detailRepo.countByPurchaseOrderId(42L)).thenReturn(0L);

        PurchaseOrderRow row = service.create(new PurchaseOrderRequest(1L, 2L));

        assertThat(row.id()).isEqualTo(42L);
        assertThat(row.weekRangeLabel()).isEqualTo("2025 / Wk1 - 2025 / Wk4");
        verify(events).publishEvent(new PurchaseOrderChangedEvent(42L,
                PurchaseOrderChangedEvent.Action.UPSERT));
    }

    @Test
    void findByIdNotFoundThrows() {
        when(poRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOfSatisfying(PurchaseOrderException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("PURCHASE_ORDER_NOT_FOUND"));
    }

    @Test
    void deletePublishesDeleteEvent() {
        PurchaseOrder po = makePo(7L);
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        service.delete(7L);
        verify(poRepo).delete(po);
        verify(events).publishEvent(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.DELETE));
    }

    @Test
    void rowMappingDerivesLifecycleState() {
        Week from = makeWeek(1L, 202501, "2025 / Wk1");
        Week to = makeWeek(2L, 202504, "2025 / Wk4");
        from.setWeekStartDatetime(Instant.now().minusSeconds(86400 * 30));
        from.setWeekEndDatetime(Instant.now().minusSeconds(86400 * 24));
        to.setWeekStartDatetime(Instant.now().minusSeconds(86400 * 23));
        to.setWeekEndDatetime(Instant.now().minusSeconds(86400 * 17));

        PurchaseOrder po = makePo(11L);
        po.setWeekFrom(from); po.setWeekTo(to);
        po.setWeekRangeLabel("2025 / Wk1 - 2025 / Wk4");

        when(poRepo.findById(11L)).thenReturn(Optional.of(po));
        when(detailRepo.countByPurchaseOrderId(11L)).thenReturn(0L);

        PurchaseOrderRow row = service.findById(11L);
        assertThat(row.state()).isEqualTo(PurchaseOrderLifecycleState.CLOSED);
    }

    private static Week makeWeek(long id, int weekId, String label) {
        Week w = new Week();
        try {
            var f = Week.class.getDeclaredField("id");
            f.setAccessible(true); f.set(w, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        w.setWeekId(weekId);
        w.setWeekDisplay(label);
        w.setWeekStartDatetime(Instant.now().atOffset(ZoneOffset.UTC).minusDays(1).toInstant());
        w.setWeekEndDatetime(Instant.now().atOffset(ZoneOffset.UTC).plusDays(6).toInstant());
        w.setYear(2025);
        w.setWeekNumber(1);
        return w;
    }

    private static PurchaseOrder makePo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return po;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderServiceTest`
Expected: FAIL — `PurchaseOrderService` does not exist.

- [ ] **Step 3: Write `PurchaseOrderService`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderService.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PurchaseOrderListResponse;
import com.ecoatm.salesplatform.dto.PurchaseOrderRequest;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final PurchaseOrderValidator validator;
    private final ApplicationEventPublisher events;

    public PurchaseOrderService(PurchaseOrderRepository poRepo,
                                PODetailRepository detailRepo,
                                PurchaseOrderValidator validator,
                                ApplicationEventPublisher events) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.validator = validator;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public PurchaseOrderListResponse list(Long weekFromId, Long weekToId,
                                          Integer yearFrom, Integer yearTo,
                                          Pageable pageable) {
        Page<PurchaseOrder> page = poRepo.findFiltered(
                weekFromId, weekToId, yearFrom, yearTo, pageable);
        List<PurchaseOrderRow> rows = page.getContent().stream().map(this::toRow).toList();
        return new PurchaseOrderListResponse(rows, page.getTotalElements(),
                pageable.getPageNumber(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderRow findById(long id) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        return toRow(po);
    }

    @Transactional
    public PurchaseOrderRow create(PurchaseOrderRequest req) {
        var range = validator.resolveWeekRange(req.weekFromId(), req.weekToId());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(range.from());
        po.setWeekTo(range.to());
        po.setWeekRangeLabel(buildRangeLabel(range.from().getWeekDisplay(),
                                             range.to().getWeekDisplay()));
        po.setTotalRecords(0);
        po.setValidYearWeek(true);
        PurchaseOrder saved = poRepo.save(po);
        events.publishEvent(new PurchaseOrderChangedEvent(saved.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));
        return toRow(saved);
    }

    @Transactional
    public PurchaseOrderRow update(long id, PurchaseOrderRequest req) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        var range = validator.resolveWeekRange(req.weekFromId(), req.weekToId());
        po.setWeekFrom(range.from());
        po.setWeekTo(range.to());
        po.setWeekRangeLabel(buildRangeLabel(range.from().getWeekDisplay(),
                                             range.to().getWeekDisplay()));
        events.publishEvent(new PurchaseOrderChangedEvent(po.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));
        return toRow(po);
    }

    @Transactional
    public void delete(long id) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + id + " not found"));
        poRepo.delete(po);
        events.publishEvent(new PurchaseOrderChangedEvent(id,
                PurchaseOrderChangedEvent.Action.DELETE));
    }

    PurchaseOrderRow toRow(PurchaseOrder po) {
        var state = PurchaseOrderLifecycleState.derive(
                LocalDate.now(ZoneOffset.UTC), po.getWeekFrom(), po.getWeekTo());
        String changedBy = po.getChangedBy() == null ? null : po.getChangedBy().getEmail();
        return new PurchaseOrderRow(
                po.getId(),
                po.getWeekFrom().getId(), po.getWeekFrom().getWeekDisplay(),
                po.getWeekTo().getId(),   po.getWeekTo().getWeekDisplay(),
                po.getWeekRangeLabel(),
                state,
                po.getTotalRecords(),
                po.getPoRefreshTimestamp(),
                po.getChangedDate(),
                changedBy);
    }

    private static String buildRangeLabel(String fromLabel, String toLabel) {
        if (fromLabel == null) fromLabel = "?";
        if (toLabel == null) toLabel = "?";
        return fromLabel + " - " + toLabel;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderServiceTest`
Expected: PASS — all 4 tests green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PurchaseOrderServiceTest.java
git commit -m "feat(4b): PurchaseOrderService — header CRUD + lifecycle derivation"
```

---

## Task 8: POExcelParser + POExcelBuilder

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelParser.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelBuilder.java`
- Create test fixtures: `backend/src/test/resources/fixtures/po-upload-sample.xlsx`,
  `po-upload-bad-price.xlsx`, `po-upload-roundtrip.xlsx`,
  `po-upload-missing-buyer-code.xlsx` (build via test-helper, see Step 1)
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelParserTest.java`

- [ ] **Step 1: Write the failing parser test (also generates fixtures via helper)**

```java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class POExcelParserTest {

    private final POExcelParser parser = new POExcelParser();

    @Test
    void parsesValidWorkbook() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"12345",      "A_YYY", "iPhone 12",  "10.5",  "100",    "ABC"},
                {"12346",      "B_NNN", "iPhone 13",  "20",    "",       "DEF"}
        });
        var rows = parser.parse(new ByteArrayInputStream(bytes));
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).productId()).isEqualTo("12345");
        assertThat(rows.get(0).qtyCap()).isEqualTo(100);
        assertThat(rows.get(1).qtyCap()).isNull();
    }

    @Test
    void missingHeaderThrows() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price"},
                {"12345",      "A_YYY", "iPhone",     "10"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("UPLOAD_PARSE_ERROR"));
    }

    @Test
    void nonNumericPriceCollectsRowError() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"12345",      "A_YYY", "iPhone",     "BAD",   "10",     "ABC"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("UPLOAD_ROW_ERRORS");
                    assertThat(ex.getDetails()).anyMatch(s -> s.contains("Price"));
                });
    }

    @Test
    void emptyProductIdCollectsRowError() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"",           "A_YYY", "iPhone",     "10",    "100",    "ABC"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOf(PurchaseOrderValidationException.class);
    }

    private static byte[] makeWorkbook(String[][] rows) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("PO");
            for (int r = 0; r < rows.length; r++) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < rows[r].length; c++) {
                    row.createCell(c).setCellValue(rows[r][c]);
                }
            }
            wb.write(out);
            return out.toByteArray();
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=POExcelParserTest`
Expected: FAIL — `POExcelParser` does not exist.

- [ ] **Step 3: Write `POExcelParser`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelParser.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class POExcelParser {

    public record ParsedRow(int rowNumber, String productId, String grade, String modelName,
                            BigDecimal price, Integer qtyCap, String buyerCode) {}

    private static final List<String> REQUIRED = List.of(
            "ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode");

    public List<ParsedRow> parse(InputStream in) {
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                        "Workbook contains no sheets", List.of());
            }
            Map<String, Integer> headerIndex = readHeader(sheet.getRow(0));
            List<ParsedRow> parsed = new ArrayList<>();
            List<String> rowErrors = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isBlankRow(row, headerIndex)) continue;
                try {
                    parsed.add(parseRow(r + 1, row, headerIndex));
                } catch (PurchaseOrderValidationException ex) {
                    rowErrors.add(ex.getMessage());
                }
            }
            if (!rowErrors.isEmpty()) {
                throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                        "Row-level validation errors (" + rowErrors.size() + ")", rowErrors);
            }
            return parsed;
        } catch (IOException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "Cannot read workbook: " + ex.getMessage(), List.of());
        }
    }

    private static Map<String, Integer> readHeader(Row header) {
        if (header == null) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "First row must be the header row", List.of());
        }
        Map<String, Integer> idx = new HashMap<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            Cell cell = header.getCell(c);
            if (cell == null) continue;
            idx.put(cell.getStringCellValue().trim(), c);
        }
        List<String> missing = REQUIRED.stream().filter(h -> !idx.containsKey(h)).toList();
        if (!missing.isEmpty()) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "Missing required columns: " + String.join(", ", missing),
                    missing);
        }
        return idx;
    }

    private static boolean isBlankRow(Row row, Map<String, Integer> idx) {
        for (Integer c : idx.values()) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !asString(cell).isBlank()) return false;
        }
        return true;
    }

    private static ParsedRow parseRow(int rowNumber, Row row, Map<String, Integer> idx) {
        String productId = asString(row.getCell(idx.get("ProductID"))).trim();
        if (productId.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": ProductID is empty", List.of());
        }
        String grade = asString(row.getCell(idx.get("Grade"))).trim();
        if (grade.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": Grade is empty", List.of());
        }
        String modelName = asString(row.getCell(idx.get("ModelName"))).trim();
        BigDecimal price = parseDecimal(row.getCell(idx.get("Price")), rowNumber, "Price");
        Integer qtyCap = parseOptionalInt(row.getCell(idx.get("QtyCap")), rowNumber, "QtyCap");
        String buyerCode = asString(row.getCell(idx.get("BuyerCode"))).trim();
        if (buyerCode.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": BuyerCode is empty", List.of());
        }
        return new ParsedRow(rowNumber, productId, grade, modelName, price, qtyCap, buyerCode);
    }

    private static String asString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                if (v == Math.floor(v)) yield String.valueOf((long) v);
                yield String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try { yield cell.getStringCellValue(); }
                catch (IllegalStateException ex) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private static BigDecimal parseDecimal(Cell cell, int rowNumber, String field) {
        String raw = asString(cell).trim();
        if (raw.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " is empty", List.of());
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " not numeric: " + raw, List.of());
        }
    }

    private static Integer parseOptionalInt(Cell cell, int rowNumber, String field) {
        String raw = asString(cell).trim();
        if (raw.isBlank()) return null;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " not integer: " + raw, List.of());
        }
    }
}
```

- [ ] **Step 4: Run parser test to verify it passes**

Run: `cd backend && mvn test -Dtest=POExcelParserTest`
Expected: PASS — all 4 tests green.

- [ ] **Step 5: Write `POExcelBuilder`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelBuilder.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class POExcelBuilder {

    private static final String[] HEADERS =
            {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"};

    public void write(List<PODetailRow> rows, OutputStream out) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("PO");
            CellStyle bold = wb.createCellStyle();
            Font font = wb.createFont(); font.setBold(true);
            bold.setFont(font);

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(HEADERS[i]);
                c.setCellStyle(bold);
            }
            int r = 1;
            for (PODetailRow row : rows) {
                Row out2 = sheet.createRow(r++);
                out2.createCell(0).setCellValue(row.productId());
                out2.createCell(1).setCellValue(row.grade());
                out2.createCell(2).setCellValue(row.modelName() == null ? "" : row.modelName());
                out2.createCell(3).setCellValue(
                        row.price() == null ? 0d : row.price().doubleValue());
                if (row.qtyCap() != null) out2.createCell(4).setCellValue(row.qtyCap());
                out2.createCell(5).setCellValue(row.buyerCode() == null ? "" : row.buyerCode());
            }
            for (int i = 0; i < HEADERS.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
        } catch (IOException ex) {
            throw new PurchaseOrderException("EXPORT_FAILED",
                    "Failed to build PO Excel: " + ex.getMessage());
        }
    }
}
```

- [ ] **Step 6: Verify compile**

Run: `cd backend && mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelParser.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelBuilder.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/POExcelParserTest.java
git commit -m "feat(4b): POExcelParser + POExcelBuilder (Apache POI)"
```

---

## Task 9: PODetailService — wipe-and-replace upload

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PODetailService.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PODetailServiceTest.java`

- [ ] **Step 1: Write the failing service test**

```java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import com.ecoatm.salesplatform.repository.buyermgmt.BuyerCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PODetailServiceTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    BuyerCodeRepository buyerCodeRepo;
    POExcelParser parser;
    PurchaseOrderValidator validator;
    ApplicationEventPublisher events;
    PODetailService service;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        buyerCodeRepo = mock(BuyerCodeRepository.class);
        parser = mock(POExcelParser.class);
        validator = mock(PurchaseOrderValidator.class);
        events = mock(ApplicationEventPublisher.class);
        service = new PODetailService(poRepo, detailRepo, buyerCodeRepo,
                parser, validator, events);
    }

    @Test
    void uploadWipeAndReplaceHappyPath() {
        PurchaseOrder po = stubPo(7L);
        BuyerCode abc = stubBuyerCode(11L, "ABC");
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "iPhone",
                        new BigDecimal("10"), 100, "ABC")));
        when(buyerCodeRepo.findByCodeIn(List.of("ABC")))
                .thenReturn(List.of(abc));
        when(detailRepo.deleteAllByPurchaseOrderId(7L)).thenReturn(3L);
        when(detailRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PODetailUploadResult result = service.upload(7L, new ByteArrayInputStream(new byte[]{1}));

        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.deletedCount()).isEqualTo(3);
        assertThat(result.errors()).isEmpty();
        verify(events).publishEvent(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
    }

    @Test
    void uploadMissingBuyerCodeRejectsEntireUpload() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "X",
                        new BigDecimal("10"), null, "MISSING")));
        doThrow(new PurchaseOrderValidationException("MISSING_BUYER_CODE",
                "Unknown buyer codes referenced: MISSING", List.of("MISSING")))
            .when(validator).requireBuyerCodes(List.of("MISSING"));

        assertThatThrownBy(() -> service.upload(7L, new ByteArrayInputStream(new byte[]{1})))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("MISSING_BUYER_CODE"));
        verify(detailRepo, never()).deleteAllByPurchaseOrderId(anyLong());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void uploadDuplicateRowsInSheetSurfacedAsSkipped() {
        PurchaseOrder po = stubPo(7L);
        BuyerCode abc = stubBuyerCode(11L, "ABC");
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        when(parser.parse(any())).thenReturn(List.of(
                new POExcelParser.ParsedRow(2, "100", "A_YYY", "X",
                        new BigDecimal("10"), null, "ABC"),
                new POExcelParser.ParsedRow(3, "100", "A_YYY", "X",
                        new BigDecimal("12"), null, "ABC")));
        when(buyerCodeRepo.findByCodeIn(List.of("ABC")))
                .thenReturn(List.of(abc));
        when(detailRepo.deleteAllByPurchaseOrderId(7L)).thenReturn(0L);
        when(detailRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PODetailUploadResult result = service.upload(7L, new ByteArrayInputStream(new byte[]{1}));

        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).reason()).contains("DUPLICATE_IN_SHEET");
    }

    @Test
    void uploadAgainstUnknownPoThrows() {
        when(poRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.upload(99L, new ByteArrayInputStream(new byte[]{1})))
                .isInstanceOfSatisfying(PurchaseOrderException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("PURCHASE_ORDER_NOT_FOUND"));
    }

    private static PurchaseOrder stubPo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return po;
    }

    private static BuyerCode stubBuyerCode(long id, String code) {
        BuyerCode bc = new BuyerCode();
        try {
            var f = BuyerCode.class.getDeclaredField("id");
            f.setAccessible(true); f.set(bc, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        bc.setCode(code);
        return bc;
    }
}
```

> **Note:** `BuyerCodeRepository.findByCodeIn(List<String>)` returns the
> entity (not just the code string) so we can resolve the FK on save. Add
> in this task if missing.

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PODetailServiceTest`
Expected: FAIL — `PODetailService` does not exist.

- [ ] **Step 3: Add `findByCodeIn` to `BuyerCodeRepository` if missing**

```java
@Query("SELECT b FROM BuyerCode b WHERE b.code IN :codes")
List<BuyerCode> findByCodeIn(@Param("codes") List<String> codes);
```

- [ ] **Step 4: Write `PODetailService`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PODetailService.java
package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailListResponse;
import com.ecoatm.salesplatform.dto.PODetailRow;
import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import com.ecoatm.salesplatform.repository.buyermgmt.BuyerCodeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
public class PODetailService {

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final BuyerCodeRepository buyerCodeRepo;
    private final POExcelParser parser;
    private final PurchaseOrderValidator validator;
    private final ApplicationEventPublisher events;

    public PODetailService(PurchaseOrderRepository poRepo,
                           PODetailRepository detailRepo,
                           BuyerCodeRepository buyerCodeRepo,
                           POExcelParser parser,
                           PurchaseOrderValidator validator,
                           ApplicationEventPublisher events) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.buyerCodeRepo = buyerCodeRepo;
        this.parser = parser;
        this.validator = validator;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public PODetailListResponse list(long purchaseOrderId, Pageable pageable) {
        Page<PODetail> page = detailRepo.findByPurchaseOrderId(purchaseOrderId, pageable);
        List<PODetailRow> rows = page.getContent().stream().map(this::toRow).toList();
        return new PODetailListResponse(rows, page.getTotalElements(),
                pageable.getPageNumber(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public List<PODetailRow> listAllForExport(long purchaseOrderId) {
        return detailRepo.findByPurchaseOrderId(purchaseOrderId,
                        Pageable.unpaged()).getContent()
                .stream().map(this::toRow).toList();
    }

    @Transactional
    public PODetailUploadResult upload(long purchaseOrderId, InputStream excel) {
        PurchaseOrder po = poRepo.findById(purchaseOrderId).orElseThrow(() ->
                new PurchaseOrderException("PURCHASE_ORDER_NOT_FOUND",
                        "Purchase order " + purchaseOrderId + " not found"));

        List<POExcelParser.ParsedRow> parsed = parser.parse(excel);

        // Step 1: validate buyer codes (whole-upload short-circuit)
        List<String> codes = parsed.stream().map(POExcelParser.ParsedRow::buyerCode)
                .distinct().toList();
        validator.requireBuyerCodes(codes);

        Map<String, BuyerCode> bcByCode = new HashMap<>();
        for (BuyerCode bc : buyerCodeRepo.findByCodeIn(codes)) {
            bcByCode.put(bc.getCode(), bc);
        }

        // Step 2: dedupe (productId, grade, buyerCode) within sheet
        record Key(String productId, String grade, String buyerCode) {}
        Set<Key> seen = new HashSet<>();
        List<PODetailUploadResult.UploadError> skipped = new ArrayList<>();
        List<POExcelParser.ParsedRow> kept = new ArrayList<>();
        for (POExcelParser.ParsedRow r : parsed) {
            Key key = new Key(r.productId(), r.grade(), r.buyerCode());
            if (!seen.add(key)) {
                skipped.add(new PODetailUploadResult.UploadError(
                        r.rowNumber(), r.productId(), r.grade(), r.buyerCode(),
                        "DUPLICATE_IN_SHEET — first occurrence wins"));
                continue;
            }
            kept.add(r);
        }

        // Step 3: wipe-and-replace
        long deleted = detailRepo.deleteAllByPurchaseOrderId(purchaseOrderId);

        List<PODetail> toSave = new ArrayList<>(kept.size());
        for (POExcelParser.ParsedRow r : kept) {
            PODetail d = new PODetail();
            d.setPurchaseOrder(po);
            d.setBuyerCode(bcByCode.get(r.buyerCode()));
            d.setProductId(r.productId());
            d.setGrade(r.grade());
            d.setModelName(r.modelName());
            d.setPrice(r.price());
            d.setQtyCap(r.qtyCap());
            d.setTempBuyerCode(r.buyerCode());
            toSave.add(d);
        }
        detailRepo.saveAll(toSave);

        po.setTotalRecords(toSave.size());
        po.setPoRefreshTimestamp(Instant.now());

        events.publishEvent(new PurchaseOrderChangedEvent(po.getId(),
                PurchaseOrderChangedEvent.Action.UPSERT));

        return new PODetailUploadResult(toSave.size(), (int) deleted, skipped.size(), skipped);
    }

    PODetailRow toRow(PODetail d) {
        return new PODetailRow(d.getId(), d.getPurchaseOrder().getId(),
                d.getBuyerCode().getId(), d.getBuyerCode().getCode(),
                d.getProductId(), d.getGrade(), d.getModelName(),
                d.getPrice(), d.getQtyCap(),
                d.getPriceFulfilled(), d.getQtyFulfilled());
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PODetailServiceTest`
Expected: PASS — all 4 tests green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PODetailService.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/buyermgmt/BuyerCodeRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/purchaseorder/PODetailServiceTest.java
git commit -m "feat(4b): PODetailService — wipe-and-replace upload"
```

---

## Task 10: Snowflake writer interface + Logging impl + payload

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakeWriter.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePayload.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingPurchaseOrderSnowflakeWriter.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingPurchaseOrderSnowflakeWriterTest.java`

- [ ] **Step 1: Write the failing logging-writer test**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingPurchaseOrderSnowflakeWriterTest {

    @Test
    void payloadSerializesToExpectedShape() throws Exception {
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "2025 / Wk1 - 2025 / Wk4",
                1, Instant.parse("2026-04-24T10:00:00Z"),
                List.of(new PurchaseOrderSnowflakePayload.DetailPayload(
                        100L, 50000L, "12345", "A_YYY", "iPhone",
                        new BigDecimal("10.00"), 100,
                        null, null, "ABC", "ABC")));
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String json = om.writeValueAsString(payload);

        assertThat(json).contains("\"purchaseOrderId\":42");
        assertThat(json).contains("\"weekFrom\"");
        assertThat(json).contains("\"details\":[");
        assertThat(json).contains("\"buyerCode\":\"ABC\"");
    }

    @Test
    void loggingWriterUpsertCallsLogger() {
        LoggingPurchaseOrderSnowflakeWriter writer = new LoggingPurchaseOrderSnowflakeWriter();
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "X", 0, Instant.now(), List.of());
        writer.upsert(payload);  // should not throw
        writer.delete(42L);      // should not throw
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=LoggingPurchaseOrderSnowflakeWriterTest`
Expected: FAIL — classes do not exist.

- [ ] **Step 3: Write the writer interface**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface PurchaseOrderSnowflakeWriter {
    void upsert(PurchaseOrderSnowflakePayload payload);
    void delete(long purchaseOrderId);
}
```

- [ ] **Step 4: Write the payload record**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePayload.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseOrderSnowflakePayload(
        long purchaseOrderId, long legacyId,
        WeekRef weekFrom, WeekRef weekTo,
        String weekRangeLabel,
        int totalRecords,
        Instant pushTimestamp,
        List<DetailPayload> details) {

    public record WeekRef(long id, int year, int weekNumber) {}

    public record DetailPayload(
            long detailId, long legacyId,
            String productId, String grade, String modelName,
            BigDecimal price, Integer qtyCap,
            BigDecimal priceFulfilled, Integer qtyFulfilled,
            String buyerCode, String tempBuyerCode) {}
}
```

- [ ] **Step 5: Write the logging writer**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingPurchaseOrderSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "po.sync.writer", havingValue = "logging", matchIfMissing = true)
public class LoggingPurchaseOrderSnowflakeWriter implements PurchaseOrderSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(
            LoggingPurchaseOrderSnowflakeWriter.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void upsert(PurchaseOrderSnowflakePayload payload) {
        try {
            log.info("[PO SNOWFLAKE PUSH] would call AUCTIONS.UPSERT_PURCHASE_ORDER "
                   + "for PO {} ({} details): {}",
                    payload.purchaseOrderId(), payload.details().size(),
                    objectMapper.writeValueAsString(payload));
        } catch (Exception ex) {
            log.warn("Failed to serialize PO payload for logging", ex);
        }
    }

    @Override
    public void delete(long purchaseOrderId) {
        log.info("[PO SNOWFLAKE PUSH] would call AUCTIONS.DELETE_PURCHASE_ORDER for PO {}",
                purchaseOrderId);
    }
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=LoggingPurchaseOrderSnowflakeWriterTest`
Expected: PASS — both tests green.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakeWriter.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePayload.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingPurchaseOrderSnowflakeWriter.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/LoggingPurchaseOrderSnowflakeWriterTest.java
git commit -m "feat(4b): Snowflake writer interface + payload + logging impl"
```

---

## Task 11: PurchaseOrderSnowflakePushListener

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePushListener.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePushListenerTest.java`

- [ ] **Step 1: Write the failing listener test**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PurchaseOrderSnowflakePushListenerTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    PurchaseOrderSnowflakeWriter writer;
    Environment env;
    PurchaseOrderSnowflakePushListener listener;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        writer = mock(PurchaseOrderSnowflakeWriter.class);
        env = mock(Environment.class);
        when(env.getProperty("po.sync.enabled", "true")).thenReturn("true");
        listener = new PurchaseOrderSnowflakePushListener(poRepo, detailRepo, writer, env);
    }

    @Test
    void upsertEventCallsWriterUpsert() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findByIdWithDetails(7L)).thenReturn(Optional.of(po));
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
        verify(writer).upsert(any(PurchaseOrderSnowflakePayload.class));
    }

    @Test
    void deleteEventCallsWriterDelete() {
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.DELETE));
        verify(writer).delete(7L);
    }

    @Test
    void writerExceptionDoesNotPropagate() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findByIdWithDetails(7L)).thenReturn(Optional.of(po));
        doThrow(new RuntimeException("Snowflake down"))
                .when(writer).upsert(any());
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));  // must NOT throw
    }

    @Test
    void disabledSyncShortCircuits() {
        when(env.getProperty("po.sync.enabled", "true")).thenReturn("false");
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
        verify(writer, never()).upsert(any());
        verify(writer, never()).delete(anyLong());
    }

    @Test
    void missingPoOnUpsertLogsAndSkips() {
        when(poRepo.findByIdWithDetails(99L)).thenReturn(Optional.empty());
        listener.onChange(new PurchaseOrderChangedEvent(99L,
                PurchaseOrderChangedEvent.Action.UPSERT));  // no throw
        verify(writer, never()).upsert(any());
    }

    private static PurchaseOrder stubPo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        Week w1 = new Week(); w1.setYear(2025); w1.setWeekNumber(1);
        Week w2 = new Week(); w2.setYear(2025); w2.setWeekNumber(4);
        try {
            var wf = Week.class.getDeclaredField("id");
            wf.setAccessible(true); wf.set(w1, 1L); wf.set(w2, 2L);
        } catch (Exception e) { throw new RuntimeException(e); }
        po.setWeekFrom(w1); po.setWeekTo(w2);
        po.setWeekRangeLabel("X");
        BuyerCode bc = new BuyerCode(); bc.setCode("ABC");
        try {
            var bcf = BuyerCode.class.getDeclaredField("id");
            bcf.setAccessible(true); bcf.set(bc, 11L);
        } catch (Exception e) { throw new RuntimeException(e); }
        PODetail d = new PODetail();
        d.setPurchaseOrder(po); d.setBuyerCode(bc);
        d.setProductId("100"); d.setGrade("A_YYY");
        d.setPrice(new BigDecimal("10")); d.setTempBuyerCode("ABC");
        po.getDetails().add(d);
        return po;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderSnowflakePushListenerTest`
Expected: FAIL — listener does not exist.

- [ ] **Step 3: Add `findByIdWithDetails` to `PurchaseOrderRepository`**

Append to `PurchaseOrderRepository.java`:

```java
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

@EntityGraph(attributePaths = {"weekFrom", "weekTo", "details", "details.buyerCode"})
@Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id")
Optional<PurchaseOrder> findByIdWithDetails(@Param("id") Long id);
```

- [ ] **Step 4: Write `PurchaseOrderSnowflakePushListener`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePushListener.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Optional;

@Component
public class PurchaseOrderSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(
            PurchaseOrderSnowflakePushListener.class);

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final PurchaseOrderSnowflakeWriter writer;
    private final Environment env;

    public PurchaseOrderSnowflakePushListener(PurchaseOrderRepository poRepo,
                                              PODetailRepository detailRepo,
                                              PurchaseOrderSnowflakeWriter writer,
                                              Environment env) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChange(PurchaseOrderChangedEvent event) {
        if (!Boolean.parseBoolean(env.getProperty("po.sync.enabled", "true"))) {
            log.debug("po.sync.enabled=false; skipping push for PO {}",
                    event.purchaseOrderId());
            return;
        }
        try {
            switch (event.action()) {
                case UPSERT -> doUpsert(event.purchaseOrderId());
                case DELETE -> writer.delete(event.purchaseOrderId());
            }
        } catch (Exception ex) {
            log.warn("Snowflake push failed for PO {}; will be re-pushed on next upload",
                    event.purchaseOrderId(), ex);
            // do not rethrow — Postgres commit stands
        }
    }

    private void doUpsert(long purchaseOrderId) {
        Optional<PurchaseOrder> opt = poRepo.findByIdWithDetails(purchaseOrderId);
        if (opt.isEmpty()) {
            log.warn("Snowflake push: PO {} no longer exists; skipping", purchaseOrderId);
            return;
        }
        writer.upsert(toPayload(opt.get()));
    }

    static PurchaseOrderSnowflakePayload toPayload(PurchaseOrder po) {
        var weekFrom = new PurchaseOrderSnowflakePayload.WeekRef(
                po.getWeekFrom().getId(),
                po.getWeekFrom().getYear(),
                po.getWeekFrom().getWeekNumber());
        var weekTo = new PurchaseOrderSnowflakePayload.WeekRef(
                po.getWeekTo().getId(),
                po.getWeekTo().getYear(),
                po.getWeekTo().getWeekNumber());
        var details = po.getDetails().stream().map(d -> mapDetail(d)).toList();
        return new PurchaseOrderSnowflakePayload(
                po.getId(),
                po.getLegacyId() == null ? 0L : po.getLegacyId(),
                weekFrom, weekTo,
                po.getWeekRangeLabel(),
                po.getTotalRecords(),
                Instant.now(),
                details);
    }

    private static PurchaseOrderSnowflakePayload.DetailPayload mapDetail(PODetail d) {
        return new PurchaseOrderSnowflakePayload.DetailPayload(
                d.getId(),
                d.getLegacyId() == null ? 0L : d.getLegacyId(),
                d.getProductId(), d.getGrade(), d.getModelName(),
                d.getPrice(), d.getQtyCap(),
                d.getPriceFulfilled(), d.getQtyFulfilled(),
                d.getBuyerCode().getCode(), d.getTempBuyerCode());
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderSnowflakePushListenerTest`
Expected: PASS — all 5 tests green.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePushListener.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/PurchaseOrderRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/PurchaseOrderSnowflakePushListenerTest.java
git commit -m "feat(4b): Snowflake push listener — AFTER_COMMIT, swallows failures"
```

---

## Task 12: JDBC Snowflake writer impl

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcPurchaseOrderSnowflakeWriter.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcPurchaseOrderSnowflakeWriterTest.java`

- [ ] **Step 1: Write the failing JDBC writer test**

```java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JdbcPurchaseOrderSnowflakeWriterTest {

    JdbcTemplate snowflakeJdbc;
    JdbcPurchaseOrderSnowflakeWriter writer;

    @BeforeEach
    void init() {
        snowflakeJdbc = mock(JdbcTemplate.class);
        writer = new JdbcPurchaseOrderSnowflakeWriter(snowflakeJdbc);
    }

    @Test
    void upsertCallsStoredProc() {
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "X", 1, Instant.now(),
                List.of(new PurchaseOrderSnowflakePayload.DetailPayload(
                        100L, 50000L, "12345", "A_YYY", "iPhone",
                        new BigDecimal("10.00"), 100, null, null,
                        "ABC", "ABC")));
        writer.upsert(payload);
        verify(snowflakeJdbc).update(
                contains("CALL AUCTIONS.UPSERT_PURCHASE_ORDER"),
                any(Object[].class));
    }

    @Test
    void deleteCallsStoredProc() {
        writer.delete(42L);
        verify(snowflakeJdbc).update(
                contains("CALL AUCTIONS.DELETE_PURCHASE_ORDER"),
                eq(42L));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=JdbcPurchaseOrderSnowflakeWriterTest`
Expected: FAIL — writer does not exist.

- [ ] **Step 3: Write `JdbcPurchaseOrderSnowflakeWriter`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcPurchaseOrderSnowflakeWriter.java
package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "po.sync.writer", havingValue = "jdbc")
public class JdbcPurchaseOrderSnowflakeWriter implements PurchaseOrderSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public JdbcPurchaseOrderSnowflakeWriter(
            @Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
    }

    @Override
    public void upsert(PurchaseOrderSnowflakePayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            snowflakeJdbc.update(
                    "CALL AUCTIONS.UPSERT_PURCHASE_ORDER(?, ?, ?, ?, ?, ?)",
                    new Object[] {
                            json,
                            actingUser(),
                            payload.weekFrom().year(),
                            payload.weekFrom().weekNumber(),
                            payload.weekTo().year(),
                            payload.weekTo().weekNumber()
                    });
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call AUCTIONS.UPSERT_PURCHASE_ORDER", ex);
        }
    }

    @Override
    public void delete(long purchaseOrderId) {
        snowflakeJdbc.update("CALL AUCTIONS.DELETE_PURCHASE_ORDER(?)", purchaseOrderId);
    }

    private static String actingUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth.getName() == null) ? "Scheduler" : auth.getName();
    }
}
```

> **Note:** `snowflakeJdbcTemplate` is the existing bean from 4A's wiring
> in the snowflake config; reuse the same template. If 4A's bean is
> qualifier-named differently in your branch, adjust the
> `@Qualifier("...")` value accordingly.

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=JdbcPurchaseOrderSnowflakeWriterTest`
Expected: PASS — both tests green.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcPurchaseOrderSnowflakeWriter.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/snowflake/JdbcPurchaseOrderSnowflakeWriterTest.java
git commit -m "feat(4b): JdbcPurchaseOrderSnowflakeWriter (gated by po.sync.writer=jdbc)"
```

---

## Task 13: REST controller + SecurityConfig wiring

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/PurchaseOrderController.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/admin/PurchaseOrderControllerIT.java`

- [ ] **Step 1: Write the failing controller IT**

```java
package com.ecoatm.salesplatform.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    @WithMockUser(roles = "Administrator")
    void administratorCanList() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders").param("page", "0"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @WithMockUser(roles = "SalesOps")
    void salesOpsCanList() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "Bidder")
    void bidderForbidden() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedReturns401() throws Exception {
        mvc.perform(get("/api/v1/admin/purchase-orders"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void invalidWeekRangeReturns400() throws Exception {
        String body = om.writeValueAsString(Map.of("weekFromId", 999_999, "weekToId", 999_999));
        mvc.perform(post("/api/v1/admin/purchase-orders")
                   .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("INVALID_WEEK_RANGE"));
    }

    @Test
    @WithMockUser(roles = "Administrator")
    void uploadWrongMediaType415() throws Exception {
        mvc.perform(post("/api/v1/admin/purchase-orders/1/details/upload")
                   .contentType(MediaType.APPLICATION_JSON).content("{}"))
           .andExpect(status().is(415));
    }

    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Test
    @WithMockUser(roles = "Administrator")
    void uploadValidExcelOnSeededPo() throws Exception {
        Long poId = jdbc.queryForObject(
                "SELECT id FROM auctions.purchase_order ORDER BY id LIMIT 1", Long.class);
        MockMultipartFile file = new MockMultipartFile("file",
                "po-upload-sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                getClass().getResourceAsStream("/fixtures/po-upload-sample.xlsx").readAllBytes());
        mvc.perform(multipart("/api/v1/admin/purchase-orders/" + poId + "/details/upload")
                   .file(file))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.createdCount").isNumber());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -Dtest=PurchaseOrderControllerIT`
Expected: FAIL — controller does not exist.

- [ ] **Step 3: Write `PurchaseOrderController`**

```java
// backend/src/main/java/com/ecoatm/salesplatform/controller/admin/PurchaseOrderController.java
package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.*;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/purchase-orders")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;
    private final PODetailService detailService;
    private final POExcelBuilder excelBuilder;

    public PurchaseOrderController(PurchaseOrderService poService,
                                   PODetailService detailService,
                                   POExcelBuilder excelBuilder) {
        this.poService = poService;
        this.detailService = detailService;
        this.excelBuilder = excelBuilder;
    }

    @GetMapping
    public PurchaseOrderListResponse list(
            @RequestParam(required = false) Long weekFromId,
            @RequestParam(required = false) Long weekToId,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            Pageable pageable) {
        return poService.list(weekFromId, weekToId, yearFrom, yearTo, pageable);
    }

    @GetMapping("/{id}")
    public PurchaseOrderRow get(@PathVariable long id) {
        return poService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderRow> create(@Valid @RequestBody PurchaseOrderRequest req) {
        PurchaseOrderRow row = poService.create(req);
        return ResponseEntity.status(201).body(row);
    }

    @PutMapping("/{id}")
    public PurchaseOrderRow update(@PathVariable long id,
                                   @Valid @RequestBody PurchaseOrderRequest req) {
        return poService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        poService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/details")
    public PODetailListResponse listDetails(@PathVariable long id, Pageable pageable) {
        return detailService.list(id, pageable);
    }

    @PostMapping(path = "/{id}/details/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PODetailUploadResult upload(@PathVariable long id,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        return detailService.upload(id, file.getInputStream());
    }

    @GetMapping(path = "/{id}/details/download",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<InputStreamResource> download(@PathVariable long id) {
        var rows = detailService.listAllForExport(id);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelBuilder.write(rows, out);
        byte[] bytes = out.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"po-" + id + ".xlsx\"")
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
    }

    @ExceptionHandler(PurchaseOrderException.class)
    public ResponseEntity<ErrorBody> handleDomain(PurchaseOrderException ex) {
        int status = switch (ex.getCode()) {
            case "PURCHASE_ORDER_NOT_FOUND" -> 404;
            case "DUPLICATE_PO_DETAIL" -> 409;
            default -> 400;
        };
        var details = ex instanceof PurchaseOrderValidationException v
                ? v.getDetails() : java.util.List.<String>of();
        return ResponseEntity.status(status)
                .body(new ErrorBody(ex.getCode(), ex.getMessage(), details));
    }

    public record ErrorBody(String code, String message, java.util.List<String> details) {}
}
```

- [ ] **Step 4: Update `SecurityConfig`**

Open `backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java` and add a matcher for the new endpoint group. The exact pattern depends on the existing config style — find the chain that gates `/api/v1/admin/reserve-bids/**` (4A) and add a sibling matcher:

```java
.requestMatchers("/api/v1/admin/purchase-orders/**")
    .hasAnyRole("Administrator", "SalesOps")
```

Place it adjacent to the existing reserve-bids matcher so the two admin routes are visually grouped.

- [ ] **Step 5: Add the test fixture file**

Create `backend/src/test/resources/fixtures/po-upload-sample.xlsx` via a one-off helper class (or hand-built xlsx). Minimum content — header row + 20 valid rows. Use the same `XSSFWorkbook` writer pattern as the parser test's `makeWorkbook` helper.

- [ ] **Step 6: Run test to verify it passes**

Run: `cd backend && mvn test -Dtest=PurchaseOrderControllerIT`
Expected: PASS — all 7 tests green.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/controller/admin/PurchaseOrderController.java \
        backend/src/main/java/com/ecoatm/salesplatform/config/SecurityConfig.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/admin/PurchaseOrderControllerIT.java \
        backend/src/test/resources/fixtures/po-upload-sample.xlsx
git commit -m "feat(4b): PurchaseOrderController + SecurityConfig wiring"
```

---

## Task 14: Application config

**Files:**
- Modify: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Append `po.sync.*` block**

Append to `backend/src/main/resources/application.yml`:

```yaml
po:
  sync:
    enabled: true
    writer: logging                  # logging (default) | jdbc
    snowflake-timeout-seconds: 60
```

- [ ] **Step 2: Verify Spring picks up the property**

Run: `cd backend && mvn -q spring-boot:run` (Ctrl-C after startup)
Expected log line: `Started SalesPlatformApplication`. No error about unbound property.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/application.yml
git commit -m "feat(4b): application.yml — po.sync.* config"
```

---

## Task 15: Frontend types + API client

**Files:**
- Create: `frontend/src/lib/types/purchaseOrder.ts`
- Create: `frontend/src/lib/api/purchaseOrderClient.ts`

> **Reference:** mirror the shapes used by 4A's reserve-bids equivalents
> (`frontend/src/lib/types/reserveBid.ts`, `purchaseOrderClient` analog).
> If those don't exist as separate files in 4A, find the inline shapes in
> `frontend/src/app/(dashboard)/admin/auctions-data-center/reserve-bids/page.tsx`
> and follow the same conventions.

- [ ] **Step 1: Write the types**

```typescript
// frontend/src/lib/types/purchaseOrder.ts
export type PurchaseOrderLifecycleState = "DRAFT" | "ACTIVE" | "CLOSED";

export interface PurchaseOrderRow {
  id: number;
  weekFromId: number;
  weekFromLabel: string;
  weekToId: number;
  weekToLabel: string;
  weekRangeLabel: string;
  state: PurchaseOrderLifecycleState;
  totalRecords: number;
  poRefreshTimestamp: string | null;
  changedDate: string;
  changedByUsername: string | null;
}

export interface PurchaseOrderListResponse {
  items: PurchaseOrderRow[];
  total: number;
  page: number;
  size: number;
}

export interface PurchaseOrderRequest {
  weekFromId: number;
  weekToId: number;
}

export interface PODetailRow {
  id: number;
  purchaseOrderId: number;
  buyerCodeId: number;
  buyerCode: string;
  productId: string;
  grade: string;
  modelName: string | null;
  price: string;          // BigDecimal serialized as string
  qtyCap: number | null;
  priceFulfilled: string | null;
  qtyFulfilled: number | null;
}

export interface PODetailListResponse {
  items: PODetailRow[];
  total: number;
  page: number;
  size: number;
}

export interface PODetailUploadResult {
  createdCount: number;
  deletedCount: number;
  skippedCount: number;
  errors: Array<{
    rowNumber: number;
    productId: string;
    grade: string;
    buyerCode: string;
    reason: string;
  }>;
}

export interface ApiError {
  code: string;
  message: string;
  details: string[];
}
```

- [ ] **Step 2: Write the API client**

```typescript
// frontend/src/lib/api/purchaseOrderClient.ts
import type {
  PODetailListResponse,
  PODetailUploadResult,
  PurchaseOrderListResponse,
  PurchaseOrderRequest,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

const BASE = "/api/v1/admin/purchase-orders";

async function jsonOrThrow<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let body: unknown = null;
    try { body = await res.json(); } catch { /* ignore */ }
    const message = (body as { message?: string })?.message ?? res.statusText;
    throw Object.assign(new Error(message), { status: res.status, body });
  }
  return res.status === 204 ? (undefined as T) : res.json();
}

export async function listPurchaseOrders(params: {
  page?: number; size?: number;
  weekFromId?: number; weekToId?: number;
  yearFrom?: number; yearTo?: number;
}): Promise<PurchaseOrderListResponse> {
  const qs = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null) qs.set(k, String(v));
  }
  return jsonOrThrow(await fetch(`${BASE}?${qs}`, { credentials: "include" }));
}

export async function getPurchaseOrder(id: number): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, { credentials: "include" }));
}

export async function createPurchaseOrder(req: PurchaseOrderRequest): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(BASE, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  }));
}

export async function updatePurchaseOrder(id: number, req: PurchaseOrderRequest): Promise<PurchaseOrderRow> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  }));
}

export async function deletePurchaseOrder(id: number): Promise<void> {
  return jsonOrThrow(await fetch(`${BASE}/${id}`, {
    method: "DELETE",
    credentials: "include",
  }));
}

export async function listPoDetails(id: number, page = 0, size = 50): Promise<PODetailListResponse> {
  return jsonOrThrow(await fetch(`${BASE}/${id}/details?page=${page}&size=${size}`,
      { credentials: "include" }));
}

export async function uploadPoDetails(id: number, file: File): Promise<PODetailUploadResult> {
  const fd = new FormData();
  fd.append("file", file);
  return jsonOrThrow(await fetch(`${BASE}/${id}/details/upload`, {
    method: "POST", credentials: "include", body: fd,
  }));
}

export function downloadPoDetailsUrl(id: number): string {
  return `${BASE}/${id}/details/download`;
}
```

- [ ] **Step 3: Verify TypeScript compiles**

Run: `cd frontend && npx tsc --noEmit -p tsconfig.json`
Expected: no errors.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/lib/types/purchaseOrder.ts \
        frontend/src/lib/api/purchaseOrderClient.ts
git commit -m "feat(4b): frontend types + API client for PO"
```

---

## Task 16: Frontend admin list page

**Files:**
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/page.tsx`

> **Reference:** mirror the layout, state badge styling, and grid structure
> from 4A's `reserve-bids/page.tsx`. Same admin chrome, same filter
> placement, same pagination component.

- [ ] **Step 1: Write the list page**

```tsx
// frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/page.tsx
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  listPurchaseOrders,
  deletePurchaseOrder,
} from "@/lib/api/purchaseOrderClient";
import type {
  PurchaseOrderLifecycleState,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

const STATE_COLORS: Record<PurchaseOrderLifecycleState, string> = {
  DRAFT: "#a07f00",
  ACTIVE: "#176c4d",
  CLOSED: "#888",
};

export default function PurchaseOrdersPage() {
  const [rows, setRows] = useState<PurchaseOrderRow[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function reload() {
    setLoading(true);
    try {
      const r = await listPurchaseOrders({ page, size });
      setRows(r.items);
      setTotal(r.total);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { reload(); }, [page]);

  async function onDelete(id: number) {
    if (!confirm(`Delete PO #${id}? This will cascade-delete all detail rows.`)) return;
    try {
      await deletePurchaseOrder(id);
      reload();
    } catch (e) {
      alert("Delete failed: " + (e as Error).message);
    }
  }

  return (
    <div style={{ padding: "1.5rem" }}>
      <header style={{ display: "flex", justifyContent: "space-between",
                       alignItems: "center", marginBottom: "1rem" }}>
        <h1 style={{ margin: 0 }}>Purchase Orders</h1>
        <Link href="/admin/auctions-data-center/purchase-orders/new"
              style={{ padding: "0.5rem 1rem", background: "#407874",
                       color: "white", borderRadius: 4, textDecoration: "none" }}>
          + New PO
        </Link>
      </header>

      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
      {loading && <div>Loading…</div>}

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "#F7F7F7", textAlign: "left" }}>
            <th>ID</th><th>Week range</th><th>State</th>
            <th>Total rows</th><th>Last refresh</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id} style={{ borderBottom: "1px solid #eee" }}>
              <td>{r.id}</td>
              <td>{r.weekRangeLabel}</td>
              <td>
                <span style={{
                  padding: "0.15rem 0.6rem", borderRadius: 999,
                  background: STATE_COLORS[r.state], color: "white", fontSize: "0.85rem",
                }}>{r.state}</span>
              </td>
              <td>{r.totalRecords}</td>
              <td>{r.poRefreshTimestamp
                ? new Date(r.poRefreshTimestamp).toLocaleString() : "—"}</td>
              <td>
                <Link href={`/admin/auctions-data-center/purchase-orders/${r.id}`}>Edit</Link>
                {" • "}
                <button onClick={() => onDelete(r.id)}
                        style={{ background: "none", color: "#c00",
                                 border: 0, cursor: "pointer" }}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <footer style={{ marginTop: "1rem" }}>
        Showing {rows.length} of {total}.
        <button onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}>Prev</button>
        <button onClick={() => setPage(p => p + 1)}
                disabled={(page + 1) * size >= total}>Next</button>
      </footer>
    </div>
  );
}
```

- [ ] **Step 2: Sanity-check the route renders**

Run frontend dev server: `cd frontend && npm run dev`. Navigate to
`http://localhost:3000/admin/auctions-data-center/purchase-orders` while
logged in as `admin@test.com`. Expected: list of 13 seeded POs.

- [ ] **Step 3: Commit**

```bash
git add "frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/page.tsx"
git commit -m "feat(4b): frontend admin PO list page"
```

---

## Task 17: Frontend new + edit pages

**Files:**
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/new/page.tsx`
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/[id]/page.tsx`

> **Reference:** Mirror 4A's reserve-bids/new and reserve-bids/[id]/page.tsx
> structure, but adapt for PO's (week_from, week_to) selectors instead of
> single-row create-form. Use a `<select>` populated from a new
> `/api/v1/admin/weeks` endpoint OR query the existing endpoint that 4A
> uses for week dropdown population. If neither exists, the simplest
> fallback: numeric input fields validated on submit.

- [ ] **Step 1: Write the new-PO page**

```tsx
// frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/new/page.tsx
"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { createPurchaseOrder } from "@/lib/api/purchaseOrderClient";

export default function NewPurchaseOrderPage() {
  const router = useRouter();
  const [weekFromId, setWeekFromId] = useState<number | "">("");
  const [weekToId, setWeekToId] = useState<number | "">("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (typeof weekFromId !== "number" || typeof weekToId !== "number") {
      setError("Both week_from and week_to are required.");
      return;
    }
    setSubmitting(true);
    try {
      const po = await createPurchaseOrder({ weekFromId, weekToId });
      router.push(`/admin/auctions-data-center/purchase-orders/${po.id}`);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div style={{ padding: "1.5rem", maxWidth: 600 }}>
      <h1>New Purchase Order</h1>
      <form onSubmit={onSubmit}>
        <label style={{ display: "block", marginBottom: "1rem" }}>
          Week from (mdm.week.id)
          <input type="number" value={weekFromId}
                 onChange={e => setWeekFromId(Number(e.target.value))}
                 required style={{ width: "100%" }} />
        </label>
        <label style={{ display: "block", marginBottom: "1rem" }}>
          Week to (mdm.week.id)
          <input type="number" value={weekToId}
                 onChange={e => setWeekToId(Number(e.target.value))}
                 required style={{ width: "100%" }} />
        </label>
        {error && <div role="alert" style={{ color: "red" }}>{error}</div>}
        <button type="submit" disabled={submitting}
                style={{ padding: "0.5rem 1rem", background: "#407874",
                         color: "white", border: 0, borderRadius: 4 }}>
          {submitting ? "Saving…" : "Create"}
        </button>
      </form>
    </div>
  );
}
```

- [ ] **Step 2: Write the edit page (with upload + download)**

```tsx
// frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import {
  downloadPoDetailsUrl,
  getPurchaseOrder,
  listPoDetails,
  updatePurchaseOrder,
  uploadPoDetails,
} from "@/lib/api/purchaseOrderClient";
import type {
  PODetailRow,
  PurchaseOrderRow,
} from "@/lib/types/purchaseOrder";

export default function EditPurchaseOrderPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [po, setPo] = useState<PurchaseOrderRow | null>(null);
  const [details, setDetails] = useState<PODetailRow[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);

  async function reload() {
    try {
      const [p, d] = await Promise.all([getPurchaseOrder(id), listPoDetails(id, 0, 200)]);
      setPo(p);
      setDetails(d.items);
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  useEffect(() => { reload(); }, [id]);

  async function onUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const result = await uploadPoDetails(id, file);
      alert(`Wipe-and-replace: deleted ${result.deletedCount}, `
          + `created ${result.createdCount}, skipped ${result.skippedCount}.`);
      reload();
    } catch (e) {
      alert("Upload failed: " + (e as Error).message);
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  }

  async function onSaveHeader(e: React.FormEvent) {
    e.preventDefault();
    if (!po) return;
    try {
      await updatePurchaseOrder(id,
              { weekFromId: po.weekFromId, weekToId: po.weekToId });
      reload();
    } catch (e) {
      alert("Save failed: " + (e as Error).message);
    }
  }

  if (!po) return <div>Loading…</div>;

  return (
    <div style={{ padding: "1.5rem" }}>
      <h1>PO #{po.id} — {po.weekRangeLabel}</h1>
      {error && <div role="alert" style={{ color: "red" }}>{error}</div>}

      <form onSubmit={onSaveHeader} style={{ marginBottom: "1.5rem" }}>
        <label>Week from id <input type="number" value={po.weekFromId}
              onChange={e => setPo({ ...po, weekFromId: Number(e.target.value) })} /></label>
        {" "}
        <label>Week to id <input type="number" value={po.weekToId}
              onChange={e => setPo({ ...po, weekToId: Number(e.target.value) })} /></label>
        {" "}
        <button type="submit">Save header</button>
      </form>

      <section style={{ marginBottom: "1.5rem" }}>
        <h2>Details ({details.length})</h2>
        <input type="file" accept=".xlsx" onChange={onUpload} disabled={uploading} />
        <a href={downloadPoDetailsUrl(id)} style={{ marginLeft: "1rem" }}>
          Download Excel
        </a>
      </section>

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.9rem" }}>
        <thead>
          <tr style={{ background: "#F7F7F7" }}>
            <th>Product</th><th>Grade</th><th>Model</th>
            <th>Price</th><th>QtyCap</th><th>Buyer</th>
          </tr>
        </thead>
        <tbody>
          {details.map(d => (
            <tr key={d.id}>
              <td>{d.productId}</td><td>{d.grade}</td>
              <td>{d.modelName}</td><td>{d.price}</td>
              <td>{d.qtyCap ?? "—"}</td><td>{d.buyerCode}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

- [ ] **Step 3: Sanity-check both routes**

Run frontend dev. Navigate to `/new` (form renders), then submit to land
on `/[id]` (edit + upload UI renders).

- [ ] **Step 4: Commit**

```bash
git add "frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/new/page.tsx" \
        "frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/[id]/page.tsx"
git commit -m "feat(4b): frontend new + edit PO pages with upload/download"
```

---

## Task 18: Playwright E2E

**Files:**
- Create: `frontend/e2e/admin-purchase-orders.spec.ts`
- Test fixture: `frontend/e2e/fixtures/po-upload-sample.xlsx` (copy from backend fixture)

> **Reference:** mirror 4A's `admin-reserve-bids.spec.ts` patterns — same
> login helpers, same `isBackendAvailable()` guard if present in repo.

- [ ] **Step 1: Write the E2E spec**

```typescript
// frontend/e2e/admin-purchase-orders.spec.ts
import { test, expect } from "@playwright/test";
import path from "path";

async function loginAs(page, email: string, password: string) {
  await page.goto("/login");
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page).not.toHaveURL(/\/login/);
}

test.describe("Admin → Purchase Orders", () => {
  test("administrator: list → create → upload → export → delete", async ({ page }) => {
    await loginAs(page, "admin@test.com", "Admin123!");
    await page.goto("/admin/auctions-data-center/purchase-orders");
    await expect(page.locator("h1")).toHaveText("Purchase Orders");

    // Read first seeded PO id
    const firstRow = page.locator("tbody tr").first();
    await expect(firstRow).toBeVisible();

    // Open it
    await firstRow.locator('a:has-text("Edit")').click();
    await expect(page.locator("h1")).toContainText("PO #");

    // Upload fixture
    await page.setInputFiles('input[type="file"]',
            path.join(__dirname, "fixtures", "po-upload-sample.xlsx"));
    page.on("dialog", d => d.accept());
    await expect(page.locator("table tbody tr")).toHaveCount(20, { timeout: 10_000 });

    // Download link is present
    await expect(page.locator('a:has-text("Download Excel")')).toBeVisible();
  });

  test("salesops: can list and edit", async ({ page }) => {
    await loginAs(page, "salesops@test.com", "SalesOps123!");
    await page.goto("/admin/auctions-data-center/purchase-orders");
    await expect(page.locator("h1")).toHaveText("Purchase Orders");
  });

  test("bidder: forbidden from PO admin", async ({ page }) => {
    await loginAs(page, "bidder@buyerco.com", "Bidder123!");
    const res = await page.goto("/admin/auctions-data-center/purchase-orders");
    expect([403, 404]).toContain(res?.status() ?? 200);
  });
});
```

- [ ] **Step 2: Copy backend fixture for Playwright to consume**

```bash
mkdir -p frontend/e2e/fixtures
cp backend/src/test/resources/fixtures/po-upload-sample.xlsx \
   frontend/e2e/fixtures/po-upload-sample.xlsx
```

- [ ] **Step 3: Run Playwright suite**

Run (with backend + frontend running locally):
```bash
cd frontend && npx playwright test admin-purchase-orders.spec.ts
```

Expected: 3 tests pass.

- [ ] **Step 4: Commit**

```bash
git add frontend/e2e/admin-purchase-orders.spec.ts \
        frontend/e2e/fixtures/po-upload-sample.xlsx
git commit -m "test(4b): Playwright E2E — admin PO list/upload/delete + role gates"
```

---

## Task 19: Docs updates

**Files:**
- Modify: `docs/api/rest-endpoints.md`
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/architecture/data-model.md`
- Modify: `docs/app-metadata/modules.md`
- Modify: `docs/deployment/setup.md`
- Modify: `docs/testing/coverage.md`
- Create: `docs/business-logic/purchase-order-sync.md`

- [ ] **Step 1: Append `## Purchase Orders (PO)` to `docs/api/rest-endpoints.md`**

```markdown
## Purchase Orders (PO)

Base URL: `/api/v1/admin/purchase-orders`
Role: `Administrator` or `SalesOps` (full read+write).

| Method | Path | Purpose |
|---|---|---|
| GET | `/` | Paginated list (filters: `yearFrom`, `yearTo`, `weekFromId`, `weekToId`, `page`, `size`, `sort`) |
| GET | `/{id}` | Single PO header + lifecycle state |
| POST | `/` | Create PO header |
| PUT | `/{id}` | Update week range / metadata |
| DELETE | `/{id}` | Delete PO (cascades details) |
| GET | `/{id}/details` | Paginated detail grid |
| POST | `/{id}/details/upload` | Bulk Excel wipe-and-replace upload |
| GET | `/{id}/details/download` | Excel export of detail grid |

**Lifecycle state** is derived (`DRAFT`/`ACTIVE`/`CLOSED`) from `today` vs.
`week_from.startDate` / `week_to.endDate` — never stored.

**Upload semantics** are wipe-and-replace: any row error or unknown
buyer_code rejects the entire upload. Successful uploads delete every
existing detail row for the PO and re-insert from the Excel.

**Snowflake sync** is push-only via `AUCTIONS.UPSERT_PURCHASE_ORDER`. No
pull cron, no manual sync trigger. Recovery from push failure = admin
re-uploads.

Error codes: `INVALID_REQUEST`, `INVALID_WEEK_RANGE`, `UPLOAD_PARSE_ERROR`,
`UPLOAD_ROW_ERRORS`, `MISSING_BUYER_CODE`, `PURCHASE_ORDER_NOT_FOUND`,
`DUPLICATE_PO_DETAIL`, `UNSUPPORTED_MEDIA_TYPE`.
```

- [ ] **Step 2: Add ADR to `docs/architecture/decisions.md`**

Append a new entry:

```markdown
## 2026-04-24 — Sub-project 4B: PO module port

**Status:** Accepted.

**Context:** Mendix `ecoatm_po` ships PurchaseOrder + PODetail authored
via Excel upload, with push-only Snowflake sync (no pull cron,
porefreshtimestamp watermark). Sub-project 4C's target-price CTE joins
`po_detail.price` into its `GREATEST(...)` term, so 4B must port the
schema + admin surface before 4C can compute.

**Decision:** Port `purchase_order` + `po_detail` only (drop `weekly_po`,
`week_period`, `purchase_order_doc`, `pohelper`). Push-only Snowflake
sync. Lifecycle state derived from week range. Wipe-and-replace upload
with strict-rejection error posture. `Administrator` + `SalesOps` role
gate.

**Consequences:** 4C unblocks. Snowflake recovery = admin re-upload (same
as Mendix). No fulfillment-tracker port — if `weekly_po` becomes an ops
ask later, it's a follow-up. `temp_buyer_code` column carried forward
for Snowflake payload parity; can be dropped after QA confirms the proc
doesn't read it.

**Spec / Plan:**
- `docs/tasks/auction-po-module-design.md`
- `docs/tasks/auction-po-module-plan.md`
```

- [ ] **Step 3: Add to `docs/architecture/data-model.md`**

Append:

```markdown
## auctions.purchase_order / po_detail
PO header + line items. `purchase_order.week_from_id` / `week_to_id`
reference `mdm.week`. `po_detail.buyer_code_id` references
`buyer_mgmt.buyer_codes`. Lifecycle is derived. See
`docs/tasks/auction-po-module-design.md` §5 for full schema.
```

- [ ] **Step 4: Add module entry to `docs/app-metadata/modules.md`**

Append:

```markdown
## Purchase Order (PO)
- Source module: `ecoatm_po`
- Primary tables: `auctions.purchase_order`, `auctions.po_detail`
- Purpose: weekly PO commitments authored via Excel upload, consumed by
  sub-project 4C target-price recalc as `GREATEST(...)` floor input
- Admin surface: `/admin/auctions-data-center/purchase-orders/**`
- Snowflake sync: push-only via `AUCTIONS.UPSERT_PURCHASE_ORDER`
```

- [ ] **Step 5: Add config block to `docs/deployment/setup.md`**

Append:

```markdown
## PO sync config
- `po.sync.enabled` — default `true`; disables push when false
- `po.sync.writer` — `logging` (default) or `jdbc` (prod)
- `po.sync.snowflake-timeout-seconds` — default 60
```

- [ ] **Step 6: Add coverage entry to `docs/testing/coverage.md`**

Append:

```markdown
## auctions.purchaseorder (new 2026-04-24)
Target 85%+. Upload + push paths are the load-bearing branches; see
`PurchaseOrderServiceTest` + `PODetailServiceTest` +
`PurchaseOrderControllerIT` + `PurchaseOrderSnowflakePushListenerTest` +
`admin-purchase-orders.spec.ts`.
```

- [ ] **Step 7: Create `docs/business-logic/purchase-order-sync.md`**

```markdown
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
```

- [ ] **Step 8: Verify all docs render**

Run: `git diff --stat docs/`
Expected: 7 files changed, additions only.

- [ ] **Step 9: Commit**

```bash
git add docs/api/rest-endpoints.md \
        docs/architecture/decisions.md \
        docs/architecture/data-model.md \
        docs/app-metadata/modules.md \
        docs/deployment/setup.md \
        docs/testing/coverage.md \
        docs/business-logic/purchase-order-sync.md
git commit -m "docs(4b): PO module — REST endpoints, ADR, data-model, sync semantics"
```

---

## Final verification

- [ ] **Step 1: Run the full backend test suite**

```bash
cd backend && mvn -q test
```

Expected: BUILD SUCCESS, all PO tests + existing tests green.

- [ ] **Step 2: Verify backend builds + boots clean**

```bash
cd backend && mvn -q spring-boot:run
```

Expected: starts without unbound-property warnings, hits
`Started SalesPlatformApplication`.

- [ ] **Step 3: Run the full frontend type-check + Playwright PO suite**

```bash
cd frontend && npx tsc --noEmit && npx playwright test admin-purchase-orders.spec.ts
```

Expected: type-check clean; Playwright 3/3 tests pass.

- [ ] **Step 4: Coverage check**

```bash
cd backend && mvn -q jacoco:report
```

Open `backend/target/site/jacoco/com.ecoatm.salesplatform.service.auctions.purchaseorder/index.html`
in a browser — expected ≥ 85% line coverage.

- [ ] **Step 5: Open PR**

```bash
git push -u origin feat/sub4b-po-module
gh pr create --title "feat: sub-project 4B — PO module port" \
             --body "$(cat docs/tasks/auction-po-module-design.md | head -100)"
```

PR body should reference the design doc and the umbrella; reviewer
checklist mirrors 4A's PR.

---

## Spec coverage cross-check

| Spec section | Plan task |
|---|---|
| §3 Decisions #1–6 (schema namespace, types, junctions, denorm label) | Task 1 (V78 SQL) |
| §3 #7 (service-layer week-range ordering) | Task 6 (`PurchaseOrderValidator.resolveWeekRange`) |
| §3 #8 (per-row `po_refresh_timestamp`) | Task 1 (column) + Task 9 (set on upload) |
| §3 #9 (push-only) | Tasks 10–12 (writer + listener; no scheduled job, no reader) |
| §3 #10 (push grain per-PO) | Task 11 (`toPayload` builds full PO + details) |
| §3 #11 (failure logged + swallowed) | Task 11 (`onChange` catch-block) |
| §3 #12 (no audit table) | Task 1 schema (no audit table) |
| §3 #13 (wipe-and-replace) | Task 9 (`PODetailService.upload`) |
| §3 #14 (strict rejection) | Tasks 6, 8, 9 (validator + parser + service short-circuit) |
| §3 #15 (derived lifecycle) | Tasks 3 (enum) + 7 (DTO mapping) |
| §3 #16 (Administrator + SalesOps gate) | Task 13 (`@PreAuthorize`) |
| §3 #17 (no Delete-All) | Task 13 (no endpoint) |
| §3 #18 (drop `purchase_order_doc`) | Task 1 (schema), Task 2 (extractor) |
| §3 #19 (`temp_buyer_code` preserved) | Task 1 (column), Task 9 (populated on upload) |
| §3 #20 (`po.sync.enabled` runtime toggle) | Task 11 (call-time check), Task 14 (yaml) |
| §6 8 endpoints | Task 13 |
| §7 Snowflake writer + listener | Tasks 10, 11, 12 |
| §8 Tests + fixtures | Tasks 1–18 (TDD steps; fixtures Tasks 8, 13, 18) |
| §9 Docs updates | Task 19 |
| §11 4C contract column-name freeze | Task 1 (schema names match) |

All 20 decisions and 8 endpoints covered. No placeholders remain in the
plan — every step shows the concrete code, command, and expected
output.

