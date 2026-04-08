# Microflow Detailed Specification: SUB_NavigateToAggregatedInventoryPage

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.DS_GetOrCreateAggregatedInventoryHelper** (Result: **$AggInventoryHelper**)**
2. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekEndDateTime > '[%CurrentDateTime%]']` (Result: **$Week**)**
3. **Update **$AggInventoryHelper**
      - Set **AggInventoryHelper_Week** = `$Week`**
4. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventoryTotals****
5. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
6. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$AuctionExists**)**
7. **Update **$AggInventoryHelper**
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **HasAuction** = `$AuctionExists!=empty`
      - Set **isCurrentWeek** = `true`**
8. **Maps to Page: **AuctionUI.PG_AggregatedInventory****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.