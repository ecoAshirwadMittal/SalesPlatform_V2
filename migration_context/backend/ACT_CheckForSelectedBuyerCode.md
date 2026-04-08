# Microflow Detailed Specification: ACT_CheckForSelectedBuyerCode

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.DS_BuyerCodeBySession** (Result: **$BuyerCode**)**
2. 🔀 **DECISION:** `$BuyerCode = empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BuyerCode`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
      2. 🏁 **END:** Return `$BuyerCode`

**Final Result:** This process concludes by returning a [Object] value.