# Auctions Schema Migration Plan

**Status:** Proposed — research only, no code written.
**Target Flyway range:** V58–V64 (current HEAD is V57).
**New schema:** `auctions`

## 1. Context & Scope

The legacy Mendix `auctionui` module is the heart of the weekly inventory
auction flow: aggregate the week's inventory, create an `Auction`, schedule
three `BidRound`s via `SchedulingAuction`, collect `BidData` from
qualified buyer codes, snapshot results, and push to Snowflake.

Partial migration already shipped in V9 (`buyer_mgmt.qualified_buyer_codes`
+ junction tables `qbc_scheduling_auctions`, `qbc_bid_rounds`,
`qbc_query_helper_auctions`) with **FKs deliberately deferred** — V9
explicitly notes "FK to auctionui.* — constraint added when auction module
is migrated." This plan completes that deferral.

Source files consulted:
- `migration_context/database/schema-auctionui.md` (authoritative — 50+ tables)
- `migration_context/database/schema-ecoatm_buyermanagement.md`
  (qualifiedbuyercodes, auctionsfeature)
- `migration_context/backend/ACT_Create_Auction.md`,
  `ACT_SaveScheduleAuction.md`, `ACT_SetAuctionScheduleStarted.md`,
  `ACT_SetAuctionScheduleClosed.md`,
  `ACT_Auction_SendAllBidsToSnowflake_Admin.md`,
  `ACT_SaveAuctionConfiguration.md`,
  `ACT_GetActiveSchedulingAuction.md`
- Existing Flyway: `backend/src/main/resources/db/migration/V9__buyer_mgmt_qualified_codes.sql`

## 2. What's Already in the Modern DB

| Table | In schema | Status |
|---|---|---|
| `buyer_mgmt.qualified_buyer_codes` | buyer_mgmt | Created in V9 |
| `buyer_mgmt.qbc_buyer_codes` | buyer_mgmt | Created in V9 |
| `buyer_mgmt.qbc_scheduling_auctions` | buyer_mgmt | **Dangling FK** — `scheduling_auction_id BIGINT` (no constraint) |
| `buyer_mgmt.qbc_bid_rounds` | buyer_mgmt | **Dangling FK** — `bid_round_id BIGINT` (no constraint) |
| `buyer_mgmt.qbc_query_helper_auctions` | buyer_mgmt | **Dangling FK** — `auction_id BIGINT` (no constraint) |
| `buyer_mgmt.qbc_submitted_by` | buyer_mgmt | Created in V9 |
| `buyer_mgmt.auctions_feature_config` | buyer_mgmt | Noted as seeded by V18 per CLAUDE.md — **verify** during implementation; if not present, add in V58 (see §7, open question Q3) |

**Nothing in an `auctions` schema exists yet.** The schema itself is not
created in V1 (`V1__create_schemas.sql` only lists identity, user_mgmt,
buyer_mgmt, sso, pws, mdm, integration) — V58 must `CREATE SCHEMA auctions`.

## 3. Legacy Entity Inventory → Modern Mapping

Tables are grouped by purpose. Row counts are from prod snapshot.

### 3.1 Core auction & scheduling (load-bearing, hot)

**Design note (2026-04-15):** Mendix forces all associations through
junction tables. Where the row counts reveal an effective N:1
relationship (one side always has ≤1 row per parent), we collapse the
junction into a direct FK column on the child. Only true M:M and
optional-link cases remain as junction tables.

| Legacy table | Rows | Modern table | Purpose |
|---|---|---|---|
| `auctionui$auction` | 5 | `auctions.auctions` | One row per weekly auction event |
| `auctionui$schedulingauction` | 15 | `auctions.scheduling_auctions` | Round-level timing + notification state (3 rows per auction). **`auction_id` FK column** absorbs `schedulingauction_auction` junction (15 rows = N:1). |
| `auctionui$bidround` | 1,917 | `auctions.bid_rounds` | Per-buyer-code snapshot of a round's submission state. **`scheduling_auction_id`, `buyer_code_id`, `week_id` FK columns** absorb `bidround_schedulingauction` (1,924), `bidround_buyercode` (1,924), `bidround_week` (119) junctions — all N:1. |
| `auctionui$auction_week` | 5 | **collapsed** — `auctions.auctions.week_id` FK | 1 week per auction (5/5) → direct FK, no junction |
| `auctionui$schedulingauction_auction` | 15 | **collapsed** → `scheduling_auctions.auction_id` | |
| `auctionui$bidround_schedulingauction` | 1,924 | **collapsed** → `bid_rounds.scheduling_auction_id` | |
| `auctionui$bidround_buyercode` | 1,924 | **collapsed** → `bid_rounds.buyer_code_id` | |
| `auctionui$bidround_week` | 119 | **collapsed** → `bid_rounds.week_id` (nullable) | 119/1917 rows → sparse N:1, FK is nullable |
| `auctionui$bidround_submittedby` | 1,145 | **collapsed** → `bid_rounds.submitted_by_user_id` (nullable) | 1,145/1,917 → sparse N:1 |
| `auctionui$schedulingauction_qualifiedbuyers` | 5,735 | **drop** — already covered by `buyer_mgmt.qbc_scheduling_auctions` | Duplicate junction; Mendix had it on both sides |

### 3.2 Bid data (the huge fact table)

All three Mendix junctions have row counts equal to the parent `biddata`
fact table → pure N:1 links. **Collapse all three into FK columns** on
`auctions.bid_data`. This saves ~35M rows of pure junction-table bloat
(relevant even at the 450K-row last-2-weeks scope).

| Legacy table | Rows | Modern table | Purpose |
|---|---|---|---|
| `auctionui$biddata` | **11,619,882** | `auctions.bid_data` | Per-buyer-code / per-grade bid fact rows. Adds FK columns: `bid_round_id`, `buyer_code_id`, `aggregated_inventory_id`. |
| `auctionui$biddata_bidround` | 11,619,882 | **collapsed** → `bid_data.bid_round_id` | |
| `auctionui$biddata_buyercode` | 11,619,802 | **collapsed** → `bid_data.buyer_code_id` | |
| `auctionui$biddata_aggregatedinventory` | 11,619,882 | **collapsed** → `bid_data.aggregated_inventory_id` | |

### 3.3 Aggregated inventory (per-device rollup per week)

Same collapse pattern — every junction is 80,298 rows vs. 87,284 parent
rows, effectively N:1 with ~8% sparsity. Direct FK columns with
`NULL`-able semantics.

| Legacy table | Rows | Modern table | Purpose |
|---|---|---|---|
| `auctionui$aggregatedinventory` | 87,284 | `auctions.aggregated_inventory` | Device × week × grade rollup. Adds FK columns: `brand_id`, `model_id`, `carrier_id`, `week_id`. (Plus optional secondary: `r2_po_max_bid_week_id`, `r3_po_max_bid_week_id`.) |
| `auctionui$aggregatedinventory_brand` | 80,298 | **collapsed** → `aggregated_inventory.brand_id` | |
| `auctionui$aggregatedinventory_model` | 80,298 | **collapsed** → `aggregated_inventory.model_id` | |
| `auctionui$aggregatedinventory_carrier` | 80,298 | **collapsed** → `aggregated_inventory.carrier_id` | |
| `auctionui$aggregatedinventory_modelname` | 80,298 | **drop** — Q1 resolved: not needed (MasterDeviceInventory handles it in modern design) | |
| `auctionui$aggregatedinventory_week` | 80,298 | **collapsed** → `aggregated_inventory.week_id` | |
| `auctionui$aggreegatedinventorytotals` | 9 | `auctions.aggregated_inventory_totals` | Per-week totals summary. Adds FK column: `week_id`. |
| `auctionui$agginventorytotals_week` | 7 | **collapsed** → `aggregated_inventory_totals.week_id` | |
| `auctionui$r2pomaxbidweek` | 3,633 | **collapsed** → `aggregated_inventory.r2_po_max_bid_week_id` (nullable) | |
| `auctionui$r3pomaxbidweek` | 3,635 | **collapsed** → `aggregated_inventory.r3_po_max_bid_week_id` (nullable) | |

### 3.4 Configuration & reference

| Legacy table | Rows | Modern table | Purpose |
|---|---|---|---|
| `auctionui$bidroundselectionfilter` | 2 | `auctions.bid_round_selection_filters` | Round-2/3 qualification criteria (target %, merged grades) |
| `auctionui$targetpricefactor` | 24 | `auctions.target_price_factors` | Band-based factor (min/max value → percentage or flat) |
| `auctionui$targetpricefactor_bidroundselectionfilter` | 24 | `auctions.target_price_factor_filters` | M:M link |
| `auctionui$bidranking` | 1 | `auctions.bid_ranking_config` | Singleton: displayRank, minBid, maxRank |
| `auctionui$sharepointmethod` | 1 | `auctions.sharepoint_method_config` | Singleton: OQL vs API upload mode |
| `auctionui$biddatatotalquantityconfig` | 4 | `auctions.bid_data_quantity_overrides` | Per-ecoid grade quantity override |

### 3.5 Reporting & documents

| Legacy table | Rows | Modern table | Purpose |
|---|---|---|---|
| `auctionui$allbiddownload` + `allbiddownload_2` | 17,776 + 4,335,383 | `auctions.all_bid_downloads` (merged) | Q2 resolved: single table with `format_version smallint` discriminator + superset of columns. |
| `auctionui$allbidsdoc`, `biddatadoc` | 1,982 / 18,858 | `auctions.all_bids_documents`, `auctions.bid_data_documents` | Q4 resolved: **keep Mendix blobstore convention** — Mendix persists `FileDocument` blobs to its internal blobstore automatically. Mirror that: store blob bytes in a `blob bytea` column (or move to external object store if a storage ADR lands) plus `file_name`, `file_size`, `mime_type`, `has_contents` metadata. Parent FK via direct column, not junction. |
| `auctionui$roundthreebuyersdatareport` | 1,958 | `auctions.round3_buyer_data_reports` | Round-3 submission audit. Adds `auction_id` FK column. |
| `auctionui$roundthreebuyersdatareport_auction` | 740 | **collapsed** → `round3_buyer_data_reports.auction_id` (nullable, 740/1,958 sparse) | |
| `auctionui$bidsubmitlog` | 24,402 | `auctions.bid_submit_log` | Retry log. Adds `bid_round_id` FK column (nullable, 3,850/24,402 sparse). |
| `auctionui$bidsubmitlog_bidround` | 3,850 | **collapsed** → `bid_submit_log.bid_round_id` | |

### 3.6 Helpers & deprecated (skip or stub)

`agginventoryhelper`, `agginventoryhelper_*`, `biddataqueryhelper*`,
`buyercodeselect_helper_*`, `biddatadeletehelper`, `datadogtest`,
`allbidszipped`, `allbidsziptemplist`, `zerobiddownload`,
`roundthreebiddataexcelexport*`, `biddata_importsettings*` —
Mendix session/request-scoped helpers. **Do not migrate as tables.**
Their purpose is request-cached computation; in Spring these become
DTOs / `@Cacheable` methods. Total: ~15 tables skipped.

**Entity count summary:** ~50 legacy tables → **~17 modern tables** in
the new `auctions` schema (most Mendix junctions collapsed to FK columns
per the design note in §3.1). Plus the 3 existing dangling FKs in
`buyer_mgmt` being tightened (V64) and one pre-req `mdm.week` creation.

## 4. Relationships (FK map)

### Into `auctions`
- `scheduling_auctions.auction_id` → `auctions.auctions(id)`
- `bid_rounds.scheduling_auction_id` → `auctions.scheduling_auctions(id)` (derived from `bidround_schedulingauction` junction — 1:1 in practice per 1924/1917 row counts, but keep the junction to match Mendix cardinality exactly)
- `bid_data.bid_round_id` (via junction) → `auctions.bid_rounds(id)`
- `bid_data.aggregated_inventory_id` (via junction) → `auctions.aggregated_inventory(id)`
- `all_bids_documents` / `bid_data_documents` → optional parent rows in `all_bid_downloads` / `bid_data`

### Out of `auctions` into existing schemas
| FK column | References |
|---|---|
| `bid_data.buyer_code_id` (via junction) | `buyer_mgmt.buyer_codes(id)` |
| `bid_round_buyer_codes.buyer_code_id` | `buyer_mgmt.buyer_codes(id)` |
| `aggregated_inventory_brands.brand_id` | `mdm.brand(id)` |
| `aggregated_inventory_models.model_id` | `mdm.model(id)` |
| `aggregated_inventory_carriers.carrier_id` | `mdm.carrier(id)` |
| `aggregated_inventory_weeks.week_id` | `mdm.week(id)` — **Q5**: verify `mdm.week` table exists; V21 lists mdm tables but "week" isn't in the CLAUDE.md row-count table |
| `bid_rounds.owner_id` / `changed_by_id` | `identity.users(id)` |
| `bid_round_submitted_by.user_id` | `user_mgmt.ecoatm_direct_users(id)` |
| `auctions.owner_id` / `changed_by_id` | `identity.users(id)` |
| `all_bid_downloads.device_id` (nullable text UUID in Mendix) | **leave as text**, do not FK to `mdm.device` — Mendix uses string UUIDs |

### Back into `buyer_mgmt` (tightening V9)
After V58 creates `auctions.scheduling_auctions`, `auctions.bid_rounds`,
`auctions.auctions`, V59 adds the three deferred constraints:
- `buyer_mgmt.qbc_scheduling_auctions.scheduling_auction_id` → `auctions.scheduling_auctions(id)`
- `buyer_mgmt.qbc_bid_rounds.bid_round_id` → `auctions.bid_rounds(id)`
- `buyer_mgmt.qbc_query_helper_auctions.auction_id` → `auctions.auctions(id)`

## 5. Proposed Flyway Migrations

### V58 — `create_auctions_schema_and_core.sql`
- **Pre-req:** `CREATE TABLE mdm.week` (port of `ecoatm_mdm$week`) —
  required FK target for `auction_weeks`, `bid_round_weeks`,
  `aggregated_inventory_weeks`, `aggregated_inventory_totals_weeks`,
  `r2_po_max_bid_weeks`, `r3_po_max_bid_weeks`. Seed 157 rows from
  legacy in a follow-up data migration (extend V21 or new V58.5).
- `CREATE SCHEMA auctions`
- `auctions.auctions` — PK id, auction_title, auction_status (enum), created_date, changed_date, owner_id → identity.users, changed_by_id → identity.users, created_by (text, Mendix username), updated_by (text)
- `auctions.auction_weeks` — (auction_id, week_id) junction
- `auctions.scheduling_auctions` — PK id, auction_week_year, round (int), name, start_datetime, end_datetime, round_status (enum), round3_init_status (enum), email_reminders (enum), has_round, is_start_notification_sent, is_end_notification_sent, is_reminder_notification_sent, notifications_enabled, snowflake_json (text), updated_by, created_by, owner_id, changed_by_id
- `auctions.scheduling_auction_auctions` — junction
- Indexes: `auctions(auction_status)`, `scheduling_auctions(round_status, start_datetime)`

### V59 — `auctions_bid_rounds_and_configuration.sql`
- `auctions.bid_rounds` — PK id, submitted bool, submitted_datetime, uploaded_to_sharepoint bool, upload_to_sharepoint_datetime, note varchar(2000), is_deprecated bool, created/changed audit cols
- `auctions.bid_round_scheduling_auctions` — junction (implements the 1:N in Mendix style)
- `auctions.bid_round_buyer_codes` — junction → `buyer_mgmt.buyer_codes`
- `auctions.bid_round_weeks` — junction → `mdm.week`
- `auctions.bid_round_submitted_by` — junction → `user_mgmt.ecoatm_direct_users`
- `auctions.bid_round_selection_filters` — PK id, round, target_percent, target_value, total_value_floor, merged_grade1/2/3, stb_allow_all_buyers_override, stb_include_all_inventory, regular_buyer_qualification (enum), regular_buyer_inventory_options (enum)
- `auctions.target_price_factors` — PK id, minimum_value, maximum_value, factor_type (enum Percentage_Factor|Flat_Amount), factor_amount
- `auctions.target_price_factor_filters` — junction
- `auctions.bid_ranking_config` — singleton
- `auctions.sharepoint_method_config` — singleton
- **Note:** deferred-FK tighten on `buyer_mgmt.qbc_*` is **moved to V64**, to run AFTER the extractor populates `auctions.*`. See §7 risk mitigation.

### V60 — `auctions_aggregated_inventory.sql`
- `auctions.aggregated_inventory` — PK id, ecoid2, name, brand (text denorm), model (text denorm), carrier (text denorm), merged_grade, datawipe, avg_payout, avg_target_price, dw_total_quantity, total_quantity, category, dw_avg_target_price, device_id (text UUID), created_at, total_payout, dw_avg_payout, dw_total_payout, round1_target_price, round2_target_price, round3_target_price, round1_target_price_dw, round2_max_bid, round1_max_bid, r2_target_price_factor, r3_target_price_factor, r2_target_price_factor_type, r3_target_price_factor_type, round2_eb_for_target, round3_eb_for_target, round1_max_bid_buyer_code (text), round2_max_bid_buyer_code (text), r2_po_max_buyer_code, r2_po_max_bid, r3_po_max_buyer_code, r3_po_max_bid, is_total_quantity_modified, is_deprecated, audit cols
- `auctions.aggregated_inventory_brands` / `_models` / `_carriers` / `_model_names` / `_weeks` — junctions
- `auctions.aggregated_inventory_totals` (+ `_weeks` junction)
- `auctions.r2_po_max_bid_weeks`, `auctions.r3_po_max_bid_weeks` — junctions
- `auctions.bid_data_quantity_overrides` — per-ecoid grade override

### V61 — `auctions_bid_data.sql` (the fact table)
- `auctions.bid_data` — PK id, ecoid, bid_quantity, bid_amount, target_price, merged_grade, buyer_code_type, payout, code (text denorm), previous_round_bid_quantity, previous_round_bid_amount, submit_datetime, maximum_quantity, highest_bid bool, bid_round int, company_name, margin, week_id int (denorm), submitted_datetime, submitted_bid_amount, submitted_bid_quantity, temp_da_bid_amount, rejected, is_changed, reject_reason text, accept_reason, merged_grade_text text, is_deprecated, data_wipe_quantity, last_valid_bid_quantity, last_valid_bid_amount, display_round2_bid_rank, display_round3_bid_rank, round2_bid_rank int, round3_bid_rank int, user text, audit cols
- `auctions.bid_data_bid_rounds` — junction (expect ~11.6M rows — see §6)
- `auctions.bid_data_buyer_codes` — junction
- `auctions.bid_data_aggregated_inventory` — junction
- **Indexes** (critical at 11.6M rows): `(bid_round)`, `(ecoid, bid_round)`, `(merged_grade, bid_round)`, `(company_name)`, junction tables get PK + reverse indexes

### V62 — `auctions_reporting_and_logs.sql`
- `auctions.all_bid_downloads` (current format)
- `auctions.all_bid_downloads_v2` OR merge (see Q2)
- `auctions.all_bids_documents`, `auctions.bid_data_documents` (metadata-only per Q4)
- `auctions.round3_buyer_data_reports` + `_auctions` junction
- `auctions.bid_submit_log` + `_bid_rounds` junction
- `auctions.all_bid_download_documents` junction

### V63 — `auctions_seed_config.sql`
- INSERT singletons: `bid_ranking_config`, `sharepoint_method_config`,
  default `target_price_factors` bands
- No bulk data migration here — historical `biddata` and
  `aggregated_inventory` (last 2 weeks only, per §7 decision 1) will be
  handled by an extension to `migration_scripts/extract_qa_data.py` in a
  follow-up PR, not by Flyway (consistent with V16–V24 convention:
  schema in pre-V15, data in V16+).

### V64 — `tighten_qbc_auctions_fks.sql`
Runs AFTER the extractor has populated `auctions.auctions`,
`auctions.scheduling_auctions`, and `auctions.bid_rounds` with remapped
IDs referenced by the existing V23 `buyer_mgmt.qbc_*` rows.
- `ALTER TABLE buyer_mgmt.qbc_scheduling_auctions ADD CONSTRAINT fk_qbc_sa_scheduling_auction FOREIGN KEY (scheduling_auction_id) REFERENCES auctions.scheduling_auctions(id)`
- `ALTER TABLE buyer_mgmt.qbc_bid_rounds ADD CONSTRAINT fk_qbc_br_bid_round FOREIGN KEY (bid_round_id) REFERENCES auctions.bid_rounds(id)`
- `ALTER TABLE buyer_mgmt.qbc_query_helper_auctions ADD CONSTRAINT fk_qbc_qh_auction FOREIGN KEY (auction_id) REFERENCES auctions.auctions(id)`

## 6. Enum / Status Values (verified against qa-0407 + migration_context)

Values confirmed by grepping `migration_context/backend/` for Mendix
enum literals and by querying the `qa-0407` source DB on 2026-04-15.

### `auctions.auctions.auction_status` — `AuctionUI.Enum_AuctionStatus`
- `Unscheduled`
- `Scheduled`
- `Started`
- `Closed`

(DB sample shows only `Closed`; full literal set extracted from
`ACT_Create_Auction`, `ACT_SetAuctionScheduleStarted`,
`ACT_SetAuctionScheduleClosed`, `ACT_Delete_Auction` microflows.)

### `auctions.scheduling_auctions.round_status` — `AuctionUI.Enum_SchedulingAuctionStatus`
- `Unscheduled`
- `Scheduled`
- `Started`
- `Closed`

### `auctions.scheduling_auctions.round3_init_status` — `AuctionUI.Enum_ScheduleAuctionInitStatus`
- `Pending` (DB sample + schema default)
- `Complete` (set by round3 init microflow)

### `auctions.scheduling_auctions.email_reminders` — `AuctionUI.ENUM_ReminderEmails`
- `NoneSent` (DB sample)
- `OneHourSent`
- `FourHourSent` (DB sample)
- `AllSent`

### `auctions.bid_data_quantity_overrides.grade` / `bid_data.merged_grade`
From `biddata.merged_grade`, `aggregatedinventory.mergedgrade`,
`biddataquantityconfig.grade`:
- `A_YYY`
- `B_NYY/D_NNY`
- `C_YNY/G_YNN`
- `E_YYN`
- `F_NYN/H_NNN`

### `auctions.target_price_factors.factor_type`
From `targetpricefactor.factortype` and
`aggregatedinventory.r2targetpricefactortype` / `r3targetpricefactortype`:
- `Percentage_Factor`
- `Flat_Amount`

### `auctions.bid_round_selection_filters.regular_buyer_qualification` — `AuctionUI.Enum_RegularBuyerQualification`
- `Only_Qualified` (DB sample)
- `All_Buyers` (from `RegularBuyerQualification.All_Buyers` microflow ref)

### `auctions.bid_round_selection_filters.regular_buyer_inventory_options` — `AuctionUI.Enum_RegularBuyerInventoryOption`
- `InventoryRound1QualifiedBids` (DB sample)
- `ShowAllInventory` (from `RegularBuyerInventoryOption.ShowAllINventory` microflow ref — note legacy typo preserved; clean up on port)

### `auctions.bid_submit_log.submit_action`
From `bidsubmitlog.submitaction`:
- `User_Submit`
- `Push_To_Sharepoint`

### `auctions.bid_submit_log.status`
From `bidsubmitlog.status`:
- `Success`
- `Error`

### `buyer_mgmt.qualified_buyer_codes.qualification_type` (already in V9)
- `Qualified`
- `Not_Qualified`
- `Manual`

## 7. Decisions, Gotchas & Open Questions

### Confirmed decisions (2026-04-15)
1. **Historical `bid_data` scope → last 2 weeks only.** The 11.6M-row
   fact table is not fully migrated. The `extract_qa_data.py` extension
   will filter `biddata` (and its three junctions + `aggregatedinventory`
   + junctions) to rows whose `mdm.week.weekstartdatetime` falls within
   the most recent 2 weeks relative to extraction time. Everything older
   stays in Snowflake. Expected modern-DB row count: ~450K bid_data rows
   (~11.6M × 2/52), well within a reasonable Flyway-adjacent bulk load.
2. **IDs are fully remapped — Mendix IDs are NOT preserved.** Consistent
   with CLAUDE.md convention (V16–V24). V58+ tables use
   `GENERATED BY DEFAULT AS IDENTITY` PKs. The extractor script builds
   FK-remap dictionaries across `auctions$auction`,
   `schedulingauction`, `bidround`, `aggregatedinventory`, `biddata`,
   and back into `buyer_mgmt.qualified_buyer_codes` for the V9
   deferred-FK tighten. No collision risk with pre-existing V23 QBC
   rows because the tighten happens as a constraint add, not a data
   rewrite — the new ID space is the one being referenced.
3. **`mdm.week` is confirmed to exist in legacy** (`ecoatm_mdm$week`,
   157 rows, 15 cols). It is **NOT yet in modern Flyway** — V13 creates
   the mdm schema but omits `week`. **Action:** add a pre-req migration
   **V57.5** (or fold into V58) that creates `mdm.week` before any
   auctions FK targets it. Columns to port verbatim: `week_id bigint`,
   `year int`, `week_number int`, `week_start_datetime`,
   `week_end_datetime`, `week_display varchar(200)`,
   `week_display_short varchar(200)`, `week_number_string varchar(200)`,
   `auction_data_purged boolean`, plus audit cols. PK: `id`. Seed data
   comes from extending V21 or a new data migration pulling all 157 rows.

### Remaining risks
- **FK tighten failure on empty reference.** If the `auctions` data
  load runs AFTER V59 creates the FK constraints, rows in
  `buyer_mgmt.qbc_scheduling_auctions` (loaded by V23) will temporarily
  point at missing `auctions.scheduling_auctions.id` values. Mitigation:
  V59 adds the FK **before** data load, but `qbc_scheduling_auctions`
  rows already exist. Options: (a) defer the FK tighten to a post-data
  migration V64, or (b) truncate+reload `qbc_*` junction tables after
  auction data lands. Recommend **(a)** — move FK tighten from V59 to
  V64 (runs after extractor populates `auctions.*`).

### Resolved questions (2026-04-15)
- **Q1.** `aggregatedinventory_modelname` junction **dropped**.
  `MasterDeviceInventory` covers the model-name dimension in the modern
  design — no `mdm.model_name` table needed.
- **Q2.** `allbiddownload` + `allbiddownload_2` **merged** into a single
  `auctions.all_bid_downloads` table with a `format_version smallint`
  column and the superset of fields.
- **Q3.** `buyer_mgmt.auctions_feature_config` — confirmed acceptable;
  verify existence during V58 implementation and create if missing.
- **Q4.** File documents — **mirror Mendix `FileDocument` blobstore
  pattern**. Persist blob bytes in `bytea` columns alongside file
  metadata (`file_name`, `file_size`, `mime_type`, `has_contents`).
  Matches Mendix's internal blobstore behavior; no external object
  store required for Phase 1.
- **Q6.** Enum values **verified** via qa-0407 query +
  `migration_context/backend/` grep. See §6 for the complete lists
  (Enum_AuctionStatus, Enum_SchedulingAuctionStatus,
  Enum_ScheduleAuctionInitStatus, ENUM_ReminderEmails,
  Enum_RegularBuyerQualification, Enum_RegularBuyerInventoryOption).
- **Q7.** `biddata.system$owner` FK target → `identity.users`, matching
  the V9 convention.

## 8. Dependency Order of Application

```
V1 create_schemas             (existing)  — needs auctions schema added OR V58 adds it
V8 buyer_mgmt_core            (existing)  — buyer_codes must exist before auctions FKs
V9 buyer_mgmt_qualified_codes (existing)  — dangling FKs tightened by V59
V13 mdm_schema                (existing)  — brand/model/carrier/week must exist (Q5)
V18 buyer_mgmt data           (existing)  — buyer_codes rows loaded
V21 mdm data                  (existing)  — brand/model rows loaded
V23 qualified_buyer_codes data(existing)  — QBC rows loaded with dangling FK cols

-- new work --
V58 auctions schema + core    (new)       CREATE SCHEMA, auctions, scheduling_auctions
V59 bid_rounds + config       (new)       bid_rounds + tighten V9 FKs
V60 aggregated_inventory      (new)       big rollup table + junctions
V61 bid_data fact table       (new)       bid_data + 3 junctions, indexes only (empty)
V62 reporting + logs          (new)       downloads, documents, audit logs
V63 seed auction config       (new)       singletons + target_price_factors
```

**Critical ordering within this batch:** V58 must run before the V9 FK
tighten in V59. V61 indexes must exist before any future bulk load of
historical `bid_data`. V63 (seed singletons) is safe at any point after
V59.

## 9. Out of Scope (deferred to follow-up PRs)
- Historical bulk-data load for `bid_data` / `aggregated_inventory` —
  extend `migration_scripts/extract_qa_data.py` in a separate task.
- Snowflake push pipeline replacement (legacy: `sendbiddatatosnowflake`,
  `sendauctiondatatosnowflake` in `auctionsfeature`) — architectural
  decision needed, will become its own ADR.
- Mendix session-scoped helpers (`*queryhelper*`, `*sessionandtab*`) —
  replaced with server-side `@Cacheable` in services, no tables.
- `FileDocument` blob migration strategy (Q4) — needs storage ADR.
