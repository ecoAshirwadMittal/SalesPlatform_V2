# Microflow Detailed Specification: Val_Schedule_TaskQueue_Required

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `' is mandatory'`**
2. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `''`
   ➔ **If [false]:**
      1. **Create Variable **$Validation** = `'Queue' + $Message`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return `$Validation`

**Final Result:** This process concludes by returning a [String] value.