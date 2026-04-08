# Microflow Detailed Specification: Sub_TaskQueue_UpdateSchedules

### 📥 Inputs (Parameters)
- **$TaskQueue** (Type: TaskQueueScheduler.TaskQueue)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[TaskQueueScheduler.Schedule_TaskQueue=$TaskQueue] [QueueName!=$TaskQueue/FullName]` (Result: **$ScheduleList**)**
2. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList**
   │ 1. **Update **$IteratorSchedule**
      - Set **QueueName** = `$TaskQueue/FullName`**
   └─ **End Loop**
3. **Commit/Save **$ScheduleList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.