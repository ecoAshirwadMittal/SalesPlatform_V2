# Microflow Detailed Specification: SUB_Configuration_FindOrCreate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **DocumentGeneration.Configuration**  (Result: **$Configuration**)**
2. 🔀 **DECISION:** `$Configuration != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Configuration`
   ➔ **If [false]:**
      1. **Create **DocumentGeneration.Configuration** (Result: **$NewConfiguration**)**
      2. 🏁 **END:** Return `$NewConfiguration`

**Final Result:** This process concludes by returning a [Object] value.