# Microflow Detailed Specification: Sub_Schedule_GetMicroflowName

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$MicroflowName** = `$Schedule/MicroflowName`**
2. **Retrieve related **QueuedAction_Schedule** via Association from **$Schedule** (Result: **$QueuedActionList**)**
3. 🔀 **DECISION:** `$QueuedActionList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$QueuedAction**)**
      2. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
      3. **Update Variable **$MicroflowName** = `if $QueuedActionParameters/ProcessName!=empty and length(trim($QueuedActionParameters/ProcessName))>0 then $QueuedActionParameters/ProcessName else $MicroflowName`**
      4. 🏁 **END:** Return `$MicroflowName`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$MicroflowName`

**Final Result:** This process concludes by returning a [String] value.