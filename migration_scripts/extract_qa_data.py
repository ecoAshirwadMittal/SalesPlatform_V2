#!/usr/bin/env python3
"""
Mendix QA/Prod → SalesPlatform_Modern data extraction script.

Connects to a Mendix PostgreSQL database (e.g. qa-0327, prod-0325) and
generates Flyway-compatible SQL migration files that seed the new schema.

Usage:
    python extract_qa_data.py --source-db qa-0327 [--output-dir ../backend/src/main/resources/db/migration]

Mendix IDs are NOT preserved — new sequential IDs are generated.
FK relationships are maintained via in-memory ID mapping dictionaries.
"""

import argparse
import os
import sys
from datetime import datetime
from collections import OrderedDict

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

# Starting ID offsets per entity (avoid collision with V15 dev seeds at 1001-1006, 9001-9006)
# Using 1-based sequential IDs; V15 seeds will be truncated before insert.
ID_START = 1

# Tables to SKIP (transient/session data, audit logs, huge auction tables not in schema yet)
SKIP_TABLES = {
    "system$session", "system$session_user",
    "saml20$samlrequest", "saml20$samlresponse", "saml20$ssolog",
    "forgotpassword$forgotpassword", "forgotpassword$forgotpassword_account",
    "forgotpassword$forgotpassword_anon_user_access",
}


def connect(db_name):
    conn = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=db_name, user=DB_USER, password=DB_PASS
    )
    conn.autocommit = True
    return conn


def fetch_all(conn, sql):
    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
        cur.execute(sql)
        return cur.fetchall()


def sql_val(v):
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


def batch_inserts(table, columns, rows, batch_size=200):
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
        parts.append(f"INSERT INTO {table} ({col_list}) VALUES\n" + ",\n".join(values) + "\nON CONFLICT DO NOTHING;\n")

    return "\n".join(parts)


class MigrationGenerator:
    def __init__(self, conn):
        self.conn = conn
        # ID mapping: {entity_type: {old_mendix_id: new_sequential_id}}
        self.id_map = {}

    def _build_map(self, entity, old_ids):
        """Assign new sequential IDs to a list of old Mendix IDs."""
        self.id_map[entity] = {}
        for i, old_id in enumerate(sorted(old_ids), start=ID_START):
            self.id_map[entity][old_id] = i

    def _map_id(self, entity, old_id):
        """Look up a new ID for an old Mendix ID. Returns NULL if not found."""
        if old_id is None:
            return None
        m = self.id_map.get(entity, {})
        return m.get(old_id)

    # -----------------------------------------------------------------------
    # V16: Identity core — languages, timezones, roles
    # -----------------------------------------------------------------------
    def gen_v16(self):
        lines = [
            "-- =============================================================================",
            "-- V16: Data migration — Identity core (languages, timezones, roles)",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
            "-- Clear V15 dev seed data",
            "DELETE FROM identity.user_role_assignments;",
            "DELETE FROM identity.user_roles;",
            "",
        ]

        # --- Languages ---
        rows = fetch_all(self.conn, 'SELECT id, code, description FROM "system$language" ORDER BY id')
        self._build_map("languages", [r["id"] for r in rows])
        data = [(self._map_id("languages", r["id"]), r.get("code", "en_US"), r.get("description")) for r in rows]
        lines.append("-- Languages")
        lines.append(batch_inserts("identity.languages", ["id", "code", "description"], data))

        # --- Timezones ---
        rows = fetch_all(self.conn, 'SELECT id, code, description, rawoffset FROM "system$timezone" ORDER BY id')
        self._build_map("timezones", [r["id"] for r in rows])
        data = [(self._map_id("timezones", r["id"]), r["code"], r["description"], r.get("rawoffset")) for r in rows]
        lines.append("-- Timezones")
        lines.append(batch_inserts("identity.timezones", ["id", "code", "description", "raw_offset"], data))

        # --- User Roles ---
        rows = fetch_all(self.conn, 'SELECT id, modelguid, name, description FROM "system$userrole" ORDER BY id')
        self._build_map("roles", [r["id"] for r in rows])
        data = [(self._map_id("roles", r["id"]), r["modelguid"], r["name"], r["description"]) for r in rows]
        lines.append("-- User Roles")
        lines.append(batch_inserts("identity.user_roles", ["id", "model_guid", "name", "description"], data))

        # --- Grantable Roles ---
        rows = fetch_all(self.conn,
            'SELECT "system$userroleid1" AS grantor, "system$userroleid2" AS grantee FROM "system$grantableroles" ORDER BY 1, 2')
        data = [(self._map_id("roles", r["grantor"]), self._map_id("roles", r["grantee"])) for r in rows
                if self._map_id("roles", r["grantor"]) and self._map_id("roles", r["grantee"])]
        lines.append("-- Grantable Roles")
        lines.append(batch_inserts("identity.grantable_roles", ["grantor_role_id", "grantee_role_id"], data))

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V17: Identity — users, accounts, role assignments
    # -----------------------------------------------------------------------
    def gen_v17(self):
        lines = [
            "-- =============================================================================",
            "-- V17: Data migration — Users, accounts, role assignments",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
            "-- Clear V15 dev seed data",
            "DELETE FROM identity.users WHERE id BETWEEN 9001 AND 9006;",
            "",
        ]

        # --- Users ---
        rows = fetch_all(self.conn, """
            SELECT id, submetaobjectname, name, password, lastlogin,
                   blocked, blockedsince, active, failedlogins,
                   webserviceuser, isanonymous, createddate, changeddate,
                   "system$owner", "system$changedby"
            FROM "system$user"
            ORDER BY id
        """)
        self._build_map("users", [r["id"] for r in rows])

        data = []
        for r in rows:
            data.append((
                self._map_id("users", r["id"]),
                r["submetaobjectname"] or "System.User",
                r["name"],
                r["password"],
                r["lastlogin"],
                r["blocked"],
                r["blockedsince"],
                r["active"],
                r["failedlogins"] or 0,
                r["webserviceuser"],
                r["isanonymous"],
                r["createddate"],
                r["changeddate"],
                self._map_id("users", r["system$owner"]),
                self._map_id("users", r["system$changedby"]),
            ))

        lines.append("-- Users")
        lines.append(batch_inserts("identity.users", [
            "id", "user_type", "name", "password", "last_login",
            "blocked", "blocked_since", "active", "failed_logins",
            "web_service_user", "is_anonymous", "created_date", "changed_date",
            "owner_id", "changed_by_id"
        ], data))

        # --- Accounts ---
        rows = fetch_all(self.conn, """
            SELECT id, fullname, email, islocaluser
            FROM "administration$account"
            ORDER BY id
        """)
        data = []
        for r in rows:
            new_uid = self._map_id("users", r["id"])
            if new_uid:
                data.append((new_uid, r["fullname"], r["email"], r["islocaluser"]))

        lines.append("-- Accounts (joined to users)")
        lines.append(batch_inserts("identity.accounts", ["user_id", "full_name", "email", "is_local_user"], data))

        # --- User-Role Assignments ---
        rows = fetch_all(self.conn, """
            SELECT "system$userid", "system$userroleid"
            FROM "system$userroles"
            ORDER BY 1, 2
        """)
        data = []
        for r in rows:
            uid = self._map_id("users", r["system$userid"])
            rid = self._map_id("roles", r["system$userroleid"])
            if uid and rid:
                data.append((uid, rid))

        lines.append("-- User-Role Assignments")
        lines.append(batch_inserts("identity.user_role_assignments", ["user_id", "role_id"], data))

        # --- User-Language ---
        rows = fetch_all(self.conn, """
            SELECT "system$userid" AS user_id, "system$languageid" AS lang_id
            FROM "system$user_language"
            ORDER BY 1
        """)
        data = []
        for r in rows:
            uid = self._map_id("users", r["user_id"])
            lid = self._map_id("languages", r["lang_id"])
            if uid and lid:
                data.append((uid, lid))

        lines.append("-- User-Language")
        lines.append(batch_inserts("identity.user_languages", ["user_id", "language_id"], data))

        # --- User-Timezone ---
        rows = fetch_all(self.conn, """
            SELECT "system$userid" AS user_id, "system$timezoneid" AS tz_id
            FROM "system$user_timezone"
            ORDER BY 1
        """)
        data = []
        for r in rows:
            uid = self._map_id("users", r["user_id"])
            tid = self._map_id("timezones", r["tz_id"])
            if uid and tid:
                data.append((uid, tid))

        lines.append("-- User-Timezone")
        lines.append(batch_inserts("identity.user_timezones", ["user_id", "timezone_id"], data))

        # Reset sequence hints
        max_user_id = max(self.id_map["users"].values()) if self.id_map["users"] else 0
        lines.append(f"\n-- Sequence hint: max user_id = {max_user_id}")

        # --- Re-insert V15 dev seed users and role assignments ---
        lines.append("")
        lines.append("-- Re-insert V15 dev seed users (for local testing)")
        lines.append("INSERT INTO identity.users (id, user_type, name, password, active, blocked, failed_logins, web_service_user, is_anonymous) VALUES")
        lines.append("  (9001, 'Administration.Account', 'admin@test.com',       '$2b$10$kvNUvxXhdOPLCgNHK2sGTeHLN6ODQgjfmMjWmE8nm.H0kv9SYjy1K', true, false, 0, false, false),")
        lines.append("  (9002, 'Administration.Account', 'coadmin@test.com',     '$2b$10$Yi7pFG4elgPqEuQDyCrqruRRk84iylC3P.APs4mFW0YuNeytws0yG', true, false, 0, false, false),")
        lines.append("  (9003, 'Administration.Account', 'salesops@test.com',    '$2b$10$OdgxmeCD08Pbq53CU59M8OyjI9JgFVgUiMpLO0CsC.lWK4/0/s0Cu', true, false, 0, false, false),")
        lines.append("  (9004, 'Administration.Account', 'salesrep@test.com',    '$2b$10$K8YsBbMRK8lLmH5Vq.HTPO1KjYMH76/6Mup3LRI7/ZHhrCStq7EbK', true, false, 0, false, false),")
        lines.append("  (9005, 'EcoATM_UserManagement.EcoATMDirectUser', 'bidder@buyerco.com', '$2b$10$pe5V18PUjvfOsJTpvS4kgOfwyu7/bdAlrqs3agUmt7M7tGFLY.D.e', true, false, 0, false, false),")
        lines.append("  (9006, 'Administration.Account', 'directadmin@test.com', '$2b$10$6XWarAdFAGyV3lPpsUkSEehN16OfScm1D9ckSCOWORc1FU4CAlpMO', true, false, 0, false, false)")
        lines.append("ON CONFLICT (id) DO NOTHING;")
        lines.append("")
        lines.append("-- Map dev seed users to QA role IDs")
        lines.append("INSERT INTO identity.user_role_assignments (user_id, role_id) VALUES")
        lines.append("  (9001, 1),   -- admin@test.com        -> Administrator")
        lines.append("  (9002, 1),   -- coadmin@test.com       -> Administrator")
        lines.append("  (9003, 6),   -- salesops@test.com      -> SalesOps")
        lines.append("  (9004, 9),   -- salesrep@test.com      -> SalesRep")
        lines.append("  (9005, 4),   -- bidder@buyerco.com     -> Bidder")
        lines.append("  (9006, 8)    -- directadmin@test.com   -> ecoAtmDirectAdmin")
        lines.append("ON CONFLICT DO NOTHING;")

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V18: Buyer management core — sales reps, buyers, buyer codes
    # -----------------------------------------------------------------------
    def gen_v18(self):
        lines = [
            "-- =============================================================================",
            "-- V18: Data migration — Sales reps, buyers, buyer codes",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # --- Sales Representatives ---
        rows = fetch_all(self.conn, """
            SELECT id, salesrepresentativeid, salesrepfirstname, salesreplastname, active,
                   createddate, changeddate, "system$owner", "system$changedby"
            FROM "ecoatm_buyermanagement$salesrepresentative"
            ORDER BY id
        """)
        self._build_map("sales_reps", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("sales_reps", r["id"]),
                r.get("salesrepresentativeid"),
                r["salesrepfirstname"], r["salesreplastname"],
                r["active"],
                r["createddate"], r["changeddate"],
                self._map_id("users", r["system$owner"]),
                self._map_id("users", r["system$changedby"]),
            ))
        lines.append("-- Sales Representatives")
        lines.append(batch_inserts("buyer_mgmt.sales_representatives", [
            "id", "sales_representative_id", "first_name", "last_name", "active",
            "created_date", "changed_date", "owner_id", "changed_by_id"
        ], data))

        # --- Buyers ---
        rows = fetch_all(self.conn, """
            SELECT id, submissionid, companyname, status, isspecialbuyer,
                   createddate, changeddate, "system$owner", "system$changedby", entityowner
            FROM "ecoatm_buyermanagement$buyer"
            ORDER BY id
        """)
        self._build_map("buyers", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("buyers", r["id"]),
                r["submissionid"],
                r["companyname"],
                r["status"],
                r["isspecialbuyer"],
                r["createddate"], r["changeddate"],
                self._map_id("users", r["system$owner"]),
                self._map_id("users", r["system$changedby"]),
                r["entityowner"],
            ))
        lines.append("-- Buyers")
        lines.append(batch_inserts("buyer_mgmt.buyers", [
            "id", "submission_id", "company_name", "status", "is_special_buyer",
            "created_date", "changed_date", "owner_id", "changed_by_id", "entity_owner"
        ], data))

        # --- Buyer ↔ Sales Rep ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_buyermanagement$buyerid" AS buyer_id,
                   "ecoatm_buyermanagement$salesrepresentativeid" AS sr_id
            FROM "ecoatm_buyermanagement$buyer_salesrepresentative"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("buyers", r["buyer_id"]), self._map_id("sales_reps", r["sr_id"]))
                for r in rows
                if self._map_id("buyers", r["buyer_id"]) and self._map_id("sales_reps", r["sr_id"])]
        lines.append("-- Buyer ↔ Sales Rep")
        lines.append(batch_inserts("buyer_mgmt.buyer_sales_reps", ["buyer_id", "sales_rep_id"], data))

        # --- Buyer Codes ---
        rows = fetch_all(self.conn, """
            SELECT id, submissionid, code, buyercodetype, status, budget,
                   softdelete, createddate, changeddate,
                   "system$owner", "system$changedby", entityowner
            FROM "ecoatm_buyermanagement$buyercode"
            ORDER BY id
        """)
        self._build_map("buyer_codes", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("buyer_codes", r["id"]),
                r["submissionid"],
                r["code"],
                r["buyercodetype"],
                r["status"],
                r["budget"],
                r["softdelete"],
                r["createddate"], r["changeddate"],
                self._map_id("users", r["system$owner"]),
                self._map_id("users", r["system$changedby"]),
                r["entityowner"],
            ))
        lines.append("-- Buyer Codes")
        lines.append(batch_inserts("buyer_mgmt.buyer_codes", [
            "id", "submission_id", "code", "buyer_code_type", "status", "budget",
            "soft_delete", "created_date", "changed_date",
            "owner_id", "changed_by_id", "entity_owner"
        ], data))

        # --- Buyer Code ↔ Buyer ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_buyermanagement$buyercodeid" AS bc_id,
                   "ecoatm_buyermanagement$buyerid" AS buyer_id
            FROM "ecoatm_buyermanagement$buyercode_buyer"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("buyer_codes", r["bc_id"]), self._map_id("buyers", r["buyer_id"]))
                for r in rows
                if self._map_id("buyer_codes", r["bc_id"]) and self._map_id("buyers", r["buyer_id"])]
        lines.append("-- Buyer Code ↔ Buyer")
        lines.append(batch_inserts("buyer_mgmt.buyer_code_buyers", ["buyer_code_id", "buyer_id"], data))

        # --- Buyer Code Change Logs ---
        rows = fetch_all(self.conn, """
            SELECT cl.id, cl.oldbuyercodetype, cl.newbuyercodetype,
                   cl.editedby, cl.editedon, cl.createddate, cl.changeddate,
                   cl."system$owner", cl."system$changedby",
                   clbc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id
            FROM "ecoatm_buyermanagement$buyercodechangelog" cl
            LEFT JOIN "ecoatm_buyermanagement$buyercodechangelog_buyercode" clbc
                ON cl.id = clbc."ecoatm_buyermanagement$buyercodechangelogid"
            ORDER BY cl.id
        """)
        self._build_map("change_logs", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("change_logs", r["id"]),
                self._map_id("buyer_codes", r["buyer_code_id"]),
                r["oldbuyercodetype"], r["newbuyercodetype"],
                r["editedby"], r["editedon"],
                r["createddate"], r["changeddate"],
                self._map_id("users", r["system$owner"]),
                self._map_id("users", r["system$changedby"]),
            ))
        lines.append("-- Buyer Code Change Logs")
        lines.append(batch_inserts("buyer_mgmt.buyer_code_change_logs", [
            "id", "buyer_code_id", "old_buyer_code_type", "new_buyer_code_type",
            "edited_by", "edited_on", "created_date", "changed_date",
            "owner_id", "changed_by_id"
        ], data))

        # --- Auctions Feature Config (singleton) ---
        rows = fetch_all(self.conn, """
            SELECT * FROM "ecoatm_buyermanagement$auctionsfeature"
            ORDER BY id LIMIT 1
        """)
        if rows:
            r = rows[0]
            data = [(
                1,  # singleton ID
                r.get("auctionround2minutesoffset", 360),
                r.get("auctionround3minutesoffset", 180),
                r.get("sendbuyertosnowflake", True),
                r.get("sendbiddatatosnowflake", True),
                r.get("sendauctiondatatosnowflake", True),
                r.get("sendbidrankingtosnowflake", False),
                r.get("createexcelbidexport", True),
                r.get("generateround3files", True),
                r.get("calculateround2buyerparticipation", True),
                r.get("sendfilestosharepointonsubmit", True),
                r.get("spretrycount", 3),
                r.get("round2criteriaactive", False),
                r.get("minimumallowedbid") or 2.00,
                False,  # legacy_auction_dashboard_active not in source
                r.get("requirewholesaleuseragreement", False),
                r.get("createddate"), r.get("changeddate"),
                self._map_id("users", r.get("system$owner")),
                self._map_id("users", r.get("system$changedby")),
            )]
            lines.append("-- Auctions Feature Config")
            lines.append(batch_inserts("buyer_mgmt.auctions_feature_config", [
                "id", "auction_round2_minutes_offset", "auction_round3_minutes_offset",
                "send_buyer_to_snowflake", "send_bid_data_to_snowflake",
                "send_auction_data_to_snowflake", "send_bid_ranking_to_snowflake",
                "create_excel_bid_export", "generate_round3_files",
                "calculate_round2_buyer_participation", "send_files_to_sharepoint_on_submit",
                "sp_retry_count", "round2_criteria_active", "minimum_allowed_bid",
                "legacy_auction_dashboard_active", "require_wholesale_user_agreement",
                "created_date", "changed_date", "owner_id", "changed_by_id"
            ], data))

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V19: User management — direct users
    # -----------------------------------------------------------------------
    def gen_v19(self):
        lines = [
            "-- =============================================================================",
            "-- V19: Data migration — ecoATM Direct Users, user-buyer links",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # --- Direct Users ---
        rows = fetch_all(self.conn, """
            SELECT id, submissionid, firstname, lastname,
                   inviteddate, lastinvitesent, activationdate,
                   isbuyerrole, userstatus, inactive, overalluserstatus,
                   landingpagepreference, acknowledgement
            FROM "ecoatm_usermanagement$ecoatmdirectuser"
            ORDER BY id
        """)
        data = []
        for r in rows:
            uid = self._map_id("users", r["id"])
            if not uid:
                continue
            data.append((
                uid,
                r["submissionid"],
                r["firstname"], r["lastname"],
                r["inviteddate"], r["lastinvitesent"], r["activationdate"],
                r["isbuyerrole"],
                r["userstatus"],
                r["inactive"],
                r["overalluserstatus"],
                r["landingpagepreference"],
                r["acknowledgement"],
            ))
        lines.append("-- ecoATM Direct Users")
        lines.append(batch_inserts("user_mgmt.ecoatm_direct_users", [
            "user_id", "submission_id", "first_name", "last_name",
            "invited_date", "last_invite_sent", "activation_date",
            "is_buyer_role", "user_status", "inactive", "overall_user_status",
            "landing_page_preference", "acknowledgement"
        ], data))

        # --- User ↔ Buyer ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_usermanagement$ecoatmdirectuserid" AS user_id,
                   "ecoatm_buyermanagement$buyerid" AS buyer_id
            FROM "ecoatm_usermanagement$ecoatmdirectuser_buyer"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("users", r["user_id"]), self._map_id("buyers", r["buyer_id"]))
                for r in rows
                if self._map_id("users", r["user_id"]) and self._map_id("buyers", r["buyer_id"])]
        lines.append("-- User ↔ Buyer")
        lines.append(batch_inserts("user_mgmt.user_buyers", ["user_id", "buyer_id"], data))

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V20: SSO configuration
    # -----------------------------------------------------------------------
    def gen_v20(self):
        lines = [
            "-- =============================================================================",
            "-- V20: Data migration — SSO configuration (SAML, keystores, certs)",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # --- SAML Authn Contexts ---
        rows = fetch_all(self.conn, """
            SELECT id, description, value, defaultpriority, provisioned, createddate, changeddate
            FROM "saml20$samlauthncontext"
            ORDER BY id
        """)
        self._build_map("authn_ctx", [r["id"] for r in rows])
        data = [(self._map_id("authn_ctx", r["id"]), r["description"], r["value"],
                 r["defaultpriority"], r["provisioned"], r["createddate"], r["changeddate"])
                for r in rows]
        lines.append("-- SAML Authn Contexts")
        lines.append(batch_inserts("sso.saml_authn_contexts", [
            "id", "description", "value", "default_priority", "provisioned", "created_date", "changed_date"
        ], data))

        # --- X.509 Certificates ---
        rows = fetch_all(self.conn, """
            SELECT id, issuername, serialnumber, subject, validfrom, validuntil
            FROM "saml20$x509certificate"
            ORDER BY id
        """)
        self._build_map("certs", [r["id"] for r in rows])
        # Get base64 cert from keyinfo
        cert_data_map = {}
        try:
            cert_links = fetch_all(self.conn, """
                SELECT "saml20$keyinfoid", "saml20$x509certificateid"
                FROM "saml20$keyinfo_x509certificate"
            """)
            for cl in cert_links:
                cert_data_map[cl["saml20$x509certificateid"]] = cl["saml20$keyinfoid"]
        except Exception:
            pass

        data = [(self._map_id("certs", r["id"]), r["issuername"], r["serialnumber"],
                 r["subject"], r["validfrom"], r["validuntil"], None)
                for r in rows]
        lines.append("-- X.509 Certificates")
        lines.append(batch_inserts("sso.x509_certificates", [
            "id", "issuer_name", "serial_number", "subject", "valid_from", "valid_until", "base64_cert"
        ], data))

        # --- Keystores ---
        try:
            rows = fetch_all(self.conn, """
                SELECT kd.id
                FROM "saml20$keydescriptor" kd
                ORDER BY kd.id
            """)
            self._build_map("keystores", [r["id"] for r in rows])
            # Keystores are complex in Mendix; store minimal for now
            data = [(self._map_id("keystores", r["id"]), 'default', None, False, None) for r in rows]
            if data:
                lines.append("-- Keystores (minimal)")
                lines.append(batch_inserts("sso.keystores", [
                    "id", "alias", "password", "rebuild_keystore", "last_changed_on"
                ], data))
        except Exception:
            self._build_map("keystores", [])
            lines.append("-- Keystores: no data found\n")

        # --- IDP Metadata ---
        rows = fetch_all(self.conn, """
            SELECT id, entityid
            FROM "saml20$entitydescriptor"
            ORDER BY id
        """)
        if not rows:
            rows = fetch_all(self.conn, "SELECT id FROM \"saml20$idpmetadata\" ORDER BY id")
        self._build_map("idp_metadata", [r["id"] for r in rows])

        # Get IDP metadata URL from sso config
        try:
            sso_rows = fetch_all(self.conn, 'SELECT idpmetadataurl FROM "saml20$ssoconfiguration" LIMIT 1')
            idp_url = sso_rows[0]["idpmetadataurl"] if sso_rows else None
        except Exception:
            idp_url = None

        data = []
        for r in rows:
            data.append((
                self._map_id("idp_metadata", r["id"]),
                r.get("entityid"),
                idp_url,
                None,  # metadata_xml
                False,
                self._map_id("certs", list(self.id_map.get("certs", {}).keys())[0]) if self.id_map.get("certs") else None,
            ))
        if data:
            lines.append("-- IDP Metadata")
            lines.append(batch_inserts("sso.idp_metadata", [
                "id", "entity_id", "idp_metadata_url", "metadata_xml", "updated", "signing_cert_id"
            ], data))

        # --- SP Metadata ---
        rows = fetch_all(self.conn, """
            SELECT id, entityid, organizationname, organizationdisplayname, organizationurl,
                   contactgivenname, contactsurname, contactemailaddress, applicationurl,
                   doesentityiddifferfromappurl, logavailabledays, useencryption,
                   encryptionmethod, encryptionkeylength
            FROM "saml20$spmetadata"
            ORDER BY id
        """)
        self._build_map("sp_metadata", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("sp_metadata", r["id"]),
                r["entityid"], r.get("organizationname"), r.get("organizationdisplayname"),
                r.get("organizationurl"), r.get("contactgivenname"), r.get("contactsurname"),
                r.get("contactemailaddress"), r.get("applicationurl"),
                r.get("doesentityiddifferfromappurl", False),
                r.get("logavailabledays"), r.get("useencryption", True),
                r.get("encryptionmethod"), r.get("encryptionkeylength"),
                self._map_id("keystores", list(self.id_map.get("keystores", {}).keys())[0]) if self.id_map.get("keystores") else None,
            ))
        if data:
            lines.append("-- SP Metadata")
            lines.append(batch_inserts("sso.sp_metadata", [
                "id", "entity_id", "organization_name", "organization_display_name",
                "organization_url", "contact_given_name", "contact_surname",
                "contact_email_address", "application_url",
                "does_entity_id_differ_from_appurl", "log_available_days", "use_encryption",
                "encryption_method", "encryption_key_length", "keystore_id"
            ], data))

        # --- SSO Configuration ---
        rows = fetch_all(self.conn, """
            SELECT id, alias, active, issamlloggingenabled, authncontext,
                   readidpmetadatafromurl, createusers,
                   allowidpinitiatedauthentication, identifyingassertiontype,
                   customidentifyingassertionname,
                   usecustomlogicforprovisioning, usecustomaftersigninlogic,
                   disablenameidpolicy, enabledelegatedauthentication,
                   delegatedauthenticationurl, enablemobileauthtoken,
                   responseprotocolbinding, enableassertionconsumerserviceindex,
                   assertionconsumerserviceindex, enableforceauthentication,
                   useencryption, encryptionmethod, encryptionkeylength,
                   wizardmode, currentwizardstep
            FROM "saml20$ssoconfiguration"
            ORDER BY id
        """)
        self._build_map("sso_configs", [r["id"] for r in rows])

        # Get claim maps
        claim_maps = {}
        try:
            cm_rows = fetch_all(self.conn, """
                SELECT cms."saml20$ssoconfigurationid" AS config_id,
                       a.name AS attr_name, om.attributename AS user_field
                FROM "saml20$claimmap_ssoconfiguration" cms
                JOIN "saml20$claimmap" cm ON cms."saml20$claimmapid" = cm.id
                LEFT JOIN "saml20$claimmap_attribute" cma ON cm.id = cma."saml20$claimmapid"
                LEFT JOIN "saml20$attribute" a ON cma."saml20$attributeid" = a.id
                LEFT JOIN "saml20$claimmap_mxobjectmember" cmom ON cm.id = cmom."saml20$claimmapid"
                LEFT JOIN mxmodelreflection$mxobjectmember om ON cmom."mxmodelreflection$mxobjectmemberid" = om.id
            """)
            for r in cm_rows:
                cid = r["config_id"]
                if cid not in claim_maps:
                    claim_maps[cid] = []
                claim_maps[cid].append({"attribute_name": r.get("attr_name"), "user_field": r.get("user_field")})
        except Exception:
            pass

        # Get FK references
        try:
            idp_link = fetch_all(self.conn, 'SELECT "saml20$ssoconfigurationid", "saml20$idpmetadataid" FROM "saml20$ssoconfiguration_idpmetadata"')
            idp_link_map = {r["saml20$ssoconfigurationid"]: r["saml20$idpmetadataid"] for r in idp_link}
        except Exception:
            idp_link_map = {}

        try:
            role_link = fetch_all(self.conn, 'SELECT "saml20$ssoconfigurationid", "system$userroleid" FROM "saml20$ssoconfiguration_defaultuserroletoassign"')
            role_link_map = {r["saml20$ssoconfigurationid"]: r["system$userroleid"] for r in role_link}
        except Exception:
            role_link_map = {}

        import json
        data = []
        for r in rows:
            old_id = r["id"]
            cm = json.dumps(claim_maps.get(old_id, []))
            data.append((
                self._map_id("sso_configs", old_id),
                r["alias"], r["active"], r.get("issamlloggingenabled", True),
                r.get("authncontext"), r.get("readidpmetadatafromurl", True),
                r.get("createusers", False), r.get("allowidpinitiatedauthentication", True),
                r.get("identifyingassertiontype"), r.get("customidentifyingassertionname"),
                r.get("usecustomlogicforprovisioning", False), r.get("usecustomaftersigninlogic", False),
                r.get("disablenameidpolicy", False), r.get("enabledelegatedauthentication", False),
                r.get("delegatedauthenticationurl"), r.get("enablemobileauthtoken", False),
                r.get("responseprotocolbinding"), r.get("enableassertionconsumerserviceindex"),
                r.get("assertionconsumerserviceindex", 0), r.get("enableforceauthentication", False),
                r.get("useencryption", True), r.get("encryptionmethod"), r.get("encryptionkeylength"),
                r.get("wizardmode", False), r.get("currentwizardstep"),
                cm,
                self._map_id("idp_metadata", idp_link_map.get(old_id)),
                self._map_id("keystores", list(self.id_map.get("keystores", {}).keys())[0]) if self.id_map.get("keystores") else None,
                self._map_id("roles", role_link_map.get(old_id)),
            ))
        if data:
            lines.append("-- SSO Configuration")
            lines.append(batch_inserts("sso.sso_configurations", [
                "id", "alias", "active", "is_saml_logging_enabled", "authn_context",
                "read_idp_metadata_from_url", "create_users",
                "allow_idp_initiated_authentication", "identifying_assertion_type",
                "custom_identifying_assertion_name",
                "use_custom_logic_for_provisioning", "use_custom_after_signin_logic",
                "disable_nameid_policy", "enable_delegated_authentication",
                "delegated_authentication_url", "enable_mobile_auth_token",
                "response_protocol_binding", "enable_assertion_consumer_service_index",
                "assertion_consumer_service_index", "enable_force_authentication",
                "use_encryption", "encryption_method", "encryption_key_length",
                "wizard_mode", "current_wizard_step", "claim_maps",
                "idp_metadata_id", "keystore_id", "default_role_id"
            ], data))

        # --- Forgot Password Config ---
        rows = fetch_all(self.conn, 'SELECT id FROM "forgotpassword$configuration" ORDER BY id LIMIT 1')
        if rows:
            data = [(1, None, None, None)]
            lines.append("-- Forgot Password Config (singleton)")
            lines.append(batch_inserts("sso.forgot_password_config", [
                "id", "deeplink_identifier", "reset_email_template", "signup_email_template"
            ], data))

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V21: MDM lookups + devices
    # -----------------------------------------------------------------------
    def gen_v21(self):
        lines = [
            "-- =============================================================================",
            "-- V21: Data migration — MDM lookups (brands, categories, models, etc.) + devices",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # Generic lookup extraction — Mendix uses entity name as column (e.g. "brand", "model")
        lookups = [
            ("brand",     "ecoatm_pwsmdm$brand",     "brand"),
            ("category",  "ecoatm_pwsmdm$category",  "category"),
            ("model",     "ecoatm_pwsmdm$model",     "model"),
            ("condition", "ecoatm_pwsmdm$condition",  "condition"),
            ("capacity",  "ecoatm_pwsmdm$capacity",  "capacity"),
            ("carrier",   "ecoatm_pwsmdm$carrier",   "carrier"),
            ("color",     "ecoatm_pwsmdm$color",     "color"),
            ("grade",     "ecoatm_pwsmdm$grade",     "grade"),
        ]

        for entity, source_table, name_col in lookups:
            try:
                rows = fetch_all(self.conn, f"""
                    SELECT id, "{name_col}" AS name, displayname,
                           isenabledforfilter AS isenabled, rank AS sortrank
                    FROM "{source_table}"
                    ORDER BY id
                """)
            except Exception:
                try:
                    rows = fetch_all(self.conn, f'SELECT id, "{name_col}" AS name FROM "{source_table}" ORDER BY id')
                    for r in rows:
                        r["displayname"] = r.get("name")
                        r["isenabled"] = True
                        r["sortrank"] = None
                except Exception:
                    lines.append(f"-- {entity}: source table not found or empty\n")
                    self._build_map(entity, [])
                    continue

            self._build_map(entity, [r["id"] for r in rows])
            data = [(self._map_id(entity, r["id"]), r.get("name"), r.get("displayname", r.get("name")),
                     r.get("isenabled", True), r.get("sortrank"))
                    for r in rows]

            lines.append(f"-- MDM: {entity}")
            lines.append(batch_inserts(f"mdm.{entity}", [
                "id", "name", "display_name", "is_enabled", "sort_rank"
            ], data))

        # --- Devices ---
        # Devices in Mendix use junction tables for FK relationships
        # We need to resolve: device_brand, device_category, device_model, etc.
        junction_tables = {
            "brand":     "ecoatm_pwsmdm$device_brand",
            "category":  "ecoatm_pwsmdm$device_category",
            "model":     "ecoatm_pwsmdm$device_model",
            "condition": "ecoatm_pwsmdm$device_condition",
            "capacity":  "ecoatm_pwsmdm$device_capacity",
            "carrier":   "ecoatm_pwsmdm$device_carrier",
            "color":     "ecoatm_pwsmdm$device_color",
            "grade":     "ecoatm_pwsmdm$device_grade",
        }

        # Build device FK maps
        device_fks = {}  # device_id -> {brand_id, category_id, ...}
        for fk_name, jt in junction_tables.items():
            try:
                jt_rows = fetch_all(self.conn, f"""
                    SELECT "ecoatm_pwsmdm$deviceid" AS device_id,
                           "ecoatm_pwsmdm${fk_name}id" AS fk_id
                    FROM "{jt}"
                """)
                for r in jt_rows:
                    did = r["device_id"]
                    if did not in device_fks:
                        device_fks[did] = {}
                    device_fks[did][f"{fk_name}_id"] = self._map_id(fk_name, r["fk_id"])
            except Exception as e:
                lines.append(f"-- WARNING: Could not read junction {jt}: {e}\n")

        # Fetch devices
        rows = fetch_all(self.conn, """
            SELECT id, sku, devicecode, devicedescription,
                   currentlistprice, currentminprice, futurelistprice, futureminprice,
                   availableqty, reservedqty, atpqty, weight, itemtype,
                   isactive, lastsynctime, createddate, changeddate
            FROM "ecoatm_pwsmdm$device"
            ORDER BY id
        """)
        self._build_map("devices", [r["id"] for r in rows])

        data = []
        for r in rows:
            old_id = r["id"]
            fks = device_fks.get(old_id, {})
            # Convert integer prices to decimal (Mendix stores as cents in some cases)
            list_price = r.get("currentlistprice") or 0
            min_price = r.get("currentminprice") or 0
            future_list = r.get("futurelistprice") or 0
            future_min = r.get("futureminprice") or 0

            data.append((
                self._map_id("devices", old_id),
                old_id,  # legacy_id
                r["sku"], r.get("devicecode"), r.get("devicedescription"),
                list_price, min_price, future_list, future_min,
                r.get("availableqty", 0), r.get("reservedqty", 0), r.get("atpqty", 0),
                r.get("weight", 0), r.get("itemtype"),
                r.get("isactive", True),
                fks.get("brand_id"), fks.get("category_id"), fks.get("model_id"),
                fks.get("condition_id"), fks.get("capacity_id"), fks.get("carrier_id"),
                fks.get("color_id"), fks.get("grade_id"),
                r.get("lastsynctime"),
                r.get("createddate"), r.get("changeddate"),
            ))

        lines.append("-- Devices")
        lines.append(batch_inserts("mdm.device", [
            "id", "legacy_id", "sku", "device_code", "description",
            "list_price", "min_price", "future_list_price", "future_min_price",
            "available_qty", "reserved_qty", "atp_qty", "weight", "item_type",
            "is_active",
            "brand_id", "category_id", "model_id",
            "condition_id", "capacity_id", "carrier_id",
            "color_id", "grade_id",
            "last_sync_time", "created_date", "updated_date"
        ], data))

        # --- Price History ---
        rows = fetch_all(self.conn, """
            SELECT ph.id, ph.listprice, ph.minprice, ph.expirationdate,
                   ph.createddate, ph.changeddate,
                   phd."ecoatm_pwsmdm$deviceid" AS device_id
            FROM "ecoatm_pwsmdm$pricehistory" ph
            LEFT JOIN "ecoatm_pwsmdm$pricehistory_devicelist" phd
                ON ph.id = phd."ecoatm_pwsmdm$pricehistoryid"
            ORDER BY ph.id
        """)
        self._build_map("price_history", [r["id"] for r in rows])
        data = []
        for r in rows:
            data.append((
                self._map_id("price_history", r["id"]),
                r["id"],  # legacy_id
                self._map_id("devices", r.get("device_id")),
                r.get("listprice"), r.get("minprice"), r.get("expirationdate"),
                r.get("createddate"), r.get("changeddate"),
            ))
        lines.append("-- Price History")
        lines.append(batch_inserts("mdm.price_history", [
            "id", "legacy_id", "device_id", "list_price", "min_price", "expiration_date",
            "created_date", "updated_date"
        ], data))

        # Reset sequences
        lines.append("")
        for entity in ["brand", "category", "model", "condition", "capacity", "carrier", "color", "grade"]:
            max_id = max(self.id_map.get(entity, {0: 0}).values()) if self.id_map.get(entity) else 0
            lines.append(f"SELECT setval('mdm.{entity}_id_seq', {max(max_id, 1)}, true);")
        max_dev = max(self.id_map.get("devices", {0: 0}).values()) if self.id_map.get("devices") else 0
        lines.append(f"SELECT setval('mdm.device_id_seq', {max(max_dev, 1)}, true);")
        max_ph = max(self.id_map.get("price_history", {0: 0}).values()) if self.id_map.get("price_history") else 0
        lines.append(f"SELECT setval('mdm.price_history_id_seq', {max(max_ph, 1)}, true);")

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V22: PWS — offers, items, orders, shipments
    # -----------------------------------------------------------------------
    def gen_v22(self):
        lines = [
            "-- =============================================================================",
            "-- V22: Data migration — PWS offers, offer items, orders, shipments",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # --- System Offers (ecoatm_pws$offer) ---
        rows = fetch_all(self.conn, """
            SELECT o.id, o.offerstatus AS status, o.offertotalquantity AS totalqty,
                   o.offertotalprice AS totalprice,
                   o.offersubmissiondate AS submissiondate,
                   o.salesreviewcompletedon, o.offercancelledon AS canceledon,
                   o.createddate, o.changeddate,
                   obc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id,
                   osr."ecoatm_buyermanagement$salesrepresentativeid" AS sales_rep_id
            FROM "ecoatm_pws$offer" o
            LEFT JOIN "ecoatm_pws$offer_buyercode" obc
                ON o.id = obc."ecoatm_pws$offerid"
            LEFT JOIN "ecoatm_pws$offer_salesrepresentative" osr
                ON o.id = osr."ecoatm_pws$offerid"
            ORDER BY o.id
        """)
        self._build_map("sys_offers", [r["id"] for r in rows])

        # --- Buyer Offers (ecoatm_pws$buyeroffer) ---
        buyer_offers = fetch_all(self.conn, """
            SELECT bo.id, bo.offerstatus AS status, bo.offerquantity AS totalqty,
                   bo.offertotal AS totalprice,
                   bo.createddate AS submissiondate, bo.createddate, bo.changeddate,
                   bobc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id
            FROM "ecoatm_pws$buyeroffer" bo
            LEFT JOIN "ecoatm_pws$buyeroffer_buyercode" bobc
                ON bo.id = bobc."ecoatm_pws$buyerofferid"
            ORDER BY bo.id
        """)
        self._build_map("buyer_offers", [r["id"] for r in buyer_offers])

        # Unified offers: system offers first, then buyer offers
        offer_data = []
        # unified offer ID counter
        offer_counter = 1
        unified_offer_map = {}  # (type, old_id) -> new_id

        for r in rows:
            unified_offer_map[("SYSTEM", r["id"])] = offer_counter
            offer_data.append((
                offer_counter, r["id"],  # id, legacy_id
                "SYSTEM", r.get("status"),
                r.get("totalqty", 0), r.get("totalprice", 0),
                self._map_id("buyer_codes", r.get("buyer_code_id")),
                self._map_id("sales_reps", r.get("sales_rep_id")),
                r.get("submissiondate"), r.get("salesreviewcompletedon"), r.get("canceledon"),
                r.get("createddate"), r.get("changeddate"),
            ))
            offer_counter += 1

        for r in buyer_offers:
            unified_offer_map[("BUYER", r["id"])] = offer_counter
            offer_data.append((
                offer_counter, r["id"],
                "BUYER", r.get("status"),
                r.get("totalqty", 0), r.get("totalprice", 0),
                self._map_id("buyer_codes", r.get("buyer_code_id")),
                None,  # no sales_rep for buyer offers
                r.get("submissiondate"), None, None,
                r.get("createddate"), r.get("changeddate"),
            ))
            offer_counter += 1

        lines.append("-- Offers (unified from system + buyer offers)")
        lines.append(batch_inserts("pws.offer", [
            "id", "legacy_id", "offer_type", "status",
            "total_qty", "total_price",
            "buyer_code_id", "sales_rep_id",
            "submission_date", "sales_review_completed_on", "canceled_on",
            "created_date", "updated_date"
        ], offer_data))

        # --- Offer Items (system) ---
        sys_items = fetch_all(self.conn, """
            SELECT oi.id, oi.offerquantity AS quantity, oi.offerprice AS price,
                   oi.offertotalprice AS totalprice,
                   oi.counterquantity AS counterqty, oi.counterprice, oi.countertotal,
                   oi.salesofferitemstatus AS itemstatus, oi.createddate, oi.changeddate,
                   oid."ecoatm_pwsmdm$deviceid" AS device_id,
                   oio."ecoatm_pws$offerid" AS offer_id,
                   oibc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id
            FROM "ecoatm_pws$offeritem" oi
            LEFT JOIN "ecoatm_pws$offeritem_device" oid ON oi.id = oid."ecoatm_pws$offeritemid"
            LEFT JOIN "ecoatm_pws$offeritem_offer" oio ON oi.id = oio."ecoatm_pws$offeritemid"
            LEFT JOIN "ecoatm_pws$offeritem_buyercode" oibc ON oi.id = oibc."ecoatm_pws$offeritemid"
            ORDER BY oi.id
        """)

        # --- Buyer Offer Items ---
        buyer_items = fetch_all(self.conn, """
            SELECT boi.id, boi.quantity, boi.offerprice AS price,
                   boi.totalprice,
                   0 AS counterqty, 0 AS counterprice, 0 AS countertotal,
                   NULL AS itemstatus, boi.createddate, boi.changeddate,
                   boid."ecoatm_pwsmdm$deviceid" AS device_id,
                   boibo."ecoatm_pws$buyerofferid" AS offer_id,
                   boibc."ecoatm_buyermanagement$buyercodeid" AS buyer_code_id
            FROM "ecoatm_pws$buyerofferitem" boi
            LEFT JOIN "ecoatm_pws$buyerofferitem_device" boid ON boi.id = boid."ecoatm_pws$buyerofferitemid"
            LEFT JOIN "ecoatm_pws$buyerofferitem_buyeroffer" boibo ON boi.id = boibo."ecoatm_pws$buyerofferitemid"
            LEFT JOIN "ecoatm_pws$buyerofferitem_buyercode" boibc ON boi.id = boibc."ecoatm_pws$buyerofferitemid"
            ORDER BY boi.id
        """)

        item_data = []
        item_counter = 1

        for r in sys_items:
            mapped_offer = unified_offer_map.get(("SYSTEM", r.get("offer_id")))
            if not mapped_offer:
                continue  # skip orphaned items with no offer FK
            dev_id = self._map_id("devices", r.get("device_id"))
            item_data.append((
                item_counter, r["id"],
                mapped_offer,
                None,  # sku (could resolve from device)
                dev_id,
                r.get("quantity", 0), r.get("price", 0), r.get("totalprice", 0),
                r.get("counterqty", 0), r.get("counterprice", 0), r.get("countertotal", 0),
                r.get("itemstatus"),
                r.get("createddate"), r.get("changeddate"),
            ))
            item_counter += 1

        for r in buyer_items:
            mapped_offer = unified_offer_map.get(("BUYER", r.get("offer_id")))
            if not mapped_offer:
                continue  # skip orphaned items with no offer FK
            dev_id = self._map_id("devices", r.get("device_id"))
            item_data.append((
                item_counter, r["id"],
                mapped_offer,
                None, dev_id,
                r.get("quantity", 0), r.get("price", 0), r.get("totalprice", 0),
                r.get("counterqty", 0), r.get("counterprice", 0), r.get("countertotal", 0),
                r.get("itemstatus"),
                r.get("createddate"), r.get("changeddate"),
            ))
            item_counter += 1

        lines.append("-- Offer Items (unified)")
        lines.append(batch_inserts("pws.offer_item", [
            "id", "legacy_id", "offer_id", "sku", "device_id",
            "quantity", "price", "total_price",
            "counter_qty", "counter_price", "counter_total",
            "item_status", "created_date", "updated_date"
        ], item_data))

        # --- Orders ---
        order_rows = fetch_all(self.conn, """
            SELECT o.id, o.ordernumber, o.orderline,
                   o.oracleorderstatus AS orderstatus, o.oracleorderstatus AS oraclestatus,
                   o.shipmethod,
                   o.shippedtotalquantity AS shippedtotalqty,
                   o.shippedtotalprice,
                   o.orderdate, o.shipdate,
                   o.oraclehttpcode,
                   o.createddate, o.changeddate,
                   oo."ecoatm_pws$offerid" AS offer_id
            FROM "ecoatm_pws$order" o
            LEFT JOIN "ecoatm_pws$offer_order" oo ON o.id = oo."ecoatm_pws$orderid"
            ORDER BY o.id
        """)
        self._build_map("orders", [r["id"] for r in order_rows])

        order_data = []
        for r in order_rows:
            order_data.append((
                self._map_id("orders", r["id"]),
                r["id"],  # legacy_id
                unified_offer_map.get(("SYSTEM", r.get("offer_id"))),
                r.get("ordernumber"), r.get("orderline"),
                r.get("orderstatus"), r.get("oraclestatus"), r.get("shipmethod"),
                r.get("shippedtotalqty", 0), r.get("shippedtotalprice", 0),
                r.get("orderdate"), r.get("shipdate"),
                r.get("oraclehttpcode"),
                r.get("createddate"), r.get("changeddate"),
            ))

        lines.append("-- Orders")
        lines.append(batch_inserts("pws.\"order\"", [
            "id", "legacy_id", "offer_id",
            "order_number", "order_line",
            "order_status", "oracle_status", "ship_method",
            "shipped_total_qty", "shipped_total_price",
            "order_date", "ship_date",
            "oracle_http_code",
            "created_date", "updated_date"
        ], order_data))

        # --- Shipment Details ---
        ship_rows = fetch_all(self.conn, """
            SELECT sd.id, sd.trackingnumber, sd.trackingurl,
                   sd.skucount, sd.quantity,
                   sd.createddate, sd.changeddate,
                   sd."ecoatm_pws$shipmentdetail_order" AS order_id
            FROM "ecoatm_pws$shipmentdetail" sd
            ORDER BY sd.id
        """)
        # order_id is a direct FK column on shipmentdetail
        ship_order_map = {r["id"]: r["order_id"] for r in ship_rows if r.get("order_id")}

        self._build_map("shipments", [r["id"] for r in ship_rows])
        ship_data = []
        for r in ship_rows:
            ship_data.append((
                self._map_id("shipments", r["id"]),
                r["id"],  # legacy_id
                self._map_id("orders", ship_order_map.get(r["id"])),
                r.get("trackingnumber"), r.get("trackingurl"),
                r.get("skucount", 0), r.get("quantity", 0),
                r.get("createddate"), r.get("changeddate"),
            ))

        lines.append("-- Shipment Details")
        lines.append(batch_inserts("pws.shipment_detail", [
            "id", "legacy_id", "order_id",
            "tracking_number", "tracking_url",
            "sku_count", "quantity",
            "created_date", "updated_date"
        ], ship_data))

        # Reset sequences
        lines.append("")
        lines.append(f"SELECT setval('pws.offer_id_seq', {max(offer_counter - 1, 1)}, true);")
        lines.append(f"SELECT setval('pws.offer_item_id_seq', {max(item_counter - 1, 1)}, true);")
        max_ord = max(self.id_map.get("orders", {0: 0}).values()) if self.id_map.get("orders") else 0
        lines.append(f"SELECT setval('pws.order_id_seq', {max(max_ord, 1)}, true);")
        max_ship = max(self.id_map.get("shipments", {0: 0}).values()) if self.id_map.get("shipments") else 0
        lines.append(f"SELECT setval('pws.shipment_detail_id_seq', {max(max_ship, 1)}, true);")

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V23: Qualified buyer codes (large table — 378K rows)
    # -----------------------------------------------------------------------
    def gen_v23(self):
        lines = [
            "-- =============================================================================",
            "-- V23: Data migration — Qualified buyer codes (large dataset)",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- WARNING: ~378K rows — this migration may take a few minutes",
            "-- =============================================================================",
            "",
        ]

        # --- Qualified Buyer Codes ---
        rows = fetch_all(self.conn, """
            SELECT id, qualificationtype, included, submitted,
                   submitteddatetime, openeddashboard, openeddashboarddatetime,
                   isspecialtreatment, createddate, changeddate,
                   "system$owner", "system$changedby"
            FROM "ecoatm_buyermanagement$qualifiedbuyercodes"
            ORDER BY id
        """)
        self._build_map("qbc", [r["id"] for r in rows])

        data = []
        for r in rows:
            data.append((
                self._map_id("qbc", r["id"]),
                r.get("qualificationtype", "Not_Qualified"),
                r.get("included", False),
                r.get("submitted", False),
                r.get("submitteddatetime"),
                r.get("openeddashboard", False),
                r.get("openeddashboarddatetime"),
                r.get("isspecialtreatment", False),
                r.get("createddate"), r.get("changeddate"),
                self._map_id("users", r.get("system$owner")),
                self._map_id("users", r.get("system$changedby")),
            ))

        lines.append("-- Qualified Buyer Codes")
        lines.append(batch_inserts("buyer_mgmt.qualified_buyer_codes", [
            "id", "qualification_type", "included", "submitted",
            "submitted_datetime", "opened_dashboard", "opened_dashboard_datetime",
            "is_special_treatment", "created_date", "changed_date",
            "owner_id", "changed_by_id"
        ], data, batch_size=500))

        # --- QBC ↔ Buyer Code ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid" AS qbc_id,
                   "ecoatm_buyermanagement$buyercodeid" AS bc_id
            FROM "ecoatm_buyermanagement$qualifiedbuyercodes_buyercode"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("qbc", r["qbc_id"]), self._map_id("buyer_codes", r["bc_id"]))
                for r in rows
                if self._map_id("qbc", r["qbc_id"]) and self._map_id("buyer_codes", r["bc_id"])]
        lines.append("-- QBC ↔ Buyer Code")
        lines.append(batch_inserts("buyer_mgmt.qbc_buyer_codes", [
            "qualified_buyer_code_id", "buyer_code_id"
        ], data, batch_size=500))

        # --- QBC ↔ Scheduling Auction (FK deferred) ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid" AS qbc_id,
                   "auctionui$schedulingauctionid" AS sa_id
            FROM "ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("qbc", r["qbc_id"]), r["sa_id"])
                for r in rows if self._map_id("qbc", r["qbc_id"])]
        lines.append("-- QBC ↔ Scheduling Auction (auction IDs kept as-is, FK deferred)")
        lines.append(batch_inserts("buyer_mgmt.qbc_scheduling_auctions", [
            "qualified_buyer_code_id", "scheduling_auction_id"
        ], data))

        # --- QBC ↔ Bid Round (FK deferred) ---
        rows = fetch_all(self.conn, """
            SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid" AS qbc_id,
                   "auctionui$bidroundid" AS br_id
            FROM "ecoatm_buyermanagement$qualifiedbuyercodes_bidround"
            ORDER BY 1, 2
        """)
        data = [(self._map_id("qbc", r["qbc_id"]), r["br_id"])
                for r in rows if self._map_id("qbc", r["qbc_id"])]
        lines.append("-- QBC ↔ Bid Round (auction IDs kept as-is, FK deferred)")
        lines.append(batch_inserts("buyer_mgmt.qbc_bid_rounds", [
            "qualified_buyer_code_id", "bid_round_id"
        ], data))

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V24: Integration config
    # -----------------------------------------------------------------------
    def gen_v24(self):
        lines = [
            "-- =============================================================================",
            "-- V24: Data migration — Integration configuration (Deposco, error mappings)",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
        ]

        # --- Deposco Config ---
        try:
            rows = fetch_all(self.conn, """
                SELECT id, baseurl, username, password, lastsynctime
                FROM "ecoatm_pwsintegration$deposcoconfig"
                ORDER BY id LIMIT 1
            """)
            if rows:
                r = rows[0]
                data = [(1, r["id"], r.get("baseurl"), r.get("username"),
                         r.get("password"), r.get("lastsynctime"),
                         5000, True, None)]
                lines.append("-- Deposco Config")
                lines.append(batch_inserts("integration.deposco_config", [
                    "id", "legacy_id", "base_url", "username", "password_hash",
                    "last_sync_time", "timeout_ms", "is_active", "updated_date"
                ], data))
        except Exception as e:
            lines.append(f"-- Deposco config: {e}\n")

        # --- Error Mappings (from pwsresponseconfig) ---
        try:
            rows = fetch_all(self.conn, """
                SELECT id, sourcesystem, sourceerrorcode, sourceerrortype,
                       usererrorcode, usererrormessage, bypassforuser
                FROM "ecoatm_pwsintegration$pwsresponseconfig"
                ORDER BY id
            """)
            data = []
            for i, r in enumerate(rows, 1):
                data.append((
                    i, r.get("sourcesystem"), r.get("sourceerrorcode"),
                    r.get("sourceerrortype"), r.get("usererrorcode"),
                    r.get("usererrormessage"), r.get("bypassforuser", False),
                ))
            if data:
                lines.append("-- Error Mappings")
                lines.append(batch_inserts("integration.error_mapping", [
                    "id", "source_system", "source_error_code", "source_error_type",
                    "user_error_code", "user_error_message", "bypass_for_user"
                ], data))
        except Exception as e:
            lines.append(f"-- Error mappings: {e}\n")

        # Reset sequences
        lines.append("")
        lines.append("SELECT setval('integration.deposco_config_id_seq', 1, true);")
        lines.append(f"SELECT setval('integration.error_mapping_id_seq', {max(len(data) if 'data' in dir() else 0, 1)}, true);")

        return "\n".join(lines)

    # -----------------------------------------------------------------------
    # V34: RMA data — statuses, reasons, RMAs, RMA items
    # -----------------------------------------------------------------------
    def gen_v34(self):
        lines = [
            "-- =============================================================================",
            "-- V34: Data migration -- RMA statuses, reasons, RMAs, RMA items",
            f"-- Generated from QA database on {datetime.now().strftime('%Y-%m-%d %H:%M')}",
            "-- =============================================================================",
            "",
            "-- Clear V33 seed data (replaced with production data below)",
            "DELETE FROM pws.rma_item;",
            "DELETE FROM pws.rma;",
            "DELETE FROM pws.rma_reason;",
            "DELETE FROM pws.rma_status;",
            "",
        ]

        # --- RMA Statuses ---
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

        status_data = []
        for r in rows:
            status_data.append((
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
        lines.append("-- RMA Statuses")
        lines.append(batch_inserts("pws.rma_status", [
            "id", "system_status", "internal_status_text", "external_status_text",
            "internal_status_hex_code", "external_status_hex_code",
            "sales_status_header_hex_code", "sales_table_hover_hex_code",
            "status_grouped_to", "sort_order", "is_default",
            "description", "status_verbiage_bidder",
            "created_date", "updated_date",
        ], status_data))

        # --- RMA Reasons ---
        rows = fetch_all(self.conn, """
            SELECT id, validreasons, isactive, createddate, changeddate
            FROM "ecoatm_rma$rmareasons"
            ORDER BY id
        """)
        self._build_map("rma_reasons", [r["id"] for r in rows])

        reason_data = []
        for r in rows:
            reason_data.append((
                self._map_id("rma_reasons", r["id"]),
                r.get("validreasons"),
                r.get("isactive", True),
                r.get("createddate"),
                r.get("changeddate"),
            ))
        lines.append("-- RMA Reasons")
        lines.append(batch_inserts("pws.rma_reason", [
            "id", "valid_reasons", "is_active", "created_date", "updated_date",
        ], reason_data))

        # --- RMAs (with FK resolution from junction tables) ---
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

        rma_data = []
        for r in rows:
            rma_data.append((
                self._map_id("rmas", r["id"]),
                r.get("number"),
                self._map_id("buyer_codes", r.get("buyer_code_id")),
                self._map_id("users", r.get("submitted_by_id")),
                self._map_id("users", r.get("reviewed_by_id")),
                self._map_id("rma_statuses", r.get("rma_status_id")),
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

        # --- RMA Items (with FK resolution from junction tables) ---
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

        item_data = []
        skipped = 0
        for r in rows:
            mapped_rma = self._map_id("rmas", r.get("rma_id"))
            if not mapped_rma:
                skipped += 1
                continue  # skip orphaned items with no RMA FK
            item_data.append((
                len(item_data) + 1,
                mapped_rma,
                self._map_id("devices", r.get("device_id")),
                self._map_id("orders", r.get("order_id")),
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

        if skipped:
            print(f"  Note: {skipped} orphaned rma_items skipped (no RMA FK)")

        # Reset sequences
        max_status = max(self.id_map.get("rma_statuses", {0: 0}).values()) if self.id_map.get("rma_statuses") else 0
        max_reason = max(self.id_map.get("rma_reasons", {0: 0}).values()) if self.id_map.get("rma_reasons") else 0
        max_rma = max(self.id_map.get("rmas", {0: 0}).values()) if self.id_map.get("rmas") else 0
        lines.append("")
        lines.append(f"SELECT setval('pws.rma_status_id_seq', {max(max_status, 1)}, true);")
        lines.append(f"SELECT setval('pws.rma_reason_id_seq', {max(max_reason, 1)}, true);")
        lines.append(f"SELECT setval('pws.rma_id_seq', {max(max_rma, 1)}, true);")
        lines.append(f"SELECT setval('pws.rma_item_id_seq', {max(len(item_data), 1)}, true);")

        return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Extract Mendix QA data and generate Flyway migrations")
    parser.add_argument("--source-db", required=True, help="Source database name (e.g. qa-0327)")
    parser.add_argument("--output-dir", default="../backend/src/main/resources/db/migration",
                        help="Output directory for SQL migration files")
    args = parser.parse_args()

    output_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), args.output_dir))
    os.makedirs(output_dir, exist_ok=True)

    print(f"Connecting to {args.source_db}...")
    conn = connect(args.source_db)
    gen = MigrationGenerator(conn)

    migrations = OrderedDict([
        ("V16__data_identity_core.sql",       gen.gen_v16),
        ("V17__data_identity_users.sql",      gen.gen_v17),
        ("V18__data_buyer_mgmt_core.sql",     gen.gen_v18),
        ("V19__data_user_mgmt.sql",           gen.gen_v19),
        ("V20__data_sso_config.sql",          gen.gen_v20),
        ("V21__data_mdm.sql",                 gen.gen_v21),
        ("V22__data_pws.sql",                 gen.gen_v22),
        ("V23__data_qualified_buyer_codes.sql", gen.gen_v23),
        ("V24__data_integration_config.sql",  gen.gen_v24),
        ("V34__data_rma.sql",                  gen.gen_v34),
    ])

    for filename, gen_func in migrations.items():
        print(f"Generating {filename}...")
        try:
            sql = gen_func()
            filepath = os.path.join(output_dir, filename)
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(sql)
            print(f"  OK Written to {filepath}")
        except Exception as e:
            print(f"  FAIL ERROR: {e}")
            import traceback
            traceback.print_exc()

    conn.close()
    print("\nDone! Run 'mvn spring-boot:run' to apply migrations via Flyway.")


if __name__ == "__main__":
    main()
