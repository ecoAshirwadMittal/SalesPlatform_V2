# Microflow Detailed Specification: Sub_QueuedTasks_SchedulesRunning

### 📥 Inputs (Parameters)
- **$QueuedTaskList** (Type: TaskQueueScheduler.QueuedTask)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorQueuedTask** in **$QueuedTaskList**
   │ 1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName=$IteratorQueuedTask/MicroflowName] [not(Running)]` (Result: **$Schedule**)**
   │ 2. 🔀 **DECISION:** `$Schedule!=empty`
   │    ➔ **If [true]:**
   │       1. **Update **$Schedule**
      - Set **Running** = `true`
      - Set **RunningQueuedActions** = `$IteratorQueuedTask/NumberOfQueuedActions`**
   │       2. **Add **$$Schedule** to/from list **$UpdateList****
   │    ➔ **If [false]:**
   └─ **End Loop**
3. **Commit/Save **$UpdateList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.