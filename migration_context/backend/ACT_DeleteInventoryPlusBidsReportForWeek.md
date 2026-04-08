# Microflow Detailed Specification: ACT_DeleteInventoryPlusBidsReportForWeek

### 📥 Inputs (Parameters)
- **$Week** (Type: AuctionUI.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Inventory** Filter: `[(AuctionUI.Inventory_Week = $Week)]` (Result: **$InventoryList_1**)**
2. **AggregateList**
3. **Create Variable **$Processed_Count** = `0`**
4. **Create Variable **$Limit** = `@AuctionUI.const_AggregatedInventoryOffsetLimit`**
5. **Create Variable **$Offset_Count** = `0`**
6. **DB Retrieve **AuctionUI.Inventory** Filter: `[(AuctionUI.Inventory_Week = $Week)]` (Result: **$InventoryList**)**
7. **Delete**
8. **JavaCallAction**
9. **Update Variable **$Processed_Count** = `$Processed_Count + $Limit`**
10. 🔀 **DECISION:** `$Processed_Count>=$TotalInventoryCount`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_DeleteAggregatedInventoryForWeek****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$Offset_Count** = `$Offset_Count + $Limit`**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.