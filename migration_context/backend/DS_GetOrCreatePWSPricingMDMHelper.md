# Microflow Detailed Specification: DS_GetOrCreatePWSPricingMDMHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSGetCreatePricingMDMHelper'`**
2. **Create Variable **$Description** = `'Get or Create PWS Pricing MDM Helper.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **AuctionUI.ACT_GET_CurrentUser** (Result: **$EcoATMDirectUser**)**
5. **Retrieve related **PWSUserPersonalization_EcoATMDirectUser_Pricing** via Association from **$EcoATMDirectUser** (Result: **$PWSMDMHelper**)**
6. 🔀 **DECISION:** `$PWSMDMHelper != empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `$PWSMDMHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.PWSUserPersonalization** (Result: **$NewPWSMDMHelper**)
      - Set **PWSUserPersonalization_EcoATMDirectUser_Pricing** = `$EcoATMDirectUser`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$NewPWSMDMHelper`

**Final Result:** This process concludes by returning a [Object] value.