# Microflow Analysis: Cal_Schedule_NextRunTime

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create Variable**
3. **Decision:** "NextRunTime > CurrentTime?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [DateTime] result.
