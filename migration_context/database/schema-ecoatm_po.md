## ecoatm_po

### weeklypo
- **Table:** `ecoatm_po$weeklypo` | **Rows:** 12,384
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 100205091710025940, 100205091709881122, 100205091709230675 |
| qty | integer | Yes | 75, 551, 521 |
| price | numeric | Yes | 37.28000000, 26.63000000, 34.55000000 |
- **PK:** id

### weeklypo_purchaseorder
- **Table:** `ecoatm_po$weeklypo_purchaseorder` | **Rows:** 12,384
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$weeklypoid | bigint | No | 100205091710025940, 100205091709881122, 100205091709230675 |
| ecoatm_po$purchaseorderid | bigint | No | 98234766872300732, 98234766872095876, 98234766872441575 |
- **PK:** ecoatm_po$weeklypoid, ecoatm_po$purchaseorderid

### weeklypo_week
- **Table:** `ecoatm_po$weeklypo_week` | **Rows:** 12,384
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$weeklypoid | bigint | No | 100205091710025940, 100205091709881122, 100205091709230675 |
| ecoatm_mdm$weekid | bigint | No | 69805794224282661, 69805794224284612, 69805794224286614 |
- **PK:** ecoatm_po$weeklypoid, ecoatm_mdm$weekid

### podetail
- **Table:** `ecoatm_po$podetail` | **Rows:** 9,895
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 94294117218579196, 94294117204737740, 94294117203136324 |
| productid | integer | Yes | 18509, 18469, 12377 |
| grade | character varying(200) | Yes | A_YYY, F_NYN/H_NNN, B_NYY/D_NNY |
| modelname | character varying(200) | Yes | Propel 5G U6080AA, Galaxy S9 64GB AT&T SM-G960U, Moto G Stylus 5G XT2131/XT2... |
| price | numeric | Yes | 22.33000000, 58.45300000, 37.28000000 |
| qtycap | integer | Yes | 210, 123, 332 |
| pricefulfilled | numeric | Yes | 20.72070000, 22.33000000, 101.16000000 |
| qtyfullfiled | integer | Yes | 75, 579, 13 |
| createddate | timestamp without time zone | Yes | 2025-04-14 19:37:40.319, 2026-02-17 00:55:55.046, 2025-04-14 19:37:40.348 |
| changeddate | timestamp without time zone | Yes | 2025-02-08 13:00:05.644, 2025-01-23 22:57:10.799, 2025-01-23 22:57:10.41 |
| system$changedby | bigint | Yes | 23925373020470267, 23925373020444341, 23925373020418781 |
| system$owner | bigint | Yes | 23925373020470267, 23925373020444341, 23925373020418781 |
| tempbuyercode | character varying(200) | Yes | K2PO, ZDPO, TEPO2 |
- **PK:** id

### podetail_purchaseorder
- **Table:** `ecoatm_po$podetail_purchaseorder` | **Rows:** 9,895
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$podetailid | bigint | No | 94294117218579196, 94294117204737740, 94294117203136324 |
| ecoatm_po$purchaseorderid | bigint | No | 98234766872403081, 98234766872441575, 98234766872377577 |
- **PK:** ecoatm_po$podetailid, ecoatm_po$purchaseorderid

### podetail_buyercode
- **Table:** `ecoatm_po$podetail_buyercode` | **Rows:** 9,872
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$podetailid | bigint | No | 94294117218579196, 94294117204737740, 94294117203136324 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299119709, 32651097298911251, 32651097299132953 |
- **PK:** ecoatm_po$podetailid, ecoatm_buyermanagement$buyercodeid

### weeklypo_podetail
- **Table:** `ecoatm_po$weeklypo_podetail` | **Rows:** 6,590
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$weeklypoid | bigint | No | 100205091709708575, 100205091709666118, 100205091709311724 |
| ecoatm_po$podetailid | bigint | No | 94294117202034538, 94294117200996243, 94294117200911537 |
- **PK:** ecoatm_po$weeklypoid, ecoatm_po$podetailid

### purchaseorderdoc
- **Table:** `ecoatm_po$purchaseorderdoc` | **Rows:** 82
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 90634942501100383, 90634942501023919, 90634942501074795 |
- **PK:** id

### weekperiod
- **Table:** `ecoatm_po$weekperiod` | **Rows:** 54
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 100486566685717321, 100486566685704629, 100486566685832644 |
- **PK:** id

### weekperiod_purchaseorder
- **Table:** `ecoatm_po$weekperiod_purchaseorder` | **Rows:** 54
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$weekperiodid | bigint | No | 100486566685717321, 100486566685704629, 100486566685832644 |
| ecoatm_po$purchaseorderid | bigint | No | 98234766872313604, 98234766872479989, 98234766872095876 |
- **PK:** ecoatm_po$weekperiodid, ecoatm_po$purchaseorderid

### weekperiod_week
- **Table:** `ecoatm_po$weekperiod_week` | **Rows:** 54
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$weekperiodid | bigint | No | 100486566685717321, 100486566685704629, 100486566685832644 |
| ecoatm_mdm$weekid | bigint | No | 69805794224286333, 69805794224282308, 69805794224287045 |
- **PK:** ecoatm_po$weekperiodid, ecoatm_mdm$weekid

### pohelper
- **Table:** `ecoatm_po$pohelper` | **Rows:** 21
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 90353467524287186, 90353467524171917, 90353467524261504 |
| enum_actiontype | character varying(6) | Yes | _New |
| enablepoupdate | boolean | Yes | false |
| missingbuyercodevalidation | boolean | Yes | true, false |
| missingbuyercodelist | text | Yes |  |
| invalidfilevalidation | boolean | Yes | false |
| invalidpoperiod | boolean | Yes | false |
| filename | character varying(200) | Yes | PO_2026-8-2026-11.xlsx, PO_2026-4-2026-7 (1).xlsx, PO_2025_39_41.xlsx |
- **PK:** id

### pohelper_account
- **Table:** `ecoatm_po$pohelper_account` | **Rows:** 21
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$pohelperid | bigint | No | 90353467524287186, 90353467524171917, 90353467524261504 |
| administration$accountid | bigint | No | 23925373020444502, 23925373020444341, 23925373020777255 |
- **PK:** ecoatm_po$pohelperid, administration$accountid

### purchaseorder
- **Table:** `ecoatm_po$purchaseorder` | **Rows:** 13
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 98234766872403081, 98234766872441575, 98234766872377577 |
| weekrange | character varying(200) | Yes | 2025 / Wk16 - 2025 / Wk19, 2025 / Wk12 - 2025 / Wk15 |
| validyearweek | boolean | Yes | false |
| porefreshtimestamp | timestamp without time zone | Yes | 2025-04-12 13:00:06.394, 2025-07-21 22:26:09.936, 2025-02-08 13:00:06.062 |
| totalrecords | integer | Yes | 1193, 365, 532 |
- **PK:** id

### purchaseorder_week_from
- **Table:** `ecoatm_po$purchaseorder_week_from` | **Rows:** 13
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$purchaseorderid | bigint | No | 98234766872403081, 98234766872441575, 98234766872377577 |
| ecoatm_mdm$weekid | bigint | No | 69805794224284525, 69805794224283875, 69805794224288427 |
- **PK:** ecoatm_po$purchaseorderid, ecoatm_mdm$weekid

### purchaseorder_week_to
- **Table:** `ecoatm_po$purchaseorder_week_to` | **Rows:** 13
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$purchaseorderid | bigint | No | 98234766872403081, 98234766872441575, 98234766872377577 |
| ecoatm_mdm$weekid | bigint | No | 69805794224286558, 69805794224283709, 69805794224282661 |
- **PK:** ecoatm_po$purchaseorderid, ecoatm_mdm$weekid

### pohelper_purchaseorder
- **Table:** `ecoatm_po$pohelper_purchaseorder` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$pohelperid | bigint | No | 90353467524133653, 90353467524171917, 90353467524235949 |
| ecoatm_po$purchaseorderid | bigint | No | 98234766872134356, 98234766872224058, 98234766872249709 |
- **PK:** ecoatm_po$pohelperid, ecoatm_po$purchaseorderid

### purchaseorder_purchaseorderdoc
- **Table:** `ecoatm_po$purchaseorder_purchaseorderdoc` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_po$purchaseorderid | bigint | No |  |
| ecoatm_po$purchaseorderdocid | bigint | No |  |
- **PK:** ecoatm_po$purchaseorderid, ecoatm_po$purchaseorderdocid

---
