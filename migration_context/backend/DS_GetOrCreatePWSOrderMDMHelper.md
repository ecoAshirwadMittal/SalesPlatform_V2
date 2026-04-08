# Microflow Detailed Specification: DS_GetOrCreatePWSOrderMDMHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSGetOrCreatePWSOrderMDMHelper'`**
2. **Create Variable **$Description** = `'Get or Create PWS Order MDM Helper.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **AuctionUI.ACT_GET_CurrentUser** (Result: **$EcoATMDirectUser**)**
5. **Retrieve related **PWSUserPersonalization_EcoATMDirectUser_Order** via Association from **$EcoATMDirectUser** (Result: **$PWSMDMHelper**)**
6. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$CaseLotsFeatureFlag**)**
7. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$AYYY_FeatureFlag**)**
8. 🔀 **DECISION:** `$PWSMDMHelper != empty`
   ➔ **If [true]:**
      1. **Update **$PWSMDMHelper** (and Save to DB)
      - Set **EnableCaseLots** = `$CaseLotsFeatureFlag`
      - Set **EnableAYYY** = `$AYYY_FeatureFlag`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$PWSMDMHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.PWSUserPersonalization** (Result: **$NewPWSMDMHelper**)
      - Set **PWSUserPersonalization_EcoATMDirectUser_Order** = `$EcoATMDirectUser`
      - Set **EnableAYYY** = `$AYYY_FeatureFlag`
      - Set **EnableCaseLots** = `$CaseLotsFeatureFlag`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$NewPWSMDMHelper`

**Final Result:** This process concludes by returning a [Object] value.