# Microflow Detailed Specification: SUB_DAWeek_GetOrCreate

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **DAWeek_Week** via Association from **$Week** (Result: **$DAWeek**)**
2. 🔀 **DECISION:** `$DAWeek != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$DAWeek`
   ➔ **If [false]:**
      1. **Create **EcoATM_DA.DAWeek** (Result: **$NewDAWeek**)
      - Set **DAWeek_Week** = `$Week`**
      2. 🏁 **END:** Return `$NewDAWeek`

**Final Result:** This process concludes by returning a [Object] value.