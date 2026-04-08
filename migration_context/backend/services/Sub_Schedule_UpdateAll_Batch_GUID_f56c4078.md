# Microflow Analysis: Sub_Schedule_UpdateAll_Batch_GUID

### Requirements (Inputs):
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Java Action Call
      - Store the result in a new variable called **$BatchObjectList**** ⚠️ *(This step has a safety catch if it fails)*
3. **Create List
      - Store the result in a new variable called **$ScheduleList****
4. **Create Variable**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Delete**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
