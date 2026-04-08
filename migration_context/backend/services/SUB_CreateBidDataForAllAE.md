# Microflow Detailed Specification: SUB_CreateBidDataForAllAE

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction = $Auction and Round = $SchedulingAuction/Round - 1]` (Result: **$SchedulingAuction_PriorRound**)**
3. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound[AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction_PriorRound and AuctionUI.BidRound_BuyerCode = $BuyerCode]]` (Result: **$BidDataList_PriorRound**)**
4. **Create Variable **$BuyerCodeType** = `$BuyerCode/BuyerCodeType`**
5. **Call Microflow **EcoATM_BuyerManagement.SUB_ListAggregateInventoryByBuyerCodeType** (Result: **$AggregatedInventoryList**)**
6. **CreateList**
7. **Call Microflow **EcoATM_BuyerManagement.SUB_GetBidRoundBySAandCode** (Result: **$NewBidRound**)**
8. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
9. **Update **$NewBidRound** (and Save to DB)
      - Set **BidRound_BidDataDoc** = `$BidDataDoc`**
10. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
11. 🔄 **LOOP:** For each **$IteratorAE** in **$AggregatedInventoryList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAE/EcoId and toString($currentObject/Grade)=$IteratorAE/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
   │ 2. 🔀 **DECISION:** `$BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │    ➔ **If [true]:**
   │       1. **Create **AuctionUI.BidData** (Result: **$NewBidData_DW**)
      - Set **EcoID** = `$IteratorAE/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAE`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `empty`
      - Set **Merged_Grade** = `$IteratorAE/MergedGrade`
      - Set **Code** = `$BuyerCode/Code`
      - Set **BidData_BidRound** = `$NewBidRound`
      - Set **CompanyName** = `$BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **TargetPrice** = `$IteratorAE/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAE/DWTotalQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAE/DWTotalQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAE/DWTotalQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAE/DWTotalQuantity`**
   │       2. **Add **$$NewBidData_DW** to/from list **$BidDataList****
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.BidData** (Result: **$NewBidData_NonDW**)
      - Set **EcoID** = `$IteratorAE/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAE`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `empty`
      - Set **Merged_Grade** = `$IteratorAE/MergedGrade`
      - Set **Code** = `$BuyerCode/Code`
      - Set **BidData_BidRound** = `$NewBidRound`
      - Set **CompanyName** = `$BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **TargetPrice** = `$IteratorAE/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAE/TotalQuantity+$MatchingBidDataTotalQuantityConfig/NonDWQuantity+$MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAE/TotalQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
   │       2. **Update **$IteratorAE**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
   │       3. **Add **$$NewBidData_NonDW
** to/from list **$BidDataList****
   └─ **End Loop**
12. **Call Microflow **EcoATM_BuyerManagement.SUB_UpdateBlankBidDataWithProrRoundBidData****
13. **Commit/Save **$BidDataList** to Database**
14. **Update **$SchedulingAuction** (and Save to DB)**
15. **Commit/Save **$AggregatedInventoryList** to Database**
16. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
17. 🏁 **END:** Return `$BidDataList`

**Final Result:** This process concludes by returning a [List] value.