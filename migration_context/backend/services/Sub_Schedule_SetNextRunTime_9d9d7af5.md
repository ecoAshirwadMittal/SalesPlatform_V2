# Microflow Analysis: Sub_Schedule_SetNextRunTime

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Decision:** "Active?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Sub microflow**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
