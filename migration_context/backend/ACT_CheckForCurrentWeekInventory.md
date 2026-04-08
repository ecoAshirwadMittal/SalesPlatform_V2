# Microflow Detailed Specification: ACT_CheckForCurrentWeekInventory

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$CurrentYear** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
2. **Create Variable **$CurrentWeekNumber** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`**
3. **DB Retrieve **EcoATM_MDM.Week** Filter: `[(WeekNumber = $CurrentWeekNumber) and (Year = $CurrentYear)]` (Result: **$CurrentWeekResult**)**
4. 🔀 **DECISION:** `$CurrentWeekResult != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **EcoATM_MDM.Week** (Result: **$NewWeek**)
      - Set **WeekNumber** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`
      - Set **WeekStartDateTime** = `[%BeginOfCurrentWeek%]`
      - Set **WeekEndDateTime** = `[%EndOfCurrentWeek%]`
      - Set **WeekDisplay** = `formatDateTime([%CurrentDateTime%],'yyyy') + ' / ' + formatDateTime([%CurrentDateTime%],'ww')`
      - Set **Year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.