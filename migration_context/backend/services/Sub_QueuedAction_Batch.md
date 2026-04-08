# Microflow Detailed Specification: Sub_QueuedAction_Batch

### 📥 Inputs (Parameters)
- **$QueuedActionParameters** (Type: TaskQueueScheduler.QueuedActionParameters)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_FetchByName** (Result: **$Schedule**)**
2. 🔀 **DECISION:** `$Schedule=empty`
   ➔ **If [false]:**
      1. **Create **TaskQueueScheduler.QueuedAction** (Result: **$QueuedAction**)
      - Set **ReferenceText** = `$QueuedActionParameters/ReferenceText`
      - Set **QueuedActionParameters_QueuedAction** = `$QueuedActionParameters`
      - Set **QueuedAction_Schedule** = `$Schedule`
      - Set **StartTime** = `[%CurrentDateTime%]`**
      2. **Commit/Save **$QueuedActionParameters** to Database**
      3. **Commit/Save **$Schedule** to Database**
      4. **Call Microflow **TaskQueueScheduler.Sub_Schedule_ExecuteMicroflowWithParam****
      5. 🏁 **END:** Return `$QueuedAction`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.