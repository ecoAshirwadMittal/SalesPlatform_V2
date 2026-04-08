# Microflow Detailed Specification: ACT_DeletePriceData

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWSMDM.PriceHistory**  (Result: **$PriceList**)**
3. **Delete**
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.