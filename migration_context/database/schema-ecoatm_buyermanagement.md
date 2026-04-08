## ecoatm_buyermanagement

### qualifiedbuyercodes
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodes` | **Rows:** 23,736
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 165788761300201077, 165788761292982258, 165788761290467371 |
| qualificationtype | character varying(13) | Yes | Qualified, Not_Qualified, Manual |
| included | boolean | Yes | true, false |
| submitted | boolean | Yes | false |
| submitteddatetime | timestamp without time zone | Yes |  |
| openeddashboard | boolean | Yes | true, false |
| openeddashboarddatetime | timestamp without time zone | Yes | 2025-12-16 12:30:23.61, 2026-01-01 03:28:28.154, 2025-12-05 12:07:47.112 |
| isspecialtreatment | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2026-02-09 22:38:37.958, 2026-01-20 18:41:47.306, 2025-12-01 17:37:34.386 |
| changeddate | timestamp without time zone | Yes | 2025-10-31 02:25:20.506, 2026-01-27 11:14:47.372, 2025-12-09 10:22:32.712 |
| system$changedby | bigint | Yes | 23925373021660346, 23925373021007967, 23925373022646096 |
| system$owner | bigint | Yes | 23925373020815588, 23925373020777255, 23925373020418781 |
- **PK:** id

### qualifiedbuyercodes_buyercode
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodes_buyercode` | **Rows:** 23,670
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesid | bigint | No | 165788761300201077, 165788761292982258, 165788761290467371 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097298718421, 32651097299106220, 32651097299117222 |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesid, ecoatm_buyermanagement$buyercodeid

### qualifiedbuyercodes_schedulingauction
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction` | **Rows:** 9,299
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesid | bigint | No | 165788761300007206, 165788761300248405, 165788761301196614 |
| auctionui$schedulingauctionid | bigint | No | 71776119062305759, 71776119062292616, 71776119062343965 |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesid, auctionui$schedulingauctionid

### qualifiedbuyercodes_bidround
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodes_bidround` | **Rows:** 1,477
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesid | bigint | No | 165788761300104834, 165788761293138968, 165788761293141004 |
| auctionui$bidroundid | bigint | No | 69242844275252217, 69242844275022056, 69242844275119168 |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesid, auctionui$bidroundid

### buyercode
- **Table:** `ecoatm_buyermanagement$buyercode` | **Rows:** 1,383
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 32651097299051199, 32651097298936410, 32651097299795471 |
| submissionid | bigint | Yes | 1487, 791, 71 |
| code | character varying(200) | Yes | 20399, 20580, 20581 |
| buyercodetype | character varying(26) | Yes | Wholesale, Data_Wipe, Purchasing_Order_Data_Wipe |
| status | character varying(8) | Yes | Active |
| createddate | timestamp without time zone | Yes | 2026-01-16 20:12:00.63, 2026-01-16 20:13:55.969, 2025-11-05 22:31:51.649 |
| changeddate | timestamp without time zone | Yes | 2025-11-03 21:15:48.744, 2026-01-13 13:57:02.624, 2025-07-03 10:28:36.149 |
| system$owner | bigint | Yes | 23925373020713794, 23925373020444341, 23925373020444502 |
| system$changedby | bigint | Yes | 23925373022364361, 23925373021878723, 23925373021314975 |
| typevalid | boolean | Yes | true |
| codeemptyvalid | boolean | Yes | true |
| entityowner | character varying(200) | Yes | kevin.kim@ecoatm.com, maria.ruvalcaba@ecoatm.com, melvin.laguren@ecoatm.com |
| softdelete | boolean | Yes | false |
| codeuniquevalid | boolean | Yes | true |
| budget | integer | Yes | 120000, 300000, 600000 |
| entitychanger | character varying(200) | Yes | kevin.kim@ecoatm.com, maria.ruvalcaba@ecoatm.com, naresh.agarwal@ecoatm.com |
| showsubmitofferbtn | boolean | Yes | true, false |
- **PK:** id

### buyercode_buyer
- **Table:** `ecoatm_buyermanagement$buyercode_buyer` | **Rows:** 1,383
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299051199, 32651097299795471, 32651097298936410 |
| ecoatm_buyermanagement$buyerid | bigint | No | 43628621391406553, 43628621392020918, 43628621391367842 |
- **PK:** ecoatm_buyermanagement$buyercodeid, ecoatm_buyermanagement$buyerid

### buyer
- **Table:** `ecoatm_buyermanagement$buyer` | **Rows:** 876
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 43628621391112002, 43628621390523817, 43628621390282418 |
| submissionid | bigint | Yes | 1006, 249, 316 |
| companyname | character varying(200) | Yes | World Trade Solutions, Nossik LLC, Luckys Trade Company Limited |
| status | character varying(8) | Yes | Disabled, Active |
| buyercodesdisplay | character varying(1000) | Yes | DW348, 30301, 26985, 26339 |
| createddate | timestamp without time zone | Yes | 2025-08-18 14:55:21.82, 2025-03-21 19:40:26.351, 2025-09-16 14:12:16.743 |
| changeddate | timestamp without time zone | Yes | 2025-07-17 15:55:50.464, 2025-07-15 21:25:20.552, 2025-11-03 21:23:54.699 |
| system$owner | bigint | Yes | 23925373020713794, 281474976710785, 23925373020444502 |
| system$changedby | bigint | Yes | 23925373020713794, 23925373020444341, 23925373021391862 |
| isfailedbuyerdisable | boolean | Yes | true, false |
| buyercodeinvalidmessage_empty | character varying(200) | Yes |  |
| buyercodesemptyvalidationmessage | character varying(200) | Yes |  |
| isspecialbuyer | boolean | Yes | true, false |
| buyeruniquevalidationmessage | character varying(200) | Yes |  |
| buyercodetypeinvalidmessage | character varying(200) | Yes |  |
| entityowner | character varying(200) | Yes | maria.ruvalcaba@ecoatm.com, melvin.laguren@ecoatm.com, nathan.mount@ecoatm.com |
| buyercodeinvalidmessage_unique | character varying(200) | Yes |  |
| buyeremptyvalidationmessage | character varying(200) | Yes |  |
| entitychanger | character varying(200) | Yes | kevin.kim@ecoatm.com, maria.ruvalcaba@ecoatm.com, naresh.agarwal@ecoatm.com |
- **PK:** id

### buyer_salesrepresentative
- **Table:** `ecoatm_buyermanagement$buyer_salesrepresentative` | **Rows:** 655
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyerid | bigint | No | 43628621391662818, 43628621390971400, 43628621390689594 |
| ecoatm_buyermanagement$salesrepresentativeid | bigint | No | 142426338215643346, 142426338215592129, 142426338215656106 |
- **PK:** ecoatm_buyermanagement$buyerid, ecoatm_buyermanagement$salesrepresentativeid

### qualifiedbuyercodesqueryhelper
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper` | **Rows:** 52
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 166351711236343459, 166351711236343683, 166351711236253985 |
- **PK:** id

### buyercodechangelog
- **Table:** `ecoatm_buyermanagement$buyercodechangelog` | **Rows:** 31
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 150589112540201240, 150589112540215519, 150589112540201515 |
| oldbuyercodetype | character varying(200) | Yes | Purchasing_Order |
| newbuyercodetype | character varying(200) | Yes | Purchasing_Order_Data_Wipe |
| createddate | timestamp without time zone | Yes | 2025-07-22 21:01:33.277, 2025-07-22 21:07:36.398, 2025-07-22 21:01:33.293 |
| changeddate | timestamp without time zone | Yes | 2025-07-22 21:01:33.277, 2025-07-22 20:52:42.481, 2025-07-22 20:52:42.457 |
| system$owner | bigint | Yes | 23925373021711853 |
| system$changedby | bigint | Yes | 23925373021711853 |
| editedon | timestamp without time zone | Yes |  |
| editedby | character varying(200) | Yes |  |
- **PK:** id

### buyercodechangelog_buyercode
- **Table:** `ecoatm_buyermanagement$buyercodechangelog_buyercode` | **Rows:** 31
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercodechangelogid | bigint | No | 150589112540201240, 150589112540215519, 150589112540201515 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299132953, 32651097299145274, 32651097298936410 |
- **PK:** ecoatm_buyermanagement$buyercodechangelogid, ecoatm_buyermanagement$buyercodeid

### salesrepresentative
- **Table:** `ecoatm_buyermanagement$salesrepresentative` | **Rows:** 6
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 142426338215643346, 142426338215592129, 142426338215656106 |
| salesrepfirstname | character varying(200) | Yes | Julie, Nick, Brian |
| salesreplastname | character varying(200) | Yes | Tepfer, Mount, Cherner |
| active | boolean | Yes | true |
| createddate | timestamp without time zone | Yes | 2025-05-07 20:27:59.215, 2025-05-07 20:57:06.466, 2025-05-07 22:45:10.842 |
| changeddate | timestamp without time zone | Yes | 2025-05-07 22:45:20.34, 2025-05-29 16:25:44.625, 2025-08-18 20:49:14.771 |
| system$owner | bigint | Yes | 23925373020418781 |
| system$changedby | bigint | Yes | 23925373020777100, 23925373020418781 |
| salesrepresentativeid | bigint | Yes | 2, 1, 4 |
- **PK:** id

### qualifiedbuyercodesqueryhelper_auction
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_auction` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid | bigint | No | 166351711236305235, 166351711236330855, 166351711236369128 |
| auctionui$auctionid | bigint | No | 54606145483006691, 54606145483019401, 54606145483032299 |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid, auctionui$auctionid

### auctionsfeature
- **Table:** `ecoatm_buyermanagement$auctionsfeature` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 117656540265054362 |
| createddate | timestamp without time zone | Yes | 2025-01-30 21:53:20.655 |
| changeddate | timestamp without time zone | Yes | 2026-01-07 22:01:51.653 |
| system$owner | bigint | Yes | 23925373020431548 |
| system$changedby | bigint | Yes | 23925373020547362 |
| auctionround2minutesoffset | integer | Yes | 360 |
| sendbuyertosnowflake | boolean | Yes | true |
| auctionround3minutesoffset | integer | Yes | 180 |
| sendbiddatatosnowflake | boolean | Yes | true |
| createexcelbidexport | boolean | Yes | true |
| sendauctiondatatosnowflake | boolean | Yes | true |
| generateround3files | boolean | Yes | true |
| calculateround2buyerparticipation | boolean | Yes | true |
| spretrycount | integer | Yes | 3 |
| sendfilestosharepointonsubmit | boolean | Yes | true |
| round2criteriaactive | boolean | Yes | false |
| minimumallowedbid | numeric | Yes | 2.00000000 |
| legacyauctiondashboardactive | boolean | Yes | false |
| requirewholesaleuseragreement | boolean | Yes | false |
| sendbidrankingtosnowflake | boolean | Yes | false |
- **PK:** id

### buyercode_sessionandtabhelper
- **Table:** `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 177329235328628927 |
| tabid | character varying(200) | Yes | 1771070464265-f8c9745a7c1d4 |
| createddate | timestamp without time zone | Yes | 2026-02-13 23:02:57.676 |
| changeddate | timestamp without time zone | Yes | 2026-02-13 23:02:57.676 |
| system$owner | bigint | Yes | 23925373022173026 |
| system$changedby | bigint | Yes | 23925373022173026 |
- **PK:** id

### buyercode_sessionandtabhelper_session
- **Table:** `ecoatm_buyermanagement$buyercode_sessionandtabhelper_session` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercode_sessionandtabhelperid | bigint | No | 177329235328628927 |
| system$sessionid | bigint | No | 6755399484233034 |
- **PK:** ecoatm_buyermanagement$buyercode_sessionandtabhelperid, system$sessionid

### buyer_round3nosalesrephelper
- **Table:** `ecoatm_buyermanagement$buyer_round3nosalesrephelper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyerid | bigint | No |  |
| ecoatm_buyermanagement$round3nosalesrephelperid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$buyerid, ecoatm_buyermanagement$round3nosalesrephelperid

### buyercode_sessionandtabhelper_buyercode
- **Table:** `ecoatm_buyermanagement$buyercode_sessionandtabhelper_buyercode` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercode_sessionandtabhelperid | bigint | No |  |
| ecoatm_buyermanagement$buyercodeid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$buyercode_sessionandtabhelperid, ecoatm_buyermanagement$buyercodeid

### buyercodeselect_helper
- **Table:** `ecoatm_buyermanagement$buyercodeselect_helper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| companyname | character varying(200) | Yes |  |
| code | character varying(200) | Yes |  |
| isupsellround | boolean | Yes |  |
| note | character varying(2000) | Yes |  |
- **PK:** id

### buyercodeselect_helper_bidround
- **Table:** `ecoatm_buyermanagement$buyercodeselect_helper_bidround` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercodeselect_helperid | bigint | No |  |
| auctionui$bidroundid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$buyercodeselect_helperid, auctionui$bidroundid

### buyercodeselect_helper_buyercode
- **Table:** `ecoatm_buyermanagement$buyercodeselect_helper_buyercode` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercodeselect_helperid | bigint | No |  |
| ecoatm_buyermanagement$buyercodeid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$buyercodeselect_helperid, ecoatm_buyermanagement$buyercodeid

### buyercodeselect_helper_schedulingauction
- **Table:** `ecoatm_buyermanagement$buyercodeselect_helper_schedulingauction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$buyercodeselect_helperid | bigint | No |  |
| auctionui$schedulingauctionid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$buyercodeselect_helperid, auctionui$schedulingauctionid

### qualifiedbuyercodes_submittedby
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodes_submittedby` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesid, system$userid

### qualifiedbuyercodesqueryhelper_session
- **Table:** `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid, system$sessionid

### round3nosalesrephelper
- **Table:** `ecoatm_buyermanagement$round3nosalesrephelper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

---
