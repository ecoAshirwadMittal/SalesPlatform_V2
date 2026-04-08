# Microflow Detailed Specification: ACT_PWSExpireDevicePriceHistoryInSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'ExpirePriceHistoryInSnowflake'`**
      2. **Create Variable **$Description** = `'Expire Price History in Snowflake'`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      4. **Create Variable **$ActingUser** = `$currentUser/Name`**
      5. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
      6. **CreateList**
      7. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentACTING_USER**)
      - Set **ARG_NAME** = `'ACTING_USER'`
      - Set **AGR_CONTENT** = `$ActingUser`**
      8. **Add **$$ArgumentACTING_USER** to/from list **$StoreProcedureArgumentList****
      9. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB+'.'+@EcoATM_PWS.PWS_ExpireDevicePriceSPROC`**
      10. **Call Microflow **Custom_Logging.SUB_Log_Info****
      11. **JavaCallAction**
      12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      13. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.