# Microflow Analysis: DS_PausedSchedule_Get

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.PausedSchedule** using filter: { Show everything } (Call this list **$PausedScheduleList**)**
2. **Decision:** "Paused?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Take the list **$PausedScheduleList**, perform a [Head], and call the result **$PausedSchedule****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
