## ecoatm_eb

### reservebid
- **Table:** `ecoatm_eb$reservebid` | **Rows:** 15,875
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 102456891864529519, 102456891865141266, 102456891864812641 |
| productid | integer | Yes | 18509, 16692, 18469 |
| grade | character varying(200) | Yes | A_YYY, B_NYY/D_NNY, F_NYN/H_NNN |
| brand | character varying(200) | Yes | Maxwest, Amazon, Microsoft |
| model | character varying(200) | Yes | Razr Plus, Galaxy Note 10+ 512GB, Moto G42 |
| bid | numeric | Yes | 53.49000000, 181.73000000, 96.30000000 |
| lastupdatedatetime | timestamp without time zone | Yes |  |
| lastawardedminprice | numeric | Yes | 58.25000000, 50.03000000, 4.40000000 |
| lastawardedweek | character varying(200) | Yes | 2026-02-13, 2025-10-10, 2026-01-16 |
| bidvalidweekdate | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes | 2026-02-18 19:00:12.764, 2026-02-18 19:00:25.035, 2026-02-18 19:00:19.691 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 19:00:25.078, 2026-02-18 19:00:19.715, 2026-02-18 19:00:12.8 |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### reservedbidaudit
- **Table:** `ecoatm_eb$reservedbidaudit` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 175640385467449479, 175640385467462299, 175640385467475093 |
| oldprice | numeric | Yes | 31.28000000, 32.28000000, 5.80000000 |
| newprice | numeric | Yes | 31.28000000, 32.28000000, 5.80000000 |
| createddate | timestamp without time zone | Yes | 2026-01-07 22:17:39.139, 2026-01-07 22:18:50.333, 2026-01-16 17:37:02.324 |
| changeddate | timestamp without time zone | Yes | 2026-01-07 22:17:39.142, 2026-01-07 22:18:50.335, 2026-01-16 17:37:02.332 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### reservebidsync
- **Table:** `ecoatm_eb$reservebidsync` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 99923616732283059 |
| lastsyncdatetime | timestamp without time zone | Yes | 2026-02-18 05:00:05.491 |
| createddate | timestamp without time zone | Yes | 2025-01-16 21:17:13.252 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 19:00:34.71 |
| system$owner | bigint | Yes | 23925373020764769 |
| system$changedby | bigint | Yes | 23925373020725955 |
- **PK:** id

### reservebidfile
- **Table:** `ecoatm_eb$reservebidfile` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### reservebidfile_reservebid
- **Table:** `ecoatm_eb$reservebidfile_reservebid` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_eb$reservebidfileid | bigint | No |  |
| ecoatm_eb$reservebidid | bigint | No |  |
- **PK:** ecoatm_eb$reservebidfileid, ecoatm_eb$reservebidid

### reservedbidaudit_reservebid
- **Table:** `ecoatm_eb$reservedbidaudit_reservebid` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_eb$reservedbidauditid | bigint | No |  |
| ecoatm_eb$reservebidid | bigint | No |  |
- **PK:** ecoatm_eb$reservedbidauditid, ecoatm_eb$reservebidid

---
