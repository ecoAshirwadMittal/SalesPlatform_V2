# Microflow Detailed Specification: SUB_UpdateAllBuyersToSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBuyerToSnowflake`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_MDM.SUB_SendAllBuyersToSnowflake****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.