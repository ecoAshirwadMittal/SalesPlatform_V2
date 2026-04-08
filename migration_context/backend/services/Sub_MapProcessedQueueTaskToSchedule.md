# Microflow Detailed Specification: Sub_MapProcessedQueueTaskToSchedule

### 📥 Inputs (Parameters)
- **$ProcessedQueueTask** (Type: System.ProcessedQueueTask)
- **$RunningScheduleList** (Type: TaskQueueScheduler.Schedule)
- **$QueuedTaskList** (Type: TaskQueueScheduler.QueuedTask)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **Find** on **$undefined** where `$ProcessedQueueTask/MicroflowName` (Result: **$RunningSchedule**)**
2. **List Operation: **Find** on **$undefined** where `$ProcessedQueueTask/MicroflowName` (Result: **$QueuedTask**)**
3. 🔀 **DECISION:** `$RunningSchedule!=empty`
   ➔ **If [true]:**
      1. **Update **$RunningSchedule**
      - Set **LastRunTime** = `$ProcessedQueueTask/Created`
      - Set **LastStarted** = `if $ProcessedQueueTask/Started!=empty then $ProcessedQueueTask/Started else $ProcessedQueueTask/StartAt`
      - Set **LastProcessed** = `if $ProcessedQueueTask/Finished=empty then [%CurrentDateTime%] else $ProcessedQueueTask/Finished`
      - Set **LastDuration** = `if $ProcessedQueueTask/Duration=empty then $RunningSchedule/LastDuration else round($ProcessedQueueTask/Duration,0)`
      - Set **Running** = `if $QueuedTask=empty then false else true`
      - Set **RunningQueuedActions** = `if $QueuedTask=empty then 0 else $QueuedTask/NumberOfQueuedActions`**
      2. 🏁 **END:** Return `$RunningSchedule`
   ➔ **If [false]:**
      1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName=$ProcessedQueueTask/MicroflowName]` (Result: **$Schedule**)**
      2. 🔀 **DECISION:** `$Schedule!=empty`
         ➔ **If [false]:**
            1. **Call Microflow **TaskQueueScheduler.Sub_ProcessedQueueTask_GetTaskQueue** (Result: **$TaskQueue**)**
            2. 🔀 **DECISION:** `$TaskQueue/AllowScheduling`
               ➔ **If [false]:**
                  1. **Create **TaskQueueScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **Schedule_TaskQueue** = `$TaskQueue`
      - Set **QueueName** = `$ProcessedQueueTask/QueueName`
      - Set **MicroflowName** = `$ProcessedQueueTask/MicroflowName`
      - Set **LastRunTime** = `$ProcessedQueueTask/Created`
      - Set **LastStarted** = `if $ProcessedQueueTask/Started!=empty then $ProcessedQueueTask/Started else $ProcessedQueueTask/StartAt`
      - Set **LastProcessed** = `if $ProcessedQueueTask/Finished=empty then [%CurrentDateTime%] else $ProcessedQueueTask/Finished`
      - Set **LastDuration** = `if $ProcessedQueueTask/Duration=empty then 0 else round($ProcessedQueueTask/Duration,0)`
      - Set **Running** = `if $QueuedTask=empty then false else true`
      - Set **RunningQueuedActions** = `if $QueuedTask=empty then 0 else $QueuedTask/NumberOfQueuedActions`**
                  2. 🏁 **END:** Return `$NewSchedule`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **Update **$Schedule**
      - Set **LastRunTime** = `$ProcessedQueueTask/Created`
      - Set **LastStarted** = `if $ProcessedQueueTask/Started!=empty then $ProcessedQueueTask/Started else $ProcessedQueueTask/StartAt`
      - Set **LastProcessed** = `if $ProcessedQueueTask/Finished=empty then [%CurrentDateTime%] else $ProcessedQueueTask/Finished`
      - Set **LastDuration** = `if $ProcessedQueueTask/Duration=empty then $RunningSchedule/LastDuration else round($ProcessedQueueTask/Duration,0)`
      - Set **Running** = `if $QueuedTask=empty then false else true`
      - Set **RunningQueuedActions** = `if $QueuedTask=empty then 0 else $QueuedTask/NumberOfQueuedActions`**
            2. 🏁 **END:** Return `$Schedule`

**Final Result:** This process concludes by returning a [Object] value.