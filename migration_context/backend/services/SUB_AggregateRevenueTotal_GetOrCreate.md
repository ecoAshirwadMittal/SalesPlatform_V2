# Microflow Detailed Specification: SUB_AggregateRevenueTotal_GetOrCreate

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AgregateRevenueTotal_DAWeek** via Association from **$DAWeek** (Result: **$AggregateRevenueTotal**)**
2. 🔀 **DECISION:** `$AggregateRevenueTotal != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$AggregateRevenueTotal`
   ➔ **If [false]:**
      1. **Create **EcoATM_DA.AgregateRevenueTotal** (Result: **$NewAggregateRevenueTotal**)
      - Set **AgregateRevenueTotal_DAWeek** = `$DAWeek`**
      2. 🏁 **END:** Return `$NewAggregateRevenueTotal`

**Final Result:** This process concludes by returning a [Object] value.