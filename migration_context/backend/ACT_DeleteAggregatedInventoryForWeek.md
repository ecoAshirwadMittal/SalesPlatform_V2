# Microflow Detailed Specification: ACT_DeleteAggregatedInventoryForWeek

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[(AuctionUI.AggregatedInventory_Week = $Week)]` (Result: **$InventoryList_1**)**
2. **AggregateList**
3. **Create Variable **$Processed_Count** = `0`**
4. **Create Variable **$Limit** = `@AuctionUI.const_AggregatedInventoryOffsetLimit`**
5. **Create Variable **$Offset_Count** = `0`**
6. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[(AuctionUI.AggregatedInventory_Week= $Week)]` (Result: **$InventoryList**)**
7. **Delete**
8. **JavaCallAction**
9. **Update Variable **$Processed_Count** = `$Processed_Count + $Limit`**
10. 🔀 **DECISION:** `$Processed_Count>=$TotalInventoryCount`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**

**Final Result:** This process concludes by returning a [Void] value.