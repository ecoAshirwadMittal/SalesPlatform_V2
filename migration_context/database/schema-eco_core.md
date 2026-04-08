## eco_core

### pwsfeatureflag
- **Table:** `eco_core$pwsfeatureflag` | **Rows:** 14
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 102738366499453600, 102738366499389677, 102738366499440891 |
| name | character varying(200) | Yes | Snowflake Sync, PWS Pricing Snowflake, Enable A_YYY |
| active | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2026-02-18 21:19:11.413, 2026-01-07 21:30:17.518, 2025-08-18 20:34:15.994 |
| changeddate | timestamp without time zone | Yes | 2025-08-21 21:20:33.535, 2025-08-21 21:20:29.967, 2025-08-18 20:34:54.596 |
| system$owner | bigint | Yes | 23925373020815588, 23925373020418781, 23925373020431548 |
| system$changedby | bigint | Yes | 23925373020815588, 23925373020418781, 23925373020739037 |
| description | text | Yes |  |
- **PK:** id

### tile
- **Table:** `eco_core$tile` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| tilename | character varying(200) | Yes |  |
| url | character varying(200) | Yes |  |
| linktype | character varying(8) | Yes |  |
- **PK:** id

---
