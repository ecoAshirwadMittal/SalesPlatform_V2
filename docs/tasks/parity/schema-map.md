# Parity Schema Map — Legacy Mendix ↔ New App (2026-07-11)

The two applications render the **same data through different schemas**. Data equality is
re-established at any time by re-running the migration (wipe → `extract_qa_data.py --source-db
<snapshot>` → fresh Flyway migrate), so both DBs derive from one snapshot even though their shapes
differ. This document is the **schema card pack**: any agent (human, subagent, or the parity
harness) comparing a page must know the schema of *the side it is testing*, query that side in its
own dialect, normalize, and match results against the other side.

Derived from `migration_scripts/extract_qa_data.py` (the authoritative transform) — regenerate
this map whenever that script changes.

---

## 1. Ground rules for schema-aware agents

**Legacy-side agent (Mendix snapshot DB)**
- Tables are `"module$entity"` — always double-quoted, lowercase (`"ecoatm_buyermanagement$buyercode"`).
- Associations live in **junction tables** `"module$entity_association"` with columns
  `"module$entityid"` — even for logically 1:1 links (e.g. offer → buyercode). Resolve FKs by
  LEFT JOINing the junction.
- Common audit columns: `createddate`, `changeddate`, `"system$owner"`, `"system$changedby"`.
- Connection: `psql -h localhost -U postgres -d <snapshot-db>` (e.g. `qa-0327`; password via
  `PGPASSWORD` env — never hardcode).

**New-side agent (`salesplatform_dev`)**
- Normalized schemas: `identity`, `user_mgmt`, `buyer_mgmt`, `mdm`, `pws`, `sso`, `integration`,
  `auctions`, `partial_credit`, `email`. Snake_case columns, real FK columns (junctions from the
  Mendix model were mostly collapsed).
- `pws."order"` must be quoted (reserved word). Audit columns are `created_date` +
  `changed_date` (identity/buyer_mgmt) or `updated_date` (mdm/pws) — the naming is inconsistent
  by domain; check per table.
- Connection: `psql -h localhost -U salesplatform -d salesplatform_dev`.

**Matching rules (both agents)**
1. **Never match on primary keys.** Mendix IDs are not preserved — new sequential IDs are
   assigned in sorted-old-id order. Match on **business keys** (per-table below) or on the
   `legacy_id` column where it exists (`mdm.device`, `mdm.price_history`, `pws.offer`,
   `pws.offer_item`, `pws."order"`, `pws.shipment_detail`, `integration.deposco_config`).
2. **Normalize before comparing:** `ORDER BY` the business key on both sides; cast numerics to a
   fixed scale (`::numeric(14,2)`); render timestamps with
   `to_char(<ts> AT TIME ZONE 'UTC', 'YYYY-MM-DD HH24:MI:SS')` (new side is TIMESTAMPTZ pinned
   UTC; legacy is naive timestamp); `coalesce` nullable text to `''`.
3. **Validation window:** run data checks **immediately after a fresh re-migration and before any
   lifecycle activity** — the new app rewrites `buyer_mgmt.qualified_buyer_codes` and
   `auctions.bid_data` itself on round events (R2/R3 services), and both apps mutate transactional
   tables once users act. Keep `AUCTIONS_LIFECYCLE_ENABLED=false` during validation (matches the
   topology runbook).
4. **Check the expected-deltas catalogue (§4) before declaring a mismatch.** Several differences
   are by construction, not defects.

---

## 2. Table map (generator scope — V16–V24 + V34)

### Identity (V16/V17)

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `system$language` | `identity.languages` | `code` | |
| `system$timezone` | `identity.timezones` | `code` | `rawoffset → raw_offset` |
| `system$userrole` | `identity.user_roles` | `name` | `modelguid → model_guid`. V16 DELETEs V15 seed roles first |
| `system$grantableroles` | `identity.grantable_roles` | (grantor,grantee) role names | junction, both role FKs remapped |
| `system$user` | `identity.users` | `name` (login/email) | `submetaobjectname → user_type`, `lastlogin → last_login`, `failedlogins → failed_logins`, `system$owner → owner_id`, `system$changedby → changed_by_id` |
| `administration$account` | `identity.accounts` | `email` | joined 1:1 on the mapped user id; `fullname → full_name`, `islocaluser → is_local_user` |
| `system$userroles` | `identity.user_role_assignments` | (user name, role name) | junction |
| `system$user_language` / `system$user_timezone` | `identity.user_languages` / `identity.user_timezones` | user name + code | junctions |

### Buyer management (V18)

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `ecoatm_buyermanagement$salesrepresentative` | `buyer_mgmt.sales_representatives` | `sales_representative_id` (fallback first+last name) | `salesrepfirstname/-lastname → first_name/last_name` |
| `ecoatm_buyermanagement$buyer` | `buyer_mgmt.buyers` | `company_name` (or `submission_id`) | `isspecialbuyer → is_special_buyer` |
| `ecoatm_buyermanagement$buyer_salesrepresentative` | `buyer_mgmt.buyer_sales_reps` | (company, rep) | junction |
| `ecoatm_buyermanagement$buyercode` | `buyer_mgmt.buyer_codes` | **`code`** | `buyercodetype → buyer_code_type`, `softdelete → soft_delete` |
| `ecoatm_buyermanagement$buyercode_buyer` | `buyer_mgmt.buyer_code_buyers` | (code, company) | junction |
| `ecoatm_buyermanagement$buyercodechangelog` (+`_buyercode` junction) | `buyer_mgmt.buyer_code_change_logs` | (code, edited_on, edited_by) | junction folded into `buyer_code_id` FK |
| `ecoatm_buyermanagement$auctionsfeature` | `buyer_mgmt.auctions_feature_config` | singleton id=1 | LIMIT 1; `legacy_auction_dashboard_active` hardcoded `false`; `minimum_allowed_bid` defaults 2.00 |

### Direct users (V19)

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `ecoatm_usermanagement$ecoatmdirectuser` | `user_mgmt.ecoatm_direct_users` | via `identity.users.name` (`user_id` = mapped `system$user` id) | rows whose user id didn't map are dropped |
| `ecoatm_usermanagement$ecoatmdirectuser_buyer` | `user_mgmt.user_buyers` | (user name, company) | junction |

### MDM (V21)

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `ecoatm_pwsmdm$brand/category/model/condition/capacity/carrier/color/grade` | `mdm.<same>` | `name` | Mendix name column **is the entity name** (table `brand`, column `brand`) → `name`; `displayname → display_name`, `isenabledforfilter → is_enabled`, `rank → sort_rank` (fallback path fills defaults if those cols are absent) |
| `ecoatm_pwsmdm$device` | `mdm.device` | **`sku`** (+ `legacy_id`) | 8 lookup FKs resolved via junctions `ecoatm_pwsmdm$device_<lookup>`; `currentlistprice → list_price`, `currentminprice → min_price`, future variants likewise; **prices passed through verbatim** (script comments note Mendix "stores cents in some cases" — watch display-format drift, no conversion is applied) |
| `ecoatm_pwsmdm$pricehistory` (+`_devicelist` junction) | `mdm.price_history` | `legacy_id` | device FK via junction |

### PWS (V22) — includes the **offer unification**

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `ecoatm_pws$offer` **UNION** `ecoatm_pws$buyeroffer` | `pws.offer` | `legacy_id` **+ `offer_type`** (`SYSTEM` \| `BUYER`) | legacy_id alone is NOT unique across the union — always pair with offer_type. SYSTEM rows numbered first, then BUYER. Column map (SYSTEM): `offerstatus → status`, `offertotalquantity → total_qty`, `offertotalprice → total_price`, `offersubmissiondate → submission_date`, `offercancelledon → canceled_on`; (BUYER): `offerquantity → total_qty`, `offertotal → total_price`, `createddate → submission_date`, no sales_rep. buyer_code/sales_rep FKs via junctions `ecoatm_pws$offer_buyercode` / `_salesrepresentative`, `ecoatm_pws$buyeroffer_buyercode` |
| `ecoatm_pws$offeritem` UNION `ecoatm_pws$buyerofferitem` | `pws.offer_item` | `legacy_id` + parent offer_type | **orphans skipped** (no offer junction row → not migrated). Device/buyer-code via `_device`/`_buyercode` junctions; `counterquantity → counter_qty` etc.; buyer items get counter fields = 0 |
| `ecoatm_pws$order` (+`ecoatm_pws$offer_order` junction) | `pws."order"` | **`order_number`** (+ `legacy_id`) | `oracleorderstatus` populates **both** `order_status` and `oracle_status`; `shippedtotalquantity → shipped_total_qty` |
| `ecoatm_pws$shipmentdetail` | `pws.shipment_detail` | `tracking_number` (+ `legacy_id`) | order FK is a **direct column** `"ecoatm_pws$shipmentdetail_order"` (not a junction) |

### Qualified buyer codes (V23 → reshaped by V72)

| Legacy | New (current shape) | Business key | Notes |
|---|---|---|---|
| `ecoatm_buyermanagement$qualifiedbuyercodes` + junctions `_buyercode`, `_schedulingauction`, `_bidround` | `buyer_mgmt.qualified_buyer_codes` with **direct** `buyer_code_id` + `scheduling_auction_id` (V72 dropped the `qbc_buyer_codes`/`qbc_scheduling_auctions` junctions) | (SA id, buyer code) | ⚠ **Fresh chain ends EMPTY by design** — V72 deletes all V23 rows because legacy SA ids were never remapped into `auctions.scheduling_auctions` (§4 #12); the R2/R3 services rewrite the table per SA at runtime. Validate fixture-driven (§3), never via snapshot counts |

### Integration (V24) & RMA (V34)

| Legacy | New | Business key | Notes |
|---|---|---|---|
| `ecoatm_pwsintegration$deposcoconfig` | `integration.deposco_config` | singleton | LIMIT 1 |
| `ecoatm_pwsintegration$pwsresponseconfig` | `integration.error_mapping` | (source_system, source_error_code) | sequential ids |
| `ecoatm_rma$rmastatus` | `pws.rma_status` | `internal_status_text` | legacy column typo `desciption → description`; V34 DELETEs the V33 seed first |
| `ecoatm_rma$rmareasons` | `pws.rma_reason` | `valid_reasons` | |
| `ecoatm_rma$rma` (+ junctions `_buyercode`, `_ecoatmdirectuser_submittedby`, `_ecoatmdirectuser_reviewedby`, `_rmastatus`) | `pws.rma` | **`number`** | no legacy_id column here |
| `ecoatm_rma$rmaitem` (+ junctions `_rma`, `_device`, `_order`) | `pws.rma_item` | (rma number, `imei`) | **orphans skipped** (no RMA junction row) |

SSO (V20, `saml20$* → sso.*`) is config-only: keystores/certs migrated **minimally**
(`base64_cert`/`metadata_xml` NULL), claim maps flattened to a JSON column. Treat as
config-presence checks, not row parity.

---

## 3. Domains OUTSIDE the generator — different validation contract

These new-app tables are **not** derived from the snapshot by `extract_qa_data.py`; do not expect
query-level equality with the legacy snapshot:

| Domain | New tables | Seeded by | Validation approach |
|---|---|---|---|
| Auctions core | `auctions.scheduling_auctions`, `bid_data`, `aggregated_inventory`, weeks, rounds | hand-authored fixtures (V58–V64, V86–V87) | **Fixture-driven**: seed *matching* state on the Mendix side (or pick a legacy SA and mirror it into fixtures), then compare page output. Pixel layer uses DATA masks + text-diff otherwise |
| Reserve bids (EB) | `auctions.reserve_bid` | V77 data seed | **Provenance verified 2026-07-12:** qa-0327-derived (`legacy_id` present; 73/A_YYY = 888.79 both sides) but **2 rows short** (14,657 vs 14,659 — RBL-D1) and `product_id` is **VARCHAR** (lexicographic sort — RBL-D2). Business key: (product_id, grade) |
| Purchase orders | `auctions.purchase_order`, `po_detail` | V81 data seed | Same as reserve bids — legacy source `ecoatm_po$*` |
| Partial credit | `partial_credit.*` | V89/V90 (schema + status/template seeds only) | Legacy `ecoatm_partialcredit$*` **data is not migrated** — legacy-only claims are an expected delta; parity is structural/flow, not row-level |
| Email | `email.*` | V92 | Greenfield unified module; no legacy peer |
| IMEI data | (V39 seed) | independent seed | outside generator scope |

---

## 4. Expected-deltas catalogue (check BEFORE filing a mismatch)

1. **Dev seed accounts:** `identity.users` ids **9001–9006** (`admin@test.com` … `directadmin@test.com`)
   + their `user_role_assignments`, `identity.accounts`, and `user_mgmt.ecoatm_direct_users` rows
   exist **only** in the new DB (dev-login seeds). Exclude `user_id BETWEEN 9001 AND 9006` in
   counts. *(Verified +6 on accounts and direct_users, 2026-07-11 gate run.)*
2. **SKIP_TABLES:** Mendix sessions (`system$session*`), SAML requests/responses/logs
   (`saml20$samlrequest/-response/ssolog`), forgot-password rows — legacy-only by design.
   Integration API tokens (~2.7M rows) and API logs (~350K) are also not migrated.
3. **Orphan skips:** offer items with no offer junction row and RMA items with no RMA junction row
   are dropped — `count(legacy) ≥ count(new)` for those two tables; the difference must equal the
   orphan count (§5 Q4 quantifies it).
4. **Offer unification:** `count(pws.offer) = count(ecoatm_pws$offer) + count(ecoatm_pws$buyeroffer)`.
5. **IDs:** all PKs differ; junction tables became FK columns; `qbc` junctions were dropped by V72.
6. **Hardcoded/derived values:** `auctions_feature_config.legacy_auction_dashboard_active = false`
   (not in source); `minimum_allowed_bid` falls back to 2.00; `pws."order".order_status` and
   `oracle_status` are the same source column; SSO certs/keystores are placeholders.
7. **V-seed replacements:** V34 deletes the V33 RMA status/reason seed before inserting QA data;
   V16/V17 delete V15 role/user seeds (then re-add the six dev users).
8. **Number formats:** device prices are copied verbatim — if legacy stored cents anywhere, the
   *stored* values still match but the **rendered** values may not; that is a UI-format defect to
   catch in the text-diff layer, not a data-migration defect.
9. **NULL device prices coerced to 0:** the generator's `r.get("currentlistprice") or 0` turns
   NULL prices into `0` — 22 inactive/zero-qty devices differ as `NULL` (legacy) vs `0.00` (new).
   *(Verified 2026-07-11: all 22 rows match this exact pattern.)*
10. **SPB test devices:** 5 seeded `SPB-DONT-USE-001…005` devices (`legacy_id IS NULL`) exist only
    in the new DB for the SPKB e2e flows — a boot-time seeder also touches their `updated_date`.
    Exclude with `WHERE legacy_id IS NOT NULL`.
11. **PartialCredit roles:** V90 seeds 4 `PartialCredit_*` roles (`_Buyer/_SalesRep/_SalesOps/_Admin`)
    with no legacy peer. Exclude with `WHERE name NOT LIKE 'PartialCredit_%'`.
12. **QBC is EMPTY on a fresh chain (by design):** V72 deletes **all** V23-seeded
    `qualified_buyer_codes` rows because the V23 junction carried raw Mendix scheduling-auction
    ids that were never remapped into `auctions.scheduling_auctions` (V64 added the FK `NOT VALID`
    for exactly this reason). The table is rewritten per scheduling auction by the R2/R3 services
    at runtime → QBC belongs to the **fixture-driven** contract (§3), not snapshot equality.
    *(Verified 2026-07-11: legacy 378,755 vs new 0.)*

---

## 5. Paired validation queries (run one per side, diff the outputs)

Run with `psql --csv -f <file>` on each side and `diff` the two CSVs (or feed both to the harness
scoreboard). Q1/Q2 are the fast whole-DB gate; Q3+ are per-domain checksums; per-page `dataChecks`
in the parity manifest follow the same pattern scoped to what the page displays.

### Q1 — Row-count parity pack

```sql
-- LEGACY side (snapshot DB)
SELECT 'users' AS k, count(*) AS c FROM "system$user"
UNION ALL SELECT 'accounts',          count(*) FROM "administration$account"
UNION ALL SELECT 'roles',             count(*) FROM "system$userrole"
UNION ALL SELECT 'sales_reps',        count(*) FROM "ecoatm_buyermanagement$salesrepresentative"
UNION ALL SELECT 'buyers',            count(*) FROM "ecoatm_buyermanagement$buyer"
UNION ALL SELECT 'buyer_codes',       count(*) FROM "ecoatm_buyermanagement$buyercode"
UNION ALL SELECT 'direct_users',      count(*) FROM "ecoatm_usermanagement$ecoatmdirectuser" du
                                      WHERE EXISTS (SELECT 1 FROM "system$user" u WHERE u.id = du.id)
UNION ALL SELECT 'devices',           count(*) FROM "ecoatm_pwsmdm$device"
UNION ALL SELECT 'price_history',     count(*) FROM "ecoatm_pwsmdm$pricehistory"
UNION ALL SELECT 'offers',            (SELECT count(*) FROM "ecoatm_pws$offer")
                                    + (SELECT count(*) FROM "ecoatm_pws$buyeroffer")
UNION ALL SELECT 'orders',            count(*) FROM "ecoatm_pws$order"
UNION ALL SELECT 'shipments',         count(*) FROM "ecoatm_pws$shipmentdetail"
UNION ALL SELECT 'rmas',              count(*) FROM "ecoatm_rma$rma"
ORDER BY 1;
```

```sql
-- NEW side (salesplatform_dev)
SELECT 'users' AS k, count(*) AS c FROM identity.users WHERE id NOT BETWEEN 9001 AND 9006
UNION ALL SELECT 'accounts',          count(*) FROM identity.accounts WHERE user_id NOT BETWEEN 9001 AND 9006
UNION ALL SELECT 'roles',             count(*) FROM identity.user_roles WHERE name NOT LIKE 'PartialCredit_%'
UNION ALL SELECT 'sales_reps',        count(*) FROM buyer_mgmt.sales_representatives
UNION ALL SELECT 'buyers',            count(*) FROM buyer_mgmt.buyers
UNION ALL SELECT 'buyer_codes',       count(*) FROM buyer_mgmt.buyer_codes
UNION ALL SELECT 'direct_users',      count(*) FROM user_mgmt.ecoatm_direct_users WHERE user_id NOT BETWEEN 9001 AND 9006
UNION ALL SELECT 'devices',           count(*) FROM mdm.device WHERE legacy_id IS NOT NULL
UNION ALL SELECT 'price_history',     count(*) FROM mdm.price_history
UNION ALL SELECT 'offers',            count(*) FROM pws.offer
UNION ALL SELECT 'orders',            count(*) FROM pws."order"
UNION ALL SELECT 'shipments',         count(*) FROM pws.shipment_detail
UNION ALL SELECT 'rmas',              count(*) FROM pws.rma
ORDER BY 1;
```

New-side WHERE clauses encode the catalogued deltas (§4): `accounts`/`direct_users` exclude the
dev seeds (`user_id NOT BETWEEN 9001 AND 9006`), `roles` excludes `PartialCredit_%`, `devices`
excludes the SPB seeds (`legacy_id IS NOT NULL`). **QBC is intentionally absent** — empty on a
fresh chain by design (§4 #12); validate it fixture-driven (§3). Expected: identical, **except**
`offer_item`/`rma_item` (orphan skips — validate via Q4 instead; both matched exactly with the
non-orphan formulas on the 2026-07-11 run: 12,478 and 12,224).

> **First full run (2026-07-11, qa-0327 → salesplatform_dev): 18/18 PASS** after catalogued-delta
> exclusions; buyer_codes field checksum identical; devices field-diff fully explained by §4 #9.

### Q2 — Field-level checksum (pattern; example: buyer codes)

```sql
-- LEGACY
SELECT md5(string_agg(
  code || '|' || coalesce(buyercodetype,'') || '|' || coalesce(status,'')
       || '|' || coalesce(budget::numeric(14,2)::text,'') || '|' || softdelete::text,
  E'\n' ORDER BY code)) AS checksum, count(*) AS rows
FROM "ecoatm_buyermanagement$buyercode";
```

```sql
-- NEW
SELECT md5(string_agg(
  code || '|' || coalesce(buyer_code_type,'') || '|' || coalesce(status,'')
       || '|' || coalesce(budget::numeric(14,2)::text,'') || '|' || soft_delete::text,
  E'\n' ORDER BY code)) AS checksum, count(*) AS rows
FROM buyer_mgmt.buyer_codes;
```

Same pattern for devices (key `sku`; fields sku|device_code|list_price|min_price|available_qty|is_active)
and users (key `name`; fields name|active|blocked — **exclude 9001–9006 on the new side**).
When a checksum differs, drop the `md5(...)` wrapper, dump both `string_agg` inputs as rows to CSV,
and `diff` to find the exact records.

### Q3 — FK-integrity spot check (offer → buyer code, via business keys)

```sql
-- LEGACY: offers per buyer code
SELECT bc.code, count(*) AS offers
FROM "ecoatm_pws$offer" o
JOIN "ecoatm_pws$offer_buyercode" j ON o.id = j."ecoatm_pws$offerid"
JOIN "ecoatm_buyermanagement$buyercode" bc ON bc.id = j."ecoatm_buyermanagement$buyercodeid"
GROUP BY bc.code ORDER BY bc.code;
```

```sql
-- NEW
SELECT bc.code, count(*) AS offers
FROM pws.offer o JOIN buyer_mgmt.buyer_codes bc ON bc.id = o.buyer_code_id
WHERE o.offer_type = 'SYSTEM'
GROUP BY bc.code ORDER BY bc.code;
```

### Q4 — Quantify the orphan-skip delta (offer items)

```sql
-- LEGACY: non-orphaned item count (should equal new-side count exactly)
SELECT (SELECT count(*) FROM "ecoatm_pws$offeritem" oi
         WHERE EXISTS (SELECT 1 FROM "ecoatm_pws$offeritem_offer" j
                        WHERE j."ecoatm_pws$offeritemid" = oi.id))
     + (SELECT count(*) FROM "ecoatm_pws$buyerofferitem" boi
         WHERE EXISTS (SELECT 1 FROM "ecoatm_pws$buyerofferitem_buyeroffer" j
                        WHERE j."ecoatm_pws$buyerofferitemid" = boi.id)) AS non_orphaned_items;
-- NEW:  SELECT count(*) FROM pws.offer_item;
```

### Per-page `dataChecks` (manifest pattern)

Each parity page carries paired queries scoped to **what the page renders**, so the DB layer
explains any masked-region difference. Example, Orders list (`/pws/orders`) for one buyer code:

```yaml
dataChecks:
  - id: orders-grid-rows
    legacySql: >
      SELECT o.ordernumber, o.oracleorderstatus,
             o.shippedtotalquantity, o.shippedtotalprice::numeric(14,2),
             to_char(o.orderdate AT TIME ZONE 'UTC','YYYY-MM-DD') AS od
      FROM "ecoatm_pws$order" o
      JOIN "ecoatm_pws$offer_order" j ON o.id = j."ecoatm_pws$orderid"
      JOIN "ecoatm_pws$offer_buyercode" ob ON j."ecoatm_pws$offerid" = ob."ecoatm_pws$offerid"
      JOIN "ecoatm_buyermanagement$buyercode" bc ON bc.id = ob."ecoatm_buyermanagement$buyercodeid"
      WHERE bc.code = :buyerCode ORDER BY o.ordernumber
    newSql: >
      SELECT o.order_number, o.order_status,
             o.shipped_total_qty, o.shipped_total_price::numeric(14,2),
             to_char(o.order_date AT TIME ZONE 'UTC','YYYY-MM-DD') AS od
      FROM pws."order" o
      JOIN pws.offer f ON f.id = o.offer_id
      JOIN buyer_mgmt.buyer_codes bc ON bc.id = f.buyer_code_id
      WHERE bc.code = :buyerCode ORDER BY o.order_number
    key: order_number
    expect: identical            # or: subset / catalogued-delta:<ref>
```

**Result-matching rule:** a page's pixel/text diff is only actionable once its `dataChecks` pass —
if the DB layer differs, fix the data (re-run the migration) before touching CSS. This is the gate
order: **migrate → Q1/Q2 whole-DB gate → per-page dataChecks → text/DOM diff → pixel diff.**

---

## 6. Schema-card briefing template (for dispatched comparison agents)

Every page-comparison agent gets both cards; it must run its own side's queries only.

```
SIDE: legacy | new
BASE URL: http://localhost:8082/... | http://localhost:3000/...
DB: qa-0327 (postgres) | salesplatform_dev (salesplatform)
DIALECT NOTES: (from §1 — quoting, junctions, audit columns)
PAGE: <pageId> — TABLES BACKING THIS PAGE: (from §2 rows relevant to the page)
BUSINESS KEYS: <per §2>
EXPECTED DELTAS IN SCOPE: <from §4>
DATACHECKS TO RUN: <from the page manifest>
RETURN: normalized result rows (CSV) + row count + checksum — the orchestrator diffs both sides.
```
