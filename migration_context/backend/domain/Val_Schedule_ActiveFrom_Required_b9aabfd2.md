# Microflow Analysis: Val_Schedule_ActiveFrom_Required

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Scheduling Allowed?"
   - If [true] -> Move to: **Empty?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Create Variable**
5. **Validation Feedback**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
