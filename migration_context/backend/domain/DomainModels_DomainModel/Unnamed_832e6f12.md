# Domain Model

## Entities

### 📦 DATotalAggregate
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALAVAILABLEQTY` | LongAttribute | - | 0 | - |
| `TOTALSOLD` | LongAttribute | - | 0 | - |
| `TOTALREVENUE` | DecimalAttribute | - | 0 | - |
| `TOTALMARGIN` | DecimalAttribute | - | 0 | - |
| `MARGINPERCENTAGE` | DecimalAttribute | - | 0 | - |
| `AVERAGESELLINGPRICE` | DecimalAttribute | - | 0 | - |
| `AVERAGEPURCHASEPRICE` | DecimalAttribute | - | 0 | - |

### 📦 DAUploadTime
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | StringAttribute | - | - | - |

### 📦 DAByBuyer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BUYERCODE` | StringAttribute | - | - | - |
| `PRODUCTID` | StringAttribute | - | - | - |
| `GRADE` | StringAttribute | - | - | - |
| `BID` | DecimalAttribute | - | 0 | - |
| `QUANTITY_ALLOCATED` | LongAttribute | - | 0 | - |

### 📦 MENDIX_DEV_UPSERT_BUYER_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | LongAttribute | - | 0 | - |

### 📦 UpsertBuerData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_BUYER_DATA` | StringAttribute | - | - | - |

### 📦 MasterDeviceInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ECOATM_CODE` | LongAttribute | - | 0 | - |
| `DEVICE_ID` | StringAttribute | 100 | - | - |
| `NAME` | StringAttribute | 512 | - | - |
| `DEVICE_BRAND` | StringAttribute | 255 | - | - |
| `DEVICE_CATEGORY` | StringAttribute | 255 | - | - |
| `DEVICE_FAMILY` | StringAttribute | 255 | - | - |
| `DEVICE_CARRIER` | StringAttribute | 255 | - | - |
| `CARRIER_DISPLAY_NAME` | StringAttribute | 255 | - | - |
| `CATEGORY_DISPLAY_NAME` | StringAttribute | 100 | - | - |
| `DEVICE_MODEL` | StringAttribute | 30 | - | - |
| `DESCRIPTION` | StringAttribute | 240 | - | - |
| `RELEASE_DATE` | DateTimeAttribute | - | - | - |
| `DB_CREATE_DATE` | DateTimeAttribute | - | - | - |
| `DB_UPDATE_DATE` | DateTimeAttribute | - | - | - |
| `CREATED_AT` | DateTimeAttribute | - | - | - |

### 📦 AgreegateInventoryReponse
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `JSON_OUTPUT` | StringAttribute | - | - | - |

### 📦 AggregatedInventoryTotals
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | StringAttribute | - | - | - |
| `DWAVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWAVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `DWAVGMARGIN` | DecimalAttribute | - | 0 | - |
| `DWTOTALQUANTITY` | LongAttribute | - | 0 | - |
| `NDWAVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `NDWAVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `NDWAVGMARGIN` | DecimalAttribute | - | 0 | - |
| `NDWTOTALQUANTITY` | LongAttribute | - | 0 | - |

### 📦 MaxUploadTimeAggreegatedInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | StringAttribute | 200 | - | - |

### 📦 MaxUploadTime
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | DateTimeAttribute | - | - | - |

### 📦 Master_Inventory_List_Snapshot
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | StringAttribute | 200 | - | - |

### 📦 AggreegatedInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ECOID` | StringAttribute | - | - | - |
| `MG` | StringAttribute | - | - | - |
| `DATAWIPE` | StringAttribute | - | - | - |
| `MAXUPLOADTIME` | StringAttribute | - | - | - |
| `AVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `AVGMARGIN` | DecimalAttribute | - | 0 | - |
| `AVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALQUANTITY` | LongAttribute | - | 0 | - |
| `DWAVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `DWAVGMARGIN` | DecimalAttribute | - | 0 | - |
| `DWAVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALQUANTITY` | LongAttribute | - | 0 | - |
| `NAME` | StringAttribute | 512 | - | - |
| `MODEL` | StringAttribute | 30 | - | - |
| `BRAND` | StringAttribute | 255 | - | - |
| `CARRIER` | StringAttribute | 255 | - | - |
| `CREATED_AT` | DateTimeAttribute | - | - | - |
| `CATEGORY` | StringAttribute | 255 | - | - |

### 📦 AggreegateInventoryWeekly
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `AVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `AVGMARGIN` | DecimalAttribute | - | 0 | - |
| `AVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALQUANTITY` | LongAttribute | - | 0 | - |
| `DWAVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `DWAVGMARGIN` | DecimalAttribute | - | 0 | - |
| `DWAVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALQUANTITY` | LongAttribute | - | 0 | - |
| `MAXUPLOADTIME` | StringAttribute | - | - | - |
| `AUCTIONWEEK` | LongAttribute | - | 0 | - |
| `AUCTIONYEAR` | LongAttribute | - | 0 | - |

### 📦 AUCTIONS_UPSERT_WEEK_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | LongAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_WEEK_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_WEEK_DATA` | StringAttribute | - | - | - |

### 📦 VW_SALE_ORDER_PO
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PACKOUT_YEAR` | LongAttribute | - | 0 | - |
| `PACKOUT_WEEK_NUM` | LongAttribute | - | 0 | - |
| `ECOATM_CODE` | LongAttribute | - | 0 | - |
| `GRADE` | StringAttribute | - | - | - |
| `BUYER_CODE` | StringAttribute | 50 | - | - |
| `SHIPPED_QUANTITY` | LongAttribute | - | 0 | - |
| `UNIT_SELLING_PRICE` | DecimalAttribute | - | 0 | - |

### 📦 EBPrice
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BUYER_CODE` | StringAttribute | 30 | - | - |
| `BID_WEEK_ENDING_DATE` | StringAttribute | 200 | - | - |
| `ECOATM_CODE` | LongAttribute | - | 0 | - |
| `DEVICE_BRAND` | StringAttribute | 255 | - | - |
| `DEVICE_MODEL` | StringAttribute | 30 | - | - |
| `GRADE` | StringAttribute | 50 | - | - |
| `MERGEDGRADE` | StringAttribute | 50 | - | - |
| `BID` | DecimalAttribute | - | 0 | - |
| `MIN_BID` | DecimalAttribute | - | 0 | - |
| `MIN_PRICE_WEEK_DATE` | StringAttribute | 200 | - | - |

### 📦 EBPriceMaxUploadTime
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MAXUPLOADTIME` | DateTimeAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_PURCHASE_ORDER
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | LongAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_PURCHASE_ORDER
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_PURCHASE_ORDER` | StringAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_BUYER_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | LongAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_BUYER_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_BUYER_DATA` | StringAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_AUCTION_AND_SCHEDULE
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | LongAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_AUCTION_AND_SCHEDULE
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_AUCTION_AND_SCHEDULE` | StringAttribute | - | - | - |

### 📦 BuyerSummaryReport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BUYER_CODE` | StringAttribute | - | - | - |
| `COMPANY_NAME` | StringAttribute | - | - | - |
| `BUDGET` | DecimalAttribute | - | - | - |
| `CURRENTSALESQTY` | LongAttribute | - | 0 | - |
| `CURRENTSPEND` | DecimalAttribute | - | 0 | - |
| `CURRENTECOATMGRADEDETAILS` | StringAttribute | - | - | - |
| `PREVIOUSSALESQTY` | LongAttribute | - | 0 | - |
| `PREVIOUSSPEND` | DecimalAttribute | - | 0 | - |
| `PREVIOUSECOATMGRADEDETAILS` | StringAttribute | - | - | - |
| `ROWTYPE` | StringAttribute | 7 | - | - |

### 📦 DAByWeek
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PRODUCTID` | LongAttribute | - | 0 | - |
| `BRAND` | StringAttribute | - | - | - |
| `MODELNAME` | StringAttribute | - | - | - |
| `GRADE` | StringAttribute | - | - | - |
| `TOTALAVAILABLEQTY` | LongAttribute | - | 0 | - |
| `AVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `AVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `TOTALDATAWIPEQTY` | LongAttribute | - | 0 | - |
| `AVGDATAWIPEPAYOUT` | DecimalAttribute | - | 0 | - |
| `AVGDATAWIPETARGETPRICE` | DecimalAttribute | - | 0 | - |
| `TOTALNONDATAWIPEQTY` | LongAttribute | - | 0 | - |
| `AVGNONDATAWIPEPAYOUT` | DecimalAttribute | - | 0 | - |
| `AVGNONDATAWIPETARGETPRICE` | DecimalAttribute | - | 0 | - |
| `TOTALSOLD` | LongAttribute | - | 0 | - |
| `AVGSALESPRICE` | DecimalAttribute | - | 0 | - |
| `TOTALREVENUE` | DecimalAttribute | - | 0 | - |
| `TOTALMARGIN` | DecimalAttribute | - | 0 | - |
| `MARGINPERCENTAGE` | DecimalAttribute | - | 0 | - |
| `EB` | DecimalAttribute | - | 0 | - |
| `BUYERJSON` | StringAttribute | - | - | - |
| `SELLINGSTATUS` | StringAttribute | - | - | - |
| `UPLOADTIME` | StringAttribute | - | - | - |
| `ROWTYPE` | StringAttribute | 6 | - | - |

### 📦 AggregatedInventoryLegacy
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ECOID` | LongAttribute | - | 0 | - |
| `DEVICE_ID` | StringAttribute | 100 | - | - |
| `MG` | StringAttribute | - | - | - |
| `DATAWIPE` | StringAttribute | 2 | - | - |
| `MAXUPLOADTIME` | StringAttribute | - | - | - |
| `AVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `AVGMARGIN` | DecimalAttribute | - | 0 | - |
| `AVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `TOTALQUANTITY` | LongAttribute | - | 0 | - |
| `DWAVGTARGETPRICE` | DecimalAttribute | - | 0 | - |
| `DWAVGMARGIN` | DecimalAttribute | - | 0 | - |
| `DWAVGPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALPAYOUT` | DecimalAttribute | - | 0 | - |
| `DWTOTALQUANTITY` | LongAttribute | - | 0 | - |
| `NAME` | StringAttribute | 512 | - | - |
| `MODEL` | StringAttribute | 30 | - | - |
| `BRAND` | StringAttribute | 255 | - | - |
| `CARRIER` | StringAttribute | 255 | - | - |
| `CREATED_AT` | DateTimeAttribute | - | - | - |
| `CATEGORY` | StringAttribute | 255 | - | - |
| `ROUND1TARGETPRICE` | DecimalAttribute | - | 0 | - |
| `ROUND1TARGETPRICE_DW` | DecimalAttribute | - | 0 | - |

### 📦 AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ECOID` | StringAttribute | - | - | - |
| `GRADE` | StringAttribute | - | - | - |
| `BRAND` | StringAttribute | - | - | - |
| `PARTNAME` | StringAttribute | - | - | - |
| `EBCOUNT` | LongAttribute | - | 0 | - |
| `DECISION` | StringAttribute | - | - | - |
| `NEWEBPRICE` | DecimalAttribute | - | 0 | - |
| `CURRENTEBPRICE` | DecimalAttribute | - | 0 | - |
| `CONSECUTIVEEB` | StringAttribute | - | - | - |
| `WEEKOFPRIORSOLD` | LongAttribute | - | 0 | - |
| `DELTALASTAWARDEDMIN` | DecimalAttribute | - | 0 | - |
| `DELTALASTSOLD5WEEKS` | DecimalAttribute | - | 0 | - |
| `COHORTDELTA` | DecimalAttribute | - | 0 | - |
| `COHORTPRICE` | DecimalAttribute | - | 0 | - |
| `CURRENTWEEKDELTA` | DecimalAttribute | - | 0 | - |
| `PREV2WEEKBID` | DecimalAttribute | - | 0 | - |
| `PREVWEEKBID` | DecimalAttribute | - | 0 | - |
| `CURRENTWEEKBID` | DecimalAttribute | - | 0 | - |
| `PREVWEEKDELTA` | DecimalAttribute | - | 0 | - |
| `LASTAWARDEDMIN` | DecimalAttribute | - | 0 | - |
| `COUNTOFPREVSOLDBID` | DecimalAttribute | - | 0 | - |
| `CURRENTWEEKCOUNT` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Integration.Admin, EcoATM_Integration.SalesLeader, EcoATM_Integration.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AUCTIONS_GENERATE_EB_CALIBRATION_REPORT_SQL
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | IntegerAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_GENERATE_EB_CALIBRATION_REPORT_SQL
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `GENERATE_EB_CALIBRATION_REPORT_SQL` | StringAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_BUYERUSER_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | IntegerAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_BUYERUSER_DATA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_BUYERUSER_DATA` | StringAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_USER_DATA_1
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | IntegerAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_USER_DATA_1
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_USER_DATA_1` | StringAttribute | - | - | - |

### 📦 AUCTIONS_UPSERT_BUYER_BIDS_JSON
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | IntegerAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_UPSERT_BUYER_BIDS_JSON
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UPSERT_BUYER_BIDS_JSON` | StringAttribute | - | - | - |

### 📦 AUCTIONS_SP_UPSERT_PWS_DEVICE_INVENTORY
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `no_of_rows_affected` | IntegerAttribute | - | 0 | - |

### 📦 ResultSet_AUCTIONS_SP_UPSERT_PWS_DEVICE_INVENTORY
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SP_UPSERT_PWS_DEVICE_INVENTORY` | StringAttribute | - | - | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `UpsertBuerData_MENDIX_DEV_UPSERT_BUYER_DATA` | 192d5740-8b76-48ea-9bca-7a5706315d84 | 8059a828-8290-4eda-828d-129190fb432f | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_WEEK_DATA_AUCTIONS_UPSERT_WEEK_DATA` | 713cfe5e-13e5-43bc-afce-0a6f266e6fd3 | dbaec13e-760d-455b-aeeb-99fb59299b2c | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_PURCHASE_ORDER_AUCTIONS_UPSERT_PURCHASE_ORDER` | 6cd28de8-3044-45b5-9a89-886a83517845 | 894d06fe-a475-47fc-9db3-db210b8f87cc | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_BUYER_DATA_AUCTIONS_UPSERT_BUYER_DATA` | d531f18b-de4a-4046-b63d-cd7dc359cc93 | 6551d46d-ceb8-4601-9ac8-366c3e4e0217 | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_AUCTION_AND_SCHEDULE_AUCTIONS_UPSERT_AUCTION_AND_SCHEDULE` | 4a48b7af-0589-406d-b0ae-ef407cc86f22 | e52cacdd-4953-4bc2-8b9b-8faf6168eed2 | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_GENERATE_EB_CALIBRATION_REPORT_SQL_AUCTIONS_GENERATE_EB_CALIBRATION_REPORT_SQL` | 71a516f3-f971-48c2-966c-11ef306fa108 | 44f2c659-5d6d-422d-b3b6-97e33ea4dc6c | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_BUYERUSER_DATA_AUCTIONS_UPSERT_BUYERUSER_DATA` | 5052d47d-aaa3-4cf6-a577-bb363fcffc2f | cdbcbbb9-94c4-41c6-8e1b-385c05a4f800 | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_USER_DATA_AUCTIONS_UPSERT_USER_DATA` | d5987b96-7db4-47b3-b6bc-c898e96e18b7 | 0c4ebb8f-af58-4820-ae76-b38eb3703704 | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_UPSERT_BUYER_BID_AUCTIONS_UPSERT_BUYER_BID` | 71af5862-8b36-4a4c-9b36-cefcc35905b6 | dbe8ecc0-2b26-4cb7-977d-8e36d1fa616e | Reference | Default | DeleteMeButKeepReferences |
| `ResultSet_AUCTIONS_SP_UPSERT_PWS_DEVICE_INVENTORY_AUCTIONS_SP_UPSERT_PWS_DEVICE_INVENTORY` | 80138c1e-ddc3-4a4d-8a9e-0887bcb2ce1e | 750abef4-a654-4a35-9be1-932189e8ea2b | Reference | Default | DeleteMeButKeepReferences |
