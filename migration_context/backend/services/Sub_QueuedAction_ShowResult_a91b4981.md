# Microflow Analysis: Sub_QueuedAction_ShowResult

### Requirements (Inputs):
- **$QueuedActionParameters** (A record of type: TaskQueueScheduler.QueuedActionParameters)
- **$Result** (A record of type: Object)

### Execution Steps:
1. **Decision:** "Success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Show Message**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
