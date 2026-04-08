# Microflow Detailed Specification: SUB_SendDevicePricingDataToSnowflake

### 📥 Inputs (Parameters)
- **$User** (Type: Variable)
- **$DeviceHistoryJSON** (Type: Variable)
- **$FutureDate** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendPricingDetailsToSnowflake'`**
2. **Create Variable **$Description** = `'Sending Pricing Details To Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
5. **CreateList**
6. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_SON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_INPUT'`
      - Set **AGR_CONTENT** = `$DeviceHistoryJSON`**
7. **Add **$$ARG_SON_CONTENT** to/from list **$StoreProcedureArgumentList****
8. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_USER**)
      - Set **ARG_NAME** = `'UPDATED_BY'`
      - Set **AGR_CONTENT** = `$User`**
9. **Add **$$ARG_USER** to/from list **$StoreProcedureArgumentList****
10. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_FutureDate**)
      - Set **ARG_NAME** = `'FUTURE_DATE'`
      - Set **AGR_CONTENT** = `$FutureDate`**
11. **Add **$$ARG_FutureDate** to/from list **$StoreProcedureArgumentList****
12. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.SendPricingDataSP`**
13. **JavaCallAction**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.