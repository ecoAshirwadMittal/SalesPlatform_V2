## ecoatm_da

### deviceallocation
- **Table:** `ecoatm_da$deviceallocation` | **Rows:** 646,720
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 97108866968254981, 97108866968255126, 97108866968255295 |
| productid | integer | Yes | 18509, 247, 3542 |
| brand | character varying(200) | Yes | Access Wireless, Acer, Ainol Novo |
| modelname | character varying(200) | Yes |  EX112, Xperia Z3 Dual SIM D6633,  SGH-T719 |
| grade | character varying(200) | Yes | A_YYY, B_NYY/D_NNY, C_YNY/G_YNN |
| availableqty | integer | Yes | 1, 10, 100 |
| payout | numeric | Yes | 7.28000000, 313.67000000, 85.33000000 |
| eb | numeric | Yes | 223.77000000, 7.28000000, 318.91000000 |
| targetprice | numeric | Yes | 223.77000000, 483.47000000, 255.84000000 |
| salesqty | integer | Yes | 0, 1, 10 |
| avgsalesprice | numeric | Yes | 255.84000000, 591.63000000, 578.97000000 |
| revenue | numeric | Yes | 920.50000000, 7484.90000000, 2703.78000000 |
| mineb | numeric | Yes |  |
| minebweekending | timestamp without time zone | Yes |  |
| margin | numeric | Yes | 875.68000000, 1296.94000000, 2704.80000000 |
| marginpercentage | numeric | Yes | 7.28000000, -58.99000000, 85.33000000 |
| review | character varying(200) | Yes | NS, SU, US |
| ischanged | boolean | Yes | false |
| datawipetargetprice | numeric | Yes | 223.77000000, 313.67000000, 399.79000000 |
| nondatawipetargetprice | numeric | Yes | 223.77000000, 516.87000000, 255.84000000 |
| buyerjson | text | Yes |  |
| changeddate | timestamp without time zone | Yes | 2025-01-23 16:25:21.394, 2025-02-06 21:30:32.494, 2025-03-05 23:52:27.212 |
| datawipepayout | numeric | Yes | 0.00000000, 0.23000000, 0.33000000 |
| nondatawipeqty | integer | Yes | 0, 1, 10 |
| nondatawipepayout | numeric | Yes | 422.63000000, 7.28000000, 313.67000000 |
| system$owner | bigint | Yes | 23925373020418781, 23925373020444502, 23925373020470267 |
| system$changedby | bigint | Yes | 23925373020418781, 23925373020444502, 23925373020470267 |
| rowtype | character varying(200) | Yes | Detail |
| datawipeqty | integer | Yes | 0, 1, 10 |
| createddate | timestamp without time zone | Yes | 2025-04-16 21:00:26.185, 2025-03-17 16:26:52.107, 2025-07-09 23:30:09.231 |
- **PK:** id

### deviceallocation_daweek
- **Table:** `ecoatm_da$deviceallocation_daweek` | **Rows:** 646,720
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$deviceallocationid | bigint | No | 97108866968254981, 97108866968255126, 97108866968255295 |
| ecoatm_da$daweekid | bigint | No | 96827391988465825, 96827391988478610, 96827391988491436 |
- **PK:** ecoatm_da$deviceallocationid, ecoatm_da$daweekid

### devicebuyer
- **Table:** `ecoatm_da$devicebuyer` | **Rows:** 1,246
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 97671816919584907, 97671816919337936, 97671816919322305 |
| buyercode | character varying(200) | Yes | FK, YN, T7 |
| bid | numeric | Yes | 120.55000000, 198.20971867, 191.06000000 |
| qtycap | integer | Yes | 12, 10, 15 |
| awardedqty | integer | Yes | 224, 703, 12 |
| reject | boolean | Yes | false |
| rejectreason | text | Yes |  |
| changeddate | timestamp without time zone | Yes | 2025-04-04 00:58:43.636, 2025-01-23 16:27:38.925, 2025-12-12 21:45:50.685 |
| system$owner | bigint | Yes | 23925373021391862, 23925373020418781 |
| eb | boolean | Yes | true, false |
| system$changedby | bigint | Yes | 23925373021391862, 23925373020418781 |
| buyername | character varying(200) | Yes | Luckys Trade Company Limited, HK Xinda Dispatch Trade Ltd., RGH Assets |
| clearingbid | boolean | Yes | true, false |
| acceptreason | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-12-12 21:47:52.644, 2026-01-22 20:39:32.67, 2026-01-22 20:41:54.763 |
| ischanged | boolean | Yes | false |
- **PK:** id

### devicebuyer_daweek
- **Table:** `ecoatm_da$devicebuyer_daweek` | **Rows:** 1,246
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$devicebuyerid | bigint | No | 97671816919584907, 97671816919337936, 97671816919322305 |
| ecoatm_da$daweekid | bigint | No | 96827391989003519, 96827391988977880, 96827391988517105 |
- **PK:** ecoatm_da$devicebuyerid, ecoatm_da$daweekid

### devicebuyer_deviceallocation
- **Table:** `ecoatm_da$devicebuyer_deviceallocation` | **Rows:** 1,246
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$devicebuyerid | bigint | No | 97671816919584907, 97671816919337936, 97671816919322305 |
| ecoatm_da$deviceallocationid | bigint | No | 97108866984803571, 97108867688057751, 97108867732898180 |
- **PK:** ecoatm_da$devicebuyerid, ecoatm_da$deviceallocationid

### devicebuyer_biddata
- **Table:** `ecoatm_da$devicebuyer_biddata` | **Rows:** 1,236
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$devicebuyerid | bigint | No | 97671816919584907, 97671816919337936, 97671816919322305 |
| auctionui$biddataid | bigint | No | 55450584695959491, 55450573046378076, 55450587080648878 |
- **PK:** ecoatm_da$devicebuyerid, auctionui$biddataid

### buyersummary
- **Table:** `ecoatm_da$buyersummary` | **Rows:** 365
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 105834591245326698, 105834591245261940, 105834591243470728 |
| buyercode | character varying(200) | Yes | J7, KAAA1, DW92 |
| buyername | character varying(200) | Yes | Billion Peak Telecom Company, S & F Electronics LLC, Pre-Loved Tech LLC |
| salesqty | bigint | Yes | 123, 354, 75 |
| amount | numeric | Yes | 410.00000000, 13749.62000000, 3577.00000000 |
| weeklybudget | numeric | Yes | 1000000.00000000, 120000.00000000, 150000.00000000 |
| previousweeksalesqty | bigint | Yes | 123, 332, 8 |
| previousweekamount | numeric | Yes | 14546.00000000, 4208.00000000, 1961.80000000 |
| previousweekweeklybudget | numeric | Yes | 1000000.00000000, 120000.00000000, 150000.00000000 |
| currentecoatmgradedetails | text | Yes |  |
- **PK:** id

### buyersummary_week
- **Table:** `ecoatm_da$buyersummary_week` | **Rows:** 365
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$buyersummaryid | bigint | No | 105834591245326698, 105834591245261940, 105834591243470728 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288427, 69805794224283709, 69805794224286333 |
- **PK:** ecoatm_da$buyersummaryid, ecoatm_mdm$weekid

### daweek
- **Table:** `ecoatm_da$daweek` | **Rows:** 57
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 96827391989080258, 96827391988593858, 96827391988773066 |
| lastuploadtime | timestamp without time zone | Yes | 2026-01-29 05:05:00, 2025-09-04 04:04:21, 2025-04-03 02:15:35 |
| isfinalized | boolean | Yes | false |
| lastrefreshtime | timestamp without time zone | Yes | 2026-01-16 02:00:01.018, 2025-03-26 22:46:51.284, 2025-02-13 23:03:08.832 |
| finalizetimestamp | timestamp without time zone | Yes |  |
- **PK:** id

### daweek_week
- **Table:** `ecoatm_da$daweek_week` | **Rows:** 57
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$daweekid | bigint | No | 96827391989080258, 96827391988773066, 96827391988593858 |
| ecoatm_mdm$weekid | bigint | No | 69805794224287821, 69805794224286333, 69805794224282308 |
- **PK:** ecoatm_da$daweekid, ecoatm_mdm$weekid

### agregaterevenuetotal
- **Table:** `ecoatm_da$agregaterevenuetotal` | **Rows:** 56
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 97953291895564436, 97953291895782276, 97953291895999730 |
| totalpayout | numeric | Yes | 47.08000000, 36.46000000, 36.97000000 |
| totalavailableqty | bigint | Yes | 140188, 153136, 126512 |
| totalsold | bigint | Yes | 94231, 105732, 86729 |
| totalrevenue | numeric | Yes | 9933055.23000000, 12821792.78000000, 11671306.26000000 |
| totalmargin | numeric | Yes | 6478822.60000000, 4194853.03000000, 4512651.74000000 |
| marginpercentage | numeric | Yes | 44.67000000, 45.17000000, 48.72000000 |
| averagesellingprice | numeric | Yes | 100.83000000, 93.95000000, 93.28000000 |
| averagepurchaseprice | numeric | Yes | 47.08000000, 36.46000000, 36.97000000 |
- **PK:** id

### agregaterevenuetotal_daweek
- **Table:** `ecoatm_da$agregaterevenuetotal_daweek` | **Rows:** 56
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$agregaterevenuetotalid | bigint | No | 97953291895564436, 97953291895782276, 97953291895999730 |
| ecoatm_da$daweekid | bigint | No | 96827391988849836, 96827391988542709, 96827391988990638 |
- **PK:** ecoatm_da$agregaterevenuetotalid, ecoatm_da$daweekid

### dahelper
- **Table:** `ecoatm_da$dahelper` | **Rows:** 22
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 93449692268193961, 93449692268078732, 93449692268053147 |
| displayda_datagrid | boolean | Yes | true |
| dagridpersonlization | text | Yes |  |
| auctionenddate | timestamp without time zone | Yes | 2026-01-21 11:00:00, 2026-02-11 12:00:00, 2026-02-18 10:00:00 |
- **PK:** id

### dahelper_account
- **Table:** `ecoatm_da$dahelper_account` | **Rows:** 22
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$dahelperid | bigint | No | 93449692268193961, 93449692268078732, 93449692268053147 |
| administration$accountid | bigint | No | 23925373020444341, 23925373020444502, 23925373020777255 |
- **PK:** ecoatm_da$dahelperid, administration$accountid

### dahelper_daweek
- **Table:** `ecoatm_da$dahelper_daweek` | **Rows:** 17
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$dahelperid | bigint | No | 93449692268193961, 93449692268053147, 93449692267937957 |
| ecoatm_da$daweekid | bigint | No | 96827391988734631, 96827391988977880, 96827391989105897 |
- **PK:** ecoatm_da$dahelperid, ecoatm_da$daweekid

### dahelper_week
- **Table:** `ecoatm_da$dahelper_week` | **Rows:** 17
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$dahelperid | bigint | No | 93449692268193961, 93449692268078732, 93449692268104360 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288427, 69805794224283709, 69805794224284892 |
- **PK:** ecoatm_da$dahelperid, ecoatm_mdm$weekid

### buyerdetail
- **Table:** `ecoatm_da$buyerdetail` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| productid | integer | Yes |  |
| grade | character varying(200) | Yes |  |
| brand | character varying(200) | Yes |  |
| model | character varying(200) | Yes |  |
| avgsalesprice | numeric | Yes |  |
| salesqty | integer | Yes |  |
| amount | numeric | Yes |  |
- **PK:** id

### buyerdetail_buyersummary
- **Table:** `ecoatm_da$buyerdetail_buyersummary` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$buyerdetailid | bigint | No |  |
| ecoatm_da$buyersummaryid | bigint | No |  |
- **PK:** ecoatm_da$buyerdetailid, ecoatm_da$buyersummaryid

### buyerdetailtotals
- **Table:** `ecoatm_da$buyerdetailtotals` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| salesqty | integer | Yes |  |
| amount | numeric | Yes |  |
- **PK:** id

### buyerdetailtotals_buyersummary
- **Table:** `ecoatm_da$buyerdetailtotals_buyersummary` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$buyerdetailtotalsid | bigint | No |  |
| ecoatm_da$buyersummaryid | bigint | No |  |
- **PK:** ecoatm_da$buyerdetailtotalsid, ecoatm_da$buyersummaryid

### deviceallocation_dahelper
- **Table:** `ecoatm_da$deviceallocation_dahelper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_da$deviceallocationid | bigint | No |  |
| ecoatm_da$dahelperid | bigint | No |  |
- **PK:** ecoatm_da$deviceallocationid, ecoatm_da$dahelperid

---
