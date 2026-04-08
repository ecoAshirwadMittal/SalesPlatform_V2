# Microflow Detailed Specification: SUB_ReserveBidData_UpdateSnowflake

### 📥 Inputs (Parameters)
- **$JSONContent** (Type: Variable)
- **$updatedBy** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
3. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$JSONContent!=empty`
         ➔ **If [true]:**
            1. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
            2. **CreateList**
            3. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentJSON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_CONTENT'`
      - Set **AGR_CONTENT** = `$JSONContent`**
            4. **Add **$$ArgumentJSON_CONTENT** to/from list **$StoreProcedureArgumentList****
            5. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentACTING_USER**)
      - Set **ARG_NAME** = `'ACTING_USER'`
      - Set **AGR_CONTENT** = `$updatedBy`**
            6. **Add **$$ArgumentACTING_USER** to/from list **$StoreProcedureArgumentList****
            7. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ 'AUCTIONS.UPSERT_RESERVE_BID(?,?)'`**
            8. **Call Microflow **Custom_Logging.SUB_Log_Info****
            9. **JavaCallAction**
            10. 🔀 **DECISION:** `$IsSuccess`
               ➔ **If [true]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.