## snowflakerestsql

### connectiondetails
- **Table:** `snowflakerestsql$connectiondetails` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | text | Yes |  |
| accounturl | text | Yes |  |
| resourcepath | text | Yes |  |
| accountidentifier | text | Yes |  |
| username | text | Yes |  |
| authenticationtype | character varying(11) | Yes |  |
- **PK:** id

### connectiondetails_account
- **Table:** `snowflakerestsql$connectiondetails_account` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| snowflakerestsql$connectiondetailsid | bigint | No |  |
| administration$accountid | bigint | No |  |
- **PK:** snowflakerestsql$connectiondetailsid, administration$accountid

### connectiondetails_privatekey
- **Table:** `snowflakerestsql$connectiondetails_privatekey` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| snowflakerestsql$connectiondetailsid | bigint | No |  |
| snowflakerestsql$privatekeyid | bigint | No |  |
- **PK:** snowflakerestsql$connectiondetailsid, snowflakerestsql$privatekeyid

### privatekey
- **Table:** `snowflakerestsql$privatekey` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| passphrase | text | Yes |  |
- **PK:** id

---
