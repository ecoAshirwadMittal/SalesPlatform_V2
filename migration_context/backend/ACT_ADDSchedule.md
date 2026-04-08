# Microflow Detailed Specification: ACT_ADDSchedule

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[ShortName='1']` (Result: **$TaskQueue**)**
2. **Create **TaskQueueScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **Schedule_TaskQueue** = `$TaskQueue`
      - Set **ActiveFrom** = `[%CurrentDateTime%]`**
3. **Maps to Page: **TaskQueueScheduler.Schedule_NewEdit****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.