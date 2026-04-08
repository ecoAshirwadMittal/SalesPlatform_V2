# Microflow Detailed Specification: Sub_Schedule_ExecuteMicroflow

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue!=empty`
   ➔ **If [true]:**
      1. **Update **$Schedule** (and Save to DB)
      - Set **Running** = `true`
      - Set **LastRunTime** = `[%CurrentDateTime%]`
      - Set **LastStarted** = `empty`
      - Set **LastProcessed** = `empty`**
      2. **JavaCallAction**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[Name='Batch']` (Result: **$DefaultTaskQueue**)**
      2. 🔀 **DECISION:** `$DefaultTaskQueue=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$Schedule**
      - Set **Schedule_TaskQueue** = `$DefaultTaskQueue`
      - Set **QueueName** = `$DefaultTaskQueue/FullName`**
            2. **Update **$Schedule** (and Save to DB)
      - Set **Running** = `true`
      - Set **LastRunTime** = `[%CurrentDateTime%]`
      - Set **LastStarted** = `empty`
      - Set **LastProcessed** = `empty`**
            3. **JavaCallAction**
            4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.