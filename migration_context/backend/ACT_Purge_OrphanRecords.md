# Microflow Detailed Specification: ACT_Purge_OrphanRecords

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
3. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
4. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
5. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      2. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.