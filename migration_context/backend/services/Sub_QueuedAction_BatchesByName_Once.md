# Microflow Detailed Specification: Sub_QueuedAction_BatchesByName_Once

### 📥 Inputs (Parameters)
- **$QueuedActionParameters** (Type: TaskQueueScheduler.QueuedActionParameters)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_FetchByName** (Result: **$Schedule**)**
2. 🔀 **DECISION:** `$Schedule=empty`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Schedule/Running`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_BatchesByCount** (Result: **$QueuedAction**)**
            2. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.