# Microflow Detailed Specification: ACT_Round2AggregatedInventory

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction_Round2** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_BuyerManagement.QualifiedBuyerCodes** Filter: `[EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction = $SchedulingAuction_Round2 and EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BuyerCode]` (Result: **$QualifiedBuyerCode**)**
3. 🔀 **DECISION:** `$QualifiedBuyerCode/Included = true`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter**)**
      2. **Call Microflow **AuctionUI.SUB_ListBuyerCodeAggregatedInventory** (Result: **$AggregatedInventoryList_Round1Bids**)**
      3. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$BuyerCode/Code and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week/AuctionUI.Auction=$Auction ]` (Result: **$AllRounds_BidDataList**)**
      4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BuyerCode = $BuyerCode] [AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction=$Auction] [BidAmount>0]` (Result: **$BidDataList_Round1**)**
      5. **CreateList**
      6. **Create Variable **$HasOneQualifyingBid** = `false`**
      7. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification = EcoATM_BuyerManagement.Enum_RegularBuyerQualification.All_Buyers or $QualifiedBuyerCode/Qualificationtype = EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual`
         ➔ **If [true]:**
            1. **Update Variable **$HasOneQualifyingBid** = `true`**
            2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ AuctionUI.BidData_BuyerCode = $BuyerCode and (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$AggregatedInventoryList_NonDW**)**
            3. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList_Round1Bids**
               │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_AggregatedInventory = $IteratorAggregatedInventory` (Result: **$Round1BidData**)**
               │ 2. 🔀 **DECISION:** `$Round1BidData!=empty`
               │    ➔ **If [false]:**
               │       1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │    ➔ **If [true]:**
               │       1. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
               │          ➔ **If [true]:**
               │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
               │                ➔ **If [Only_Qualified]:**
               │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/DWAvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/DWAvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/DWAvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/DWAvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
               │                            ➔ **If [false]:**
               │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                                  ➔ **If [true]:**
               │                                     1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                                  ➔ **If [false]:**
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [AllBidders]:**
               │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                ➔ **If [All_Buyers]:**
               │                   1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                   2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [(empty)]:**
               │          ➔ **If [false]:**
               │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
               │                ➔ **If [Only_Qualified]:**
               │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/AvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/AvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/AvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/AvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                                  ➔ **If [true]:**
               │                                     1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                                  ➔ **If [false]:**
               │                ➔ **If [AllBidders]:**
               │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                ➔ **If [All_Buyers]:**
               │                   1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                   2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [(empty)]:**
               └─ **End Loop**
            4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            5. 🔀 **DECISION:** `$HasOneQualifyingBid = true`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$Round2AggregatedInventoryList`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ AuctionUI.BidData_BuyerCode = $BuyerCode and (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$AggregatedInventoryList_NonDW**)**
            2. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList_Round1Bids**
               │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_AggregatedInventory = $IteratorAggregatedInventory` (Result: **$Round1BidData**)**
               │ 2. 🔀 **DECISION:** `$Round1BidData!=empty`
               │    ➔ **If [false]:**
               │       1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │    ➔ **If [true]:**
               │       1. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
               │          ➔ **If [true]:**
               │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
               │                ➔ **If [Only_Qualified]:**
               │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/DWAvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/DWAvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/DWAvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/DWAvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
               │                            ➔ **If [false]:**
               │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                                  ➔ **If [true]:**
               │                                     1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                                  ➔ **If [false]:**
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [AllBidders]:**
               │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                ➔ **If [All_Buyers]:**
               │                   1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                   2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [(empty)]:**
               │          ➔ **If [false]:**
               │             1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification`
               │                ➔ **If [Only_Qualified]:**
               │                   1. 🔀 **DECISION:** `($IteratorAggregatedInventory/AvgTargetPrice = 0 and $Round1BidData/BidAmount > 0) or ( $IteratorAggregatedInventory/AvgTargetPrice!= 0) and ($Round1BidData/BidAmount div $IteratorAggregatedInventory/AvgTargetPrice>= 1 - $BidRoundSelectionFilter/TargetPercent) or ($IteratorAggregatedInventory/AvgTargetPrice - $Round1BidData/BidAmount <= $BidRoundSelectionFilter/TargetValue)`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.InventoryRound1Bids and $Round1BidData/BidAmount > 0`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                               1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                                  ➔ **If [true]:**
               │                                     1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                                  ➔ **If [false]:**
               │                ➔ **If [AllBidders]:**
               │                   1. 🔀 **DECISION:** `$Round1BidData/BidAmount > 0`
               │                      ➔ **If [true]:**
               │                         1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                         2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                      ➔ **If [false]:**
               │                         1. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory`
               │                            ➔ **If [true]:**
               │                               1. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                            ➔ **If [false]:**
               │                ➔ **If [All_Buyers]:**
               │                   1. **Update Variable **$HasOneQualifyingBid** = `true`**
               │                   2. **Add **$$IteratorAggregatedInventory
** to/from list **$Round2AggregatedInventoryList****
               │                ➔ **If [(empty)]:**
               └─ **End Loop**
            3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            4. 🔀 **DECISION:** `$HasOneQualifyingBid = true`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$Round2AggregatedInventoryList`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.