# Microflow Detailed Specification: SUB_GetOrCreateAgrregateTotals

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.DS_GetOrCreateAggregatedInventoryTotalsByWeek** (Result: **$AggreegatedInventoryTotal**)**
2. **Update **$AggreegatedInventoryTotal** (and Save to DB)
      - Set **AvgTargetPrice** = `$AggregatedInventory/AvgTargetPrice`
      - Set **AvgPayout** = `$AggregatedInventory/AvgPayout`
      - Set **TotalPayout** = `$AggregatedInventory/TotalPayout`
      - Set **DWAvgTargetPrice** = `$AggregatedInventory/DWAvgTargetPrice`
      - Set **DWAvgPayout** = `$AggregatedInventory/DWAvgPayout`
      - Set **DWTotalQuantity** = `$AggregatedInventory/DWTotalQuantity`
      - Set **TotalQuantity** = `$AggregatedInventory/TotalQuantity`
      - Set **DWTotalPayout** = `$AggregatedInventory/DWTotalPayout`**
3. 🏁 **END:** Return `$AggreegatedInventoryTotal`

**Final Result:** This process concludes by returning a [Object] value.