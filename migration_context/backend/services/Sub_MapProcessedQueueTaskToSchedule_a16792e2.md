# Microflow Analysis: Sub_MapProcessedQueueTaskToSchedule

### Requirements (Inputs):
- **$ProcessedQueueTask** (A record of type: System.ProcessedQueueTask)
- **$RunningScheduleList** (A record of type: TaskQueueScheduler.Schedule)
- **$QueuedTaskList** (A record of type: TaskQueueScheduler.QueuedTask)

### Execution Steps:
1. **Take the list **$RunningScheduleList**, perform a [Find] where: { $ProcessedQueueTask/MicroflowName }, and call the result **$RunningSchedule****
2. **Take the list **$QueuedTaskList**, perform a [Find] where: { $ProcessedQueueTask/MicroflowName }, and call the result **$QueuedTask****
3. **Decision:** "Running Schedule?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.LastRunTime] to: "$ProcessedQueueTask/Created"
      - Change [TaskQueueScheduler.Schedule.LastStarted] to: "if $ProcessedQueueTask/Started!=empty
then $ProcessedQueueTask/Started
else $ProcessedQueueTask/StartAt"
      - Change [TaskQueueScheduler.Schedule.LastProcessed] to: "if $ProcessedQueueTask/Finished=empty
then [%CurrentDateTime%]
else $ProcessedQueueTask/Finished
"
      - Change [TaskQueueScheduler.Schedule.LastDuration] to: "if $ProcessedQueueTask/Duration=empty
then $RunningSchedule/LastDuration
else round($ProcessedQueueTask/Duration,0)
"
      - Change [TaskQueueScheduler.Schedule.Running] to: "if $QueuedTask=empty 
then false 
else true"
      - Change [TaskQueueScheduler.Schedule.RunningQueuedActions] to: "if $QueuedTask=empty
then 0
else $QueuedTask/NumberOfQueuedActions
"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
