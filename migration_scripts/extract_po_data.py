#!/usr/bin/env python3
"""
extract_po_data.py — Extract PurchaseOrder + PODetail from a Mendix snapshot DB
and emit V79 Flyway migration.

Source tables (kept):
  ecoatm_po$purchaseorder
  ecoatm_po$podetail
  ecoatm_po$podetail_purchaseorder        (junction → po_detail.purchase_order_id)
  ecoatm_po$podetail_buyercode            (junction → po_detail.buyer_code_id)
  ecoatm_po$purchaseorder_week_from       (junction → purchase_order.week_from_id)
  ecoatm_po$purchaseorder_week_to         (junction → purchase_order.week_to_id)

Skipped (per design doc §2):
  ecoatm_po$weeklypo, ecoatm_po$weekperiod, ecoatm_po$purchaseorderdoc,
  ecoatm_po$pohelper, all junctions involving them.

FK remap strategy (deviates from the original plan because the dev schema
does NOT carry buyer_codes.legacy_id — it preserves submission_id instead):
  - week junctions:        Mendix `ecoatm_mdm$week`.(year, weeknumber) →
                           dev `mdm.week.week_id` = year*100 + weeknumber.
  - buyer_code junctions:  Mendix `ecoatm_buyermanagement$buyercode`.(submissionid, code) →
                           dev `buyer_mgmt.buyer_codes`.(submission_id, code).

Output: backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql

Usage:
  python extract_po_data.py --source-db qa-0327
  python extract_po_data.py --source-db prod-0325
"""
import argparse
import os
import sys
from datetime import datetime, timezone
from decimal import Decimal
from pathlib import Path

import psycopg2

DB_HOSTS = {
    "qa-0327": "localhost",
    "prod-0325": "localhost",
}

OUT_PATH = (
    Path(__file__).parent.parent
    / "backend/src/main/resources/db/migration/V79__data_auctions_purchase_order.sql"
)

GENERATION_TIMESTAMP = datetime.now(timezone.utc).isoformat()

# Chunk size matches extract_eb_data.py / extract_qa_data.py conventions.
DETAIL_CHUNK = 1000

# Skip cap — if PODetail rows exceed this with broken FKs, the source data
# integrity is suspect and we abort instead of silently dropping rows.
SKIP_CAP = 10


def connect(db_name: str):
    if db_name not in DB_HOSTS:
        # Fall through to psycopg2 so the failure message is identical to a
        # genuine connection error (keeps the test_script_with_invalid_db_fails
        # assertion stable).
        host = "localhost"
    else:
        host = DB_HOSTS[db_name]
    password = os.environ.get("PGPASSWORD") or "Agarwal1$"
    return psycopg2.connect(
        host=host, dbname=db_name, user="postgres", password=password,
    )


def sql_literal(v):
    """SQL-quote a Python value, ASCII-clean to avoid encoding surprises."""
    if v is None:
        return "NULL"
    if isinstance(v, bool):
        return "TRUE" if v else "FALSE"
    if isinstance(v, (int, float, Decimal)):
        return str(v)
    if isinstance(v, datetime):
        return f"'{v.isoformat()}'::timestamptz"
    s = str(v).replace("'", "''")
    s = s.encode("ascii", errors="replace").decode("ascii")
    return "'" + s + "'"


def sql_coalesce_ts(v):
    """Return a TIMESTAMPTZ literal, falling back to generation-time for NULLs.

    `purchase_order` has no createddate/changeddate in source, so we always
    coalesce to GENERATION_TIMESTAMP for those headers.
    """
    if v is None:
        return f"'{GENERATION_TIMESTAMP}'::timestamptz"
    return sql_literal(v)


def fetch_all(conn, sql):
    with conn.cursor() as cur:
        cur.execute(sql)
        cols = [d[0] for d in cur.description]
        return [dict(zip(cols, r)) for r in cur.fetchall()]


def load_week_lookup(conn):
    """Build mendix_week_id → (year, weeknumber) map from `ecoatm_mdm$week`."""
    rows = fetch_all(conn, """
        SELECT id, year, weeknumber
        FROM "ecoatm_mdm$week"
        WHERE year IS NOT NULL AND weeknumber IS NOT NULL
    """)
    return {r["id"]: (r["year"], r["weeknumber"]) for r in rows}


def load_buyer_code_lookup(conn):
    """Build mendix_buyercode_id → (submissionid, code) map."""
    rows = fetch_all(conn, """
        SELECT id, submissionid, code
        FROM "ecoatm_buyermanagement$buyercode"
        WHERE submissionid IS NOT NULL AND code IS NOT NULL
    """)
    return {r["id"]: (r["submissionid"], r["code"]) for r in rows}


def extract_purchase_orders(conn):
    return fetch_all(conn, """
        SELECT id, weekrange, validyearweek, porefreshtimestamp, totalrecords
        FROM "ecoatm_po$purchaseorder"
        ORDER BY id
    """)


def extract_po_details(conn):
    # `qtyfullfiled` is the typo'd Mendix column name; alias to qtyfulfilled.
    return fetch_all(conn, """
        SELECT id, productid, grade, modelname, price, qtycap,
               pricefulfilled, qtyfullfiled AS qtyfulfilled,
               tempbuyercode, createddate, changeddate
        FROM "ecoatm_po$podetail"
        ORDER BY id
    """)


def extract_junction(conn, table, key_col, val_col):
    rows = fetch_all(conn, f'SELECT * FROM "{table}"')
    return {r[key_col]: r[val_col] for r in rows}


def emit_purchase_orders(po_rows, week_from_map, week_to_map, week_lookup):
    """Build the INSERT for auctions.purchase_order."""
    skipped = []
    sql_rows = []
    for r in po_rows:
        wf_mendix = week_from_map.get(r["id"])
        wt_mendix = week_to_map.get(r["id"])
        if wf_mendix is None or wt_mendix is None:
            skipped.append((r["id"], "missing week junction"))
            continue
        wf = week_lookup.get(wf_mendix)
        wt = week_lookup.get(wt_mendix)
        if wf is None or wt is None:
            skipped.append((r["id"], "week not found in ecoatm_mdm$week"))
            continue
        wf_natural = wf[0] * 100 + wf[1]
        wt_natural = wt[0] * 100 + wt[1]
        sql_rows.append(
            f"    ({r['id']}, "
            f"(SELECT id FROM mdm.week WHERE week_id = {wf_natural}), "
            f"(SELECT id FROM mdm.week WHERE week_id = {wt_natural}), "
            f"{sql_literal(r['weekrange'] or 'UNKNOWN')}, "
            f"{sql_literal(bool(r['validyearweek']) if r['validyearweek'] is not None else True)}, "
            f"{r['totalrecords'] if r['totalrecords'] is not None else 0}, "
            f"{sql_literal(r['porefreshtimestamp'])}, "
            f"{sql_coalesce_ts(None)}, "  # created_date — not in source
            f"{sql_coalesce_ts(None)})"   # changed_date — not in source
        )
    return sql_rows, skipped


def emit_po_details(pod_rows, pod_to_po, pod_to_bc, bc_lookup):
    """Build INSERT chunks for auctions.po_detail."""
    skipped = []
    resolved_rows = []
    for r in pod_rows:
        po_legacy = pod_to_po.get(r["id"])
        bc_mendix = pod_to_bc.get(r["id"])
        if po_legacy is None or bc_mendix is None:
            skipped.append((r["id"], "missing FK junction"))
            continue
        bc_pair = bc_lookup.get(bc_mendix)
        if bc_pair is None:
            skipped.append((r["id"], f"buyer_code {bc_mendix} not found"))
            continue
        resolved_rows.append({**r, "po_legacy": po_legacy, "bc_pair": bc_pair})
    return resolved_rows, skipped


def emit_sql(po_sql_rows, pod_resolved):
    lines = [
        "-- =============================================================================",
        "-- V79: auctions — purchase_order + po_detail data load",
        "-- Generated by migration_scripts/extract_po_data.py — do not edit by hand.",
        f"-- Generated at {GENERATION_TIMESTAMP}",
        "--",
        "-- FK remap strategy:",
        "--   * week_from_id / week_to_id  → mdm.week.week_id (year*100 + week_number)",
        "--   * buyer_code_id              → buyer_mgmt.buyer_codes (submission_id, code)",
        "-- =============================================================================",
        "",
    ]

    if po_sql_rows:
        lines.append("INSERT INTO auctions.purchase_order")
        lines.append(
            "    (legacy_id, week_from_id, week_to_id, week_range_label,"
        )
        lines.append(
            "     valid_year_week, total_records, po_refresh_timestamp,"
        )
        lines.append("     created_date, changed_date) VALUES")
        lines.append(",\n".join(po_sql_rows) + ";")
        lines.append("")

    for i in range(0, len(pod_resolved), DETAIL_CHUNK):
        batch = pod_resolved[i:i + DETAIL_CHUNK]
        lines.append("INSERT INTO auctions.po_detail")
        lines.append(
            "    (legacy_id, purchase_order_id, buyer_code_id,"
        )
        lines.append(
            "     product_id, grade, model_name, price, qty_cap,"
        )
        lines.append(
            "     price_fulfilled, qty_fulfilled, temp_buyer_code,"
        )
        lines.append("     created_date, changed_date) VALUES")
        rows_sql = []
        for r in batch:
            sub_id, bc_code = r["bc_pair"]
            rows_sql.append(
                f"    ({r['id']}, "
                f"(SELECT id FROM auctions.purchase_order WHERE legacy_id = {r['po_legacy']}), "
                f"(SELECT id FROM buyer_mgmt.buyer_codes "
                f"WHERE submission_id = {sub_id} AND code = {sql_literal(bc_code)} LIMIT 1), "
                f"{sql_literal(str(r['productid']) if r['productid'] is not None else '')}, "
                f"{sql_literal(r['grade'])}, "
                f"{sql_literal(r['modelname'])}, "
                f"{r['price'] if r['price'] is not None else 0}, "
                f"{r['qtycap'] if r['qtycap'] is not None else 'NULL'}, "
                f"{r['pricefulfilled'] if r['pricefulfilled'] is not None else 'NULL'}, "
                f"{r['qtyfulfilled'] if r['qtyfulfilled'] is not None else 'NULL'}, "
                f"{sql_literal(r['tempbuyercode'])}, "
                f"{sql_coalesce_ts(r['createddate'])}, "
                f"{sql_coalesce_ts(r['changeddate'])})"
            )
        lines.append(",\n".join(rows_sql) + ";")
        lines.append("")

    return "\n".join(lines) + "\n"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-db", required=True)
    args = parser.parse_args()

    with connect(args.source_db) as conn:
        week_lookup = load_week_lookup(conn)
        bc_lookup = load_buyer_code_lookup(conn)

        pos = extract_purchase_orders(conn)
        week_from_map = extract_junction(
            conn,
            "ecoatm_po$purchaseorder_week_from",
            "ecoatm_po$purchaseorderid",
            "ecoatm_mdm$weekid",
        )
        week_to_map = extract_junction(
            conn,
            "ecoatm_po$purchaseorder_week_to",
            "ecoatm_po$purchaseorderid",
            "ecoatm_mdm$weekid",
        )

        pods = extract_po_details(conn)
        pod_to_po = extract_junction(
            conn,
            "ecoatm_po$podetail_purchaseorder",
            "ecoatm_po$podetailid",
            "ecoatm_po$purchaseorderid",
        )
        pod_to_bc = extract_junction(
            conn,
            "ecoatm_po$podetail_buyercode",
            "ecoatm_po$podetailid",
            "ecoatm_buyermanagement$buyercodeid",
        )

    po_sql_rows, po_skipped = emit_purchase_orders(
        pos, week_from_map, week_to_map, week_lookup,
    )
    pod_resolved, pod_skipped = emit_po_details(
        pods, pod_to_po, pod_to_bc, bc_lookup,
    )

    for sid, reason in po_skipped:
        print(f"WARN: skipping purchase_order legacy_id={sid} ({reason})",
              file=sys.stderr)
    for sid, reason in pod_skipped[:50]:
        print(f"WARN: skipping po_detail legacy_id={sid} ({reason})",
              file=sys.stderr)

    if len(pod_skipped) > SKIP_CAP:
        print(f"FATAL: {len(pod_skipped)} po_detail rows skipped "
              f"(>{SKIP_CAP} threshold). Investigate junction integrity.",
              file=sys.stderr)
        sys.exit(1)

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUT_PATH.write_text(emit_sql(po_sql_rows, pod_resolved), encoding="utf-8")

    print(f"Wrote {OUT_PATH}: "
          f"{len(po_sql_rows)} PO rows, {len(pod_resolved)} detail rows, "
          f"{len(po_skipped)} PO + {len(pod_skipped)} detail skipped.")


if __name__ == "__main__":
    sys.exit(main())
