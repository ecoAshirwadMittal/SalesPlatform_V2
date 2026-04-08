## ecoatm_rma

### rmaitem
- **Table:** `ecoatm_rma$rmaitem` | **Rows:** 3,797
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 157063037005242772, 157063037005386272, 157063037004810946 |
| imei | text | Yes |  |
| shipdate | timestamp without time zone | Yes | 2025-10-28 21:07:44, 2025-10-17 20:20:33, 2025-11-19 21:56:36 |
| returnreason | text | Yes |  |
| status | character varying(7) | Yes | Approve, Decline |
| ordernumber | character varying(200) | Yes | 5003449, 5004049, 5004275 |
| saleprice | integer | Yes | 173, 395, 383 |
| declinereason | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2026-01-13 16:10:35.544, 2025-11-20 18:59:27.939, 2025-12-18 12:55:50.373 |
| changeddate | timestamp without time zone | Yes | 2026-01-06 12:33:27.119, 2026-01-09 16:56:30.532, 2026-02-16 14:24:28.554 |
| system$changedby | bigint | Yes | 23925373020444502, 23925373021775668, 23925373020777255 |
| system$owner | bigint | Yes | 23925373021545825, 23925373021775668, 23925373021188101 |
| entityowner | character varying(200) | Yes | maria.ruvalcaba@ecoatm.com, ventasusa@celucambio.com, corbalinc26@gmail.com |
| statusdisplay | character varying(200) | Yes | Approved, Approve, Declined |
| entitychanger | character varying(200) | Yes | maria.ruvalcaba@ecoatm.com, corbalinc26@gmail.com, nicholas.prodzenko@ecoatm.com |
- **PK:** id

### rmaitem_device
- **Table:** `ecoatm_rma$rmaitem_device` | **Rows:** 3,797
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaitemid | bigint | No | 157063037005242772, 157063037005386272, 157063037004810946 |
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865103349473, 119626865103099172, 119626865103409980 |
- **PK:** ecoatm_rma$rmaitemid, ecoatm_pwsmdm$deviceid

### rmaitem_order
- **Table:** `ecoatm_rma$rmaitem_order` | **Rows:** 3,797
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaitemid | bigint | No | 157063037005242772, 157063037005386272, 157063037004810946 |
| ecoatm_pws$orderid | bigint | No | 129197014311654117, 129197014311766380, 129197014311641920 |
- **PK:** ecoatm_rma$rmaitemid, ecoatm_pws$orderid

### rmaitem_rma
- **Table:** `ecoatm_rma$rmaitem_rma` | **Rows:** 3,797
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaitemid | bigint | No | 157063037005242772, 157063037005386272, 157063037004810946 |
| ecoatm_rma$rmaid | bigint | No | 157344511981820813, 157344511981872307, 157344511981832843 |
- **PK:** ecoatm_rma$rmaitemid, ecoatm_rma$rmaid

### invalidimei_exporthelper
- **Table:** `ecoatm_rma$invalidimei_exporthelper` | **Rows:** 1,318
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 165507286306150826, 165507286305885037, 165507286305886815 |
| imei | character varying(200) | Yes | 357397705507688, 350419535427544, 357879430855003 |
| reason | character varying(200) | Yes | Invalid IMEI, Duplicate IMEI, Invalid Reason |
- **PK:** id

### rmafile
- **Table:** `ecoatm_rma$rmafile` | **Rows:** 526
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 164381386399114219, 164381386399138746, 164381386399053339 |
| isvalid | boolean | Yes | true, false |
| invalidreason | text | Yes |  |
- **PK:** id

### rmareturnlabel
- **Table:** `ecoatm_rma$rmareturnlabel` | **Rows:** 295
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 163536961468968143, 163536961469186323, 163536961468918065 |
- **PK:** id

### rma
- **Table:** `ecoatm_rma$rma` | **Rows:** 286
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 157344511981820813, 157344511981872307, 157344511981832843 |
| number | text | Yes |  |
| requestskus | integer | Yes | 1, 9, 4 |
| requestqty | integer | Yes | 8, 75, 12 |
| requestsalestotal | integer | Yes | 265, 3963, 5410 |
| approvaldate | timestamp without time zone | Yes | 2025-12-24 17:12:56.714, 2026-02-07 02:39:57.607, 2025-11-01 02:02:49.641 |
| approvedskus | integer | Yes | 1, 9, 4 |
| approvedqty | integer | Yes | 8, 75, 12 |
| approvedsalestotal | integer | Yes | 3963, 5410, 330 |
| submitteddate | timestamp without time zone | Yes | 2026-01-05 22:31:25.407, 2025-10-31 15:40:01.023, 2025-12-19 22:30:47.257 |
| reviewcompletedon | timestamp without time zone | Yes |  |
| createddate | timestamp without time zone | Yes | 2026-02-04 22:04:58.46, 2026-02-10 14:54:21.334, 2025-12-08 16:28:02.134 |
| changeddate | timestamp without time zone | Yes | 2026-02-06 16:00:07.702, 2026-02-18 00:20:02.932, 2026-02-05 13:00:08.328 |
| system$changedby | bigint | Yes | 23925373020444502, 23925373020957334, 23925373021775668 |
| system$owner | bigint | Yes | 23925373021545825, 23925373021176012, 23925373021188101 |
| allrmaitemsvalid | boolean | Yes | true, false |
| oraclermastatus | text | Yes |  |
| oraclenumber | character varying(200) | Yes | 4500158, 4500038, 4500166 |
| oracleid | character varying(200) | Yes | 70256013, 70990006, 70110001 |
| systemstatus | character varying(200) | Yes | Approved, Partial Receipt, New |
| issuccessful | boolean | Yes | true, false |
| oraclehttpcode | integer | Yes | 504, 201, 400 |
| entityowner | character varying(200) | Yes | maria.ruvalcaba@ecoatm.com, ventasusa@celucambio.com, corbalinc26@gmail.com |
| declinedcount | integer | Yes | 3, 10, 2 |
| jsoncontent | text | Yes |  |
| oraclejsonresponse | text | Yes |  |
| entitychanger | character varying(200) | Yes | maria.ruvalcaba@ecoatm.com, Deposco, corbalinc26@gmail.com |
| approvedcount | integer | Yes | 8, 75, 12 |
- **PK:** id

### rma_buyercode
- **Table:** `ecoatm_rma$rma_buyercode` | **Rows:** 286
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaid | bigint | No | 157344511981820813, 157344511981872307, 157344511981832843 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299652416, 32651097299204995, 32651097299896932 |
- **PK:** ecoatm_rma$rmaid, ecoatm_buyermanagement$buyercodeid

### rma_ecoatmdirectuser_submittedby
- **Table:** `ecoatm_rma$rma_ecoatmdirectuser_submittedby` | **Rows:** 286
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaid | bigint | No | 157344511981820813, 157344511981872307, 157344511981832843 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373021545825, 23925373021176012, 23925373021188101 |
- **PK:** ecoatm_rma$rmaid, ecoatm_usermanagement$ecoatmdirectuserid

### rma_rmastatus
- **Table:** `ecoatm_rma$rma_rmastatus` | **Rows:** 278
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaid | bigint | No | 157344511981820813, 157344511981872307, 157344511981832843 |
| ecoatm_rma$rmastatusid | bigint | No | 158751886864810567, 158751886864836082, 158751886864822939 |
- **PK:** ecoatm_rma$rmaid, ecoatm_rma$rmastatusid

### rmafile_rma
- **Table:** `ecoatm_rma$rmafile_rma` | **Rows:** 271
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmafileid | bigint | No | 164381386399138746, 164381386399040212, 164381386399320915 |
| ecoatm_rma$rmaid | bigint | No | 157344511981872307, 157344511981832843, 157344511981386154 |
- **PK:** ecoatm_rma$rmafileid, ecoatm_rma$rmaid

### rma_ecoatmdirectuser_reviewedby
- **Table:** `ecoatm_rma$rma_ecoatmdirectuser_reviewedby` | **Rows:** 263
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaid | bigint | No | 157344511981872307, 157344511981832843, 157344511981386154 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373020739037, 23925373020444502, 23925373020457621 |
- **PK:** ecoatm_rma$rmaid, ecoatm_usermanagement$ecoatmdirectuserid

### rmaid
- **Table:** `ecoatm_rma$rmaid` | **Rows:** 49
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 166633186212785290, 166633186212964701, 166633186212977353 |
| maxrmaid | integer | Yes | 7, 13, 15 |
| createddate | timestamp without time zone | Yes | 2025-11-10 22:04:14.744, 2025-10-29 14:52:54.144, 2026-01-13 16:10:39.792 |
| changeddate | timestamp without time zone | Yes | 2025-12-22 17:01:57.074, 2026-02-09 17:03:18.288, 2025-11-06 17:59:08.781 |
| system$owner | bigint | Yes | 23925373021545825, 23925373021775668, 23925373021188101 |
| system$changedby | bigint | Yes | 23925373021545825, 23925373021775668, 23925373021188101 |
- **PK:** id

### rmaid_buyercode
- **Table:** `ecoatm_rma$rmaid_buyercode` | **Rows:** 49
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaidid | bigint | No | 166633186212785290, 166633186212964701, 166633186212977353 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299204995, 32651097299230941, 32651097299729092 |
- **PK:** ecoatm_rma$rmaidid, ecoatm_buyermanagement$buyercodeid

### rmareasons
- **Table:** `ecoatm_rma$rmareasons` | **Rows:** 16
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 167759086119563964, 167759086119564513, 167759086119551150 |
| validreasons | character varying(200) | Yes | Defective Battery/ Lower 69%, Gaps/ Parts Fit/ Screen Lif..., Defective Charger/ Data Port |
| isactive | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2025-10-28 20:48:06.07, 2025-10-28 20:46:09.269, 2025-10-28 20:48:15.512 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 20:46:33.508, 2025-10-28 20:46:24.566, 2025-10-28 20:45:58.597 |
| system$owner | bigint | Yes | 23925373020880413 |
| system$changedby | bigint | Yes | 23925373020880413 |
- **PK:** id

### rmastatus
- **Table:** `ecoatm_rma$rmastatus` | **Rows:** 9
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 158751886864836082, 158751886864810567, 158751886864823116 |
| systemstatus | text | Yes |  |
| internalstatushexcode | character varying(33) | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-10-28 20:55:25.056, 2025-10-28 20:55:07.152, 2025-10-28 20:54:25.263 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 20:55:17.96, 2025-10-28 20:53:27.209, 2025-10-28 20:54:24.202 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588, 23925373020431548 |
| statusgroupedto | character varying(16) | Yes | Open, Closed, Declined |
| internalstatustext | character varying(200) | Yes | Canceled, Declined, Received |
| statusverbiagebidder | text | Yes |  |
| desciption | text | Yes |  |
| salesstatusheaderhexcode | character varying(33) | Yes |  |
| salestablehoverhexcode | character varying(33) | Yes |  |
| sortorder | integer | Yes |  |
| externalstatustext | character varying(200) | Yes | Canceled, Submitted, Declined |
| externalstatushexcode | character varying(33) | Yes |  |
| isdefaultstatus | boolean | Yes | true, false |
- **PK:** id

### rmatemplate
- **Table:** `ecoatm_rma$rmatemplate` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 151996487423754480 |
| templatename | character varying(200) | Yes | RMA Request Template - Sept... |
| isactive | boolean | Yes | true |
- **PK:** id

### invalidimei_exporthelper_invalidrmafileexport
- **Table:** `ecoatm_rma$invalidimei_exporthelper_invalidrmafileexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$invalidimei_exporthelperid | bigint | No |  |
| ecoatm_rma$invalidrmafileexportid | bigint | No |  |
- **PK:** ecoatm_rma$invalidimei_exporthelperid, ecoatm_rma$invalidrmafileexportid

### invalidrmafileexport
- **Table:** `ecoatm_rma$invalidrmafileexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### rma_rmaexceldocument
- **Table:** `ecoatm_rma$rma_rmaexceldocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaid | bigint | No |  |
| ecoatm_rma$rmaexceldocumentid | bigint | No |  |
- **PK:** ecoatm_rma$rmaid, ecoatm_rma$rmaexceldocumentid

### rmadetailsexport
- **Table:** `ecoatm_rma$rmadetailsexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### rmaexceldocument
- **Table:** `ecoatm_rma$rmaexceldocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### rmaitem_rmadetailsexport
- **Table:** `ecoatm_rma$rmaitem_rmadetailsexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_rma$rmaitemid | bigint | No |  |
| ecoatm_rma$rmadetailsexportid | bigint | No |  |
- **PK:** ecoatm_rma$rmaitemid, ecoatm_rma$rmadetailsexportid

---
