# Microflow Analysis: Sub_Schedule_UpdateAll_Batch

### Requirements (Inputs):
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { Show everything } (Call this list **$ScheduleList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
