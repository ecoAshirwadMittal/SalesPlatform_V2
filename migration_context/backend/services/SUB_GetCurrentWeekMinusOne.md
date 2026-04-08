# Microflow Detailed Specification: SUB_GetCurrentWeekMinusOne

### 📥 Inputs (Parameters)
- **$CurrentWeek** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Create Variable **$year** = `parseInteger(formatDateTime(subtractDays($CurrentWeek/WeekEndDateTime,7),'YYYY'))`**
3. 🔀 **DECISION:** `$CurrentWeek/WeekNumber != 1`
   ➔ **If [true]:**
      1. **Create Variable **$week** = `$CurrentWeek/WeekNumber- 1`**
      2. **DB Retrieve **EcoATM_MDM.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$CurrentWeekMinusOne**)**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return `$CurrentWeekMinusOne`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_MDM.Week** Filter: `[ ( Year = $year ) ]` (Result: **$Last_Week_Prior_Year**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return `$Last_Week_Prior_Year`

**Final Result:** This process concludes by returning a [Object] value.