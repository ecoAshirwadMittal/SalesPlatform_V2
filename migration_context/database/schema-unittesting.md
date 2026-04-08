## unittesting

### testsuite
- **Table:** `unittesting$testsuite` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| module | character varying(200) | Yes |  |
| lastrun | timestamp without time zone | Yes |  |
| lastruntime | bigint | Yes |  |
| testcount | bigint | Yes |  |
| testfailedcount | bigint | Yes |  |
| autorollbackmfs | boolean | Yes |  |
| result | character varying(10) | Yes |  |
| prefix1 | character varying(10) | Yes |  |
| prefix2 | character varying(10) | Yes |  |
- **PK:** id

### unittest
- **Table:** `unittesting$unittest` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(200) | Yes |  |
| result | character varying(10) | Yes |  |
| resultmessage | text | Yes |  |
| laststep | text | Yes |  |
| lastrun | timestamp without time zone | Yes |  |
| ismf | boolean | Yes |  |
| readabletime | character varying(300) | Yes |  |
| _dirty | boolean | Yes |  |
- **PK:** id

### unittest_testsuite
- **Table:** `unittesting$unittest_testsuite` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| unittesting$unittestid | bigint | No |  |
| unittesting$testsuiteid | bigint | No |  |
- **PK:** unittesting$unittestid, unittesting$testsuiteid

---
