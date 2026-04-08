# Microflow Analysis: DS_ToWeekPO

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **EcoATM_MDM.Week** using filter: { [WeekStartDateTime>=$FromToDate and WeekEndDateTime<=$ToDate] } (Call this list **$WeekList**)**
4. **Take the list **$WeekList**, perform a [Sort], and call the result **$SortedWeekList****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
