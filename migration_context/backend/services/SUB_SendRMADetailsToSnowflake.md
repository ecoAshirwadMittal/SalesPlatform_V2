# Microflow Detailed Specification: SUB_SendRMADetailsToSnowflake

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendRMAToSnowflake'`**
2. **Create Variable **$Description** = `'Send RMA to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **EcoATM_RMA.SUB_SetRMAOwnerAndChanger****
5. **ExportXml**
6. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
7. **CreateList**
8. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentJSON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_CONTENT'`
      - Set **AGR_CONTENT** = `$JSON_RMADetails`**
9. **Add **$$ArgumentJSON_CONTENT** to/from list **$StoreProcedureArgumentList****
10. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_RMA.PWS_UpsertRMAStoredProc`**
11. **JavaCallAction**
12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
13. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.