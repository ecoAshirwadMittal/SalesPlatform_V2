# Microflow Detailed Specification: SUB_GetOrCreateRMAUiHelper

### 📥 Inputs (Parameters)
- **$RMAMasterHelper** (Type: EcoATM_RMA.RMAMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **RMAUiHelper_RMAMasterHelper** via Association from **$RMAMasterHelper** (Result: **$RMAUiHelperList**)**
2. 🔀 **DECISION:** `$RMAUiHelperList != empty`
   ➔ **If [false]:**
      1. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper**)
      - Set **RMAUiHelper_RMAMasterHelper** = `$RMAMasterHelper`**
      2. 🏁 **END:** Return `$NewRMAUiHelper`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$RMAUiHelper`

**Final Result:** This process concludes by returning a [Object] value.