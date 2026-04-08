# Microflow Detailed Specification: Sub_Schedule_SetNextRunTime

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/Active`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Cal_Schedule_NextRunTime** (Result: **$NextRunTime**)**

**Final Result:** This process concludes by returning a [Void] value.