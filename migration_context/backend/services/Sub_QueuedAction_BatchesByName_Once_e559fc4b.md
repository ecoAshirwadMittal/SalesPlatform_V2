# Microflow Analysis: Sub_QueuedAction_BatchesByName_Once

### Requirements (Inputs):
- **$QueuedActionParameters** (A record of type: TaskQueueScheduler.QueuedActionParameters)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Sub_Schedule_FetchByName"
      - Store the result in a new variable called **$Schedule****
2. **Decision:** "empty"
   - If [false] -> Move to: **Running already?**
   - If [true] -> Move to: **Activity**
3. **Decision:** "Running already?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
