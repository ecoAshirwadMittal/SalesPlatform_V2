# Microflow Detailed Specification: SUB_LoadPWSInventory_Task

### 📥 Inputs (Parameters)
- **$Limit** (Type: Variable)
- **$Offset** (Type: Variable)
- **$MDMFuturePriceHelper** (Type: EcoATM_PWS.MDMFuturePriceHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **ExecuteDatabaseQuery**
3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **List Operation: **Head** on **$undefined** (Result: **$VW_FACT_INVENTORY_PWS_CURRENT**)**
6. **Create Variable **$ResultJson** = `$VW_FACT_INVENTORY_PWS_CURRENT/RESULTJSON`**
7. **ImportXml**
8. **AggregateList**
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return `$DeviceCount`

**Final Result:** This process concludes by returning a [Integer] value.