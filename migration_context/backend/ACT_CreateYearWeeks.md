# Microflow Detailed Specification: ACT_CreateYearWeeks

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **EcoATM_MDM.Week**  (Result: **$WeekList**)**
3. **Delete**
4. **LogMessage**
5. **Create Variable **$year** = `2024`**
6. **Create Variable **$weeknumber** = `1`**
7. **Create Variable **$FirstDayofYear** = `parseDateTime('1-1-' + toString($year),'M-d-yyyy')`**
8. **Create Variable **$BeginOfFirstWeekDateTime** = `beginOfWeek($FirstDayofYear)`**
9. **Create Variable **$EndOfFirstWeekDateTime** = `endOfWeek($FirstDayofYear)`**
10. **Create **EcoATM_MDM.Week** (Result: **$NewWeek**)
      - Set **Year** = `$year`
      - Set **WeekNumber** = `$weeknumber`
      - Set **WeekStartDateTime** = `$BeginOfFirstWeekDateTime`
      - Set **WeekEndDateTime** = `$EndOfFirstWeekDateTime`
      - Set **WeekDisplay** = `toString($year) + ' / Wk' + substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`
      - Set **WeekDisplayShort** = `'Wk' + substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`
      - Set **WeekNumberString** = `substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`**
11. **Create Variable **$FirstOfWeekDateTime** = `$BeginOfFirstWeekDateTime`**
12. **Create Variable **$EndOfWeekDateTime** = `$EndOfFirstWeekDateTime`**
13. **Update Variable **$weeknumber** = `$weeknumber + 1`**
14. **Update Variable **$FirstOfWeekDateTime** = `addDays($FirstOfWeekDateTime,7)`**
15. **Update Variable **$EndOfWeekDateTime** = `addDays($EndOfWeekDateTime,7)`**
16. **Create **EcoATM_MDM.Week** (Result: **$NewWeek_1**)
      - Set **Year** = `$year`
      - Set **WeekNumber** = `$weeknumber`
      - Set **WeekStartDateTime** = `$FirstOfWeekDateTime`
      - Set **WeekEndDateTime** = `$EndOfWeekDateTime`
      - Set **WeekDisplay** = `toString($year) + ' / Wk' + substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`
      - Set **WeekDisplayShort** = `'Wk' + substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`
      - Set **WeekNumberString** = `substring('0'+toString($weeknumber), length(toString($weeknumber))-1)`**
17. 🔀 **DECISION:** `$weeknumber = 52`
   ➔ **If [true]:**
      1. **LogMessage**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.