# Microflow Detailed Specification: ACT_RetrieveBuyerSummaryReport

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Call Microflow **EcoATM_Reports.SUB_RetrieveLatestSummaryReportBySession** (Result: **$BuyerBidSummaryReportHelper**)**
3. 🔀 **DECISION:** `$BuyerBidSummaryReportHelper=empty or addMinutes($BuyerBidSummaryReportHelper/createdDate,@AuctionUI.ReportsSessionCachingInMinutes)<[%CurrentDateTime%]`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.Auction**  (Result: **$LastTwoAuctions**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$FirstAuction**)**
      3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject!=$FirstAuction` (Result: **$SecondAuction**)**
      4. **Retrieve related **SchedulingAuction_Auction** via Association from **$FirstAuction** (Result: **$FirstAuctionSchedulingAuctionList**)**
      5. **Retrieve related **SchedulingAuction_Auction** via Association from **$SecondAuction** (Result: **$SecondAuctionSchedulingAuctionList**)**
      6. **List Operation: **Head** on **$undefined** (Result: **$FirstAuctionSchedule**)**
      7. **List Operation: **Head** on **$undefined** (Result: **$SecondAuctionSchedule**)**
      8. **Create Variable **$Variable** = `false`**
      9. **Create Variable **$OQLQuery** = `'SELECT b."CompanyName" as BuyerName, bc."Code" as BuyerCode, COUNT(DISTINCT CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN (CAST(bd."EcoID" AS STRING) + bd."Merged_Grade") END) AS LotsBid1, COUNT(DISTINCT CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year+''' THEN (CAST(bd."EcoID" AS STRING) + bd."Merged_Grade") END) AS LotsBid2, SUM(CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN CASE WHEN (bd."SubmittedBidQuantity" = -1 or bd."BidQuantity" is null or bd."BidQuantity">bd."MaximumQuantity") THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END END) AS UnitsBid1, SUM(CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year +''' THEN CASE WHEN (bd."SubmittedBidQuantity" = -1 or bd."BidQuantity" is null or bd."BidQuantity">bd."MaximumQuantity") THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END END) AS UnitsBid2, CASE WHEN COUNT(DISTINCT CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year+''' THEN bd."EcoID" END) IS NULL OR COUNT(DISTINCT CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN bd."EcoID" END) > COUNT(DISTINCT CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year+''' THEN bd."EcoID" END) THEN ''Up'' ELSE ''Down'' END AS UpOrDownLots, CASE WHEN SUM(CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year +''' THEN CASE WHEN (bd."SubmittedBidQuantity" = -1 or bd."SubmittedBidQuantity" is null or bd."BidQuantity">bd."MaximumQuantity") THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END END) IS NULL OR SUM(CASE WHEN sa."Auction_Week_Year" = '''+$FirstAuctionSchedule/Auction_Week_Year+''' THEN CASE WHEN (bd."SubmittedBidQuantity" = -1 or bd."SubmittedBidQuantity" is null or bd."BidQuantity">bd."MaximumQuantity") THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END END) > SUM(CASE WHEN sa."Auction_Week_Year" = '''+$SecondAuctionSchedule/Auction_Week_Year +''' THEN CASE WHEN (bd."SubmittedBidQuantity" = -1 or bd."SubmittedBidQuantity" is null or bd."BidQuantity">bd."MaximumQuantity") THEN bd."MaximumQuantity" ELSE bd."SubmittedBidQuantity" END END) THEN ''Up'' ELSE ''Down'' END AS UpOrDownUnits FROM "AuctionUI"."BidData" bd JOIN bd/AuctionUI.BidData_BidRound/AuctionUI.BidRound bdr JOIN bdr/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode bc JOIN bc/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer b JOIN bdr/AuctionUI.BidRound_SchedulingAuction/AuctionUI."SchedulingAuction" sa WHERE sa."Auction_Week_Year" IN ('''+$FirstAuctionSchedule/Auction_Week_Year+''', '''+$SecondAuctionSchedule/Auction_Week_Year+''') AND (bd."SubmittedBidAmount" > 0 OR bd."SubmittedBidQuantity" > 0) AND bdr.Submitted GROUP BY b."CompanyName", bc."Code" '`**
      10. **JavaCallAction**
      11. **Retrieve related **Auction_Week** via Association from **$FirstAuction** (Result: **$Week**)**
      12. **Create **EcoATM_Reports.BuyerBidSummaryReportHelper** (Result: **$NewBuyerBidSummaryReportHelper**)
      - Set **BuyerBidSummaryReport_NP_BuyerBidSummaryReportHelper** = `$SummaryResult`
      - Set **CurrentAuctionName** = `$FirstAuction/AuctionTitle`
      - Set **PreviousAuctionName** = `$SecondAuction/AuctionTitle`
      - Set **BuyerBidSummaryReportHelper_Session** = `$currentSession`**
      13. **Maps to Page: **EcoATM_Reports.BuyerSummaryReportOverviewNew****
      14. **Call Microflow **Custom_Logging.SUB_Log_Info****
      15. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_Reports.BuyerSummaryReportOverviewNew****
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.