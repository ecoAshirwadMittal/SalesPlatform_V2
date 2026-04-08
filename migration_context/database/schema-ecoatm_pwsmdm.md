## ecoatm_pwsmdm

### pricehistory
- **Table:** `ecoatm_pwsmdm$pricehistory` | **Rows:** 74,053
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 122160139900512454, 122160139898617906, 122160139894694201 |
| listprice | integer | Yes | 75, 395, 780 |
| minprice | integer | Yes | 257, 71, 675 |
| expirationdate | timestamp without time zone | Yes | 2025-01-31 00:00:00 |
| createddate | timestamp without time zone | Yes | 2025-01-31 16:28:57.202, 2025-01-30 21:38:07.232, 2025-01-31 03:12:23.303 |
| changeddate | timestamp without time zone | Yes | 2025-01-31 03:06:40.935, 2025-01-31 03:49:03.302, 2025-01-31 03:05:52.562 |
| system$owner | bigint | Yes | 23925373020828316, 23925373020418781, 23925373020431548 |
| system$changedby | bigint | Yes | 23925373020828316, 23925373020418781, 23925373020431548 |
- **PK:** id

### pricehistory_devicelist
- **Table:** `ecoatm_pwsmdm$pricehistory_devicelist` | **Rows:** 74,053
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$pricehistoryid | bigint | No | 122160139900512454, 122160139898617906, 122160139894694201 |
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
- **PK:** ecoatm_pwsmdm$pricehistoryid, ecoatm_pwsmdm$deviceid

### device
- **Table:** `ecoatm_pwsmdm$device` | **Rows:** 23,109
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| sku | character varying(200) | Yes | PWS000000100297, PWS000000100304, PWS000000100305 |
| devicecode | character varying(200) | Yes | 19750108, 19077563, 19073448 |
| devicedescription | character varying(200) | Yes | iPhone 15 Plus 512GB (Veriz..., Galaxy S23 Ultra 256GB (Unl..., Galaxy S22+ 5G 128GB (Veriz... |
| availableqty | integer | Yes | 258, 696, 592 |
| currentlistprice | integer | Yes | 75, 1145, 579 |
| currentminprice | integer | Yes | 675, 210, 845 |
| changeddate | timestamp without time zone | Yes | 2025-12-16 20:51:22.357, 2025-12-16 20:51:20.41, 2025-12-16 20:51:14.706 |
| searchattr | text | Yes |  |
| isactive | boolean | Yes | true, false |
| system$owner | bigint | Yes | 23925373020815588, 9851624184924352 |
| futurelistprice | integer | Yes | 0 |
| system$changedby | bigint | Yes | 23925373020777255, 23925373022364450, 23925373021014749 |
| futureminprice | integer | Yes | 0 |
| createddate | timestamp without time zone | Yes | 2025-05-26 08:45:57.16, 2025-10-02 03:13:42.202, 2025-10-02 03:11:46.748 |
| lastupdatedate | timestamp without time zone | Yes | 2025-12-05 02:11:41.439, 2025-10-18 03:51:26.533, 2026-01-26 20:41:05.998 |
| weight | numeric | Yes | 0.50000000, 0.49000000, 2.00000000 |
| itemtype | character varying(200) | Yes | SPB, PWS |
| modelyear | character varying(50) | Yes |  |
| reservedqty | integer | Yes | 10, -1, 5 |
| atpqty | integer | Yes | 258, 696, 592 |
| lastsynctime | timestamp without time zone | Yes | 2025-12-01 15:33:35.746, 2026-02-19 20:04:46.879, 2026-02-18 16:09:42.991 |
| deposcopageno | integer | Yes | 9, 8, 12 |
- **PK:** id

### device_condition
- **Table:** `ecoatm_pwsmdm$device_condition` | **Rows:** 23,105
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$conditionid | bigint | No | 116812115334935242, 116812115335152946, 116812115335101969 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$conditionid

### device_capacity
- **Table:** `ecoatm_pwsmdm$device_capacity` | **Rows:** 23,092
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$capacityid | bigint | No | 121878664915715579, 121878664915714725, 121878664915715686 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$capacityid

### device_carrier
- **Table:** `ecoatm_pwsmdm$device_carrier` | **Rows:** 23,086
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$carrierid | bigint | No | 118500965195187541, 118500965195186425, 118500965195199112 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$carrierid

### device_color
- **Table:** `ecoatm_pwsmdm$device_color` | **Rows:** 23,077
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$colorid | bigint | No | 117375065288359374, 117375065288346234, 117375065288350005 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$colorid

### device_category
- **Table:** `ecoatm_pwsmdm$device_category` | **Rows:** 23,013
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$categoryid | bigint | No | 121034239985582211, 121034239985582445, 121034239985582479 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$categoryid

### device_grade
- **Table:** `ecoatm_pwsmdm$device_grade` | **Rows:** 22,994
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$gradeid | bigint | No | 118219490218552451, 118219490218475913, 118219490218476109 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$gradeid

### device_brand
- **Table:** `ecoatm_pwsmdm$device_brand` | **Rows:** 22,991
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$brandid | bigint | No | 116530640358212024, 116530640358211917, 116530640358212184 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$brandid

### device_model
- **Table:** `ecoatm_pwsmdm$device_model` | **Rows:** 22,985
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865103381062 |
| ecoatm_pwsmdm$modelid | bigint | No | 119063915148723639, 119063915148724917, 119063915148724117 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$modelid

### device_note
- **Table:** `ecoatm_pwsmdm$device_note` | **Rows:** 20,532
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865104440761, 119626865103612895, 119626865102136669 |
| ecoatm_pwsmdm$noteid | bigint | No | 122723089852049632, 122723089851873419, 122723089852364387 |
- **PK:** ecoatm_pwsmdm$deviceid, ecoatm_pwsmdm$noteid

### note
- **Table:** `ecoatm_pwsmdm$note` | **Rows:** 20,532
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 122723089852049632, 122723089851873419, 122723089845933413 |
| notes | text | Yes |  |
| system$changedby | bigint | Yes | 23925373020815588, 23925373020828316, 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2026-01-13 16:02:36.695, 2025-06-24 16:05:34.562, 2025-06-24 16:05:17.923 |
| createddate | timestamp without time zone | Yes | 2026-01-13 16:02:36.695, 2025-05-27 22:02:20.316, 2025-05-01 18:47:38.505 |
| system$owner | bigint | Yes | 23925373020815588, 23925373020418781, 23925373020457621 |
- **PK:** id

### devicetemp
- **Table:** `ecoatm_pwsmdm$devicetemp` | **Rows:** 20,485
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 134545041434591451, 134545041433681189, 134545041434498870 |
| sku | character varying(200) | Yes | PWS511559757919, PWS10010764, PWS10010898 |
| devicecode | bigint | Yes | 19093807, 19751025, 19750108 |
| devicedescription | character varying(200) | Yes | Galaxy S23 Ultra 256GB (Unl..., Galaxy S22+ 5G 128GB (Veriz..., iPad Air 11-inch (M3) 512GB... |
| devicecarrier | character varying(200) | Yes | T-Mobile, WIFI, Unlocked |
| availableqty | integer | Yes | 365, 262, 89 |
| searchattr | character varying(500) | Yes | iPhone 14 256GB (T-Mobile)...., Galaxy Z Flip3 5G 128GB (Ve..., Galaxy Z Flip3 5G 256GB (Ve... |
| devicecolor | character varying(200) | Yes | Ultramarine, Red, Pacific Blue |
| devicecapacity | character varying(200) | Yes | 2TB, 1TB, 128GB |
| devicemodel | character varying(200) | Yes | Pixel 7a, Galaxy S25 Edge, iPhone SE (2nd generation) |
| devicecondition | character varying(200) | Yes | B, VALUE, GRADEB |
| devicecategory | character varying(200) | Yes | Cell Phone, Tablet |
| devicebrand | character varying(200) | Yes | Samsung, Google, Apple |
| devicegrade | character varying(200) | Yes | Value, B+, A |
| isnew | boolean | Yes | false |
- **PK:** id

### offerid
- **Table:** `ecoatm_pwsmdm$offerid` | **Rows:** 259
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 144115188075907380, 144115188076342422, 144115188076752300 |
| maxofferid | integer | Yes | 8, 12, 96 |
| system$changedby | bigint | Yes | 23925373021176012, 23925373022147278, 23925373022364361 |
| changeddate | timestamp without time zone | Yes | 2026-01-22 21:21:09.475, 2026-02-16 20:12:15.699, 2025-12-03 15:10:52.174 |
| createddate | timestamp without time zone | Yes | 2025-10-27 12:09:46.971, 2026-02-19 01:43:16.585, 2026-01-22 21:21:09.475 |
| system$owner | bigint | Yes | 23925373022147035, 23925373022147278, 23925373022364361 |
- **PK:** id

### offerid_buyercode
- **Table:** `ecoatm_pwsmdm$offerid_buyercode` | **Rows:** 252
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$offeridid | bigint | No | 144115188075907380, 144115188076752300, 144115188076342422 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299767879, 32651097299243972, 32651097299207268 |
- **PK:** ecoatm_pwsmdm$offeridid, ecoatm_buyermanagement$buyercodeid

### color
- **Table:** `ecoatm_pwsmdm$color` | **Rows:** 184
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 117375065288359374, 117375065288350005, 117375065288346234 |
| color | character varying(200) | Yes | Blue/silver, Ultramarine, Red |
| isenabledforfilter | boolean | Yes | true, false |
| system$changedby | bigint | Yes | 23925373020815588, 9851624184924352, 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2026-01-26 20:41:41.144, 2025-12-08 19:09:35.433, 2025-11-17 19:16:34.854 |
| createddate | timestamp without time zone | Yes | 2025-10-23 06:10:24.906, 2025-09-07 06:41:44.361, 2025-06-04 08:45:35.088 |
| system$owner | bigint | Yes | 9851624184924352 |
| rank | integer | Yes | 1070, 551, 780 |
| displayname | character varying(200) | Yes | Blue/silver, Ultramarine, Red |
- **PK:** id

### model
- **Table:** `ecoatm_pwsmdm$model` | **Rows:** 150
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 119063915148723639, 119063915148724117, 119063915148724917 |
| model | character varying(200) | Yes | Pixel 7a, Galaxy S25 Edge, iPhone SE (2nd generation) |
| system$changedby | bigint | Yes | 23925373020815588, 9851624184924352, 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2025-12-08 19:21:35.05, 2025-11-17 19:34:11.556, 2025-11-17 19:30:18.501 |
| createddate | timestamp without time zone | Yes | 2025-12-05 02:12:00.215, 2025-04-16 04:01:09.033, 2025-10-18 03:42:43.023 |
| system$owner | bigint | Yes | 9851624184924352, 23925373020418781 |
| rank | integer | Yes | 845, 8, 354 |
| isenabledforfilter | boolean | Yes | true, false |
| displayname | character varying(200) | Yes | Pixel 7a, Galaxy S25 Edge, iPhone SE (2nd generation) |
- **PK:** id

### caselot
- **Table:** `ecoatm_pwsmdm$caselot` | **Rows:** 35
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 172262685746922171, 172262685746922834, 172262685746923879 |
| caselotid | character varying(200) | Yes | SPB10000097-46, SPB10000100-48, SPB10000111-48 |
| caselotsize | integer | Yes | 47, 20, 23 |
| caselotprice | integer | Yes | 0 |
| caselotavlqty | integer | Yes | 12, 7, 5 |
| caselotreservedqty | integer | Yes | 0 |
| caselotatpqty | integer | Yes | 12, 7, 5 |
| createdby | character varying(200) | Yes | ashirwad.mittal@ecoatm.com |
| updatedby | character varying(200) | Yes | ashirwad.mittal@ecoatm.com |
| isactive | boolean | Yes | true |
| createddate | timestamp without time zone | Yes | 2026-02-18 21:19:11.162, 2026-02-18 21:19:11.117, 2026-02-18 21:19:11.136 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:11.311 |
| system$changedby | bigint | Yes | 23925373020815588 |
| system$owner | bigint | Yes | 23925373020815588 |
- **PK:** id

### caselot_device
- **Table:** `ecoatm_pwsmdm$caselot_device` | **Rows:** 35
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pwsmdm$caselotid | bigint | No | 172262685746922171, 172262685746922834, 172262685746923879 |
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865105377079, 119626865105376856, 119626865105274452 |
- **PK:** ecoatm_pwsmdm$caselotid, ecoatm_pwsmdm$deviceid

### condition
- **Table:** `ecoatm_pwsmdm$condition` | **Rows:** 30
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 116812115335152946, 116812115334935242, 116812115335127325 |
| condition | character varying(200) | Yes | iPhone 13 Pro 128gb Unlocke..., B, iPhone 14 Pro Max 256gb Unl... |
| system$changedby | bigint | Yes | 9851624184924352, 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2025-06-04 08:45:35.108, 2026-02-09 16:11:17.446, 2025-11-25 20:40:31.746 |
| createddate | timestamp without time zone | Yes | 2026-02-09 16:12:35.415, 2026-01-26 21:10:24.952, 2025-09-07 06:40:37.506 |
| system$owner | bigint | Yes | 9851624184924352 |
| rank | integer | Yes | 10, 40, 20 |
- **PK:** id

### capacity
- **Table:** `ecoatm_pwsmdm$capacity` | **Rows:** 10
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 121878664915715579, 121878664915714725, 121878664915715686 |
| capacity | character varying(200) | Yes | 16GB, 2TB, 1TB |
| isenabledforfilter | boolean | Yes | true, false |
| system$changedby | bigint | Yes | 23925373020815588, 9851624184924352 |
| changeddate | timestamp without time zone | Yes | 2025-12-08 15:06:35.375, 2025-10-28 20:57:18.023, 2026-02-19 20:04:49.235 |
| createddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| rank | integer | Yes | 10, 50, 60 |
| displayname | character varying(200) | Yes | 16GB, 2TB, 1TB |
- **PK:** id

### carrier
- **Table:** `ecoatm_pwsmdm$carrier` | **Rows:** 6
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 118500965195187541, 118500965195186425, 118500965195187727 |
| carrier | character varying(200) | Yes | T-Mobile, WIFI, Unlocked |
| isenabledforfilter | boolean | Yes | true |
| system$changedby | bigint | Yes | 9851624184924352, 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 16:48:41.185, 2025-12-23 23:19:45.043, 2026-02-19 20:04:49.233 |
| createddate | timestamp without time zone | Yes | 2025-11-21 21:14:03.125 |
| system$owner | bigint | Yes | 9851624184924352 |
| rank | integer | Yes | 30, 10, 40 |
| displayname | character varying(200) | Yes | T-Mobile, WIFI, Unlocked |
- **PK:** id

### grade
- **Table:** `ecoatm_pwsmdm$grade` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 118219490218475742, 118219490218475807, 118219490218475913 |
| grade | character varying(200) | Yes | B+, A, B |
| isenabledforfilter | boolean | Yes | true, false |
| system$changedby | bigint | Yes | 23925373020815588, 9851624184924352 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:23:06.974, 2026-02-19 20:04:49.246 |
| createddate | timestamp without time zone | Yes | 2025-12-01 22:40:32.943 |
| system$owner | bigint | Yes | 9851624184924352 |
| rank | integer | Yes | 10, 20, 30 |
| displayname | character varying(200) | Yes | YYY, Value, B+ |
- **PK:** id

### brand
- **Table:** `ecoatm_pwsmdm$brand` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 116530640358211917, 116530640358212024, 116530640358212184 |
| brand | character varying(200) | Yes | Samsung, Google, Apple |
| system$changedby | bigint | Yes | 9851624184924352 |
| changeddate | timestamp without time zone | Yes | 2026-02-19 20:04:49.231 |
| createddate | timestamp without time zone | Yes |  |
| isenabledforfilter | boolean | Yes | true |
| system$owner | bigint | Yes |  |
| rank | integer | Yes | 10, 20, 30 |
| displayname | character varying(200) | Yes | Samsung, Google, Apple |
- **PK:** id

### category
- **Table:** `ecoatm_pwsmdm$category` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 121034239985582211, 121034239985582445, 121034239985582479 |
| category | character varying(200) | Yes | Tablet, CellPhone,  |
| system$changedby | bigint | Yes | 23925373020815588, 9851624184924352 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 20:57:18.108, 2026-02-19 20:04:49.228 |
| createddate | timestamp without time zone | Yes |  |
| isenabledforfilter | boolean | Yes | true, false |
| system$owner | bigint | Yes |  |
| rank | integer | Yes | 10, 100, 20 |
| displayname | character varying(200) | Yes | Tablet, Cell Phone,  |
- **PK:** id

### devicesftemp
- **Table:** `ecoatm_pwsmdm$devicesftemp` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| sku | character varying(200) | Yes |  |
| system$changedby | bigint | Yes |  |
| reservedqty | integer | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| atpqty | integer | Yes |  |
| avlqty | integer | Yes |  |
- **PK:** id

---
