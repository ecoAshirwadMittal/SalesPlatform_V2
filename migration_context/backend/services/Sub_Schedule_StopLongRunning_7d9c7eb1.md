# Microflow Analysis: Sub_Schedule_StopLongRunning

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [Running=true()]
[LastStarted<$DateTimeLimit]
 } (Call this list **$ScheduleList_longRunning**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
