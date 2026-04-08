## ecoatm_lock

### lock
- **Table:** `ecoatm_lock$lock` | **Rows:** 1,015
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 168322036073065496, 168322036073616604, 168322036073215978 |
| objectid | bigint | Yes | 124130464731295198, 124130464731195047, 124130464731168204 |
| objecttype | character varying(200) | Yes | EcoATM_PWS |
| objectsource | character varying(200) | Yes | OfferPage |
| objectname | character varying(200) | Yes | Offer |
| lockedby | character varying(200) | Yes | contact@abctech.world, grande9029@gmail.com, lowell.wang@unineedgroup.com |
| lockedon | timestamp without time zone | Yes | 2026-02-03 21:13:41.619, 2025-12-15 19:17:05.419, 2025-12-16 15:41:09.183 |
| lockupdatedon | timestamp without time zone | Yes | 2026-02-04 18:18:33.047, 2026-01-28 16:30:31.687, 2025-11-17 04:21:11.995 |
| active | boolean | Yes | false |
- **PK:** id

---
