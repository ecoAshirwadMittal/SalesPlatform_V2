# Microflow Detailed Specification: SE_LoadDAData

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Call Microflow **EcoATM_DA.SUB_GetOrCreateDAWeekCurrentWeek** (Result: **$DAWeek**)**
3. **Call Microflow **EcoATM_DA.SUB_LoadDAData** (Result: **$Variable**)**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.