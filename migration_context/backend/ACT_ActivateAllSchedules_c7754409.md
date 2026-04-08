# Microflow Analysis: ACT_ActivateAllSchedules

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [Active = false()] } (Call this list **$ScheduleList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Log Message**
5. **Show Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
