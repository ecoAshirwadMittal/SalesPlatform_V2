# Microflow Detailed Specification: SUB_LoadPWSInventory_Task_Snowflake

### 📥 Inputs (Parameters)
- **$Limit** (Type: Variable)
- **$Offset** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSInventorySyncTask_SnowflakeQuery'`**
2. **Create Variable **$Description** = `'PWS Inventory Sync Snowflake Query. Offset: ' + $Offset + ' Limit: ' + $Limit`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **ExecuteDatabaseQuery**
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
7. **JavaCallAction**
8. **AggregateList**
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return `$DeviceCount`

**Final Result:** This process concludes by returning a [Integer] value.