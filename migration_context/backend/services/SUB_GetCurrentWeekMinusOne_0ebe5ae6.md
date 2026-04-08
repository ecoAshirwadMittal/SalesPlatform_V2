# Microflow Analysis: SUB_GetCurrentWeekMinusOne

### Requirements (Inputs):
- **$CurrentWeek** (A record of type: AuctionUI.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Decision:** "Week Number != 1"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Search the Database for **AuctionUI.Week** using filter: { [
  (
    Year = $year
  )
] } (Call this list **$Last_Week_Prior_Year**)**
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
