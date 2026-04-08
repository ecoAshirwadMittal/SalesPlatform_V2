# Microflow Detailed Specification: DS_GetOrCreatePWSConstants

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.PWSConstants**  (Result: **$PWSConstants**)**
2. 🔀 **DECISION:** `$PWSConstants!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$PWSConstants`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.PWSConstants** (Result: **$NewPWSConstants**)**
      2. 🏁 **END:** Return `$NewPWSConstants`

**Final Result:** This process concludes by returning a [Object] value.