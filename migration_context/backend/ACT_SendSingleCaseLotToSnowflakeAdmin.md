# Microflow Detailed Specification: ACT_SendSingleCaseLotToSnowflakeAdmin

### 📥 Inputs (Parameters)
- **$CaseLot** (Type: EcoATM_PWSMDM.CaseLot)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [false]:**
      1. **Show Message (Information): `Feature flag turned off. Cannot send data to Snowflake.`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **CreateList**
      2. **Add **$$CaseLot** to/from list **$CaseLotList****
      3. **Create Variable **$TimerName** = `'SendSingleCaseLotToSnowflakeAdmin'`**
      4. **Create Variable **$Description** = `'Sending Single CaseLot to Snowflake Admin'`**
      5. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      6. **ExportXml**
      7. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
      8. **CreateList**
      9. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_SON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_INPUT'`
      - Set **AGR_CONTENT** = `$JSON_CaseLotData`**
      10. **Add **$$ARG_SON_CONTENT** to/from list **$StoreProcedureArgumentList****
      11. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.PWS_CaseLotsSP`**
      12. **JavaCallAction**
      13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      14. **Show Message (Information): `Sending CaseLot to Snowflake Complete!`**
      15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.