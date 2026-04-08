# Microflow Analysis: Val_TaskQueue_ShortName_Required

### Requirements (Inputs):
- **$TaskQueue** (A record of type: TaskQueueScheduler.TaskQueue)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Filled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
