# Microflow Detailed Specification: ACT_RequestRMA

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$IsClosePage** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_UserManagement.DS_GetCurrentEcoATMDirectUser** (Result: **$EcoATMDirectUser**)**
2. **Create **EcoATM_RMA.RMA** (Result: **$NewRMA**)
      - Set **RMA_BuyerCode** = `$BuyerCode`
      - Set **RMA_EcoATMDirectUser_SubmittedBy** = `$EcoATMDirectUser`**
3. 🔀 **DECISION:** `$IsClosePage`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Maps to Page: **EcoATM_RMA.RequestRMA****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_RMA.RequestRMA****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.