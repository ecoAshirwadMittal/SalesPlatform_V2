# Microflow Detailed Specification: Val_Schedule_ActiveUntil_GreaterThan_ActiveFrom

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `' must be greater than Active from'`**
2. 🔀 **DECISION:** `$Schedule/TaskQueueScheduler.Schedule_TaskQueue!=empty and $Schedule/TaskQueueScheduler.Schedule_TaskQueue/TaskQueueScheduler.TaskQueue/AllowScheduling`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Schedule/ActiveUntil=empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Schedule/ActiveFrom=empty`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$Schedule/ActiveUntil > $Schedule/ActiveFrom`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `''`
                     ➔ **If [false]:**
                        1. **Create Variable **$Validation** = `'Active from' + $Message`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$Validation`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `''`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `''`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `''`

**Final Result:** This process concludes by returning a [String] value.