# Microflow Detailed Specification: SUB_SendCaseLotsToSnowflakeInitiate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.CaseLot**  (Result: **$CaseLotList**)**
2. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
3. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'InitiateSendCaseLotsToSnowflake'`**
      2. **Create Variable **$Description** = `'Initiate Sending CaseLots to Snowflake'`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      4. **ExportXml**
      5. **Call Microflow **EcoATM_PWS.SUB_SendCaseLotsToSnowflake****
      6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.