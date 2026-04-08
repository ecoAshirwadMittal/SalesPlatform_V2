# Microflow Analysis: Sub_ProcessedQueueTaskToSchedule

### Requirements (Inputs):
- **$QueuedTaskList** (A record of type: TaskQueueScheduler.QueuedTask)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [Running=true()]
[LastRunTime!=empty] } (Call this list **$RunningScheduleList**)**
2. **Decision:** "Running schedules?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
