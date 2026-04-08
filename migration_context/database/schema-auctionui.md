## auctionui

### biddata
- **Table:** `auctionui$biddata` | **Rows:** 11,619,882
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 55450586199466572, 55450586199466698, 55450586199466807 |
| biddataid | bigint | Yes | 123203501, 123203502, 123203503 |
| ecoid | integer | Yes | 10002, 10004, 10006 |
| bidquantity | integer | Yes | -1, 0, 1 |
| bidamount | numeric | Yes | -305.00000000, 0.00000000, 0.01000000 |
| targetprice | numeric | Yes | 0.41000000, 0.68000000, 0.96000000 |
| merged_grade | character varying(200) | Yes | A_YYY, B_NYY/D_NNY, C_YNY/G_YNN |
| changeddate | timestamp without time zone | Yes | 2026-01-16 02:39:24.154, 2026-01-16 02:39:24.155, 2026-01-16 02:39:24.156 |
| system$owner | bigint | Yes | 23925373020418781, 23925373020444341, 23925373020444954 |
| system$changedby | bigint | Yes | 23925373020418781, 23925373020444341, 23925373020444954 |
| createddate | timestamp without time zone | Yes | 2026-01-16 02:39:23.096, 2026-01-16 02:39:23.097, 2026-01-16 02:39:23.098 |
| buyercodetype | character varying(200) | Yes | Data_Wipe |
| payout | numeric | Yes | 0.00000000 |
| user | character varying(200) | Yes |  |
| code | character varying(200) | Yes | AAAA1, AD, BE |
| previousroundbidquantity | integer | Yes | -1, 0, 1 |
| previousroundbidamount | numeric | Yes | 0.00000000, 0.01643655, 0.02000000 |
| submitdatetime | timestamp without time zone | Yes |  |
| maximumquantity | integer | Yes | 1, 10, 100 |
| highestbid | boolean | Yes | false, true |
| bidround | integer | Yes | 1, 2, 3 |
| companyname | character varying(200) | Yes | AaSie Industries DBA RYC El..., Aden Group, AGL General Trading LLC-FZ |
| margin | numeric | Yes | 0.00000000 |
| weekid | integer | Yes | 0 |
| submitteddatetime | timestamp without time zone | Yes | 2026-01-16 05:19:51.144, 2026-01-16 05:19:51.145, 2026-01-16 16:45:17.751 |
| submittedbidamount | numeric | Yes | 0.00000000, 0.01000000, 0.01643655 |
| submittedbidquantity | integer | Yes | -1, 0, 1 |
| tempdabidamount | numeric | Yes | 0.00000000 |
| rejected | boolean | Yes | false |
| ischanged | boolean | Yes | false |
| rejectreason | text | Yes |  |
| acceptreason | character varying(200) | Yes |  |
| mergedgrade | text | Yes |  |
| isdeprecated | boolean | Yes | false |
| data_wipe_quantity | integer | Yes | 0, 1, 10 |
| lastvalidbidquantity | integer | Yes | 0, 1, 10 |
| lastvalidbidamount | numeric | Yes | 0.00000000, 0.01000000, 1.00000000 |
| displayround3bidrank | character varying(200) | Yes | 1, 2, 3 |
| displayround2bidrank | character varying(200) | Yes | 1, 2, 3 |
| round2bidrank | integer | Yes | 1, 10, 11 |
| round3bidrank | integer | Yes | 1, 10, 11 |
- **PK:** id

### biddata_aggregatedinventory
- **Table:** `auctionui$biddata_aggregatedinventory` | **Rows:** 11,619,882
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataid | bigint | No | 55450586199466572, 55450586199466698, 55450586199466807 |
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
- **PK:** auctionui$biddataid, auctionui$aggregatedinventoryid

### biddata_biddatadoc
- **Table:** `auctionui$biddata_biddatadoc` | **Rows:** 11,619,882
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataid | bigint | No | 55450586199466572, 55450586199466698, 55450586199466807 |
| auctionui$biddatadocid | bigint | No | 61643019903691488, 61643019903691614, 61643019903691689 |
- **PK:** auctionui$biddataid, auctionui$biddatadocid

### biddata_bidround
- **Table:** `auctionui$biddata_bidround` | **Rows:** 11,619,882
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataid | bigint | No | 55450586199466572, 55450586199466698, 55450586199466807 |
| auctionui$bidroundid | bigint | No | 69242844274994413, 69242844274994525, 69242844274994603 |
- **PK:** auctionui$biddataid, auctionui$bidroundid

### biddata_buyercode
- **Table:** `auctionui$biddata_buyercode` | **Rows:** 11,619,802
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataid | bigint | No | 55450586199466572, 55450586199466698, 55450586199466807 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097298462001, 32651097298462190, 32651097298462213 |
- **PK:** auctionui$biddataid, ecoatm_buyermanagement$buyercodeid

### allbiddownload_2
- **Table:** `auctionui$allbiddownload_2` | **Rows:** 4,335,383
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 176766285374292162, 176766285374292335, 176766285374292463 |
| ecoatmcode | integer | Yes | 10002, 10004, 10006 |
| category | character varying(200) | Yes | Cell Phone, MP3 Player, Tablet |
| deviceid | character varying(200) | Yes | 00021D07-FD1F-43D1-8975-852..., 00027A55-7B4F-47A5-9C29-F11..., 0003A08A-C60F-4A92-991D-944... |
| brandname | character varying(200) | Yes | Access Wireless, Acer, Alcatel |
| partnumber | character varying(200) | Yes |  |
| partname | character varying(200) | Yes | (Consumer Cellular) U3900, (Public Mobile CAN) N762, (Tracfone) 800G |
| added | character varying(200) | Yes |  |
| buyercode | character varying(200) | Yes | AAAA1, AD, BI |
| a_yyy | character varying(200) | Yes | $0.00, $0.01, $0.02 |
| maxofgradea_yyy | character varying(200) | Yes | $0.00, $0.68, $0.82 |
| a_yyyestimatedquantity | integer | Yes | 0, 1, 10 |
| a_yyyquantitycap | integer | Yes | 1, 10, 100 |
| b_nyy_d_nny | character varying(200) | Yes | $0.00, $0.02, $0.04 |
| maxofgradeb_nyy_d_nny | character varying(200) | Yes | $0.00, $0.24, $0.25 |
| b_nyy_d_nnyestimatedquantity | integer | Yes | 0, 1, 10 |
| b_nyy_d_nnyquantitycap | integer | Yes | 1, 10, 100 |
| c_yny_g_ynn | character varying(200) | Yes | $0.00, $0.02, $0.04 |
| maxofgradec_yny_g_ynn | character varying(200) | Yes | $0.00, $0.16, $0.27 |
| c_yny_g_ynnestimatedquantity | integer | Yes | 0, 1, 10 |
| c_yny_g_ynnquantitycap | integer | Yes | 1, 10, 100 |
| e_yyn | character varying(200) | Yes | $0.00, $0.01, $0.02 |
| maxofgradee_yyn | character varying(200) | Yes | $0.00, $0.21, $0.25 |
| e_yynestimatedquantity | integer | Yes | 0, 1, 10 |
| e_yynquantitycap | integer | Yes | 1, 10, 100 |
| f_nyn_h_nnn | character varying(200) | Yes | $0.00, $0.02, $0.10 |
| maxofgradef_nyn_h_nnn | character varying(200) | Yes | $0.00, $0.27, $0.41 |
| f_nyn_h_nnnestimatedquantity | integer | Yes | 0, 1, 10 |
| f_nyn_h_nnnquantitycap | integer | Yes | 1, 10, 100 |
| auction_week_year | character varying(200) | Yes | 2026 / Wk03, 2026 / Wk04, 2026 / Wk05 |
- **PK:** id

### aggregatedinventory
- **Table:** `auctionui$aggregatedinventory` | **Rows:** 87,284
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoid2 | integer | Yes | 18509, 247, 12377 |
| name | character varying(200) | Yes | Y7 2019 DUB-LX1, RAZR MIAMI INK ED V3-MIE, Treasure L51AL/L52VL |
| brand | character varying(200) | Yes | Amgoo, Maxwest, Amazon |
| model | character varying(200) | Yes | Celero3 5G+, iPad 3, A9L |
| carrier | character varying(200) | Yes | US Cellular, X, T-Mobile |
| mergedgrade | character varying(200) | Yes | A_YYY, F_NYN/H_NNN, B_NYY/D_NNY |
| datawipe | character varying(200) | Yes | , DW |
| avgpayout | numeric | Yes | 22.33000000, 38.45000000, 9.84000000 |
| avgtargetprice | numeric | Yes | 7.28000000, 85.33000000, 88.78000000 |
| dwtotalquantity | integer | Yes | 505, 90, 16 |
| totalquantity | integer | Yes | 845, 537, 75 |
| category | character varying(200) | Yes | MP3 Player, Tablet, Cell Phone |
| dwavgtargetprice | numeric | Yes | 138.35000000, 7.28000000, 37.28000000 |
| deviceid | character varying(200) | Yes | 9DBFBE7E-AB10-4781-8E2C-2D0..., 3577D98E-3F74-4FCD-92F5-E26..., 43F2A902-1DF1-4830-AD76-903... |
| createdat | timestamp without time zone | Yes | 2015-05-15 07:00:00, 2022-08-30 07:00:00, 2014-01-25 08:00:00 |
| dwavgpayout | numeric | Yes | 38.45000000, 2.44000000, 313.67000000 |
| totalpayout | numeric | Yes | 22.33000000, 793.00000000, 1753.00000000 |
| dwtotalpayout | numeric | Yes | 793.00000000, 1753.00000000, 2004.00000000 |
| changeddate | timestamp without time zone | Yes | 2026-02-09 23:19:52.681, 2026-02-04 23:29:05.567, 2026-02-03 22:53:14.939 |
| system$owner | bigint | Yes | 9851624184950013, 23925373020470267, 23925373020444341 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020998413, 23925373021011861 |
| createddate | timestamp without time zone | Yes | 2026-02-20 02:07:17.503, 2026-01-16 02:07:05.968, 2026-01-23 03:23:27.151 |
| round1targetprice_dw | numeric | Yes | 223.77000000, 85.33000000, 58.31000000 |
| round3targetprice | numeric | Yes | 138.35000000, 7.28000000, 37.28000000 |
| round2maxbid | numeric | Yes | 96.30000000, 85.33000000, 58.31000000 |
| round1targetprice | numeric | Yes | 53.49000000, 138.35000000, 7.28000000 |
| round2targetprice | numeric | Yes | 422.63000000, 7.28000000, 138.35000000 |
| round1maxbid | numeric | Yes | 96.30000000, 85.33000000, 2.20000000 |
| r3targetpricefactortype | character varying(17) | Yes | Percentage_Factor, Flat_Amount |
| r2targetpricefactor | numeric | Yes | 100.72000000, 107.07000000, 106.17000000 |
| r3targetpricefactor | numeric | Yes | 10.00000000, 5.00000000, 105.00000000 |
| r2targetpricefactortype | character varying(17) | Yes | Percentage_Factor |
| round3ebfortarget | numeric | Yes | 138.35000000, 7.28000000, 37.28000000 |
| round2ebfortarget | numeric | Yes | 138.35000000, 7.28000000, 37.28000000 |
| round1maxbidbuyercode | character varying(200) | Yes | Y2,Y2EXT,ZY,WVEXT, ZC,ZO,LP, FK |
| round2maxbidbuyercode | character varying(200) | Yes | ZO,HT,ZO,HT, ZC,TY, Y6,ZB |
| r2pomaxbuyercode | character varying(200) | Yes | ZDPO, PAAPO, FIPO1 |
| r2pomaxbid | numeric | Yes | 104.59812000, 24.10600000, 117.44000000 |
| r3pomaxbid | numeric | Yes | 104.59812000, 24.10600000, 117.44000000 |
| istotalquantitymodified | boolean | Yes | true, false |
| r3pomaxbuyercode | character varying(200) | Yes | ZDPO, PAAPO, FIPO1 |
| isdeprecated | boolean | Yes | false |
- **PK:** id

### aggregatedinventory_brand
- **Table:** `auctionui$aggregatedinventory_brand` | **Rows:** 80,298
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoatm_mdm$brandid | bigint | No | 173951535607275153, 173951535607188681, 173951535607187113 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$brandid

### aggregatedinventory_carrier
- **Table:** `auctionui$aggregatedinventory_carrier` | **Rows:** 80,298
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoatm_mdm$carrierid | bigint | No | 175077435514041347, 175077435514029573, 175077435514053824 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$carrierid

### aggregatedinventory_model
- **Table:** `auctionui$aggregatedinventory_model` | **Rows:** 80,298
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoatm_mdm$modelid | bigint | No | 170855310863513928, 170855310863467793, 170855310864777229 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$modelid

### aggregatedinventory_modelname
- **Table:** `auctionui$aggregatedinventory_modelname` | **Rows:** 80,298
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoatm_mdm$modelnameid | bigint | No | 174514485562653452, 174514485562605467, 174514485562037905 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$modelnameid

### aggregatedinventory_week
- **Table:** `auctionui$aggregatedinventory_week` | **Rows:** 80,298
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369475144168, 68961369478085319, 68961369475497570 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288427, 69805794224288598, 69805794224288931 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$weekid

### bidsubmitlog
- **Table:** `auctionui$bidsubmitlog` | **Rows:** 24,402
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 150026162587299911, 150026162587613277, 150026162590508047 |
| submittedby | character varying(200) | Yes | aljaraselectronics@gmail.com, ekomventures@gmail.com, webuyimax@gmail.com |
| submitdatetime | timestamp without time zone | Yes | 2025-09-22 15:05:08.626, 2025-09-26 19:40:32.156, 2025-09-22 13:55:20.824 |
| submitaction | character varying(18) | Yes | Push_To_Sharepoint, User_Submit |
| retrycount | integer | Yes | 3, 0, 2 |
| status | character varying(7) | Yes | Error, Success |
| message | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-07-28 11:04:48.8, 2025-09-08 15:38:47.976, 2025-09-22 15:05:08.626 |
| changeddate | timestamp without time zone | Yes | 2025-11-11 15:50:07.531, 2026-01-14 16:23:45.905, 2026-02-16 16:59:33.062 |
| system$owner | bigint | Yes | 23925373021660346, 23925373021007967, 23925373022646096 |
| system$changedby | bigint | Yes | 23925373021660346, 23925373021007967, 23925373022646096 |
- **PK:** id

### biddatadoc
- **Table:** `auctionui$biddatadoc` | **Rows:** 18,858
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 61643019902154064, 61643019900210017, 61643019903269633 |
- **PK:** id

### allbiddownload
- **Table:** `auctionui$allbiddownload` | **Rows:** 17,776
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 61924495797736461, 61924495797848799, 61924495353564449 |
| ecoatmcode | integer | Yes | 2880, 12377, 12059 |
| category | character varying(200) | Yes | MP3 Player, Tablet, Cell Phone |
| deviceid | character varying(200) | Yes | 48882060-bfb8-4419-9a77-b55..., 060a05dd-5d98-4fb2-bb65-d1d..., 92d5198e-03fc-4e46-86f3-b15... |
| brandname | character varying(200) | Yes | Unnecto, Sony Ericsson, SanDisk |
| partnumber | character varying(200) | Yes |  |
| partname | character varying(200) | Yes | Y7 2019 DUB-LX1, Galaxy Z Flip 4G 256GB Spri..., Treasure L51AL/L52VL |
| added | character varying(200) | Yes |  |
| buyercode | character varying(200) | Yes | FC, DW65, TH |
| a_yyy | character varying(200) | Yes | $0.00 |
| maxofgradea_yyy | character varying(200) | Yes | $12.88, $5.14, $42.57 |
| a_yyyestimatedquantity | numeric | Yes | 21.00000000, 410.00000000, 182.00000000 |
| a_yyyquantitycap | integer | Yes |  |
| b_nyy_d_nny | character varying(200) | Yes | $0.00 |
| maxofgradeb_nyy_d_nny | character varying(200) | Yes | $11.88, $5.49, $53.9 |
| b_nyy_d_nnyestimatedquantity | numeric | Yes | 52.00000000, 46.00000000, 28.00000000 |
| b_nyy_d_nnyquantitycap | integer | Yes |  |
| c_yny_g_ynn | character varying(200) | Yes | $0.00 |
| maxofgradec_yny_g_ynn | character varying(200) | Yes | $49.55, $404.03, $12.88 |
| c_yny_g_ynnestimatedquantity | numeric | Yes | 410.00000000, 46.00000000, 187.00000000 |
| c_yny_g_ynnquantitycap | integer | Yes |  |
| e_yyn | character varying(200) | Yes | $0.00 |
| maxofgradee_yyn | character varying(200) | Yes | $145.19, $12.88, $517.44 |
| e_yynestimatedquantity | numeric | Yes | 230.00000000, 46.00000000, 133.00000000 |
| e_yynquantitycap | integer | Yes |  |
| f_nyn_h_nnn | character varying(200) | Yes | $0.00 |
| maxofgradef_nyn_h_nnn | character varying(200) | Yes | $7.27, $102.51, $14.25 |
| f_nyn_h_nnnestimatedquantity | numeric | Yes | 52.00000000, 46.00000000, 31.00000000 |
| f_nyn_h_nnnquantitycap | integer | Yes |  |
| auction_week_year | character varying(200) | Yes |  |
- **PK:** id

### allbiddownload_allbidsdoc
- **Table:** `auctionui$allbiddownload_allbidsdoc` | **Rows:** 17,776
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$allbiddownloadid | bigint | No | 61924495797736461, 61924495797848799, 61924495353564449 |
| auctionui$allbidsdocid | bigint | No | 53480245575898423, 53480245575991793, 53480245576033804 |
- **PK:** auctionui$allbiddownloadid, auctionui$allbidsdocid

### schedulingauction_qualifiedbuyers
- **Table:** `auctionui$schedulingauction_qualifiedbuyers` | **Rows:** 5,735
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$schedulingauctionid | bigint | No | 71776119062305759, 71776119062292616, 71776119062343965 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097298718421, 32651097299106220, 32651097299614461 |
- **PK:** auctionui$schedulingauctionid, ecoatm_buyermanagement$buyercodeid

### bidsubmitlog_bidround
- **Table:** `auctionui$bidsubmitlog_bidround` | **Rows:** 3,850
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidsubmitlogid | bigint | No | 150026162590485944, 150026162590297996, 150026162590228576 |
| auctionui$bidroundid | bigint | No | 69242844275107147, 69242844275252217, 69242844275172473 |
- **PK:** auctionui$bidsubmitlogid, auctionui$bidroundid

### r3pomaxbidweek
- **Table:** `auctionui$r3pomaxbidweek` | **Rows:** 3,635
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369482643179, 68961369483829810, 68961369483262390 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288931, 69805794224288427 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$weekid

### r2pomaxbidweek
- **Table:** `auctionui$r2pomaxbidweek` | **Rows:** 3,633
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggregatedinventoryid | bigint | No | 68961369482643179, 68961369483829810, 68961369483262390 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288931, 69805794224288427 |
- **PK:** auctionui$aggregatedinventoryid, ecoatm_mdm$weekid

### allbidsdoc
- **Table:** `auctionui$allbidsdoc` | **Rows:** 1,982
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 53480245575973264, 53480245575690559, 53480245576099026 |
- **PK:** id

### roundthreebuyersdatareport
- **Table:** `auctionui$roundthreebuyersdatareport` | **Rows:** 1,958
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 137078313659413321, 137078313660753764, 137078313662215132 |
| companyname | character varying(200) | Yes | Billion Peak Telecom Company, S & F Electronics LLC, MUK Electronics |
| buyercodes | text | Yes |  |
| submittedby | character varying(200) | Yes | ashirwad.mittal@ecoatm.com, nathan.rohrer@ecoatm.com, naresh.agarwal@ecoatm.com |
| submittedon | timestamp without time zone | Yes | 2025-06-11 16:26:53.674, 2025-06-25 17:05:33.691, 2025-05-07 16:05:32.144 |
| createddate | timestamp without time zone | Yes | 2025-04-16 22:08:58.101, 2026-02-17 20:00:14.766, 2026-01-20 19:29:07.32 |
| changeddate | timestamp without time zone | Yes | 2025-05-07 15:43:04.525, 2025-06-25 16:29:48.475, 2026-02-18 17:59:47.148 |
| system$owner | bigint | Yes | 23925373020495635, 23925373020470267, 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020495635, 23925373020470267, 23925373020815588 |
- **PK:** id

### bidround_biddatadoc
- **Table:** `auctionui$bidround_biddatadoc` | **Rows:** 1,924
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No | 69242844275107147, 69242844275279080, 69242844275022056 |
| auctionui$biddatadocid | bigint | No | 61643019903924297, 61643019903854155, 61643019903737542 |
- **PK:** auctionui$bidroundid, auctionui$biddatadocid

### bidround_buyercode
- **Table:** `auctionui$bidround_buyercode` | **Rows:** 1,924
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No | 69242844275107147, 69242844275279080, 69242844275022056 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097298718421, 32651097299106220, 32651097299614461 |
- **PK:** auctionui$bidroundid, ecoatm_buyermanagement$buyercodeid

### bidround_schedulingauction
- **Table:** `auctionui$bidround_schedulingauction` | **Rows:** 1,924
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No | 69242844275107147, 69242844275279080, 69242844275022056 |
| auctionui$schedulingauctionid | bigint | No | 71776119062305759, 71776119062292616, 71776119062343965 |
- **PK:** auctionui$bidroundid, auctionui$schedulingauctionid

### bidround
- **Table:** `auctionui$bidround` | **Rows:** 1,917
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 69242844275107147, 69242844275022056, 69242844275279080 |
| bidroundid | bigint | Yes | 18782, 18353, 17764 |
| submitted | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2026-02-13 17:39:35.444, 2026-01-16 14:15:52.815, 2026-02-03 03:12:53.266 |
| changeddate | timestamp without time zone | Yes | 2026-02-02 02:27:07.729, 2026-02-16 15:20:05.59, 2026-02-02 16:47:37.852 |
| system$changedby | bigint | Yes | 23925373021007967, 23925373022646096, 23925373022671603 |
| system$owner | bigint | Yes | 23925373021007967, 23925373022646096, 23925373022671603 |
| note | character varying(2000) | Yes | This bid ahs been submitted..., Please submit your bids on ...,    |
| uploadedtosharepoint | boolean | Yes | true, false |
| submitteddatatime | timestamp without time zone | Yes | 2026-01-27 15:49:02.441, 2026-02-09 16:38:22.331, 2026-02-16 14:50:23.709 |
| uploadtosharepointdatetime | timestamp without time zone | Yes | 2026-02-16 15:24:08.52, 2026-02-02 15:53:02.828, 2026-02-11 18:00:48.719 |
| isdeprecated | boolean | Yes | false |
- **PK:** id

### bidround_submittedby
- **Table:** `auctionui$bidround_submittedby` | **Rows:** 1,145
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No | 69242844275107147, 69242844275252217, 69242844275172473 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373021007967, 23925373022646096, 23925373021673769 |
- **PK:** auctionui$bidroundid, ecoatm_usermanagement$ecoatmdirectuserid

### roundthreebuyersdatareport_auction
- **Table:** `auctionui$roundthreebuyersdatareport_auction` | **Rows:** 740
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$roundthreebuyersdatareportid | bigint | No | 137078313662218314, 137078313666113229, 137078313662200810 |
| auctionui$auctionid | bigint | No | 54606145483006691, 54606145483045017, 54606145482993914 |
- **PK:** auctionui$roundthreebuyersdatareportid, auctionui$auctionid

### bidround_week
- **Table:** `auctionui$bidround_week` | **Rows:** 119
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No | 69242844275250542, 69242844275109788, 69242844275295308 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288855, 69805794224288427, 69805794224288267 |
- **PK:** auctionui$bidroundid, ecoatm_mdm$weekid

### agginventoryhelper
- **Table:** `auctionui$agginventoryhelper` | **Rows:** 27
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 104427216359653514, 104427216359820024, 104427216359845562 |
| hasauction | boolean | Yes | true, false |
| hasschedule | boolean | Yes | true, false |
| hasauctionbeentriggered | boolean | Yes | true, false |
| hasinventory | boolean | Yes | true, false |
| auctionname | character varying(200) | Yes |  |
| iscurrentweek | boolean | Yes | true, false |
| wastitleempty | boolean | Yes | true, false |
| istotalquantitymodified | boolean | Yes | true, false |
- **PK:** id

### agginventoryhelper_account
- **Table:** `auctionui$agginventoryhelper_account` | **Rows:** 27
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$agginventoryhelperid | bigint | No | 104427216359653514, 104427216359909593, 104427216359845562 |
| administration$accountid | bigint | No | 23925373020444502, 23925373020444341, 23925373020777255 |
- **PK:** auctionui$agginventoryhelperid, administration$accountid

### roundthreebiddataexcelexport
- **Table:** `auctionui$roundthreebiddataexcelexport` | **Rows:** 26
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 141018963332295205, 141018963332103880, 141018963332105659 |
| errormessage | character varying(200) | Yes | Unable to import data due t..., Column with name: 'Estimate..., Sheet with a name 'BidData'... |
- **PK:** id

### targetpricefactor
- **Table:** `auctionui$targetpricefactor` | **Rows:** 24
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 110901140824037179, 110901140824011883, 110901140823998778 |
| minimumvalue | numeric | Yes | 500.00000000, 300.00000000, 550.00000000 |
| maximumvalue | numeric | Yes | 199.99000000, 149.99000000, 24.99000000 |
| factortype | character varying(17) | Yes | Percentage_Factor, Flat_Amount |
| factoramount | numeric | Yes | 106.58000000, 10.00000000, 100.13000000 |
- **PK:** id

### targetpricefactor_bidroundselectionfilter
- **Table:** `auctionui$targetpricefactor_bidroundselectionfilter` | **Rows:** 24
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$targetpricefactorid | bigint | No | 110901140824037179, 110901140824011883, 110901140823998778 |
| auctionui$bidroundselectionfilterid | bigint | No | 85849867896788630, 85849867896763108 |
- **PK:** auctionui$targetpricefactorid, auctionui$bidroundselectionfilterid

### allbiddownload_screenhelper
- **Table:** `auctionui$allbiddownload_screenhelper` | **Rows:** 23
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 81627743249047419, 81627743249162573, 81627743249034454 |
| r1caption | character varying(200) | Yes |  |
| r2caption | character varying(200) | Yes |  |
| upsellcaption | character varying(200) | Yes |  |
- **PK:** id

### agginventoryhelper_week
- **Table:** `auctionui$agginventoryhelper_week` | **Rows:** 22
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$agginventoryhelperid | bigint | No | 104427216359653514, 104427216359909593, 104427216359820024 |
| ecoatm_mdm$weekid | bigint | No | 69805794224287821, 69805794224286614, 69805794224287417 |
- **PK:** auctionui$agginventoryhelperid, ecoatm_mdm$weekid

### schedulingauction
- **Table:** `auctionui$schedulingauction` | **Rows:** 15
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 71776119062305759, 71776119062292616, 71776119062343965 |
| auction_week_year | character varying(200) | Yes | 2026 / Wk04, 2026 / Wk07, 2026 / Wk06 |
| round | integer | Yes | 3, 2, 1 |
| name | character varying(200) | Yes | Round 2, Round 1, Upsell Round |
| start_datetime | timestamp without time zone | Yes | 2026-02-12 21:00:00, 2026-02-16 18:00:00, 2026-01-19 18:00:00 |
| end_datetime | timestamp without time zone | Yes | 2026-02-03 12:00:00, 2026-02-16 12:30:00, 2026-02-02 13:00:00 |
| isstartnotificationsent | boolean | Yes | true |
| isendnotificationsent | boolean | Yes | false |
| isremindernotificationsent | boolean | Yes | true, false |
| roundstatus | character varying(11) | Yes | Closed |
| hasround | boolean | Yes | true |
| createddate | timestamp without time zone | Yes | 2026-01-16 02:28:52.051, 2026-01-30 02:49:59.283, 2026-02-13 01:56:03.958 |
| changeddate | timestamp without time zone | Yes | 2026-02-16 17:31:00.885, 2026-01-20 17:01:00.583, 2026-02-18 22:00:00.221 |
| system$owner | bigint | Yes | 23925373020444341, 23925373020470267 |
| emailreminders | character varying(12) | Yes | OneHourSent, AllSent, FourHourSent |
| updatedby | character varying(200) | Yes |  |
| createdby | character varying(200) | Yes |  |
| notificationsenabled | boolean | Yes | true |
| round3initstatus | character varying(8) | Yes | Complete, Pending |
| snowflakejson | text | Yes |  |
| system$changedby | bigint | Yes | 23925373020444341, 23925373020777255, 23925373020815588 |
- **PK:** id

### schedulingauction_auction
- **Table:** `auctionui$schedulingauction_auction` | **Rows:** 15
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$schedulingauctionid | bigint | No | 71776119062305759, 71776119062292616, 71776119062343965 |
| auctionui$auctionid | bigint | No | 54606145482993914, 54606145483032299, 54606145483019401 |
- **PK:** auctionui$schedulingauctionid, auctionui$auctionid

### aggreegatedinventorytotals
- **Table:** `auctionui$aggreegatedinventorytotals` | **Rows:** 9
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 107804916081346311, 107804916081320626, 107804916081205469 |
| avgtargetprice | numeric | Yes | 91.07000000, 71.65000000, 0.00000000 |
| avgpayout | numeric | Yes | 41.51000000, 43.18000000, 0.00000000 |
| totalpayout | numeric | Yes | 5158627.84000000, 5298574.41000000, 6278419.76000000 |
| totalquantity | bigint | Yes | 136368, 131932, 145587 |
| dwavgtargetprice | numeric | Yes | 197.42000000, 186.66000000, 0.00000000 |
| dwavgpayout | numeric | Yes | 88.12000000, 92.59000000, 0.00000000 |
| dwtotalpayout | numeric | Yes | 3658730.67000000, 3888065.71000000, 3663601.42000000 |
| dwtotalquantity | bigint | Yes | 36616, 42171, 41234 |
| maxuploadtime | timestamp without time zone | Yes | 2026-02-05 21:24:13, 2025-12-24 19:06:21, 2026-02-12 20:45:59 |
| changeddate | timestamp without time zone | Yes | 2026-01-30 02:40:17.115, 2026-02-13 01:50:34.449, 2026-01-23 03:23:28.255 |
| system$owner | bigint | Yes | 9851624184950013, 23925373020444341, 23925373020418781 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020444341, 23925373020418781 |
| createddate | timestamp without time zone | Yes | 2025-12-25 00:15:18.754, 2026-02-13 01:50:34.442, 2026-02-06 02:26:23.502 |
- **PK:** id

### agginventorytotals_week
- **Table:** `auctionui$agginventorytotals_week` | **Rows:** 7
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$aggreegatedinventorytotalsid | bigint | No | 107804916081346311, 107804916081320626, 107804916081384652 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288427, 69805794224288598, 69805794224288931 |
- **PK:** auctionui$aggreegatedinventorytotalsid, ecoatm_mdm$weekid

### auction
- **Table:** `auctionui$auction` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 54606145482993914, 54606145483032299, 54606145483019401 |
| auctiontitle | character varying(200) | Yes | Auction 2026 / Wk04, Auction 2026 / Wk06, Auction 2026 / Wk07 |
| createddate | timestamp without time zone | Yes | 2026-02-13 01:53:50.213, 2026-01-30 02:48:06.715, 2026-01-23 03:32:13.186 |
| changeddate | timestamp without time zone | Yes | 2026-02-04 22:01:00.896, 2026-01-28 22:00:00.352, 2026-02-18 22:00:00.233 |
| system$changedby | bigint | Yes | 23925373020444341, 23925373020470267 |
| system$owner | bigint | Yes | 23925373020444341, 23925373020470267 |
| auctionstatus | character varying(11) | Yes | Closed |
| createdby | character varying(200) | Yes |  |
| updatedby | character varying(200) | Yes |  |
- **PK:** id

### auction_week
- **Table:** `auctionui$auction_week` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$auctionid | bigint | No | 54606145482993914, 54606145483032299, 54606145483019401 |
| ecoatm_mdm$weekid | bigint | No | 69805794224288427, 69805794224288598, 69805794224288931 |
- **PK:** auctionui$auctionid, ecoatm_mdm$weekid

### biddatatotalquantityconfig
- **Table:** `auctionui$biddatatotalquantityconfig` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 141863388275354753, 141863388275354970, 141863388275355040 |
| ecoid | integer | Yes | 14281, 14299, 14323 |
| grade | character varying(11) | Yes | A_YYY |
| nondwquantity | integer | Yes | 0 |
| datawipequantity | integer | Yes | 104, 134, 425 |
- **PK:** id

### bidroundselectionfilter
- **Table:** `auctionui$bidroundselectionfilter` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 85849867896763108, 85849867896788630 |
| submissionid | bigint | Yes | 2, 4 |
| round | integer | Yes | 2, 3 |
| targetpercent | numeric | Yes | 0.00000000, 0.15000000 |
| targetvalue | integer | Yes | 0, 15 |
| totalvaluefloor | integer | Yes | 0 |
| mergedgrade1 | character varying(200) | Yes | A_YYY |
| mergedgrade2 | character varying(200) | Yes | C_YNY/G_YNN |
| mergedgrade3 | character varying(200) | Yes | E_YYN |
| createddate | timestamp without time zone | Yes | 2024-08-30 17:48:13.319, 2025-02-26 21:17:40.936 |
| changeddate | timestamp without time zone | Yes | 2025-02-26 21:20:06.902, 2026-02-06 18:33:22.978 |
| system$owner | bigint | Yes | 23925373020419277, 23925373020533909 |
| system$changedby | bigint | Yes | 23925373020419277, 23925373020470267 |
| stballowallbuyersoverride | boolean | Yes | false, true |
| regularbuyerqualification | character varying(14) | Yes | Only_Qualified |
| stbincludeallinventory | boolean | Yes | false |
| regularbuyerinventoryoptions | character varying(28) | Yes | InventoryRound1QualifiedBids |
- **PK:** id

### biddata_importsettings
- **Table:** `auctionui$biddata_importsettings` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 86975767803592894 |
| templatename | character varying(200) | Yes |  |
- **PK:** id

### bidranking
- **Table:** `auctionui$bidranking` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 179018085188003062 |
| displayrank | boolean | Yes | true |
| includeebforranking | boolean | Yes | true |
| minbid | numeric | Yes | 2.00000000 |
| maxrank | integer | Yes | 5 |
| startingrank | integer | Yes | 1 |
- **PK:** id

### sharepointmethod
- **Table:** `auctionui$sharepointmethod` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 177892185281134792 |
| method | character varying(3) | Yes | OQL |
- **PK:** id

### agginventoryhelper_auction
- **Table:** `auctionui$agginventoryhelper_auction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$agginventoryhelperid | bigint | No |  |
| auctionui$auctionid | bigint | No |  |
- **PK:** auctionui$agginventoryhelperid, auctionui$auctionid

### allbiddownload_2_allbidsdoc
- **Table:** `auctionui$allbiddownload_2_allbidsdoc` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$allbiddownload_2id | bigint | No |  |
| auctionui$allbidsdocid | bigint | No |  |
- **PK:** auctionui$allbiddownload_2id, auctionui$allbidsdocid

### allbidsdocall_auction
- **Table:** `auctionui$allbidsdocall_auction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$allbidsdocid | bigint | No |  |
| auctionui$auctionid | bigint | No |  |
- **PK:** auctionui$allbidsdocid, auctionui$auctionid

### allbidsdocdw_auction
- **Table:** `auctionui$allbidsdocdw_auction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$allbidsdocid | bigint | No |  |
| auctionui$auctionid | bigint | No |  |
- **PK:** auctionui$allbidsdocid, auctionui$auctionid

### allbidszipped
- **Table:** `auctionui$allbidszipped` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### allbidsziptemplist
- **Table:** `auctionui$allbidsziptemplist` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### biddata_importsettings_defaulttemplate
- **Table:** `auctionui$biddata_importsettings_defaulttemplate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddata_importsettingsid | bigint | No |  |
| excelimporter$templateid | bigint | No |  |
- **PK:** auctionui$biddata_importsettingsid, excelimporter$templateid

### biddatadeletehelper
- **Table:** `auctionui$biddatadeletehelper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| startdate | timestamp without time zone | Yes |  |
| enddate | timestamp without time zone | Yes |  |
| scripttorun | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### biddataqueryhelper
- **Table:** `auctionui$biddataqueryhelper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### biddataqueryhelper_auction
- **Table:** `auctionui$biddataqueryhelper_auction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataqueryhelperid | bigint | No |  |
| auctionui$auctionid | bigint | No |  |
- **PK:** auctionui$biddataqueryhelperid, auctionui$auctionid

### biddataqueryhelper_buyercode
- **Table:** `auctionui$biddataqueryhelper_buyercode` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataqueryhelperid | bigint | No |  |
| ecoatm_buyermanagement$buyercodeid | bigint | No |  |
- **PK:** auctionui$biddataqueryhelperid, ecoatm_buyermanagement$buyercodeid

### biddataqueryhelper_schedulingauction
- **Table:** `auctionui$biddataqueryhelper_schedulingauction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataqueryhelperid | bigint | No |  |
| auctionui$schedulingauctionid | bigint | No |  |
- **PK:** auctionui$biddataqueryhelperid, auctionui$schedulingauctionid

### biddataqueryhelper_session
- **Table:** `auctionui$biddataqueryhelper_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$biddataqueryhelperid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** auctionui$biddataqueryhelperid, system$sessionid

### bidround_ecoatmdirectuser
- **Table:** `auctionui$bidround_ecoatmdirectuser` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No |  |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No |  |
- **PK:** auctionui$bidroundid, ecoatm_usermanagement$ecoatmdirectuserid

### bidround_xmldocumenttemplate
- **Table:** `auctionui$bidround_xmldocumenttemplate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$bidroundid | bigint | No |  |
| excelimporter$xmldocumenttemplateid | bigint | No |  |
- **PK:** auctionui$bidroundid, excelimporter$xmldocumenttemplateid

### datadogtest
- **Table:** `auctionui$datadogtest` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| rolelist | text | Yes |  |
- **PK:** id

### roundthreebiddataexcelexport_roundthreebiddatareport
- **Table:** `auctionui$roundthreebiddataexcelexport_roundthreebiddatareport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$roundthreebiddataexcelexportid | bigint | No |  |
| auctionui$roundthreebiddatareportid | bigint | No |  |
- **PK:** auctionui$roundthreebiddataexcelexportid, auctionui$roundthreebiddatareportid

### roundthreebiddatareport
- **Table:** `auctionui$roundthreebiddatareport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| codegrade | character varying(200) | Yes |  |
| brand | character varying(200) | Yes |  |
| partname | character varying(200) | Yes |  |
| estimatedunitcount | integer | Yes |  |
| buyerqtycap | character varying(200) | Yes |  |
| winningsku | character varying(200) | Yes |  |
| bid | character varying(200) | Yes |  |
| maxbid | character varying(200) | Yes |  |
| acceptmaxbid | character varying(200) | Yes |  |
| buyername | character varying(2000) | Yes |  |
| buyercode | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
| bidrank | integer | Yes |  |
- **PK:** id

### roundthreebuyersdatarepor_roundthreebiddataexcelexpor
- **Table:** `auctionui$roundthreebuyersdatarepor_roundthreebiddataexcelexpor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$roundthreebuyersdatareportid | bigint | No |  |
| auctionui$roundthreebiddataexcelexportid | bigint | No |  |
- **PK:** auctionui$roundthreebuyersdatareportid, auctionui$roundthreebiddataexcelexportid

### zerobiddownload
- **Table:** `auctionui$zerobiddownload` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| ecoatmcode | integer | Yes |  |
| brandname | character varying(200) | Yes |  |
| partname | character varying(200) | Yes |  |
| buyercode | character varying(200) | Yes |  |
| a_yyy | integer | Yes |  |
| b_nyy | integer | Yes |  |
| c_yny | integer | Yes |  |
| d_nny | integer | Yes |  |
| e_yyn | integer | Yes |  |
| f_nyn | integer | Yes |  |
| g_ynn | integer | Yes |  |
| h_nnn | integer | Yes |  |
| inactive | character varying(200) | Yes |  |
- **PK:** id

### zerobiddownload_allbidsdoc
- **Table:** `auctionui$zerobiddownload_allbidsdoc` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| auctionui$zerobiddownloadid | bigint | No |  |
| auctionui$allbidsdocid | bigint | No |  |
- **PK:** auctionui$zerobiddownloadid, auctionui$allbidsdocid

---
