# Microflow Analysis: ACT_CheckForCurrentWeekInventory

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **EcoATM_MDM.Week** using filter: { [(WeekNumber = $CurrentWeekNumber) and (Year = $CurrentYear)] } (Call this list **$CurrentWeekResult**)**
4. **Decision:** "Week exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
