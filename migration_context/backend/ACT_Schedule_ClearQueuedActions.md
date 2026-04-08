# Microflow Detailed Specification: ACT_Schedule_ClearQueuedActions

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedAction_Schedule** via Association from **$Schedule** (Result: **$QueuedActionList**)**
2. 🔀 **DECISION:** `$QueuedActionList!=empty`
   ➔ **If [true]:**
      1. **Delete**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.