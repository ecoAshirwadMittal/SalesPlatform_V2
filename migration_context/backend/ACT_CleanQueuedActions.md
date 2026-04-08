# Microflow Detailed Specification: ACT_CleanQueuedActions

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.QueuedAction** Filter: `[not(TaskQueueScheduler.QueuedAction_Schedule/TaskQueueScheduler.Schedule)]` (Result: **$QueuedActionList**)**
2. 🔀 **DECISION:** `$QueuedActionList!=empty`
   ➔ **If [true]:**
      1. **Delete**
      2. **DB Retrieve **TaskQueueScheduler.QueuedActionParameters** Filter: `[not(TaskQueueScheduler.QueuedActionParameters_QueuedAction/TaskQueueScheduler.QueuedAction)]` (Result: **$QueuedActionParametersList**)**
      3. 🔀 **DECISION:** `$QueuedActionParametersList!=empty`
         ➔ **If [true]:**
            1. **Delete**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **TaskQueueScheduler.QueuedActionParameters** Filter: `[not(TaskQueueScheduler.QueuedActionParameters_QueuedAction/TaskQueueScheduler.QueuedAction)]` (Result: **$QueuedActionParametersList**)**
      2. 🔀 **DECISION:** `$QueuedActionParametersList!=empty`
         ➔ **If [true]:**
            1. **Delete**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.