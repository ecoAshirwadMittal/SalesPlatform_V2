# Microflow Detailed Specification: ACT_OpenInventoryOverviewForSelectedAuction

### 📥 Inputs (Parameters)
- **$SelectedWeek** (Type: EcoATM_MDM.Week)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuctionHelper_2** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Auction_Week** via Association from **$SelectedWeek** (Result: **$Auction**)**
2. **Retrieve related **AggregatedInventory_Week** via Association from **$SelectedWeek** (Result: **$AggregatedInventoryList**)**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
4. **Call Microflow **AuctionUI.ACT_LoadScheduleAuction_Helper** (Result: **$SchedulingAuction_Helper**)**
5. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `$Auction!=empty`
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **AggInventoryHelper_Week** = `$SelectedWeek`
      - Set **HasSchedule** = `$SchedulingAuctionList!=empty`
      - Set **HasAuctionBeenTriggered** = `$Auction!=empty`**
6. **Maps to Page: **AuctionUI.Inventory_Auction_Overview****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.