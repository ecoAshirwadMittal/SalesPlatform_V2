## ecoatm_pwsintegration

### accesstoken
- **Table:** `ecoatm_pwsintegration$accesstoken` | **Rows:** 282,435
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 159877786771652825, 159877786771652870, 159877786771653057 |
| access_token | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-08-21 22:01:00.497, 2025-08-21 22:04:00.914, 2025-08-21 22:07:00.289 |
| changeddate | timestamp without time zone | Yes | 2025-08-21 22:01:00.497, 2025-08-21 22:04:00.915, 2025-08-21 22:07:00.29 |
| system$owner | bigint | Yes | 9851624184950013, 23925373020815588 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020815588 |
- **PK:** id

### facilityinventoryitem_test
- **Table:** `ecoatm_pwsintegration$facilityinventoryitem_test` | **Rows:** 58,138
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 152277976769617744, 152277976774138988, 152277976770586564 |
| facility | text | Yes |  |
| _parameter_ | integer | Yes | 8, 12, 10 |
| createddate | timestamp without time zone | Yes | 2026-02-19 19:36:12.182, 2026-02-19 19:36:09.141, 2026-02-19 19:35:58.386 |
| changeddate | timestamp without time zone | Yes | 2026-02-19 19:36:41.691, 2026-02-19 19:36:41.68, 2026-02-19 19:36:41.682 |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### inventory_test
- **Table:** `ecoatm_pwsintegration$inventory_test` | **Rows:** 58,138
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 155937151466618639, 155937151470982308, 155937151465457593 |
| total | text | Yes |  |
| availabletopromise | text | Yes |  |
| unallocated | text | Yes |  |
| allocated | text | Yes |  |
| _parameter_ | integer | Yes | 8, 12, 10 |
| createddate | timestamp without time zone | Yes | 2026-02-19 19:36:12.182, 2026-02-19 19:35:58.386, 2026-02-19 19:36:24.314 |
| changeddate | timestamp without time zone | Yes | 2026-02-19 19:36:41.691, 2026-02-19 19:36:41.68, 2026-02-19 19:36:41.682 |
| ecoatm_pwsintegration$inventory_test_facilityinventoryitem_test | bigint | Yes | 152277976769617744, 152277976774138988, 152277976770586564 |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### iteminventoryitem_test
- **Table:** `ecoatm_pwsintegration$iteminventoryitem_test` | **Rows:** 29,069
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 151152069707320816, 151152069705996369, 151152069708394596 |
| itemnumber | text | Yes |  |
| pageno | integer | Yes |  |
| _parameter_ | integer | Yes | 8, 12, 10 |
| createddate | timestamp without time zone | Yes | 2026-02-19 19:36:12.182, 2026-02-19 19:36:24.314, 2026-02-19 19:36:09.141 |
| changeddate | timestamp without time zone | Yes | 2026-02-19 19:36:41.691, 2026-02-19 19:36:41.68, 2026-02-19 19:36:41.682 |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### integration
- **Table:** `ecoatm_pwsintegration$integration` | **Rows:** 23,246
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 152840912828074197, 152840912827902282, 152840912827901249 |
| url | text | Yes |  |
| method | character varying(6) | Yes | POST, GET |
| request | text | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| endtime | timestamp without time zone | Yes | 2026-02-19 20:13:10.356, 2026-02-18 09:39:12.328, 2026-02-19 01:39:29.411 |
| response | text | Yes |  |
| responsecode | character varying(200) | Yes | NA |
| errortype | character varying(200) | Yes |  |
| errormessage | text | Yes |  |
| stacktrace | text | Yes |  |
| issuccessful | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2026-02-19 20:13:10.356, 2026-02-18 09:39:12.328, 2026-02-19 01:39:29.411 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 12:42:00.918, 2026-02-19 19:29:09.801, 2026-02-18 13:48:10.882 |
| system$owner | bigint | Yes | 23925373020892813, 23925373022224307, 9851624184950013 |
| system$changedby | bigint | Yes | 23925373020892813, 23925373022224307, 9851624184950013 |
- **PK:** id

### stockunititems
- **Table:** `ecoatm_pwsintegration$stockunititems` | **Rows:** 11,201
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 171418260817358582, 171418260817896658, 171418260816905931 |
| quantity | text | Yes |  |
| lpnnumber | text | Yes |  |
| itemnumber | text | Yes |  |
| allocatedordernumber | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2026-02-18 21:19:02.05, 2026-02-18 21:18:56.98, 2026-02-18 21:19:03.141 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:09.551, 2026-02-18 21:18:59.602, 2026-02-18 21:18:58.331 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### pwsresponseconfig
- **Table:** `ecoatm_pwsintegration$pwsresponseconfig` | **Rows:** 27
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 128352589380060974, 128352589380062376, 128352589380061765 |
| sourcesystem | character varying(200) | Yes | ORACLE |
| sourceerrorcode | character varying(200) | Yes | 05, 63, 08 |
| sourceerrortype | character varying(200) | Yes | INFO, ERROR |
| sourceerrormessage | text | Yes |  |
| usererrorcode | character varying(200) | Yes | PWS-01, PWS-60, PWS-07 |
| usererrormessage | character varying(500) | Yes | At least one line must be e..., Buyer Code is not active at..., Freight Carrier is not valid |
| bypassforuser | boolean | Yes | true, false |
- **PK:** id

### desposcoapis
- **Table:** `ecoatm_pwsintegration$desposcoapis` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 158188936911388911, 158188936911401676, 158188936911401789 |
| serviceurl | text | Yes |  |
| servicename | character varying(12) | Yes | StockUnit, Shipment, Inventory |
| createddate | timestamp without time zone | Yes | 2025-08-18 20:36:48.513, 2025-08-18 20:37:00.2, 2025-08-18 20:37:09.223 |
| changeddate | timestamp without time zone | Yes | 2025-08-18 20:36:58.881, 2025-08-18 20:37:07.688, 2025-08-18 20:37:29.123 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### desposcoapis_deposcoconfig
- **Table:** `ecoatm_pwsintegration$desposcoapis_deposcoconfig` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsintegration$desposcoapisid | bigint | No | 158188936911388911, 158188936911401676, 158188936911401789 |
| ecoatm_pwsintegration$deposcoconfigid | bigint | No | 153966812260729014 |
- **PK:** ecoatm_pwsintegration$desposcoapisid, ecoatm_pwsintegration$deposcoconfigid

### deposcoconfig
- **Table:** `ecoatm_pwsintegration$deposcoconfig` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 153966812260729014 |
| baseurl | text | Yes |  |
| username | character varying(500) | Yes | api.user |
| password | character varying(500) | Yes | ecoatm2025! |
| teststring | text | Yes |  |
| lastsynctime | timestamp without time zone | Yes | 2026-02-19 20:03:59.713 |
| pagecount | integer | Yes | 30 |
| reportattr | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-08-18 20:36:09.598 |
| changeddate | timestamp without time zone | Yes | 2026-02-19 20:04:42.766 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### pwsconfiguration
- **Table:** `ecoatm_pwsintegration$pwsconfiguration` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 130604389193757391 |
| oracleusername | character varying(200) | Yes | UhVw7dQhah5tcB_d0hroKg.. |
| oraclepassword | character varying(200) | Yes | QUUf0h6xyaWX_h8WceiGpg.. |
| oracleapipathtoken | character varying(400) | Yes | https://api-dev.ecoatm.com/... |
| oracleapipathcreateorder | character varying(400) | Yes | https://api-dev.ecoatm.com/... |
| oraclehttprequesttimeouttoken | integer | Yes | 300 |
| oraclehttprequesttimeoutcreateorder | integer | Yes | 300 |
| isoraclecreateorderapion | boolean | Yes | true |
| oracleapipathcreaterma | character varying(400) | Yes |  |
| isoraclecreatermaapion | boolean | Yes | false |
| oraclehttprequesttimeoutcreaterma | integer | Yes | 300 |
- **PK:** id

---
