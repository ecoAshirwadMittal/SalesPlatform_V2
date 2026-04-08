# Microflow Analysis: ACT_Schedule_ClearQueuedActions

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionList****
2. **Decision:** "QueuedActions?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Delete**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
