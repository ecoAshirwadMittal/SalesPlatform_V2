# Microflow Detailed Specification: Sub_ProcessedQueueTaskToSchedule

### 📥 Inputs (Parameters)
- **$QueuedTaskList** (Type: TaskQueueScheduler.QueuedTask)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Running=true()] [LastRunTime!=empty]` (Result: **$RunningScheduleList**)**
2. 🔀 **DECISION:** `$RunningScheduleList!=empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create Variable **$DateTimeLimit** = `addHours( [%CurrentDateTime%], -@TaskQueueScheduler.TasksCompletedOlderThanHours )`**
      2. **List Operation: **Head** on **$undefined** (Result: **$OldestRunningSchedule**)**
      3. 🔀 **DECISION:** `$OldestRunningSchedule/LastRunTime>=$DateTimeLimit`
         ➔ **If [true]:**
            1. **Update Variable **$DateTimeLimit** = `if $OldestRunningSchedule/LastRunTime>$DateTimeLimit then $OldestRunningSchedule/LastRunTime else $DateTimeLimit`**
            2. **DB Retrieve **System.ProcessedQueueTask** Filter: `[Created>=$DateTimeLimit]` (Result: **$ProcessedQueueTaskList**)**
            3. 🔀 **DECISION:** `$ProcessedQueueTaskList!=empty`
               ➔ **If [true]:**
                  1. **CreateList**
                  2. **Create Variable **$PreviousMicroflowQueue** = `'nothing'`**
                  3. **Create Variable **$Microflow_Queue** = `'nothing'`**
                  4. 🔄 **LOOP:** For each **$IteratorProcessedQueueTask** in **$ProcessedQueueTaskList**
                     │ 1. **Update Variable **$Microflow_Queue** = `$IteratorProcessedQueueTask/MicroflowName +$IteratorProcessedQueueTask/QueueName`**
                     │ 2. 🔀 **DECISION:** `$Microflow_Queue=$PreviousMicroflowQueue`
                     │    ➔ **If [true]:**
                     │    ➔ **If [false]:**
                     │       1. **Update Variable **$PreviousMicroflowQueue** = `$Microflow_Queue`**
                     │       2. **Call Microflow **TaskQueueScheduler.Sub_MapProcessedQueueTaskToSchedule** (Result: **$Schedule**)**
                     │       3. 🔀 **DECISION:** `$Schedule!=empty`
                     │          ➔ **If [true]:**
                     │             1. **List Operation: **Find** on **$undefined** where `$Schedule/MicroflowName` (Result: **$RunningSchedule**)**
                     │             2. 🔀 **DECISION:** `$RunningSchedule!=empty`
                     │                ➔ **If [true]:**
                     │                   1. 🔀 **DECISION:** `$Schedule/RunningQueuedActions!=empty and $Schedule/RunningQueuedActions>0`
                     │                      ➔ **If [true]:**
                     │                         1. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true`
                     │                            ➔ **If [false]:**
                     │                               1. **Add **$$Schedule** to/from list **$UpdateList****
                     │                            ➔ **If [true]:**
                     │                               1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_SetNextRunTime** (Result: **$Schedule_1**)**
                     │                               2. **Add **$$Schedule** to/from list **$UpdateList****
                     │                      ➔ **If [false]:**
                     │                         1. **Call Microflow **TaskQueueScheduler.ACT_Schedule_ClearQueuedActions****
                     │                         2. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true`
                     │                            ➔ **If [false]:**
                     │                               1. **Add **$$Schedule** to/from list **$UpdateList****
                     │                            ➔ **If [true]:**
                     │                               1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_SetNextRunTime** (Result: **$Schedule_1**)**
                     │                               2. **Add **$$Schedule** to/from list **$UpdateList****
                     │                ➔ **If [false]:**
                     │                   1. **Add **$$Schedule** to/from list **$UpdateList****
                     │          ➔ **If [false]:**
                     └─ **End Loop**
                  5. **Commit/Save **$UpdateList** to Database**
                  6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_StopLongRunning****
            2. **Update Variable **$DateTimeLimit** = `if $OldestRunningSchedule/LastRunTime>$DateTimeLimit then $OldestRunningSchedule/LastRunTime else $DateTimeLimit`**
            3. **DB Retrieve **System.ProcessedQueueTask** Filter: `[Created>=$DateTimeLimit]` (Result: **$ProcessedQueueTaskList**)**
            4. 🔀 **DECISION:** `$ProcessedQueueTaskList!=empty`
               ➔ **If [true]:**
                  1. **CreateList**
                  2. **Create Variable **$PreviousMicroflowQueue** = `'nothing'`**
                  3. **Create Variable **$Microflow_Queue** = `'nothing'`**
                  4. 🔄 **LOOP:** For each **$IteratorProcessedQueueTask** in **$ProcessedQueueTaskList**
                     │ 1. **Update Variable **$Microflow_Queue** = `$IteratorProcessedQueueTask/MicroflowName +$IteratorProcessedQueueTask/QueueName`**
                     │ 2. 🔀 **DECISION:** `$Microflow_Queue=$PreviousMicroflowQueue`
                     │    ➔ **If [true]:**
                     │    ➔ **If [false]:**
                     │       1. **Update Variable **$PreviousMicroflowQueue** = `$Microflow_Queue`**
                     │       2. **Call Microflow **TaskQueueScheduler.Sub_MapProcessedQueueTaskToSchedule** (Result: **$Schedule**)**
                     │       3. 🔀 **DECISION:** `$Schedule!=empty`
                     │          ➔ **If [true]:**
                     │             1. **List Operation: **Find** on **$undefined** where `$Schedule/MicroflowName` (Result: **$RunningSchedule**)**
                     │             2. 🔀 **DECISION:** `$RunningSchedule!=empty`
                     │                ➔ **If [true]:**
                     │                   1. 🔀 **DECISION:** `$Schedule/RunningQueuedActions!=empty and $Schedule/RunningQueuedActions>0`
                     │                      ➔ **If [true]:**
                     │                         1. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true`
                     │                            ➔ **If [false]:**
                     │                               1. **Add **$$Schedule** to/from list **$UpdateList****
                     │                            ➔ **If [true]:**
                     │                               1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_SetNextRunTime** (Result: **$Schedule_1**)**
                     │                               2. **Add **$$Schedule** to/from list **$UpdateList****
                     │                      ➔ **If [false]:**
                     │                         1. **Call Microflow **TaskQueueScheduler.ACT_Schedule_ClearQueuedActions****
                     │                         2. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling=true`
                     │                            ➔ **If [false]:**
                     │                               1. **Add **$$Schedule** to/from list **$UpdateList****
                     │                            ➔ **If [true]:**
                     │                               1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_SetNextRunTime** (Result: **$Schedule_1**)**
                     │                               2. **Add **$$Schedule** to/from list **$UpdateList****
                     │                ➔ **If [false]:**
                     │                   1. **Add **$$Schedule** to/from list **$UpdateList****
                     │          ➔ **If [false]:**
                     └─ **End Loop**
                  5. **Commit/Save **$UpdateList** to Database**
                  6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.