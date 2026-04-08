# Microflow Detailed Specification: ACT_GetBidsReportsPage_2

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$week** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`**
2. **Create Variable **$year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
3. **DB Retrieve **AuctionUI.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$ExistingWeek**)**
4. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
5. 🔀 **DECISION:** `$Auction = empty`
   ➔ **If [true]:**
      1. **Show Message (Information): `No active auction is present at this time.`**
      2. **ShowHomePage**
      3. 🔀 **DECISION:** `1 = 2`
         ➔ **If [true]:**
            1. **Create **AuctionUI.Week** (Result: **$CurrentWeek**)
      - Set **WeekNumber** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`
      - Set **WeekStartDateTime** = `[%BeginOfCurrentWeek%]`
      - Set **WeekEndDateTime** = `[%EndOfCurrentWeek%]`
      - Set **WeekDisplay** = `formatDateTime([%CurrentDateTime%],'yyyy') + ' / WK' + formatDateTime([%CurrentDateTime%],'ww')`
      - Set **Year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`
      - Set **WeekDisplayShort** = `'WK' + formatDateTime([%CurrentDateTime%],'ww')`**
            2. **Create **AuctionUI.AuctionsBidReportHeader** (Result: **$NewAuctionsBidReportHeader**)
      - Set **AuctionsBidReportHeader_Week** = `$ExistingWeek`
      - Set **AuctionTitle** = `$ExistingWeek/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`**
            3. **Create **AuctionUI.AuctionsBidReportHelper** (Result: **$NewAuctionsBidReportHelper**)
      - Set **RoundOneStarted** = `false`
      - Set **RoundTwoStarted** = `false`
      - Set **UpsellRoundStarted** = `false`
      - Set **RoundOneComplete** = `false`
      - Set **RoundTwoComplete** = `false`
      - Set **UpsellRoundComplete** = `false`
      - Set **AuctionsBidReportHelper_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader`
      - Set **AuctionTitle** = `$NewAuctionsBidReportHeader/AuctionTitle`
      - Set **AuctionScheduled** = `false`
      - Set **RoundOneBuyerParticipation** = `''`
      - Set **RoundTwoBuyerParticipation** = `''`
      - Set **UpsellRoundBuyerParticipation** = `''`
      - Set **RoundOneStartTime** = `dateTime(2024, 4, 24, 9, 49, 38)`
      - Set **RoundTwoStartTime** = `dateTime(2024, 4, 24, 9, 49, 51)`
      - Set **UpsellRoundStartTime** = `dateTime(2024, 4, 24, 9, 50, 4)`
      - Set **RoundOneEndTime** = `dateTime(2024, 4, 24, 10, 40, 6)`
      - Set **RoundTwoEndTime** = `dateTime(2024, 4, 24, 10, 40, 17)`
      - Set **UpsellRoundEndTime** = `dateTime(2024, 4, 24, 10, 40, 28)`
      - Set **RoundOneLotsBid** = `''`
      - Set **RoundTwoLotsBid** = `''`
      - Set **UpsellRoundLotsBid** = `''`
      - Set **RoundOneRevenue** = `''`
      - Set **RoundTwoRevenue** = `''`
      - Set **UpsellRoundRevenue** = `''`
      - Set **RoundOneMargin** = `''`
      - Set **RoundTwoMargin** = `''`
      - Set **UpsellRoundMargin** = `''`
      - Set **RoundOneUnitsBid** = `''`
      - Set **RoundTwoUnitsBid** = `''`
      - Set **UpsellRoundUnitsBid** = `''`
      - Set **AuctionComplete** = `false`
      - Set **RoundOneFileLink** = `''`
      - Set **RoundTwoFileLink** = `''`
      - Set **UpsellRoundFileLink** = `''`**
            4. **Create **AuctionUI.AuctionsBidReportTuple** (Result: **$NewAuctionsBidReportTuple**)
      - Set **AuctionsBidReportTuple_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader`**
            5. **Create **AuctionUI.AuctionsBidReportTupleHelper** (Result: **$NewAuctionsBidReportTupleHelper**)
      - Set **AuctionsBidReportTupleHelper_AuctionsBidReportTuple** = `$NewAuctionsBidReportTuple`
      - Set **ProductID** = `empty`
      - Set **Model** = `''`
      - Set **ModelName** = `''`
      - Set **Grade** = `''`
      - Set **RoundOneQuantity** = `empty`
      - Set **RoundOneAverageBid** = `empty`
      - Set **RoundTwoQuantity** = `empty`
      - Set **RoundTwoAverageBid** = `empty`
      - Set **UpsellRoundQuantity** = `empty`
      - Set **UpsellRoundAverageBid** = `empty`
      - Set **Revenue** = `empty`
      - Set **Margin** = `empty`
      - Set **BidRoundID** = `empty`
      - Set **Round** = `empty`
      - Set **BidSubmitted** = `false`**
            6. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_NewWeek**)
      - Set **HasAuction** = `false`
      - Set **HasInventory** = `false`
      - Set **InventoryOverview_Helper_Week** = `$CurrentWeek`
      - Set **WasTitleEmpty** = `false`
      - Set **HasAuctionBeenTriggered** = `false`**
            7. **Maps to Page: **AuctionUI.Reports_UI_Overview****
            8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      2. **Call Microflow **AuctionUI.ACT_LoadScheduleAuction_Helper** (Result: **$SchedulingAuction_Helper**)**
      3. **Create Variable **$HasAuction** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction != empty`**
      4. **Retrieve related **Inventory_Week** via Association from **$ExistingWeek** (Result: **$InventoryList**)**
      5. **AggregateList**
      6. **Create Variable **$HasInventory** = `$InventoryListCount > 0`**
      7. **Create Variable **$HasSchedule** = `false`**
      8. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
      9. **Retrieve related **InventoryOverview_Helper_Week** via Association from **$ExistingWeek** (Result: **$InventoryOverview_HelperList**)**
      10. **AggregateList**
      11. 🔀 **DECISION:** `$InventoryOverview_HelperList != empty`
         ➔ **If [false]:**
            1. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_CurrentWeek**)
      - Set **HasAuction** = `$HasAuction`
      - Set **HasInventory** = `$HasInventory`
      - Set **InventoryOverview_Helper_Week** = `$ExistingWeek`
      - Set **HasAuctionBeenTriggered** = `false`
      - Set **WasTitleEmpty** = `false`**
            2. 🔀 **DECISION:** `$HasInventory`
               ➔ **If [false]:**
                  1. **Call Microflow **AuctionUI.ACT_StartInventoryUpload****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Create **AuctionUI.AuctionsBidReportHeader** (Result: **$NewAuctionsBidReportHeader_1**)
      - Set **AuctionsBidReportHeader_Week** = `$ExistingWeek`
      - Set **AuctionTitle** = `$ExistingWeek/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`**
                  2. **Create **AuctionUI.AuctionsBidReportHelper** (Result: **$NewAuctionsBidReportHelper_1**)
      - Set **RoundOneStarted** = `false`
      - Set **RoundTwoStarted** = `false`
      - Set **UpsellRoundStarted** = `false`
      - Set **RoundOneComplete** = `false`
      - Set **RoundTwoComplete** = `false`
      - Set **UpsellRoundComplete** = `false`
      - Set **AuctionsBidReportHelper_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader_1`
      - Set **AuctionTitle** = `$NewAuctionsBidReportHeader_1/AuctionTitle`
      - Set **AuctionScheduled** = `false`
      - Set **RoundOneBuyerParticipation** = `''`
      - Set **RoundTwoBuyerParticipation** = `''`
      - Set **UpsellRoundBuyerParticipation** = `''`
      - Set **RoundOneStartTime** = `dateTime(2024, 4, 24, 9, 49, 38)`
      - Set **RoundTwoStartTime** = `dateTime(2024, 4, 24, 9, 49, 51)`
      - Set **UpsellRoundStartTime** = `dateTime(2024, 4, 24, 9, 50, 4)`
      - Set **RoundOneEndTime** = `dateTime(2024, 4, 24, 10, 40, 6)`
      - Set **RoundTwoEndTime** = `dateTime(2024, 4, 24, 10, 40, 17)`
      - Set **UpsellRoundEndTime** = `dateTime(2024, 4, 24, 10, 40, 28)`
      - Set **RoundOneLotsBid** = `''`
      - Set **RoundTwoLotsBid** = `''`
      - Set **UpsellRoundLotsBid** = `''`
      - Set **RoundOneRevenue** = `''`
      - Set **RoundTwoRevenue** = `''`
      - Set **UpsellRoundRevenue** = `''`
      - Set **RoundOneMargin** = `''`
      - Set **RoundTwoMargin** = `''`
      - Set **UpsellRoundMargin** = `''`
      - Set **RoundOneUnitsBid** = `''`
      - Set **RoundTwoUnitsBid** = `''`
      - Set **UpsellRoundUnitsBid** = `''`
      - Set **AuctionComplete** = `false`
      - Set **RoundOneFileLink** = `''`
      - Set **RoundTwoFileLink** = `''`
      - Set **UpsellRoundFileLink** = `''`**
                  3. **Create **AuctionUI.AuctionsBidReportTuple** (Result: **$NewAuctionsBidReportTuple_1**)
      - Set **AuctionsBidReportTuple_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader_1`**
                  4. **Create **AuctionUI.AuctionsBidReportTupleHelper** (Result: **$NewAuctionsBidReportTupleHelper_1**)
      - Set **AuctionsBidReportTupleHelper_AuctionsBidReportTuple** = `$NewAuctionsBidReportTuple_1`
      - Set **ProductID** = `empty`
      - Set **Model** = `''`
      - Set **ModelName** = `''`
      - Set **Grade** = `''`
      - Set **RoundOneQuantity** = `empty`
      - Set **RoundOneAverageBid** = `empty`
      - Set **RoundTwoQuantity** = `empty`
      - Set **RoundTwoAverageBid** = `empty`
      - Set **UpsellRoundQuantity** = `empty`
      - Set **UpsellRoundAverageBid** = `empty`
      - Set **Revenue** = `empty`
      - Set **Margin** = `empty`
      - Set **BidRoundID** = `empty`
      - Set **Round** = `empty`
      - Set **BidSubmitted** = `false`**
                  5. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_NewWeek_1**)
      - Set **HasAuction** = `false`
      - Set **HasInventory** = `false`
      - Set **InventoryOverview_Helper_Week** = `$ExistingWeek`
      - Set **WasTitleEmpty** = `false`
      - Set **HasAuctionBeenTriggered** = `false`**
                  6. **Maps to Page: **AuctionUI.Reports_UI_Overview****
                  7. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **List Operation: **Head** on **$undefined** (Result: **$NewInventoryOverview_Helper**)**
            2. 🔀 **DECISION:** `$HasInventory`
               ➔ **If [false]:**
                  1. **Call Microflow **AuctionUI.ACT_StartInventoryUpload****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_CurrentWeek_1**)
      - Set **HasAuction** = `$NewInventoryOverview_Helper/HasAuction`
      - Set **HasInventory** = `$NewInventoryOverview_Helper/HasInventory`
      - Set **WasTitleEmpty** = `$NewInventoryOverview_Helper/WasTitleEmpty`
      - Set **InventoryOverview_Helper_Week** = `$NewInventoryOverview_Helper/AuctionUI.InventoryOverview_Helper_Week`
      - Set **HasAuctionBeenTriggered** = `$NewInventoryOverview_Helper/HasAuctionBeenTriggered`**
                  2. **Create **AuctionUI.AuctionsBidReportHeader** (Result: **$NewAuctionsBidReportHeader_2**)
      - Set **AuctionsBidReportHeader_Week** = `$ExistingWeek`
      - Set **AuctionTitle** = `$ExistingWeek/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`**
                  3. **Create **AuctionUI.AuctionsBidReportHelper** (Result: **$NewAuctionsBidReportHelper_2**)
      - Set **RoundOneStarted** = `false`
      - Set **RoundTwoStarted** = `false`
      - Set **UpsellRoundStarted** = `false`
      - Set **RoundOneComplete** = `false`
      - Set **RoundTwoComplete** = `false`
      - Set **UpsellRoundComplete** = `false`
      - Set **AuctionsBidReportHelper_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader_2`
      - Set **AuctionTitle** = `$NewAuctionsBidReportHeader_2/AuctionTitle`
      - Set **AuctionScheduled** = `false`
      - Set **RoundOneBuyerParticipation** = `''`
      - Set **RoundTwoBuyerParticipation** = `''`
      - Set **UpsellRoundBuyerParticipation** = `''`
      - Set **RoundOneStartTime** = `dateTime(2024, 4, 24, 9, 49, 38)`
      - Set **RoundTwoStartTime** = `dateTime(2024, 4, 24, 9, 49, 51)`
      - Set **UpsellRoundStartTime** = `dateTime(2024, 4, 24, 9, 50, 4)`
      - Set **RoundOneEndTime** = `dateTime(2024, 4, 24, 10, 40, 6)`
      - Set **RoundTwoEndTime** = `dateTime(2024, 4, 24, 10, 40, 17)`
      - Set **UpsellRoundEndTime** = `dateTime(2024, 4, 24, 10, 40, 28)`
      - Set **RoundOneLotsBid** = `''`
      - Set **RoundTwoLotsBid** = `''`
      - Set **UpsellRoundLotsBid** = `''`
      - Set **RoundOneRevenue** = `''`
      - Set **RoundTwoRevenue** = `''`
      - Set **UpsellRoundRevenue** = `''`
      - Set **RoundOneMargin** = `''`
      - Set **RoundTwoMargin** = `''`
      - Set **UpsellRoundMargin** = `''`
      - Set **RoundOneUnitsBid** = `''`
      - Set **RoundTwoUnitsBid** = `''`
      - Set **UpsellRoundUnitsBid** = `''`
      - Set **AuctionComplete** = `false`
      - Set **RoundOneFileLink** = `''`
      - Set **RoundTwoFileLink** = `''`
      - Set **UpsellRoundFileLink** = `''`**
                  4. **Create **AuctionUI.AuctionsBidReportTuple** (Result: **$NewAuctionsBidReportTuple_2**)
      - Set **AuctionsBidReportTuple_AuctionsBidReportHeader** = `$NewAuctionsBidReportHeader_2`**
                  5. **Create **AuctionUI.AuctionsBidReportTupleHelper** (Result: **$NewAuctionsBidReportTupleHelper_2**)
      - Set **AuctionsBidReportTupleHelper_AuctionsBidReportTuple** = `$NewAuctionsBidReportTuple_2`
      - Set **ProductID** = `empty`
      - Set **Model** = `''`
      - Set **ModelName** = `''`
      - Set **Grade** = `''`
      - Set **RoundOneQuantity** = `empty`
      - Set **RoundOneAverageBid** = `empty`
      - Set **RoundTwoQuantity** = `empty`
      - Set **RoundTwoAverageBid** = `empty`
      - Set **UpsellRoundQuantity** = `empty`
      - Set **UpsellRoundAverageBid** = `empty`
      - Set **Revenue** = `empty`
      - Set **Margin** = `empty`
      - Set **BidRoundID** = `empty`
      - Set **Round** = `empty`
      - Set **BidSubmitted** = `false`**
                  6. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_NewWeek_2**)
      - Set **HasAuction** = `false`
      - Set **HasInventory** = `false`
      - Set **InventoryOverview_Helper_Week** = `$ExistingWeek`
      - Set **WasTitleEmpty** = `false`
      - Set **HasAuctionBeenTriggered** = `false`**
                  7. **Maps to Page: **AuctionUI.Reports_UI_Overview****
                  8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.