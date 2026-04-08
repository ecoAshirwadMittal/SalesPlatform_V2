# Microflow Detailed Specification: SUB_Round2AggregatedInventorySingleItem

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$BuyerCodeList** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter**)**
3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ AuctionUI.BidData_BuyerCode = $BuyerCode and (BidAmount > 0)]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$AggregatedInventoryList_Round1Bids**)**
4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BuyerCode = $BuyerCode] [AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction=$Auction] [BidAmount>0]` (Result: **$BidDataList_Round1**)**
5. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList_Round1Bids**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_AggregatedInventory = $IteratorAggregatedInventory` (Result: **$Round1BidData**)**
   │ 2. 🔀 **DECISION:** `$Round1BidData!=empty`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
   │                ➔ **If [(empty)]:**
   │                ➔ **If [All_Buyers]:**
   │                   1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                ➔ **If [AllBidders]:**
   │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
   │                      ➔ **If [true]:**
   │                         1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
   │                            ➔ **If [true]:**
   │                               1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                            ➔ **If [false]:**
   │                ➔ **If [Only_Qualified]:**
   │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/DWAvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/DWAvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/DWAvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/DWAvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
   │                      ➔ **If [true]:**
   │                         1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
   │                                  ➔ **If [false]:**
   │                                  ➔ **If [true]:**
   │                                     1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                            ➔ **If [true]:**
   │                               1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
   │                ➔ **If [(empty)]:**
   │                ➔ **If [All_Buyers]:**
   │                   1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                ➔ **If [AllBidders]:**
   │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
   │                      ➔ **If [true]:**
   │                         1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
   │                            ➔ **If [true]:**
   │                               1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                            ➔ **If [false]:**
   │                ➔ **If [Only_Qualified]:**
   │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/AvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/AvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/AvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/AvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
   │                      ➔ **If [true]:**
   │                         1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
   │                                  ➔ **If [false]:**
   │                                  ➔ **If [true]:**
   │                                     1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   │                            ➔ **If [true]:**
   │                               1. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   └─ **End Loop**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.