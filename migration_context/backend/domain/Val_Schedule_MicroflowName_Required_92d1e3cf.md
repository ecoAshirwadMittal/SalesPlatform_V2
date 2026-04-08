# Microflow Analysis: Val_Schedule_MicroflowName_Required

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Filled?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Create Variable**
4. **Validation Feedback**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
