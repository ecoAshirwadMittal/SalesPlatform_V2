# Microflow Detailed Specification: ACT_CreateWeeks2025_26

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **EcoATM_MDM.Week** Filter: `[Year=2025 or Year=2026]` (Result: **$WeekList**)**
3. **Delete**
4. **LogMessage**
5. **Create Variable **$year** = `2025`**
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
      2. **Create Variable **$year2026** = `2026`**
      3. **LogMessage**
      4. **Create Variable **$weeknumber2026** = `1`**
      5. **Create Variable **$FirstDayofYear2026** = `parseDateTime('12-25-' + toString(2025),'M-d-yyyy')`**
      6. **Create Variable **$BeginOfFirstWeekDateTime2026** = `beginOfWeek($FirstDayofYear2026)`**
      7. **Create Variable **$EndOfFirstWeekDateTime2026** = `endOfWeek($FirstDayofYear2026)`**
      8. **Create **EcoATM_MDM.Week** (Result: **$NewWeek2026**)
      - Set **Year** = `$year2026`
      - Set **WeekNumber** = `$weeknumber2026`
      - Set **WeekStartDateTime** = `$BeginOfFirstWeekDateTime2026`
      - Set **WeekEndDateTime** = `$EndOfFirstWeekDateTime2026`
      - Set **WeekDisplay** = `toString($year2026) + ' / Wk' + substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`
      - Set **WeekDisplayShort** = `'Wk' + substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`
      - Set **WeekNumberString** = `substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`**
      9. **Create Variable **$FirstOfWeekDateTime2026** = `$BeginOfFirstWeekDateTime2026`**
      10. **Create Variable **$EndOfWeekDateTime2026** = `$EndOfFirstWeekDateTime2026`**
      11. **Update Variable **$weeknumber2026** = `$weeknumber2026 + 1`**
      12. **Update Variable **$FirstOfWeekDateTime2026** = `addDays($FirstOfWeekDateTime2026,7)`**
      13. **Update Variable **$EndOfWeekDateTime2026** = `addDays($EndOfWeekDateTime2026,7)`**
      14. **Create **EcoATM_MDM.Week** (Result: **$NewWeek_12026**)
      - Set **Year** = `$year2026`
      - Set **WeekNumber** = `$weeknumber2026`
      - Set **WeekStartDateTime** = `$FirstOfWeekDateTime2026`
      - Set **WeekEndDateTime** = `$EndOfWeekDateTime2026`
      - Set **WeekDisplay** = `toString($year2026) + ' / Wk' + substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`
      - Set **WeekDisplayShort** = `'Wk' + substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`
      - Set **WeekNumberString** = `substring('0'+toString($weeknumber2026), length(toString($weeknumber2026))-1)`**
      15. 🔀 **DECISION:** `$weeknumber2026 = 53`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
               *(Merging with existing path logic)*
   ➔ **If [false]:**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.