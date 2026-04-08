# Microflow Detailed Specification: ACT_Create_Auction

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
2. **Create **AuctionUI.Auction** (Result: **$NewAuction**)
      - Set **Auction_Week** = `$Week`**
3. **Update **$AggInventoryHelper**
      - Set **HasAuctionBeenTriggered** = `true`
      - Set **AggInventoryHelper_Auction** = `$NewAuction`
      - Set **WasTitleEmpty** = `trim($SchedulingAuction_Helper/Auction_Name)= ''`
      - Set **HasAuction** = `true`
      - Set **HasSchedule** = `false`**
4. **Update **$SchedulingAuction_Helper**
      - Set **Auction_Name** = `if trim($AggInventoryHelper/AuctionName) != '' then 'Auction ' + $Week/WeekDisplay + ' ' + $AggInventoryHelper/AuctionName else 'Auction ' + $Week/WeekDisplay`**
5. **Update **$NewAuction**
      - Set **AuctionTitle** = `$SchedulingAuction_Helper/Auction_Name`**
6. **Create Variable **$StartHourOffset** = `16`**
7. **Update **$SchedulingAuction_Helper**
      - Set **Round1_Start_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset)`
      - Set **Round1_End_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+87)`
      - Set **Round2_Start_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+88)`
      - Set **Round2_End_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+112)`
      - Set **Round3_Start_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+113)`
      - Set **Round3_End_Datetime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+140)`
      - Set **SchedulingAuction_Helper_Auction** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction`
      - Set **Round1_Status** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`
      - Set **isRound2Active** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`
      - Set **Round3_Status** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`
      - Set **Auction_Week_Year** = `$Week/WeekDisplay`**
8. **Call Microflow **AuctionUI.ACT_Create_SchedulingAuction_Helper_Default** (Result: **$SchedulingAuction_Helper_2**)**
9. **Call Microflow **AuctionUI.ACT_LoadBuyerTotals****
10. **Maps to Page: **AuctionUI.Inventory_Auction_Overview****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.