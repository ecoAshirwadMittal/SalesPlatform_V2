# Microflow Detailed Specification: SUB_SendUserToSnowflake

### 📥 Inputs (Parameters)
- **$EcoATMDirectUserList** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendUserToSnowflake'`**
2. **Create Variable **$Description** = `'Send User to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **ExportXml**
5. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
6. **CreateList**
7. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentJSON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_CONTENT'`
      - Set **AGR_CONTENT** = `$JSON_UserDetails`**
8. **Add **$$ArgumentJSON_CONTENT** to/from list **$StoreProcedureArgumentList****
9. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_UserManagement.PWS_UpsertUserStoredProc`**
10. **JavaCallAction**
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.