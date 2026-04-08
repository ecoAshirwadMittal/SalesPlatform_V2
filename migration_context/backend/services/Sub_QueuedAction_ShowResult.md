# Microflow Detailed Specification: Sub_QueuedAction_ShowResult

### 📥 Inputs (Parameters)
- **$QueuedActionParameters** (Type: TaskQueueScheduler.QueuedActionParameters)
- **$Result** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Result=true`
   ➔ **If [true]:**
      1. **Show Message (Information): `Queued microflow {1} {2} {3}`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Attempt to queue process {1} failed`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.