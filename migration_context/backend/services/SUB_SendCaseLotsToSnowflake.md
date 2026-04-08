# Microflow Detailed Specification: SUB_SendCaseLotsToSnowflake

### 📥 Inputs (Parameters)
- **$JSON_CaseLotData** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendCaseLotDetailsToSnowflake'`**
2. **Create Variable **$Description** = `'Sending CaseLot Details To Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
5. **CreateList**
6. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ARG_SON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_INPUT'`
      - Set **AGR_CONTENT** = `$JSON_CaseLotData`**
7. **Add **$$ARG_SON_CONTENT** to/from list **$StoreProcedureArgumentList****
8. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.PWS_CaseLotsSP`**
9. **JavaCallAction**
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.