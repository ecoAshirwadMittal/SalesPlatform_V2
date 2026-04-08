# Microflow Detailed Specification: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing_2

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **CreateList**
3. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList_All**)**
4. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ] ]` (Result: **$BuyerCodeList_Submitted**)**
5. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_NotSubmitted**)**
6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_SetSharepointDownloadStart****
7. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
8. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
9. **CreateList**
10. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
11. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_DW**)**
12. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_ALL**)**
13. **Create Variable **$FileCreated_DW** = `false`**
14. **Create Variable **$FileCreated_NonDW** = `false`**
15. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_DW**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocDW_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
16. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_ALL**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocAll_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
17. **CreateList**
18. **CreateList**
19. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_NotSubmitted**
   │ 1. 🔀 **DECISION:** `$IteratorBuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$FileCreated_DW = false`
   │          ➔ **If [true]:**
   │             1. **Update Variable **$FileCreated_DW** = `true`**
   │             2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week=$Week] [DWTotalQuantity > 0]` (Result: **$AgregatedInventoryList_DW**)**
   │             3. **LogMessage**
   │             4. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
   │             5. **LogMessage**
   │             6. 🔄 **LOOP:** For each **$IteratorAllBidDownload_1** in **$AllBidDownloadList**
   │                │ 1. **Update **$IteratorAllBidDownload_1**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_DW`**
   │                └─ **End Loop**
   │             7. **LogMessage**
   │             8. **LogMessage**
   │             9. **Commit/Save **$AllBidDownloadList** to Database**
   │             10. **LogMessage**
   │             11. **LogMessage**
   │             12. **JavaCallAction**
   │             13. **LogMessage**
   │             14. **LogMessage**
   │             15. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_ClearAlBidDownloadList** (Result: **$Deleted_2**)**
   │             16. 🔀 **DECISION:** `$Deleted_2 = true`
   │                ➔ **If [true]:**
   │                   1. **LogMessage**
   │                   2. **LogMessage**
   │                   3. **Update **$ExcelOutput_DW**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │                   4. **LogMessage**
   │                   5. **LogMessage**
   │                   6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess2**)**
   │                   7. 🔀 **DECISION:** `$UploadSuccess2 = true`
   │                      ➔ **If [true]:**
   │                         1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound_1**)**
   │                         2. 🔀 **DECISION:** `$BidRound_1 != empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$BidRound_1**
      - Set **UploadedToSharepoint** = `true`**
   │                               2. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                               3. **LogMessage**
   │                            ➔ **If [false]:**
   │                               1. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                               2. **LogMessage**
   │                      ➔ **If [false]:**
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. **LogMessage**
   │             2. **LogMessage**
   │             3. **Update **$ExcelOutput_DW**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             4. **LogMessage**
   │             5. **LogMessage**
   │             6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess2**)**
   │             7. 🔀 **DECISION:** `$UploadSuccess2 = true`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound_1**)**
   │                   2. 🔀 **DECISION:** `$BidRound_1 != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound_1**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                         3. **LogMessage**
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                         2. **LogMessage**
   │                ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$FileCreated_NonDW = false`
   │          ➔ **If [true]:**
   │             1. **Update Variable **$FileCreated_NonDW** = `true`**
   │             2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week ]` (Result: **$AgregatedInventoryList_ALL**)**
   │             3. **LogMessage**
   │             4. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
   │             5. **LogMessage**
   │             6. 🔄 **LOOP:** For each **$IteratorAllBidDownload** in **$AllBidDownloadList**
   │                │ 1. **Update **$IteratorAllBidDownload**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_ALL`**
   │                └─ **End Loop**
   │             7. **LogMessage**
   │             8. **LogMessage**
   │             9. **Commit/Save **$AllBidDownloadList** to Database**
   │             10. **LogMessage**
   │             11. **LogMessage**
   │             12. **JavaCallAction**
   │             13. **LogMessage**
   │             14. **LogMessage**
   │             15. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_ClearAlBidDownloadList** (Result: **$Deleted**)**
   │             16. 🔀 **DECISION:** `$Deleted = true`
   │                ➔ **If [true]:**
   │                   1. **LogMessage**
   │                   2. **LogMessage**
   │                   3. **Update **$ExcelOutput_ALL**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │                   4. **LogMessage**
   │                   5. **LogMessage**
   │                   6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess**)**
   │                   7. 🔀 **DECISION:** `$UploadSuccess = true`
   │                      ➔ **If [true]:**
   │                         1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound**)**
   │                         2. 🔀 **DECISION:** `$BidRound != empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$BidRound**
      - Set **UploadedToSharepoint** = `true`**
   │                               2. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                               3. **LogMessage**
   │                            ➔ **If [false]:**
   │                               1. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                               2. **LogMessage**
   │                      ➔ **If [false]:**
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. **LogMessage**
   │             2. **LogMessage**
   │             3. **Update **$ExcelOutput_ALL**
      - Set **Name** = `$IteratorBuyerCode/Code+'_'+formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`
      - Set **DeleteAfterDownload** = `false`**
   │             4. **LogMessage**
   │             5. **LogMessage**
   │             6. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$UploadSuccess**)**
   │             7. 🔀 **DECISION:** `$UploadSuccess = true`
   │                ➔ **If [true]:**
   │                   1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]` (Result: **$BidRound**)**
   │                   2. 🔀 **DECISION:** `$BidRound != empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$BidRound**
      - Set **UploadedToSharepoint** = `true`**
   │                         2. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                         3. **LogMessage**
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                         2. **LogMessage**
   │                ➔ **If [false]:**
   └─ **End Loop**
20. **LogMessage**
21. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus** (Result: **$Variable**)**
22. **Commit/Save **$BidRoundList_ToCommit** to Database**
23. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
24. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
25. **Delete**
26. **Delete**
27. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids****
28. **LogMessage**
29. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.