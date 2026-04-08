# Microflow Detailed Specification: SUB_SendOnlyRMADetailsToSnowflake

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendOnlyRMAToSnowflake'`**
2. **Create Variable **$Description** = `'Send only RMA to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **DB Retrieve **System.User** Filter: `[id=$RMA/System.owner]` (Result: **$User**)**
5. **Update **$RMA** (and Save to DB)
      - Set **EntityOwner** = `if $User!=empty then $User/Name else $currentUser/Name`
      - Set **EntityChanger** = `'Deposco'`
      - Set **SystemStatus** = `$RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/SystemStatus`**
6. **ExportXml**
7. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
8. **CreateList**
9. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentJSON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_CONTENT'`
      - Set **AGR_CONTENT** = `$JSON_OnlyRMADetails`**
10. **Add **$$ArgumentJSON_CONTENT** to/from list **$StoreProcedureArgumentList****
11. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_RMA.PWS_UpsertOnlyRMAStoredProc`**
12. **JavaCallAction**
13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
14. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.