# Microflow Detailed Specification: SUB_GetBuyerAwardSummaryrReportForWeek

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$Week!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_Reports.SUB_LoadBuyerAwardsSummaryReport** (Result: **$Message**)**
      2. **Update **$DAHelper** (and Save to DB)
      - Set **DisplayDA_DataGrid** = `true`
      - Set **DAHelper_Week** = `$Week`**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `false`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Error****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.