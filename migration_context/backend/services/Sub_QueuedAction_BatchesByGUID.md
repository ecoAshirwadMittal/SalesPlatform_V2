# Microflow Detailed Specification: Sub_QueuedAction_BatchesByGUID

### 📥 Inputs (Parameters)
- **$OQL** (Type: Variable)
- **$MaxBatches** (Type: Variable)
- **$BatchSize** (Type: Variable)
- **$ReferenceText** (Type: Variable)
- **$MicroflowName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Running and MicroflowName=$MicroflowName ]` (Result: **$Running**)**
2. 🔀 **DECISION:** `$Running=empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `-99`
   ➔ **If [true]:**
      1. **Create Variable **$OQL_GUIDs** = `'select distinct ObjectId from ( ' + $OQL + ' ) o'`**
      2. **Call Microflow **TaskQueueScheduler.Sub_OQL_GetGUID** (Result: **$GUID**)**
      3. 🔀 **DECISION:** `$GUID!=empty`
         ➔ **If [true]:**
            1. **Create Variable **$OQL_Batch** = `''`**
            2. **Create Variable **$BatchCount** = `0`**
            3. 🔄 **LOOP:** For each **$undefined** in **$undefined**
               └─ **End Loop**
            4. 🏁 **END:** Return `$BatchCount`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `-1`

**Final Result:** This process concludes by returning a [Integer] value.