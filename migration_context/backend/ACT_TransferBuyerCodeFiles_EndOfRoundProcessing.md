# Microflow Detailed Specification: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **JavaCallAction**
3. **LogMessage**
4. **CreateList**
5. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList_All**)**
6. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ] ]` (Result: **$BuyerCodeList_Submitted**)**
7. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_NotSubmitted**)**
8. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_SetSharepointDownloadStart****
9. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
10. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
11. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
12. **CreateList**
13. **CreateList**
14. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_NotSubmitted**
   │ 1. 🔀 **DECISION:** `$IteratorBuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │    ➔ **If [true]:**
   │       1. **LogMessage**
   │       2. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_DW**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocDW_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
   │       3. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_DW**)**
   │       4. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week=$Week] [DWTotalQuantity > 0]` (Result: **$AgregatedInventoryList_DW**)**
   │       5. **LogMessage**
   │       6. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
   │       7. **LogMessage**
   │       8. 🔄 **LOOP:** For each **$IteratorAllBidDownload_1** in **$AllBidDownloadList**
   │          │ 1. **Update **$IteratorAllBidDownload_1**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_DW`**
   │          └─ **End Loop**
   │       9. **LogMessage**
   │       10. **LogMessage**
   │       11. **Commit/Save **$AllBidDownloadList** to Database**
   │       12. **LogMessage**
   │       13. **LogMessage**
   │       14. **JavaCallAction**
   │       15. **LogMessage**
   │       16. **LogMessage**
   │       17. **LogMessage**
   │       18. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_ClearAlBidDownloadList** (Result: **$Deleted_2**)**
   │       19. 🔀 **DECISION:** `$Deleted_2 = true`
   │          ➔ **If [true]:**
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
   │                         3. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
   │                         4. **Delete**
   │                         5. **LogMessage**
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound_1** to/from list **$BidRoundList_ToCommit****
   │                         2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
   │                         3. **Delete**
   │                         4. **LogMessage**
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. **LogMessage**
   │       2. **Create **AuctionUI.AllBidsDoc** (Result: **$NewAllBidsDoc_ALL**)
      - Set **DeleteAfterDownload** = `true`
      - Set **AllBidsDocAll_Auction** = `$SchedulingAuction/AuctionUI.SchedulingAuction_Auction`**
   │       3. **Create **XLSReport.CustomExcel** (Result: **$ExcelOutput_ALL**)**
   │       4. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week ]` (Result: **$AgregatedInventoryList_ALL**)**
   │       5. **LogMessage**
   │       6. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_NonDW****
   │       7. **LogMessage**
   │       8. 🔄 **LOOP:** For each **$IteratorAllBidDownload** in **$AllBidDownloadList**
   │          │ 1. **Update **$IteratorAllBidDownload**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc_ALL`**
   │          └─ **End Loop**
   │       9. **LogMessage**
   │       10. **LogMessage**
   │       11. **Commit/Save **$AllBidDownloadList** to Database**
   │       12. **LogMessage**
   │       13. **LogMessage**
   │       14. **JavaCallAction**
   │       15. **LogMessage**
   │       16. **LogMessage**
   │       17. **LogMessage**
   │       18. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_ClearAlBidDownloadList** (Result: **$Deleted**)**
   │       19. 🔀 **DECISION:** `$Deleted = true`
   │          ➔ **If [true]:**
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
   │                         3. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
   │                         4. **Delete**
   │                         5. **LogMessage**
   │                      ➔ **If [false]:**
   │                         1. **Add **$$BidRound** to/from list **$BidRoundList_ToCommit****
   │                         2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete****
   │                         3. **Delete**
   │                         4. **LogMessage**
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   └─ **End Loop**
15. **LogMessage**
16. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus** (Result: **$Variable**)**
17. **Commit/Save **$BidRoundList_ToCommit** to Database**
18. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids****
19. **LogMessage**
20. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.