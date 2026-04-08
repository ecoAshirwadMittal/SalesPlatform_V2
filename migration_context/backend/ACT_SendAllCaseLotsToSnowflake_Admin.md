# Microflow Detailed Specification: ACT_SendAllCaseLotsToSnowflake_Admin

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [false]:**
      1. **Show Message (Information): `Feature flag turned off. Cannot send data to Snowflake.`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.CaseLot**  (Result: **$CaseLotList**)**
      2. **Create Variable **$TimerName** = `'SendAllCaseLotsToSnowflakeAdmin'`**
      3. **Create Variable **$Description** = `'Sending All CaseLots to Snowflake Admin'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      5. **ExportXml**
      6. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
      7. **CreateList**
      8. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_SON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_INPUT'`
      - Set **AGR_CONTENT** = `$JSON_CaseLotData`**
      9. **Add **$$ARG_SON_CONTENT** to/from list **$StoreProcedureArgumentList****
      10. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.PWS_CaseLotsSP`**
      11. **JavaCallAction**
      12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      13. **Show Message (Information): `Sending CaseLots to Snowflake Complete!`**
      14. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.