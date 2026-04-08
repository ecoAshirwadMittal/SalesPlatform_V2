# Microflow Analysis: Sub_Schedule_UpdateAll_Batch_Sequential

### Requirements (Inputs):
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Create Variable**
3. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [contains(MicroflowName,'Schedule_UpdateAll')]
[changedDate=empty or changedDate<$CurrentDateTime] } (Call this list **$ScheduleList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
