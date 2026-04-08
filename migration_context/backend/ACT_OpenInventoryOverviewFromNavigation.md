# Microflow Detailed Specification: ACT_OpenInventoryOverviewFromNavigation

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuctionHelper_2** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.Auction_Week/EcoATM_MDM.Week !=empty]` (Result: **$Auction_MostRecent**)**
2. 🔀 **DECISION:** `$Auction_MostRecent != empty`
   ➔ **If [true]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction_MostRecent** (Result: **$Week**)**
      2. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
      3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction_MostRecent** (Result: **$SchedulingAuctionList**)**
      4. **Call Microflow **AuctionUI.ACT_LoadScheduleAuction_Helper** (Result: **$SchedulingAuction_Helper**)**
      5. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction!=empty`
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **AggInventoryHelper_Week** = `$Week`
      - Set **HasSchedule** = `$SchedulingAuctionList!=empty`
      - Set **HasAuctionBeenTriggered** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction!=empty`
      - Set **AggInventoryHelper_Auction** = `$Auction_MostRecent`**
      6. **Maps to Page: **AuctionUI.Inventory_Auction_Overview****
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `No active auction`**
      2. **ShowHomePage**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.