# Microflow Detailed Specification: ACT_GetBuyerBidDetailReport_fromSummary

### 📥 Inputs (Parameters)
- **$BuyerCode_String** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( Code = $BuyerCode_String ) ]` (Result: **$BuyerCode**)**
4. **DB Retrieve **AuctionUI.Auction**  (Result: **$LastTwoAuctions**)**
5. **List Operation: **Head** on **$undefined** (Result: **$FirstAuction**)**
6. **List Operation: **FindByExpression** on **$undefined** where `$currentObject!=$FirstAuction` (Result: **$SecondAuction**)**
7. **Retrieve related **SchedulingAuction_Auction** via Association from **$FirstAuction** (Result: **$FirstAuctionSchedulingAuctionList**)**
8. **Retrieve related **SchedulingAuction_Auction** via Association from **$SecondAuction** (Result: **$SecondAuctionSchedulingAuctionList**)**
9. **List Operation: **Head** on **$undefined** (Result: **$FirstAuctionSchedule**)**
10. **List Operation: **Head** on **$undefined** (Result: **$SecondAuctionSchedule**)**
11. **Create Variable **$OQLQuery** = `'SELECT bd."EcoID" as ProductId, ai."Model" as Model, ai."Name" as ModelName, bd."Merged_Grade" as Grade, MAX(CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN (CASE WHEN bd."SubmittedBidQuantity" = -1 OR bd."BidQuantity" IS NULL OR bd."SubmittedBidQuantity" > ai."TotalQuantity" THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END) END) AS Quantity1, MAX(CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year +''' THEN (CASE WHEN bd."SubmittedBidQuantity" = -1 OR bd."BidQuantity" IS NULL OR bd."SubmittedBidQuantity" > ai."TotalQuantity" THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END) END) AS Quantity2, MAX(CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN (CASE WHEN bd."SubmittedBidAmount" > 0 THEN bd."SubmittedBidAmount" ELSE NULL END) END) AS Bid1, MAX(CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year +''' THEN (CASE WHEN bd."SubmittedBidAmount" > 0 THEN bd."SubmittedBidAmount" ELSE NULL END) END) AS Bid2 FROM "AuctionUI"."BidData" bd JOIN bd/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory ai JOIN bd/AuctionUI.BidData_BidRound/AuctionUI.BidRound bdr JOIN bdr/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode bc JOIN bdr/AuctionUI.BidRound_SchedulingAuction/AuctionUI."SchedulingAuction" sa WHERE bd."EcoID" = ai."EcoId" AND bd."Merged_Grade" = ai."MergedGrade" AND sa."Auction_Week_Year" IN ('''+$FirstAuctionSchedule/Auction_Week_Year+''', '''+$SecondAuctionSchedule/Auction_Week_Year+''') AND (bd."SubmittedBidAmount" > 0 OR bd."SubmittedBidQuantity" > 0) AND bdr.Submitted AND bc."Code"= ('''+$BuyerCode/Code+''') GROUP BY bd."EcoID", bd."Merged_Grade",ai."Model",ai."Name" '`**
12. **JavaCallAction**
13. 🔀 **DECISION:** `$SummaryResult!=empty`
   ➔ **If [true]:**
      1. **AggregateList**
      2. **AggregateList**
      3. **List Operation: **FilterByExpression** on **$undefined** where `if ($currentObject/Quantity1!=empty and $currentObject/Bid1!=empty) then ($currentObject/Quantity1>0 and $currentObject/Bid1>0) else false` (Result: **$List_LotsBid1**)**
      4. **AggregateList**
      5. **List Operation: **FilterByExpression** on **$undefined** where `if ($currentObject/Quantity2!=empty and $currentObject/Bid2!=empty) then ($currentObject/Quantity2>0 and $currentObject/Bid2>0) else false` (Result: **$List_LotsBid2**)**
      6. **AggregateList**
      7. **Create **EcoATM_Reports.BuyerBidDetailReportHelperOb** (Result: **$NewBuyerBidSummaryReportHelper**)
      - Set **BuyerBidDetailReportHelperOb_BuyerBidDetailReport_NP** = `$SummaryResult`
      - Set **CurrentAuctionName** = `substring($FirstAuction/AuctionTitle, 8)`
      - Set **PreviousAuctionName** = `substring($SecondAuction/AuctionTitle,8)`
      - Set **LotsBid1** = `$LotsBid1`
      - Set **LotsBid2** = `$LotsBid2`
      - Set **UnitsBid1** = `$UnitsBid1`
      - Set **UnitsBid2** = `$UnitsBid2`
      - Set **UpOrDownLots** = `if $LotsBid1>$LotsBid2 then'Up' else 'Down'`
      - Set **UpOrDownBids** = `if $UnitsBid1>$UnitsBid2 then 'Up' else 'Down'`
      - Set **ShowTotals** = `true`**
      8. **Maps to Page: **EcoATM_Reports.AuctionBuyerBidDetailReport****
      9. **Call Microflow **Custom_Logging.SUB_Log_Info****
      10. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **EcoATM_Reports.BuyerBidDetailReportHelperOb** (Result: **$NewBuyerBidSummaryReportHelper_1**)
      - Set **BuyerBidDetailReportHelperOb_BuyerBidDetailReport_NP** = `$SummaryResult`
      - Set **CurrentAuctionName** = `substring($FirstAuction/AuctionTitle,8)`
      - Set **PreviousAuctionName** = `substring($SecondAuction/AuctionTitle,8)`
      - Set **ShowTotals** = `false`**
      2. **Maps to Page: **EcoATM_Reports.AuctionBuyerBidDetailReport****
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.