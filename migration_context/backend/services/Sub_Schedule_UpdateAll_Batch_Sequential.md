# Microflow Detailed Specification: Sub_Schedule_UpdateAll_Batch_Sequential

### 📥 Inputs (Parameters)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
2. **Create Variable **$CurrentDateTime** = `parseDateTimeUTC($QueuedActionParameters/Param1,'yyyy-MM-dd HH:mm:ss',[%CurrentDateTime%])`**
3. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[contains(MicroflowName,'Schedule_UpdateAll')] [changedDate=empty or changedDate<$CurrentDateTime]` (Result: **$ScheduleList**)**
4. 🔄 **LOOP:** For each **$Iterator** in **$ScheduleList**
   │ 1. **Update **$Iterator**
      - Set **MicroflowName** = `$Iterator/MicroflowName`**
   └─ **End Loop**
5. **Commit/Save **$ScheduleList** to Database**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.