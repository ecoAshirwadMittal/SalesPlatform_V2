## ecoatm_direct_theme

### idletimeout
- **Table:** `ecoatm_direct_theme$idletimeout` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 134263563892917997 |
| lastrecordedactivity | timestamp without time zone | Yes | 2025-10-07 18:08:50.59 |
| idletimeextension | timestamp without time zone | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-10-07 18:08:50.59 |
| changeddate | timestamp without time zone | Yes | 2025-10-07 18:08:50.59 |
| system$owner | bigint | Yes | 23925373020739037 |
| system$changedby | bigint | Yes | 23925373020739037 |
- **PK:** id

### idletimeout_session
- **Table:** `ecoatm_direct_theme$idletimeout_session` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_direct_theme$idletimeoutid | bigint | No | 134263563892917997 |
| system$sessionid | bigint | No | 6755399472448278 |
- **PK:** ecoatm_direct_theme$idletimeoutid, system$sessionid

### idletimeoutconfiguration
- **Table:** `ecoatm_direct_theme$idletimeoutconfiguration` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 139611588448485574 |
| serialnumber | bigint | Yes | 1 |
| idletimeoutwarning | integer | Yes | 900 |
| multitabcheckinterval | integer | Yes | 10 |
| timerwarningmessage | text | Yes |  |
| warningseconds | integer | Yes | 300 |
| isactive | boolean | Yes | true |
| system$changedby | bigint | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
- **PK:** id

---
