# Microflow Detailed Specification: ACT_CalculateHighestBids

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
4. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList_1**)**
5. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/id = $Week] [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[BidAmount > 0]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true]]` (Result: **$AggregatedInventoryList**)**
6. **Call Microflow **AuctionUI.SUB_CalcHighBids_ResetHighBids****
7. **CreateList**
8. **CreateList**
9. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList**
   │ 1. **Retrieve related **BidData_AggregatedInventory** via Association from **$IteratorAggregatedInventory** (Result: **$AllBids_CurrentAggInv**)**
   │ 2. 🔀 **DECISION:** `$AllBids_CurrentAggInv=empty`
   │    ➔ **If [false]:**
   │       1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true and $currentObject/BidAmount > 0` (Result: **$SubmittedBidDataList**)**
   │       2. 🔀 **DECISION:** `$SubmittedBidDataList!=empty`
   │          ➔ **If [true]:**
   │             1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$FilterRound2BidData**)**
   │             2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$FilterRound1BidData**)**
   │             3. 🔀 **DECISION:** `$FilterRound1BidData!=empty`
   │                ➔ **If [true]:**
   │                   1. 🔄 **LOOP:** For each **$IteratorRound2BidData** in **$FilterRound2BidData**
   │                      │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound2BidData/EcoID and $currentObject/Merged_Grade= $IteratorRound2BidData/Merged_Grade and $currentObject/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code= $IteratorRound2BidData/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code` (Result: **$FindRound1BidData**)**
   │                      │ 2. 🔀 **DECISION:** `$FindRound1BidData=empty`
   │                      │    ➔ **If [true]:**
   │                      │    ➔ **If [false]:**
   │                      │       1. **Remove **$$FindRound1BidData** to/from list **$FilterRound1BidData****
   │                      └─ **End Loop**
   │                   2. **Add **$$FilterRound1BidData** to/from list **$FilterRound2BidData****
   │                   3. **AggregateList**
   │                   4. **List Operation: **Filter** on **$undefined** where `$MaximumBidAmount` (Result: **$MaxBidDataList**)**
   │                   5. **AggregateList**
   │                   6. 🔀 **DECISION:** `$Count>1`
   │                      ➔ **If [true]:**
   │                         1. **List Operation: **Sort** on **$undefined** sorted by: SubmitDateTime (Descending) (Result: **$SortSubmitTime_Tie**)**
   │                         2. **List Operation: **Head** on **$undefined** (Result: **$HighestBidTieResult**)**
   │                         3. **Update **$HighestBidTieResult**
      - Set **HighestBid** = `true`**
   │                         4. **Add **$$HighestBidTieResult** to/from list **$HighestBidDataList_toCommit****
   │                         5. **Remove **$$HighestBidTieResult** to/from list **$FilterRound2BidData****
   │                         6. **Add **$$FilterRound2BidData** to/from list **$All_BidDataList****
   │                      ➔ **If [false]:**
   │                         1. **List Operation: **Head** on **$undefined** (Result: **$SingleHighestBidResult**)**
   │                         2. **Update **$SingleHighestBidResult**
      - Set **HighestBid** = `true`**
   │                         3. **Add **$$SingleHighestBidResult** to/from list **$HighestBidDataList_toCommit****
   │                         4. **Remove **$$SingleHighestBidResult** to/from list **$FilterRound2BidData****
   │                         5. **Add **$$FilterRound2BidData** to/from list **$All_BidDataList****
   │                ➔ **If [false]:**
   │          ➔ **If [false]:**
   │    ➔ **If [true]:**
   └─ **End Loop**
10. **Commit/Save **$HighestBidDataList_toCommit** to Database**
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.