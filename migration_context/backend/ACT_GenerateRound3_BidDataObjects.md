# Microflow Detailed Specification: ACT_GenerateRound3_BidDataObjects

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[(Status='Active' and AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction = $Auction) or (Status='Active' and EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/isSpecialBuyer = true) ]` (Result: **$BuyerCodeList**)**
3. **CreateList**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_ListSpecialTreatmentBuyerBidData** (Result: **$BidDataListSpecial**)**
   │ 2. 🔀 **DECISION:** `$BidDataListSpecial = empty`
   │    ➔ **If [true]:**
   │       1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction = $Auction] [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode] [HighestBid=false and BidAmount > 0]` (Result: **$BidDataList**)**
   │       2. **AggregateList**
   │       3. 🔀 **DECISION:** `$Count=0`
   │          ➔ **If [false]:**
   │             1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$Round2BidData**)**
   │             2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$Round1BidData**)**
   │             3. 🔄 **LOOP:** For each **$IteratorRound2BidData** in **$Round2BidData**
   │                │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound2BidData/EcoID and $currentObject/Merged_Grade= $IteratorRound2BidData/Merged_Grade and $currentObject/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code= $IteratorRound2BidData/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code` (Result: **$FindRound1BidData**)**
   │                │ 2. 🔀 **DECISION:** `$FindRound1BidData=empty`
   │                │    ➔ **If [true]:**
   │                │    ➔ **If [false]:**
   │                │       1. **Remove **$$FindRound1BidData** to/from list **$Round1BidData****
   │                └─ **End Loop**
   │             4. **Add **$$Round1BidData
** to/from list **$Round2BidData****
   │             5. **Create **AuctionUI.BidRound** (Result: **$NewBidRound**)
      - Set **Submitted** = `false`
      - Set **BidRound_SchedulingAuction** = `$SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$IteratorBuyerCode`**
   │             6. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
   │             7. **Update **$NewBidRound** (and Save to DB)
      - Set **BidRound_BidDataDoc** = `$BidDataDoc`**
   │             8. **Update **$BidDataDoc** (and Save to DB)
      - Set **BidRound_BidDataDoc** = `$NewBidRound`**
   │             9. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction = $Auction] [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $IteratorBuyerCode] [HighestBid=true and BidAmount > 0]` (Result: **$BidDataList_HighBids**)**
   │             10. 🔄 **LOOP:** For each **$IteratorBidData_HighBids** in **$BidDataList_HighBids**
   │                │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorBidData_HighBids/EcoID and $currentObject/MergedGrade=$IteratorBidData_HighBids/MergedGrade` (Result: **$BidDataList_MatchHighBids**)**
   │                │ 2. **Remove **$$BidDataList_MatchHighBids
** to/from list **$Round2BidData****
   │                └─ **End Loop**
   │             11. 🔄 **LOOP:** For each **$IteratorBidData** in **$Round2BidData**
   │                │ 1. 🔀 **DECISION:** `$IteratorBidData/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │                │    ➔ **If [true]:**
   │                │       1. **Create **AuctionUI.BidData** (Result: **$NewBidData_DW**)
      - Set **EcoID** = `$IteratorBidData/EcoID`
      - Set **BidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **BidData_AggregatedInventory** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory`
      - Set **BidAmount** = `$IteratorBidData/BidAmount`
      - Set **BidData_BuyerCode** = `$IteratorBuyerCode`
      - Set **BidData_BidRound** = `$NewBidRound`
      - Set **Merged_Grade** = `$IteratorBidData/Merged_Grade`
      - Set **TargetPrice** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/DWAvgTargetPrice`
      - Set **SubmitDateTime** = `$IteratorBidData/SubmitDateTime`
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **MaximumQuantity** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/DWTotalQuantity`
      - Set **PreviousRoundBidAmount** = `$IteratorBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`**
   │                │       2. **Add **$$NewBidData_DW** to/from list **$BidDataList_ToCommit****
   │                │    ➔ **If [false]:**
   │                │       1. **Create **AuctionUI.BidData** (Result: **$NewBidData_NonDW**)
      - Set **EcoID** = `$IteratorBidData/EcoID`
      - Set **BidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **BidData_AggregatedInventory** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory`
      - Set **BidAmount** = `$IteratorBidData/BidAmount`
      - Set **BidData_BuyerCode** = `$IteratorBuyerCode`
      - Set **BidData_BidRound** = `$NewBidRound`
      - Set **Merged_Grade** = `$IteratorBidData/Merged_Grade`
      - Set **TargetPrice** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AvgTargetPrice`
      - Set **SubmitDateTime** = `$IteratorBidData/SubmitDateTime`
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **MaximumQuantity** = `$IteratorBidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/TotalQuantity`
      - Set **PreviousRoundBidAmount** = `$IteratorBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **BidRound** = `$SchedulingAuction/Round`**
   │                │       2. **Add **$$NewBidData_NonDW
** to/from list **$BidDataList_ToCommit****
   │                └─ **End Loop**
   │             12. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCode/Code` (Result: **$ExistingBuyerCode**)**
   │             13. 🔀 **DECISION:** `$ExistingBuyerCode != empty`
   │                ➔ **If [true]:**
   │                ➔ **If [false]:**
   │                   1. **Add **$$IteratorBuyerCode** to/from list **$Round3_BuyerCodes****
   │          ➔ **If [true]:**
   │    ➔ **If [false]:**
   │       1. **Add **$$BidDataListSpecial
** to/from list **$BidDataList_ToCommit****
   │       2. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCode/Code` (Result: **$ExistingBuyerCode**)**
   │       3. 🔀 **DECISION:** `$ExistingBuyerCode != empty`
   │          ➔ **If [true]:**
   │          ➔ **If [false]:**
   │             1. **Add **$$IteratorBuyerCode** to/from list **$Round3_BuyerCodes****
   └─ **End Loop**
6. **Commit/Save **$BidDataList_ToCommit** to Database**
7. **Update **$SchedulingAuction** (and Save to DB)**
8. **Call Microflow **AuctionUI.SUB_GenerateRound3_BidDataFiles****
9. **Call Microflow **AuctionUI.ACT_Round3_StartNotification****
10. **Update **$SchedulingAuction** (and Save to DB)
      - Set **RoundStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Started`
      - Set **SchedulingAuction_QualifiedBuyers** = `$Round3_BuyerCodes`
      - Set **Round3InitStatus** = `AuctionUI.Enum_ScheduleAuctionInitStatus.Complete`**
11. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
12. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_SetAuctionStatus****
      2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      3. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.