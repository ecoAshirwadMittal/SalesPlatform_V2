# Microflow Detailed Specification: DS_FromWeekPO

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$FromToDate** = `subtractDays([%BeginOfCurrentDay%],42)`**
2. **Create Variable **$ToDate** = `addDays([%BeginOfCurrentDay%],70)`**
3. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekStartDateTime>=$FromToDate and WeekEndDateTime<=$ToDate]` (Result: **$WeekList**)**
4. **List Operation: **Sort** on **$undefined** sorted by: Year (Ascending), WeekNumber (Ascending) (Result: **$SortedWeekList**)**
5. 🏁 **END:** Return `$SortedWeekList`

**Final Result:** This process concludes by returning a [List] value.