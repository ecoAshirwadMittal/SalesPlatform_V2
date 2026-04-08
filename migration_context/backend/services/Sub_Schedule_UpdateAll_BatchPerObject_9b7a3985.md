# Microflow Analysis: Sub_Schedule_UpdateAll_BatchPerObject

### Requirements (Inputs):
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Java Action Call
      - Store the result in a new variable called **$Schedule****
3. **Run another process: "TaskQueueScheduler.Sub_UPDSchedule"
      - Store the result in a new variable called **$UpdatedObject****
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
