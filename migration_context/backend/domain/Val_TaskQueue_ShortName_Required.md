# Microflow Detailed Specification: Val_TaskQueue_ShortName_Required

### 📥 Inputs (Parameters)
- **$TaskQueue** (Type: TaskQueueScheduler.TaskQueue)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `' is mandatory'`**
2. 🔀 **DECISION:** `$TaskQueue/ShortName!=empty and length(trim($TaskQueue/ShortName))>0`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `''`
   ➔ **If [false]:**
      1. **Create Variable **$Validation** = `'Short name' + $Message`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return `$Validation`

**Final Result:** This process concludes by returning a [String] value.