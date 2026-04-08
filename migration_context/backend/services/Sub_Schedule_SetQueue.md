# Microflow Detailed Specification: Sub_Schedule_SetQueue

### 📥 Inputs (Parameters)
- **$MicroflowName** (Type: Variable)
- **$QueueName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[FullName=$QueueName]` (Result: **$TaskQueue**)**
2. 🔀 **DECISION:** `$TaskQueue!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_FetchByName** (Result: **$Schedule**)**
      2. **Update **$Schedule** (and Save to DB)
      - Set **QueueName** = `$TaskQueue/FullName`
      - Set **Schedule_TaskQueue** = `$TaskQueue`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.