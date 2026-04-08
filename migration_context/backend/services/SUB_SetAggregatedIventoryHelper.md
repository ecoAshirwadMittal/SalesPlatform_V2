# Microflow Detailed Specification: SUB_SetAggregatedIventoryHelper

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
2. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventoryTotals****
3. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
4. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.Auction_Week=$Week]` (Result: **$Auction**)**
5. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekEndDateTime > '[%CurrentDateTime%]']` (Result: **$CurrentWeek**)**
6. **Update **$AggInventoryHelper**
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **HasAuction** = `$Auction!=empty`
      - Set **isCurrentWeek** = `$CurrentWeek=$Week`**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.