# Microflow Detailed Specification: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing_PB

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **JavaCallAction**
3. **LogMessage**
4. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
5. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active' ) ]` (Result: **$ActiveBuyerCodeList**)**
6. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ] ]` (Result: **$SubmittedBuyerCodeList**)**
7. **List Operation: **Subtract** on **$undefined** (Result: **$NotSubmittedActiveBuyerCodeList**)**
8. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
9. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
10. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_DW**)**
11. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_ALL**)**
12. **Create Variable **$FileCreated_DW** = `false`**
13. **Create Variable **$FileCreated_NonDW** = `false`**
14. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_DW**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocDW_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
15. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_ALL**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocAll_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
16. **CreateList**
17. **CreateList**
18. **CreateList**
19. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week ]` (Result: **$AggregatedInventoryList_ALL**)**
20. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/DWTotalQuantity > 0` (Result: **$DWAggregatedInventoryList**)**
21. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week=$Week] [DWTotalQuantity > 0]` (Result: **$AgregatedInventoryList_DW**)**
22. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$NotSubmittedActiveBuyerCodeList**
   │ 1. 🔀 **DECISION:** `$IteratorBuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$FileCreated_DW = false`
   │          ➔ **If [true]:**
   │             1. **LogMessage**
   │             2. **Update Variable **$FileCreated_DW** = `true`**
   │             3. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
   │             4. 🔄 **LOOP:** For each **$IteratorAllBidDownload_DW** in **$DWAllBidDownloadList**
   │                │ 1. **Update **$IteratorAllBidDownload_DW**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_DW`**
   │                └─ **End Loop**
   │             5. **Commit/Save **$DWAllBidDownloadList** to Database**
   │             6. **JavaCallAction**
   │             7. **LogMessage**
   │             8. **Update **$ExcelOutput_DW**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             9. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess2**)**
   │             10. 🔀 **DECISION:** `$UploadSuccess2`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound_1**)**
   │                   2. 🔀 **DECISION:** `$BidRound_1 != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound_1**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. **LogMessage**
   │             2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_SetBuyerCode****
   │             3. **JavaCallAction**
   │             4. **LogMessage**
   │             5. **Update **$ExcelOutput_DW**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess2**)**
   │             7. 🔀 **DECISION:** `$UploadSuccess2`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound_1**)**
   │                   2. 🔀 **DECISION:** `$BidRound_1 != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound_1**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$FileCreated_NonDW = false`
   │          ➔ **If [true]:**
   │             1. **LogMessage**
   │             2. **Update Variable **$FileCreated_NonDW** = `true`**
   │             3. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
   │             4. 🔄 **LOOP:** For each **$IteratorAllBidDownload** in **$AllBidDownloadList**
   │                │ 1. **Update **$IteratorAllBidDownload**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_ALL`**
   │                └─ **End Loop**
   │             5. **Commit/Save **$AllBidDownloadList** to Database**
   │             6. **JavaCallAction**
   │             7. **LogMessage**
   │             8. **Update **$ExcelOutput_ALL**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             9. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess**)**
   │             10. 🔀 **DECISION:** `$UploadSuccess`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound**)**
   │                   2. 🔀 **DECISION:** `$BidRound != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. **LogMessage**
   │             2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_SetBuyerCode****
   │             3. **JavaCallAction**
   │             4. **LogMessage**
   │             5. **Update **$ExcelOutput_ALL**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess**)**
   │             7. 🔀 **DECISION:** `$UploadSuccess`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound**)**
   │                   2. 🔀 **DECISION:** `$BidRound != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                ➔ **If [false]:**
   └─ **End Loop**
23. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus** (Result: **$Variable**)**
24. **Commit/Save **$BidRoundList_ToCommit** to Database**
25. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
26. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
27. **Delete**
28. **Delete**
29. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids****
30. **LogMessage**
31. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.