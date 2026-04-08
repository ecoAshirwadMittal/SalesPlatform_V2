# Microflow Analysis: SUB_GetCurrentWeekPlusOne

### Requirements (Inputs):
- **$CurrentWeek** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Variable**
4. **Search the Database for **EcoATM_MDM.Week** using filter: { [(WeekNumber = $week and Year = $year)] } (Call this list **$CurrentWeekPlusOne**)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
