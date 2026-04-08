# Microflow Detailed Specification: ACT_GetInventoryWeekCount

### 📥 Inputs (Parameters)
- **$Week** (Type: AuctionUI.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Inventory_Week** via Association from **$Week** (Result: **$InventoryList**)**
2. **AggregateList**
3. 🏁 **END:** Return `$Count`

**Final Result:** This process concludes by returning a [Integer] value.