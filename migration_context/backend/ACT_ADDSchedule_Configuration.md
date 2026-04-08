# Microflow Detailed Specification: ACT_ADDSchedule_Configuration

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[ShortName='Batch']` (Result: **$TaskQueue**)**
2. **Create **TaskQueueScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **Schedule_TaskQueue** = `$TaskQueue`**
3. **Maps to Page: **TaskQueueScheduler.Schedule_NewEdit_Configuration****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.