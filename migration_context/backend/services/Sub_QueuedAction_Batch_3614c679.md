# Microflow Analysis: Sub_QueuedAction_Batch

### Requirements (Inputs):
- **$QueuedActionParameters** (A record of type: TaskQueueScheduler.QueuedActionParameters)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Sub_Schedule_FetchByName"
      - Store the result in a new variable called **$Schedule****
2. **Decision:** "empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$QueuedAction****
4. **Permanently save **$undefined** to the database.**
5. **Permanently save **$undefined** to the database.**
6. **Run another process: "TaskQueueScheduler.Sub_Schedule_ExecuteMicroflowWithParam"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
