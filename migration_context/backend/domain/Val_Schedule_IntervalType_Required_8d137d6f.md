# Microflow Analysis: Val_Schedule_IntervalType_Required

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Scheduling Allowed?"
   - If [true] -> Move to: **Filled?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "Filled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
