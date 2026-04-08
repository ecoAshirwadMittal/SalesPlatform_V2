## ecoatm_mdm

### masterdeviceinventory
- **Table:** `ecoatm_mdm$masterdeviceinventory` | **Rows:** 44,008
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 95701492084263788, 95701492082386592, 95701492082218002 |
| ecoatm_code | bigint | Yes | 2571, 34884, 32514 |
| device_id | character varying(100) | Yes | 924bfe95-0d6a-45b3-96d6-42d..., 060a05dd-5d98-4fb2-bb65-d1d..., 92d5198e-03fc-4e46-86f3-b15... |
| name | character varying(512) | Yes | Aspire 5102WLMi-MCE, Versa P/75HC, MUHAMMAD ALI BOXING [E] |
| device_brand | character varying(255) | Yes | Olympus, SanDisk, Sony Ericsson |
| device_category | character varying(255) | Yes | Ink and Toner, Storage, Tablet |
| device_family | character varying(255) | Yes | iPad 3, Galaxy Z, Moto G Pure |
| device_carrier | character varying(255) | Yes | US Cellular, X, Orange |
| carrier_display_name | character varying(255) | Yes | Verizon, Sprint/Other, AT&T |
| category_display_name | character varying(100) | Yes | Ink and Toner, Storage, Tablet |
| device_model | character varying(30) | Yes | Armor, Celero3 5G+, iPad 3 |
| description | character varying(240) | Yes | Samsung.Galaxy A50 128GB Ot..., Nokia.7.1.DEFAULT.TA-1096.D..., Philips.Other.X.9@9T.X |
| release_date | timestamp without time zone | Yes | 2015-05-15 07:00:00, 2014-02-21 08:00:00, 2019-08-20 07:00:00 |
| db_create_date | timestamp without time zone | Yes | 2025-04-02 07:00:00, 2025-03-04 08:00:00, 2025-04-11 07:00:00 |
| db_update_date | timestamp without time zone | Yes | 2025-04-02 07:00:00, 2025-03-04 08:00:00, 2025-04-11 07:00:00 |
| created_at | timestamp without time zone | Yes | 2015-11-10 08:00:00, 2012-09-19 07:00:00, 2012-05-02 07:00:00 |
- **PK:** id

### modelname
- **Table:** `ecoatm_mdm$modelname` | **Rows:** 9,270
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 174514485562653452, 174514485560650215, 174514485562254144 |
| modelname | character varying(200) | Yes | Y7 2019 DUB-LX1, RAZR MIAMI INK ED V3-MIE, Treasure L51AL/L52VL |
| displayname | character varying(200) | Yes | Y7 2019 DUB-LX1, RAZR MIAMI INK ED V3-MIE, Treasure L51AL/L52VL |
| isenabledforfilter | boolean | Yes | true, false |
| rank | integer | Yes | 0 |
| buyercodetype | character varying(26) | Yes | Wholesale, Data_Wipe |
- **PK:** id

### model
- **Table:** `ecoatm_mdm$model` | **Rows:** 5,375
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 170855310863513928, 170855310863467793, 170855310864777229 |
| model | character varying(200) | Yes | Celero3 5G+, iPad 3, A9L |
| displayname | character varying(200) | Yes | Celero3 5G+, iPad 3, A9L |
| isenabledforfilter | boolean | Yes | true, false |
| rank | integer | Yes | 0 |
| buyercodetype | character varying(26) | Yes | Wholesale, Data_Wipe |
- **PK:** id

### brand
- **Table:** `ecoatm_mdm$brand` | **Rows:** 203
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 173951535607236827, 173951535607195362, 173951535607198448 |
| brand | character varying(200) | Yes | Sony Ericsson, Unnecto, SanDisk |
| displayname | character varying(200) | Yes | Sony Ericsson, Unnecto, SanDisk |
| isenabledforfilter | boolean | Yes | true, false |
| rank | integer | Yes | 0 |
| buyercodetype | character varying(26) | Yes | Wholesale, Data_Wipe |
- **PK:** id

### week
- **Table:** `ecoatm_mdm$week` | **Rows:** 157
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 69805794224290239, 69805794224292664, 69805794224270652 |
| weekid | bigint | Yes | 90, 210, 198 |
| year | integer | Yes | 2026, 2025, 2024 |
| weeknumber | integer | Yes | 43, 16, 42 |
| weekstartdatetime | timestamp without time zone | Yes | 2025-02-27 05:00:00, 2026-06-18 04:00:00, 2026-02-19 05:00:00 |
| weekenddatetime | timestamp without time zone | Yes | 2025-08-07 03:59:59.999, 2026-10-29 03:59:59.999, 2024-08-01 03:59:59.999 |
| weekdisplay | character varying(200) | Yes | 2024 / Wk01, 2024 / Wk02, 2024 / Wk03 |
| weekdisplayshort | character varying(200) | Yes | Wk42, Wk32, Wk24 |
| weeknumberstring | character varying(200) | Yes | 21, 24, 17 |
| createddate | timestamp without time zone | Yes | 2024-05-21 19:22:01.991, 2024-05-21 19:22:02.012, 2024-12-27 21:18:59.872 |
| changeddate | timestamp without time zone | Yes | 2026-01-15 02:09:17.754, 2026-01-15 02:20:26.257, 2026-01-15 02:20:31.711 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020815588, 23925373020444341 |
| system$owner | bigint | Yes | 281474976710785, 23925373020418937 |
| auctiondatapurged | boolean | Yes | true, false |
- **PK:** id

### carrier
- **Table:** `ecoatm_mdm$carrier` | **Rows:** 42
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 175077435514041347, 175077435514029573, 175077435514053824 |
| carrier | character varying(200) | Yes | X, US Cellular, T-Mobile |
| displayname | character varying(200) | Yes | X, US Cellular, T-Mobile |
| isenabledforfilter | boolean | Yes | true, false |
| rank | integer | Yes | 0 |
| buyercodetype | character varying(26) | Yes | Wholesale, Data_Wipe |
- **PK:** id

### companyholiday
- **Table:** `ecoatm_mdm$companyholiday` | **Rows:** 7
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 142144863238881700, 142144863238907010, 142144863238907158 |
| holidaydate | timestamp without time zone | Yes | 2025-07-04 00:00:00, 2025-06-19 00:00:00, 2025-01-01 00:00:00 |
| holidaydescription | character varying(200) | Yes | Thanksgiving, 4th of July, Memorial Day |
- **PK:** id

### userhelperguide
- **Table:** `ecoatm_mdm$userhelperguide` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 94857067151504023, 94857067151516856, 94857067151529693 |
| active | boolean | Yes | true, false |
| version | character varying(200) | Yes | V6, V2, V5 |
| guidetype | character varying(20) | Yes | PWS_Grading_Details, Sales_Platform_Terms, Auctions |
- **PK:** id

---
