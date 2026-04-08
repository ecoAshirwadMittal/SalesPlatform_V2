# Microflow Detailed Specification: SUB_GetOrCreatePOHelper

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
2. **Retrieve related **POHelper_Account** via Association from **$Account** (Result: **$POHelper**)**
3. 🔀 **DECISION:** `$POHelper != empty`
   ➔ **If [true]:**
      1. **Update **$POHelper**
      - Set **ENUM_ActionType** = `empty`
      - Set **EnablePOUpdate** = `false`
      - Set **MissingBuyerCodeValidation** = `false`
      - Set **InvalidFileValidation** = `false`
      - Set **InValidPOPeriod** = `false`**
      2. 🏁 **END:** Return `$POHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_PO.POHelper** (Result: **$NewPOHelper**)
      - Set **POHelper_Account** = `$Account`**
      2. 🏁 **END:** Return `$NewPOHelper`

**Final Result:** This process concludes by returning a [Object] value.