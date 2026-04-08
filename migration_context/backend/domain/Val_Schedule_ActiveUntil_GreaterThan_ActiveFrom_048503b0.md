# Microflow Analysis: Val_Schedule_ActiveUntil_GreaterThan_ActiveFrom

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Scheduling Allowed?"
   - If [true] -> Move to: **ActiveUntil Empty?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "ActiveUntil Empty?"
   - If [false] -> Move to: **ActiveFrom Empty?**
   - If [true] -> Move to: **Finish**
4. **Decision:** "ActiveFrom Empty?"
   - If [false] -> Move to: **ValidTo > ValidFrom**
   - If [true] -> Move to: **Finish**
5. **Decision:** "ValidTo > ValidFrom"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
