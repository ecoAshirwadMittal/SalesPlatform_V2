#!/usr/bin/env python3
"""
Standalone V34 generator that bridges Mendix source IDs to actual target DB IDs.

Connects to BOTH databases:
  - Mendix source (e.g. prod-0325) for RMA data
  - Target (salesplatform_dev) for actual ID lookups via natural keys

Usage:
    python gen_v34_standalone.py --source-db prod-0325
"""

import argparse
import os
import sys
from datetime import datetime

try:
    import psycopg2
    import psycopg2.extras
except ImportError:
    print("ERROR: psycopg2 not installed. Run: pip install psycopg2-binary")
    sys.exit(1)


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
MENDIX_HOST = "localhost"
MENDIX_PORT = 5432
MENDIX_USER = "postgres"
MENDIX_PASS = "Agarwal1$"

TARGET_HOST = "localhost"
TARGET_PORT = 5432
TARGET_DB = "salesplatform_dev"
TARGET_USER = "salesplatform"
TARGET_PASS = "salesplatform"


def connect_mendix(db_name: str):
    conn = psycopg2.connect(
        host=MENDIX_HOST, port=MENDIX_PORT,
        dbname=db_name, user=MENDIX_USER, password=MENDIX_PASS
    )
    conn.autocommit = True
    return conn


def connect_target():
    conn = psycopg2.connect(
        host=TARGET_HOST, port=TARGET_PORT,
        dbname=TARGET_DB, user=TARGET_USER, password=TARGET_PASS
    )
    conn.autocommit = True
    return conn


def fetch_all(conn, sql: str):
    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
        cur.execute(sql)
        return cur.fetchall()


def sql_val(v) -> str:
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


def batch_inserts(table: str, columns: list, rows: list, batch_size: int = 200) -> str:
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
        parts.append(f"INSERT INTO {table} ({col_list}) VALUES\n" + ",\n".join(values) + "\nON CONFLICT DO NOTHING;\n")
    return "\n".join(parts)


# ---------------------------------------------------------------------------
# Build natural-key maps: Mendix ID -> Target DB ID
# ---------------------------------------------------------------------------
def build_user_map(mendix_conn, target_conn) -> dict:
    """Map Mendix system$user.id -> target identity.users.id via name (email)."""
    mendix_users = fetch_all(mendix_conn, 'SELECT id, name FROM "system$user" ORDER BY id')
    target_users = fetch_all(target_conn, "SELECT id, name FROM identity.users")
    target_by_name = {r["name"]: r["id"] for r in target_users}
    result = {}
    for r in mendix_users:
        tid = target_by_name.get(r["name"])
        if tid is not None:
            result[r["id"]] = tid
    print(f"  Users: {len(result)}/{len(mendix_users)} mapped")
    return result


def build_buyer_code_map(mendix_conn, target_conn) -> dict:
    """Map Mendix buyercode.id -> target buyer_mgmt.buyer_codes.id via code."""
    mendix_rows = fetch_all(mendix_conn, 'SELECT id, code FROM "ecoatm_buyermanagement$buyercode" ORDER BY id')
    target_rows = fetch_all(target_conn, "SELECT id, code FROM buyer_mgmt.buyer_codes")
    target_by_code = {r["code"]: r["id"] for r in target_rows}
    result = {}
    for r in mendix_rows:
        tid = target_by_code.get(r["code"])
        if tid is not None:
            result[r["id"]] = tid
    print(f"  Buyer codes: {len(result)}/{len(mendix_rows)} mapped")
    return result


def build_device_map(mendix_conn, target_conn) -> dict:
    """Map Mendix device.id -> target mdm.device.id via sku."""
    mendix_rows = fetch_all(mendix_conn, 'SELECT id, sku FROM "ecoatm_pwsmdm$device" ORDER BY id')
    target_rows = fetch_all(target_conn, "SELECT id, sku FROM mdm.device")
    target_by_sku = {r["sku"]: r["id"] for r in target_rows}
    result = {}
    for r in mendix_rows:
        tid = target_by_sku.get(r["sku"])
        if tid is not None:
            result[r["id"]] = tid
    print(f"  Devices: {len(result)}/{len(mendix_rows)} mapped")
    return result


def build_order_map(mendix_conn, target_conn) -> dict:
    """Map Mendix order.id -> target pws.order.id via order_number."""
    mendix_rows = fetch_all(mendix_conn, 'SELECT id, ordernumber FROM "ecoatm_pws$order" ORDER BY id')
    target_rows = fetch_all(target_conn, 'SELECT id, order_number FROM pws."order"')
    target_by_num = {r["order_number"]: r["id"] for r in target_rows}
    result = {}
    for r in mendix_rows:
        tid = target_by_num.get(r["ordernumber"])
        if tid is not None:
            result[r["id"]] = tid
    print(f"  Orders: {len(result)}/{len(mendix_rows)} mapped")
    return result


def map_id(mapping: dict, old_id):
    if old_id is None:
        return None
    return mapping.get(old_id)


# ---------------------------------------------------------------------------
# Generate V34
# ---------------------------------------------------------------------------
def generate_v34(mendix_conn, user_map: dict, bc_map: dict, device_map: dict, order_map: dict) -> str:
    lines = [
        "-- =============================================================================",
        "-- V34: Data migration -- RMA statuses, reasons, RMAs, RMA items",
        f"-- Generated from Mendix source on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
        "-- Standalone generator: maps Mendix IDs to actual target DB IDs via natural keys",
        "-- =============================================================================",
        "",
        "-- Clear V33 seed data (replaced with production data below)",
        "DELETE FROM pws.rma_item;",
        "DELETE FROM pws.rma;",
        "DELETE FROM pws.rma_reason;",
        "DELETE FROM pws.rma_status;",
        "",
    ]

    # --- RMA Statuses (self-contained, sequential IDs starting at 1) ---
    rows = fetch_all(mendix_conn, """
        SELECT id, systemstatus, internalstatustext, externalstatustext,
               internalstatushexcode, externalstatushexcode,
               salesstatusheaderhexcode, salestablehoverhexcode,
               statusgroupedto, sortorder, isdefaultstatus,
               desciption, statusverbiagebidder,
               createddate, changeddate
        FROM "ecoatm_rma$rmastatus"
        ORDER BY sortorder, id
    """)
    status_map = {}
    for i, r in enumerate(rows, start=1):
        status_map[r["id"]] = i

    status_data = []
    for r in rows:
        status_data.append((
            status_map[r["id"]],
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
    lines.append("-- RMA Statuses")
    lines.append(batch_inserts("pws.rma_status", [
        "id", "system_status", "internal_status_text", "external_status_text",
        "internal_status_hex_code", "external_status_hex_code",
        "sales_status_header_hex_code", "sales_table_hover_hex_code",
        "status_grouped_to", "sort_order", "is_default",
        "description", "status_verbiage_bidder",
        "created_date", "updated_date",
    ], status_data))

    # --- RMA Reasons (self-contained, sequential IDs) ---
    rows = fetch_all(mendix_conn, """
        SELECT id, validreasons, isactive, createddate, changeddate
        FROM "ecoatm_rma$rmareasons"
        ORDER BY id
    """)
    reason_map = {}
    for i, r in enumerate(rows, start=1):
        reason_map[r["id"]] = i

    reason_data = []
    for r in rows:
        reason_data.append((
            reason_map[r["id"]],
            r.get("validreasons"),
            r.get("isactive", True),
            r.get("createddate"),
            r.get("changeddate"),
        ))
    lines.append("-- RMA Reasons")
    lines.append(batch_inserts("pws.rma_reason", [
        "id", "valid_reasons", "is_active", "created_date", "updated_date",
    ], reason_data))

    # --- RMAs (with FK resolution via natural-key maps) ---
    rows = fetch_all(mendix_conn, """
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
    rma_map = {}
    for i, r in enumerate(rows, start=1):
        rma_map[r["id"]] = i

    rma_data = []
    skipped_rmas = 0
    for r in rows:
        mapped_bc = map_id(bc_map, r.get("buyer_code_id"))
        mapped_submitted = map_id(user_map, r.get("submitted_by_id"))
        mapped_reviewed = map_id(user_map, r.get("reviewed_by_id"))
        mapped_status = map_id(status_map, r.get("rma_status_id"))

        if not mapped_bc:
            skipped_rmas += 1
            del rma_map[r["id"]]  # remove from map so items are also skipped
            continue

        rma_data.append((
            rma_map[r["id"]],
            r.get("number"),
            mapped_bc,
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
    if skipped_rmas:
        print(f"  Note: {skipped_rmas} RMAs skipped (buyer_code not found in target)")
    lines.append("-- RMAs")
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
    ], rma_data))

    # --- RMA Items ---
    rows = fetch_all(mendix_conn, """
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

    item_data = []
    skipped_items = 0
    for r in rows:
        mapped_rma = map_id(rma_map, r.get("rma_id"))
        if not mapped_rma:
            skipped_items += 1
            continue
        item_data.append((
            len(item_data) + 1,
            mapped_rma,
            map_id(device_map, r.get("device_id")),
            map_id(order_map, r.get("order_id")),
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
    lines.append("-- RMA Items")
    lines.append(batch_inserts("pws.rma_item", [
        "id", "rma_id", "device_id", "order_id",
        "imei", "order_number", "ship_date", "sale_price",
        "return_reason", "status", "status_display", "decline_reason",
        "entity_owner", "entity_changer",
        "created_date", "updated_date",
    ], item_data))

    if skipped_items:
        print(f"  Note: {skipped_items} orphaned rma_items skipped")

    # Reset sequences
    max_status = max(status_map.values()) if status_map else 0
    max_reason = max(reason_map.values()) if reason_map else 0
    max_rma = max((v for v in rma_map.values()), default=0)
    lines.append("")
    lines.append(f"SELECT setval('pws.rma_status_id_seq', {max(max_status, 1)}, true);")
    lines.append(f"SELECT setval('pws.rma_reason_id_seq', {max(max_reason, 1)}, true);")
    lines.append(f"SELECT setval('pws.rma_id_seq', {max(max_rma, 1)}, true);")
    lines.append(f"SELECT setval('pws.rma_item_id_seq', {max(len(item_data), 1)}, true);")

    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Generate V34 RMA migration with correct target DB IDs")
    parser.add_argument("--source-db", required=True, help="Mendix source database name (e.g. prod-0325)")
    parser.add_argument("--output-dir", default="../backend/src/main/resources/db/migration",
                        help="Output directory for the SQL migration file")
    args = parser.parse_args()

    output_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), args.output_dir))
    os.makedirs(output_dir, exist_ok=True)

    print(f"Connecting to Mendix source: {args.source_db}...")
    mendix_conn = connect_mendix(args.source_db)

    print(f"Connecting to target: {TARGET_DB}...")
    target_conn = connect_target()

    print("Building natural-key maps...")
    user_map = build_user_map(mendix_conn, target_conn)
    bc_map = build_buyer_code_map(mendix_conn, target_conn)
    device_map = build_device_map(mendix_conn, target_conn)
    order_map = build_order_map(mendix_conn, target_conn)

    print("Generating V34__data_rma.sql...")
    sql = generate_v34(mendix_conn, user_map, bc_map, device_map, order_map)

    filepath = os.path.join(output_dir, "V34__data_rma.sql")
    with open(filepath, "w", encoding="utf-8") as f:
        f.write(sql)
    print(f"  OK Written to {filepath}")

    print(f"\nSummary:")
    print(f"  Users mapped:       {len(user_map)}")
    print(f"  Buyer codes mapped: {len(bc_map)}")
    print(f"  Devices mapped:     {len(device_map)}")
    print(f"  Orders mapped:      {len(order_map)}")

    mendix_conn.close()
    target_conn.close()
    print("\nDone! Run 'mvn spring-boot:run' to apply V34 via Flyway.")


if __name__ == "__main__":
    main()
