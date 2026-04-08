# Microflow Detailed Specification: ACT_GetBuyerSummaryReportOverview

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed' ) ]` (Result: **$Auction_Active**)**
2. 🔀 **DECISION:** `$Auction_Active != empty`
   ➔ **If [true]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction_Active** (Result: **$CurrentAuction**)**
      2. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $CurrentAuction/WeekNumber -1 or WeekID = $CurrentAuction/WeekID -1 ) ]` (Result: **$PreviousAuction**)**
      3. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( BuyerCodeType != empty ) and ( EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction or EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $CurrentAuction or AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $CurrentAuction or AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/EcoATM_Buyer.BidData_BidRound/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction ) and (AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/Submitted = true or EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true ) ]` (Result: **$BuyerCodeListNew**)**
      4. **DB Retrieve **AuctionUI.BuyerCode**  (Result: **$BuyerCodeListALL**)**
      5. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( BuyerCodeType != empty ) and ( EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction or EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $PreviousAuction or AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $PreviousAuction or AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/EcoATM_Buyer.BidData_BidRound/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction ) ]` (Result: **$BuyerCodeListOld**)**
      6. **CreateList**
      7. **CreateList**
      8. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[(EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $CurrentAuction ) and EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true]` (Result: **$BidDataListPrimaryNew**)**
      9. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataListCleanNew**)**
      10. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[( EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $PreviousAuction) and EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true]` (Result: **$BidDataListPrimaryOld**)**
      11. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataListCleanOld**)**
      12. **DB Retrieve **AuctionUI.BuyerBidSummaryReport** Filter: `[ ( AuctionUI.BuyerBidSummaryReport_Week = $CurrentAuction ) ]` (Result: **$BuyerBidSummaryReportListExisting**)**
      13. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $PreviousAuction and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) ]` (Result: **$AggregatedInventoryListOld_1**)**
      14. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $CurrentAuction and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) ]` (Result: **$AggregatedInventoryListNew_1**)**
      15. 🔄 **LOOP:** For each **$IteratorBuyerCodeOld** in **$BuyerCodeListOld**
         │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $IteratorBuyerCodeOld or $currentObject/EcoATM_Buyer.BidData_BuyerCode = $IteratorBuyerCodeOld` (Result: **$OldBidDataList**)**
         │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCodeOld` (Result: **$OldBidDataList_1**)**
         │ 3. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCodeOld/Code` (Result: **$OldTupleMatch**)**
         │ 4. 🔀 **DECISION:** `$OldTupleMatch = empty`
         │    ➔ **If [true]:**
         │       1. 🔀 **DECISION:** `not($OldTupleMatch/LotsBid1 = 0 and $OldTupleMatch/UnitsBid1 = 0) or not($OldTupleMatch/LotsBid1 = 0)`
         │          ➔ **If [true]:**
         │             1. **AggregateList**
         │             2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $PreviousAuction and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $IteratorBuyerCodeOld and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true]` (Result: **$AggregatedInventoryListOld**)**
         │             3. **AggregateList**
         │             4. **Create **AuctionUI.BuyerBidSummaryReport_NP** (Result: **$NewBuyerBidSummaryReportHelper**)
      - Set **BuyerCode** = `$IteratorBuyerCodeOld/Code`
      - Set **BuyerName** = `$IteratorBuyerCodeOld/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **UnitsBid2** = `$SumBidQuantOld`
      - Set **LotsBid2** = `$LotsBidOld`**
         │             5. **Add **$$NewBuyerBidSummaryReportHelper** to/from list **$BuyerBidSummaryReportHelperList****
         │          ➔ **If [false]:**
         │    ➔ **If [false]:**
         └─ **End Loop**
      16. 🔄 **LOOP:** For each **$IteratorBuyerCodeNew** in **$BuyerCodeListNew**
         │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $IteratorBuyerCodeNew or $currentObject/EcoATM_Buyer.BidData_BuyerCode = $IteratorBuyerCodeNew` (Result: **$NewBidDataList**)**
         │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true` (Result: **$IsSubmitted**)**
         │ 3. 🔀 **DECISION:** `$IsSubmitted != empty`
         │    ➔ **If [true]:**
         │       1. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCodeNew` (Result: **$NewBidDataList_1**)**
         │       2. **AggregateList**
         │       3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $CurrentAuction and ( EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $IteratorBuyerCodeNew or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $IteratorBuyerCodeNew/Code )and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true]` (Result: **$AggregatedInventoryListTupleNew**)**
         │       4. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $CurrentAuction and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $IteratorBuyerCodeNew]` (Result: **$AggregatedInventoryListTupleNew_1**)**
         │       5. **AggregateList**
         │       6. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCodeNew/Code` (Result: **$MatchExisting**)**
         │       7. 🔀 **DECISION:** `$MatchExisting = empty`
         │          ➔ **If [false]:**
         │             1. 🔀 **DECISION:** `$MatchExisting/Submitted = true`
         │                ➔ **If [true]:**
         │                   1. **Update **$MatchExisting**
      - Set **LotsBid1** = `$LotsBidNew`
      - Set **UnitsBid1** = `$SumBidQuantNew`
      - Set **BuyerName** = `$IteratorBuyerCodeNew/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **Auction1** = `$CurrentAuction/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`
      - Set **Auction2** = `$PreviousAuction/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`**
         │                ➔ **If [false]:**
         │                   1. **Delete**
         │          ➔ **If [true]:**
         │             1. **Create **AuctionUI.BuyerBidSummaryReport** (Result: **$NewBuyerBidSummaryReport**)
      - Set **BuyerCode** = `$IteratorBuyerCodeNew/Code`
      - Set **BuyerName** = `$IteratorBuyerCodeNew/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **Auction1** = `$CurrentAuction/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`
      - Set **Auction2** = `$PreviousAuction/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`
      - Set **LotsBid1** = `$LotsBidNew`
      - Set **LotsBid2** = `empty`
      - Set **UnitsBid1** = `$SumBidQuantNew`
      - Set **UnitsBid2** = `empty`
      - Set **UpOrDownLotsBid** = `''`
      - Set **UpOrDownUnitsBid** = `''`
      - Set **BuyerBidSummaryReport_Week** = `$CurrentAuction`**
         │             2. **Add **$$NewBuyerBidSummaryReport** to/from list **$BuyerBidSummaryReportListExisting****
         │    ➔ **If [false]:**
         └─ **End Loop**
      17. 🔄 **LOOP:** For each **$IteratorBuyerBidSummaryReport** in **$BuyerBidSummaryReportListExisting**
         │ 1. **List Operation: **Find** on **$undefined** where `$IteratorBuyerBidSummaryReport/BuyerCode` (Result: **$Match**)**
         │ 2. 🔀 **DECISION:** `$Match != empty`
         │    ➔ **If [false]:**
         │    ➔ **If [true]:**
         │       1. **Create Variable **$LotsBidArrow** = `if $IteratorBuyerBidSummaryReport/LotsBid1 > $Match/LotsBid2 then '⇧' else if $IteratorBuyerBidSummaryReport/LotsBid1 < $Match/LotsBid2 then '⇩' else ''`**
         │       2. **Create Variable **$UnitsBidArrow** = `if $IteratorBuyerBidSummaryReport/UnitsBid1 > $Match/UnitsBid2 then '⇧' else if $IteratorBuyerBidSummaryReport/UnitsBid1 < $Match/UnitsBid2 then '⇩' else ''`**
         │       3. **Update **$IteratorBuyerBidSummaryReport**
      - Set **LotsBid2** = `$Match/LotsBid2`
      - Set **UnitsBid2** = `$Match/UnitsBid2`
      - Set **UpOrDownLotsBid** = `$LotsBidArrow`
      - Set **UpOrDownUnitsBid** = `$UnitsBidArrow`**
         └─ **End Loop**
      18. **Commit/Save **$BuyerBidSummaryReportList** to Database**
      19. **Commit/Save **$BuyerBidSummaryReportListExisting** to Database**
      20. **Delete**
      21. **Maps to Page: **AuctionUI.BuyerSummaryReportOverview****
      22. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed']` (Result: **$Auction_Closed**)**
      2. **Show Message (Information): `No active auction is present at this time.`**
      3. **ShowHomePage**
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.