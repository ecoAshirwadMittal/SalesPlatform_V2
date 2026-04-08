# Microflow Analysis: Sub_PauseSchedule

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.PausedSchedule** using filter: { Show everything } (Call this list **$PausedScheduleList**)**
2. **Aggregate List
      - Store the result in a new variable called **$Count****
3. **Decision:** "Count=1?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Count=0?**
4. **Take the list **$PausedScheduleList**, perform a [Head], and call the result **$PausedSchedule****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
