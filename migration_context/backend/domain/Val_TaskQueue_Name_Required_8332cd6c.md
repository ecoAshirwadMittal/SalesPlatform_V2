# Microflow Analysis: Val_TaskQueue_Name_Required

### Requirements (Inputs):
- **$TaskQueue** (A record of type: TaskQueueScheduler.TaskQueue)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Decision:** "Filled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
