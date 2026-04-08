# Microflow Detailed Specification: ACT_Open_CurrentWeek_InventoryAuctionOverview

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.DS_GetOrCreateAggregatedInventoryHelper** (Result: **$AggInventoryHelper**)**
2. **Call Microflow **AuctionUI.GetOrCreateSchedulingAuctionHelper** (Result: **$SchedulingAuction_Helper**)**
3. **Call Microflow **AuctionUI.ACT_OpenInventoryOverviewFromNavigation****
4. **Call Microflow **AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.