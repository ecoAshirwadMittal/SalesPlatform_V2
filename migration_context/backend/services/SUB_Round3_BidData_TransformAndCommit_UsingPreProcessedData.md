# Microflow Detailed Specification: SUB_Round3_BidData_TransformAndCommit_UsingPreProcessedData

### 📥 Inputs (Parameters)
- **$ExcelIMport_BidData** (Type: AuctionUI.BidDataImport_Round3)
- **$RoundThreeBidDataExcelExport** (Type: AuctionUI.RoundThreeBidDataExcelExport)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BidRoundList** (Type: AuctionUI.BidRound)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)
- **$RoundThreeBuyersDataReport** (Type: AuctionUI.RoundThreeBuyersDataReport)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$Failed** = `false`**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
5. **Call Microflow **AuctionUI.ACT_DeleteRound3BidDataForBuyer****
6. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList_BuyerAllCodes**)**
7. **CreateList**
8. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
9. 🔄 **LOOP:** For each **$IteratorBidData_Round3** in **$ExcelIMport_BidData**
   │ 1. **Create Variable **$EcoID** = `parseInteger(substring($IteratorBidData_Round3/Code_Grade, 0, find($IteratorBidData_Round3/Code_Grade, ' ')))`**
   │ 2. **Create Variable **$MergedGrade** = `substring($IteratorBidData_Round3/Code_Grade, find($IteratorBidData_Round3/Code_Grade, ' ') + 1)`**
   │ 3. **List Operation: **FindByExpression** on **$undefined** where `toUpperCase($currentObject/Code) = toUpperCase($IteratorBidData_Round3/Code)` (Result: **$BuyerCode**)**
   │ 4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoId=$EcoID and $currentObject/MergedGrade=$MergedGrade` (Result: **$AggregatedInventory**)**
   │ 5. 🔀 **DECISION:** `$AggregatedInventory!=empty`
   │    ➔ **If [true]:**
   │       1. **Call Microflow **AuctionUI.SUB_GetOrCreateBidRound** (Result: **$BidRound**)**
   │       2. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
   │       3. **Call Microflow **AuctionUI.SUB_GetBidValueForImportedBidData** (Result: **$Bid**)**
   │       4. **Call Microflow **AuctionUI.SUB_GetBidQuantityValueForImportedBidData** (Result: **$BidQuantity**)**
   │       5. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │          ➔ **If [true]:**
   │             1. **Create **AuctionUI.BidData** (Result: **$BidData_DW**)
      - Set **EcoID** = `$EcoID`
      - Set **BidData_AggregatedInventory** = `$AggregatedInventory`
      - Set **BidAmount** = `$Bid`
      - Set **Merged_Grade** = `$AggregatedInventory/MergedGrade`
      - Set **Code** = `$BuyerCode/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$Buyer/CompanyName`
      - Set **TargetPrice** = `$AggregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$AggregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`
      - Set **PreviousRoundBidAmount** = `$Bid`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **BidQuantity** = `$BidQuantity`
      - Set **PreviousRoundBidQuantity** = `$BidQuantity`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
   │             2. **Add **$$BidData_DW** to/from list **$BidDataList_Updates****
   │          ➔ **If [false]:**
   │             1. **Create **AuctionUI.BidData** (Result: **$BidData_NonDW**)
      - Set **EcoID** = `$EcoID`
      - Set **BidData_AggregatedInventory** = `$AggregatedInventory`
      - Set **BidAmount** = `$Bid`
      - Set **Merged_Grade** = `$AggregatedInventory/MergedGrade`
      - Set **Code** = `$BuyerCode/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$Buyer/CompanyName`
      - Set **TargetPrice** = `$AggregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$AggregatedInventory/TotalQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`
      - Set **PreviousRoundBidAmount** = `$Bid`
      - Set **PreviousRoundBidQuantity** = `$BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidQuantity** = `$BidQuantity`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
   │             2. **Add **$$BidData_NonDW** to/from list **$BidDataList_Updates****
   │    ➔ **If [false]:**
   │       1. **Call Microflow **Custom_Logging.SUB_Log_Info****
   └─ **End Loop**
10. 🔀 **DECISION:** `$Failed != true`
   ➔ **If [true]:**
      1. **List Operation: **Union** on **$undefined** (Result: **$BidRoundList_Distinct**)**
      2. **Set **$$BidRoundList_Distinct** to/from list **$BidRoundList****
      3. **Delete**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return `$BidDataList_Updates`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.