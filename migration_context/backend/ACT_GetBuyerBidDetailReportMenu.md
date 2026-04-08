# Microflow Detailed Specification: ACT_GetBuyerBidDetailReportMenu

### 📥 Inputs (Parameters)
- **$WeekOld** (Type: AuctionUI.Week)
- **$WeekNew** (Type: AuctionUI.Week)
- **$BuyerCode** (Type: AuctionUI.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[ ( EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $WeekNew or EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $WeekNew ) and EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true ]` (Result: **$BidDataList**)**
2. **DB Retrieve **AuctionUI.BuyerBidDetailReport** Filter: `[ ( BuyerCode = $BuyerCode/Code ) ]` (Result: **$BuyerBidDetailReportListPreExisting**)**
3. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataListClean**)**
4. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ ( AuctionUI.AggregatedInventory_Week = $WeekNew ) and ( EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/BidAmount > 0 and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code ) ]` (Result: **$AggregatedInventoryListNew**)**
5. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[ ( EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $WeekOld or EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $WeekOld ) and EcoATM_Buyer.BidData_BuyerCode = $BuyerCode and EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true ]` (Result: **$BidDataListOld**)**
6. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataListCleanOld**)**
7. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ ( AuctionUI.AggregatedInventory_Week = $WeekOld ) and ( EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/BidAmount > 0 and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code ) ]` (Result: **$AggregatedInventoryListOld**)**
8. **CreateList**
9. **CreateList**
10. **Create Variable **$UnitsBidOld** = `0`**
11. **Create Variable **$UnitsBidNew** = `0`**
12. **Create Variable **$LotsBidOld** = `0`**
13. **Create Variable **$LotsBidNew** = `0`**
14. 🔄 **LOOP:** For each **$IteratorAggregatedInventoryOld** in **$AggregatedInventoryListOld**
   │ 1. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryOld` (Result: **$FilteredAssociationOld**)**
   │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryOld/MergedGrade` (Result: **$FilteredGradeOld**)**
   │ 3. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryOld/EcoId` (Result: **$NewBidDataListOld**)**
   │ 4. 🔀 **DECISION:** `$NewBidDataListOld != empty`
   │    ➔ **If [true]:**
   │       1. **AggregateList**
   │       2. **Update Variable **$UnitsBidOld** = `$UnitsBidOld + $MaxBidQuant2`**
   │       3. **Create Variable **$QuantOld** = `0`**
   │       4. **Create Variable **$BidAmountOld** = `0`**
   │       5. **Create Variable **$AveBidCounterOld** = `0`**
   │       6. 🔄 **LOOP:** For each **$IteratorBidDataOld** in **$NewBidDataListOld**
   │          │ 1. **Update Variable **$QuantOld** = `$QuantOld + $IteratorBidDataOld/SanitizedBidQuantity`**
   │          │ 2. **Update Variable **$AveBidCounterOld** = `$AveBidCounterOld + 1`**
   │          │ 3. **Update Variable **$BidAmountOld** = `$BidAmountOld + $IteratorBidDataOld/BidAmount`**
   │          └─ **End Loop**
   │       7. **Create Variable **$AveBidOld** = `if $BidAmountOld = 0 or $BidAmountOld = empty or $AveBidCounterOld = 0 or $AveBidCounterOld = empty then 0 else $BidAmountOld div $AveBidCounterOld`**
   │       8. **Update Variable **$LotsBidOld** = `$LotsBidOld + 1`**
   │       9. **Create **AuctionUI.BuyerBidDetailReportHelper** (Result: **$NewBuyerBidDetailReportHelper**)
      - Set **AverageBid2** = `$AveBidOld`
      - Set **Quantity2** = `$QuantOld`
      - Set **ProductID** = `$IteratorAggregatedInventoryOld/EcoId`
      - Set **Model** = `$IteratorAggregatedInventoryOld/Model`
      - Set **ModelName** = `$IteratorAggregatedInventoryOld/Name`
      - Set **Merged_Grade** = `$IteratorAggregatedInventoryOld/MergedGrade`**
   │       10. **Add **$$NewBuyerBidDetailReportHelper** to/from list **$BuyerBidDetailReportHelperList****
   │    ➔ **If [false]:**
   └─ **End Loop**
15. 🔄 **LOOP:** For each **$IteratorAggregatedInventoryNew** in **$AggregatedInventoryListNew**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventoryNew/EcoId` (Result: **$NewBuyerBidDetailReport**)**
   │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/EcoId` (Result: **$PreExistingTuple**)**
   │ 3. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/Model` (Result: **$PreExistingTuple_1**)**
   │ 4. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/Name` (Result: **$PreExistingTuple_2**)**
   │ 5. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventoryNew/MergedGrade` (Result: **$PreExistingTuple_3**)**
   │ 6. 🔀 **DECISION:** `$NewBuyerBidDetailReport = empty`
   │    ➔ **If [true]:**
   │       1. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew` (Result: **$FilteredAssociation**)**
   │       2. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/MergedGrade` (Result: **$FilteredGrade**)**
   │       3. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/EcoId` (Result: **$NewBidDataList**)**
   │       4. 🔀 **DECISION:** `$NewBidDataList != empty`
   │          ➔ **If [true]:**
   │             1. **AggregateList**
   │             2. **Update Variable **$UnitsBidNew** = `$UnitsBidNew + $MaxBidQuant2_1`**
   │             3. **Create Variable **$Quant** = `0`**
   │             4. **Create Variable **$BidAmount** = `0`**
   │             5. **Create Variable **$AveBidCounter** = `0`**
   │             6. 🔄 **LOOP:** For each **$IteratorBidDataNew** in **$NewBidDataList**
   │                │ 1. **Update Variable **$Quant** = `$Quant + $IteratorBidDataNew/SanitizedBidQuantity`**
   │                │ 2. **Update Variable **$AveBidCounter** = `$AveBidCounter + 1`**
   │                │ 3. **Update Variable **$BidAmount** = `$BidAmount + $IteratorBidDataNew/BidAmount`**
   │                └─ **End Loop**
   │             7. **Create Variable **$AveBid** = `if $BidAmount = 0 or $BidAmount = empty or $AveBidCounter = 0 or $AveBidCounter = empty then 0 else $BidAmount div $AveBidCounter`**
   │             8. **Update Variable **$LotsBidNew** = `$LotsBidNew + 1`**
   │             9. 🔀 **DECISION:** `$PreExistingTuple_3 = empty`
   │                ➔ **If [true]:**
   │                   1. **Create **AuctionUI.BuyerBidDetailReport** (Result: **$NewBuyerBidDetailReportTuple**)
      - Set **ProductID** = `$IteratorAggregatedInventoryNew/EcoId`
      - Set **Model** = `$IteratorAggregatedInventoryNew/Model`
      - Set **ModelName** = `$IteratorAggregatedInventoryNew/Name`
      - Set **Merged_Grade** = `$IteratorAggregatedInventoryNew/MergedGrade`
      - Set **AverageBid1** = `$AveBid`
      - Set **Quantity1** = `$Quant`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **WeekID** = `$WeekNew/WeekID`**
   │                   2. **Add **$$NewBuyerBidDetailReportTuple** to/from list **$BuyerBidDetailReportListPreExisting****
   │                   3. **Create Variable **$Variable** = `0`**
   │                ➔ **If [false]:**
   │                   1. **Update **$PreExistingTuple_3**
      - Set **AverageBid1** = `$AveBid`
      - Set **Quantity1** = `$Quant`
      - Set **WeekID** = `$WeekNew/WeekID`**
   │          ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew` (Result: **$FilteredAssociation**)**
   │       2. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/MergedGrade` (Result: **$FilteredGrade**)**
   │       3. **List Operation: **Filter** on **$undefined** where `$IteratorAggregatedInventoryNew/EcoId` (Result: **$NewBidDataList**)**
   │       4. 🔀 **DECISION:** `$NewBidDataList != empty`
   │          ➔ **If [true]:**
   │             1. **AggregateList**
   │             2. **Update Variable **$UnitsBidNew** = `$UnitsBidNew + $MaxBidQuant2_1`**
   │             3. **Create Variable **$Quant** = `0`**
   │             4. **Create Variable **$BidAmount** = `0`**
   │             5. **Create Variable **$AveBidCounter** = `0`**
   │             6. 🔄 **LOOP:** For each **$IteratorBidDataNew** in **$NewBidDataList**
   │                │ 1. **Update Variable **$Quant** = `$Quant + $IteratorBidDataNew/SanitizedBidQuantity`**
   │                │ 2. **Update Variable **$AveBidCounter** = `$AveBidCounter + 1`**
   │                │ 3. **Update Variable **$BidAmount** = `$BidAmount + $IteratorBidDataNew/BidAmount`**
   │                └─ **End Loop**
   │             7. **Create Variable **$AveBid** = `if $BidAmount = 0 or $BidAmount = empty or $AveBidCounter = 0 or $AveBidCounter = empty then 0 else $BidAmount div $AveBidCounter`**
   │             8. **Update Variable **$LotsBidNew** = `$LotsBidNew + 1`**
   │             9. 🔀 **DECISION:** `$PreExistingTuple_3 = empty`
   │                ➔ **If [true]:**
   │                   1. **Create **AuctionUI.BuyerBidDetailReport** (Result: **$NewBuyerBidDetailReportTuple**)
      - Set **ProductID** = `$IteratorAggregatedInventoryNew/EcoId`
      - Set **Model** = `$IteratorAggregatedInventoryNew/Model`
      - Set **ModelName** = `$IteratorAggregatedInventoryNew/Name`
      - Set **Merged_Grade** = `$IteratorAggregatedInventoryNew/MergedGrade`
      - Set **AverageBid1** = `$AveBid`
      - Set **Quantity1** = `$Quant`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **WeekID** = `$WeekNew/WeekID`**
   │                   2. **Add **$$NewBuyerBidDetailReportTuple** to/from list **$BuyerBidDetailReportListPreExisting****
   │                   3. **Create Variable **$Variable** = `0`**
   │                ➔ **If [false]:**
   │                   1. **Update **$PreExistingTuple_3**
      - Set **AverageBid1** = `$AveBid`
      - Set **Quantity1** = `$Quant`
      - Set **WeekID** = `$WeekNew/WeekID`**
   │          ➔ **If [false]:**
   └─ **End Loop**
16. **Create Variable **$LotsBidArrow** = `if $LotsBidNew > $LotsBidOld then '⇧' else if $LotsBidNew < $LotsBidOld then '⇩' else ''`**
17. **Create Variable **$UnitsBidArrow** = `if $UnitsBidNew > $UnitsBidOld then '⇧' else if $UnitsBidNew < $UnitsBidOld then '⇩' else ''`**
18. 🔄 **LOOP:** For each **$IteratorBuyerBidDetailReport** in **$BuyerBidDetailReportListPreExisting**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorBuyerBidDetailReport/BuyerCode` (Result: **$Match**)**
   │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerBidDetailReport/ProductID` (Result: **$Match2**)**
   │ 3. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerBidDetailReport/Model` (Result: **$Match2_1**)**
   │ 4. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerBidDetailReport/ModelName` (Result: **$Match2_2**)**
   │ 5. **List Operation: **Find** on **$undefined** where `$IteratorBuyerBidDetailReport/Merged_Grade` (Result: **$MatchFinal**)**
   │ 6. 🔀 **DECISION:** `$MatchFinal != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorBuyerBidDetailReport**
      - Set **AverageBid2** = `$MatchFinal/AverageBid2`
      - Set **Quantity2** = `$MatchFinal/Quantity2`
      - Set **UnitsBid1** = `$UnitsBidNew`
      - Set **UnitsBid2** = `$UnitsBidOld`
      - Set **LotsBid1** = `$LotsBidNew`
      - Set **LotsBid2** = `$LotsBidOld`
      - Set **UpOrDownLotsBid** = `$LotsBidArrow`
      - Set **UpOrDownUnitsBid** = `$UnitsBidArrow`**
   │    ➔ **If [false]:**
   └─ **End Loop**
19. **Create **AuctionUI.BuyerBidUnitsLotsHelper** (Result: **$NewBuyerBidUnitsLotsHelper**)
      - Set **LotsBidNew** = `$LotsBidNew`
      - Set **LotsBidOld** = `$LotsBidOld`
      - Set **UnitsBidNew** = `$UnitsBidNew`
      - Set **UnitsBidOld** = `$UnitsBidOld`
      - Set **LotsArrow** = `$LotsBidArrow`
      - Set **UnitsArrow** = `$UnitsBidArrow`**
20. **Commit/Save **$BuyerBidDetailReportList** to Database**
21. **Commit/Save **$BuyerBidDetailReportListPreExisting** to Database**
22. **Delete**
23. **Maps to Page: **AuctionUI.AuctionBuyerBidDetailReport****
24. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.