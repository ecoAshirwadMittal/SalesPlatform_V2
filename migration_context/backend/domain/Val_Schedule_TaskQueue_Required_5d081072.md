# Microflow Analysis: Val_Schedule_TaskQueue_Required

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Filled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
