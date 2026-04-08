# Microflow Analysis: Sub_QueuedAction_BatchesByName

### Requirements (Inputs):
- **$QueuedActionParameters** (A record of type: TaskQueueScheduler.QueuedActionParameters)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Sub_Schedule_FetchByName"
      - Store the result in a new variable called **$Schedule****
2. **Decision:** "empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_BatchesByCount"
      - Store the result in a new variable called **$QueuedAction****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
