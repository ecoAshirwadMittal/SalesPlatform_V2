#!/usr/bin/env python3
"""
Mendix QA/Prod → SalesPlatform_Modern RMA data extraction script.

Connects to a Mendix PostgreSQL database and generates a Flyway-compatible
SQL migration file (V34) that seeds the pws.rma and pws.rma_item tables
with production data.

Requires the same ID maps as extract_qa_data.py for FK remapping:
- buyer_codes  (from ecoatm_buyermanagement$buyercode)
- users        (from system$user → identity.users)
- devices      (from ecoatm_pwsmdm$device → mdm.device)
- orders       (from ecoatm_pws$order → pws.order)

Usage:
    python extract_rma_data.py --source-db prod-0325 [--output-dir ../backend/src/main/resources/db/migration]
"""

import argparse
import os
import sys
from datetime import datetime
from typing import Any

try:
    import psycopg2
    import psycopg2.extras
except ImportError:
    print("ERROR: psycopg2 not installed. Run: pip install psycopg2-binary")
    sys.exit(1)


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
DB_HOST = "localhost"
DB_PORT = 5432
DB_USER = "postgres"
DB_PASS = "Agarwal1$"

ID_START = 1


def connect(db_name: str):
    conn = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=db_name, user=DB_USER, password=DB_PASS,
    )
    conn.autocommit = True
    return conn


def fetch_all(conn, sql: str) -> list:
    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
        cur.execute(sql)
        return cur.fetchall()


def sql_val(v: Any) -> str:
    """Convert a Python value to a SQL literal."""
    if v is None:
        return "NULL"
    if isinstance(v, bool):
        return "true" if v else "false"
    if isinstance(v, (int, float)):
        return str(v)
    if isinstance(v, datetime):
        return f"'{v.isoformat()}'"
    s = str(v).replace("'", "''")
    return f"'{s}'"


def batch_inserts(table: str, columns: list[str], rows: list[tuple],
                  batch_size: int = 200) -> str:
    """Generate INSERT statements in batches."""
    if not rows:
        return ""
    parts = []
    col_list = ", ".join(columns)
    for i in range(0, len(rows), batch_size):
        batch = rows[i:i + batch_size]
        values = []
        for row in batch:
            vals = ", ".join(sql_val(v) for v in row)
            values.append(f"  ({vals})")
        parts.append(
            f"INSERT INTO {table} ({col_list}) VALUES\n"
            + ",\n".join(values)
            + "\nON CONFLICT DO NOTHING;\n"
        )
    return "\n".join(parts)


class RmaMigrationGenerator:
    def __init__(self, conn):
        self.conn = conn
        self.id_map: dict[str, dict[int, int]] = {}

    def _build_map(self, entity: str, old_ids: list[int]) -> None:
        self.id_map[entity] = {}
        for i, old_id in enumerate(sorted(old_ids), start=ID_START):
            self.id_map[entity][old_id] = i

    def _map_id(self, entity: str, old_id: int | None) -> int | None:
        if old_id is None:
            return None
        return self.id_map.get(entity, {}).get(old_id)

    def _build_prerequisite_maps(self) -> None:
        """Build ID maps for entities referenced as FKs in RMA tables."""
        print("  Building buyer_codes map...")
        rows = fetch_all(self.conn, """
            SELECT id FROM "ecoatm_buyermanagement$buyercode" ORDER BY id
        """)
        self._build_map("buyer_codes", [r["id"] for r in rows])
        print(f"    {len(rows)} buyer codes")

        print("  Building users map...")
        rows = fetch_all(self.conn, """
            SELECT id FROM "system$user"
            WHERE submetaobjectname NOT IN ('System.AnonymousUser', 'System.ScheduledEventUser')
            ORDER BY id
        """)
        self._build_map("users", [r["id"] for r in rows])
        print(f"    {len(rows)} users")

        # Direct users map (ecoatm_usermanagement$ecoatmdirectuser → users)
        # Direct users share the same ID space as system$user, so we use the users map
        print("  Building direct_users -> users map...")
        rows = fetch_all(self.conn, """
            SELECT id FROM "ecoatm_usermanagement$ecoatmdirectuser" ORDER BY id
        """)
        # Direct user IDs are a subset of system$user IDs, already mapped
        du_count = sum(1 for r in rows if self._map_id("users", r["id"]) is not None)
        print(f"    {du_count}/{len(rows)} direct users mapped to identity.users")

        print("  Building devices map...")
        rows = fetch_all(self.conn, """
            SELECT id FROM "ecoatm_pwsmdm$device" ORDER BY id
        """)
        self._build_map("devices", [r["id"] for r in rows])
        print(f"    {len(rows)} devices")

        print("  Building orders map...")
        rows = fetch_all(self.conn, """
            SELECT id FROM "ecoatm_pws$order" ORDER BY id
        """)
        self._build_map("orders", [r["id"] for r in rows])
        print(f"    {len(rows)} orders")

    def generate(self) -> str:
        self._build_prerequisite_maps()

        lines = [
            "-- =============================================================================",
            "-- V34: Data migration — RMA statuses, reasons, RMAs, RMA items",
            f"-- Generated from Mendix database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
            "-- Clear V33 seed data (replaced with production data below)",
            "DELETE FROM pws.rma_item;",
            "DELETE FROM pws.rma;",
            "DELETE FROM pws.rma_reason;",
            "DELETE FROM pws.rma_status;",
            "",
        ]

        # ---- RMA Statuses ----
        lines.append(self._gen_rma_statuses())

        # ---- RMA Reasons ----
        lines.append(self._gen_rma_reasons())

        # ---- RMAs ----
        lines.append(self._gen_rmas())

        # ---- RMA Items ----
        lines.append(self._gen_rma_items())

        # ---- Reset sequences ----
        lines.append(self._gen_sequence_resets())

        return "\n".join(lines)

    def _gen_rma_statuses(self) -> str:
        """Extract rma_status from ecoatm_rma$rmastatus (9 rows)."""
        lines = ["-- RMA Statuses"]
        rows = fetch_all(self.conn, """
            SELECT id, systemstatus, internalstatustext, externalstatustext,
                   internalstatushexcode, externalstatushexcode,
                   salesstatusheaderhexcode, salestablehoverhexcode,
                   statusgroupedto, sortorder, isdefaultstatus,
                   desciption, statusverbiagebidder,
                   createddate, changeddate
            FROM "ecoatm_rma$rmastatus"
            ORDER BY sortorder, id
        """)
        self._build_map("rma_statuses", [r["id"] for r in rows])

        data = []
        for r in rows:
            data.append((
                self._map_id("rma_statuses", r["id"]),
                r.get("systemstatus"),
                r.get("internalstatustext"),
                r.get("externalstatustext"),
                r.get("internalstatushexcode"),
                r.get("externalstatushexcode"),
                r.get("salesstatusheaderhexcode"),
                r.get("salestablehoverhexcode"),
                r.get("statusgroupedto"),
                r.get("sortorder"),
                r.get("isdefaultstatus", False),
                r.get("desciption"),
                r.get("statusverbiagebidder"),
                r.get("createddate"),
                r.get("changeddate"),
            ))

        lines.append(batch_inserts("pws.rma_status", [
            "id", "system_status", "internal_status_text", "external_status_text",
            "internal_status_hex_code", "external_status_hex_code",
            "sales_status_header_hex_code", "sales_table_hover_hex_code",
            "status_grouped_to", "sort_order", "is_default",
            "description", "status_verbiage_bidder",
            "created_date", "updated_date",
        ], data))

        print(f"  rma_status: {len(data)} rows")
        return "\n".join(lines)

    def _gen_rma_reasons(self) -> str:
        """Extract rma_reason from ecoatm_rma$rmareasons (16 rows)."""
        lines = ["-- RMA Reasons"]
        rows = fetch_all(self.conn, """
            SELECT id, validreasons, isactive, createddate, changeddate
            FROM "ecoatm_rma$rmareasons"
            ORDER BY id
        """)
        self._build_map("rma_reasons", [r["id"] for r in rows])

        data = []
        for r in rows:
            data.append((
                self._map_id("rma_reasons", r["id"]),
                r.get("validreasons"),
                r.get("isactive", True),
                r.get("createddate"),
                r.get("changeddate"),
            ))

        lines.append(batch_inserts("pws.rma_reason", [
            "id", "valid_reasons", "is_active", "created_date", "updated_date",
        ], data))

        print(f"  rma_reason: {len(data)} rows")
        return "\n".join(lines)

    def _gen_rmas(self) -> str:
        """Extract RMAs with FK resolution from junction tables."""
        lines = ["-- RMAs"]

        # Main RMA data
        rows = fetch_all(self.conn, """
            SELECT r.id, r.number,
                   r.requestskus, r.requestqty, r.requestsalestotal,
                   r.approvedskus, r.approvedqty, r.approvedsalestotal,
                   r.approvedcount, r.declinedcount,
                   r.submitteddate, r.approvaldate, r.reviewcompletedon,
                   r.systemstatus, r.oraclermastatus, r.oraclenumber, r.oracleid,
                   r.oraclehttpcode, r.issuccessful, r.allrmaitemsvalid,
                   r.jsoncontent, r.oraclejsonresponse,
                   r.entityowner, r.entitychanger,
                   r.createddate, r.changeddate,
                   bc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id,
                   sub."ecoatm_usermanagement$ecoatmdirectuserid" AS submitted_by_id,
                   rev."ecoatm_usermanagement$ecoatmdirectuserid" AS reviewed_by_id,
                   rs."ecoatm_rma$rmastatusid" AS rma_status_id
            FROM "ecoatm_rma$rma" r
            LEFT JOIN "ecoatm_rma$rma_buyercode" bc
                ON r.id = bc."ecoatm_rma$rmaid"
            LEFT JOIN "ecoatm_rma$rma_ecoatmdirectuser_submittedby" sub
                ON r.id = sub."ecoatm_rma$rmaid"
            LEFT JOIN "ecoatm_rma$rma_ecoatmdirectuser_reviewedby" rev
                ON r.id = rev."ecoatm_rma$rmaid"
            LEFT JOIN "ecoatm_rma$rma_rmastatus" rs
                ON r.id = rs."ecoatm_rma$rmaid"
            ORDER BY r.id
        """)
        self._build_map("rmas", [r["id"] for r in rows])

        data = []
        skipped = 0
        for r in rows:
            mapped_buyer = self._map_id("buyer_codes", r.get("buyer_code_id"))
            mapped_submitted = self._map_id("users", r.get("submitted_by_id"))
            mapped_reviewed = self._map_id("users", r.get("reviewed_by_id"))
            mapped_status = self._map_id("rma_statuses", r.get("rma_status_id"))

            data.append((
                self._map_id("rmas", r["id"]),
                r.get("number"),
                mapped_buyer,
                mapped_submitted,
                mapped_reviewed,
                mapped_status,
                r.get("requestskus", 0),
                r.get("requestqty", 0),
                r.get("requestsalestotal", 0),
                r.get("approvedskus", 0),
                r.get("approvedqty", 0),
                r.get("approvedsalestotal", 0),
                r.get("approvedcount", 0),
                r.get("declinedcount", 0),
                r.get("submitteddate"),
                r.get("approvaldate"),
                r.get("reviewcompletedon"),
                r.get("systemstatus"),
                r.get("oraclermastatus"),
                r.get("oraclenumber"),
                r.get("oracleid"),
                r.get("oraclehttpcode"),
                r.get("issuccessful"),
                r.get("allrmaitemsvalid"),
                r.get("jsoncontent"),
                r.get("oraclejsonresponse"),
                r.get("entityowner"),
                r.get("entitychanger"),
                r.get("createddate"),
                r.get("changeddate"),
            ))

        lines.append(batch_inserts("pws.rma", [
            "id", "number",
            "buyer_code_id", "submitted_by_user_id", "reviewed_by_user_id",
            "rma_status_id",
            "request_skus", "request_qty", "request_sales_total",
            "approved_skus", "approved_qty", "approved_sales_total",
            "approved_count", "declined_count",
            "submitted_date", "approval_date", "review_completed_on",
            "system_status", "oracle_rma_status", "oracle_number", "oracle_id",
            "oracle_http_code", "is_successful", "all_rma_items_valid",
            "json_content", "oracle_json_response",
            "entity_owner", "entity_changer",
            "created_date", "updated_date",
        ], data))

        print(f"  rma: {len(data)} rows")
        return "\n".join(lines)

    def _gen_rma_items(self) -> str:
        """Extract RMA items with FK resolution from junction tables."""
        lines = ["-- RMA Items"]

        rows = fetch_all(self.conn, """
            SELECT ri.id, ri.imei, ri.ordernumber, ri.shipdate, ri.saleprice,
                   ri.returnreason, ri.status, ri.statusdisplay, ri.declinereason,
                   ri.entityowner, ri.entitychanger,
                   ri.createddate, ri.changeddate,
                   rir."ecoatm_rma$rmaid" AS rma_id,
                   rid."ecoatm_pwsmdm$deviceid" AS device_id,
                   rio."ecoatm_pws$orderid" AS order_id
            FROM "ecoatm_rma$rmaitem" ri
            LEFT JOIN "ecoatm_rma$rmaitem_rma" rir
                ON ri.id = rir."ecoatm_rma$rmaitemid"
            LEFT JOIN "ecoatm_rma$rmaitem_device" rid
                ON ri.id = rid."ecoatm_rma$rmaitemid"
            LEFT JOIN "ecoatm_rma$rmaitem_order" rio
                ON ri.id = rio."ecoatm_rma$rmaitemid"
            ORDER BY ri.id
        """)

        data = []
        skipped = 0
        for i, r in enumerate(rows, start=ID_START):
            mapped_rma = self._map_id("rmas", r.get("rma_id"))
            if not mapped_rma:
                skipped += 1
                continue  # Skip orphaned items with no RMA FK

            mapped_device = self._map_id("devices", r.get("device_id"))
            mapped_order = self._map_id("orders", r.get("order_id"))

            data.append((
                len(data) + 1,  # sequential ID
                mapped_rma,
                mapped_device,
                mapped_order,
                r.get("imei"),
                r.get("ordernumber"),
                r.get("shipdate"),
                r.get("saleprice"),
                r.get("returnreason"),
                r.get("status"),
                r.get("statusdisplay"),
                r.get("declinereason"),
                r.get("entityowner"),
                r.get("entitychanger"),
                r.get("createddate"),
                r.get("changeddate"),
            ))

        lines.append(batch_inserts("pws.rma_item", [
            "id", "rma_id", "device_id", "order_id",
            "imei", "order_number", "ship_date", "sale_price",
            "return_reason", "status", "status_display", "decline_reason",
            "entity_owner", "entity_changer",
            "created_date", "updated_date",
        ], data))

        if skipped:
            print(f"  rma_item: {len(data)} rows ({skipped} orphaned items skipped)")
        else:
            print(f"  rma_item: {len(data)} rows")
        return "\n".join(lines)

    def _gen_sequence_resets(self) -> str:
        """Reset PostgreSQL sequences to the max inserted ID."""
        rma_status_count = len(self.id_map.get("rma_statuses", {}))
        rma_reason_count = len(self.id_map.get("rma_reasons", {}))
        rma_count = len(self.id_map.get("rmas", {}))

        # rma_item count: use the data length from _gen_rma_items
        # Since we can't easily pass it, we'll use a safe upper bound
        lines = [
            "",
            "-- Reset sequences to continue after inserted data",
            f"SELECT setval('pws.rma_status_id_seq', {max(rma_status_count, 1)}, true);",
            f"SELECT setval('pws.rma_reason_id_seq', {max(rma_reason_count, 1)}, true);",
            f"SELECT setval('pws.rma_id_seq', {max(rma_count, 1)}, true);",
            "SELECT setval('pws.rma_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pws.rma_item), true);",
        ]
        return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Extract Mendix RMA data and generate Flyway V34 migration"
    )
    parser.add_argument(
        "--source-db", required=True,
        help="Source Mendix database name (e.g. prod-0325, qa-0327)"
    )
    parser.add_argument(
        "--output-dir",
        default="../backend/src/main/resources/db/migration",
        help="Output directory for SQL migration file"
    )
    args = parser.parse_args()

    output_dir = os.path.abspath(
        os.path.join(os.path.dirname(__file__), args.output_dir)
    )
    os.makedirs(output_dir, exist_ok=True)

    print(f"Connecting to {args.source_db}...")
    conn = connect(args.source_db)

    gen = RmaMigrationGenerator(conn)

    filename = "V34__data_rma.sql"
    print(f"\nGenerating {filename}...")
    try:
        sql = gen.generate()
        filepath = os.path.join(output_dir, filename)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(sql)
        print(f"\nOK — Written to {filepath}")

        # Print summary
        for entity, mapping in gen.id_map.items():
            if entity in ("rma_statuses", "rma_reasons", "rmas"):
                print(f"  {entity}: {len(mapping)} rows")
    except Exception as e:
        print(f"FAIL — ERROR: {e}")
        import traceback
        traceback.print_exc()

    conn.close()
    print("\nDone! Run 'mvn spring-boot:run' to apply V34 via Flyway.")


if __name__ == "__main__":
    main()
