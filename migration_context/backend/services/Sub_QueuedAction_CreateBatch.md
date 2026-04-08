# Microflow Detailed Specification: Sub_QueuedAction_CreateBatch

### 📥 Inputs (Parameters)
- **$MicroflowName** (Type: Variable)
- **$ReferenceText** (Type: Variable)
- **$Param1** (Type: Variable)
- **$Param2** (Type: Variable)
- **$Param3** (Type: Variable)
- **$ShowResult** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **TaskQueueScheduler.QueuedActionParameters** (Result: **$QueuedActionParameters**)
      - Set **ProcessName** = `$MicroflowName`
      - Set **ReferenceText** = `$ReferenceText`
      - Set **Param1** = `$Param1`
      - Set **Param2** = `$Param2`
      - Set **Param3** = `$Param3`**
2. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_Batch** (Result: **$QueuedAction**)**
3. 🔀 **DECISION:** `$ShowResult`
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_ShowResult****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.