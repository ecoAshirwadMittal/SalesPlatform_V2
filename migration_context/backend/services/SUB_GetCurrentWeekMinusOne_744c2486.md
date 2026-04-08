# Microflow Analysis: SUB_GetCurrentWeekMinusOne

### Requirements (Inputs):
- **$CurrentWeek** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Create Variable**
3. **Decision:** "Week Number != 1"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Variable**
5. **Search the Database for **EcoATM_MDM.Week** using filter: { [(WeekNumber = $week and Year = $year)] } (Call this list **$CurrentWeekMinusOne**)**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
