#!/usr/bin/env python3
"""
Data Migration Generator — Extracts from Mendix source DB, generates Flyway SQL.
New sequential IDs are assigned; Mendix IDs are NOT preserved.

Usage:  python scripts/generate_data_migrations.py --source-db qa-0327
"""
import argparse, os, sys
from datetime import datetime
from collections import defaultdict
import psycopg2, psycopg2.extras

OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "backend", "src", "main", "resources", "db", "migration")
BATCH = 500


def connect(dbname):
    return psycopg2.connect(host="localhost", dbname=dbname, user="postgres", password="Agarwal1$")


def q(conn, sql):
    with conn.cursor() as cur:
        cur.execute(sql)
        return cur.fetchall()


def sv(v):
    if v is None: return "NULL"
    if isinstance(v, bool): return "true" if v else "false"
    if isinstance(v, (int, float)): return str(v)
    s = str(v).replace("'", "''")
    return f"'{s}'"


def write_ins(f, tbl, cols, rows):
    if not rows:
        f.write(f"-- No data for {tbl}\n\n"); return
    cl = ", ".join(cols)
    for i in range(0, len(rows), BATCH):
        batch = rows[i:i+BATCH]
        f.write(f"INSERT INTO {tbl} ({cl}) VALUES\n")
        f.write(",\n".join("  (" + ", ".join(sv(c) for c in r) + ")" for r in batch))
        f.write(";\n\n")


def idmap(rows, start=1):
    m = {}
    for i, r in enumerate(rows, start):
        m[r[0]] = i
    return m


def fk(val, m):
    if val is None: return None
    return m.get(val)


# ── V16: Identity ─────────────────────────────────────────────────────────────
def gen_identity(conn, f, M):
    f.write(f"-- V16: Identity data migration | Source: {conn.info.dbname} | {datetime.now().isoformat()}\n\n")
    f.write("DELETE FROM identity.user_role_assignments WHERE user_id >= 9000;\n")
    f.write("DELETE FROM identity.accounts WHERE user_id >= 9000;\n")
    f.write("DELETE FROM identity.users WHERE id >= 9000;\n")
    f.write("DELETE FROM identity.user_roles WHERE id >= 1000;\n\n")

    # Languages
    rows = q(conn, 'SELECT id, code, description FROM "system$language" ORDER BY id')
    lm = idmap(rows); M["lang"] = lm
    write_ins(f, "identity.languages", ["id","code","description"],
              [(lm[r[0]], r[1], r[2]) for r in rows])

    # Timezones
    rows = q(conn, 'SELECT id, code, description, rawoffset FROM "system$timezone" ORDER BY id')
    tm = idmap(rows); M["tz"] = tm
    write_ins(f, "identity.timezones", ["id","code","description","raw_offset"],
              [(tm[r[0]], r[1], r[2], r[3]) for r in rows])

    # User roles
    rows = q(conn, 'SELECT id, modelguid, name, description FROM "system$userrole" ORDER BY id')
    rm = idmap(rows); M["role"] = rm
    write_ins(f, "identity.user_roles", ["id","model_guid","name","description"],
              [(rm[r[0]], r[1], r[2], r[3]) for r in rows])

    # Grantable roles
    rows = q(conn, 'SELECT "system$userroleid1", "system$userroleid2" FROM "system$grantableroles"')
    write_ins(f, "identity.grantable_roles", ["grantor_role_id","grantee_role_id"],
              [(fk(r[0],rm), fk(r[1],rm)) for r in rows if fk(r[0],rm) and fk(r[1],rm)])

    # Users
    rows = q(conn, '''SELECT id, submetaobjectname, name, password, lastlogin,
        blocked, blockedsince, active, failedlogins, webserviceuser, isanonymous,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "system$user" ORDER BY id''')
    um = idmap(rows); M["user"] = um
    mapped = []
    for r in rows:
        ut = r[1] or "System.User"
        if "EcoATMDirectUser" in ut: ut = "EcoATM_UserManagement.EcoATMDirectUser"
        elif "Account" in ut: ut = "Administration.Account"
        else: ut = "System.User"
        mapped.append((um[r[0]], ut, r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10],
                        r[11], r[12], fk(r[13],um), fk(r[14],um)))
    write_ins(f, "identity.users",
        ["id","user_type","name","password","last_login","blocked","blocked_since",
         "active","failed_logins","web_service_user","is_anonymous",
         "created_date","changed_date","owner_id","changed_by_id"], mapped)

    # User-Language
    rows = q(conn, 'SELECT "system$userid", "system$languageid" FROM "system$user_language"')
    write_ins(f, "identity.user_languages", ["user_id","language_id"],
              [(fk(r[0],um), fk(r[1],lm)) for r in rows if fk(r[0],um) and fk(r[1],lm)])

    # User-Timezone
    rows = q(conn, 'SELECT "system$userid", "system$timezoneid" FROM "system$user_timezone"')
    write_ins(f, "identity.user_timezones", ["user_id","timezone_id"],
              [(fk(r[0],um), fk(r[1],tm)) for r in rows if fk(r[0],um) and fk(r[1],tm)])

    # User role assignments
    rows = q(conn, 'SELECT "system$userid", "system$userroleid" FROM "system$userroles"')
    write_ins(f, "identity.user_role_assignments", ["user_id","role_id"],
              [(fk(r[0],um), fk(r[1],rm)) for r in rows if fk(r[0],um) and fk(r[1],rm)])

    # Accounts
    rows = q(conn, 'SELECT id, fullname, email, islocaluser FROM "administration$account" ORDER BY id')
    write_ins(f, "identity.accounts", ["user_id","full_name","email","is_local_user"],
              [(fk(r[0],um), r[1], r[2], r[3]) for r in rows if fk(r[0],um)])


# ── V17: User Management ─────────────────────────────────────────────────────
def gen_user_mgmt(conn, f, M):
    um = M["user"]
    f.write(f"-- V17: User Management data migration | {datetime.now().isoformat()}\n\n")

    # ecoATM Direct Users (no createddate/changeddate/owner in source)
    rows = q(conn, '''SELECT id, submissionid, firstname, lastname,
        inviteddate, lastinvitesent, activationdate,
        isbuyerrole, userstatus, inactive, overalluserstatus,
        landingpagepreference, acknowledgement
        FROM "ecoatm_usermanagement$ecoatmdirectuser" ORDER BY id''')
    mapped = []
    for r in rows:
        uid = fk(r[0], um)
        if not uid: continue
        mapped.append((uid, r[1], r[2], r[3], r[4], r[5], r[6],
                        r[7], r[8], r[9], r[10], r[11], r[12]))
    write_ins(f, "user_mgmt.ecoatm_direct_users",
        ["user_id","submission_id","first_name","last_name",
         "invited_date","last_invite_sent","activation_date",
         "is_buyer_role","user_status","inactive","overall_user_status",
         "landing_page_preference","acknowledgement"], mapped)

    # Store raw user-buyer for deferred resolution
    M["_raw_ub"] = q(conn,
        'SELECT "ecoatm_usermanagement$ecoatmdirectuserid", "ecoatm_buyermanagement$buyerid" '
        'FROM "ecoatm_usermanagement$ecoatmdirectuser_buyer"')


# ── V18: Buyer Management ────────────────────────────────────────────────────
def gen_buyer(conn, f, M):
    um = M["user"]
    f.write(f"-- V18: Buyer Management data migration | {datetime.now().isoformat()}\n\n")

    # Sales Reps
    rows = q(conn, '''SELECT id, salesrepresentativeid, salesrepfirstname, salesreplastname, active,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$salesrepresentative" ORDER BY id''')
    sm = idmap(rows); M["srep"] = sm
    write_ins(f, "buyer_mgmt.sales_representatives",
        ["id","sales_representative_id","first_name","last_name","active",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(sm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], fk(r[7],um), fk(r[8],um)) for r in rows])

    # Buyers
    rows = q(conn, '''SELECT id, submissionid, companyname, status, isspecialbuyer,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$buyer" ORDER BY id''')
    bm = idmap(rows); M["buyer"] = bm
    write_ins(f, "buyer_mgmt.buyers",
        ["id","submission_id","company_name","status","is_special_buyer",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(bm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], fk(r[7],um), fk(r[8],um)) for r in rows])

    # Buyer-SalesRep
    rows = q(conn, 'SELECT "ecoatm_buyermanagement$buyerid", "ecoatm_buyermanagement$salesrepresentativeid" '
                    'FROM "ecoatm_buyermanagement$buyer_salesrepresentative"')
    write_ins(f, "buyer_mgmt.buyer_sales_reps", ["buyer_id","sales_rep_id"],
              [(fk(r[0],bm), fk(r[1],sm)) for r in rows if fk(r[0],bm) and fk(r[1],sm)])

    # Buyer Codes
    rows = q(conn, '''SELECT id, submissionid, code, buyercodetype, status, budget, softdelete,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$buyercode" ORDER BY id''')
    bcm = idmap(rows); M["bcode"] = bcm
    write_ins(f, "buyer_mgmt.buyer_codes",
        ["id","submission_id","code","buyer_code_type","status","budget","soft_delete",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(bcm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], fk(r[9],um), fk(r[10],um))
         for r in rows])

    # BuyerCode-Buyer
    rows = q(conn, 'SELECT "ecoatm_buyermanagement$buyercodeid", "ecoatm_buyermanagement$buyerid" '
                    'FROM "ecoatm_buyermanagement$buyercode_buyer"')
    write_ins(f, "buyer_mgmt.buyer_code_buyers", ["buyer_code_id","buyer_id"],
              [(fk(r[0],bcm), fk(r[1],bm)) for r in rows if fk(r[0],bcm) and fk(r[1],bm)])

    # Buyer Code Change Logs
    jn = q(conn, 'SELECT "ecoatm_buyermanagement$buyercodechangelogid", "ecoatm_buyermanagement$buyercodeid" '
                  'FROM "ecoatm_buyermanagement$buyercodechangelog_buyercode"')
    cl_bc = {r[0]: r[1] for r in jn}
    rows = q(conn, '''SELECT id, oldbuyercodetype, newbuyercodetype, editedby, editedon,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$buyercodechangelog" ORDER BY id''')
    clm = idmap(rows)
    write_ins(f, "buyer_mgmt.buyer_code_change_logs",
        ["id","buyer_code_id","old_buyer_code_type","new_buyer_code_type","edited_by","edited_on",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(clm[r[0]], fk(cl_bc.get(r[0]),bcm), r[1], r[2], r[3], r[4], r[5], r[6], fk(r[7],um), fk(r[8],um))
         for r in rows])

    # Auctions Feature Config
    rows = q(conn, '''SELECT id, auctionround2minutesoffset, auctionround3minutesoffset,
        sendbuyertosnowflake, sendbiddatatosnowflake, sendauctiondatatosnowflake,
        sendbidrankingtosnowflake, createexcelbidexport, generateround3files,
        calculateround2buyerparticipation, sendfilestosharepointonsubmit,
        spretrycount, round2criteriaactive, minimumallowedbid,
        legacyroundthree, requirewholesaleuseragreement,
        createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$auctionsfeature" ORDER BY id''')
    afm = idmap(rows)
    write_ins(f, "buyer_mgmt.auctions_feature_config",
        ["id","auction_round2_minutes_offset","auction_round3_minutes_offset",
         "send_buyer_to_snowflake","send_bid_data_to_snowflake","send_auction_data_to_snowflake",
         "send_bid_ranking_to_snowflake","create_excel_bid_export","generate_round3_files",
         "calculate_round2_buyer_participation","send_files_to_sharepoint_on_submit",
         "sp_retry_count","round2_criteria_active","minimum_allowed_bid",
         "legacy_auction_dashboard_active","require_wholesale_user_agreement",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(afm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10],
          r[11], r[12], r[13], r[14], r[15], r[16], r[17], fk(r[18],um), fk(r[19],um))
         for r in rows])

    # Resolve deferred user-buyer assignments
    raw = M.get("_raw_ub", [])
    mapped = [(fk(r[0],um), fk(r[1],bm)) for r in raw if fk(r[0],um) and fk(r[1],bm)]
    if mapped:
        f.write("-- Deferred from V17: user-buyer assignments\n")
        write_ins(f, "user_mgmt.user_buyers", ["user_id","buyer_id"], mapped)


# ── V19: Qualified Buyer Codes ────────────────────────────────────────────────
def gen_qbc(conn, f, M):
    um, bcm = M["user"], M["bcode"]
    f.write(f"-- V19: Qualified Buyer Codes data migration | {datetime.now().isoformat()}\n\n")

    rows = q(conn, '''SELECT id, qualificationtype, included, submitted,
        submitteddatetime, openeddashboard, openeddashboarddatetime,
        isspecialtreatment, createddate, changeddate, "system$owner", "system$changedby"
        FROM "ecoatm_buyermanagement$qualifiedbuyercodes" ORDER BY id''')
    print(f"    {len(rows)} QBC rows loaded")
    qm = idmap(rows); M["qbc"] = qm
    write_ins(f, "buyer_mgmt.qualified_buyer_codes",
        ["id","qualification_type","included","submitted","submitted_datetime",
         "opened_dashboard","opened_dashboard_datetime","is_special_treatment",
         "created_date","changed_date","owner_id","changed_by_id"],
        [(qm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9],
          fk(r[10],um), fk(r[11],um)) for r in rows])

    # QBC-BuyerCode
    rows = q(conn, 'SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid", "ecoatm_buyermanagement$buyercodeid" '
                    'FROM "ecoatm_buyermanagement$qualifiedbuyercodes_buyercode"')
    print(f"    {len(rows)} QBC-buyercode links")
    write_ins(f, "buyer_mgmt.qbc_buyer_codes", ["qualified_buyer_code_id","buyer_code_id"],
              [(fk(r[0],qm), fk(r[1],bcm)) for r in rows if fk(r[0],qm) and fk(r[1],bcm)])

    # QBC-SchedulingAuction (raw IDs — no auction module yet)
    rows = q(conn, 'SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid", "auctionui$schedulingauctionid" '
                    'FROM "ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction"')
    write_ins(f, "buyer_mgmt.qbc_scheduling_auctions", ["qualified_buyer_code_id","scheduling_auction_id"],
              [(fk(r[0],qm), r[1]) for r in rows if fk(r[0],qm)])

    # QBC-BidRound (raw IDs)
    rows = q(conn, 'SELECT "ecoatm_buyermanagement$qualifiedbuyercodesid", "auctionui$bidroundid" '
                    'FROM "ecoatm_buyermanagement$qualifiedbuyercodes_bidround"')
    write_ins(f, "buyer_mgmt.qbc_bid_rounds", ["qualified_buyer_code_id","bid_round_id"],
              [(fk(r[0],qm), r[1]) for r in rows if fk(r[0],qm)])


# ── V20: SSO ─────────────────────────────────────────────────────────────────
def gen_sso(conn, f, M):
    rm, um = M["role"], M["user"]
    f.write(f"-- V20: SSO data migration | {datetime.now().isoformat()}\n\n")

    # SAML AuthN Contexts
    rows = q(conn, 'SELECT id, description, value, defaultpriority, provisioned, createddate, changeddate '
                    'FROM "saml20$samlauthncontext" ORDER BY id')
    cm = idmap(rows); M["actx"] = cm
    write_ins(f, "sso.saml_authn_contexts",
        ["id","description","value","default_priority","provisioned","created_date","changed_date"],
        [(cm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6]) for r in rows])

    # X.509 Certificates
    rows = q(conn, 'SELECT id, issuername, serialnumber, subject, validfrom, validuntil, base64 '
                    'FROM "saml20$x509certificate" ORDER BY id')
    xm = idmap(rows); M["cert"] = xm
    write_ins(f, "sso.x509_certificates",
        ["id","issuer_name","serial_number","subject","valid_from","valid_until","base64_cert"],
        [(xm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6]) for r in rows])

    # Keystores
    rows = q(conn, 'SELECT id, alias, password, rebuildkeystore, lastchangedon FROM "saml20$keystore" ORDER BY id')
    km = idmap(rows); M["ks"] = km
    write_ins(f, "sso.keystores",
        ["id","alias","password","rebuild_keystore","last_changed_on"],
        [(km[r[0]], r[1], r[2], r[3], r[4]) for r in rows])

    # IDP Metadata (sparse — only id column in source)
    rows = q(conn, 'SELECT id FROM "saml20$idpmetadata" ORDER BY id')
    im = idmap(rows); M["idp"] = im
    write_ins(f, "sso.idp_metadata", ["id","entity_id","updated"],
              [(im[r[0]], None, False) for r in rows])

    # SP Metadata
    rows = q(conn, '''SELECT id, entityid, organizationname, organizationdisplayname,
        organizationurl, contactgivenname, contactsurname, contactemailaddress,
        applicationurl, doesentityiddifferfromappurl, logavailabledays,
        useencryption, encryptionmethod, encryptionkeylength
        FROM "saml20$spmetadata" ORDER BY id''')
    spm = idmap(rows)
    # Get keystore link
    kl = {r[0]:r[1] for r in q(conn, 'SELECT "saml20$spmetadataid","saml20$keystoreid" FROM "saml20$spmetadata_keystore"')}
    write_ins(f, "sso.sp_metadata",
        ["id","entity_id","organization_name","organization_display_name",
         "organization_url","contact_given_name","contact_surname","contact_email_address",
         "application_url","does_entity_id_differ_from_appurl","log_available_days",
         "use_encryption","encryption_method","encryption_key_length","keystore_id"],
        [(spm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10],
          r[11], r[12], r[13], fk(kl.get(r[0]),km))
         for r in rows])

    # SSO Configuration
    rows = q(conn, '''SELECT id, alias, active, issamlloggingenabled,
        authncontext, readidpmetadatafromurl, createusers,
        allowidpinitiatedauthentication, identifyingassertiontype,
        customidentifyingassertionname, usecustomlogicforprovisioning,
        usecustomaftersigninlogic, disablenameidpolicy,
        enabledelegatedauthentication, delegatedauthenticationurl,
        enablemobileauthtoken, responseprotocolbinding,
        enableassertionconsumerserviceindex, assertionconsumerserviceindex,
        enableforceauthentication, useencryption, encryptionmethod,
        encryptionkeylength, wizardmode, currentwizardstep
        FROM "saml20$ssoconfiguration" ORDER BY id''')
    ssm = idmap(rows)
    il = {r[0]:r[1] for r in q(conn, 'SELECT "saml20$ssoconfigurationid","saml20$idpmetadataid" FROM "saml20$ssoconfiguration_idpmetadata"')}
    rl = {r[0]:r[1] for r in q(conn, 'SELECT "saml20$ssoconfigurationid","system$userroleid" FROM "saml20$ssoconfiguration_defaultuserroletoassign"')}
    write_ins(f, "sso.sso_configurations",
        ["id","alias","active","is_saml_logging_enabled","authn_context",
         "read_idp_metadata_from_url","create_users","allow_idp_initiated_authentication",
         "identifying_assertion_type","custom_identifying_assertion_name",
         "use_custom_logic_for_provisioning","use_custom_after_signin_logic",
         "disable_nameid_policy","enable_delegated_authentication","delegated_authentication_url",
         "enable_mobile_auth_token","response_protocol_binding",
         "enable_assertion_consumer_service_index","assertion_consumer_service_index",
         "enable_force_authentication","use_encryption","encryption_method",
         "encryption_key_length","wizard_mode","current_wizard_step",
         "claim_maps","idp_metadata_id","keystore_id","default_role_id"],
        [(ssm[r[0]], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9],
          r[10], r[11], r[12], r[13], r[14], r[15], r[16], r[17], r[18],
          r[19], r[20], r[21], r[22], r[23], r[24],
          "[]", fk(il.get(r[0]),im), fk(kl.get(r[0]),km) if kl else None, fk(rl.get(r[0]),rm))
         for r in rows])

    # Forgot Password Config (only id column in source)
    rows = q(conn, 'SELECT id FROM "forgotpassword$configuration" ORDER BY id')
    fpm = idmap(rows)
    write_ins(f, "sso.forgot_password_config", ["id"],
              [(fpm[r[0]],) for r in rows])


# ── V21: MDM ─────────────────────────────────────────────────────────────────
def gen_mdm(conn, f, M):
    f.write(f"-- V21: MDM data migration | {datetime.now().isoformat()}\n\n")

    # Lookup tables — each uses its entity name as the "name" column
    lookups = [
        ("brand", "ecoatm_pwsmdm$brand", "brand"),
        ("category", "ecoatm_pwsmdm$category", "category"),
        ("model", "ecoatm_pwsmdm$model", "model"),
        ("condition", "ecoatm_pwsmdm$condition", "condition"),
        ("capacity", "ecoatm_pwsmdm$capacity", "capacity"),
        ("carrier", "ecoatm_pwsmdm$carrier", "carrier"),
        ("color", "ecoatm_pwsmdm$color", "color"),
        ("grade", "ecoatm_pwsmdm$grade", "grade"),
    ]
    # Some have displayname, some don't; some use isenabledforfilter, some isenabledforfunctionaldevicesfilter
    for name, src, ncol in lookups:
        # Check if displayname column exists
        try:
            rows = q(conn, f'SELECT id, {ncol}, displayname, isenabledforfilter, rank FROM "{src}" ORDER BY id')
            has_display = True
        except:
            conn.rollback()
            try:
                rows = q(conn, f'SELECT id, {ncol}, NULL as dn, isenabledforfunctionaldevicesfilter, rank FROM "{src}" ORDER BY id')
                has_display = False
            except:
                conn.rollback()
                rows = q(conn, f'SELECT id, {ncol}, NULL as dn, true as en, rank FROM "{src}" ORDER BY id')
        lm = idmap(rows); M[f"m_{name}"] = lm
        write_ins(f, f"mdm.{name}", ["id","legacy_id","name","display_name","is_enabled","sort_rank"],
                  [(lm[r[0]], r[0], r[1], r[2], r[3], r[4]) for r in rows])

    # Device FK lookups via junction tables
    jq = {
        "brand": ('ecoatm_pwsmdm$device_brand', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$brandid'),
        "category": ('ecoatm_pwsmdm$device_category', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$categoryid'),
        "model": ('ecoatm_pwsmdm$device_model', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$modelid'),
        "condition": ('ecoatm_pwsmdm$device_condition', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$conditionid'),
        "capacity": ('ecoatm_pwsmdm$device_capacity', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$capacityid'),
        "carrier": ('ecoatm_pwsmdm$device_carrier', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$carrierid'),
        "color": ('ecoatm_pwsmdm$device_color', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$colorid'),
        "grade": ('ecoatm_pwsmdm$device_grade', 'ecoatm_pwsmdm$deviceid', 'ecoatm_pwsmdm$gradeid'),
    }
    dl = defaultdict(dict)
    for ln, (jtbl, dcol, lcol) in jq.items():
        for r in q(conn, f'SELECT "{dcol}","{lcol}" FROM "{jtbl}"'):
            nv = fk(r[1], M.get(f"m_{ln}", {}))
            if nv: dl[r[0]][ln] = nv

    # Devices
    rows = q(conn, '''SELECT id, sku, devicecode, devicedescription,
        currentlistprice, currentminprice, futurelistprice, futureminprice,
        availableqty, reservedqty, atpqty, weight, itemtype, isactive,
        lastsynctime, createddate, changeddate
        FROM "ecoatm_pwsmdm$device" ORDER BY id''')
    dm = idmap(rows); M["m_device"] = dm
    mapped = []
    for r in rows:
        d = dl.get(r[0], {})
        mapped.append((dm[r[0]], r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7],
                        r[8], r[9], r[10], r[11], r[12], r[13],
                        d.get("brand"), d.get("category"), d.get("model"), d.get("condition"),
                        d.get("capacity"), d.get("carrier"), d.get("color"), d.get("grade"),
                        r[14], r[15], r[16]))
    write_ins(f, "mdm.device",
        ["id","legacy_id","sku","device_code","description",
         "list_price","min_price","future_list_price","future_min_price",
         "available_qty","reserved_qty","atp_qty","weight","item_type","is_active",
         "brand_id","category_id","model_id","condition_id",
         "capacity_id","carrier_id","color_id","grade_id",
         "last_sync_time","created_date","updated_date"], mapped)

    # Price History — FK is inline via junction if exists, else skip
    # Check for junction table
    try:
        ph_dev = {r[0]:r[1] for r in q(conn, 'SELECT "ecoatm_pwsmdm$pricehistoryid","ecoatm_pwsmdm$deviceid" FROM "ecoatm_pwsmdm$pricehistory_device"')}
    except:
        conn.rollback()
        # Try direct FK column in price history table
        try:
            ph_dev = {r[0]:r[1] for r in q(conn, 'SELECT id, "ecoatm_pwsmdm$pricehistory_device" FROM "ecoatm_pwsmdm$pricehistory" WHERE "ecoatm_pwsmdm$pricehistory_device" IS NOT NULL')}
        except:
            conn.rollback()
            ph_dev = {}

    rows = q(conn, 'SELECT id, listprice, minprice, expirationdate, createddate, changeddate '
                    'FROM "ecoatm_pwsmdm$pricehistory" ORDER BY id')
    pm = idmap(rows)
    write_ins(f, "mdm.price_history",
        ["id","legacy_id","device_id","list_price","min_price","expiration_date","created_date","updated_date"],
        [(pm[r[0]], r[0], fk(ph_dev.get(r[0]),dm), r[1], r[2], r[3], r[4], r[5]) for r in rows])

    # Reset sequences
    f.write("-- Reset sequences\n")
    for t in ["brand","category","model","condition","capacity","carrier","color","grade","device","price_history"]:
        f.write(f"SELECT setval(pg_get_serial_sequence('mdm.{t}','id'), COALESCE((SELECT MAX(id) FROM mdm.{t}),0)+1);\n")
    f.write("\n")


# ── V22: PWS ─────────────────────────────────────────────────────────────────
def gen_pws(conn, f, M):
    bcm = M.get("bcode", {})
    f.write(f"-- V22: PWS data migration | {datetime.now().isoformat()}\n\n")

    # Offers (system)
    obc = {r[0]:r[1] for r in q(conn, 'SELECT "ecoatm_pws$offerid","ecoatm_buyermanagement$buyercodeid" FROM "ecoatm_pws$offer_buyercode"')}
    rows = q(conn, '''SELECT id, offerstatus, offertotalquantity, offertotalprice,
        offersubmissiondate, salesreviewcompletedon, offercancelledon,
        createddate, changeddate
        FROM "ecoatm_pws$offer" ORDER BY id''')
    om = idmap(rows); M["p_offer"] = om
    mapped = [(om[r[0]], r[0], "SYSTEM", r[1], r[2], r[3], fk(obc.get(r[0]),bcm), None,
               r[4], r[5], r[6], r[7], r[8]) for r in rows]

    # Buyer offers
    bobc = {r[0]:r[1] for r in q(conn, 'SELECT "ecoatm_pws$buyerofferid","ecoatm_buyermanagement$buyercodeid" FROM "ecoatm_pws$buyeroffer_buyercode"')}
    rows2 = q(conn, '''SELECT id, offerstatus, offerquantity, offertotal,
        createddate, changeddate
        FROM "ecoatm_pws$buyeroffer" ORDER BY id''')
    bom = idmap(rows2, len(om)+1); M["p_boffer"] = bom
    for r in rows2:
        mapped.append((bom[r[0]], r[0], "BUYER", r[1], r[2], r[3], fk(bobc.get(r[0]),bcm), None,
                        None, None, None, r[4], r[5]))
    # Merge maps for offer_item FK resolution
    all_om = {**om}
    for old, new in bom.items(): all_om[old] = new
    M["p_all_offer"] = all_om

    write_ins(f, "pws.offer",
        ["id","legacy_id","offer_type","status","total_qty","total_price",
         "buyer_code_id","sales_rep_id","submission_date","sales_review_completed_on",
         "canceled_on","created_date","updated_date"], mapped)

    # Offer Items
    iof = {r[0]:r[1] for r in q(conn, 'SELECT "ecoatm_pws$offeritemid","ecoatm_pws$offerid" FROM "ecoatm_pws$offeritem_offer"')}
    rows = q(conn, '''SELECT id, offerquantity, offerprice, offertotalprice,
        counterquantity, counterprice, countertotal, salesofferitemstatus,
        createddate, changeddate
        FROM "ecoatm_pws$offeritem" ORDER BY id''')
    oim = idmap(rows)
    # Filter out orphaned offer items (no linked offer — offer_id is NOT NULL in schema)
    oi_mapped = []
    skipped = 0
    for r in rows:
        oid = fk(iof.get(r[0]), all_om)
        if oid is None:
            skipped += 1
            continue
        oi_mapped.append((oim[r[0]], r[0], oid, None, None,
                          r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9]))
    if skipped:
        print(f"    Skipped {skipped} orphaned offer items (no offer link)")
    write_ins(f, "pws.offer_item",
        ["id","legacy_id","offer_id","sku","device_id",
         "quantity","price","total_price","counter_qty","counter_price","counter_total",
         "item_status","created_date","updated_date"], oi_mapped)

    # Orders — FK to offer might be inline via a junction or column
    # Check for junction table first, then inline column
    try:
        oof = {r[0]:r[1] for r in q(conn, 'SELECT "ecoatm_pws$orderid","ecoatm_pws$offerid" FROM "ecoatm_pws$order_offer"')}
    except:
        conn.rollback()
        oof = {}  # Will try to find FK inline

    rows = q(conn, '''SELECT id, ordernumber, orderline, oracleorderstatus, shipmethod,
        shippedtotalquantity, shippedtotalprice, orderdate, shipdate, oraclehttpcode,
        createddate, changeddate
        FROM "ecoatm_pws$order" ORDER BY id''')
    ordm = idmap(rows); M["p_order"] = ordm
    write_ins(f, 'pws."order"',
        ["id","legacy_id","offer_id","order_number","order_line","order_status",
         "oracle_status","ship_method","shipped_total_qty","shipped_total_price",
         "order_date","ship_date","oracle_http_code","created_date","updated_date"],
        [(ordm[r[0]], r[0], fk(oof.get(r[0]),all_om), r[1], r[2], None,
          r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10], r[11]) for r in rows])

    # Shipment Details — FK column is inline: ecoatm_pws$shipmentdetail_order
    rows = q(conn, '''SELECT id, trackingnumber, trackingurl, skucount, quantity,
        "ecoatm_pws$shipmentdetail_order", createddate, changeddate
        FROM "ecoatm_pws$shipmentdetail" ORDER BY id''')
    sdm = idmap(rows)
    write_ins(f, "pws.shipment_detail",
        ["id","legacy_id","order_id","tracking_number","tracking_url",
         "sku_count","quantity","created_date","updated_date"],
        [(sdm[r[0]], r[0], fk(r[5],ordm), r[1], r[2], r[3], r[4], r[6], r[7]) for r in rows])

    # Reset sequences
    f.write("-- Reset sequences\n")
    for t in ["offer","offer_item","order","shipment_detail"]:
        f.write(f"SELECT setval(pg_get_serial_sequence('pws.{t}','id'), COALESCE((SELECT MAX(id) FROM pws.\"{t}\"),0)+1);\n")
    f.write("\n")


# ── Main ──────────────────────────────────────────────────────────────────────
def main():
    p = argparse.ArgumentParser()
    p.add_argument("--source-db", required=True)
    args = p.parse_args()

    conn = connect(args.source_db)
    conn.autocommit = True
    out = os.path.normpath(OUTPUT_DIR)
    print(f"Source: {args.source_db} -> {out}")

    M = {}
    steps = [
        ("V16__migrate_identity_data.sql", gen_identity),
        ("V17__migrate_user_mgmt_data.sql", gen_user_mgmt),
        ("V18__migrate_buyer_mgmt_data.sql", gen_buyer),
        ("V19__migrate_buyer_mgmt_qbc_data.sql", gen_qbc),
        ("V20__migrate_sso_data.sql", gen_sso),
        ("V21__migrate_mdm_data.sql", gen_mdm),
        ("V22__migrate_pws_data.sql", gen_pws),
    ]
    for fname, gen in steps:
        fp = os.path.join(out, fname)
        print(f"\n=== {fname} ===")
        with open(fp, "w", encoding="utf-8") as f:
            gen(conn, f, M)
        print(f"  -> {os.path.getsize(fp)/1024:.1f} KB")

    conn.close()
    print(f"\nDone! {len(steps)} migration files generated.")
    for k, v in M.items():
        if not k.startswith("_") and isinstance(v, dict):
            print(f"  {k}: {len(v)} records")

if __name__ == "__main__":
    main()
