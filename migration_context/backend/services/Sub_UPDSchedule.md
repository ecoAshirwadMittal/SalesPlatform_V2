# Microflow Detailed Specification: Sub_UPDSchedule

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Schedule_TaskQueue** via Association from **$Schedule** (Result: **$TaskQueue**)**
2. 🔀 **DECISION:** `$TaskQueue=empty`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Schedule/QueueName!=$TaskQueue/FullName`
         ➔ **If [true]:**
            1. **Update **$Schedule**
      - Set **QueueName** = `$TaskQueue/FullName`**
            2. 🔀 **DECISION:** `$Schedule/MicroflowName!=$Schedule/OldMicroflowName`
               ➔ **If [true]:**
                  1. **Update **$Schedule**
      - Set **OldMicroflowName** = `$Schedule/MicroflowName`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Schedule/MicroflowName!=$Schedule/OldMicroflowName`
               ➔ **If [true]:**
                  1. **Update **$Schedule**
      - Set **OldMicroflowName** = `$Schedule/MicroflowName`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[FullName=$Schedule/QueueName]` (Result: **$TaskQueueNew**)**
      2. **Update **$Schedule**
      - Set **QueueName** = `if $TaskQueueNew!=empty then $TaskQueueNew/FullName else empty`
      - Set **Schedule_TaskQueue** = `$TaskQueueNew`**
      3. 🔀 **DECISION:** `$Schedule/MicroflowName!=$Schedule/OldMicroflowName`
         ➔ **If [true]:**
            1. **Update **$Schedule**
      - Set **OldMicroflowName** = `$Schedule/MicroflowName`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.