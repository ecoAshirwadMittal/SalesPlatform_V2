# Microflow Detailed Specification: Sub_PausedSchedule_Reactivate

### 📥 Inputs (Parameters)
- **$PausedSchedule** (Type: TaskQueueScheduler.PausedSchedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Schedule_PausedSchedule** via Association from **$PausedSchedule** (Result: **$ScheduleList**)**
2. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList**
   └─ **End Loop**
3. **Call Microflow **TaskQueueScheduler.Sub_PreviousInstance_Refresh****
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.