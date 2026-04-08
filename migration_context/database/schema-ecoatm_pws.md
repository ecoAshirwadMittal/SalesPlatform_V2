## ecoatm_pws

### skusyncdetail
- **Table:** `ecoatm_pws$skusyncdetail` | **Rows:** 11,603,451
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 153122387330597015, 153122387330597192, 153122387330597332 |
| sku | character varying(200) | Yes | INT000000100309, INT000000100318, INT000000101536 |
| previoussynctime | timestamp without time zone | Yes | 2025-08-21 21:24:01.754, 2025-08-21 22:09:11.023, 2025-08-21 22:21:11.646 |
| previoussyncqty | integer | Yes | -1, -10, -100 |
| synctime | timestamp without time zone | Yes | 2025-08-21 21:24:01.754, 2025-08-21 21:44:03.351, 2025-08-22 00:07:41.935 |
| syncqty | integer | Yes | -1, -10, -11 |
| deltaqty | integer | Yes | -1, -10, -103 |
| notfound | character varying(200) | Yes | Deposco, N/A, Sales Platform |
| createddate | timestamp without time zone | Yes | 2025-09-29 22:10:35.908, 2025-11-17 12:10:50.375, 2025-11-19 06:10:51.647 |
| changeddate | timestamp without time zone | Yes | 2025-11-09 00:10:49.702, 2025-12-24 08:10:40.931, 2025-11-27 12:10:47.01 |
| system$owner | bigint | Yes | 23925373020431548, 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020431548, 23925373020815588 |
- **PK:** id

### skusyncdetail_pwsinventorysyncreport
- **Table:** `ecoatm_pws$skusyncdetail_pwsinventorysyncreport` | **Rows:** 11,603,451
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$skusyncdetailid | bigint | No | 153122387330597015, 153122387330597192, 153122387330597332 |
| ecoatm_pws$pwsinventorysyncreportid | bigint | No | 159033361841520856, 159033361841521002, 159033361841521115 |
- **PK:** ecoatm_pws$skusyncdetailid, ecoatm_pws$pwsinventorysyncreportid

### buyerofferitem
- **Table:** `ecoatm_pws$buyerofferitem` | **Rows:** 744,669
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 120189815055670639, 120189815055682999, 120189815055695575 |
| quantity | integer | Yes | 0, 1, 10 |
| offerprice | integer | Yes | 0, 1, 100 |
| totalprice | integer | Yes | 100, 10080, 1020 |
| cssclass | character varying(200) | Yes | , default-data, offer-price-green |
| system$changedby | bigint | Yes | 23925373020418781, 23925373020419339, 23925373020431548 |
| changeddate | timestamp without time zone | Yes | 2025-04-17 20:57:17.077, 2025-04-17 20:57:17.086, 2025-04-17 20:57:17.09 |
| createddate | timestamp without time zone | Yes | 2025-04-17 20:57:16.793, 2025-04-17 20:58:06.422, 2025-04-17 20:58:06.429 |
| system$owner | bigint | Yes | 23925373020418781, 23925373020419339, 23925373020431548 |
| isexceedqty | boolean | Yes | false, true |
| islowerthencurrentprice | boolean | Yes | false, true |
| caseoffertotalprice | integer | Yes | 0 |
| _type | character varying(19) | Yes | Functional_Case_Lot, Functional_Device, Untested_Device |
| ismodified | boolean | Yes | false, true |
- **PK:** id

### buyerofferitem_device
- **Table:** `ecoatm_pws$buyerofferitem_device` | **Rows:** 744,669
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$buyerofferitemid | bigint | No | 120189815055670639, 120189815055682999, 120189815055695575 |
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865103926100, 119626865102442068, 119626865103381062 |
- **PK:** ecoatm_pws$buyerofferitemid, ecoatm_pwsmdm$deviceid

### buyerofferitem_buyeroffer
- **Table:** `ecoatm_pws$buyerofferitem_buyeroffer` | **Rows:** 271,918
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$buyerofferitemid | bigint | No | 120189815055820485, 120189815055820620, 120189815055820786 |
| ecoatm_pws$buyerofferid | bigint | No | 117093590311633031, 117093590311633244, 117093590311645897 |
- **PK:** ecoatm_pws$buyerofferitemid, ecoatm_pws$buyerofferid

### buyerofferitem_buyercode
- **Table:** `ecoatm_pws$buyerofferitem_buyercode` | **Rows:** 261,318
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$buyerofferitemid | bigint | No | 120189815055946352, 120189815055946406, 120189815055946551 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299051661, 32651097299064172, 32651097299119500 |
- **PK:** ecoatm_pws$buyerofferitemid, ecoatm_buyermanagement$buyercodeid

### offerdataexport
- **Table:** `ecoatm_pws$offerdataexport` | **Rows:** 168,623
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 146648462866251988, 146648462866252125, 146648462866252163 |
| offerid | character varying(200) | Yes | 2455925180, 2193925034, 2462125064 |
| offerstatus | character varying(200) | Yes | Partially Loaded, Pick Complete, Buyer Acceptance |
| buyername | character varying(200) | Yes | Pre-Loved Tech LLC, Prop & Tec LLC, The Future Mobile LLC |
| buyercode | character varying(200) | Yes | 24440, 29920, 28379 |
| salesrep | character varying(200) | Yes | Nick Prodzenko, Brian Tepfer, Julie Spellman |
| skus | integer | Yes | 0, 1, 10 |
| qty | integer | Yes | 0, 1, 10 |
| offerprice | numeric | Yes | 45320.00000000, 7490.00000000, 43500.00000000 |
| offerdate | timestamp without time zone | Yes | 2026-01-19 23:52:44.856, 2025-07-24 15:32:35.892, 2025-11-20 20:28:10.794 |
| lastupdated | timestamp without time zone | Yes | 2025-10-10 16:50:19.44, 2025-07-25 19:43:10.98, 2025-09-18 18:25:32.391 |
- **PK:** id

### imeidetail
- **Table:** `ecoatm_pws$imeidetail` | **Rows:** 155,911
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 153685337288747129, 153685337288747185, 153685337288747388 |
| imeinumber | character varying(200) | Yes | 0, 089663170204465958, 350002260201332 |
| createddate | timestamp without time zone | Yes | 2025-09-16 20:29:38.354, 2025-09-16 20:29:38.767, 2025-09-16 20:29:38.785 |
| changeddate | timestamp without time zone | Yes | 2025-09-17 21:34:28.237, 2025-09-16 20:30:56.661, 2025-11-14 23:19:32.785 |
| system$owner | bigint | Yes | 9851624184950013, 23925373020815588 |
| system$changedby | bigint | Yes | 23925373021520279, 9851624184950013, 23925373020815588 |
| serialnumber | character varying(200) | Yes | 350002261370631, 350002262833223, 350002263483911 |
| boxlpnnumber | character varying(200) | Yes | LPN00062412072, LPN00062417022, LPN00062415714 |
- **PK:** id

### imeidetail_offeritem
- **Table:** `ecoatm_pws$imeidetail_offeritem` | **Rows:** 155,911
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$imeidetailid | bigint | No | 153685337288747129, 153685337288747185, 153685337288747388 |
| ecoatm_pws$offeritemid | bigint | No | 125819314593593057, 125819314594664980, 125819314593653371 |
- **PK:** ecoatm_pws$imeidetailid, ecoatm_pws$offeritemid

### imeidetail_shipmentdetail
- **Table:** `ecoatm_pws$imeidetail_shipmentdetail` | **Rows:** 155,911
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$imeidetailid | bigint | No | 153685337288747129, 153685337288747185, 153685337288747388 |
| ecoatm_pws$shipmentdetailid | bigint | No | 157625986958253339, 157625986958386788, 157625986958600475 |
- **PK:** ecoatm_pws$imeidetailid, ecoatm_pws$shipmentdetailid

### offeritem
- **Table:** `ecoatm_pws$offeritem` | **Rows:** 31,440
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 125819314594664980, 125819314592875241, 125819314589848563 |
| offerquantity | integer | Yes | 123, 332, 75 |
| offerprice | integer | Yes | 90, 257, 71 |
| offertotalprice | integer | Yes | 7325, 5510, 1740 |
| salesofferitemstatus | character varying(8) | Yes | Counter, Finalize, Decline |
| counterprice | integer | Yes | 75, 780, 395 |
| counterquantity | integer | Yes | 123, 332, 75 |
| countertotal | integer | Yes | 905, 6000, 970 |
| buyercounterstatus | character varying(7) | Yes | Decline, Accept |
| minpercentage | numeric | Yes | -19.23076923, -54.66101695, -91.30434783 |
| listpercentage | numeric | Yes | -54.66101695, -1.21765601, -43.97905759 |
| changeddate | timestamp without time zone | Yes | 2026-01-05 21:19:03.356, 2025-09-16 20:30:08.641, 2025-09-17 21:34:15.789 |
| system$owner | bigint | Yes | 23925373022147278, 23925373022364361, 23925373021314975 |
| system$changedby | bigint | Yes | 23925373020458622, 9851624184950013, 23925373021011861 |
| finalofferquantity | integer | Yes | 123, 332, 75 |
| finaloffertotalprice | integer | Yes | 7325, 5510, 1740 |
| createddate | timestamp without time zone | Yes | 2025-05-07 21:35:03.952, 2025-06-09 12:15:56.652, 2026-01-12 19:54:40.996 |
| finalofferprice | integer | Yes | 845, 75, 354 |
| sameskuofferavailable | boolean | Yes | true, false |
| quantitycssstyle | character varying(200) | Yes |  |
| offerdrawerstatus | character varying(15) | Yes | Sales_Review, Buyer_Declined, Countered |
| reservedon | timestamp without time zone | Yes | 2025-08-25 17:50:11.537, 2025-08-25 17:50:11.548, 2025-08-25 17:50:11.557 |
| ordersynced | boolean | Yes | true, false |
| reserved | boolean | Yes | true, false |
| validqty | boolean | Yes | true, false |
| shippedprice | integer | Yes | 34000, 5012, 125580 |
| shippedqty | integer | Yes | 330, 90, 16 |
| countercasepricetotal | integer | Yes |  |
| sortorder | integer | Yes | 99, 0 |
- **PK:** id

### offeritem_device
- **Table:** `ecoatm_pws$offeritem_device` | **Rows:** 31,440
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No | 125819314594664980, 125819314592875241, 125819314589848563 |
| ecoatm_pwsmdm$deviceid | bigint | No | 119626865103926100, 119626865102442068, 119626865102136669 |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pwsmdm$deviceid

### offeritem_offer
- **Table:** `ecoatm_pws$offeritem_offer` | **Rows:** 31,440
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No | 125819314594664980, 125819314592875241, 125819314589848563 |
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pws$offerid

### offeritem_buyercode
- **Table:** `ecoatm_pws$offeritem_buyercode` | **Rows:** 31,348
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No | 125819314594664980, 125819314592875241, 125819314589848563 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299767879, 32651097299243972, 32651097299207268 |
- **PK:** ecoatm_pws$offeritemid, ecoatm_buyermanagement$buyercodeid

### offeritem_order
- **Table:** `ecoatm_pws$offeritem_order` | **Rows:** 27,113
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No | 125819314594664980, 125819314592875241, 125819314589848563 |
| ecoatm_pws$orderid | bigint | No | 129197014312356071, 129197014311769277, 129197014311641920 |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pws$orderid

### offeritem_shipmentdetail
- **Table:** `ecoatm_pws$offeritem_shipmentdetail` | **Rows:** 19,685
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$shipmentdetailid | bigint | No | 157625986958253339, 157625986958386788, 157625986958600475 |
| ecoatm_pws$offeritemid | bigint | No | 125819314593593057, 125819314594664980, 125819314593653371 |
- **PK:** ecoatm_pws$shipmentdetailid, ecoatm_pws$offeritemid

### offer
- **Table:** `ecoatm_pws$offer` | **Rows:** 3,068
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
| offerstatus | character varying(16) | Yes | Declined, Sales_Review, Pending_Order |
| offertotalquantity | integer | Yes | 123, 332, 75 |
| offertotalprice | integer | Yes | 34000, 4905, 82460 |
| offersubmissiondate | timestamp without time zone | Yes | 2026-01-19 23:52:44.856, 2025-07-24 15:32:35.892, 2025-11-20 20:28:10.794 |
| updatedate | timestamp without time zone | Yes | 2026-01-19 23:54:39.708, 2025-06-17 21:34:52.196, 2025-09-17 16:45:45.423 |
| createddate | timestamp without time zone | Yes | 2025-10-31 20:06:25.758, 2025-12-22 17:42:29.944, 2025-10-21 16:20:18.893 |
| changeddate | timestamp without time zone | Yes | 2025-11-28 19:19:09.888, 2025-11-21 17:01:30.023, 2025-10-15 20:19:08.72 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373021802811, 23925373021176012 |
| system$owner | bigint | Yes | 23925373022147278, 23925373022364361, 23925373021314975 |
| salesreviewcompletedon | timestamp without time zone | Yes | 2026-02-17 14:23:35.377, 2025-08-19 12:21:33.185, 2026-02-09 20:07:22.124 |
| finaloffertotalqty | integer | Yes | 123, 332, 75 |
| isvalidoffer | boolean | Yes | true, false |
| finaloffersubmittedon | timestamp without time zone | Yes | 2025-06-05 16:43:20.928, 2025-08-15 14:52:45.822, 2025-09-04 15:06:23.981 |
| offeravgprice | numeric | Yes | 472.00000000, 793.00000000, 182.00000000 |
| finaloffertotalsku | integer | Yes | 13, 28, 121 |
| finaloffertotalprice | integer | Yes | 34000, 4905, 82460 |
| counteroffertotalqty | integer | Yes | 90, 16, 26 |
| offerminpercentagevariance | numeric | Yes | -0.69930070, -1.22950820, 1.50943396 |
| counterresponsesubmittedon | timestamp without time zone | Yes | 2025-09-02 18:11:30.969, 2025-07-24 22:34:20.734, 2025-11-20 15:07:12.178 |
| counteroffertotalsku | integer | Yes | 67, 8, 12 |
| offerskucount | integer | Yes | 13, 28, 121 |
| counteroffertotalprice | integer | Yes | 60030, 1860, 4110 |
| counterofferavgprice | numeric | Yes | 302.00000000, 166.00000000, 516.00000000 |
| counterofferminpercentagevariance | numeric | Yes | -1.45695364, -33.55263158, -4.13135593 |
| offerreverteddate | timestamp without time zone | Yes | 2026-02-13 18:44:28.704, 2025-08-13 16:15:08.429, 2025-12-17 18:59:16.409 |
| sameskuoffer | boolean | Yes | true, false |
| offerid | character varying(200) | Yes | 2455925180, 2193925034, 2462125064 |
| offerbeyondsla | boolean | Yes | true, false |
| buyeroffercancelled | boolean | Yes | true, false |
| selleroffercancelled | boolean | Yes | true, false |
| offertype | character varying(17) | Yes | OfferFlow |
| offercancelledon | timestamp without time zone | Yes | 2025-09-09 20:27:03.218, 2026-02-17 14:23:35.377, 2025-10-22 16:46:50.437 |
| allskuswithvalidoffer | boolean | Yes | true |
| secondremindersent | boolean | Yes | false |
| firstremindersent | boolean | Yes | false |
| isuntesteddeviceexist | boolean | Yes | true, false |
| isfunctionaldeviceexist | boolean | Yes | true, false |
| iscaselotsexist | boolean | Yes | false |
| shippedquantity | integer | Yes | 123, 332, 8 |
| orderpackquantity | integer | Yes | 332, 8, 75 |
| canceledquantity | integer | Yes | 10, 15, 23 |
- **PK:** id

### offer_salesrepresentative
- **Table:** `ecoatm_pws$offer_salesrepresentative` | **Rows:** 3,051
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
| ecoatm_buyermanagement$salesrepresentativeid | bigint | No | 142426338215643346, 142426338215592129, 142426338215656106 |
- **PK:** ecoatm_pws$offerid, ecoatm_buyermanagement$salesrepresentativeid

### offer_buyercode
- **Table:** `ecoatm_pws$offer_buyercode` | **Rows:** 3,046
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299767879, 32651097299243972, 32651097299207268 |
- **PK:** ecoatm_pws$offerid, ecoatm_buyermanagement$buyercodeid

### offer_orderstatus
- **Table:** `ecoatm_pws$offer_orderstatus` | **Rows:** 2,964
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
| ecoatm_pws$orderstatusid | bigint | No | 156218612074417344, 156218612074419409, 156218612074417255 |
- **PK:** ecoatm_pws$offerid, ecoatm_pws$orderstatusid

### offersubmittedby_account
- **Table:** `ecoatm_pws$offersubmittedby_account` | **Rows:** 2,964
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464731295198 |
| administration$accountid | bigint | No | 23925373022147278, 23925373022364361, 23925373021314975 |
- **PK:** ecoatm_pws$offerid, administration$accountid

### shipmentdetail
- **Table:** `ecoatm_pws$shipmentdetail` | **Rows:** 2,858
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 157625986958253339, 157625986958386788, 157625986958600475 |
| trackingnumber | character varying(1000) | Yes | 8887 6536 8376, 4809 1154 8340, 3937 1864 1735 |
| trackingurl | character varying(2000) | Yes | https://www.fedex.com/fedex..., https://www.fedex.com/fedex..., https://www.fedex.com/fedex... |
| skucount | integer | Yes | 67, 8, 12 |
| quantity | integer | Yes | 198, 123, 8 |
| createddate | timestamp without time zone | Yes | 2025-12-19 21:04:00.991, 2025-11-26 21:49:13.274, 2025-12-18 14:17:36.875 |
| changeddate | timestamp without time zone | Yes | 2025-12-17 21:49:04.119, 2026-01-30 12:04:03.079, 2025-09-25 20:19:10.009 |
| ecoatm_pws$shipmentdetail_order | bigint | Yes | 129197014311345217, 129197014311640285, 129197014311294042 |
| system$owner | bigint | Yes | 9851624184950013, 23925373020815588 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020815588 |
- **PK:** id

### order
- **Table:** `ecoatm_pws$order` | **Rows:** 2,580
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 129197014312356071, 129197014311769277, 129197014311652113 |
| ordernumber | character varying(200) | Yes | 5002391, 5001699, 5004242 |
| orderline | character varying(200) | Yes | 70458004, 70755001, 67637000 |
| orderdate | timestamp without time zone | Yes | 2026-02-03 00:50:29.832, 2025-10-20 13:08:22.856, 2025-06-17 21:34:52.196 |
| oracleorderstatus | text | Yes |  |
| jsoncontent | text | Yes |  |
| changeddate | timestamp without time zone | Yes | 2025-09-16 20:31:09.385, 2025-11-17 21:19:48.938, 2025-09-29 21:04:20.171 |
| system$owner | bigint | Yes | 23925373021011861, 23925373021176012, 23925373021737821 |
| oraclehttpcode | integer | Yes | 504, 201, 400 |
| system$changedby | bigint | Yes | 23925373021647881, 23925373021916812, 23925373020444954 |
| oraclejsonresponse | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-05-29 20:05:43.447, 2025-06-24 14:45:51.268, 2025-04-24 13:42:03.237 |
| issuccessful | boolean | Yes | true, false |
| shipmethod | character varying(200) | Yes | FedEx Priority Overnight, FedEx Standard Overnight, Ship Outside System |
| shippedtotalsku | integer | Yes | 8, 176, 13 |
| hasshipmentdetails | boolean | Yes | true, false |
| shiptoaddress | character varying(2000) | Yes | 2934 nw 72nd ave, Miami, USA, null, Pompano Beach, null, null, Dublin, null |
| shippedtotalprice | integer | Yes | 22165, 7190, 177960 |
| shippedtotalquantity | integer | Yes | 71, 123, 332 |
| legacyorder | boolean | Yes | true, false |
| shipdate | timestamp without time zone | Yes | 2025-10-28 21:07:44, 2026-01-07 21:07:15, 2025-12-04 21:30:11 |
- **PK:** id

### order_buyercode
- **Table:** `ecoatm_pws$order_buyercode` | **Rows:** 2,570
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$orderid | bigint | No | 129197014312356071, 129197014311769277, 129197014311641920 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299230238, 32651097299204995, 32651097299782159 |
- **PK:** ecoatm_pws$orderid, ecoatm_buyermanagement$buyercodeid

### ordercreatedby_account
- **Table:** `ecoatm_pws$ordercreatedby_account` | **Rows:** 2,480
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$orderid | bigint | No | 129197014312356071, 129197014311769277, 129197014311641920 |
| administration$accountid | bigint | No | 23925373021011861, 23925373021176012, 23925373021010324 |
- **PK:** ecoatm_pws$orderid, administration$accountid

### offer_order
- **Table:** `ecoatm_pws$offer_order` | **Rows:** 2,475
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730439677, 124130464730604919, 124130464730963752 |
| ecoatm_pws$orderid | bigint | No | 129197014312356071, 129197014311769277, 129197014311652113 |
- **PK:** ecoatm_pws$offerid, ecoatm_pws$orderid

### pwsinventorysyncreport
- **Table:** `ecoatm_pws$pwsinventorysyncreport` | **Rows:** 1,949
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 159033361842138399, 159033361842290838, 159033361842270695 |
| reportdate | timestamp without time zone | Yes | 2025-09-20 22:40:00.552, 2025-12-16 18:10:00.602, 2026-01-17 00:10:00.558 |
- **PK:** id

### pwssearch
- **Table:** `ecoatm_pws$pwssearch` | **Rows:** 1,785
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 131730289104275966, 131730289104641402, 131730289102282398 |
| searchstr1 | character varying(200) | Yes | I, 13, s22 |
| searchstr2 | character varying(200) | Yes | phone, mini, 14 |
| searchstr3 | character varying(200) | Yes | 11, max, plus |
| searchstr4 | character varying(200) | Yes | b, max, MAX |
| searchstr5 | character varying(200) | Yes | unlocked |
| searchstr6 | character varying(200) | Yes |  |
| searchstr7 | character varying(200) | Yes |  |
| searchstr8 | character varying(200) | Yes |  |
| searchstr9 | character varying(200) | Yes |  |
| searchstr10 | character varying(200) | Yes |  |
| searchstring | character varying(200) | Yes | apple iphone 11, 17 pro max, 13 |
| createddate | timestamp without time zone | Yes | 2025-05-27 18:54:45.001, 2025-09-16 17:06:28.322, 2025-11-20 13:56:14.508 |
| changeddate | timestamp without time zone | Yes | 2026-01-13 01:39:40.184, 2025-07-25 13:56:20.003, 2025-03-21 13:59:11.393 |
| system$owner | bigint | Yes | 23925373022646096, 23925373022671603, 23925373022364361 |
| system$changedby | bigint | Yes | 23925373022646096, 23925373022671603, 23925373022364361 |
- **PK:** id

### buyeroffer
- **Table:** `ecoatm_pws$buyeroffer` | **Rows:** 1,120
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 117093590313079635, 117093590312926430, 117093590313476757 |
| offerid | bigint | Yes | 1337, 1593, 1318 |
| offertotal | integer | Yes | 515, 5700, 340 |
| createddate | timestamp without time zone | Yes | 2026-02-11 18:26:48.249, 2025-07-08 14:27:07.87, 2025-08-18 15:36:39.855 |
| changeddate | timestamp without time zone | Yes | 2026-01-15 14:43:28.387, 2025-12-22 15:57:14.363, 2026-01-10 00:21:13.888 |
| system$owner | bigint | Yes | 23925373022415609, 23925373021200454, 23925373021161590 |
| system$changedby | bigint | Yes | 23925373021481457, 23925373021750780, 23925373020688027 |
| offerstatus | character varying(16) | Yes |  |
| offerquantity | integer | Yes | 60, 26, 0 |
| offerskus | integer | Yes | 0, 1, 4 |
| isexceedingqty | boolean | Yes | true, false |
| cssclass | character varying(200) | Yes | pws-font-animation,  |
| submitoffer | boolean | Yes | true, false |
| isfunctionaldevicesexist | boolean | Yes | true, false |
| iscaselotsexist | boolean | Yes | false |
| isuntesteddeviceexist | boolean | Yes | false |
- **PK:** id

### salesreviewcompletedby_account
- **Table:** `ecoatm_pws$salesreviewcompletedby_account` | **Rows:** 808
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464731053970, 124130464731422439, 124130464731641266 |
| administration$accountid | bigint | No | 23925373021391862, 23925373020418781, 23925373020457621 |
- **PK:** ecoatm_pws$offerid, administration$accountid

### buyeroffer_buyercode
- **Table:** `ecoatm_pws$buyeroffer_buyercode` | **Rows:** 530
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$buyerofferid | bigint | No | 117093590312747229, 117093590312376194, 117093590312311748 |
| ecoatm_buyermanagement$buyercodeid | bigint | No | 32651097299795471, 32651097299818824, 32651097299243972 |
- **PK:** ecoatm_pws$buyerofferid, ecoatm_buyermanagement$buyercodeid

### buyerofferitem_caselot
- **Table:** `ecoatm_pws$buyerofferitem_caselot` | **Rows:** 420
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$buyerofferitemid | bigint | No | 120189815265847132, 120189815265741556, 120189815265785306 |
| ecoatm_pwsmdm$caselotid | bigint | No | 172262685746922171, 172262685746922834, 172262685746923879 |
- **PK:** ecoatm_pws$buyerofferitemid, ecoatm_pwsmdm$caselotid

### counteroffersubmittedby_account
- **Table:** `ecoatm_pws$counteroffersubmittedby_account` | **Rows:** 405
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464731641266, 124130464731643588, 124130464730257477 |
| administration$accountid | bigint | No | 23925373021011861, 23925373021802811, 23925373021176012 |
- **PK:** ecoatm_pws$offerid, administration$accountid

### offer_cancelledby
- **Table:** `ecoatm_pws$offer_cancelledby` | **Rows:** 389
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464731095573, 124130464731295198, 124130464730270155 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373021200579, 23925373021010324, 23925373021737821 |
- **PK:** ecoatm_pws$offerid, ecoatm_usermanagement$ecoatmdirectuserid

### pwsuserpersonalization
- **Table:** `ecoatm_pws$pwsuserpersonalization` | **Rows:** 337
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 123004564824067625, 123004564824773494, 123004564824495030 |
| datagrid2personalization | text | Yes |  |
| datagrid2personalizationrevieworder | text | Yes |  |
| enableayyy | boolean | Yes | true, false |
| enablecaselots | boolean | Yes | false |
- **PK:** id

### pwsuserpersonalization_ecoatmdirectuser_order
- **Table:** `ecoatm_pws$pwsuserpersonalization_ecoatmdirectuser_order` | **Rows:** 268
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$pwsuserpersonalizationid | bigint | No | 123004564824067625, 123004564824773494, 123004564824495030 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373022646096, 23925373022671603, 23925373022364361 |
- **PK:** ecoatm_pws$pwsuserpersonalizationid, ecoatm_usermanagement$ecoatmdirectuserid

### offerdataexport_offerexceldocument
- **Table:** `ecoatm_pws$offerdataexport_offerexceldocument` | **Rows:** 254
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerdataexportid | bigint | No | 146648462866279230, 146648462866253244, 146648462866301922 |
| ecoatm_pws$offerexceldocumentid | bigint | No | 146366987889566885, 146366987889579865, 146366987889554119 |
- **PK:** ecoatm_pws$offerdataexportid, ecoatm_pws$offerexceldocumentid

### offerrevertedby_account
- **Table:** `ecoatm_pws$offerrevertedby_account` | **Rows:** 109
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offerid | bigint | No | 124130464730413768, 124130464731093552, 124130464730871959 |
| administration$accountid | bigint | No | 23925373020739037, 23925373021391862, 23925373020713688 |
- **PK:** ecoatm_pws$offerid, administration$accountid

### offeritem_offerdetailsexport
- **Table:** `ecoatm_pws$offeritem_offerdetailsexport` | **Rows:** 43
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No | 125819314595827962, 125819314595825386, 125819314595827430 |
| ecoatm_pws$offerdetailsexportid | bigint | No | 146085512913829005, 146085512912869079 |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pws$offerdetailsexportid

### offeritemdataexport
- **Table:** `ecoatm_pws$offeritemdataexport` | **Rows:** 22
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 150307637563491570, 150307637563492965, 150307637563492139 |
| offerdate | character varying(200) | Yes | 06/18/2025, 07/10/2025, 04/29/2025 |
| status | character varying(200) | Yes | Countered, Seller_Declined, Ordered |
| customer | character varying(200) | Yes | Celebrium Technologies Limited, Skyminimart, Fusion Celular SRL |
| qty | integer | Yes | 10, 5, 3 |
| listprice | character varying(200) | Yes | $250 |
| offerprice | character varying(200) | Yes | $260, $190, $230 |
| color | character varying(200) | Yes | Gold, Midnight Green, Space Gray |
| sku | character varying(200) | Yes | PWS226410264440, PWS947283065329, PWS111162992028 |
- **PK:** id

### offeritemdataexport_offeritemexceldocument
- **Table:** `ecoatm_pws$offeritemdataexport_offeritemexceldocument` | **Rows:** 22
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemdataexportid | bigint | No | 150307637563491570, 150307637563492965, 150307637563492139 |
| ecoatm_pws$offeritemexceldocumentid | bigint | No | 149744687610069140 |
- **PK:** ecoatm_pws$offeritemdataexportid, ecoatm_pws$offeritemexceldocumentid

### orderstatus
- **Table:** `ecoatm_pws$orderstatus` | **Rows:** 21
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 156218612074417736, 156218612074417893, 156218612074417584 |
| systemstatus | character varying(200) | Yes | Partially Loaded, Pick Complete, Buyer Acceptance |
| internalstatustext | character varying(200) | Yes | Partially Loaded, Pick Complete, Buyer Acceptance |
| externalstatustext | character varying(200) | Yes | Order Cancelled, Awaiting Carrier Pickup, In Process |
| interstatushexcode | character varying(33) | Yes | pws_bgcolor_peach, pws_bgcolor_brightyellow, pws_bgcolor_lightgreen |
| externalstatushexcode | character varying(33) | Yes | pws_bgcolor_seagreen, pws_bgcolor_red, pws_bgcolor_darkorange |
| systemstatusdescription | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-08-18 20:29:16.744, 2025-08-18 20:29:16.74, 2025-08-18 20:29:16.745 |
| changeddate | timestamp without time zone | Yes | 2025-08-18 20:29:16.747, 2025-08-21 21:07:42.446, 2025-08-21 21:07:38.434 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### managefiledocument
- **Table:** `ecoatm_pws$managefiledocument` | **Rows:** 13
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 124411939707070139, 124411939706148680, 124411939706353541 |
| message | character varying(500) | Yes |  |
| processpercentage | integer | Yes | 0 |
| hasprocessfailed | boolean | Yes | false |
| detailledmessage | text | Yes |  |
- **PK:** id

### pwsuserpersonalization_ecoatmdirectuser_pricing
- **Table:** `ecoatm_pws$pwsuserpersonalization_ecoatmdirectuser_pricing` | **Rows:** 12
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$pwsuserpersonalizationid | bigint | No | 123004564823568548, 123004564822557268, 123004564823457103 |
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373020764769, 23925373020713794, 23925373020815588 |
- **PK:** ecoatm_pws$pwsuserpersonalizationid, ecoatm_usermanagement$ecoatmdirectuserid

### navigationmenu
- **Table:** `ecoatm_pws$navigationmenu` | **Rows:** 10
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 177047760351028358, 177047760351041320, 177047760351002960 |
| name | character varying(200) | Yes | Counters, Grading, Shop |
| isactive | boolean | Yes | true |
| order | integer | Yes | 4, 6, 3 |
| microflowname | character varying(200) | Yes | EcoATM_PWS.NAV_DynamicMenu_..., EcoATM_PWS.NAV_DynamicMenu_..., EcoATM_PWS.NAV_DynamicMenu_... |
| iconcssclass | character varying(200) | Yes | FA_Light300 FA_Light300-f571, FA_Light300 FA_Light300-f4c0, FA_Light300 FA_Light300-f05a |
| usergroup | character varying(5) | Yes | Sales, Buyer |
| loadingmessage | character varying(200) | Yes | Loading Pricing, Loading FAQ's, Loading Inventory |
| createddate | timestamp without time zone | Yes | 2026-01-07 21:28:55.38, 2026-01-07 21:27:33.224, 2026-01-07 21:25:37.037 |
| changeddate | timestamp without time zone | Yes | 2026-01-07 21:26:53.418, 2026-01-07 21:29:39.589, 2026-01-07 21:28:35.64 |
| system$owner | bigint | Yes | 23925373020815588 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### offerexceldocument
- **Table:** `ecoatm_pws$offerexceldocument` | **Rows:** 5
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 146366987889541308, 146366987889554119, 146366987889566885 |
- **PK:** id

### maintenancemode
- **Table:** `ecoatm_pws$maintenancemode` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 155655662120993017 |
| isenabled | boolean | Yes | false |
| bannerstarttime | timestamp without time zone | Yes | 2025-08-20 16:00:00 |
| starttime | timestamp without time zone | Yes | 2025-08-21 21:00:00 |
| endtime | timestamp without time zone | Yes | 2025-08-25 10:00:00 |
| bannertitle | text | Yes |  |
| bannermessage | text | Yes |  |
| pagetitle | text | Yes |  |
| pageheader | text | Yes |  |
| pagemessage | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-08-19 17:38:48.304 |
| changeddate | timestamp without time zone | Yes | 2025-08-23 14:03:03.592 |
| system$owner | bigint | Yes | 23925373020418781 |
| system$changedby | bigint | Yes | 23925373020431548 |
- **PK:** id

### mdmfuturepricehelper
- **Table:** `ecoatm_pws$mdmfuturepricehelper` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 120471290032160918 |
| futurepwspricedate | timestamp without time zone | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-01-30 21:35:34.236 |
| changeddate | timestamp without time zone | Yes | 2025-06-14 07:00:02.084 |
| system$owner | bigint | Yes | 23925373020431548 |
| system$changedby | bigint | Yes | 23925373021014749 |
- **PK:** id

### offeritemexceldocument
- **Table:** `ecoatm_pws$offeritemexceldocument` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 149744687610069140 |
- **PK:** id

### pricingupdatefile
- **Table:** `ecoatm_pws$pricingupdatefile` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 119908340078867655 |
- **PK:** id

### pricingupdatefile_mdmfuturepricehelper
- **Table:** `ecoatm_pws$pricingupdatefile_mdmfuturepricehelper` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$pricingupdatefileid | bigint | No | 119908340078867655 |
| ecoatm_pws$mdmfuturepricehelperid | bigint | No | 120471290032160918 |
- **PK:** ecoatm_pws$pricingupdatefileid, ecoatm_pws$mdmfuturepricehelperid

### pwsconstants
- **Table:** `ecoatm_pws$pwsconstants` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 126945214496506108 |
| system$changedby | bigint | Yes | 23925373020418781 |
| changeddate | timestamp without time zone | Yes | 2025-05-07 20:47:29.115 |
| createddate | timestamp without time zone | Yes | 2025-05-07 20:47:29.114 |
| system$owner | bigint | Yes | 23925373020418781 |
| sladays | integer | Yes | 2 |
| salesemail | character varying(200) | Yes | wholesalesupport@ecoatm.com |
| sendsecondreminder | boolean | Yes | true |
| sendfirstreminder | boolean | Yes | true |
| hoursfirstcounterreminderemail | integer | Yes | 24 |
| hourssecondcounterreminderemail | integer | Yes | 48 |
- **PK:** id

### imeidetail_orderdetailsexportbydevice
- **Table:** `ecoatm_pws$imeidetail_orderdetailsexportbydevice` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$imeidetailid | bigint | No |  |
| ecoatm_pws$orderdetailsexportbydeviceid | bigint | No |  |
- **PK:** ecoatm_pws$imeidetailid, ecoatm_pws$orderdetailsexportbydeviceid

### offerdetailsexport
- **Table:** `ecoatm_pws$offerdetailsexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### offeritem_caselot
- **Table:** `ecoatm_pws$offeritem_caselot` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No |  |
| ecoatm_pwsmdm$caselotid | bigint | No |  |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pwsmdm$caselotid

### offeritem_devicesftemp
- **Table:** `ecoatm_pws$offeritem_devicesftemp` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No |  |
| ecoatm_pwsmdm$devicesftempid | bigint | No |  |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pwsmdm$devicesftempid

### offeritem_orderdetailsexport
- **Table:** `ecoatm_pws$offeritem_orderdetailsexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No |  |
| ecoatm_pws$orderdetailsexportid | bigint | No |  |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pws$orderdetailsexportid

### offeritem_orderstatus
- **Table:** `ecoatm_pws$offeritem_orderstatus` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$offeritemid | bigint | No |  |
| ecoatm_pws$orderstatusid | bigint | No |  |
- **PK:** ecoatm_pws$offeritemid, ecoatm_pws$orderstatusid

### orderdataexport
- **Table:** `ecoatm_pws$orderdataexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| devicecode | character varying(200) | Yes |  |
| category | character varying(200) | Yes |  |
| model | character varying(200) | Yes |  |
| carrier | character varying(200) | Yes |  |
| capacity | character varying(200) | Yes |  |
| color | character varying(200) | Yes |  |
| grade | character varying(200) | Yes |  |
| availableqty | character varying(200) | Yes |  |
| quantity | integer | Yes |  |
| brand | character varying(200) | Yes |  |
| offerprice | integer | Yes |  |
| totalprice | bigint | Yes |  |
| listprice | integer | Yes |  |
| caseprice | numeric | Yes |  |
| sheetname | character varying(200) | Yes |  |
| casepackqty | integer | Yes |  |
| caseoffer | integer | Yes |  |
- **PK:** id

### orderdataexport_caselots
- **Table:** `ecoatm_pws$orderdataexport_caselots` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$orderdataexportid | bigint | No |  |
| ecoatm_pws$orderexceldocumentid | bigint | No |  |
- **PK:** ecoatm_pws$orderdataexportid, ecoatm_pws$orderexceldocumentid

### orderdataexport_devices
- **Table:** `ecoatm_pws$orderdataexport_devices` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$orderdataexportid | bigint | No |  |
| ecoatm_pws$orderexceldocumentid | bigint | No |  |
- **PK:** ecoatm_pws$orderdataexportid, ecoatm_pws$orderexceldocumentid

### orderdetailsexport
- **Table:** `ecoatm_pws$orderdetailsexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### orderdetailsexportbydevice
- **Table:** `ecoatm_pws$orderdetailsexportbydevice` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### orderexceldocument
- **Table:** `ecoatm_pws$orderexceldocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### orderhistoryexport
- **Table:** `ecoatm_pws$orderhistoryexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### orderhistoydownload
- **Table:** `ecoatm_pws$orderhistoydownload` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| ordernumber | character varying(200) | Yes |  |
| offerdate | timestamp without time zone | Yes |  |
| orderdate | timestamp without time zone | Yes |  |
| orderstatus | character varying(200) | Yes |  |
| skucount | integer | Yes |  |
| totalquantity | integer | Yes |  |
| totalprice | integer | Yes |  |
| buyer | character varying(200) | Yes |  |
| buyercode | character varying(200) | Yes |  |
| company | character varying(200) | Yes |  |
| lastupdatedate | timestamp without time zone | Yes |  |
| offerordertype | character varying(200) | Yes |  |
| shipmethod | character varying(200) | Yes |  |
| shipdate | timestamp without time zone | Yes |  |
- **PK:** id

### orderhistoydownload_orderhistoryexport
- **Table:** `ecoatm_pws$orderhistoydownload_orderhistoryexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$orderhistoydownloadid | bigint | No |  |
| ecoatm_pws$orderhistoryexportid | bigint | No |  |
- **PK:** ecoatm_pws$orderhistoydownloadid, ecoatm_pws$orderhistoryexportid

### pricingdataexport
- **Table:** `ecoatm_pws$pricingdataexport` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| sku | character varying(200) | Yes |  |
| model | character varying(200) | Yes |  |
| carrier | character varying(200) | Yes |  |
| capacity | character varying(200) | Yes |  |
| color | character varying(200) | Yes |  |
| grade | character varying(200) | Yes |  |
| lookup | text | Yes |  |
| currentlistprice | character varying(200) | Yes |  |
| newlistprice | character varying(200) | Yes |  |
| currentminprice | character varying(200) | Yes |  |
| newminprice | character varying(200) | Yes |  |
| skustatus | character varying(200) | Yes |  |
| brand | character varying(200) | Yes |  |
| category | character varying(200) | Yes |  |
- **PK:** id

### pricingdataexport_pricingexceldocument
- **Table:** `ecoatm_pws$pricingdataexport_pricingexceldocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$pricingdataexportid | bigint | No |  |
| ecoatm_pws$pricingexceldocumentid | bigint | No |  |
- **PK:** ecoatm_pws$pricingdataexportid, ecoatm_pws$pricingexceldocumentid

### pricingexceldocument
- **Table:** `ecoatm_pws$pricingexceldocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### pwssearch_session
- **Table:** `ecoatm_pws$pwssearch_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$pwssearchid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** ecoatm_pws$pwssearchid, system$sessionid

### skusyncdetail_device
- **Table:** `ecoatm_pws$skusyncdetail_device` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_pws$skusyncdetailid | bigint | No |  |
| ecoatm_pwsmdm$deviceid | bigint | No |  |
- **PK:** ecoatm_pws$skusyncdetailid, ecoatm_pwsmdm$deviceid

---
