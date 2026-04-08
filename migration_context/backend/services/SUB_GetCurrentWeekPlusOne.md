# Microflow Detailed Specification: SUB_GetCurrentWeekPlusOne

### 📥 Inputs (Parameters)
- **$CurrentWeek** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$week_old** = `parseInteger(formatDateTime(addDays($CurrentWeek/WeekEndDateTime,7),'w'))`**
2. **Create Variable **$week** = `$CurrentWeek/WeekNumber+1`**
3. **Create Variable **$year** = `parseInteger(formatDateTime(addDays($CurrentWeek/WeekEndDateTime,7),'YYYY'))`**
4. **DB Retrieve **EcoATM_MDM.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$CurrentWeekPlusOne**)**
5. 🏁 **END:** Return `$CurrentWeekPlusOne`

**Final Result:** This process concludes by returning a [Object] value.