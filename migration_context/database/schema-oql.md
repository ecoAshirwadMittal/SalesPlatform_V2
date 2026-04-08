## oql

### exampleperson
- **Table:** `oql$exampleperson` | **Rows:** 6
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 78531518502273258, 78531518502273818, 78531518502273314 |
| number | bigint | Yes | 2, 1, 4 |
| name | character varying(200) | Yes | Walter Plinge, Joe Bloggs, John Doe |
| dateofbirth | timestamp without time zone | Yes | 1995-11-22 05:00:00, 2002-05-18 04:00:00, 1962-02-01 05:00:00 |
| age | integer | Yes | 22, 17, 34 |
| longage | bigint | Yes | 22, 17, 34 |
| active | boolean | Yes | true, false |
| heightindecimal | numeric | Yes | 4.90000000, 4.05000000, 0.00000000 |
| gender | character varying(6) | Yes | Other, Male, Female |
- **PK:** id

### marriedto
- **Table:** `oql$marriedto` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| oql$examplepersonid1 | bigint | No | 78531518502273314, 78531518502273575 |
| oql$examplepersonid2 | bigint | No | 78531518502273258, 78531518502273451 |
- **PK:** oql$examplepersonid1, oql$examplepersonid2

### csvdownload
- **Table:** `oql$csvdownload` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

---
