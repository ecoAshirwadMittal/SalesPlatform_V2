# Microflow Detailed Specification: SUB_GetDADataByWeek

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$DAWeek!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_DA.SUB_LoadDAData** (Result: **$Message**)**
      2. 🔀 **DECISION:** `$Message = empty`
         ➔ **If [true]:**
            1. **Update **$DAHelper** (and Save to DB)
      - Set **DisplayDA_DataGrid** = `true`
      - Set **DAHelper_DAWeek** = `$DAWeek`**
            2. **Call Microflow **Custom_Logging.SUB_Log_Info****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Create **EcoATM_DA.StringHelper** (Result: **$NewStringHelper_Failure**)
      - Set **Key** = `'Error'`
      - Set **Value** = `$Message`**
            2. **Maps to Page: **EcoATM_DA.DeviceAllocation_DataUnavailable****
            3. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `false`**
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `false`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.