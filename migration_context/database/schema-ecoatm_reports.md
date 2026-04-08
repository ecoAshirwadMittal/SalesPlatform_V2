## ecoatm_reports

### buyerawardsummarytotals
- **Table:** `ecoatm_reports$buyerawardsummarytotals` | **Rows:** 41
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 103582791429879983, 103582791430315754, 103582791430021168 |
| salesqty | integer | Yes | 75143, 101527, 386552 |
| amount | numeric | Yes | 9514479.12000000, 4611050.78000000, 17513836.58000000 |
| weeklybudget | numeric | Yes | 2200000.00000000, 450.00000000, 1550000.00000000 |
| previousweeksalesqty | integer | Yes | 248365, 89830, 65897 |
| previousweekamount | numeric | Yes | 9038270.97000000, 8042455.38000000, 36403949.07000000 |
| previousweekweeklybudget | numeric | Yes | 2200000.00000000, 450.00000000, 1550000.00000000 |
- **PK:** id

### buyerawardsummarytotals_week
- **Table:** `ecoatm_reports$buyerawardsummarytotals_week` | **Rows:** 41
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_reports$buyerawardsummarytotalsid | bigint | No | 103582791429879983, 103582791430315754, 103582791430021168 |
| ecoatm_mdm$weekid | bigint | No | 69805794224287821, 69805794224286333, 69805794224287045 |
- **PK:** ecoatm_reports$buyerawardsummarytotalsid, ecoatm_mdm$weekid

### cohortmapping
- **Table:** `ecoatm_reports$cohortmapping` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| currentecoid | integer | Yes |  |
| currentmodelname | character varying(2000) | Yes |  |
| cohortecoid | integer | Yes |  |
| cohortmodelname | character varying(2000) | Yes |  |
| avgsellingprice | numeric | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### cohortmappingdoc
- **Table:** `ecoatm_reports$cohortmappingdoc` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

---
