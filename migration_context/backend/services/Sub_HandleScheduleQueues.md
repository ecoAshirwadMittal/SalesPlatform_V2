# Microflow Detailed Specification: Sub_HandleScheduleQueues

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$OQL_QueuedTask** = `'select MicroflowName, QueueName, ContextType , count(*) as NumberOfQueuedActions from System.QueuedTask where MicroflowName is not null and ContextType!=''ScheduledEvent'' group by MicroflowName, QueueName, ContextType order by MicroflowName, QueueName, ContextType'`**
2. **JavaCallAction**
3. **Call Microflow **TaskQueueScheduler.Sub_ProcessedQueueTaskToSchedule****
4. **Call Microflow **TaskQueueScheduler.Sub_QueuedTasks_SchedulesRunning****
5. **Delete**
6. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true()] [Running = false()] [Active = true()] [ActiveFrom <= '[%CurrentDateTime%]'] [NextRunTime != empty] [NextRunTime <= '[%CurrentDateTime%]']` (Result: **$ScheduleList**)**
7. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList**
   │ 1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_ExecuteMicroflow** (Result: **$Variable**)**
   └─ **End Loop**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.