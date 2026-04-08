# Microflow Detailed Specification: ACT_LoadPWSInventoryData

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSLoadInventoryData'`**
2. **Create Variable **$Description** = `'Loading PWS Inventory Data'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_PWS.SUB_LoadPWSInventory_Snowflake****
5. **Call Microflow **EcoATM_PWS.SUB_LoadPWSInventory_Deposco****
6. **Maps to Page: **EcoATM_PWS.PWS_Pricing****
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.