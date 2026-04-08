# Microflow Detailed Specification: SUB_CopyCarryOverBidData

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidDataTemp_LastWeek** (Type: AuctionUI.BidDataTemp)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CarryOverBidsCopy'`**
2. **Create Variable **$Description** = `'Copying bids for CarryOver for Buyer Code : '+$NP_BuyerCodeSelect_Helper/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$ScheduleAuction**)**
5. **Retrieve related **SchedulingAuction_Auction** via Association from **$ScheduleAuction** (Result: **$Auction**)**
6. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
7. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
8. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound] [AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code =$NP_BuyerCodeSelect_Helper/Code]` (Result: **$BidData_CurrentWeek**)**
9. **CreateList**
10. 🔄 **LOOP:** For each **$IteratorLastWeekBidDataTemp** in **$BidDataTemp_LastWeek**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorLastWeekBidDataTemp/EcoID and $currentObject/Merged_Grade = $IteratorLastWeekBidDataTemp/Merged_Grade` (Result: **$BidData**)**
   │ 2. 🔀 **DECISION:** `$BidData!=empty`
   │    ➔ **If [true]:**
   │       1. **Update **$BidData**
      - Set **BidAmount** = `$IteratorLastWeekBidDataTemp/Amount`
      - Set **BidQuantity** = `$IteratorLastWeekBidDataTemp/Quantity`**
   │       2. **Add **$$BidData** to/from list **$BidDataList_Updates****
   │    ➔ **If [false]:**
   │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoId = $IteratorLastWeekBidDataTemp/EcoID and $currentObject/MergedGrade = $IteratorLastWeekBidDataTemp/Merged_Grade` (Result: **$AggregatedInventoryFound**)**
   │       2. 🔀 **DECISION:** `$IteratorLastWeekBidDataTemp!=empty and $IteratorLastWeekBidDataTemp/Amount!= empty`
   │          ➔ **If [true]:**
   │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData**)
      - Set **EcoID** = `$IteratorLastWeekBidDataTemp/EcoID`
      - Set **BidQuantity** = `$IteratorLastWeekBidDataTemp/Quantity`
      - Set **BidAmount** = `$IteratorLastWeekBidDataTemp/Amount`
      - Set **TargetPrice** = `$IteratorLastWeekBidDataTemp/TargetPrice`
      - Set **MaximumQuantity** = `$IteratorLastWeekBidDataTemp/MaximumQuantity`
      - Set **Merged_Grade** = `$IteratorLastWeekBidDataTemp/Merged_Grade`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **BidData_AggregatedInventory** = `$AggregatedInventoryFound`
      - Set **BidData_BuyerCode** = `$BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode`**
   │             2. **Add **$$NewBidData** to/from list **$BidDataList_Updates****
   │          ➔ **If [false]:**
   └─ **End Loop**
11. **Commit/Save **$BidDataList_Updates** to Database**
12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
13. 🏁 **END:** Return `$BidDataList_Updates`

**Final Result:** This process concludes by returning a [List] value.