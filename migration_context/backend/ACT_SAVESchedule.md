# Microflow Detailed Specification: ACT_SAVESchedule

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Val_ProcessSchedule** (Result: **$Validations**)**
2. 🔀 **DECISION:** `$Validations=''`
   ➔ **If [true]:**
      1. **Commit/Save **$Schedule** to Database**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.