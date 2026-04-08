# Microflow Detailed Specification: Sub_Schedule_StopLongRunning

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$DateTimeLimit** = `addHours( [%CurrentDateTime%], -@TaskQueueScheduler.TasksCompletedOlderThanHours )`**
2. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Running=true()] [LastStarted<$DateTimeLimit]` (Result: **$ScheduleList_longRunning**)**
3. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList_longRunning**
   │ 1. **Call Microflow **TaskQueueScheduler.ACT_Schedule_StopRunning** (Result: **$Variable**)**
   └─ **End Loop**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.