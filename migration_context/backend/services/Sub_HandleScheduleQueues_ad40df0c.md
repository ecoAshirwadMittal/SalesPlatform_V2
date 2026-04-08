# Microflow Analysis: Sub_HandleScheduleQueues

### Execution Steps:
1. **Create Variable**
2. **Java Action Call
      - Store the result in a new variable called **$QueuedTaskList****
3. **Run another process: "TaskQueueScheduler.Sub_ProcessedQueueTaskToSchedule"**
4. **Run another process: "TaskQueueScheduler.Sub_QueuedTasks_SchedulesRunning"**
5. **Delete**
6. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true()]
[Running = false()]
[Active = true()]
[ActiveFrom <= '[%CurrentDateTime%]']
[NextRunTime != empty]
[NextRunTime <= '[%CurrentDateTime%]']
 } (Call this list **$ScheduleList**)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
