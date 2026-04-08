# Microflow Detailed Specification: ACT_SAVETaskQueue

### 📥 Inputs (Parameters)
- **$TaskQueue** (Type: TaskQueueScheduler.TaskQueue)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Val_ProcessTaskQueue** (Result: **$Validations**)**
2. 🔀 **DECISION:** `$Validations=''`
   ➔ **If [true]:**
      1. **Commit/Save **$TaskQueue** to Database**
      2. **Call Microflow **TaskQueueScheduler.Sub_TaskQueue_UpdateSchedules****
      3. **Close current page/popup**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.