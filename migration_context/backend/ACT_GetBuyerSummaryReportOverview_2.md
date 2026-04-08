# Microflow Detailed Specification: ACT_GetBuyerSummaryReportOverview_2

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed' ) ]` (Result: **$Auction_Active**)**
2. 🔀 **DECISION:** `$Auction_Active = empty`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed']` (Result: **$Auction_Closed**)**
      2. **Show Message (Information): `No active auction is present at this time.`**
      3. **ShowHomePage**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction_Active** (Result: **$CurrentAuction**)**
      2. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $CurrentAuction/WeekNumber -1 or WeekID = $CurrentAuction/WeekID -1 ) ]` (Result: **$PreviousAuction**)**
      3. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( BuyerCodeType != empty ) and ( AuctionUI.BidData_BuyerCode/AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction ) ]` (Result: **$BuyerCodeListNew**)**
      4. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( BuyerCodeType != empty ) and ( AuctionUI.BidData_BuyerCode/AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction ) ]` (Result: **$BuyerCodeListOld**)**
      5. **CreateList**
      6. **CreateList**
      7. **DB Retrieve **AuctionUI.BidData** Filter: `[(AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $CurrentAuction ) and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true]` (Result: **$BidDataListPrimaryNew**)**
      8. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataListClean**)**
      9. **List Operation: **Filter** on **$undefined** where `-1` (Result: **$BidDataListMinus1**)**
      10. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$BidDataListMinusEmpty**)**
      11. **List Operation: **Filter** on **$undefined** where `0` (Result: **$BidDataListMinusZero**)**
      12. **List Operation: **Union** on **$undefined** (Result: **$BidDataListMinusReady**)**
      13. **List Operation: **Subtract** on **$undefined** (Result: **$BidDataListSansMinus1New**)**
      14. 🔄 **LOOP:** For each **$IteratorBidDataNew** in **$BidDataListSansMinus1New**
         │ 1. **Update **$IteratorBidDataNew**
      - Set **BidQuantity** = `if $IteratorBidDataNew/BidQuantity < 0 then 0 else $IteratorBidDataNew/BidQuantity`**
         └─ **End Loop**
      15. 🔄 **LOOP:** For each **$IteratorBidDataMinusReadyNew** in **$BidDataListMinusReady**
         │ 1. 🔀 **DECISION:** `$IteratorBidDataMinusReadyNew/BidAmount != empty or $IteratorBidDataMinusReadyNew/BidAmount != -1`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBidDataMinusReadyNew**
      - Set **BidQuantity** = `if $IteratorBidDataMinusReadyNew/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity != empty then $IteratorBidDataMinusReadyNew/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity else 0`**
         │       2. **Add **$$IteratorBidDataMinusReadyNew** to/from list **$BidDataListSansMinus1New****
         │    ➔ **If [false]:**
         └─ **End Loop**
      16. **Commit/Save **$BidDataListMinusReady** to Database**
      17. **Commit/Save **$BidDataListSansMinus1New** to Database**
      18. **List Operation: **Union** on **$undefined** (Result: **$BidDataListCleanNew**)**
      19. **DB Retrieve **AuctionUI.BidData** Filter: `[(AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction or AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $PreviousAuction) and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true]` (Result: **$BidDataListPrimaryOld**)**
      20. **List Operation: **Filter** on **$undefined** where `-1` (Result: **$BidDataListMinus1Old**)**
      21. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$BidDataListMinusEmptyOld**)**
      22. **List Operation: **Filter** on **$undefined** where `0` (Result: **$BidDataListMinusZeroOld**)**
      23. **List Operation: **Union** on **$undefined** (Result: **$BidDataListMinusReadyOld**)**
      24. **List Operation: **Subtract** on **$undefined** (Result: **$BidDataListSansMinus1Old**)**
      25. 🔄 **LOOP:** For each **$IteratorBidDataOld** in **$BidDataListSansMinus1Old**
         │ 1. **Update **$IteratorBidDataOld**
      - Set **BidQuantity** = `if $IteratorBidDataOld/BidQuantity < 0 then 0 else $IteratorBidDataOld/BidQuantity`**
         └─ **End Loop**
      26. 🔄 **LOOP:** For each **$IteratorBidDataMinusReadyOld** in **$BidDataListMinusReadyOld**
         │ 1. 🔀 **DECISION:** `$IteratorBidDataMinusReadyOld/BidAmount != empty or $IteratorBidDataMinusReadyOld/BidAmount != -1`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBidDataMinusReadyOld**
      - Set **BidQuantity** = `if $IteratorBidDataMinusReadyOld/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity != empty then $IteratorBidDataMinusReadyOld/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity else 0`**
         │       2. **Add **$$IteratorBidDataMinusReadyOld** to/from list **$BidDataListSansMinus1Old****
         │    ➔ **If [false]:**
         └─ **End Loop**
      27. **Commit/Save **$BidDataListMinusReadyOld** to Database**
      28. **Commit/Save **$BidDataListSansMinus1Old** to Database**
      29. **List Operation: **Union** on **$undefined** (Result: **$BidDataListCleanOld**)**
      30. **DB Retrieve **AuctionUI.BuyerBidSummaryReport** Filter: `[ ( AuctionUI.BuyerBidSummaryReport_Week = $CurrentAuction ) ]` (Result: **$BuyerBidSummaryReportListExisting**)**
      31. 🔄 **LOOP:** For each **$IteratorBuyerCodeOld** in **$BuyerCodeListOld**
         │ 1. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCodeOld` (Result: **$OldBidDataList**)**
         │ 2. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCodeOld/Code` (Result: **$OldTupleMatch**)**
         │ 3. 🔀 **DECISION:** `$OldTupleMatch = empty`
         │    ➔ **If [true]:**
         │       1. **AggregateList**
         │       2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $PreviousAuction] [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData or AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true] [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData/AuctionUI.BidData_BuyerCode = $IteratorBuyerCodeOld]` (Result: **$AggregatedInventoryListOld**)**
         │       3. **AggregateList**
         │       4. **Create **AuctionUI.BuyerBidSummaryReportHelper** (Result: **$NewBuyerBidSummaryReportHelper**)
      - Set **BuyerCode** = `$IteratorBuyerCodeOld/Code`
      - Set **BuyerName** = `$IteratorBuyerCodeOld/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **UnitsBid2** = `$SumBidQuantOld`
      - Set **LotsBid2** = `$LotsBidOld`**
         │       5. **Add **$$NewBuyerBidSummaryReportHelper** to/from list **$BuyerBidSummaryReportHelperList****
         │    ➔ **If [false]:**
         └─ **End Loop**
      32. 🔄 **LOOP:** For each **$IteratorBuyerCodeNew** in **$BuyerCodeListNew**
         │ 1. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCodeNew` (Result: **$NewBidDataList**)**
         │ 2. **AggregateList**
         │ 3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $CurrentAuction] [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData or AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true] [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData/AuctionUI.BidData_BuyerCode = $IteratorBuyerCodeNew]` (Result: **$AggregatedInventoryListTupleNew**)**
         │ 4. **AggregateList**
         │ 5. **List Operation: **Find** on **$undefined** where `$IteratorBuyerCodeNew/Code` (Result: **$MatchExisting**)**
         │ 6. 🔀 **DECISION:** `$MatchExisting = empty`
         │    ➔ **If [true]:**
         │       1. **Create **AuctionUI.BuyerBidSummaryReport** (Result: **$NewBuyerBidSummaryReport**)
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
         │       2. **Add **$$NewBuyerBidSummaryReport** to/from list **$BuyerBidSummaryReportList****
         │    ➔ **If [false]:**
         │       1. **Update **$MatchExisting**
      - Set **LotsBid1** = `$LotsBidNew`
      - Set **UnitsBid1** = `$SumBidQuantNew`**
         └─ **End Loop**
      33. 🔄 **LOOP:** For each **$IteratorBuyerBidSummaryReport** in **$BuyerBidSummaryReportList**
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
      34. **Commit/Save **$BuyerBidSummaryReportList** to Database**
      35. **Commit/Save **$BuyerBidSummaryReportListExisting** to Database**
      36. **Maps to Page: **AuctionUI.BuyerSummaryReportOverview****
      37. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.