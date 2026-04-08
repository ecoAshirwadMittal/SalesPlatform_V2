# Microflow Analysis: ACT_Schedule_StopRunning

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Decision:** "Running?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Sub microflow**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
