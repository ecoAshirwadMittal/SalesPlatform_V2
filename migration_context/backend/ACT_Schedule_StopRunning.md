# Microflow Detailed Specification: ACT_Schedule_StopRunning

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/Running`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_GetMicroflowName** (Result: **$MicroflowName**)**
      2. **DB Retrieve **System.QueuedTask** Filter: `[MicroflowName=$MicroflowName]` (Result: **$QueuedTask**)**
      3. 🔀 **DECISION:** `$QueuedTask=empty`
         ➔ **If [false]:**
            1. **Show Message (Information): `Microflow {1} cannot be stopped because it is actively processed by Mendix currently`**
            2. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **Call Microflow **TaskQueueScheduler.ACT_Schedule_ClearQueuedActions****
            2. **Update **$Schedule** (and Save to DB)
      - Set **Running** = `false`**
            3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.