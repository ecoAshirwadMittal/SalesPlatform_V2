# Microflow Detailed Specification: ACT_InactivateAllSchedules

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Active = true()]` (Result: **$ScheduleList**)**
2. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList**
   │ 1. **Update **$IteratorSchedule**
      - Set **Active** = `false`**
   └─ **End Loop**
3. **Commit/Save **$ScheduleList** to Database**
4. **LogMessage**
5. **Show Message (Information): `All schedules have been set to inactive`**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.