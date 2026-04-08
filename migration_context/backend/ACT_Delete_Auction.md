# Microflow Detailed Specification: ACT_Delete_Auction

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
2. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
3. **Delete**
4. **ExecuteDatabaseQuery**
5. **ExecuteDatabaseQuery**
6. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
7. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `false`
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **HasSchedule** = `false`**
8. **Maps to Page: **AuctionUI.PG_AggregatedInventory****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.