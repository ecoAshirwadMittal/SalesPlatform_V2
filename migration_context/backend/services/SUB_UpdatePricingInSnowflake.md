# Microflow Detailed Specification: SUB_UpdatePricingInSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PricingDataToSnowflakeScheduledEvent'`**
2. **Create Variable **$Description** = `'Scheduled Activity SCE_CurrentListPrice_Update.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
5. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @EcoATM_PWS.UpdatePricingDataSP`**
6. **JavaCallAction**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.