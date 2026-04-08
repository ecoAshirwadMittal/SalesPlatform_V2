# Microflow Detailed Specification: ACT_UpdateRMAFromDeposco

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Timer** = `'RMASyncFromDesposco'`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **DB Retrieve **EcoATM_RMA.RMA** Filter: `[SystemStatus!='Canceled' and SystemStatus!='Submitted' and SystemStatus!='Received'] [OracleNumber!=empty]` (Result: **$RMAList**)**
4. **AggregateList**
5. **Call Microflow **Custom_Logging.SUB_Log_Info****
6. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
7. **Retrieve related **DesposcoAPIs_DeposcoConfig** via Association from **$DeposcoConfig** (Result: **$DesposcoAPIsList**)**
8. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/ServiceName = EcoATM_PWSIntegration.ENUM_DeposcoServices.RMA` (Result: **$DesposcoAPIsForOrderHistory**)**
9. **DB Retrieve **EcoATM_RMA.RMAStatus**  (Result: **$RMAStatusList**)**
10. 🔄 **LOOP:** For each **$IteratorRMA** in **$RMAList**
   │ 1. **Call Microflow **EcoATM_RMA.SUB_SyncRMAStatus****
   └─ **End Loop**
11. **Commit/Save **$RMAList** to Database**
12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
13. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.