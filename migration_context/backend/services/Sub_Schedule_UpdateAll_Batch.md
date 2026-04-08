# Microflow Detailed Specification: Sub_Schedule_UpdateAll_Batch

### 📥 Inputs (Parameters)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
2. **DB Retrieve **TaskQueueScheduler.Schedule**  (Result: **$ScheduleList**)**
3. 🔄 **LOOP:** For each **$Iterator** in **$ScheduleList**
   │ 1. **Call Microflow **TaskQueueScheduler.Sub_UPDSchedule** (Result: **$UpdatedObject**)**
   └─ **End Loop**
4. **Commit/Save **$ScheduleList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.