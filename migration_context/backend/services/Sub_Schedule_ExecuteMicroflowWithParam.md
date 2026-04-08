# Microflow Detailed Specification: Sub_Schedule_ExecuteMicroflowWithParam

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Schedule/Running`
         ➔ **If [true]:**
            1. **Update **$Schedule** (and Save to DB)
      - Set **RunningQueuedActions** = `$Schedule/RunningQueuedActions+1`**
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$Schedule** (and Save to DB)
      - Set **Running** = `true`
      - Set **LastRunTime** = `[%CurrentDateTime%]`
      - Set **LastStarted** = `empty`
      - Set **LastProcessed** = `empty`
      - Set **RunningQueuedActions** = `1`**
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create Variable **$DefaultQueueName** = `'Batch'`**
      2. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[Name=$DefaultQueueName]` (Result: **$DefaultTaskQueue**)**
      3. 🔀 **DECISION:** `$DefaultTaskQueue=empty`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$Schedule**
      - Set **Schedule_TaskQueue** = `$DefaultTaskQueue`
      - Set **QueueName** = `$DefaultTaskQueue/FullName`**
            2. 🔀 **DECISION:** `$Schedule/Running`
               ➔ **If [true]:**
                  1. **Update **$Schedule** (and Save to DB)
      - Set **RunningQueuedActions** = `$Schedule/RunningQueuedActions+1`**
                  2. **JavaCallAction**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$Schedule** (and Save to DB)
      - Set **Running** = `true`
      - Set **LastRunTime** = `[%CurrentDateTime%]`
      - Set **LastStarted** = `empty`
      - Set **LastProcessed** = `empty`
      - Set **RunningQueuedActions** = `1`**
                  2. **JavaCallAction**
                  3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.