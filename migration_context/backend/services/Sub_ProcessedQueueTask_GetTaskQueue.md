# Microflow Detailed Specification: Sub_ProcessedQueueTask_GetTaskQueue

### 📥 Inputs (Parameters)
- **$ProcessedQueueTask** (Type: System.ProcessedQueueTask)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[FullName=$ProcessedQueueTask/QueueName]` (Result: **$TaskQueue**)**
2. 🔀 **DECISION:** `$TaskQueue!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$TaskQueue`
   ➔ **If [false]:**
      1. **Create **TaskQueueScheduler.TaskQueue** (Result: **$NewTaskQueue**)
      - Set **FullName** = `$ProcessedQueueTask/QueueName`
      - Set **Name** = `substring($ProcessedQueueTask/QueueName,find($ProcessedQueueTask/QueueName,'.')+1)`
      - Set **ShortName** = `substring($ProcessedQueueTask/QueueName,find($ProcessedQueueTask/QueueName,'.')+1)`**
      2. 🏁 **END:** Return `$NewTaskQueue`

**Final Result:** This process concludes by returning a [Object] value.