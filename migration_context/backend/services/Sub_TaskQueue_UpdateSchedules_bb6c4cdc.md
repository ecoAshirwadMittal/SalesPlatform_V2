# Microflow Analysis: Sub_TaskQueue_UpdateSchedules

### Requirements (Inputs):
- **$TaskQueue** (A record of type: TaskQueueScheduler.TaskQueue)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [TaskQueueScheduler.Schedule_TaskQueue=$TaskQueue]
[QueueName!=$TaskQueue/FullName] } (Call this list **$ScheduleList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
