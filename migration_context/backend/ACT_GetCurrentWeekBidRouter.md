# Microflow Detailed Specification: ACT_GetCurrentWeekBidRouter

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$week** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`**
2. **Create Variable **$year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
3. **DB Retrieve **EcoATM_MDM.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$ExistingWeek**)**
4. 🏁 **END:** Return `$ExistingWeek`

**Final Result:** This process concludes by returning a [Object] value.