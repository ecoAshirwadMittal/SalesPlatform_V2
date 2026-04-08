# Microflow Detailed Specification: SUB_GetAggregateTotals

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **ExecuteDatabaseQuery**
2. **List Operation: **Head** on **$undefined** (Result: **$NewAggregatedInventoryTotals**)**
3. 🏁 **END:** Return `$NewAggregatedInventoryTotals`

**Final Result:** This process concludes by returning a [Object] value.