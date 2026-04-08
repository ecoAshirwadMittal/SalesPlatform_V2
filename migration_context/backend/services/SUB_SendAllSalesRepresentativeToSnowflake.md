# Microflow Detailed Specification: SUB_SendAllSalesRepresentativeToSnowflake

### 📥 Inputs (Parameters)
- **$SalesRepresentativeList** (Type: EcoATM_BuyerManagement.SalesRepresentative)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **ExportXml**
3. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
4. **CreateList**
5. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ArgumentJSON_CONTENT**)
      - Set **ARG_NAME** = `'JSON_CONTENT'`
      - Set **AGR_CONTENT** = `$JSON_SalesRepresentatives`**
6. **Add **$$ArgumentJSON_CONTENT** to/from list **$StoreProcedureArgumentList****
7. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.PWS_LoadSalesRepSPROC`**
8. **Call Microflow **Custom_Logging.SUB_Log_Info****
9. **JavaCallAction**
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.