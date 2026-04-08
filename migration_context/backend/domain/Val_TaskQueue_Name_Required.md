# Microflow Detailed Specification: Val_TaskQueue_Name_Required

### 📥 Inputs (Parameters)
- **$TaskQueue** (Type: TaskQueueScheduler.TaskQueue)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$AttributeName** = `'Name'`**
2. **Create Variable **$Message** = `' is mandatory'`**
3. 🔀 **DECISION:** `$TaskQueue/Name!=empty and length(trim($TaskQueue/Name))>0`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `''`
   ➔ **If [false]:**
      1. **Create Variable **$Validation** = `$AttributeName + $Message`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return `$Validation`

**Final Result:** This process concludes by returning a [String] value.